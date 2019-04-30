echo "***********Creating file entry for each db entry***********"
max_file_entry_id=$(mysql ifs -uroot -ppassword -hifs-database -s -e 'select max(id) from file_entry;')

mkdir -p "/mnt/ifs_storage/ifs/100000000_999999999/000000_999999/000_999"

for i in `seq 1 ${max_file_entry_id}`;
do
  if [ "${i}" != "8" ]
  then
    cp /robot-tests/upload_files/testing.pdf /mnt/ifs_storage/ifs/100000000_999999999/000000_999999/000_999/${i}
  fi
done
