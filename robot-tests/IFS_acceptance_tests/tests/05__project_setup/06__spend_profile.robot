*** Settings ***
Documentation     INFUND-3970 As a partner I want a spend profile page in Project setup so that I can access and share Spend profile information within my partner organisation before submitting to the Project Manager
...
...               INFUND-3764 As a partner I want to view a Spend Profile showing my partner organisation's eligible project costs divided equally over the duration of our project to begin review our project costs before submitting to the Project Manager
...
...               INFUND-3765 As a partner I want to be able to edit my Spend profile so I can prepare an updated profile for my organisation before submission to the Project Manager
...
...               INFUND-3971 As a partner I want to be able to view my spend profile in a summary table so that I can review my spend profile by financial year
...
...               INFUND-2638 As a Competitions team member I want to view a page providing a link to each partners' submitted spend profile so that I can confirm these have been approved by the Technical Lead
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
@{database}       pymysql    ${database_name}    ${database_user}    ${database_password}    ${database_host}    ${database_port}

*** Test Cases ***
Lead partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]    HappyPath
    [Setup]    Log in as user    steve.smith@empire.com    Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
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


Lead partner marks spend profile as complete
    [Documentation]    INFUND-3765
    [Tags]
    Given the user navigates to the page            ${server}/project-setup/project/1/partner-organisation/31/spend-profile/
    When the user clicks the button/link            jQuery=.button:contains("Mark as complete")
    Then the user should see the text in the page   Your Spend Profile is currently marked as complete
    And the user should not see the element         css=table a[type="number"]    # checking here that the table has become read-only
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


Non-lead partner marks Spend Profile as complete
    [Documentation]    INFUND-3767
    [Tags]
    When the user clicks the button/link            jQuery=.button:contains("Mark as complete")
    Then the user should see the text in the page   Your Spend Profile is currently marked as complete
    And the user should not see the element         css=table a[type="number"]    # checking here that the table has become read-only
    [Teardown]    logout as user


Project Manager doesn't have the option to submit spend profiles until all partners have marked as complete
    [Documentation]    INFUND-3767
    [Tags]
    [Setup]    guest user log-in    worth.email.test+projectlead@gmail.com    Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link    link=Spend profile
    Then the user should not see the element    jQuery=.button:contains("Review and submit total project profile spend")
    [Teardown]    logout as user


Academic partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]
    [Setup]    Log in as user    pete.tom@egg.com    Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK
    And the user should see the text in the page    EGGS - Spend profile


Academic partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page    January 2017
    And the user should see the text in the page    36 Months


Academic partner marks Spend Profile as complete
    [Documentation]    INFUND-3767
    [Tags]
    When the user clicks the button/link            jQuery=.button:contains("Mark as complete")
    Then the user should see the text in the page   Your Spend Profile is currently marked as complete
    And the user should not see the element         css=table a[type="number"]    # checking here that the table has become read-only
    [Teardown]    logout as user


Lead partner can view partners' spend profiles
    [Documentation]    INFUND-3767
    [Tags]
    [Setup]    guest user log-in    worth.email.test+projectlead@gmail.com    Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
    Then the user clicks the button/link    link=Vitruvius Stonework Limited
    And the user should not see an error in the page
    And the user goes back to the previous page
    And the user clicks the button/link    link=Ludlow
    And the user should not see an error in the page
    And the user goes back to the previous page
    And the user clicks the button/link    link=EGGS
    And the user should not see an error in the page
    And the user goes back to the previous page

Lead partner can view combined spend profile
    [Documentation]    INFUND-3767
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Review and submit total project profile")
    Then the user should see the text in the page    This is the proposed spend profile for your project.
    And the user should see the text in the page    The spend profile that you submit will be used as the base for your project spend over the following financial years.


Project Manager can choose cancel on the dialogue
    [Documentation]    INFUND-3767
    When the user clicks the button/link    jQuery=.button:contains("Submit project spend profile")
    And the user clicks the button/link    jQuery=.button:contains("Cancel")
    Then the user should see the element    jQuery=.button:contains("Submit project spend profile")


Project Manager can submit the project's spend profiles
    [Documentation]    INFUND-3767
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Submit project spend profile")
    And the user should see the element    jQuery=.button:contains("Cancel")
    When the user clicks the button/link    css=div.modal-confirm-spend-profile-totals .button.large
    Then the user should see the text in the page    Project setup status
    And the user should see the element    jQuery=ul li.complete:nth-child(4)


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
    [Teardown]  Logout as user

Project Finance is able to see Spend Profile approval page
    [Documentation]    INFUND-2638
    [Tags]
    [Setup]  Log in as user                         project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page            ${server}/project-setup-management/project/1/spend-profile/approval  #Navigate via url TODO INFUND-5560
    Then the user should see the element            jQuery=#content div.grid-row div.column-third.alignright.extra-margin h2:contains("Spend profile")
    # TODO INFUND-5546
    #    And the user should not see the element  jQuery=h2:contains("The spend profile has been approved.")
    #    Same for Rejection
    When the user should see the text in the page    Lead technologist
    #Then the user should see the text in the page    Competition Technologist One  #The name of the leadTech Upcoming functionality TODO
    When the user should see the text in the page    Project spend profile
    Then the user clicks the button/link             link=Vitruvius Stonework Limited-spend-profile.csv
    And the user clicks the button/link              link=Ludlow-spend-profile.csv
    And the user clicks the button/link              link=EGGS-spend-profile.csv
    When the user should see the text in the page    Approved by Lead technologist
    Then the element should be disabled              jQuery=#accept-profile
#    Then the user should not see the element         jQuery=#accept-profile
    When the user selects the checkbox               jQuery=#approvedByLeadTechnologist
    Then the user should see the element             jQuery=#accept-profile
    And the user should see the element              jQuery=#content .button.button.button-warning.large:contains("Reject spend profile")
    [Teardown]  Logout as user

Comp Admin is able to see Spend Profile approval page
    [Documentation]    INFUND-2638
    [Tags]    Pending
    #TODO Comp Admin can Approve/Reject Spend Profile INFUND-5546
    [Setup]  Log in as user                 john.doe@innovateuk.test    Passw0rd
    Given the user navigates to the page    ${server}/project-setup-management/project/1/spend-profile/approval  #Navigate via url TODO INFUND-5560
    Then the user should see the element    jQuery=#content div.grid-row div.column-third.alignright.extra-margin h2:contains("Spend profile")
    When the user selects the checkbox      jQuery=#approvedByLeadTechnologist
    Then the user should see the element    jQuery=#accept-profile
    And the user should see the element     jQuery=#content .button.button.button-warning.large:contains("Reject spend profile")
    [Teardown]  Logout as user

Project Finance is able to Reject Spend Profile
    [Documentation]    INFUND-2638
    [Tags]    HappyPath
    [Setup]  Log in as user                        project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page           ${server}/project-setup-management/project/1/spend-profile/approval
    And the user should see the element            jQuery=#content .button.button.button-warning.large:contains("Reject spend profile")
    When the user clicks the button/link           jQuery=#content .button.button.button-warning.large:contains("Reject spend profile")
    Then the user should see the text in the page  Before taking this action please contact the project manager
    When the user clicks the button/link           jQuery=.modal-reject-profile button:contains("Cancel")
    Then the user should not see an error in the page
    #    When the user clicks the button/link           jQuery=#content .button.button.button-warning.large:contains("Reject spend profile")
    #    And the user clicks the button/link            jQuery=.modal-reject-profile button:contains('Reject spend profile')
    #    Then the user should see the element           jQuery=h3:contains("The spend profile has been rejected")
    # The above lines are passing, but they are disabled so that the Sp Prof can be Approved. This will be changed with upcoming functionality.

Project Finance is able to Approve Spend Profile
    [Documentation]    INFUND-2638
    [Tags]    HappyPath
    Given the user navigates to the page             ${server}/project-setup-management/project/1/spend-profile/approval
    When the user selects the checkbox               jQuery=#approvedByLeadTechnologist
    Then the user should see the element             jQuery=button:contains("Approved")
    When the user clicks the button/link             jQuery=button:contains("Approved")
    Then the user should see the text in the page    approved and accepted by the Lead technologist
    When the user clicks the button/link             jQuery=.modal-accept-profile button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link             jQuery=button:contains("Approved")
    And the user clicks the button/link              jQuery=.modal-accept-profile button:contains("Accept documents")
    Then the user should not see the element         jQuery=h3:contains("The spend profile has been approved")
    When the user navigates to the page              ${server}/project-setup-management/competition/6/status
#    Then the user should see the element             jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(5).status.ok TODO INFUND-5560
    [Teardown]  Logout as user


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

erase the rejection from the database
    Connect to Database    @{database}
    execute sql string    UPDATE `${database_name}`.`spend_profile` SET `approval`='null' WHERE `id`='1';
    execute sql string    UPDATE `${database_name}`.`spend_profile` SET `approval`='null' WHERE `id`='2';
    execute sql string    UPDATE `${database_name}`.`spend_profile` SET `approval`='null' WHERE `id`='3';
