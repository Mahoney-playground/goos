# syntax=docker/dockerfile-upstream@sha256:694457a230ae531f1506cf56c222db773f9c3e6c34bf791e6fad06cadf1dd46a
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
    ./gradlew --offline install test; \
    echo $? > build_result; \
    set -e

FROM builder as checker
RUN build_result=$(cat build_result); \
    if [ "$build_result" -gt 0 ]; then >&2 echo "The build failed, check output of builder stage"; fi; \
    exit "$build_result"

FROM worker as app
ARG username
ARG work_dir

COPY --from=builder --chown=$username $work_dir/core/build/install/core/lib/external ./external
COPY --from=builder --chown=$username $work_dir/core/build/install/core/lib/core-0.1.0.jar .

CMD java -jar core-0.1.0.jar


FROM worker as tests
ARG username
ARG work_dir

COPY --from=builder --chown=$username $work_dir/end-to-end-tests/build/install/end-to-end-tests/lib/external ./external
COPY --from=builder --chown=$username $work_dir/end-to-end-tests/build/install/end-to-end-tests/lib/internal ./internal
COPY --from=builder --chown=$username $work_dir/end-to-end-tests/build/install/end-to-end-tests/lib/end-to-end-tests-0.1.0.jar .

CMD java -jar end-to-end-tests-0.1.0.jar
