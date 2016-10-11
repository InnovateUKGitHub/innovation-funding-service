*** Settings ***
Documentation     INFUND-184: As an applicant and on the over view of the application, I am able to see the character count and status of the questions, so I am able to see if my questions are valid    #Suite Setup    Run Keywords    Guest user log-in    &{lead_applicant_credentials}
...
...               INFUND-186: As an applicant and in the application form, I should be able to change the state of a question to mark as complete, so I don't have to revisit the question.
...
...               INFUND-66: As an applicant and I am on the application form, I can fill in the questions belonging to the application, so I can apply for the competition
...
...               INFUND-42: As an applicant and I am on the application form, I get guidance for questions, so I know what I need to fill in.
...
...               INFUND-183: As a an applicant and I am in the application form, I can see the character count that I have left, so I comply to the rules of the question
...
...               INFUND-4694 As an applicant I want to be able to provide details of my previous submission if I am allowed to resubmit my project in the current competition so that I comply with Innovate UK competition eligibility criteria
Suite Setup       log in and create new application if there is not one already
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot
Resource          ../../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Test Cases ***
Application details: Previous submission
    [Documentation]    INFUND-4694
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Application details
    When the user clicks the button/link    jQuery=label:contains(Yes) input
    Then the user should see the text in the page    Please provide the details of this previous application
    And the user should see the text in the page    Previous application number
    And the user should see the text in the page    Previous application title
    When the user clicks the button/link    jQuery=label:contains(No) input
    Then The user should not see the element    id=application_details-previousapplicationnumber

Autosave in the form questions
    [Documentation]    INFUND-189
    [Tags]    HappyPath
    [Setup]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Project summary
    When the user edits the 'project summary' question
    And the user reloads the page
    Then the text should be visible

Word count works
    [Documentation]    INFUND-198
    [Tags]    HappyPath
    When the Applicant edits the Project summary
    Then the word count should be correct for the Project summary
    And the Applicant edits the Project description question (300 words)
    Then the word count for the Project description question should be correct (100 words)

Guidance of the questions
    [Documentation]    INFUND-190
    [Tags]
    When the user clicks the button/link    css=#form-input-11 .summary
    Then the user should see the element    css=#details-content-0 p

Marking a question as complete
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    HappyPath
    When the Applicant edits Project summary and marks it as complete
    Then the text box should turn to green
    And the button state should change to 'Edit'
    And the question should be marked as complete on the application overview page

Mark a question as incomplete
    [Documentation]    INFUND-210,
    ...    INFUND-202
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Project summary
    When the user clicks the button/link    css=#form-input-11 div.textarea-footer > button[name="mark_as_incomplete"]
    Then the text box should be editable
    And the button state should change to 'Mark as complete'
    And the question should not be marked as complete on the application overview page

Navigation of the form sections
    [Documentation]    INFUND-189
    [Tags]    HappyPath
    And the user clicks the button/link    link=Application details
    Then the user should see the text in the page    Enter the full title of your project
    When the applicant navigates to the next section
    Then the user should see the text in the page    Please provide a short summary of your project.
    When the applicant navigates to the next section
    Then the user should see the text in the page    Please provide a brief description of your project
    When the applicant navigates to the next section
    Then the user should see the text in the page    If your application doesn't align with the scope
    When the applicant navigates to the next section
    Then the user should see the text in the page    What should I include in the business opportunity section
    When the applicant navigates to the next section
    Then the user should see the text in the page    What should I include in the market opportunity section
    When the applicant navigates to the next section
    Then the user should see the text in the page    What should I include in the project exploitation section
    When the applicant navigates to the next section
    Then the user should see the text in the page    What should I include in the benefits section
    When the applicant navigates to the next section
    Then the user should see the text in the page    Describe the areas of work
    When the applicant navigates to the next section
    Then the user should see the text in the page    Explain how your project is innovative
    When the applicant navigates to the next section
    Then the user should see the text in the page    We recognise that many of the projects we fund are risky
    When the applicant navigates to the next section
    Then the user should see the text in the page    Describe your capability to develop and exploit this technology
    When the applicant navigates to the next section
    Then the user should see the text in the page    Tell us the total costs of the project and how
    When the applicant navigates to the next section
    Then the user should see the text in the page    What should I include in the financial support from Innovate UK section
    When the applicant navigates to the next section
    Then the user should see the text in the page    Only your organisation can see this level of detail.
    When the applicant navigates to the next section
    Then the user should see the text in the page    This is the financial overview

Review and submit button
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    When the user clicks the button/link    jQuery=.button:contains("Review & submit")
    Then the user should see the text in the page    Application Summary
    And the user should see the text in the page    Please review your application before final submission

*** Keywords ***
the text should be visible
    wait until element contains    css=#form-input-11 .editor    I am a robot

The user clicks the section link and is redirected to the correct section
    [Arguments]    ${link}    ${url}
    The user clicks the button/link    link=${link}
    The user should be redirected to the correct page    ${url}
    The user clicks the button/link    link=Application Overview

the Applicant edits the Project summary
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    Focus    css=.app-submit-btn
    Wait Until Element Contains    css=#form-input-11 .count-down    400
    Focus    css=#form-input-11 .editor
    The user enters text to a text field    css=#form-input-11 .editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.
    Focus    css=.app-submit-btn
    Sleep    500ms

the word count should be correct for the Project summary
    wait until element contains    css=#form-input-11 .count-down    369

the Applicant edits the Project description question (300 words)
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    The user enters text to a text field    css=#form-input-11 .editor    Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium libero. Cras id dui. Aenean ut
    Focus    css=.app-submit-btn
    Sleep    300ms

the text box should turn to green
    the user should see the element    css=#form-input-11 div.marked-as-complete img.marked-as-complete
    Element Should Be Disabled    css=#form-input-11 textarea

the button state should change to 'Edit'
    the user should see the element    jQuery=button:contains("Edit")

the word count for the Project description question should be correct (100 words)
    Element Should Contain    css=#form-input-11 .count-down    100

the Applicant edits Project summary and marks it as complete
    focus    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    focus    css=#form-input-11 .editor
    The user enters text to a text field    css=#form-input-11 .editor    Hi, I’m a robot @#$@#$@#$
    the user clicks the button/link    css=#form-input-11 div.textarea-footer button[name="mark_as_complete"]

the question should be marked as complete on the application overview page
    The user clicks the button/link    link=Application Overview
    The user should see the element    jQuery=#section-1 .section:nth-child(2) img[src*="/images/field/field-done-right"]

the text box should be editable
    Wait Until Element Is Enabled    css=#form-input-11 textarea

the button state should change to 'Mark as complete'
    the user should see the element    jQuery=button:contains("Mark as complete")

the question should not be marked as complete on the application overview page
    The user clicks the button/link    link=Application Overview
    Run Keyword And Ignore Error    confirm action
    the user should see the element    jQuery=#section-1 .section:nth-child(2)
    the user should not see the element    jQuery=#section-1 .section:nth-child(2) img[src*="/images/field/field-done-right"]

The applicant navigates to the next section
    The user clicks the button/link    css=.next .pagination-label
    Run Keyword And Ignore Error    confirm action
