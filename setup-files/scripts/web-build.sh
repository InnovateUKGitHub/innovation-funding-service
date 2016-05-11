set -e

cd ../../ifs-web-service
if [ "$1" == "-all" ]; then
    cd ./ifs-core/src/main/resources/static/
    compass clean && compass compile
    gulp
    cd -
elif [ "$1" == "-css" ]; then
    cd ./ifs-core/src/main/resources/static/
    compass clean && compass compile
    cd -
elif [ "$1" == "-js" ]; then
    cd ./ifs-core/src/main/resources/static/
    gulp
    cd -
fi
./gradlew -s cleanDeploy 
