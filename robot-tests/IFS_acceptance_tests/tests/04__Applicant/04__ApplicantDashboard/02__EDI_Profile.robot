*** Settings ***
Documentation     IFS-11252 EDI Section to Applicant Profile
...
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${competitionNameEDI}   Performance testing competition
${applicationNameEDI}   EDI Application


*** Test Cases ***
Applicant can view EDI section in profile page
    [Documentation]  IFS-11252
    Given the user logs-in in new browser  	        &{lead_applicant_credentials}
    When the user clicks the button/link            link = Profile
    Then the user should see EDI section details    Incomplete  Not Applicable  Start now

Applicant checks the status of EDI as Incomplete When user not started the edi survey
    [Documentation]  IFS-11253
    Given the user creates a new application
    And the user fills in the EDI application details   ${applicationNameEDI}  ${tomorrowday}  ${month}  ${nextyear}
    When the user clicks the button/link                link = Application team
    Then the user should see the element                jQuery = td:contains("Steve Smith") ~ td:contains("Incomplete") ~ td:contains("Lead applicant")

Lead applicant can not mark the application team as complete when the edi survey is not started
    [Documentation]  IFS-11253
    When the user clicks the button/link                   id = application-question-complete
    Then the user should see a field and summary error     Complete our equality,diversity and inclusion survey.

Applicant can view the EDI incomplete status
    [Documentation]  IFS-11252
    Given the user clicks the button/link           jQuery = a:contains("here")
    When the user changed EDI survey status         INPROGRESS  2076-01-22 01:02:03
    Then the user should see EDI section details    Incomplete  22 January 2076  Continue

Applicant can not mark the application team as complete when edi survey is not completed by lead
    [Documentation]  IFS-11253
    [Setup]  get application Id                            ${applicationNameEDI}
    Given the user navigates to the page                   ${server}/application/${applicationIdEDI}
    When the user clicks the button/link                   link = Application team
    And the user clicks the button/link                    id = application-question-complete
    Then the user should see a field and summary error     Complete our equality,diversity and inclusion survey.

Lead applicant adds a partner organisation and check the status of edi as incomplete
    [Documentation]  IFS-11253
    Given the user clicks the button/link              link = Application overview
    When the lead invites already registered user      ${collaborator1_credentials["email"]}  ${competitionNameEDI}
    And logging in and error checking                  ${collaborator1_credentials["email"]}  ${short_password}
    And the user clicks the button/link                jQuery = button:contains("Save and continue")
    And the user clicks the button/link                link = Application team
    Then the user should see the element               jQuery = td:contains("Jessica Doe") ~ td:contains("Incomplete")

Applicant can view the EDI status as complete in profile
    [Documentation]  IFS-11252
    Given log in as a different user                &{lead_applicant_credentials}
    When the user clicks the button/link            link = Profile
    And the user changed EDI survey status          COMPLETE  2089-03-25 01:02:03
    Then the user should see EDI section details    Complete  25 March 2089  Review EDI summary

Lead applicant can mark the application team as complete when edi status is complete for lead applicant
    [Documentation]  IFS-11253
    Given the user navigates to the page            ${server}/application/${applicationIdEDI}
    When the user clicks the button/link            link = Application team
    And the user clicks the button/link             id = application-question-complete
    And the user clicks the button/link             link = Application overview
    Then the user should see the element            jQuery = li:contains("Application team") > .task-status-complete

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
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
    the user clicks the button/link             link = Edit your details
    the user clicks the button/link             jQuery = button:contains("Save changes")

the user creates a new application
    the user select the competition and starts application     ${competitionNameEDI}
    the user selects the radio button                          createNewApplication  true
    the user clicks the button/link                            jQuery = .govuk-button:contains("Continue")
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible  jQuery = label:contains("Empire Ltd")
    Run Keyword if  '${status}' == 'PASS'    the user clicks the button twice   jQuery = label:contains("Empire Ltd")
    the user clicks the button/link                            jQuery = button:contains("Save and continue")

get application Id
    [Arguments]  ${appName}
    ${applicationIdEDI} =  get application id by name    ${appName}
    Set suite variable    ${applicationIdEDI}

the user fills in the EDI application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user clicks the button/link       link = Application details
    the user enters text to a text field  id = name  ${appTitle}
    the user enters text to a text field  id = startDate  ${tomorrowday}
    the user enters text to a text field  css = #application_details-startdate_month  ${month}
    the user enters text to a text field  css = #application_details-startdate_year  ${nextyear}
    the user enters text to a text field  css = [id="durationInMonths"]  24
    the user can mark the question as complete
    the user should see the element       jQuery = li:contains("Application details") > .task-status-complete