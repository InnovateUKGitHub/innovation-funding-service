#!/bin/bash

set -e

cd "$(dirname "$0")"

project_root_dir="`pwd`/../.."
webtest_patches_dir="${project_root_dir}/ifs-data-layer/ifs-data-service/src/main/resources/db/webtest"

reset_db () {
    mysql -uroot -ppassword -hifs-database -e "drop database if exists ifs";
    mysql -uroot -ppassword -hifs-database -e "create database ifs";
    mysql -uroot -ppassword -hifs-database -e "drop database if exists ifs_test";
    mysql -uroot -ppassword -hifs-database -e "create database ifs_test";
}

get_current_patch_level () {

    # extract the current version of the webtest data
    echo "`find ${webtest_patches_dir} -name '*__Base_webtest_data.sql' | sed 's/.*\(V.*\)_[0-9]*__.*/\1/g'`"
}

do_baseline () {

    generate_test_class="ifs-data-layer/ifs-data-service/src/test/java/org/innovateuk/ifs/testdata/GenerateTestData.java"

    # navigate to project root
    cd ${project_root_dir}

    # clean database
    reset_db

    ./gradlew clean build buildDocker -x test
    ./gradlew processResources processTestResources

    # run generator test class
    IFS_GENERATE_TEST_DATA_EXECUTION=SINGLE_THREADED IFS_GENERATE_TEST_DATA_COMPETITION_FILTER=ALL_COMPETITIONS ./gradlew -PtestGroups=generatetestdata :ifs-data-layer:ifs-data-service:cleanTest :ifs-data-layer:ifs-data-service:test --tests org.innovateuk.ifs.testdata.GenerateTestData -x asciidoctor

    # extract the current version of the webtest data
    current_version="`get_current_patch_level`_"

    cd ${webtest_patches_dir}
    for i in ${current_version}*; do mv $i ${i/${current_version}/tmp_${new_version}}; done
    rm -f ${new_version}*.sql
    for i in tmp_${new_version}*; do mv $i ${i/tmp_${new_version}/${new_version}}; done

    cd ${project_root_dir}/setup-files/scripts

    # create baseline dump
    ./create-baseline-dump.sh ${new_version}

    cd ${project_root_dir}

    reset_db

    cat << EOF
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*                                                                       *
*           You have successfully run a webtest baseline.               *
*       Please verify the changes by running a full acceptance suite    *
*                                                                       *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
EOF

}

if [[ -n "$1" ]]; then
    new_version_or_current=$1
else
    new_version_or_current=`get_current_patch_level`
fi

new_version="${new_version_or_current}_"

echo "Creating new baseline patch - current version is `get_current_patch_level` and new version will be ${new_version_or_current}"

do_baseline
