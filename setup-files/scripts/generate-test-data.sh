#!/bin/bash

set -e

cd "$(dirname "$0")"

project_root_dir="`pwd`/../.."
webtest_patches_dir="${project_root_dir}/ifs-data-layer/ifs-data-service/src/main/resources/db/webtest"

get_current_patch_level () {

    # extract the current version of the webtest data
    echo "`find ${webtest_patches_dir} -name '*__Base_webtest_data.sql' | sed 's/.*\(V.*\)_[0-9]*__.*/\1/g'`"
}

new_version_or_current=`get_current_patch_level`
force=""
profile=""

while getopts ":f :v: :a" opt ; do
    case ${opt} in
        v)
            new_version_or_current="$OPTARG"
        ;;
        f)
            force="true"
        ;;
        a)
            profile="-Pprofile=automated"
        ;;
    esac
done

new_version="${new_version_or_current}_"

run_flyway_clean () {

    cd ${project_root_dir}

    ./gradlew -PopenshiftEnv=unused $profile -Pcloud=automated -Pifs.company-house.key=unused ifs-data-layer:ifs-data-service:flywayClean

    cd -
}

run_flyway_migrate() {

    cd ${project_root_dir}

    ./gradlew -PopenshiftEnv=unused $profile -Pcloud=automated -Pifs.company-house.key=unused ifs-data-layer:ifs-data-service:flywayMigrate

    cd -
}

do_baseline () {

    generate_test_class="ifs-data-layer/ifs-data-service/src/test/java/org/innovateuk/ifs/testdata/GenerateTestData.java"

    # clean database
    run_flyway_clean

    # navigate to project root
    cd ${project_root_dir}

    ./gradlew -PopenshiftEnv=unused $profile -Pcloud=automated -Pifs.company-house.key=unused clean processResources processTestResources

    # run generator test class
    IFS_GENERATE_TEST_DATA_EXECUTION=SINGLE_THREADED IFS_GENERATE_TEST_DATA_COMPETITION_FILTER=ALL_COMPETITIONS ./gradlew -PopenshiftEnv=unused $profile -Pcloud=automated -Pifs.company-house.key=unused -PtestGroups=generatetestdata :ifs-data-layer:ifs-data-service:cleanTest :ifs-data-layer:ifs-data-service:test --tests org.innovateuk.ifs.testdata.GenerateTestData -x asciidoctor

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

    # check that the new sequence of patches works
    run_flyway_clean
    run_flyway_migrate

    cat << EOF
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*                                                                       *
*           You have successfully run a webtest baseline.               *
*       Please verify the changes by running a full acceptance suite    *
*                                                                       *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
EOF

}

cat << EOF
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*                                                                       *
*           You are about to run a webtest baseline.                    *
*       This will take a while so make sure you are not in a rush       *
*                                                                       *
*                   Current version is `get_current_patch_level`                          *
*                   New version will be ${new_version_or_current}                         *
*                                                                       *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
EOF

if [[ -z "${force}" ]]; then
    while true; do
        read -p "Do you want to start the baseline? (y/N)" yn
        case $yn in
            [Yy]* ) do_baseline; break;;
            [Nn]* ) exit;;
            * ) exit;;
        esac
    done
else
    do_baseline
fi