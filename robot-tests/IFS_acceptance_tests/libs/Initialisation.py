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

# execute SQL query using execute() method, to fetch the Competitions
cursor.execute("""SELECT `id`,`name` FROM competition""")

# Fetch all competition records
competition_ids = {}
for comp in cursor.fetchall():
    competition_ids[comp[1]] = int(comp[0])
    #print(competition_ids)

# execute SQL query using execute() method, to fetch the Applications
cursor.execute("""SELECT `id`,`name` FROM application""")

# Fetch all application records
application_ids = {}
for app in cursor.fetchall():
    application_ids[app[1]] = int(app[0])
    print(application_ids)

# disconnect from server
db.close()