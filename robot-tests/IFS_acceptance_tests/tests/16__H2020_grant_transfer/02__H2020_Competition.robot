*** Settings ***
Documentation  IFS-5158 - Competition Template
Suite Setup       Custom setup
Suite Teardown    Custom teardown
Resource          ../../resources/defaultResources.robot
Resource          ../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${CA_UpcomingComp}   ${server}/management/dashboard/upcoming
${competitionTitle}  H2020 Grant Transfer

*** Test Cases ***
User can select H2020 Competition Template and complete Initial details
    [Documentation]  IFS-5158
    Given a user starts a new competition
    When the user clicks the button/link                               link = Initial details
    Then the user selects the option from the drop-down menu           Horizon 2020   name = competitionTypeId
    And the user is able to complete Initial details section
    And the user should see the read-only view of the initial details
    [Teardown]  the user clicks the button/link                        link = Return to setup overview

User can populate the Completion Stage, Milestones and Public content
    [Documentation]  IFS-5158
     Given the user fills in the CS Milestones                                      release-feedback-completion-stage  1  ${nextyear}
     When the user clicks the button/link                                           link = Public content
     Then The user completes Public content for H2020 registration and publishes
     [Teardown]  the user clicks the button/link                                    link = Return to setup overview

User can populate Terms and Conditions
    [Documentation]  IFS-5158
    Given the user clicks the button/link                   link = Terms and conditions
    Then the user clicks the button/link                    jQuery = button:contains("Done")
    [Teardown]  the user clicks the button/link             link = Return to setup overview

User can populate Funding information and Eligibility
    [Documentation]  IFS-5158
    Given the user clicks the button/link          link = Funding information
    When the user completes funding information
    Then the user clicks the button/link           link = Return to setup overview
    And the user fills in the CS Eligibility       ${BUSINESS_TYPE_ID}   3  false  single-or-collaborative

User can complete the Application
    [Documentation]  IFS-5158
    Given the user clicks the button/link                      link = Application
    When the user completes the application proccess details
    Then the user clicks the button/link                       link = Return to setup overview

User can finish setting up the grant transfer
    [Documentation]  IFS-5158
    Given the user completes grant transfer setup
    Then the user should see the element             jQuery = h2:contains("Ready to open") ~ ul a:contains("${competitionTitle}")
    [Teardown]  Get competition id and set open date to yesterday  ${competitionTitle}

Applicant user start a grant transfer
    [Documentation]  IFS-5158
    [Setup]  log in as a different user    &{collaborator1_credentials}
    Given the user navigates to the page   ${server}/competition/${competitionId}/overview
    When the user clicks the button/link   jQuery = a:contains("Start new application")
    Then the user is able to go to Application overview

*** Keywords ***
Custom setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    Connect to database  @{database}

A user starts a new competition
    the user navigates to the page        ${CA_UpcomingComp}
    the user clicks the button/link       jQuery = .govuk-button:contains("Create competition")

The user is able to complete Initial details section
    the user enters text to a text field                            css = #title  ${competitionTitle}
    the user selects the radio button                               fundingType  GRANT
    the user selects the option from the drop-down menu             None  id = innovationSectorCategoryId
    the user selects the value from the drop-down menu              67  name = innovationAreaCategoryIds[0]
    the user enters text to a text field                            id = openingDateDay    10
    the user enters text to a text field                            id = openingDateMonth    1
    the user enters text to a text field                            id = openingDateYear     ${nextyear}
    the user selects the option from the drop-down menu             Ian Cooper    id = innovationLeadUserId
    the user selects the option from the drop-down menu             John Doe   id = executiveUserId
    the user clicks the button twice                                css = label[for = "stateAid2"]
    the user clicks the button/link                                 jQuery = button:contains("Done")
    the user should see the read-only view of the initial details

The user should see the read-only view of the initial details
    the user should see the element    jQuery = dd:contains("H2020 Grant Transfer")
    the user should see the element    jQuery = dt:contains("Funding type") ~ dd:contains("Grant")
    the user should see the element    jQuery = dd:contains("None")
    the user should see the element    jQuery = dd:contains("None")
    the user should see the element    jQuery = dd:contains("10 January ${nextyear}")
    the user should see the element    jQuery = dd:contains("Ian Cooper")
    the user should see the element    jQuery = dd:contains("John Doe")
    the user should see the element    jQuery = dt:contains("State aid") ~ dd:contains("No")

The user completes funding information
     the user enters text to a text field    id = funders[0].funder    FunderName
     the user enters text to a text field    id = funders[0].funderBudget    20000
     the user enters text to a text field    id = pafNumber    2016
     the user enters text to a text field    id = budgetCode    2004
     the user enters text to a text field    id = activityCode    4242
     the user clicks the button/link         id = generate-code
     the user clicks the button/link         jQuery = button:contains("Done")

The user completes Public content for H2020 registration and publishes
    the user fills in the public content competition inforation and search
    the user fills in the public content summary
    the user fills in public content eligibility
    the user fills in public content scope
    the user fills in public content save the dates
    the user fills in public content how to apply section
    the user fills in public content supporting information section
    # Publish and return
    the user clicks the button/link         jQuery = button:contains("Publish content")

The user fills in the public content competition inforation and search
    the user clicks the button/link         link = Competition information and search
    the user enters text to a text field    id = shortDescription  Horizon 2020 competition
    the user enters text to a text field    id = projectFundingRange  Up to £5million
    the user enters text to a text field    css = [aria-labelledby = "eligibilitySummary"]  Summary of eligiblity
    the user selects the radio button       publishSetting  invite
    the user enters text to a text field    id = keywords  Search, Testing, Robot
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Competition information and search") ~ .task-status-complete

The user fills in the public content summary
    the user clicks the button/link         link = Summary
    the user enters text to a text field    css = .editor  This is a Summary description
    the user enters text to a text field    id = projectSize   10 millions
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Summary") ~ .task-status-complete

The user fills in public content eligibility
    the user clicks the button/link         link = Eligibility
    the user enters text to a text field    id = contentGroups[0].heading  Heading 1
    the user enters text to a text field    jQuery = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Eligibility") ~ .task-status-complete

The user fills in public content scope
    the user clicks the button/link         link = Scope
    the user enters text to a text field    id = contentGroups[0].heading  Heading 1
    the user enters text to a text field    jQuery = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Scope") ~ .task-status-complete

The user fills in public content save the dates
    the user clicks the button/link         link = Dates
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Dates") ~ .task-status-complete

The user fills in public content how to apply section
    the user clicks the button/link         link = How to apply
    the user enters text to a text field    id = contentGroups[0].heading    Heading 1
    the user enters text to a text field    css = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("How to apply") ~ .task-status-complete

The user fills in public content supporting information section
    the user clicks the button/link         link = Supporting information
    the user enters text to a text field    id = contentGroups[0].heading    Heading 1
    the user enters text to a text field    css = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Supporting information") ~ .task-status-complete

The user completes the application proccess details
    the user clicks the button/link         link = Application details
    the user clicks the button/link         jQuery = button:contains("Done")
    the user clicks the button/link         link = Public description
    the user clicks the button/link         jQuery = button:contains("Done")
    the user clicks the button/link         link = Finances
    the user selects the radio button       applicationFinanceType  STANDARD
    the user clicks the button twice        css = label[for = "include-growth-table-no"]
    the user selects the radio button       includeJesForm  false
    the user selects the radio button       includeYourOrganisationSection  false
    the user enters text to a text field    css = .editor  Those are the rules that apply to Finances
    the user clicks the button/link         jQuery = .govuk-button:contains("Done")
    the user clicks the button/link         jQuery = .govuk-button:contains("Done")

The user completes grant transfer setup
    the user clicks the button/link             jQuery = a:contains("Complete")
    the user clicks the button/link             css = button[type="submit"]
    the user navigates to the page              ${CA_UpcomingComp}

The user is able to go to Application overview
     the user clicks the button/link  jQuery = .govuk-button:contains("Save and continue")
     the user should see the element  link = Project details
     the user should see the element  link = Application team
     the user should see the element  link = Public description
     the user should see the element  link = Project documents
     the user should see the element  link = Finances overview

Custom teardown
    the user closes the browser
    disconnect from database