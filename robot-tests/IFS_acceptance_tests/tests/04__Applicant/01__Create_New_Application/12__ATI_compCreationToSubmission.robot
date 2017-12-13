*** Settings ***
Documentation   IFS-2396  ATI Competition type template
...
Suite Setup     Custom Suite Setup
Suite Teardown  Close browser and delete emails
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${ATIcompetitionTitle}  ATI Competition
${ATIapplicationTitle}  ATI application

*** Test Cases ***
Comp Admin creates an ATI competition
    [Documentation]  IFS-2396
    [Tags]  HappyPath
    Given The user logs-in in new browser          &{Comp_admin1_credentials}
    And the user navigates to the page             ${CA_UpcomingComp}
    When the user clicks the button/link           link=Create competition
    Then the user fills in the CS Initial details  ${ATIcompetitionTitle}  ${month}  ${nextyear}  Aerospace Technology Institute
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility       ${RTO_TYPE_ID}
    And the user fills in the CS Milestones        ${month}  ${nextyear}
    And the user marks the application as done     yes  ${compType_Programme}
    And the user fills in the CS Assessors
    When the user clicks the button/link           link=Public content
    Then the user fills in the Public content and publishes  ATI
    When the user clicks the button/link           link=Return to setup overview
    Then the user should see the element           jQuery=div:contains("Public content") ~ .task-status-complete
    When the user clicks the button/link           jQuery=a:contains("Complete")
    Then the user clicks the button/link           jQuery=a:contains("Done")

Applicant applies to newly created ATI competition
    [Documentation]  IFS-2286
    [Tags]  HappyPath  MySQL
    When the competition is open                                 ${ATIcompetitionTitle}
    Then Lead Applicant applies to the new created competition   ${ATIcompetitionTitle}  &{RTO_lead_applicant_credentials}

Applicant submits his application
    [Documentation]  IFS-2286
    [Tags]  HappyPath  Pending
#    TODO  Pending due to IFS-2447
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details      ${ATIapplicationTitle}  Feasibility studies  ${tomorrowday}  ${month}  ${nextyear}
    Then the lead applicant fills all the questions and marks as complete(Programme)
    When the user navigates to Your-finances page       ${ATIapplicationTitle}
    And the user marks the finances as complete         ${ATIapplicationTitle}   labour costs  54,000  yes
    Then the applicant submits the application

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser