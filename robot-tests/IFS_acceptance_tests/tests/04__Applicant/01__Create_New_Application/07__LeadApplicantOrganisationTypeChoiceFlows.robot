*** Settings ***
Documentation       INFUND-669 As an applicant I want to create a new application so...
...                 INFUND-1904 As a user registering an account and submitting the data I expect to receive a verification email so...
...                 INFUND-1920 As an applicant once I am accessing my dashboard and clicking on the newly created application for the first time, it will allow me to invite contributors and partners
...                 IFS-47 As an applicant creating an account I am able to select Business or RTO where both have been set in Competition setup
Suite Setup         the user starts a competition create account journey for both RTO and Business organisation types
Suite Teardown      the user closes the browser
Force Tags          Applicant
Resource            ../../../resources/defaultResources.robot


*** Test Cases ***
User should see RTO guidance when choosing RTO as a business type
    [Documentation]    INFUND-669 INFUND-1904 INFUND-1785
    [Tags]    HappyPath    SmokeTest
    When the user chooses an organisation type        ${RTO_TYPE_ID}
    Then the user should see the text in the element  css=#rto-guidance h2  You can only lead an application as an RTO if both the following rules are met:

User can choose RTO when both RTO and Business are eligible
    [Documentation]    IFS-1014
    [Tags]    HappyPath    SmokeTest
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see the element    jQuery=h1:contains("Enter organisation details")

User can choose Business when both RTO and Business are eligible
    [Documentation]    IFS-1014
    [Tags]    HappyPath    SmokeTest
    Given the user clicks the button/link         jQuery=a:contains("Back to choose your organisation type")
    When the user chooses an organisation type    ${BUSINESS_TYPE_ID}
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see the element   jQuery=h1:contains("Enter organisation details")

User cannot choose Research when both Research and Public sector types are ineligible
    [Documentation]    IFS-1014
    [Tags]    HappyPath    SmokeTest
    Given the user clicks the button/link    jQuery=a:contains("Back to choose your organisation type")
    When the user chooses an organisation type    ${ACADEMIC_TYPE_ID}
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    And the user should see the text in the element  css=#main-content p    Your organisation type does not match our eligibility criteria for lead applicants.
    [Teardown]    go back

User cannot choose Public Sector when both Research and Public sector types are ineligible
    [Documentation]    IFS-1014
    [Tags]    HappyPath    SmokeTest
    When the user chooses an organisation type    ${PUBLIC_SECTOR_TYPE_ID}
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    And the user should see the text in the element  css=#main-content p    Your organisation type does not match our eligibility criteria for lead applicants.

*** Keywords ***

the user chooses an organisation type
    [Arguments]    ${org_type_id}
    the user selects the radio button    organisationTypeId  ${org_type_id}

the user starts a competition create account journey for both RTO and Business organisation types
    the guest user opens the browser
    the user navigates to the page    ${frontDoor}
    the user starts a competition create account journey  ${createApplicationOpenCompetition}

the user starts a competition create account journey
    [Arguments]    ${competition_name}
    the user clicks the button/link in the paginated list    link=${competition_name}
    the user clicks the button/link    link=Start new application
    the user clicks the button/link    link=Continue without an account