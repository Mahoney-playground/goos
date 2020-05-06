# syntax=docker/dockerfile-upstream:1.1.7-experimental
ARG username=worker
ARG work_dir=/home/$username/work

FROM openjdk:14.0.1-jdk-slim as worker
ARG username
ARG work_dir

RUN apt-get -qq update && \
    DEBIAN_FRONTEND=noninteractive apt-get -qq -o=Dpkg::Use-Pty=0 install \
      libxrender1 libxtst6 libxi6 \
      fontconfig \
      xvfb \
      && rm -rf /var/lib/apt/lists/*
RUN mkdir /tmp/.X11-unix && chmod 1777 /tmp/.X11-unix
COPY --chown=root scripts/simple-xvfb-run.sh /usr/bin/simple-xvfb-run

RUN addgroup --system $username --gid 1000 && \
    adduser --system $username --ingroup $username --uid 1001

USER $username
RUN mkdir -p $work_dir
WORKDIR $work_dir


FROM worker as builder
ARG username
ENV GRADLE_OPTS='-Dorg.gradle.daemon=false -Xms256m -Xmx2g --illegal-access=deny'

COPY --chown=$username . .

# Can't use docker ARG values in the --mount argument: https://github.com/moby/buildkit/issues/815
RUN --mount=type=cache,target=/home/worker/.gradle,gid=1000,uid=1001 \
    ./gradlew downloadDependencies

RUN --mount=type=cache,target=/home/worker/.gradle,gid=1000,uid=1001 \
    --network=none \
    set +e; \
    simple-xvfb-run ./gradlew --offline check projectReport install; \
    echo $? > build_result;

FROM builder as checker
RUN build_result=$(cat build_result); \
    if [ "$build_result" -gt 0 ]; then >&2 echo "The build failed, check output of builder stage"; fi; \
    exit "$build_result"


FROM worker as end-to-end-tests
ARG username
ARG work_dir

COPY --from=checker --chown=$username $work_dir/end-to-end-tests/build/install/end-to-end-tests/lib/external ./external
COPY --from=checker --chown=$username $work_dir/end-to-end-tests/build/install/end-to-end-tests/lib/internal ./internal
COPY --from=checker --chown=$username $work_dir/end-to-end-tests/build/install/end-to-end-tests/lib/end-to-end-tests-0.1.0.jar .

ENTRYPOINT ["java", "-jar", "--illegal-access=deny", "end-to-end-tests-0.1.0.jar"]


FROM worker as app
ARG username
ARG work_dir

ENTRYPOINT ["simple-xvfb-run", "java", "--illegal-access=deny", "-jar", "core-0.1.0.jar"]


FROM worker as instrumentedapp
ARG username
ARG work_dir

USER root

RUN apt-get -qq update && \
    DEBIAN_FRONTEND=noninteractive apt-get -qq -o=Dpkg::Use-Pty=0 install \
      curl \
      && rm -rf /var/lib/apt/lists/*

USER $username

COPY --from=checker --chown=$username $work_dir/core/build/install/core/lib/external ./external
COPY --from=checker --chown=$username $work_dir/core/build/install/core/lib/internal ./internal
COPY --from=checker --chown=$username $work_dir/core/build/install/core/lib/core-0.1.0.jar .
COPY --from=end-to-end-tests --chown=$username $work_dir/external/marathon-java-agent-*.jar ./external/marathon-java-agent.jar

EXPOSE 1234

ENTRYPOINT ["simple-xvfb-run", "java", "-javaagent:external/marathon-java-agent.jar=1234", "-Xshare:off", "--illegal-access=deny", "--add-exports", "java.desktop/sun.awt=ALL-UNNAMED", "-jar", "core-0.1.0.jar"]

COPY --chown=$username scripts/app-running.sh $work_dir/app-running.sh

HEALTHCHECK --interval=1s --retries=20 --timeout=5s CMD ./app-running.sh
