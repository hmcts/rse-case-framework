# Use this script to export changes to the keycloak configuration.
# When running docker-compose make changes in the keycloak admin console (http://localhost:8090),
# then run this script to export the modified realm configuration.

# Copy a script to the keycloak container to perform an export
docker cp docker-exec-cmd.sh rse-case-framework_keycloak_1:/tmp/docker-exec-cmd.sh
# Execute the script inside of the container
docker exec -it rse-case-framework_keycloak_1 /tmp/docker-exec-cmd.sh
# Grab the finished export from the container
docker cp rse-case-framework_keycloak_1:/tmp/test_realm.json .
