*** Settings ***
Documentation     INFUND-172: As a lead applicant and I am on the application summary, I can submit the application, so I can verify it that it is ready for submission
...
...               INFUND-185: As an applicant, on the application summary and pressing the submit application button, it should give me a message that I can no longer alter the application.
...
...               INFUND-927 As a lead partner i want the system to show me when all questions and sections (partner finances) are complete on the finance summary, so that i know i can submit the application
...
...               INFUND-1137 As an applicant I want to be shown confirmation information when I submit my application submission so I can confirm this has been sent and I can be given guidance for the next stages
...
...               INFUND-1887 As the Service Delivery Manager, I want to send an email notification to an applicant once they have successfully submitted a completed application so they have confidence their application has been received by Innovate UK
...
...               INFUND-3107 When clicking the "X", the form submits and doesn't cancel the action when using a modal popup
...
...               INFUND-1786 As a lead applicant I would like view the submitting an application terms and conditions page so that I know what I am agreeing to
...
...               INFUND-9058 Update 'Application submitted' and 'Application status' pages to the same view
...
...               IFS-942 Information message when application has reached 100% complete
...
...               IFS-753 Missing functionality on Mark as complete option in Application summary
Suite Setup       new account complete all but one
Suite Teardown    Custom Suite Teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot
Resource          ../../10__Project_setup/PS_Common.robot

*** Test Cases ***
Submit button disabled when application is incomplete
    [Documentation]    INFUND-927, IFS-942, IFS-753
    [Tags]    Email    HappyPath
    [Setup]  Log in as a different user                ${submit_bus_email}  ${correct_password}
    Given the user navigates to the page               ${DASHBOARD_URL}
    When the user clicks the button/link               link=${application_bus_name}
    And the user should not see the element            jQuery=.message-alert:contains("Now your application is complete, you need to review and then submit.")
    And the user clicks the button/link                link=Your finances
    And the user clicks the button/link                link= Application overview
    And the user clicks the button/link                jQuery=.button:contains("Review and submit")
    Then the submit button should be disabled
    When the user clicks the button/link               jQuery= button:contains("Application details")
    Then the user should see the element               jQuery= div[id="collapsible-0"] button:contains("Mark as complete")+button:contains("Return and edit")
    When the user clicks the button/link               jQuery=button:contains("Mark as complete")
    Then the user should see the element               jQuery=h1:contains("Application details")
    And the user should see a field and summary error  Please enter a future date
    And the user should see a field and summary error  Please select a research category
    And the user should see a field and summary error  Please tell us if this application is a resubmission or not

Applicant has read only view on review and submit page
    [Documentation]    INFUND-7405, INFUND-8599
    [Tags]    HappyPath
    Given the user navigates to the page                  ${DASHBOARD_URL}
    And the user clicks the button/link                   link=${application_bus_name}
    When the applicant completes the application details  Application details
    And the user clicks the button/link                   link=Return to application overview
    And the user clicks the button/link                   link=Your finances
    And the user marks the finances as complete           ${application_bus_name}  labour costs  n/a
    And the user clicks the button/link                   link=Review and submit
    Then the user should not see the element              css=input

Your Project costs section is read-only once application is marked as complete
    [Documentation]    INFUND-6788, INFUND-7405
    [Tags]
    Given the user navigates to Your-finances page  ${application_bus_name}
    And the user clicks the button/link             link=Your project costs
    And the user should not see the element         css=input
    When the user clicks the button/link            jQuery=button:contains("Overhead costs")
    Then the user should not see the element        css=input

Submit flow business lead (complete application)
    [Documentation]    INFUND-205, INFUND-9058, INFUND-1887, INFUND-3107, INFUND-4010, IFS-942
    [Tags]    HappyPath    Email    SmokeTest
    Given log in as a different user                        ${submit_bus_email}  ${correct_password}
    And the user clicks the button/link                     link=${application_bus_name}
    And the user should see the text in the element         css=.message-alert  Now your application is complete, you need to review and then submit.
    When the user clicks the button/link                    link=Review and submit
    Then the user should be redirected to the correct page  summary
    And the applicant clicks the submit button and the clicks cancel in the submit modal
    And the applicant clicks the submit and then clicks the "close button" in the modal
    And the applicant clicks Yes in the submit modal
    Then the user should be redirected to the correct page  submit
    And the user should see the text in the page            Application submitted
    And The user should see the element                     link=Finished
    # TODO add check here once IFS-270 done

The applicant should get a confirmation email
    [Documentation]    INFUND-1887
    [Tags]    Email    HappyPath    SmokeTest
    Then the user reads his email    ${submit_bus_email}    Successful submission of application    You have successfully submitted an application

Submitted application is read only
    [Documentation]    INFUND-1938, INFUND-9058
    [Tags]    Email    SmokeTest
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link     link=${application_bus_name}
    and the user clicks the button/link     link=Return to dashboard
    and the user clicks the button/link     link=${application_bus_name}
    When the user clicks the button/link    link=View application
    And The user should be redirected to the correct page    summary
    Then the user can check that the sections are read only  ${application_bus_name}

Status of the submitted application
    [Documentation]    INFUND-1137
    [Tags]    Email
    When the user navigates to the page   ${DASHBOARD_URL}
    Then the user should see the element  jQuery=.in-progress li:contains("${application_bus_name}") .msg-progress:contains("Application submitted")
    And the user clicks the button/link   link=${application_bus_name}
    And the user should see the element   link=View application
    And the user should see the element   link=Print application

RTO lead has read only view after submission
    [Documentation]    INFUND-7405, INFUND-8599
    [Tags]    HappyPath
    [Setup]  log in as a different user             ${submit_rto_email}    ${correct_password}
    Given the user navigates to the page                  ${DASHBOARD_URL}
    And the user clicks the button/link                   link=${application_rto_name}
    When the applicant completes the application details  Application details
    When the user clicks the button/link     link=Return to application overview
    And the user clicks the button/link                   link=Your finances
    When Run Keyword And Ignore Error Without Screenshots  the user clicks the button/link  css=.extra-margin-bottom [aria-expanded="false"]
    Then the user clicks the button/link   jQuery=button:contains("Not requesting funding")
    And the user puts zero project costs
    When the user clicks the button/link     link=Return to application overview
    And the user clicks the button/link      link=Review and submit
    And the user should not see the element  css=input

Submit flow rto lead (complete application)
    [Documentation]  IFS-1051
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link                     link=${application_rto_name}
    And the user should see the text in the element         css=.message-alert  Now your application is complete, you need to review and then submit.
    When the user clicks the button/link                    link=Review and submit
    Then the user should be redirected to the correct page  summary
    And the applicant clicks Yes in the submit modal
    Then the user should be redirected to the correct page  submit
    And the user should see the text in the page            Application submitted
    And The user should see the element                     link=Finished

Applications are on Dashboard when Competition is Closed
    [Documentation]  IFS-1149
    [Tags]  MySQL
    Given the competition is closed
    Then the user should be able to see his application on his dashboard  ${submit_bus_email}  ${application_bus_name}
    And the user should be able to see his application on his dashboard   ${submit_rto_email}  ${application_rto_name}

*** Keywords ***
the applicant clicks Yes in the submit modal
    the user clicks the button/link    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jQuery=.button:contains("Yes, I want to submit my application")

the applicant clicks the submit button and the clicks cancel in the submit modal
    Wait Until Element Is Enabled Without Screenshots    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jquery=button:contains("Cancel")

The user can check that the sections are read only
    [Arguments]  ${application_name}
    the user navigates to the page    ${dashboard_url}
    the user clicks the button/link    link=${application_name}
    the user clicks the button/link    link=View application
    the user clicks the button/link    css=.section-overview section:nth-of-type(1) .collapsible:nth-of-type(4)
    the user should not see the element    jQuery=button:contains("Edit")
    the user clicks the button/link    css=.section-overview section:nth-of-type(2) .collapsible:nth-of-type(10)
    the user should not see the element    jQuery=.button:contains("Edit")
    the user clicks the button/link    css=.section-overview section:nth-of-type(3) .collapsible:nth-of-type(1)
    the user should not see the element    jQuery=.button:contains("Edit")

the submit button should be disabled
    Element Should Be Disabled    jQuery=button:contains("Submit application")

the applicant accepts the terms and conditions
    the user selects the checkbox    agree-terms-page
    the user selects the checkbox    stateAidAgreed

the applicant marks the first section as complete
    Given the user navigates to the page    ${DASHBOARD_URL}
    the user clicks the button/link    link=${application_name}
    the applicant completes the application details

the applicant clicks the submit and then clicks the "close button" in the modal
    Wait Until Element Is Enabled Without Screenshots    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jQuery=button:contains("Close")

the user puts zero project costs
    [Documentation]  To be refactored with existing keyword
    the user clicks the button/link  link=Your project costs
    the user clicks the button/link  css=label[for="stateAidAgreed"]
    the user clicks the button/link  jQuery=button:contains("Mark as complete")
    the user clicks the button/link  link=Your project costs
    the user has read only view once section is marked complete

the competition is closed
    Connect to Database    @{database}
    execute sql string    UPDATE `${database_name}`.`milestone` SET `date`='2017-08-01 11:00:00' WHERE `type`='SUBMISSION_DATE' AND `competition_id`='${UPCOMING_COMPETITION_TO_ASSESS_ID}';

the user should be able to see his application on his dashboard
    [Arguments]  ${user}  ${application}
    log in as a different user       ${user}  ${correct_password}
    the user should see the element  jQuery=.in-progress li:contains("${application}") .msg-deadline-waiting:contains("Awaiting assessment") + .msg-progress:contains("Application submitted")

Custom Suite Teardown
    The user closes the browser
    #Is required to return the competition back to its initial status for the following suites to run
    Connect to Database  @{database}
    execute sql string   UPDATE `${database_name}`.`milestone` SET `date`='2077-09-09 11:00:00' WHERE `type`='SUBMISSION_DATE' AND `competition_id`='${UPCOMING_COMPETITION_TO_ASSESS_ID}';
