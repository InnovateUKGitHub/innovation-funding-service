*** Settings ***
Documentation   IFS-4189 Add/Remove Stakeholders
...
...             IFS-4190 Create a new user in stakeholder role
Resource        ../../resources/defaultResources.robot
Resource        ../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${openProgrammeCompetitionName}  Photonics for All
${openProgrammeCompetitionId}    ${competition_ids['${openProgrammeCompetitionName}']}

*** Test Cases ***
The internal user cannot invite a Stakeholder when they have triggered the name validation
    [Documentation]  IFS-4190
    [Tags]
    Given the user logs-in in new browser                &{Comp_admin1_credentials}
    And the user navigates to the page                   ${SERVER}/management/competition/setup/${competition_ids['${openProgrammeCompetitionName}']}/manage-stakeholders
    When the user triggers the name validation
    Then the user should see the name validation messages

The internal user cannot invite a Stakeholder when they have triggered the email validation
    [Documentation]  IFS-4190
    [Tags]
    When the user triggers the email validation
    Then the user should see a field and summary error    Please enter a valid email address.

The internal user cannot invite users with an Innovate UK email as Stakeholders
    [Documentation]  IFS-4190
    [Tags]
    When the user enters an Innovate UK email
    Then the user should see a field and summary error    Stakeholders cannot be registered with an Innovate UK email address.

The internal user invites a Stakeholder
    [Documentation]  IFS-4190
    [Tags]
    Given the user enters the correct details of a Stakeholder
    When the user clicks the button/link    jQuery = a:contains("Added to competition")
    Then the user should see the element    jQuery = td:contains("Stake Holder") ~ td:contains("stakeHolder@test.com") ~ td:contains("Invite pending")

The internal user adds a Stakeholder to the competition
    [Documentation]  IFS-4189
    [Tags]
    Given the user clicks the button/link    css = a[href="?tab=add"]
    When the user clicks the button/link     jQuery = td:contains("Rayon Kevin") button[type="submit"]
    And the user clicks the button/link      jQuery = a:contains("Added to competition")
    Then the user should see the element     jQuery = td:contains("Rayon Kevin") ~ td:contains("Added")

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

The internal user removes a Stakeholder from the competition
    [Documentation]  IFS-4189
    [Tags]
    [Setup]  Log in as a different user       &{Comp_admin1_credentials}
    Given the user navigates to the page      ${SERVER}/management/competition/setup/${competition_ids['${openProgrammeCompetitionName}']}/manage-stakeholders?tab=added
    Given the user clicks the button/link     jQuery = button[type="submit"]
    When the user clicks the button/link      css = a[href="?tab=add"]
    Then the user should see the element      jQuery = td:contains("Rayon Kevin") button[type="submit"]

The Stakeholder can no longer see the competition
    [Documentation]  IFS-4189
    [Tags]
    When log in as a different user             &{stakeholder_user}
    Then the user should not see the element    jQuery = h3:contains("${openProgrammeCompetitionName}")

*** Keywords ***
the user triggers the name validation
    the user clicks the button/link         jQuery = span:contains("Invite a new stakeholder")
    the user enters text to a text field    id = emailAddress  stakeHolder@test.com
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user should see the name validation messages
    the user should see a field and summary error    Please enter a first name.
    the user should see a field and summary error    Your first name should have at least 2 characters.
    the user should see a field and summary error    Please enter a last name.
    the user should see a field and summary error    Your last name should have at least 2 characters.

the user triggers the email validation
    the user enters text to a text field             id = firstName     Stake
    the user enters text to a text field             id = lastName      Holder
    the user enters text to a text field             id = emailAddress  stakeHoldertest.com
    the user clicks the button/link                  css = button[name = "inviteStakeholder"]

the user enters an Innovate UK email
    the user enters text to a text field    id = firstName     Stake
    the user enters text to a text field    id = lastName      Holder
    the user enters text to a text field    id = emailAddress  stakeHolder@innovateuk.test
    the user clicks the button/link         css = button[name = "inviteStakeholder"]

the user enters the correct details of a Stakeholder
    the user enters text to a text field    id = firstName     Stake
    the user enters text to a text field    id = lastName      Holder
    the user enters text to a text field    id = emailAddress  stakeHolder@test.com
    the user clicks the button/link         css = button[name = "inviteStakeholder"]