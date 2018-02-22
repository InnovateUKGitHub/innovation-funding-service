#!/bin/bash

set -e

cd "$(dirname "$0")"

reset_db () {
    mysql -uroot -ppassword -hifs-database -e "drop database if exists ifs";
    mysql -uroot -ppassword -hifs-database -e "create database ifs";
    mysql -uroot -ppassword -hifs-database -e "drop database if exists ifs_test";
    mysql -uroot -ppassword -hifs-database -e "create database ifs_test";

    ./gradlew flywayClean flywayMigrate
}

do_baseline () {
    generate_test_class="ifs-data-layer/ifs-data-service/src/test/java/org/innovateuk/ifs/testdata/GenerateTestData.java"

    # navigate to project root
    cd ../../

    # clean database
    reset_db

    ./gradlew clean build buildDocker -x test
    ./gradlew processResources processTestResources

    # unignore generator test class
#    sed -i -e 's/import org.junit.Ignore;//' $generate_test_class
#    sed -i -e 's/@Ignore//' $generate_test_class

    # run generator test class
    IFS_GENERATE_TEST_DATA_EXECUTION=SINGLE_THREADED ./gradlew :ifs-data-layer:ifs-data-service:cleanTest :ifs-data-layer:ifs-data-service:test --tests org.innovateuk.ifs.testdata.GenerateTestData -x asciidoctor

    cd ifs-data-layer/ifs-data-service/src/main/resources/db/webtest/

    # extract the current version of the webtest data
    oldversion="`find . -name '*__Base_webtest_data.sql' | sed 's/.*\(V.*\)_[0-9]*__.*/\1/g'`_"

    for i in ${oldversion}*; do mv $i ${i/${oldversion}/tmp_${newversion}}; done
    rm -f ${newversion}*.sql
    for i in tmp_${newversion}*; do mv $i ${i/tmp_${newversion}/${newversion}}; done

    cd ../../../../../../../

    # create baseline dump
    setup-files/scripts/create-baseline-dump.sh ${newversion}

    reset_db

    #verify correct build
    ./gradlew clean buildDocker initDB composeUp syncShib -x test

    cat << EOF
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*                                                                       *
*           You have successfully run a webtest baseline.               *
*       please verify the changes by running a full acceptance suite    *
*                                                                       *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
EOF

}

newversion=${1?please specify the new baseline version(e.g. V100_12)}
newversion="${newversion}_"


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




