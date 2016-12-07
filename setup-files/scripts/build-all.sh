set -e

cd ../../ifs-data-service
./gradlew -s build -PexcludeTests=com/worth/ifs/application/controller/DatabasePatchingTest.class $1 $2
cd -
cd ../../ifs-web-service
./gradlew -s build $1 $2
