*** Keywords ***


The user navigates to the page
    [Arguments]     ${TARGET_URL}
    Go To           ${TARGET_URL}
    Page Should Not Contain         Error
    Page Should Not Contain         something went wrong


The user is on the page
    [Arguments]     ${TARGET_URL}
    Location Should Be           ${TARGET_URL}
    Page Should Not Contain         Error
    Page Should Not Contain         something went wrong


Applicant edits the 'Project Summary' question
    focus    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Input Text    css=#form-input-11 .editor    I am a robot

Mark scope question 13 as editable
    Click Element    css=#question-13 div.textarea-wrapped.marked-as-complete.word-count div.textarea-footer > button

Question should be editable
    [Arguments]    ${Mark_question_as_incomplete}
    ${status}    ${value}=    Run Keyword And Ignore Error    Element Should Be Visible    ${Mark_question_as_incomplete}
    Run Keyword If    '${status}' == 'PASS'    Click Element    ${Mark_question_as_incomplete}
    sleep    2s

Mark question 12 as editable
    click Element    css=#form-input-12 div.textarea-wrapped.marked-as-complete.word-count div.textarea-footer > button
    Sleep    1s

Switch to the first browser
    Switch browser    1

Applicant is in the 'Your Finance' sub-section
    Go To    ${FINANCES}
    Click Element    Link=Your finances


the applicant is in the "Your Finances" sub-section
    Applicant is in the 'Your Finance' sub-section

the applicant is in the Finance section
    Go To    ${FINANCES}

Applicant is on the overview page
    Location Should contain    ${APPLICATION_OVERVIEW_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the Application form
    Location Should Be    ${APPLICATION_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'application details' question
    Location Should Be    ${APPLICATION_DETAILS_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'project summary' question
    Location Should Be    ${PROJECT_SUMMARY_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'public description' question
    Location Should Be    ${PUBLIC_DESCRIPTION_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'scope' question
    Location Should Be    ${SCOPE_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'business opportunity question'
    Location Should Be    ${BUSINESS_OPPORTUNITY_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'potential market' question
    Location Should Be    ${POTENTIAL_MARKET_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'project exploitation' question
    Location Should Be    ${PROJECT_EXPLOITATION_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'economic benefit' question
    Location Should Be    ${ECONOMIC_BENEFIT_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'technical approach' question
    Location Should Be    ${TECHNICAL APPROACH_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'innovation' question
    Location Should Be    ${INNOVATION_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'risks' question
    Location Should Be    ${RISKS_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'funding' question
    Location Should Be    ${FUNDING_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'project team' question
    Location Should Be    ${PROJECT_TEAM_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the 'adding value' question
    Location Should Be    ${ADDING_VALUE_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the scope section
    Location Should Be    ${SCOPE_SECTION_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the Application questions section
    Location Should Be    ${APPLICATION_QUESTIONS_SECTION_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the "Your approach..." section
    Location Should Be    ${PROJECT_URL}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Applicant is on the Finances section
    Location Should Be    ${FINANCES}
    Page Should Not Contain    error
    Page Should Not Contain    Error

Create new application
    go to    ${CREATE_APPLICATION_PAGE}
    Input Text    id=application_name    Form test application
    Click Element    css=#content > form > input

User should be redirected to the correct page
    [Arguments]    ${URL}
    Location Should Contain    ${URL}
    Page Should Not Contain    error

User navigates to the page
    [Arguments]    ${PAGE_URL}
    go to    ${PAGE_URL}

User enters text to a text field
    [Arguments]    ${TEXT_FIELD}    ${TEXT_INPUT}
    Wait Until Element Is Visible    ${TEXT_FIELD}
    Clear Element Text    ${TEXT_FIELD}
    input text    ${TEXT_FIELD}    ${TEXT_INPUT}

user clicks the button/link
    [Arguments]    ${BUTTON}
    click element    ${BUTTON}

User should see the text in the page
    [Arguments]    ${VISIBLE_TEXT}
    wait until page contains    ${VISIBLE_TEXT}

User should see an error
    [Arguments]    ${ERROR_TEXT}
    Page should contain element    css=.error-message
    Page should contain    ${ERROR_TEXT}

the guest user enters the log in credentials
    [Arguments]    ${USER_NAME}    ${PASSWORD}
    Input Text    id=id_email    ${USER_NAME}
    Input Password    id=id_password    ${PASSWORD}

User should see the element
    [Arguments]    ${ELEMENT}
    Wait Until Element Is Visible    ${ELEMENT}

User should not see the element
    [Arguments]    ${NOT_VISIBLE_ELEMENT}
    sleep    500ms
    Element Should Not Be Visible    ${NOT_VISIBLE_ELEMENT}

User should not see the text in the page
    [Arguments]    ${NOT_VISIBLE_TEXT}
    sleep    500ms
    Page should not contain    ${NOT_VISIBLE_TEXT}

User should get an error page
    [Arguments]    ${ERROR_TEXT}
    Page should contain element    css=.error
    Page should contain    ${ERROR_TEXT}
