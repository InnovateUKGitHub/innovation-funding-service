echo "SELECT version
  FROM ifs_baseline.schema_version AS blsv 
 WHERE blsv.version NOT IN (SELECT version FROM ifs_dry_run_live.schema_version);" | mysql -uroot -ppassword
