*** Settings ***
Documentation     -INFUND-172: As a lead applicant and I am on the application summary, I can submit the application, so I can verify it that it is ready for submission
...
...
...
Suite Setup       new account complete all but one
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../FinanceSection_Commons.robot

*** Variables ***

*** Test Cases ***
