ARG build_home=/home/build/dev

FROM openjdk:13.0.1-jdk-slim as builder
ARG build_home

RUN addgroup --system build && \
    adduser --system build && \
    usermod -a -G build build

USER build
RUN mkdir $build_home
WORKDIR $build_home

COPY --chown=build . .

RUN ./gradlew -g caches/dependency-cache --build-cache --no-daemon build

FROM openjdk:13.0.1-jdk-slim as app
ARG app_dir=/usr/local/app
ARG build_home

RUN addgroup --system apprunner && \
    adduser --system apprunner && \
    usermod -a -G apprunner apprunner

RUN mkdir -p $app_dir && chown apprunner:apprunner $app_dir
USER apprunner
WORKDIR $app_dir

COPY --from=builder --chown=apprunner $build_home/core/build/libs/core-0.1.0.jar .
CMD java -jar core-0.1.0.jar

FROM openjdk:13.0.1-jdk-slim as tests
ARG app_dir=/usr/local/app
ARG build_home

RUN addgroup --system apprunner && \
    adduser --system apprunner && \
    usermod -a -G apprunner apprunner

RUN mkdir -p $app_dir && chown apprunner:apprunner $app_dir
USER apprunner
WORKDIR $app_dir

COPY --from=builder --chown=apprunner $build_home/end-to-end-tests/build/libs/end-to-end-tests-0.1.0.jar .
CMD java -jar end-to-end-tests-0.1.0.jar
