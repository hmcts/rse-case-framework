set -e
trap "kill 0" EXIT

export IDAM_URI='http://localhost:8090/auth/realms/rse'
export NG_COMMAND='ng serve --host 0.0.0.0 --configuration local --proxy-config proxy.conf.dev.json'
docker-compose up -V --no-deps --build --abort-on-container-exit db frontend keycloak \
  & ./gradlew java:bootRun -i
wait
