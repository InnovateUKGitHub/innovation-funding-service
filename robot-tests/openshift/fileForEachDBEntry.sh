echo "***********Creating file entry for each db entry***********"
max_file_entry_id=$(mysql ifs -uroot -ppassword -hifs-database -s -e 'select max(id) from file_entry;')
# DATA_SERVICE_POD=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | grep ^data-service | awk '{ print $1 }')

# curl -L https://github.com/openshift/origin/releases/download/v3.11.0/openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit.tar.gz | tar xvz
# ocpath=openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit/

mkdir -p "/mnt/ifs_storage/ifs/000000000_999999999/000000_999999/000_999/.test"

for i in `seq 1 ${max_file_entry_id}`;
do
  if [ "${i}" != "8" ]
  then
    # ./${ocpath}/oc ${SVC_ACCOUNT_CLAUSE} rsync --include=testing.pdf ${i} ${DATA_SERVICE_POD}:/mnt/ifs_storage/000000000_999999999/000000_999999/000_999/${i}
    cp /robot-tests/upload_files/testing.pdf /mnt/ifs_storage/ifs/000000000_999999999/000000_999999/000_999/.test/${i}
  fi
done
