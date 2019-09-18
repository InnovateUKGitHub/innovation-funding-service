*** Settings ***
Documentation     INFUND-39: As an applicant and I am on the application overview, I can select a section of the application, so I can see the status of each subsection in this section
...
...               INFUND-1072: As an Applicant I want to see the Application overview page redesigned so that they meet the agreed style
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Status changes when we assign a question
    [Documentation]    INFUND-39
    [Tags]
    [Setup]     Log in as a different user         &{lead_applicant_credentials}
    Given the user navigates to the page           ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link            link = Academic robot test application
    And the user clicks the button/link            link = Project summary
    When the Applicant edits the Project summary
    And the applicant assigns the Project Summary  Arsene Wenger
    Then the assign status should be correct for the Project Summary
    And the blue flag should not be visible

*** Keywords ***
Custom Suite Setup
    the guest user opens the browser
    Connect to database  @{database}
    Login new application invite academic  ${test_mailbox_one}+academictest@gmail.com  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation

the Applicant edits the Project summary
    Clear Element Text                    css = .textarea-wrapped .editor
    The user enters text to a text field  css = .textarea-wrapped .editor    Check last updated date@#$
    Set Focus To Element                                   id = application-question-complete
    wait for autosave

the assign status should be correct for the Project Summary
    the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link  link = Academic robot test application
    the user should see the element  jQuery = li:contains("Project summary"):contains("Assigned to"):contains("Arsene Wenger")

the applicant assigns the Project Summary question from the overview page
    [Arguments]    ${assignee_name}
    the user clicks the button/link  jQuery = li:contains("Project summary") .assign-button button
    the user clicks the button/link  jQuery = li:contains("Project summary") button:contains("${assignee_name}")
    Sleep                            500ms    # otherwise it stops while Assigning..

the applicant assigns the Project Summary
    [Arguments]    ${assignee_name}
    the user clicks the button/link     jQuery = a:contains("Assign to someone else")
    the user should see the element     jQuery = h2:contains("Assign this question to someone else.")
    the user clicks the button/link     jQuery = label:contains("${assignee_name}")
    the user clicks the button/link     jQuery = label:contains("${assignee_name}")
    the user clicks the button/link     jQuery = label:contains("${assignee_name}")
    the user clicks the button/link     jQuery = button:contains("Save and return to")

a blue flag should be visible for the Project Summary in overview page
    Wait Until Page Does Not Contain Without Screenshots  Assigning to Steve Smith...    10s
    The user should see the element                       jQuery = li:contains("Project summary") > .assign-container

the blue flag should not be visible
    the user should not see the element  jQuery = li:contains("Project summary") > .action-required

the assign button should say Assigned to you
    Element Should Contain  jQuery = li:contains("Project summary") > .assign-container.secondary-notification button    you

Custom suite teardown
    Disconnect from database
    Close browser and delete emails