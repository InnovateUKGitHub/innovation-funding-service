*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***
${assessor_ben}              Benjamin Nixon
${assessor_joel}             Joel George
${assessor_madeleine}        Madeleine Martin
${assessor_riley}            Riley Butler
${assessor_ben_email}        benjamin.nixon@gmail.com
${assessor_joel_email}       joel.george@gmail.com
${assessor_madeleine_email}  madeleine.martin@gmail.com
${assessor_riley_email}      riley.butler@gmail.com
${aaron_robertson_email}     aaron.robertson@load.example.com
${Neural_network_application}      ${application_ids["${CLOSED_COMPETITION_APPLICATION_TITLE}"]}
${computer_vision_application_name}  Computer vision and machine learning for transport networks
${computer_vision_application}     ${application_ids["${computer_vision_application_name}"]}
${crowd_source_application_name}   Crowd sourced cycling navigator
${crowd_source_application}        ${application_ids["${crowd_source_application_name}"]}

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

the competition admin invites assessors to the competition
    the competition admin selects assessors and add them to invite list
    the competition admin should not see invited assessors on find tab

the competition admin selects assessors and add them to invite list
#competition admin selecting the assessor name checkboxs
    the user clicks the button/link      jQuery=tr:contains("${assessor_ben}") label
    the user clicks the button/link      jQuery=tr:contains("${assessor_joel}") label
    the user clicks the button/link      jquery=tr:contains("${assessor_madeleine}") label
    the user clicks the button/link      jquery=tr:contains("${assessor_riley}") label
    the user clicks the button/link      jQuery=button:contains("Add selected to invite list")
    the user should see the element      jQuery=td:contains("${assessor_ben}") + td:contains("${assessor_ben_email}")
    the user should see the element      jQuery=td:contains("${assessor_joel}") + td:contains("${assessor_joel_email}")
    the user should see the element      jQuery=td:contains("${assessor_madeleine}") + td:contains("${assessor_madeleine_email}")
    the user should see the element      jQuery=td:contains("${assessor_riley}") + td:contains("${assessor_riley_email}")

the competition admin should not see invited assessors on find tab
    When the user clicks the button/link      link=Find
    Then the user should not see the element  jQuery=td:contains("${assessor_ben}")
    And the user should not see the element   jQuery=td:contains("${assessor_joel}")
    And the user should not see the element   jquery=tr:contains("${assessor_madeleine}")

the user moves the closed competition to panel
    the user clicks the button/link     jQuery=button:contains("Notify assessors")
    the user clicks the button/link     jQuery=button:contains("Close assessment")

the compadmin can remove an assessor or application from the invite list
    [Arguments]   ${assessor_or_application}
    the user clicks the button/link      jQuery=td:contains("${assessor_or_application}") ~ td:contains("Remove")
    the user clicks the button/link      link=Find
    the user should see the element      jQuery=tr:contains("${assessor_or_application}")

the compAdmin resends the invites for interview panel
    [Arguments]  ${resendAssessor1}   ${resendAssessor2}
    the user clicks the button/link      jQuery=button:contains("Resend invites")
    the user should see the element      jQuery=h2:contains("Recipients") ~ p:contains("${resendAssessor1}")
    the user should see the element      jQuery=h2:contains("Recipients") ~ p:contains("${resendAssessor2}")
    the user clicks the button/link      jQuery=button:contains("Send invite")

Get the total number of submitted applications
    ${NUMBER_OF_APPLICATIONS}=    Get matching xpath count    //div[2]/table/tbody/tr
    Set Test Variable    ${NUMBER_OF_APPLICATIONS}