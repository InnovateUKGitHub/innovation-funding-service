*** Settings ***
Resource          GLOBAL_LIBRARIES.robot
#Resource          MYSQL_AND_DATE_KEYWORDS.robot
#Resource          ../variables/GLOBAL_VARIABLES.robot
Resource          ../GLOBAL_LIBRARIES.robot

*** Variables ***
${database_name}    ifs
${database_user}    root
${database_password}    password
${database_host}    ifs-database
${database_port}    3306
@{database}       pymysql    ${database_name}    ${database_user}    ${database_password}    ${database_host}    ${database_port}


*** Test Cases ***
Custom keyword
    ${OPEN_COMPETITION} =  get comp id from comp title  Connected digital additive manufacturing
    Set Global Variable  ${OPEN_COMPETITION}
    ${READY_TO_OPEN_COMPETITION} =  get comp id from comp title  Photonics for health
    Set Global Variable  ${READY_TO_OPEN_COMPETITION}
    ${FUNDERS_PANEL_COMPETITION}   get comp id from comp title  Internet of Things
    Set global variable     ${FUNDERS_PANEL_COMPETITION}
    ${IN_ASSESSMENT_COMPETITION}   get comp id from comp title  Sustainable living models for the future
    Set global variable     ${IN_ASSESSMENT_COMPETITION}

*** Keywords ***
get comp id from comp title
    [Arguments]  ${title}
    Connect to Database     @{database}
    ${result} =  query  SELECT `id` FROM `${database_name}`.`competition` WHERE `name`='${title}';
    Log  ${result}
    # the result of this query looks like ((13,),) so you need get the value array[0][0]
    ${result} =  get from list  ${result}  0
    ${competitionId} =  get from list  ${result}  0
    Log  ${competitionId}
    [Return]  ${competitionId}


