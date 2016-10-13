*** Settings ***
Documentation     INFUND-3970 As a partner I want a spend profile page in Project setup so that I can access and share Spend profile information within my partner organisation before submitting to the Project Manager
...
...               INFUND-3764 As a partner I want to view a Spend Profile showing my partner organisation's eligible project costs divided equally over the duration of our project to begin review our project costs before submitting to the Project Manager
...
...               INFUND-3765 As a partner I want to be able to edit my Spend profile so I can prepare an updated profile for my organisation before submission to the Project Manager
...
...               INFUND-3971 As a partner I want to be able to view my spend profile in a summary table so that I can review my spend profile by financial year
Suite Setup       the project finance user generates the spend profile table
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Resource          ../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***

*** Test Cases ***
Lead partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]    HappyPath
    [Setup]    Log in as user    steve.smith@empire.com    Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
#    Then the user clicks the button/link    link=Vitruvius Stonework Limited    # That's for when the Lead Partner has to choose which SP to see
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK
    And the user should see the text in the page    Vitruvius Stonework Limited - Spend profile

Lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page    January 2017
    And the user should see the text in the page    36 Months

Calculations in the spend profile table
    [Documentation]    INFUND-3764
    [Tags]    HappyPath
    Given the user should see the element    jQuery=div.spend-profile-table
    Then element should contain    css=div.spend-profile-table tr:nth-child(1) td:nth-child(38)    £ 104,354    #Labour
    Then element should contain    css=div.spend-profile-table tr:nth-child(2) td:nth-child(38)    £ 0          #Overheads
    Then element should contain    css=div.spend-profile-table tr:nth-child(3) td:nth-child(38)    £ 188,025    #Materials
    Then element should contain    css=div.spend-profile-table tr:nth-child(4) td:nth-child(38)    £ 0          #Capital usage
    Then element should contain    css=div.spend-profile-table tr:nth-child(5) td:nth-child(38)    £ 23,423     #Subcontracting
    Then element should contain    css=div.spend-profile-table tr:nth-child(6) td:nth-child(38)    £ 7,956      #Travel & subsistence
    Then element should contain    css=div.spend-profile-table tr:nth-child(7) td:nth-child(38)    £ 32,444     #Other costs
     #${duration} is No of Months + 1, due to header
    And the sum of tds equals the total    div.spend-profile-table   1    38    104354    # Labour
    And the sum of tds equals the total    div.spend-profile-table   3    38    188025    # Materials
    And the sum of tds equals the total    div.spend-profile-table   5    38    23423     # Subcontracting
    And the sum of tds equals the total    div.spend-profile-table   6    38    7956      # Travel & subsistence
    And the sum of tds equals the total    div.spend-profile-table   7    38    32444     # Other Costs

Lead Partner can see Spend profile summary
    [Documentation]    INFUND-3971
    [Tags]
    Given the user navigates to the page            ${server}/project-setup/project/1/partner-organisation/31/spend-profile/
    And the user should see the text in the page    Project costs for financial year
    And the user moves focus to the element         jQuery=div.grid-container table
    Then the user sees the text in the element      jQuery=div.grid-container table tr:nth-child(1) td:nth-child(2)    £ 29,667
    And the user sees the text in the element       jQuery=div.grid-container table tr:nth-child(2) td:nth-child(2)    £ 118,740
    And the user sees the text in the element       jQuery=div.grid-container table tr:nth-child(3) td:nth-child(2)    £ 118,740
    And the user sees the text in the element       jQuery=div.grid-container table tr:nth-child(4) td:nth-child(2)    £ 89,055

Lead partner can edit his spend profile with invalid values
    [Documentation]    INFUND-3765
    [Tags]
    When the user clicks the button/link             jQuery=.button:contains("Edit spend profile")
    Then the text box should be editable             css=#row-Labour-0
    When the user enters text to a text field        css=#row-Labour-0    2899
    And the user moves focus to the element          css=#row-Labour-2
    Then the user should see the text in the page    Unable to submit spend profile
    And the user should see the text in the page     Your total costs are higher than your eligible costs
    Then the field has value                         css=#row-total-Labour    £ 104,364
    And the user should see the element              jQuery=tr:nth-child(1) .cell-error
    When the user clicks the button/link             jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should see the text in the page    Your spend profile has total costs higher than eligible project costs,
    When the user clicks the button/link             jQuery=.button:contains("Edit spend profile")
    Then the user enters text to a text field        css=#row-Labour-0    2889
    # And the user should not see the element        jQuery=.tr:nth-child(1).error    TODO INFUND-5156
    When the user enters text to a text field        css=#row-Materials-3    -55
    And the user moves focus to the element          css=#row-Materials-5
    Then the user should see the text in the page    This field should be 0 or higher
    # When the user enters text to a text field      css=#row-Labour-3    35.25
    # Then the user should see the text in the page  TODO INFUND-5172
    When the user enters text to a text field        css=#row-Materials-3    5223
    And the user moves focus to the element          css=#row-Materials-5
    # And the user should not see the text in the page   This field should be 0 or higher    TODO INFUND-5160
    # Then the user should not see the element       jQuery=.tr:nth-child(1).error    TODO INFUND-5156
    Then the user clicks the button/link             jQuery=.button:contains("Save and return to spend profile overview")

Lead partner can edit his spend profile with valid values
    [Documentation]    INFUND-3765
    [Tags]
    Given the user navigates to the page                 ${server}/project-setup/project/1/partner-organisation/31/spend-profile/
    When the user clicks the button/link                 jQuery=.button:contains("Edit spend profile")
    And the user should not see the element              css=table a[type="number"]
    Then the text box should be editable                 css=#row-Labour-0
    When the user enters text to a text field            css=#row-Labour-24    2000
    And the user moves focus to the element              css=#row-Labour-25
    Then the field has value                             css=#row-total-Labour    £ 103,455
    And the user should not see the text in the page     Unable to save spend profile
    When the user enters text to a text field            css=#row-Travel--subsistence-35    0
    And the user moves focus to the element              css=#row-Travel--subsistence-33
    Then the field has value                             css=#row-total-Travel--subsistence    £ 7,735
    And the user should not see the text in the page     Unable to save spend profile
    Then the user clicks the button/link                 jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should not see the text in the page    Your spend profile has total costs higher than eligible project costs,

Lead Partners Spend profile summary gets updated when edited
    [Documentation]    INFUND-3971
    [Tags]
    Given the user navigates to the page             ${server}/project-setup/project/1/partner-organisation/31/spend-profile/
    Then the user should see the text in the page    Project costs for financial year
    And the user sees the text in the element        jQuery=div.grid-container table tr:nth-child(3) td:nth-child(2)    £ 117,841
    And the user sees the text in the element        jQuery=div.grid-container table tr:nth-child(4) td:nth-child(2)    £ 88,834

Lead partner submits Spend Profile
    [Documentation]    INFUND-3765
    [Tags]
    Given the user navigates to the page            ${server}/project-setup/project/1/partner-organisation/31/spend-profile/
    When the user clicks the button/link            jQuery=.button:contains("Mark as complete")
    Then the user should see the text in the page   Your Spend Profile is currently marked as complete
    And the user should not see the element         css=table a[type="number"]
    [Teardown]    Logout as user

# TODO update the acc tests for Editing the Spend Profile by a non-lead partner  INFUND-5153

Non-lead partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]
    [Setup]    Log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK
    And the user should see the text in the page    Ludlow - Spend profile

Non-lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page    January 2017
    And the user should see the text in the page    36 Months
    [Teardown]    Logout as user


Status updates correctly for internal user's table
    [Documentation]    INFUND-4049
    [Tags]    Experian
    [Setup]    guest user log-in    john.doe@innovateuk.test    Passw0rd
    When the user navigates to the page    ${internal_project_summary}
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.ok
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(2).status.ok
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(3).status.ok
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(4).status.action
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(6).status.action



*** Keywords ***
the project finance user generates the spend profile table
    log in as user    project.finance1@innovateuk.test    Passw0rd
    the user navigates to the page    ${server}/project-setup-management/project/1/spend-profile/summary    # For now we need to go to the url directly, as the project finance dashboard doesn't exist yet.
    the user clicks the button/link    jQuery=.button:contains("Generate Spend Profile")
    the user clicks the button/link    name=submit-app-details    # this second click is confirming the decision on the modal
    logout as user

the sum of tds equals the total
    [Arguments]    ${table}    ${row}    ${duration}    ${total}
    # This Keyword performs a for loop that iterates per column (in a specific row)
    # gets the sum of the cells and evaluates whether the sum of them equals their total
    ${sum} =    convert to number    0
    ${total} =    convert to number    ${total}
    : FOR    ${i}    IN RANGE    2    ${duration}    # due to header in the first column
    \    ${text} =    Get Text    jQuery=${table} tr:nth-child(${row}) td:nth-child(${i})
    \    ${formatted} =    Remove String    ${text}    ,    # Remove the comma from the number
    \    ${cell} =    convert to integer    ${formatted}
    \    ${sum} =    Evaluate    ${sum}+${cell}
    Should Be Equal As Integers    ${sum}    ${total}

the text box should be editable
    [Arguments]    ${element}
    Wait until element is visible    ${element}
    Element Should Be Enabled        ${element}

the field has value
    [Arguments]    ${field}    ${value}
    wait until element is visible    ${field}
    ${var} =  get value     ${field}
    should be equal as strings    ${var}    ${value}
