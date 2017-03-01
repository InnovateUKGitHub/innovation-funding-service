storedFileFolder=$1

echo "***********Creating file entry for each db entry***********"
max_file_entry_id=$(mysql ifs -uroot -ppassword -hifs-database -s -e 'select max(id) from file_entry;')
for i in `seq 1 ${max_file_entry_id}`;
do
  if [ "${i}" != "8" ]
  then
    cp /tmp/testing.pdf ${storedFileFolder}/000000000_999999999/000000_999999/000_999/${i}
  fi
done