*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Keywords ***
Invited guest user log in
    [Arguments]  ${email}  ${password}
    Logging in and Error Checking  ${email}  ${password}

the assessor adds score and feedback for every question
    [Arguments]   ${no_of_questions}
    The user clicks the button/link                       link=Scope
    The user selects the index from the drop-down menu    1    css=.research-category
    The user clicks the button/link                       jQuery=label:contains("Yes")
    The user enters text to a text field                  css=.editor    Testing scope feedback text
    mouse out  css=.editor
    Wait Until Page Contains Without Screenshots          Saved!
    :FOR  ${INDEX}  IN RANGE  1  ${no_of_questions}
      \    the user clicks the button/link    css=.next
      \    The user selects the option from the drop-down menu    10    css=.assessor-question-score
      \    The user enters text to a text field    css=.editor    Testing feedback text
      \    mouse out  css=.editor
      \    Wait Until Page Contains Without Screenshots    Saved!
    The user clicks the button/link               jquery=button:contains("Save and return to assessment overview")
