set -e
trap "kill 0" EXIT

export NG_COMMAND='ng serve --host 0.0.0.0 --configuration local'
docker-compose up -V --no-deps --build --abort-on-container-exit db frontend \
  & ./wait-for-it.sh -t 0 localhost:5432 -- ./gradlew bootRun -i

wait
