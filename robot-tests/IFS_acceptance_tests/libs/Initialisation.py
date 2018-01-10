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
    competitionId = comp[0]
    competitionName = comp[1]
    if comp[1] is None:
        competition_ids['none'] = str(competitionId)
    else:
        competition_ids[competitionName] = str(competitionId)

cursor.execute("SELECT `id`,`email` FROM user")

user_ids = {}
for user in cursor.fetchall():
    userId = user[0]
    userEmail = user[1]
    user_ids[userEmail] = str(userId)

# execute SQL query using execute() method, to fetch the Applications
cursor.execute("SELECT `id`,`name` FROM application")

# Fetch the application records
application_ids = {}
for app in cursor.fetchall():
    applicationId = app[0]
    applicationName = app[1]
    application_ids[applicationName] = str(applicationId)

# execute SQL query using execute() method, to fetch the Application Assessments
cursor.execute("select p.id, pa.email, a.name from application a join process p on p.target_id = a.id and p.process_type = 'Assessment' join process_role pr on pr.id = p.participant_id join user pa on pa.id = pr.user_id")

# Fetch the assessment records and store as a map of application names to maps of assessor email addresses and their assessment ids
assessment_ids = {}
for ass in cursor.fetchall():
    assessmentId = ass[0]
    assessorEmail = ass[1]
    applicationName = ass[2]

    if applicationName in assessment_ids:
        existing_record = assessment_ids[applicationName]
        existing_record[assessorEmail] = str(assessmentId)
    else:
        first_record = {}
        first_record[assessorEmail] = str(assessmentId)
        assessment_ids[applicationName] = first_record


# execute SQL query using execute() method, to fetch the Applications
cursor.execute("SELECT `id`,`name` FROM project")

# Fetch the project records
project_ids = {}
for proj in cursor.fetchall():
    projectId = proj[0]
    projectName = proj[1]
    project_ids[projectName] = str(projectId)


# disconnect from server
cursor.close()
db.close()


# different from the project_ids dictionary that we create during startup, this method can be used to look up
# new project ids that were not present during the start of the test runs
def getProjectId(name):
    db, cursor = connectToDb()

    # execute SQL query using execute() method, to fetch the Applications
    cursor.execute("SELECT `id` FROM project where `name` = '" + name + "'")

    id = cursor.fetchone()[0]

    # disconnect from server
    cursor.close()
    db.close()

    return id