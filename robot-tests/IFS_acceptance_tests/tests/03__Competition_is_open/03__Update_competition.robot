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
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin    MySQL
Resource          ../../resources/defaultResources.robot
Resource          ../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
@{database}       pymysql    ${database_name}    ${database_user}    ${database_password}    ${database_host}    ${database_port}

*** Test Cases ***
Application details are editable (Ready to Open)
    [Documentation]    INFUND-6937
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains(Upcoming)
    And the user clicks the button/link    link=${ready_to_open_competition_name}
    And the user clicks the button/link    jQuery=a:contains(and update competition setup)
    And the user clicks the button/link    link=Application
    And the user should see the element    link=Application details
    When the user clicks the button/link    link=Application details
    AND the user should see the element    jQuery=.button:contains("Edit this question")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    Then the user is able to change the value of the fields
    [Teardown]    the user clicks the button/link    link=Application

Project summary is editable (Ready to Open)
    [Documentation]    INFUND-6938
    [Tags]
    When The user clicks the button/link    link=Project summary
    And the user should see the element    jquery=h1:contains("Project summary")
    When the user clicks the button/link    jQuery=.button:contains("Edit this question")
    Then The user enters text to a text field    id= question.maxWords    100
    And the user clicks the button/link    css=input.button.button-large

Public description should be editable (Ready to Open)
    [Documentation]    INFUND-6939
    [Tags]
    Given the user clicks the button/link    link=Public description
    When the user clicks the button/link    jQuery=.button:contains("Edit this question")
    Then The user enters text to a text field    id= question.maxWords    100
    And the user clicks the button/link    css=input.button.button-large

Scope is editable (Ready to Open)
    [Documentation]    INFUND-6940
    [Tags]
    Given The user clicks the button/link    link=Scope
    When the user clicks the button/link    jQuery=.button:contains("Edit this question")
    Then The user enters text to a text field    id= question.maxWords    100
    And the user clicks the button/link    css=input.button.button-large

Assessed Questions are editable (Ready to Open)
    [Documentation]    INFUND-6936
    [Tags]
    When the user clicks the button/link    link=Business opportunity
    Then the user should see the element    jQuery=h1:contains("Business opportunity")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And the user edits the assessed question information
    And the user clicks the button/link    jQuery=.button[value="Save and close"]
    And wait for autosave
    When the user clicks the button/link    link=Business opportunity
    Then the user sees the correct assessed question information
    And the user clicks the button/link    link = Return to application questions

Finances are editable (Ready to Open)
    [Documentation]    INFUND-6941
    [Tags]
    Given The user clicks the button/link    link=Finances
    And the user should see the element    jquery=h1:contains("Application finances")
    When the user clicks the button/link    jQuery=.button:contains("Edit this question")
    Then the user clicks the button/link    jQuery=.button:contains("Save and close")
    [Teardown]    the user clicks the button/link    link=Competition setup

Eligibility is editable (Ready to Open)
    [Documentation]    INFUND-6792
    [Tags]
    When the user clicks the button/link    link=Eligibility
    Then the user should see the element    jquery=h1:contains("Eligibility")
    And The user clicks the button/link    jQuery=button:contains(Edit)
    And the user selects the radio button    singleOrCollaborative    single
    And The user clicks the button/link    jQuery=button:contains(Done)

Funding Information is editable (Open)
    [Documentation]    INFUND-7083
    [Tags]    HappyPath
    [Setup]    The user clicks the button/link    jQuery=a:contains(My dashboard)
    Given the user clicks the button/link    link=${open_competition_link_2}
    And the user clicks the button/link    jQuery=a:contains(and update competition setup)
    When the user clicks the button/link    link=Funding information
    And the user should see the element    jquery=h1:contains("Funding information")
    And the user clicks the button/link    jQuery=.button:contains("Edit")
    And The user enters text to a text field    id=funders0.funder    Funders Edit test
    And the user should see the element    id=0-funderBudget
    And the user should see the element    id=pafNumber
    And the user should see the element    id=budgetCode
    And the user should see the element    id=activityCode
    And The user clicks the button/link    jQuery=.button:contains("Done")
    Then The user should see the element    jQuery=.button:contains("Edit")
    And The user should see the text in the page    Funders Edit test
    [Teardown]    the user clicks the button/link    link=Competition setup

Milestones are editable (Open)
    [Documentation]    INFUND-6694
    [Tags]
    When the user clicks the button/link    link=Milestones
    And the user clicks the button/link    jQuery=button:contains(Edit)
    Then the user should see that the element is disabled    jQuery=tr:nth-child(1) .year input
    And the user should see that the element is disabled    jQuery=tr:nth-child(2) .year input
    And the user fills in the milestone data with valid information
    And the user clicks the button/link    jQuery=button:contains(Done)
    And the user clicks the button/link    jQuery=.button:contains("Edit")
    And the user resets the milestone data
    And the user clicks the button/link    jQuery=button:contains(Done)
    [Teardown]    the user clicks the button/link    link=Competition setup

Application details are not editable (Open)
    [Documentation]    INFUND-6937
    [Tags]
    When the user clicks the button/link    link=Application
    And the user clicks the button/link    link=Application details
    Then the user should not see the element    jQuery=.button:contains("Edit this question")
    [Teardown]    The user clicks the button/link    link=Application

Assessed Questions are not editable (Open)
    [Documentation]    INFUND-6936
    [Tags]
    When the user clicks the button/link    link=Business opportunity
    And the user should see the element    jquery=h1:contains("Business opportunity")
    Then the user should not see the element    jquery=.button:contains("Edit")
    [Teardown]    The user clicks the button/link    link=Application

Eligibility is not editable (Open)
    [Documentation]    INFUND-6792
    [Tags]
    [Setup]    The user clicks the button/link    link=Competition setup
    When The user clicks the button/link    link=Eligibility
    And the user should see the element    jquery=h1:contains("Eligibility")
    Then The user should not see the element    css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    [Teardown]    The user clicks the button/link    link = Return to setup overview

Public Description is not editable (Open)
    [Documentation]    INFUND-6939
    [Tags]
    Given The user clicks the button/link    link=Application
    When The user clicks the button/link    link=Public description
    And the user should see the element    jquery=h1:contains("Public description")
    Then The user should not see the element    css = input
    And The user should not see the element    jQuery=.button:contains("Edit this question")
    And The user should not see the element    jQuery=.button[value="Save and close"]
    [Teardown]    The user clicks the button/link    link = Return to application questions

Project Summary is not editable (Open)
    [Documentation]    INFUND-6938
    [Tags]
    When The user clicks the button/link    link=Project summary
    And the user should see the element    jquery=h1:contains("Project summary")
    Then The user should not see the element    css = input
    And The user should not see the element    jQuery=.button:contains("Edit this question")
    And The user should not see the element    jQuery=.button[value="Save and close"]
    [Teardown]    The user clicks the button/link    link = Return to application questions

Scope is not editable (Open)
    [Documentation]    INFUND-6940
    [Tags]
    When The user clicks the button/link    link=Scope
    Then the user should see the element    jquery=h1:contains("Scope")
    And The user should not see the element    css = input
    And The user should not see the element    jQuery=.button:contains("Edit this question")
    And The user should not see the element    jQuery=.button[value="Save and close"]
    [Teardown]    The user clicks the button/link    link = Return to application questions

Finances not editable (Open)
    [Documentation]    INFUND-6941
    [Tags]
    When The user clicks the button/link    link=Finances
    And the user should see the element    jquery=h1:contains("Application finances")
    Then The user should not see the element    css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    [Teardown]    The user clicks the button/link    link = Return to application questions

Initial details editable before notify date (Open)
    [Documentation]    INFUND-6661
    [Setup]    the user clicks the button/link    link=Competition setup
    Given the user clicks the button/link    link=Initial details
    And the user clicks the button/link    jQuery=.button:contains("Edit")
    And the user should see that the element is disabled    id=openingDateDay
    And the user should see that the element is disabled    id=openingDateMonth
    And the user should see that the element is disabled    id=openingDateYear
    And the user should see that the element is disabled    id=competitionTypeId
    And the user should see that the element is disabled    id=innovationSectorCategoryId
    And the user should see that the element is disabled    id=innovationAreaCategoryId-0
    When the user selects the option from the drop-down menu    Peter Freeman    id=leadTechnologistUserId
    And the user selects the option from the drop-down menu    John Doe    id=executiveUserId
    And the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the element    jQuery=.button:contains("Edit")
    And The user should see the text in the page    Peter Freeman
    And The user should see the text in the page    John Doe
    [Teardown]    the user clicks the button/link    link=Competition setup

Assessors editable before Notifications Date (Open)
    [Documentation]    INFUND-6695
    [Tags]    MySQL    HappyPath
    [Setup]    Connect to Database    @{database}
    Given the user clicks the button/link    link=Assessors
    Then the user should see the element    jQuery=.button:contains("Edit")
    And the user should see the element    jQuery=dt:contains("How many assessors") + dd:contains("1")
    When the user clicks the button/link    jQuery=.button:contains("Edit")
    Then the user selects the radio button    assessorCount    5
    And the user should see the element    css=#assessorPay[readonly="readonly"]
    When the user clicks the button/link    jQuery=.button:contains("Done")
    And the user should see the element    jQuery=dt:contains("How many assessors") + dd:contains("5")
    And the user should see the element    jQuery=.button:contains("Edit")
    [Teardown]    return the database to its previous status

Initial details not editable after notify date (Open)
    [Documentation]    INFUND-6661
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_NOT_EDITABLE_COMP}
    Given the user clicks the button/link    link=Initial details
    Then the user should not see the element    jQuery=.button:contains("Edit")
    And the user should not see the element    jQuery=.button:contains("Done")
    [Teardown]    the user clicks the button/link    link=Competition setup

Funding Information not editable after notifications date (Open)
    [Documentation]    INFUND-7183
    [Tags]
    When The user clicks the button/link    link=Funding information
    And the user should see the element    jquery=h1:contains("Funding information")
    Then The user should not see the element    css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    [Teardown]    the user clicks the button/link    link=Competition setup

Assessors not editable after Notifications Date (Open)
    [Documentation]    INFUND-6695
    [Tags]
    When the user clicks the button/link    link=Assessors
    Then the user should not see the element    jQuery=.button:contains("Edit")
    And the user should not see the element    jQuery=.button:contains("Done")

*** Keywords ***
the user can see the open date of the competition belongs to the future
    the user should see the element    jQuery=h2:contains('Ready to open') ~ ul a:contains('${READY_TO_OPEN_COMPETITION_NAME}')
    the user should see the element    jQuery=li div:contains('${READY_TO_OPEN_COMPETITION_NAME}') ~ *:contains(24/02/2018)
    ${openDate} =    robot.libraries.DateTime.Convert Date    2018-02-24
    ${today} =    get current date
    Should Be True    '${today}'<'${openDate}'

the user is able to change the value of the fields
    the user navigates to the page    ${server}/management/competition/setup/${READY_TO_OPEN_COMPETITION}/section/application/detail/edit
    the user selects the radio button    useResubmissionQuestion    use-resubmission-question-no
    the user clicks the button/link    jQuery=.button:contains("Save and close")
    the user clicks the button/link    link=Application details
    the user should see the element    jQuery=dl dt:contains("Resubmission") + dd:contains("No")
    the user clicks the button/link    jQuery=.button:contains("Edit this question")
    the user clicks the button/link    jQuery=label[for="use-resubmission-question-yes"]
    the user clicks the button/link    jQuery=.button:contains("Save and close")
    the user clicks the button/link    link=Application details
    the user should see the element    jQuery=dl dt:contains("Resubmission") + dd:contains("Yes")

Custom suite setup
    Guest user log-in    &{Comp_admin1_credentials}
    ${today}=    get time
    ${tomorrow} =    Add time To Date    ${today}    1 day
    Set suite variable    ${tomorrow}

The user moves the open date to the past
    Connect to Database    @{database}
    Change the open date of the Competition in the database to one day before    ${open_competition_link_2}

there is a future Notifications date
    [Documentation]    There are no testing data for `milestone`.`type`="NOTIFICATIONS". So i am using MySQL to create a future date
    ...    I am updating Competition=1. Because is the Competition that remains in Open State.
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${tomorrow}' WHERE `id`='6';

return the database to its previous status
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE `id`='6';

the user moves the competition back again
    the user navigates to the page    ${server}/management/competition/setup/${READY_TO_OPEN_COMPETITION}
    Run Keyword And Ignore Error    the user clicks the button/link    jQuery=.button:contains("Save")
    the user closes the browser

the user fills in the milestone data with valid information
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

the user resets the milestone data
    The user enters text to a text field    name=milestoneEntries[SUBMISSION_DATE].day    09
    The user enters text to a text field    name=milestoneEntries[SUBMISSION_DATE].month    09
    The user enters text to a text field    name=milestoneEntries[SUBMISSION_DATE].year    2067
    The user enters text to a text field    name=milestoneEntries[ALLOCATE_ASSESSORS].day    10
    The user enters text to a text field    name=milestoneEntries[ALLOCATE_ASSESSORS].month    09
    The user enters text to a text field    name=milestoneEntries[ALLOCATE_ASSESSORS].year    2067
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_BRIEFING].day    11
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_BRIEFING].month    09
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_BRIEFING].year    2067
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_ACCEPTS].day    12
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_ACCEPTS].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_ACCEPTS].year    2068
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_DEADLINE].day    29
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_DEADLINE].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_DEADLINE].year    2068
    The user enters text to a text field    name=milestoneEntries[LINE_DRAW].day    20
    The user enters text to a text field    name=milestoneEntries[LINE_DRAW].month    02
    The user enters text to a text field    name=milestoneEntries[LINE_DRAW].year    2068
    The user enters text to a text field    name=milestoneEntries[ASSESSMENT_PANEL].day    20
    The user enters text to a text field    name=milestoneEntries[ASSESSMENT_PANEL].month    3
    The user enters text to a text field    name=milestoneEntries[ASSESSMENT_PANEL].year    2068
    The user enters text to a text field    name=milestoneEntries[PANEL_DATE].day    20
    The user enters text to a text field    name=milestoneEntries[PANEL_DATE].month    4
    The user enters text to a text field    name=milestoneEntries[PANEL_DATE].year    2068
    The user enters text to a text field    name=milestoneEntries[FUNDERS_PANEL].day    20
    The user enters text to a text field    name=milestoneEntries[FUNDERS_PANEL].month    5
    The user enters text to a text field    name=milestoneEntries[FUNDERS_PANEL].year    2068
    The user enters text to a text field    name=milestoneEntries[NOTIFICATIONS].day    20
    The user enters text to a text field    name=milestoneEntries[NOTIFICATIONS].month    6
    The user enters text to a text field    name=milestoneEntries[NOTIFICATIONS].year    2068
    The user enters text to a text field    name=milestoneEntries[RELEASE_FEEDBACK].day    20
    The user enters text to a text field    name=milestoneEntries[RELEASE_FEEDBACK].month    7
    The user enters text to a text field    name=milestoneEntries[RELEASE_FEEDBACK].year    2068

Custom suite teardown
    the user moves the competition back again
    the user closes the browser