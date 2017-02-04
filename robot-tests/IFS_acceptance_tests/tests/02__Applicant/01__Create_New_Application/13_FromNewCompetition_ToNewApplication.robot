*** Settings ***
Documentation  INFUND-6390 As an Applicant I will be invited to add project costs, organisation and funding details via links within the 'Finances' section of my application
...
...            INFUND-6393 As an Applicant I will be invited to add Staff count and Turnover where the include projected growth table is set to 'No' within the Finances page of Competition setup
Suite Setup    Custom Suite Setup
Force Tags     Applicant  CompAdmin
Resource       ../../../resources/defaultResources.robot
Resource       ../FinanceSection_Commons.robot
Resource       ../../04__Comp_Admin/CompAdmin_Commons.robot

*** Variables ***
${lastFourdigits}    \d{4}
${competitionForthisSuite}    From new Competition to New Application
${applicationTitle}    New Application from the New Competition

*** Test Cases ***
# For the testing of the story INFUND-6393, we need to create New Competition in order to apply the new Comp Setup fields
# Then continue with the applying to this Competition, in order to see the new Fields applied
Comp Admin starts a new Competition
    [Documentation]
    [Tags]  HappyPath
    [Setup]  guest user log-in  &{Comp_admin1_credentials}
    Given the user navigates to the page  ${CA_UpcomingComp}
    When the user clicks the button/link  jQuery=.button:contains("Create competition")
    Then the user fills in the CS Initial details  From new Competition to New Application  ${day}  ${month}  ${year}
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility
    And the user fills in the CS Milestones  ${day}  ${month}  ${nextyear}

Comp Admin fills in the Milestone Dates and can see them fortmatted afterwards
    [Documentation]  INFUND-7820
    [Tags]
    Given the user should see the element   jQuery=img[title$="is done"] + h3:contains("Milestones")
    When the user clicks the button/link    link=Milestones
    Then the user should see the element    jQuery=button:contains("Edit")
    And the user should see the dates in full format
    Then the user clicks the button/link    link=Competition setup

Application Finances should not include project growth
    [Documentation]  INFUND-6393
    [Tags]
    Given the user should see the element  jQuery=h1:contains("Competition setup")
    When the user clicks the button/link   link=Application
    And the user clicks the button/link    link=Finances
    When the user clicks the button/link   jQuery=a:contains("Edit this question")
    Then the user clicks the button/link   jQuery=label[for="include-growth-table-no"]
    And the user clicks the button/link    jQuery=button:contains("Save and close")
    When the user clicks the button/link   link=Finances
    Then the user should see the element   jQuery=dt:contains("Include project growth table") + dd:contains("No")
    When the user clicks the button/link   link=Application
    Then the user clicks the button/link   link=Competition setup

Comp admin completes ths competition setup
    [Documentation]
    [Tags]  HappyPath
    Given the user should see the element  jQuery=h1:contains("Competition setup")
    Then the user marks the Application as done
    And the user fills in the CS Assessors
    When the user clicks the button/link  jQuery=a:contains("Save")
    And the user navigates to the page    ${CA_UpcomingComp}
    Then the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competitionForthisSuite}")

Competition is Open to Applications
    [Documentation]
    [Tags]  HappyPath  MySQL
    [Setup]  Connect to Database  @{database}
    When Change the open date of the Competition in the database to one day before  ${competitionForthisSuite}
    Then the user navigates to the page  ${CA_Live}
    And the user should see the element  jQuery=h2:contains("Open") ~ ul a:contains("${competitionForthisSuite}")

Create new Application for this Competition
    [Documentation]
    [Tags]  HappyPath
    [Setup]  Connect to Database  @{database}
    Given log in as a different user   &{lead_applicant_credentials}
    ${competitionId} =  get comp id from comp title  From new Competition to New Application
    Log  ${competitionId}
    When the user navigates to the page   ${server}/competition/${competitionId}/info/eligibility
    Then the user clicks the button/link  jQuery=a:contains("Apply now")
    And the user clicks the button/link   jQuery=button:contains("Begin application")

Applicant visits his Finances
    [Documentation]
    [Tags]
    Given the user should see the element  jQuery=h1:contains("Application overview")
    When the user clicks the button/link   link=Your finances
    Then the user should see the element   jQuery=img.assigned[alt*=project]
    And the user should see the element    jQuery=img.assigned[alt*=organisation]
    And the the user should see that the funding depends on the research area
    And the user should see his finances empty
    [Teardown]  the user clicks the button/link  jQuery=a:contains("Return to application overview")


Applicant fills in the Application Details
    [Documentation]
    [Tags]  HappyPath
    Given the user should see the element      jQuery=h1:contains("Application overview")
    When the user clicks the button/link       link=Application details
    Then the user enters text to a text field  css=#application_details-title  ${applicationTitle}
    And the user clicks the button/link        jQuery=label[for="financePosition-cat-33"]
    And the user clicks the button/link        jQuery=label[for="resubmission-no"]
    And the user enters text to a text field   css=#application_details-startdate_day  ${day}
    And the user enters text to a text field   css=#application_details-startdate_month  ${month}
    And the user enters text to a text field   css=#application_details-startdate_year  ${nextyear}
    And the user enters text to a text field   css=#application_details-duration  24
    When The user clicks the button/link       jQuery=button[name="mark_as_complete"]
    Then the user clicks the button/link       link=Application Overview
    And the user should see the element        jQuery=img.complete[alt*="Application details"]

Turnover and Staff count fields
    [Documentation]  INFUND-6393
    [Tags]
    Given the user clicks the button/link  link=Your finances
    Then the user clicks the button/link   link=Your organisation
    And the user should see the text in the page  Turnover (£)
    And the user should see the text in the page  Full time employees

Organisation client side validation

Organisation server side validation

Mark Organisation as complete

Funding subsection opens when Appl details and organisation info are provided

*** Keywords ***
Custom Suite Setup
    ${day} =  get tomorrow day
    Set suite variable  ${day}
    ${month} =  get tomorrow month
    set suite variable  ${month}
    ${year} =  get tomorrow year
    Set suite variable  ${year}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    ${tomorrowfull} =  get tomorrow full
    Set suite variable  ${tomorrowfull}
    ${tomorrow_nextyear} =  get tomorrow full next year
    Set suite variable  ${tomorrow_nextyear}

the user should see the dates in full format
    the user should see the element  jQuery=td:contains("Briefing event") ~ td:contains("${tomorrow_nextyear}")

the user selects no in project growth table
    the user clicks the button/link   jQuery=label[for="include-growth-table-no"]

the the user should see that the funding depends on the research area
    the user should see the element  jQuery=h3:contains("Your funding") + p:contains("You must give your project a research category in application details")

the user should see his finances empty
    the user should see the element  jQuery=thead:contains("Total project costs") ~ *:contains("£0")
