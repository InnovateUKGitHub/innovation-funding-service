*** Settings ***
Resource          ../defaultResources.robot

*** Variables ***
@{database}       pymysql    ${database_name}    ${database_user}    ${database_password}    ${database_host}    ${database_port}

*** Keywords ***
the assessment start period changes in the db in the past
    [Arguments]   ${competition_id}
    ${yesterday} =    get yesterday
    When execute sql string     INSERT IGNORE INTO `${database_name}`.`milestone` (date, type, competition_id) VALUES('${yesterday}', 'OPEN_DATE', '${competition_id}'), ('${yesterday}', 'SUBMISSION_DATE', '${competition_id}'), ('${yesterday}', 'ASSESSORS_NOTIFIED', '${competition_id}');
    And execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`='${competition_id}' and type IN ('OPEN_DATE', 'SUBMISSION_DATE', 'ASSESSORS_NOTIFIED');
    And reload page

the submission date changes in the db in the past
    [Arguments]   ${competition_id}
    ${yesterday} =    get yesterday
    execute sql string  INSERT IGNORE INTO `${database_name}`.`milestone` (date, type, competition_id) VALUES('${yesterday}', 'OPEN_DATE', '${competition_id}'), ('${yesterday}', 'SUBMISSION_DATE', '${competition_id}');
    execute sql string  UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`='${competition_id}' and type IN ('OPEN_DATE', 'SUBMISSION_DATE');
    reload page

the calculation of the remaining days should be correct
    [Arguments]    ${END_DATE}    ${COMPETITION_ID}
    ${GET_TIME}=    get time    hour    UTC
    ${TIME}=    Convert To Number    ${GET_TIME}
    ${CURRENT_DATE}=    Get Current Date    UTC    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Run keyword if    ${TIME} >= 12    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d
    ...    exclude_millis=true
    ...    ELSE    set variable    ${CURRENT_DATE}
    ${MILESTONE_DATE}=    Convert Date    ${END_DATE}    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.my-applications .msg-deadline[data-competition-id='${COMPETITION_ID}'] .days-remaining
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

the total calculation in dashboard should be correct
    [Arguments]    ${TEXT}    ${Section_Xpath}
    [Documentation]    This keyword uses 2 arguments. The first one is about the page's text (competition or application) and the second is about the Xpath selector.
    ${NO_OF_COMP_OR_APPL}=    Get Matching Xpath Count    ${Section_Xpath}
    Page Should Contain    ${TEXT} (${NO_OF_COMP_OR_APPL})

The assessment deadline for the ${IN_ASSESSMENT_COMPETITION_NAME} changes to the past
    ${yesterday} =   get yesterday
    When execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`='${IN_ASSESSMENT_COMPETITION}' and type = 'ASSESSOR_DEADLINE';
    And reload page

the days remaining should be correct (Top of the page)
    [Arguments]    ${END_DATE}
    ${GET_TIME}=    get time    hour    UTC
    ${TIME}=    Convert To Number    ${GET_TIME}
    ${CURRENT_DATE}=    Get Current Date    UTC    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Run keyword if    ${TIME} >= 12    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d
    ...    exclude_millis=true
    ...    ELSE    set variable    ${CURRENT_DATE}
    ${MILESTONE_DATE}=    Convert Date    ${END_DATE}    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    #Get the text from the deadline element
    ${string}=  Get Text  css=.sub-header .deadline
    #Extract the numbers from the string with regexp
    ${SCREEN_NO_OF_DAYS_LEFT_LIST}=    get regexp matches    ${string}    \\d+
    #Get the first item from the matched array of numbers
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get From List    ${SCREEN_NO_OF_DAYS_LEFT_LIST}    0
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

the days remaining should be correct (Applicant's dashboard)
    [Arguments]    ${END_DATE}  ${applicationName}
    ${GET_TIME}=    get time    hour    UTC
    ${TIME}=    Convert To Number    ${GET_TIME}
    ${CURRENT_DATE}=    Get Current Date    UTC    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Run keyword if    ${TIME} >= 11    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d
    ...    exclude_millis=true
    ...    ELSE    set variable    ${CURRENT_DATE}
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${END_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    jQuery=.in-progress li:contains("${applicationName}") .days-remaining
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

Get competitions id and set it as suite variable
    [Arguments]  ${competitionTitle}
    ${competitionId} =  get comp id from comp title  ${competitionTitle}
    Set suite variable  ${competitionId}

Save competition's current dates
    [Arguments]  ${competitionId}
    Connect to Database  @{database}
    ${result} =  Query  SELECT DATE_FORMAT(`date`, '%Y-%l-%d %H:%i:%s') FROM `${database_name}`.`milestone` WHERE `competition_id`='${competitionId}' AND type='OPEN_DATE';
    ${result} =  get from list  ${result}  0
    ${openDate} =  get from list  ${result}  0
    ${result} =  Query  SELECT DATE_FORMAT(`date`, '%Y-%l-%d %H:%i:%s') FROM `${database_name}`.`milestone` WHERE `competition_id`='${competitionId}' AND type='SUBMISSION_DATE';
    ${result} =  get from list  ${result}  0
    ${submissionDate} =  get from list  ${result}  0
    [Return]  ${openDate}  ${submissionDate}

Return the competition's milestones to their initial values
    [Arguments]  ${competitionId}  ${openDate}  ${submissionDate}
    Connect to Database  @{database}
    Execute SQL String  UPDATE `${database_name}`.`milestone` SET `date`='${openDate}' WHERE `competition_id`='${competitionId}' AND `type`='OPEN_DATE';
    Execute SQL String  UPDATE `${database_name}`.`milestone` SET `date`='${submissionDate}' WHERE `competition_id`='${competitionId}' AND `type`='SUBMISSION_DATE';

The competitions date changes so it is now Open
    [Arguments]  ${competition}
    Connect to Database  @{database}
    Change the open date of the Competition in the database to one day before  ${competition}
    the user navigates to the page   ${CA_Live}
    the user should see the element  jQuery=h2:contains("Open") ~ ul a:contains("${competition}")

Change the open date of the Competition in the database to one day before
    [Arguments]  ${competitionName}
    ${yesterday} =  get yesterday
    ${competitionId} =  get comp id from comp title  ${competitionName}
    execute sql string  UPDATE `${database_name}`.`milestone` SET `date`='${yesterday}' WHERE `competition_id`='${competitionId}' AND `type` = 'OPEN_DATE';

Change the close date of the Competition in the database to tomorrow
    [Arguments]  ${competition}
    ${tomorrow} =  get tomorrow
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${tomorrow}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'SUBMISSION_DATE';

Change the close date of the Competition in the database to a fortnight
    [Arguments]  ${competition}
    ${fortnight} =  get fortnight
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${fortnight}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'SUBMISSION_DATE';

Change the close date of the Competition in the database to thirteen days
    [Arguments]  ${competition}
    ${thirteen} =  get thirteen days
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${thirteen}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'SUBMISSION_DATE';

Change the open date of the Competition in the database to tomorrow
    [Arguments]  ${competition}
    ${tomorrow} =  get tomorrow
    Connect to Database    @{database}
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${tomorrow}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'OPEN_DATE';

get yesterday
    ${today} =    Get Time
    ${yesterday} =  Subtract Time From Date  ${today}  1 day  exclude_millis=True
    [Return]    ${yesterday}

get today
    ${today} =    Get Current Date  UTC   result_format=%-d %B %Y    exclude_millis=true
    # This format is like: 4 February 2017
    [Return]    ${today}

get today short month
    ${today} =    Get Current Date  UTC   result_format=%-d %b %Y    exclude_millis=true
    # This format is like: 4 Feb 2017
    [Return]    ${today}

get tomorrow
    ${today} =    Get Time
    ${tomorrow} =     Add time To Date    ${today}    1 day
    [Return]    ${tomorrow}

get fortnight
    ${today} =    Get Time
    ${fortnight} =     Add time To Date    ${today}    14 day
    [Return]    ${fortnight}

get thirteen days
    ${today} =    Get Time
    ${thirteen} =     Add time To Date    ${today}    13 day
    [Return]    ${thirteen}

get tomorrow day
    ${today}=    get time
    ${tomorrow} =    Add time To Date    ${today}    1 day    result_format=%d    exclude_millis=true
    [Return]    ${tomorrow}

get tomorrow month
    ${today}=    get time
    ${tomorrow} =    Add time To Date    ${today}    1 day    result_format=%m    exclude_millis=true
    [Return]    ${tomorrow}

get tomorrow month as word
    ${today}=    get time
    ${tomorrowMonthWord} =    Add time To Date    ${today}    1 day    result_format=%B    exclude_millis=true
    [Return]    ${tomorrowMonthWord}

get next month
    ${today}=  get time
    ${month} =  Add time To Date  ${today}    31 days    result_format=%m    exclude_millis=true
    [Return]    ${month}

get month as word
    ${month} =  Get Current Date  UTC   result_format=%B    exclude_millis=true
    # This format is like June instead of 06
    [Return]    ${month}

get next year
    ${year} =    get time    year    NOW + 370d
    [Return]    ${year}

get comp id from comp title
    [Arguments]  ${name}
    ${id} =   get table id by name  competition  ${name}
    [Return]  ${id}

get application id by name
    [Arguments]  ${name}
    ${id} =   get table id by name  application  ${name}
    [Return]  ${id}

get project id by name
    [Arguments]  ${name}
    ${id} =   get table id by name  project  ${name}
    [Return]  ${id}

get organisation id by name
    [Arguments]  ${name}
    ${id} =   get table id by name  organisation  ${name}
    [Return]  ${id}

get table id by name
    [Arguments]  ${table}  ${name}
    Connect to Database  @{database}
    ${result} =  query  SELECT `id` FROM `${database_name}`.`${table}` WHERE `name`="${name}";
    # the result of this query looks like ((13,),) so you need get the value array[0][0]
    ${result} =  get from list  ${result}  0
    ${id} =      get from list  ${result}  0
    [Return]  ${id}

# The below keyword gets date from first selector and checks if it is greater than the date from second selector
# For example 12 February 2018 > 26 January 2017 . Greater in this case means latest.
verify first date is greater than or equal to second
    [Arguments]  ${selector1}   ${selector2}
    ${date_in_text_format1}=  Get text  ${selector1}
    ${date1}=  Convert Date  ${date_in_text_format1}  date_format=%d %B %Y  exclude_millis=true
    ${date_in_text_format2}=  Get text  ${selector2}
    ${date2}=  Convert Date  ${date_in_text_format2}  date_format=%d %B %Y  exclude_millis=true
    Should be true  '${date1}'>='${date2}'

Set predefined date variables
    ${status}  ${value} =  run keyword and ignore error  Variable Should Exist  ${tomorrowday}
    Run Keyword If  '${status}' == 'FAIL'  Set global date variables

Set global date variables
    ${month} =          get tomorrow month
    set global variable  ${month}
    ${nextMonth} =  get next month
    set global variable  ${nextMonth}
    ${nextyear} =       get next year
    Set global variable  ${nextyear}
    ${tomorrowday} =    get tomorrow day
    Set global variable  ${tomorrowday}
    ${monthWord} =      get month as word
    set global variable  ${monthWord}