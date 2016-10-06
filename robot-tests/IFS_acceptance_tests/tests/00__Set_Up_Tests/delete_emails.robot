*** Settings ***
Documentation     -INFUND-3382: Create delete email steps at the start of test runs
Force Tags        Email    SmokeTest    HappyPath
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot
Resource          ../../resources/keywords/EMAIL_KEYWORDS.robot

*** Test Cases ***
Empty test mailboxes to set up for email enabled test runs
    [Documentation]    INFUND-3822
    [Tags]
    Delete the emails from both test mailboxes
