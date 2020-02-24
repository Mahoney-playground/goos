ARG build_home=/home/build/dev

FROM openjdk:13.0.1-jdk-slim as builder
ARG build_home

RUN addgroup --system build && \
    adduser --system build --ingroup build

USER build
RUN mkdir $build_home
WORKDIR $build_home

# Cache the gradle binaries
COPY --chown=build gradlew gradlew
COPY --chown=build gradle gradle
RUN ./gradlew --no-daemon --version

# Cache basic dependencies
COPY --chown=build .editorconfig .editorconfig
COPY --chown=build buildSrc buildSrc

COPY --chown=build deps1.build.gradle.kts deps1.build.gradle.kts
RUN ./gradlew --no-daemon -b deps1.build.gradle.kts downloadDependencies

COPY --chown=build deps2.build.gradle.kts deps2.build.gradle.kts
RUN ./gradlew --no-daemon -b deps2.build.gradle.kts downloadDependencies

COPY --chown=build settings.gradle.kts settings.gradle.kts
COPY --chown=build build.gradle.kts build.gradle.kts
COPY --chown=build core/build.gradle.kts core/build.gradle.kts

RUN ./gradlew --no-daemon  downloadDependencies

COPY --chown=build . .

RUN ./gradlew --no-daemon build

FROM openjdk:13.0.1-jdk-slim as app
ARG app_dir=/usr/local/app
ARG build_home

RUN addgroup --system apprunner && \
    adduser --system apprunner --ingroup apprunner

RUN mkdir -p $app_dir && chown apprunner:apprunner $app_dir
USER apprunner
WORKDIR $app_dir

COPY --from=builder --chown=apprunner $build_home/core/build/libs/core-0.1.0.jar .
CMD java -jar core-0.1.0.jar

FROM openjdk:13.0.1-jdk-slim as tests
ARG app_dir=/usr/local/app
ARG build_home

RUN addgroup --system apprunner && \
    adduser --system apprunner --ingroup apprunner

RUN mkdir -p $app_dir && chown apprunner:apprunner $app_dir
USER apprunner
WORKDIR $app_dir

COPY --from=builder --chown=apprunner $build_home/end-to-end-tests/build/libs/end-to-end-tests-0.1.0.jar .
CMD java -jar end-to-end-tests-0.1.0.jar
