set -e

cd ../../ifs-data-service
./gradlew -s testCommonCode testCommonCodeCopy cleanDeploy -x test
cd -
cd ../../ifs-web-service
./gradlew -s cleanDeploy -x test
