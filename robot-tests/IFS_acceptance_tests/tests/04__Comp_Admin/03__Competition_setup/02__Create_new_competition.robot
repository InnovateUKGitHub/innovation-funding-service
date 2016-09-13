*** Settings ***
Documentation     INFUND-2945 As a Competition Executive I want to be able to create a new competition from the Competitions Dashboard so Innovate UK can create a new competition
...
...               INFUND-2982: Create a Competition: Step 1: Initial details
...
...               INFUND-2983: As a Competition Executive I want to be informed if the competition will fall under State Aid when I select a 'Competition type' in competition setup
...
...               INFUND-2984: As a Competition Executive I want the competition code field in the 'Initial details' tab in competition setup to generate based on open date and number of competitions in that month
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
...               INFUND-4682 Initial details can be saved with an opening date in the past
...
...               INFUND-2980 As a Competition Executive I want to see a newly created competition listed in the Competition Dashboard so that I can view and update further details
...
...               INFUND-2993 As a competitions team member I want to be able to add milestones when creating my competition so these can be used manage its progress
...
...               INFUND-4468 As a Competitions team member I want to include additional criteria in Competitions Setup so that the "Ready to Open" state cannot be set until these conditions are met
...
...
...               INFUND-3001 As a Competitions team member I want the service to automatically save my edits while I work through Initial Details section in Competition Setup the so that I do not lose my changes
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin    CompSetup
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot    # TODO Known bug INFUND-4681, enforces the Competition Type (in Initial Details) to be Programme else Application Questions lead to 404, please do not change the value!

*** Test Cases ***
User can create a new competition
    [Documentation]    INFUND-2945
    ...    INFUND-2982
    ...    INFUND-2983
    ...    INFUND-2986
    ...    IFUND-3888
    ...    INFUND-3002
    ...    INFUND-2980
    [Tags]    HappyPath
    Given the user clicks the button/link    id=section-3
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    Then the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    And The user should not see the element    jQuery('.button:contains("Save as Ready To Open")

New competition shows in Preparation section with the default name
    [Documentation]    INFUND-2980
    Given The user clicks the button/link    link=All competitions
    And The user clicks the button/link    id=section-3
    Then the competition should show in the correct section    css=section:nth-child(4) li:nth-child(2)    No competition title defined    #this keyword checks if the new application shows in the second line of the "In preparation" competitions

Initial details server-side validations
    [Documentation]    INFUND-2982
    ...
    ...    IFUND-3888
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    Given The user clicks the button/link    link=Initial Details
    and the user should not see the element    css=#stateAid
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see an error    Please enter a title
    And the user should see an error    Please select a competition type
    And the user should see an error    Please select an innovation sector
    And the user should see an error    Please select an innovation area
    And the user should see an error    Please enter an opening year
    And the user should see an error    Please enter an opening day
    And the user should see an error    Please enter an opening month
    And the user should see an error    Please select a lead technologist
    And the user should see an error    Please select a competition executive

Initial details correct state aid status
    [Documentation]    INFUND-2982
    ...
    ...    INFUND-2983
    ...
    ...    INFUND-3888
    [Tags]    Pending
    #This ticket marked as pending because atm there is only one competition type. We should recheck this in sprint15
    When the user selects the option from the drop-down menu    SBRI    id=competitionTypeId
    Then the user should see the element    css=.no
    When the user selects the option from the drop-down menu    Special    id=competitionTypeId
    Then the user should see the element    css=.no
    When the user selects the option from the drop-down menu    Additive Manufacturing    id=competitionTypeId
    Then the user should see the element    css=.yes
    When the user selects the option from the drop-down menu    Programme    id=competitionTypeId
    Then the user should see the element    css=.yes

Initial details client-side validations
    [Documentation]    INFUND-2982
    ...
    ...    INFUND-3888
    [Tags]    HappyPath
    When the user enters text to a text field    id=title    Competition title
    Then the user should not see the error any more    Please enter a title
    When the user selects the option from the drop-down menu    Programme    id=competitionTypeId
    Then the user should not see the error any more    Please select a competition type
    When the user selects the option from the drop-down menu    Health and life sciences    id=innovationSectorCategoryId
    Then the user should not see the error any more    Please select an innovation sector
    When the user selects the option from the drop-down menu    Advanced Therapies    id=innovationAreaCategoryId
    Then the user should not see the error any more    Please select an innovation area
    When the user enters text to a text field    id=openingDateDay    01
    Then the user should not see the error any more    Please enter an opening day
    When the user enters text to a text field    Id=openingDateMonth    12
    Then the user should not see the error any more    Please enter an opening month
    When the user enters text to a text field    id=openingDateYear    2017
    Then the user should not see the error any more    Please enter an opening year
    When the user selects the option from the drop-down menu    Competition Technologist One    id=leadTechnologistUserId
    Then the user should not see the error any more    Please select a lead technologist
    When the user selects the option from the drop-down menu    Competition Executive Two    id=executiveUserId
    Then The user should not see the text in the page    Please select a competition executive    #Couldn't use this keyword : "Then the user should not see the error any more" . Because there is not any error in the page
    ##    State aid value is tested in 'Initial details correct state aid status'

Initial details: Autosave
    [Documentation]    INFUND-3001
    [Tags]    Pending
    #pendingI NFUND-4764
    When the user clicks the button/link    link=Competition set up
    and the user clicks the button/link    link=Initial Details
    Then the user should see the correct values in the initial details form

Initial details should not allow to mark as complete when date is in past
    [Documentation]    INFUND-4682
    Given the user enters text to a text field    id=openingDateDay    01
    And the user enters text to a text field    Id=openingDateMonth    12
    And the user enters text to a text field    id=openingDateYear    2015
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then The user should not see the element    jQuery=.button:contains("Edit")
    [Teardown]    the user enters text to a text field    id=openingDateYear    2017

Initial details mark as done
    [Documentation]    INFUND-2982
    ...
    ...    INFUND-2983
    ...
    ...    INFUND-3888
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page    Competition Executive Two
    And the user should see the text in the page    1/12/2017
    And the user should see the text in the page    Competition Technologist One
    And the user should see the text in the page    Competition title
    And the user should see the text in the page    Health and life sciences
    And the user should see the text in the page    Advanced Therapies
    And the user should see the text in the page    Programme
    And the user should see the text in the page    NO
    And the user should see the element    jQuery=.button:contains("Edit")

Initial details can be edited again
    [Documentation]    INFUND-2985
    ...
    ...    INFUND-3182
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Edit")
    And the user enters text to a text field    id=title    Test competition
    And the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page    1/12/2017
    And the user should see the text in the page    Competition Technologist One
    And the user should see the text in the page    Test competition
    And the user should see the text in the page    Health and life sciences
    And the user should see the text in the page    Advanced Therapies
    And the user should see the text in the page    Programme
    And the user should see the text in the page    NO

Initial details should have a green check
    [Documentation]    INFUND-3002
    [Tags]    HappyPath
    When The user clicks the button/link    link=Competition set up
    Then the user should see the element    jQuery=img.section-status:eq(0)
    And the user should not see the element    jQuery=.button:contains("Save as Ready To Open")

New application shows in Preparation section with the new name
    [Documentation]    INFUND-2980
    Given The user clicks the button/link    link=All competitions
    And The user clicks the button/link    id=section-3
    Then the competition should show in the correct section    css=section:nth-child(4) > ul    Test competition    #This keyword checks if the new competition shows in the "In preparation" test

Funding information server-side validations
    [Documentation]    INFUND-2985
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    Given the user clicks the button/link    link=Funding Information
    And the user redirects to the page    Funding information    Reporting fields
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see an error    Please enter a funder name
    And the user should see an error    Please enter a budget
    And the user should see an error    Please enter a PAF number
    And the user should see an error    Please enter a budget code
    And the user should see an error    Please enter an activity code
    And the user should see an error    Please generate a competition code

Funding information client-side validations
    [Documentation]    INFUND-2985
    [Tags]    HappyPath
    When the user enters text to a text field    id=funder    FunderName
    Then the user should not see the error any more    Please enter a funder name
    And the user enters text to a text field    id=funderBudget    20000
    Then the user should not see the error any more    Please enter a budget
    When the user enters text to a text field    id=pafNumber    2016
    Then the user should not see the error any more    Please enter a PAF number
    And the user enters text to a text field    id=budgetCode    2004
    Then the user should not see the error any more    Please enter a budget code
    And the user enters text to a text field    id=activityCode    4242
    Then the user should not see the error any more    Please enter an activity code
    When the user clicks the button/link    jQuery=.button:contains("Generate code")
    Then The user should not see the text in the page    Please generate a competition code

Funding informations calculations
    [Documentation]    INFUND-2985
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=Button:contains("+Add co-funder")
    and the user should see the element    jQuery=Button:contains("+Add co-funder")
    Then the user should see the element    css=#co-funder-row-0
    And the user enters text to a text field    id=0-funder    FunderName2
    And the user enters text to a text field    id=0-funderBudget    1000
    Then the total should be correct    £ 21,000

Funding Information can be saved
    [Documentation]    INFUND-3182
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page    FunderName
    And the user should see the text in the page    FunderName2
    And the user should see the text in the page    £21,000
    And the user should see the text in the page    2016
    And the user should see the text in the page    2004
    And the user should see the text in the page    4242
    And the user should see the text in the page    1712-1
    And the user should see the element    jQuery=.button:contains("Edit")

Funding Information can be edited
    [Documentation]    INFUND-3002
    When the user clicks the button/link    jQuery=.button:contains("Edit")
    And the user enters text to a text field    id=funder    testFunder
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page    testFunder

Funding information should have a green check
    [Documentation]    INFUND-3002
    [Tags]    HappyPath
    When The user clicks the button/link    link=Competition set up
    Then the user should see the element    jQuery=img.section-status:eq(1)
    And the user should not see the element    jQuery=.button:contains("Save as Ready To Open")

Eligibility page should contain the correct options
    [Documentation]    INFUND-2989
    ...
    ...    INFUND-2990
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    Given the user clicks the button/link    link=Eligibility
    And the user should see the text in the page    Does the competition have multiple stream?
    Then the user should see the element    jQuery=label:contains(Single or Collaborative)
    When the user should see the element    jQuery=label:contains(Collaborative)
    And the user should see the element    jQuery=label:contains(Business)
    And the user should see the element    jQuery=label:contains(Research)
    And the user should see the element    jQuery=label:contains(Either)
    And the user should see the element    jQuery=label:contains(Yes)
    And the user should see the element    jQuery=label:contains(No)
    And the user should see the element    jQuery=label:contains(Technical feasibility)
    And the user should see the element    jQuery=label:contains(Industrial research)
    And the user should see the element    jQuery=label:contains(Experimental development)

Eligibility server-side validations
    [Documentation]    INFUND-2986
    [Tags]    HappyPath
    [Setup]
    Given the user selects the radio button    multipleStream    yes
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page    Please select at least one research category
    And the user should see the text in the page    Please select a collaboration level
    And the user should see the text in the page    Please select a lead applicant type
    And the user should see the text in the page    A stream name is required

Eligibility client-side validations
    [Documentation]    INFUND-2986
    ...
    ...    IINFUND-2988
    ...
    ...    INFUND-3888
    [Tags]    HappyPath
    Given the user selects the radio button    multipleStream    yes
    When the user selects the checkbox    id=research-categories-33
    And the user selects the checkbox    id=research-categories-34
    And the user selects the checkbox    id=research-categories-35
    And the user moves focus to a different part of the page
    When the user selects the radio button    singleOrCollaborative    single
    And the user selects the radio button    leadApplicantType    business
    And the user selects the option from the drop-down menu    30%    name=researchParticipationAmountId
    And the user moves focus to a different part of the page
    Then the user should not see the text in the page    Please select a collaboration level
    And the user should not see the text in the page    Please select a lead applicant type
    And the user should not see the text in the page    Please select at least one research category
    And the user enters text to a text field    id=streamName    Test stream name
    And the user moves focus to a different part of the page
    And the user should not see the text in the page    A stream name is required

Eligibility can be marked as done then edit again
    [Documentation]    INFUND-3051
    ...
    ...    INFUND-3872
    ...
    ...    INFUND-3002
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page    Yes
    And the user should see the text in the page    Single
    And the user should see the text in the page    Business
    And the user should see the text in the page    30%
    And the user should see the text in the page    Test stream name
    And the user should see the text in the page    Technical feasibility, Industrial research, Experimental development
    And The user should not see the element    id=streamName
    When the user clicks the button/link    link=Competition set up
    When the user clicks the button/link    link=Eligibility
    And the user clicks the button/link    jQuery=.button:contains("Edit")
    And the user clicks the button/link    jQuery=.button:contains("Done")

Eligibility should have a green check
    [Documentation]    INFUND-3002
    [Tags]    HappyPath
    When The user clicks the button/link    link=Competition set up
    Then the user should see the element    jQuery=img.section-status:eq(2)
    And the user should not see the element    jQuery=.button:contains("Save as Ready To Open")

Milestones: Page should contain the correct fields
    [Documentation]    INFUND-2993
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    When the user clicks the button/link    link=Milestones
    Then The user should see the text in the page    1. Open date
    And the user should see the text in the page    2. Briefing event
    And the user should see the text in the page    3. Submission date
    And the user should see the text in the page    4. Allocate accessors
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

Milestones: Server side validations
    [Documentation]    INFUND-2993
    When the user fills the milestones with invalid data
    And the user clicks the button/link    jQuery=button:contains(Done)
    Then Validation summary should be visible

Milestones: Client side validations
    [Documentation]    INFUND-2993
    [Tags]    HappyPath
    When the user fills the milestones with valid data
    Then The user should not see the text in the page    please enter a future date that is after the previous milestone

Milestones: Correct Weekdays should show
    [Documentation]    INFUND-2993
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=button:contains(Done)
    Then the weekdays should be correct

Milestones: Green check should show
    [Documentation]    INFUND-2993
    When The user clicks the button/link    link=Competition set up
    Then the user should see the element    css=li:nth-child(4) .section-status
    And the user should not see the element    jQuery=.button:contains("Save as Ready To Open")

Application questions: All the sections should be visible
    [Documentation]    INFUND-3000
    [Tags]    HappyPath
    [Setup]    go to    ${COMP_MANAGEMENT_COMP_SETUP}
    When The user clicks the button/link    link=Application Questions
    Then The user should see the text in the page    Template: Programme 10 questions
    And the user should see the text in the page    Scope
    And the user should see the text in the page    2. Potential market
    And the user should see the text in the page    3. Project exploitation
    And the user should see the text in the page    4. Economic benefit
    And the user should see the text in the page    5. Technical approach
    And the user should see the text in the page    6. Innovation
    And the user should see the text in the page    7. Risks
    And the user should see the text in the page    8. Project team
    And the user should see the text in the page    9. Funding
    And the user should see the text in the page    10. Adding value
    [Teardown]    The user clicks the button/link    jQuery=li:nth-child(5) .button:contains(Edit)

Application questions: server side validations
    [Documentation]    INFUND-3000
    [Tags]    HappyPath
    Given The user should see the element    jQuery=.button[value="Save and close"]
    When the user leaves all the question field empty
    And The user clicks the button/link    jQuery=.button[value="Save and close"]
    And The user clicks the button/link    jQuery=.button[value="Save and close"]
    Then the validation error above the question should be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=div:nth-child(4) div:nth-child(4) label:contains(Question guidance)    This field cannot be left blank

Application questions: Client side validations
    [Documentation]    INFUND-3000
    [Tags]    HappyPath
    Given the user fills the empty question fields
    Then the validation error above the question should not be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=div:nth-child(4) div:nth-child(4) label:contains(Question guidance)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Max word count)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Max word count)    This field cannot be left blank

Application questions: Mark as done and the Edit again
    [Documentation]    INFUND-3000
    [Tags]    HappyPath
    [Setup]    The user clicks the button/link    jQuery=.grid-row div:nth-child(2) label:contains(Yes)
    When The user clicks the button/link    jQuery=.button[value="Save and close"]
    Then The user should see the text in the page    Test title
    And the user should see the text in the page    Subtitle test
    And the user should see the text in the page    Test guidance title
    And the user should see the text in the page    Guidance text test
    And the user should see the text in the page    150
    And the user should see the text in the page    Yes
    And The user clicks the button/link    jQuery=button:contains(Done)

Application questions: should have a green check
    [Tags]    HappyPath
    When The user clicks the button/link    link=Competition set up
    Then the user should see the element    css=ul > li:nth-child(5) > img

Ready To Open button should be visible
    [Documentation]    INFUND-3002
    ...
    ...    INFUND-4468
    [Tags]    HappyPath
    Then the user should see the element    jQuery=.button:contains("Save as Ready To Open")

Ready to open button shouldn't be visible when the user re-edits the question
    [Documentation]    INFUND-4468
    [Setup]
    Given The user clicks the button/link    link=Initial Details
    When the user clicks the button/link    jQuery=.button:contains("Edit")
    And The user clicks the button/link    link=Competition set up
    Then the user should not see the element    jQuery=.button:contains("Save as Ready To Open")
    [Teardown]    Run keywords    Given The user clicks the button/link    link=Initial Details
    ...    AND    The user clicks the button/link    jQuery=.button:contains("Done")
    ...    AND    And The user clicks the button/link    link=Competition set up

User should be able to Save the competition as open
    [Documentation]    INFUND-4468
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Save as Ready To Open")
    And the user clicks the button/link    link=All competitions
    And the user clicks the button/link    id=section-3
    Then the competition should show in the correct section    css=section:nth-child(5) ul    Test competition
    # The above line checks that the section 'Ready to Open' there is a competition named Test competition

*** Keywords ***
the user moves focus to a different part of the page
    focus    link=Sign out

the user should not see the error any more
    [Arguments]    ${ERROR_TEXT}
    run keyword and ignore error    mouse out    css=input
    Focus    jQuery=.button:contains("Done")
    sleep    200ms
    Wait Until Element Does Not Contain    css=.error-message    ${ERROR_TEXT}
    Wait Until Page Does Not Contain    Saving...

the total should be correct
    [Arguments]    ${Total}
    mouse out    css=input
    Focus    jQuery=Button:contains("Done")
    Wait Until Element Contains    css=.no-margin    ${Total}

the user leaves all the question field empty
    Clear Element Text    css=.editor
    Press Key    css=.editor    \\8
    focus    jQuery=.button[value="Save and close"]
    sleep    200ms
    The user enters text to a text field    id=question.title    ${EMPTY}
    The user enters text to a text field    id=question.guidanceTitle    ${EMPTY}
    The user enters text to a text field    jQuery=[id="question.maxWords"]    ${EMPTY}

the validation error above the question should be visible
    [Arguments]    ${QUESTION}    ${ERROR}
    Element Should Contain    ${QUESTION}    ${ERROR}

the user fills the empty question fields
    The user enters text to a text field    id=question.title    Test title
    The user enters text to a text field    id=question.subTitle    Subtitle test
    The user enters text to a text field    id=question.guidanceTitle    Test guidance title
    The user enters text to a text field    css=.editor    Guidance text test
    The user enters text to a text field    id=question.maxWords    150

the validation error above the question should not be visible
    [Arguments]    ${QUESTION}    ${ERROR}
    focus    jQuery=.button[value="Save and close"]
    wait until element is not visible    css=error-message
    Element Should not Contain    ${QUESTION}    ${ERROR}

The competition should show in the correct section
    [Arguments]    ${SECTION}    ${COMP_NAME}
    Element should contain    ${SECTION}    ${COMP_NAME}

the user fills the milestones with invalid data
    input text    name=milestoneEntries[OPEN_DATE].day    15
    input text    name=milestoneEntries[OPEN_DATE].month    1
    input text    name=milestoneEntries[OPEN_DATE].year    2019
    input text    name=milestoneEntries[BRIEFING_EVENT].day    14
    input text    name=milestoneEntries[BRIEFING_EVENT].month    1
    input text    name=milestoneEntries[BRIEFING_EVENT].year    2019
    input text    name=milestoneEntries[SUBMISSION_DATE].day    13
    input text    name=milestoneEntries[SUBMISSION_DATE].month    1
    input text    name=milestoneEntries[SUBMISSION_DATE].year    2019
    input text    name=milestoneEntries[ALLOCATE_ASSESSORS].day    12
    input text    name=milestoneEntries[ALLOCATE_ASSESSORS].month    1
    input text    name=milestoneEntries[ALLOCATE_ASSESSORS].year    2019
    input text    name=milestoneEntries[ASSESSOR_BRIEFING].day    11
    input text    name=milestoneEntries[ASSESSOR_BRIEFING].month    1
    input text    name=milestoneEntries[ASSESSOR_BRIEFING].year    2019
    input text    name=milestoneEntries[ASSESSOR_ACCEPTS].day    10
    input text    name=milestoneEntries[ASSESSOR_ACCEPTS].month    1
    input text    name=milestoneEntries[ASSESSOR_ACCEPTS].year    2019
    input text    name=milestoneEntries[ASSESSOR_DEADLINE].day    9
    input text    name=milestoneEntries[ASSESSOR_DEADLINE].month    1
    input text    name=milestoneEntries[ASSESSOR_DEADLINE].year    2019
    input text    name=milestoneEntries[LINE_DRAW].day    8
    input text    name=milestoneEntries[LINE_DRAW].month    1
    input text    name=milestoneEntries[LINE_DRAW].year    2019
    input text    name=milestoneEntries[ASSESSMENT_PANEL].day    7
    input text    name=milestoneEntries[ASSESSMENT_PANEL].month    1
    input text    name=milestoneEntries[ASSESSMENT_PANEL].year    2019
    input text    name=milestoneEntries[PANEL_DATE].day    6
    input text    name=milestoneEntries[PANEL_DATE].month    1
    input text    name=milestoneEntries[PANEL_DATE].year    2019
    input text    name=milestoneEntries[FUNDERS_PANEL].day    5
    input text    name=milestoneEntries[FUNDERS_PANEL].month    1
    input text    name=milestoneEntries[FUNDERS_PANEL].year    2019
    input text    name=milestoneEntries[NOTIFICATIONS].day    4
    input text    name=milestoneEntries[NOTIFICATIONS].month    1
    input text    name=milestoneEntries[NOTIFICATIONS].year    2019
    input text    name=milestoneEntries[RELEASE_FEEDBACK].day    333
    input text    name=milestoneEntries[RELEASE_FEEDBACK].month    1
    input text    name=milestoneEntries[RELEASE_FEEDBACK].year    2019

Validation summary should be visible
    Then The user should see the text in the page    2. Briefing event: please enter a future date that is after the previous milestone
    And the user should see the text in the page    3. Submission date: please enter a future date that is after the previous milestone
    And the user should see the text in the page    4. Allocate accessors: please enter a future date that is after the previous milestone
    And the user should see the text in the page    5. Assessor briefing: please enter a future date that is after the previous milestone
    And the user should see the text in the page    6. Assessor accepts: please enter a future date that is after the previous milestone
    And the user should see the text in the page    7. Assessor deadline: please enter a future date that is after the previous milestone
    And the user should see the text in the page    8. Line draw: please enter a future date that is after the previous milestone
    And the user should see the text in the page    9. Assessment panel: please enter a future date that is after the previous milestone
    And the user should see the text in the page    10. Panel date: please enter a future date that is after the previous milestone
    And the user should see the text in the page    11. Funders panel: please enter a future date that is after the previous milestone
    And the user should see the text in the page    12. Notifications: please enter a future date that is after the previous milestone
    And the user should see the text in the page    13. Release feedback: please enter a valid date

the user fills the milestones with valid data
    input text    name=milestoneEntries[OPEN_DATE].day    10
    input text    name=milestoneEntries[OPEN_DATE].month    1
    input text    name=milestoneEntries[OPEN_DATE].year    2019
    input text    name=milestoneEntries[BRIEFING_EVENT].day    11
    input text    name=milestoneEntries[BRIEFING_EVENT].month    1
    input text    name=milestoneEntries[BRIEFING_EVENT].year    2019
    input text    name=milestoneEntries[SUBMISSION_DATE].day    12
    input text    name=milestoneEntries[SUBMISSION_DATE].month    1
    input text    name=milestoneEntries[SUBMISSION_DATE].year    2019
    input text    name=milestoneEntries[ALLOCATE_ASSESSORS].day    13
    input text    name=milestoneEntries[ALLOCATE_ASSESSORS].month    1
    input text    name=milestoneEntries[ALLOCATE_ASSESSORS].year    2019
    input text    name=milestoneEntries[ASSESSOR_BRIEFING].day    14
    input text    name=milestoneEntries[ASSESSOR_BRIEFING].month    1
    input text    name=milestoneEntries[ASSESSOR_BRIEFING].year    2019
    input text    name=milestoneEntries[ASSESSOR_ACCEPTS].day    15
    input text    name=milestoneEntries[ASSESSOR_ACCEPTS].month    1
    input text    name=milestoneEntries[ASSESSOR_ACCEPTS].year    2019
    input text    name=milestoneEntries[ASSESSOR_DEADLINE].day    16
    input text    name=milestoneEntries[ASSESSOR_DEADLINE].month    1
    input text    name=milestoneEntries[ASSESSOR_DEADLINE].year    2019
    input text    name=milestoneEntries[LINE_DRAW].day    17
    input text    name=milestoneEntries[LINE_DRAW].month    1
    input text    name=milestoneEntries[LINE_DRAW].year    2019
    input text    name=milestoneEntries[ASSESSMENT_PANEL].day    18
    input text    name=milestoneEntries[ASSESSMENT_PANEL].month    1
    input text    name=milestoneEntries[ASSESSMENT_PANEL].year    2019
    input text    name=milestoneEntries[PANEL_DATE].day    19
    input text    name=milestoneEntries[PANEL_DATE].month    1
    input text    name=milestoneEntries[PANEL_DATE].year    2019
    input text    name=milestoneEntries[FUNDERS_PANEL].day    20
    input text    name=milestoneEntries[FUNDERS_PANEL].month    1
    input text    name=milestoneEntries[FUNDERS_PANEL].year    2019
    input text    name=milestoneEntries[NOTIFICATIONS].day    21
    input text    name=milestoneEntries[NOTIFICATIONS].month    1
    input text    name=milestoneEntries[NOTIFICATIONS].year    2019
    input text    name=milestoneEntries[RELEASE_FEEDBACK].day    22
    input text    name=milestoneEntries[RELEASE_FEEDBACK].month    1
    input text    name=milestoneEntries[RELEASE_FEEDBACK].year    2019
    Focus    jQuery=button:contains(Done)
    sleep    500ms

the weekdays should be correct
    element should contain    css=tr:nth-child(1) td:nth-child(2)    Thu
    element should contain    css=tr:nth-child(2) td:nth-child(2)    Fri
    element should contain    css=tr:nth-child(3) td:nth-child(2)    Sat
    element should contain    css=tr:nth-child(4) td:nth-child(2)    Sun
    element should contain    css=tr:nth-child(5) td:nth-child(2)    Mon
    element should contain    css=tr:nth-child(6) td:nth-child(2)    Tue
    element should contain    css=tr:nth-child(7) td:nth-child(2)    Wed
    element should contain    css=tr:nth-child(8) td:nth-child(2)    Thu
    element should contain    css=tr:nth-child(9) td:nth-child(2)    Fri
    element should contain    css=tr:nth-child(10) td:nth-child(2)    Sat
    element should contain    css=tr:nth-child(11) td:nth-child(2)    Sun
    element should contain    css=tr:nth-child(12) td:nth-child(2)    Mon
    element should contain    css=tr:nth-child(13) td:nth-child(2)    Tue

the pre-field date should be correct
    Element Should Contain    css=#milestone-OPEN_DATE~ .js-addWeekDay    Fri
    ${YEAR} =    Get Value    css=.date-group:nth-child(1) .year .width-small
    Should Be Equal As Strings    ${YEAR}    2017
    ${MONTH} =    Get Value    css=.date-group:nth-child(1) .month .width-small
    Should Be Equal As Strings    ${MONTH}    12
    ${DAY} =    Get Value    css=.date-group:nth-child(1) .js-visited
    Should Be Equal As Strings    ${DAY}    1

the user should see the correct values in the initial details form
    ${input_value} =    Get Value    id=title
    Should Be Equal    ${input_value}    Competition title
    Page Should Contain    Programme
    Page Should Contain    Health and life sciences
    Page Should Contain    Advanced Therapies
    ${input_value} =    Get Value    id=openingDateDay
    Should Be Equal As Strings    ${input_value}    01
    ${input_value} =    Get Value    Id=openingDateMonth
    Should Be Equal As Strings    ${input_value}    12
    ${input_value} =    Get Value    id=openingDateYear
    Should Be Equal As Strings    ${input_value}    2017
    Page Should Contain    Competition Technologist One
    page should contain    Competition Executive Two
