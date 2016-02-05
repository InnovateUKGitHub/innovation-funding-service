cd ../../ifs-data-service
./gradlew testCommonCode testCommonCodeCopy cleanDeploy
cd -
cd ../../ifs-web-service
./gradlew cleanDeploy
