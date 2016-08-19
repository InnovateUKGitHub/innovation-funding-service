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
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin    CompSetup
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Test Cases ***
User can navigate to the competition setup form
    [Documentation]    INFUND-2945
    ...
    ...
    ...    INFUND-2982
    ...
    ...
    ...    INFUND-2983
    ...
    ...
    ...    INFUND-2986
    ...
    ...
    ...    IFUND-3888
    ...
    ...
    ...    INFUND-3002  As a Competition Executive and I have added all information in all obligatory fields I want to mark the competition ready for open.
    [Tags]    HappyPath
    Given the user clicks the button/link    id=section-3
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    Then the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    When the user clicks the button/link    link=Initial Details
    Then the user redirects to the page    Initial details    This will create a new Competition
    And the user should not see the element    css=#stateAid

Competition code validation
    [Documentation]    INFUND-2985
    ...
    ...    INFUND-3182
    ...
    ...    IFUND-3888
    [Setup]    The user clicks the button/link    css=.next a
    When the user clicks the button/link    jQuery=.button:contains("Generate code")
    Then the user should see an error    Please set a start date for your competition before generating the competition code, you can do this in the Initial Details section
    [Teardown]    The user clicks the button/link    css=.prev a

Initial details server-side validations
    [Documentation]    INFUND-2982
    ...
    ...    IFUND-3888
    [Tags]
    Given the user should not see the element    css=#stateAid
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
    [Tags]    HappyPath
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
    # TODO Update Date validation messages after INFUND-4676 is done.
    When the user enters text to a text field    id=title    Competition title
    Then the user should not see the error any more    Please enter a title
    When the user selects the option from the drop-down menu    Additive Manufacturing    id=competitionTypeId
    Then the user should not see the error any more    Please select a competition type
    When the user selects the option from the drop-down menu    Health and life sciences    id=innovationSectorCategoryId
    Then the user should not see the error any more    Please select an innovation sector
    When the user selects the option from the drop-down menu    Advanced Therapies    id=innovationAreaCategoryId
    Then the user should not see the error any more    Please select an innovation area
    When the user enters text to a text field    id=openingDateDay    01
#    Then the user should not see the error any more    Please enter an opening day
    When the user enters text to a text field    Id=openingDateMonth    12
#    Then the user should not see the error any more    Please enter an opening month
    When the user enters text to a text field    id=openingDateYear    2017
#    Then the user should not see the error any more    Please enter an opening year
    When the user selects the option from the drop-down menu    Competition Technologist One    id=leadTechnologistUserId
    Then the user should not see the error any more    Please select a lead technologist
    When the user selects the option from the drop-down menu    Competition Executive Two    id=executiveUserId
    Then the user should not see the error any more    Please select a competition executive
    ##    State aid value is tested in 'Initial details correct state aid status'

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
    And the user should see the text in the page    Additive Manufacturing
    And the user should see the text in the page    NO
    And the user should see the element    jQuery=.button:contains("Edit")

Initial details can be edited again
    [Documentation]    INFUND-2985
    ...
    ...    INFUND-3182
    ...    INFUND-3876
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Edit")
    And the user enters text to a text field    id=title    Test competition
    And the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page    1/12/2017
    And the user should see the text in the page    Competition Technologist One
    And the user should see the text in the page    Test competition
    And the user should see the text in the page    Health and life sciences
    And the user should see the text in the page    Advanced Therapies
    And the user should see the text in the page    Additive Manufacturing
    And the user should see the text in the page    NO
    When The user clicks the button/link    link=Competition set up
    Then the user should not see the element    jQuery=.button:contains("Save as Ready To Open")

Funding information server-side validations
    [Documentation]    INFUND-2985
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
    # TODO Update validation messages after INFUND-4676 is done.
    When the user enters text to a text field    id=funder    FunderName
    Then the user should not see the error any more    Please enter a funder name
    And the user enters text to a text field    id=funderBudget    20000
    Then the user should not see the error any more    Please enter a budget
    When the user enters text to a text field    id=pafNumber    2016
    Then the user should not see the error any more    Please enter a PAF number
    And the user enters text to a text field    id=budgetCode    2004
    Then the user should not see the error any more    Please enter a budget code
    And the user enters text to a text field    id=activityCode    4242
#    Then the user should not see the error any more    Please enter an activity code
    When the user clicks the button/link    jQuery=.button:contains("Generate code")
#    Then The user should not see the text in the page    Please generate a competition code

Funding informations calculations
    [Documentation]    INFUND-2985
    When the user clicks the button/link    jQuery=Button:contains("+Add co-funder")
    and the user should see the element    jQuery=Button:contains("+Add co-funder")
    Then the user should see the element    css=#co-funder-row-0
    And the user enters text to a text field    id=0-funder    FunderName2
    And the user enters text to a text field    id=0-funderBudget    1000
    Then the total should be correct    £ 21,000

Funding Information can be saved
    [Documentation]    INFUND-3182
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
    [Documentation]    INFUND-3876
    When the user clicks the button/link        jQuery=.button:contains("Edit")
    And the user enters text to a text field    id=funder    testFunder
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page   testFunder
    When the user clicks the button/link    link=Competition set up
    Then the user should not see the element    jQuery=.button:contains("Save as Ready To Open")

Eligibility page should contain the correct options
    [Documentation]    INFUND-2989
    ...
    ...    INFUND-2990
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
    [Tags]
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
    [Tags]
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
    ...    INFUND-3876
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then the user should see the text in the page    Yes
    And the user should see the text in the page    Single
    And the user should see the text in the page    Business
    And the user should see the text in the page    30%
    And the user should see the text in the page    Test stream name
    And the user should see the text in the page    Technical feasibility, Industrial research, Experimental development
    And The user should not see the element    id=streamName
    When the user clicks the button/link    link=Competition set up
    Then the user should see the element    jQuery=.button:contains("Save as Ready To Open")
    When the user clicks the button/link    link=Eligibility
    And the user clicks the button/link    jQuery=.button:contains("Edit")
    When the user clicks the button/link    link=Competition set up
    Then the user should not see the element    jQuery=.button:contains("Save as Ready To Open")
    When the user clicks the button/link    link=Eligibility
    And the user clicks the button/link    jQuery=.button:contains("Done")
    [Teardown]    The user clicks the button/link    link=Competition set up

Save as Ready To Open button
    [Documentation]    INFUND-3876
    [Setup]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}
    Given the user should see the element        jQuery=.button:contains("Save as Ready To Open")
    When the user clicks the button/link         jQuery=.button:contains("Save as Ready To Open")
    Then the user should see the element         jQuery=img.section-status:eq(0)
    And the user should see the element          jQuery=img.section-status:eq(1)
    And the user should see the element          jQuery=img.section-status:eq(2)
    When the user clicks the button/link         link=All competitions
    And the user clicks the button/link          id=section-3
    Then element text should be                  //*[@id="content"]/section[2]/div/div/ul/li[1]/div[1]/h3/a    Test competition
    [Teardown]    the user navigates to the page    ${COMP_MANAGEMENT_COMP_SETUP}

Application questions: All the sections should be visible
    [Documentation]    INFUND-3000
    [Tags]    Pending
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

Application questions: server side validations
    [Documentation]    INFUND-3000
    [Tags]    Pending
    Given The user clicks the button/link    jQuery=li:nth-child(5) .button:contains(Edit)
    And The user should see the element    jQuery=.button[value="Save and close"]
    When the user leaves all the question field empty
    And The user clicks the button/link    jQuery=.button[value="Save and close"]
    And The user clicks the button/link    jQuery=.button[value="Save and close"]
    Then the validation error above the question should be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    #To do: investigate why this step fails with chrome driver    INFUND-4514
    And the validation error above the question should be visible    jQuery=div:nth-child(4) div:nth-child(4) label:contains(Question guidance)    This field cannot be left blank

Application questions: Client side validations
    [Documentation]    INFUND-3000
    [Tags]    Pending
    Given the user fills the empty question fields
    Then the validation error above the question should not be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=div:nth-child(4) div:nth-child(4) label:contains(Question guidance)    This field cannot be left blank
    And The user enters text to a text field    id=question.maxWords    ${EMPTY}
    And the validation error above the question should be visible    jQuery=label:contains(Max word count)    This field cannot be left blank
    And input text    jQuery=[id="question.maxWords"]    150
    And the validation error above the question should not be visible    jQuery=label:contains(Max word count)    This field cannot be left blank

Application questions: Mark as done and the Edit again
    [Documentation]    INFUND-3000
    [Tags]    Pending
    [Setup]    The user clicks the button/link    jQuery=.grid-row div:nth-child(2) label:contains(Yes)
    When The user clicks the button/link    jQuery=.button[value="Save and close"]
    Then The user should see the text in the page    Test title
    And the user should see the text in the page    Subtitle test
    And the user should see the text in the page    Test guidance title
    And the user should see the text in the page    Guidance text test
    And the user should see the text in the page    150
    And the user should see the text in the page    Yes

*** Keywords ***
the user moves focus to a different part of the page
    focus    link=Sign out

the user should not see the error any more
    [Arguments]    ${ERROR_TEXT}
    run keyword and ignore error    mouse out    css=input
    Focus    jQuery=.button:contains("Done")
    sleep    200ms
    Wait Until Element Does Not Contain    css=.error-message    ${ERROR_TEXT}

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
