*** Settings ***
Documentation     -INFUND-225 As an assessor and I am signed in, I have an overview of the competitions for which I assess the applications, so that I can see my workload.
...
...               -INFUND-246 As an assessor I want to see my assessment progress at competition level (how many assessments completed vs. total), so I can manage workload.
...
...               -INFUND-284- As an assessor I can log into the system to be redirected to my dashboard, so I can view my assessments
...
...               INFUND-337
Suite Setup       Login as user    &{assessor_credentials}
Suite Teardown    TestTeardown User closes the browser
Test Setup
Test Teardown
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/Assessor_actions.robot

*** Variables ***
${reject_application_name}    Security for the Internet of Things
${accept_application_name}    Using natural gas to heat homes
${competition_name}    Technology Inspired
${deadline_month}    December deadline
${deadline_day}    31
${competitions_for_assessment_string}    Competitions for Assessment
${competition_details_page_title}    Competition Details
@{competitions_assessment_progress_before}    1    4
@{competitions_assessment_progress_after}    1    3
${partners_header_text}    Partners
${partner_to_check}    Ludlow
${accept_application_first_question_title}    How does your project align with the scope of this competition?
${persistence_application_name}    A new innovative solution

*** Test Cases ***
Assessment progress is 1 out of 4
    [Documentation]    INFUND-302
    [Tags]    Assessor
    When Assessor is viewing the Competitions list
    Then Competitions progress should show    @{competitions_assessment_progress_before}

Assessor can see the applications details page
    [Documentation]    INFUND-337
    [Tags]    Assessor
    When Assessor clicks the competition
    Then Competition's details page should be visible

Application invitation review page shows the title
    [Documentation]    INFUND-329
    [Tags]    Assessor
    Given Assessor is viewing the Competitions Applications list
    When Assessor opens an application    ${accept_application_name}
    Then Application invitation Review page shows the Application title

Application invitation review page shows partners
    [Documentation]    INFUND-329
    [Tags]    Assessor
    Given Assessor is viewing the Competitions Applications list
    When Assessor opens an application    ${accept_application_name}
    Then Application invitation Review page shows the Partners organisations

Application state changes when accepting an invitation for assessment
    [Documentation]    INFUND-338
    [Tags]    Assessor
    Given Assessor is viewing the Competitions Applications list
    When Assessor opens an application    ${accept_application_name}
    And Assessor accepts the application
    Then Application status should change to open    ${accept_application_name}

Application state changes when rejecting an invitation for assessment
    [Documentation]    INFUND-338
    [Tags]    Assessor
    Given Assessor is viewing the Competitions Applications list
    And Assessor opens an application    ${reject_application_name}
    When Assessor rejects the application
    And Assessor enters the reason for rejection
    Then Application is not visible in the applications list    ${reject_application_name}

Application Summary sections can be opened and closed
    [Documentation]    INFUND-354
    [Tags]    Assessor    Failing
    Given Assessor is viewing the Competitions Applications list
    When Assessor opens an application    ${accept_application_name}
    And Assessor clicks the Review Button
    and the Assessor clicks a collapsible section
    Then the Section contents will be visible

Application Summary sections contain questions
    [Documentation]    INFUND-354
    [Tags]    Assessor    Failing
    Given Assessor is viewing the Competitions Applications list
    When Assessor opens an application    ${accept_application_name}
    And Assessor clicks the Review Button
    And the Assessor clicks a collapsible section
    Then the Section contents will contain a question    ${accept_application_first_question_title}

Application Summary shows your feedback when appropriate
    [Documentation]    INFUND-357
    [Tags]    Assessor    Failing
    Given Assessor is viewing the Competitions Applications list
    And Assessor opens an application    ${accept_application_name}
    And Assessor clicks the Review Button
    When Assessor selects "No"
    And Your feedback textarea appears
    And Assessor selects "Yes"
    Then Your feedback textarea disappears

Application Summary returns an error message when submitting empty feedback
    [Documentation]    INFUND-357
    [Tags]    Assessor
    Given Assessor is viewing the Competitions Applications list
    And Assessor opens an application    ${accept_application_name}
    And Assessor clicks the Review Button
    When Assessor selects "No"
    And Assessor save while Your Feedback is empty
    Then form will not be submitted

Assessor can see the competitions that he/she accepted
    [Documentation]    INFUND-292
    [Tags]    Assessor
    When Assessor is viewing the Competitions list
    Then Assessor sees competitions he or she accepted

Competition has a deadline
    [Documentation]    INFUND-292
    [Tags]    Assessor
    When Assessor is viewing the Competitions list
    Then Competition has a deadline

Competition has days remaining
    [Documentation]    INFUND-292
    [Tags]    Assessor
    When Assessor is viewing the Competitions list
    Then Competition has a number of days remaining

Competition for Assessment count
    [Documentation]    INFUND-292
    [Tags]    Assessor
    When Assessor is viewing the Competitions list
    Then Competitions for Assessment shows an amount of competitions

Applications details page has two lists
    [Documentation]    INFUND-322
    [Tags]    Assessor
    When Assessor is viewing the Competitions list
    When Assessor clicks the competition
    Then Details page should contain a list with the applications for assessment
    And Page should contain a list with the submitted assessments

Assessment progress is 1 out of 3
    [Documentation]    INFUND-302
    [Tags]    Assessor
    When Assessor is viewing the Competitions list
    Then Competitions progress should show    @{competitions_assessment_progress_after}

Application Review changes are persisted when saving
    [Documentation]    INFUND-354
    [Tags]    Assessor    Failing
    ${section_name} =    Set Variable    Scope
    ${feedback_selection_value} =    Set Variable    No
    ${feedback_textarea_value} =    Set Variable    Test feedback text UNIQUE123
    Given Assessor is viewing the Competitions Applications list
    And Assessor opens an application    ${persistence_application_name}
    And Assessor clicks a section    ${section_name}
    When the assessor enters feedback    ${feedback_selection_value}    ${feedback_textarea_value}
    And refreshes the page
    Then the feedback should be present    ${feedback_selection_value}    ${feedback_textarea_value}

*** Keywords ***
Assessor clicks the Review Button
    #Click Element    link=Review assessment
    Click Element    css=a.button.button-pull-right.no-margin

Assessor selects "No"
    Select From List    xpath=//*[@name="is-suitable-for-funding"]    No

Your Feedback textarea appears
    Wait Until Element Is Visible    xpath=//*[@id="recommendation-feedback-group"]

Competition's details page should be visible
    Page Should Contain    ${competition_details_page_title}

Details page should contain a list with the applications for assessment
    Page Should Contain Element    css=#content > div.my-applications > div.in-progress

Assessor accepts the application
    Click Element    xpath=//button[@name="accept"]

Application status should change to open
    [Arguments]    ${application_name}
    Element Should Contain    xpath=//li[.//a[contains(text(),'${application_name}')]]//div[contains(@class,"open")]    Open

Assessor rejects the application
    Click Element    xpath=//*[@id="content"]//*[@class="application-summary"]//a[contains(text(),"Reject")]

Assessor enters the reason for rejection
    Input Text    id=rejection-observations    Test rejections
    Click Element    xpath=//*[@class="modal-reject-assessment"]//button[@name="reject"]

Page should contain a list with the submitted assessments
    Page Should Contain Element    css=#content > div.my-applications > div.submitted

Application is not visible in the applications list
    [Arguments]    ${application_name}
    Page Should Not Contain Element    xpath=//li//a[contains(text(),'${application_name}')]

Assessor opens an application
    [Arguments]    ${application_name}
    Click Element    xpath=//li//a[contains(text(),'${application_name}')]
    #Click Element    xpath=//*[contains(text(),"${application_name}")]

Assessor is viewing the Competitions list
    Go To    ${SERVER}/assessor/dashboard

Assessor sees competitions he or she accepted
    Element Should Be Visible    xpath=//*[@class='my-applications' and .//*[contains(text(),"${competition_name}")]]

Competition has a deadline
    Element Should Be Visible    css=.competition-deadline
    Element Should Contain    css=.competition-deadline .day    ${deadline_day}
    Element Should Contain    css=.competition-deadline .month    ${deadline_month}

Competition has a number of days remaining
    ${number_of_days_element_text}=    Get Text    //*[@class='in-progress' and .//*[contains(text(),"Technology Inspired")]]//div[./span[contains(text(), "Days left")]]/div
    Should Match Regexp    ${number_of_days_element_text}    ^[0-9]{1,3}$

Competitions for Assessment shows an amount of competitions
    ${competitions_amount_element_text} =    Get Text    xpath=//*[contains(text(),'${competitions_for_assessment_string}')]
    Should Match Regexp    ${competitions_amount_element_text}    ${competitions_for_assessment_string} \\([0-9]+\\)

Assessor is viewing the Competitions Applications list
    Go To    ${SERVER}/assessor/dashboard
    Click Element    link=${competition_name}

Competitions progress should show
    [Arguments]    @{assessment_progress}
    ${assessment_progress_element} =    Set Variable    //*[@class='in-progress' and .//*[contains(text(),"${competition_name}")]]//div[//*[contains(text(), "Assessment progress")]]/div/p/strong
    ${assessment_progress_assessed}=    Get Text    xpath=${assessment_progress_element}/span[1]
    ${assessment_progress_total}=    Get Text    xpath=${assessment_progress_element}/span[2]
    Should Be Equal As Integers    ${assessment_progress_assessed}    @{assessment_progress}[0]
    Should Be Equal As Integers    ${assessment_progress_total}    @{assessment_progress}[1]
    Should Be True    ${assessment_progress_assessed}<=${assessment_progress_total}

Application invitation Review page shows the Application title
    #Get Value    ${accept_application_name}
    Element Should Be Visible    xpath=//*[contains(text(),"${accept_application_name}")]

Application invitation Review page shows the Partners organisations
    Element Should Be Visible    xpath=//*[contains(text(),"${partners_header_text}")]/following-sibling::*[1]//*[contains(text(),"${partner_to_check}")]

Assessor selects "Yes"
    Select From List    xpath=//*[@name="is-suitable-for-funding"]    Yes

Your Feedback textarea disappears
    Wait Until Element Is Not Visible    xpath=//*[@id="recommendation-feedback-group"]

Assessor save while Your Feedback is empty
    Input Text    name=suitable-for-funding-feedback    ${EMPTY}
    Click Element    name=confirm-submission

Form will not be submitted
    Location Should Contain    summary

the Assessor clicks a collapsible section
    Click Element    xpath=//*[@aria-controls="collapsible-1"]

the Section contents will be visible
    Wait Until Element Is Visible    xpath=//*[@id="collapsible-1"]

the Section contents will contain a question
    [Arguments]    ${question_title}
    Wait Until Element Is Visible    xpath=//*[@id="collapsible-1"]//*[contains(text(), "${question_title}")]

Assessor clicks a section
    [Arguments]    ${section_name}
    Click Element    link=${section_name}
    #Click Element    xpath=//li//a[contains(text(),'${section_name}')]
    #Click Element    xpath=//*[contains(text(),"${section_name}")]

the assessor enters feedback
    [Arguments]    ${feedback_dropdown_value}    ${feedback_text_value}
    Select From List    xpath=//*[@class="question"]//select    ${feedback_dropdown_value}
    Input Text    xpath=//*[@class="question"]//textarea    ${feedback_text_value}
    Sleep    1s

refreshes the page
    Reload Page

the feedback should be present
    [Arguments]    ${feedback_dropdown_value}    ${feedback_text_value}
    ${selected_value} =    Get Selected List Value    xpath=//*[@class="question"]//select
    Should Be Equal As Strings    ${selected_value}    ${feedback_dropdown_value}
    Textarea Value Should Be    xpath=//*[@class="question"]//textarea    ${feedback_text_value}
