*** Settings ***
Documentation     INFUND-6390 As an Applicant I will be invited to add project costs, organisation and funding details via links ƒin the 'Finances' section of my application
...
...               INFUND-6393 As an Applicant I will be invited to add Staff count and Turnover where the include projected growth table is set to 'No' within the Finances page of Competition setup
...
...               INFUND-6395 s an Applicant I will be invited to add Projected growth, and Organisation size where the include projected growth table is set to Yes within the Finances page of Competition setup
...
...               INFUND-6895 As an Lead Applicant I will be advised that changing my Research category after completing Funding level will reset the 'Funding level'
...
...               INFUND-9151 Update 'Application details' where a single 'Innovation area' set in 'Initial details'
...
...               IFS-40 As a comp executive I am able to select an 'Innovation area' of 'All' where the 'Innovation sector' is 'Open'
...
...               IFS-1015 As a Lead applicant with an existing account I am informed if my Organisation type is NOT eligible to lead
...
...               IFS-3938 As an applicant the requirement prerequesites for Your funding are clear
Suite Setup       Set predefined date variables
Suite Teardown    Close browser and delete emails
Force Tags        Applicant  CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot
Resource          ../../10__Project_setup/PS_Common.robot
Resource          ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${compWithoutGrowth}         FromCompToNewAppl without GrowthTable
${applicationWithoutGrowth}  NewApplFromNewComp without GrowthTable
${compWithGrowth}            All-Innov-Areas With GrowthTable    #of Sector Competition type
${applicationWithGrowth}     All-Innov-Areas Application With GrowthTable
${newUsersEmail}             liam@innovate.com
${ineligibleMessage}         Your organisation type does not match our eligibility criteria for lead applicants.

*** Test Cases ***
Comp Admin starts a new Competition
    [Documentation]    INFUND-6393
    [Tags]  HappyPath
    [Setup]  the user logs-in in new browser       &{Comp_admin1_credentials}
    # For the testing of the story INFUND-6393, we need to create New Competition in order to apply the new Comp Setup fields
    # Then continue with the applying to this Competition, in order to see the new Fields applied
    Given the user navigates to the page           ${CA_UpcomingComp}
    When the user clicks the button/link           jQuery = .govuk-button:contains("Create competition")
    Then the user fills in the CS Initial details  ${compWithoutGrowth}  ${month}  ${nextyear}  ${compType_Programme}  2  GRANT
    And the user selects the Terms and Conditions
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility       ${BUSINESS_TYPE_ID}  1  true  collaborative     # 1 means 30%
    And the user fills in the CS Milestones        project-setup-completion-stage   ${month}   ${nextyear}
    And the user fills in the CS Documents in other projects

Comp Admin fills in the Milestone Dates and can see them formatted afterwards
    [Documentation]    INFUND-7820
    [Tags]
    Given the user should see the element    jQuery = div:contains("Milestones") ~ .task-status-complete
    When the user clicks the button/link     link = Milestones
    And the user clicks the button/link      jQuery = a:contains("Next")
    Then the user should see the element     jQuery = button:contains("Edit")
    And the user should see the dates in full format
    Then the user clicks the button/link     link = Competition setup

Comp admin completes ths competition setup
    [Documentation]    INFUND-6393
    [Tags]  HappyPath
    Given the user should see the element        jQuery = h1:contains("Competition setup")
    Then the user marks the Application as done  no  Programme
    And the user fills in the CS Assessors
    When the user clicks the button/link         link = Public content
    Then the user fills in the Public content and publishes  NoGrowthTable
    And the user clicks the button/link          link = Return to setup overview
    And the user should see the element          jQuery = div:contains("Public content") ~ .task-status-complete
    When the user clicks the button/link         jQuery = a:contains("Complete")
    Then the user clicks the button/link         css = button[type="submit"]
    And the user navigates to the page           ${CA_UpcomingComp}
    Then the user should see the element         jQuery = h2:contains("Ready to open") ~ ul a:contains("${compWithoutGrowth}")

Competition is Open to Applications
    [Documentation]    INFUND-6393
    [Tags]  MySQL  HappyPath
    Change the open date of the Competition in the database to one day before  ${compWithoutGrowth}

Create new Application for this Competition
    [Tags]  HappyPath
    Given Log in as a different user              &{lead_applicant_credentials}
    Then logged in user applies to competition    ${compWithoutGrowth}  1

Applicant visits his Finances
    [Documentation]    INFUND-6393  IFS-3938
    [Tags]  HappyPath
    Given the user should see the element          jQuery = h1:contains("Application overview")
    When the user clicks the button/link           link = Your finances
    Then the user should see the element           jQuery = li:contains("Your project costs") > .task-status-incomplete
    And the user should see the element            jQuery = li:contains("Your organisation") > .task-status-incomplete
    And the user should see that the funding depends on the research area
    And the user should see his finances empty
    [Teardown]  the user clicks the button/link    jQuery = a:contains("Return to application overview")

Applicant fills in the Application Details
    [Documentation]  INFUND-6895  INFUND-9151
    [Tags]  HappyPath
    When the user clicks the button/link             link = Application details
    Then The user fills in the Application details   ${applicationWithoutGrowth}  ${tomorrowday}  ${month}  ${nextyear}
    And the user selects Research category           Feasibility studies

Application details read only view shows correct details without innovation area
    [Documentation]  IFS-4722
    [Tags]
    Given The user clicks the button/link    link = Application details
    Then the user should see the element     jQuery = dt:contains("Application name") + dd:contains("NewApplFromNewComp without GrowthTable")
    And The user should not see the element  jQuery = dt:contains("Innovation area")
    [Teardown]  the user clicks the button/link  link = Application overview

Turnover and Staff count fields
    [Documentation]    INFUND-6393
    [Tags]  HappyPath
    Given the user clicks the button/link         link = Your finances
    Then the user clicks the button/link          link = Your organisation
    And the user should see the element           jQuery = div label:contains("Turnover (£)")
    And the user should see the element           jQuery = div label:contains("Full time employees")
    And the user should see the element           jQuery = div span:contains("Number of full time employees at your organisation.")

Once the project growth table is selected
    [Documentation]    INFUND-6393 IFS-40
    [Tags]  HappyPath
    [Setup]    log in as a different user                &{Comp_admin1_credentials}
    Given the user navigates to the page                 ${CA_UpcomingComp}
    When the user clicks the button/link                 jQuery = .govuk-button:contains("Create competition")
    # For the testing of story IFS-40, turning this competition into Sector with All innovation areas
    Then the user fills in the Open-All Initial details  ${compWithGrowth}  ${month}  ${nextyear}
    And the user selects the Terms and Conditions
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility             ${BUSINESS_TYPE_ID}  1  true  collaborative     # 1 means 30%
    And the user fills in the CS Milestones              project-setup-completion-stage   ${month}   ${nextyear}
    Then the user marks the Application as done          yes  Sector
    And the user fills in the CS Assessors
    And the user fills in the CS Documents in other projects
    When the user clicks the button/link                 link = Public content
    Then the user fills in the Public content and publishes  GrowthTable
    And the user clicks the button/link                  link = Return to setup overview
    And the user should see the element                  jQuery = div:contains("Public content") ~ .task-status-complete
    When the user clicks the button/link                 jQuery = a:contains("Complete")
    Then the user clicks the button/link                 css = button[type="submit"]
    And the user navigates to the page                   ${CA_UpcomingComp}
    Then the user should see the element                 jQuery = h2:contains("Ready to open") ~ ul a:contains("${compWithGrowth}")
    [Teardown]  Change the open date of the Competition in the database to one day before  ${compWithGrowth}

As next step the Applicant cannot see the turnover field
    [Documentation]    INFUND-6393, INFUND-6395
    [Tags]  HappyPath
    Given Log in as a different user                             &{lead_applicant_credentials}
    And logged in user applies to competition                    ${compWithGrowth}  1
    When the user clicks the button/link                         link = Your finances
    And the user clicks the button/link                          link = Your organisation
    Then the user should not see the element                     jQuery = div label:contains("Turnover (£)")
    And the user should see the element                          jQuery = div label:contains("Full time employees")
    And the user should see the element                          jQuery = span:contains("How many full-time employees did you have on the project at the close of your last financial year?")

Organisation server side validation when no
    [Documentation]    INFUND-6393
    [Tags]
    [Setup]    log in as a different user                 &{lead_applicant_credentials}
    Given the user navigates to Your-finances page        ${applicationWithoutGrowth}
    Then the user clicks the button/link                  link = Your organisation
    And the user selects the checkbox                     stateAidAgreed
    When the user clicks the button/link                  jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error    Enter your organisation size.
    And the user should see a field and summary error     ${empty_field_warning_message}
    And the user should see a field and summary error     ${empty_field_warning_message}
    And the user should not see the element               jQuery = h1:contains("Your finances")
    # Checking that by marking as complete, the user doesn't get redirected to the main finances page

Organisation client side validation when no
    [Documentation]    INFUND-6393
    [Tags]
    Given the user selects medium organisation size
    When the user enters text to a text field           css = #turnover  ${empty}
    And Set Focus To Element                            css = #headCount
    Then the user should see a field and summary error  ${empty_field_warning_message}
    And the user enters text to a text field            css = #headCount  ${empty}
    When Set Focus To Element                           jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error  ${empty_field_warning_message}
    When the user enters text to a text field           css = #turnover  150
    And the user enters text to a text field            css = #headCount  0
    And Set Focus To Element                            jQuery = button:contains("Mark as complete")
    Then the user should not see the element            css = .govuk-error-message

Mark Organisation as complete when no
    [Documentation]    INFUND-6393
    [Tags]  HappyPath
    [Setup]  the user navigates to Your-finances page        ${applicationWithoutGrowth}
    Given the user clicks the button/link                  link = Your organisation
    And the user enters text to a text field      css = #headCount    42
    And the user enters text to a text field      css = #turnover    17506
    And the user selects medium organisation size
    And the user selects the checkbox             stateAidAgreed
    When the user clicks the button/link          jQuery = button:contains("Mark as complete")
    Then the user should see the element          jQuery = li:contains("Your organisation") > .task-status-complete
    When the user clicks the button/link          link = Your organisation
    Then The user should not see the element      css = input
    And the user should see the element           jQuery = button:contains("Edit")
    And the user clicks the button/link           jQuery = a:contains("Return to finances")

The Lead applicant is able to edit and re-submit when no
    [Documentation]    INFUND-8518
    [Tags]
    The user can edit resubmit and read only of the organisation   headCount

Funding subsection opens when Appl details and organisation info are provided
    [Documentation]    INFUND-6895
    [Tags]
    Given the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link     link = ${applicationWithoutGrowth}
    When the user should see the element    jQuery = li:contains("Application details") > .task-status-complete
    And the user clicks the button/link     link = Your finances
    And the user should see the element     jQuery = li:contains("Your organisation") > .task-status-complete
    Then the user should see the element    jQuery = li:contains("Your funding") > .task-status-incomplete

Organisation server side validation when yes
    [Documentation]    INFUND-6393
    [Tags]
    [Setup]  the user navigates to the growth table finances
    Given the user clicks the button/link  link = Your organisation
    And the user selects the checkbox      stateAidAgreed
    When the user clicks the button/link   jQuery = button:contains("Mark as complete")
    And the user should see the element    jQuery = .govuk-error-summary__list li:contains("${empty_field_warning_message}")
    And the user should see the element    jQuery = .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element    jQuery = .govuk-error-summary__list li:contains("${enter_a_valid_date}")
    And the user should see the element    jQuery = .govuk-error-message:contains("${enter_a_valid_date}")
    And The user should see a field error  ${empty_field_warning_message}
    And The user should see a field error  ${enter_a_valid_date}
    #And The user should see a field error    Enter your organisation size
    #TODO Enable the above checks when IFS-535 is ready

Organisation client side validation when yes
    [Documentation]    INFUND-6395
    [Tags]
    When the user enters text to a text field                 css = #financialYearEndMonthValue    42
    Then the user should see a field and summary error        ${enter_a_valid_date}
    When the user enters text to a text field                 css = #financialYearEndMonthValue    12
    And the user enters text to a text field                  css = #financialYearEndYearValue    ${nextyear}
    Then the user should see a field and summary error        Please enter a past date.
    When the user enters text to a text field                 css = #financialYearEndYearValue    2016
    And the user enters text to a text field                  css = #annualTurnoverAtLastFinancialYear    ${EMPTY}
    Then the user should see a field and summary error        ${empty_field_warning_message}
    When the user enters text to a text field                 css = #annualTurnoverAtLastFinancialYear    8
    Then the user should not see the element                  css = #annualTurnoverAtLastFinancialYear-form-group .govuk-error-message
    And the user enters text to a text field                  css = #annualProfitsAtLastFinancialYear    -5
    When the user enters text to a text field                 css = #annualExportAtLastFinancialYear    ${empty}
    Then the user should see a field and summary error        ${empty_field_warning_message}
    And the user enters text to a text field                  css = #researchAndDevelopmentSpendAtLastFinancialYear    2147483647

Mark Organisation as complete when yes
    [Documentation]    INFUND-6393
    [Tags]  HappyPath
    [Setup]    the user navigates to the growth table finances
    Given the user clicks the button/link        link = Your organisation
    And the user selects medium organisation size
    Then the user enters text to a text field    css = #financialYearEndMonthValue    12
    And the user enters text to a text field     css = #financialYearEndYearValue    2016
    And the user populates the project growth table
    When the user enters text to a text field    css = #headCountAtLastFinancialYear  4
    And the user clicks the button/link          jQuery = button:contains("Save and return to finances")
    And the user clicks the button/link          link = Your organisation
    Then the user should see the element         jQuery = #researchAndDevelopmentSpendAtLastFinancialYear[value = "15000"]
    And the user selects the checkbox            stateAidAgreed
    When the user clicks the button/link         jQuery = button:contains("Mark as complete")
    Then the user should see the element         jQuery = li:contains("Your organisation") > .task-status-complete

The Lead Applicant is able to edit and re-submit when yes
    [Documentation]    INFUND-8518
    [Tags]
    Given the user can edit resubmit and read only of the organisation  headCountAtLastFinancialYear

Lead applicant can see all innovation areas
    [Documentation]  IFS-40
    [Tags]
    Given the user navigates to the page         ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link          jQuery = a:contains('Untitled application'):last
    And the user clicks the button/link          link = Application details
    #The fact that the link is present means that the innovation area is not pre-defined
    When the user clicks the button/link         css = #researchArea
    Then the user should see the element         jQuery = label[for^="innovationAreaChoice"]:contains("Biosciences")           # from sector Health and life sciences
    And the user should see the element          jQuery = label[for^="innovationAreaChoice"]:contains("Forming technologies")  # from sector Materials and manufacturing
    And the user should see the element          jQuery = label[for^="innovationAreaChoice"]:contains("Space technology")      # from sector Emerging and enabling
    And the user should see the element          jQuery = label[for^="innovationAreaChoice"]:contains("Offshore wind")         # from sector Infrastructure systems
    And the user should see the element          jQuery = label[for^="innovationAreaChoice"]:contains("Marine transport")      # from sector Transport
    When the user selects the radio button       innovationAreaChoice  19  # Bio
    And the user clicks the button/link          css = button[name="save-innovation-area"]
    Then the user should see the element         jQuery = label[for="researchArea"] + + *:contains("Biosciences")
    [Teardown]  the user clicks the button/link  jQuery = button:contains("Save and return to application overview")

Applicant can view and edit project growth table
    [Documentation]    INFUND-6395
    [Tags]
    Given the user navigates to the growth table finances
    When the user clicks the button/link                link = Your organisation
    Then the user should view the project growth table
    And the user can edit the project growth table
    And the user populates the project growth table
    And the user selects the checkbox                   stateAidAgreed
    And the user clicks the button/link                 jQuery = button:contains("Mark as complete")

The Lead Applicant fills in the Application Details for App with Growth
    [Documentation]  This step is required for following test cases
    [Tags]
    Given the user clicks the button/link           link = Application overview
    When the user clicks the button/link            link = Application details
    Then the user fills in the Application details  ${applicationWithGrowth}  ${tomorrowday}  ${month}  ${nextyear}

Application details read only view shows correct details with innovation area
    [Documentation]  IFS-4722
    [Tags]
    Given The user clicks the button/link    link = Application details
    Then the user should see the element     jQuery = dt:contains("Application name") + dd:contains("All-Innov-Areas Application With GrowthTable")
    And The user should see the element  jQuery = dt:contains("Innovation area") + dd:contains("Biosciences")
    [Teardown]  the user clicks the button/link  link = Application overview

Newly created collaborator can view and edit project Growth table
    [Documentation]    INFUND-8426
    [Tags]
    [Setup]    Invite a non-existing collaborator in Application with Growth table
    Given the user navigates to Your-finances page  ${applicationWithGrowth}
    And the user clicks the button/link             link = Your organisation
    And the user selects medium organisation size
    Then the user enters text to a text field       css = #financialYearEndMonthValue    12
    And the user enters text to a text field        css = #financialYearEndYearValue    2016
    And the user populates the project growth table
    And the user selects the checkbox                stateAidAgreed
    And the user clicks the button/link             jQuery = button:contains("Mark as complete")
    And the user should not see an error in the page

Invite Collaborator in Application with Growth table
    [Documentation]    INFUND-8518 INFUND-8561
    [Tags]
    Given the lead applicant invites an existing user  ${compWithGrowth}  ${collaborator1_credentials["email"]}
    When log in as a different user                    &{collaborator1_credentials}
    Then the user reads his email and clicks the link  ${collaborator1_credentials["email"]}  Invitation to collaborate in ${compWithGrowth}  You will be joining as part of the organisation  2
    When the user should see the element               jQuery = h2:contains("We have found an account with the invited email address")
    Then the user clicks the button/link               link = Continue
    And the user clicks the button/link                css = .govuk-button[type="submit"]    #Save and continue

Non-lead can mark Organisation as complete
    [Documentation]    INFUND-8518 INFUND-8561
    [Tags]
    Given the user navigates to Your-finances page  ${applicationWithGrowth}
    And the user clicks the button/link             link = Your organisation
    Then the user selects medium organisation size
    And the user enters text to a text field        css = #financialYearEndMonthValue    12
    And the user enters text to a text field        css = #financialYearEndYearValue    2016
    Then the user populates the project growth table
    And the user enters text to a text field        css = #headCountAtLastFinancialYear    42
    And the user selects the checkbox               stateAidAgreed
    When the user clicks the button/link            jQuery = button:contains("Mark as complete")
    Then the user should see the element            jQuery = li:contains("Your organisation") > .task-status-complete

Non-lead can can edit and remark Organisation as Complete
    [Documentation]    INFUND-8518 INFUND-8561
    [Tags]
    Given the user can edit resubmit and read only of the organisation    headCountAtLastFinancialYear

RTOs are not allowed to apply on Competition where only Businesses are allowed to lead
    [Documentation]  IFS-1015
    [Tags]  HappyPath
    Given the logged in user should not be able to apply in a competition he has not right to  antonio.jenkins@jabbertype.example.com  ${compWithoutGrowth}  3
    When the user should see the element           jQuery = h1:contains("You are not eligible to start an application")
    Then the user should see the element           jQuery = p:contains("${ineligibleMessage}")

Business organisation is not allowed to apply on Comp where only RTOs are allowed to lead
    [Documentation]  IFS-1015
    [Tags]  HappyPath
    Given the logged in user should not be able to apply in a competition he has not right to  theo.simpson@katz.example.com  ${openCompetitionRTO_name}  1
    When the user should see the element           jQuery = h1:contains("You are not eligible to start an application")
    Then the user should see the element           jQuery = p:contains("${ineligibleMessage}")

*** Keywords ***

the user should see the dates in full format
    ${today} =    Get time
    ${tomorrowMonthWord} =    Add time To Date    ${today}    1 day    result_format=%B    exclude_millis=true
    the user should see the element   jQuery = td:contains("Allocate assessors") ~ td:contains("4 ${tomorrowMonthWord} ${nextyear}")

the user should see that the funding depends on the research area
    the user clicks the button/link  link = Your funding
    the user should see the element  jQuery = li:contains("mark the") a:contains("research category")
    the user clicks the button/link  link = Your finances

the user should see his finances empty
    the user should see the element  jQuery = thead:contains("Total project costs") ~ *:contains("£0")

the user enters value to field
    [Arguments]  ${field}  ${value}
    the user enters text to a text field  jQuery = td:contains("${field}") + td input  ${value}

the user should see an error message in the field
    [Arguments]  ${field}  ${errmsg}
    the user should see the element  jQuery = span:contains("${field}") + *:contains("${errmsg}")

the user populates the project growth table
    the user enters value to field    Annual turnover    65000
    the user enters value to field    Annual profit    2000
    the user enters value to field    Annual export    3000
    the user enters value to field    Research and development spend    15000

the user should view the project growth table
    the user should see the text in the element    css = table.govuk-table tr:nth-of-type(1) th:nth-of-type(1)    Section
    the user should see the text in the element    css = table.govuk-table tr:nth-of-type(1) th:nth-of-type(2)    Last financial year (£)
    the user should see the text in the element    css = tr:nth-child(1) td:nth-child(1) span    Annual turnover
    the user should see the element                css = td input[value="65000"]
    the user should see the text in the element    css = tr:nth-child(2) td:nth-child(1) span    Annual profits
    the user should see the element                css = td input[value="2000"]
    the user should see the text in the element    css = tr:nth-child(3) td:nth-child(1) span    Annual export
    the user should see the element                css = td input[value="3000"]
    the user should see the text in the element    css = tr:nth-child(4) td:nth-child(1) span    Research and development spend
    the user should see the element                css = td input[value="15000"]

the user can edit the project growth table
    the user clicks the button/link         jQuery = button.button-clear:contains('Edit')
    the user selects the radio button       organisationSize    ${SMALL_ORGANISATION_SIZE}
    the user enters text to a text field    css = tr:nth-child(1) .govuk-input    4000
    the user enters text to a text field    css = td input[value="65000"]    5000

the applicant enters valid inputs
    The user clicks the button/link         jquery = li:nth-last-child(1) button:contains('Add additional partner organisation')
    The user enters text to a text field    name = organisations[1].organisationName  ${organisationLudlowName}
    The user enters text to a text field    name = organisations[1].invites[0].personName    Jessica Doe
    The user enters text to a text field    name = organisations[1].invites[0].email    ${collaborator1_credentials["email"]}
    Set Focus To Element                                     jquery = button:contains("Save changes")
    The user clicks the button/link         jquery = button:contains("Save changes")

the user can edit resubmit and read only of the organisation
    [Arguments]    ${headcount_field_id}
    the user should see the element         jQuery = li:contains("Your organisation") > .task-status-complete
    the user clicks the button/link         link = Your organisation
    the user clicks the button/link         jQuery = button:contains("Edit")
    the user enters text to a text field    css = #${headcount_field_id}    2
    the user selects the checkbox           stateAidAgreed
    the user clicks the button/link         jQuery = button:contains("Mark as complete")
    the user should not see an error in the page
    the user should see the element         jQuery = li:contains("Your organisation") > .task-status-complete
    the user clicks the button/link         link = Your organisation
    the user should see the element         jQuery = dt:contains("employees") + dd:contains("2")

the lead applicant invites an existing user
    [Arguments]    ${comp_title}    ${EMAIL_INVITED}
    log in as a different user            &{lead_applicant_credentials}
    the user navigates to the page        ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link       jquery = .in-progress a:contains("${applicationWithGrowth}")
    the user fills in the inviting steps no edit  ${EMAIL_INVITED}

the user navigates to the growth table finances
    the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link  jQuery = .in-progress a:contains("Untitled application"):last
    the user clicks the button/link  link = Your finances

Invite a non-existing collaborator in Application with Growth table
    the user should see the element       jQuery = h1:contains("Application overview")
    the user fills in the inviting steps no edit  ${newUsersEmail}
    newly invited collaborator can create account and sign in  ${newUsersEmail}

Newly invited collaborator can create account and sign in
    [Arguments]  ${newUsersEmail}
    logout as user
    the user reads his email and clicks the link  ${newUsersEmail}  Invitation to collaborate in ${compWithGrowth}  You will be joining as part of the organisation  2
    the user clicks the button/link               jQuery = a:contains("Yes, accept invitation")
    the user should see the element               jquery = h1:contains("Choose organisation type")
    the user completes the new account creation   ${newUsersEmail}  ${PUBLIC_SECTOR_TYPE_ID}

the user fills in the Open-All Initial details
    [Arguments]  ${compTitle}  ${month}  ${nextyear}
    the user clicks the button/link                      link = Initial details
    the user enters text to a text field                 css = #title  ${compTitle}
    the user selects the radio button                    fundingType  GRANT
    the user selects the option from the drop-down menu  Sector  id = competitionTypeId
    the user selects the option from the drop-down menu  Open  id = innovationSectorCategoryId
    the user selects the value from the drop-down menu   -1  id = innovationAreaCategoryIds
    the user enters text to a text field                 css = #openingDateDay  1
    the user enters text to a text field                 css = #openingDateMonth  ${month}
    the user enters text to a text field                 css = #openingDateYear  ${nextyear}
    the user selects the value from the drop-down menu   24  id = innovationLeadUserId
    the user selects the value from the drop-down menu   21  id = executiveUserId
    the user clicks the button twice                     css = label[for="stateAid2"]
    the user clicks the button/link                      jQuery = button:contains("Done")
    the user clicks the button/link                      link = Competition setup
    the user should see the element                      jQuery = div:contains("Initial details") ~ .task-status-complete

the logged in user should not be able to apply in a competition he has not right to
    [Arguments]  ${email}  ${competition}  ${applicationType}
    log in as a different user          ${email}  ${short_password}
    the user clicks the button/link     link = Innovation Funding Service
    the user clicks the button/link in the paginated list  link = ${competition}
    the user clicks the button/link     link = Start new application
    the user clicks the button/link     link = Apply with a different organisation
    the user selects the radio button   organisationTypeId  ${applicationType}
    the user clicks the button/link     jQuery = button:contains("Save and continue")