*** Settings ***
Documentation   IFS-1012 As a comp exec I am able to set Research and Public sector as an Eligible Lead applicant options in Competition setup
Suite Setup     Custom Suite Setup
Suite Teardown  Close browser and delete emails
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${compResearch}     Research can lead
${compPublic}       Public Sector can lead
${researchLeadApp}  Research Leading Application
${publicLeadApp}    Public Sector leading Application

*** Test Cases ***
Comp Admin Creates Competitions where Research or Public sector can lead
    [Documentation]  IFS-1012
    [Tags]  CompAdmin
    Given the user logs-in in new browser  &{Comp_admin1_credentials}
    Then The competition admin creates a competition for  ${RTO_TYPE_ID}  ${compResearch}  Research
    And The competition admin creates a competition for  ${PUBLIC_SECTOR_TYPE_ID}  ${compPublic}  Public

Applicant Applies to Research leading Competition
    [Documentation]  IFS-1012
    [Tags]  Applicant  HappyPath
    [Setup]  log in as a different user  antonio.jenkins@jabbertype.example.com  ${short_password}
    Given logged in user applies to competition     ${openCompetitionResearch_name}
    When the user clicks the button/link            link=Application details
    Then the user fills in the Application details  ${researchLeadApp}  Experimental development  ${tomorrowday}  ${month}  ${nextyear}
    And the user marks every section but one as complete  ${researchLeadApp}
    When the academic user fills in his finances           ${researchLeadApp}
    Then he is not able to submit his application as he exceeds research participation

If Research participation is not 100pc collaborating is required to submit the application
    [Documentation]  IFS-1012
    [Tags]  Applicant
    [Setup]  the user clicks the button/link   link=Application overview
    When the user fills in the inviting steps  ${test_mailbox_one}+amy@gmail.com
    Then the user logs out if they are logged in
    When the collaborator accepts and fills in his part in the application  ${test_mailbox_one}+amy@gmail.com


Applicant Applies to Public content leading Competition
    [Documentation]  IFS-1012
    [Tags]  Applicant  HappyPath
    [Setup]  log in as a different user  dave.adams@gmail.com  ${short_password}
    Given logged in user applies to competition  ${openCompetitionPublicSector_name}
    When the user clicks the button/link         link=Application details
    Then the user fills in the Application details  ${publicLeadApp}  Industrial research  ${tomorrowday}  ${month}  ${nextyear}
    And the user marks every section but one as complete  ${publicLeadApp}
    When the user navigates to Your-finances page  ${publicLeadApp}
    And the user marks the finances as complete    ${publicLeadApp}

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

The competition admin creates a competition for
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_Generic}
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${RTO_TYPE_ID}
    the user fills in the CS Milestones   ${month}  ${nextMonth}  ${nextyear}
    the user marks the Application as done  yes
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  jQuery=a:contains("Done")
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")

he is not able to submit his application as he exceeds research participation
    the user navigates to the page   ${dashboard_url}
    the user clicks the button/link  link=${researchLeadApp}
    the user clicks the button/link  link=Review and submit
    the user should see the element  jQuery=button:disabled:contains("Submit application")

the collaborator accepts and fills in his part in the application
    [Arguments]  ${collaborator}
    the user reads his email and clicks the link  ${collaborator}  Invitation to collaborate in ${openCompetitionResearch_name}  You are invited by  2
    the user is able to confirm the invite  ${collaborator}  ${short_password}
    Capture Page Screenshot
