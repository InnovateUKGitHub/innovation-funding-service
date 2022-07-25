echo "***********Creating file entry for each db entry***********"
max_file_entry_id=$(mysql ifs -uroot -ppassword -hifs-database -s -e 'select max(id) from file_entry;')

TEST_FILES_DIR="/tmp"

mkdir -p "${TEST_FILES_DIR}"

for i in `seq 1 ${max_file_entry_id}`;
do
  if [ "${i}" != "8" ]
  then
    CURRENT_FILE_PATH="${TEST_FILES_DIR}/${i}"
    cp /robot-tests/upload_files/testing.pdf $CURRENT_FILE_PATH
  fi
done

