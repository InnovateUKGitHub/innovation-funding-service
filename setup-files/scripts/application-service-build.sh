set -e

cd ../..
./gradlew :ifs-web-service:ifs-application-service:cleanDeploy -x test

