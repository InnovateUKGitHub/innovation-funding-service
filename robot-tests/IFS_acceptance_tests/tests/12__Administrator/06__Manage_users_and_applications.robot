*** Settings ***
Documentation     IFS-604: IFS Admin user navigation to Manage users section
...
...               IFS-606: Manage internal users: Read only view of internal user profile
...
...               IFS-27:  Invite new internal user
...
...               IFS-642: Email to new internal user inviting them to register
...
...               IFS-643: Complete internal user registration
...
...               IFS-644: Disable or reenable user profile
...
...               IFS-983: Manage users: Pending registration tab
...
...               IFS-2412: Internal users resend invites
...
...               IFS-2842: Add modals to the resending of invites to internal users
...
...               IFS-1944: Internal - Invite internal user - error field is missing
...
...               IFS-50 Change an existing unsuccessful application into a successful project in setup
...
...               IFS-7160  CSS & Admins cannot amend email addresses if there are pending invites in the service
...
...               IFS-7429 Administrator has access to Project Details after Finance reviewer is assigned
...
...               IFS-7483 Inactive innovation lead appearing in list of available innovation leads
...
...               IFS-7975 KTP Invite new KTA
...
...               IFS-7976 IFS Admin can add a role profile of KTA to an external user
...
...               IFS-7934 KTA Account creation journey
...
...               IFS-7960 KTA Dashboard
...
...               IFS-8095 Content improvement for KTA journey
...
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Administrator  CompAdmin


# NOTE: Please do not use hard coded email in this suite. We always need to check local vs remote for the difference in the domain name !!!

*** Variables ***
${localEmailInvtedUser}                  ifs.innovationLead@innovateuk.ukri.test
${remoteEmailInvtedUser}                 ifs.innovationLead@innovateuk.ukri.org
${invalidEmail}                          test@test.com
${adminChangeEmailOld}                   aaron.powell@example.com
${adminChangeEmailNew}                   aaron.powell2@example.com
${supportChangeEmailOld}                 jacqueline.white@gmail.com
${supportChangeEmailNew}                 jacqueline.white2@gmail.com
${newPendingEmail}                       gintare@tester.com
${emailToChange}                         steve.smith@empire.com
${validKTNDomainEmail}                   jake.Rayan@ktn-uk.test
${KTNDomainEmailAssessor}                alyssa.smith@ktn-uk.test
${nonKTNDomainEmailAssessor}             simon.bates@gmail.com
${inviteExternalUserText}                Invite a new external user
${firstNameInvalidCharacterMessage}      Their first name should have at least 2 characters.
${lastNameInvalidCharacterMessage}       Their last name should have at least 2 characters.
${newFirstNameInvalidCharacterMessage}   Your first name should have at least 2 characters.
${newLastNameInvalidCharacterMessage}    Your last name should have at least 2 characters.
${firstNameValidationMessage}            Please enter a first name.
${lastNameValidationMessage}             Please enter a last name.
${emailAddressValidationMessage}         Please enter an email address.
${invalidKTNDomainValidationMessage}     You must enter a valid Knowledge Transfer Network email address.
${blankKTNDomainValidationMessage}       You must enter a Knowledge Transfer Network email address.
${summaryError}                          Role profile cannot be created without a knowledge transfer network email address.
${KTAEmailInviteText}                    You've been invited to become a knowledge transfer adviser for the Innovation Funding Service
${emailInviteSubject}                    Invitation to Innovation Funding Service

*** Test Cases ***


Administrator is able to access Project details once a finance contact is assigned
    [Documentation]  IFS-7429
    Given finance reviewer is added to the project    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/project/${PS_PD_Project_Id}/details
    When log in as a different user                   &{Comp_admin1_credentials}
    Then the user navigates to the page               ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/project/${PS_PD_Project_Id}/details
    [Teardown]  The user clicks the button/link       link = Dashboard
