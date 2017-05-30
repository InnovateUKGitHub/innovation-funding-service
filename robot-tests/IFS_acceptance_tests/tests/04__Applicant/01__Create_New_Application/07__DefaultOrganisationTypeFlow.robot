*** Settings ***
Documentation     INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile so...
...
...               INFUND-886:As an applicant I want the system to recognise an existing user profile if I try to create a new account with matching details so...
...
...               INFUND-6387 As an Applicant creating an account I will be invited to answer questions for diversity monitoring purposes so...
...
...               INFUND-885: As an applicant I want to be able to submit a username (email address) and password combination to create a new profile...
...
...               INFUND-1147: Further acceptance tests for the create account page
...
...               INFUND-2497: As a new user I would like to have an indication that my password is correct straight after typing...
Suite Setup       the user opens the browser
Test Setup        the user navigates to the competition overview
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot


*** Test Cases ***
the user follows default Business organisation type flow
    Given the user fills out the organisation form for a competition with default Business organisation type
    When the user submits the organisation details form
    Then the user should skip the choice page
    And the user should see Business automatically selected in the confirm organisation page

the user follows default RTO organisation type flow
    Given the user fills out the organisation form for a competition with default RTO organisation type
    When the user submits the organisation details form
    Then the user should skip the choice page
    And the user should see RTO automatically selected in the confirm organisation page

*** Keywords ***
the user navigates to the competition overview
    the user navigates to the page    ${frontDoor}

the user should see RTO automatically selected in the confirm organisation page
    the user should see the text in the element  jQuery=fieldset p:first  Research and technology organisations (RTOs)

the user should see Business automatically selected in the confirm organisation page
    the user should see the text in the element  jQuery=fieldset p:first  Business

the user should skip the choice page
    the user should see the text in the page  Confirm your organisation details are correct

the user fills out the organisation form for a competition with default RTO organisation type
    the user opens a competition and fills out the organisation form  Predicting market trends programme

the user fills out the organisation form for a competition with default Business organisation type
    the user opens a competition and fills out the organisation form  Aerospace technology investment sector

the user submits the organisation details form
    the user clicks the button/link    jQuery=button:contains("Continue")

the user opens a competition and fills out the organisation form
    [Arguments]    ${competition_name}
    the user clicks the button/link    link=${competition_name}
    the user clicks the button/link    link=Start new application
    the user clicks the button/link    jQuery=.button:contains("Create account")
    the user enters text to a text field    id=organisationSearchName    Hive IT
    the user clicks the button/link    id=org-search
    the user clicks the button/link    Link=HIVE IT LIMITED
    the user selects the checkbox    address-same