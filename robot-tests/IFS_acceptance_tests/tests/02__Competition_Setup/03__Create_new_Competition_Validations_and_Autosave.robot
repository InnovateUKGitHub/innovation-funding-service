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
...               INFUND-3001 As a Competitions team member I want the service to automatically save my edits while I work through Initial details section in Competition Setup the so that I do not lose my changes
...
...               INFUND-4581 As a Competitions team member I want the service to automatically save my edits while I work through Funding information section in Competition Setup the so that I do not lose my changes
...
...               INFUND-4586 As a Competitions team member I want the service to automatically save my edits while I work through Application Questions section in Competition Setup the so that I do not lose my changes
...
...               INFUND-5639 As a Competitions team member I want to be able to view the Application process within the application question section in Competition Setup so that I can set up my competition using more convenient navigation
...
...               INFUND-5641 As a Competitions team member I want to be able to update the assessor setup questions so that I can amend the defaults if required for the competition
...
...               IFS-380 As a comp executive I am able to confirm if an assessment panel is required in competition setup
...
...               IFS-631 As a comp executive I am able to confirm if an interview stage is required in competition setup
Suite Setup       Custom suite setup
Suite Teardown    The user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          CompAdmin_Commons.robot

*** Test Cases ***
Initial details: server-side validations
    [Documentation]  INFUND-2982 IFUND-3888
    [Tags]    HappyPath
    Given the user navigates to the page   ${CA_UpcomingComp}
    And the user clicks the button/link    jQuery=.button:contains("Create competition")
    And The user clicks the button/link    link=Initial details
    When the user clicks the button/link   jQuery=button:contains("Done")
    Then the user should see an error    Please enter a title.
    And the user should see an error    Please select a competition type.
    And the user should see an error    Please select an innovation sector.
    And the user should see an error    Please select an innovation area.
    And the user should see an error    Please enter a valid date.
    And the user should see an error    Please select an Innovation Lead.
    And the user should see an error    Please select a Portfolio Manager.
    And the user should see an error    Please select a state aid option.

Initial details: client-side validations
    [Documentation]  INFUND-2982  INFUND-3888
    [Tags]  HappyPath
    When the user enters text to a text field    id=title    Validations Test
    Then the user should not see the error any more    Please enter a title.
    When the user selects the option from the drop-down menu    Programme    id=competitionTypeId
    Then the user should not see the error any more    Please select a competition type.
    When the user selects the option from the drop-down menu    Health and life sciences    id=innovationSectorCategoryId
    Then the user should not see the error any more    Please select an innovation sector.
    When the user selects the option from the drop-down menu    Advanced therapies    name=innovationAreaCategoryIds[0]
    Then the user should not see the error any more    Please select an innovation area.
    When the user enters text to a text field    id=openingDateDay    01
    #Then the user should not see the error any more    Please enter an opening day.
    When the user enters text to a text field    Id=openingDateMonth    12
    #Then the user should not see the error any more    Please enter an opening month.
    When the user enters text to a text field    id=openingDateYear  ${nextYear}
    #Then the user should not see the error any more    Please enter an opening year.
    When the user clicks the button twice              css=label[for="stateAid2"]
    Then the user should not see the error any more    Please select a state aid option.
    When the user selects the option from the drop-down menu    Ian Cooper    id=innovationLeadUserId
    Then the user should not see the error any more    Please select an Innovation Lead.
    When the user selects the option from the drop-down menu    John Doe     id=executiveUserId
    Then The user should not see the text in the page    Please select a Portfolio manager.    #Couldn't use this keyword : "Then the user should not see the error any more" . Because there is not any error in the page
    [Teardown]  wait for autosave

Initial details: Autosave
    [Documentation]    INFUND-3001
    [Tags]
    When the user clicks the button/link    link=Competition setup
    And the user clicks the button/link    link=Initial details
    Then the user should see the correct values in the initial details form

Initial details: should not allow dates in the past
    [Documentation]    INFUND-4682
    Given the user enters text to a text field    id=openingDateDay    01
    And the user enters text to a text field    Id=openingDateMonth    12
    And the user enters text to a text field    id=openingDateYear    2015
    And the user moves focus and waits for autosave
    When the user clicks the button/link    jQuery=button:contains("Done")
    Then The user should not see the element    jQuery=.button:contains("Edit")
    [Teardown]    #the user enters text to a text field    id=openingDateYear    2017

Initial details: mark as done
    [Documentation]  INFUND-2982 INFUND-2983 INFUND-3888
    [Tags]    HappyPath
    Given The user enters valid data in the initial details
    And the user moves focus and waits for autosave
    When the user clicks the button/link    jQuery=button:contains("Done")
    Then the user should see the element    jQuery=.button:contains("Edit")

Funding information server-side validations
    [Documentation]    INFUND-2985
    [Tags]    HappyPath
    [Setup]    The user navigates to the Validation competition
    Given the user clicks the button/link    link=Funding information
    And the user should see the text in the page    Funding information
    When the user clicks the button/link    jQuery=button:contains("Done")
    Then the user should see an error    Please enter a funder name.
    And the user should see an error    Please enter a budget.
    And the user should see an error    Please generate a competition code.

Funding information client-side validations
    [Documentation]    INFUND-2985
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Generate code")
    Then the user should not see the error any more    Please generate a competition code.
    When the user enters text to a text field    id=funders[0].funder    FunderName
    Then the user should not see the error any more    Please enter a funder name.
    And the user enters text to a text field    id=funders[0].funderBudget    20000
    And the user enters text to a text field    id=pafNumber    2016
    And the user enters text to a text field    id=budgetCode    2004
    And the user enters text to a text field    id=activityCode    4242
    Then The user should not see the error text in the page   Please enter a budget.

Funding information Autosave
    [Documentation]    INFUND-4581
    Given the user moves focus and waits for autosave
    When the user clicks the button/link    link=Competition setup
    And the user clicks the button/link    link=Funding information
    Then the user should see the correct details in the funding information form

Eligibility server-side validations
    [Documentation]    INFUND-2986
    [Tags]    HappyPath
    [Setup]    The user navigates to the Validation competition
    Given The user clicks the button/link  link = Eligibility
    When the user clicks the button/link   jQuery = button:contains("Done")
    Then The user should see a field and summary error   Please select at least one research category
    And The user should see a field and summary error    Please select a collaboration level
    And The user should see a field and summary error    Please select a lead applicant type
    And The user should see a field and summary error    Please select a resubmission option
    And The user should see a field and summary error    Please select an override funding rules option.

Eligibility funding level validation
    [Documentation]
    [Tags]  IFS-3622
    Given the user clicks the button twice              css = label[for="comp-overrideFundingRules-yes"]
    When the user clicks the button/link                jQuery = button:contains("Done")
    Then The user should see a field and summary error  Please select a funding level.

Eligibility client-side validations
    [Documentation]    INFUND-2986 INFUND-2988 INFUND-3888
    [Tags]    HappyPath
    When the user selects the checkbox    research-categories-33
    And the user selects the checkbox    research-categories-34
    And the user selects the checkbox    research-categories-35
    And the user moves focus and waits for autosave
    When the user selects the radio button    singleOrCollaborative    single
    And the user selects the checkbox  lead-applicant-type-1  #business
    And the user moves focus and waits for autosave
    And the user selects the option from the drop-down menu    50%    name=researchParticipationAmountId
    And the user moves focus and waits for autosave
    And the user clicks the button twice     css=label[for="comp-overrideFundingRules-no"]
    And the user moves focus and waits for autosave
    Then the user should not see the text in the page    Please select a collaboration level
    And the user should not see the text in the page    Please select a lead applicant type
    And the user should not see the text in the page    Please select at least one research category
    And the user moves focus and waits for autosave
    And the user should not see the text in the page    A stream name is required
    And the user selects the radio button    resubmission    no
    And the user moves focus and waits for autosave
    And the user should not see the text in the page    Please select a resubmission option
    And the user cannot see a validation error in the page

Eligibility Autosave
    [Documentation]  INFUND-4582
    [Tags]
    When the user clicks the button/link  link=Competition setup
    and the user clicks the button/link   link=Eligibility
    Then the user should see the correct details in the eligibility form

Milestones: Server side validations, submission time is default
    [Documentation]  INFUND-2993, INFUND-7632
    [Tags]  HappyPath
    [Setup]  The user navigates to the Validation competition
    Given the user clicks the button/link             link=Milestones
    When the user fills the milestones with invalid data
    And the users waits until the page is autosaved
    And the user clicks the button/link               jQuery=button:contains(Done)
    Then Validation summary should be visible
    Then the user should see the text in the element  jQuery=tr:nth-of-type(3) td:nth-of-type(1) option:selected  12:00 pm
    [Teardown]  the user clicks the button/link       link=Competition setup

Milestones: Client side validations, submission time is non-default
    [Documentation]  INFUND-2993, INFUND-7632
    [Tags]  HappyPath
    The user fills in the CS Milestones  ${month}  ${nextYear}

Milestones: Autosave
    [Documentation]  INFUND-2993 INFUND-7632
    [Tags]
    When the user clicks the button/link    link=Milestones
    Then the user should see the correct inputs in the Milestones form

Application finances: validation empty
    [Documentation]  IFS-630
    [Tags]
    [Setup]    The user navigates to the Validation competition
    Given the user clicks the button/link     link=Application
    And the user clicks the button/link       link=Finances
    And the user enters text to a text field  css=.editor  ${EMPTY}
    When the user moves focus to the element  jQuery=button:contains("Done")
    Then the user should see an error         This field cannot be left blank.
    And the user enters text to a text field  css=.editor  Funding rules for this competition added
    And the user clicks the button/link       jQuery=button:contains("Done")

Application finances: able to edit the field
    [Documentation]  IFS-630
    [Tags]
    [Setup]  The user navigates to the Validation competition
    Given the user clicks the button/link      link=Application
    And the user clicks the button/link        link=Finances
    When the user clicks the button/link       link=Edit this question
    Then the user enters text to a text field  css=.editor  Funding rules for this competition updated
    And the user clicks the button/link        jQuery=button:contains("Done")
    And the user should not see an error in the page

Assessor: Server-side validation
    [Documentation]  INFUND-5641, IFS-380, IFS-631
    [Setup]    The user navigates to the Validation competition
    Given the user clicks the button/link      link=Assessors
    When The user enters text to a text field  id=assessorPay  ${EMPTY}
    And the user clicks the button/link        jQuery=button:contains("Done")
    Then the user should see a field error     Please enter how much assessors will be paid.
    And the user should see a field error      Please select an assessment panel option.
    And the user should see a field error      Please select an interview stage option.
    When the user selects the radio button     hasAssessmentPanel  hasAssessmentPanel-0
    Then the user selects the radio button     hasInterviewStage  hasInterviewStage-0
    And the user clicks the button/link        jQuery=button:contains("Done")

Assessor: Client-side validation
    [Documentation]  INFUND-5641
    When The user enters text to a text field  id=assessorPay  1.1
    And the user selects the radio button      assessorCount   5
    Then the user should see an error          This field can only accept whole numbers
    When The user enters text to a text field  id=assessorPay  120
    And the user selects the radio button      assessorCount   5
    Then The user should not see the text in the page  This field can only accept whole numbers
    And the user clicks the button/link        link = Competition setup

Documents in project setup: The competition admin is required to enter a title and guidance message
    [Documentation]
    [Tags]
    Given the user clicks the button/link       link = Documents in project setup
    And the user clicks the button/link         link = Add document type
    When the user clicks the button/link        css = button[type = "submit"]
    Then the user should see the group of errors

Documents in project setup: The competition admin addresses the errors
    [Documentation]
    [Tags]
    Given the user enters text to a text field    id = title    Test document type
    And the user moves focus and waits for autosave
    Then the user should not see the element      jQuery = a:contains("Please enter a title.")
    When the user clicks the button/link          jQuery = span:contains("PDF")
    #And the user moves focus and waits for autosave
    Then the user should not see the element      jQuery = a:contains("You need to select at least one file type.")
    When the user enters text to a text field     css = .editor    Guidance test.
    And the user moves focus and waits for autosave
    Then the user should not see the element      jQuery = a:contains("Please enter guidance for the applicant.")

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${month} =  get tomorrow month
    set suite variable  ${month}
    ${nextYear} =  get next year
    Set suite variable  ${nextYear}
    ${tomorrowMonthWord} =  get tomorrow month as word
    set suite variable  ${tomorrowMonthWord}


the user moves focus and waits for autosave
    focus    link=Sign out
    Wait For Autosave

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
    Wait Until Element Is Not Visible Without Screenshots    css=error-message
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
    the user should see a summary error  2. Briefing event: Please enter a future date that is after the previous milestone.
    the user should see a summary error  3. Submission date: Please enter a future date that is after the previous milestone.
    the user should see a summary error  4. Allocate assessors: Please enter a future date that is after the previous milestone.
    the user should see a summary error  5. Assessor briefing: Please enter a future date that is after the previous milestone.
    the user should see a summary error  6. Assessor accepts: Please enter a future date that is after the previous milestone.
    the user should see a summary error  7. Assessor deadline: Please enter a future date that is after the previous milestone.
    the user should see a summary error  8. Line draw: Please enter a future date that is after the previous milestone.
    the user should see a summary error  9. Assessment panel: Please enter a future date that is after the previous milestone.
    the user should see a summary error  10. Panel date: Please enter a future date that is after the previous milestone.
    the user should see a summary error  11. Funders panel: Please enter a future date that is after the previous milestone.
    the user should see a summary error  12. Notifications: Please enter a future date that is after the previous milestone.
    the user should see a summary error  13. Release feedback: Please enter a future date that is after the previous milestone.

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
    The user selects the index from the drop-down menu    1    id=milestoneEntries[SUBMISSION_DATE].time
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

the user should see the correct values in the initial details form
    the user should see the element  css=#title[value="Validations Test"]
    the user should see the element  jQuery=#competitionTypeId option[selected]:contains("Programme")
    the user should see the element  jQuery=#innovationSectorCategoryId option[selected]:contains("life sciences")
    the user should see the element  jQuery=[name^="innovationAreaCategoryIds"]:contains("Advanced therapies")
    the user should see the element  css=#openingDateDay[value="1"]
    the user should see the element  css=#openingDateMonth[value="12"]
    the user should see the element  css=#openingDateYear[value="${nextYear}"]
    the user should see the element  jQuery=#innovationLeadUserId option[selected]:contains("Ian Cooper")
    the user should see the element  jQuery=#executiveUserId option[selected]:contains("John Doe")

the user should see the correct details in the funding information form
    ${input_value} =    Get Value    id=funders[0].funder
    Should Be Equal    ${input_value}    FunderName
    ${input_value} =    Get Value    id=funders[0].funderBudget
    Should Be Equal As Strings    ${input_value}    20000
    ${input_value} =    Get Value    id=pafNumber
    Should Be Equal As Strings    ${input_value}    2016
    ${input_value} =    Get Value    id=budgetCode
    Should Be Equal As Strings    ${input_value}    2004
    ${input_value} =    Get Value    id=activityCode
    Should Be Equal As Strings    ${input_value}    4242

the user should see the correct details in the eligibility form
    the user sees that the radio button is selected    singleOrCollaborative    single
    the user should see that the checkbox is selected   research-categories-33
    the user should see that the checkbox is selected   research-categories-34
    the user should see that the checkbox is selected   research-categories-35
    the user should see that the checkbox is selected   lead-applicant-type-1  # business
    Page Should Contain    50%
    the user sees that the radio button is selected    resubmission    no

The user should not see the error text in the page
    [Arguments]    ${ERROR_TEXT}
    Run Keyword And Ignore Error Without Screenshots    mouse out    css=input
    Focus    jQuery=button:contains("Done")
    Wait Until Page Does Not Contain Without Screenshots    ${ERROR_TEXT}

the users waits until the page is autosaved
    Focus    jQuery=button:contains(Done)
    Wait For Autosave

the user should see the correct inputs in the Milestones form
    the user should see the element  jQuery=tr:contains("Open date") td:contains("${tomorrowMonthWord} ${nextyear}")
    the user should see the element  jQuery=tr:contains("Briefing event") td:contains("${tomorrowMonthWord} ${nextyear}")
    the user should see the element  jQuery=tr:contains("Submission date") td:contains("12:00 pm") ~ td:contains("${tomorrowMonthWord} ${nextyear}")
    the user should see the element  jQuery=button:contains("Edit")

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
    And the user selects the option from the drop-down menu    Advanced therapies    name=innovationAreaCategoryIds[0]
    And the user enters text to a text field    id=openingDateDay    01
    And the user enters text to a text field    Id=openingDateMonth    12
    And the user enters text to a text field    id=openingDateYear  ${nextYear}
    And the user selects the option from the drop-down menu    Ian Cooper    id=innovationLeadUserId
    And the user selects the option from the drop-down menu    John Doe    id=executiveUserId

The user navigates to the Validation competition
    The user navigates to the page     ${CA_UpcomingComp}
    The user clicks the button/link    link=Validations Test

the user should not see the error any more
    [Arguments]    ${ERROR_TEXT}
    Run Keyword And Ignore Error Without Screenshots    mouse out    css=input
    Focus    jQuery=button:contains("Done")
    Wait for autosave
    Wait Until Element Does Not Contain Without Screenshots    css=.error-message    ${ERROR_TEXT}

the user should see the group of errors
    the user should see a summary error    Please enter guidance for the applicant.
    the user should see a summary error    Please enter a title.
#    the user should see a summary error    You need to select at least one file type.
