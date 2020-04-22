# syntax=docker/dockerfile-upstream:1.1.7-experimental
ARG username=worker
ARG work_dir=/home/$username/work

FROM openjdk:13.0.1-jdk-slim as worker
ARG username
ARG work_dir

RUN addgroup --system $username --gid 1000 && \
    adduser --system $username --ingroup $username --uid 1001

USER $username
RUN mkdir -p $work_dir
WORKDIR $work_dir


FROM worker as builder
ARG username
ENV GRADLE_OPTS='-Dorg.gradle.daemon=false -Xms256m -Xmx512m --illegal-access=deny'

COPY --chown=$username . .

# Can't use docker ARG values in the --mount argument: https://github.com/moby/buildkit/issues/815
RUN --mount=type=cache,target=/home/worker/.gradle,gid=1000,uid=1001 \
    ./gradlew downloadDependencies

RUN --mount=type=cache,target=/home/worker/.gradle,gid=1000,uid=1001 \
    --network=none \
    set +e; \
    ./gradlew --offline check install; \
    echo $? > build_result;

FROM builder as checker
RUN build_result=$(cat build_result); \
    if [ "$build_result" -gt 0 ]; then >&2 echo "The build failed, check output of builder stage"; fi; \
    exit "$build_result"


FROM worker as runner
ARG username
ARG work_dir

USER root
RUN apt-get -qq update && \
    DEBIAN_FRONTEND=noninteractive apt-get -qq -o=Dpkg::Use-Pty=0 install \
      libxrender1 libxtst6 libxi6 \
      fontconfig \
      xvfb && \
    rm -rf /var/lib/apt/lists/*
RUN mkdir /tmp/.X11-unix && chmod 1777 /tmp/.X11-unix
USER $username


FROM worker as end-to-end-tests
ARG username
ARG work_dir

COPY --from=checker --chown=$username $work_dir/end-to-end-tests/build/install/end-to-end-tests/lib/external ./external
COPY --from=checker --chown=$username $work_dir/end-to-end-tests/build/install/end-to-end-tests/lib/internal ./internal
COPY --from=checker --chown=$username $work_dir/end-to-end-tests/build/install/end-to-end-tests/lib/end-to-end-tests-0.1.0.jar .

ENTRYPOINT ["java", "-jar", "end-to-end-tests-0.1.0.jar"]


FROM runner as app
ARG username
ARG work_dir

COPY --from=checker --chown=$username $work_dir/core/build/install/core/lib/external ./external
COPY --from=checker --chown=$username $work_dir/core/build/install/core/lib/core-0.1.0.jar .

ENTRYPOINT ["xvfb-run", "--error-file=/dev/stderr", "java", "-jar", "core-0.1.0.jar"]


FROM app as instrumentedapp
ARG username
ARG work_dir

COPY --from=end-to-end-tests --chown=$username $work_dir/external/marathon-java-agent-*.jar ./external/marathon-java-agent.jar

EXPOSE 1234

ENTRYPOINT ["xvfb-run", "--error-file=/dev/stderr", "java", "-javaagent:external/marathon-java-agent.jar=1234", "-jar", "core-0.1.0.jar"]
