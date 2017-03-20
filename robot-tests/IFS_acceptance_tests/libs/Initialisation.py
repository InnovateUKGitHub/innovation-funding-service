#!/usr/bin/python

import MySQLdb

config = {
    'user': 'root',
    'passwd': 'password',
    'host': 'ifs-database',
    'db': 'ifs',
    'port': 3306,
}

# Open database connection
db = MySQLdb.connect(**config)

# prepare a cursor object using cursor() method
cursor = db.cursor()

# execute SQL query using execute() method.
cursor.execute("""SELECT `id`,`name` FROM competition""")

# Fetch a single row using fetchone() method.
competition_ids = {}
for comp in cursor.fetchall():
    competition_ids[comp[1]] = int(comp[0])
    #print(competition_ids)

# disconnect from server
db.close()