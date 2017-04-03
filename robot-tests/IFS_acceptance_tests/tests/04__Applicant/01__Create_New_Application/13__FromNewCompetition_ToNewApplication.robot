*** Settings ***
Documentation     INFUND-6390 As an Applicant I will be invited to add project costs, organisation and funding details via links ƒin the 'Finances' section of my application
...
...               INFUND-6393 As an Applicant I will be invited to add Staff count and Turnover where the include projected growth table is set to 'No' within the Finances page of Competition setup
...
...               INFUND-6395 s an Applicant I will be invited to add Projected growth, and Organisation size where the include projected growth table is set to Yes within the Finances page of Competition setup
...
...               INFUND-6895 As an Lead Applicant I will be advised that changing my Research category after completing Funding level will reset the 'Funding level'
Suite Setup       Custom Suite Setup
Suite Teardown    the user closes the browser
Force Tags        Applicant    CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../FinanceSection_Commons.robot
Resource          ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${compWithoutGrowth}    From new Competition to New Application
${applicationTitle}    New Application from the New Competition
${compWITHGrowth}    Competition with growth table

*** Test Cases ***
Comp Admin starts a new Competition
    [Documentation]    INFUND-6393
    [Tags]    HappyPath
    [Setup]    guest user log-in    &{Comp_admin1_credentials}
    # For the testing of the story INFUND-6393, we need to create New Competition in order to apply the new Comp Setup fields
    # Then continue with the applying to this Competition, in order to see the new Fields applied
    Given the user navigates to the page    ${CA_UpcomingComp}
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    Then the user fills in the CS Initial details    ${compWithoutGrowth}  ${tomorrowday}  ${month}  ${nextyear}
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility
    And the user fills in the CS Milestones  ${tomorrowday}  ${dayAfterTomorrow}  ${month}  ${nextyear}

Comp Admin fills in the Milestone Dates and can see them formatted afterwards
    [Documentation]    INFUND-7820
    [Tags]
    Given the user should see the element    jQuery=img[title$="is done"] + h3:contains("Milestones")
    When the user clicks the button/link    link=Milestones
    Then the user should see the element    jQuery=button:contains("Edit")
    And the user should see the dates in full format
    Then the user clicks the button/link    link=Competition setup

Application Finances should not include project growth
    [Documentation]    INFUND-6393
    [Tags]
    Given the user decides about the growth table    no    No

Comp admin completes ths competition setup
    [Documentation]    INFUND-6393
    [Tags]    HappyPath
    Given the user should see the element    jQuery=h1:contains("Competition setup")
    Then the user marks the Application as done
    And the user fills in the CS Assessors
    When the user clicks the button/link  link=Public content
    Then the user fills in the Public content and publishes
    And the user clicks the button/link  link=Return to setup overview
    And the user should see the element  css=img[title='The "Public content" section is done']
    When the user clicks the button/link    jQuery=a:contains("Save")
    And the user navigates to the page    ${CA_UpcomingComp}
    Then the user should see the element    jQuery=h2:contains("Ready to open") ~ ul a:contains("${compWithoutGrowth}")

Competition is Open to Applications
    [Documentation]    INFUND-6393
    [Tags]    HappyPath    MySQL
    The competitions date changes so it is now Open    ${compWithoutGrowth}

Create new Application for this Competition
    [Tags]    HappyPath
    Lead Applicant applies to the new created competition    ${compWithoutGrowth}

Applicant visits his Finances
    [Documentation]    INFUND-6393
    [Tags]
    Given the user should see the element    jQuery=h1:contains("Application overview")
    When the user clicks the button/link    link=Your finances
    Then the user should see the element    jQuery=li:contains("Your project costs") > .action-required
    And the user should see the element    jQuery=li:contains("Your organisation") > .action-required
    And the the user should see that the funding depends on the research area
    And the user should see his finances empty
    [Teardown]    the user clicks the button/link    jQuery=a:contains("Return to application overview")

Applicant fills in the Application Details
    [Documentation]    INFUND-6895
    [Tags]    HappyPath
    Given the user should see the element    jQuery=h1:contains("Application overview")
    When the user clicks the button/link    link=Application details
    Then the user enters text to a text field    css=#application_details-title    ${applicationTitle}
    And the user selects feasibility studies and no to resubmission and an innovation area
    And the user enters text to a text field    css=#application_details-startdate_day    ${tomorrowday}
    And the user enters text to a text field    css=#application_details-startdate_month    ${month}
    And the user enters text to a text field    css=#application_details-startdate_year    ${nextyear}
    And the user enters text to a text field    css=#application_details-duration    24
    When The user clicks the button/link    jQuery=button[name="mark_as_complete"]
    Then the user clicks the button/link    link=Application Overview
    And the user should see the element     jQuery=li:contains("Application details") > .task-status-complete

Turnover and Staff count fields
    [Documentation]    INFUND-6393
    [Tags]
    Given the user clicks the button/link    link=Your finances
    Then the user clicks the button/link    link=Your organisation
    And the user should see the text in the page    Turnover (£)
    And the user should see the text in the page    Full time employees
    And the user should see the text in the page    Number of full time employees at your organisation.

Once the project growth table is selected
    [Documentation]    INFUND-6393
    [Tags]
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page    ${CA_UpcomingComp}
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    Then the user fills in the CS Initial details    Competition with growth table  ${tomorrowday}  ${month}  ${nextyear}
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility
    And the user fills in the CS Milestones  ${tomorrowday}  ${dayAfterTomorrow}  ${month}  ${nextyear}
    When the user decides about the growth table    yes    Yes
    Then the user marks the Application as done
    And the user fills in the CS Assessors
    When the user clicks the button/link  link=Public content
    Then the user fills in the Public content and publishes
    And the user clicks the button/link  link=Return to setup overview
    And the user should see the element  css=img[title='The "Public content" section is done']
    When the user clicks the button/link    jQuery=a:contains("Save")
    And the user navigates to the page    ${CA_UpcomingComp}
    Then the user should see the element    jQuery=h2:contains("Ready to open") ~ ul a:contains("${compWITHGrowth}")
    [Teardown]    The competitions date changes so it is now Open    ${compWITHGrowth}

As next step the Applicant cannot see the turnover field
    [Documentation]    INFUND-6393, INFUND-6395
    [Tags]
    Given Lead Applicant applies to the new created competition    ${compWITHGrowth}
    When the user clicks the button/link    link=Your finances
    And the user clicks the button/link    link=Your organisation
    Then the user should not see the text in the page    Turnover (£)
    And the user should see the text in the page    Full time employees
    And the user should see the text in the page    How many full-time employees did you have on the project at the close of your last financial year?

Organisation server side validation when no
    [Documentation]    INFUND-6393
    [Tags]    HappyPath
    [Setup]    log in as a different user    &{lead_applicant_credentials}
    Given the user navigates to Your-finances page    ${applicationTitle}
    Then the user clicks the button/link    link=Your organisation
    When the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then the user should see the element    jQuery=.error-summary-list:contains("Enter your organisation size.")
    When the user enters text to a text field    jQuery=label:contains("Turnover") + input    -42
    And the user enters text to a text field    jQuery=label:contains("employees") + input    15.2
    And the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then the user should see the element    jQuery=.error-summary li:contains("This field should be 0 or higher.")
    And the user should see the element    jQuery=.error-summary li:contains("This field can only accept whole numbers.")
    And the user should not see the element    jQuery=h1:contains("Your finances")
    # Checking that by marking as complete, the user doens't get redirected to the main finances page

Organisation client side validation when no
    [Documentation]    INFUND-6393
    [Tags]
    Given the user selects medium organisation size
    When the user enters text to a text field    jQuery=label:contains("Turnover") + input    -33
    And the user moves focus to the element    jQuery=label:contains("Full time employees") + input
    Then the user should see a field and summary error      This field should be 0 or higher.
    And the user enters text to a text field    jQuery=label:contains("Full time employees") + input    ${empty}
    When the user moves focus to the element    jQuery=button:contains("Mark as complete")
    Then the user should see a field and summary error      This field cannot be left blank.
    When the user enters text to a text field    jQuery=label:contains("Turnover") + input    150
    And the user enters text to a text field    jQuery=label:contains("employees") + input    0
    And the user moves focus to the element    jQuery=button:contains("Mark as complete")
    Then the user should not see the element    css=.error-message

Mark Organisation as complete when no
    [Documentation]    INFUND-6393
    [Tags]    HappyPath
    Given the user enters text to a text field    jQuery=label:contains("employees") + input    42
    And the user enters text to a text field    jQuery=label:contains("Turnover") + input    17506
    And the user selects medium organisation size
    When the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then the user should see the element    jQuery=li:contains("Your organisation") > .task-status-complete
    When the user clicks the button/link    link=Your organisation
    # Then the user should see the fields in readonly mode, but currently they are missing this attribute
    # TODO INFUND-8071
    Then the user should see the element    jQuery=button:contains("Edit your organisation")
    And the user clicks the button/link    jQuery=a:contains("Return to finances")

The Lead applicant is able to edit and re-submit when no
    [Documentation]  INFUND-8518
    [Tags]
    Given the user can edit resubmit and read only of the organisation

Funding subsection opens when Appl details and organisation info are provided
    [Documentation]    INFUND-6895
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${dashboard_url}
    And the user clicks the button/link    link=${applicationTitle}
    When the user should see the element   jQuery=li:contains("Application details") > .task-status-complete
    And the user clicks the button/link    link=Your finances
    And the user should see the element    jQuery=li:contains("Your organisation") > .task-status-complete
    Then the user should see the element    jQuery=li:contains("Your funding") > .action-required

Organisation server side validation when yes
    [Documentation]    INFUND-6393
    [Tags]
    [Setup]    the user navigates to the growth table finances
    Given the user clicks the button/link    link=Your organisation
    When the user clicks the button/link    jQuery=button:contains("Mark as complete")
    #Then the user should see the element    jQuery=.error-summary-list:contains("Enter your organisation size.")
    And the user should see the element    jQuery=.error-summary-list li:contains("This field cannot be left blank.")
    And the user should see the element    jQuery=.error-message:contains("This field cannot be left blank.")
    And the user should see the element    jQuery=.error-summary-list li:contains("Please enter a valid date.")
    And the user should see the element    jQuery=.error-message:contains("Please enter a valid date.")
    And The user should see a field error    This field cannot be left blank.
    And The user should see a field error    Please enter a valid date.
    #And The user should see a field error    Enter your organisation size
    #TODO Enable the above checks when INFUND-8297 is ready

Organisation client side validation when yes
    [Documentation]    INFUND-6395
    [Tags]
    When the user enters text to a text field    css=input[name$="month"]    42
    Then the user should see a field and summary error      Please enter a valid date.
    When the user enters text to a text field    css=input[name$="month"]    12
    And the user enters text to a text field    css=input[name$="year"]    ${nextyear}
    Then the user should see a field and summary error      Please enter a past date.
    When the user enters text to a text field    css=input[name$="year"]    2016
    And the user enters value to field    Annual turnover    ${EMPTY}
    Then the user should see a field and summary error      This field cannot be left blank.
    When the user enters value to field    Annual turnover    8.5
    And the user moves focus to the element    jQuery=td:contains("Annual profit") + td input
    Then the user should see a field and summary error      This field can only accept whole numbers.
    And the user enters value to field    Annual profit    -5
    When the user enters value to field    Annual export    ${empty}
    Then the user should see a field and summary error      This field cannot be left blank.
    When the user enters value to field    Research and development spend    6666666666666666666666666666666666666666666
    And the user moves focus to the element    jQuery=label:contains("employees") + input
    Then the user should see an error message in the field    Research and development spend    This field should be 2147483647 or lower.
    # TODO This error message will be different after INFUND-8080
    And the user enters value to field    Research and development spend    2147483647
    When the user enters text to a text field    jQuery=label:contains("employees") + input    22.4
    Then the user should see a field and summary error      This field can only accept whole numbers.
    And the user should not see the element    jQuery=span:contains("Research and development spend") + *:contains("This field should be 2147483647 or lower.")
    When the user enters text to a text field    jQuery=label:contains("employees") + input    1
    Then the user should not see the element    jQuery=span:contains("employees") + .error-message

Mark Organisation as complete when yes
    [Documentation]    INFUND-6393
    [Tags]
    [Setup]    the user navigates to the growth table finances
    Given the user clicks the button/link    link=Your organisation
    And the user selects medium organisation size
    Then the user enters text to a text field    css=input[name$="month"]    12
    And the user enters text to a text field    css=input[name$="year"]    2016
    And the user populates the project growth table
    When the user enters text to a text field    jQuery=label:contains("employees") + input    4
    # TODO pending due to INFUND-8107
    #    And the user clicks the button/link    jQuery=a:contains("Return to finances")
    #    And the user clicks the button/link    link=Your organisation
    #    Then the user should see the element    jQuery=td:contains("Research and development spend") + td input[value="15000"]
    When the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then the user should see the element    jQuery=li:contains("Your organisation") > .task-status-complete

The Lead Applicant is able to edit and re-submit when yes
    [Documentation]  INFUND-8518
    [Tags]
    Given the user can edit resubmit and read only of the organisation

Applicant can view and edit project growth table
    [Documentation]    INFUND-6395
    [Tags]
    Given the user navigates to the growth table finances
    When the user clicks the button/link    link=Your organisation
    Then the user should view the project growth table
    And the user can edit the project growth table
    And the user populates the project growth table
    and the user clicks the button/link     jQuery=button:contains("Mark as complete")

Newly created collaborator can view and edit project Growth table
    [Documentation]  INFUND-8426
    [Tags]
    [Setup]  Invite a non-existing collaborator in Appplication with Growth table
    When the user navigates to the growth table finances
    and the user clicks the button/link     link=Your organisation
    and the user selects medium organisation size
    then the user enters text to a text field    css=input[name$="month"]    12
    and the user enters text to a text field    css=input[name$="year"]    2016
    and the user populates the project growth table
    and the user clicks the button/link      jQuery=button:contains("Mark as complete")
    and the user should not see an error in the page

Invite Collaborator in Application with Growth table
    [Documentation]  INFUND-8518
    [Tags]  Email  Failing
    # TODO INFUND-8561
    [Setup]  the user navigates to the page             ${dashboard_url}
    Given the lead applicant invites an existing user   ${compWITHGrowth}  ${collaborator1_credentials["email"]}
    When the user reads his email and clicks the link   ${collaborator1_credentials["email"]}  Invitation to collaborate in ${compWITHGrowth}  You will be joining as part of the organisation    3
    Then the user should see the element                jQuery=h1:contains("We have found an account with the invited email address")
    And the user clicks the button/link                 link=Sign into the Innovation Funding Service.
    When guest user log-in                              &{collaborator1_credentials}
    Then the user clicks the button/link                link=Continue to application

Non-lead can mark Organisation as complete
    [Documentation]  INFUND-8518
    [Tags]  Failing
    # TODO INFUND-8561
    Given the user navigates to the page            ${DASHBOARD_URL}
    And the user clicks the button/link             link=${compWITHGrowth}
    When the user clicks the button/link            link=Your finances
    And the user clicks the button/link             link=Your organisation
    Then the user selects medium organisation size
    And the user enters text to a text field        css=input[name$="month"]  12
    And the user enters text to a text field        css=input[name$="year"]  2016
    Then the user populates the project growth table
    And the user enters text to a text field        jQuery=label:contains("employees") + input  42
    When the user clicks the button/link            jQuery=button:contains("Mark as complete")
    Then the user should see the element            jQuery=li:contains("Your organisation") > .task-status-complete

Non-lead can can edit and remark Organisation as Complete
    [Documentation]  INFUND-8518
    [Tags]  Failing
    # TODO INFUND-8561
    Given the user can edit resubmit and read only of the organisation

*** Keywords ***
Custom Suite Setup
    ${tomorrowday} =    get tomorrow day
    Set suite variable    ${tomorrowday}
    ${dayAfterTomorrow} =  get the day after tomorrow
    Set suite variable    ${dayAfterTomorrow}
    ${month} =    get tomorrow month
    set suite variable    ${month}
    ${year} =    get tomorrow year
    Set suite variable    ${year}
    ${nextyear} =    get next year
    Set suite variable    ${nextyear}
    ${tomorrowfull} =    get tomorrow full
    Set suite variable    ${tomorrowfull}
    ${dateDayAfterNextYear} =  get the day after tomorrow full next year
    Set suite variable    ${dateDayAfterNextYear}
    Delete the emails from both test mailboxes

the user should see the dates in full format
    the user should see the element    jQuery=td:contains("Allocate assessors") ~ td:contains("${dateDayAfterNextYear}")

the the user should see that the funding depends on the research area
    the user should see the element    jQuery=h3:contains("Your funding") + p:contains("You must select a research category in application details ")

the user should see his finances empty
    the user should see the element    jQuery=thead:contains("Total project costs") ~ *:contains("£0")

the user selects feasibility studies and no to resubmission and an innovation area
    the user clicks the button/link    jQuery=legend:contains("Research category")
    the user clicks the button/link    jQuery=button:contains("Choose your research")
    the user clicks the button twice   jQuery=label[for^="researchCategoryChoice"]:contains("Feasibility studies")
    the user clicks the button/link    jQuery=button:contains(Save)
    the user clicks the button/link    jQuery=button:contains("Change your innovation area")
    the user clicks the button twice   jQuery=label[for="innovationAreaChoice-5"]
    the user clicks the button/link    jQuery=button:contains(Save)
    the user clicks the button twice   jQuery=label[for="application.resubmission-no"]


the user decides about the growth table
    [Arguments]    ${edit}    ${read}
    the user should see the element    jQuery=h1:contains("Competition setup")
    the user clicks the button/link    link=Application
    the user clicks the button/link    link=Finances
    the user clicks the button/link    jQuery=a:contains("Edit this question")
    the user clicks the button/link    jQuery=label[for="include-growth-table-${edit}"]
    capture page screenshot
    the user clicks the button/link    jQuery=button:contains("Save and close")
    the user clicks the button/link    link=Finances
    the user should see the element    jQuery=dt:contains("Include project growth table") + dd:contains("${read}")
    capture page screenshot
    the user clicks the button/link    link=Application
    the user clicks the button/link    link=Competition setup

The competitions date changes so it is now Open
    [Arguments]    ${competition}
    Connect to Database    @{database}
    Change the open date of the Competition in the database to one day before    ${competition}
    the user navigates to the page    ${CA_Live}
    the user should see the element    jQuery=h2:contains("Open") ~ ul a:contains("${competition}")

Lead Applicant applies to the new created competition
    [Arguments]    ${competition}
    Connect to Database    @{database}
    log in as a different user    &{lead_applicant_credentials}
    ${competitionId} =    get comp id from comp title    ${competition}
    the user navigates to the page    ${server}/competition/${competitionId}/info/eligibility
    the user clicks the button/link    jQuery=a:contains("Apply now")
    the user clicks the button/link    jQuery=a:contains("Begin application")

the user enters value to field
    [Arguments]    ${field}    ${value}
    the user enters text to a text field    jQuery=td:contains("${field}") + td input    ${value}

the user should see an error message in the field
    [Arguments]    ${field}    ${errmsg}
    the user should see the element    jQuery=span:contains("${field}") + *:contains("${errmsg}")

the user selects medium organisation size
    the user selects the radio button    financePosition-organisationSize  ${MEDIUM_ORGANISATION_SIZE}
    the user selects the radio button    financePosition-organisationSize  ${MEDIUM_ORGANISATION_SIZE}

the user populates the project growth table
    the user enters value to field    Annual turnover    65000
    the user enters value to field    Annual profit    2000
    the user enters value to field    Annual export    3000
    the user enters value to field    Research and development spend    15000

the user should view the project growth table
    the user should see the text in the element    css=table.extra-margin-bottom tr:nth-of-type(1) th:nth-of-type(1)    Section
    the user should see the text in the element    css=table.extra-margin-bottom tr:nth-of-type(1) th:nth-of-type(2)    Last financial year (£)
    the user should see the text in the element    jQuery=tr:nth-child(1) td:nth-child(1) span    Annual turnover
    the user should see the element    jQuery=td input[value="65000"]
    the user should see the text in the element    jQuery=tr:nth-child(2) td:nth-child(1) span    Annual profits
    the user should see the element    jQuery=td input[value="2000"]
    the user should see the text in the element    jQuery=tr:nth-child(3) td:nth-child(1) span    Annual export
    the user should see the element    jQuery=td input[value="3000"]
    the user should see the text in the element    jQuery=tr:nth-child(4) td:nth-child(1) span    Research and development spend
    the user should see the element    jQuery=td input[value="15000"]

the user can edit the project growth table
    the user clicks the button/link    jQuery=button.buttonlink:contains('Edit your organisation')
    then the user selects the radio button    financePosition-organisationSize    ${SMALL_ORGANISATION_SIZE}
    the user enters text to a text field    jQuery=tr:nth-child(1) .form-control    4000
    the user enters text to a text field    jQuery=td input[value="65000"]    5000

the applicant enters valid inputs
    The user clicks the button/link         jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    The user enters text to a text field    name=organisations[1].organisationName  Ludlow
    The user enters text to a text field    name=organisations[1].invites[0].personName    Jessica Doe
    The user enters text to a text field    name=organisations[1].invites[0].email  ${collaborator1_credentials["email"]}
    focus    jquery=button:contains("Save changes")
    The user clicks the button/link    jquery=button:contains("Save changes")

the user can edit resubmit and read only of the organisation
    the user should see the element             jQuery=li:contains("Your organisation") > .task-status-complete
    the user clicks the button/link             link=Your organisation
    the user clicks the button/link             jQuery=button:contains("Edit your organisation")
    the user enters text to a text field        jQuery=label:contains("employees") + input  2
    the user clicks the button/link             jQuery=button:contains("Mark as complete")
    the user should not see an error in the page
    the user should see the element             jQuery=li:contains("Your organisation") > .task-status-complete
    the user clicks the button/link             link=Your organisation
    the user should see the element             jQuery=dt:contains("employees") + dd:contains("2")

the lead applicant invites an existing user
    [Arguments]  ${comp_title}  ${EMAIL_INVITED}
    the user clicks the button/link    link=${comp_title}
    the user clicks the button/link    link=view team members and add collaborators
    the user clicks the button/link    link=Invite new contributors
    the user clicks the button/link    jQuery=button:contains('Add additional partner organisation')
    Input Text                         name=organisations[1].organisationName    innovate
    Input Text                         name=organisations[1].invites[0].personName    Partner name
    Input Text                         css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input  ${EMAIL_INVITED}
    the user clicks the button/link    jQuery=.button:contains("Save changes")
    the user logs out if they are logged in

the user navigates to the growth table finances
    the user navigates to the page  ${DASHBOARD_URL}
    the user clicks the button/link    jQuery=a:contains('Untitled application'):last
    the user clicks the button/link  link=Your finances

Invite a non-existing collaborator in Appplication with Growth table
    the user clicks the button/link      jQuery=a:contains("Application Overview")
    the user clicks the button/link       jQuery=a:contains("view team members and add collaborators")
    the user clicks the button/link       jQuery=a:contains("Add partner organisation")
    the user should see the element       jQuery=h1:contains(Add organisation)
    the user enters text to a text field      id=organisationName    innovate
    the user enters text to a text field       id=applicants0.name    liam
    the user enters text to a text field       id=applicants0.email    liam@innovate.com
    the user clicks the button/link        jQuery=button:contains("Add organisation and invite applicants")
    the user should not see an error in the page
    the user logs out if they are logged in
    newly invited collaborator can create account and sign in

Newly invited collaborator can create account and sign in
    the user reads his email and clicks the link     liam@innovate.com  Invitation to collaborate in ${compWITHGrowth}  You will be joining as part of the organisation    3
    the user clicks the button/link      jQuery=a:contains("Yes, accept invitation")
    the user should see the element      jquery=h1:contains("Choose your organisation type")
    the user completes the new account creation

the user completes the new account creation
    the user selects the radio button    organisationType  radio-1
     #TODO change the radio button option to radio-4 once INFUND-8896 is fixed
     #TODO radio button option can't be changed yet due to INFUND-9078
    the user clicks the button/link     jQuery=button:contains("Continue")
    the user should see the element     jQuery=span:contains("Create your account")
    the user enters text to a text field     id=organisationSearchName   innovate
    the user clicks the button/link        jQuery=button:contains("Search")
    wait for autosave
    the user clicks the button/link        jQuery=a:contains("INNOVATE LTD")
    the user selects the checkbox     address-same
    wait for autosave
    the user clicks the button/link     jQuery=button:contains("Save organisation and continue")
    then the user should not see an error in the page
    the user clicks the button/link     jQuery=a:contains("Confirm")
    the user should be redirected to the correct page    ${SERVER}/registration/register
    the user enters text to a text field     jQuery=input[id="firstName"]   liam
    the user enters text to a text field     JQuery=input[id="lastName"]   smithson
    the user enters text to a text field     jQuery=input[id="phoneNumber"]   077712567890
    the user enters text to a text field     jQuery=input[id="password"]  ${correct_password}
    the user enters text to a text field    jQuery=input[id="retypedPassword"]  ${correct_password}
    the user selects the checkbox      termsAndConditions
    the user clicks the button/link     jQuery=button:contains("Create account")
    the user should see the text in the page    Please verify your email address
    the user reads his email and clicks the link      liam@innovate.com   Please verify your email address    Once verified you can sign into your account.
    the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    the user clicks the button/link     link=Sign in
    then the user should see the text in the page      Sign in
    the user enters text to a text field      jQuery=input[id="username"]   liam@innovate.com
    the user enters text to a text field      jQuery=input[id="password"]  ${correct_password}
    the user clicks the button/link         jQuery=button:contains("Sign in")
