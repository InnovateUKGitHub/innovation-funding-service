if [ -z "$1" ]; then
    echo "Please enter a database name - usage: ./import-database-data.sh mydatabase myusername mypassword"
    exit 1
fi

if [ -z "$2" ]; then
    echo "Please enter a username - usage: ./import-database-data.sh mydatabase myusername mypassword"
    exit 1
fi

if [ -z "$3" ]; then
    echo "Please enter a password - usage: ./import-database-data.sh mydatabase myusername mypassword"
    exit 1
fi


read -p "This will import fresh data into your $1 database - are you sure? " -n 1 -r
echo 
if [[ $REPLY =~ ^[Yy]$ ]]
then

  mysql $1 -u$2 -p$3 < ../data-dump/ifs-data.sql

fi
