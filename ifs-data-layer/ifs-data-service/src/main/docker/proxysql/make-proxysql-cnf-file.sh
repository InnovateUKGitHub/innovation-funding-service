#/bin/bash

set -e

sed -i "s#<<DB_USER>>#$DB_USER#g;s#<<DB_PASS>>#$DB_PASS#g;s#<<DB_NAME>>#$DB_NAME#g;s#<<DB_HOST>>#$DB_HOST#g;s#<<DB_PORT>>#$DB_PORT#g" /etc/proxysql.cnf

files=(/dump/rewrites/*)

for i in "${files[@]}"
do
    table_name=$(echo $i | sed "s/.*\///" | sed "s/.*\///" | sed "s/\..*//")

    field_array=( $(cut -d '|' -f1 $i) )
	mask_array=( $(cut -d '|' -f2 $i) )

    full_select_statement_query="SELECT CONCAT('SELECT SQL_NO_CACHE ', ( SELECT GROUP_CONCAT(t.col) FROM (SELECT COLUMN_NAME as col FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '$table_name' AND TABLE_SCHEMA = '$DB_NAME') t WHERE t.col IS NOT NULL) , ' FROM $table_name' );"

    full_select_statement_result=$(mysql -h$DB_HOST -u$DB_USER -p$DB_PASS -P$DB_PORT $DB_NAME -N -s -e "$full_select_statement_query")

    replacement_pattern=$full_select_statement_result

    for j in "${!field_array[@]}"
    do
        replacement_pattern=$( echo $replacement_pattern | sed s/${field_array[j]}/${mask_array[j]}/ )
    done

    if [ "$((j))" -gt "1" ]; then
       echo '    ,' >> /dump/query_rules
    fi

    rule_id=$j
    echo '    {' >> /dump/query_rules
    echo "        rule_id=$rule_id" >> /dump/query_rules
    echo '        active=1' >> /dump/query_rules
    match_pattern="^SELECT /\*!40001 SQL_NO_CACHE \*/ \* FROM \`$table_name\`"
    echo "        match_pattern=\"$match_pattern\"" >> /dump/query_rules
    echo "        replace_pattern=\"$replacement_pattern\"" >> /dump/query_rules
    echo '        destination_hostgroup=0' >> /dump/query_rules
    echo '        apply=1' >> /dump/query_rules
    echo '    }' >> /dump/query_rules

done

sed -i "/<<QUERY_RULES>>/{
    s/<<QUERY_RULES>>//g
    r /dump/query_rules
}" /etc/proxysql.cnf