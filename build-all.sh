cd ifs-data-service
./gradlew clean deployToTomcat client clientCopy
cd -
cd ifs-web-service
./gradlew clean deployToTomcat
