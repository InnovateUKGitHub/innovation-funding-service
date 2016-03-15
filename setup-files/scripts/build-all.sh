set -e

cd ../../ifs-data-service
./gradlew -s testCommonCode testCommonCodeCopy cleanDeploy
cd -
cd ../../ifs-web-service
./gradlew -s cleanDeploy
