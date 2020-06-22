*** Settings ***
Documentation     IFS-7316 Internal user can invite new external user when project is Live
...
...               IFS-7317
Suite Setup       The user logs-in in new browser  &{ifs_admin_user_credentials}
Suite Teardown
#The user closes the browser
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Applicant_Commons.robot

*** Variables ***
${externalUserInviteLink}                   ${server}/project-setup-management/project/8/grants/invite/send
${newExternalUserFCEmail}                   external_user_fc@example.com
${newExternalUserMOEmail}                   external_user_mo@example.com
${existingUser}                             daniel.tan@example.com
${orgName}                                  University of Bath
${externalUserEmailInviteSubject}           invitation to participate in a project
${externalUserEmailVerificationSubject}   	Please verify your email address
${emailInviteContentPattern}                You have been invited to participate in this Innovation Funding Service project:
${emailVerificationContentPattern}          You have recently set up an account with the Innovation Funding Service.
${validationHeader}                         There is a problem.
${firstNameValidation}                      Please enter their first name.
${firstNameLengthValidation}                Their first name must have at least 2 characters.
${lastNameValidation}                       Please enter their last name.
${lastNameValidationLength}                 Their last name must have at least 2 characters.
${emailAddressValidation}                   Please enter an email address.
${externalUserRoleValidation}               Please select a role.

*** Test Cases ***
IFS Admin gets validation error messages on not completing the fields
    [Documentation]  IFS-7316
    Given the user navigates to the page                   ${externalUserInviteLink}
    When the user clicks the button/link                   jquery = button:contains("Send invitation")
    Then the user should see validation error messages

IFS Admin is able to invite new external user as finance contact to a live project
    [Documentation]  IFS-7316
    Given ifs admin invites a new external user                 FFCname  LFCName  ${newExternalUserFCEmail}  GRANTS_PROJECT_FINANCE_CONTACT
    And the user should see the element                         link = Please select an organisation.
    When the user selects the option from the drop-down menu    ${orgName}  id = organisationId
    And the user clicks the button/link                         jquery = button:contains("Send invitation")
#    Then the user should see the element  This will be completed once 7318 is done

The new external user can accept the email invite as FC
    [Documentation]  IFS-7316
    Given the user reads his email and clicks the link     ${newExternalUserFCEmail}  ${externalUserEmailInviteSubject}  ${emailInviteContentPattern}  1

IFS Admin is able to invite an existing user as a project manager to a live project
    [Documentation]  IFS-7316
    Given ifs admin invites a new external user     Daniel  Tan  ${existingUser}  GRANTS_PROJECT_MANAGER
    When the user clicks the button/link            link = Innovation Funding Service
    [Teardown]  logout as user

The existing user can accept the email invite
    [Documentation]  IFS-7316
    Given the user reads his email and clicks the link     ${existingUser}  ${externalUserEmailInviteSubject}  ${emailInviteContentPattern}  1
    And the user clicks the button/link                    link = Sign in to your account
    When logging in and error checking                      ${existingUser}  ${short_password}
    Then the user can see multiple user dashboard

IFS Admin is able to invite new external user as monitoring officer to a live project
    [Documentation]  IFS-7316
    Given ifs admin invites a new external user     FMOname  LMOName  ${newExternalUserMOEmail}  GRANTS_MONITORING_OFFICER
    When the user clicks the button/link            link = Innovation Funding Service
    [Teardown]  logout as user

The new external user can accept the email invite as MO
    [Documentation]  IFS-7316
    Given the user reads his email and clicks the link        ${newExternalUserMOEmail}  ${externalUserEmailInviteSubject}  ${emailInviteContentPattern}  1
    When the user clicks the button/link                      link = Create an account
    Then the user provides details and creates an account     FMOname  LMOName
    And the user reads his email and clicks the link          ${newExternalUserMOEmail}  ${externalUserEmailVerificationSubject}  ${emailVerificationContentPattern}  1

The new external user signs in
    Given the user clicks the button/link     link = Sign in
    When logging in and error checking        ${newExternalUserMOEmail}  ${short_password}
    Then the user can see multiple user dashboard

*** Keywords ***
the user should see validation error messages
    the user should see the element     jQuery = h2:contains("${validationHeader}")
    the user should see the element     link = ${firstNameValidation}
    the user should see the element     jQuery = span:contains("${firstNameLengthValidation}")
    the user should see the element     link = ${lastNameValidation}
    the user should see the element     jQuery = span:contains("${lastNameValidationLength}")
    the user should see the element     link = ${emailAddressValidation}
    the user should see the element     link = ${externalUserRoleValidation}

ifs admin invites a new external user
    [Arguments]  ${firstName}  ${lastName}  ${email}  ${user_role}
    the user enters text to a text field                   id = firstName  ${firstName}
    the user enters text to a text field                   id = lastName  ${lastName}
    the user enters text to a text field                   id = email  ${email}
    the user selects the value from the drop-down menu     ${user_role}  id = role
    the user clicks the button/link                        jquery = button:contains("Send invitation")

the user can see multiple user dashboard
     the user should see the element     jQuery = h2:contains("Projects")
     the user should see the element     jQuery = h2:contains("Applications")

the user provides details and creates an account
     [Arguments]  ${firstName}  ${lastname}
#    validation error messages check  CURRENTLY GIVING ISE
     The user fills in account details     ${firstName}  ${lastname}
     the user clicks the button/link       jQuery = button:contains("Create account")
     the user should see the element       jQuery = h1:contains("Please verify your email address")

validation error messages check
    the user clicks the button/link     jQuery = button:contains("Create account")


