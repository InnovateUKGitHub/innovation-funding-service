*** Settings ***
Documentation     -INFUND-3382: Create delete email steps at the start of test runs
Force Tags        Email    Pending    # This Pending tag is in place to prevent this keyword from running as part of a normal test run
...                                   # However, it is still being used as part of the micro_run_tests script - please do not remove during refactoring!
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Empty test mailboxes to set up for email enabled test runs
    [Documentation]    INFUND-3822
    [Tags]
    Delete the emails from both test mailboxes
