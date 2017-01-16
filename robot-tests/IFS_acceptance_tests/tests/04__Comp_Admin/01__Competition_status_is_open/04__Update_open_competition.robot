*** Settings ***
Documentation     INFUND-6661 As a Competitions team member I want to be able to update Initial details throughout the life of the competition
...               INFUND-6937 As a Competitions team member I want to be able to view Application details throughout the life of the competition
...               INFUND-6938 As a Competitions team member I want to be able to view Project summary throughout the life of the competition
...               INFUND-6939 As a Competitions team member I want to be able to view Public description throughout the life of the competition
...               INFUND-6940 As a Competitions team member I want to be able to view Scope throughout the life of the competition
...               INFUND-6941 As a Competitions team member I want to be able to view Finances throughout the life of the competition
...               INFUND-6792 As a Competitions team member I want to be able to view Eligibility throughout the life of the competition
...               INFUND-7083 As a Competitions team member I want to be able to update PAF number, budget and activity codes throughout the life of the competition
...               INFUND-6695 As a Competitions team member I want to be able to update the number of Assessors required per applicationthroughout the life of the competition
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    MySQL
Resource          ../../../resources/defaultResources.robot
Resource          ../CompAdmin_Commons.robot

*** Variables ***
@{database}       pymysql    ${database_name}    ${database_user}    ${database_password}    ${database_host}    ${database_port}

*** Test Cases ***
User can update initial details of a competition before notify date
    [Documentation]    INFUND-6661
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_UPDATE_COMP}
    Given the user clicks the button/link    link=Initial details
    And the user clicks the button/link    jQuery=.button:contains("Edit")
    And the user should see that the element is disabled    id=openingDateDay
    And the user should see that the element is disabled    id=openingDateMonth
    And the user should see that the element is disabled    id=openingDateYear
    And the user should see that the element is disabled    id=competitionTypeId
    And the user should see that the element is disabled    id=innovationSectorCategoryId
    And the user should see that the element is disabled    id=innovationAreaCategoryId-0
    When the user selects the option from the drop-down menu    Peter Freeman    id=leadTechnologistUserId
    And the user selects the option from the drop-down menu    Toby Reader    id=executiveUserId
    And the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the element    jQuery=.button:contains("Edit")
    And The user should see the text in the page    Peter Freeman
    And The user should see the text in the page    Toby Reader

User cannot update initial details of a competition after notify date
    [Documentation]    INFUND-6661
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_OPEN_COMP}
    Given the user clicks the button/link    link=Initial details
    Then the user should not see the element    jQuery=.button:contains("Edit")
    And the user should not see the element    jQuery=.button:contains("Done")

Comp admin can edit Application details before Open date
    [Documentation]    INFUND-6937
    [Tags]
    Given the user navigates to the page    ${CA_UpcomingComp}
    Then the user can see the open date of the competition belongs to the future
    When the user navigates to the page    ${server}/management/competition/setup/${READY_TO_OPEN_COMPETITION}
    And the user clicks the button/link    link=Application
    Then the user should see the element    link=Application details
    When the user clicks the button/link    link=Application details
    Then the user should see the element    jQuery=.button:contains("Edit this question")
    When the user clicks the button/link    jQuery=.button:contains("Edit this question")
    Then the user is able to change the value of the fields

Comp admin has read only view of Application details past Open date
    [Documentation]    INFUND-6937
    ...    Trying this test case on Compd_id=1. Is an Open competition, so his Open date belongs to the past
    [Tags]
    Given the user navigates to the page    ${CA_Live}
    Then the user should see the element    jQuery=h2:contains('Open') ~ ul a:contains('Connected digital additive')
    When the user navigates to the page    ${server}/management/competition/setup/1/section/application/detail
    Then the user should not see the element    jQuery=.button:contains("Edit this question")
    When the user navigates to the page    ${server}/management/competition/setup/1/section/application/detail/edit
    And the user clicks the button/link    jQuery=.button:contains("Save and close")
    Then the user should see the element    jQuery=ul.error-summary-list:contains("The competition is no longer editable.")

Comp admin can edit Project summary before Open date
    [Documentation]    INFUND-6938
    [Tags]
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_READY_TO_OPEN}
    Given the user clicks the button/link    link=Application
    Then The user should see the text in the page    Project summary
    And The user clicks the button/link    link=Project summary
    And the user should see the element    jquery=h1:contains("Project summary")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And The user enters text to a text field    id= question.maxWords    100
    And the user clicks the button/link    css=input.button.button-large

Comp admin can edit Public description details before Open date
    [Documentation]    INFUND-6939
    [Tags]
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_READY_TO_OPEN}
    Given The user clicks the button/link    link=Application
    Then The user should see the text in the page    Public description
    And The user clicks the button/link    link=Public description
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And The user enters text to a text field    id= question.maxWords    100
    And the user clicks the button/link    css=input.button.button-large
    And The user clicks the button/link    link=Scope
    And the user should see the element    jquery=h1:contains("Scope")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And The user enters text to a text field    id= question.maxWords    100
    And the user clicks the button/link    css=input.button.button-large

Comp admin can edit Scope before Open date
    [Documentation]    INFUND-6940
    [Tags]
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_READY_TO_OPEN}
    Given the user clicks the button/link    link=Application
    Then The user should see the text in the page    Scope
    And The user clicks the button/link    link=Scope
    And the user should see the element    jquery=h1:contains("Scope")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And The user enters text to a text field    id= question.maxWords    100
    And the user clicks the button/link    css=input.button.button-large

Comp admin can edit Finances before open Date
    [Documentation]    INFUND-6941
    [Tags]
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_READY_TO_OPEN}
    Given the user clicks the button/link    link=Application
    Then The user should see the text in the page    Finances
    And The user clicks the button/link    link=Finances
    And the user should see the element    jquery=h1:contains("Application finances")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And the user clicks the button/link    jQuery=.button:contains("Save and close")

Comp admin can edit Eligibility before Open date
    [Documentation]    INFUND-6792
    [Tags]
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_READY_TO_OPEN}
    Given the user clicks the button/link    link=Eligibility
    Then the user should see the element    jquery=h1:contains("Eligibility")
    And The user clicks the button/link    jQuery=button:contains(Edit)
    And the user selects the radio button    singleOrCollaborative    single
    And The user clicks the button/link    jQuery=button:contains(Done)

Comp admin can edit Assessors page before Notifications Date
    [Documentation]    INFUND-6695
    [Tags]    MySQL    HappyPath
    [Setup]    Connect to Database    @{database}
    Given log in as a different user    &{Comp_admin1_credentials}
    And there is a future Notifications date
    When the user navigates to the page    ${server}/management/competition/setup/1/section/assessors
    Then the user should see the element    jQuery=.button:contains("Edit")
    And the user should see the element    jQuery=dt:contains("How many assessors") + dd:contains("3")
    When the user clicks the button/link    jQuery=.button:contains("Edit")
    Then the user selects the radio button    assessorCount    5
    And the user should see the element    css=#assessorPay[readonly="readonly"]
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should not see an error in the page
    And the user should see the element    jQuery=dt:contains("How many assessors") + dd:contains("5")
    And the user should see the element    jQuery=.button:contains("Edit")
    [Teardown]    return the database to its previous status

Comp admin cannot edit Assessors page after Notifications Date
    [Documentation]    INFUND-6695
    [Tags]    Pending
    # TODO Pending due to INFUND-7511

Comp admin has read only view of Eligibility past Open date
    [Documentation]    INFUND-6792
    [Tags]
    Given The user navigates to the page    ${SERVER}/management/competition/setup/11/
    And The user clicks the button/link    link=Eligibility
    And the user should see the element    jquery=h1:contains("Eligibility")
    And The user should not see the element    css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    [Teardown]    The user clicks the button/link    link = Return to setup overview

Comp admin has read only view of Public Description past Open date
    [Documentation]    INFUND-6939
    [Tags]
    Given The user clicks the button/link    link=Application
    When The user should see the text in the page    Public description
    And The user clicks the button/link    link=Public description
    Then the user should see the element    jquery=h1:contains("Public description")
    And The user should not see the element    css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    [Teardown]    The user clicks the button/link    link = Return to application questions

Comp admin has read only view of Project Summary past Open date
    [Documentation]    INFUND-6938
    [Tags]
    When The user clicks the button/link    link=Project summary
    Then the user should see the element    jquery=h1:contains("Project summary")
    And The user should not see the element    css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    [Teardown]    The user clicks the button/link    link = Return to application questions

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

there is a future Notifications date
    [Documentation]    There are no testing data for `milestone`.`type`="NOTIFICATIONS". So i am using MySQL to create a future date
    ...    I am updating Competition=1. Because is the Competition that remains in Open State.
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${tomorrow}' WHERE `id`='6';

return the database to its previous status
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE `id`='6';
