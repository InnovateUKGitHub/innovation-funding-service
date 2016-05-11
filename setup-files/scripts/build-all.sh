set -e

cd ../../ifs-data-service
./gradlew -s testCommonCode testCommonCodeCopy cleanDeploy $1 $2
cd -
cd ../../ifs-web-service
./gradlew -s cleanDeploy $1 $2
