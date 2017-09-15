*** Settings ***
Documentation   IFS-1012 As a comp exec I am able to set Research and Public sector as an Eligible Lead applicant options in Competition setup
Suite Setup     Custom Suite Setup
Suite Teardown  Close browser and delete emails
Force Tags      Applicant  CompAdmin  HappyPath
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${compResearch}  Research can lead
${compPublic}    Public Sector can lead


*** Test Cases ***
Comp Admin Creates Competitions where Research or Public sector can lead
    [Documentation]  IFS-1012
    [Tags]
    [Setup]  the user logs-in in new browser       &{Comp_admin1_credentials}
    The competition admin creates a competition for  ${RTO_TYPE_ID}  ${compResearch}  Research
    The competition admin creates a competition for  ${PUBLIC_SECTOR_TYPE_ID}  ${compPublic}  Public

#Applicant Applies to Research leading Competition
#    [Documentation]  IFS-1012
#    [Tags]
#    [Setup]  log in as a different user  antonio.jenkins@jabbertype.example.com  ${short_password}
#    the user navigates to the page  ${frontDoor}
#    the user clicks the button/link  link=${openCompetitionResearch_name}
#




*** Keywords ***
Custom Suite Setup
    ${month} =          get tomorrow month
    set suite variable  ${month}
    ${nextMonth} =  get next month
    set suite variable  ${nextMonth}
    ${nextMonthWord} =  get next month as word
    ${nextyear} =       get next year
    Set suite variable  ${nextyear}

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