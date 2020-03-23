*** Settings ***
Documentation     INFUND-2312: Competition status in assessment
...
...               INFUND-3175: Applications in project setup still have a 'Review and submit' button
...
...               INFUND-3740: Buttons still show as if the application were editable
Suite Setup       The user logs-in in new browser  &{lead_applicant_credentials}
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${application_not_submitted_message}    This application has not been entered into the competition

*** Test Cases ***
Application shows as not submitted on the dashboard
    [Documentation]    INFUND-2741
    [Tags]  HappyPath
    Then the user should see that the application is not submitted
    And the user clicks the button/link    link = ${IN_ASSESSMENT_APPLICATION_1_TITLE}

Application shows as not submitted on the overview
    [Documentation]    INFUND-2742
    [Tags]  HappyPath
    When the user navigates to the page              ${SERVER}/application/${IN_ASSESSMENT_APPLICATION_1_NUMBER}
    Then the user should see the element            jQuery = .warning-alert:contains("${application_not_submitted_message}")

Submit button should be disabled
    [Documentation]    INFUND-2742, INFUND-2312, INFUND-3175
    [Tags]  HappyPath
    When the user navigates to the page              ${SERVER}/application/${IN_ASSESSMENT_APPLICATION_1_NUMBER}/summary
    Then the user should see the element             jQuery = .warning-alert:contains("This application has not been entered into the competition")
    And the user should not see the element          jQuery = .govuk-button:contains("Submit application")
    And the user should not see the element          jQuery = .govuk-button:contains("Review and submit")

Applicant shouldn't see the Mark as complete-Edit-Save buttons
    [Documentation]    INFUND-3740
    [Tags]  HappyPath
    Given the user navigates to the page        ${SERVER}/application/${IN_ASSESSMENT_APPLICATION_1_NUMBER}/summary
    When The user clicks the button/link        jQuery = button:contains(Project summary)
    Then The user should not see the element    jQuery = .govuk-button:contains(Mark as complete)
    And the user should not see the element     jQuery = button:contains(Edit)
    And the user navigates to the page          ${SERVER}/application/${IN_ASSESSMENT_APPLICATION_1_NUMBER}/form/question/44
    Then The user should not see the element    jQuery = button:contains("Save and return to application overview")

Already submitted application should not show error when the competition is closed
    [Documentation]    INFUND-3175
    [Tags]  HappyPath
    When the user navigates to the page                  ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link                  link = ${FUNDERS_PANEL_APPLICATION_1_TITLE}
    Then the user should not see the element             jQuery = .warning-alert:contains("Application not submitted")

*** Keywords ***
the user should see that the application is not submitted
    the user should see the element    css = div.status-and-action .not-submitted
