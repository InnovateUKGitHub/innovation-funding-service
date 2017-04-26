*** Settings ***
Documentation     INFUND-550 As an assessor I want the ‘Assessment summary’ page to show me complete and incomplete sections, so that I can easily judge how much of the application is left to do
...
...               INFUND-1485 As an Assessor I want to be able to provide my final feedback for the application so that I can tell Innovate UK whether or not I recommend the application for funding.
...
...               INFUND-4217 Assessor journey form validation
...
...               INFUND-3720 As an Assessor I can see deadlines for the assessment of applications currently in assessment on my dashboard, so that I am reminded to deliver my work on time
...
...               INFUND-5179 Introduce new resource DTO classes for recommending and rejecting assessments
...
...               INFUND-5765 As an assessor I need to be able to progress an assessment to the state of Assessed so that I am able to select it to submit
...
...               INFUND-5712 As an Assessor I can review the recommended for funding status of applications that I have assessed so that I can track my work
...
...               INFUND-3726 As an Assessor I can select one or more assessments to submit so that I can work in my preferred way
...
...               INFUND-3724 As an Assessor and I am looking at my competition assessment dashboard, I can review the status of applications that I am allocated so that I can track my work
...
...               INFUND-5739 As an Assessor I can submit all the applications that I have selected so that my assessment work is completed
...
...               INFUND-3743 As an Assessor I want to see all the assessments that I have already submitted in this competition so that I can see what I have done already.
...
...               INFUND-3719 As an Assessor and I have accepted applications to assess within a competition, I can see progress on my dashboard so I can keep track of my work
Suite Setup       guest user log-in    felix.wilson@gmail.com    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Summary:All the sections are present
    [Documentation]    INFUND-4648
    [Tags]    HappyPath
    When The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the user should see that the element is disabled    id=submit-assessment-button
    And the user clicks the button/link    link=Intelligent Building
    And the user clicks the button/link    jQuery=.button:contains("Review and complete your assessment")
    Then the user should see the element    jQuery=h2:contains("Review assessment")
    And the user should see the element    jQuery=span:contains("Do you believe that this application is suitable for funding?")
    And the user should see the element    id=form-input-feedback
    And the user should see the element    id=form-input-comments

Summary:Number of days remaining until assessment submission
    [Documentation]    INFUND-3720
    [Tags]    HappyPath
    Then The user should see the text in the page    days left to submit
    And the days remaining should be correct (Top of the page)    2068-01-28

Summary shows questions as incomplete
    [Documentation]    INFUND-550
    Then the collapsible button should contain    jQuery=button:contains(1. Business opportunity)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(2. Potential market)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(3. Project exploitation)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(4. Economic benefit)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(5. Technical approach)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(6. Innovation)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(7. Risks)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(8. Project team)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(9. Funding)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(10. Adding value)    Incomplete
    And the collapsible button should contain    jQuery=button:contains(Scope)    Incomplete

Summary: Questions should show without score
    [Documentation]    INFUND-550
    Then the collapsible button should contain    jQuery=button:contains(1. Business opportunity)    N/A
    And the collapsible button should contain    jQuery=button:contains(2. Potential market)    N/A
    And the collapsible button should contain    jQuery=button:contains(3. Project exploitation)    N/A
    And the collapsible button should contain    jQuery=button:contains(4. Economic benefit)    N/A
    And the collapsible button should contain    jQuery=button:contains(5. Technical approach)    N/A
    And the collapsible button should contain    jQuery=button:contains(6. Innovation)    N/A
    And the collapsible button should contain    jQuery=button:contains(7. Risks)    N/A
    And the collapsible button should contain    jQuery=button:contains(8. Project team)    N/A
    And the collapsible button should contain    jQuery=button:contains(9. Funding)    N/A
    And the collapsible button should contain    jQuery=button:contains(10. Adding value)    N/A
    [Teardown]    The user clicks the button/link    link=Back to your assessment overview

Summary:Questions should show as complete
    [Documentation]    INFUND-550
    [Tags]    HappyPath
    [Setup]    Go to    ${SERVER}/assessment/assessor/dashboard/competition/4
    Given The user clicks the button/link    link=Intelligent Building
    And the user adds score and feedback for every question
    When the user clicks the button/link    link=Review and complete your assessment
    Then the collapsible button should contain    jQuery=button:contains(1. Business opportunity)    Complete
    And the collapsible button should contain    jQuery=button:contains(2. Potential market)    Complete
    And the collapsible button should contain    jQuery=button:contains(3. Project exploitation)    Complete
    And the collapsible button should contain    jQuery=button:contains(4. Economic benefit)    Complete
    And the collapsible button should contain    jQuery=button:contains(5. Technical approach)    Complete
    And the collapsible button should contain    jQuery=button:contains(6. Innovation)    Complete
    And the collapsible button should contain    jQuery=button:contains(7. Risks)    Complete
    And the collapsible button should contain    jQuery=button:contains(8. Project team)    Complete
    And the collapsible button should contain    jQuery=button:contains(9. Funding)    Complete
    And the collapsible button should contain    jQuery=button:contains(10. Adding value)    Complete
    And the collapsible button should contain    jQuery=button:contains(Scope)    Complete

Summary:Questions should show the scores
    [Documentation]    INFUND-550
    [Tags]    HappyPath
    Then The user should see the text in the page    Total: 100/100
    And The user should see the text in the page    100%
    And the collapsible button should contain    jQuery=button:contains(1. Business opportunity)    Score 10/10
    And the collapsible button should contain    jQuery=button:contains(2. Potential market)    Score 10/10
    And the collapsible button should contain    jQuery=button:contains(3. Project exploitation)    Score 10/10
    And the collapsible button should contain    jQuery=button:contains(4. Economic benefit)    Score 10/10
    And the collapsible button should contain    jQuery=button:contains(5. Technical approach)    Score 10/10
    And the collapsible button should contain    jQuery=button:contains(6. Innovation)    Score 10/10
    And the collapsible button should contain    jQuery=button:contains(7. Risks)    Score 10/10
    And the collapsible button should contain    jQuery=button:contains(8. Project team)    Score 10/10
    And the collapsible button should contain    jQuery=button:contains(9. Funding)    Score 10/10
    And the collapsible button should contain    jQuery=button:contains(10. Adding value)    Score 10/10

Summary:Feedback should show in each section
    [Documentation]    INFUND-550
    When the user clicks the button/link    jQuery=button:contains(1. Business opportunity)
    Then the user should see the text in the page    Testing Business opportunity feedback text
    When the user clicks the button/link    jQuery=button:contains(2. Potential market)
    Then the user should see the text in the page    Testing Potential market feedback text
    When the user clicks the button/link    jQuery=button:contains(3. Project exploitation)
    Then the user should see the text in the page    Testing Project exploitation feedback text
    When the user clicks the button/link    jQuery=button:contains(4. Economic benefit)
    Then the user should see the text in the page    Testing Economic benefit feedback text
    When the user clicks the button/link    jQuery=button:contains(5. Technical approach)
    Then the user should see the text in the page    Testing Technical approach feedback text
    When the user clicks the button/link    jQuery=button:contains(6. Innovation)
    Then the user should see the text in the page    Testing Innovation feedback text
    When the user clicks the button/link    jQuery=button:contains(7. Risks)
    Then the user should see the text in the page    Testing Risks feedback text
    When the user clicks the button/link    jQuery=button:contains(8. Project team)
    Then the user should see the text in the page    Testing Project team feedback text
    When the user clicks the button/link    jQuery=button:contains(9. Funding)
    Then the user should see the text in the page    Testing Funding feedback text
    When the user clicks the button/link    jQuery=button:contains(10. Adding value)
    Then the user should see the text in the page    Testing Adding value feedback text
    When the user clicks the button/link    jQuery=button:contains(Scope)
    Then the user should see the text in the page    Testing scope feedback text

Summary:Assessor can return to each question
    [Documentation]    INFUND-4648
    And the user should see the element    jQuery=#collapsible-0 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-1 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-2 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-3 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-4 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-5 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-6 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-7 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-8 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-9 a:contains(Return to this question in the application)
    And the user should see the element    jQuery=#collapsible-10 a:contains(Return to this question in the application)
    When the user clicks the button/link    jQuery=#collapsible-1 a:contains(Return to this question in the application)
    Then the user should see the text in the page    What is the business opportunity that your project addresses?
    And the user goes back to the previous page
    When the user clicks the button/link    jQuery=#collapsible-10 a:contains(Return to this question in the application)
    Then the user should see the text in the page    How does financial support from Innovate UK and its funding partners add value?
    And the user goes back to the previous page

Summary:Assessor should be able to re-edit before submit
    [Documentation]    INFUND-3400
    When The user clicks the button/link    jQuery=#collapsible-1 a:contains(Return to this question)
    and The user should see the text in the page    What is the business opportunity that your project addresses?
    When the user selects the option from the drop-down menu    8    id=assessor-question-score
    And the user enters text to a text field    css=.editor    This is a new feedback entry.
    And the user clicks the button/link    jQuery=a:contains(Back to your assessment overview)
    And the user clicks the button/link    jQuery=a:contains(Review and complete your assessment)
    When The user clicks the button/link    jQuery=button:contains(1. Business opportunity)
    Then the user should see the text in the page    This is a new feedback entry.
    And the user should see the text in the page    8

Summary:Funding Decision Validations
    [Documentation]    INFUND-1485
    ...
    ...    INFUND-4217
    ...
    ...    INFUND-5228
    [Tags]
    When The user clicks the button/link    jQuery=.button:contains(Save assessment)
    And The user should see an error    Please indicate your decision.
    And The user enters text to a text field    id=feedback    ${EMPTY}
    And The user enters text to a text field    id=comment    ${EMPTY}
    Then the user selects the radio button    fundingConfirmation    false
    And The user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then The user should see an error    Please enter your feedback.

Summary:Word count check(Your feedback)
    [Documentation]    INFUND-1485
    ...
    ...    INFUND-4217
    ...
    ...    INFUND-5178
    ...
    ...    INFUND-5179
    [Tags]    HappyPath
    [Setup]    browser validations have been disabled
    When the user enters multiple strings into a text field    id=feedback    t    5001
    And the user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then the user should see an error    This field cannot contain more than 5,000 characters.
    When the user enters multiple strings into a text field    id=feedback    w${SPACE}    102
    And the user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.
    And the word count should be correct    Words remaining: -2
    When the user enters text to a text field    id=feedback    Testing the feedback word count.
    Then The user should not see the text in the page    Maximum word count exceeded. Please reduce your word count to 100.
    And the word count should be correct    Words remaining: 95

Summary:Word count check(Comments for InnovateUK)
    [Documentation]    INFUND-1485
    ...
    ...    INFUND-4217
    ...
    ...    INFUND-5178
    ...
    ...    INFUND-5179
    When the user enters multiple strings into a text field    id=comment    a${SPACE}    102
    And the user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.
    And the word count should be correct    Words remaining: -2
    When the user enters text to a text field    id=comment    Testing the comments word count.
    Then The user should not see the text in the page    Maximum word count exceeded. Please reduce your word count to 100.
    And the word count should be correct    Words remaining: 95

User Saves the Assessment as Recommended
    [Documentation]    INFUND-4996
    ...
    ...    INFUND-5765
    ...
    ...    INFUND-3726
    ...
    ...    INFUND-6040
    ...
    ...    INFUND-3724
    [Tags]    HappyPath
    Given the user enters text to a text field    id=feedback    ${EMPTY}
    And the user selects the radio button    fundingConfirmation    true
    When The user clicks the button/link    jQuery=.button:contains(Save assessment)
    Then The user should not see the text in the page    Please enter your feedback
    And The user should see the text in the page    Assessed
    And the user should see the element    css=li:nth-child(6) .positive
    And the user should see the element    css=li:nth-child(6) .selection-button-checkbox
    And the application should have the correct status    css=.progress-list li:nth-child(6)    Assessed

User Saves the Assessment as Not Recommended
    [Documentation]    INFUND-5712
    ...
    ...    INFUND-3726
    ...
    ...    INFUND-6040
    ...
    ...    INFUND-3724
    [Tags]    HappyPath
    [Setup]
    Given The user clicks the button/link    link=Park living
    And the user adds score and feedback for every question
    And the user clicks the button/link    jQuery=.button:contains("Review and complete your assessment")
    When the user selects the radio button    fundingConfirmation    false
    And the user enters text to a text field    id=feedback    Negative feedback
    And The user clicks the button/link    jQuery=.button:contains(Save assessment)
    And The user should see the element    css=li:nth-child(5) .negative
    And The user should see the element    css=li:nth-child(5) .selection-button-checkbox
    And the application should have the correct status    css=.progress-list li:nth-child(5)    Assessed
    And the application should have the correct status    css=.progress-list li:nth-child(6)    Assessed

Submit Assessments
    [Documentation]    INFUND-5739
    ...
    ...    INFUND-3743
    ...
    ...    INFUND-6358
    [Tags]    HappyPath
    Given the user should see the element    jQuery=.in-progress li:nth-child(6):contains("Intelligent Building")
    And the user should see that the element is disabled    id=submit-assessment-button
    When the user clicks the button/link    css=.in-progress li:nth-child(6) .selection-button-checkbox
    And the user clicks the button/link    jQuery=button:contains("Submit assessments")
    And The user clicks the button/link    jQuery=button:contains("Cancel")
    And The user clicks the button/link    jQuery=button:contains("Submit assessments")
    And The user clicks the button/link    jQuery=button:contains("Yes I want to submit the assessments")
    Then the application should have the correct status    css=div.submitted    Submitted assessment
    And the user should see the element    css=li:nth-child(5) .selection-button-checkbox    #This keyword verifies that only one applications has been submitted
    And The user should see the text in the page    Intelligent Building
    And The user should see the text in the page    98
    And The user should not see the element    link=Intelligent Building

Progress of the applications in Dashboard
    [Documentation]    INFUND-3719, INFUND-9007
    [Tags]    HappyPath
    ${ACCEPTED_LIST}=    Get Webelements    jQuery=.my-applications .in-progress li:not(:contains("Pending"))
    ${EXPECTED_TOTAL_ACCEPTED}=    Get Length    ${ACCEPTED_LIST}
    ${PENDING_LIST}=    Get Webelements    jQuery=.my-applications .in-progress li:contains("Pending")
    ${EXPECTED_TOTAL_PENDING}=    Get Length    ${PENDING_LIST}
    When The user navigates to the page    ${assessor_dashboard_url}
    Then the progress of the applications should be correct    ${EXPECTED_TOTAL_ACCEPTED}    ${EXPECTED_TOTAL_PENDING}
    And the user should see the text in the page    ${EXPECTED_TOTAL_PENDING} applications awaiting acceptance | ${EXPECTED_TOTAL_ACCEPTED} applications to assess

*** Keywords ***
the collapsible button should contain
    [Arguments]    ${BUTTON}    ${TEXT}
    Element Should Contain    ${BUTTON}    ${TEXT}

the user adds score and feedback for every question
    The user clicks the button/link    link=Scope
    The user selects the index from the drop-down menu    1    id=research-category
    The user clicks the button/link    jQuery=label:contains(Yes)
    The user enters text to a text field    css=.editor    Testing scope feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Business opportunity feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Potential market feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Project exploitation feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Economic benefit feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Technical approach feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Innovation feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Risks feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Project team feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Funding feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    css=.next
    The user selects the option from the drop-down menu    10    id=assessor-question-score
    The user enters text to a text field    css=.editor    Testing Adding value feedback text
    Focus    jQuery=a:contains("Sign out")
    Wait Until Page Contains Without Screenshots    Saving
    The user clicks the button/link    jquery=button:contains("Save and return to assessment overview")

the word count should be correct
    [Arguments]    ${wordCount}
    the user should see the text in the page    ${wordCount}

The user accepts the juggling is word that sound funny application
    The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    The user clicks the button/link    jQuery=a:contains("Accept or reject")
    The user should see the text in the page    Accept application
    The user clicks the button/link    jQuery=button:contains("Accept")
    The user should be redirected to the correct page    ${Assessor_application_dashboard}

the status of the status of the application should be correct
    [Arguments]    ${ELEMENT}    ${STATUS}
    Element should contain    ${ELEMENT}    ${STATUS}

the application should have the correct status
    [Arguments]    ${APPLICATION}    ${STATUS}
    element should contain    ${APPLICATION}    ${STATUS}

the progress of the applications should be correct
    [Arguments]    ${EXPECTED_TOTAL_ACCEPTED}    ${EXPECTED_TOTAL_PENDING}
    ${TOTAL_PENDING}=    Get text    css=.action-required .pending-applications    #gets the pending apps
    Should Be Equal As Integers    ${TOTAL_PENDING}    ${EXPECTED_TOTAL_PENDING}
    ${TOTAL_ACCEPTED}=    Get text    css=.action-required .accepted-applications    #gets the total number
    Should Be Equal As Integers    ${TOTAL_ACCEPTED}    ${EXPECTED_TOTAL_ACCEPTED}
