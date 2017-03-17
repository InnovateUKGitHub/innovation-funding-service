*** Settings ***
Resource          MYSQL_AND_DATE_KEYWORDS.robot
#Resource          ../variables/GLOBAL_VARIABLES.robot

*** Variables ***

*** Keywords ***


*** Test Cases ***
Custom keyword
    ${OPEN_COMPETITION}   get comp id from comp title  ${OPEN_COMPETITION_NAME}
     Set global variable     ${OPEN_COMPETITION}
    ${READY_TO_OPEN_COMPETITION}   get comp id from comp title  ${READY_TO_OPEN_COMPETITION_NAME}
     Set global variable     ${READY_TO_OPEN_COMPETITION}
    ${FUNDERS_PANEL_COMPETITION}   get comp id from comp title  ${FUNDERS_PANEL_COMPETITION_NAME}
     Set global variable     ${FUNDERS_PANEL_COMPETITION}
    ${IN_ASSESSMENT_COMPETITION}   get comp id from comp title  ${IN_ASSESSMENT_COMPETITION_NAME}
     Set global variable     ${IN_ASSESSMENT_COMPETITION}



