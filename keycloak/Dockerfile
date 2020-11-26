FROM quay.io/keycloak/keycloak:11.0.3
COPY test_realm.json /
ENV KEYCLOAK_USER admin
ENV KEYCLOAK_PASSWORD admin
ENV KEYCLOAK_IMPORT test_realm.json
EXPOSE 8080
