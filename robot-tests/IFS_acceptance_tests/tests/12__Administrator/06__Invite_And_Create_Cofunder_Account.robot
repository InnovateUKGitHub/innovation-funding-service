*** Settings ***
Documentation    IFS-8401  Co funder - invite & create account
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
${validSupporterEmail}                   jake.simor@gmail.com
${organisationName}                      Example Organisation
${inviteExternalUserText}                Invite a new external user
${firstNameInvalidCharacterMessage}      Their first name should have at least 2 characters.
${lastNameInvalidCharacterMessage}       Their last name should have at least 2 characters.
${firstNameValidationMessage}            Please enter a first name.
${lastNameValidationMessage}             Please enter a last name.
${emailAddressValidationMessage}         Please enter an email address.
${organisationValidationMessage}         Please enter an organisation.
${summaryError}                          Role profile cannot be created without a knowledge transfer network email address.
${coFunderEmailInviteText}               You've been invited to become a supporter for the Innovation Funding Service.
${emailInviteSubject}                    You have been invited to become a supporter

*** Test Cases ***
Select an external role field validations
    [Documentation]  IFS-8401
    Given the user clicks the button/link                  link = Manage users
    When the user clicks the button/link                   link = Invite a new external user
    And the user clicks the button/link                    jQuery = button:contains("Save and continue")
    Then the user should see a field and summary error     You must select a user role.

Invite a new supporter user field validations
    [Documentation]  IFS-8401
    When the user selects a new external user role                                   SUPPORTER
    And the user clicks the button/link                                              jQuery = button:contains("Send invitation")
    Then the user should see invite a new supporter user field validation message

Administrator can cancel the new supporter user details entered
    [Documentation]  IFS-8401
    Given the user fills invite a new external user fields          Jack  Simor   ${validSupporterEmail}
    And the user enters text to a text field                        id = organisation   ${organisationName}
    When the user clicks the button/link                            link = Cancel
    Then the user should see the element                            link = Invite a new external user

Administrator can sucessfully save the confunder details and return to the manage users page
    [Documentation]  IFS-8401
    Given the user clicks the button/link                     link = Invite a new external user
    And the user selects a new external user role             SUPPORTER
    When the user fills invite a new external user fields     Jack  Simor  ${validSupporterEmail}
    And the user enters text to a text field                  id = organisation   ${organisationName}
    And the user clicks the button/link                       jQuery = button:contains("Send invitation")
    Then the user should see the element                      jQuery = td:contains("Supporter")+td:contains("${validSupporterEmail}")
    [Teardown]  Logout as user

The user accepts the invite for supporter user role
    [Documentation]  IFS-8401
    When the user reads his email and clicks the link      ${validSupporterEmail}  ${emailInviteSubject}  ${coFunderEmailInviteText}
    Then the user should see the element                   jQuery = h1:contains("Create a supporter account")

The supporter creates a new account
    [Documentation]  IFS-8401
    Given the confunder user enters the details to create account     Jake   Simor
    When the user clicks the button/link                              name = create-account
    Then the user should see the element                              jQuery = h1:contains("Your account has been created")

IFS Admin can see the new supporter user in the system
    [Documentation]  IFS-8401
    Given the user clicks the button/link        link = Sign into your account
    And logging in and error checking            &{ifs_admin_user_credentials}
    When the user clicks the button/link         link = Manage users
    And the user enters text to a text field     id = filter  jake.simor
    When the user clicks the button/link         css = input[type="submit"]
    Then the user should see the element         link = ${validSupporterEmail}

IFS Admin cannot add a role profile of supporter to a KT Network user
    [Documentation]  IFS-8401
    Given the user navigates to the page                        ${server}/management/admin/users/active
    When the user selects an existing user to edit details      hermen.mermen@ktn-uk.test  hermen.mermen@ktn-uk.test
    Then the user should not see the element                    link = Add a new external role profile

IFS Admin can add a role profile of supporter to an existing assessor
    [Documentation]  IFS-8401
    Given the user navigates to the page                          ${server}/management/admin/users/active
    When the user selects an existing user to edit details        kinney  alexis.kinney@gmail.com
    And the user adds a new external role profile of supporter     Example Organisation 2
    Then the user should see the element                          jQuery = td:contains("Supporter") ~ td:contains("Active")
    And the user should not see the element                       link = Add a new external role profile

Comp Admin should be able to see the details of assessor with new role profile of supporter
    [Documentation]  IFS-8401
    [Setup]  log in as a different user          &{Comp_admin1_credentials}
    Given the user clicks the button/link        link = Assessor status
    And the user search for an existing user     kinney
    When the user clicks the button/link         link = View details
    Then the user should see the element         jQuery = td:contains("Supporter") ~ td:contains("Active")
    And the user should not see the element      jQuery = button:contains("Save and return")

*** Keywords ***
Custom suite setup
    The user logs-in in new browser     &{ifs_admin_user_credentials}

the user should see invite a new supporter user field validation message
    The user should see a field and summary error     ${firstNameInvalidCharacterMessage}
    The user should see a field and summary error     ${firstNameValidationMessage}
    The user should see a field and summary error     ${lastNameValidationMessage}
    The user should see a field and summary error     ${lastNameInvalidCharacterMessage}
    The user should see a field and summary error     ${organisationValidationMessage}
    The user should see a field and summary error     ${emailAddressValidationMessage}

the user selects an existing user to edit details
    [Arguments]  ${name}  ${email}
    the user search for an existing user     ${name}
    the user clicks the button/link          jQuery = .user-profile:contains("${email}") a:contains("Edit")

the user adds a new external role profile of supporter
    [Arguments]   ${orgName}
    the user clicks the button/link               link = Add a new external role profile
    the user selects a new external user role     SUPPORTER
    the user enters text to a text field          id = organisation   ${orgName}
    the user clicks the button/link               jQuery = button:contains("Confirm role profile")

the confunder user enters the details to create account
    [Arguments]  ${firstName}  ${lastName}
    the user enters text to a text field                   name = firstName  ${firstName}
    the user enters text to a text field                   name = lastName  ${lastName}
    the user enters text to a text field                   name = password  ${short_password}
    the user selects the checkbox                          termsAndConditions