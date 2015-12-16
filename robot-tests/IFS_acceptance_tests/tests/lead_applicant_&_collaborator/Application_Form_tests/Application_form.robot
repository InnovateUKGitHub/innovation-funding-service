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
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
Verify the Autosave for the form text areas
    [Documentation]    INFUND-189
    [Tags]    Applicant    Autosave    Form
    Given the Applicant is in the project summary question
    When the Applicant enters some text
    and the Applicant refreshes the page
    Then the text should be visible

Verify the Questions guidance for the "Rovel additive..." Application form
    [Documentation]    INFUND-190
    [Tags]    Applicant    Form
    Given the Applicant is in the project summary question
    When the applicant clicks the "What should I include in project summary?" question
    Then the guidance should be visible

Verify the navigation in the form sections
    [Documentation]    INFUND-189
    [Tags]    Applicant    Form
    Given the Applicant is on the applicaton overview page
    When the Applicant clicks a section then the Applicant navigates to the correct section

Verify the last update metadata
    [Documentation]    INFUND-283
    ...    This test case has been commented because of the changes in the overview page
    [Tags]    Applicant    Form
    #Given the applicant is on the application overview page
    #and opens the 'Your business proposition' section
    #Then the last update date of question 1 is a date in the past
    #Given the Applicant is in the application form
    #and the applicant is on section 'Your business proposition'
    #and the last update date of question 1 is a date in the past
    #When the Applicant edits question 1
    #and the Applicant refreshes the page
    #Then the last update date should be updated
    #and the applicant is on the application overview page
    #and opens the 'Your business proposition' section
    #and the last update date should be updated

Verify that the word count is available
    [Documentation]    INFUND-198
    [Tags]    Applicant    Word count    Form
    Given the Applicant is in the project summary question
    Then the word count should be available in the text area

Verify that the word count works
    [Documentation]    INFUND-198
    [Tags]    Applicant    Word count    Form
    Given the Applicant is in the public description question
    When the Applicant edits the Public description
    Then the word count should be correct for the Public description
    And when the Applicant edits the Project description question (500 words)
    Then the word count for the Public description question should be correct (0 words)

Verify the "review and submit" button
    [Tags]    Applicant    Review and submit    Form
    Given the applicant is on the application overview page
    When the Applicant clicks the "Review and submit" button
    Then the Applicant will navigate to the summary page

Verify that when the Applicant marks as complete the text box should be green and the state changes to edit
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    Applicant    Mark as complete    Form
    Given the Applicant is in the application form
    When the Applicant edits 'Public description' and marks it as complete
    Then the text box should turn to green
    and the button state should change to 'Edit'
    and the question should be marked as complete on the application overview page

Verify that when the Applicant marks as incomplete the text box should be green and the state changes to edit
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    Applicant    Mark as complete    Form
    Given the Applicant is in the application form
    When the Applicant marks as incomplete 'Public description'
    Then the text box should be editable
    and the button state should change to 'Mark as complete'
    and the question should not be marked as complete on the application overview page

*** Keywords ***
the Applicant is in the application form
    Applicant goes to the Application form

the Applicant is in the project summary question
    Applicant goes to the 'project summary' question

the Applicant is in the public description question
    Applicant goes to the 'public description' question

the Applicant is on the applicaton overview page
    Applicant goes to the Overview page

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

When the Applicant clicks a section then the Applicant navigates to the correct section
    Click Element    link=Application details
    Location Should Be    ${APPLICATION_DETAILS_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=Project summary
    Location Should Be    ${PROJECT_SUMMARY_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=Public description
    Location Should Be    ${PUBLIC_DESCRIPTION_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=Scope
    Location Should Be    ${SCOPE_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=1. Business opportunity
    Location Should Be    ${BUSINESS_OPPORTUNITY_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=2. Potential market
    Location Should Be    ${POTENTIAL_MARKET_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=3. Project exploitation
    Location Should Be    ${PROJECT_EXPLOITATION_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=4. Economic benefit
    Location Should Be    ${ECONOMIC_BENEFIT_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=5. Technical approach
    Location Should Be    ${TECHNICAL_APPROACH_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=6. Innovation
    Location Should Be    ${INNOVATION_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=7. Risks
    Location Should Be    ${RISKS_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=8. Project team
    Location Should Be    ${PROJECT_TEAM_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=9. Funding
    Location Should Be    ${FUNDING_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=10. Adding value
    Location Should Be    ${ADDING_VALUE_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=Your finances
    Location Should Be    ${YOUR_FINANCES_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview
    Click Element    link=Finances overview
    Location Should Be    ${FINANCES_OVERVIEW_URL}
    Page Should Not Contain Element    css=body.error
    Click Element    link=Application Overview

the word count should be available in the text area
    Page Should Contain Element    css=#form-input-11 .count-down

When the Applicant edits the Public description
    Input Text    css=#form-input-12 .editor    &nbsp;
    Focus    css=.app-submit-btn
    Sleep    1s
    Wait Until Element Contains    css=#form-input-12 .count-down    500
    Input Text    css=#form-input-12 .editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.
    Focus    css=.app-submit-btn
    Sleep    1s

the word count should be correct for the Public description
    sleep    1s
    Element Should Contain    css=#form-input-12 .count-down    500

And when the Applicant edits the Project description question (500 words)
    Click Element    link=Scope (Gateway question)
    Input Text    css=#form-input-12 .editor    &nbsp;
    Input Text    css=#form-input-12 .editor    0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 90 1 2 3 4 5 6 7 8 9 10 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8
    Focus    css=.app-submit-btn
    Sleep    2s

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

the word count for the Public description question should be correct (0 words)
    Element Should Contain    css=#form-input-12 span.count-down    0

the Applicant edits 'Public description' and marks it as complete
    Clear Element Text    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    Hi, I’m a robot @#$@#$@#$
    Click Element    css=#form-input-12 div.textarea-footer button[name="mark_as_complete"]

the question should be marked as complete on the application overview page
    Go To    ${APPLICATION_OVERVIEW_URL}
    Page Should Contain Element    css=#form-input-12 .complete

the Applicant marks as incomplete 'Public description'
    Click Button    css=#form-input-12 div.textarea-footer > button[name="mark_as_incomplete"]

the text box should be editable
    Element Should Be Enabled    css=#form-input-12 textarea

the button state should change to 'Mark as complete'
    Page Should Contain Element    css=#form-input-12 button    Mark as complete

the question should not be marked as complete on the application overview page
    Go To    ${APPLICATION_OVERVIEW_URL}
    Page Should Contain Element    css=#form-input-12    Question element found on application overview
    Page Should Not Contain Element    css=#form-input-12 div.marked-as-complete img.marked-as-complete    Mark as complete class is not found, that's correct

the last update date of question 1 is a date in the past
    Page Should Contain Element    css=#question-1
    Element Should Contain    css=#question-1    Last updated: 18 September 10:33AM by you

the Applicant edits question 1
    Clear Element Text    css=#question-1 textarea
    Input Text    css=#question-1 textarea    Check last updated date.
    Focus    css=.app-submit-btn
    Sleep    2s

the last update date should be updated
    Page Should Contain Element    css=#question-1
    Element Should Contain    css=#question-1    Last updated: Today by you

the applicant is on section 'Your business proposition'
    Go To    ${YOUR_BUSINESS_URL}

the applicant is on the application overview page
    Go To    ${APPLICATION_OVERVIEW_URL}

opens the 'Your business proposition' section
    #Click Button    css=#section-3 button
