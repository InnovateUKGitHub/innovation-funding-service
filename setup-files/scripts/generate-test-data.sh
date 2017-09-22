#!/bin/sh
cd "$(dirname "$0")"

function reset_db() {
    mysql -uroot -ppassword -hifs-database -e "drop database ifs";
    mysql -uroot -ppassword -hifs-database -e "create database ifs";
    mysql -uroot -ppassword -hifs-database -e "drop database ifs_test";
    mysql -uroot -ppassword -hifs-database -e "create database ifs_test";

    ./gradlew flywayClean flywayMigrate
}

function do_baseline() {
    generate_test_class="ifs-data-layer/ifs-data-service/src/test/java/org/innovateuk/ifs/testdata/GenerateTestData.java"

    # navigate to project root
    cd ../../

    # clean database
    reset_db

    ./gradlew clean build buildDocker -x test
    ./gradlew processResources processTestResources

    # unignore generator test class
    sed -i -e 's/import org.junit.Ignore;//' $generate_test_class
    sed -i -e 's/@Ignore//' $generate_test_class

    # run generator test class
    ./gradlew :ifs-data-layer:ifs-data-service:cleanTest :ifs-data-layer:ifs-data-service:test --tests org.innovateuk.ifs.testdata.GenerateTestData -x asciidoctor

    # create baseline dump
    setup-files/scripts/create-baseline-dump.sh ${newversion}
    cd ifs-data-layer/ifs-data-service/src/main/resources/db/webtest/

    for i in ${oldversion}*; do mv $i ${i/${oldversion}/${newversion}}; done

    cd ../../../../../../../

    # ignore generator test class
    sed -i -e '/import/i \
    import org.junit.Ignore;\
    ' $generate_test_class

    sed -i -e '/public class/i \
     @Ignore\
    ' $generate_test_class

    reset_db

    #verify correct build
    ./gradlew clean build buildDocker

    reset_db

    ./gradlew composeUp syncShib

    cat << EOF
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*                                                                       *
*           You have successfully run a webtest baseline.               *
*       please verify the changes by running a full acceptance suite    *
*                                                                       *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
EOF

}

oldversion=${1?please specify the old baseline version(e.g. V100_11_)}
newversion=${2?please specify the new baseline version(e.g. V100_12_)}


cat << EOF
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*                                                                       *
*           You are about to run a webtest baseline.                    *
*       This will take a while so make sure you are not in a rush       *
*                                                                       *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
EOF

while true; do
    read -p "Do you want to start the baseline? (y/N)" yn
    case $yn in
        [Yy]* ) do_baseline $1 $2; break;;
        [Nn]* ) exit;;
        * ) exit;;
    esac
done




