*** Settings ***
Documentation     INFUND-6661 As a Competitions team member I want to be able to update Initial details throughout the life of the competition
...
...               INFUND-6937 As a Competitions team member I want to be able to view Application details throughout the life of the competition
...
...               INFUND-6938 As a Competitions team member I want to be able to view Project summary throughout the life of the competition
...
...               INFUND-6939 As a Competitions team member I want to be able to view Public description throughout the life of the competition
...
...               INFUND-6940 As a Competitions team member I want to be able to view Scope throughout the life of the competition
...
...               INFUND-6941 As a Competitions team member I want to be able to view Finances throughout the life of the competition
...
...               INFUND-6792 As a Competitions team member I want to be able to view Eligibility throughout the life of the competition
...
...               INFUND-7083 As a Competitions team member I want to be able to update PAF number, budget and activity codes throughout the life of the competition
...
...               INFUND-6695 As a Competitions team member I want to be able to update the number of Assessors required per applicationthroughout the life of the competition
...
...               INFUND-6694 As a Competitions team member I want to be able to update Milestones throughout the life of the competition
...
...               IFS-4982 Move Funding type selection from front door to Initial details
...
...               IFS-7195  Organisational eligibility category in Competition setup
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot

# ${ready_to_open_competition_name} is the 'Photonics for health'

*** Test Cases ***
Application details are editable (Ready to Open)
    [Documentation]    INFUND-6937
    [Tags]
    Given the user clicks the button/link    jQuery = a:contains(Upcoming)
    And the user clicks the button/link      link = ${ready_to_open_competition_name}
    And the user clicks the button/link      jQuery = a:contains(and update competition setup)
    And the user clicks the button/link      link = Application
    When the user clicks the button/link     link = Application details
    And the user should see the element      jQuery = .govuk-button:contains("Edit this question")
    And the user clicks the button/link      jQuery = .govuk-button:contains("Edit this question")
    Then the user is able to change the value of the fields
    [Teardown]    the user clicks the button/link    link = Application

Project summary is editable (Ready to Open)
    [Documentation]    INFUND-6938
    [Tags]
    When The user clicks the button/link         link = Project summary
    And the user should see the element          jquery = h1:contains("Project summary")
    When the user clicks the button/link         jQuery = .govuk-button:contains("Edit this question")
    Then The user enters text to a text field    id = question.maxWords    100
    And the user clicks the button/link          jQuery=button:contains("Done")

Public description should be editable (Ready to Open)
    [Documentation]    INFUND-6939
    [Tags]
    Given the user clicks the button/link        link = Public description
    When the user clicks the button/link         jQuery = .govuk-button:contains("Edit this question")
    Then The user enters text to a text field    id = question.maxWords    100
    And the user clicks the button/link          jQuery = button:contains("Done")

Scope is editable (Ready to Open)
    [Documentation]    INFUND-6940
    [Tags]
    Given The user clicks the button/link        link = Scope
    When the user clicks the button/link         jQuery = .govuk-button:contains("Edit this question")
    Then The user enters text to a text field    id = question.maxWords    100
    And the user clicks the button/link          jQuery = button:contains("Done")

Assessed Questions are editable (Ready to Open)
    [Documentation]    INFUND-6936
    [Tags]
    When the user clicks the button/link    jQuery = a:contains("Business opportunity")
    Then the user should see the element    jQuery = h1:contains("Business opportunity")
    And the user clicks the button/link     jQuery = .govuk-button:contains("Edit this question")
    And the user edits the assessed question information
    And the user clicks the button/link     jQuery = button:contains("Done")
    When the user clicks the button/link    jQuery = a:contains("Business opportunity")
    Then the user sees the correct read only view of the question
    And the user clicks the button/link     link = Return to application questions

Finances are editable (Ready to Open)
    [Documentation]  INFUND-6941
    [Tags]
    Given the user navigates to the page         ${server}/management/competition/setup/${READY_TO_OPEN_COMPETITION}/section/application/landing-page
    When The user clicks the button/link         link = Finances
    And the user should see the element          jQuery = h1:contains("Finances")
    When the user clicks the button/link         jQuery = a:contains("Edit this question")
    Then if textarea is empty the proper validation messages are shown
    And the user clicks the button/link          jQuery = button:contains("Done")
    [Teardown]  the user clicks the button/link  link = Competition setup

Project eligibility is editable (Ready to Open)
    [Documentation]    INFUND-6792
    [Tags]
    When the user clicks the button/link    link = Project eligibility
    Then the user should see the element    jquery = h1:contains("Project eligibility")
    And The user clicks the button/link     jQuery = button:contains(Edit)
    And the user selects the radio button   singleOrCollaborative  single
    And The user clicks the button/link     jQuery = button:contains(Done)

Organisational eligibility is editable (Ready to Open)
    [Documentation]     IFS-7195 IFS-7246
    [Tags]
    Given the user clicks the button/link                    link = Return to setup overview
    And the user clicks the button/link                      link = ${organisationalEligibilityTitle}
    And the user clicks the button/link                      jQuery = button:contains("Edit")
    When the user selects the radio button                   internationalOrganisationsApplicable       false
    And the user clicks the button/link                      jQuery = button:contains("Save and continue")
    And the user clicks the button/link                      link = Competition setup
    Then the user should see the element                     jQuery = li:contains("Organisational eligibility") .task-status-complete

Funding Information is editable (Open)
    [Documentation]    INFUND-7083
    [Tags]
    [Setup]    The user clicks the button/link  jQuery = a:contains(Dashboard)
    Given the user clicks the button/link       link = ${openCompetitionBusinessRTO_name}
    And the user clicks the button/link         link = View and update competition setup
    When the user clicks the button/link        link = Funding information
    And the user should see the element         jquery = h1:contains("Funding information")
    And the user clicks the button/link         jQuery = .govuk-button:contains("Edit")
    And the user edits autocomplete field       id = funders[0].funder    Aerospace Technology Institute (ATI)
    And the user should see the element         id = funders[0].funderBudget
    And the user should see the element         id = pafNumber
    And the user should see the element         id = budgetCode
    And the user should see the element         id = activityCode
    And the user clicks the button/link         jQuery = button:contains("Done")
    Then The user should see the element        jQuery = button:contains("Edit")
    And The user should see the element         jQuery = td:contains("Aerospace Technology Institute (ATI)")
    [Teardown]    the user clicks the button/link  link = Competition setup

Milestones are editable (Open)
    [Documentation]    INFUND-6694
    [Tags]
    When the user clicks the button/link                     link = Milestones
    And the user clicks the button/link                      jQuery = a:contains("Next")
    And the user clicks the button/link                      jQuery = button:contains(Edit)
    Then the user should see that the element is disabled    css = tr:nth-child(1) .year input
    And the user should see that the element is disabled     css = tr:nth-child(2) .year input
    And the user fills in the milestone data with valid information
    And the user clicks the button/link                      jQuery = button:contains(Done)
    And the user clicks the button/link                      jQuery = .govuk-button:contains("Edit")
    And the user resets the milestone data
    And the user clicks the button/link                      jQuery = button:contains(Done)
    [Teardown]    the user clicks the button/link            link = Competition setup

Application details are not editable (Open)
    [Documentation]    INFUND-6937
    [Tags]
    When the user clicks the button/link             link = Application
    And the user clicks the button/link              link = Application details
    Then the user should not see the element         jQuery = .govuk-button:contains("Edit this question")
    [Teardown]    The user clicks the button/link    link = Application

Assessed Questions are not editable (Open)
    [Documentation]    INFUND-6936
    [Tags]
    When the user clicks the button/link             jQuery = a:contains("Business opportunity")
    And the user should see the element              jquery = h1:contains("Business opportunity")
    Then the user should not see the element         jquery = .govuk-button:contains("Edit")
    [Teardown]    The user clicks the button/link    link = Application

Project eligibility is not editable (Open)
    [Documentation]    INFUND-6792
    [Tags]
    [Setup]    The user clicks the button/link       link = Competition setup
    When The user clicks the button/link             link = Project eligibility
    And the user should see the element              jquery = h1:contains("Project eligibility")
    Then The user should not see the element         css = input
    And The user should not see the element          jquery = .govuk-button:contains("Edit")
    And The user should not see the element          jquery = button:contains("Done")
    [Teardown]    The user clicks the button/link    link = Return to setup overview

Public Description is not editable (Open)
    [Documentation]    INFUND-6939
    [Tags]
    Given The user clicks the button/link            link = Application
    When The user clicks the button/link             link = Public description
    And the user should see the element              jquery = h1:contains("Public description")
    Then The user should not see the element         css = input
    And The user should not see the element          jQuery = .govuk-button:contains("Edit this question")
    And The user should not see the element          jQuery = .govuk-button[value="Done"]
    [Teardown]    The user clicks the button/link    link = Return to application questions

Project Summary is not editable (Open)
    [Documentation]    INFUND-6938
    [Tags]
    When The user clicks the button/link             link = Project summary
    And the user should see the element              jQuery = h1:contains("Project summary")
    Then The user should not see the element         css = input
    And The user should not see the element          jQuery = .govuk-button:contains("Edit this question")
    And The user should not see the element          jQuery = .govuk-button[value="Done"]
    [Teardown]    The user clicks the button/link    link = Return to application questions

Scope is not editable (Open)
    [Documentation]    INFUND-6940
    [Tags]
    When The user clicks the button/link             link = Scope
    Then the user should see the element             jQuery = h1:contains("Scope")
    And The user should not see the element          css = input
    And The user should not see the element          jQuery = .govuk-button:contains("Edit this question")
    And The user should not see the element          jQuery = .govuk-button[value="Done"]
    [Teardown]    The user clicks the button/link    link = Return to application questions

Finances not editable (Open)
    [Documentation]    INFUND-6941
    [Tags]
    When The user clicks the button/link             link = Finances
    And the user should see the element              jquery = h1:contains("Finances")
    Then The user should not see the element         css = input
    And The user should not see the element          jquery = .govuk-button:contains("Edit")
    And The user should not see the element          jquery = button:contains("Done")
    [Teardown]    The user clicks the button/link    link = Return to application questions

Initial details editable before notify date (Open)
    [Documentation]    INFUND-6661  IFS-4982
    [Setup]    the user clicks the button/link              link = Competition setup
    Given the user clicks the button/link                   link = Initial details
    And the user clicks the button/link                     jQuery = .govuk-button:contains("Edit")
    And the user should see the element                     jQuery = dt:contains("Funding type") ~ dd:contains("Grant")
    And the user should see the element                     jQuery = dt:contains("Competition type") ~ dd:contains("Programme")
    And the user should see the element                     jQuery = dt:contains("Opening date") ~ dd:contains("${openCompetitionBusinessRTOOpenDate}")
    And the user should see that the element is disabled    id = innovationSectorCategoryId
    And the user should see that the element is disabled    name = innovationAreaCategoryIds[0]
    When the user selects the option from the drop-down menu    Ian Cooper    id = innovationLeadUserId
    And the user selects the option from the drop-down menu     John Doe    id = executiveUserId
    And the user clicks the button/link                     jQuery = button:contains("Done")
    Then the user should see the element                    jQuery = .govuk-button:contains("Edit")
    And The user should see the element                     jQuery = dt:contains("Competition Lead") ~ dd:contains("Ian Cooper")
    And The user should see the element                     jQuery = dt:contains("Portfolio Manager") ~ dd:contains("John Doe")
    [Teardown]    the user clicks the button/link           link = Competition setup

Assessors editable before Notifications Date (Open)
    [Documentation]  INFUND-6695 IFS-380
    [Tags]
    Given the user clicks the button/link     link = Assessors
    Then the user should see the element      jQuery = .govuk-button:contains("Edit")
    And the user should see the element       jQuery = dt:contains("How many assessors") + dd:contains("1")
    When the user clicks the button/link      jQuery = .govuk-button:contains("Edit")
    Then the user selects the radio button    assessorCount    5
    And the user selects the radio button     hasAssessmentPanel    0
    And the user selects the radio button     hasInterviewStage    0
    And the user selects the radio button     averageAssessorScore    0
    And the user should see the element       css = #assessorPay[readonly="readonly"]
    When the user clicks the button/link      jQuery = button:contains("Done")
    And the user should see the element       jQuery = dt:contains("How many assessors") + dd:contains("5")
    And the user should see the element       jQuery = .govuk-button:contains("Edit")
    [Teardown]    return the database to its previous status

Initial details not editable after notify date (Open)
    [Documentation]    INFUND-6661
    [Setup]    the user navigates to the page        ${COMP_MANAGEMENT_NOT_EDITABLE_COMP}
    Given the user clicks the button/link            link = Initial details
    Then the user should not see the element         jQuery = .govuk-button:contains("Edit")
    And the user should not see the element          jQuery = button:contains("Done")
    [Teardown]    the user clicks the button/link    link = Competition setup

Funding Information not editable after notifications date (Open)
    [Documentation]    INFUND-7183
    [Tags]
    When The user clicks the button/link             link = Funding information
    And the user should see the element              jQuery = h1:contains("Funding information")
    Then The user should not see the element         css = input
    And The user should not see the element          jQuery = .govuk-button:contains("Edit")
    And The user should not see the element          jQuery = button:contains("Done")
    [Teardown]    the user clicks the button/link    link = Competition setup

Assessors not editable after Notifications Date (Open)
    [Documentation]    INFUND-6695
    [Tags]
    When the user clicks the button/link        link = Assessors
    Then the user should not see the element    jQuery = .govuk-button:contains("Edit")
    And the user should not see the element     jQuery = button:contains("Done")

Organisational eligibility is not editable (Open)
     [Documentation]  IFS-7195
     [Tags]
     Given the user clicks the button/link            link = Return to setup overview
     When the user clicks the button/link             link = ${organisationalEligibilityTitle}
     Then the user should not see the element         jQuery = button:contains("Edit")

*** Keywords ***
the user can see the open date of the competition belongs to the future
    the user should see the element    jQuery = h2:contains('Ready to open') ~ ul a:contains('${READY_TO_OPEN_COMPETITION_NAME}')
    the user should see the element    jQuery = li div:contains('${READY_TO_OPEN_COMPETITION_NAME}') ~ *:contains(24/02/2018)
    ${openDate} =    robot.libraries.DateTime.Convert Date    2018-02-24
    ${today} =    get current date
    Should Be True    '${today}'<'${openDate}'

the user is able to change the value of the fields
    the user navigates to the page    ${server}/management/competition/setup/${READY_TO_OPEN_COMPETITION}/section/application/detail/edit
    the user enters text to a text field  id = minProjectDuration  2
    the user enters text to a text field  id = maxProjectDuration  30
    the user selects the radio button  useResubmissionQuestion    use-resubmission-question-no
    the user clicks the button/link    jQuery = .govuk-button:contains("Done")
    the user clicks the button/link    link = Application details
    the user should see the element    jQuery = dt:contains("Minimum") + dd:contains("2")
    the user should see the element    jQuery = dt:contains("Maximum") + dd:contains("30")
    the user should see the element    jQuery = dt:contains("resubmission") + dd:contains("No")
    the user clicks the button/link    jQuery = .govuk-button:contains("Edit this question")
    the user clicks the button/link    jQuery = label[for="use-resubmission-question-yes"]
    the user clicks the button/link    jQuery = .govuk-button:contains("Done")
    the user clicks the button/link    link = Application details
    the user should see the element    jQuery = dt:contains("resubmission") + dd:contains("Yes")

Custom suite setup
    the user logs-in in new browser  &{Comp_admin1_credentials}
    ${today}=    get time
    ${tomorrow} =    Add time To Date    ${today}    1 day
    Set suite variable    ${tomorrow}
    Connect to Database  @{database}

return the database to its previous status
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE `id`='6';

the user moves the competition back again
    the user navigates to the page    ${server}/management/competition/setup/${READY_TO_OPEN_COMPETITION}/section/application/landing-page
    the user clicks the button/link    jQuery = button:contains("Done")   # this action  is marking appication section complete
    the user clicks the button/link    link = Competition setup
    the user clicks the button/link    jQuery = a:contains("Complete setup")
    the user clicks the button/link    css = button[type="submit"]
    the user closes the browser

the user fills in the milestone data with valid information
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

the user resets the milestone data
    The user resets the milestone data for milestone    SUBMISSION_DATE    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    ALLOCATE_ASSESSORS    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    ASSESSOR_BRIEFING    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    ASSESSOR_ACCEPTS    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    ASSESSOR_DEADLINE    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    LINE_DRAW    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    ASSESSMENT_PANEL    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    PANEL_DATE    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    FUNDERS_PANEL    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    NOTIFICATIONS    ${createApplicationOpenCompetitionId}
    The user resets the milestone data for milestone    RELEASE_FEEDBACK    ${createApplicationOpenCompetitionId}

if textarea is empty the proper validation messages are shown
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the text in the element  css = .editor  Funding rules for this competition are now entered.
    run keyword if  '${status}'=='FAIL'  Run keywords  Set Focus To Element     css = .editor
    ...                                           AND  Set Focus To Element     css = .govuk-button[type="submit"]
    ...                                           AND  the user should see a field error  ${empty_field_warning_message}
    ...                                           AND  the user clicks the button/link  css = .govuk-button[type="submit"]
    ...                                           AND  the user should see a field and summary error  ${empty_field_warning_message}
    ...                                           AND  the user enters text to a text field  css=.editor  Funding rules for this competition are now entered.

the user resets the milestone data for milestone
    [Arguments]    ${milestone}    ${competitionId}
    The user enters text to a text field    name = milestoneEntries[${milestone}].day    ${getMilestoneDay(${competitionId}, "${milestone}")}
    The user enters text to a text field    name = milestoneEntries[${milestone}].month    ${getMilestoneMonth(${competitionId}, "${milestone}")}
    The user enters text to a text field    name = milestoneEntries[${milestone}].year    ${getMilestoneYear(${competitionId}, "${milestone}")}

Custom suite teardown
    the user moves the competition back again
    Disconnect from database
    the user closes the browser
