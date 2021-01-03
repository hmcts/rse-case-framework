set -e

./gradlew angular:build
export IDAM_URI='http://localhost:8090/auth/realms/rse'
export NG_COMMAND='ng serve --host 0.0.0.0 --configuration local --proxy-config proxy.conf.dev.json'
docker-compose -p dev -f docker-compose.yml -f docker-compose.dev.yml up -d -V --no-deps --build db frontend keycloak

# Backend toggleable
if [ "$#" -ne 1 ]; then
  ./gradlew java:bootRun -is
fi
