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
...
...               INFUND-3766 As a project manager I want a summary page so I can review of all partners’ spend profiles that are marked as complete
...
...               INFUND-5194 As a partner I want to be see the project costs that were input during Finance Checks showing in the default spend profile so that I can begin to review my spend profile using the approved figures
...
...               INFUND-4819 As an academic partner I want to be given an alternative view of the Spend Profile in Project Setup so that I can submit information approriate to an academic organisation
...
...               INFUND-5609 PM can see partners' spend profiles before they are marked as complete, and can see buttons to edit and mark as complete
...
...               INFUND-5911 Internal users should not have access to external users' pages

Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot

*** Variables ***
@{database}       pymysql    ${database_name}    ${database_user}    ${database_password}    ${database_host}    ${database_port}
${la_fromage_overview}    ${server}/project-setup/project/4
${external_spendprofile_summary}  ${server}/project-setup/project/4/partner-organisation/21/spend-profile

*** Test Cases ***
Project Finance user generates the Spend Profile
    [Documentation]  INFUND-5194
    [Tags]    HappyPath
    [Setup]  log in as user                 project.finance1@innovateuk.test    Passw0rd
    Given the project finance user moves La Fromage into project setup if it isn't already
    When the user navigates to the page     ${server}/project-setup-management/project/4/finance-check
    Then the user should see the element    jQuery=.table-progress tr:nth-child(1) td:contains("approved")
    And the user should see the element     jQuery=.table-progress tr:nth-child(2) td:contains("approved")
    And the user should see the element     jQuery=.table-progress tr:nth-child(3) td:contains("approved")
    Then the user should see the element    jQuery=.button:contains("Generate Spend Profile")

Project Finance cancels the generation of the Spend Profile
    [Documentation]  INFUND-5194
    [Tags]
    When the user clicks the button/link           jQuery=.button:contains("Generate Spend Profile")
    Then the user should see the text in the page  This will generate a flat profile spend for all project partners.
    When the user clicks the button/link           jQuery=.button:contains("Cancel")
    Then the user should not see an error in the page

Project Finance generates the Spend Profile
    [Documentation]  INFUND-5194
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Generate Spend Profile")
    And the user clicks the button/link     jQuery=.button:contains("Generate spend profile")
    Then the user should see the element    jQuery=.success-alert p:contains("The finance checks have been approved and profiles generated.")


Lead partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]    HappyPath
    [Setup]    Log in as a different user    steve.smith@empire.com    Passw0rd
    Given the user clicks the button/link    link=00000016: Cheese is good
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK
    And the user should see the text in the page    Cheeseco - Spend profile


Lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page     October 2020
    And the user should see the text in the page     3 months

Calculations in the spend profile table
    [Documentation]    INFUND-3764
    [Tags]    HappyPath
    Given the user should see the element    jQuery=div.spend-profile-table
    Then element should contain    css=div.spend-profile-table tr:nth-child(1) td:nth-child(5)    £ 8,000      #Labour
    Then element should contain    css=div.spend-profile-table tr:nth-child(2) td:nth-child(5)    £ 2,000      #Overheads
    Then element should contain    css=div.spend-profile-table tr:nth-child(3) td:nth-child(5)    £ 10,000     #Materials
    Then element should contain    css=div.spend-profile-table tr:nth-child(4) td:nth-child(5)    £ 10,000     #Capital usage
    Then element should contain    css=div.spend-profile-table tr:nth-child(5) td:nth-child(5)    £ 10,000     #Subcontracting
    Then element should contain    css=div.spend-profile-table tr:nth-child(6) td:nth-child(5)    £ 10,000     #Travel & subsistence
    Then element should contain    css=div.spend-profile-table tr:nth-child(7) td:nth-child(5)    £ 10,000     #Other costs
     #${duration} is No of Months + 1, due to header
    And the sum of tds equals the total    div.spend-profile-table   1    5    8000    # Labour
    And the sum of tds equals the total    div.spend-profile-table   3    5    10000   # Materials
    And the sum of tds equals the total    div.spend-profile-table   5    5    10000   # Subcontracting
    And the sum of tds equals the total    div.spend-profile-table   6    5    10000   # Travel & subsistence
    And the sum of tds equals the total    div.spend-profile-table   7    5    10000   # Other Costs

Lead Partner can see Spend profile summary
    [Documentation]    INFUND-3971
    [Tags]     HappyPath
    Given the user navigates to the page            ${external_spendprofile_summary}
    And the user should see the text in the page    Project costs for financial year
    And the user moves focus to the element         jQuery=div.grid-container table
    Then the user sees the text in the element      jQuery=div.grid-container table tr:nth-child(1) td:nth-child(2)    £ 60,000

Lead partner can edit his spend profile with invalid values
    [Documentation]    INFUND-3765
    [Tags]     #HappyPath
    When the user clicks the button/link               jQuery=.button:contains("Edit spend profile")
    Then the text box should be editable               css=#row-1-0
    When the user enters text to a text field          css=#row-1-0    2899
    And the user moves focus to the element            css=#row-1-2
    Then the user should see the text in the page      Unable to submit spend profile.
    And the user should see the text in the page       Your total costs are higher than your eligible costs
    Then the field has value                           css=#row-total-1    £ 8,233
    And the user should see the element                jQuery=.cell-error #row-total-1
    When the user clicks the button/link               jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should see the text in the page      Your spend profile has total costs higher than eligible project costs,
    When the user clicks the button/link               jQuery=.button:contains("Edit spend profile")
    Then the user enters text to a text field          css=#row-1-0    2666
    And the user should not see the element            jQuery=.cell-error #row-total-1
    When the user enters text to a text field          css=#row-3-2    -55
    And the user moves focus to the element            css=#row-3-1
    Then the user should see the text in the page      This field should be 0 or higher
    When the user enters text to a text field          css=#row-1-2    35.25
    And the user moves focus to the element            css=#row-4-2
    Then the user should see the text in the page      This field should be a number
    When the user enters text to a text field          css=#row-3-2    3333
    And the user moves focus to the element            css=#row-3-1
    And the user should not see the text in the page   This field should be 0 or higher
    When the user enters text to a text field          css=#row-3-2    2667
    And the user moves focus to the element            css=#row-3-1
    Then the user should not see the text in the page  This field should be 0 or higher
    And the user should not see the element            jQuery=.cell-error #row-total-1
    Then the user clicks the button/link               jQuery=.button:contains("Save and return to spend profile overview")


Lead partner can edit his spend profile with valid values
    [Documentation]    INFUND-3765
    [Tags]     HappyPath
    Given the user navigates to the page                 ${external_spendprofile_summary}
    When the user clicks the button/link                 jQuery=.button:contains("Edit spend profile")
    And the user should not see the element              css=table a[type="number"]    # checking here that the table is not read-only
    Then the text box should be editable                 css=#row-1-0
    When the user enters text to a text field            css=#row-1-0    2000
    And the user moves focus to the element              css=#row-1-1
    Then the field has value                             css=#row-total-1    £ 7,334
    And the user should not see the text in the page     Unable to save spend profile
    When the user enters text to a text field            css=#row-6-1    0
    And the user moves focus to the element              css=#row-6-2
    Then the field has value                             css=#row-total-6    £ 6,667
    And the user should not see the text in the page     Unable to save spend profile
    Then the user clicks the button/link                 jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should not see the text in the page    Your spend profile has total costs higher than eligible project costs,

Lead Partners Spend profile summary gets updated when edited
    [Documentation]    INFUND-3971
    [Tags]     HappyPath
    Given the user navigates to the page             ${external_spendprofile_summary}
    Then the user should see the text in the page    Project costs for financial year
    And the user sees the text in the element        jQuery=div.grid-container table tr:nth-child(1) td:nth-child(2)    £ 56,001



Project Manager can see Spend Profile in Progress
    [Documentation]    done during refactoring, no ticket attached
    [Tags]
    [Setup]  Log in as a different user     worth.email.test+fundsuccess@gmail.com    Passw0rd
    Given the user navigates to the page    ${external_spendprofile_summary}
    Then the user should see the element    link=Cheeseco
    And the user should see the element     jQuery=.extra-margin-bottom tr:nth-child(1) td:nth-child(2):contains("In progress")


Lead partner marks spend profile as complete
    [Documentation]    INFUND-3765
    [Tags]    HappyPath
    [Setup]  Log in as a different user                         steve.smith@empire.com    Passw0rd
    Given the user navigates to the page            ${external_spendprofile_summary}
    When the user clicks the button/link            jQuery=.button:contains("Mark as complete")
    Then the user should see the text in the page   We have reviewed and confirmed your project costs
    And the user should not see the element         css=table a[type="number"]    # checking here that the table has become read-only


# TODO update the acc tests for Editing the Spend Profile by a non-lead partner  INFUND-5153

Non-lead partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]    HappyPath
    [Setup]    Log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user clicks the button/link    link=00000016: Cheese is good
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK
    And the user should see the text in the page    Ludlow - Spend profile

Non-lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page     October 2020
    And the user should see the text in the page     3 months

Non-lead partner marks Spend Profile as complete
    [Documentation]    INFUND-3767
    [Tags]    HappyPath
    When the user clicks the button/link            jQuery=.button:contains("Mark as complete")
    Then the user should see the text in the page   We have reviewed and confirmed your project costs
    And the user should not see the element         css=table a[type="number"]    # checking here that the table has become read-only


Project Manager doesn't have the option to submit spend profiles until all partners have marked as complete
    [Documentation]    INFUND-3767
    [Tags]
    [Setup]    log in as a different user    worth.email.test+fundsuccess@gmail.com    Passw0rd
    Given the user clicks the button/link    link=00000016: Cheese is good
    When the user clicks the button/link    link=Spend profile
    Then the user should not see the element    jQuery=.button:contains("Review and submit total project profile spend")


Academic partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]    HappyPath
    [Setup]    Log in as a different user    pete.tom@egg.com    Passw0rd
    Given the user clicks the button/link    link=00000016: Cheese is good
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK
    And the user should see the text in the page    EGGS - Spend profile

Academic partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page     October 2020
    And the user should see the text in the page     3 months

Academic partner can see the alternative academic view of the spend profile
    [Documentation]    INFUND-4819
    [Tags]
    Then the user should see the text in the page    Je-S category
    And the user should see the text in the page    Investigations
    And the user should see the text in the page    Estates costs


Academic partner marks Spend Profile as complete
    [Documentation]    INFUND-3767
    [Tags]    HappyPath
    When the user clicks the button/link            jQuery=.button:contains("Mark as complete")
    Then the user should see the text in the page   We have reviewed and confirmed your project costs
    And the user should not see the element         css=table a[type="number"]    # checking here that the table has become read-only


Project Manager can view partners' spend profiles
    [Documentation]    INFUND-3767, INFUND-3766, INFUND-5609
    [Tags]    HappyPath
    [Setup]    log in as a different user    worth.email.test+fundsuccess@gmail.com    Passw0rd
    Given the user clicks the button/link    link=00000016: Cheese is good
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
    Then the user clicks the button/link    link=Cheeseco
    And the user should see the text in the page   We have reviewed and confirmed your project costs
    And the user goes back to the previous page
    And the user clicks the button/link    link=Ludlow
    And the user should see the text in the page   We have reviewed and confirmed your project costs
    And the user should not see the element    jQuery=.button:contains("Edit")
    And the user should not see the element    jQuery=.button:contains("Mark as complete")
    And the user goes back to the previous page
    And the user clicks the button/link    link=EGGS
    And the user should see the text in the page   We have reviewed and confirmed your project costs
    And the user should not see the element    jQuery=.button:contains("Edit")
    And the user should not see the element    jQuery=.button:contains("Mark as complete")
    And the user goes back to the previous page
    When the user should see all spend profiles as complete
    Then the user should see the element    jQuery=a:contains("Review and submit total project")
    # Note that the above button cannot be cought with its complete text, due to a break

Partners are not able to see the spend profile summary page
    [Documentation]  INFUND-3766
    [Tags]
    Given log in as a different user    steve.smith@empire.com    Passw0rd
    And the user navigates to the page  ${external_spendprofile_summary}
    Then the user should see the text in the page  Cheeseco - Spend profile
    Given log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    And the user navigates to the page and gets a custom error message    ${external_spendprofile_summary}    You do not have the necessary permissions for your request
    Given log in as a different user    pete.tom@egg.com    Passw0rd
    And the user navigates to the page and gets a custom error message    ${external_spendprofile_summary}    You do not have the necessary permissions for your request


Project Manager can view combined spend profile
    [Documentation]    INFUND-3767
    [Tags]    HappyPath
    [Setup]    log in as a different user    worth.email.test+fundsuccess@gmail.com    Passw0rd
    Given the user navigates to the page    ${external_spendprofile_summary}
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
    [Tags]    HappyPath
    [Setup]    log in as a different user    worth.email.test+fundsuccess@gmail.com    Passw0rd
    Given the user navigates to the page    ${server}/project-setup/project/4/spend-profile/total
    When the user clicks the button/link    jQuery=.button:contains("Submit project spend profile")
    And the user should see the element    jQuery=.button:contains("Cancel")
    When the user clicks the button/link    css=div.modal-confirm-spend-profile-totals .button.large

PM's Spend profile Summary page gets updated after submit
    [Documentation]    INFUND-3766
    [Tags]
    Given the user navigates to the page     ${external_spendprofile_summary}
    Then the user should see the element     jQuery=.success-alert.extra-margin-bottom p:contains("All project spend profiles have been submitted to Innovate UK")
    And the user should see the element      link=Total project profile spend
    And the user should not see the element  jQuery=.button:contains("Submit project spend profile")

Partners can see the Spend Profile section completed
    [Documentation]  INFUND-3767,INFUND-3766
    [Tags]
    Given Log in as a different user       worth.email.test+fundsuccess@gmail.com    Passw0rd
    And the user clicks the button/link    link=00000016: Cheese is good
    Then the user should see the element   jQuery=li.complete:nth-of-type(6)
    Given Log in as a different user       steve.smith@empire.com    Passw0rd
    And the user clicks the button/link    link=00000016: Cheese is good
    Then the user should see the element   jQuery=li.complete:nth-of-type(6)
    Given Log in as a different user       jessica.doe@ludlow.co.uk    Passw0rd
    And the user clicks the button/link    link=00000016: Cheese is good
    Then the user should see the element   jQuery=li.complete:nth-of-type(6)
    Given Log in as a different user       pete.tom@egg.com    Passw0rd
    And the user clicks the button/link    link=00000016: Cheese is good
    Then the user should see the element   jQuery=li.complete:nth-of-type(6)

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049
    [Tags]    Experian    #HappyPath
    [Setup]  Log in as a different user     john.doe@innovateuk.test    Passw0rd
    When the user navigates to the page     ${server}/project-setup-management/competition/3/status
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.ok
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(2).status.action
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(3).status.waiting
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(4).status.ok
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(5).status.action

Project Finance is able to see Spend Profile approval page
    [Documentation]    INFUND-2638, INFUND-5617
    [Tags]    HappyPath
    [Setup]  Log in as a different user                     project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page                    ${server}/project-setup-management/competition/3/status
    And the user clicks the button/link                     jQuery=td:nth-child(6) a
    Then the user should be redirected to the correct page  ${server}/project-setup-management/project/4/spend-profile/approval
    And the user should see the element                     jQuery=#content div.grid-row div.column-third.alignright.extra-margin h2:contains("Spend profile")
    And the user should not see the element                 jQuery=h2:contains("The spend profile has been approved")
    And the user should not see the element                 jQuery=h2:contains("The spend profile has been rejected")
    When the user should see the text in the page           Innovation Lead
    Then the user should see the text in the page           Robin Wilson
    When the user should see the text in the page           Project spend profile
    Then the user clicks the button/link                    link=Cheeseco-spend-profile.csv
    And the user clicks the button/link                     link=Ludlow-spend-profile.csv
    And the user clicks the button/link                     link=EGGS-spend-profile.csv
    When the user should see the text in the page           Approved by Innovation Lead
    Then the element should be disabled                     jQuery=#accept-profile
    When the user selects the checkbox                      jQuery=#approvedByLeadTechnologist
    Then the user should see the element                    jQuery=#accept-profile
    And the user should see the element                     jQuery=#content .button.button.button-warning.large:contains("Reject")

Comp Admin is able to see Spend Profile approval page
    [Documentation]    INFUND-2638, INFUND-5617
    [Tags]
    [Setup]  Log in as a different user              john.doe@innovateuk.test    Passw0rd
    Given the user navigates to the page             ${server}/project-setup-management/project/4/spend-profile/approval
    Then the user should see the element             jQuery=#content div.grid-row div.column-third.alignright.extra-margin h2:contains("Spend profile")
    And the element should be disabled               jQuery=#accept-profile
    And the user should see the element              jQuery=#content .button.button.button-warning.large:contains("Reject")
    When the user clicks the button/link             jQuery=#content .button.button.button-warning.large:contains("Reject")
    Then the user should see the text in the page    Before taking this action please contact the project manager
    When the user clicks the button/link             jQuery=.modal-reject-profile button:contains("Cancel")
    Then the user should not see an error in the page
    When the user selects the checkbox               jQuery=#approvedByLeadTechnologist
    Then the user should see the element             jQuery=#accept-profile
    When the user clicks the button/link             jQuery=button:contains("Approved")
    Then the user should see the text in the page    Approved by Innovation Lead
    When the user clicks the button/link             jQuery=.modal-accept-profile button:contains("Cancel")
    Then the user should not see an error in the page

Project Finance is able to Reject Spend Profile
    [Documentation]    INFUND-2638, INFUND-5617
    [Tags]
    [Setup]  Log in as a different user            project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page           ${server}/project-setup-management/project/4/spend-profile/approval
    And the user should see the element            jQuery=#content .button.button.button-warning.large:contains("Reject")
    When the user clicks the button/link           jQuery=#content .button.button.button-warning.large:contains("Reject")
    Then the user should see the text in the page  Before taking this action please contact the project manager
    When the user clicks the button/link           jQuery=.modal-reject-profile button:contains("Cancel")
    Then the user should not see an error in the page
    #    When the user clicks the button/link           jQuery=#content .button.button.button-warning.large:contains("Reject")
    #    And the user clicks the button/link            jQuery=.modal-reject-profile button:contains('Reject')
    #    Then the user should see the element           jQuery=h3:contains("The spend profile has been rejected")
    # The above lines are passing, but they are disabled so that the Sp Prof can be Approved. This will be changed with upcoming functionality.

Project Finance is able to Approve Spend Profile
    [Documentation]    INFUND-2638, INFUND-5617
    [Tags]    HappyPath
    Given the user navigates to the page             ${server}/project-setup-management/project/4/spend-profile/approval
    When the user selects the checkbox               jQuery=#approvedByLeadTechnologist
    Then the user should see the element             jQuery=button:contains("Approved")
    When the user clicks the button/link             jQuery=button:contains("Approved")
    Then the user should see the text in the page    Approved by Innovation Lead
    When the user clicks the button/link             jQuery=.modal-accept-profile button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link             jQuery=button:contains("Approved")
    And the user clicks the button/link              jQuery=.modal-accept-profile button:contains("Accept documents")
    Then the user should not see the element         jQuery=h3:contains("The spend profile has been approved")
    When the user navigates to the page              ${server}/project-setup-management/competition/3/status
    Then the user should see the element             jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(5).status.ok

Project finance user cannot access internal users' spend profile page
    [Documentation]    INFUND-5911
    When the user navigates to the page and gets a custom error message  ${server}/project-setup/project/4/partner-organisation/21/spend-profile    You do not have the necessary permissions for your request


*** Keywords ***
the project finance user moves La Fromage into project setup if it isn't already
    log in as a different user    project.finance1@innovateuk.test    Passw0rd
    the user navigates to the page    ${server}/management/dashboard/projectSetup
    ${update_comp}  ${value}=  run keyword and ignore error    the user should not see the text in the page  La Fromage
    run keyword if    '${update_comp}' == 'PASS'    the project finance user moves La Fromage into project setup

the project finance user moves La Fromage into project setup
    the user navigates to the page    ${server}/management/competition/3
    the user selects the option from the drop-down menu    Yes    id=fund16
    the user selects the option from the drop-down menu    No    id=fund17
    the user clicks the button/link    jQuery=.button:contains("Notify applicants")
    the user clicks the button/link    name=publish
    the user should see the text in the page    Assessor Feedback
    the user can see the option to upload a file on the page    ${server}/management/competition/3/application/16
    the user uploads the file    ${valid_pdf}
    the user can see the option to upload a file on the page    ${server}/management/competition/3/application/17
    the user uploads the file    ${valid_pdf}
    the user navigates to the page    ${server}/management/competition/3
    the user clicks the button/link    jQuery=.button:contains("Publish assessor feedback")
    the user clicks the button/link    name=publish


the user uploads the file
    [Arguments]    ${upload_filename}
    Choose File    id=assessorFeedback    ${UPLOAD_FOLDER}/${upload_filename}
    Sleep    500ms


the users fill out project details
    When Log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Ludlow
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    When Log in as a different user    pete.tom@egg.com    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=EGGS
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    When Log in as a different user    steve.smith@empire.com    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Cheeseco
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user clicks the button/link    link=Project manager
    And the user selects the radio button    projectManager    projectManager1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user clicks the button/link    link=Project address
    And the user selects the radio button    addressType    REGISTERED
    And the user clicks the button/link    jQuery=.button:contains("Save")
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=button:contains("Submit")

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

other partners spend profiles get submitted
    [Documentation]  currently not in use
    Connect to Database    @{database}
    execute sql string     UPDATE `${database_name}`.`spend_profile` SET `marked_as_complete`='1' WHERE `id`='2';
    execute sql string     UPDATE `${database_name}`.`spend_profile` SET `marked_as_complete`='1' WHERE `id`='3';

the user should see all spend profiles as complete
    the user should see the element     jQuery=.extra-margin-bottom tr:nth-child(1) td:nth-child(2):contains("Complete")
    the user should see the element     jQuery=.extra-margin-bottom tr:nth-child(2) td:nth-child(2):contains("Complete")
    the user should see the element     jQuery=.extra-margin-bottom tr:nth-child(3) td:nth-child(2):contains("Complete")
