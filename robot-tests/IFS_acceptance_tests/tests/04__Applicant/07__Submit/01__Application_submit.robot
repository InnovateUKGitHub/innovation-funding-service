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
...
...               IFS-3603 - IFS-3746 GDS - Satisfaction survey
Suite Setup       Custom Suite Setup
Suite Teardown    Custom Suite Teardown
Force Tags        Applicant  MySQL
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot
Resource          ../../10__Project_setup/PS_Common.robot

*** Test Cases ***
Submit button disabled when application is incomplete
    [Documentation]    INFUND-927, IFS-942, IFS-753
    [Tags]  HappyPath
    Given the user navigates to the page               ${APPLICANT_DASHBOARD_URL}
    When the user clicks the button/link               link = ${application_rto_name}
    And the user should not see the element            jQuery = .message-alert:contains("Now your application is complete, you need to review and then submit.")
    And the user clicks the button/link                link = Your finances
    And the user clicks the button/link                link = Application overview
    And the user clicks the button/link                jQuery = .govuk-button:contains("Review and submit")
    Then the submit button should be disabled
    When the user clicks the button/link               jQuery = button:contains("Application details")
    Then the user should see the element               jQuery = div[id="collapsible-1"] button:contains("Mark as complete")+button:contains("Return and edit")
    When the user clicks the button/link               jQuery = button:contains("Mark as complete")
    Then the user should see the element               jQuery = h1:contains("Application details")
    And the user should see a field and summary error  Please enter a future date
    And the user should see a field and summary error  Please tell us if this application is a resubmission or not

RTO lead has read only view after submission
    [Documentation]    INFUND-7405, INFUND-8599
    [Tags]
    Given the user navigates to the page                   ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link                    link = ${application_rto_name}
    And the user fills in the organisation information     ${application_rto_name}  ${SMALL_ORGANISATION_SIZE}
    And the user clicks the button/link                    link = Your funding
    And the user marks your funding section as complete
    And the user enters the project location
    When Run Keyword And Ignore Error Without Screenshots  the user clicks the button/link  css = .govuk-details__summary[aria-expanded="false"]
    And the user puts zero project costs
    When the user clicks the button/link                   link = Return to application overview
    And the user clicks the button/link                    link = Review and submit
    And the user should not see the element                css = input

Submit flow rto lead (complete application)
    [Documentation]  IFS-1051
    [Tags]
    Given the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    When the applicant completes the application details   ${application_rto_name}  ${tomorrowday}  ${month}  ${nextyear}
    And the user should see the text in the element         css = .message-alert  Now your application is complete, you need to review and then submit.
    When the user clicks the button/link                    link = Review and submit
    Then the user should be redirected to the correct page  summary
    And the applicant clicks Yes in the submit modal
    Then the user should be redirected to the correct page  track
    And the user should see the element                     jQuery = h2:contains("Application submitted")
    And The user should see the element                     link = Finished

Satisfaction survey:validations
    #The survey needs to be set to enabled in gradle.properties
    [Documentation]  IFS-3603
    [Tags]  survey  HappyPath
    Given the user clicks the button/link                 link = Finished
    When the user clicks the button/link                  css = button[type="submit"]  #Send feedback
    Then the user should see a field and summary error    Please select a level of satisfaction.
    And the user should see a field and summary error     ${empty_field_warning_message}

Applicant submit satisfaction survey after submitting application
    #The survey needs to be set to enabled in gradle.properties
    [Documentation]  IFS-3603
    [Tags]  survey  HappyPath
    Given the user selects the radio button      satisfaction  5
    When the user enters text to a text field    name = comments  Very satisfied
    Then the user clicks the button/link         css = button[type="submit"]  #Send feedback

The applicant should get a confirmation email
    [Documentation]    INFUND-1887
    [Tags]
    Then the user reads his email    ${RTO_lead_applicant_credentials["email"]}    Successful submission of application    You have successfully submitted an application

Submitted application is read only
    [Documentation]    INFUND-1938, INFUND-9058
    [Tags]
    Given the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link     link = ${application_rto_name}
    When the user clicks the button/link    link = View application
    And The user should be redirected to the correct page    summary
    Then the user can check that the sections are read only  ${application_rto_name}

Status of the submitted application
    [Documentation]    INFUND-1137
    [Tags]
    When the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    Then the user should see the element  jQuery = .in-progress li:contains("${application_rto_name}") .msg-progress:contains("Application submitted")
    And the user clicks the button/link   link = ${application_rto_name}
    And the user should see the element   link = View application
    And the user should see the element   link = Print application

*** Keywords ***
the applicant clicks Yes in the submit modal
    the user clicks the button/link    jQuery = .govuk-button:contains("Submit application")
    the user clicks the button/link    jQuery = .govuk-button:contains("Yes, I want to submit my application")

the applicant clicks the submit button and the clicks cancel in the submit modal
    Wait Until Element Is Enabled Without Screenshots    jQuery = .govuk-button:contains("Submit application")
    the user clicks the button/link    jQuery = .govuk-button:contains("Submit application")
    the user clicks the button/link    jquery = button:contains("Cancel")

The user can check that the sections are read only
    [Arguments]  ${application_name}
    the user navigates to the page         ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link        link = ${application_name}
    the user clicks the button/link        link = View application
    the user clicks the button/link        css = .section-overview section:nth-of-type(1) .collapsible:nth-of-type(4)
    the user should not see the element    jQuery = button:contains("Edit")
    the user clicks the button/link        css = .section-overview section:nth-of-type(2) .collapsible:nth-of-type(10)
    the user should not see the element    jQuery = .govuk-button:contains("Edit")
    the user clicks the button/link        css = .section-overview section:nth-of-type(3) .collapsible:nth-of-type(1)
    the user should not see the element    jQuery = .govuk-button:contains("Edit")

the submit button should be disabled
    Element Should Be Disabled    jQuery = button:contains("Submit application")

the applicant accepts the terms and conditions
    the user selects the checkbox    agree-terms-page
    the user selects the checkbox    stateAidAgreed

the applicant marks the first section as complete
    the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link    link = ${application_name}
    the applicant completes the application details  ${application_name}  ${tomorrowday}  ${month}  ${nextyear}

the applicant clicks the submit and then clicks the "close button" in the modal
    Wait Until Element Is Enabled Without Screenshots    jQuery = .govuk-button:contains("Submit application")
    the user clicks the button/link    jQuery = .govuk-button:contains("Submit application")
    the user clicks the button/link    jQuery = button:contains("Close")

the user puts zero project costs
    [Documentation]  To be refactored with existing keyword
    the user clicks the button/link  link = Your project costs
    the user clicks the button/link  css = label[for="stateAidAgreed"]
    the user clicks the button/link  jQuery = button:contains("Mark as complete")
    the user clicks the button/link  link = Your project costs
    the user has read only view once section is marked complete

the user should be able to see his application on his dashboard
    [Arguments]  ${user}  ${application}
    log in as a different user       ${user}  ${correct_password}
    the user should see the element  jQuery = .in-progress li:contains("${application}") .msg-deadline-waiting:contains("Awaiting assessment") + .msg-progress:contains("Application submitted")

Custom Suite Teardown
    The user closes the browser
    Disconnect from database

Get the original values of the competition's milestones
    ${openDate}  ${submissionDate} =  Save competition's current dates  ${UPCOMING_COMPETITION_TO_ASSESS_ID}
    Set suite variable  ${openDate}
    Set suite variable  ${submissionDate}

Custom Suite Setup
    Set predefined date variables
    Connect to database  @{database}
    the user logs-in in new browser   &{rto_lead_applicant_credentials}
    logged in user applies to competition   ${openCompetitionRTO_name}  1
    create new application for submitting  ${application_rto_name}

create new application for submitting
    [Arguments]  ${application_name}
    the user clicks the button/link                   link=Application details
    the user enters text to a text field              css=[id="application.name"]    ${application_name}
    the user clicks the button/link                   jQuery=button:contains("Save and return")
    the user marks every section but one as complete  ${application_name}  Experimental development

the user marks every section but one as complete
    [Arguments]  ${application_name}  ${rescat}
    the user navigates to the page    ${server}
    the user clicks the button/link    link=${application_name}
    the applicant completes Application Team
    the user selects Research category  ${rescat}
    the lead applicant fills all the questions and marks as complete(programme)

Your Project costs section is read-only once application is marked as complete
    the user navigates to Your-finances page  ${application_rto_name}
    the user clicks the button/link             link = Your project costs
    the user should not see the element         css = input
    the user clicks the button/link            jQuery = button:contains("Overhead costs")
    the user should not see the element        css = input
