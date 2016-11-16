*** Settings ***
Documentation     INFUND-1110: As an applicant/partner applicant I want to add my required Funding Level so that innovate uk know my grant request
Suite Setup       log in and create new application if there is not one already
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot

*** Variables ***
${no_org_selected_message}    Funding level allowed depends on organisation size. Please select your organisation size.
${incorrect_funding_level_message}    This field should be

*** Test Cases ***
Org size must be selected
    [Documentation]    INFUND-2643
    [Tags]    HappyPath
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Your finances
    When the applicant enters the funding level    50
    And The user clicks the button/link    jQuery=button:contains("Save and return to application overview")
    Then the 'your finances' section cannot be successfully saved with the message    ${no_org_selected_message}

Small org can't be more than 70%
    [Documentation]    INFUND-1100
    [Tags]    HappyPath
    When the applicant enters organisation size details    SMALL    82
    Then the 'your finances' section cannot be successfully saved with the message    ${incorrect_funding_level_message}

Small org can be up to 70%
    [Documentation]    INFUND-1100
    [Tags]    HappyPath
    When the applicant enters organisation size details    SMALL    68
    Then the 'your finances' section can be successfully saved    SMALL    68

Medium organisation can't be more than 60%
    [Documentation]    INFUND-1100
    [Tags]
    When the applicant enters organisation size details    MEDIUM    68
    Then the 'your finances' section cannot be successfully saved with the message    ${incorrect_funding_level_message}

Medium organisation can be up to 60%
    [Documentation]    INFUND-1100
    [Tags]
    When the applicant enters organisation size details    MEDIUM    53
    Then the 'your finances' section can be successfully saved    MEDIUM    53

Large organisation can't be more than 50%
    [Documentation]    INFUND-1100
    [Tags]
    When the applicant enters organisation size details    LARGE    54
    Then the 'your finances' section cannot be successfully saved with the message    ${incorrect_funding_level_message}

Large organisation can be up to 50%
    [Documentation]    INFUND-1100
    [Tags]
    When the applicant enters organisation size details    LARGE    43
    Then the 'your finances' section can be successfully saved    LARGE    43

*** Keywords ***
The applicant enters organisation size details
    [Arguments]    ${org_size_option}    ${funding_level}
    the applicant enters the organisation size    ${org_size_option}
    the applicant enters the funding level    ${funding_level}
    the user moves focus to the element    jQuery=button:contains("Save and return to application overview")
    The user clicks the button/link    jQuery=button:contains("Save and return to application overview")

The 'your finances' section can be successfully saved
    [Arguments]    ${org_size_option}    ${funding_level}
    the user moves focus to the element    link=Your finances
    The user clicks the button/link    link=Your finances
    the applicant can see the correct organisation size has been selected    ${org_size_option}
    the applicant can see the correct funding level has been saved    ${funding_level}

The 'your finances' section cannot be successfully saved with the message
    [Arguments]    ${warning_message}
    the user should see the text in the page    Organisation Size
    the user should see the text in the page    ${warning_message}

The applicant enters the organisation size
    [Arguments]    ${org_size_option}
    the user selects the radio button    financePosition-organisationSize    ${org_size_option}

The applicant enters the funding level
    [Arguments]    ${funding_level}
    the user enters text to a text field    id=cost-financegrantclaim    ${funding_level}

The applicant can see the correct organisation size has been selected
    [Arguments]    ${org_size_option}
    the user sees that the radio button is selected    financePosition-organisationSize    ${org_size_option}

The applicant can see the correct funding level has been saved
    [Arguments]    ${funding_level}
    the user should see the element    id=cost-financegrantclaim
    ${saved_funding_level} =    Get Element Attribute    id=cost-financegrantclaim@value
    Should Be Equal As Integers    ${saved_funding_level}    ${funding_level}
