#!/bin/bash

set -e

project_root_dir="`pwd`/../.."
webtest_patches_dir="${project_root_dir}/ifs-data-layer/ifs-data-service/src/main/resources/db/webtest"

get_current_patch_level () {

    # extract the current version of the webtest data
    echo "`find ${webtest_patches_dir} -name '*__Base_webtest_data.sql' | sed 's/.*\(V.*\)_[0-9]*__.*/\1/g'`"
}

new_version_or_current=`get_current_patch_level`

while getopts ":f :v: :a" opt ; do
    case ${opt} in
        v)
            new_version_or_current="$OPTARG"
        ;;
    esac
done

new_version="${new_version_or_current}_"
# extract the current version of the webtest data
current_version="`get_current_patch_level`_"

cat << EOF
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*                                                                       *
*           You are about to renumber a webtest baseline.               *
*       This will take a while so make sure you are not in a rush       *
*                                                                       *
*                   Current version is `get_current_patch_level`                           *
*                   New version will be ${new_version_or_current}                          *
*                                                                       *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
EOF


new_version_or_current=`get_current_patch_level`
cd ${webtest_patches_dir}
for i in ${current_version}*; do mv $i ${i/${current_version}/tmp_${new_version}}; done
rm -f ${new_version}*.sql
for i in tmp_${new_version}*; do mv $i ${i/tmp_${new_version}/${new_version}}; done