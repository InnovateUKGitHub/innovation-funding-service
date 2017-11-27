*** Settings ***
Documentation   Suite description
...
...             IFS-2192 As a Portfolio manager I am able to create an EOI competition
Suite Setup     custom suite setup
Suite Teardown  Close browser and delete emails
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot

# This suite covers creating EOI type competition
*** Variables ***
${compType_EOI}    Expression of interest
*** Test Cases ***
Comp Admin Creates Competitions where Research or Public sector can lead
    [Documentation]  IFS-1012 IFS-182
    [Tags]  CompAdmin  HappyPath
    Given Logging in and Error Checking                     &{Comp_admin1_credentials}
    Then The competition admin creates a competition for    ${business_type_id}  EOI comp  EOI

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
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_EOI}
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}
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

the user marks the Application as done
    [Arguments]  ${growthTable}  ${comp_type}
    the user clicks the button/link  link=Application
    the user marks EOI application details as complete
    the user fills in the Finances questions  ${growthTable}
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element  jQuery=div:contains("Application") ~ .task-status-complete

the assessed questions are marked complete except finances(EOI type)
    the user marks each question as complete  Business opportunity
    the user marks each question as complete  Potential market
    the user marks each question as complete  Project exploitation
    the user marks each question as complete  Economic benefit

the user marks EOI application details as complete
    the user marks each question as complete  Application details
    the user marks each question as complete  Public description
    the user marks each question as complete  Scope
