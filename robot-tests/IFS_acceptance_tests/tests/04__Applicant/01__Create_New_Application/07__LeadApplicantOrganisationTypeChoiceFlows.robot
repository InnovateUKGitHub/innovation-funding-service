*** Settings ***
Documentation       INFUND-669 As an applicant I want to create a new application so...
...                 INFUND-1904 As a user registering an account and submitting the data I expect to receive a verification email so...
...                 INFUND-1920 As an applicant once I am accessing my dashboard and clicking on the newly created application for the first time, it will allow me to invite contributors and partners
...                 IFS-47 As an applicant creating an account I am able to select Business or RTO where both have been set in Competition setup
Suite Setup         the guest user opens the browser
Test Setup          the user navigates to the competition overview
Suite Teardown      the user closes the browser
Force Tags          Applicant
Resource            ../../../resources/defaultResources.robot


*** Test Cases ***
User can choose RTO organisation when both organisation types are allowed
    [Documentation]    INFUND-669 INFUND-1904 INFUND-1785
    [Tags]    HappyPath    SmokeTest
    Given the user starts a competition create account journey with choice for organisation types
    When the user chooses the RTO organisation type
    Then the user should see the text in the element  css=#rto-guidance h2  You can only lead an application as an RTO if both the following rules are met:
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    And the user should see the text in the element  css=h1  Your organisation

#User organisation type is selected by default when Business is the only allowed type
#    [Documentation]    IFS-47
#    Given the user fills out the organisation form for a competition with default Business organisation type
#    When the user submits the organisation details form
#    Then the user should skip the choice page
#    And the user should see Business automatically selected in the confirm organisation page

#User organisation type is selected by default when RTO is the only allowed type
#    [Documentation]    IFS-47
#    Given the user fills out the organisation form for a competition with default RTO organisation type
#    When the user submits the organisation details form
#    Then the user should skip the choice page
#    And the user should see RTO automatically selected in the confirm organisation page

*** Keywords ***
the user navigates to the competition overview
    the user navigates to the page    ${frontDoor}

the user should see RTO automatically selected in the confirm organisation page
    the user should see the text in the element  jQuery=fieldset p:first  Research and technology organisations (RTOs)

the user should see Business automatically selected in the confirm organisation page
    the user should see the text in the element  jQuery=fieldset p:first  Business

the user should skip the choice page
    the user should see the text in the element  css=h2  Confirm your organisation details are correct

the user starts a competition create account journey for default RTO organisation type
    the user starts a competition create account journey  Predicting market trends programme

the user starts a competition create account journey for default Business organisation type
    the user starts a competition create account journey  Aerospace technology investment sector

the user starts a competition create account journey with choice for organisation types
    the user starts a competition create account journey  Home and industrial efficiency programme

the user submits the organisation details form
    the user clicks the button/link    jQuery=button:contains("Continue")

the user chooses the Business organisation type
    the user selects the radio button    organisationTypeId    radio-1
    the user clicks the button/link    jQuery=button:contains("Save and continue")

the user chooses the RTO organisation type
    the user selects the radio button    organisationTypeId    radio-3

the user confirms the organisation details
    the user clicks the button/link    jQuery=a:contains("Save and continue")

the user starts a competition create account journey
    [Arguments]    ${competition_name}
    the user clicks the button/link    link=${competition_name}
    the user clicks the button/link    link=Start new application
    the user clicks the button/link    jQuery=.button:contains("Create account")

the user fills out the organisation form
    the user enters text to a text field    id=organisationSearchName    Hive IT
    the user clicks the button/link    id=org-search
    the user clicks the button/link    Link=HIVE IT LIMITED
    the user selects the checkbox    address-same