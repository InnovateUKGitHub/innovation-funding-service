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
Suite Setup       Log in and create a new application for the Aerospace competition
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot

*** Variables ***
${aeroApplication}  Aerospace test application

*** Test Cases ***
Application details: Previous submission
    [Documentation]    INFUND-4694
    Given the user navigates to the page           ${DASHBOARD_URL}
    And the user clicks the button/link            link=${aeroApplication}
    And the user clicks the button/link            link=Application details
    When the user clicks the button/link           jQuery=label:contains("Yes")
    Then the user should see the text in the page  Please provide the details of this previous application
    And the user should see the text in the page   Previous application number
    And the user should see the text in the page   Previous application title
    When the user clicks the button/link           jQuery=label:contains("No")
    Then The user should not see the element       id=application_details-previousapplicationnumber

Application details: Research category
    [Documentation]    INFUND-6823
    Given The user clicks the button/link    jQuery=button:contains("Choose your research category")
    and the user should not see the text in the page    Changing the research category will reset the funding level for all business participants.
    Then the user should see the element    jQuery=label:contains("Industrial research")
    And the user should see the element    jQuery=label:contains("Feasibility studies")
    And the user should see the element    jQuery=label:contains("Experimental development")
    and the user clicks the button/link    jQuery=button:contains(Save)
    Then the user should see an error    This field cannot be left blank
    and the user clicks the button twice    jQuery=label[for^="researchCategoryChoice"]:contains("Feasibility studies")
    and the user clicks the button/link    jQuery=button:contains(Save)
    and the user should see the element    jQuery=div:contains("Chosen research category: Feasibility studies")

Research Category : Autosave not applicable
    [Documentation]    INFUND-6823, INFUND-8251
    When the user clicks the button/link    jQuery=button:contains("Change your research category")
    #    TODO commented due to IFS-1511
    # and the user should see the text in the page    Changing the research category will reset the funding level for all business participants.
    And the user should see the element    jQuery=label:contains("Industrial research")
    And the user clicks the button twice    jQuery=label[for^="researchCategoryChoice"]:contains("Industrial research")
    And the user clicks the button/link    jQuery=a:contains("Application details")
    And the user should see the element    jQuery=div:contains("Chosen research category: Feasibility studies")
    And the finance summary page should show a warning

Application details: Innovation area section is visible
    [Documentation]  INFUND-8115 INFUND-9154
    [Tags]
    Given the user clicks the button/link    link=Application overview
    And the user clicks the button/link    link=Application details
    Given the user should not see the element    jQuery=button:contains("Change your innovation area")
    When The user clicks the button/link    jQuery=button:contains("Choose your innovation area")
    Then the user should see the element    jQuery=label:contains("Digital manufacturing"):contains("Process analysis and control technologies including digital, sensor technology and metrology.")
    And the user should see the element    jQuery=label:contains("My innovation area is not listed")
    And the user should see the element    jQuery=a:contains("Cancel")
    and the user clicks the button/link    jQuery=button:contains("Save")
    Then the user should see an error    This field cannot be left blank
    and the user clicks the button/link    jQuery=label:contains("Digital manufacturing")
    and the user clicks the button/link    jQuery=button:contains("Save")
    Then the user should see the element    jQuery=button:contains("Change your innovation area")

Autosave in the form questions
    [Documentation]    INFUND-189
    [Tags]    HappyPath
    [Setup]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=${aeroApplication}
    When the user clicks the button/link    link=Application details
    then the application details need to be autosaved
    and the user clicks the button/link    link=Application overview
    And the user clicks the button/link    link=Project summary
    When The user enters text to a text field    css=.editor    I am a robot
    And the user reloads the page
    Then the text should be visible

Word count works
    [Documentation]    INFUND-198
    [Tags]    HappyPath
    When the user enters multiple strings into a text field         css=.editor  a${SPACE}  31
    Then the word count should be correct for the Project summary

Guidance of the questions
    [Documentation]    INFUND-190
    [Tags]
    When the user clicks the button/link    css=.summary
    Then the user should see the element    css=#details-content-0 p

Marking a question as complete
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then the text box should turn to green
    And the user should see the element    jQuery=button:contains("Edit")
    And the question should be marked as complete on the application overview page

Mark a question as incomplete
    [Documentation]  INFUND-210, INFUND-202
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Project summary
    When the user clicks the button/link    jQuery=button:contains("Edit")
    Then the text box should be editable
    And the user should see the element    jQuery=button:contains("Mark as complete")
    And the question should not be marked as complete on the application overview page

Review and submit button
    [Documentation]  IFS-751
    [Tags]
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=${aeroApplication}
    When the user clicks the button/link  jQuery=.button:contains("Review and submit")
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
    And the user fills in the application details
    Then the user should no longer see the Mark-as-complete-link

Collaborator: read only view of Application details
    [Documentation]    INFUND-8251 , INFUND-8260
    [Tags]
    [Setup]    Log in as a different user    &{collaborator1_credentials}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=${Competition_E2E}
    When the user clicks the button/link    link=Application details
    then the user should not see the element    css=input
    and the user should see the element    jQuery=a:contains("Return to application overview")

*** Keywords ***
the text should be visible
    Wait Until Element Contains Without Screenshots    css=.editor    I am a robot

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

the Applicant edits Project summary and marks it as complete
    focus    css=.editor
    Clear Element Text    css=.editor
    Press Key    css=.editor    \\8
    focus    css=.editor
    The user enters text to a text field    css=.editor    Hi, I’m a robot @#$@#$@#$
    the user clicks the button/link    jQuery=button:contains("Mark as complete")

the question should be marked as complete on the application overview page
    The user clicks the button/link    link=Application overview
    The user should see the element    jQuery=li:nth-child(2) span:contains("Complete")

the text box should be editable
    Wait Until Element Is Enabled Without Screenshots    css= textarea

the question should not be marked as complete on the application overview page
    The user clicks the button/link    link=Application overview
    the user should see the element    jQuery=li:nth-child(2)
    the user should not see the element    jQuery=li:nth-child(2) span:contains("Complete")

the finance summary page should show a warning
    The user clicks the button/link    link=Application overview
    The user clicks the button/link    link=Your finances
    the user should see the element    jQuery=h3:contains("Your funding") + p:contains("You must select a research category in"):contains("application details")

Log in and create a new application for the Aerospace competition
    Given the user logs-in in new browser  &{lead_applicant_credentials}
    When the user navigates to the page    ${SERVER}/competition/${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS}/overview/
    the user clicks the button/link             jQuery=a:contains("Start new application")

    #The following two lines are failing if we don't have any other application for the same competition
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain    You have an application in progress
            Run Keyword If    '${status}' == 'PASS'    Run keywords    And the user clicks the button/link    jQuery=Label:contains("Yes, I want to create a new application.")
            ...    AND    And the user clicks the button/link    jQuery=.button:contains("Continue")

    And the user clicks the button/link    jQuery=a:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title  ${aeroApplication}
    And the user clicks the button/link    jQuery=button:contains("Save and return")

the user should no longer see the Mark-as-complete-link
    ${appId} =  get application id by name  ${aeroApplication}
    the user navigates to the page       ${server}/application/${appId}/summary
    the user should see the element      jQuery=.collapsible:contains("Application details") button:contains("Return and edit")
    the user should not see the element  jQuery=.collapsible:contains("Application details") button:contains("Mark as complete")