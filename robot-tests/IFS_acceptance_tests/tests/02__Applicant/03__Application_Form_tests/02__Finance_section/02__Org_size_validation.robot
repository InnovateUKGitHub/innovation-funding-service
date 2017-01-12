*** Settings ***
Documentation     INFUND-1110: As an applicant/partner applicant I want to add my required Funding Level so that innovate uk know my grant request
Suite Setup       log in and create new application if there is not one already
Suite Teardown    the user closes the browser
Force Tags        Applicant    Pending
# TODO peding due to INFUND-6390 this test case will not be this way when 6390 is merged.
Resource          ../../../../resources/defaultResources.robot
Resource          FinanceSection_Commons.robot

*** Variables ***
${no_org_selected_message}    Funding level allowed depends on organisation size. Please select your organisation size.
${incorrect_funding_level_message}    This field should be

*** Test Cases ***
Small org can't have more than 70% funding level
    [Documentation]    INFUND-1110
    [Tags]    HappyPath
    [Setup]  Applicant navigates to the finances of the Robot application
    Given the user clicks the button/link        link=Your organisation
    Then the user selects the radio button       financePosition-organisationSize  financePosition-organisationSize-SMALL
    And the user clicks the button/link          jQuery=button:contains("Mark as complete")
    When Applicant navigates to the finances of the Robot application
    And the user clicks the button/link          link=Your funding
    When the user enters text to a text field    css=#cost-financegrantclaim  80
    And the user moves focus to the element      jQuery=label[data-target="other-funding-table"]
    Then the user should see the element         jQuery=span.error-message:contains("This field should be 70% or lower.")

Small org can be up to 70%
    [Documentation]    INFUND-1110
    [Tags]    HappyPath
    When The applicant enters Org Size and Funding level  SMALL  70
    Then the user should not see the element  jQuery=.error-message

Medium organisation can't be more than 60%
    [Documentation]    INFUND-1110
    [Tags]
    When The applicant enters Org Size and Funding level  MEDIUM  68
    Then the user should see the element  jQuery=span.error-message:contains("This field should be 60% or lower.")

Medium organisation can be up to 60%
    [Documentation]    INFUND-1110
    [Tags]
    When The applicant enters Org Size and Funding level  MEDIUM  53
    Then the user should not see the element  jQuery=.error-message

Large organisation can't be more than 50%
    [Documentation]    INFUND-1110
    [Tags]
    When The applicant enters Org Size and Funding level  LARGE  62
    Then the user should see the element  jQuery=span.error-message:contains("This field should be 50% or lower.")

Large organisation can be up to 50%
    [Documentation]    INFUND-1110
    [Tags]
    When The applicant enters Org Size and Funding level  LARGE  43
    Then the user should not see the element  jQuery=.error-message