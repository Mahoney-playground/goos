FROM openjdk:16.0.1-jdk-slim
ARG username=workers
ARG work_dir=/home/$username/work

RUN addgroup --system $username --gid 2000 && \
    adduser --system $username --ingroup $username --uid 2001

USER $username
RUN mkdir -p $work_dir
WORKDIR $work_dir
ENV GRADLE_OPTS='-Dorg.gradle.daemon=false -Xms256m -Xmx2g'

COPY --chown=$username . .

RUN ./gradlew --no-watch-fs --debug --stacktrace build
