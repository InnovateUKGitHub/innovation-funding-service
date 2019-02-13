*** Settings ***
Documentation   IFS-4189 Add/Remove Stakeholders
...
...             IFS-4190 Create a new user in stakeholder role
...
...             IFS-4314 Stakeholder invite email
...
...             IFS-4252 Stakeholder registration
Force Tags      HappyPath
Resource        ../../resources/defaultResources.robot
Resource        ../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${openProgrammeCompetitionName}  Photonics for All
${openProgrammeCompetitionId}    ${competition_ids['${openProgrammeCompetitionName}']}
${stakeholderEmail}              stakeHolder@test.com
${applicantEmail}                louis.morgan@example.com
${previousStakeholderEmail}      blake.wood@gmail.com

*** Test Cases ***
The internal user cannot invite a Stakeholder when they have triggered the name validation
    [Documentation]  IFS-4190
    [Tags]
    Given Comp admin navigate to stakeholders page
    When the user clicks the button/link             jQuery = span:contains("Invite a new stakeholder")
    And the user triggers the name validation
    Then the user should see the name validation messages

The internal user cannot invite a Stakeholder when they have triggered the email validation
    [Documentation]  IFS-4190
    [Tags]
    When the user triggers the email validation
    Then the user should see a field and summary error    ${enter_a_valid_email}

The internal user cannot invite users with an Innovate UK email as Stakeholders
    [Documentation]  IFS-4190
    [Tags]
    When the user enters an Innovate UK email
    Then the user should see the element    jQuery = .govuk-error-summary__list:contains("Stakeholders cannot be members of Innovate UK.")

The internal user can invite an applicant who already has an account
    [Documentation]  IFS-4288
    [Tags]
    Given the user enters the correct details of applicant
    When the user clicks the button/link    jQuery = a:contains("Added to competition")
    Then the user should see the element    jQuery = td:contains("Louis Morgan") ~ td:contains("${applicantEmail}") ~ td:contains("Added")

The internal user can invite an assessor who is already a stakeholder
    [Documentation]  IFS-4288
    [Tags]
    Given the user invite the stakholder
    Then the user should not see the element    jQuery = td:contains("Blake Wood")
    When the user clicks the button/link        jQuery = a:contains("Added to competition")
    Then the user should see the element        jQuery = td:contains("Blake Wood") ~ td:contains("${previousStakeholderEmail}") ~ td:contains("Added")

The internal user invites a new Stakeholder
    [Documentation]  IFS-4190
    [Tags]
    Given the user clicks the button/link   jQuery = span:contains("Invite a new stakeholder")
    And the user enters the correct details of a Stakeholder
    When the user clicks the button/link    jQuery = a:contains("Added to competition")
    Then the user should see the element    jQuery = td:contains("Stake Holder") ~ td:contains("${stakeholderEmail}") ~ td:contains("Invite pending")
    [Teardown]  logout as user

Check existing applicant is emailed and directed to sign in
    [Documentation]  IFS-4288
    [Tags]
    Given the user reads his email and clicks the link    ${applicantEmail}  Invite to view a competition: ${openProgrammeCompetitionName}  Sign in  1
    Then the user should see the element                  jQuery = h1:contains("Sign in")

Check existing assesor from the list is emailed and directed to sign in
    [Documentation]  IFS-4288
    [Tags]
    Given the user reads his email and clicks the link    ${previousStakeholderEmail}  Invite to view a competition: ${openProgrammeCompetitionName}  Sign in  1
    Then the user should see the element                  jQuery = h1:contains("Sign in")

Create stakeholders account validations from email
    [Documentation]  IFS-4252
    [Tags]
    Given the user reads his email and clicks the link    ${stakeholderEmail}  Invite to Innovation Funding Service  You have been invited to view the following competition  1
    When the user clicks the button/link                  jQuery = .govuk-button:contains("Create account")
    Then the user should see the validation errors

Invited stakeholder registration flow
    [Documentation]  IFS-4252
    [Tags]
    When the user enters the details and create account
    Then the user should see the element                  jQuery = h1:contains("Your account has been created")
    When the user clicks the button/link                  jQuery = a:contains("Sign into your account")
    Then Logging in and Error Checking                    ${stakeholderEmail}  ${short_password}
    And the user should see the element                   jQuery = h2:contains("Open") ~ ul a:contains("${openProgrammeCompetitionName}")

The internal user checks the status for newly added stakeholder
    [Documentation]  IFS-4252
    [Tags]
    Given log in as a different user          &{Comp_admin1_credentials}
    And the user navigates to the page        ${SERVER}/management/competition/setup/${competition_ids['${openProgrammeCompetitionName}']}/manage-stakeholders
    When the user clicks the button/link      css = a[href="?tab=added"]
    Then the user should see the element      jQuery = td:contains("Stake Holder") ~ td:contains("Added")

The internal user adds a Stakeholder to the competition
    [Documentation]  IFS-4189  IFS-4314
    [Tags]
    Given the user select stakeholder and add to competition
    When the user reads his email             ${stakeholder_user["email"]}    Invite to view a competition: ${openProgrammeCompetitionName}    You have been invited to view
    Then the user navigates to the page       ${LOGIN_URL}

The Stakeholder can see their dashboard and the competitions they were added to
    [Documentation]  IFS-4189
    [Tags]
    When log in as a different user         &{stakeholder_user}
    Then the user should see the element    jQuery = h3:contains("${openProgrammeCompetitionName}")

The Stakeholder can search for a competition
    [Documentation]  IFS-4189
    [Tags]
    Given the user enters text to a text field    searchQuery  ${openProgrammeCompetitionName}
    When the user clicks the button/link          id = searchsubmit
    And the user clicks the button/link           link = ${openProgrammeCompetitionName}
    Then the user should see the element          jQuery = h1:contains("${openProgrammeCompetitionId}: ${openProgrammeCompetitionName}")
    [Teardown]  The user clicks the button/link   link = Dashboard

The Stakeholder can search for application
    [Documentation]  IFS-4564
    [Tags]
    Given the user enters text to a text field    searchQuery  ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    When the user clicks the button/link          id = searchsubmit
    And the user clicks the button/link           link = ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    Then the user should see the element          jQuery = span:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    [Teardown]  The user clicks the button/link   link = Dashboard

The Stakeholder cannot search for unassigned applications
    [Documentation]  IFS-4564
    [Tags]
    Given the user enters text to a text field    searchQuery  ${OPEN_COMPETITION_APPLICATION_1_NUMBER}
    When the user clicks the button/link          id = searchsubmit
    Then the user should see the element          jQuery = p:contains("0") strong:contains("${OPEN_COMPETITION_APPLICATION_1_NUMBER}")
    [Teardown]  The user clicks the button/link   link = Dashboard

The internal user removes a Stakeholder from the competition
    [Documentation]  IFS-4189
    [Tags]
    [Setup]  Log in as a different user       &{Comp_admin1_credentials}
    Given the user navigates to the page      ${SERVER}/management/competition/setup/${competition_ids['${openProgrammeCompetitionName}']}/manage-stakeholders?tab=added
    When the user clicks the button/link      jQuery = td:contains("Rayon Kevin") button[type="submit"]
    And the user clicks the button/link       css = a[href="?tab=add"]
    Then the user should see the element      jQuery = td:contains("Rayon Kevin") button[type="submit"]

The Stakeholder can no longer see the competition
    [Documentation]  IFS-4189
    [Tags]
    When log in as a different user             &{stakeholder_user}
    Then the user should not see the element    jQuery = h3:contains("${openProgrammeCompetitionName}")

*** Keywords ***
Comp admin navigate to stakeholders page
    the user logs-in in new browser    &{Comp_admin1_credentials}
    the user clicks the button/link    link = ${openProgrammeCompetitionName}
    the user clicks the button/link    link = View and update competition setup
    the user clicks the button/link    link = Stakeholders

the user triggers the name validation
    the user enters text to a text field    id = emailAddress  stakeHolder@test.com
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user should see the name validation messages
    the user should see a field and summary error    ${enter_a_first_name}
    the user should see a field and summary error    Your first name should have at least 2 characters.
    the user should see a field and summary error    ${enter_a_last_name}
    the user should see a field and summary error    Your last name should have at least 2 characters.

the user triggers the email validation
    the user enters text to a text field    id = firstName     Stake
    the user enters text to a text field    id = lastName      Holder
    the user enters text to a text field    id = emailAddress  stakeHoldertest.com
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user enters an Innovate UK email
    the user enters text to a text field    id = firstName     Stake
    the user enters text to a text field    id = lastName      Holder
    the user enters text to a text field    id = emailAddress  stakeHolder@innovateuk.ukri.test
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user enters the correct details of a Stakeholder
    the user enters text to a text field    id = firstName     Stake
    the user enters text to a text field    id = lastName      Holder
    the user enters text to a text field    id = emailAddress  ${stakeholderEmail}
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user enters the correct details of applicant
    the user enters text to a text field    id = firstName     Louis
    the user enters text to a text field    id = lastName      Morgan
    the user enters text to a text field    id = emailAddress  ${applicantEmail}
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user invite the stakholder
    the user clicks the button/link     jQuery = a[href='?tab=add']
    the user should see the element     jQuery = td:contains("Blake Wood")
    the user clicks the button/link     jQuery = span:contains("Invite a new stakeholder")
    the user enters the correct details of a current stakeholder

the user enters the correct details of a current stakeholder
    the user enters text to a text field    id = firstName     Blake
    the user enters text to a text field    id = lastName      Wood
    the user enters text to a text field    id = emailAddress  ${previousStakeholderEmail}
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user should see the validation errors
    the user should see a field and summary error    Please enter a first name.
    the user should see a field and summary error     Please enter a last name.
    the user should see a field and summary error     Please enter your password.

the user enters the details and create account
    the user enters text to a text field     id = firstName  Stake
    the user enters text to a text field     id = lastName  Holder
    the user enters text to a text field     id = password  ${short_password}
    the user clicks the button/link          jQuery = .govuk-button:contains("Create account")

the user select stakeholder and add to competition
    the user clicks the button/link           css = a[href="?tab=add"]
    When the user clicks the button/link      jQuery = td:contains("Rayon Kevin") button[type="submit"]
    And the user clicks the button/link       jQuery = a:contains("Added to competition")
    Then the user should see the element      jQuery = td:contains("Rayon Kevin") ~ td:contains("Added")