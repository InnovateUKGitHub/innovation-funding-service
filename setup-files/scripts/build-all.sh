set -e

cd ../../ifs-data-service
./gradlew -s cleanDeploy -PexcludeTests=com/worth/ifs/application/controller/DatabasePatchingTest.class $1 $2
cd -
cd ../../ifs-web-service
./gradlew -s cleanDeploy $1 $2
