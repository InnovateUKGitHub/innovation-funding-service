*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
The user clicks the button/link
    [Arguments]    ${BUTTON}
    Wait Until Element Is Enabled  ${BUTTON}
    Set Focus To Element    ${BUTTON}
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    click element    ${BUTTON}

The user clicks the button with resubmission
    [Arguments]    ${BUTTON}
    ${Start_Url} =   Get Location
    Wait Until Element Is Visible Without Screenshots    ${BUTTON}
    Wait Until Element Is Enabled  ${BUTTON}
    Set Focus To Element    ${BUTTON}
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    click element    ${BUTTON}
    ${End_Url} =   Get Location
    Run Keyword If   '${Start_Url}' == '${End_Url}'     The user retries submission    ${BUTTON}

The user retries submission
    [Arguments]    ${BUTTON}
    Wait Until Element Is Visible Without Screenshots    ${BUTTON}
    Wait Until Element Is Enabled  ${BUTTON}
    Set Focus To Element    ${BUTTON}
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    click element    ${BUTTON}

The user clicks the button/link in the paginated list
    [Arguments]    ${BUTTON}
    Do Keyword With Pagination     click element     ${BUTTON}

the user clicks the button twice
    [Arguments]    ${element}
    the user clicks the button/link  ${element}
    the user clicks the button/link  ${element}

The user should see the element
    [Arguments]    ${ELEMENT}
    Wait Until Element Is Visible Without Screenshots    ${ELEMENT}

The user should see the element in the paginated list
    [Arguments]    ${ELEMENT}
    Do Keyword With Pagination     Wait Until Element Is Visible Without Screenshots    ${ELEMENT}

The user should not see the element in the paginated list
    [Arguments]    ${ELEMENT}
    ${elementFound}=   Do Keyword With Pagination And Ignore Error     Wait Until Element Is Visible Without Screenshots    ${ELEMENT}
    Run Keyword If   ${elementFound} == True    Fail   'Should not have found the element ${ELEMENT} in the paginated list'

The user should not see the element
    [Arguments]    ${NOT_VISIBLE_ELEMENT}
    Wait Until Element Is Not Visible    ${NOT_VISIBLE_ELEMENT}

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
    [Arguments]  ${name}
    the user clicks the button/link     jQuery = a:contains("Assign to someone else")
    the user should see the element     jQuery = h2:contains("Assign this question to someone else.")
    the user clicks the button/link     jQuery = label:contains("${name}")
    the user clicks the button/link     jQuery = label:contains("${name}")
    the user clicks the button/link     jQuery = button:contains("Save and return to")

the user assigns the question to the collaborator
    [Arguments]    ${name}
    Wait Until Element Is Not Visible Without Screenshots    css=div.event-alert
    the user clicks the button/link     jQuery = a:contains("Assign to someone else")
    the user should see the element     jQuery = h2:contains("Assign this question to someone else.")
    the user clicks the button/link     jQuery = label:contains("${name}")
    the user clicks the button/link     jQuery = label:contains("${name}")
    the user clicks the button/link     jQuery = label:contains("${name}")
    the user clicks the button/link     jQuery = button:contains("Save and return to")
    Reload Page

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

the user should see that the element is disabled
    [Arguments]    ${element}
    the user should not see an error in the page
    Wait Until Element Is Visible Without Screenshots    ${element}
    element should be disabled    ${element}

The user fills the empty question fields
    The user enters text to a text field    id=question.title    Test title
    The user enters text to a text field    jQuery = label:contains("Question subtitle") + div .editor    Subtitle test
    The user enters text to a text field    id=question.guidanceTitle    Test guidance title
    The user enters text to a text field    jQuery = label:contains("Question guidance") + div .editor    Guidance text test
    The user enters text to a text field    id=question.maxWords    150

The user fills the empty assessment fields
    The user enters text to a text field    id=question.assessmentGuidance    Business opportunity guidance
    The user enters text to a text field    id=guidanceRows[0].scoreFrom    30
    The user enters text to a text field    id=guidanceRows[0].scoreTo    35
    The user enters text to a text field    id=guidanceRows[0].justification    This is a justification

The user checks the question fields
    The user should see the element    jQuery = dd:contains("Test title")
    The user should see the element    jQuery = dd:contains("Subtitle test")
    The user should see the element    jQuery = dd:contains("Test guidance title")
    The user should see the element    jQuery = dd:contains("Guidance text test")
    The user should see the element    jQuery = dd:contains("150")

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

the user expands the section
    [Arguments]  ${section}
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery=button:contains("${section}")[aria-expanded="false"]
    run keyword if  '${status}'=='PASS'  the user clicks the button/link  jQuery=button:contains("${section}")[aria-expanded="false"]

the user collapses the section
    [Arguments]  ${section}
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery=button:contains("${section}")[aria-expanded="true"]
    run keyword if  '${status}'=='PASS'  the user clicks the button/link  jQuery=button:contains("${section}")[aria-expanded="true"]

the internal sends the descision notification email to all applicants
    [Arguments]  ${email}
    the user enters text to a text field  css=.editor  ${email}
    the user clicks the button/link       css=.govuk-button[data-js-modal="send-to-all-applicants-modal"]
    the user clicks the button/link       css=button[name="send-emails"]

the internal user navigates to the project setup competition
    [Arguments]  ${comp_name}
    the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    :FOR    ${i}    IN RANGE  10
    \  ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    the user should see the element    link=${comp_name}
    \  Exit For Loop If  '${status}'=='PASS'
    \  run keyword if  '${status}'=='FAIL'  the user clicks the button/link  jQuery=button:contains("Next")
    \  ${i} =  Set Variable  ${i + 1}
    the user clicks the button/link       link=${comp_name}




