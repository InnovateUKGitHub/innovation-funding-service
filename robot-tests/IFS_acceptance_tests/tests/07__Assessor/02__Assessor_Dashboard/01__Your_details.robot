*** Settings ***
Documentation     INFUND-1480 As an assessor I want to be able to update/edit my profile information so that it is up to date.
Suite Setup       Custom Suite Setup
Suite Teardown    The user closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Cancel button returns to read only view
    [Documentation]    INFUND-8009
    Given the user clicks the button/link                   jQuery = a:contains("Cancel")
    Then the user should be redirected to the correct page  ${assessment_details_url}

Back button returns to assessor dashboard
    [Documentation]    INFUND-8009
    Given the user clicks the button/link                   link = ${ASSESSOR_DASHBOARD_TITLE}
    Then the user should be redirected to the correct page  ${ASSESSOR_DASHBOARD_URL}
    [Teardown]    user opens the edit details form

Back button from edit returns to read only view
    [Documentation]    INFUND-8009
    Given the user clicks the button/link  link=Your details
    Then the user should be redirected to the correct page  ${assessment_details_url}
    [Teardown]    the user clicks the button/link           jQuery = a:contains("Edit")

Validations for invalid inputs
    [Documentation]    INFUND-1480
    Given the user should see the element        jQuery = h1:contains("Edit your details")
    And the user should see the element          jQuery = h3:contains("Email") ~ p:contains("bob.malone@gmail.com")
    When the user enters the invalid details
    Then the user should see the validation messages

Valid Profile Update
    [Documentation]    INFUND-1480
    When the assessor updates profile details
    And the user clicks the button/link    jQuery = a:contains("your details")
    Then the saved changes are visible

*** Keywords ***
Custom Suite Setup
   the assessor logs-in
   ${status}   ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery = h1:contains("Dashboard")
   Run Keyword If   '${status}' == 'PASS'  Run keyword   the user clicks the button/link  id = dashboard-link-ASSESSOR
   User opens the edit details form

the assessor updates profile details
    The user enters text to a text field  id = firstName    Joy
    The user enters text to a text field  id = lastName    Archer
    Set Focus To Element                  id = firstName
    the user enters text to a text field  id = addressLine1    7, Phoenix house
    the user enters text to a text field  id = town    Reading
    the user enters text to a text field  id = postcode    RG1 7UH
    the user enters text to a text field  id = phoneNumber    18549731414
    the user clicks the button/link       jQuery = button:contains("Save and return to your details")

the saved changes are visible
    the user should see the element   jQuery = dd:contains("Joy")
    the user should see the element   jQuery = dd:contains("Archer")
    the user should see the element   jQuery = dd:contains("18549731414")

User opens the edit details form
    Given the user clicks the button/link  jQuery = a:contains("your details")
    And the user clicks the button/link    jQuery = a:contains("Edit")

the assessor logs-in
   The guest user opens the browser
   The guest user inserts user email and password   &{assessor_bob_credentials}
   the user clicks the button/link                  jQuery = button:contains("Sign in")

the user enters the invalid details
    the user enters text to a text field     id = firstName    Joy12
    the user enters text to a text field     id = lastName    Archer12
    the user enters text to a text field     id = phoneNumber    18549731414test
    the user enters text to a text field     id = addressLine1    ${EMPTY}
    the user enters text to a text field     id = town    ${EMPTY}
    the user enters text to a text field     id = postcode    ${EMPTY}
    the user clicks the button/link          jQuery = button:contains("Save and return to your details")

the user should see the validation messages
    the user should see a field and summary error     Invalid first name.
    the user should see a field and summary error     Invalid last name.
    the user should see a field and summary error     ${enter_a_phone_number_between_8_and_20_digits}
    the user should see a field and summary error     The first line of the address cannot be blank.
    the user should see a field and summary error     The postcode cannot be blank.
    the user should see a field and summary error     The town cannot be blank.