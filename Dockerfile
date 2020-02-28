# syntax=docker/dockerfile:experimental
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

COPY --chown=$username . .
# Can't use docker ARG values in the --mount argument: https://github.com/moby/buildkit/issues/815
RUN --mount=type=cache,target=/home/worker/.gradle,gid=1000,uid=1001 ./gradlew --no-daemon --build-cache build


FROM worker as app
ARG username
ARG work_dir

COPY --from=builder --chown=$username $work_dir/core/build/libs/core-0.1.0.jar .
CMD java -jar core-0.1.0.jar


FROM worker as tests
ARG username
ARG work_dir

COPY --from=builder --chown=$username $work_dir/end-to-end-tests/build/libs/end-to-end-tests-0.1.0.jar .
CMD java -jar end-to-end-tests-0.1.0.jar
