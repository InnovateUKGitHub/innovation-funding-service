*** Settings ***
Documentation     INFUND-37 As an applicant and I am on the application overview, I can view the status of this application, so I know what actions I need to take
...
...               INFUND-8614 Application dashboard - hours left
Suite Setup       log in and create new application if there is not one already  Robot test application
Suite Teardown
#Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${project_change_request_message}    Contact your Innovate UK monitoring officer to discuss a project change request.

*** Test Cases ***
Applicant is able to see additional funding message
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    When The user navigates to the page     ${APPLICANT_DASHBOARD_URL}
    Then the user should see the element    jquery = a:contains("You may be eligible for additional funding")

Applicant goes through the queries to apply for additional funding
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given The user clicks the button/link           jquery = a:contains("You may be eligible for additional funding")
    When the user should see the element            jquery = a:contains("Start now")
    Then the user clicks the button/link            jquery = a:contains("Start now")
    And the user should not see an error in the page

Applicant applying for additional funding is a business or third sector
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user selects the radio button     govuk-radios__item  yes
    When the user clicks the button/link        jquery = button:contains("Next")
    Then the user should see the element        jquery = a:contains("Change answer")
    And the user should not see an error in the page

Applicant applying for additional funding is a business but not an Innovate UK award recipient
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user selects the radio button     govuk-radios__item  no
    When the user clicks the button/link        jquery = button:contains("Next")
    Then the user is able to see relevant links for award recipients
    And the user should see the element         jquery = a:contains("Start again")

Applicant applying for additional funding is a business and an Innovate UK award recipient
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user navigates to the page        ${APPLICANT_ADDITIONAL_FUNDING_QUERIES_URL}/award-recipient
    When the user selects the radio button      govuk-radios__item  yes
    And the user clicks the button/link         jquery = button:contains("Next")
    Then the user should see the element        jquery = a:contains("Start again")

Applicant applying for additional funding is an Innovate UK award recipient and needs extension in project period
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user selects the radio button          govuk-radios__item  yes
    When the user clicks the button/link             jquery = button:contains("Next")
    Then the user should see the text in the page    ${project_change_request_message}

#Milestone date for application in progress is visible
#    [Documentation]  INFUND-37 INFUND-5485
#    [Tags]  HappyPath
#    When The user navigates to the page  ${APPLICANT_DASHBOARD_URL}
#    Then the user should see the date for submission of application
#
#Number of days remaining until submission should be correct
#    [Documentation]  INFUND-37 INFUND-5485
#    [Tags]  HappyPath
#    The days remaining should be correct (Applicant's dashboard)  ${openCompetitionBusinessRTOCloseDate}  Robot test application
#
#Hours remaining should show the last 24hours
#    [Documentation]    INFUND-8614
#    [Tags]  HappyPath
#    [Setup]    Custom setup
#    When the user reloads the page
#    Then the user should see the element    jQuery = .status-msg:contains("hours left")
#    [Teardown]     execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${openCompetitionRTOCloseDate} 00:00:00' WHERE `competition_id`='${openCompetitionRTO}' and type IN ('SUBMISSION_DATE');
#
#Collaborator is able to remove an application
#    [Documentation]  IFS-7088
#    [Setup]  Log in as a different user       &{collaborator1_credentials}
#    Given the user clicks the button/link     jQuery = button[name="hide-application-${CLOSED_COMPETITION_APPLICATION_NAME_NUMBER}"]
#    When the user clicks the button/link      jQuery = li:contains("innovative") button:contains("Remove application")
#    Then the user should not see the element  jQuery = li:contains("innovative")
#
#Lead still sees application
#    [Documentation]  IFS-7088
#    Given Log in as a different user       &{lead_applicant_credentials}
#    Then the user should see the element   jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}")
#
#Lead is able to delete an application
#    [Documentation]  IFS-7088
#    Given the user clicks the button/link     jQuery = button[name="delete-application-${CLOSED_COMPETITION_APPLICATION_NAME_NUMBER}"]
#    When the user clicks the button/link      jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}") button:contains("Delete application")
#    Then the user should not see the element  jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}")
#
#Application is no longer visible to anyone
#    [Documentation]  IFS-7088
#    Given log in as a different user          &{collaborator2_credentials}
#    Then the user should not see the element  jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}")
#    And the user reads his email              ${collaborator2_credentials["email"]}  Successful deletion of application  All the application information has been deleted

*** Keywords ***
the user is able to see relevant links for award recipients
    the user should see the element  jquery = a:contains("Visit GOV.UK (opens in a new window)")
    the user should see the element  jquery = a:contains("Visit the British Business Bank (opens in a new window)")
    the user should see the element  jquery = a:contains("Apply for funding with Innovate UK (opens in a new window)")


Custom setup
    ${TIME}=    Get Current Date    UTC    + 3 hours    exclude_millis=true    # This line gets the current date/time and adds 3 hours
    Connect to Database    @{database}
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${TIME}' WHERE `competition_id`='${openCompetitionRTO}' and type IN ('SUBMISSION_DATE');

the user should see the date for submission of application
    the user should see the element  jQuery=li:contains("Robot test application") .status:contains("days left"):contains("% complete")

Custom suite teardown
    The user closes the browser
    Disconnect from database