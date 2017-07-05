#/bin/bash

set -e

files=(rewrites/*)

for i in "${files[@]}"
do
    table_name=$(echo $i | sed "s/.*\///" | sed "s/.*\///" | sed "s/\..*//")

    field_array=( $(cut -d '|' -f1 $i) )
	mask_array=( $(cut -d '|' -f2 $i) )

    match_pattern="^SELECT /\*!40001 SQL_NO_CACHE \*/ \* FROM $table_name"

    full_select_statement_query="SELECT CONCAT('SELECT SQL_NO_CACHE ', ( SELECT GROUP_CONCAT(t.col) FROM (SELECT COLUMN_NAME as col FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '$table_name' AND TABLE_SCHEMA = '$DB_NAME') t WHERE t.col IS NOT NULL) , ' FROM $table_name' );"

    full_select_statement_result=$(mysql -h$DB_HOST -u$DB_USER -p$DB_PASS -P$DB_PORT $DB_NAME -N -s -e "$full_select_statement_query")

    replacement_pattern=$full_select_statement_result

    for j in "${!field_array[@]}"
    do
        replacement_pattern=$( echo $replacement_pattern | sed s/${field_array[j]}/${mask_array[j]}/ )
    done

    echo $replacement_pattern

done

