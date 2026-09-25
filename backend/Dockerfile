# ---------- Stage 1: Build ----------
# Full Maven + JDK image — only used to compile, never shipped
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first (and mvnw if you use it) — this layer only
# invalidates when dependencies change, not on every code edit,
# so Docker can cache downloaded deps across rebuilds
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the actual source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Stage 2: Runtime ----------
# Slim JRE-only image — no Maven, no JDK, no build cache left behind
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as an unprivileged user, not root
RUN addgroup -S spring && adduser -S spring -G spring

# Copy ONLY the built jar from the build stage
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar
USER spring

# Default/documentation port — Render overrides this via $PORT at runtime
EXPOSE 9080

# Note: no ENV vars for DATABASE_URL, STRIPE_SECRET_KEY etc. here —
# those come from Render's environment variable settings, not the image.
# Baking secrets into the image would leak them into image layers/history.

ENTRYPOINT ["java", "-jar", "app.jar"]