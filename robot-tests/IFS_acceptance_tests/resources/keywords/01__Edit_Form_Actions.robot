*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***


the user selects the checkbox
    [Arguments]    ${checkbox}
    Select Checkbox    ${checkbox}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

the user unselects the checkbox
    [Arguments]    ${checkbox}
    Unselect Checkbox    ${checkbox}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

the user selects the radio button
    [Arguments]    ${RADIO_BUTTON}    ${RADIO_BUTTON_OPTION}
    the user should see the element    ${RADIO_BUTTON}
    Select Radio Button    ${RADIO_BUTTON}    ${RADIO_BUTTON_OPTION}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

the user moves focus to the element
    [Arguments]    ${element}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA
    wait until element is visible    ${element}
    focus    ${element}

the user sees that the radio button is selected
    [Arguments]    ${RADIO_BUTTON}    ${SELECTION}
    wait until element is visible    ${RADIO_BUTTON}
    Radio Button Should Be Set To    ${RADIO_BUTTON}    ${SELECTION}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

the user sees that the radio button is not selected
    [Arguments]    ${RADIO_BUTTON}
    wait until element is visible    ${RADIO_BUTTON}
    Radio Button Should Not Be Selected    ${RADIO_BUTTON}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

the user selects the option from the drop-down menu
    [Arguments]    ${option}    ${drop-down}
    wait until element is visible    ${drop-down}
    Select From List    ${drop-down}    ${option}
    mouse out    ${drop-down}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

the user submits the form
    Submit Form
    Page Should Not Contain    Error
    Page Should Not Contain Button    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

Question should be editable
    [Arguments]    ${Mark_question_as_incomplete}
    ${status}    ${value}=    Run Keyword And Ignore Error    Element Should Be Visible    ${Mark_question_as_incomplete}
    Run Keyword If    '${status}' == 'PASS'    Click Element    ${Mark_question_as_incomplete}
    wait for autosave

The user enters text to a text field
    [Arguments]    ${TEXT_FIELD}    ${TEXT_INPUT}
    Wait Until Element Is Visible    ${TEXT_FIELD}
    Clear Element Text    ${TEXT_FIELD}
    wait until keyword succeeds    10    200ms    input text    ${TEXT_FIELD}    ${TEXT_INPUT}
    Mouse Out    ${TEXT_FIELD}
    Wait for autosave

the user sees the text in the element
    [Arguments]    ${element}    ${text}
    wait until element is visible    ${element}
    Wait Until Keyword Succeeds    10    500ms    element should contain    ${element}    ${text}

the user clears the text from the element
    [Arguments]    ${element}
    wait until element is visible    ${element}
    clear element text    ${element}
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking    (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

the user sees the text in the text field
    [Arguments]    ${textfield}    ${text}
    wait until element is visible    ${textfield}
    wait until keyword succeeds    10    200ms    textfield should contain    ${textfield}    ${text}

the user selects the index from the drop-down menu
    [Arguments]    ${option}    ${drop-down}
    wait until element is visible    ${drop-down}
    Select From List By Index    ${drop-down}    ${option}
    mouse out    ${drop-down}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

the user moves the mouse away from the element
    [Arguments]    ${element}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA
    wait until element is visible    ${element}
    mouse out    ${element}

the user should see the dropdown option selected
    [Arguments]    ${option}    ${drop-down}
    List Selection Should Be    ${drop-down}    ${option}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking    (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

the user edits the 'Project Summary' question
    focus    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Input Text    css=#form-input-11 .editor    I am a robot
    wait for autosave

the applicant adds some content and marks this section as complete
    Focus    css=#form-input-4 .editor
    Input Text    css=#form-input-4 .editor    This is some random text
    the user clicks the button/link    name=mark_as_complete
    the user should see the element    name=mark_as_incomplete

the applicant edits the "economic benefit" question
    the user clicks the button/link    name=mark_as_incomplete
    the user should see the element    name=mark_as_complete

The user enters multiple strings into a text field
    [Arguments]    ${field}    ${string}    ${multiplicity}
    #Keyword uses custom IfsLibrary keyword "repeat string"
    ${concatenated_string} =    repeat string    ${string}    ${multiplicity}
    Wait Until Element Is Visible    ${field}
    wait until keyword succeeds    30s    200ms    Input Text    ${field}    ${concatenated_string}
