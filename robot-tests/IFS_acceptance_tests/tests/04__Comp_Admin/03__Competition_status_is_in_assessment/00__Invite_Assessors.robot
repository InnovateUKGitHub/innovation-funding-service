*** Settings ***
Documentation     INFUND-6604 As a member of the competitions team I can view the Invite assessors dashboard...
...
...               INFUND-6602 As a member of the competitions team I can navigate to the dashboard of an 'In assessment' competition...
...
...               INFUND-6392 As a member of the competitions team, I can add/remove an assessor...
...
...               INFUND-6412 As a member of the competitions team, I can view the invite list before sending invites...
...
...               INFUND-6414 As a member of the competitions team, I can select 'Invite individual' to review invitation and then 'Send invite' ...
...
...               INFUND-6411 As a member of the competitions team, I can add a non-registered assessor to my invite list so...
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
The User can Add and Remove Assessors
    [Documentation]    INFUND-6602 INFUND-6604 INFUND-6392 INFUND-6412
    [Tags]
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And The user clicks the button/link    jQuery=.button:contains("Invite assessors")
    And The user should see the element    link=Overview
    When The user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Add)
    And The user clicks the button/link    link=Invite
    Then The user should see the text in the page    will.smith@gmail.com
    And The user should see the text in the page    Will Smith
    And The user should see the element    jQuery=tr:nth-child(1) .no
    #TODO Add an extra check for the innovation area when INFUND-6865 is ready
    When The user clicks the button/link    link=Find
    And The user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Remove)
    And The user clicks the button/link    link=Invite
    Then The user should not see the text in the page    will.smith@gmail.com
    [Teardown]    The user clicks the button/link    link=Find

Remove users from the list
    [Documentation]    INFUND-7354
    When The user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Add)
    And The user clicks the button/link    link=Invite
    And The user should see the text in the page    will.smith@gmail.com
    And The user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Remove from list)
    Then The user should not see the text in the page    will.smith@gmail.com
    [Teardown]    The user clicks the button/link    link=Find

Invite Individual Assessors
    [Documentation]    INFUND-6414
    Given The user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Add)
    And The user clicks the button/link    link=Invite
    When the user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Invite individual)
    And The user should see the text in the page    Please visit our new online Innovation funding service to respond to this request
    And The user enters text to a text field    css=#subject    Invitation to assess 'Sustainable living models for the future' @
    And the user clicks the button/link    jQuery=.button:contains(Send invite)
    Then The user should not see the text in the page    Will Smith
    And The user clicks the button/link    link=Find
    And the user should not see the text in the page    Will Smith

Invite non-registered users
    [Documentation]    INFUND-6411
    [Tags]
    Given the user clicks the button/link    link=Invite
    #when the user clicks the button/link    jQuery=span:contains("Add a non-registered assessor to your list")
    #And The user clicks the button/link    jQuery=button:contains("Add another assessor of this type")
    And The user enters text to a text field    css=#invite-table tr:nth-of-type(1) td:nth-of-type(1) input    Olivier Giroud
    And The user enters text to a text field    css=#invite-table tr:nth-of-type(1) td:nth-of-type(2) input    worth.email.test+OlivierGiroud@gmail.com
    And the user selects the option from the drop-down menu    Data    id=grouped-innovation-area
    And the user clicks the button/link    jQuery=.button:contains("Add assessor(s) to list")
    Then the user should not see the element    css=tr:nth-child(1).no
    And The user should see the text in the page    Olivier Giroud
    And The user should see the text in the page    worth.email.test+OlivierGiroud@gmail.com
    And The user should see the text in the page    Data
