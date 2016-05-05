*** Settings ***
Documentation     -INFUND-184: As an applicant and on the over view of the application, I am able to see the character count and status of the questions, so I am able to see if my questions are valid
...
...               -INFUND-186: As an applicant and in the application form, I should be able to change the state of a question to mark as complete, so I don't have to revisit the question.
...
...               -INFUND-66: As an applicant and I am on the application form, I can fill in the questions belonging to the application, so I can apply for the competition
...
...               -INFUND-42: As an applicant and I am on the application form, I get guidance for questions, so I know what I need to fill in.
...
...               -INFUND-183: As a an applicant and I am in the application form, I can see the character count that I have left, so I comply to the rules of the question
Suite Setup       Run Keywords    Guest user log-in    &{lead_applicant_credentials}
...               AND    Create new application
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Variables ***
${CREATE_APPLICATION_PAGE}    ${SERVER}/application/create/1?accept=accepted
${NEW_TEST_APPLICATION_PROJECT_SUMMARY}    ${SERVER}/application/1/form/question/11
${NEW_TEST_APPLICATION_PUBLIC_DESCRIPTION}    ${SERVER}/application/1/form/question/12
${NEW_TEST_APPLICATION_OVERVIEW}    ${SERVER}/application/1

*** Test Cases ***
Verify the Autosave for the form text areas
    [Documentation]    INFUND-189
    [Tags]    Applicant    Form    HappyPath
    [Setup]
    Given the user navigates to the page    ${NEW_TEST_APPLICATION_PROJECT_SUMMARY}
    When the user edits the 'project summary' question
    And the user reloads the page
    Then the text should be visible

Verify the Questions guidance for the "Rovel additive..." Application form
    [Documentation]    INFUND-190
    [Tags]    Applicant    Form
    Given the user navigates to the page    ${PROJECT_SUMMARY_URL}
    When the user clicks the button/link    css=#form-input-11 .summary
    Then the user should see the element    css=#details-content-0 p

Verify the navigation in the form sections
    [Documentation]    INFUND-189
    [Tags]    Applicant    Form    HappyPath
    When the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    Then the user clicks the section links and is redirected to the correct sections

Verify that the word count works
    [Documentation]    INFUND-198
    [Tags]    Applicant    Form
    Given the user navigates to the page    ${NEW_TEST_APPLICATION_PUBLIC_DESCRIPTION}
    When the Applicant edits the Public description
    Then the word count should be correct for the Public description
    And the Applicant edits the Project description question (300 words)
    Then the word count for the Project description question should be correct (100 words)

Verify the "review and submit" button
    [Tags]    Applicant    Form
    Given the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    When the user clicks the button/link        jQuery=.button:contains("Review & submit")
    Then the user should be redirected to the correct page      ${summary_url}

Verify that when the Applicant marks as complete the text box should be green and the state changes to edit
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    Applicant    Form    HappyPath
    Given the user navigates to the page    ${NEW_TEST_APPLICATION_PUBLIC_DESCRIPTION}
    When the Applicant edits 'Public description' and marks it as complete
    Then the text box should turn to green
    And the button state should change to 'Edit'
    And the question should be marked as complete on the application overview page

Verify that when the Applicant marks as incomplete the text box is no longer green and the state changes to be editable
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    Applicant    Form    HappyPath
    Given the user navigates to the page    ${NEW_TEST_APPLICATION_PUBLIC_DESCRIPTION}
    When the Applicant marks as incomplete 'Public description'
    Then the text box should be editable
    And the button state should change to 'Mark as complete'
    And the question should not be marked as complete on the application overview page

*** Keywords ***

the text should be visible
    Element Should Contain    css=#form-input-11 .editor    I am a robot


the user clicks the section links and is redirected to the correct sections

    The user clicks the section link and is redirected to the correct section      Application details     ${APPLICATION_DETAILS_URL}
    The user clicks the section link and is redirected to the correct section      Project summary         ${PROJECT_SUMMARY_URL}
    The user clicks the section link and is redirected to the correct section      Public description      ${PUBLIC_DESCRIPTION_URL}
    The user clicks the section link and is redirected to the correct section      Scope                   ${SCOPE_URL}
    The user clicks the section link and is redirected to the correct section      1. Business opportunity    ${BUSINESS_OPPORTUNITY_URL}
    The user clicks the section link and is redirected to the correct section      2. Potential market        ${POTENTIAL_MARKET_URL}
    The user clicks the section link and is redirected to the correct section      3. Project exploitation    ${PROJECT_EXPLOITATION_URL}
    The user clicks the section link and is redirected to the correct section      4. Economic benefit        ${ECONOMIC_BENEFIT_URL}
    The user clicks the section link and is redirected to the correct section      5. Technical approach      ${TECHNICAL_APPROACH_URL}
    The user clicks the section link and is redirected to the correct section      6. Innovation              ${INNOVATION_URL}
    The user clicks the section link and is redirected to the correct section      7. Risks                   ${RISKS_URL}
    The user clicks the section link and is redirected to the correct section      8. Project team            ${PROJECT_TEAM_URL}
    The user clicks the section link and is redirected to the correct section      9. Funding                 ${FUNDING_URL}
    The user clicks the section link and is redirected to the correct section      10. Adding value            ${ADDING_VALUE_URL}
    The user clicks the section link and is redirected to the correct section      Your finances           ${YOUR_FINANCES_URL}
    The user clicks the section link and is redirected to the correct section      Finances overview        ${FINANCES_OVERVIEW_URL}


The user clicks the section link and is redirected to the correct section
    [Arguments]     ${link}     ${url}
    The user clicks the button/link     link=${link}
    The user should be redirected to the correct page   ${url}
    The user clicks the button/link     link=Application Overview



the Applicant edits the Public description
    Clear Element Text    css=#form-input-12 .editor
    Press Key    css=#form-input-12 .editor    \\8
    Focus    css=.app-submit-btn
    Sleep    1s
    Wait Until Element Contains    css=#form-input-12 .count-down    400
    Focus    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.
    Focus    css=.app-submit-btn
    Sleep    1s

the word count should be correct for the Public description
    sleep    1s
    Element Should Contain    css=#form-input-12 .count-down    369

the Applicant edits the Project description question (300 words)
    Clear Element Text    css=#form-input-12 .editor
    Press Key    css=#form-input-12 .editor    \\8
    Input Text    css=#form-input-12 .editor    Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium libero. Cras id dui. Aenean ut
    Focus    css=.app-submit-btn
    Sleep    10s


the text box should turn to green
    Page Should Contain Element    css=#form-input-12 div.marked-as-complete img.marked-as-complete
    Element Should Be Disabled    css=#form-input-12 textarea

the button state should change to 'Edit'
    Page Should Contain Element    css=#form-input-12 button    Edit

the word count for the Project description question should be correct (100 words)
    Element Should Contain    css=#form-input-12 .count-down    100

the Applicant edits 'Public description' and marks it as complete
    focus    css=#form-input-12 .editor
    Clear Element Text    css=#form-input-12 .editor
    Press Key    css=#form-input-12 .editor    \\8
    focus    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    Hi, I’m a robot @#$@#$@#$
    Click Element    css=#form-input-12 div.textarea-footer button[name="mark_as_complete"]

the question should be marked as complete on the application overview page
    The user navigates to the page    ${NEW_TEST_APPLICATION_OVERVIEW}
    The user should see the element     jQuery=#section-1 .section:nth-child(3) img[src="/images/field/field-done-right.png"]

the Applicant marks as incomplete 'Public description'
    Click Button    css=#form-input-12 div.textarea-footer > button[name="mark_as_incomplete"]

the text box should be editable
    Element Should Be Enabled    css=#form-input-12 textarea

the button state should change to 'Mark as complete'
    Page Should Contain Element    css=#form-input-12 button    Mark as complete

the question should not be marked as complete on the application overview page
    The user navigates to the page    ${NEW_TEST_APPLICATION_OVERVIEW}
    Page Should Contain Element    jQuery=#section-1 .section:nth-child(3)    Question element found on application overview
    Page Should Not Contain Element    jQuery=#section-1 .section:nth-child(3) img[src="/images/field/field-done-right.png"]
