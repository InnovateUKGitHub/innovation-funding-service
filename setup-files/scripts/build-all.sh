cd ../../ifs-data-service
./gradlew clean deployToTomcat client clientCopy testCommonCode testCommonCodeCopy
cd -
cd ../../ifs-web-service
./gradlew clean deployToTomcat
