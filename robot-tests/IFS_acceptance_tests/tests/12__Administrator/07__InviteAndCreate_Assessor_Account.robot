*** Settings ***
Documentation    IFS-11788  Assessor pool: Create account journey for assessor
...
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Administrator  CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/Assessor_Commons.robot

*** Variables ***
${firstNameInvalidCharacterMessage}     Their first name should have at least 2 characters.
${lastNameInvalidCharacterMessage}      Their last name should have at least 2 characters.
${firstNameValidationMessage}           Please enter a first name.
${lastNameValidationMessage}            Please enter a last name.
${emailAddressValidationMessage}        Please enter an email address.
${innovationAreaValidationMessage}      Please enter an innovation sector and area.
${validAssessorEmail}                   alome.lolome@gmail.com
${assessorInviteEmailSubject}           You have been invited to become a assessor
${AssessorEmailInviteDescription}       You've been invited to become a assessor for the Innovation Funding Service.


*** Test Cases ***
Invite a new supporter user field validations
    [Documentation]  IFS-11787
    Given the user clicks the button/link                                       link = Manage users
    And the user clicks the button/link                                         link = Invite a new external user
    When the user selects a new external user role                              ASSESSOR
    And the user clicks the button/link                                         jQuery = button:contains("Send invitation")
    Then the user should see invite an assessor user field validation message

Admin sents an invite for assessor
    [Documentation]  IFS-11788
    When the user fills invite a new external user fields     alome  lolome  ${validAssessorEmail}
    And the user selects innovation area and sector
    And the user clicks the button/link                       jQuery = button:contains("Send invitation")
    Then the user should see the element                      jQuery = td:contains("Assessor")+td:contains("${validAssessorEmail}")
    [Teardown]  Logout as user

The Assesssor creates a new account
    [Documentation]  IFS-11788
    Given the user reads his email and clicks the link         ${validAssessorEmail}  ${assessorInviteEmailSubject}  ${AssessorEmailInviteDescription}
    And assessor enters the details to create an account       alome  lolome
    When the user clicks the button/link                       name = create-account
    Then the user should see the element                       jQuery = h1:contains("Your account has been created")
    And the user should see the element                        jQuery = li:contains("provide information about your skill areas")
    And the user should see the element                        jQuery = li:contains("declare any interests (yours or family members)")
    And the user should see the element                        jQuery = li:contains("sign your contract")

Assessor can login and sign the contract
    [Documentation]  IFS-11788
    Given the user clicks the button/link   link = Sign into your account
    And logging in and error checking       ${validAssessorEmail}   ${short_password}
    When the user clicks the button/link    link = your assessor agreement
    And the user clicks the button/link     jQuery = button:contains("Save and return to assessments")
    And the user clicks the button/link     link = your assessor agreement
    Then the user should see the element    jQuery = p:contains("You signed the assessor agreement")

Admin can see the new assessor in the system
    [Documentation]  IFS-11788
    Given log in as a different user             &{ifs_admin_user_credentials}
    When the user clicks the button/link         link = Manage users
    And the user enters text to a text field     id = filter  alome.lolome
    When the user clicks the button/link         css = input[type="submit"]
    Then the user should see the element         link = ${validAssessorEmail}

Admin can view the new assessor in invite assessors page of an existing competiton
    [Documentation]  IFS-11788
    Given the user navigates to the page        ${server}/management/dashboard/live
    When the user clicks the button/link        link = ${CLOSED_COMPETITION_NAME2}
    And the user clicks the button/link         link = Invite assessors to assess the competition
    And the user enters text to a text field    id = assessorNameFilter  alome
    And the user clicks the button/link         id = assessor-filter-button
    Then the user should see the element        jQuery = td:contains("alome.lolome") +td+ td:contains("Biosciences")

Competiton admin can view new assessor status
    [Documentation]  IFS-11788
    Given log in as a different user             &{Comp_admin1_credentials}
    When the user clicks the button/link         link = Assessor status
    And the user enters text to a text field     id = filter  alome.lolome@gmail.com
    When the user clicks the button/link         css = input[type="submit"]
    Then the user should see the element         link = ${validAssessorEmail}


*** Keywords ***
Custom suite setup
    The user logs-in in new browser     &{ifs_admin_user_credentials}

assessor enters the details to create an account
    [Arguments]  ${firstName}  ${lastName}
    the user enters text to a text field                   name = firstName  ${firstName}
    the user enters text to a text field                   name = lastName  ${lastName}
    the user enters text to a text field                   id = addressForm.postcodeInput  BE1 4HT
    the user clicks the button/link                        id = postcode-lookup
    the user selects the index from the drop-down menu     1  id=addressForm.selectedPostcodeIndex
    the user enters text to a text field                   name = phoneNumber  98765631464
    the user enters text to a text field                   name = password   ${short_password}

the user selects innovation area and sector
    the user selects the option from the drop-down menu   Health and life sciences  id = selectedInnovationArea
    the user selects the option from the drop-down menu   Biosciences  id = grouped-innovation-area

the user should see invite an assessor user field validation message
    The user should see a field and summary error     ${firstNameInvalidCharacterMessage}
    The user should see a field and summary error     ${firstNameValidationMessage}
    The user should see a field and summary error     ${lastNameValidationMessage}
    The user should see a field and summary error     ${lastNameInvalidCharacterMessage}
    The user should see a field and summary error     ${innovationAreaValidationMessage}
    The user should see a field and summary error     ${emailAddressValidationMessage}