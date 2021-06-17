To build:

```bash
docker build .
```

To build locally with JDK >= 14 installed:

```bash
./gradlew build
```

The output will be in `build/goos`.

To reproduce the problem with Gradle & JDK 16:
```bash
rm -rf target gradlehome && \
GRADLE_OPTS='-Dorg.gradle.daemon=false -Xms256m -Xmx2g' ./gradlew -g gradlehome --stacktrace build
```
