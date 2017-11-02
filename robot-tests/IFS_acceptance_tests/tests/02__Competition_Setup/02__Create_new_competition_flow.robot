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
Suite Setup       Custom suite setup
Suite Teardown    The user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          CompAdmin_Commons.robot
Resource          ../04__Applicant/Applicant_Commons.robot

*** Variables ***
${peter_freeman}  Peter Freeman
${competitionTitle}  Test competition

*** Test Cases ***
User can create a new competition
    [Documentation]    INFUND-2945, INFUND-2982, INFUND-2983, INFUND-2986, INFUND-3888, INFUND-3002, INFUND-2980, INFUND-4725, IFS-1104
    [Tags]    HappyPath
    Given the user navigates to the page       ${CA_UpcomingComp}
    When the user clicks the button/link       jQuery=.button:contains("Create competition")
    And The user should see the element        css=#compCTA[disabled]
    And The user should not see the element    link=Funding information
    And The user should not see the element    link=Eligibility
    And The user should not see the element    link=Milestones
    And The user should not see the element    link=Application
    And The user should not see the element    link=Assessors
    And The user should not see the element    link=Public content
    And The user should see the element        link=Initial details
    And The user should not see the element    link=Stakeholders
    And The user should see the element        jQuery=p:contains("Once you complete, this competition will be ready to open.")

New competition shows in Preparation section
    [Documentation]    INFUND-2980
    Given The user clicks the button/link    link=All competitions
    And the user navigates to the page    ${CA_UpcomingComp}
    Then the competition should show in the correct section    css=section:nth-of-type(1) li:nth-child(2)    No competition title defined    #this keyword checks if the new application shows in the second line of the "In preparation" competitions

Initial details - User enters valid values and marks as done
    [Documentation]    INFUND-2982, INFUND-3888, INFUND-2983, INFUND-6478, INFUND-6479
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    Given the user clicks the button/link                       link=Initial details
    And the user should see the option in the drop-down menu    Generic  id=competitionTypeId
    When the user selects the option from the drop-down menu    Programme    id=competitionTypeId
    And the user clicks the button/link                         jQuery=button:contains("+ add another innovation area")
    And the user enters valid data in the initial details
    And the user moves focus and waits for autosave
    When the user clicks the button/link             jQuery=button:contains("Done")
    Then the user should see the text in the page    John Doe
    And the user should see the text in the page     1/12/${nextyear}
    And the user should see the text in the page     Ian Cooper
    And the user should see the text in the page     Competition title
    And the user should see the text in the page     Emerging and enabling
    And the user should see the text in the page     Satellite applications
    And the user should see the text in the page     Space technology
    And the user should see the text in the page     Sector
    And the user should see the text in the page     Yes
    And the user should see the element              jQuery=.button:contains("Edit")

Initial details - Innovation sector of Open should be visible
    [Documentation]    INFUND-9152
    [Tags]    HappyPath
    Given the user clicks the button/link                               jQuery=.button:contains("Edit")
    Then the user should see the element                                jQuery=button:contains("+ add another innovation area")
    When the user selects the option from the drop-down menu            Programme    id=competitionTypeId
    And the user selects the option from the drop-down menu             Open    id=innovationSectorCategoryId
    And the user selects the option from the drop-down menu             Biosciences    css=[id="innovationAreaCategoryIds[0]"]
    And the user clicks the button/link                                 jQuery=button:contains("+ add another innovation area")
    Then the user selects the option from the drop-down menu            Sector    id=competitionTypeId
    When the user selects the option from the drop-down menu            Open    id=innovationSectorCategoryId
    Then the user should not see the selected option again
    When the user clicks the button/link                                jQuery=button:contains("Done")
    Then the user should see the text in the page                       Open
    And the user should see the text in the page                        Biosciences
    And the user should see the element                                 jQuery=.button:contains("Edit")

Initial details - Competitions allow multiple innovation areas
    [Documentation]    INFUND-6478, INFUND-6479
    [Tags]    HappyPath
    Given the user clicks the button/link            jQuery=.button:contains("Edit")
    When the user enters multiple innovation areas
    And the user clicks the button/link              jQuery=button:contains("Done")
    Then The user should see the text in the page    Space technology
    And The user should see the text in the page     Creative industries

Initial Details - User can remove an innovation area
    [Documentation]    INFUND-6478, INFUND-6479
    [Tags]
    Given the user clicks the button/link  jQuery=.button:contains("Edit")
    And the user clicks the button/link    jQuery=#innovation-row-2 button:contains('Remove')
    When the user clicks the button/link   jQuery=button:contains("Done")
    Then the user should not see the text in the page  Space technology

Initial Details - drop down menu is populated with comp admin users
    [Documentation]    INFUND-6905
    [Tags]    HappyPath
    [Setup]    the user clicks the button/link    jQuery=.button:contains("Edit")
    When the user should see the option in the drop-down menu    John Doe    name=executiveUserId
    And the user should see the option in the drop-down menu    Robert Johnson    name=executiveUserId

Initial details - Comp Type and Date should not be editable
    [Documentation]    INFUND-2985, INFUND-3182, INFUND-4892
    [Tags]    HappyPath
    And the user enters text to a text field  css=#title  ${competitionTitle}
    And The element should be disabled        css=#competitionTypeId
    And The element should be disabled        css=#openingDateDay
    And the user clicks the button/link       jQuery=button:contains("Done")
    Then the user should see the text in the page   1/12/${nextyear}
    And the user should see the text in the page    Ian Cooper
    And the user should see the text in the page    ${competitionTitle}
    And the user should see the text in the page    Open
    And the user should see the text in the page    Biosciences
    And the user should see the text in the page    Creative industries
    And the user should see the text in the page    Yes

Initial details - should have a green check
    [Documentation]    INFUND-3002
    [Tags]    HappyPath
    When The user clicks the button/link     link=Competition setup
    Then the user should see the element     jQuery=li:contains("Initial details") .task-status-complete
    And the user should see the element      css=#compCTA[disabled]

User should have access to all the sections
    [Documentation]    INFUND-4725, IFS-1104
    Given the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    Then The user should see the element    link=Funding information
    And The user should see the element    link=Eligibility
    And The user should see the element    link=Milestones
    And The user should see the element    link=Application
    And The user should see the element    link=Assessors
    And The user should see the element    link=Public content
    And The user should see the element    link=Stakeholders

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
    Given the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    And The user clicks the button/link   link=All competitions
    And the user navigates to the page    ${CA_UpcomingComp}
    Then the user should see the element  jQuery=section:contains("In preparation") li:contains("${competitionTitle}")

Funding information: calculations
    [Documentation]    INFUND-2985
    ...
    ...    INFUND-4894
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    Given the user clicks the button/link    link=Funding information
    And the user clicks the button/link    jQuery=.button:contains("Generate code")
    And the user enters text to a text field    id=funders[0].funder    FunderName
    And the user enters text to a text field    id=funders[0].funderBudget    20000
    And the user enters text to a text field    id=pafNumber    2016
    And the user enters text to a text field    id=budgetCode    2004
    And the user enters text to a text field    id=activityCode    4242
    When the user clicks the button/link    jQuery=Button:contains("+Add co-funder")
    and the user should see the element    jQuery=Button:contains("+Add co-funder")
    And the user should see the element    jQuery=Button:contains("Remove")
    And the user enters text to a text field    id=funders[1].funder    FunderName2
    And the user enters text to a text field    id=funders[1].funderBudget    1000
    Then the total should be correct    £21,000
    When the user clicks the button/link    jQuery=Button:contains("Remove")
    Then the total should be correct    £20,000

Funding information: can be saved
    [Documentation]    INFUND-3182
    [Tags]    HappyPath
    Given the user moves focus and waits for autosave
    When the user clicks the button/link    jQuery=button:contains("Done")
    Then the user should see the text in the page    FunderName
    And the user should see the text in the page    £20,000
    And the user should see the text in the page    2016
    And the user should see the text in the page    2004
    And the user should see the text in the page    4242
    And the user should see the text in the page    1812-1
    And the user should see the element    jQuery=button:contains("Edit")

Funding information: can be edited
    [Documentation]    INFUND-3002
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Edit")
    And the user enters text to a text field    id=funders[0].funder    testFunder
    And the user moves focus and waits for autosave
    When the user clicks the button/link    jQuery=button:contains("Done")
    Then the user should see the text in the page    testFunder

Funding information: should have a green check
    [Documentation]    INFUND-3002
    [Tags]    HappyPath
    When The user clicks the button/link    link=Competition setup
    Then the user should see the element    css=li:nth-child(2) .task-status-complete
    And the user should see the element     css=#compCTA[disabled]

Eligibility: Contain the correct options
    [Documentation]  INFUND-2989 INFUND-2990 INFUND-9225
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    Given the user clicks the button/link    link=Eligibility
    And the user should see the text in the page    Please choose the project type.
    Then the user should see the element    jQuery=label:contains("Single or Collaborative")
    When the user should see the element    jQuery=label:contains("Collaborative")
    And the user should see the element    jQuery=label:contains("Business")
    And the user should see the element    jQuery=label[for="lead-applicant-type-2"]:contains("Research")
    And the user should see the element    jQuery=label:contains("Research and technology organisation")
    And the user should see the element    jQuery=label:contains("Public sector")
    And the user should see the element    jQuery=div:nth-child(7) label:contains("Yes")
    And the user should see the element    jQuery=div:nth-child(7) label:contains("No")
    And the user should see the element    jQuery=label:contains("Feasibility studies")
    And the user should see the element    jQuery=label:contains("Industrial research")
    And the user should see the element    jQuery=label:contains("Experimental development")
    And the resubmission should not have a default selection

Eligibility: Mark as Done then Edit again
    [Documentation]    INFUND-3051 INFUND-3872 INFUND-3002 INFUND-9225
    [Tags]    HappyPath
    Given the user selects the checkbox    research-categories-33
    And the user selects the checkbox    research-categories-34
    And the user selects the checkbox    research-categories-35
    And the user selects the radio button    singleOrCollaborative    single
    And the user selects the checkbox   lead-applicant-type-1  # business
    And the user selects the checkbox   lead-applicant-type-3  # RTOs
    And the user selects the option from the drop-down menu    50%    name=researchParticipationAmountId
    And the user moves focus and waits for autosave
    And the user selects the radio button    resubmission    no
    When the user clicks the button/link    jQuery=button:contains("Done")
    Then the user should see the text in the page    Yes
    And the user should see the text in the page    Single
    And the user should see the text in the page    Business
    And the user should see the text in the page    50%
    And the user should see the text in the page    Feasibility studies
    And the user should see the text in the page    Industrial research
    And the user should see the text in the page    Experimental development
    And The user should not see the element    id=streamName
    When the user clicks the button/link    link=Competition setup
    When the user clicks the button/link    link=Eligibility
    And the user clicks the button/link    jQuery=.button:contains("Edit")
    And the user clicks the button/link    jQuery=button:contains("Done")

Eligibility: Should have a Green Check
    [Documentation]    INFUND-3002
    [Tags]    HappyPath
    When The user clicks the button/link    link=Competition setup
    Then the user should see the element    css=li:nth-child(3) .task-status-complete
    And the user should see the element     css=#compCTA[disabled]

Milestones: Page should contain the correct fields
    [Documentation]    INFUND-2993
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
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
    [Tags]    HappyPath
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
    [Tags]    HappyPath
    [Setup]  The user navigates to the page  ${COMP_MANAGEMENT_COMP_SETUP}
    #Writing the following selectors using jQuery in order to avoidhardcoded numbers.
    When The user clicks the button/link  link=Application
    Then the user should see the element  jQuery= h2:contains("Sector competition questions")
    When the user should see the element  link=Application details
    Then the user should see the element  link=Project summary
    And the user should see the element   link=Public description
    And the user should see the element   link=Scope
    When the user should see the element  jQuery=a:contains("Need or challenge")
    Then the user should see the element  jQuery=a:contains("Approach and innovation")
    And the user should see the element   jQuery=a:contains("Team and resources")
    And the user should see the element   jQuery=a:contains("Market awareness")
    And the user should see the element   jQuery=a:contains("Outcomes and route to market")
    And the user should see the element   jQuery=a:contains("Wider impacts")
    And the user should see the element   jQuery=a:contains("Project management")
    And the user should see the element   jQuery=a:contains("Risks")
    And the user should see the element   jQuery=a:contains("Additionality")
    And the user should see the element   jQuery=a:contains("Costs and value for money")
    And the user should see the element   jQuery=.buttonlink:contains("Add question")
    And the user should see the element   link=Finances

Application: Need or challenge
    [Documentation]    INFUND-5632 INFUND-5685 INFUND-5630 INFUND-6283
    [Tags]
    When the user clicks the button/link    jQuery=h4 a:contains("Need or challenge")
    Then the user should see the element    jQuery=dt:contains("Question heading") + dd:contains("Need or challenge")
    When the user clicks the button/link    link=Edit this question
    And the user edits the assessed question information
    And The user clicks the button/link     css=.button[value="Save and close"]
    When the user clicks the button/link    jQuery=h4 a:contains("Need or challenge")
    Then the user sees the correct read only view of the question
    When the user clicks the button/link    link=Edit this question
    And the user selects the radio button   question.writtenFeedback  0
    And the user selects the radio button   question.scored  0
    And the user should not be able to edit the assessed question feedback
    And the user clicks the button/link     jQuery=.button[value="Save and close"]
    And the user clicks the button/link     jQuery=h4 a:contains("Need or challenge")
    Then the user should not see the assessed question feedback
    [Teardown]  The user clicks the button/link  link=Application

Application: Application details
    [Documentation]    INFUND-5633
    Given the user clicks the button/link    link=Application details
    And the user should see the element    jQuery=h1:contains("Application details")
    And the user should see the text in the page    These are the default questions included in the application details section.
    When the user clicks the button/link    link=Edit this question
    And the user selects the radio button    useResubmissionQuestion    false
    And The user clicks the button/link    jQuery=button:contains("Save and close")
    And the user clicks the button/link    link=Application details
    Then The user should see the text in the page    Application details
    And the user should see the text in the page    No
    [Teardown]    The user clicks the button/link    link=Application

Application: Scope
    [Documentation]  INFUND-5634 INFUND-5635
    Given the user clicks the button/link         link=Scope
    Then the user should see the element          jQuery=h1:contains("Scope")
    And the user should see the text in the page  You can edit this question for the applicant as well as the guidance for assessors.
    When the user clicks the button/link    link=Edit this question
    And The user fills the empty question fields
    And The user clicks the button/link    jQuery=.button[value="Save and close"]
    And the user clicks the button/link    link=Scope
    Then The user should see the text in the page    Scope
    And the user checks the question fields

Application: Scope Assessment questions
    [Documentation]    INFUND-5631    INFUND-6044  INFUND-6283
    Given the user clicks the button/link    link=Edit this question
    And the user selects the radio button    question.writtenFeedback    1
    And the user fills the scope assessment questions
    When the user clicks the button/link    jQuery=.button[value="Save and close"]
    And the user clicks the button/link    link=Scope
    Then the user checks the scope assessment questions
    And the user clicks the button/link    link=Edit this question
    And the user selects the radio button    question.writtenFeedback    0
    And the user should not be able to edit the scope feedback
    And the user clicks the button/link    jQuery=.button[value="Save and close"]
    And the user clicks the button/link    link=Scope
    Then the user should not see the scope feedback
    [Teardown]    The user clicks the button/link    link=Application

Application: Project Summary
    [Documentation]  INFUND-5636 INFUND-5637
    Given the user clicks the button/link    link=Project summary
    And the user should see the element      jQuery=h1:contains("Project summary")
    And the user should see the text in the page    You can edit this question for the applicant as well as the guidance for assessors.
    When the user clicks the button/link    link=Edit this question
    And The user fills the empty question fields
    And The user clicks the button/link    css=.button[value="Save and close"]
    And the user clicks the button/link    link=Project summary
    Then The user should see the text in the page    Project summary
    And the user checks the question fields
    [Teardown]    The user clicks the button/link    link=Application


Adding a new Assessed Application Question
    [Documentation]  IFS-182
    [Tags]
    Given the user clicks the button/link  css=p button[type="submit"]  #Add question link
    When the user clicks the button/link   css=input[type="submit"]  #Save and close
    Then the user should the server side validation working
    When the user is able to configure the new question
    Then the user should be able to see the read only view of question correctly

Removing an Assessed Application Question
    [Documentation]  IFS-182
    [Tags]
    Given the user clicks the button/link     jQuery=a:contains("Costs and value for money")
    When the user clicks the button/link      css=button[name="deleteQuestion"]
    Then the user should not see the element  jQuery=a:contains("Costs and value for money")
    When the user clicks the button/link      jQuery=li:contains("Additionality") button:contains("Remove")
    Then the user should not see the element  jQuery=a:contains("Additionality")

Application: Finances
    [Documentation]    INFUND-5640, INFUND-6039, INFUND-6773
    [Tags]  HappyPath
    Given the user clicks the button/link    link=Finances
    Then the user should see the element     jQuery=h1:contains("Application finances")
    And the user should see the element      jQuery=.panel:contains("The competition template will select the following finance sections for each partner.")
    When the user clicks the button/link     link=Edit this question
    Then the user should see the element     css=input:checked ~ label[for="full-application-finance-yes"]
    And the user should see the element      css=label[for="full-application-finance-no"]
    # Please note that the above radio button is not clickable at the moment. Not part of the MVP. Is included for future functionality purpose.
    When the user selects the radio button   includeGrowthTable  include-growth-table-no
    And the user enters text to a text field  css=.editor  Funding rules for this competition are now entered.
    And The user clicks the button/link      css=button[type="submit"]  #Save and close
    When the user clicks the button/link     link=Finances
    Then the user should see the element     jQuery=dt:contains("Include project growth table") ~ dd:contains("No")
    And the user should see the element      jQuery=dt:contains("Funding rules for this competition") ~ dd:contains("Funding rules for this competition are now entered.")
    [Teardown]  the user clicks the button/link  link=Return to application questions

Application: Mark as done should display green tick
    [Documentation]    INFUND-5964
    [Tags]
    Given The user clicks the button/link     css=button.button  #Done button
    Then The user should not see the element  css=button.button
    When The user clicks the button/link      link=Return to setup overview
    Then the user should see the element      jQuery=li:contains("Application") .task-status-complete

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
    Then the user clicks the button/link  jQuery=.button:contains("Done")
    When the user navigates to the page   ${CA_UpcomingComp}
    Then the user should see the element  jQuery=section:contains("Ready to open") li:contains("${competitionTitle}")

Requesting the id of this Competition
    [Documentation]  retriving the id of the competition so that we can use it in urls
    [Tags]  MySQL
    ${competitionId} =  get comp id from comp title  ${competitionTitle}
    Set suite variable  ${competitionId}

Ready To Open button is visible when the user re-opens a section
    [Documentation]    INFUND-4468
    [Tags]
    [Setup]  the user navigates to the page  ${server}/management/competition/setup/${competitionId}
    When The user clicks the button/link     link=Initial details
    And the user clicks the button/link      jQuery=.button:contains("Edit")
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

User should be able to Save the Competition as Open
    [Documentation]    INFUND-4468, INFUND-3002
    [Tags]
    [Setup]  the user navigates to the page  ${server}/management/competition/setup/${competitionId}
    When the user clicks the button/link     css=#compCTA
    Then the user clicks the button/link     jQuery=.button:contains("Done")
    When the user clicks the button/link   link=All competitions
    And the user navigates to the page     ${CA_UpcomingComp}
    Then the user should see the element   jQuery=section:contains("Ready to open") li:contains("${competitionTitle}")

Assessor: Contain the correct options
    [Documentation]    INFUND-5641
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    Given the user clicks the button/link    link=Assessors
    And the user should see the text in the page    How many assessors are required for each application?
    Then the user should see the element    jQuery=label:contains(1)
    When the user should see the element    jQuery=label:contains(3)
    And the user should see the element    jQuery=label:contains(5)
    And the user should see the text in the page    How much do assessors receive per application
    And the user should see the element    id=assessorPay

Assessor: Mark as Done then Edit again
     [Documentation]    INFUND-5641 IFS-380
     [Tags]    HappyPath
    When the user selects the checkbox         assessors-62
    Then the user enters text to a text field  id=assessorPay  100
    When the user clicks the button/link       jQuery=button:contains("Done")
    Then the user should see the element       jQuery=dt:contains("How many assessors") + dd:contains("5")
    And the user should see the element        jQuery=dt:contains("How much do assessors receive") + dd:contains("100")
    And the user should see the element        jQuery=dt:contains("assessment panel") + dd:contains("No")
    And the user should see the element        jQuery=dt:contains("interview stage") + dd:contains("No")
    When the user clicks the button/link       jQuery=.button:contains("Edit")
    Then the user selects the radio button     hasInterviewStage  hasInterviewStage-0
    When the user clicks the button/link       jQuery=button:contains("Done")
    Then the user should see the element       jQuery=dt:contains("interview stage") + dd:contains("Yes")

Assessor: Should have a Green Check
    [Documentation]  INFUND-5641
    [Tags]  HappyPath
    When The user clicks the button/link    link=Competition setup
    Then the user should see the element    jQuery=li:contains("Assessors") .task-status-complete
    And the user clicks the button/link     css=#compCTA
    And the user clicks the button/link     jQuery=.button:contains("Done")
    When the user navigates to the page     ${CA_UpcomingComp}
    Then the user should see the element    jQuery=section:contains("Ready to open") li:contains("${competitionTitle}")

Innovation leads can be added to a competition
    [Documentation]    IFS-192, IFS-1104
    [Tags]  HappyPath
    When the user navigates to the page       ${COMP_MANAGEMENT_COMP_SETUP}/manage-innovation-leads/find
    Then the user should see the element      jQuery=h1:contains("Manage innovation leads")
    And the user should see the element       jQuery=span.lead-count:contains("0")  # Lead count from key statistics
    And the user should see the element       jQuery=.standard-definition-list dd:contains("Open") ~ dd:contains("Biosciences") ~ dd:contains("Ian Cooper") ~ dd:contains("John Doe")
    And the user should see the element       jQuery=li.selected a:contains("Find")
    When the user clicks the button/link      jQuery=td:contains(${peter_freeman}) button:contains("Add")
    Then the user should not see the element  jQuery=td:contains(${peter_freeman})
    And the user should not see the element   jQuery=td:contains("Ian Cooper")
    And the user should see the element       jQuery=span.lead-count:contains("1")
    When the user clicks the button/link      jQuery=a:contains("Overview")
    Then the user should see the element      jQuery=span.total-count:contains("1")
    And the user should not see the element   jQuery=td:contains("Ian Cooper")
    And the user clicks the button/link       jQuery=td:contains(${peter_freeman}) button:contains("Remove")
    And the user should see the element       jQuery=span.lead-count:contains("0")
    And the user should see the element       jQuery=span.total-count:contains("0")
    When the user clicks the button/link      jQuery=.inline-nav a:contains("Find")
    Then the user should see the element      jQuery=td:contains(${peter_freeman}) button:contains("Add")

The Applicant is able to apply to the competition once is Open and see the correct Questions
    [Documentation]  IFS-182
    [Tags]  HappyPath  MySQL
    [Setup]  the competition moves to Open state
    Given log in as a different user           &{lead_applicant_credentials}
    And logged in user applies to competition  ${competitionTitle}
    Then the user should see the element       jQuery=li:contains("Tell us how your project is innovative.")
    And the user should not see the element    jQuery=li:contains("Costs and value for money")
    And the user should not see the element    jQuery=li:contains("Additionality")

*** Keywords ***
the user moves focus and waits for autosave
    focus    link=Sign out
    Wait For Autosave

the total should be correct
    [Arguments]    ${Total}
    mouse out    css=input
    Focus    jQuery=button:contains("Done")
    Wait Until Element Contains Without Screenshots    css=p.no-margin    ${Total}

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
    element should contain    css=tr:nth-child(1) td:nth-child(3)    Thu
    element should contain    css=tr:nth-child(2) td:nth-child(3)    Fri
    element should contain    css=tr:nth-child(3) td:nth-child(3)    Sat
    element should contain    css=tr:nth-child(4) td:nth-child(3)    Sun
    element should contain    css=tr:nth-child(5) td:nth-child(3)    Mon
    element should contain    css=tr:nth-child(6) td:nth-child(3)    Tue
    element should contain    css=tr:nth-child(7) td:nth-child(3)    Wed
    element should contain    css=tr:nth-child(8) td:nth-child(3)    Thu
    element should contain    css=tr:nth-child(9) td:nth-child(3)    Fri
    element should contain    css=tr:nth-child(10) td:nth-child(3)    Sat
    element should contain    css=tr:nth-child(11) td:nth-child(3)    Sun
    element should contain    css=tr:nth-child(12) td:nth-child(3)    Mon
    element should contain    css=tr:nth-child(13) td:nth-child(3)    Tue

the pre-field date should be correct
    Element Should Contain    css=#milestone-OPEN_DATE~ .js-addWeekDay    Sat
    ${YEAR} =    Get Value    css=.date-group:nth-child(1) .year .width-small
    Should Be Equal As Strings    ${YEAR}  ${nextyear}
    ${MONTH} =    Get Value    css=.date-group:nth-child(1) .month .width-small
    Should Be Equal As Strings    ${MONTH}    12
    ${DAY} =    Get Value    css=.date-group:nth-child(1) .day .width-small
    Should Be Equal As Strings    ${DAY}    1

the resubmission should not have a default selection
    the user should see the element  css=[name="resubmission"]:not(:checked) ~ label

The user enters valid data in the initial details
    Given the user enters text to a text field                css=#title  Competition title
    When the user selects the option from the drop-down menu  Sector  id=competitionTypeId
    And the user selects the option from the drop-down menu   Infrastructure systems  id=innovationSectorCategoryId
    And the user selects the option from the drop-down menu   Offshore wind  css=[id="innovationAreaCategoryIds[0]"]
    And the user selects the option from the drop-down menu   Open  id=innovationSectorCategoryId
    And the user selects the option from the drop-down menu   Biosciences     css=[id="innovationAreaCategoryIds[0]"]
    And the user selects the option from the drop-down menu   Emerging and enabling  id=innovationSectorCategoryId
    And the user selects the option from the drop-down menu   Satellite applications  css=[id="innovationAreaCategoryIds[0]"]
    And the user selects the option from the drop-down menu   Space technology  css=[id="innovationAreaCategoryIds[1]"]
    And the user enters text to a text field    id=openingDateDay    01
    And the user enters text to a text field    Id=openingDateMonth    12
    And the user enters text to a text field    id=openingDateYear  ${nextyear}
    And the user selects the option from the drop-down menu    Ian Cooper    id=innovationLeadUserId
    And the user selects the option from the drop-down menu    John Doe   id=executiveUserId

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

the user should not see the assessed question feedback
    the user should not see the text in the page    Out of
    the user should not see the text in the page    Guidance for assessing business opportunity
    the user should not see the text in the page    Your score should be based upon the following:
    the user should not see the text in the page    There is little or no business drive to the project.

Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}

the user enters multiple innovation areas
    the user clicks the button/link    jQuery=.buttonlink:contains("+ add another innovation area")
    the user selects the option from the drop-down menu    Space technology    css=[id="innovationAreaCategoryIds[1]"]
    the user clicks the button/link    jQuery=.buttonlink:contains("+ add another innovation area")
    List Should not Contain Value    css=[id="innovationAreaCategoryIds[2]"]    Space technology
    the user selects the option from the drop-down menu    Creative industries    css=[id="innovationAreaCategoryIds[2]"]

The user should not see the selected option again
    List Should not Contain Value    css=[id="innovationAreaCategoryIds[1]"]    Biosciences

the user should the server side validation working
    #TODO Amend the following to cover error-summary. Cover radio buttons as well - IFS-?
    the user should see a field error  This field cannot be left blank.
    the user should see a field error  Please enter a justification.

the user is able to configure the new question
    the user enters text to a text field  id=question.title  Please provide us with more inforrmation on how your project is different from pre-existing projects.
    the user enters text to a text field  id=question.shortTitle  Tell us how your project is innovative.
    the user enters text to a text field  id=question.subTitle  Adding value on existing projects is important to InnovateUK.
    the user enters text to a text field  id=question.guidanceTitle  Innovation is crucial to the continuing success of any organization.
    the user enters text to a text field  css=.editor  Please use Microsoft Word where possible. If you complete your application using Google Docs or any other open source software, this can be incompatible with the application form.
    the user enters text to a text field  id=question.maxWords  500
    the user selects the radio button     question.appendix  1
    the user selects the radio button     question.scored  1
    the user enters text to a text field  question.scoreTotal  10
    the user selects the radio button     question.writtenFeedback  1
    the user enters text to a text field  question.assessmentGuidanceTitle  Please bare in mind on how well the applicant is able to justify his arguments.
    the user enters text to a text field  question.assessmentGuidance   The better you understand the problem the simpler your explanation is.
    the user enters text to a text field  guidanceRows[0].justification  This the 9-10 Justification
    the user enters text to a text field  guidanceRows[1].justification  This the 7-8 Justification
    the user enters text to a text field  guidanceRows[2].justification  This the 5-6 Justification
    the user enters text to a text field  guidanceRows[3].justification  This the 3-4 Justification
    the user enters text to a text field  guidanceRows[4].justification  This the 1-2 Justification
    the user enters text to a text field  question.assessmentMaxWords  120
    the user clicks the button/link       css=input[type="submit"]

the user should be able to see the read only view of question correctly
    the user clicks the button/link  jQuery=a:contains("Tell us how your project is innovative.")
    the user should see the element  jQuery=dt:contains("Question heading") + dd:contains("Tell us how your project is innovative")
    the user should see the element  jQuery=dt:contains("Question title") + dd:contains("Please provide us with more inforrmation on how your project is different from pre-existing projects.")
    the user should see the element  jQuery=dt:contains("Question subtitle") + dd:contains("Adding value on existing projects is important to InnovateUK.")
    the user should see the element  jQuery=dt:contains("Guidance title") + dd:contains("Innovation is crucial to the continuing success of any organization.")
    the user should see the element  jQuery=dt:contains("Guidance") + dd:contains("Please use Microsoft Word where possible.")
    the user should see the element  jQuery=dt:contains("Max word count") + dd:contains("500")
    the user should see the element  jQuery=dt:contains("Appendix") + dd:contains("Yes")
    the user should see the element  jQuery=dt:contains("Scored") + dd:contains("Yes")
    the user should see the element  jQuery=dt:contains("Out of") + dd:contains("10")
    the user should see the element  jQuery=dt:contains("Written feedback") + dd:contains("Yes")
    the user should see the element  jQuery=dt:contains("Guidance title") + dd:contains("Please bare in mind on how well the applicant is able to justify his arguments.")
    the user should see the element  jQuery=dt:contains("Guidance") + dd:contains("The better you understand the problem the simpler your explanation is.")
    the user should see the element  jQuery=dt:contains("9-10") + dd:contains("This the 9-10 Justification")
    the user should see the element  jQuery=dt:contains("7-8") + dd:contains("This the 7-8 Justification")
    the user should see the element  jQuery=dt:contains("5-6") + dd:contains("This the 5-6 Justification")
    the user should see the element  jQuery=dt:contains("3-4") + dd:contains("This the 3-4 Justification")
    the user should see the element  jQuery=dt:contains("1-2") + dd:contains("This the 1-2 Justification")
    the user should see the element  jQuery=dt:contains("Max word count") + dd:contains("120")
    the user clicks the button/link  link=Return to application questions

the competition moves to Open state
    ${yesterday} =  get yesterday
    Connect to Database  @{database}
    execute sql string  UPDATE `${database_name}`.`milestone` SET `date`='${yesterday}' WHERE `competition_id`='${competitionId}' AND `type`='OPEN_DATE';
