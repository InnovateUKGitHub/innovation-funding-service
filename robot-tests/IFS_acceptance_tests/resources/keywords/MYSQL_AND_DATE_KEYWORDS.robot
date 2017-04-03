*** Settings ***
Resource          ../GLOBAL_LIBRARIES.robot
Resource          ../variables/GLOBAL_VARIABLES.robot
Resource          ../variables/User_credentials.robot
Resource          Login_actions.robot
Resource          User_actions.robot

*** Variables ***
@{database}       pymysql    ${database_name}    ${database_user}    ${database_password}    ${database_host}    ${database_port}

*** Keywords ***
the assessment start period changes in the db in the past
    ${today}=    get time
    ${yesterday} =    Subtract Time From Date    ${today}    1 day
    When execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`='${UPCOMING_COMPETITION_TO_ASSESS_ID}' and type IN ('OPEN_DATE', 'SUBMISSION_DATE', 'ASSESSORS_NOTIFIED');
    And reload page

the calculation of the remaining days should be correct
    [Arguments]    ${END_DATE}
    ${CURRENT_DATE}=    Get Current Date    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d    exclude_millis=true
    ${MILESTONE_DATE}=    Convert Date    ${END_DATE}    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.my-applications .msg-deadline .days-remaining
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

the total calculation in dashboard should be correct
    [Arguments]    ${TEXT}    ${Section_Xpath}
    [Documentation]    This keyword uses 2 arguments. The first one is about the page's text (competition or application) and the second is about the Xpath selector.
    ${NO_OF_COMP_OR_APPL}=    Get Matching Xpath Count    ${Section_Xpath}
    Page Should Contain    ${TEXT} (${NO_OF_COMP_OR_APPL})

The assessment deadline for the ${IN_ASSESSMENT_COMPETITION_NAME} changes to the past
    ${today}=    get time
    ${yesterday} =    Subtract Time From Date    ${today}    1 day
    When execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`='${IN_ASSESSMENT_COMPETITION}' and type = 'ASSESSOR_DEADLINE';
    And reload page

the days remaining should be correct (Top of the page)
    [Arguments]    ${END_DATE}
    ${CURRENT_DATE}=    Get Current Date    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d    exclude_millis=true
    ${MILESTONE_DATE}=    Convert Date    ${END_DATE}    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.sub-header .deadline span
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

the days remaining should be correct (Applicant's dashboard)
    [Arguments]    ${END_DATE}
    ${CURRENT_DATE}=    Get Current Date    result_format=%Y-%m-%d    exclude_millis=true
    ${MILESTONE_DATE}=    Convert Date    ${END_DATE}    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${CURRENT_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.in-progress li:nth-child(2) .days-remaining
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

get yesterday
    ${today} =    get time
    ${yesterday} =    Subtract Time From Date    ${today}    1 day
    [Return]    ${yesterday}

get today
    ${today} =    Get Current Date    result_format=%-d %B %Y    exclude_millis=true
    [Return]    ${today}

get tomorrow full
    ${today}=    get time
    ${tomorrow} =    Add time To Date  ${today}  1 day  result_format=%-d %B %Y  exclude_millis=true
    # This format is like: 4 February 2017
    [Return]  ${tomorrow}

get tomorrow day
    ${today}=    get time
    ${tomorrow} =    Add time To Date  ${today}  1 day  result_format=%d  exclude_millis=true
    [Return]  ${tomorrow}

get the day after tomorrow
    ${today}=    get time
    ${aftertomorrow} =    Add time To Date  ${today}  2 days  result_format=%d  exclude_millis=true
    [Return]  ${aftertomorrow}

get the day after tomorrow full next year
    ${today} =    get time
    ${tommorow} =  Add time To Date  ${today}  2 days  result_format=%-d %B  exclude_millis=true
    ${nextyear} =  get next year
    ${tomorrow_nextyear} =  Catenate  ${tommorow}  ${nextyear}
    [Return]  ${tomorrow_nextyear}

get tomorrow month
    ${today}=    get time
    ${tomorrow} =    Add time To Date  ${today}  1 day  result_format=%m  exclude_millis=true
    [Return]  ${tomorrow}

get tomorrow year
    ${today}=    get time
    ${tomorrow} =    Add time To Date  ${today}  1 day  result_format=%Y  exclude_millis=true
    [Return]  ${tomorrow}

get next year
    ${year} =  get time    year    NOW + 370d
    [Return]  ${year}

get comp id from comp title
    [Arguments]  ${title}
    ${result} =  query  SELECT `id` FROM `${database_name}`.`competition` WHERE `name`='${title}';
    Log  ${result}
    # the result of this query looks like ((13,),) so you need get the value array[0][0]
    ${result} =  get from list  ${result}  0
    ${competitionId} =  get from list  ${result}  0
    Log  ${competitionId}
    [Return]  ${competitionId}