*** Settings ***
Documentation     IFS-11252 EDI Section to Applicant Profile
...
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***



*** Test Cases ***
Applicant can view EDI section in profile page
    [Documentation]  IFS-11252
    Given the user logs-in in new browser  	        &{lead_applicant_credentials}
    When the user clicks the button/link            link = Profile
    Then the user should see EDI section details    Incomplete  Not Applicable  Start now

Applicant can view the EDI incomplete status
    [Documentation]  IFS-11252
    When the user changed EDI survey status         INPROGRESS  2022-01-22 01:02:03
    Then the user should see EDI section details    Incomplete  01 January 2022  Continue

Applicant can view the EDI compelete status
    [Documentation]  IFS-11252
    When the user changed EDI survey status         COMPLETE  2023-03-25 01:02:03
    Then the user should see EDI section details    Complete  25 March 2024  Review EDI summary

*** Keywords ***
Custom Suite Setup
    Connect to Database    @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user should see EDI section details
    [Arguments]  ${ediStatus}  ${ediReviewDate}  ${ediButton}
    the user should see the element  jQuery = h2:contains("Equality, diversity and inclusion")
    the user should see the element  jQuery = p:contains("Please complete our EDI monitoring survey. It helps us ensure we treat everyone who engages with us fairly and equally. The survey is mandatory and should takes no more than 10 minutes. You will be redirected to another page to complete this survey, but you can return here at any point.")
    the user should see the element  jQuery = th:contains("Survey status")+td:contains("${ediStatus}")
    the user should see the element  jQuery = th:contains("Last reviewed")+td:contains("${ediReviewDate}")
    the user should see the element  jQuery = a:contains("${ediButton}")
    the user should see the element  css=[href="https://loans-innovateuk.cs80.force.com/EDI/s"]

the user changed EDI survey status
    [Arguments]  ${ediStatus}  ${ediReviewDate}
    execute sql string   UPDATE `${database_name}`.`user` SET `edi_status` = '${ediStatus}', `edi_review_date` = '${ediReviewDate}' WHERE (`email` = 'steve.smith@empire.com');
    reload page
