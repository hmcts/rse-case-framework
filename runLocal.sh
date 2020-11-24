set -e
trap "kill 0" EXIT

export NG_COMMAND='ng serve --host 0.0.0.0 --configuration local'
docker-compose up -V --no-deps --build --abort-on-container-exit db frontend keycloak \
  & ./wait-for-it.sh localhost:8090/auth/realms/rse && ./gradlew java:bootRun -i
wait
