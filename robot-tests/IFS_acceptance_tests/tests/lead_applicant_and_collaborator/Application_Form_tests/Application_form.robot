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
Suite Setup       Run Keywords    Login as User    &{lead_applicant_credentials}
...               AND    Create new application
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${CREATE_APPLICATION_PAGE}    ${SERVER}/application/create/1?accept=accepted
${NEW_TEST_APPLICATION_PROJECT_SUMMARY}    ${SERVER}/application/7/form/question/11
${NEW_TEST_APPLICATION_PUBLIC_DESCRIPTION}    ${SERVER}/application/7/form/question/12
${NEW_TEST_APPLICATION_OVERVIEW}    ${SERVER}/application/7

*** Test Cases ***
Verify the Autosave for the form text areas
    [Documentation]    INFUND-189
    [Tags]    Applicant    Autosave    Form
    [Setup]
    Given Applicant goes to the 'project summary' question of the new application
    When the Applicant enters some text
    and the Applicant refreshes the page
    Then the text should be visible

Verify the Questions guidance for the "Rovel additive..." Application form
    [Documentation]    INFUND-190
    [Tags]    Applicant    Form
    Given Applicant goes to the 'project summary' question
    When the applicant clicks the "What should I include in project summary?" question
    Then the guidance should be visible

Verify the navigation in the form sections
    [Documentation]    INFUND-189
    [Tags]    Applicant    Form
    Given Applicant goes to the Overview page
    When the Applicant clicks a section then the Applicant navigates to the correct section

Verify that the word count works
    [Documentation]    INFUND-198
    [Tags]    Applicant    Word count    Form
    Given Applicant goes to the 'public description' question of the new application
    When the Applicant edits the Public description
    Then the word count should be correct for the Public description
    And the Applicant edits the Project description question (400 words)
    Then the word count for the Project description question should be correct (100 words)

Verify the "review and submit" button
    [Tags]    Applicant    Review and submit    Form
    Given the applicant is on the application overview page
    When the Applicant clicks the "Review and submit" button
    Then the Applicant will navigate to the summary page

Verify that when the Applicant marks as complete the text box should be green and the state changes to edit
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    Applicant    Mark as complete    Form
    Given Applicant goes to the 'public description' question of the new application
    When the Applicant edits 'Public description' and marks it as complete
    Then the text box should turn to green
    and the button state should change to 'Edit'
    and the question should be marked as complete on the application overview page

Verify that when the Applicant marks as incomplete the text box should be green and the state changes to edit
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    Applicant    Mark as complete    Form
    Given Applicant goes to the 'public description' question of the new application
    When the Applicant marks as incomplete 'Public description'
    Then the text box should be editable
    and the button state should change to 'Mark as complete'
    and the question should not be marked as complete on the application overview page

*** Keywords ***
the Applicant enters some text
    Applicant edits the 'Project Summary' question
    Focus    css=.app-submit-btn
    Sleep    2s

the Applicant refreshes the page
    Reload Page
    sleep    1s

the text should be visible
    Element Should Contain    css=#form-input-11 .editor    I am a robot

the applicant clicks the "What should I include in project summary?" question
    Wait Until Element Is Visible    css=#form-input-11 .summary
    Click Element    css=#form-input-11 .summary

the guidance should be visible
    Element Should Be Visible    css=#details-content-0 p

the Applicant clicks a section then the Applicant navigates to the correct section
    Click Element    link=Application details
    Location Should Be    ${APPLICATION_DETAILS_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=Project summary
    Click Element    link=Project summary
    Location Should Be    ${PROJECT_SUMMARY_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=Public description
    Click Element    link=Public description
    Location Should Be    ${PUBLIC_DESCRIPTION_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=Scope
    Click Element    link=Scope
    Location Should Be    ${SCOPE_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=1. Business opportunity
    Click Element    link=1. Business opportunity
    Location Should Be    ${BUSINESS_OPPORTUNITY_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=2. Potential market
    Click Element    link=2. Potential market
    Location Should Be    ${POTENTIAL_MARKET_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=3. Project exploitation
    Click Element    link=3. Project exploitation
    Location Should Be    ${PROJECT_EXPLOITATION_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=4. Economic benefit
    Click Element    link=4. Economic benefit
    Location Should Be    ${ECONOMIC_BENEFIT_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=5. Technical approach
    Click Element    link=5. Technical approach
    Location Should Be    ${TECHNICAL_APPROACH_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=6. Innovation
    Click Element    link=6. Innovation
    Location Should Be    ${INNOVATION_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=7. Risks
    Click Element    link=7. Risks
    Location Should Be    ${RISKS_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=8. Project team
    Click Element    link=8. Project team
    Location Should Be    ${PROJECT_TEAM_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=9. Funding
    Click Element    link=9. Funding
    Location Should Be    ${FUNDING_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=10. Adding value
    Click Element    link=10. Adding value
    Location Should Be    ${ADDING_VALUE_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=Your finances
    Click Element    link=Your finances
    Location Should Be    ${YOUR_FINANCES_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Wait Until Element Is Visible    link=Finances overview
    Click Element    link=Finances overview
    Location Should Be    ${FINANCES_OVERVIEW_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview

the word count should be available in the text area
    Page Should Contain Element    css=#form-input-11 .count-down

the Applicant edits the Public description
    #Clear Element Text    css=#form-input-12 .editor
    #Press Key    css=#form-input-12 .editor    \\8
    #Focus    css=.app-submit-btn
    #Sleep    1s
    Wait Until Element Contains    css=#form-input-12 .count-down    500
    Focus    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.
    Focus    css=.app-submit-btn
    Sleep    1s

the word count should be correct for the Public description
    sleep    1s
    Element Should Contain    css=#form-input-12 .count-down    469

the Applicant edits the Project description question (400 words)
    Clear Element Text    css=#form-input-12 .editor
    Press Key    css=#form-input-12 .editor    \\8
    Input Text    css=#form-input-12 .editor    Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium libero. Cras id dui. Aenean ut eros et nisl sagittis vestibulum. Nullam nulla eros, ultricies sit amet, nonummy id, imperdiet feugiat, pede. Sed lectus. Donec mollis hendrerit risus. Phasellus nec sem in justo pellentesque facilisis. Etiam imperdiet imperdiet orci. Nunc nec neque. Phasellus leo dolor, tempus non, auctor et, hendrerit quis, nisi. Curabitur ligula sapien, tincidunt non, euismod vitae, posuere imperdiet, leo. Maecenas malesuada. Praesent congue erat at massa. Sed cursus turpis vitae tortor. Donec posuere vulputate arcu. Phasellus accumsan cursus velit. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Sed aliquam, nisi quis porttitor congue, elit erat euismod orci, ac
    Focus    css=.app-submit-btn
    Sleep    10s

the Applicant clicks the "Review and submit" button
    Page Should Contain element    link=Review & submit
    Click Element    link=Review & submit

the Applicant will navigate to the summary page
    Location Should Be    ${SUMMARY_URL}

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
    Go To    ${NEW_TEST_APPLICATION_OVERVIEW}
    Page Should Contain Element    css=#form-input-12 .complete

the Applicant marks as incomplete 'Public description'
    Click Button    css=#form-input-12 div.textarea-footer > button[name="mark_as_incomplete"]

the text box should be editable
    Element Should Be Enabled    css=#form-input-12 textarea

the button state should change to 'Mark as complete'
    Page Should Contain Element    css=#form-input-12 button    Mark as complete

the question should not be marked as complete on the application overview page
    Go To    ${NEW_TEST_APPLICATION_OVERVIEW}
    Page Should Contain Element    css=#form-input-12    Question element found on application overview
    Page Should Not Contain Element    css=#form-input-12 div.marked-as-complete img.marked-as-complete    Mark as complete class is not found, that's correct

the applicant is on the application overview page
    Go To    ${APPLICATION_OVERVIEW_URL}

Applicant goes to the 'project summary' question of the new application
    Go To    ${NEW_TEST_APPLICATION_PROJECT_SUMMARY}

Applicant goes to the 'public description' question of the new application
    go to    ${NEW_TEST_APPLICATION_PUBLIC_DESCRIPTION}
