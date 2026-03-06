FROM ghcr.io/graalvm/native-image-community:25 AS builder
WORKDIR /workspace

COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY gradle gradle
COPY settings.gradle.kts settings.gradle.kts
COPY build.gradle.kts build.gradle.kts

RUN chmod +x gradlew
COPY src src
RUN ./gradlew nativeCompile -x test

FROM debian:bookworm-slim
RUN groupadd --system app && useradd --system app --gid app
WORKDIR /app

COPY --from=builder /workspace/build/native/nativeCompile/talent_hub /app/talent_hub

EXPOSE 8080
USER app
ENTRYPOINT ["/app/talent_hub"]
