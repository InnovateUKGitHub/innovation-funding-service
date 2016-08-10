set -e

cd ../..
./gradlew :ifs-web-service:ifs-project-setup-service:cleanDeploy -x test

