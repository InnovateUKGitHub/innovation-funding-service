set -e

cd ../..
./gradlew :ifs-web-service:ifs-project-setup-mgt-service:cleanDeploy -x test

