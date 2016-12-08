*** Settings ***
Documentation     INFUND-5629 As a Competitions team member I want to be able to edit Application Questions individually in Competition Setup so that I can manage the question and associated applicant and assessor guidance in one place
Suite Setup       Run Keywords    Guest user log-in    &{Comp_admin1_credentials}
...               AND    User creates a new competition for Application tests
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Business opportunity Server-side validations
    [Documentation]    INFUND-5629 INFUND-5685
    [Tags]    HappyPath
    Given The user clicks the button/link    link=Application
    And The user clicks the button/link    link=Business opportunity
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    When the user leaves all the question field empty
    And the user leaves all the assesment questions empty
    And The user clicks the button/link    jQuery=.button[value="Save and close"]
    Then the validation error above the question should be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=label:contains(Question guidance)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=label:contains(Max word count)    This field cannot be left blank
    And the user should see the text in the page    Please enter a from score
    And the user should see the text in the page    Please enter a to score
    And the user should see the text in the page    Please enter a justification

Business opportunity: Client side validations
    [Documentation]    INFUND-5629 INFUND-5685
    [Tags]    HappyPath
    And the user fills the empty assessment fields
    When the user fills the empty question fields
    Then the validation error above the question should not be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Question guidance)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Max word count)    This field cannot be left blank
    And the user should not see the text in the page   Please enter a from score
    And the user should not see the text in the page   Please enter a to score
    And the user should not see the text in the page   Please enter a justification

Business opportunity: Autosave
    [Documentation]    INFUND-5629 INFUND-5685
    [Tags]    Failing
    #TODO work out why its failing!
    Given the user moves focus and waits for autosave
    When the user clicks the button/link    link=Application
    And The user clicks the button/link    link=Business opportunity
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    Then the user should see the correct inputs in the Applications questions form
    And the user should see the correct inputs in assesment questions

Business opportunity: Mark as done
    [Documentation]    INFUND-5629
    [Tags]    HappyPath
    When The user clicks the button/link    jQuery=.button[value="Save and close"]
    And the user clicks the button/link    link=Business opportunity
    Then The user should see the text in the page    Business opportunity
    And The user should see the text in the page    Test title
    And The user should see the text in the page    Subtitle test
    And The user should see the text in the page    Test guidance title
    And The user should see the text in the page    Guidance text test
    And The user should see the text in the page    150
    And The user should see the text in the page    No

*** Keywords ***
the user leaves all the question field empty
    Clear Element Text    css=.editor
    Press Key    css=.editor    \\8
    focus    jQuery=.button[value="Save and close"]
    sleep    200ms
    The user enters text to a text field    id=question.title    ${EMPTY}
    The user enters text to a text field    id=question.guidanceTitle    ${EMPTY}
    The user enters text to a text field    jQuery=[id="question.maxWords"]    ${EMPTY}

The user leaves all the assesment questions empty
    The user enters text to a text field    id=guidanceRow-0-scorefrom    ${EMPTY}
    The user enters text to a text field    id=guidanceRow-0-scoreto    ${EMPTY}
    the user enters text to a text field    id=guidanceRow-0-justification    ${EMPTY}


the validation error above the question should be visible
    [Arguments]    ${QUESTION}    ${ERROR}
    Element Should Contain    ${QUESTION}    ${ERROR}

the validation error above the question should not be visible
    [Arguments]    ${QUESTION}    ${ERROR}
    focus    jQuery=.button[value="Save and close"]
    wait until element is not visible    css=error-message
    Element Should not Contain    ${QUESTION}    ${ERROR}

the user moves focus and waits for autosave
    focus    link=Sign out
    sleep    500ms
    Wait For Autosave

the user should see the correct inputs in the Applications questions form
    ${input_value} =    Get Value    id=question.title
    Should Be Equal    ${input_value}    Test title
    ${input_value} =    Get Value    id=question.subTitle
    Should Be Equal    ${input_value}    Subtitle test
    ${input_value} =    Get Value    id=question.guidanceTitle
    Should Be Equal    ${input_value}    Test guidance title
    ${input_value} =    Get Value    id=question.guidance
    Should Be Equal    ${input_value}    Guidance text test
    ${input_value} =    Get Value    id=question.maxWords
    Should Be Equal    ${input_value}    150

The user should see the correct inputs in assesment questions
    ${input_value} =    Get Value    id=guidanceRow-0-scorefrom
    Should Be Equal    ${input_value}    30
    ${input_value} =    Get Value    id=guidanceRow-0-scoreto
    Should Be Equal    ${input_value}    35
    ${input_value} =    Get Value    id=guidanceRow-0-justification
    Should Be Equal    ${input_value}    This is a justification

User creates a new competition for Application tests
    Given the user clicks the button/link    id=section-3
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    And the user clicks the button/link    link=Initial details
    And the user enters text to a text field    id=title    Test competition
    And the user selects the option from the drop-down menu    Programme    id=competitionTypeId
    And the user selects the option from the drop-down menu    Health and life sciences    id=innovationSectorCategoryId
    And the user selects the option from the drop-down menu    Advanced Therapies    id=innovationAreaCategoryId
    And the user enters text to a text field    id=openingDateDay    01
    And the user enters text to a text field    Id=openingDateMonth    12
    And the user enters text to a text field    id=openingDateYear    2017
    And the user selects the option from the drop-down menu    Ian Cooper    id=leadTechnologistUserId
    And the user selects the option from the drop-down menu    Toby Reader    id=executiveUserId
    And the user clicks the button/link    jQuery=.button:contains("Done")
    And the user clicks the button/link    link=Competition setup
