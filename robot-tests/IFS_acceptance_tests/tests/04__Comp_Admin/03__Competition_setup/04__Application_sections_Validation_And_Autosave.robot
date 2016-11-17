*** Settings ***
Documentation     INFUND-5629 As a Competitions team member I want to be able to edit Application Questions individually in Competition Setup so that I can manage the question and associated applicant and assessor guidance in one place
Suite Setup       Run Keywords    Guest user log-in    &{Comp_admin1_credentials}
...               AND    User creates a new competition for Application tests
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Business opportunity Server-side validations
    [Documentation]    INFUND-5629
    Given The user clicks the button/link    link=Application
    And The user clicks the button/link    link=Business opportunity
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    When the user leaves all the question field empty
    And The user clicks the button/link    jQuery=.button[value="Save and close"]
    Then the validation error above the question should be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=label:contains(Question guidance)    This field cannot be left blank
    #And the validation error above the question should be visible    jQuery=label:contains(Max word count)    This field cannot be left blank
    # Remove the above comment a soon as inf-5980 is done

Business opportunity: Client side validations
    [Documentation]    INFUND-5629
    When the user fills the empty question fields
    Then the validation error above the question should not be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Question guidance)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Max word count)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Max word count)    This field cannot be left blank

Business opportunity: Autosave
    [Documentation]    INFUND-5629
    [Tags]    Pending
    # Pending Infund-5980
    Given the user moves focus and waits for autosave
    When the user clicks the button/link    link=Application
    And The user clicks the button/link    link=Business opportunity
    Then the user should see the correct inputs in the Applications questions form

Business opportunity: Mark as done
    [Documentation]    INFUND-5629
    [Tags]    Pending
    #TO DO pending due to INFUND-6242
    When The user clicks the button/link    jQuery=.button[value="Save and close"]
    And the user clicks the button/link    link=Business opportunity
    Then The user should see the text in the page    Business opportunity
    And The user should see the text in the page    Test title
    And The user should see the text in the page    Subtitle test
    And The user should see the text in the page    Test guidance title
    And The user should see the text in the page    Guidance text test
    And The user should see the text in the page    150
    And The user should see the text in the page    No
    [Teardown]    The user clicks the button/link    link=Application

Scope Server-side validations
    [Documentation]    INFUND-5635
    Given The user clicks the button/link    link=Scope
    And the user clicks the button/link    jQuery=.button:contains("Edit this question")
    When the user leaves all the question field empty
    And The user clicks the button/link    jQuery=.button[value="Save and close"]
    Then the validation error above the question should be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    And the validation error above the question should be visible    jQuery=label:contains(Question guidance)    This field cannot be left blank
    #And the validation error above the question should be visible    jQuery=label:contains(Max word count)    This field cannot be left blank
    # Remove the above comment a soon as inf-5980 is done

Scope: Client side validations
    [Documentation]    INFUND-5635
    When the user fills the empty question fields
    Then the validation error above the question should not be visible    jQuery=label:contains(Question title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Question guidance title)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Question guidance)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Max word count)    This field cannot be left blank
    And the validation error above the question should not be visible    jQuery=label:contains(Max word count)    This field cannot be left blank

Scope: Autosave
    [Documentation]    INFUND-5635
    [Tags]    Pending
    # Pending Infund-5980
    Given the user moves focus and waits for autosave
    When the user clicks the button/link    link=Application
    And The user clicks the button/link    link=Scope
    Then the user should see the correct inputs in the Applications questions form

Scope: Mark as done
    [Documentation]    INFUND-5635
    [Tags]    Pending
    #TO DO pending due to INFUND-6242
    When The user clicks the button/link    jQuery=.button[value="Save and close"]
    And the user clicks the button/link    link=Scope
    Then The user should see the text in the page    Scope
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
    ${input_value} =    Get Value    css=.editor
    Should Be Equal    ${input_value}    Guidance text test
    ${input_value} =    Get Value    id=question.maxWords
    Should Be Equal    ${input_value}    150

User creates a new competition for Application tests
    Given the user clicks the button/link    id=section-3
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    And the user clicks the button/link    link=Initial Details
    And the user enters text to a text field    id=title    Test competition
    And the user selects the option from the drop-down menu    Programme    id=competitionTypeId
    And the user selects the option from the drop-down menu    Health and life sciences    id=innovationSectorCategoryId
    And the user selects the option from the drop-down menu    Advanced Therapies    id=innovationAreaCategoryId
    And the user enters text to a text field    id=openingDateDay    01
    And the user enters text to a text field    Id=openingDateMonth    12
    And the user enters text to a text field    id=openingDateYear    2017
    And the user selects the option from the drop-down menu    Competition Technologist One    id=leadTechnologistUserId
    And the user selects the option from the drop-down menu    Competition Executive Two    id=executiveUserId
    And the user clicks the button/link    jQuery=.button:contains("Done")
    And the user clicks the button/link    link=Competition setup
