*** Settings ***
Documentation     IFS-7316 Internal user can invite new external user when project is Live
...
...               IFS-7317
Suite Setup       The user logs-in in new browser  &{ifs_admin_user_credentials}
Suite Teardown    The user closes the browser
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Applicant_Commons.robot

*** Variables ***
${externalUserInviteLink}            ${server}/project-setup-management/project/8/grants/invite/send
${newExternalUserEmail}              external_user@example.com
${orgName}                           University of Bath
${externalUserEmailInviteSubject}    invitation to participate in a project
${emailInviteContentPattern}         You have been invited to participate in this Innovation Funding Service project:
${validationHeader}                  There is a problem.
${firstNameValidation}               Please enter their first name.
${firstNameLengthValidation}         Their first name must have at least 2 characters.
${lastNameValidation}                Please enter their last name.
${lastNameValidationLength}          Their last name must have at least 2 characters.
${emailAddressValidation}            Please enter an email address.
${externalUserRoleValidation}        Please select a role.

*** Test Cases ***
IFS Admin gets validation error messages on not completing the fields
    [Documentation]  IFS-7316
    Given the user navigates to the page                ${externalUserInviteLink}
    When the user clicks the button/link                jquery = button:contains("Send invitation")
    Then the user should see validation error messages

IFS Admin is able to invite new external user to a live project
    [Documentation]  IFS-7316
    Given ifs admin invites a new external user                 ${newExternalUserEmail}  GRANTS_PROJECT_FINANCE_CONTACT
    And the user should see the element                         link = Please select an organisation.
    When the user selects the option from the drop-down menu    ${orgName}  id = organisationId
    And the user clicks the button/link                         jquery = button:contains("Send invitation")
#    Then the user should see the element  This will be completed once 7318 is done

The new external user can accept the email invite
    [Documentation]  IFS-7316
    Given the user reads his email and clicks the link     ${newExternalUserEmail}  ${externalUserEmailInviteSubject}  ${emailInviteContentPattern}  1

*** Keywords ***
ifs admin invites a new external user
    [Arguments]  ${email}  ${user_role}
    the user enters text to a text field                   id = firstName  FName
    the user enters text to a text field                   id = lastName  LName
    the user enters text to a text field                   id = email  ${email}
    the user selects the value from the drop-down menu     ${user_role}  id = role
    the user clicks the button/link                        jquery = button:contains("Send invitation")

the user should see validation error messages
    the user should see the element     jQuery = h2:contains("${validationHeader}")
    the user should see the element     link = ${firstNameValidation}
    the user should see the element     jQuery = span:contains("${firstNameLengthValidation}")
    the user should see the element     link = ${lastNameValidation}
    the user should see the element     jQuery = span:contains("${lastNameValidationLength}")
    the user should see the element     link = ${emailAddressValidation}
    the user should see the element     link = ${externalUserRoleValidation}

