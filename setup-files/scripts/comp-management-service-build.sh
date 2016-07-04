set -e

cd ../..
./gradlew :ifs-web-service:ifs-competition-mgt-service:cleanDeploy -x test

