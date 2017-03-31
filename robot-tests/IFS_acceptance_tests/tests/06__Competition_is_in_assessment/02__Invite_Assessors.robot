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
...
...               INFUND-6450 As a member of the competitions team, I can see the status of each assessor invite so...
...
...               INFUND-6448 As a member of the competitions team, I can remove an assessor from the invite list so...
...
...               INFUND-6450 As a member of the competitions team, I can see the status of each assessor invite so I know if they have accepted, declined or still awaiting response
...
...               INFUND-6389 As a member of the competitions team I can see the innovation sector and innovation area(s) on the Invite assessors dashboard so ...
...
...               INFUND-6449 As a member of the competitions team, I can see the invited assessors list so...
...
...               INFUND-6669 As a member of the competitions team I can view an assessors profile so that I can decide if they are suitable to assess the competition
...
...               INFUND-6388 As a member of the competitions team I can see the key statistics on the Invite Assessors dashboard so that I can easily see how invitations are progressing
...
...               INFUND-6403 Filter and Pagination on 'Find' tab of Invite dashboard
...
...               INFUND-6453 Filter and pagination on 'Overview' tab of Invite assessors dashboard
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Check the initial key statistics
    [Documentation]    INFUND-6388
    [Tags]
    Given the user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link    jQuery=a:contains("Invite assessors to assess the competition")
    And the user clicks the button/link    link=Overview

Filtering in the Invite Overview page
    [Documentation]    INFUND-6453
    [Tags]
    Given the user selects the option from the drop-down menu    Assembly / disassembly / joining    id=filterInnovationArea
    And the user selects the option from the drop-down menu    Invite declined    id=filterStatus
    And the user selects the option from the drop-down menu    Yes    id=filterContract
    When the user clicks the button/link    jQuery=button:contains(Filter)
    Then the user should see the element    jQuery=td:contains("Josephine")
    And the user should not see the element    jQuery=td:contains("No")
    And the user clicks the button/link    jQuery=a:contains("Clear filters")
    And the user should not see the element    jQuery=td:contains("Josephine")

The User can Add and Remove Assessors
    [Documentation]    INFUND-6602 INFUND-6604 INFUND-6392 INFUND-6412 INFUND-6388
    [Tags]
    Given The user clicks the button/link    link=Find
    And the user clicks the button/link    jQuery=a:contains(41 to)
    When The user clicks the button/link    jQuery=td:contains("Will Smith") ~ td .button:contains("Add")
    And The user clicks the button/link    link=Invite
    Then The user should see the text in the page    will.smith@gmail.com
    And The user should see the text in the page    Will Smith
    And The user should see the element    jQuery=td:contains("Will Smith") ~ td .yes
    And the user should see the element    jQuery=td:contains("Will Smith") ~ td:nth-child(3):contains("Precision medicine")
    And the user should see the element    jQuery=td:contains("Will Smith") ~ td:nth-child(3):contains("Nanotechnology / nanomaterials")
    And the user should see the element    jQuery=td:contains("Will Smith") ~ td:nth-child(3):contains("Energy systems")
    And the calculations of the Assessors on invite list should be correct
    When The user clicks the button/link    link=Invite
    And The user clicks the button/link    jQuery=td:contains("Will Smith") ~ td .button:contains("Remove")
    Then The user should not see the text in the page    Will Smith
    [Teardown]    The user clicks the button/link    link=Find

Filter on innovation area
    [Documentation]    INFUND-6403
    [Tags]
    Given the user selects the option from the drop-down menu    Offshore wind    id=filterInnovationArea
    When the user clicks the button/link    jQuery=button:contains(Filter)
    Then the user should see the element    jQuery=td:contains("Laura Weaver")
    And the user should not see the element    jQuery=td:contains("Addison Shannon")
    And the user clicks the button/link    jQuery=a:contains("Clear all filters")
    And the user should not see the element    jQuery=td:contains("Laura Weaver")
    And the user should see the element    jQuery=td:contains("Addison Shannon")

Next/Previous pagination on Find tab
    [Documentation]    INFUND-6403
    [Tags]
    When the user clicks the button/link    jQuery=.pagination-label:contains(Next)
    Then the user should see the element    jQuery=.pagination-part-title:contains(1 to 20)
    And the user should see the element    jQuery=.pagination-part-title:contains(41 to)
    And the user clicks the button/link    jQuery=.pagination-label:contains(Previous)
    And the user should not see the element    jQuery=.pagination-label:contains(Previous)
    And the user should not see the element    jQuery=.pagination-part-title:contains(41 to)

Page list pagination on Find tab
    [Documentation]    INFUND-6403
    [Tags]
    When the user clicks the button/link    jQuery=a:contains(41 to)
    Then the user should see the element    jQuery=.pagination-label:contains(Previous)
    And the user should not see the element    jQuery=.pagination-label:contains("Next)

The user can select the profile link
    [Documentation]    INFUND-6669
    [Tags]
    [Setup]
    When the user clicks the button/link    link=Will Smith
    Then the user should see the text in the page    will.smith@gmail.com
    And the user should see the text in the page    028572565937
    And the user should see the text in the page    Solar energy research
    And the user should see the text in the page    Precision medicine
    And the user should see the text in the page    Business
    [Teardown]    The user clicks the button/link    link=Back

Innovation sector and area are correct
    [Documentation]    INFUND-6389
    [Tags]
    Given the user should see the element    jQuery=.heading-secondary:contains("Sustainable living models for the future")
    And the user should see the element    jQuery=.standard-definition-list dt:contains("Innovation sector")
    And the user should see the element    jQuery=.standard-definition-list dt:contains("Innovation area")
    And the user should see the element    jQuery=.standard-definition-list dd:contains("Materials and manufacturing")
    And the user should see the element    jQuery=.standard-definition-list dd:contains("Satellite Applications")

Invite Individual Assessors
    [Documentation]    INFUND-6414
    [Tags]
    Given The user clicks the button/link    jQuery=td:contains("Will Smith") ~ td .button:contains("Add")
    And The user clicks the button/link    link=Invite
    When the user clicks the button/link    jQuery=td:contains("Will Smith") .button:contains("Invite individual")
    And The user should see the text in the page    Please visit our new online Innovation Funding Service to respond to this request
    And The user enters text to a text field    css=#subject    Invitation to assess 'Sustainable living models for the future' @
    And the user clicks the button/link    jQuery=.button:contains("Send invite")
    Then The user should not see the text in the page    Will Smith
    And The user clicks the button/link    link=Find
    And the user should not see the text in the page    Will Smith

Invite non-registered assessors server side validations
    [Documentation]    INFUND-6411
    [Tags]
    Given the user clicks the button/link    link=Invite
    When the user clicks the button/link    jQuery=span:contains("Add a non-registered assessor to your list")
    And the user clicks the button/link    jQuery=.button:contains("Add assessors to list")
    Then the user should see a field error    Please enter an innovation sector and area.
    And the user should see a field error    Please enter a name.
    And the user should see a field error    Please enter an email address.

Invite non-registered users
    [Documentation]    INFUND-6411
    ...
    ...    INFUND-6448
    [Tags]
    When The user enters text to a text field    css=#invite-table tr:nth-of-type(1) td:nth-of-type(1) input    Olivier Giroud
    And The user should not see the text in the page    Please enter a name.    #check for the client side validation
    And The user enters text to a text field    css=#invite-table tr:nth-of-type(1) td:nth-of-type(2) input    ${test_mailbox_one}+OlivierGiroud@gmail.com
    And The user should not see the text in the page    Please enter a name.    #check for the client side validation
    And the user selects the option from the drop-down menu    Emerging and enabling technologies    css=.js-progressive-group-select
    And the user selects the option from the drop-down menu    Emerging Technology    id=grouped-innovation-area
    And The user should not see the text in the page    Please enter an innovation sector and area.    #check for the client side validation
    And the user clicks the button/link    jQuery=.button:contains("Add assessors to list")
    Then the user should see the element    css=.no
    And The user should see the element    jQuery=td:contains("Olivier Giroud")
    And The user should see the element    jQuery=td:contains("Olivier Giroud") ~ td:contains(${test_mailbox_one}+OlivierGiroud@gmail.com)
    And The user should see the element    jQuery=td:contains("Olivier Giroud") ~ td:contains("Emerging Technology")
    And The user should see the element    jQuery=td:contains("Olivier Giroud") ~ td .button:contains("Remove")

Assessor overview information
    [Documentation]    INFUND-6450
    ...
    ...    INFUND-6449
    [Tags]
    Given The user clicks the button/link    link=Overview
    And the user clicks the button/link    jQuery=.pagination-label:contains("Next")
    Then the user should see the element    jQuery=td:contains("Paul Plum") ~ td:contains("Invite accepted")
    And the user clicks the button/link    jQuery=.pagination-label:contains("Next")
    And the user should see the element    jQuery=td:contains("Will Smith") ~ td:nth-of-type(5):contains("Awaiting response")
    And the user should see the element    jQuery=td:contains("Will Smith") ~ td:nth-of-type(6):contains("Invite sent:")
    And the user clicks the button/link    jQuery=.pagination-label:contains("Previous")
    And the user should see the element    jQuery=td:contains("Josephine Peters") ~ td:nth-of-type(5):contains("Invite declined")
    And the user should see the element    jQuery=td:contains("Josephine Peters") ~ td:contains("Academic")
    And the user should see the element    jQuery=td:contains("Josephine Peters") ~ td:contains("Yes")
    And the user should see the element    jQuery=td:contains("Josephine Peters") ~ td:contains("Invite declined as not available")
    And the user should see the element    jQuery=td:contains("Josephine Peters") ~ td:contains("Assembly / disassembly / joining")

*** Keywords ***
The key statistics are calculated
    #Calculation of the Invited Assessors
    ${INVITED_ASSESSORS}=    Get matching xpath count    //table/tbody/tr
    ${INVITED_COUNT}=    Get text    css=div:nth-child(1) > div > span
    Should Be Equal As Integers    ${INVITED_ASSESSORS}    ${INVITED_COUNT}
    #Calculation of the Accepted Assessors
    ${ACCEPTED_ASSESSORS}=    Get matching xpath count    //*[text()="Invite accepted"]
    ${ACCEPTED_COUNT}=    Get text    css=div:nth-child(2) > div > span
    Should Be Equal As Integers    ${ACCEPTED_COUNT}    ${ACCEPTED_ASSESSORS}
    #Calculation of the declined Assessors
    ${DECLINED_ASSESSORS}=    Get matching xpath count    //*[text()="Invite declined"]
    ${DECLINED_COUNT}=    Get text    css=div:nth-child(3) > div > span
    Should Be Equal As Integers    ${DECLINED_ASSESSORS}    ${DECLINED_COUNT}

the calculations of the Assessors on invite list should be correct
    #Calculation of the Assessors on invite list
    ${ASSESSORS_ON_LIST}=    Get matching xpath count    //form/table/tbody/tr
    ${ASSESSORS_COUNT}=    Get text    css=div:nth-child(4) > div > span
    Should Be Equal As Integers    ${ASSESSORS_ON_LIST}    ${ASSESSORS_COUNT}
