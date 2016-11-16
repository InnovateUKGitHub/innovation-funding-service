*** Settings ***
Documentation     INFUND-2982: Create a Competition: Step 1: Initial details
...
...               INFUND-2983: As a Competition Executive I want to be informed if the competition will fall under State Aid when I select a 'Competition type' in competition setup
...
...               INFUND-2986 Create a Competition: Step 3: Eligibility
...
...               IFUND-3888 Rearrangement of Competitions setup
...
...               INFUND-4682 Initial details can be saved with an opening date in the past
...
...               INFUND-2993 As a competitions team member I want to be able to add milestones when creating my competition so these can be used manage its progress
...
...               INFUND-3001 As a Competitions team member I want the service to automatically save my edits while I work through Initial Details section in Competition Setup the so that I do not lose my changes
...
...               INFUND-4581 As a Competitions team member I want the service to automatically save my edits while I work through Funding Information section in Competition Setup the so that I do not lose my changes
...
...               INFUND-4586 As a Competitions team member I want the service to automatically save my edits while I work through Application Questions section in Competition Setup the so that I do not lose my changes
...
...               INFUND-5639 As a Competitions team member I want to be able to view the Application process within the application question section in Competition Setup so that I can set up my competition using more convenient navigation
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Initial details: server-side validations
    [Documentation]    INFUND-2982
    ...
    ...    IFUND-3888
    [Tags]    HappyPath
    Given the user clicks the button/link    id=section-3
    And the user clicks the button/link    jQuery=.button:contains("Create competition")
    And The user clicks the button/link    link=Initial Details
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see an error    Please enter a title
    And the user should see an error    Please select a competition type
    And the user should see an error    Please select an innovation sector
    And the user should see an error    Please select an innovation area
    And the user should see an error    Please enter an opening year
    And the user should see an error    Please enter an opening day
    And the user should see an error    Please enter an opening month
    And the user should see an error    Please select an Innovation Lead
    And the user should see an error    Please select a competition executive

Initial details: client-side validations
    [Documentation]    INFUND-2982
    ...
    ...    INFUND-3888
    [Tags]    HappyPath
    #TODO Remove the comments when the inf-5327 is fixed
    When the user enters text to a text field    id=title    Validations Test
    Then the user should not see the error any more    Please enter a title
    When the user selects the option from the drop-down menu    Programme    id=competitionTypeId
    Then the user should not see the error any more    Please select a competition type
    When the user selects the option from the drop-down menu    Health and life sciences    id=innovationSectorCategoryId
    Then the user should not see the error any more    Please select an innovation sector
    When the user selects the option from the drop-down menu    Advanced Therapies    id=innovationAreaCategoryId
    Then the user should not see the error any more    Please select an innovation area
    When the user enters text to a text field    id=openingDateDay    01
    #Then the user should not see the error any more    Please enter an opening day
    When the user enters text to a text field    Id=openingDateMonth    12
    #Then the user should not see the error any more    Please enter an opening month
    When the user enters text to a text field    id=openingDateYear    2017
    #Then the user should not see the error any more    Please enter an opening year
    When the user selects the option from the drop-down menu    Competition Technologist One    id=leadTechnologistUserId
    Then the user should not see the error any more    Please select a lead technologist
    When the user selects the option from the drop-down menu    Competition Executive Two    id=executiveUserId
    Then The user should not see the text in the page    Please select a competition executive    #Couldn't use this keyword : "Then the user should not see the error any more" . Because there is not any error in the page
    ##    State aid value is tested in 'Initial details correct state aid status'

Initial details: Autosave
    [Documentation]    INFUND-3001
    [Tags]    Pending
    # TODO pending due Ito NFUND-5367
    When the user clicks the button/link    link=Competition setup
    and the user clicks the button/link    link=Initial Details
    Then the user should see the correct values in the initial details form

Initial details: should not allow dates in the past
    [Documentation]    INFUND-4682
    Given the user enters text to a text field    id=openingDateDay    01
    And the user enters text to a text field    Id=openingDateMonth    12
    And the user enters text to a text field    id=openingDateYear    2015
    And the user moves focus and waits for autosave
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then The user should not see the element    jQuery=.button:contains("Edit")
    [Teardown]    #the user enters text to a text field    id=openingDateYear    2017

Initial details: mark as done
    [Documentation]    INFUND-2982
    ...
    ...    INFUND-2983
    ...
    ...    INFUND-3888
    [Tags]    HappyPath
    Given The user enters valid data in the initial details
    And the user moves focus and waits for autosave
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the element    jQuery=.button:contains("Edit")

Funding information server-side validations
    [Documentation]    INFUND-2985
    [Tags]    HappyPath
    [Setup]    The user navigates to the Validation competition
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
    When the user clicks the button/link    jQuery=.button:contains("Generate code")
    Then the user should not see the error any more    Please generate a competition code
    When the user enters text to a text field    id=funders0.funder    FunderName
    Then the user should not see the error any more    Please enter a funder name
    And the user enters text to a text field    id=0-funderBudget    20000
    Then the user should not see the error any more    Please enter a budget
    When the user enters text to a text field    id=pafNumber    2016
    Then the user should not see the error any more    Please enter a PAF number
    And the user enters text to a text field    id=budgetCode    2004
    Then the user should not see the error any more    Please enter a budget code
    And the user enters text to a text field    id=activityCode    4242
    Then The user should not see the error text in the page    Please enter an activity code

Funding information Autosave
    [Documentation]    INFUND-4581
    Given the user moves focus and waits for autosave
    When the user clicks the button/link    link=Competition setup
    And the user clicks the button/link    link=Funding Information
    Then the user should see the correct details in the funding information form

Eligibility server-side validations
    [Documentation]    INFUND-2986
    [Tags]    HappyPath
    [Setup]    The user navigates to the Validation competition
    Given The user clicks the button/link    link=Eligibility
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page    Please select at least one research category
    And the user should see the text in the page    Please select a collaboration level
    And the user should see the text in the page    Please select a lead applicant type
    And the user should see the text in the page    Please select a resubmission option

Eligibility client-side validations
    [Documentation]    INFUND-2986
    ...
    ...    IINFUND-2988
    ...
    ...    INFUND-3888
    [Tags]    HappyPath
    When the user selects the checkbox    id=research-categories-33
    And the user selects the checkbox    id=research-categories-34
    And the user selects the checkbox    id=research-categories-35
    And the user moves focus and waits for autosave
    When the user selects the radio button    singleOrCollaborative    single
    And the user selects the radio button    leadApplicantType    business
    And the user moves focus and waits for autosave
    And the user selects the option from the drop-down menu    50%    name=researchParticipationAmountId
    And the user moves focus and waits for autosave
    Then the user should not see the text in the page    Please select a collaboration level
    And the user should not see the text in the page    Please select a lead applicant type
    And the user should not see the text in the page    Please select at least one research category
    And the user moves focus and waits for autosave
    And the user should not see the text in the page    A stream name is required
    And the user selects the radio button    resubmission    no
    And the user should not see the text in the page    Please select a resubmission option

Eligibility Autosave
    [Documentation]    INFUND-4582
    [Tags]
    When the user clicks the button/link    link=Competition setup
    and the user clicks the button/link    link=Eligibility
    Then the user should see the correct details in the eligibility form

Milestones: Server side validations
    [Documentation]    INFUND-2993
    [Tags]    HappyPath
    [Setup]    The user navigates to the Validation competition
    Given the user clicks the button/link    link=Milestones
    When the user fills the milestones with invalid data
    And the users waits until the page is autosaved
    And the user clicks the button/link    jQuery=button:contains(Done)
    Then Validation summary should be visible

Milestones: Client side validations
    [Documentation]    INFUND-2993
    [Tags]    HappyPath
    When the user fills the milestones with valid data
    Then The user should not see the text in the page    please enter a future date that is after the previous milestone
    Then The user should not see the text in the page    please enter a valid date

Milestones: Autosave
    [Tags]
    When the user clicks the button/link    link=Competition setup
    And the user clicks the button/link    link=Milestones
    Then the user should see the correct inputs in the Milestones form

*** Keywords ***
the user moves focus and waits for autosave
    focus    link=Sign out
    sleep    500ms
    Wait For Autosave

the user leaves all the question field empty
    Clear Element Text    css=.editor
    Press Key    css=.editor    \\8
    focus    jQuery=.button[value="Save and close"]
    sleep    200ms
    The user enters text to a text field    id=question.title    ${EMPTY}
    The user enters text to a text field    id=question.guidanceTitle    ${EMPTY}
    The user enters text to a text field    jQuery=[id="question.maxWords"]    ${EMPTY}
    the user moves focus and waits for autosave

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

the user fills the milestones with invalid data
    The user enters text to a text field    name=milestoneEntries[OPEN_DATE].day    15
    The user enters text to a text field    name=milestoneEntries[OPEN_DATE].month    1
    The user enters text to a text field    name=milestoneEntries[OPEN_DATE].year    2019
    The user enters text to a text field    name=milestoneEntries[BRIEFING_EVENT].day    14
    The user enters text to a text field    name=milestoneEntries[BRIEFING_EVENT].month    1
    The user enters text to a text field    name=milestoneEntries[BRIEFING_EVENT].year    2019
    The user enters text to a text field    name=milestoneEntries[SUBMISSION_DATE].day    13
    The user enters text to a text field    name=milestoneEntries[SUBMISSION_DATE].month    1
    The user enters text to a text field    name=milestoneEntries[SUBMISSION_DATE].year    2019
    The user enters text to a text field    name=milestoneEntries[ALLOCATE_ASSESSORS].day    12
    The user enters text to a text field    name=milestoneEntries[ALLOCATE_ASSESSORS].month    1
    The user enters text to a text field    name=milestoneEntries[ALLOCATE_ASSESSORS].year    2019
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_BRIEFING].day    11
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_BRIEFING].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_BRIEFING].year    2019
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_ACCEPTS].day    10
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_ACCEPTS].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_ACCEPTS].year    2019
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_DEADLINE].day    9
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_DEADLINE].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSOR_DEADLINE].year    2019
    The user enters text to a text field    name=milestoneEntries[LINE_DRAW].day    8
    The user enters text to a text field    name=milestoneEntries[LINE_DRAW].month    1
    The user enters text to a text field    name=milestoneEntries[LINE_DRAW].year    2019
    The user enters text to a text field    name=milestoneEntries[ASSESSMENT_PANEL].day    7
    The user enters text to a text field    name=milestoneEntries[ASSESSMENT_PANEL].month    1
    The user enters text to a text field    name=milestoneEntries[ASSESSMENT_PANEL].year    2019
    The user enters text to a text field    name=milestoneEntries[PANEL_DATE].day    6
    The user enters text to a text field    name=milestoneEntries[PANEL_DATE].month    1
    The user enters text to a text field    name=milestoneEntries[PANEL_DATE].year    2019
    The user enters text to a text field    name=milestoneEntries[FUNDERS_PANEL].day    5
    The user enters text to a text field    name=milestoneEntries[FUNDERS_PANEL].month    1
    The user enters text to a text field    name=milestoneEntries[FUNDERS_PANEL].year    2019
    The user enters text to a text field    name=milestoneEntries[NOTIFICATIONS].day    4
    The user enters text to a text field    name=milestoneEntries[NOTIFICATIONS].month    1
    The user enters text to a text field    name=milestoneEntries[NOTIFICATIONS].year    2019
    The user enters text to a text field    name=milestoneEntries[RELEASE_FEEDBACK].day    3
    The user enters text to a text field    name=milestoneEntries[RELEASE_FEEDBACK].month    1
    The user enters text to a text field    name=milestoneEntries[RELEASE_FEEDBACK].year    2018

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
    And the user should see the text in the page    13. Release feedback: please enter a future date that is after the previous milestone

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
    sleep    500ms

the user should see the correct values in the initial details form
    ${input_value} =    Get Value    id=title
    Should Be Equal    ${input_value}    Validations Test
    Page Should Contain    Programme
    Page Should Contain    Health and life sciences
    Page Should Contain    Advanced Therapies
    ${input_value} =    Get Value    id=openingDateDay
    Should Be Equal As Strings    ${input_value}    1
    ${input_value} =    Get Value    Id=openingDateMonth
    Should Be Equal As Strings    ${input_value}    12
    ${input_value} =    Get Value    id=openingDateYear
    Should Be Equal As Strings    ${input_value}    2017
    Page Should Contain    Competition Technologist One
    page should contain    Competition Executive Two

the user should see the correct details in the funding information form
    ${input_value} =    Get Value    id=funders0.funder
    Should Be Equal    ${input_value}    FunderName
    ${input_value} =    Get Value    id=0-funderBudget
    Should Be Equal As Strings    ${input_value}    20000.00
    ${input_value} =    Get Value    id=pafNumber
    Should Be Equal As Strings    ${input_value}    2016
    ${input_value} =    Get Value    id=budgetCode
    Should Be Equal As Strings    ${input_value}    2004
    ${input_value} =    Get Value    id=activityCode
    Should Be Equal As Strings    ${input_value}    4242

the user should see the correct details in the eligibility form
    the user sees that the radio button is selected    singleOrCollaborative    single
    Checkbox Should Be Selected    id=research-categories-33
    Checkbox Should Be Selected    id=research-categories-34
    Checkbox Should Be Selected    id=research-categories-35
    the user sees that the radio button is selected    leadApplicantType    business
    Page Should Contain    50%
    the user sees that the radio button is selected    resubmission    no

The user should not see the error text in the page
    [Arguments]    ${ERROR_TEXT}
    run keyword and ignore error    mouse out    css=input
    Focus    jQuery=.button:contains("Done")
    Wait Until Page Does Not Contain    ${ERROR_TEXT}

the users waits until the page is autosaved
    Focus    jQuery=button:contains(Done)
    sleep    1s
    Wait For Autosave

the user should see the correct inputs in the Milestones form
    Element Should Contain    css=tr:nth-of-type(1) td:nth-of-type(1)    Thu
    Element Should Contain    css=tr:nth-of-type(2) td:nth-of-type(1)    Fri
    Element Should Contain    css=tr:nth-of-type(3) td:nth-of-type(1)    Sat
    Element Should Contain    css=tr:nth-of-type(4) td:nth-of-type(1)    Sun
    Element Should Contain    css=tr:nth-of-type(5) td:nth-of-type(1)    Mon
    Element Should Contain    css=tr:nth-of-type(6) td:nth-of-type(1)    Tue
    Element Should Contain    css=tr:nth-of-type(7) td:nth-of-type(1)    Wed
    Element Should Contain    css=tr:nth-of-type(8) td:nth-of-type(1)    Thu
    Element Should Contain    css=tr:nth-of-type(9) td:nth-of-type(1)    Fri
    Element Should Contain    css=tr:nth-of-type(10) td:nth-of-type(1)    Sat
    Element Should Contain    css=tr:nth-of-type(11) td:nth-of-type(1)    Sun
    Element Should Contain    css=tr:nth-of-type(12) td:nth-of-type(1)    Mon
    Element Should Contain    css=tr:nth-of-type(13) td:nth-of-type(1)    Tue

the user should see the correct inputs in the Applications questions form
    ${input_value} =    Get Value    id=question.title
    Should Be Equal    ${input_value}    Test title
    ${input_value} =    Get Value    id=question.subTitle
    Should Be Equal    ${input_value}    Subtitle test
    ${input_value} =    Get Value    id=question.guidanceTitle
    Should Be Equal    ${input_value}    Test guidance title
    ${input_value} =    Get Value    css=.editor
    Should Be Equal    ${input_value}    Guidance text test
    ${input_value} =    Get Value    id=question.maxWords
    Should Be Equal    ${input_value}    150

The user enters valid data in the initial details
    Given the user enters text to a text field    id=title    Validations Test
    And the user selects the option from the drop-down menu    Programme    id=competitionTypeId
    And the user selects the option from the drop-down menu    Health and life sciences    id=innovationSectorCategoryId
    And the user selects the option from the drop-down menu    Advanced Therapies    id=innovationAreaCategoryId
    And the user enters text to a text field    id=openingDateDay    01
    And the user enters text to a text field    Id=openingDateMonth    12
    And the user enters text to a text field    id=openingDateYear    2017
    And the user selects the option from the drop-down menu    Competition Technologist One    id=leadTechnologistUserId
    And the user selects the option from the drop-down menu    Competition Executive Two    id=executiveUserId

The user navigates to the Validation competition
    The user navigates to the page    ${SERVER}/management/dashboard/upcoming
    The user clicks the button/link    link=Validations Test

the user should not see the error any more
    [Arguments]    ${ERROR_TEXT}
    run keyword and ignore error    mouse out    css=input
    Focus    jQuery=.button:contains("Done")
    Wait for autosave
    Wait Until Element Does Not Contain    css=.error-message    ${ERROR_TEXT}
    sleep    500ms
