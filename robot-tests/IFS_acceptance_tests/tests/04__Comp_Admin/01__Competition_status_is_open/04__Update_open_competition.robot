*** Settings ***
<<<<<<< HEAD
Documentation
...               INFUND-6661 As a Competitions team member I want to be able to update Initial details throughout the life of the competition
...               INFUND-6937  As a Competitions team member I want to be able to view Application details throughout the life of the competition
...               INFUND-6938  As a Competitions team member I want to be able to view Project summary throughout the life of the competition
...               INFUND-6939  As a Competitions team member I want to be able to view Public description throughout the life of the competition
...               INFUND-6940  As a Competitions team member I want to be able to view Scope throughout the life of the competition
...               INFUND-6941  As a Competitions team member I want to be able to view Finances throughout the life of the competition
...               INFUND-6792  As a Competitions team member I want to be able to view Eligibility throughout the life of the competition
...               INFUND-7083  As a Competitions team member I want to be able to update PAF number, budget and activity codes throughout the life of the competition

=======
Documentation     INFUND-6661 As a Competitions team member I want to be able to update Initial details throughout the life of the competition
...
...               INFUND-6937 As a Competitions team member I want to be able to view Application details throughout the life of the competition
>>>>>>> 7d0c54929f14889b187a2123f678b88894b9d0e3
Suite Setup       Log in as user    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../CompAdmin_Commons.robot

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
<<<<<<< HEAD
    [Documentation]     INFUND-6937, INFUND-6938, INFUND-6939, INFUND-6940
    [Tags]
    [Setup]     log in as a different user    &{Comp_admin1_credentials}
    Given The user navigates to the page    ${SERVER}/management/competition/setup/6/
    And The user clicks the button/link    link=Application
    Then The user should see the text in the page   Application details
    And The user clicks the button/link     link=Application details
    And the user should see the element    jquery=h1:contains("Application details")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And the user clicks the button/link    jQuery=.button:contains("Save and close")
    And The user clicks the button/link     link=Project summary
    And the user should see the element    jquery=h1:contains("Project summary")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And The user enters text to a text field    id= question.maxWords  100
    And the user clicks the button/link    css=input.button.button-large
    And The user clicks the button/link     link=Public description
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And The user enters text to a text field    id= question.maxWords  100
    And the user clicks the button/link    css=input.button.button-large
    And The user clicks the button/link     link=Scope
    And the user should see the element    jquery=h1:contains("Scope")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And The user enters text to a text field    id= question.maxWords  100
    And the user clicks the button/link    css=input.button.button-large


Comp admin can edit Finances before open Date
    [Documentation]     INFUND-6941
    [Tags]
    [Setup] The user navigates to the page    ${SERVER}/management/competition/setup/6/
    And The user clicks the button/link     link=Finances
    And the user should see the element    jquery=h1:contains("Application finances")
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    And the user clicks the button/link    jQuery=.button:contains("Save and close")
=======
    [Documentation]     INFUND-6937
    [Tags]
    [Setup]     log in as a different user  &{Comp_admin1_credentials}
    Given the user navigates to the page    ${CA_UpcomingComp}
    Then the user can see the open date of the competition belongs to the future
    When the user navigates to the page       ${server}/management/competition/setup/${READY_TO_OPEN_COMPETITION}
    And the user clicks the button/link       link=Application
    Then the user should see the element      link=Application details
    When the user clicks the button/link      link=Application details
    Then the user should see the element      jQuery=.button:contains("Edit this question")
    When the user clicks the button/link      jQuery=.button:contains("Edit this question")
    Then the user is able to change the value of the fields


#The following is part of another story.
#
#    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
#    And the user clicks the button/link    jQuery=.button:contains("Save and close")
#    And The user clicks the button/link     link=Project summary
#    And the user should see the element    jquery=h1:contains("Project summary")
#    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
#    And The user enters text to a text field    id= question.maxWords  100
#    And the user clicks the button/link    css=input.button.button-large
#    And The user clicks the button/link     link=Public description
#    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
#    And The user enters text to a text field    id= question.maxWords  100
#    And the user clicks the button/link    css=input.button.button-large
#    And The user clicks the button/link     link=Scope
#    And the user should see the element    jquery=h1:contains("Scope")
#    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
#    And The user enters text to a text field    id= question.maxWords  100
#    And the user clicks the button/link    css=input.button.button-large
#    And The user clicks the button/link     link=Finances
#    And the user should see the element    jquery=h1:contains("Application finances")
#    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
#    And the user clicks the button/link    jQuery=.button:contains("Save and close")
>>>>>>> 7d0c54929f14889b187a2123f678b88894b9d0e3

Comp admin can edit Eligibility before Open date
    [Documentation]     INFUND-6792
    [Tags]
    [Setup] The user navigates to the page    ${SERVER}/management/competition/setup/6/
    And The user clicks the button/link    link=Eligibility
    And the user should see the element    jquery=h1:contains("Eligibility")
    And The user clicks the button/link     css=button.button
    And the user selects the radio button     singleOrCollaborative    single
    And The user clicks the button/link      css=button.button



*** Keywords ***
the user can see the open date of the competition belongs to the future
    the user should see the element  jQuery=h2:contains('Ready to open') ~ ul a:contains('${READY_TO_OPEN_COMPETITION_NAME}')
    the user should see the element  jQuery=li div:contains('${READY_TO_OPEN_COMPETITION_NAME}') ~ *:contains(24/02/2018)
    ${openDate} =  robot.libraries.DateTime.Convert Date  2018-02-24
    ${today} =  get current date
    Should Be True  '${today}'<'${openDate}'

the user is able to change the value of the fields
    the user navigates to the page     ${server}/management/competition/setup/${READY_TO_OPEN_COMPETITION}/section/application/detail/edit
    the user selects the radio button  useResubmissionQuestion  use-resubmission-question-no
    the user clicks the button/link    jQuery=.button:contains("Save and close")
    the user clicks the button/link    link=Application details
    the user should see the element    jQuery=dl dt:contains("Resubmission") + dd:contains("No")
    the user clicks the button/link    jQuery=.button:contains("Edit this question")
    the user selects the radio button  useResubmissionQuestion  use-resubmission-question-yes
    the user clicks the button/link    jQuery=.button:contains("Save and close")
    the user clicks the button/link    link=Application details
    the user should see the element    jQuery=dl dt:contains("Resubmission") + dd:contains("Yes")