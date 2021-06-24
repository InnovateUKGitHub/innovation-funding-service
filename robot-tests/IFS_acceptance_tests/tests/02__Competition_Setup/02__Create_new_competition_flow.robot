*** Settings ***
Documentation     INFUND-2945 As a Competition Executive I want to be able to create a new competition from the Competitions Dashboard so Innovate UK can create a new competition
...
...               INFUND-2982: Create a Competition: Step 1: Initial details
...
...               INFUND-2983: As a Competition Executive I want to be informed if the competition will fall under State Aid when I select a 'Competition type' in competition setup
...
...               INFUND-2986 Create a Competition: Step 3: Eligibility
...
...               INFUND-3182 As a Competition Executive I want to the ability to save progress on each tab in competition setup.
...
...               IFUND-3888 Rearrangement of Competitions setup
...
...               INFUND-3000 As a competitions team member I want to be able to configure application form questions during Competition Setup so that correct details are provided for each competition
...
...               INFUND-3002 As a Competition Executive and I have added all information in all obligatory fields I want to mark the competition ready for open
...
...               INFUND-2980 As a Competition Executive I want to see a newly created competition listed in the Competition Dashboard so that I can view and update further details
...
...               INFUND-2993 As a competitions team member I want to be able to add milestones when creating my competition so these can be used manage its progress
...
...               INFUND-4468 As a Competitions team member I want to include additional criteria in Competitions Setup so that the "Ready to Open" state cannot be set until these conditions are met
...
...               INFUND-4725 As a Competitions team member I want to be guided to complete all mandatory information in the Initial details section so that I can access the correct details in the other sections in Competition Setup.
...
...               INFUND-4892 As a Competitions team member I want to be prevented from making amendments to some Competition Setup details so that I do not affect affect other setup details that have been saved so far for this competition
...
...               INFUND-4894 As a competition executive I want have a remove button in order to remove the new added supporter rows in the funding information section
...
...               INFUND-5639 As a Competitions team member I want to be able to view the Application process within the application question section in Competition Setup so that I can set up my competition using more convenient navigation
...
...               INFUND-5640 As a Competitions team member I want to be able to edit the Finances questions in Competition Setup so that I can include the appropriate sections required for the competition
...
...               INFUND-5632 As a Competitions team member I want to be able to view application questions separately in Competition Setup so that I can more easily manage all sections required for each question in one place
...
...               INFUND-5634 As a Competitions team member I want to be able to view setup questions in the Scope section of Competition Setup so that I can review the questions and guidance to be shown to the applicants
...
...               INFUND-5636 As a Competitions team member I want to be able to view setup questions in the Project Summary section of Competition Setup so that I can review the questions and guidance to be shown to the applicants
...
...               INFUND-5637 As a Competitions team member I want to be able to edit setup questions in the Project Summary section of Competition Setup so that I can amend the defaults if required for the competition
...
...               INFUND-5635 As a Competitions team member I want to be able to edit questions in the Scope section of Competition Setup so that I can amend the defaults if required for the competition
...
...               INFUND-5641 As a Competitions team member I want to be able to update the assessor setup questions so that I can amend the defaults if required for the competition
...
...               INFUND-5633 As a Competitions team member I want to be able to set up questions in the Application Details section of Competition Setup so that I can amend the defaults if necessary for the competitions
...
...               INFUND-6478 As a Competitions executive I will be able to view all innovation areas selected when viewing Initial details of my competition in read only mode and the Competition type is Sector competition
...               INFUND-6479 As a Competitions executive I will be able to edit (add or remove) multiple innovation areas when editing the Initial details of my application and the Competition type is Sector competition
...
...               INFUND-6773 As a Competitions team member I want to see Finances form defaulted to Full application finances
...
...               INFUND-6922 Update 'Competition setup' menu page to include a link to new 'Public content' page
...
...               INFUND-9225 Update 'Eligibility' > 'Lead applicant' to enable single or multi-selection
...
...               INFUND-9152 Add an 'Innovation sector' of 'Open' where 'Competition type' is 'Sector'
...
...               IFS-192 Select additional Innovation Lead stakeholders in Competition Setup
...
...               IFS-1104 Add Stakeholder link to Competition Setup
...
...               IFS-2192 As a Portfolio manager I am able to create an EOI competition
...
...               IFS-2285 APC Competition template: BEIS Value for Money: Pro-forma Spreadsheet
...
...               IFS-2776 As an Portfolio manager I am able to set the min/max project duration for a competition
...
...               IFS-3086 Investigate options to support selection of grant terms and conditions in Competition setup
...
...               IFS-2833 As a Portfolio manager I am able to edit the 'Question heading' in Project details
...
...               IFS-1084 As a comp exec I am able to delete a competition prior to the competition opens date
...
...               IFS-3916 Configurable Project Setup documents: Configuration
...
...               IFS-2941 As an applicant I am only offered the Research category eligible for the competition
...
...               IFS-4190 Create new user in stakeholder role
...
...               IFS-3287 As a Portfolio Manager I am able to switch off requirement for Research category
...
...               IFS-4253 New Stakeholder invite and create account email
...
...               IFS-4345 As a Portfolio Manager I am able to select the Standard with VAT form for certain competitions
...
...               IFS-4186 Competition Setup - change layout to separate items not required for open
...
...               IFS-4982 Move Funding type selection from front door to Initial details
...
...               IFS-7310 Internal user can allow multiple appendices in comp creation
...
...               IFS-7702  Configurable multiple choice questions - Comp setup
...
...               IFS-7703 Applicant can answer multiple choice questions
...
...               IFS-7700 EDI application question configuration
...
...               IFS-8522 Loans - Change of EDI survey link
...
...               IFS-8496 Unable to delete competitions in the upcoming tab
...
...               IFS-8779 Subsidy Control - Create a New Competition - Initial Details
...
...               IFS-6775 Initial details type ahead
...
...               IFS-8791 Subsidy Control - Create a New Competition - Funding Eligibility and Funding Levels
...
...               IFS-9214 Add dual T&Cs to Subsidy Control Competitions
...
...               IFS-9482 Loans: Comp setup - new question in Project details section
...
...               IFS-8847 Always open competitions: new comp setup configuration
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/Assessor_Commons.robot

*** Variables ***
${peter_freeman}            Peter Freeman
${competitionTitle}         Competition title   #Test competition
${amendedQuestion}          Need or challenge
${customQuestion}           How innovative is your project?

#application questions to be completed
@{applicationQuestions}     Public description  Team and resources  Market awareness  Outcomes and route to market  Wider impacts  Additionality  Costs and value for money

*** Test Cases ***
User can create a new competition
    [Documentation]    INFUND-2945, INFUND-2982, INFUND-2983, INFUND-2986, INFUND-3888, INFUND-3002, INFUND-2980, INFUND-4725, IFS-1104,  IFS-8779
    [Tags]  HappyPath
    Given the user navigates to the page       ${CA_UpcomingComp}
    When the user clicks the button/link       jQuery = .govuk-button:contains("Create competition")
    And The user should see the element        css = #compCTA[disabled]
    And The user should not see the element    link = Funding information
    And The user should not see the element    link = Project eligibility
    And The user should not see the element    link = Milestones
    And The user should not see the element    link = Application
    And The user should not see the element    link = Assessors
    And The user should not see the element    link = Documents
    And The user should not see the element    link = Public content
    And The user should see the element        link = Initial details
    And The user should not see the element    link = Stakeholders
    And The user should see the element        jQuery = p:contains("When complete, this competition will open on the date set in Milestones.")

Initial details - User enters valid values and marks as done
    [Documentation]  INFUND-2982  INFUND-3888  INFUND-2983  INFUND-6478  INFUND-6479  IFS-4982  IFS-8779 IFS-6775
    [Tags]  HappyPath
    Given the user clicks the button/link                       link = Initial details
    And the user clicks the button/link                         jQuery = button:contains("+ add another innovation area")
    And the user enters valid data in the initial details
    When the user clicks the button/link                        jQuery = button:contains("Done")
    Then the user should see the read-only view of the initial details

Initial details - Competitions allow multiple innovation areas
    [Documentation]    INFUND-6478, INFUND-6479
    [Tags]
    Given the user clicks the button/link            jQuery = .govuk-button:contains("Edit")
    When the user enters multiple innovation areas
    And the user clicks the button/link              jQuery = button[class = "govuk-button"]
    Then The user should see the element             jQuery = dd:contains("Creative industries, Satellite applications, Space technology")

Initial Details - User can remove an innovation area
    [Documentation]    INFUND-6478, INFUND-6479
    [Tags]
    Given the user clicks the button/link       jQuery = .govuk-button:contains("Edit")
    And the user clicks the button/link         jQuery = #innovation-row-2 button:contains('Remove')
    When the user clicks the button/link        jQuery = button:contains("Done")
    Then the user should not see the element    jQuery = dd:contains("Space technology")

Initial Details - drop down menu is populated with comp admin users
    [Documentation]    INFUND-6905, IFS-6775
    [Tags]
    [Setup]    the user clicks the button/link     jQuery = .govuk-button:contains("Edit")
    When the user sees element in type ahead       executiveUserId  j  John Doe
    And the user sees element in type ahead        executiveUserId  r  Robert Johnson

Initial details - Comp Type, funding rule and Date should not be editable
    [Documentation]    INFUND-2985, INFUND-3182, INFUND-4892,  IFS-8779
    [Tags]
    And the user should not see the element   id = competitionTypeId
    And the user should not see the element   id = openingDateDay
    And the user should not see the element   id = fundingRule
    And the user clicks the button/link       jQuery = button:contains("Done")

Initial details - should have a green check
    [Documentation]    INFUND-3002
    [Tags]  HappyPath
    When The user clicks the button/link    link = Back to competition details
    Then the user should see the element    jQuery = li:contains("Initial details") .task-status-complete
    And the user should see the element     css = #compCTA[disabled]

User should have access to all the sections
    [Documentation]    INFUND-4725, IFS-1104  IFS-3086  IFS-4186
    Given The user should see the element    jQuery = h2:contains("Publish") ~ ul a:contains("Milestones")
    And The user should see the element      jQuery = h2:contains("Publish") ~ ul a:contains("Public content")
    And The user should see the element      jQuery = h2:contains("Competition setup") ~ ul a:contains("Terms and conditions")
    And The user should see the element      jQuery = h2:contains("Competition setup") ~ ul a:contains("Funding information")
    And The user should see the element      jQuery = h2:contains("Competition setup") ~ ul a:contains("Project eligibility")
    And The user should see the element      jQuery = h2:contains("Competition setup") ~ ul a:contains("Application")
    And the user should see the element      link = Documents
    And The user should see the element      jQuery = h2:contains("Assessment") ~ ul a:contains("Assessors")
    And The user should see the element      jQuery = h2:contains("Competition access") ~ ul a:contains("Innovation leads")

The user must select the Terms and Conditions they want Applicants to accept
    [Documentation]  IFS-3086  IFS-6205  IFS-9214
    [Tags]  HappyPath
    Given the user clicks the button/link     link = Terms and conditions
    When the user should see the element      link = Loans (opens in a new window)
    And the user clicks the button/link       jQuery = button:contains("Done")
    And the user clicks the button twice      jQuery = label:contains("Loans")
    And the user clicks the button/link       jQuery = button:contains("Done")
    And the user should see the element       jQuery = dt:contains("Subsidy control terms and conditions") ~ dd:contains("Loans")
    And the user should see the element       jQuery = dt:contains("State aid terms and conditions") ~ dd:contains("Loans")
    And the user clicks the button/link       link = Return to setup overview
    And the user should see the element       jQuery = li:contains("Terms and conditions") .task-status-complete

Internal user can navigate to Public Content without having any issues
    [Documentation]  INFUND-6922
    [Tags]  HappyPath
    Given the user clicks the button/link        link = Public content
    Then the user should not see an error in the page
    And the user should see the element          jQuery = h1:contains("Public content")
    And the user should see the element          jQuery = a:contains("Competition information and search")
    And the user should see the element          jQuery = a:contains("Summary")
    And the user should see the element          jQuery = a:contains("Eligibility")
    And the user should see the element          jQuery = a:contains("Scope")
    And the user should see the element          jQuery = a:contains("Dates")
    And the user should see the element          jQuery = a:contains("How to apply")
    And the user should see the element          jQuery = a:contains("Supporting information")
    [Teardown]  the user clicks the button/link  link = Return to setup overview

New application shows in Preparation section
    [Documentation]    INFUND-2980
    [Setup]  Get competitions id and set it as suite variable  ${competitionTitle}
    Given the user navigates to the page    ${CA_UpcomingComp}
    Then the user should see the element    jQuery = section:contains("In preparation") li:contains("${competitionTitle}")

Funding information: calculations
    [Documentation]  INFUND-2985 INFUND-4894
    [Tags]  HappyPath
    [Setup]  the user navigates to the page     ${SERVER}/management/competition/setup/${competitionId}
    Given the user clicks the button/link       link = Funding information
    And the user clicks the button/link         id = generate-code
    And the user check for competition code
    And the user enters text to a text field    id = funders[0].funderBudget    20000
    And the user enters text to a text field    id = pafNumber    2016
    And the user enters text to a text field    id = budgetCode    2004
    And the user enters text to a text field    id = activityCode    4242
    And the user enters text to an autocomplete field  id = funders[0].funder    Advanced Propulsion Centre (APC)
    And the user clicks the button/link         id = funders[0].funder
    And click element                           id = funders[0].funder__option--0
    When the user clicks the button/link        jQuery = Button:contains("+Add co-funder")
    And the user should see the element         jQuery = Button:contains("+Add co-funder")
    And the user should see the element         jQuery = Button:contains("Remove")
    And the user enters text to an autocomplete field   id = funders[1].funder   Aerospace Technology Institute (ATI)
    And the user clicks the button/link         id = funders[1].funder
    And click element                           id = funders[1].funder__option--0
    And the user enters text to a text field    id = 1-funderBudget    1000
    Then the total should be correct            Total: £21,000
    When the user clicks the button/link        jQuery = Button:contains("Remove")
    Then the total should be correct            Total: £20,000

Funding information: can be saved
    [Documentation]    INFUND-3182
    [Tags]  HappyPath
    Given the user clicks the button/link   jQuery = button:contains("Done")
    Then the user should see the element    jQuery = td:contains("Advanced Propulsion Centre (APC)")
    And the user should see the element     jQuery = th:contains("Total") ~ td:contains("£20,000")
    And the user should see the element     jQuery = dt:contains("PAF number") ~ dd:contains("2016")
    And the user should see the element     jQuery = dt:contains("Budget code") ~ dd:contains("2004")
    And the user should see the element     jQuery = dt:contains("Activity code") ~ dd:contains("4242")
    And the user should see the element     jQuery = dt:contains("Competition code") ~ dd:contains("${nextyearintwodigits}01-1")

Funding information: can be edited
    [Documentation]    INFUND-3002
    [Tags]
    Given the user clicks the button/link  jQuery = .govuk-button:contains("Edit")
    And the user edits autocomplete field  id = funders[0].funder    Centre for Connected and Autonomous Vehicles (CCAV)
    When the user clicks the button/link   jQuery = button:contains("Done")
    Then the user should see the element   jQUery = td:contains("Centre for Connected and Autonomous Vehicles (CCAV)")

Funding information: should have a green check
    [Documentation]    INFUND-3002
    [Tags]  HappyPath
    When The user clicks the button/link    link = Back to competition details
    Then the user should see the element    jQuery = li:contains("Funding information") .task-status-complete
    And the user should see the element     css = #compCTA[disabled]

Project eligibility: Contain the correct options
    [Documentation]  INFUND-2989 INFUND-2990 INFUND-9225  IFS-3287
    [Tags]  HappyPath
    Given the user clicks the button/link  link = Project eligibility
    And the user should see the element    jQuery = h2:contains("Please choose the project type.")
    Then the user should see the element   jQuery = label:contains("Single or Collaborative")
    When the user should see the element   jQuery = label:contains("Collaborative")
    And the user should see the element    jQuery = label:contains("Business")
    And the user should see the element    jQuery = label[for="lead-applicant-type-2"]:contains("Research")
    And the user should see the element    jQuery = label:contains("Research and technology organisation")
    And the user should see the element    jQuery = label:contains("Public sector")
    And the user should see the element    css = label[for="comp-resubmissions-yes"]
    And the user should see the element    css = label[for="comp-resubmissions-no"]
    And the resubmission should not have a default selection

Project eligibility: Mark as Done then Edit again
    [Documentation]    INFUND-3051 INFUND-3872 INFUND-3002 INFUND-9225  IFS-8044
    [Tags]  HappyPath
    Given the user selects the radio button    singleOrCollaborative    single
    And the user selects the checkbox          lead-applicant-type-1  # business
    And the user selects the checkbox          lead-applicant-type-3  # RTOs
    And the user selects the option from the drop-down menu    50%    name=researchParticipationAmountId
    And the user selects the radio button      resubmission    no
    When the user clicks the button/link       jQuery = button:contains("Done")
    Then the user should see the element       jQuery = dt:contains("Project type") ~ dd:contains("Single")
    And the user should see the element        jQuery = dt:contains("Research participation") ~ dd:contains("50%")
    And the user should see the element        jQuery = dt:contains("Are resubmissions allowed") ~ dd:contains("No")
    And The user should not see the element    id = streamName
    When the user clicks the button/link       link = Back to competition details
    When the user clicks the button/link       link = Project eligibility
    And the user clicks the button/link        jQuery = .govuk-button:contains("Edit")
    And the user clicks the button/link        jQuery = button:contains("Done")

Project eligibility: Should have a Green Check
    [Documentation]    INFUND-3002
    [Tags]  HappyPath
    When the user clicks the button/link    link = Back to competition details
    Then the user should see the element    jQuery = li:contains("Project eligibility") .task-status-complete
    And the user should see the element     css = #compCTA[disabled]

Funding eligibility: Mark as Done
    [Documentation]  IFS-8791
    Given the user clicks the button/link         link = Funding eligibility
    And the user should see the element           jQuery = h2:contains("Are research categories applicable?")
    And the user selects the radio button         researchCategoriesApplicable  true
    And the user should see the element           jQuery = label:contains("Feasibility studies")
    And the user should see the element           jQuery = label:contains("Industrial research")
    And the user should see the element           jQuery = label:contains("Experimental development")
    When the user selects the checkbox            research-categories-33  #Feasibility
    And the user selects the checkbox             research-categories-34  #Industrial
    And the user selects the checkbox             research-categories-34  #Experimental
    And the user clicks the button/link           jQuery = button:contains("Done")
    And the user should see the element           jQuery = p:contains("Set the maximum funding level percentage for the business sizes for each research category.")
    And the user should see the element           jQuery = p:contains("You can only use whole numbers from 0 to 100.")
    And the user should see the element           jQuery = td:contains("Micro or small")
    And the user should see the element           jQuery = td:contains("Medium")
    And the user should see the element           jQuery = td:contains("Large")
    And the user enters text to a text field      maximums[0][0].maximum  75
    And the user enters text to a text field      maximums[0][1].maximum  75
    And the user enters text to a text field      maximums[1][0].maximum  75
    And the user enters text to a text field      maximums[1][1].maximum  75
    And the user enters text to a text field      maximums[2][0].maximum  75
    And the user enters text to a text field      maximums[2][1].maximum  75
    And the user clicks the button/link           jQuery = button:contains("Done")
    Then The user clicks the button/link          link = Return to setup overview

Milestones: Page should contain the correct fields
    [Documentation]    INFUND-2993  IFS-8847
    [Tags]
    Given the user clicks the button/link         link = Milestones
    And the user should see the element           jQuery = h1:contains("Completion stage")
    And the user should see the element           jQuery = label:contains("Release feedback")
    And the user should see the element           jQuery = label:contains("Project setup")
    When the user selects the radio button        selectedCompletionStage  PROJECT_SETUP
    And the user clicks the button/link           jQuery = button:contains("Done")
    And the user clicks the button twice          jQuery = label:contains("No")
    And the user clicks the button/link           jQuery = button:contains("Save and continue")
    Then the pre-field date should be correct

Milestones: Correct Weekdays should show
    [Documentation]    INFUND-2993
    [Tags]  HappyPath
    [Setup]  the user navigates to the page    ${SERVER}/management/competition/setup/${competitionId}/section/milestones
    Given the user fills the milestones with valid data
    When the user clicks the button/link       jQuery = button:contains(Done)
    Then the weekdays should be correct

Milestones: Green check should show
    [Documentation]    INFUND-2993
    [Tags]  HappyPath
    When The user clicks the button/link    link = Back to competition details
    Then the user should see the element    jQuery = li:contains("Milestones") .task-status-complete
    And the user should see the element     css = #compCTA[disabled]

Application - Application process Page
    [Documentation]    INFUND-3000 INFUND-5639
    [Tags]
    #Writing the following selectors using jQuery in order to avoid hardcoded numbers.
    When the user clicks the button/link  link = Application
    Then the user should see all application elements

*** Keywords ***
the total should be correct
    [Arguments]    ${Total}
    mouse out    css=input
    Set Focus To Element    jQuery=button:contains("Done")
    Wait Until Element Contains Without Screenshots    css=.govuk-heading-s  ${Total}

the user should see all application elements
    the user should see the element  jQuery = h2:contains("Sector competition questions")
    the user should see the element  link = Application details
    the user should see the element  link = Project summary
    the user should see the element   link = Public description
    the user should see the element   link = Scope
    the user should see the element  jQuery = a:contains("${amendedQuestion}")
    the user should see the element  jQuery = a:contains("Approach and innovation")
    the user should see the element   jQuery = a:contains("Team and resources")
    the user should see the element   jQuery = a:contains("Market awareness")
    the user should see the element   jQuery = a:contains("Outcomes and route to market")
    the user should see the element   jQuery = a:contains("Wider impacts")
    the user should see the element   jQuery = a:contains("Project management")
    the user should see the element   jQuery = a:contains("Risks")
    the user should see the element   jQuery = a:contains("Additionality")
    the user should see the element   jQuery = a:contains("Costs and value for money")
    the user should see the element   jQuery = .button-clear:contains("Add question")
    the user should see the element   link = Finances

the user fills the milestones with valid data
    The user enters text to a text field    name = milestoneEntries[OPEN_DATE].day    10
    The user enters text to a text field    name = milestoneEntries[OPEN_DATE].month    1
    The user enters text to a text field    name = milestoneEntries[OPEN_DATE].year    2024
    The user enters text to a text field    name = milestoneEntries[BRIEFING_EVENT].day    11
    The user enters text to a text field    name = milestoneEntries[BRIEFING_EVENT].month    1
    The user enters text to a text field    name = milestoneEntries[BRIEFING_EVENT].year    2024
    The user enters text to a text field    name = milestoneEntries[SUBMISSION_DATE].day    12
    The user enters text to a text field    name = milestoneEntries[SUBMISSION_DATE].month    1
    The user enters text to a text field    name = milestoneEntries[SUBMISSION_DATE].year    2024
    The user enters text to a text field    name = milestoneEntries[ALLOCATE_ASSESSORS].day    13
    The user enters text to a text field    name = milestoneEntries[ALLOCATE_ASSESSORS].month    1
    The user enters text to a text field    name = milestoneEntries[ALLOCATE_ASSESSORS].year    2024
    The user enters text to a text field    name = milestoneEntries[ASSESSOR_BRIEFING].day    14
    The user enters text to a text field    name = milestoneEntries[ASSESSOR_BRIEFING].month    1
    The user enters text to a text field    name = milestoneEntries[ASSESSOR_BRIEFING].year    2024
    The user enters text to a text field    name = milestoneEntries[ASSESSOR_ACCEPTS].day    15
    The user enters text to a text field    name = milestoneEntries[ASSESSOR_ACCEPTS].month    1
    The user enters text to a text field    name = milestoneEntries[ASSESSOR_ACCEPTS].year    2024
    The user enters text to a text field    name = milestoneEntries[ASSESSOR_DEADLINE].day    16
    The user enters text to a text field    name = milestoneEntries[ASSESSOR_DEADLINE].month    1
    The user enters text to a text field    name = milestoneEntries[ASSESSOR_DEADLINE].year    2024
    The user enters text to a text field    name = milestoneEntries[LINE_DRAW].day    17
    The user enters text to a text field    name = milestoneEntries[LINE_DRAW].month    1
    The user enters text to a text field    name = milestoneEntries[LINE_DRAW].year    2024
    The user enters text to a text field    name = milestoneEntries[ASSESSMENT_PANEL].day    18
    The user enters text to a text field    name = milestoneEntries[ASSESSMENT_PANEL].month    1
    The user enters text to a text field    name = milestoneEntries[ASSESSMENT_PANEL].year    2024
    The user enters text to a text field    name = milestoneEntries[PANEL_DATE].day    19
    The user enters text to a text field    name = milestoneEntries[PANEL_DATE].month    1
    The user enters text to a text field    name = milestoneEntries[PANEL_DATE].year    2024
    The user enters text to a text field    name = milestoneEntries[FUNDERS_PANEL].day    20
    The user enters text to a text field    name = milestoneEntries[FUNDERS_PANEL].month    1
    The user enters text to a text field    name = milestoneEntries[FUNDERS_PANEL].year    2024
    The user enters text to a text field    name = milestoneEntries[NOTIFICATIONS].day    21
    The user enters text to a text field    name = milestoneEntries[NOTIFICATIONS].month    1
    The user enters text to a text field    name = milestoneEntries[NOTIFICATIONS].year    2024
    The user enters text to a text field    name = milestoneEntries[RELEASE_FEEDBACK].day    22
    The user enters text to a text field    name = milestoneEntries[RELEASE_FEEDBACK].month    1
    The user enters text to a text field    name = milestoneEntries[RELEASE_FEEDBACK].year    2024
    Set Focus To Element    jQuery = button:contains(Done)

the weekdays should be correct
    element should contain    css = tr:nth-child(1) td:nth-child(3)     Wed
    element should contain    css = tr:nth-child(2) td:nth-child(3)     Thu
    element should contain    css = tr:nth-child(3) td:nth-child(3)     Fri
    element should contain    css = tr:nth-child(4) td:nth-child(3)     Sat
    element should contain    css = tr:nth-child(5) td:nth-child(3)     Sun
    element should contain    css = tr:nth-child(6) td:nth-child(3)     Mon
    element should contain    css = tr:nth-child(7) td:nth-child(3)     Tue
    element should contain    css = tr:nth-child(8) td:nth-child(3)     Wed
    element should contain    css = tr:nth-child(9) td:nth-child(3)     Thu
    element should contain    css = tr:nth-child(10) td:nth-child(3)    Fri
    element should contain    css = tr:nth-child(11) td:nth-child(3)    Sat
    element should contain    css = tr:nth-child(12) td:nth-child(3)    Sun
    element should contain    css = tr:nth-child(13) td:nth-child(3)    Mon

the pre-field date should be correct
    Element Should Contain        id = milestoneWeekdayEntry-OPEN_DATE    Mon
    ${YEAR} =    Get Value        css = #milestoneWeekdayEntry-OPEN_DATE ~ .year .govuk-input--width-4  # Get the value within the YEAR field
    Should Be Equal As Strings    ${YEAR}  ${nextyear}
    ${MONTH} =    Get Value       css = #milestoneWeekdayEntry-OPEN_DATE ~ .month .govuk-input--width-4  # Get the value within the MONTH field
    Should Be Equal As Strings    ${MONTH}    1
    ${DAY} =    Get Value         css = #milestoneWeekdayEntry-OPEN_DATE ~ .day .govuk-input--width-4    #Get the value within the DAY field
    Should Be Equal As Strings    ${DAY}    10

the resubmission should not have a default selection
    the user should see the element  css=[name="resubmission"]:not(:checked) ~ label

The user enters valid data in the initial details
    the user enters text to a text field                    css = #title  ${competitionTitle}
    the user selects the radio button                       fundingType  LOAN
    the user selects the option from the drop-down menu     Sector  id = competitionTypeId
    the user selects the radio button                       fundingRule  SUBSIDY_CONTROL
    the user selects the option from the drop-down menu     Infrastructure systems  id = innovationSectorCategoryId
    the user selects the value from the drop-down menu      32   name = innovationAreaCategoryIds[0]
    the user selects the option from the drop-down menu     Open  id = innovationSectorCategoryId
    the user selects the value from the drop-down menu      19     name = innovationAreaCategoryIds[0]
    the user selects the option from the drop-down menu     Emerging and enabling  id = innovationSectorCategoryId
    the user selects the value from the drop-down menu      6  name = innovationAreaCategoryIds[0]
    the user selects the value from the drop-down menu      15  name = innovationAreaCategoryIds[1]
    the user enters text to a text field                    id = openingDateDay    10
    the user enters text to a text field                    id = openingDateMonth    1
    the user enters text to a text field                    id = openingDateYear     ${nextyear}
    the user selects option from type ahead                 innovationLeadUserId  i  Ian Cooper
    the user selects option from type ahead                 executiveUserId  j  John Doe

The competition should show in the correct section
    [Arguments]    ${SECTION}    ${COMP_NAME}
    Element should contain    ${SECTION}    ${COMP_NAME}

the user fills the scope assessment questions
    The user clicks the button/link         jQuery = button:contains("+Add guidance row")
    The user enters text to a text field    id = guidanceRows[2].subject    New subject
    The user enters text to a text field    id = guidanceRows[2].justification    This is a justification
    The user enters text to a text field    id = question.assessmentGuidance    Guidance for assessing scope section
    The user clicks the button/link         id = remove-guidance-row-1

the user checks the scope assessment questions
    The user should see the element                 jQuery = dt:contains("New subject") ~ dd:contains("This is a justification")
    The user should not see the element             jQuery = dt:contains("NO") ~ dd:contains("One or more of the above requirements have not been satisfied.")
    The user should see the element                 jQuery = dt:contains("Written feedback")
    The user should see the element                 jQuery = p:contains("Guidance for assessing scope section")
    The user should see the element                 jQuery = dt:contains("Scope 'Y/N' question")
    The user should see the element                 jQuery = dt:contains("Research category question")

the user should not be able to edit the scope feedback
    the user should not see the element    id = question.assessmentGuidanceTitle
    the user should not see the element    id = question.assessmentGuidance
    the user should not see the element    id = guidanceRows[0].subject
    the user should not see the element    id = guidanceRows[0].justification
    the user should not see the element    jQuery = Button:contains("+Add guidance row")

the user should not see the scope feedback
    the user should not see the element            jQuery = dt:contains("Guidance title") ~ dd:contains("Guidance for assessing scope")
    the user should not see the element            jQuery = p:contains("Your answer should be based upon the following:")
    the user should not see the element            jQuery = dt:contains("NO") ~ dd:contains("One or more of the above requirements have not been satisfied.")

the user should not be able to edit the assessed question feedback
    the user should not see the element    id = question.assessmentGuidanceTitle
    the user should not see the element    id = question.assessmentGuidance
    the user should not see the element    id = guidanceRows[0].scoreFrom
    the user should not see the element    id = guidanceRows[0].scoreTo
    the user should not see the element    id = guidanceRows[0].justification
    the user should not see the element    jQuery = Button:contains("+Add guidance row")
    the user should not see the element    id = question.scoreTotal

Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Set predefined date variables
    Connect to database  @{database}

the user enters multiple innovation areas
    the user clicks the button/link                        jQuery = .button-clear:contains("+ add another innovation area")
    the user selects the value from the drop-down menu     15    name=innovationAreaCategoryIds[1]
    the user clicks the button/link                        jQuery = .button-clear:contains("+ add another innovation area")
    the user should see the element                        jQuery = #innovation-row-2 option:disabled:contains("Space technology")
    the user selects the value from the drop-down menu     12    name=innovationAreaCategoryIds[2]

The user should not see the selected option again
    List Should not Contain Value    css = [id="innovationAreaCategoryIds[1]"]    Biosciences

the user marks every application question as complete
    :FOR   ${ELEMENT}   IN    @{applicationQuestions}
         \    the user marks question as complete  ${ELEMENT}
    the user marks the question as complete with other options     Approach and innovation  3
    the user marks the question as complete with other options     Project management  2
    the user marks the question as complete with other options     Risks  2

the user marks question as complete
    [Arguments]  ${question_link}
    the user should not see the element     jQuery = li:contains("${question_link}") .task-status-complete
    the user clicks the button/link         jQuery = a:contains("${question_link}")
    the user clicks the button/link         jQuery = button:contains('Done')
    the user should see the element         jQuery = li:contains("${question_link}") .task-status-complete

the user should see the read-only view of the initial details
    the user should see the element    jQuery = dd:contains("Competition title")
    the user should see the element    jQuery = dt:contains("Funding type") ~ dd:contains("Loan")
    the user should see the element    jQuery = dd:contains("Sector")
    the user should see the element    jQuery = dd:contains("Emerging and enabling")
    the user should see the element    jQuery = dd:contains("Satellite applications")
    the user should see the element    jQuery = dd:contains("Space technology")
    the user should see the element    jQuery = dd:contains("10 January ${nextyear}")
    the user should see the element    jQuery = dd:contains("Ian Cooper")
    the user should see the element    jQuery = dd:contains("John Doe")

the comp admin creates competition
    the user navigates to the page        ${CA_UpcomingComp}
    the user clicks the button/link       link = Create competition
    the user navigates to the page        ${CA_UpcomingComp}

the user fills new application details
    the user enters text to a text field             id = name  New application
    the user enters text to a text field             id = startDate  ${tomorrowday}
    the user enters text to a text field             css = #application_details-startdate_month  ${month}
    the user enters text to a text field             css = #application_details-startdate_year  ${nextyear}
    the user enters text to a text field             id = durationInMonths  45
    the user should not see the element              css = label[for="resubmission-no"]  #Set on line 307
    the user clicks the button/link                  id = innovationAreaName
    the user selects the radio button                innovationAreaChoice  NOT_APPLICABLE
    the user clicks the button/link                  jQuery = button:contains("Save")
    the user can mark the question as complete

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user check for competition code
    the user sees the text in the text field    name = competitionCode     ${nextyearintwodigits}

the comp admin creates competition with all sections details
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingRule}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}   ${isOpenComp}
    the user navigates to the page                          ${CA_UpcomingComp}
    the user clicks the button/link                         jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details                ${competition}  ${month}  ${nextyear}  ${compType}  ${fundingRule}  ${fundingType}
    Run Keyword If  '${fundingType}' == 'PROCUREMENT'  the user selects procurement Terms and Conditions
    ...  ELSE  the user selects the Terms and Conditions    ${compType}  ${fundingRule}
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility                                     ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}  # 1 means 30%
    the user fills in the CS funding eligibility                                     ${researchCategory}   ${compType}   ${fundingRule}
    the user selects the organisational eligibility to no                            false
    the user fills in the CS Milestones                                              ${completionStage}   ${month}   ${nextyear}   ${isOpenComp}
    Run Keyword If  '${fundingType}' == 'PROCUREMENT'  the user marks the procurement application as done      ${projectGrowth}  ${compType}
    ...  ELSE IF  '${fundingType}' == 'KTP'  the user marks the KTP application details as done     ${compType}
    ...  ELSE  the user marks the application as done                                ${projectGrowth}  ${compType}  ${competition}
    the user fills in the CS Assessors                                               ${fundingType}
    Run Keyword If  '${fundingType}' == 'PROCUREMENT'  the user select no documents
    ...  ELSE  the user fills in the CS Documents in other projects
    the user clicks the button/link                                                  link = Public content
    the user fills in the Public content and publishes                               ${extraKeyword}
    the user clicks the button/link                                                  link = Return to setup overview
    the user clicks the button/link                                                  link = Innovation leads
    the user clicks the button/link                                                  jQuery = td:contains("Peter Freeman") button:contains("Add")
    the user clicks the button/link                                                  link = Competition details
    the user clicks the button/link                                                  link = Stakeholders
    the user select stakeholder and add to competition
    the user clicks the button/link                                                  link = Competition setup
    the user clicks the button/link                                                  link = Documents
    the user clicks the button/link                                                  id = doneButton