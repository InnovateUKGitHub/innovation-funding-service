*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***
${assessor_ben}              Benjamin Nixon
${assessor_joel}             Joel George
${assessor_madeleine}        Madeleine Martin
${assessor_riley}            Riley Butler
${panel_assessor_ben}        benjamin.nixon@gmail.com
${panel_assessor_joel}       joel.george@gmail.com
${panel_assessor_madeleine}  madeleine.martin@gmail.com
${panel_assessor_riley}      riley.butler@gmail.com

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

the comp admin invite assessors for the competition
    And the user clicks the button/link      jQuery=tr:contains("${assessor_ben}") label
    And the user clicks the button/link      jQuery=tr:contains("${assessor_joel}") label
    And the user clicks the button/link      jquery=tr:contains("${assessor_madeleine}") label
    And the user clicks the button/link      jquery=tr:contains("${assessor_riley}") label
    When the user clicks the button/link     jQuery=button:contains("Add selected to invite list")
    Then the user should see the element     jQuery=td:contains("${assessor_ben}") + td:contains("${panel_assessor_ben}")
    And the user should see the element      jQuery=td:contains("${assessor_joel}") + td:contains("${panel_assessor_joel}")
    And the user should see the element      jQuery=td:contains("${assessor_madeleine}") + td:contains("${panel_assessor_madeleine}")
    And the user should see the element      jQuery=td:contains("${assessor_riley}") + td:contains("${panel_assessor_riley}")
    When the user clicks the button/link      link=Find
    Then the user should not see the element  jQuery=td:contains("${assessor_ben}")
    And the user should not see the element   jQuery=td:contains("${assessor_joel}")
    And the user should not see the element   jquery=tr:contains("${assessor_madeleine}")
