#!/usr/bin/python
import MySQLdb

_open_competition_name = 'Connected digital additive manufacturing'
_ready_to_open_competition_name = 'Photonics for health'
_funders_panel_competition_name = 'Internet of Things'
_in_assessment_competition_name = 'Sustainable living models for the future'
_competitionList = { _open_competition_name, _ready_to_open_competition_name, _funders_panel_competition_name, _in_assessment_competition_name}

# Open database connection
db = MySQLdb.connect("ifs-database","root","password","ifs" )

# prepare a cursor object using cursor() method
cursor = db.cursor()

for comp in _competitionList:
    sql = "SELECT * FROM competition \
       WHERE name = '%s'" % (comp)

    try:
        # Execute the SQL command
        cursor.execute(sql)
        # Fetch all the rows in a list of lists.
        results = cursor.fetchall()
        for row in results:
            if comp == _open_competition_name:
                OPEN_COMPETITION_ID = row[0]
                break
            if comp == _ready_to_open_competition_name:
                READY_TO_OPEN_COMPETITION_ID = row[0]
                break
            if comp == _in_assessment_competition_name:
                IN_ASSESSMENT_COMPETITION_ID = row[0]
                break
            if comp == _funders_panel_competition_name:
                FUNDERS_PANEL_COMPETITION_ID = row[0]
                break
    except:
        print "Error: unable to fetch data"

# disconnect from server
db.close()


