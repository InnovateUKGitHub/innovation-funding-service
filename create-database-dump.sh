if [ -z "$1" ]; then
    echo "Please enter a database name - usage: ./create-database-dump.sh mydatabase myusername mypassword"
    exit 1
fi

if [ -z "$2" ]; then
    echo "Please enter a username - usage: ./create-database-dump.sh mydatabase myusername mypassword"
    exit 1
fi

if [ -z "$3" ]; then
    echo "Please enter a password - usage: ./create-database-dump.sh mydatabase myusername mypassword"
    exit 1
fi

mysqldump $1 -u$2 -p$3 --no-create-info --insert-ignore --complete-insert > setup-files/data-dump/ifs-data.sql
