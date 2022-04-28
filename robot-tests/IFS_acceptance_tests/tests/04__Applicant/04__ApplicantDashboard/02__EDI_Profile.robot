*** Settings ***
Documentation    IFS-11252 EDI Section to Applicant Profile
...
...              IFS-11490 EDI: open in second tab
...
...              IFS-11534 Applicant/Assessors for EDI
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot


*** Variables ***
${competitionNameEDI}           Performance testing competition
${assessorcompetitionNameEDI}   EDI Testing Competition
${applicationNameEDI}           EDI Application
${AssessorapplicationNameEDI}   EDI Assessor application


*** Test Cases ***
Applicant can view EDI section in profile page
    [Documentation]  IFS-11252  IFS-11490
    Given External user starts a new application
    When the user clicks the button/link            link = Profile
    Then the user should see EDI section details    Incomplete  Not Applicable  Start now

Applicant checks the status of EDI as Incomplete When user not started the edi survey
    [Documentation]  IFS-11253  IFS-11341
    Given the user clicks the button/link               link = Back to applications
    And the user clicks the button/link                 link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    And the user fills in the EDI application details   ${applicationNameEDI}  ${tomorrowday}  ${month}  ${nextyear}
    When the user clicks the button/link                link = Application team
    Then the user should see the element                jQuery = td:contains("ediFirstName SurName") ~ td:contains("Incomplete") ~ td:contains("Lead applicant")

Applicant checks the status of EDI as Incomplete on application summary when edi survey is not started
    [Documentation]  IFS-11253  IFS-11341
    Given the user clicks the button/link                       link = Application overview
    When the user clicks the button/link                        id = application-overview-submit-cta
    And the user clicks the button/link                         id = accordion-questions-heading-1-1
    Then the user should see the read only view of EDI status   Incomplete

Lead applicant can not mark the application team as complete when the edi survey is not started
    [Documentation]  IFS-11253
    Given the user clicks the button/link                  link = Application overview
    And the user clicks the button/link                    link = Application team
    When the user clicks the button/link                   id = application-question-complete
    Then the user should see a field and summary error     Complete our equality,diversity and inclusion survey.

Applicant can view the EDI incomplete status
    [Documentation]  IFS-11252  IFS-11490
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

Lead applicant adds a partner organisation and check the status of edi as Complete
    [Documentation]  IFS-11253
    Given the user clicks the button/link              link = Application overview
    When the lead invites already registered user      ${collaborator1_credentials["email"]}  ${competitionNameEDI}
    And logging in and error checking                  ${collaborator1_credentials["email"]}  ${short_password}
    And the user clicks the button/link                jQuery = button:contains("Save and continue")
    And the user clicks the button/link                link = Application team
    Then the user should see the element               jQuery = td:contains("Jessica Doe") ~ td:contains("Complete")

Applicant can view the EDI status as complete in profile
    [Documentation]  IFS-11252  IFS-11490
    Given log in as a different user                ediFirstName.SurName@gmail.com  ${short_password}
    When the user clicks the button/link            link = Profile
    And the user changed EDI survey status          COMPLETE  2089-03-25 01:02:03
    Then the user should see EDI section details    Complete  25 March 2089  Review EDI summary

Lead applicant can mark the application team as complete when edi status is complete for lead applicant
    [Documentation]  IFS-11253  IFS-11341
    Given the user navigates to the page            ${server}/application/${applicationIdEDI}
    When the user clicks the button/link            link = Application team
    And the user clicks the button/link             id = application-question-complete
    Then the user should see the element            jQuery = td:contains("ediFirstName SurName") ~ td:contains("Complete") ~ td:contains("Lead applicant")
    And the user clicks the button/link             link = Application overview
    Then the user should see the element            jQuery = li:contains("Application team") > .task-status-complete

Lead applicant check the status of edi as complete when edi survey is complete for lead applicant
    [Documentation]  IFS-11341
    When the user clicks the button/link                        id = application-overview-submit-cta
    Then the user should see the read only view of EDI status   Complete

Assessor can not see EDI status when he is only assessor not the applicant
    [Documentation]  IFS-11534
    Given log in as a different user     	          &{edi_assessor_credentials}
    When the user clicks the button/link              link = Profile
    Then the user should not see the element          jQuery = h2:contains("Equality, diversity and inclusion")
    And the user should not see the element           jQuery = p:contains("Please complete our EDI monitoring survey. It helps us ensure we treat everyone who engages with us fairly and equally. The survey is mandatory and should takes no more than 10 minutes. You will be redirected to another page to complete this survey, but you can return here at any point.")

Assessor/Applicant checks the status of EDI as Incomplete When user not started the edi survey
    [Documentation]  IFS-11534
    Given log in as a different user     	            &{edi_assessor_credentials}
    And the assessor creates a new application
    And the user fills in the EDI application details   ${AssessorapplicationNameEDI}  ${tomorrowday}  ${month}  ${nextyear}
    When the user clicks the button/link                link = Application team
    Then the user should see the element                jQuery = td:contains("Aaron Jennings") ~ td:contains("Incomplete") ~ td:contains("Lead applicant")

Assessor can view EDI section as a incomplete in profile page
    [Documentation]  IFS-11534
    When the user clicks the button/link                 link = Profile
    Then the user should see EDI section details         Incomplete  Not Applicable  Start now
    And the user should see the element                  jQuery = h1:contains("DOI")

Assessor/applicant can not mark the application team as complete when the edi survey is not started
    [Documentation]  IFS-11534
    Given log in as a different user  	                   &{edi_assessor_credentials}
    And the user clicks the button/link                    jQuery = h2:contains("Applications")
    And the user clicks the button/link                    link = ${AssessorapplicationNameEDI}
    And the user clicks the button/link                    link = Application team
    When the user clicks the button/link                   id = application-question-complete
    And the user clicks the button/link                    jQuery = a:contains("here")
    And the assessor changed EDI survey status             INPROGRESS  2076-01-22 01:02:03
    And the user edit the profile of an assessor
    Then the user should see EDI section details           Incomplete  22 January 2076  Continue

Assessor/Applicant can view the EDI status as complete in profile
    [Documentation]  IFS-11534
    When the assessor changed EDI survey status      COMPLETE  2089-03-25 01:02:03
    And the user edit the profile of an assessor
    Then the user should see EDI section details     Complete  25 March 2089  Review EDI summary

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    Connect to Database    @{database}
    The guest user opens the browser

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user should see EDI section details
    [Arguments]  ${ediStatus}  ${ediReviewDate}  ${ediButton}
    the user should see the element  jQuery = h2:contains("Equality, diversity and inclusion")
    the user should see the element  jQuery = p:contains("Please complete our EDI monitoring survey.")
    the user should see the element  jQuery = th:contains("Survey status")+td:contains("${ediStatus}")
    the user should see the element  jQuery = th:contains("Last reviewed")+td:contains("${ediReviewDate}")
    the user should see the element  jQuery = a:contains("${ediButton}")
    the user should see the element  css=[href="https://loans-innovateuk.cs80.force.com/EDI/s"][target="_blank"]

the user changed EDI survey status
    [Arguments]  ${ediStatus}  ${ediReviewDate}
    execute sql string   UPDATE `${database_name}`.`user` SET `edi_status` = '${ediStatus}', `edi_review_date` = '${ediReviewDate}' WHERE (`email` = 'ediFirstName.SurName@gmail.com');
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

get application Id of assessor
    ${assessorApplicationIdEDI} =  get application id by name    ${AssessorapplicationNameEDI}
    Set suite variable    ${assessorApplicationIdEDI}

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

the user should see the read only view of EDI status
    [Arguments]  ${ediStatus}
    the user should see the element                 jQuery = h3:contains("Team members")
    the user should see the element                 jQuery = th:contains("EDI status")
    the user should see the element                 jQuery = td:contains("ediFirstName SurName") ~ td:contains("${ediStatus}")

the assessor creates a new application
    the user select the competition and starts application     ${competitionNameEDI}
    the user selects the radio button                          organisationTypeId  1
    the user clicks the button/link                            jQuery = button:contains("Save and continue")
    the user enters text to a text field                       id = organisationSearchName  ASOS
    the user clicks the button/link                            jQuery = button:contains("Search")
    the user clicks the button/link                            link = ASOS PLC
    the user clicks the button/link                            jQuery = button:contains("Save and continue")
    the user selects the checkbox                              agree
    the user clicks the button/link                            jQuery = button:contains("Continue")

the assessor should see the read only view of EDI status
    [Arguments]  ${ediStatus}
    the user should see the element                 jQuery = h3:contains("Team members")
    the user should see the element                 jQuery = th:contains("EDI status")
    the user should see the element                 jQuery = td:contains("Aaron Jennings") ~ td:contains("${ediStatus}")

the assessor changed EDI survey status
    [Arguments]  ${ediStatus}  ${ediReviewDate}
    execute sql string   UPDATE `${database_name}`.`user` SET `edi_status` = '${ediStatus}', `edi_review_date` = '${ediReviewDate}' WHERE (`email` = 'Aaron.Jennings@ukri.org');

the user edit the profile of an assessor
    the user navigates to the page         ${server}/assessment/profile/details/edit
    the user enters text to a text field   id= addressLine1  test
    the user enters text to a text field   id= town  test
    the user enters text to a text field   id= postcode  test
    the user clicks the button/link        jQuery=button:contains("Save and return to your details")
    the user clicks the button/link        link = Profile

External user starts a new application
    the user select the competition and starts application          ${competitionNameEDI}
    the user clicks the button/link                                 link = Continue and create an account
    the user selects the radio button                               organisationTypeId    radio-1
    the user clicks the button/link                                 jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House            ASOS  ASOS PLC
    the user should be redirected to the correct page               ${SERVER}/registration/register
    the user enters the details and clicks the create account       ediFirstName  SurName  ediFirstName.SurName@gmail.com  ${short_password}
    the user reads his email and clicks the link                    ediFirstName.SurName@gmail.com  Please verify your email address  Once verified you can sign into your account.
    the user clicks the button/link                                 link = Sign in
    Logging in and Error Checking                                   ediFirstName.SurName@gmail.com  ${short_password}