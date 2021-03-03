*** Settings ***
Documentation     INFUND-37 As an applicant and I am on the application overview, I can view the status of this application, so I know what actions I need to take
...
...               INFUND-8614 Application dashboard - hours left
...
...               IFS-8651 Invites not deleted when application is deleted
...
Suite Setup       log in and create new application if there is not one already  Robot test application
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${applicationName}      Delete app invite test
${knowledgeBaseOrg}     Queen Mary University of London
${inviteEmail}          test@testing.com
${compName}             No aid comp

*** Test Cases ***
Milestone date for application in progress is visible
    [Documentation]  INFUND-37 INFUND-5485
    [Tags]  HappyPath
    When The user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    Then the user should see the date for submission of application

Number of days remaining until submission should be correct
    [Documentation]  INFUND-37 INFUND-5485
    [Tags]  HappyPath
    The days remaining should be correct (Applicant's dashboard)  ${openCompetitionBusinessRTOCloseDate}  Robot test application

Hours remaining should show the last 24hours
    [Documentation]    INFUND-8614
    [Tags]  HappyPath
    [Setup]    Custom setup
    When the user reloads the page
    Then the user should see the element    jQuery = .status-msg:contains("hours left")
    [Teardown]     execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${openCompetitionRTOCloseDate} 00:00:00' WHERE `competition_id`='${openCompetitionRTO}' and type IN ('SUBMISSION_DATE');

Collaborator is able to remove an application
    [Documentation]  IFS-7088
    [Setup]  Log in as a different user       &{collaborator1_credentials}
    Given the user clicks the button/link     jQuery = button[name="hide-application-${CLOSED_COMPETITION_APPLICATION_NAME_NUMBER}"]
    When the user clicks the button/link      jQuery = li:contains("innovative") button:contains("Remove application")
    Then the user should not see the element  jQuery = li:contains("innovative")

Lead still sees application
    [Documentation]  IFS-7088
    Given Log in as a different user                          &{lead_applicant_credentials}
    And the user clicks the application tile if displayed
    Then the user should see the element                      jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}")

Lead is able to delete an application
    [Documentation]  IFS-7088
    Given the user clicks the button/link     jQuery = button[name="delete-application-${CLOSED_COMPETITION_APPLICATION_NAME_NUMBER}"]
    When the user clicks the button/link      jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}") button:contains("Delete application")
    Then the user should not see the element  jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}")

Application is no longer visible to anyone
    [Documentation]  IFS-7088
    Given log in as a different user          &{collaborator2_credentials}
    Then the user should not see the element  jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}")
    And the user reads his email              ${collaborator2_credentials["email"]}  Successful deletion of application  All the application information has been deleted

All invites associated with an application is deleted when the application is deleted
    [Documentation]  IFS-8651
    [Setup]  the lead applicant sends an invite to the application team
    Given the user can see application created in db table
    And the user can see invite sent in db table
    When competition is closed
    And the user deletes the application
    Then user can see application deleted in db table
    And user can see invite deleted in db table

*** Keywords ***
Custom setup
    ${TIME}=    Get Current Date    UTC    + 3 hours    exclude_millis=true    # This line gets the current date/time and adds 3 hours
    Connect to Database    @{database}
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${TIME}' WHERE `competition_id`='${openCompetitionRTO}' and type IN ('SUBMISSION_DATE');

the user should see the date for submission of application
    the user should see the element  jQuery=li:contains("Robot test application") .status:contains("days left"):contains("% complete")

the lead applicant sends an invite to the application team
    log in as a different user                                 &{lead_applicant_credentials}
    the user select the competition and starts application     ${compName}
    the user apply with knowledge base organisation            London  ${knowledgeBaseOrg}
    the user clicks the button/link                            link = Application details
    the user enters text to a text field                       css = [id="name"]  ${applicationName}
    the user clicks the button/link                            jQuery = button:contains("Save and return")
    the user clicks the button/link                            link = Application team
    the user clicks the button/link                            jQuery = button:contains("Add person to ${knowledgeBaseOrg}")
    the user enters text to a text field                       css = [name=name]  Test Testing
    the user enters text to a text field                       css = [name=email]  ${inviteEmail}
    the user clicks the button/link                            jQuery = button:contains("Invite to application")
    the user should see the element                            jQuery = td:contains("${inviteEmail}") + td:contains("Resend invitation")

get application Id
    [Arguments]  ${appName}
    ${applicationID} =  get application id by name    ${appName}
    Set suite variable    ${applicationID}

the user can see application created in db table
    get application Id                    ${applicationName}
    ${count} =  get table count by id     application  id  ${applicationID}
    Should be true                        ${count} > 0

the user can see invite sent in db table
    ${count} =  get table count by id     invite  target_id  ${applicationID}
    Should be true                        ${count} > 0

user can see application deleted in db table
    ${count} =  get table count by id     application  id  ${applicationID}
    Should be true                        ${count} == 0

user can see invite deleted in db table
    ${count} =  get table count by id     invite  target_id  ${applicationID}
    Should be true                        ${count} == 0

competition is closed
    Get competitions id and set it as suite variable    ${compName}
    update milestone to yesterday                       ${competitionId}  SUBMISSION_DATE

the user deletes the application
    the user navigates to the page      ${server}/applicant/dashboard
    the user clicks the button/link     jQuery = button[name="delete-application-${applicationID}"]
    the user clicks the button/link     jQuery = li:contains("${applicationID}") button:contains("Delete application")

Custom suite teardown
    The user closes the browser
    Disconnect from database