set -e

export IDAM_URI='http://localhost:5000/o'
export NG_COMMAND='ng serve --host 0.0.0.0 --configuration local --proxy-config proxy.conf.dev.json'
docker-compose -p dev -f docker-compose.yml -f docker-compose.dev.yml up -d -V --build db xui-manage-cases

# Backend toggleable
if [ "$#" -ne 1 ]; then
  ./gradlew java:backend:bootRun -is
fi
