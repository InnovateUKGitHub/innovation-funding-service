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
...
...               IFS-1576 Allow changes to Target start date until generation of Spend Profile
Suite Setup       all previous sections of the project are completed
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          PS_Common.robot

*** Variables ***
${project_overview}    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}
${external_spendprofile_summary}    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner-organisation/${Katz_Id}/spend-profile
${project_duration}    36
&{lead_applicant_credentials_sp}  email=${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}  password=${short_password}
&{collaborator1_credentials_sp}   email=${PS_SP_APPLICATION_PARTNER_EMAIL}  password=${short_password}
&{collaborator2_credentials_sp}   email=${PS_SP_APPLICATION_ACADEMIC_EMAIL}  password=${short_password}

*** Test Cases ***
Project Finance user generates the Spend Profile
    [Documentation]    INFUND-5194
    [Tags]    HappyPath
    [Setup]  log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page    ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/finance-check
    Then the user should see the element    jQuery=a.eligibility-0:contains("Approved")
    And the user should see the element     jQuery=a.eligibility-1:contains("Approved")
    And the user should see the element     jQuery=a.eligibility-2:contains("Approved")
    Then the user should see the element    css=.generate-spend-profile-main-button


Project Finance cancels the generation of the Spend Profile
    [Documentation]    INFUND-5194
    [Tags]
    When the user clicks the button/link    css=.generate-spend-profile-main-button
    Then the user should see the text in the page    This will generate a flat spend profile for all project partners.
    When the user clicks the button/link    jQuery=button:contains("Cancel")

Project Finance generates the Spend Profile
    [Documentation]    INFUND-5194, INFUND-5987
    [Tags]    HappyPath
    When the user clicks the button/link    css=.generate-spend-profile-main-button
    And the user clicks the button/link     css=#generate-spend-profile-modal-button
    Then the user should see the element    jQuery=.success-alert p:contains("The finance checks have been approved and profiles generated.")
    When the user navigates to the page     ${server}/project-setup-management/competition/${PS_SP_Competition_Id}/status
    Then the user should see the element    css=#table-project-status tr:nth-of-type(3) td:nth-of-type(4).ok
    And the user reads his email            ${PS_SP_APPLICATION_PM_EMAIL}  Your spend profile is available  The finance checks for all partners in the project have now been completed

Lead partner can view spend profile page
    [Documentation]    INFUND-3970, INFUND-6138, INFUND-5899, INFUND-7685
    [Tags]    HappyPath
    [Setup]    Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    When the user clicks the button/link             link=status of my partners
    Then the user should see the text in the page    Project team status
    And the user should see the element              css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    When the user clicks the button/link             link=Project setup status
    Then the user should see the element      css=li.require-action:nth-child(6)
    When the user clicks the button/link     link=Spend profile
    And the user should not see the element    link=Total project profile spend
    And the user clicks the button/link      link=${Katz_Name}
    Then the user should not see an error in the page
    And the user should see the text in the page    We have reviewed and confirmed your project costs.
    And the user should see the text in the page    ${Katz_Name} - Spend profile
    And the user clicks the button/link    link=Spend profile overview
    And the user should see the text in the page    This overview shows the spend profile status of each organisation in your project.
    [Teardown]    the user goes back to the previous page

Lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page     June 2020
    And the user should see the text in the page     ${project_duration} months

Calculations in the spend profile table
    [Documentation]    INFUND-3764, INFUND-6148
    [Tags]    HappyPath
    Given the user should see the element  css=.spend-profile-table
    Then the user should see the element   jQuery=th:contains("Labour") ~ td.fix-right:contains("£ 3,081")
    And the user should see the element    jQuery=th:contains("Overheads") ~ td.fix-right:contains("£ 0")
    And the user should see the element    jQuery=th:contains("Materials") ~ td.fix-right:contains("£ 100,200")
    And the user should see the element    jQuery=th:contains("Capital usage") ~ td.fix-right:contains("£ 552")
    And the user should see the element    jQuery=th:contains("Subcontracting") ~ td.fix-right:contains("£ 90,000")
    And the user should see the element    jQuery=th:contains("Travel and subsistence") ~ td.fix-right:contains("£ 5,970")
    And the user should see the element    jQuery=th:contains("Other costs") ~ td.fix-right:contains("£ 1,100")
    And the user should see the element    jQuery=th:contains("Finance") ~ td.fix-right:contains("£ 30")
    And the user should see the element    jQuery=th:contains("Other Funding") ~ td.fix-right:contains("£ 2,468")
    #${duration} is No of Months + 1, due to header
    And the sum of tds equals the total    .spend-profile-table  1  38  3081    # Labour
    And the sum of tds equals the total    .spend-profile-table  3  38  100200  # Materials
    And the sum of tds equals the total    .spend-profile-table  5  38  90000   # Subcontracting
    And the sum of tds equals the total    .spend-profile-table  6  38  5970    # Travel & subsistence
    And the sum of tds equals the total    .spend-profile-table  7  38  1100    # Other costs

Lead Partner can see Spend profile summary
    [Documentation]    INFUND-3971, INFUND-6148
    [Tags]
    Given the user navigates to the page  ${external_spendprofile_summary}/review
    When the user should see the element  jQuery=.grid-container th:contains("Financial year") + th:contains("Project spend")
    Then the user should see the element  jQuery=.grid-container table tr:nth-child(1) td:nth-child(2):contains("£ 56,605")

Lead partner can edit his spend profile with invalid values and see the error messages
    [Documentation]  INFUND-3765, INFUND-6907, INFUND-6801, INFUND-7409, INFUND-6148 INFUND-6146
    [Tags]
    When the user clicks the button/link       jQuery=.button:contains("Edit spend profile")
    Then the user should see the element       jQuery=th:contains("Labour") + td input
    When the user enters text to a text field  jQuery=th:contains("Labour") + td input   520
    And the user moves focus to the element    jQuery=th:contains("Overheads") + td input
    Then the user should see the element       jQuery=.error-summary:contains("Unable to submit spend profile.")
    And the user should see the element        jQuery=.form-group-error th:contains("Labour")
    And the user should see the element        jQuery=th:contains("Labour") ~ .fix-right.cell-error input[data-calculation-rawvalue="3495"]
    # Project costs for financial year are instantly reflecting the financial values INFUND-3971, INFUND-6148
    And the user should see the element        jQuery=.grid-container table tr:nth-child(1) td:nth-child(2):contains("£ 57,019")
    When the user clicks the button/link       jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should see the element       jQuery=.error-summary:contains("Your total costs are higher than the eligible project costs.")
    When the user clicks the button/link       jQuery=.button:contains("Edit spend profile")
    Then the user enters text to a text field  jQuery=th:contains("Labour") + td input  10
#    And the user should not see the element   jQuery=.form-group-error th:contains("Labour")  # TODO IFS-1120
    When the user enters text to a text field  jQuery=th:contains("Overheads") ~ td:nth-child(4) input  -55
    And the user moves focus to the element    jQuery=th:contains("Overheads") ~ td:nth-child(5)
    Then the user should see the element       jQuery=.error-summary-list li:contains("This field should be 0 or higher")
    When the user enters text to a text field  jQuery=th:contains("Overheads") ~ td:nth-child(4) input  35.25
    And the user moves focus to the element    jQuery=th:contains("Overheads") ~ td:nth-child(5)
    Then the user should see the element       jQuery=.error-summary-list li:contains("This field can only accept whole numbers")
    When the user clicks the button/link       jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should not see an error in the page
    When the user enters text to a text field  jQuery=th:contains("Overheads") ~ td:nth-child(4) input  0
    And the user moves focus to the element    css=.spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(2) input
    And the user should not see the element    css=.error-summary-list
    When the user enters text to a text field  jQuery=th:contains("Other Funding") ~ td:nth-child(4) input  ${empty}
    Then the user should see the element       jQuery=.error-summary:contains("This field cannot be left blank.")
    And the user enters text to a text field   jQuery=th:contains("Other Funding") ~ td:nth-child(4) input  68

Lead partner can edit his spend profile with valid values
    [Documentation]    INFUND-3765
    [Tags]    HappyPath
    Given the user navigates to the page       ${external_spendprofile_summary}/review
    When the user clicks the button/link       jQuery=.button:contains("Edit spend profile")
    And the user should see the element        css=table [type="number"]    # checking here that the table is not read-only
    Then the user should see the element       jQuery=th:contains("Labour") + td input
    When the user enters text to a text field  jQuery=th:contains("Labour") + td input  14
    And the user moves focus to the element    jQuery=th:contains("Labour") ~ td:nth-child(4) input
    Then the user should see the element       jQuery=th:contains("Labour") ~ td.fix-right input[data-calculation-rawvalue="2989"]
    When the user enters text to a text field  jQuery=th:contains("Subcontracting") ~ td:nth-child(5) input  0
    And the user moves focus to the element    jQuery=th:contains("Subcontracting") ~ td:nth-child(7) input
    Then the user should see the element       jQuery=th:contains("Subcontracting") ~ td.fix-right input[data-calculation-rawvalue="90000"]
    And the user should not see the element    jQuery=.error-summary:contains("Unable to save spend profile")
    When the user clicks the button/link       jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should not see the element   jQuery=.error-summary:contains("Your total costs are higher than the eligible project costs.")

Project Manager can see Spend Profile in Progress
    [Documentation]    done during refactoring, no ticket attached
    [Tags]
    [Setup]  Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user navigates to the page     ${external_spendprofile_summary}
    Then the user should see the element     link=${PS_SP_APPLICATION_LEAD_ORGANISATION_NAME}
    And the user should see the element      jQuery=.task-list li:nth-child(1):contains("In progress")

Lead partner marks spend profile as complete
    [Documentation]    INFUND-3765, INFUND-6138
    [Tags]    HappyPath
    [Setup]  Log in as a different user        ${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}    ${short_password}
    Given the user navigates to the page       ${external_spendprofile_summary}/review
    When the user clicks the button/link       jQuery=.button:contains("Mark as complete")
    Then the user should not see the element   jQuery=.success-alert p:contains("Your spend profile is marked as complete. You can still edit this page.")
    And the user should not see the element    css=table a[type="number"]    # checking here that the table has become read-only
    When the user clicks the button/link            link=Project setup status
    And the user clicks the button/link             link=status of my partners
    Then the user should see the text in the page    Project team status
    And the user should see the element              css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    When the user clicks the button/link             link=Project setup status
    Then the user should see the element             css=li.require-action:nth-child(6)

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Tags]    HappyPath
    [Setup]    Log in as a different user           ${PS_SP_APPLICATION_PARTNER_EMAIL}    ${short_password}
    Given the user clicks the button/link           link=${PS_SP_APPLICATION_TITLE}
    And the user should see the element             css=ul li.complete:nth-child(1)
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
    Given the user clicks the button/link           link=${PS_SP_APPLICATION_TITLE}
    When the user clicks the button/link             link=status of my partners
    Then the user should see the text in the page    Project team status
    And the user should see the element              css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    When the user clicks the button/link             link=Project setup status
    Then the user should see the element             css=li.require-action:nth-child(6)
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
    And the user should see the text in the page     June 2020
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
    Then the user should see the element    css=ul li.complete:nth-child(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    And the user should see the element    css=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(5)

Project Manager doesn't have the option to send spend profiles until all partners have marked as complete
    [Documentation]    INFUND-3767, INFUND-6138
    [Tags]
    [Setup]    log in as a different user       ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user clicks the button/link       link=${PS_SP_APPLICATION_TITLE}
    And the user clicks the button/link             link=status of my partners
    Then the user should see the text in the page    Project team status
    And the user should see the element              css=#table-project-status tr:nth-of-type(3) td.status.action:nth-of-type(5)
    When the user clicks the button/link             link=Project setup status
    Then the user should see the element             jQuery=li.require-action:nth-child(6)
    When the user clicks the button/link        link=Spend profile
    Then the user should not see the element    jQuery=.button:contains("Review spend profiles")
    #The complete name of the button is anyways not selected. Please use the short version of it.

Academic partner can view spend profile page
    [Documentation]    INFUND-3970, INFUND-5899
    [Tags]    HappyPath
    [Setup]    Log in as a different user           ${PS_SP_APPLICATION_ACADEMIC_EMAIL}    ${short_password}
    Given the user clicks the button/link           link=${PS_SP_APPLICATION_TITLE}
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
    And the user should see the text in the page     June 2020
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
    When the user enters text to a text field          css=.spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(2) input   0  # Other - Directly allocated
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(3) input    0  # Other - Directly allocated
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(9) td:nth-of-type(2) input    0  # Other - Exceptions
    And the user enters text to a text field           css=.spend-profile-table tbody .form-group-row:nth-child(9) td:nth-of-type(3) input    0  # Other - Exceptions
    And the user should not see the text in the page   Your total costs are higher than your eligible costs

Academic partner edits spend profile and this updates on the table
    [Documentation]    INFUND-5846
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Save and return to spend profile overview")
    Then the user should see the element    jQuery=.button:contains("Edit spend profile")
    And element should contain    css=.spend-profile-table tbody tr:nth-of-type(1) td:nth-of-type(1)    3
    And element should contain    css=.spend-profile-table tbody tr:nth-of-type(2) td:nth-of-type(3)    1

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
    Then the user should see the element    css=ul li.complete:nth-child(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    And the user should see the element    css=#table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(5)


Project Manager can view partners' spend profiles
    [Documentation]    INFUND-3767, INFUND-3766, INFUND-5609
    [Tags]    HappyPath
    [Setup]    log in as a different user           ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user clicks the button/link           link=${PS_SP_APPLICATION_TITLE}
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
    And the user navigates to the page and gets a custom error message  ${external_spendprofile_summary}    ${403_error_message}
    Given log in as a different user               ${PS_SP_APPLICATION_ACADEMIC_EMAIL}    ${short_password}
    And the user navigates to the page and gets a custom error message  ${external_spendprofile_summary}    ${403_error_message}

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
    Then the user should see the element    jQuery=.task-list li:nth-child(1):contains("Complete")
    And the user should see the element     jQuery=.task-list li:nth-child(2):contains("Complete")
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
    And the user should see the element      jQuery=.task-list li:nth-child(2):contains("In progress")

Partner can receive edit rights to his SP
    [Documentation]    INFUND-6350
    [Tags]    HappyPath
    [Setup]  log in as a different user     ${PS_SP_APPLICATION_PARTNER_EMAIL}  ${short_password}
    Given the user navigates to the page    ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}
    Then the user should see the element    css=li.require-action:nth-child(6)
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
    When the user clicks the button/link     css=.modal-confirm-spend-profile-totals .button[value="Send"]

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
    And the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(5)


Partners can see the Spend Profile section completed
    [Documentation]    INFUND-3767,INFUND-3766
    [Tags]
    Given Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    And the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    Then the user should see the element    css=li.waiting:nth-of-type(6)
    Given Log in as a different user    ${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}    ${short_password}
    And the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    Then the user should see the element    css=li.waiting:nth-of-type(6)
    Given Log in as a different user    ${PS_SP_APPLICATION_PARTNER_EMAIL}    ${short_password}
    And the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    Then the user should see the element    css=li.complete:nth-of-type(6)
    Given Log in as a different user    ${PS_SP_APPLICATION_ACADEMIC_EMAIL}    ${short_password}
    And the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    Then the user should see the element    css=li.complete:nth-of-type(6)

Project Finance is able to see Spend Profile approval page
    [Documentation]    INFUND-2638, INFUND-5617, INFUND-3973, INFUND-5942
    [Tags]    HappyPath
    [Setup]    Log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page     ${server}/project-setup-management/competition/${PS_SP_Competition_Id}/status
    And the user clicks the button/link      css=#table-project-status tbody tr:nth-child(3) td.status.action:nth-child(6) a
    Then the user should be redirected to the correct page    ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/spend-profile/approval
    And the user should see the element    jQuery=#content div.grid-row div.column-third.alignright.extra-margin h2:contains("Spend profile")
    And the user should not see the element    jQuery=h2:contains("The spend profile has been approved")
    And the user should not see the element    jQuery=h2:contains("The spend profile has been rejected")
    And the user should see the text in the page  Innovation Lead
    And the user should see the text in the page    Peter Freeman
    When the user should see the text in the page    Project spend profile
    Then the user clicks the button/link   link=${Katz_Name}-spend-profile.csv
    And the user clicks the button/link   link=${Meembee_Name}-spend-profile.csv
    And the user clicks the button/link   link=${Zooveo_Name}-spend-profile.csv
    When the user should see the text in the page    Approved by Innovate UK
    Then the element should be disabled    css=#accept-profile
    When the user selects the checkbox    approvedByLeadTechnologist
    Then the user should see the element    css=#accept-profile
    And the user should see the element    jQuery=#content .button.button.button-warning:contains("Reject")

Check if target start date can be changed until SP approval
    [Documentation]    IFS-1576
    [Tags]
    Given Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    When the user navigates to the page  ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/details
    And the user changes the start date back again  2021
    Then the user should see the element  jQuery=#start-date:contains("1 Jun 2021")
    When the user changes the start date back again  2020
    Then the user should see the element  jQuery=#start-date:contains("1 Jun 2020")

Check if project manager and project address fields are still editable
   [Documentation]    IFS-1577, IFS-1578
   [Tags]
   Given Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
   Then check if project manager and project address fields are editable  ${PS_SP_APPLICATION_PROJECT}

Comp Admin is able to see Spend Profile approval page
    [Documentation]    INFUND-2638, INFUND-5617, INFUND-6226, INFUND-5549
    [Tags]
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page    ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/spend-profile/approval
    Then the user should see the element    jQuery=#content div.grid-row div.column-third.alignright.extra-margin h2:contains("Spend profile")
    And the element should be disabled    css=#accept-profile
    And the user should see the element    jQuery=#content .button-warning:contains("Reject")
    And the user should see the text in the page  Innovation Lead
    And the user should see the text in the page  Peter Freeman
    When the user clicks the button/link    jQuery=#content .button-warning:contains("Reject")
    Then the user should see the text in the page    You should contact the Project Manager to explain why the spend profile is being returned.
    When the user clicks the button/link    jQuery=.modal-reject-profile button:contains("Cancel")
    Then the user should not see an error in the page
    When the user selects the checkbox    approvedByLeadTechnologist
    Then the user should see the element    css=#accept-profile
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
    Then the user should see the element     css=#table-project-status tr:nth-of-type(3) td:nth-of-type(1).status.ok         # Project details
    And the user should see the element      css=#table-project-status tr:nth-of-type(3) td:nth-of-type(2).status.ok         # MO
    And the user should see the element      css=#table-project-status tr:nth-of-type(3) td:nth-of-type(3).status.ok         # Bank details
    And the user should see the element      css=#table-project-status tr:nth-of-type(3) td:nth-of-type(4).status.ok         # Finance checks
    And the user should see the element      css=#table-project-status tr:nth-of-type(3) td:nth-of-type(5).status.action     # Spend Profile
    And the user should see the element      css=#table-project-status tr:nth-of-type(3) td:nth-of-type(6).status.ok         # Other Docs
    And the user should see the element      css=#table-project-status tr:nth-of-type(3) td:nth-of-type(7).status            # GOL
    And the user should not see the element    css=#table-project-status tr:nth-of-type(3) td:nth-of-type(7).status.waiting    # specifically checking regression issue INFUND-7119

Project Finance is able to Reject Spend Profile
    [Documentation]    INFUND-2638, INFUND-5617
    [Tags]
    [Setup]    Log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_APPLICATION_PROJECT}/spend-profile/approval
    And the user should see the element      jQuery=#content .button.button.button-warning:contains("Reject")
    When the user clicks the button/link     jQuery=#content .button.button.button-warning:contains("Reject")
    Then the user should see the text in the page    You should contact the Project Manager to explain why the spend profile is being returned.
    When the user clicks the button/link    jQuery=.modal-reject-profile button:contains("Cancel")
    Then the user should not see an error in the page
    When the user clicks the button/link    jQuery=#content .button.button.button-warning:contains("Reject")
    And the user clicks the button/link    jQuery=.modal-reject-profile button:contains('Reject')

Status updates to a cross for the internal user's table
    [Documentation]    INFUND-6977
    [Tags]
    When the user navigates to the page      ${server}/project-setup-management/competition/${PS_SP_Competition_Id}/status
    Then the user should see the element    css=#table-project-status tr:nth-of-type(3) td:nth-of-type(5).status.rejected

Lead partner can see that the spend profile has been rejected
    [Documentation]    INFUND-6977
    [Tags]
    Given log in as a different user    ${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}    ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    Then the user should see the element    css=li.require-action:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    [Teardown]    the user goes back to the previous page

Non Lead partners should still see a tick instead of an hourglass when spend profile has been rejected
    [Documentation]    INFUND-7422
    [Tags]
    Given log in as a different user        ${PS_SP_APPLICATION_PARTNER_EMAIL}    ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    Then the user should see the element    css=li.complete:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(5)
    Given log in as a different user        ${PS_SP_APPLICATION_ACADEMIC_EMAIL}   ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    Then the user should see the element    css=li.complete:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(5)

Lead partner no longer has the 'submitted' view of the spend profiles
    [Documentation]    INFUND-6977, INFUND-7422
    Given Log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
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
    When the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    Then the user should see the element    css=li.require-action:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(2) td.status.action:nth-of-type(5)
    And the user goes back to the previous page
    When the user clicks the button/link    link=Spend profile
    And the user clicks the button/link    jQuery=a:contains("Submit to lead partner")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    Then the user should see the text in the page    Your spend profile has been sent to the lead partner
    When the user goes back to the previous page
    And the user clicks the button/link    link=Project setup status
    And the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(5)

Academic partner receives edit rights and can submit their spend profile
    [Documentation]    INFUND-6977
    Given log in as a different user    ${PS_SP_APPLICATION_ACADEMIC_EMAIL}    ${short_password}
    When the user clicks the button/link    link=${PS_SP_APPLICATION_TITLE}
    Then the user should see the element    css=li.require-action:nth-of-type(6)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(3) td.status.action:nth-of-type(5)
    And the user goes back to the previous page
    And the user clicks the button/link    link=Spend profile
    When the user clicks the button/link    jQuery=a:contains("Submit to lead partner")
    And the user clicks the button/link    jQuery=.button:contains("Submit")
    Then the user should see the text in the page    Your spend profile has been sent to the lead partner
    When the user goes back to the previous page
    And the user clicks the button/link    link=Project setup status
    And the user clicks the button/link    link=status of my partners
    Then the user should see the element    css=#table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(5)


Lead partner can send the combined spend profile
    [Documentation]    INFUND-6977
    [Setup]    log in as a different user    ${PS_SP_APPLICATION_PM_EMAIL}    ${short_password}
    Given the user navigates to the page     ${external_spendprofile_summary}
    When the user clicks the button/link     jQuery=.button:contains("Review and send total project spend profile")
    Then the user clicks the button/link     jQuery=.button:contains("Send project spend profile")
    And the user should see the element      jQuery=button:contains("Cancel")
    When the user clicks the button/link     css=.modal-confirm-spend-profile-totals .button[value="Send"]


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
    Then the user should see the element    css=#table-project-status tr:nth-of-type(3) td:nth-of-type(5).status.ok
    And the user should see the element     css=#table-project-status tr:nth-of-type(3) td:nth-of-type(7).status.action   # GOL

Project Finance still has a link to the spend profile after approval
    [Documentation]    INFUND-6046
    [Tags]
    When the user clicks the button/link           css=td:nth-child(6) a
    Then the user should see the text in the page  Project spend profile
    And the user clicks the button/link            link=${Katz_Name}-spend-profile.csv
    And the user clicks the button/link            link=${Meembee_Name}-spend-profile.csv
    And the user clicks the button/link            link=${Zooveo_Name}-spend-profile.csv
    And the user should see the text in the page   The spend profile has been approved

Project finance user cannot access external users' spend profile page
    [Documentation]    INFUND-5911
    [Tags]
    When the user navigates to the page and gets a custom error message  ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/partner-organisation/${Katz_Id}/spend-profile    ${403_error_message}

Target start date cannot be changed after SP approval
    [Documentation]    INFUND-1576
    [Tags]
    Given Log in as a different user  ${PS_SP_APPLICATION_PM_EMAIL}  ${short_password}
    When the user navigates to the page  ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/details
    Then the user should not see the element  jQuery=a:contains("Target start date")
    When the user navigates to the page and gets a custom error message  ${server}/project-setup/project/${PS_SP_APPLICATION_PROJECT}/details/project-address  ${403_error_message}

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
    \    ${text} =    Get Text    css=${table} tr:nth-child(${row}) td:nth-child(${i})
    \    ${formatted} =    Remove String    ${text}    ,    # Remove the comma from the number
    \    ${cell} =    convert to integer    ${formatted}
    \    ${sum} =    Evaluate    ${sum}+${cell}
    Should Be Equal As Integers    ${sum}    ${total}

the user should see all spend profiles as complete
    the user should see the element  jQuery=.task-list li:contains(${Katz_Name}) span:contains("Complete")
    the user should see the element  jQuery=.task-list li:contains(${Meembee_Name}) span:contains("Complete")
    the user should see the element  jQuery=.task-list li:contains(${Zooveo_name}) span:contains("Complete")

all previous sections of the project are completed
    the user logs-in in new browser           &{lead_applicant_credentials}
    project partners submit finance contacts
    partners submit bank details
    project finance approves bank details
    project manager submits other documents   ${PS_SP_APPLICATION_PM_EMAIL}  ${short_password}  ${PS_SP_APPLICATION_PROJECT}
    project finance approves other documents  ${PS_SP_APPLICATION_PROJECT}
    project finance reviews Finance checks

project partners submit finance contacts
    the partner submits their finance contact  ${Katz_Id}  ${PS_SP_APPLICATION_PROJECT}  &{lead_applicant_credentials_sp}
    the partner submits their finance contact  ${Meembee_Id}  ${PS_SP_APPLICATION_PROJECT}  &{collaborator1_credentials_sp}
    the partner submits their finance contact  ${Zooveo_Id}  ${PS_SP_APPLICATION_PROJECT}  &{collaborator2_credentials_sp}

partners submit bank details
    partner submits his bank details  ${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}  ${PS_SP_APPLICATION_PROJECT}  ${account_one}  ${sortCode_one}
    partner submits his bank details  ${PS_SP_APPLICATION_PARTNER_EMAIL}  ${PS_SP_APPLICATION_PROJECT}  ${account_one}  ${sortCode_one}
    partner submits his bank details  ${PS_SP_APPLICATION_ACADEMIC_EMAIL}  ${PS_SP_APPLICATION_PROJECT}  ${account_one}  ${sortCode_one}

project finance approves bank details
    log in as a different user                          &{internal_finance_credentials}
    the project finance user approves bank details for  ${Katz_Name}  ${PS_SP_APPLICATION_PROJECT}
    the project finance user approves bank details for  ${Meembee_Name}  ${PS_SP_APPLICATION_PROJECT}
    the project finance user approves bank details for  ${Zooveo_Name}  ${PS_SP_APPLICATION_PROJECT}

project finance reviews Finance checks
    log in as a different user              &{internal_finance_credentials}
    project finance approves Viability for  ${Katz_Id}  ${PS_SP_APPLICATION_PROJECT}
    project finance approves Viability for  ${Meembee_Id}  ${PS_SP_APPLICATION_PROJECT}
    project finance approves Eligibility    ${Katz_Id}  ${Meembee_Id}  ${Zooveo_Id}  ${PS_SP_APPLICATION_PROJECT}

the user returns edit rights for the organisation
    [Arguments]    ${org_name}
    the user clicks the button/link  link=${org_name}
    the user clicks the button/link  jQuery=.button:contains("Allow edits")
    the user clicks the button/link  jQuery=.button:contains("Allow partner to edit")
    the user should see the text in the page    In progress

check if project manager and project address fields are editable
    [Arguments]  ${project}
    the user navigates to the page  ${server}/project-setup/project/${project}/details
    check if project address can be changed
    check if project manager can be changed

check if project address can be changed
    the user clicks the button/link  jQuery=a:contains("Project address")
    the user selects the radio button  addressType  address-use-operating
    the user clicks the button/link  jQuery=button:contains("Save")
    the user clicks the button/link  jQuery=a:contains("Project address")
    the user sees that the radio button is selected  addressType  address-use-operating
    the user selects the radio button  addressType  address-use-org
    the user clicks the button/link  jQuery=button:contains("Save")

check if project manager can be changed
    the user clicks the button/link  jQuery=a:contains("Project Manager")
    the user selects the radio button  projectManager  projectManager2
    the user clicks the button/link  jQuery=button:contains("Save")
    the user clicks the button/link  jQuery=a:contains("Project Manager")
    the user sees that the radio button is selected  projectManager  projectManager2
    the user selects the radio button  projectManager  projectManager1
    the user clicks the button/link  jQuery=button:contains("Save")
