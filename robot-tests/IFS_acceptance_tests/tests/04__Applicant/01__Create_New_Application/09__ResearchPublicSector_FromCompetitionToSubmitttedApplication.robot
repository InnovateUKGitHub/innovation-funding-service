*** Settings ***
Documentation   IFS-1012 As a comp exec I am able to set Research and Public sector as an Eligible Lead applicant options in Competition setup
...
...             IFS-182 As a comp exec I am able to configure Assessed questions in Competition setup
...
...             IFS-2879: As a Research applicant I MUST accept the grant terms and conditions
...
...             IFS-2832 As a Portfolio manager I am able to remove the Project details questions
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
${customQuestion}   How innovative is your project?

*** Test Cases ***
Comp Admin Creates Competitions where Research or Public sector can lead
    [Documentation]  IFS-1012 IFS-182 IFS-2832
    [Tags]  CompAdmin  HappyPath
    # In this test case we also check that we can remove the Project details questions in Comp Setup.
    Given Logging in and Error Checking                   &{Comp_admin1_credentials}
    Then The competition admin creates a competition for  ${ACADEMIC_TYPE_ID}  ${compResearch}  Research
    And The competition admin creates a competition for   ${PUBLIC_SECTOR_TYPE_ID}  ${compPublic}  Public

Requesting the id of this Competition
    [Documentation]  IFS-182
    ...   retrieving the id of the competition so that we can use it in urls
    [Tags]  HappyPath  MySQL
    ${reseachCompId} =  get comp id from comp title  ${compResearch}
    Set suite variable  ${reseachCompId}

The Applicant is able to apply to the competition once is Open and see the correct Questions
    [Documentation]  IFS-182 IFS-2832
    [Tags]  HappyPath  MySQL
    [Setup]  the competition moves to Open state  ${reseachCompId}
    Given log in as a different user              &{collaborator2_credentials}
    And logged in user applies to competition research     ${compResearch}  2
    Then the user should see the element          jQuery=li:contains("${customQuestion}")
    When the user should see the element          jQuery=li:contains("Scope")
    Then the user should not see the element      jQuery=li:contains("Public description")
    And the user should not see the element       jQuery=li:contains("Project summary")

Applicant Applies to Research leading Competition
    [Documentation]  IFS-1012  IFS-2879
    [Tags]  Applicant  HappyPath
    [Setup]  Log in as a different user                   antonio.jenkins@jabbertype.example.com  ${short_password}
    # This application is for competition Photonics for Research, which is Web test data.
    # That is why we have 2 diferent test cases, where Research users apply to a Research leading competition.
    Given logged in user applies to competition research  ${openCompetitionResearch_name}  2
    When the user clicks the button/link                  link=Application details
    Then the user fills in the Application details        ${researchLeadApp}  ${tomorrowday}  ${month}  ${nextyear}
    And the user marks every section but one as complete  ${researchLeadApp}  Experimental development
    When the academic user fills in his finances          ${researchLeadApp}
    And the user enters the project location
    Then user is not able to submit his application as he exceeds research participation
    And the user clicks the button/link                   link=Application overview
    And collaborating is required to submit the application if Research participation is not 100pc   ${openCompetitionResearch_name}  ${researchLeadApp}  antonio.jenkins@jabbertype.example.com
#Here?? at this point the comp is available

Applicant Applies to Public content leading Competition
    [Documentation]  IFS-1012
    [Tags]  Applicant  HappyPath  CompAdmin
    [Setup]  log in as a different user                   becky.mason@gmail.com  ${short_password}
    # This application is for competition Photonics for Public, which is Web test data.
    Given logged in user applies to competition public           ${openCompetitionPublicSector_name}  4
    When the user clicks the button/link                  link=Application details
    Then the user fills in the Application details        ${publicLeadApp}  ${tomorrowday}  ${month}  ${nextyear}
    And the user marks every section but one as complete  ${publicLeadApp}  Experimental development
    When the user navigates to Your-finances page         ${publicLeadApp}
    Then the user marks the finances as complete          ${publicLeadApp}  Calculate  52,214  no
    And collaborating is required to submit the application if Research participation is not 100pc  ${openCompetitionPublicSector_name}  ${publicLeadApp}  becky.mason@gmail.com

Project Finance is able to see the Overheads costs file
    [Documentation]  IFS-1724
    [Tags]  CompAdmin
    [Setup]  log in as a different user  &{internal_finance_credentials}
    Given the competition is now in Project Setup
    Then the user is able to download the overheads file

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser

The competition admin creates a competition for
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_Generic}
    the user selects the Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}  1  # 1 means 30%
    the user fills in the CS Milestones   ${month}  ${nextyear}
    the internal user can see that the Generic competition has only one Application Question
    The user removes the Project details questions and marks the Application section as done  yes  Generic
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  css=button[type="submit"]
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user removes some of the Project details questions
    [Documentation]  IFS-2832
    the user clicks the button/link      jQuery=li:contains("Project summary") button:contains("Remove")
    the user should not see the element  jQuery=li:contains("Project summary")
    the user marks each question as complete  Public description
    the user marks each question as complete  Scope
    the user clicks the button/link      link=Public description
    the user clicks the button/link      css=button[name="deleteQuestion"]
    the user should not see the element  jQuery=li:contains("Public description")

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
#here?? - Does this work?

the collaborator accepts and fills in his part in the application
    [Arguments]  ${competition}  ${application}
    the user reads his email and clicks the link  ${collaborator}  Invitation to collaborate in ${competition}  You are invited by  2
    the user is able to confirm the invite        ${collaborator}  ${short_password}
    the user navigates to Your-finances page      ${application}
    the user marks the finances as complete       ${application}  Calculate  52,214  no

the lead is able to submit the application
    [Arguments]  ${user}  ${application}
    log in as a different user       ${user}  ${short_password}
    the user clicks the button/link  link=${application}
    then the applicant completes application team
    the user clicks the button/link  link=Review and submit
    the user should see the element  jQuery=.message-alert:contains("You will not be able to make changes")
    the user clicks the button/link  css=#submit-application-button
    the user clicks the button/link  css=button[type="submit"][data-submitted-text]
    the user clicks the button/link  link=Finished

the competition is now in Project Setup
    moving competition to Closed                  ${openCompetitionPublicSector}
    making the application a successful project   ${openCompetitionPublicSector}  ${publicLeadApp}
    moving competition to Project Setup           ${openCompetitionPublicSector}

the user is able to download the overheads file
    ${projectId} =  get project id by name  ${publicLeadApp}
    ${organisationId} =  get organisation id by name  Dreambit
    the project finance user is able to download the overheads file    ${projectId}  ${organisationId}

the internal user can see that the Generic competition has only one Application Question
    the user clicks the button/link  link=Application
    the user clicks the button/link  link=1. Edit this question
    the user is able to configure the new question  ${customQuestion}
    the user should be able to see the read only view of question correctly  ${customQuestion}
    the user clicks the button/link  link=Competition setup
