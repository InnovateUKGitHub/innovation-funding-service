*** Settings ***
Documentation     INFUND-1110: As an applicant/partner applicant I want to add my required Funding Level so that innovate uk know my grant request
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    User closes the browser
Force Tags
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Variables ***
${small_org_option}    SMALL
${medium_org_option}    MEDIUM
${large_org_option}    LARGE

*** Test Cases ***
Small organisation can't choose over 70% funding
    [Documentation]    INFUND-1100
    [Tags]    Organisation    Funding    Finance
    When the applicant enters organisation size details    ${small_org_option}    82
    Then the 'your finances' section cannot be successfully saved

Small organisation can choose up to 70% funding
    [Documentation]    INFUND-1100
    [Tags]    Organisation    Funding    Finance    HappyPath
    When the applicant enters organisation size details    ${small_org_option}    68
    Then the 'your finances' section can be successfully saved    ${small_org_option}    68

Medium organisation can't choose over 60% funding
    [Documentation]    INFUND-1100
    [Tags]    Organisation    Funding    Finance
    When the applicant enters organisation size details    ${medium_org_option}    68
    Then the 'your finances' section cannot be successfully saved

Medium organisation can choose up to 60% funding
    [Documentation]    INFUND-1100
    [Tags]    Organisation    Funding    Finance
    When the applicant enters organisation size details    ${medium_org_option}    53
    Then the 'your finances' section can be successfully saved    ${medium_org_option}    53

Large organisation can't choose over 50% funding
    [Documentation]    INFUND-1100
    [Tags]    Organisation    Funding    Finance
    When the applicant enters organisation size details    ${large_org_option}    54
    Then the 'your finances' section cannot be successfully saved

Large organisation can choose up to 50% funding
    [Documentation]    INFUND-1100
    [Tags]    Organisation    Funding    Finance
    When the applicant enters organisation size details    ${large_org_option}    43
    Then the 'your finances' section can be successfully saved    ${large_org_option}    43

*** Keywords ***
The applicant enters organisation size details
    [Arguments]    ${org_size_option}    ${funding_level}
    The user navigates to the page    ${YOUR_FINANCES_URL}
    Applicant enters the organisation size    ${org_size_option}
    Applicant enters the funding level    ${funding_level}
    Applicant chooses to save and return to application overview

The 'your finances' section can be successfully saved
    [Arguments]    ${org_size_option}    ${funding_level}
    The user navigates to the page    ${YOUR_FINANCES_URL}
    Applicant can see the correct organisation size has been selected    ${org_size_option}
    Applicant can see the correct funding level has been saved    ${funding_level}

The 'your finances' section cannot be successfully saved
    the user is on the page    ${your_finances_url}
    the user should see the text in the page    This field should be

Applicant enters the organisation size
    [Arguments]    ${org_size_option}
    Select Radio Button    financePosition-organisationSize    ${org_size_option}

Applicant enters the funding level
    [Arguments]    ${funding_level}
    Input Text    id=cost-financegrantclaim    ${funding_level}

Applicant chooses to save and return to application overview
    Click Button    Save and return to application overview
    # the user clicks the button/link    link=Save and return to application overview
    # the user should be redirected to the correct page    ${application_overview_url}

Applicant can see the correct organisation size has been selected
    [Arguments]    ${org_size_option}
    Radio Button Should Be Set To    financePosition-organisationSize    ${org_size_option}

Applicant can see the correct funding level has been saved
    [Arguments]    ${funding_level}
    Wait Until Element Is Visible    id=cost-financegrantclaim
    ${saved_funding_level} =    Get Element Attribute    id=cost-financegrantclaim@value
    Should Be Equal As Integers    ${saved_funding_level}    ${funding_level}
