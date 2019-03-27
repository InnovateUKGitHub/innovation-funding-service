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
...
...               INFUND-1985 Rename 'Overview' tab on Invite assessors dashboard to 'Pending and rejected'
...
...               IFS-33 As a comp exec I can select and add multiple assessors to the invite list
...
...               IFS-1146 Assessor management - Resending invite emails in bulk
...
...               IFS-1445 Assessor management- 'Accepted' tab on invite assessors dashboard
...
...               IFS-3943 Assessor profile view - Internal
Suite Setup       Custom suite setup
Suite Teardown    Custom teardown
Force Tags        CompAdmin  Assessor
Resource          ../../resources/defaultResources.robot
Resource          ../07__Assessor/Assessor_Commons.robot

*** Variables ***
${assessor_to_add}    Alexis Kinney
${invitedAssessor}    will.smith@gmail.com

*** Test Cases ***
Check the initial key statistics
    [Documentation]    INFUND-6388
    [Tags]
    Given the user clicks the button/link  link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link    jQuery = a:contains("Invite assessors to assess the competition")
    And the user clicks the button/link    link = Pending and declined

Filtering in the Invite Pending and declined page
    [Documentation]    INFUND-6453
    [Tags]
    Given the user filter assessors by innovation area, status, contract and DOI
    Then the user should not see the element   jQuery = td:contains("No")
    When the user clicks the button/link       jQuery = a:contains("Clear filters")
    Then the user should see the element       jQuery = td:contains("David")

The User can Add and Remove Assessors
    [Documentation]    INFUND-6602 INFUND-6604 INFUND-6392 INFUND-6412 INFUND-6388
    [Tags]
    Given the user add assessor to invite list
    Then the user should see assessor details
    And the user can remove an assessor from the invite list
    [Teardown]    The user clicks the button/link      link = Find

The user can remove all people from the list
    [Documentation]    IFS-36
    [Tags]
    Given the user invites multiple assessors
    When the user clicks the button/link           jQuery = button:contains("Remove all")
    Then the user should not see the element       jQuery = td:contains("${assessor_to_add}")
    [Teardown]    The user clicks the button/link  link = Find

Filter on innovation area
    [Documentation]    INFUND-6403
    [Tags]
    Given the user filter assessors by innovation area
    When the user clicks the button/link      jQuery = a:contains("Clear all filters")
    Then the user should not see the element  jQuery = td:contains("Laura Weaver")
    And the user should see the element       jQuery = td:contains("Addison Shannon")

Next/Previous pagination on Find tab
    [Documentation]    INFUND-6403
    [Tags]
    Given the user clicks the button/link  link = 21 to 40
    Then the user should see the element   jQuery = .pagination-label:contains("Previous")
    And the user should see the element    jQuery = .pagination-label:contains("Next")

Assessor link goes to the assessor profile
    [Documentation]    INFUND-6669  IFS-3943
    [Tags]
    Given the user click on assessor name link
    Then the user should see the assessor details and DOI
    [Teardown]  The user clicks the button/link  link = Back

Innovation sector and area are correct
    [Documentation]    INFUND-6389
    [Tags]
    Given the user should see the element  jQuery = .govuk-caption-l:contains("${IN_ASSESSMENT_COMPETITION_NAME}")
    And the user should see the element    jQuery = dt:contains("Innovation sector") ~ dd:contains("Materials and manufacturing")
    And the user should see the element    jQuery = dt:contains("Innovation area") ~ dd:contains("Digital manufacturing")

Invite multiple assessors
    [Documentation]    INFUND-6414
    [Tags]
    Given the user invites multiple assessors
    When the user send invite to assessors
    Then the user should not see invited assessor on inivte and find tab

Invite non-registered assessors server side validations
    [Documentation]    INFUND-6411
    [Tags]
    Given the user clicks the button/link   link = Invite
    When the user clicks the button/link    jQuery = span:contains("Add a non-registered assessor to your list")
    And the user clicks the button/link     jQuery = .govuk-button:contains("Add assessors to list")
    Then the user should see server side validations triggered correctly

Invite non-registered users
    [Documentation]    INFUND-6411 INFUND-6448
    [Tags]
    Given the user enter non-registres assessor details and add to assessors list
    Then the user should see an assessor details

Assessor overview information
    [Documentation]    INFUND-6450 INFUND-6449
    [Tags]
    Given The user clicks the button/link  link = Pending and declined
    Then the user should see assessors details on pending and declined tab

Assessor accepted information
    [Documentation]  IFS-1445
    [Tags]
    Given the user clicks the button/link  link = Accepted
    And the user clicks the button/link    jQuery = .pagination-label:contains("Next")
    Then the user should see the element   jQuery = td:contains("Paul Plum")

Select to add all assessors to invite list
    [Documentation]  IFS-33
    [Tags]
    [Setup]  the user clicks the button/link  link = Find
    Given the user selects the checkbox       select-all-check
    When the user clicks the button/link      jQuery = button:contains("Add selected to invite list")
    And the user clicks the button/link       link = Find
    Then the user should see the element      jQuery = td:contains("No available assessors found.")

Bulk resend button is disabled until user selects an assessor
    [Documentation]  IFS-1146
    [Tags]
    [Setup]  the user clicks the button/link  link = Pending and declined
    Given the element should be disabled      jQuery = button:contains("Resend invites")
    When the user selects the checkbox        select-all-check
    And the user clicks the button/link       jQuery = button:contains("Resend invites")
    Then the user should see the element      css = input[id = "subject"][value = "Invitation to assess '${IN_ASSESSMENT_COMPETITION_NAME}'"]

Bulk resend email updates the invite sent date
    [Documentation]  IFS-1146
    [Tags]
    Given the user clicks the button/link  jQuery = button:contains("Send invite")
    Then the user should see the element   jQuery = td:contains("David Peters") ~ td:contains("Invite sent: ${today}")

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${today}  get today short month
    set suite variable  ${today}

The key statistics are calculated
    #Calculation of the Invited Assessors
    ${INVITED_ASSESSORS} =     Get Element Count    //table/tbody/tr
    ${INVITED_COUNT} =     Get text    css = div:nth-child(1) > div > span
    Should Be Equal As Integers    ${INVITED_ASSESSORS}    ${INVITED_COUNT}
    #Calculation of the Accepted Assessors
    ${ACCEPTED_ASSESSORS} =     Get Element Count    //*[text() = "Invite accepted"]
    ${ACCEPTED_COUNT} =     Get text    css = div:nth-child(2) > div > span
    Should Be Equal As Integers    ${ACCEPTED_COUNT}    ${ACCEPTED_ASSESSORS}
    #Calculation of the declined Assessors
    ${DECLINED_ASSESSORS} =     Get Element Count    //*[text() = "Invite declined"]
    ${DECLINED_COUNT} =     Get text    css = div:nth-child(3) > div > span
    Should Be Equal As Integers    ${DECLINED_ASSESSORS}    ${DECLINED_COUNT}

the user invites multiple assessors
    the user clicks the button/link     link = 1 to 20
    the user selects the checkbox  assessor-row-1
    the user selects the checkbox  assessor-row-2
    the user selects the checkbox  assessor-row-3
    the user clicks the button/link     jQuery = button:contains("Add selected to invite list")
    the user should see the element     jQuery = td:contains("${assessor_to_add}")

the user send invite to assessors
    the user clicks the button/link            jQuery = a:contains("Review and send invites")
    the user should see the element            jQuery = p:contains("Please visit our online Innovation Funding Service to respond to this request")
    the user should see the client and server side validation for subject
    the user enters text to a text field       css = #subject  Invitation to assess '${IN_ASSESSMENT_COMPETITION_NAME}' @
    the user clicks the button/link            jQuery = .govuk-button:contains("Send invite")

the user should not see invited assessor on inivte and find tab
    the user clicks the button/link           link = Invite
    the user should not see the element       link = ${assessor_to_add}
    the user clicks the button/link           link = Find
    the user should not see the element       link = ${assessor_to_add}

Custom teardown
    the user clicks the button/link  link = Invite
    the user clicks the button/link  jQuery = button:contains("Remove all")
    the user closes the browser

the user should see the client and server side validation for subject
    the user enters text to a text field        id = subject   ${EMPTY}
    the user clicks the button/link             css = button[type = "submit"]    #Send invite
    the user should see a field and summary error  Please enter a subject for the email.

the user filter assessors by innovation area, status, contract and DOI
    the user selects the option from the drop-down menu    Assembly / disassembly / joining  id = filterInnovationArea
    the user selects the option from the drop-down menu    Invite declined  id = filterStatus
    the user selects the option from the drop-down menu    Yes  id = filterContract
    the user clicks the button/link                        jQuery = button:contains(Filter)
    the user should see the element                        jQuery = td:contains("Josephine")

the user add assessor to invite list
    the user clicks the button/link    link = Find
    the user clicks the button/link    jQuery = a:contains("41 to")
    the user clicks the button/link    jQuery = a:contains("61 to")
    the user clicks the button/link    jQuery = input[value = "${getUserId("${invitedAssessor}")}"] ~ label
    the user should see the element    jQuery = .govuk-hint:contains("1 assessors selected")
    the user clicks the button/link    jQuery = button:contains("Add selected to invite list")

the user should see assessor details
    the user should see the element   jQuery = td:contains("${invitedAssessor}")
    the user should see the element    jQuery = td:contains("Will Smith") ~ td .yes
    the user should see the element    jQuery = td:contains("Will Smith") ~ td:nth-child(3):contains("Precision medicine")
    the user should see the element    jQuery = td:contains("Will Smith") ~ td:nth-child(3):contains("Nanotechnology / nanomaterials")
    the user should see the element    jQuery = td:contains("Will Smith") ~ td:nth-child(3):contains("Energy systems")

the user can remove an assessor from the invite list
    the user clicks the button/link         link = Invite
    the user clicks the button/link         jQuery = td:contains("Will Smith") ~ td .button-clear:contains("Remove")
    the user should not see the element     link = Will Smith

the user filter assessors by innovation area
    the user selects the option from the drop-down menu  Offshore wind  id = filterInnovationArea
    the user clicks the button/link                      jQuery = button:contains(Filter)
    the user should see the element                      jQuery = td:contains("Laura Weaver")
    the user should not see the element                  jQuery = td:contains("${assessor_to_add}")

the user click on assessor name link
    the user clicks the button/link   jQuery = a:contains("41 to")
    the user clicks the button/link   jQuery = a:contains("61 to")
    the user clicks the button/link   link = Will Smith

the user should see the assessor details and DOI
    the user should see the element    jQuery = dt:contains("Email address") ~ dd:contains("${invitedAssessor}")
    the user should see the element    jQuery = dt:contains("Assessor type") + dd:contains("Business")
    the user should see the element    jQuery = dt:contains("Phone") + dd:contains("28572565937")
    the user should see the element    jQuery = h3:contains("Innovation areas") ~ .govuk-table th:contains("Health and life sciences")
    the user should see the element    jQuery = h3:contains("Skill areas") ~ p:contains("Solar energy research")
    the user clicks the button/link    link = DOI
    the user should see the element    jQuery = h2:contains("Principal employer and role") ~ p:contains(" Smith Systems")

the user should see server side validations triggered correctly
    the user should see a field error   Please enter an innovation sector and area.
    the user should see a field error   Please enter a name.
    the user should see a field error   Please enter an email address.

the user enter non-registres assessor details and add to assessors list
    Set Focus To Element                                 jQuery = .govuk-button:contains("Add assessors to list")
    the user enters text to a text field                 css = #invite-table tr:nth-of-type(1) td:nth-of-type(1) input   Olivier Giroud
    the user should not see the element                  jQuery = label:contains("Please enter a name.")    #check for the client side validation
    the user enters text to a text field                 css = #invite-table tr:nth-of-type(1) td:nth-of-type(2) input   ${test_mailbox_one}+OlivierGiroud@gmail.com
    the user should not see the element                  jQuery = label:contains("Please enter an email address.")    #check for the client side validation
    the user selects the option from the drop-down menu  Emerging and enabling    css = .js-progressive-group-select
    the user selects the option from the drop-down menu  Emerging technology    id = grouped-innovation-area
    the user should not see the element                  jQuery = label:contains("Please enter an innovation sector and area.")    #check for the client side validation
    the user clicks the button/link                      jQuery = .govuk-button:contains("Add assessors to list")

the user should see an assessor details
    the user should see the element       css = .no
    the user should see the element       jQuery = td:contains("Olivier Giroud")
    the user should see the element       jQuery = td:contains("Olivier Giroud") ~ td:contains(${test_mailbox_one}+OlivierGiroud@gmail.com)
    the user should see the element       jQuery = td:contains("Olivier Giroud") ~ td:contains("Emerging technology")
    the user should see the element       jQuery = td:contains("Olivier Giroud") ~ td .button-clear:contains("Remove")

the user should see assessors details on pending and declined tab
    the user should see the element    jQuery = td:contains("Josephine Peters") ~ td:nth-of-type(6):contains("Invite declined")
    the user should see the element    jQuery = td:contains("Josephine Peters") ~ td:contains("Academic")
    the user should see the element    jQuery = td:contains("Josephine Peters") ~ td:contains("Yes")
    the user should see the element    jQuery = td:contains("Josephine Peters") ~ td:contains("Invite declined: not available")
    the user should see the element    jQuery = td:contains("Josephine Peters") ~ td:contains("Assembly / disassembly / joining")
    the user should see the element    jQuery = td:contains("${assessor_to_add}") ~ td:nth-of-type(6):contains("Awaiting response")
    the user should see the element    jQuery = td:contains("${assessor_to_add}") ~ td:nth-of-type(7):contains("Invite sent:")