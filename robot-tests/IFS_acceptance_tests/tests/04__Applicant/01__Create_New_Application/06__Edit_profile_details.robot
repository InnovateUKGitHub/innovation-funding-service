*** Settings ***
Documentation     INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
...
...               INFUND-6387 As an Applicant creating an account I will be invited to answer questions for diversity monitoring purposes so that InnovateUK complies with BEIS ministerial requirement
...
...               INFUND-9245 Add marketing email option tick box to the 'Your profile' > 'Your details' page
...
...               IFS-951  Display 'Organisation type' against user
...
...               IFS-41 Add read only view of marketing email option selected in 'Your profile' > 'Your details' page
Suite Setup       the user logs-in in new browser  &{lead_applicant_credentials}
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
View and edit profile link is visible in the Dashboard page
    [Documentation]    INFUND-1042
    [Tags]
    When the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    Then the user should see the element  link = Profile

View and edit profile link redirects to the Your profile page
    [Documentation]    INFUND-1042  IFS-951
    [Tags]
    When the user clicks the button/link  link = Profile
    And the user should see the element   jQuery = .govuk-table td:contains("Business")
    Then the user should see the element  link = Edit your details

Edit the profile and verify if the changes are saved
    [Documentation]    INFUND-1042, INFUND-6387, INFUND-9245, IFS-41
    [Tags]  HappyPath
    Given the user navigates to the page                   ${APPLICANT_DASHBOARD_URL}
    When the user clicks the button/link                   link = Profile
    And the user clicks the button/link                    link = Edit your details
    And the user enters profile details
    Then the user should see the element                   jQuery = p:contains("Chris Brown")
    And the user should see the element                    jQuery = p:contains("0123456789")
    When the user clicks the button/link                   link = Edit your details
    And Checkbox Should Be Selected                        allowMarketingEmails
    When the user can change their details back again
    Then the user should see the element  jQuery = h3:contains("Email preferences") + p:contains("You have asked for updates on Innovate UK competitions by email.")

Verify that the applicant's name has been changed on other parts of the site
    [Documentation]    INFUND-1042
    [Tags]  HappyPath
    Given the user navigates to the page           ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link            link = Profile
    And the user clicks the button/link            link = Edit your details
    When the user enters profile details
    And The user clicks the button/link            link = Dashboard
    And The user clicks the button/link            link = ${OPEN_COMPETITION_APPLICATION_NAME}
    And The user clicks the button/link            link = Application team
    Then the user should see the element           jQuery = td:contains("Chris Brown")
    And the user navigates to the page             ${EDIT_PROFILE_URL}
    And the user can change their details back again

Display errors for invalid inputs of the First name
    [Documentation]    INFUND-1042
    [Tags]
    Given the user navigates to the page                  ${EDIT_PROFILE_URL}
    And browser validations have been disabled
    When the user fills in the first name                 ${EMPTY}
    Then the user should see a field error                ${enter_a_first_name}
    And browser validations have been disabled
    And browser validations have been disabled
    And the user fills in the first name                  testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes
    And the user clicks the button/link                   css = [name="create-account"]
    And the user should see a field and summary error     Your first name cannot have more than 70 characters.
    And browser validations have been disabled
    And the user fills in the first name                  A
    And the user should see a field error                 Your first name should have at least 2 characters.

Display errors for invalid inputs of the Last name
    [Documentation]    INFUND-1042
    [Tags]
    Given the user navigates to the page                  ${EDIT_PROFILE_URL}
    And browser validations have been disabled
    When the user fills in the last name                  ${EMPTY}
    Then the user should see a field error                ${enter_a_last_name}
    And browser validations have been disabled
    And browser validations have been disabled
    And the user fills in the last name                   testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttes
    And the user clicks the button/link                   css = [name="create-account"]
    And the user should see a field and summary error     Your last name cannot have more than 70 characters.
    And browser validations have been disabled
    And the user fills in the last name                   B
    And the user should see a field error                 Your last name should have at least 2 characters.

Display errors for invalid inputs of the Phone field
    [Documentation]    INFUND-1042 : As an applicant I want to be able to edit my user profile details so I can be identified to other users in the system
    [Tags]
    Given the user navigates to the page                   ${EDIT_PROFILE_URL}
    And browser validations have been disabled
    When the user fills in the Phone field                 ${EMPTY}
    Then the user should see a field error                 ${enter_a_phone_number}
    And browser validations have been disabled
    And the user fills in the Phone field                  121212121212121212121
    And the user clicks the button/link                    css = [name="create-account"]
    And the user should see a field and summary error      ${enter_a_phone_number_between_8_and_20_digits}
    And browser validations have been disabled
    And the user fills in the Phone field                  12
    And the user should see a field error                  ${enter_a_phone_number_between_8_and_20_digits}

*** Keywords ***
the user enters profile details
    The user enters text to a text field  id = firstName    Chris
    The user enters text to a text field  id = lastName    Brown
    The user enters text to a text field  id = phoneNumber    +-0123456789
    the user selects the checkbox         allowMarketingEmails
    the user clicks the button/link       css = [name="create-account"]

the user fills in the first name
    [Arguments]    ${first name}
    The user enters text to a text field  id = firstName    ${first_name}
    The user enters text to a text field  id = lastName    Brown
    The user enters text to a text field  id = phoneNumber    0123456789
    the user clicks the button/link       css = [name="create-account"]

the user fills in the last name
    [Arguments]    ${Last_name}
    The user enters text to a text field  id = firstName    Chris
    The user enters text to a text field  id = lastName    ${Last_name}
    The user enters text to a text field  id = phoneNumber    0123456789
    the user clicks the button/link       css = [name="create-account"]

the user fills in the phone field
    [Arguments]    ${phone_field}
    The user enters text to a text field  id = firstName    Chris
    The user enters text to a text field  id = lastName    Brown
    The user enters text to a text field  id = phoneNumber    ${phone_field}
    the user clicks the button/link       css = [name="create-account"]

the user can change their details back again
    The user enters text to a text field  id = firstName    Steve
    The user enters text to a text field  id = lastName    Smith
    The user enters text to a text field  id = phoneNumber    +-0123456789
    the user clicks the button/link       css = [name="create-account"]

other contributors should see the applicant's updated name for the assignation options
    Log in as a different user                &{collaborator1_credentials}
    go to    ${APPLICATION_OVERVIEW_URL}
    The user should see the element           jQuery = li:nth-child(2) span:contains("Chris Brown")
    log in as a different user                &{lead_applicant_credentials}
