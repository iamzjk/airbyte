ARG JDK_VERSION=14.0.2
FROM openjdk:${JDK_VERSION}-slim AS server

EXPOSE 8000

ENV APPLICATION airbyte-server

WORKDIR /app

ADD build/distributions/${APPLICATION}-0.30.23-alpha.tar /app

# wait for upstream dependencies to become available before starting server
ENTRYPOINT ["/bin/bash", "-c", "${APPLICATION}-0.30.23-alpha/bin/${APPLICATION}"]
