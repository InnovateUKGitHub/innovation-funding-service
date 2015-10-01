if [ -z "$1" ]; then
    echo "Please enter a database name - usage: ./drop-database.sh mydatabase myusername mypassword"
    exit 1
fi

if [ -z "$2" ]; then
    echo "Please enter a username - usage: ./drop-database.sh mydatabase myusername mypassword"
    exit 1
fi

if [ -z "$3" ]; then
    echo "Please enter a password - usage: ./drop-database.sh mydatabase myusername mypassword"
    exit 1
fi


read -p "This will drop all tables and data in your $1 database - are you sure? " -n 1 -r
echo 
if [[ $REPLY =~ ^[Yy]$ ]]
then

  mysql $1 -u$2 -p$3 < setup-files/data-dump/ifs-drop-tables.sql

fi
