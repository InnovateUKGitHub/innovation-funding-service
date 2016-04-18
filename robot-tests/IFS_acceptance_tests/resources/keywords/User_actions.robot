*** Keywords ***
The user navigates to the page
    [Arguments]    ${TARGET_URL}
    Go To    ${TARGET_URL}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Wait Until Element Is Visible    id=global-header
    Page Should Contain    BETA
    # "Contact us" checking (INFUND-1289)
    # Pending completion of INFUND-2544, INFUND-2545
    # Wait Until Page Contains Element   link=Contact Us
    # Page Should Contain Link        href=${SERVER}/info/contact

The user navigates to the page without the usual headers
    [Arguments]    ${TARGET_URL}
    Go To    ${TARGET_URL}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request

The user navigates to the page and gets a custom error message
    [Arguments]    ${TARGET_URL}    ${CUSTOM_ERROR_MESSAGE}
    Go To    ${TARGET_URL}
    Page Should Contain    ${CUSTOM_ERROR_MESSAGE}

The user is on the page
    [Arguments]    ${TARGET_URL}
    Location Should Contain    ${TARGET_URL}
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Wait Until Element Is Visible    id=global-header
    Page Should Contain    BETA
    # "Contact us" checking (INFUND-1289)
    # Pending completion of INFUND-2544, INFUND-2545
    # Wait Until Page Contains Element   link=Contact Us
    # Page Should Contain Link        href=${SERVER}/info/contact


The user should be redirected to the correct page
    [Arguments]    ${URL}
    Sleep    500ms
    Location Should Contain    ${URL}
    Page Should Not Contain    error
    Page Should Not Contain    Page or resource not found

the user reloads the page
    Reload Page
    # Error checking
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Element Should Be Visible    id=global-header
    Page Should Contain    BETA

Applicant edits the 'Project Summary' question
    focus    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Input Text    css=#form-input-11 .editor    I am a robot

Question should be editable
    [Arguments]    ${Mark_question_as_incomplete}
    ${status}    ${value}=    Run Keyword And Ignore Error    Element Should Be Visible    ${Mark_question_as_incomplete}
    Run Keyword If    '${status}' == 'PASS'    Click Element    ${Mark_question_as_incomplete}
    sleep    2s

Switch to the first browser
    Switch browser    1

Create new application
    go to    ${CREATE_APPLICATION_PAGE}
    Input Text    id=application_name    Form test application
    Click Element    css=#content > form > input
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request

The user enters text to a text field
    [Arguments]    ${TEXT_FIELD}    ${TEXT_INPUT}
    Wait Until Element Is Visible    ${TEXT_FIELD}
    Clear Element Text    ${TEXT_FIELD}
    input text    ${TEXT_FIELD}    ${TEXT_INPUT}

The user clicks the button/link
    [Arguments]    ${BUTTON}
    Wait Until Element Is Visible   ${BUTTON}
    click element    ${BUTTON}

The user should see the text in the page
    [Arguments]    ${VISIBLE_TEXT}
    wait until page contains    ${VISIBLE_TEXT}

The user should not see the text in the page
    [Arguments]    ${NOT_VISIBLE_TEXT}
    sleep    500ms
    Page should not contain    ${NOT_VISIBLE_TEXT}

the user should not see an error in the page
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request

The user should see an error
    [Arguments]    ${ERROR_TEXT}
    Page should contain element    css=.error-message
    Page should contain    ${ERROR_TEXT}

the guest user enters the log in credentials
    [Arguments]    ${USER_NAME}    ${PASSWORD}
    Input Text    id=username    ${USER_NAME}
    Input Password    id=password    ${PASSWORD}

The user should see the element
    [Arguments]    ${ELEMENT}
    Wait Until Element Is Visible    ${ELEMENT}

The user should not see the element
    [Arguments]    ${NOT_VISIBLE_ELEMENT}
    sleep    500ms
    Element Should Not Be Visible    ${NOT_VISIBLE_ELEMENT}

The user should get an error page
    [Arguments]    ${ERROR_TEXT}
    Page should contain element    css=.error
    Page should contain    ${ERROR_TEXT}

The user should see the browser notification
    [Arguments]    ${MESSAGE}
    # Note - this keyword has been implemented to prevent failures on sauce labs
    # from different browsers not showing the notifications correctly
    Run keyword if    '${REMOTE_URL}' == ''    the user should see the notification    ${MESSAGE}

The user should see the notification
    [Arguments]    ${MESSAGE}
    Wait Until Element Is Visible    css=div.event-alert
    Wait Until Page Contains    ${MESSAGE}

The applicant assigns the question to the collaborator
    [Arguments]    ${TEXT_AREA}    ${TEXT}    ${NAME}
    focus    ${TEXT_AREA}
    The user enters text to a text field    ${TEXT_AREA}    ${TEXT}
    When the user clicks the button/link    css=.assign-button
    Then the user clicks the button/link    jQuery=button:contains("${NAME}")

the user assigns the question to the collaborator
    [Arguments]    ${name}
    The user clicks the button/link    css=.assign-button
    The user clicks the button/link    jQuery=button:contains("${NAME}")
    Reload Page

The user goes back to the previous page
    Go Back

browser validations have been disabled
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');jQuery('[maxlength]').removeAttr('maxlength');

The user verifies their email
    [Arguments]    ${verify_link}
    Go To    ${verify_link}
    Page Should Contain    Account verified

the user can remove the uploaded file
    [Arguments]    ${file_name}
    Reload Page
    Click Button    name=remove_uploaded_file
    Wait Until Page Does Not Contain    Remove
    Page Should Contain    Upload
    Page Should Not Contain    ${file_name}

the user cannot remove the uploaded file
    [Arguments]    ${file_name}
    Page Should Not Contain    Remove

the user clicks the link from the appropriate email sender
    Run keyword if    '${RUNNING_ON_DEV}' == ''    the user opens the mailbox and verifies the email sent from a developer machine
    Run keyword if    '${RUNNING_ON_DEV}' != ''    the user opens the mailbox and verifies the official innovate email

the user opens the mailbox and verifies the email sent from a developer machine
    the user opens the mailbox and verifies the email from    dev-dwatson-liferay-portal@hiveit.co.uk

the user opens the mailbox and verfies the official innovate email
    the user opens the mailbox and verifies the email from    noresponse@innovateuk.gov.uk

the user opens the mailbox and verifies the email from
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test@gmail.com    password=testtest1
    ${LATEST} =    wait for email
    ${HTML}=    get email body    ${LATEST}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${LATEST}
    log    ${LINK}
    ${VERIFY_EMAIL}=    Get From List    ${LINK}    1
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

the user opens the mailbox and accepts the invitation to collaborate
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test@gmail.com    password=testtest1
    ${LATEST} =    wait for email
    ${HTML}=    get email body    ${LATEST}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${LATEST}
    log    ${LINK}
    ${VERIFY_EMAIL}=    Get From List    ${LINK}    2
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

Delete the emails from the test mailbox
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test@gmail.com    password=testtest1
    Delete All Emails
    close mailbox

the user enters the details and clicks the create account
    [Arguments]    ${REG_EMAIL}
    Input Text    id=firstName    Stuart
    Input Text    id=lastName    ANDERSON
    Input Text    id=phoneNumber    23232323
    Input Text    id=email    ${REG_EMAIL}
    Input Password    id=password    Passw0rd2
    Input Password    id=retypedPassword    Passw0rd2
    Select Checkbox    termsAndConditions
    Submit Form

the user fills the create account form
    [Arguments]    ${NAME}    ${LAST_NAME}
    Input Text    id=firstName    ${NAME}
    Input Text    id=lastName    ${LAST_NAME}
    Input Text    id=phoneNumber    0612121212
    Input Password    id=password    Passw0rd
    Input Password    id=retypedPassword    Passw0rd
    Select Checkbox    termsAndConditions
    Submit Form


the address fields should be filled
    # postcode lookup implemented on some machines but not others, so check which is running:
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' != ''    the address fields should be filled with valid data
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' == ''    the address fields should be filled with dummy data

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

the user cannot see a validation error in the page
    Element Should Not Be Visible       css=.error