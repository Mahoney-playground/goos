# syntax=docker/dockerfile:1.4.0
ARG username=worker
ARG work_dir=/home/$username/work
ARG gid=1000
ARG uid=1001

FROM eclipse-temurin:17.0.1_12-jdk-focal as worker
ARG username
ARG work_dir
ARG gid
ARG uid

# Install xvfb so that a GUI (and GUI tests) can run
RUN apt-get -qq update && \
    DEBIAN_FRONTEND=noninteractive apt-get -qq -o=Dpkg::Use-Pty=0 install \
      libxrender1 libxtst6 libxi6 \
      fontconfig \
      xvfb \
      && rm -rf /var/lib/apt/lists/*
RUN mkdir /tmp/.X11-unix && chmod 1777 /tmp/.X11-unix
COPY --chown=root scripts/simple-xvfb-run.sh /usr/bin/simple-xvfb-run

RUN addgroup --system $username --gid $gid && \
    adduser --system $username --ingroup $username --uid $uid

USER $username
RUN mkdir -p $work_dir
WORKDIR $work_dir


# Copy across all the *.gradle.kts files in a separate stage
# This will not get any layer caching if anything in the context has changed, but when we
# subsequently copy them into a different stage that stage *will* get layer caching. So if none of
# the *.gradle.kts files have changed, a subsequent command will also get layer caching.
FROM alpine as gradle-files
RUN --mount=type=bind,target=/docker-context \
    mkdir -p /gradle-files; \
    cd /docker-context/; \
    find . -name "*.gradle.kts" -exec cp --parents "{}" /gradle-files/ \;


FROM worker as builder
ARG username
ARG gid
ARG uid

# The single use daemon will be unavoidable in future so don't waste time trying to prevent it
ENV GRADLE_OPTS='-Dorg.gradle.daemon=false'
ARG gradle_cache_dir=/home/$username/.gradle/caches

# Download gradle in a separate step to benefit from layer caching
COPY --chown=$username gradle/wrapper gradle/wrapper
COPY --chown=$username gradlew gradlew
COPY --chown=$username gradle.properties gradle.properties
RUN ./gradlew --version

# Do all the downloading in one step...
COPY --chown=$username --from=gradle-files /gradle-files ./
COPY --chown=$username libraries/indexhtml libraries/indexhtml
COPY --chown=$username gradle/build-plugins gradle/build-plugins
RUN --mount=type=cache,target=$gradle_cache_dir,gid=$gid,uid=$uid \
    ./gradlew --no-watch-fs --stacktrace downloadDependencies

COPY --chown=$username . .

FROM builder as tester
# So the actual build can run without network access. Proves no tests rely on external services.
RUN --mount=type=cache,target=$gradle_cache_dir,gid=$gid,uid=$uid \
    --network=none \
    simple-xvfb-run ./gradlew --no-watch-fs --offline build || mkdir -p build


FROM scratch as build-output
ARG work_dir

COPY --from=tester $work_dir/build .

# The builder step is guaranteed not to fail, so that the worker output can be tagged and its
# contents (build reports) extracted.
# You run this as:
# `docker build . --target build-reports --output build-reports && docker build .`
# to retrieve the build reports whether or not the previous line exited successfully.
# Workaround for https://github.com/moby/buildkit/issues/1421
FROM builder as checker
RUN --mount=type=cache,target=$gradle_cache_dir,gid=$gid,uid=$uid \
    --network=none \
    simple-xvfb-run ./gradlew --no-watch-fs --stacktrace --offline build


FROM worker as end-to-end-tests
ARG username
ARG work_dir

COPY --from=checker --chown=$username $work_dir/build/end-to-end-tests/install/end-to-end-tests/lib/external ./external
COPY --from=checker --chown=$username $work_dir/build/end-to-end-tests/install/end-to-end-tests/lib/internal ./internal
COPY --from=checker --chown=$username $work_dir/build/end-to-end-tests/install/end-to-end-tests/lib/end-to-end-tests.jar .

ENTRYPOINT ["java", "-jar", "-enableassertions", "end-to-end-tests.jar"]


FROM worker as auction-xmpp-integration-tests
ARG username
ARG work_dir

COPY --from=checker --chown=$username $work_dir/build/app-src/auction/xmpp-integration-tests/install/auction-xmpp-integration-tests/lib/external ./external
COPY --from=checker --chown=$username $work_dir/build/app-src/auction/xmpp-integration-tests/install/auction-xmpp-integration-tests/lib/internal ./internal
COPY --from=checker --chown=$username $work_dir/build/app-src/auction/xmpp-integration-tests/install/auction-xmpp-integration-tests/lib/auction-xmpp-integration-tests.jar .

ENTRYPOINT ["java", "-jar", "-enableassertions", "auction-xmpp-integration-tests.jar"]


FROM worker as instrumentedapp
ARG username
ARG work_dir

USER root

RUN apt-get -qq update && \
    DEBIAN_FRONTEND=noninteractive apt-get -qq -o=Dpkg::Use-Pty=0 install \
      curl \
      && rm -rf /var/lib/apt/lists/*

USER $username

# The duplication with app here is necessary to cache the installation of curl in a layer
COPY --from=checker --chown=$username $work_dir/build/goos/lib/external ./external
COPY --from=checker --chown=$username $work_dir/build/goos/lib/agents ./agents
COPY --from=checker --chown=$username $work_dir/build/goos/lib/internal ./internal
COPY --from=checker --chown=$username $work_dir/build/goos/lib/goos.jar .

EXPOSE 1234

ENTRYPOINT ["simple-xvfb-run", "java", "-javaagent:agents/marathon-java-agent.jar=1234", "-Xshare:off", "--add-exports", "java.desktop/sun.awt=ALL-UNNAMED", "-jar", "goos.jar"]

COPY --chown=$username scripts/app-running.sh $work_dir/app-running.sh

HEALTHCHECK --interval=1s --retries=20 --timeout=5s CMD ./app-running.sh


FROM worker as app
ARG username
ARG work_dir

# By coping across 3rd party dependencies in a separate step we allow caching of that layer, which
# should be much less changeable than the jars we build
COPY --from=checker --chown=$username $work_dir/build/goos/lib/external ./external
COPY --from=checker --chown=$username $work_dir/build/goos/lib/internal ./internal
COPY --from=checker --chown=$username $work_dir/build/goos/lib/goos.jar .

ENTRYPOINT ["simple-xvfb-run", "java", "-jar", "goos.jar"]
