#!/usr/bin/python

import pytz
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


# execute SQL query using execute() method, to fetch the Projects
cursor.execute("SELECT `id`,`name` FROM project")

# Fetch the project records
project_ids = {}
for proj in cursor.fetchall():
    projectId = proj[0]
    projectName = proj[1]
    project_ids[projectName] = str(projectId)

# execute SQL query using execute() method, to fetch the Organisations
cursor.execute("SELECT `id`,`name` FROM organisation")

# Fetch the Organisation records
organisation_ids = {}
for org in cursor.fetchall():
    organisationId = org[0]
    organisationName = org[1]
    organisation_ids[organisationName] = str(organisationId)


# execute SQL query using execute() method, to fetch the Competition milestones
cursor.execute("SELECT c.id, c.name, m.type, m.date FROM competition c JOIN milestone m ON m.competition_id = c.id")

competition_milestones = {}
for comp in cursor.fetchall():

    competitionId = comp[0]
    competitionName = comp[1]
    milestoneType = comp[2]
    milestoneDateDb = comp[3]

    utc = pytz.utc
    bst = pytz.timezone('Europe/London')
    milestoneDate = utc.localize(milestoneDateDb).astimezone(bst) if milestoneDateDb is not None else None

    milestones_for_competition = competition_milestones[competitionId] if competitionId in competition_milestones else {}

    dates_for_milestone = milestones_for_competition[milestoneType] if milestoneType in milestones_for_competition else {}

    dates_for_milestone['rawDate'] = milestoneDate
    dates_for_milestone['simpleDate'] = milestoneDate.strftime('%Y-%m-%d') if milestoneDate else None            # 2002-03-28
    dates_for_milestone['prettyDayMonth'] = milestoneDate.strftime('%-d %B') if milestoneDate else None          # 4 February
    dates_for_milestone['prettyDate'] = milestoneDate.strftime('%-d %B %Y') if milestoneDate else None           # 4 February 2002
    dates_for_milestone['prettyDateTime'] = milestoneDate.strftime('%-d %B %Y %-I:%M') + milestoneDate.strftime('%p').lower() if milestoneDate else None  # 4 February 2002 2:04am
    dates_for_milestone['prettyLongDate'] = milestoneDate.strftime('%A %-d %B %Y') if milestoneDate else None    # Sunday 2 February 2002
    dates_for_milestone['prettyLongDateTime'] = milestoneDate.strftime('%A %-d %B %Y %-I:%M') + milestoneDate.strftime('%p').lower() if milestoneDate else None  # Sunday 4 February 2002 2:04am
    dates_for_milestone['prettyLongTimeDate'] = milestoneDate.strftime('%-I:%M') + milestoneDate.strftime('%p').lower() + milestoneDate.strftime(' %A %-d %B %Y') if milestoneDate else None  # 2:05am Sunday 4 February 2002
    dates_for_milestone['dateTimeDb'] = milestoneDate.strftime('%Y-%m-%d %-I:%M:%S') if milestoneDate else None  # 2002-02-02 2:05:36
    dates_for_milestone['day'] = milestoneDate.strftime('%-d') if milestoneDate else None    # 2 as day the - means that there is no 0 if date is 02
    dates_for_milestone['month'] = milestoneDate.strftime('%-m') if milestoneDate else None  # 2 as month the - means that there is no 0 if date is 02
    dates_for_milestone['year'] = milestoneDate.strftime('%Y') if milestoneDate else None    # 2002

    competition_milestones[competitionId] = milestones_for_competition
    milestones_for_competition[milestoneType] = dates_for_milestone

# format 2018-1-1 (no zero padding)
def getSimpleMilestoneDate(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['simpleDate']

# format 1 January (no zero padding)
def getPrettyMilestoneDayMonth(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['prettyDayMonth']

# format 1 January 2018 (no zero padding)
def getPrettyMilestoneDate(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['prettyDate']

# format 1 January 2018 9:01am (no zero padding on day and hour)
def getPrettyMilestoneDateTime(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['prettyDateTime']

# format Monday 1 January 2018 (no zero padding on day)
def getPrettyLongMilestoneDate(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['prettyLongDate']

# format Monday 1 January 2018 9:01am (no zero padding on day and hour)
def getPrettyLongMilestoneDateTime(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['prettyLongDateTime']

# format 9:01am Monday 1 January 2018 (no zero padding on day and hour)
def getPrettyLongMilestoneTimeDate(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['prettyLongTimeDate']

# format 2018-01-01 00:00:00 - perfect format for database updates
def getMilestoneDateTimeDb(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['dateTimeDb']

# format 1 (no zero padding on day)
def getMilestoneDay(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['day']

# format 1 (no zero padding on month)
def getMilestoneMonth(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['month']

# format 2018
def getMilestoneYear(competitionId, milestoneType):
    return competition_milestones[competitionId][milestoneType]['year']

# disconnect from server
cursor.close()
db.close()


# different from the project_ids dictionary that we create during startup, this method can be used to look up
# new project ids that were not present during the start of the test runs
def getProjectId(name):
    db, cursor = connectToDb()

    # execute SQL query using execute() method, to fetch the Projects
    cursor.execute("SELECT `id` FROM project where `name` = '" + name + "'")

    id = cursor.fetchone()[0]

    # disconnect from server
    cursor.close()
    db.close()

    return id

# One can use this function in order to request the User Id of a user, by providing his email address.
def getUserId(email):
    db, cursor = connectToDb()

    # execute SQL query using execute() method, to fetch the Users
    cursor.execute("SELECT `id` FROM user where `email` = '" + email + "'")

    id = cursor.fetchone()[0]

    # disconnect from server
    cursor.close()
    db.close()

    return id