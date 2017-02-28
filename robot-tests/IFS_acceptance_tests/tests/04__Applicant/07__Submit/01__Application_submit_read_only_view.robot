*** Settings ***
Documentation     -INFUND-172: As a lead applicant and I am on the application summary, I can submit the application, so I can verify it that it is ready for submission
...
...
...
Suite Setup
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../FinanceSection_Commons.robot

*** Variables ***

*** Test Cases ***
Applicant submits the application
    [Documentation]
    [Tags]
    [Setup] Login as lead applicant
    When The user navigates to the page     dashboard
    and the user clicks the button/link       ${New_innovation_solution}
    then the applicant completes the application details    Application details
    and user marks the finances as complete
    when the user clicks the button/link     link=Review and submit
    then the user should not see the element     css=input

Application should have overheads sheet
    [Documentation]
    [Tags]
    #This test checks the overheads section is read only after applciation is submitted and has the
    Given the user navigates to Your-finances page   ${New_innovation_solution}
    When the user clicks the button/link     jQuery=button:contains("Overhead costs")
    then the user should see the element      link=${excel_file}
    and the user should not see the element     css=input




