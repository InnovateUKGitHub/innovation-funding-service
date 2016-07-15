*** Settings ***
Documentation     -INFUND-3382: Create delete email steps at the start of test runs
Force Tags        Email
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot


*** Test Cases ***

Empty test mailboxes to set up for email enabled test runs
    [Documentation]   INFUND-3822
    [Tags]    HappyPath
    Delete the emails from both test mailboxes