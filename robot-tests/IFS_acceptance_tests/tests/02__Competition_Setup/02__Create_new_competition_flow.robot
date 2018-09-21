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
...               INFUND-4894 As a competition executive I want have a remove button in order to remove the new added co-funder rows in the funding information section
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
Suite Setup       Custom suite setup
Suite Teardown    The user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          CompAdmin_Commons.robot
Resource          ../04__Applicant/Applicant_Commons.robot

*** Variables ***
${peter_freeman}  Peter Freeman
${competitionTitle}  Test competition
${amendedQuestion}  Need or challenge
${customQuestion}  How innovative is your project?

*** Test Cases ***
User can create a new competition
    [Documentation]    INFUND-2945, INFUND-2982, INFUND-2983, INFUND-2986, INFUND-3888, INFUND-3002, INFUND-2980, INFUND-4725, IFS-1104
    [Tags]
    Given the user navigates to the page       ${CA_UpcomingComp}
    When the user clicks the button/link       jQuery = .govuk-button:contains("Create competition")
    And The user should see the element        css = #compCTA[disabled]
    And The user should not see the element    link = Funding information
    And The user should not see the element    link = Eligibility
    And The user should not see the element    link = Milestones
    And The user should not see the element    link = Application
    And The user should not see the element    link = Assessors
    # TODO IFS-4186 Uncomment when this functionality is enabled.
#    And The user should not see the element    link = Documents in project setup
    And The user should not see the element    link = Public content
    And The user should see the element        link = Initial details
    And The user should not see the element    link = Stakeholders
    And The user should see the element        jQuery = p:contains("Once you complete, this competition will be ready to open.")

Initial details - User enters valid values and marks as done
    [Documentation]    INFUND-2982, INFUND-3888, INFUND-2983, INFUND-6478, INFUND-6479
    [Tags]
    Given the user clicks the button/link                       link = Initial details
    And the user clicks the button/link                         jQuery = button:contains("+ add another innovation area")
    And the user enters valid data in the initial details
    And the user moves focus and waits for autosave
    And the user clicks the button twice                        css = label[for = "stateAid2"]
    When the user clicks the button/link                        jQuery = button:contains("Done")
    Then the user should see the read-only view of the initial details

Initial details - Innovation sector of Open should be visible
    [Documentation]    INFUND-9152
    [Tags]
    Given the user clicks the button/link                       jQuery = .govuk-button:contains("Edit")  # Click Edit
    When the user selects the option from the drop-down menu    Programme    id = competitionTypeId
    Then the user selects the option from the drop-down menu    Sector    id = competitionTypeId
    When the user clicks the button/link                        css = button[class = "govuk-button"]  # Click Done

Initial details - Competitions allow multiple innovation areas
    [Documentation]    INFUND-6478, INFUND-6479
    [Tags]
    Given the user clicks the button/link            jQuery = .govuk-button:contains("Edit")
    When the user enters multiple innovation areas
    And the user clicks the button/link              jQuery = button[class = "govuk-button"]
    Then The user should see the text in the page    Space technology
    And The user should see the text in the page     Creative industries

Initial Details - User can remove an innovation area
    [Documentation]    INFUND-6478, INFUND-6479
    [Tags]
    Given the user clicks the button/link  jQuery = .govuk-button:contains("Edit")
    And the user clicks the button/link    jQuery = #innovation-row-2 button:contains('Remove')
    When the user clicks the button/link   jQuery = button:contains("Done")
    Then the user should not see the text in the page  Space technology

Initial Details - drop down menu is populated with comp admin users
    [Documentation]    INFUND-6905
    [Tags]
    [Setup]    the user clicks the button/link                   jQuery = .govuk-button:contains("Edit")
    When the user should see the option in the drop-down menu    John Doe    name = executiveUserId
    And the user should see the option in the drop-down menu     Robert Johnson    name = executiveUserId

Initial details - Comp Type and Date should not be editable
    [Documentation]    INFUND-2985, INFUND-3182, INFUND-4892
    [Tags]
    And the user enters text to a text field  css = #title  ${competitionTitle}
    And The element should be disabled        css = #competitionTypeId
    And The element should be disabled        css = #openingDateDay
    And the user clicks the button/link       jQuery = button:contains("Done")

Initial details - should have a green check
    [Documentation]    INFUND-3002
    [Tags]
    When The user clicks the button/link     link=Competition setup
    Then the user should see the element     jQuery=li:contains("Initial details") .task-status-complete
    And the user should see the element      css=#compCTA[disabled]

User should have access to all the sections
    [Documentation]    INFUND-4725, IFS-1104  IFS-3086
    Given The user should see the element   link = Terms and conditions
    And The user should see the element     link = Funding information
    And The user should see the element     link = Eligibility
    And The user should see the element     link = Milestones
    And The user should see the element     link = Application
    And The user should see the element     link = Assessors
    # TODO IFS-4186 Uncomment when this functionality is enabled.
#    And the user should see the element     link = Documents in project setup
    And The user should see the element     link = Public content
    And The user should see the element     link = Stakeholders

The user must select the Terms and Conditions they want Applicants to accept
    [Documentation]  IFS-3086
    [Tags]
    Given the user clicks the button/link    link=Terms and conditions
    When the user selects the option from the drop-down menu    5  id=termsAndConditionsId  #5 selects the option with the value of 5, which refers to APC
    And the user clicks the button/link      css=button.govuk-button  #Done
    Then the user should see the element     link=Advanced Propulsion Centre (APC)
    And the user clicks the button/link      link=Competition setup
    And the user should see the element      jQuery=li:contains("Terms and conditions") .task-status-complete

Internal user can navigate to Public Content without having any issues
    [Documentation]  INFUND-6922
    [Tags]
    Given the user clicks the button/link  link=Public content
    Then the user should not see an error in the page
    And the user should see the element  jQuery=h1:contains("Public content")
    And the user should see the element  jQuery=a:contains("Competition information and search")
    And the user should see the element  jQuery=a:contains("Summary")
    And the user should see the element  jQuery=a:contains("Eligibility")
    And the user should see the element  jQuery=a:contains("Scope")
    And the user should see the element  jQuery=a:contains("Dates")
    And the user should see the element  jQuery=a:contains("How to apply")
    And the user should see the element  jQuery=a:contains("Supporting information")
    [Teardown]  the user clicks the button/link  link=Return to setup overview

New application shows in Preparation section with the new name
    [Documentation]    INFUND-2980
    Given the user navigates to the page    ${CA_UpcomingComp}
    Then the user should see the element  jQuery=section:contains("In preparation") li:contains("${competitionTitle}")

Funding information: calculations
    [Documentation]  INFUND-2985 INFUND-4894
    [Tags]
    [Setup]    the user clicks the button/link  link=${competitionTitle}
    Given the user clicks the button/link    link=Funding information
    And the user clicks the button/link    id=generate-code
    And the user enters text to a text field    id=funders[0].funder    FunderName
    And the user enters text to a text field    id=funders[0].funderBudget    20000
    And the user enters text to a text field    id=pafNumber    2016
    And the user enters text to a text field    id=budgetCode    2004
    And the user enters text to a text field    id=activityCode    4242
    When the user clicks the button/link        jQuery=Button:contains("+Add co-funder")
    and the user should see the element         jQuery=Button:contains("+Add co-funder")
    And the user should see the element         jQuery=Button:contains("Remove")
    And the user enters text to a text field    id=1-funder    FunderName2
    And the user enters text to a text field    id=1-funderBudget    1000
    Then the total should be correct            Total: £21,000
    When the user clicks the button/link        jQuery=Button:contains("Remove")
    Then the total should be correct            Total: £20,000

Funding information: can be saved
    [Documentation]    INFUND-3182
    [Tags]
    Given the user moves focus and waits for autosave
    When the user clicks the button/link            jQuery=button:contains("Done")
    Then the user should see the text in the page   FunderName
    And the user should see the text in the page    £20,000
    And the user should see the text in the page    2016
    And the user should see the text in the page    2004
    And the user should see the text in the page    4242
    And the user should see the text in the page    1901-1
    And the user should see the element             jQuery=button:contains("Edit")

Funding information: can be edited
    [Documentation]    INFUND-3002
    [Tags]
    When the user clicks the button/link    jQuery=.govuk-button:contains("Edit")
    And the user enters text to a text field    id=funders[0].funder    testFunder
    And the user moves focus and waits for autosave
    When the user clicks the button/link    jQuery=button:contains("Done")
    Then the user should see the text in the page    testFunder

Funding information: should have a green check
    [Documentation]    INFUND-3002
    [Tags]
    When The user clicks the button/link    link=Competition setup
    Then the user should see the element    css=li:nth-child(2) .task-status-complete
    Then the user should see the element    jQuery=li:contains("Funding information") .task-status-complete
    And the user should see the element     css=#compCTA[disabled]

Eligibility: Contain the correct options
    [Documentation]  INFUND-2989 INFUND-2990 INFUND-9225
    [Tags]
    Given the user clicks the button/link  link = Eligibility
    And the user should see the text in the page    Please choose the project type.
    Then the user should see the element   jQuery = label:contains("Single or Collaborative")
    When the user should see the element   jQuery = label:contains("Collaborative")
    And the user should see the element    jQuery = label:contains("Business")
    And the user should see the element    jQuery = label[for="lead-applicant-type-2"]:contains("Research")
    And the user should see the element    jQuery = label:contains("Research and technology organisation")
    And the user should see the element    jQuery = label:contains("Public sector")
    And the user should see the element    css = label[for="comp-resubmissions-yes"]
    And the user should see the element    css = label[for="comp-resubmissions-no"]
    And the user should see the element    jQuery = label:contains("Feasibility studies")
    And the user should see the element    jQuery = label:contains("Industrial research")
    And the user should see the element    jQuery = label:contains("Experimental development")
    And the user should see the element    css = label[for="comp-overrideFundingRules-yes"]
    And the user should see the element    css = label[for="comp-overrideFundingRules-no"]
    And the resubmission should not have a default selection

Eligibility: Mark as Done then Edit again
    [Documentation]    INFUND-3051 INFUND-3872 INFUND-3002 INFUND-9225
    [Tags]
    Given the user selects the checkbox      research-categories-33
    And the user selects the checkbox        research-categories-34
    And the user selects the radio button    singleOrCollaborative    single
    And the user selects the checkbox        lead-applicant-type-1  # business
    And the user selects the checkbox        lead-applicant-type-3  # RTOs
    And the user selects the option from the drop-down menu    50%    name=researchParticipationAmountId
    And the user selects the radio button    resubmission    no
    And the user moves focus and waits for autosave
    And the user clicks the button twice     css = label[for="comp-overrideFundingRules-no"]
    And the user moves focus and waits for autosave
    When the user clicks the button/link     jQuery = button:contains("Done")
    Then the user should see the element     jQuery = dt:contains("Project type") ~ dd:contains("Single")
    And the user should see the element      jQuery = dt:contains("Research categories") ~ dd:contains("Feasibility studies")
    And the user should see the element      jQuery = dt:contains("Research categories") ~ dd:contains("Industrial research")
    And the user should see the element      jQuery = dt:contains("Lead applicant") ~ dd:contains("Business")
    And the user should see the element      jQuery = dt:contains("Research participation") ~ dd:contains("50%")
    And the user should see the element      jQuery = dt:contains("Are resubmissions allowed") ~ dd:contains("No")
    And the user should see the element      jQuery = dt:contains("Override funding rules") ~ dd:contains("No")
    And The user should not see the element  id = streamName
    When the user clicks the button/link     link = Competition setup
    When the user clicks the button/link     link = Eligibility
    And the user clicks the button/link      jQuery = .govuk-button:contains("Edit")
    And the user clicks the button/link      jQuery = button:contains("Done")

Eligibility: Should have a Green Check
    [Documentation]    INFUND-3002
    [Tags]
    When The user clicks the button/link    link=Competition setup
    Then the user should see the element    css=li:nth-child(3) .task-status-complete
    And the user should see the element     css=#compCTA[disabled]

Milestones: Page should contain the correct fields
    [Documentation]    INFUND-2993
    [Tags]
    When the user clicks the button/link    link=Milestones
    Then the user should see the text in the page  Make sure that dates are in order of milestones, for example the briefing date cannot come after the submission date.
    When The user should see the text in the page   1. Open date
    And the user should see the text in the page    2. Briefing event
    And the user should see the text in the page    3. Submission date
    And the user should see the text in the page    4. Allocate assessors
    And the user should see the text in the page    5. Assessor briefing
    And the user should see the text in the page    6. Assessor accepts
    And the user should see the text in the page    7. Assessor deadline
    And the user should see the text in the page    8. Line draw
    And the user should see the text in the page    9. Assessment panel
    And the user should see the text in the page    10. Panel date
    And the user should see the text in the page    11. Funders panel
    And the user should see the text in the page    12. Notifications
    And the user should see the text in the page    13. Release feedback
    And the pre-field date should be correct

Milestones: Correct Weekdays should show
    [Documentation]    INFUND-2993
    [Tags]
    Given the user fills the milestones with valid data
    When the user clicks the button/link    jQuery=button:contains(Done)
    Then the weekdays should be correct

Milestones: Green check should show
    [Documentation]    INFUND-2993
    [Tags]
    When The user clicks the button/link    link=Competition setup
    Then the user should see the element    css=li:nth-child(4) .task-status-complete
    And the user should see the element     css=#compCTA[disabled]

Application - Application process Page
    [Documentation]    INFUND-3000 INFUND-5639
    [Tags]
    #Writing the following selectors using jQuery in order to avoid hardcoded numbers.
    When The user clicks the button/link  link=Application
    Then the user should see the element  jQuery=h2:contains("Sector competition questions")
    When the user should see the element  link=Application details
    Then the user should see the element  link=Project summary
    And the user should see the element   link=Public description
    And the user should see the element   link=Scope
    When the user should see the element  jQuery=a:contains("${amendedQuestion}")
    Then the user should see the element  jQuery=a:contains("Approach and innovation")
    And the user should see the element   jQuery=a:contains("Team and resources")
    And the user should see the element   jQuery=a:contains("Market awareness")
    And the user should see the element   jQuery=a:contains("Outcomes and route to market")
    And the user should see the element   jQuery=a:contains("Wider impacts")
    And the user should see the element   jQuery=a:contains("Project management")
    And the user should see the element   jQuery=a:contains("Risks")
    And the user should see the element   jQuery=a:contains("Additionality")
    And the user should see the element   jQuery=a:contains("Costs and value for money")
    And the user should see the element   jQuery=.button-clear:contains("Add question")
    And the user should see the element   link=Finances

Application: Application details validations
    [Documentation]  IFS-2776
    [Tags]
    Given the user clicks the button/link      link=Application details
    And the user enters text to a text field   id=minProjectDuration  ${empty}
    And the user enters text to a text field   id=maxProjectDuration  ${empty}
    # And the user unchecks the resubmission radio button
    # TODO IFS-3188

    When the user selects the radio button     useResubmissionQuestion  true
    Then the user should see the element       jQuery=label[for="minProjectDuration"] + .govuk-error-message:contains("This field cannot be left blank")
    And the user should see the element        jQuery=label[for="maxProjectDuration"] + .govuk-error-message:contains("This field cannot be left blank")
    When the user clicks the button/link       css=button[type="submit"]
    Then the user should see the element       css=.govuk-error-summary__list

    When the user enters text to a text field  id=minProjectDuration  -2
    And the user enters text to a text field   id=maxProjectDuration  -3
    Then the user should see a field error     This field should be 1 or higher.
    And the user should see a field error      The maximum must be larger than the minimum.

    When the user enters text to a text field  id=minProjectDuration  66
    And the user enters text to a text field   id=maxProjectDuration  65
    Then the user should see a field error     The minimum must be smaller than the maximum.
    And the user should see a field error      This field should be 60 or lower.

    When the user enters text to a text field  id=minProjectDuration  59
    And the user clicks the button/link        css=button[type="submit"]
    Then the user should see a summary error   This field should be 60 or lower
    [Teardown]  the user clicks the button/link  link=Application


Application: Application details
    [Documentation]  INFUND-5633 IFS-2776
    [Tags]
    Given the user clicks the button/link         link=Application details
    And the user should see the element           jQuery=h1:contains("Application details")
    When the user selects the radio button        useResubmissionQuestion  false
    Then the user enters text to a text field     id=minProjectDuration  2
    And the user enters text to a text field      id=maxProjectDuration  60
    And The user clicks the button/link           css=button[type="submit"]
    And the user should see the element           jQuery=li:contains("Application details") .task-status-complete
    When the user clicks the button/link          link=Application details
    Then the user should see the element          jQuery=dt:contains("resubmission") + dd:contains("No")
    And the user should see the element           jQuery=dt:contains("Minimum") + dd:contains("2")
    And the user should see the element           jQuery=dt:contains("Maximum") + dd:contains("60")
    [Teardown]  The user clicks the button/link   link=Application

Application: Scope
    [Documentation]  INFUND-5634 INFUND-5635
    [Tags]
    Given the user clicks the button/link         link=Scope
    Then the user should see the element          jQuery=h1:contains("Scope")
    And the user should see the text in the page  You can edit this question for the applicant as well as the guidance for assessors.
    When The user fills the empty question fields
    And The user enters text to a text field      id=question.shortTitle  Test heading
    And The user clicks the button/link           css=button[type="submit"]
    And the user clicks the button/link           link=Test heading
    Then the user should see the element          jQuery=h1:contains("Test heading")
    And the user checks the question fields

Application: Scope Assessment questions
    [Documentation]    INFUND-5631    INFUND-6044  INFUND-6283
    [Tags]
    Given the user clicks the button/link  link=Edit this question
    And the user selects the radio button  question.writtenFeedback    1
    And the user fills the scope assessment questions
    When the user clicks the button/link   css=button[type="submit"]
    And the user clicks the button/link    link=Test heading
    Then the user checks the scope assessment questions
    And the user clicks the button/link    link=Edit this question
    And the user selects the radio button  question.writtenFeedback    0
    And the user should not be able to edit the scope feedback
    And the user clicks the button/link    css=button[type="submit"]
    And the user clicks the button/link    link=Test heading
    Then the user should not see the scope feedback
    [Teardown]    The user clicks the button/link    link=Application

Application: Project Summary
    [Documentation]  INFUND-5636 INFUND-5637
    [Tags]
    Given the user clicks the button/link    link=Project summary
    And the user should see the element      jQuery=h1:contains("Project summary")
    And the user should see the text in the page    You can edit this question for the applicant as well as the guidance for assessors.
    When The user fills the empty question fields
    And The user clicks the button/link    css=button[type="submit"]
    And the user clicks the button/link    link=Project summary
    Then The user should see the text in the page    Project summary
    And the user checks the question fields
    [Teardown]  The user clicks the button/link  link=Application

Application: Need or challenge
    [Documentation]  INFUND-5632 INFUND-5685 INFUND-5630 INFUND-6283 IFS-2776
    [Tags]
    Given the user should not see the element  jQuery=li:contains("${amendedQuestion}") .task-status-complete
    When the user clicks the button/link    jQuery=h4 a:contains("${amendedQuestion}")
    And the user clicks the button/link     css=button[type="submit"]
    And the user clicks the button/link     jQuery=h4 a:contains("${amendedQuestion}")
    Then the user should see the element    jQuery=dt:contains("Question heading") + dd:contains("${amendedQuestion}")
    # The above steps verify that when the question is not completed and you click it, you land on the edit mode
    # If question is completed and you click it, you should land on the read only mode.
    When the user clicks the button/link    link=Edit this question
    And the user edits the assessed question information
    And The user clicks the button/link     css=button[type="submit"]
    When the user clicks the button/link    jQuery=h4 a:contains("${amendedQuestion}")
    Then the user sees the correct read only view of the question
    When the user clicks the button/link    link=Edit this question
    And the user selects the radio button   question.writtenFeedback  0
    And the user selects the radio button   question.scored  0
    And the user should not be able to edit the assessed question feedback
    And the user clicks the button/link     jQuery=button[type="submit"]
    When the user clicks the button/link      jQuery=h4 a:contains("${amendedQuestion}")
    Then the user should not see the element  jQuery=dt:contains("Guidance") + dd:contains("Your score should be based upon the following")

Application: marking questions as complete
    [Documentation]  IFS-743
    [Tags]
    When the user clicks the button/link      link=Application
    Then the user marks question as complete  Public description
    And the user marks question as complete   Approach and innovation
    And the user marks question as complete   Team and resources
    And the user marks question as complete   Market awareness
    And the user marks question as complete   Outcomes and route to market
    And the user marks question as complete   Wider impacts
    And the user marks question as complete   Project management
    And the user marks question as complete   Risks
    And the user marks question as complete   Additionality
    And the user marks question as complete   Costs and value for money

Adding a new Assessed Application Question
    [Documentation]  IFS-182    IFS-2285
    [Tags]
    Given the user clicks the button/link                css=p button[type="submit"]  #Add question link
    When the user is able to configure the new question  ${customQuestion}
    And the user clicks the button/link                  jQuery=li:contains("${customQuestion}")
    Then the user should be able to see the read only view of question correctly  ${customQuestion}

Removing an Assessed Application Question
    [Documentation]  IFS-182
    [Tags]
    Given the user clicks the button/link     jQuery=a:contains("Costs and value for money")
    When the user clicks the button/link      css=button[name="deleteQuestion"]
    Then the user should not see the element  jQuery=a:contains("Costs and value for money")
    When the user should see the element      jQuery=li:contains("Additionality") .task-status-complete
    Then the user should not see the element  jQuery=li:contains("Additionality") button:contains("Remove")

Application: Finances
    [Documentation]    INFUND-5640, INFUND-6039, INFUND-6773  IFS-2192
    [Tags]
    Given the user clicks the button/link    link=Finances
    When the user should see the element     jQuery=h1:contains("Application finances")
    And the user should see the element      jQuery=.panel:contains("The competition template will select the following finance sections for each partner.")
    And the user selects the radio button    applicationFinanceType  STANDARD
#   The Project Growth table option is defaulted to yes for Sector type comp and "No" option is disabled.
    And the user should not see the element  css=input[id="include-growth-table-no"]
    When the user selects the radio button    includeGrowthTable  true
    And the user enters text to a text field  css=.editor  Funding rules for this competition are now entered.
    Then The user clicks the button/link      css=button[type="submit"]  #Save and close
    When the user clicks the button/link     link=Finances
    Then the user should see the element     jQuery=dt:contains("Include project growth table")+dd:contains("Yes")
    And the user should see the element      jQuery=dt:contains("Funding rules for this competition")+dd:contains("Funding rules for this competition are now entered.")
    [Teardown]  the user clicks the button/link  link=Return to application questions

Application: Done enabled when all questions are marked as complete
    [Documentation]    INFUND-5964
    [Tags]
    Given The user clicks the button/link     css = button[class = "govuk-button"]  # Done button
    Then The user should not see the element  css = button[class = "govuk-button"]  # Done button
    When The user clicks the button/link      link=Return to setup overview
    Then the user should see the element      jQuery=li:contains("Application") .task-status-complete

# TODO IFS-4186 Uncomment when this functionality is enabled.
#Documents in project setup: The competition admin adds document requirements
#    [Documentation]    IFS-3916
#    [Tags]
#    Given the user clicks the button/link        link = Documents in project setup
#    And the user clicks the button/link          link = Add document type
#    When the user enters text to a text field    id = title    Test document type
#    And the user clicks the button/link          jQuery = span:contains("PDF")
#    And the user clicks the button/link          jQuery = span:contains("Spreadsheet")
#    And the user enters text to a text field     css = .editor    Guidance test.
#    And the user clicks the button/link          css = button[type = "submit"]
#    And the user should see the element          jQuery = span:contains("Test document type")

# TODO IFS-4186 Uncomment when this functionality is enabled.
#Documents in project setup: The competition admin removes a document
#    [Documentation]    IFS-3916
#    [Tags]
#    Given the user clicks the button/link       jQuery = span:contains("Test document type") ~ a:contains("Edit")
#    When the user clicks the button/link        css = button[name = "removeDocument"]
#    And the user clicks the button/link         jQuery = button:contains("Confirm")
#    Then the user should not see the element    jQuery = span:contains("Test document type")
#    And the user clicks the button/link         link = Competition setup

Public content is required for a Competition to be setup
    [Documentation]
    [Tags]
    Given the user clicks the button/link  link=Public content
    When the user fills in the Public content and publishes  GrowthTable
    And the user clicks the button/link    link=Return to setup overview
    Then the user should see the element   jQuery=li:contains("Public content") .task-status-complete

Complete button disabled when sections are edited
    [Documentation]  IFs-648
    [Tags]
    Given the user should see the element  css=#compCTA
    When the user clicks the button/link  link=Eligibility
    And the user clicks the button/link   jQuery=button:contains("Edit")
    And the user clicks the button/link   link=Competition setup
    Then the user should see the element  css=#compCTA[disabled="disabled"]
    When the user clicks the button/link  link=Eligibility
    And the user clicks the button/link   jQuery=button:contains("Done")
    And the user clicks the button/link   link=Return to setup overview
    Then the user should not see the element  css=#compCTA[disabled="disabled"]

Moving competition to Ready to Open state
    [Documentation]
    [Tags]
#    The following steps will move the comp from "In preparation" to "Ready to Open" state
    When the user clicks the button/link  css=#compCTA
    Then the user clicks the button/link  jQuery=.govuk-button:contains("Done")
    When the user navigates to the page   ${CA_UpcomingComp}
    Then the user should see the element  jQuery=section:contains("Ready to open") li:contains("${competitionTitle}")

Requesting the id of this Competition
    [Documentation]  retrieving the id of the competition so that we can use it in urls
    [Tags]   MySQL
    ${competitionId} =  get comp id from comp title  ${competitionTitle}
    Set suite variable  ${competitionId}

Ready To Open button is visible when the user re-opens a section
    [Documentation]    INFUND-4468
    [Tags]
    [Setup]  the user navigates to the page  ${server}/management/competition/setup/${competitionId}
    When The user clicks the button/link     link=Initial details
    And the user clicks the button/link      jQuery=.govuk-button:contains("Edit")
    And The user clicks the button/link      link=Competition setup
    Then the user should see the element     css=#compCTA[disabled="disabled"]
    [Teardown]    Run keywords    Given The user clicks the button/link    link=Initial details
    ...    AND    The user clicks the button/link    jQuery=button:contains("Done")
    ...    AND    And The user clicks the button/link    link=Competition setup

Application: Edit again should mark as incomplete
    [Documentation]    INFUND-5964
    [Tags]
    [Setup]  the user clicks the button/link    link=Application
    Given the user clicks the button/link       link=Application details
    When the user clicks the button/link        link=Edit this question
    And the user navigates to the page          ${server}/management/competition/setup/${competitionId}
    Then the user should see the element        css=#compCTA[disabled="disabled"]
    When the user navigates to the page         ${server}/management/competition/setup/${competitionId}/section/application/landing-page
    Then the user clicks the button/link        jQuery=button:contains("Done")
    When the user clicks the button/link        link=Application details
    And the user clicks the button/link         css=button[type="submit"]
    Then the user should see the element        jQuery=li:contains("Application details") .task-status-complete


User should be able to Save the Competition as Open
    [Documentation]    INFUND-4468, INFUND-3002
    [Tags]
    [Setup]  the user navigates to the page  ${server}/management/competition/setup/${competitionId}/section/application/landing-page
    And the user clicks the button/link      css=button.govuk-button
    Given the user navigates to the page     ${server}/management/competition/setup/${competitionId}
    And the user should see the element      jQuery=li:contains("Application") .task-status-complete
    When the user clicks the button/link     css=#compCTA
    Then the user clicks the button/link     jQuery=.govuk-button:contains("Done")
    When the user clicks the button/link     link=Competition
    And the user navigates to the page       ${CA_UpcomingComp}
    Then the user should see the element     jQuery=section:contains("Ready to open") li:contains("${competitionTitle}")

Assessor: Contain the correct options
    [Documentation]    INFUND-5641
    [Tags]
    [Setup]  the user clicks the button/link  link=${competitionTitle}
    Given The user clicks the button/link   link=View and update competition setup
    And the user clicks the button/link     link=Assessors
    And the user should see the text in the page    How many assessors are required for each application?
    Then the user should see the element    jQuery=label:contains(1)
    When the user should see the element    jQuery=label:contains(3)
    And the user should see the element     jQuery=label:contains(5)
    And the user should see the text in the page    How much do assessors receive per application
    And the user should see the element     id=assessorPay

Assessor: Mark as Done then Edit again
    [Documentation]    INFUND-5641 IFS-380
    [Tags]
    Given the user selects the radio button    assessorCount   5
    And the user selects the radio button      hasInterviewStage  hasInterviewStage-1
    And the user selects the radio button      hasAssessmentPanel  0
    Then the user enters text to a text field  id=assessorPay  100
    When the user clicks the button/link       jQuery=button:contains("Done")
    Then the user should see the element       jQuery=dt:contains("How many assessors") + dd:contains("5")
    And the user should see the element        jQuery=dt:contains("How much do assessors receive") + dd:contains("100")
    And the user should see the element        jQuery=dt:contains("assessment panel") + dd:contains("No")
    And the user should see the element        jQuery=dt:contains("interview stage") + dd:contains("No")
    When the user clicks the button/link       jQuery=.govuk-button:contains("Edit")
    Then the user selects the radio button     hasInterviewStage  hasInterviewStage-0
    When the user clicks the button/link       jQuery=button:contains("Done")
    Then the user should see the element       jQuery=dt:contains("interview stage") + dd:contains("Yes")

Assessor: Should have a Green Check
    [Documentation]  INFUND-5641
    [Tags]
    When The user clicks the button/link    link=Competition setup
    Then the user should see the element    jQuery=li:contains("Assessors") .task-status-complete
    And the user clicks the button/link     css=#compCTA
    And the user clicks the button/link     jQuery=.govuk-button:contains("Done")
    When the user navigates to the page     ${CA_UpcomingComp}
    Then the user should see the element    jQuery=section:contains("Ready to open") li:contains("${competitionTitle}")

Innovation leads can be added to a competition
    [Documentation]    IFS-192, IFS-1104
    [Tags]
    [Setup]  the user clicks the button/link  link = ${competitionTitle}
    Given The user clicks the button/link     link = View and update competition setup
    And The user clicks the button/link       link = Stakeholders
    And the user should see the element       jQuery = h1:contains("Manage innovation leads")
    #And the user should see the element       jQuery=span.lead-count:contains("0")  # Lead count from key statistics
    When the user clicks the button/link      jQuery = td:contains(${peter_freeman}) button:contains("Add")
    Then the user should not see the element  jQuery = td:contains(${peter_freeman})
    #And the user should not see the element   jQuery=td:contains("Ian Cooper")
    And the user should see the element       jQuery = span.lead-count:contains("1")
    When the user clicks the button/link      jQuery = a:contains("Overview")
    Then the user should see the element      jQuery = span.total-count:contains("1")
    #And the user should not see the element   jQuery=td:contains("Ian Cooper")
    And the user clicks the button/link       jQuery = td:contains(${peter_freeman}) button:contains("Remove")
    And the user should see the element       jQuery = span.lead-count:contains("0")
    And the user should see the element       jQuery = span.total-count:contains("0")
    When the user clicks the button/link      jQuery = .govuk-tabs__list a:contains("Find")
    Then the user should see the element      jQuery = td:contains(${peter_freeman}) button:contains("Add")

User deletes the competition
    [Documentation]  IFS-1084
    Given the user navigates to the page      ${CA_UpcomingComp}
    And The user clicks the button/link       link=No competition title defined
    When the user clicks the button/link      link=Delete competition
    And the user clicks the button/link       css=.delete-modal button[type="submit"]
    And the user navigates to the page        ${CA_UpcomingComp}
    Then The user should not see the element  link=No competition title defined

User cannot delete competition with assessors
   [Documentation]  IFS-1084
   Given the user clicks the button/link     link=Photonics for health
   And The user clicks the button/link       link=View and update competition setup
   When the user clicks the button/link      link=Delete competition
   And the user clicks the button/link       css=.delete-modal button[type="submit"]
   Then The user should see a summary error  You cannot delete this competition as assessors have been invited.

The Applicant is able to apply to the competition once is Open
    [Documentation]  IFS-182
    [Tags]  MySQL
    [Setup]  the competition moves to Open state    ${competitionId}
    Given log in as a different user                &{lead_applicant_credentials}
    And logged in user applies to competition       ${competitionTitle}  1

The Applicant should see the selected research cartegories
    [Documentation]  IFS-2941
    When the user clicks the button/link     link = Research category
    Then the user should see the element     css = label[for="researchCategory1"]
    And the user should see the element      css = label[for="researchCategory2"]
    When the user clicks the button twice    jQuery = label:contains("Feasibility studies")
    And the user clicks the button/link      id = application-question-save

The Applicant see the correct Questions
    [Documentation]   IFS-182
    Given the user should see the element            jQuery=li:contains("${customQuestion}")
    And the user should not see the element          jQuery=li:contains("Costs and value for money")
    #default question that has been removed is not there.

The internal user cannot invite a Stakeholder when they have triggered the name validation
    [Documentation]  IFS-4190
    [Tags]
    Given log in as a different user                     &{Comp_admin1_credentials}
    And the user navigates to the page                   ${SERVER}/management/competition/setup/${competitionId}/manage-stakeholders
    When the user triggers the name validation
    Then the user should see the name validation messages

The internal user cannot invite a Stakeholder when they have triggered the email validation
    [Documentation]  IFS-4190
    [Tags]
    Then the user triggers the email validation

The internal user cannot invite users with an Innovate UK email as Stakeholders
    [Documentation]  IFS-4190
    [Tags]
    When the user enters an Innovate UK email
    Then the user should see a field and summary error    Stakeholders cannot be registered with an Innovate UK email address.

The internal user invites a Stakeholder
    [Documentation]  IFS-4190
    [Tags]
    Then the user enters the correct details of a Stakeholder
    # There's no way of verifying if this has been successful yet, which is why there is no check.

*** Keywords ***
the user moves focus and waits for autosave
    focus    link=Sign out
    Wait For Autosave

the total should be correct
    [Arguments]    ${Total}
    mouse out    css=input
    Focus    jQuery=button:contains("Done")
    Wait Until Element Contains Without Screenshots    css=.govuk-heading-s  ${Total}

the user fills the milestones with valid data
    The user enters text to a text field    name=milestoneEntries[OPEN_DATE].day    10
    The user enters text to a text field    name=milestoneEntries[OPEN_DATE].month    1
    The user enters text to a text field    name=milestoneEntries[OPEN_DATE].year    2019
    The user enters text to a text field    name=milestoneEntries[BRIEFING_EVENT].day    11
    The user enters text to a text field    name=milestoneEntries[BRIEFING_EVENT].month    1
    The user enters text to a text field    name=milestoneEntries[BRIEFING_EVENT].year    2019
    The user enters text to a text field    name=milestoneEntries[SUBMISSION_DATE].day    12
    The user enters text to a text field    name=milestoneEntries[SUBMISSION_DATE].month    1
    The user enters text to a text field    name=milestoneEntries[SUBMISSION_DATE].year    2019
    The user enters text to a text field    name=milestoneEntries[ALLOCATE_ASSESSORS].day    13
    The user enters text to a text field    name=milestoneEntries[ALLOCATE_ASSESSORS].month    1
    The user enters text to a text field    name=milestoneEntries[ALLOCATE_ASSESSORS].year    2019
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_BRIEFING].day    14
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_BRIEFING].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_BRIEFING].year    2019
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_ACCEPTS].day    15
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_ACCEPTS].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_ACCEPTS].year    2019
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_DEADLINE].day    16
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_DEADLINE].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_DEADLINE].year    2019
    The user enters text to a text field    name=milestoneEntries[LINE_DRAW].day    17
    The user enters text to a text field    name=milestoneEntries[LINE_DRAW].month    1
    The user enters text to a text field    name=milestoneEntries[LINE_DRAW].year    2019
    The user enters text to a text field    name=milestoneEntries[ASSESSMENT_PANEL].day    18
    The user enters text to a text field    name=milestoneEntries[ASSESSMENT_PANEL].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSMENT_PANEL].year    2019
    The user enters text to a text field    name=milestoneEntries[PANEL_DATE].day    19
    The user enters text to a text field    name=milestoneEntries[PANEL_DATE].month    1
    The user enters text to a text field    name=milestoneEntries[PANEL_DATE].year    2019
    The user enters text to a text field    name=milestoneEntries[FUNDERS_PANEL].day    20
    The user enters text to a text field    name=milestoneEntries[FUNDERS_PANEL].month    1
    The user enters text to a text field    name=milestoneEntries[FUNDERS_PANEL].year    2019
    The user enters text to a text field    name=milestoneEntries[NOTIFICATIONS].day    21
    The user enters text to a text field    name=milestoneEntries[NOTIFICATIONS].month    1
    The user enters text to a text field    name=milestoneEntries[NOTIFICATIONS].year    2019
    The user enters text to a text field    name=milestoneEntries[RELEASE_FEEDBACK].day    22
    The user enters text to a text field    name=milestoneEntries[RELEASE_FEEDBACK].month    1
    The user enters text to a text field    name=milestoneEntries[RELEASE_FEEDBACK].year    2019
    Focus    jQuery=button:contains(Done)
    wait for autosave

the weekdays should be correct
    element should contain    css = tr:nth-child(1) td:nth-child(3)     Thu
    element should contain    css = tr:nth-child(2) td:nth-child(3)     Fri
    element should contain    css = tr:nth-child(3) td:nth-child(3)     Sat
    element should contain    css = tr:nth-child(4) td:nth-child(3)     Sun
    element should contain    css = tr:nth-child(5) td:nth-child(3)     Mon
    element should contain    css = tr:nth-child(6) td:nth-child(3)     Tue
    element should contain    css = tr:nth-child(7) td:nth-child(3)     Wed
    element should contain    css = tr:nth-child(8) td:nth-child(3)     Thu
    element should contain    css = tr:nth-child(9) td:nth-child(3)     Fri
    element should contain    css = tr:nth-child(10) td:nth-child(3)    Sat
    element should contain    css = tr:nth-child(11) td:nth-child(3)    Sun
    element should contain    css = tr:nth-child(12) td:nth-child(3)    Mon
    element should contain    css = tr:nth-child(13) td:nth-child(3)    Tue

the pre-field date should be correct
    Element Should Contain        id = milestoneWeekdayEntry-OPEN_DATE    Thu
    ${YEAR} =    Get Value        css = #milestoneWeekdayEntry-OPEN_DATE ~ .year .govuk-input--width-4  # Get the value within the YEAR field
    Should Be Equal As Strings    ${YEAR}  ${nextyear}
    ${MONTH} =    Get Value       css = #milestoneWeekdayEntry-OPEN_DATE ~ .month .govuk-input--width-4  # Get the value within the MONTH field
    Should Be Equal As Strings    ${MONTH}    1
    ${DAY} =    Get Value         css = #milestoneWeekdayEntry-OPEN_DATE ~ .day .govuk-input--width-4    #Get the value within the DAY field
    Should Be Equal As Strings    ${DAY}    10

the resubmission should not have a default selection
    the user should see the element  css=[name="resubmission"]:not(:checked) ~ label

The user enters valid data in the initial details
    Given the user enters text to a text field                 css = #title  Competition title
    When the user selects the option from the drop-down menu   Sector  id = competitionTypeId
    And the user selects the option from the drop-down menu    Infrastructure systems  id = innovationSectorCategoryId
    And the user selects the option from the drop-down menu    Offshore wind  name = innovationAreaCategoryIds[0]
    And the user selects the option from the drop-down menu    Open  id = innovationSectorCategoryId
    And the user selects the option from the drop-down menu    Biosciences     name = innovationAreaCategoryIds[0]
    And the user selects the option from the drop-down menu    Emerging and enabling  id = innovationSectorCategoryId
    And the user selects the option from the drop-down menu    Satellite applications  name = innovationAreaCategoryIds[0]
    And the user selects the option from the drop-down menu    Space technology  name = innovationAreaCategoryIds[1]
    And the user enters text to a text field                   id = openingDateDay    10
    And the user enters text to a text field                   id = openingDateMonth    1
    And the user enters text to a text field                   id = openingDateYear     ${nextyear}
    And the user selects the option from the drop-down menu    Ian Cooper    id = innovationLeadUserId
    And the user selects the option from the drop-down menu    John Doe   id = executiveUserId

The competition should show in the correct section
    [Arguments]    ${SECTION}    ${COMP_NAME}
    Element should contain    ${SECTION}    ${COMP_NAME}

the user fills the scope assessment questions
    The user clicks the button/link    jQuery=button:contains("+Add guidance row")
    The user enters text to a text field    id=guidanceRows[2].subject    New subject
    The user enters text to a text field    id=guidanceRows[2].justification    This is a justification
    The user enters text to a text field    id=question.assessmentGuidance    Guidance for assessing scope section
    The user clicks the button/link    id=remove-guidance-row-1

the user checks the scope assessment questions
    The user should see the text in the page    New subject
    The user should see the text in the page    This is a justification
    The user should not see the text in the page    One or more of the above requirements have not been satisfied.
    The user should see the text in the page    Written feedback
    The user should see the text in the page    Guidance for assessing scope section
    The user should see the text in the page    Scope 'Y/N' question
    The user should see the text in the page    Research category question

the user should not be able to edit the scope feedback
    the user should not see the element    id=question.assessmentGuidanceTitle
    the user should not see the element    id=question.assessmentGuidance
    the user should not see the element    id=guidanceRows[0].subject
    the user should not see the element    id=guidanceRows[0].justification
    the user should not see the element    jQuery=Button:contains("+Add guidance row")

the user should not see the scope feedback
    the user should not see the text in the page    Guidance for assessing scope
    the user should not see the text in the page    Your answer should be based upon the following:
    the user should not see the text in the page    One or more of the above requirements have not been satisfied

the user should not be able to edit the assessed question feedback
    the user should not see the element    id=question.assessmentGuidanceTitle
    the user should not see the element    id=question.assessmentGuidance
    the user should not see the element    id=guidanceRows[0].scoreFrom
    the user should not see the element    id=guidanceRows[0].scoreTo
    the user should not see the element    id=guidanceRows[0].justification
    the user should not see the element    jQuery=Button:contains("+Add guidance row")
    the user should not see the element    id=question.scoreTotal

Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}

the user enters multiple innovation areas
    the user clicks the button/link    jQuery=.button-clear:contains("+ add another innovation area")
    the user selects the option from the drop-down menu    Space technology    name=innovationAreaCategoryIds[1]
    the user clicks the button/link    jQuery=.button-clear:contains("+ add another innovation area")
    List Should not Contain Value    css=[id="innovationAreaCategoryIds[2]"]    Space technology
    the user selects the option from the drop-down menu    Creative industries    name=innovationAreaCategoryIds[2]

The user should not see the selected option again
    List Should not Contain Value    css=[id="innovationAreaCategoryIds[1]"]    Biosciences

the user marks question as complete
    [Arguments]  ${question_link}
    the user should not see the element  jQuery=li:contains("${question_link}") .task-status-complete
    the user clicks the button/link      jQuery=a:contains("${question_link}")
    the user clicks the button/link      css=button[type="submit"]
    the user should see the element      jQuery=li:contains("${question_link}") .task-status-complete

the user should see the read-only view of the initial details
    the user should see the element    jQuery = dd:contains("Competition title")
    the user should see the element    jQuery = dd:contains("Sector")
    the user should see the element    jQuery = dd:contains("Emerging and enabling")
    the user should see the element    jQuery = dd:contains("Satellite applications")
    the user should see the element    jQuery = dd:contains("Space technology")
    the user should see the element    jQuery = dd:contains("10/1/${nextyear}")
    the user should see the element    jQuery = dd:contains("Ian Cooper")
    the user should see the element    jQuery = dd:contains("John Doe")
    the user should see the element    jQuery = dt:contains("State aid") ~ dd:contains("No")

the user triggers the name validation
    the user clicks the button/link         jQuery = span:contains("Invite a new stakeholder")
    the user enters text to a text field    id = emailAddress  stakeHolder@test.com
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user should see the name validation messages
    the user should see a field and summary error    Please enter a first name.
    the user should see a field and summary error    Your first name should have at least 2 characters.
    the user should see a field and summary error    Please enter a last name.
    the user should see a field and summary error    Your last name should have at least 2 characters.

the user triggers the email validation
    the user enters text to a text field             id = firstName     Stake
    the user enters text to a text field             id = lastName      Holder
    the user enters text to a text field             id = emailAddress  stakeHoldertest.com
    the user clicks the button/link                  css = button[name = "inviteStakeholder"]
    the user should see a field and summary error    Please enter a valid email address.

the user enters an Innovate UK email
    the user enters text to a text field    id = firstName     Stake
    the user enters text to a text field    id = lastName      Holder
    the user enters text to a text field    id = emailAddress  stakeHolder@innovateuk.test
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user enters the correct details of a Stakeholder
    the user enters text to a text field    id = firstName     Stake
    the user enters text to a text field    id = lastName      Holder
    the user enters text to a text field    id = emailAddress  stakeHolder@test.com
    the user clicks the button/link         css = button[name = "inviteStakeholder"]
