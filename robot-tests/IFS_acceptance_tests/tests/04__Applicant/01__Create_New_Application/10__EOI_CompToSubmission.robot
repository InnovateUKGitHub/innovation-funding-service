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
${comp_name}       EOI comp
*** Test Cases ***
Comp Admin Creates EOI type competition
    [Documentation]  IFS-2192
    [Tags]  CompAdmin  HappyPath
    Given Logging in and Error Checking                     &{Comp_admin1_credentials}
    Then The competition admin creates a EOI Comp     ${business_type_id}  ${comp_name}  EOI

Applicant applies to newly created EOI comp
    [Documentation]  IFS-2192
    [Tags]  HappyPath
    [Setup]  the EOI comp is now open to apply    ${comp_name}
    Lead Applicant applies to the new created competition    ${comp_name}

*** Keywords ***
Custom Suite Setup
    predefined date keywords
    The guest user opens the browser

The competition admin creates a EOI Comp
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_EOI}
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}
    the user fills in the CS Milestones   ${month}  ${nextMonth}  ${nextyear}
    the user marks the Application as done
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  jQuery=a:contains("Done")
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user marks the Application as done
    the user clicks the button/link  link=Application
    the user marks EOI application details as complete
    the assessed questions are marked complete(EOI type)
    the user fills in the Finances questions
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element  jQuery=div:contains("Application") ~ .task-status-complete

the assessed questions are marked complete(EOI type)
    the user marks each question as complete  Business opportunity and potential market
    the user marks each question as complete  Innovation
    the user marks each question as complete  Project team
    the user marks each question as complete  Funding and adding value
    the user should see the element           jQuery=button:contains("Add question")

the user marks EOI application details as complete
    the user marks each question as complete  Application details
    the user marks each question as complete  Project summary
    the user marks each question as complete  Scope

# PLease note the finances in the commons cant be used as we have default text in Funding rules text area and
# finances requirements are not fully ready yet
the user fills in the Finances questions
    the user clicks the button/link   link=Finances
    the user clicks the button/link   jQuery=.button:contains("Done")

the EOI comp is now open to apply
    [Arguments]  ${competition_name}
    Connect to Database  @{database}
    change the open date of the competition in the database to one day before  ${competition_name}