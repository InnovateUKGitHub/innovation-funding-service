cd ../../ifs-data-service
./gradlew clean client clientCopy testCommonCode testCommonCodeCopy deployToTomcat
cd -
cd ../../ifs-web-service
./gradlew clean deployToTomcat
