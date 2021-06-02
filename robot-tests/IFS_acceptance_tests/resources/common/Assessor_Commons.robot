*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***
${assessor_ben}              Benjamin Nixon
${assessor_joel}             Joel George
${assessor_madeleine}        Madeleine Martin
${assessor_riley}            Riley Butler
${assessor_ben_email}        benjamin.nixon@gmail.com
${assessor_joel_email}       joel.george@gmail.com
${assessor_joel_id}          ${user_ids['${assessor_joel_email}']}
${assessor_madeleine_email}  madeleine.martin@gmail.com
${assessor_riley_email}      riley.butler@gmail.com
${aaron_robertson_email}     aaron.robertson@load.example.com
${peter_styles_email}        peter.styles@load.example.com
${Neural_network_application}          ${application_ids["${CLOSED_COMPETITION_APPLICATION_TITLE}"]}
${computer_vision_application_name}    Computer vision and machine learning for transport networks
${computer_vision_application}         ${application_ids["${computer_vision_application_name}"]}
${crowd_source_application_name}       Crowd sourced cycling navigator
${crowd_source_application}            ${application_ids["${crowd_source_application_name}"]}

*** Keywords ***
Invited guest user log in
    [Arguments]  ${email}  ${password}
    Logging in and Error Checking  ${email}  ${password}

the assessor adds score and feedback for every question
    [Arguments]   ${no_of_questions}
    The user clicks the button/link                       link = Scope
    The user selects the index from the drop-down menu    1    css = .research-category
    The user clicks the button/link                       jQuery = label:contains("Yes")
    The user enters text to a text field                  css = .editor    Testing scope feedback text
    Wait for autosave
    mouse out  css = .editor
    Wait Until Page Contains Without Screenshots          Saved!
    :FOR  ${INDEX}  IN RANGE  1  ${no_of_questions}
      \    the user clicks the button/link    css = .next
      \    The user selects the option from the drop-down menu    10    css = .assessor-question-score
      \    The user enters text to a text field    css = .editor    Testing feedback text
      \    Wait for autosave
      \    mouse out  css = .editor
      \    Wait Until Page Contains Without Screenshots    Saved!
    The user clicks the button with resubmission              jquery = button:contains("Save and return to assessment overview")

the competition admin invites assessors to the competition
    the competition admin selects assessors and add them to invite list
    the competition admin should not see invited assessors on find tab

the competition admin selects assessors and add them to invite list
#competition admin selecting the assessor name checkboxs
    the user clicks the button/link      jQuery = tr:contains("${assessor_ben}") label
    the user clicks the button/link      jQuery = tr:contains("${assessor_joel}") label
    the user clicks the button/link      jquery = tr:contains("${assessor_madeleine}") label
    the user clicks the button/link      jquery = tr:contains("${assessor_riley}") label
    the user clicks the button/link      jQuery = button:contains("Add selected to invite list")
    the user should see the element      jQuery = td:contains("${assessor_ben}") + td:contains("${assessor_ben_email}")
    the user should see the element      jQuery = td:contains("${assessor_joel}") + td:contains("${assessor_joel_email}")
    the user should see the element      jQuery = td:contains("${assessor_madeleine}") + td:contains("${assessor_madeleine_email}")
    the user should see the element      jQuery = td:contains("${assessor_riley}") + td:contains("${assessor_riley_email}")

the competition admin should not see invited assessors on find tab
    When the user clicks the button/link      link = Find
    Then the user should not see the element  jQuery = td:contains("${assessor_ben}")
    And the user should not see the element   jQuery = td:contains("${assessor_joel}")
    And the user should not see the element   jquery = tr:contains("${assessor_madeleine}")

the user moves the closed competition to panel
    the user clicks the button/link     jQuery = button:contains("Notify assessors")
    the user clicks the button/link     jQuery = button:contains("Close assessment")
    the user should see the element          jQuery = h1:contains("Panel")

the compadmin can remove an assessor or application from the invite list
    [Arguments]   ${assessor_or_application}
    the user clicks the button/link      jQuery = tr:contains("${assessor_or_application}") button:contains("Remove")
    the user clicks the button/link      link = Find
    the user should see the element      jQuery = tr:contains("${assessor_or_application}")

the compAdmin resends the invites for interview panel
    [Arguments]  ${resendAssessor1}   ${resendAssessor2}
    the user clicks the button/link      jQuery = button:contains("Resend invites")
    the user should see the element      jQuery = h2:contains("Recipients") ~ p:contains("${resendAssessor1}")
    the user should see the element      jQuery = h2:contains("Recipients") ~ p:contains("${resendAssessor2}")
    the user clicks the button/link      jQuery = button:contains("Send invite")

Get the total number of submitted applications
    ${NUMBER_OF_APPLICATIONS} =     Get Element Count    //div/table/tbody/tr
    Set Test Variable    ${NUMBER_OF_APPLICATIONS}

The internal user invites a user as an assessor
    [Arguments]  ${name}  ${email}
    the user clicks the button/link                      jQuery = span:contains("Add a non-registered assessor to your list")
    The user enters text to a text field                 css = #invite-table tr:nth-of-type(1) td:nth-of-type(1) input  ${name}
    The user enters text to a text field                 css = #invite-table tr:nth-of-type(1) td:nth-of-type(2) input  ${email}
    the user selects the option from the drop-down menu  Emerging and enabling  css = .js-progressive-group-select
    the user selects the option from the drop-down menu  Emerging technology    id = grouped-innovation-area
    the user clicks the button/link                      jQuery = .govuk-button:contains("Add assessors to list")

the user should see the competition details
    [Arguments]  ${comp_name}  ${comp_status}  ${sector}  ${area}  ${link}  ${link2}
    the user should see the element      jQuery =.govuk-caption-l:contains("${comp_name}")
    the user should see the element      jQuery =h1:contains("${comp_status}")
    the user should see the element      jQuery = dt:contains("Competition type") ~ dd:contains("Programme")
    the user should see the element      jQuery = dt:contains("Innovation sector") ~ dd:contains("${sector}")
    the user should see the element      jQuery = dt:contains("Innovation area") ~ dd:contains("${area}")
    #The following checks test if the correct buttons are disabled
    the user should see the element      jQuery = .disabled:contains("${link}")
    the user should see the element      jQuery = a:contains("Manage assessments")
    the user should see the element      jQuery = a:contains("${link2}")

comp admin navigate to manage applications
    the user clicks the button/link       link = ${IN_ASSESSMENT_COMPETITION_NAME}
    the user clicks the button/link       jQuery = a:contains("Manage assessments")
    the user clicks the button/link       jQuery = a:contains("Manage applications")

assessor should see the competition terms and conditions
    [Arguments]  ${back_link}
    the user expands the section           Award terms and conditions
    the user clicks the button/link        jQuery = a:contains("Innovate UK")
    the user should see the element        jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    [Teardown]  the user clicks the button/link  link = ${back_link}

the assessor accept the application
    [Arguments]   ${comp_name}  ${application_name}
    the user clicks the button/link       jQuery = h2:contains("Attend panel") + ul li h3:contains("${comp_name}")
    the user clicks the button/link       jQuery = .progress-list div:contains("${application_name}") ~ div a:contains("Accept or reject")
    the user selects the radio button     reviewAccept  true
    the user clicks the button/link       css = button[type="submit"]  # Confirm

the user adds an assessor to application
    [Arguments]   ${CheckboxId}
    the user selects the checkbox     ${CheckboxId}
    the user clicks the button/link   jQuery = button:contains("Add to application")

the user adds an application to an assessor
    [Arguments]   ${CheckboxId}
    the user selects the checkbox     ${CheckboxId}
    the user clicks the button/link   jQuery = button:contains("Add to assessor")