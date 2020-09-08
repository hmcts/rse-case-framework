trap "kill 0" EXIT

export NG_COMMAND='ng serve --host 0.0.0.0 --configuration local'
./gradlew bootRun & docker-compose up --no-deps --build --abort-on-container-exit db frontend

wait
