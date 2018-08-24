*** Settings ***
Documentation     INFUND-184: As an applicant and on the over view of the application, I am able to see the character count and status of the questions, so I am able to see if my questions are valid
...
...               INFUND-186: As an applicant and in the application form, I should be able to change the state of a question to mark as complete, so I don't have to revisit the question.
...
...               INFUND-66: As an applicant and I am on the application form, I can fill in the questions belonging to the application, so I can apply for the competition
...
...               INFUND-42: As an applicant and I am on the application form, I get guidance for questions, so I know what I need to fill in.
...
...               INFUND-183: As a an applicant and I am in the application form, I can see the character count that I have left, so I comply to the rules of the question
...
...               INFUND-4694 As an applicant I want to be able to provide details of my previous submission if I am allowed to resubmit my project in the current competition so that I comply with Innovate UK competition eligibility criteria
...
...               INFUND-6823 As an Applicant I want to be invited to select the primary 'Research area' for my project
...
...               INFUND-9154 Update 'Application details' > 'Innovation area' options to those set in 'Initial details' > 'Innovation area'
Suite Setup       Custom Suite Setup
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot

*** Variables ***
${aeroApplication}  Aerospace test application

*** Test Cases ***
Application details: Previous submission
    [Documentation]    INFUND-4694
    Given the user navigates to the page                ${DASHBOARD_URL}
    And the user clicks the button/link                 link=${aeroApplication}
    And the user clicks the button/link                 link=Application details
    When the user clicks the button/link                id=application-question-complete
    Then the user should see a field and summary error  Please tell us if this application is a resubmission or not.
    When the user clicks the button twice               css=label[for="application.resubmission-yes"]
    And the user clicks the button/link                 id=application-question-complete
    Then the user should see a field and summary error  Please enter the previous application number.
    And the user should see a field and summary error   Please enter the previous application title.
    When the user clicks the button/link                css=label[for="application.resubmission-no"]
    Then The user should not see the element            css=[id="application.previousApplicationNumber"]

Application details: Innovation area section is visible
    [Documentation]  INFUND-8115 INFUND-9154
    [Tags]
    Given the user clicks the button/link      link=Application overview
    And the user clicks the button/link        link=Application details
    Given the user should not see the element  jQuery=button:contains("Change your innovation area")
    When The user clicks the button/link       jQuery=button:contains("Choose your innovation area")
    Then the user should see the element       jQuery=label:contains("Digital manufacturing"):contains("Process analysis and control technologies including digital, sensor technology and metrology.")
    And the user should see the element        jQuery=label:contains("My innovation area is not listed")
    And the user should see the element        jQuery=a:contains("Cancel")
    When the user clicks the button/link       jQuery=button:contains("Save")
    Then the user should see an error          This field cannot be left blank
    When the user clicks the button/link       jQuery=label:contains("Digital manufacturing")
    And the user clicks the button/link        jQuery=button:contains("Save")
    Then the user should see the element       jQuery=button:contains("Change your innovation area")

Autosave in the form questions
    [Documentation]    INFUND-189
    [Tags]    HappyPath
    [Setup]
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=${aeroApplication}
    When the user clicks the button/link  link=Application details
    Then the application details need to be autosaved
    And the user clicks the button/link   link=Application overview
    And the user clicks the button/link   link=Project summary
    When The user enters text to a text field  css=.editor  I am a robot
    And the user reloads the page
    Then the user should see the text in the element  css=.editor  I am a robot

Word count works
    [Documentation]    INFUND-198
    [Tags]    HappyPath
    When the user enters multiple strings into a text field         css=.editor  a${SPACE}  31
    Then the word count should be correct for the Project summary

Guidance of the questions
    [Documentation]    INFUND-190
    [Tags]
    When the user clicks the button/link    css=.govuk-details__summary
    Then the user should see the element    css=.govuk-details__text p

Marking a question as complete
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then the text box should turn to green
    And the word count should be correct for the Project summary
    And the user should see the element    jQuery=button:contains("Edit")
    And the question should be marked as complete on the application overview page

Mark a question as incomplete
    [Documentation]  INFUND-210, INFUND-202
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Project summary
    When the user clicks the button/link     jQuery=button:contains("Edit")
    Then the text box should be editable
    And the user should see the element      jQuery=button:contains("Mark as complete")
    And the question should not be marked as complete on the application overview page

Review and submit button
    [Documentation]  IFS-751
    [Tags]
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=${aeroApplication}
    When the user clicks the button/link  jQuery=.govuk-button:contains("Review and submit")
    Then the user should see the element  jQuery=h1:contains("Application summary")
    And the user should see the text in the page  Please review your application before final submission

Incomplete sections contain mark as complete link
    [Documentation]  IFS-751
    [Tags]  MySQL
    Given the user should see the element  jQuery=button:contains("Application details") .section-incomplete
    When the user expands the section      Application details
    Then the user should see the element   jQuery=.collapsible:contains("Application details") button:contains("Mark as complete")
    And the user should see the element    jQuery=.collapsible:contains("Application details") button:contains("Return and edit")
    When the user clicks the button/link   jQuery=.collapsible:contains("Application details") button:contains("Mark as complete")
    And the user fills in the Application details  ${aeroApplication}  ${tomorrowday}  ${month}  ${nextyear}
    Then the user should no longer see the Mark-as-complete-link  Application details

Research section incomplete
    [Documentation]  IFS-2123
    Given The user clicks the button/link  link=Application overview
    Then the user should see the element   jQuery=li:contains("Research category") .task-status-incomplete

Research category validation
    [Documentation]  IFS-2123
    Given The user clicks the button/link  link=Research category
    When The user clicks the button/link   id=application-question-save
    Then The user should see a field and summary error  This field cannot be left blank.
    [Teardown]  the user clicks the button/link  link=Application overview

Mark research section as complete
    [Documentation]  IFS-2123
    Given the user selects Research category  Industrial research
    When The user clicks the button/link      link=Research category
    Then the user should see the element     jQuery=.success-alert ~ p:contains("Industrial research")

Mark research section as incomplete
    [Documentation]  IFS-2123
    Given the user clicks the button/link    css=button[name="mark_as_incomplete"]
    When the user clicks the button/link     id=application-question-save
    Then The user should see the element     jQuery=li:contains("Research category") .task-status-incomplete

Collaborator: read only view of Application details
    [Documentation]    INFUND-8251 , INFUND-8260
    [Tags]
    [Setup]    Log in as a different user    &{collaborator1_credentials}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=${Competition_E2E}
    When the user clicks the button/link    link=Application details
    then the user should not see the element    css=input
    [Teardown]   the user clicks the button/link    jQuery=a:contains("Return to application overview")

Collaborator: read only view of research
    [Documentation]  IFS-2321
    Given the user clicks the button/link  link=Research category
    Then the user should not see the element  css=button[name="mark_as_incomplete"]
    And the user should not see the element   id=application-question-save
    And the user should see the element       jQuery=a:contains("Return to application overview")

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    Log in and create a new application for the Aerospace competition
    ${appId} =  get application id by name  ${aeroApplication}
    Set suite variable  ${appId}

the application details need to be autosaved
    the user enters text to a text field    application.durationInMonths    22
    wait for autosave
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=Application details
    the user should not see the text in the element    application.durationInMonths    22

the word count should be correct for the Project summary
    Wait Until Element Contains Without Screenshots    css=.count-down    369

the text box should turn to green
    the user should see the element    css=div.success-alert
    Element Should Be Disabled    css= textarea

the question should be marked as complete on the application overview page
    The user clicks the button/link    link=Application overview
    The user should see the element    jQuery=li:nth-child(4) span:contains("Complete")

the text box should be editable
    Wait Until Element Is Enabled Without Screenshots    css= textarea

the question should not be marked as complete on the application overview page
    The user clicks the button/link    link=Application overview
    the user should see the element    css=li:nth-child(2) .task-status-incomplete
    the user should not see the element    jQuery=li:nth-child(2) span:contains("Complete")

Log in and create a new application for the Aerospace competition
    The user logs-in in new browser  &{lead_applicant_credentials}
    The user navigates to the page   ${SERVER}/competition/${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS}/overview/
    the user clicks the button/link  link=Start new application

    #The following two lines are failing if we don't have any other application for the same competition
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain    You have an application in progress
            Run Keyword If    '${status}' == 'PASS'    Run keywords    And the user selects the radio button     createNewApplication  true      #Yes, I want to create a new application.
            ...    AND    And the user clicks the button/link    jQuery=.govuk-button:contains("Continue")
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain   org2
            Run Keyword If    '${status}' == 'PASS'    the user selects the radio button     selectedOrganisationId  21
    the user clicks the button/link    css=.govuk-button[type="submit"]   #Save and continue

    The user clicks the button/link    jQuery=button:contains("Save and return to application overview")
    The user clicks the button/link    link=Application details
    The user enters text to a text field  id=application.name  ${aeroApplication}
    The user clicks the button/link       id=application-question-save

the user should no longer see the Mark-as-complete-link
    [Arguments]  ${Section}
    the user navigates to the page       ${server}/application/${appId}/summary
    the user should see the element      jQuery=.collapsible:contains("${Section}") button:contains("Return and edit")