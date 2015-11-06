*** Settings ***
Suite Setup       Login as user    &{collaborator1_credentials}
Suite Teardown    User closes the browser
Resource          GLOBAL_LIBRARIES.robot
Resource          GLOBAL_VARIABLES.robot
Resource          Login_actions.robot
Resource          USER_CREDENTIALS.robot

*** Test Cases ***
lead applicant makes a change to the question
    When User opens the question
    When the lead Applicant edits the question
    When the lead Applicant assigns the question 13 to the collaborator    Jessica Doe
    Then the success assignment message should show

*** Keywords ***
User opens the question
    Go to    ${APPLICATION_COLLABORATION_QUESTION}

the Applicant adds some text to the question
    Clear Element Text    name=question[13]
    Input Text    name=question[11]    Save test #123
    Focus    css=.app-submit-btn
    Sleep    2s

the lead Applicant edits the question
    Wait Until Page Contains Element    css=#question-13 .count-down    499
    Input Text    name=question[13]    oi. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.

the lead Applicant assigns the question 13 to the collaborator
    [Arguments]    ${assignee_name}
    Click Element    //*[@id="question-13"]/div/div[2]/div[2]/div/div[1]/button
    Wait Until Page Contains Element    //*[@id="question-13”]//button[contains(text(), “Jessica Doe”)]

the success assignment message should show
    Wait Until Element Is Visible    css=body > div.event-alert > p > span

