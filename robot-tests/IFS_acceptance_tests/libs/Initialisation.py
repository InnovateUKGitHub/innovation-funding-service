#!/usr/bin/python

import pymysql
import os
config = ''

def connectToDb():
    try:
        os.environ['bamboo_IFS_MYSQL_USER_NAME']
        print("Using server mysql config")
        config = {
            'user': os.environ['bamboo_IFS_MYSQL_USER_NAME'],
            'passwd': os.environ['bamboo_IFS_MYSQL_PASSWORD'],
            'host': os.environ['bamboo_IFS_MYSQL_HOSTNAME'],
            'db': os.environ['bamboo_IFS_MYSQL_DB_NAME'],
            'port': 3306,
        }
    except KeyError:
        print("Using local mysql config")
        config = {
            'user': 'root',
            'passwd': 'password',
            'host': 'ifs-database',
            'db': 'ifs',
            'port': 3306,
        }

    # Open database connection
    db = pymysql.connect(**config)

    # prepare a cursor object using cursor() method
    cursor = db.cursor()

    return db, cursor

db, cursor = connectToDb()

# execute SQL query using execute() method, to fetch the Competitions
cursor.execute("SELECT `id`,`name` FROM competition")

# Fetch all competition records
competition_ids = {}
for comp in cursor.fetchall():
    if comp[1] is None:
        competition_ids['none'] = int(comp[0])
    else:
        competition_ids[comp[1]] = int(comp[0])

# execute SQL query using execute() method, to fetch the Applications
cursor.execute("SELECT `id`,`name` FROM application")

# Fetch the application records
application_ids = {}
for app in cursor.fetchall():
    application_ids[app[1]] = int(app[0])

# execute SQL query using execute() method, to fetch the Application Assessments
cursor.execute("select p.id, pa.email, a.name from application a join process p on p.target_id = a.id and p.process_type = 'Assessment' join process_role pr on pr.id = p.participant_id join user pa on pa.id = pr.user_id")

# Fetch the assessment records and store as a map of application names to maps of assessor email addresses and their assessment ids
assessment_ids = {}
for ass in cursor.fetchall():

    application_name = ass[2]

    if application_name in assessment_ids:
        existing_record = assessment_ids[application_name]
        existing_record[ass[1]] = int(ass[0])
    else:
        first_record = {}
        first_record[ass[1]] = int(ass[0])
        assessment_ids[application_name] = first_record


# execute SQL query using execute() method, to fetch the Applications
cursor.execute("SELECT `id`,`name` FROM project")

# Fetch the application records
project_ids = {}
for app in cursor.fetchall():
    project_ids[app[1]] = int(app[0])


# disconnect from server
cursor.close()
db.close()


def getProjectId(name):
    db, cursor = connectToDb()

    # execute SQL query using execute() method, to fetch the Applications
    cursor.execute("SELECT `id` FROM project where `name` like '%" + name + "%'")

    id = cursor.fetchone()[0]

    # disconnect from server
    cursor.close()
    db.close()

    return id