*** Settings ***
Documentation  INFUND-6390 As an Applicant I will be invited to add project costs, organisation and funding details via links within the 'Finances' section of my application
...
...            INFUND-6393 As an Applicant I will be invited to add Staff count and Turnover where the include projected growth table is set to 'No' within the Finances page of Competition setup
...
...            INFUND-6395 s an Applicant I will be invited to add Projected growth, and Organisation size where the include projected growth table is set to Yes within the Finances page of Competition setup
...
...            INFUND-6895 As an Lead Applicant I will be advised that changing my Research category after completing Funding level will reset the 'Funding level'
Suite Setup    Custom Suite Setup
Force Tags     Applicant  CompAdmin
Resource       ../../../resources/defaultResources.robot
Resource       ../FinanceSection_Commons.robot
Resource       ../../04__Comp_Admin/CompAdmin_Commons.robot

*** Variables ***
${compWithoutGrowth}    From new Competition to New Application
${applicationTitle}    New Application from the New Competition
${compWITHGrowth}    Competition with growth table

*** Test Cases ***
# For the testing of the story INFUND-6393, we need to create New Competition in order to apply the new Comp Setup fields
# Then continue with the applying to this Competition, in order to see the new Fields applied
Comp Admin starts a new Competition
    [Documentation]  INFUND-6393
    [Tags]  HappyPath
    [Setup]  guest user log-in  &{Comp_admin1_credentials}
    Given the user navigates to the page  ${CA_UpcomingComp}
    When the user clicks the button/link  jQuery=.button:contains("Create competition")
    Then the user fills in the CS Initial details  ${compWithoutGrowth}  ${day}  ${month}  ${year}
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility
    And the user fills in the CS Milestones  ${day}  ${month}  ${nextyear}

Comp Admin fills in the Milestone Dates and can see them formatted afterwards
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
    Given the user decides about the growth table  no  No

Comp admin completes ths competition setup
    [Documentation]  INFUND-6393
    [Tags]  HappyPath
    Given the user should see the element  jQuery=h1:contains("Competition setup")
    Then the user marks the Application as done
    And the user fills in the CS Assessors
    When the user clicks the button/link  jQuery=a:contains("Save")
    And the user navigates to the page    ${CA_UpcomingComp}
    Then the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${compWithoutGrowth}")

Competition is Open to Applications
    [Documentation]  INFUND-6393
    [Tags]  HappyPath  MySQL
    The competitions date changes so it is now Open  ${compWithoutGrowth}

Create new Application for this Competition
    [Documentation]
    [Tags]  HappyPath
    Lead Applicant applies to the new created competition  ${compWithoutGrowth}

Applicant visits his Finances
    [Documentation]  INFUND-6393
    [Tags]
    Given the user should see the element  jQuery=h1:contains("Application overview")
    When the user clicks the button/link   link=Your finances
    Then the user should see the element   jQuery=img.assigned[alt*=project]
    And the user should see the element    jQuery=img.assigned[alt*=organisation]
    And the the user should see that the funding depends on the research area
    And the user should see his finances empty
    [Teardown]  the user clicks the button/link  jQuery=a:contains("Return to application overview")

Applicant fills in the Application Details
    [Documentation]  INFUND-6895
    [Tags]  HappyPath
    Given the user should see the element      jQuery=h1:contains("Application overview")
    When the user clicks the button/link       link=Application details
    Then the user enters text to a text field  css=#application_details-title  ${applicationTitle}
    And the user selects technical feasibility and no to resubmission
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
    And the user should see the text in the page  Number of full time employees at your organisation.

Once the project growth table is selected
    [Documentation]  INFUND-6393
    [Tags]
    [Setup]  log in as a different user  &{Comp_admin1_credentials}
    Given the user navigates to the page  ${CA_UpcomingComp}
    When the user clicks the button/link  jQuery=.button:contains("Create competition")
    Then the user fills in the CS Initial details  Competition with growth table  ${day}  ${month}  ${year}
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility
    And the user fills in the CS Milestones  ${day}  ${month}  ${nextyear}
    When the user decides about the growth table  yes  Yes
    Then the user marks the Application as done
    And the user fills in the CS Assessors
    When the user clicks the button/link  jQuery=a:contains("Save")
    And the user navigates to the page    ${CA_UpcomingComp}
    Then the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${compWITHGrowth}")
    [Teardown]  The competitions date changes so it is now Open  ${compWITHGrowth}

As next step the Applicant cannot see the turnover field
    [Documentation]  INFUND-6393, INFUND-6395
    [Tags]
    Given Lead Applicant applies to the new created competition  ${compWITHGrowth}
    When the user clicks the button/link  link=Your finances
    And the user clicks the button/link   link=Your organisation
    Then the user should not see the text in the page  Turnover (£)
    And the user should see the text in the page  Full time employees
    And the user should see the text in the page  How many full-time employees did you have on the project at the close of your last financial year?

Organisation server side validation when no
    [Documentation]  INFUND-6393
    [Tags]  HappyPath
    # TODO Pending due to INFUND-8033
    [Setup]  log in as a different user  &{lead_applicant_credentials}
    Given the user navigates to Your-finances page    ${applicationTitle}
    Then the user clicks the button/link  link=Your organisation
    When the user clicks the button/link  jQuery=button:contains("Mark as complete")
    Then the user should see the element  jQuery=.error-summary-list:contains("Enter your organisation size.")

Organisation client side validation when no
    [Documentation]  INFUND-6393
    [Tags]
    Given the user selects medium organisation size
    When the user enters text to a text field  jQuery=label:contains("Turnover") + input  -33
    And the user moves focus to the element    jQuery=label:contains("Full time employees") + input
    Then the user should see the element       jQuery=span:contains("Turnover") ~ .error-message:contains("This field should be 0 or higher.")
    And the user enters text to a text field   jQuery=label:contains("Full time employees") + input  ${empty}
    When the user moves focus to the element   jQuery=button:contains("Mark as complete")
    And the user should see the element        jQuery=span:contains("Full time employees") ~ .error-message:contains("This field cannot be left blank.")
    When the user enters text to a text field  jQuery=label:contains("Turnover") + input  150
    And the user enters text to a text field   jQuery=label:contains("employees") + input  0
    And the user moves focus to the element    jQuery=button:contains("Mark as complete")
    Then the user should not see the element   css=.error-message

Mark Organisation as complete when no
    [Documentation]  INFUND-6393
    [Tags]  HappyPath
    Given the user enters text to a text field  jQuery=label:contains("employees") + input  42
    And the user enters text to a text field    jQuery=label:contains("Turnover") + input  17506
    And the user selects medium organisation size
    When the user clicks the button/link        jQuery=button:contains("Mark as complete")
    Then the user should see the element        jQuery=img.complete[alt*="Your organisation"]
    When the user clicks the button/link        link=Your organisation
    # Then the user should see the fields in readonly mode, but currently they are missing this attribute
    # TODO INFUND-8071
    Then the user should see the element        jQuery=button:contains("Edit your organisation")
    And the user clicks the button/link         jQuery=a:contains("Return to finances")

Funding subsection opens when Appl details and organisation info are provided
    [Documentation]  INFUND-6895
    [Tags]  HappyPath
    [Setup]  the user navigates to the page  ${dashboard_url}
    And the user clicks the button/link      link=${applicationTitle}
    When the user should see the element     jQuery=img.complete[alt*="Application details"]
    And the user clicks the button/link      link=Your finances
    And the user should see the element      jQuery=img.complete[alt*="Your organisation"]
    Then the user should see the element     jQuery=img.assigned[alt*="Your funding"]

Organisation server side validation when yes
    [Documentation]  INFUND-6393
    [Tags]
    [Setup]  the user navigates to Your-finances page  ${compWITHGrowth}
    # TODO Update when INFUND-8033 is done
    Given the user clicks the button/link  link=Your organisation
    When the user clicks the button/link   jQuery=button:contains("Mark as complete")
    Then the user should see the element   jQuery=.error-summary-list:contains("Enter your organisation size.")

Organisation client side validation when yes
    [Documentation]  INFUND-6395
    [Tags]
    When the user enters text to a text field  css=input[name$="month"]  42
    And the user enters text to a text field   css=input[name$="year"]  ${nextyear}
    Then the user should see the element       jQuery=.error-message:contains("Please enter a valid date.")
    When the user enters text to a text field  css=input[name$="month"]  12
    Then the user should see the element       jQuery=.error-message:contains("Please enter a past date.")
    And the user should not see the element    jQuery=.error-message:contains("Please enter a valid date.")
    When the user enters text to a text field  css=input[name$="year"]  2016
    And the user enters value to field         Annual turnover  ${EMPTY}
    And the user moves focus to the element    jQuery=button:contains("Mark as complete")
    Then the user should not see the element   jQuery=.error-message:contains("Please enter a past date.")
    And the user should see an error message in the field  Annual turnover  This field cannot be left blank.
    When the user enters value to field        Annual turnover  8.5
    And the user moves focus to the element    jQuery=td:contains("Annual profit") + td input
    Then the user should see an error message in the field  Annual turnover  This field can only accept whole numbers.
    # TODO such error messages should also trigger Error summary INFUND-8056
    And the user enters value to field         Annual profit  -5
    When the user enters value to field        Annual export  ${empty}
    Then the user should see an error message in the field  Annual export  This field cannot be left blank.
    When the user enters value to field        Research and development spend  6666666666666666666666666666666666666666666
    And the user moves focus to the element    jQuery=label:contains("employees") + input
    Then the user should see an error message in the field  Research and development spend  This field should be 2147483647 or lower.
    # TODO This error message will be different after INFUND-8080
    And the user enters value to field         Research and development spend  2147483647
    When the user enters text to a text field  jQuery=label:contains("employees") + input  22.4
    Then the user should see an error message in the field  employees  This field can only accept whole numbers.
    And the user should not see the element    jQuery=span:contains("Research and development spend") + *:contains("This field should be 2147483647 or lower.")
    When the user enters text to a text field  jQuery=label:contains("employees") + input  1
    Then the user should not see the element   jQuery=span:contains("employees") + .error-message

Mark Organisation as complete when yes
    [Documentation]  INFUND-6393
    [Tags]
    [Setup]  the user navigates to Your finances page  ${compWITHGrowth}
    Given the user clicks the button/link             link=Your organisation
    And the user selects medium organisation size
    Then the user enters text to a text field         css=input[name$="month"]  12
    And the user enters text to a text field          css=input[name$="year"]  2016
    Then the user enters value to field               Annual turnover  65000
    And the user enters value to field                Annual profit  2000
    And the user enters value to field                Annual export  3000
    And the user enters value to field                Research and development spend  15000
    When the user enters text to a text field         jQuery=label:contains("employees") + input  4
    # TODO pending due to INFUND-8107
    #    And the user clicks the button/link               jQuery=a:contains("Return to finances")
    #    And the user clicks the button/link               link=Your organisation
    #    Then the user should see the element              jQuery=td:contains("Research and development spend") + td input[value="15000"]
    When the user clicks the button/link              jQuery=button:contains("Mark as complete")
    Then the user should see the element              jQuery=img.complete[alt*="Your organisation"]

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

the the user should see that the funding depends on the research area
    the user should see the element  jQuery=h3:contains("Your funding") + p:contains("You must select a research category in application details ")

the user should see his finances empty
    the user should see the element  jQuery=thead:contains("Total project costs") ~ *:contains("£0")

the user selects technical feasibility and no to resubmission
    # Often those labels need double click. Thus i made a separate keyword to looks more tidy
    the user clicks the button/link  jQuery=label[for="financePosition-cat-33"]
    the user clicks the button/link  jQuery=label[for="financePosition-cat-33"]
    the user clicks the button/link  jQuery=label[for="resubmission-no"]
    the user clicks the button/link  jQuery=label[for="resubmission-no"]

the user decides about the growth table
    [Arguments]  ${edit}  ${read}
    the user should see the element  jQuery=h1:contains("Competition setup")
    the user clicks the button/link  link=Application
    the user clicks the button/link  link=Finances
    the user clicks the button/link  jQuery=a:contains("Edit this question")
    the user clicks the button/link  jQuery=label[for="include-growth-table-${edit}"]
    capture page screenshot
    the user clicks the button/link  jQuery=button:contains("Save and close")
    the user clicks the button/link  link=Finances
    the user should see the element  jQuery=dt:contains("Include project growth table") + dd:contains("${read}")
    capture page screenshot
    the user clicks the button/link  link=Application
    the user clicks the button/link  link=Competition setup

The competitions date changes so it is now Open
    [Arguments]  ${competition}
    Connect to Database  @{database}
    Change the open date of the Competition in the database to one day before  ${competition}
    the user navigates to the page  ${CA_Live}
    the user should see the element  jQuery=h2:contains("Open") ~ ul a:contains("${competition}")

Lead Applicant applies to the new created competition
    [Arguments]  ${competition}
    Connect to Database  @{database}
    log in as a different user   &{lead_applicant_credentials}
    ${competitionId} =  get comp id from comp title  ${competition}
    the user navigates to the page   ${server}/competition/${competitionId}/info/eligibility
    the user clicks the button/link  jQuery=a:contains("Apply now")
    the user clicks the button/link  jQuery=button:contains("Begin application")

the user enters value to field
    [Arguments]  ${field}  ${value}
    the user enters text to a text field  jQuery=td:contains("${field}") + td input  ${value}

the user should see an error message in the field
    [Arguments]  ${field}  ${errmsg}
    the user should see the element  jQuery=span:contains("${field}") + *:contains("${errmsg}")

the user selects medium organisation size
    the user clicks the button/link      jQuery=label:contains("Medium")  # TODO This selector will chenge with INFUND-8071
    the user clicks the button/link      jQuery=label:contains("Medium")  # Click it twice