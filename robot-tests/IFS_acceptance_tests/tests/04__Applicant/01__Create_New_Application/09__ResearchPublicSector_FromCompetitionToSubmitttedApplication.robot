*** Settings ***
Documentation   IFS-1012 As a comp exec I am able to set Research and Public sector as an Eligible Lead applicant options in Competition setup
...
...             IFS-182 As a comp exec I am able to configure Assessed questions in Competition setup
Suite Setup     Custom Suite Setup
Suite Teardown  Close browser and delete emails
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot

# This Suite moves competition Photonics for Public to Project Setup
# This suite is using Generic Type Competitions

*** Variables ***
${compResearch}     Research can lead    # Of Generic competition Type
${compPublic}       Public Sector can lead    # Of Generic competition Type
${researchLeadApp}  Research Leading Application
${publicLeadApp}    Public Sector leading Application
${collaborator}     ${test_mailbox_one}+amy@gmail.com
${compPublicPage}   ${server}/management/competition/${openCompetitionPublicSector}

*** Test Cases ***
Comp Admin Creates Competitions where Research or Public sector can lead
    [Documentation]  IFS-1012 IFS-182
    [Tags]  CompAdmin  HappyPath
    Given Logging in and Error Checking                   &{Comp_admin1_credentials}
    Then The competition admin creates a competition for  ${ACADEMIC_TYPE_ID}  ${compResearch}  Research
    And The competition admin creates a competition for   ${PUBLIC_SECTOR_TYPE_ID}  ${compPublic}  Public

Requesting the id of this Competition
    [Documentation]  IFS-182
    ...   retrieving the id of the competition so that we can use it in urls
    [Tags]  MySQL
    ${reseachCompId} =  get comp id from comp title  ${compResearch}
    Set suite variable  ${reseachCompId}

The Applicant is able to apply to the competition once is Open and see the correct Questions
    [Documentation]  IFS-182
    [Tags]  HappyPath  MySQL
    [Setup]  the competition moves to Open state  ${reseachCompId}
    Given log in as a different user              &{collaborator2_credentials}
    And logged in user applies to competition     ${compResearch}
    Then the user should see the element          jQuery=li:contains("Tell us how your project is innovative.")

Applicant Applies to Research leading Competition
    [Documentation]  IFS-1012
    [Tags]  Applicant  HappyPath
    [Setup]  Log in as a different user                   antonio.jenkins@jabbertype.example.com  ${short_password}
    Given logged in user applies to competition           ${openCompetitionResearch_name}
    When the user clicks the button/link                  link=Application details
    Then the user fills in the Application details        ${researchLeadApp}  Experimental development  ${tomorrowday}  ${month}  ${nextyear}
    And the user marks every section but one as complete  ${researchLeadApp}
    When the academic user fills in his finances          ${researchLeadApp}
    Then user is not able to submit his application as he exceeds research participation
    And the user clicks the button/link                   link=Application overview
    And collaborating is required to submit the application if Research participation is not 100pc   ${openCompetitionResearch_name}  ${researchLeadApp}  antonio.jenkins@jabbertype.example.com

Applicant Applies to Public content leading Competition
    [Documentation]  IFS-1012
    [Tags]  Applicant  HappyPath  CompAdmin
    [Setup]  log in as a different user                   becky.mason@gmail.com  ${short_password}
    Given logged in user applies to competition           ${openCompetitionPublicSector_name}
    When the user clicks the button/link                  link=Application details
    Then the user fills in the Application details        ${publicLeadApp}  Industrial research  ${tomorrowday}  ${month}  ${nextyear}
    And the user marks every section but one as complete  ${publicLeadApp}
    When the user navigates to Your-finances page         ${publicLeadApp}
    Then the user marks the finances as complete          ${publicLeadApp}  Calculate  52,214
    And collaborating is required to submit the application if Research participation is not 100pc  ${openCompetitionPublicSector_name}  ${publicLeadApp}  becky.mason@gmail.com

Project Finance is able to see the Overheads costs file
    [Documentation]  IFS-1724
    [Tags]  CompAdmin
    [Setup]  log in as a different user  &{internal_finance_credentials}
    Given the competition is now in Project Setup
    Then the project finance is able to download the Overheads file

*** Keywords ***
Custom Suite Setup
    ${month} =          get tomorrow month
    set suite variable  ${month}
    ${nextMonth} =  get next month
    set suite variable  ${nextMonth}
    ${nextyear} =       get next year
    Set suite variable  ${nextyear}
    ${tomorrowday} =    get tomorrow day
    Set suite variable  ${tomorrowday}
    The guest user opens the browser

The competition admin creates a competition for
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_Generic}
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}
    the user fills in the CS Milestones   ${month}  ${nextMonth}  ${nextyear}
    the internal user can see that the Generic competition has only one Application Question
    the user marks the Application as done  yes  Generic
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  jQuery=a:contains("Done")
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")

user is not able to submit his application as he exceeds research participation
    the user navigates to the page   ${dashboard_url}
    the user clicks the button/link  link=${researchLeadApp}
    the user clicks the button/link  link=Review and submit
    the user should see the element  jQuery=button:disabled:contains("Submit application")

Collaborating is required to submit the application if Research participation is not 100pc
    [Arguments]  ${competition}  ${application}  ${lead}
    the user fills in the inviting steps  ${collaborator}
    the user logs out if they are logged in
    the collaborator accepts and fills in his part in the application  ${competition}  ${application}
    the lead is able to submit the application  ${lead}  ${application}

the collaborator accepts and fills in his part in the application
    [Arguments]  ${competition}  ${application}
    the user reads his email and clicks the link  ${collaborator}  Invitation to collaborate in ${competition}  You are invited by  2
    the user is able to confirm the invite        ${collaborator}  ${short_password}
    the user navigates to Your-finances page      ${application}
    the user marks the finances as complete       ${application}  Calculate  52,214

the lead is able to submit the application
    [Arguments]  ${user}  ${application}
    log in as a different user       ${user}  ${short_password}
    the user clicks the button/link  link=${application}
    the user clicks the button/link  link=Review and submit
    the user should see the element  jQuery=.message-alert:contains("You will not be able to make changes")
    the user clicks the button/link  css=#submit-application-button
    the user clicks the button/link  css=button[type="submit"][data-submitted-text]
    the user clicks the button/link  link=Finished

the competition is now in Project Setup
    moving competition to Closed
    making the application a successful project
    moving competition to Project Setup

moving competition to Closed
    Connect to Database  @{database}
    execute sql string   UPDATE `${database_name}`.`milestone` SET `date`='2017-09-09 11:00:00' WHERE `type`='SUBMISSION_DATE' AND `competition_id`='${openCompetitionPublicSector}';

making the application a successful project
    the user navigates to the page   ${compPublicPage}
    the user clicks the button/link  jQuery=.button-large:contains("Notify assessors")
    the user clicks the button/link  jQuery=.button-large:contains("Close assessment")
    the user navigates to the page   ${compPublicPage}/funding
    the user clicks the button/link  jQuery=tr:contains("${publicLeadApp}") label
    the user clicks the button/link  css=[type="submit"][value="FUNDED"]
    the user navigates to the page   ${compPublicPage}/manage-funding-applications
    the user clicks the button/link  jQuery=tr:contains("${publicLeadApp}") label
    the user clicks the button/link  css=[name="write-and-send-email"]
    the internal sends the descision notification email to all applicants  Successful!

moving competition to Project Setup
    the user navigates to the page   ${compPublicPage}
    the user clicks the button/link  jQuery=.button-large:contains("Release feedback")

the project finance is able to download the Overheads file
    ${projectId} =  get project id by name  ${publicLeadApp}
    ${organisationId} =  get organisation id by name  Dreambit
    the user downloads the file  ${internal_finance_credentials["email"]}  ${server}/project-setup-management/project/${projectId}/finance-check/organisation/${organisationId}/eligibility  ${DOWNLOAD_FOLDER}/${excel_file}
    remove the file from the operating system  ${excel_file}

the internal user can see that the Generic competition has only one Application Question
    the user clicks the button/link  link=Application
    the user clicks the button/link  link=1. Edit this question
    the user clicks the button/link  link=Edit this question
    the user is able to configure the new question
    the user should be able to see the read only view of question correctly
    the user clicks the button/link  link=Competition setup
