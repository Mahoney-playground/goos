# syntax=docker/dockerfile:experimental
ARG build_dev=/home/build/dev

FROM openjdk:13.0.1-jdk-slim as builder
ARG build_dev

RUN addgroup --system build --gid 1000 && \
    adduser --system build --ingroup build --uid 1001

USER build
RUN mkdir -p $build_dev
WORKDIR $build_dev

COPY --chown=build . .
RUN --mount=type=cache,target=/home/build/.gradle,gid=1000,uid=1001 ./gradlew --no-daemon --build-cache build


FROM openjdk:13.0.1-jdk-slim as app
ARG app_dir=/usr/local/app
ARG build_dev

RUN addgroup --system apprunner && \
    adduser --system apprunner --ingroup apprunner

RUN mkdir -p $app_dir && chown apprunner:apprunner $app_dir
USER apprunner
WORKDIR $app_dir

COPY --from=builder --chown=apprunner $build_dev/core/build/libs/core-0.1.0.jar .
CMD java -jar core-0.1.0.jar


FROM openjdk:13.0.1-jdk-slim as tests
ARG app_dir=/usr/local/app
ARG build_dev

RUN addgroup --system apprunner && \
    adduser --system apprunner --ingroup apprunner

RUN mkdir -p $app_dir && chown apprunner:apprunner $app_dir
USER apprunner
WORKDIR $app_dir

COPY --from=builder --chown=apprunner $build_dev/end-to-end-tests/build/libs/end-to-end-tests-0.1.0.jar .
CMD java -jar end-to-end-tests-0.1.0.jar
