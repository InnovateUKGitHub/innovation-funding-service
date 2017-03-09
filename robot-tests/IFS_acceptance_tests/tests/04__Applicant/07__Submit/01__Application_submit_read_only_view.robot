*** Settings ***
Documentation
...
...
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../FinanceSection_Commons.robot

*** Variables ***
*** Test Cases ***
Applicant submits the application
    [Documentation]
    [Tags]
    When the user navigates to the page    ${DASHBOARD_URL}
    and the user clicks the button/link      link=create new submit application
    then the applicant completes the application details    Application details
    and the user clicks the button/link     link=Return to application overview
    and the user clicks the button/link     link=Your finances
    and the user marks the finances as complete     ${Competition_E2E}
    when the user clicks the button/link     link=Review and submit
    then the user should not see the element     css=input

Application should have overheads sheet
    [Documentation]
    [Tags]
    #This test checks the overheads section is read only after applciation is submitted and has the
    Given the user navigates to Your-finances page   ${Competition_E2E}
    When The user clicks the button/link      link=Your project costs
    then the user clicks the button/link     jQuery=button:contains("Overhead costs")
    #Need to click twice to expand the dropdwon
    and the user clicks the button/link     jQuery=button:contains("Overhead costs")
    and the user should see the element      link=${excel_file}
    and the user should not see the element     css=input




