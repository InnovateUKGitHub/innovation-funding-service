*** Settings ***
Resource          ../defaultResources.robot

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
    ${GET_TIME}=    get time    hour    UTC
    ${TIME}=    Convert To Number    ${GET_TIME}
    ${CURRENT_DATE}=    Get Current Date    UTC    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Run keyword if    ${TIME} >= 12    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d
    ...    exclude_millis=true
    ...    ELSE    set variable    ${CURRENT_DATE}
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
    [Arguments]    ${END_DATE}
    ${GET_TIME}=    get time    hour    UTC
    ${TIME}=    Convert To Number    ${GET_TIME}
    ${CURRENT_DATE}=    Get Current Date    UTC    result_format=%Y-%m-%d    exclude_millis=true
    ${STARTING_DATE}=    Run keyword if    ${TIME} >= 11    Add Time To Date    ${CURRENT_DATE}    1 day    result_format=%Y-%m-%d
    ...    exclude_millis=true
    ...    ELSE    set variable    ${CURRENT_DATE}
    ${NO_OF_DAYS_LEFT}=    Subtract Date From Date    ${END_DATE}    ${STARTING_DATE}    verbose    exclude_millis=true
    ${NO_OF_DAYS_LEFT}=    Remove String    ${NO_OF_DAYS_LEFT}    days
    ${SCREEN_NO_OF_DAYS_LEFT}=    Get Text    css=.in-progress li:nth-child(3) .days-remaining
    Should Be Equal As Numbers    ${NO_OF_DAYS_LEFT}    ${SCREEN_NO_OF_DAYS_LEFT}

get yesterday
    ${today} =    Get Time
    ${yesterday} =    Subtract Time From Date    ${today}    1 day
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

get fifteen days
    ${today} =    Get Time
    ${fifteen} =     Add time To Date    ${today}    15 day
    [Return]    ${fifteen}

get tomorrow day
    ${today}=    get time
    ${tomorrow} =    Add time To Date    ${today}    1 day    result_format=%d    exclude_millis=true
    [Return]    ${tomorrow}

get tomorrow month
    ${today}=    get time
    ${tomorrow} =    Add time To Date    ${today}    1 day    result_format=%m    exclude_millis=true
    [Return]    ${tomorrow}

get next month
    ${today}=  get time
    ${month} =  Add time To Date  ${today}    31 days    result_format=%m    exclude_millis=true
    [Return]    ${month}

get next month as word
    ${today}=  get time
    ${month} =  Add time To Date  ${today}    31 days    result_format=%B    exclude_millis=true
    # This format is like June instead of 06
    [Return]    ${month}

get next year
    ${year} =    get time    year    NOW + 370d
    [Return]    ${year}

get comp id from comp title
    [Arguments]    ${title}
    Connect to Database    @{database}
    ${result} =    query    SELECT `id` FROM `${database_name}`.`competition` WHERE `name`='${title}';
    Log    ${result}
    # the result of this query looks like ((13,),) so you need get the value array[0][0]
    ${result} =    get from list    ${result}    0
    ${competitionId} =    get from list    ${result}    0
    Log    ${competitionId}
    [Return]    ${competitionId}

get application id by name
    [Arguments]   ${name}
    Connect to Database    @{database}
    ${result} =    query    SELECT `id` FROM `${database_name}`.`application` WHERE `name`='${name}';
    ${result} =    get from list    ${result}    0
    ${applicationId} =    get from list    ${result}    0
    [Return]    ${applicationId}

# The below keyword gets date from first selector and checks if it is greater than the date from second selector
# For example 12 February 2018 > 26 January 2017 . Greater in this case means latest.
verify first date is greater than or equal to second
    [Arguments]  ${selector1}   ${selector2}
    ${date_in_text_format1}=  Get text  ${selector1}
    ${date1}=  Convert Date  ${date_in_text_format1}  date_format=%d %B %Y  exclude_millis=true
    ${date_in_text_format2}=  Get text  ${selector2}
    ${date2}=  Convert Date  ${date_in_text_format2}  date_format=%d %B %Y  exclude_millis=true
    Should be true  '${date1}'>='${date2}'
