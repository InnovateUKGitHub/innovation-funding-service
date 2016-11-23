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
    When execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`='${READY_TO_OPEN_COMPETITION}' and type IN ('OPEN_DATE', 'SUBMISSION_DATE', 'ASSESSORS_NOTIFIED');
    And reload page

the calculation of the remaining days should be correct
    [Arguments]    ${END_DATE}
    ${CURRENT_DATE}=    Get Current Date    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d    exclude_millis=true
    ${MILESTONE_DATE}=    Convert Date    ${END_DATE}    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.my-applications div:nth-child(2) .pie-container .pie-overlay .day
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

the total calculation in dashboard should be correct
    [Arguments]    ${TEXT}    ${Section_Xpath}
    [Documentation]    This keyword uses 2 arguments. The first one is about the page's text (competition or application) and the second is about the Xpath selector.
    ${NO_OF_COMP_OR_APPL}=    Get Matching Xpath Count    ${Section_Xpath}
    Page Should Contain    ${TEXT} (${NO_OF_COMP_OR_APPL})

The assessment deadline for the Juggling Craziness changes to the past
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
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.sub-header .pie-overlay .day
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

the days remaining should be correct (Applicant's dashboard)
    [Arguments]    ${END_DATE}
    ${CURRENT_DATE}=    Get Current Date    result_format=%Y-%m-%d    exclude_millis=true
    ${MILESTONE_DATE}=    Convert Date    ${END_DATE}    result_format=%Y-%m-%d    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${MILESTONE_DATE}    ${CURRENT_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.in-progress li:nth-child(1) div:nth-child(2) .pie-container .pie-overlay .day
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

get yesterday
    ${today} =    get time
    ${yesterday} =    Subtract Time From Date    ${today}    1 day
    [Return]    ${yesterday}

Change the open date of the Sarcasm Stupendousness in the database to one day before
    ${yesterday} =    get yesterday
    When execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${yesterday}' WHERE `competition_id`='${READY_TO_OPEN_COMPETITION}' and type = 'OPEN_DATE';
    And the user reloads the page
    Then element should not contain    jQuery=section:nth-child(4)    Sarcasm Stupendousness
