*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
The user clicks the button/link
    [Arguments]    ${BUTTON}
    Wait Until Element Is Visible Without Screenshots    ${BUTTON}
    Focus    ${BUTTON}
    wait for autosave
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    click element    ${BUTTON}


The user should not see the text in the page
    [Arguments]    ${NOT_VISIBLE_TEXT}
    Wait Until Page Does Not Contain Without Screenshots    ${NOT_VISIBLE_TEXT}


The user should see the element
    [Arguments]    ${ELEMENT}
    Wait Until Element Is Visible Without Screenshots    ${ELEMENT}

The user should not see the element
    [Arguments]    ${NOT_VISIBLE_ELEMENT}
    Wait Until Element Is Not Visible Without Screenshots    ${NOT_VISIBLE_ELEMENT}

the user should get a new print window
    [Arguments]  ${title}
    Select Window    Title=${title}

The user should see the browser notification
    [Arguments]    ${MESSAGE}
    # Note - this keyword has been implemented to prevent failures on sauce labs
    # from different browsers not showing the notifications correctly
    Run keyword if    '${REMOTE_URL}' == ''    the user should see the notification    ${MESSAGE}

The user should see the notification
    [Arguments]    ${MESSAGE}
    Wait Until Element Is Visible Without Screenshots    css=div.event-alert
    Wait Until Page Contains Without Screenshots    ${MESSAGE}

The applicant assigns the question to the collaborator
    [Arguments]    ${TEXT_AREA}    ${TEXT}    ${NAME}
    focus    ${TEXT_AREA}
    The user enters text to a text field    ${TEXT_AREA}    ${TEXT}
    When the user clicks the button/link    css=.assign-button > button
    Then the user clicks the button/link    jQuery=button:contains("${NAME}")

the user assigns the question to the collaborator
    [Arguments]    ${name}
    Wait Until Element Is Not Visible Without Screenshots    css=div.event-alert
    The user clicks the button/link    css=.assign-button > button
    The user clicks the button/link    jQuery=button:contains("${NAME}")
    Reload Page

the user clicks the button twice
    [Arguments]    ${element}
    the user clicks the button/link  ${element}
    the user clicks the button/link  ${element}

The element should be disabled
    [Arguments]    ${ELEMENT}
    Element Should Be Disabled    ${ELEMENT}


the address fields should be filled
    # postcode lookup implemented on some machines but not others, so check which is running:
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' != 'NO'    the address fields should be filled with valid data
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' == 'NO'    the address fields should be filled with dummy data

the address fields should be filled with valid data
    Textfield Should Contain    id=addressForm.selectedPostcode.addressLine1    Am Reprographics
    Textfield Should Contain    id=addressForm.selectedPostcode.addressLine2    King William House
    Textfield Should Contain    id=addressForm.selectedPostcode.addressLine3    13 Queen Square
    Textfield Should Contain    id=addressForm.selectedPostcode.town    Bristol
    Textfield Should Contain    id=addressForm.selectedPostcode.county    City of Bristol
    Textfield Should Contain    id=addressForm.selectedPostcode.postcode    BS1 4NT

the address fields should be filled with dummy data
    Textfield Should Contain    id=addressForm.selectedPostcode.addressLine1    Montrose House 1
    Textfield Should Contain    id=addressForm.selectedPostcode.addressLine2    Clayhill Park
    Textfield Should Contain    id=addressForm.selectedPostcode.addressLine3    Cheshire West and Chester
    Textfield Should Contain    id=addressForm.selectedPostcode.town    Neston
    Textfield Should Contain    id=addressForm.selectedPostcode.county    Cheshire
    Textfield Should Contain    id=addressForm.selectedPostcode.postcode    CH64 3RU


the user submits their information
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    the user selects the checkbox    termsAndConditions
    Submit Form

the user cannot login with either password
    The user navigates to the page    ${LOGIN_URL}
    Input Text    id=username    ${valid_email}
    Input Password    id=password    ${correct_password}
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    ${unsuccessful_login_message}
    Page Should Contain    Your email/password combination doesn't seem to work
    go to    ${LOGIN_URL}
    Input Text    id=username    ${valid_email}
    Input Password    id=password    ${incorrect_password}
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    ${unsuccessful_login_message}
    Page Should Contain    Your email/password combination doesn't seem to work


the lead applicant invites a registered user
    [Arguments]    ${EMAIL_LEAD}    ${EMAIL_INVITED}
    run keyword if    ${smoke_test}!=1    invite a registered user    ${EMAIL_LEAD}    ${EMAIL_INVITED}
    run keyword if    ${smoke_test}==1    invite a new academic    ${EMAIL_LEAD}    ${EMAIL_INVITED}


invite a new academic
    [Arguments]    ${EMAIL_LEAD}    ${EMAIL_INVITED}
    guest user log-in    ${EMAIL_LEAD}    ${correct_password}
    the user clicks the button/link    link=${application_name}
    the user clicks the button/link    link=view team members and add collaborators
    the user clicks the button/link    jQuery=.button:contains("Invite new contributors")
    the user clicks the button/link    jQuery=.button:contains("Add additional partner organisation")
    the user enters text to a text field    name=organisations[1].organisationName    university of liverpool
    the user enters text to a text field    name=organisations[1].invites[0].personName    Academic User
    the user enters text to a text field    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    ${EMAIL_INVITED}
    the user clicks the button/link    jQuery=.button:contains("Save changes")


the user should see that the element is disabled
    [Arguments]    ${element}
    the user should not see an error in the page
    Wait Until Element Is Visible Without Screenshots    ${element}
    element should be disabled    ${element}

The user fills the empty question fields
    The user enters text to a text field    id=question.title    Test title
    the user moves focus and waits for autosave
    The user enters text to a text field    id=question.subTitle    Subtitle test
    the user moves focus and waits for autosave
    The user enters text to a text field    id=question.guidanceTitle    Test guidance title
    the user moves focus and waits for autosave
    The user enters text to a text field    css=.editor    Guidance text test
    the user moves focus and waits for autosave
    The user enters text to a text field    id=question.maxWords    150
    the user moves focus and waits for autosave

The user fills the empty assessment fields
    The user enters text to a text field    id=question.assessmentGuidance    Business opportunity guidance
    the user moves focus and waits for autosave
    The user enters text to a text field    id=guidanceRow-0-scorefrom    30
    the user moves focus and waits for autosave
    The user enters text to a text field    id=guidanceRow-0-scoreto    35
    the user moves focus and waits for autosave
    The user enters text to a text field    id=guidanceRow-0-justification    This is a justification
    the user moves focus and waits for autosave

The user checks the question fields
    The user should see the text in the page    Test title
    The user should see the text in the page    Subtitle test
    The user should see the text in the page    Test guidance title
    The user should see the text in the page    Guidance text test
    The user should see the text in the page    150
    The user should see the text in the page    No

The user checks the assessment fields
    The user should see the text in the page    Business opportunity guidance
    The user should see the text in the page    30
    The user should see the text in the page    35
    The user should see the text in the page    This is  justification

The user should see the text in the element
    [Arguments]    ${element}    ${text}
    Wait Until Element Is Visible Without Screenshots    ${element}
    Wait Until Element Contains Without Screenshots    ${element}    ${text}
    the user should not see an error in the page

The user should not see the text in the element
    [Arguments]    ${element}    ${text}
    Wait Until Element Is Visible Without Screenshots    ${element}
    Wait Until Element Does Not Contain Without Screenshots    ${element}    ${text}
    the user should not see an error in the page
