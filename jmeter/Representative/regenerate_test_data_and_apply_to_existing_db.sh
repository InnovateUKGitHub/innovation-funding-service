#!/usr/bin/env bash

set -e

declare -i count
count=1
file="files-to-generate.csv"
lines=`cat ${file} | wc -l | sed -e 's/^[ \t]*//'`

mysql ifs -hifs-database -uroot -ppassword --skip-column-names < add-indexes.sql 2> /dev/null

cat ${file} | while read line
do
        text=`echo $line | cut -d'"' -f2`     # get the first name
        sql=`echo $line | cut -d'"' -f4`     # get the first name
        csv=`echo $line | cut -d'"' -f6`     # get the first name

        echo "${count} / ${lines} - ${text}"
        mysql ifs -hifs-database -uroot -ppassword --skip-column-names < ${sql} > ${csv} 2> /dev/null
        echo "------------------"
        count=$count+1
done

mysql ifs -hifs-database -uroot -ppassword --skip-column-names < remove-indexes.sql 2> /dev/null
