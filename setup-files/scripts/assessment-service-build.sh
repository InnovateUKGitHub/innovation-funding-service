set -e

cd ../..
./gradlew :ifs-web-service:ifs-assessment-service:cleanDeploy -x test

