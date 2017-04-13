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
...
...               INFUND-3973 As a Project Finance team member I want to be able to export submitted spend profile tables so that these may be distributed offline to Lead Technologists and Monitoring Officers
...
...               INFUND-5846 As a partner in an acedemic organisation I want to be able to edit my Spend profile so I can prepare an updated profile for my organisation before submission to the Project Manager
...
...               INFUND-5441 As a Project Finance team member I want to be able to export submitted spend profile tables from academic organisations so that these may be distributed offline to Lead Technologists and Monitoring Officers
...
...               INFUND-6046 Spend Profile should have a link when Done
...
...               INFUND-6350 As a lead partner I want to be able to return edit rights to a non-lead partner so that they can further amend their Spend Profile if requested by the lead
...
...               INFUND-6146 Saving blank fields on the spend profile results in an internal server error (null pointer exception)
...
...               INFUND-6225 External user status indicator for spend profile should show as 'waiting' once submitted
...
...               INFUND-6226 Comp admin user (non project finance) not able to view the spend profile page
...
...               INFUND-6881 Non-lead External User should see Green Check once he submits SP
...
...               INFUND-7119 GOL status for Internal user
...
...               INFUND-6977 As a lead partner I want to be given back edit rights to the Spend Profile so that I can manage further edits if they have been rejected by Innovate UK
...
...               INFUND-6907 Internal server error if tried to save SP with a validation error in it
...
...               INFUND-6852 When partner submits SP, he should get a popup Submit-Cancel, before is Submitted
...
...               INFUND-6801 Show text instead of Id - Spend Profile - Error Summary
...
...               INFUND-6138 Partners should be able to see the correct status of SP so to take action
...
...               INFUND-7409 PM is redirected to the wrong screen when saving their spend profile
...
...               INFUND-5899 As an internal user I want to be able to use the breadcrumb navigation consistently throughout Project Setup so I can return to the previous page as appropriate
...
...               INFUND-5549 As a Competitions team member I want to see the Innovation Lead  listed in the Spend profile approval page so that I can confirm who is required to approve the Spend Profiles
...
...               INFUND-6148 Negative numbers on spend profile table generation not disallowed by rounding logic
...
...               INFUND-7422 On rejection non-lead partners should still see a tick instead of an hourglass, until edit rights have been returned to them
...
...               INFUND-7685 Broken link on spend profile page

Suite Setup       all previous sections of the project are completed
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot
Resource          PS_Variables.robot

*** Variables ***
${project_overview}    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}
${external_spendprofile_summary}    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner-organisation/${Katz_Id}/spend-profile
${project_duration}    36

*** Test Cases ***
Project Finance user generates the Spend Profile
    [Documentation]    INFUND-5194
    [Tags]    HappyPath
    [Setup]    log in as user               &{internal_finance_credentials}
    Given the user navigates to the page    ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/finance-check
    Then the user should see the element    jQuery=a.eligibility-0:contains("Approved")
    And the user should see the element     jQuery=a.eligibility-1:contains("Approved")
    And the user should see the element     jQuery=a.eligibility-2:contains("Approved")
    Then the user should see the element    jQuery=.generate-spend-profile-main-button


Project Finance cancels the generation of the Spend Profile
    [Documentation]    INFUND-5194
    [Tags]
    When the user clicks the button/link    jQuery=.generate-spend-profile-main-button
    Then the user should see the text in the page    This will generate a flat spend profile for all project partners.
    When the user clicks the button/link    jQuery=button:contains("Cancel")

Project Finance generates the Spend Profile
    [Documentation]    INFUND-5194, INFUND-5987
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.generate-spend-profile-main-button
    And the user clicks the button/link     jQuery=#generate-spend-profile-modal-button
    Then the user should see the element    jQuery=.success-alert p:contains("The finance checks have been approved and profiles generated.")
    When the user navigates to the page     ${server}/project-setup-management/competition/${PS_SP_Competition_Id}/status
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(4).ok


Lead partner can view spend profile page
    [Documentation]    INFUND-3970, INFUND-6138, INFUND-5899, INFUND-7685
    [Tags]    HappyPath
    [Setup]    Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    When the user clicks the button/link             link=status of my partners
    Then the user should see the text in the page    Project team status
    And the user should see the element              jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    When the user clicks the button/link             link=Project setup status
    Then the user should see the element      jQuery=li.require-action:nth-child(6)
    When the user clicks the button/link     link=Spend profile
    And the user should not see the element    link=Total project profile spend
    And the user clicks the button/link      link=${Katz_Name}
    Then the user should not see an error in the page
    And the user should see the text in the page    We have reviewed and confirmed your project costs.
    And the user should see the text in the page    ${Katz_Name} - Spend profile
    # And the user clicks the button/link    link=Spend profile overview    # TODO please enable this line and delete the following one when working on INFUND-8132
    And the user navigates to the page    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner-organisation/${Katz_Id}/spend-profile
    And the user should see the text in the page    This overview shows the spend profile status of each organisation in your project.
    [Teardown]    the user goes back to the previous page

Lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page     June 2017
    And the user should see the text in the page     ${project_duration} months

Calculations in the spend profile table
    [Documentation]    INFUND-3764, INFUND-6148
    [Tags]    HappyPath
    Given the user should see the element    jQuery=div.spend-profile-table
    Then element should contain    css=.spend-profile-table tbody tr:nth-child(1) td:nth-last-child(2)    £ 4,622     #Labour
    Then element should contain    css=.spend-profile-table tbody tr:nth-child(2) td:nth-last-child(2)    £ 0     #Overheads
    Then element should contain    css=.spend-profile-table tbody tr:nth-child(3) td:nth-last-child(2)    £ 150,300    #Materials
    Then element should contain    css=.spend-profile-table tbody tr:nth-child(4) td:nth-last-child(2)    £ 828    #Capital usage
    Then element should contain    css=.spend-profile-table tbody tr:nth-child(5) td:nth-last-child(2)    £ 135,000    #Subcontracting
    Then element should contain    css=.spend-profile-table tbody tr:nth-child(6) td:nth-last-child(2)    £ 8,955    #Travel & subsistence
    Then element should contain    css=.spend-profile-table tbody tr:nth-child(7) td:nth-last-child(2)    £ 1,650    #Other costs
    #${duration} is No of Months + 1, due to header
    And the sum of tds equals the total    div.spend-profile-table    1    38    4622     # Labour
    And the sum of tds equals the total    div.spend-profile-table    3    38    150300    # Materials
    And the sum of tds equals the total    div.spend-profile-table    5    38    135000    # Subcontracting
    And the sum of tds equals the total    div.spend-profile-table    6    38    8955    # Travel & subsistence
    And the sum of tds equals the total    div.spend-profile-table    7    38    1650    # Other costs

Lead Partner can see Spend profile summary
    [Documentation]    INFUND-3971, INFUND-6148
    [Tags]    Failing
    #TODO this test case needs to be moved, to another project where the PM != Lead partner.
    Given the user navigates to the page            ${external_spendprofile_summary}/review
    And the user should see the text in the page    Project costs for financial year
    And the user moves focus to the element         jQuery=.grid-container table
    Then the user sees the text in the element      jQuery=.grid-container table tr:nth-child(1) td:nth-child(2)    £ 10,957

Lead partner can edit his spend profile with invalid values
    [Documentation]    INFUND-3765, INFUND-6907, INFUND-6801, INFUND-7409, INFUND-6148
    [Tags]
    Given log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    And the user navigates to the page            ${external_spendprofile_summary}/review
    # TODO please delete the above two lines when INFUND-8138 is completed
    When the user clicks the button/link               jQuery=.button:contains("Edit spend profile")
    Then the user should not see the text in the element  css=#content > form   -
    And the text box should be editable               css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(1) input  # Labour-June17
    When the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(1) input   520
    And the user moves focus to the element            css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(3) input
    Then the user should see the text in the page      Unable to submit spend profile.
    And the user should see the text in the page       Your total costs are higher than your eligible costs
    Then the field has value                           css=.spend-profile-table tbody .form-group-row:nth-child(1) input[id^='row-total-']    £ 5,000
    And the user should see the element                jQuery=.form-group-row:nth-child(1) .cell-error input[id^='row-total-']
    And the user clicks the button/link                jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should see the text in the page      You cannot submit your spend profile. Your total costs are higher than the eligible project costs.
    And the user should see the element                jQuery=.error-summary-list li:contains("Labour")
    When the user clicks the button/link               jQuery=.button:contains("Edit spend profile")
    Then the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(1) input    142
    And the user should not see the element            jQuery=.form-group-row:nth-child(1) .cell-error input[id^='row-total-']
    When the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(2) td:nth-of-type(3) input    -55  # Materials-Aug17
    And the user moves focus to the element            css=.spend-profile-table tbody .form-group-row:nth-child(2) td:nth-of-type(2) input
    Then the user should see the element               jQuery=.error-summary-list li:contains("This field should be 0 or higher")
    When the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(2) td:nth-of-type(3) input    35.25
    And the user moves focus to the element            css=.spend-profile-table tbody .form-group-row:nth-child(2) td:nth-of-type(3) input
    Then the user should see the text in the page      This field can only accept whole numbers
    When the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(3) input    abcd
    And the user moves focus to the element            css=.spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(3) input
    Then the user should see the text in the page      Unable to submit spend profile
    When the user clicks the button/link               jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should not see the text in the page  internal server error
    When the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(3) input    200
    And the user moves focus to the element            css=.spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(2) input
    And the user should not see the element            jQuery=.error-summary-list li:contains("This field should be 0 or higher")
    When the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(3) input    4175
    And the user moves focus to the element            css=.spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(2) input
    Then the user should not see the element           jQuery=.error-summary-list li:contains("This field should be 0 or higher")
    And the user should not see the element            jQuery=.form-group-row:nth-child(1) .cell-error input[id^='row-total-']
    Then the user clicks the button/link               jQuery=.button:contains("Save and return to spend profile overview")


Lead partner can submit empty cells and this is handled gracefully
    [Documentation]    INFUND-6146
    When the user enters text to a text field    css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(1) input    ${empty}
    And the user clicks the button/link    jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should not see an error in the page
    [Teardown]    the user enters text to a text field    css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(1) input    142

Lead partner can edit his spend profile with valid values
    [Documentation]    INFUND-3765
    [Tags]    HappyPath
    Given the user navigates to the page                ${external_spendprofile_summary}/review
    When the user clicks the button/link                jQuery=.button:contains("Edit spend profile")
    And the user should not see the element             css=table a[type="number"]    # checking here that the table is not read-only
    Then the text box should be editable                css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(1) input  # Labour
    When the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(1) input    140
    And the user moves focus to the element             css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(2) input
    Then the field has value                            css=.spend-profile-table tbody .form-group-row:nth-child(1) input[id^='row-total-']    £ 4,620
    And the user should not see the text in the page    Unable to save spend profile
    When the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(5) td:nth-of-type(2) input    0  # Subcontracting
    And the user moves focus to the element             css=.spend-profile-table tbody .form-group-row:nth-child(5) td:nth-of-type(3) input
    Then the field has value                            css=.spend-profile-table tbody .form-group-row:nth-child(5) input[id^='row-total-']    £ 131,250
    And the user should not see the text in the page    Unable to save spend profile
    Then the user clicks the button/link                jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should not see the text in the page   You cannot submit your spend profile. Your total costs are higher than the eligible project costs.

Lead Partners Spend profile summary gets updated when edited
    [Documentation]    INFUND-3971, INFUND-6148
    [Tags]    HappyPath
    Given the user navigates to the page           ${external_spendprofile_summary}/review
    Then the user should see the text in the page  Project costs for financial year
    And the user sees the text in the element      jQuery=.grid-container table tr:nth-child(1) td:nth-child(2)    £ 80,009

Project Manager can see Spend Profile in Progress
    [Documentation]    done during refactoring, no ticket attached
    [Tags]
    [Setup]    Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user navigates to the page     ${external_spendprofile_summary}
    Then the user should see the element     link=${PS_SP_APPLICATION_LEAD_ORGANISATION_NAME}
    And the user should see the element      jQuery=.extra-margin-bottom tr:nth-child(1) td:nth-child(2):contains("In progress")

Lead partner marks spend profile as complete
    [Documentation]    INFUND-3765, INFUND-6138
    [Tags]    HappyPath
    [Setup]    Log in as a different user      ${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}    ${short_password}
    Given the user navigates to the page       ${external_spendprofile_summary}/review
    When the user clicks the button/link       jQuery=.button:contains("Mark as complete")
    Then the user should not see the element   jQuery=.success-alert p:contains("Your spend profile is marked as complete. You can still edit this page.")
    And the user should not see the element    css=table a[type="number"]    # checking here that the table has become read-only
    When the user clicks the button/link            link=Project setup status
    And the user clicks the button/link             link=status of my partners
    Then the user should see the text in the page    Project team status
    And the user should see the element              jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    When the user clicks the button/link             link=Project setup status
    Then the user should see the element             jQuery=li.require-action:nth-child(6)

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Tags]    HappyPath
    [Setup]    Log in as a different user           ${PS_SP_APPLICATION_PARTNER_EMAIL}    ${short_password}
    Given the user clicks the button/link           link=${PS_SP_APPLICATION_HEADER}
    And the user should see the element             jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    Then the user should see the element            link = Monitoring Officer
    And the user should see the element             link = Bank details
    And the user should see the element         link = Finance checks
    And the user should see the element             link= Spend profile
    And the user should not see the element         link = Grant offer letter


Non-lead partner can view spend profile page
    [Documentation]    INFUND-3970, INFUND-6138, INFUND-5899
    [Tags]    HappyPath
    [Setup]    Log in as a different user           ${PS_SP_APPLICATION_PARTNER_EMAIL}    ${short_password}
    Given the user clicks the button/link           link=${PS_SP_APPLICATION_HEADER}
    When the user clicks the button/link             link=status of my partners
    Then the user should see the text in the page    Project team status
    And the user should see the element              jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    When the user clicks the button/link             link=Project setup status
    Then the user should see the element             jQuery=li.require-action:nth-child(6)
    When the user clicks the button/link             link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    We have reviewed and confirmed your project costs.
    And the user should see the text in the page    ${Meembee_Name} - Spend profile
    And the user clicks the button/link    link=Project setup status
    And the user should see the text in the page    You need to complete the following steps before you can start your project.
    [Teardown]    the user goes back to the previous page

Non-lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page     June 2017
    And the user should see the text in the page     ${project_duration} months

Industrial partner can choose cancel on the dialogue
    [Documentation]    INFUND-6852
    When the user clicks the button/link    jQuery=a:contains("Submit to lead partner")
    And the user clicks the button/link     jQuery=button:contains("Cancel")
    Then the user should see the element    jQuery=a:contains("Submit to lead partner")

Non-lead partner marks Spend Profile as complete
    [Documentation]    INFUND-3767
    [Tags]    HappyPath
    When the user clicks the button/link             jQuery=a:contains("Submit to lead partner")
    And the user clicks the button/link             jQuery=.button:contains("Submit")
    Then the user should see the text in the page    We have reviewed and confirmed your project costs
    And the user should not see the element          css=table a[type="number"]    # checking here that the table has become read-only

Status updates for industrial user after spend profile submission
    [Documentation]    INFUND-6881
    When the user navigates to the page    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}
    Then the user should see the element    jQuery=ul li.complete:nth-child(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(5)

Project Manager doesn't have the option to send spend profiles until all partners have marked as complete
    [Documentation]    INFUND-3767, INFUND-6138
    [Tags]
    [Setup]    log in as a different user       ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user clicks the button/link       link=${PS_SP_APPLICATION_HEADER}
    And the user clicks the button/link             link=status of my partners
    Then the user should see the text in the page    Project team status
    And the user should see the element              jQuery=#table-project-status tr:nth-of-type(3) td.status.action:nth-of-type(5)
    When the user clicks the button/link             link=Project setup status
    Then the user should see the element             jQuery=li.require-action:nth-child(6)
    When the user clicks the button/link        link=Spend profile
    Then the user should not see the element    jQuery=.button:contains("Review spend profiles")
    #The complete name of the button is anyways not selected. Please use the short version of it.

Academic partner can view spend profile page
    [Documentation]    INFUND-3970, INFUND-5899
    [Tags]    HappyPath
    [Setup]    Log in as a different user           ${PS_SP_APPLICATION_ACADEMIC_EMAIL}    ${short_password}
    Given the user clicks the button/link           link=${PS_SP_APPLICATION_HEADER}
    When the user clicks the button/link            link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    We have reviewed and confirmed your project costs.
    And the user should see the text in the page    ${Zooveo_Name} - Spend profile
    And the user clicks the button/link    link=Project setup status
    And the user should see the text in the page    You need to complete the following steps before you can start your project.
    [Teardown]    the user goes back to the previous page

Academic partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page     June 2017
    And the user should see the text in the page     ${project_duration} months

Academic partner can see the alternative academic view of the spend profile
    [Documentation]    INFUND-4819
    [Tags]
    Then the user should see the text in the page    Je-S category
    And the user should see the text in the page     Investigations
    And the user should see the text in the page     Estates costs

Academic partner spend profile server side validations
    [Documentation]    INFUND-5846
    [Tags]
    Given the user clicks the button/link            jQuery=.button:contains("Edit spend profile")
    When the user enters text to a text field        css=.spend-profile-table tbody .form-group-row:nth-child(5) td:nth-of-type(1) input    -1    # Directly incurredStaff
    And the user enters text to a text field         css=.spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(3) input    3306  # Travel and subsistence
    And the user moves focus to the element          css=.spend-profile-table tbody .form-group-row:nth-child(7) td:nth-of-type(6) input
    And the user clicks the button/link              jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should see the text in the page    Your total costs are higher than your eligible costs.
    And the user should see the text in the page     This field should be 0 or higher.

Academic partner spend profile client side validations
    [Documentation]    INFUND-5846
    [Tags]
    When the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(1) input    3  # Staff
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(2) td:nth-of-type(1) input    1  # Travel
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(1) input    1  # Other - Directly incurred
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(5) td:nth-of-type(1) input    2  # Estates
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(1) input    0  # Other - Directly allocated
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(9) td:nth-of-type(1) input    0  # Other - Exceptions
    And the user moves focus to the element            link=Project setup status
    Then the user should not see the text in the page  This field should be 0 or higher
    When the user makes all values zeros               2    ${project_duration}  # Travel
    Then the user makes all values zeros               3    ${project_duration}  # Other - Directly incurred
    And the user makes all values zeros                5    ${project_duration}  # Estates
    When the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(2) input   0  # Other - Directly allocated
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(3) input    0  # Other - Directly allocated
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(9) td:nth-of-type(2) input    0  # Other - Exceptions
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(9) td:nth-of-type(3) input    0  # Other - Exceptions
    And the user should not see the text in the page   Your total costs are higher than your eligible costs
    #TODO Replace keyword -the user makes all values zeros- ticket: INFUND-6851

Academic partner edits spend profile and this updates on the table
    [Documentation]    INFUND-5846
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should see the element    jQuery=.button:contains("Edit spend profile")
    And element should contain    css=.spend-profile-table tbody tr:nth-of-type(1) td:nth-of-type(1)    3
    And element should contain    css=.spend-profile-table tbody tr:nth-of-type(2) td:nth-of-type(3)    0

Academic partner can choose cancel on the dialogue
    [Documentation]    INFUND-6852
    When the user clicks the button/link    jQuery=a:contains("Submit to lead partner")
    And the user clicks the button/link     jQuery=button:contains("Cancel")
    Then the user should see the element    jQuery=a:contains("Submit to lead partner")

Academic partner marks Spend Profile as complete
    [Documentation]    INFUND-3767
    [Tags]    HappyPath
    When the user clicks the button/link           jQuery=a:contains("Submit to lead partner")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    Then the user should see the text in the page  We have reviewed and confirmed your project costs
    And the user should not see the element        css=table a[type="number"]    # checking here that the table has become read-only

Status updates for academic user after spend profile submission
    [Documentation]    INFUND-6881
    When the user navigates to the page    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}
    Then the user should see the element    jQuery=ul li.complete:nth-child(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(5)


Project Manager can view partners' spend profiles
    [Documentation]    INFUND-3767, INFUND-3766, INFUND-5609
    [Tags]    HappyPath
    [Setup]    log in as a different user           ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user clicks the button/link           link=${PS_SP_APPLICATION_HEADER}
    When the user clicks the button/link            link=Spend profile
    Then the user should not see an error in the page
    Then the user clicks the button/link            link=${PS_SP_APPLICATION_LEAD_ORGANISATION_NAME}
    And the user should see the text in the page    We have reviewed and confirmed your project costs
    And the user goes back to the previous page
    And the user clicks the button/link             link=${Meembee_Name}
    And the user should see the text in the page    We have reviewed and confirmed your project costs
    And the user should not see the element         jQuery=.button:contains("Edit")
    And the user should not see the element         jQuery=.button:contains("Mark as complete")
    And the user goes back to the previous page
    And the user clicks the button/link             link=${Zooveo_Name}
    And the user should see the text in the page    We have reviewed and confirmed your project costs
    And the user should not see the element         jQuery=.button:contains("Edit")
    And the user should not see the element         jQuery=.button:contains("Mark as complete")
    And the user goes back to the previous page
    When the user should see all spend profiles as complete
    Then the user should see the element            jQuery=.button:contains("Review and send total project spend profile")

Partners are not able to see the spend profile summary page
    [Documentation]    INFUND-3766
    [Tags]
    Given log in as a different user               ${PS_SP_APPLICATION_PARTNER_EMAIL}  ${short_password}
    And the user navigates to the page and gets a custom error message  ${external_spendprofile_summary}    You do not have the necessary permissions for your request
    Given log in as a different user               ${PS_SP_APPLICATION_ACADEMIC_EMAIL}    ${short_password}
    And the user navigates to the page and gets a custom error message  ${external_spendprofile_summary}    You do not have the necessary permissions for your request

Project Manager can view combined spend profile
    [Documentation]    INFUND-3767
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user navigates to the page     ${external_spendprofile_summary}
    When the user clicks the button/link     jQuery=.button:contains("Review and send total project spend profile")
    Then the user should see the text in the page    This is the spend profile for your project.
    And the user should see the text in the page     Your submitted spend profile will be used as the base for your project spend over the following financial years.

Project Manager can choose cancel on the dialogue
    [Documentation]    INFUND-3767
    When the user clicks the button/link    jQuery=.button:contains("Send project spend profile")
    And the user clicks the button/link     jQuery=button:contains("Cancel")
    Then the user should see the element    jQuery=.button:contains("Send project spend profile")

Project Manager can see the button Allow edits
    [Documentation]    INFUND-6350
    [Tags]
    Given the user navigates to the page    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner-organisation/${Katz_Id}/spend-profile
    Then the user should see the element    jQuery=.extra-margin-bottom tr:nth-child(1) td:nth-child(2):contains("Complete")
    And the user should see the element     jQuery=.extra-margin-bottom tr:nth-child(2) td:nth-child(2):contains("Complete")
    Then the user clicks the button/link    link=${Meembee_Name}
    And the user should see the element     jQuery=.button:contains("Allow edits")

Other partners cannot enable edit-ability by themselves
    [Documentation]    INFUND-6350
    [Tags]
    [Setup]  log in as a different user       ${PS_SP_APPLICATION_PARTNER_EMAIL}  ${short_password}
    When the user navigates to the page       ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner-organisation/${Meembee_Id}/spend-profile/review
    Then the user should not see the element  jQuery=.button:contains("Allow edits")

PM can return edit rights to partners
    [Documentation]    INFUND-6350
    [Tags]    HappyPath
    [Setup]  log in as a different user      ${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}  ${short_password}
    Given the user navigates to the page     ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner-organisation/${Meembee_Id}/spend-profile/review
    When the user clicks the button/link     jQuery=.button:contains("Allow edits")
    And the user clicks the button/link      jQuery=.button:contains("Allow partner to edit")
    Then the user navigates to the page      ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner-organisation/${Katz_Id}/spend-profile
    And the user should see the element      jQuery=.extra-margin-bottom tr:nth-child(2) td:nth-child(2):contains("In progress")

Partner can receive edit rights to his SP
    [Documentation]    INFUND-6350
    [Tags]    HappyPath
    [Setup]  log in as a different user     ${PS_SP_APPLICATION_PARTNER_EMAIL}  ${short_password}
    Given the user navigates to the page    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}
    Then the user should see the element    jQuery=li.require-action:nth-child(6)
    When the user clicks the button/link    link=Spend profile
    Then the user should see the element    jQuery=.button:contains("Edit spend profile")
    When the user clicks the button/link    jQuery=a:contains("Submit to lead partner")
    And the user clicks the button/link    jQuery=.button:contains("Submit")

Project Manager can send the project's spend profiles
    [Documentation]    INFUND-3767
    [Tags]    HappyPath
    [Setup]    log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user navigates to the page     ${external_spendprofile_summary}
    When the user clicks the button/link     jQuery=.button:contains("Review and send total project spend profile")
    Then the user clicks the button/link     jQuery=.button:contains("Send project spend profile")
    And the user should see the element      jQuery=button:contains("Cancel")
    When the user clicks the button/link     jQuery=.modal-confirm-spend-profile-totals .button[value="Send"]

PM's Spend profile Summary page gets updated after submit
    [Documentation]    INFUND-3766
    [Tags]
    Given the user navigates to the page     ${external_spendprofile_summary}
    Then the user should see the element     jQuery=.success-alert.extra-margin-bottom p:contains("All project spend profiles have been sent to Innovate UK.")
    And the user should not see the element  jQuery=.button:contains("Send project spend profile")

Status updates after spend profile submitted
    [Documentation]    INFUND-6225
    Given the user navigates to the page    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}
    When the user clicks the button/link    link=status of my partners
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(5)


Partners can see the Spend Profile section completed
    [Documentation]    INFUND-3767,INFUND-3766
    [Tags]
    Given Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    And the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    Then the user should see the element    jQuery=li.waiting:nth-of-type(6)
    Given Log in as a different user    ${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}    ${short_password}
    And the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    Then the user should see the element    jQuery=li.waiting:nth-of-type(6)
    Given Log in as a different user    ${PS_SP_APPLICATION_PARTNER_EMAIL}    ${short_password}
    And the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    Then the user should see the element    jQuery=li.complete:nth-of-type(6)
    Given Log in as a different user    ${PS_SP_APPLICATION_ACADEMIC_EMAIL}    ${short_password}
    And the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    Then the user should see the element    jQuery=li.complete:nth-of-type(6)

Project Finance is able to see Spend Profile approval page
    [Documentation]    INFUND-2638, INFUND-5617, INFUND-3973, INFUND-5942
    [Tags]    HappyPath
    [Setup]    Log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page     ${server}/project-setup-management/competition/${PS_SP_Competition_Id}/status
    And the user clicks the button/link      jQuery=#table-project-status tbody tr:nth-child(3) td.status.action:nth-child(6) a
    Then the user should be redirected to the correct page    ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/spend-profile/approval
    And the user should see the element    jQuery=#content div.grid-row div.column-third.alignright.extra-margin h2:contains("Spend profile")
    And the user should not see the element    jQuery=h2:contains("The spend profile has been approved")
    And the user should not see the element    jQuery=h2:contains("The spend profile has been rejected")
    And the user should see the text in the page  Innovation Lead
    And the user should see the text in the page    Peter Freeman
    When the user should see the text in the page    Project spend profile
    Then the user clicks the button/link             link=${Katz_Name}-spend-profile.csv
    And the user clicks the button/link    link=${Meembee_Name}-spend-profile.csv
    And the user clicks the button/link    link=${Zooveo_Name}-spend-profile.csv
    When the user should see the text in the page    Approved by Innovate UK
    Then the element should be disabled    jQuery=#accept-profile
    When the user selects the checkbox    approvedByLeadTechnologist
    Then the user should see the element    jQuery=#accept-profile
    And the user should see the element    jQuery=#content .button.button.button-warning.large:contains("Reject")

Comp Admin is able to see Spend Profile approval page
    [Documentation]    INFUND-2638, INFUND-5617, INFUND-6226, INFUND-5549
    [Tags]
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page    ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/spend-profile/approval
    Then the user should see the element    jQuery=#content div.grid-row div.column-third.alignright.extra-margin h2:contains("Spend profile")
    And the element should be disabled    jQuery=#accept-profile
    And the user should see the element    jQuery=#content .button.button.button-warning.large:contains("Reject")
    And the user should see the text in the page  Innovation Lead
    And the user should see the text in the page  Peter Freeman
    When the user clicks the button/link    jQuery=#content .button.button.button-warning.large:contains("Reject")
    Then the user should see the text in the page    You should contact the Project Manager to explain why the spend profile is being returned.
    When the user clicks the button/link    jQuery=.modal-reject-profile button:contains("Cancel")
    Then the user should not see an error in the page
    When the user selects the checkbox    approvedByLeadTechnologist
    Then the user should see the element    jQuery=#accept-profile
    When the user clicks the button/link    jQuery=button:contains("Approved")
    Then the user should see the text in the page    Approved by Innovate UK
    When the user clicks the button/link    jQuery=.modal-accept-profile button:contains("Cancel")
    Then the user should not see an error in the page

Comp Admin can download the Spend Profile csv
    [Documentation]    INFUND-3973, INFUND-5875
    [Tags]
    Given the user navigates to the page    ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/spend-profile/approval
    And the user should see the element     jQuery=h2:contains("Spend profile")
    Then the user should see the element    link=${Katz_Name}-spend-profile.csv
    And the user should see the element     link=${Meembee_Name}-spend-profile.csv
    And the user should see the element     link=${Zooveo_Name}-spend-profile.csv
    When the user clicks the button/link    link=${Katz_Name}-spend-profile.csv
    Then the user should not see an error in the page
    When the user clicks the button/link    link=${Meembee_Name}-spend-profile.csv
    Then the user should not see an error in the page
    When the user clicks the button/link    link=${Zooveo_Name}-spend-profile.csv
    Then the user should not see an error in the page

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049 ,INFUND-5543, INFUND-7119
    [Tags]    Experian    HappyPath
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    When the user navigates to the page      ${server}/project-setup-management/competition/${PS_SP_Competition_Id}/status
    Then the user should see the element     jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(1).status.ok         # Project details
    And the user should see the element      jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(2).status.ok         # MO
    And the user should see the element      jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(3).status.ok         # Bank details
    And the user should see the element      jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(4).status.ok         # Finance checks
    And the user should see the element      jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(5).status.action     # Spend Profile
    And the user should see the element      jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(6).status.ok         # Other Docs
    And the user should see the element      jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(7).status            # GOL
    And the user should not see the element    jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(7).status.waiting    # specifically checking regression issue INFUND-7119

Project Finance is able to Reject Spend Profile
    [Documentation]    INFUND-2638, INFUND-5617
    [Tags]
    [Setup]    Log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/spend-profile/approval
    And the user should see the element      jQuery=#content .button.button.button-warning.large:contains("Reject")
    When the user clicks the button/link     jQuery=#content .button.button.button-warning.large:contains("Reject")
    Then the user should see the text in the page    You should contact the Project Manager to explain why the spend profile is being returned.
    When the user clicks the button/link    jQuery=.modal-reject-profile button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=#content .button.button.button-warning.large:contains("Reject")
    And the user clicks the button/link    jQuery=.modal-reject-profile button:contains('Reject')

Status updates to a cross for the internal user's table
    [Documentation]    INFUND-6977
    [Tags]
    When the user navigates to the page      ${server}/project-setup-management/competition/${PS_SP_Competition_Id}/status
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(5).status.rejected

Lead partner can see that the spend profile has been rejected
    [Documentation]    INFUND-6977
    [Tags]
    Given log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    # TODO please switch out the PM login above with lead partner once INFUND-8136 is completed
    When the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    Then the user should see the element    jQuery=li.require-action:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    [Teardown]    the user goes back to the previous page

Non Lead partners should still see a tick instead of an hourglass when spend profile has been rejected
    [Documentation]    INFUND-7422
    [Tags]
    Given log in as a different user        ${PS_SP_APPLICATION_PARTNER_EMAIL}    ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    Then the user should see the element    jQuery=li.complete:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(5)
    Given log in as a different user        ${PS_SP_APPLICATION_ACADEMIC_EMAIL}   ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    Then the user should see the element    jQuery=li.complete:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(5)

Lead partner no longer has the 'submitted' view of the spend profiles
    [Documentation]    INFUND-6977, INFUND-7422
    Given Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    And the user clicks the button/link    link=Spend profile
    Then the user should not see the element    jQuery=.success-alert.extra-margin-bottom p:contains("All project spend profiles have been sent to Innovate UK.")
    And the user should see the text in the page    This overview shows the spend profile status of each organisation in your project.
    And the user should see the element    jQuery=.button:contains("Review and send total project spend profile")

Lead partner can return edit rights to other project partners
    [Documentation]    INFUND-6977
    When the user returns edit rights for the organisation    ${Zooveo_Name}
    And the user returns edit rights for the organisation    ${Meembee_name}


Lead partner can edit own spend profile and mark as complete
    [Documentation]    INFUND-6977, INFUNF-7409
    When the user clicks the button/link    link=${Katz_name}
    And the user should see the text in the page    Your spend profile is marked as complete
    And the user clicks the button/link    jQuery=.button:contains("Edit spend profile")
    And the user clicks the button/link    jQuery=.button:contains("Save and return to spend profile overview")
    And the user clicks the button/link    jQuery=.button:contains("Mark as complete")

Industrial partner receives edit rights and can submit their spend profile
    [Documentation]    INFUND-6977
    Given log in as a different user    ${PS_SP_APPLICATION_PARTNER_EMAIL}    ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    Then the user should see the element    jQuery=li.require-action:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td.status.action:nth-of-type(5)
    And the user goes back to the previous page
    When the user clicks the button/link    link=Spend profile
    And the user clicks the button/link    jQuery=a:contains("Submit to lead partner")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    Then the user should see the text in the page    Your spend profile has been sent to the lead partner
    When the user goes back to the previous page
    And the user clicks the button/link    link=Project setup status
    And the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(5)

Academic partner receives edit rights and can submit their spend profile
    [Documentation]    INFUND-6977
    Given log in as a different user    ${PS_SP_APPLICATION_ACADEMIC_EMAIL}    ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_HEADER}
    Then the user should see the element    jQuery=li.require-action:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(3) td.status.action:nth-of-type(5)
    And the user goes back to the previous page
    And the user clicks the button/link    link=Spend profile
    When the user clicks the button/link    jQuery=a:contains("Submit to lead partner")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    Then the user should see the text in the page    Your spend profile has been sent to the lead partner
    When the user goes back to the previous page
    And the user clicks the button/link    link=Project setup status
    And the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(5)


Lead partner can send the combined spend profile
    [Documentation]    INFUND-6977
    [Setup]    log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user navigates to the page     ${external_spendprofile_summary}
    When the user clicks the button/link     jQuery=.button:contains("Review and send total project spend profile")
    Then the user clicks the button/link     jQuery=.button:contains("Send project spend profile")
    And the user should see the element      jQuery=button:contains("Cancel")
    When the user clicks the button/link     jQuery=.modal-confirm-spend-profile-totals .button[value="Send"]


Project Finance is able to Approve Spend Profile
    [Documentation]    INFUND-2638, INFUND-5617, INFUND-5507, INFUND-5549
    [Tags]    HappyPath
    [Setup]    log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page    ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/spend-profile/approval
    When the user selects the checkbox      approvedByLeadTechnologist
    Then the user should see the element    jQuery=button:contains("Approved")
    And the user should see the text in the page  Innovation Lead
    And the user should see the text in the page  Peter Freeman
    When the user clicks the button/link    jQuery=button:contains("Approved")
    Then the user should see the text in the page  Approved by Innovate UK
    When the user clicks the button/link    jQuery=.modal-accept-profile button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=button:contains("Approved")
    And the user clicks the button/link     jQuery=.modal-accept-profile button:contains("Approve")
    And the user should see the text in the page    ${PS_SP_APPLICATION_TITLE}
    Then the user should not see the element      jQuery=h3:contains("The spend profile has been approved")

Status updates correctly for internal user's table after approval
    [Documentation]    INFUND-5543
    [Tags]
    When the user navigates to the page     ${server}/project-setup-management/competition/${PS_SP_Competition_Id}/status
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(5).status.ok
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(3) td:nth-of-type(7).status.action   # GOL

Project Finance still has a link to the spend profile after approval
    [Documentation]    INFUND-6046
    [Tags]
    When the user clicks the button/link           jQuery=td:nth-child(6) a
    Then the user should see the text in the page  Project spend profile
    And the user clicks the button/link            link=${Katz_Name}-spend-profile.csv (opens in a new window)    #TODO NUNO - maybe remove as it's downloaded???
    And the user clicks the button/link            link=${Meembee_Name}-spend-profile.csv (opens in a new window)
    And the user clicks the button/link            link=${Zooveo_Name}-spend-profile.csv (opens in a new window)
    And the user should see the text in the page   The spend profile has been approved

Project finance user cannot access external users' spend profile page
    [Documentation]    INFUND-5911
    [Tags]
    When the user navigates to the page and gets a custom error message  ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner-organisation/${Katz_Id}/spend-profile    You do not have the necessary permissions for your request


*** Keywords ***
the user uploads the file
    [Arguments]    ${upload_filename}
    Choose File    id=assessorFeedback    ${UPLOAD_FOLDER}/${upload_filename}


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


the user makes all values zeros
    [Arguments]    ${row}    ${project_duration}
    : FOR    ${i}    IN RANGE    1    ${project_duration}
    \    the user enters text to a text field  css=.spend-profile-table tbody .form-group-row:nth-child(${row}) td:nth-of-type(${i}) input  0

the text box should be editable
    [Arguments]    ${element}
    Wait Until Element Is Visible Without Screenshots    ${element}
    Element Should Be Enabled    ${element}

the field has value
    [Arguments]    ${field}    ${value}
    Wait Until Element Is Visible Without Screenshots    ${field}
    ${var} =    get value    ${field}
    should be equal as strings    ${var}    ${value}

the user should see all spend profiles as complete
    the user should see the element    jQuery=.extra-margin-bottom tr:nth-child(1) td:nth-child(2):contains("Complete")
    the user should see the element    jQuery=.extra-margin-bottom tr:nth-child(2) td:nth-child(2):contains("Complete")
    the user should see the element    jQuery=.extra-margin-bottom tr:nth-child(3) td:nth-child(2):contains("Complete")

all previous sections of the project are completed
    partners submit their finance contacts
    partners submit bank details
    project finance approves bank details
    project manager submits other documents
    project finance approves other documents
    project finance reviews Finance checks

partners submit their finance contacts
    guest user log-in                  ${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}    ${short_password}
    the user navigates to the page     ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/details/finance-contact?organisation=${Katz_Id}
    the user selects the radio button  financeContact    financeContact1
    the user clicks the button/link    jQuery=.button:contains("Save")
    log in as a different user         ${PS_SP_APPLICATION_PARTNER_EMAIL}    ${short_password}
    the user navigates to the page     ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/details/finance-contact?organisation=${Meembee_Id}
    the user selects the radio button  financeContact    financeContact1
    the user clicks the button/link    jQuery=.button:contains("Save")
    log in as a different user         ${PS_SP_APPLICATION_ACADEMIC_EMAIL}    ${short_password}
    the user navigates to the page     ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/details/finance-contact?organisation=${Zooveo_Id}
    the user selects the radio button  financeContact    financeContact1
    the user clicks the button/link    jQuery=.button:contains("Save")

partners submit bank details
    partner submits his bank details  ${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}
    partner submits his bank details  ${PS_SP_APPLICATION_PARTNER_EMAIL}
    partner submits his bank details  ${PS_SP_APPLICATION_ACADEMIC_EMAIL}

partner submits his bank details
    [Arguments]  ${email}
    log in as a different user            ${email}    ${short_password}
    the user navigates to the page        ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/bank-details
    the user enters text to a text field  id=bank-acc-number  51406795
    the user enters text to a text field  id=bank-sort-code  404745
    the user selects the radio button     addressType    REGISTERED
    the user clicks the button/link       jQuery=.button:contains("Submit bank account details")
    the user clicks the button/link       jQuery=.button:contains("Submit")

project finance approves bank details
    log in as a different user                   &{internal_finance_credentials}
    proj finance approves partners bank details  ${Katz_Id}
    proj finance approves partners bank details  ${Meembee_Id}
    proj finance approves partners bank details  ${Zooveo_Id}

proj finance approves partners bank details
    [Arguments]  ${id}
    the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/organisation/${id}/review-bank-details
    the user clicks the button/link    jQuery=.button:contains("Approve bank account details")
    the user clicks the button/link    jQuery=.button:contains("Approve account")

project manager submits other documents
    log in as a different user        ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    the user navigates to the page    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner/documents
    choose file                       name=collaborationAgreement    ${upload_folder}/testing.pdf
    choose file                       name=exploitationPlan    ${upload_folder}/testing.pdf
    the user reloads the page
    the user clicks the button/link    jQuery=.button:contains("Submit documents")
    the user clicks the button/link    jQuery=.button:contains("Submit")

project finance approves other documents
    log in as a different user         &{internal_finance_credentials}
    the user navigates to the page     ${SERVER}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/partner/documents
    the user clicks the button/link    jQuery=.button:contains("Accept documents")
    the user clicks the button/link    jQuery=.modal-accept-docs .button:contains("Accept Documents")

project finance reviews Finance checks
    log in as a different user         &{internal_finance_credentials}
    project finance approves Viability for  ${Katz_Id}
    project finance approves Viability for  ${Meembee_Id}
    project finance approves Eligibility

project finance approves Viability for
    [Arguments]  ${partner}
    the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/finance-check/organisation/${partner}/viability
    the user selects the checkbox      costs-reviewed
    the user selects the checkbox      project-viable
    the user moves focus to the element  link=Contact us
    the user selects the option from the drop-down menu  Green  id=rag-rating
    the user clicks the button/link    css=#confirm-button
    the user clicks the button/link    jQuery=.modal-confirm-viability .button:contains("Confirm viability")

project finance approves Eligibility
    the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/finance-check/organisation/${Katz_Id}/eligibility
    the user approves project costs
    the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/finance-check/organisation/${Meembee_Id}/eligibility
    the user approves project costs
    the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/finance-check/organisation/${Zooveo_Id}/eligibility
    the user approves project costs

the user approves project costs
    the user selects the checkbox    project-eligible
    the user selects the option from the drop-down menu    Green    id=rag-rating
    the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    the user clicks the button/link    name=confirm-eligibility


the user returns edit rights for the organisation
    [Arguments]    ${org_name}
    the user clicks the button/link    link=${org_name}
    the user clicks the button/link    jQuery=.button:contains("Allow edits")
    the user clicks the button/link    jQuery=.button:contains("Allow partner to edit")
    the user should see the text in the page    In progress
