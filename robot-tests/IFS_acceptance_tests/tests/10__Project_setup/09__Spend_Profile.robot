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
...
...               IFS-1579 Allow change of Finance Contact until generation of GOL
...
...               IFS-2062 Row to be taken off from Query responses tab once SP is generated
...
...               IFS-2016 Project Setup task management: Spend Profile
...
...               IFS-2221 Spend Profile Generation - Ensure Bank Details are approved or not required
...
...               IFS-6732 Ensure spend profile cannot be generated when there is a pending invite
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          PS_Common.robot

*** Variables ***
${project_overview}    ${server}/project-setup/project/${PS_SP_Project_Id}
${external_spendprofile_summary}    ${server}/project-setup/project/${PS_SP_Project_Id}/partner-organisation/${Ooba_Lead_Org_Id}/spend-profile
${project_duration}    48
&{lead_applicant_credentials_sp}  email=${PS_SP_Lead_PM_Email}  password=${short_password}
&{collaborator1_credentials_sp}   email=${PS_SP_Partner_Email}   password=${short_password}
&{collaborator2_credentials_sp}   email=${PS_SP_Academic_Partner_Email}  password=${short_password}

*** Test Cases ***
Internal user can not generate SP with pending invites
    [Documentation]  IFS-6732
    Given the user invites a new partner org
    And the user can not generates the Spend Profile
    [Teardown]  the user removes a new partner org

Check if target start date can be changed until SP approval
    [Documentation]    IFS-1576
    [Tags]  HappyPath
    Given log in as a different user         &{ifs_admin_user_credentials}
    When the user navigates to the page      ${server}/project-setup-management/competition/${PS_Competition_Id}/project/${PS_SP_Project_Id}/details
    And the user changes the start date      2021
    Then the user should see the element     jQuery = #start-date:contains("1 Jan 2021")
    [Teardown]  the user changes the start date   2020

Project Finance user generates the Spend Profile
    [Documentation]    INFUND-5194
    [Tags]  HappyPath
    [Setup]  log in as a different user     &{internal_finance_credentials}
    Given the user navigates to the page    ${server}/project-setup-management/project/${PS_SP_Project_Id}/finance-check
    Then the user should see finance eligibilty approved and able to generate spend profile

Project Finance cancels the generation of the Spend Profile
    [Documentation]    INFUND-5194
    [Tags]  HappyPath
    Given the user clicks the button/link    css = .generate-spend-profile-main-button
    When the user should see the element     jQuery = p:contains("This will generate a flat spend profile for all project partners.")
    Then the user clicks the button/link     jQuery = button:contains("Cancel")

# Below 2 Query/SP tests are added in this file as they depend on approving all pre-requisites and generating SP
Project finance sends a query to lead organisation
    [Documentation]    IFS-2062
    [Tags]
    Given the user navigates to the page      ${server}/project-setup-management/project/${PS_SP_Project_Id}/finance-check/organisation/${Ooba_Lead_Org_Id}/query
    Then the project finance user post a new query

Lead partner responds to query
    [Documentation]    IFS-2062
    [Tags]
    [Setup]  Log in as a different user        &{lead_applicant_credentials_sp}
    Given the user navigates to the page       ${server}/project-setup/project/${PS_SP_Project_Id}/finance-checks
    And the user clicks the button/link        link = Respond
    When the user enters text to a text field  css = .editor  Responding to finance query
    Then the user clicks the button/link       jQuery = .govuk-button:contains("Post response")

Project Finance goes through the Generate Spend Profile tab to generate the Spend Profile and should not see query responses flagged
    [Documentation]    INFUND-5194, INFUND-5987, IFS-2062, IFS-2016
    [Tags]  HappyPath
    [Setup]  log in as a different user     &{internal_finance_credentials}
    Given the user navigates to the page    ${server}/project-setup-management/competition/${PS_Competition_Id}/status/all
    Then the project finance user generate spend profile
    And the project finance user should not see query responses flagged

Project Finance should no longer see the project in the Generate Spend Profile tab
    [Documentation]    IFS-2016
    [Tags]  HappyPath
    Given the user navigates to the page      ${server}/project-setup-management/competition/${PS_SP_Project_Id}/status/pending-spend-profiles
    Then the user should not see the element  link = ${PS_SP_Application_Title}

Lead partner can view spend profile page
    [Documentation]    INFUND-3970, INFUND-6138, INFUND-5899, INFUND-7685
    [Tags]  HappyPath
    [Setup]    Log in as a different user            &{lead_applicant_credentials_sp}
    Given the user clicks the button/link            link = ${PS_SP_Application_Title}
    Then the lead partner can view the generated spend profile
    [Teardown]    the user goes back to the previous page

Lead partner can see project details and calculations on spend profile
    [Documentation]    INFUND-3970  INFUND-3764  INFUND-6148
    [Tags]
    Given the lead partner can see correct project start date and duration
    And the lead partner can see calculations in the spend profile table

Lead Partner can see Spend profile summary
    [Documentation]    INFUND-3971, INFUND-6148
    [Tags]  HappyPath
    Given the user navigates to the page     ${external_spendprofile_summary}/review
    Then the user should see the element     jQuery = .govuk-main-wrapper th:contains("Financial year") + th:contains("Project spend")
    And the user should see the element      jQuery = .govuk-main-wrapper table tr:nth-child(1) td:nth-child(2):contains("£12,668")

Spend profile: validations
    [Documentation]  INFUND-3765, INFUND-6907, INFUND-6801, INFUND-7409, INFUND-6148 INFUND-6146
    [Tags]  HappyPath
    Given the user clicks the button/link       jQuery = a:contains("Edit spend profile")
    Then the lead partner can edit his spend profile with invalid values and see the error messages

Lead partner can edit his spend profile with valid values
    [Documentation]    INFUND-3765
    [Tags]  HappyPath
    Given the user navigates to the page       ${external_spendprofile_summary}/review
    When the user clicks the button/link       jQuery = a:contains("Edit spend profile")
    Then the user enter valid values and save changes

Project Manager can see Spend Profile in Progress
    [Documentation]    done during refactoring, no ticket attached
    [Tags]  HappyPath
    [Setup]  Log in as a different user      &{lead_applicant_credentials_sp}
    Given the user navigates to the page     ${external_spendprofile_summary}
    Then the user should see the element     link = ${Ooba_Lead_Org_Name}
    And the user should see the element      jQuery = .task-list li:nth-child(1):contains("In progress")

Lead partner marks spend profile as complete
    [Documentation]    INFUND-3765, INFUND-6138
    [Tags]  HappyPath
    [Setup]  Log in as a different user              &{lead_applicant_credentials_sp}
    Given the user navigates to the page             ${external_spendprofile_summary}/review
    Then the user marks spend profile as compelete and check status updated

Non-lead partner can view spend profile page
    [Documentation]    INFUND-3970, INFUND-6138, INFUND-5899
    [Tags]  HappyPath
    [Setup]    Log in as a different user            &{collaborator1_credentials_sp}
    Given the user clicks the button/link            link = ${PS_SP_Application_Title}
    Then the non lead partner able to see the submitted spend profile
    [Teardown]    the user goes back to the previous page

Non-lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the element         jQuery = dt:contains("Project start date") ~ dd:contains("1 January 2021")
    And the user should see the element          jQuery = dt:contains("Duration") ~ dd:contains("${project_duration} months")

Industrial partner can choose cancel on the dialogue
    [Documentation]    INFUND-6852
    Given the user clicks the button/link  link = Submit to lead partner
    When the user clicks the button/link   jQuery = button:contains("Cancel")
    Then the user should see the element   link = Submit to lead partner

Non-lead partner marks Spend Profile as complete
    [Documentation]    INFUND-3767
    [Tags]
    Given the user clicks the button/link          link = Submit to lead partner
    When the user clicks the button/link           jQuery = .govuk-button:contains("Submit")
    Then the user should see the element           jQuery = p:contains("We have reviewed and confirmed your project costs")
    And the user should not see the element        css = table a[type = "number"]    # checking here that the table has become read-only

Status updates for industrial user after spend profile submission
    [Documentation]    INFUND-6881
    [Setup]  the user navigates to the page    ${server}/project-setup/project/${PS_SP_Project_Id}
    Given the user should see the element     css = ul li.complete:nth-child(6)
    When the user clicks the button/link    link = View the status of partners
    Then the user should see the element    css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(7)
    And the user should see the element     css = #table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(7)

Academic partner can view spend profile page
    [Documentation]    INFUND-3970, INFUND-5899
    [Tags]  HappyPath
    [Setup]    Log in as a different user           &{collaborator2_credentials_sp}
    Given the user clicks the button/link           link = ${PS_SP_Application_Title}
    When the user clicks the button/link            link = Spend profile
    Then the user should not see an error in the page
    And the user should see the element             jQuery = p:contains("We have reviewed and confirmed your project costs.")
    And the user should see the element             jQuery = h2:contains("${Jabbertype_Partner_Org_Name} - Spend profile")
    And the user clicks the button/link             link = Set up your project
    And the user should see the element             jQuery = .message-alert:contains("You must complete your project and bank details within 30 days of our notification to you.")
    [Teardown]    the user goes back to the previous page

Academic partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the element         jQuery = dt:contains("Project start date") ~ dd:contains("1 January 2021")
    And the user should see the element          jQuery = dt:contains("Duration") ~ dd:contains("${project_duration} months")

Academic partner can see the alternative academic view of the spend profile
    [Documentation]    INFUND-4819
    [Tags]
    Then the user should see the element       jQuery = th:contains("Je-S category")
    And the user should see the element        jQuery = th:contains("Exceptions")

Academic partner spend profile: validations
    [Documentation]    INFUND-5846
    [Tags]
    Given the user clicks the button/link            jQuery = a:contains("Edit spend profile")
    And the user enters text to a text field         css = .spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(3) input    3306  # Travel and subsistence
    And Set Focus To Element                         css = .spend-profile-table tbody .form-group-row:nth-child(7) td:nth-of-type(6) input
    Then the user should see a summary error         Your total costs are higher than your eligible costs.
    And academic partner enter valid values in spend profile then should'e see validation error messages


Academic partner edits spend profile and this updates on the table
    [Documentation]    INFUND-5846
    [Tags]
    When the user clicks the button/link    jQuery = button:contains("Save and return to spend profile overview")
    Then the user should see the element    jQuery = a:contains("Edit spend profile")
    And element should contain              css = .spend-profile-table tbody tr:nth-of-type(1) td:nth-of-type(1)    3
    And element should contain              css = .spend-profile-table tbody tr:nth-of-type(2) td:nth-of-type(1)    1

Academic partner marks Spend Profile as complete
    [Documentation]    INFUND-3767
    [Tags]
    When the user clicks the button/link           link = Submit to lead partner
    And the user clicks the button/link            jQuery = button.govuk-button:contains("Submit")
    Then the user should see the element           jQuery = p:contains("We have reviewed and confirmed your project costs")
    And the user should not see the element        css = table a[type = "number"]    # checking here that the table has become read-only

Status updates for academic user after spend profile submission
    [Documentation]    INFUND-6881
    When the user navigates to the page     ${server}/project-setup/project/${PS_SP_Project_Id}
    Then the user should see the element    css = ul li.complete:nth-child(6)
    When the user clicks the button/link    link = View the status of partners
    Then the user should see the element    css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(7)
    And the user should see the element     css = #table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(7)

Project Manager can view partners' spend profiles
    [Documentation]    INFUND-3767, INFUND-3766, INFUND-5609
    [Tags]
    [Setup]  log in as a different user             &{lead_applicant_credentials_sp}
    Given the user clicks the button/link           link = ${PS_SP_Application_Title}
    When the user clicks the button/link            link = Spend profile
    Then project finance user can view all partners spend profile

Partners are not able to see the spend profile summary page
    [Documentation]    INFUND-3766
    [Tags]
    Given log in as a different user               &{collaborator1_credentials_sp}
    And the user navigates to the page and gets a custom error message  ${external_spendprofile_summary}    ${403_error_message}
    Given log in as a different user               &{collaborator2_credentials_sp}
    And the user navigates to the page and gets a custom error message  ${external_spendprofile_summary}    ${403_error_message}

Project Manager can view combined spend profile
    [Documentation]    INFUND-3767
    [Tags]
    [Setup]    log in as a different user            &{lead_applicant_credentials_sp}
    Given the user navigates to the page             ${external_spendprofile_summary}
    When the user clicks the button/link             jQuery = a:contains("Review and submit project spend profile")
    Then the user should see the element             jQuery = p:contains("This is the spend profile for your project.")
    And the user should see the element              jQuery = p:contains("Your submitted spend profile will be used as the base for your project spend over the following financial years.")

Project Manager can choose cancel on the dialogue
    [Documentation]    INFUND-3767
    When the user clicks the button/link  jQuery = a:contains("Submit project spend profile")
    And the user clicks the button/link   jQuery = button:contains("Cancel")
    Then the user should see the element  jQuery = a:contains("Submit project spend profile")

Project Manager can see the button Allow edits
    [Documentation]    INFUND-6350
    [Tags]
    Given the user navigates to the page    ${server}/project-setup/project/${PS_SP_Project_Id}/partner-organisation/${Ooba_Lead_Org_Id}/spend-profile
    And the user should see the element     jQuery = .task-list li:nth-child(1):contains("Complete")
    And the user should see the element     jQuery = .task-list li:nth-child(2):contains("Complete")
    When the user clicks the button/link    link = ${Wordpedia_Partner_Org_Name}
    Then the user should see the element    jQuery = .govuk-button:contains("Allow edits")

Other partners cannot enable edit-ability by themselves
    [Documentation]    INFUND-6350
    [Tags]
    [Setup]  log in as a different user       &{collaborator1_credentials_sp}
    When the user navigates to the page       ${server}/project-setup/project/${PS_SP_Project_Id}/partner-organisation/${Wordpedia_Partner_Org_Id}/spend-profile/review
    Then the user should not see the element  jQuery = .govuk-button:contains("Allow edits")

PM can return edit rights to partners
    [Documentation]    INFUND-6350
    [Tags]
    [Setup]  log in as a different user      &{lead_applicant_credentials_sp}
    Given the user navigates to the page     ${server}/project-setup/project/${PS_SP_Project_Id}/partner-organisation/${Wordpedia_Partner_Org_Id}/spend-profile/review
    When the user clicks the button/link     jQuery = .govuk-button:contains("Allow edits")
    And the user clicks the button/link      jQuery = .govuk-button:contains("Allow partner to edit")
    Then the user navigates to the page      ${server}/project-setup/project/${PS_SP_Project_Id}/partner-organisation/${Ooba_Lead_Org_Id}/spend-profile
    And the user should see the element      jQuery = .task-list li:nth-child(3):contains("In progress")

Partner can receive edit rights to his SP
    [Documentation]    INFUND-6350
    [Tags]
    [Setup]  log in as a different user     &{collaborator1_credentials_sp}
    Given the user navigates to the page    ${server}/project-setup/project/${PS_SP_Project_Id}
    Then the user should see the element    css = li.require-action:nth-child(7)
    When the user clicks the button/link    link = Spend profile
    Then the user should see the element    jQuery = a:contains("Edit spend profile")
    When the user clicks the button/link    link = Submit to lead partner
    And the user clicks the button/link     jQuery = button.govuk-button:contains("Submit")

Project Manager can send the project's spend profiles
    [Documentation]    INFUND-3767
    [Tags]
    [Setup]    log in as a different user    &{lead_applicant_credentials_sp}
    Given the user navigates to the page     ${external_spendprofile_summary}
    When the user clicks the button/link     jQuery = a:contains("Review and submit project spend profile")
    Then the user clicks the button/link     jQuery = a:contains("Submit project spend profile")
    And the user should see the element      jQuery = button:contains("Cancel")
    When the user clicks the button/link     id = submit-send-all-spend-profiles

PM's Spend profile Summary page gets updated after submit
    [Documentation]    INFUND-3766
    [Tags]
    Given the user navigates to the page     ${external_spendprofile_summary}
    Then the user should see the element     jQuery = .success-alert p:contains("All project spend profiles have been sent to Innovate UK.")
    And the user should not see the element  jQuery = a:contains("Submit project spend profile")

Status updates after spend profile submitted
    [Documentation]    INFUND-6225  INFUND-3767  INFUND-3766
    Given the user navigates to the page    ${server}/project-setup/project/${PS_SP_Project_Id}
    When the user clicks the button/link    link = View the status of partners
    And the user should see the element     css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(7)
    Then partners can see the Spend Profile section completed

Project Finance is able to see Spend Profile approval page
    [Documentation]    INFUND-2638, INFUND-5617, INFUND-3973, INFUND-5942 IFS-1871
    [Tags]
    [Setup]  Log in as a different user            &{internal_finance_credentials}
    Given the user navigates to the page             ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the project finance user should see the spend profile details

Comp Admin is able to see Spend Profile approval page
    [Documentation]    INFUND-2638, INFUND-5617, INFUND-6226, INFUND-5549
    [Tags]
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_Project_Id}/spend-profile/approval
    Then the comp admin should see the spend profile details

Comp Admin can download the Spend Profile csv
    [Documentation]    INFUND-3973, INFUND-5875
    [Tags]
    Given the user navigates to the page    ${server}/project-setup-management/project/${PS_SP_Project_Id}/spend-profile/approval
    And the user should see the element     jQuery = h1:contains("Spend profile")
    Then the comp admin can download the SP CSV files

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049 ,INFUND-5543, INFUND-7119
    [Tags]
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    When the user navigates to the page      ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the comp admin should see the SP status uodated correctly

Project Finance is able to Reject Spend Profile
    [Documentation]    INFUND-2638, INFUND-5617
    [Tags]
    [Setup]    Log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_Project_Id}/spend-profile/approval
    Then the project finance reject the SP

Status updates to a cross for the internal user's table
    [Documentation]    INFUND-6977
    [Tags]
    When the user navigates to the page     ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the user should see the element    css = #table-project-status tr:nth-of-type(6) td:nth-of-type(7).status.rejected  # Rejected Spend profile

Lead partner can see that the spend profile has been rejected
    [Documentation]    INFUND-6977
    [Tags]
    Given log in as a different user        &{lead_applicant_credentials_sp}
    When the user clicks the button/link    link = ${PS_SP_Application_Title}
    Then the user should see the element    css = li.lead-action-required:nth-child(7)
    When the user clicks the button/link    link = View the status of partners
    Then the user should see the element    css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(7)
    [Teardown]    the user goes back to the previous page

Non Lead partners should still see a tick instead of an hourglass when spend profile has been rejected
    [Documentation]    INFUND-7422
    [Tags]
    [Setup]  log in as a different user        &{collaborator1_credentials_sp}
    Given the user shouldn't see rejected SP message
    When log in as a different user        &{collaborator2_credentials_sp}
    Then the user shouldn't see rejected SP message

Lead partner no longer has the 'submitted' view of the spend profiles
    [Documentation]    INFUND-6977, INFUND-7422
    Given Log in as a different user            &{lead_applicant_credentials_sp}
    When the user clicks the button/link        link = ${PS_SP_Application_Title}
    And the user clicks the button/link         link = Spend profile
    Then the user should not see the element    jQuery = .success-alert p:contains("All project spend profiles have been sent to Innovate UK.")
    And the user should see the element         jQuery = a:contains("Review and submit project spend profile")

Lead partner can return edit rights to other project partners
    [Documentation]    INFUND-6977
    When the user returns edit rights for the organisation    ${Jabbertype_Partner_Org_Name}
    Then the user should see the element                      jQuery = li:nth-child(2) span:contains("In progress")
    When the user returns edit rights for the organisation    ${Wordpedia_Partner_Org_Name}
    Then the user should see the element                      jQuery = li:nth-child(3) span:contains("In progress")

Lead partner can edit own spend profile and mark as complete
    [Documentation]    INFUND-6977, INFUNF-7409
    When the user clicks the button/link    link = ${Ooba_Lead_Org_Name}
    And the user should see the element     jQuery = .success-alert:contains("Your spend profile is marked as complete")
    And the user clicks the button/link     jQuery = a:contains("Edit spend profile")
    And the user clicks the button/link     jQuery = button:contains("Save and return to spend profile overview")
    And the user clicks the button/link     css = [name = "mark-as-complete"]

Industrial partner receives edit rights and can submit their spend profile
    [Documentation]    INFUND-6977
    Given log in as a different user        &{collaborator1_credentials_sp}
    Then Industrial/academic partner able to edit SP after receiving rights from lead   3

Academic partner receives edit rights and can submit their spend profile
    [Documentation]    INFUND-6977
    Given log in as a different user        &{collaborator2_credentials_sp}
    Then Industrial/academic partner able to edit SP after receiving rights from lead   2

Lead partner can send the combined spend profile
    [Documentation]    INFUND-6977
    [Setup]    log in as a different user    &{lead_applicant_credentials_sp}
    Given the user navigates to the page     ${external_spendprofile_summary}
    When the user clicks the button/link     jQuery = a:contains("Review and submit project spend profile")
    Then the user clicks the button/link     jQuery = a:contains("Submit project spend profile")
    And the user should see the element      jQuery = button:contains("Cancel")
    When the user clicks the button/link     id = submit-send-all-spend-profiles

Project Finance is able to Approve Spend Profile
    [Documentation]    INFUND-2638, INFUND-5617, INFUND-5507, INFUND-5549
    [Tags]
    [Setup]    log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page     ${server}/project-setup-management/project/${PS_SP_Project_Id}/spend-profile/approval
    Then the project finance approves to SP

Status updates correctly for internal user's table after approval
    [Documentation]    INFUND-5543
    [Tags]
    When the user navigates to the page     ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the user should see the element    css = #table-project-status tr:nth-of-type(8) td:nth-of-type(7).status.ok        # Completed Spend profile
    And the user should see the element     css = #table-project-status > tbody > tr:nth-child(8) > td.govuk-table__cell.status.action > a   # GOL

Project Finance still has a link to the spend profile after approval
    [Documentation]    INFUND-6046
    [Tags]
    When the user clicks the button/link           jQuery = th:contains("${PS_SP_Application_Title}") ~ td:nth-child(8) a
    Then the user clicks the button/link           link = ${Ooba_Lead_Org_Name}-spend-profile.csv
    And the user clicks the button/link            link = ${Wordpedia_Partner_Org_Name}-spend-profile.csv
    And the user clicks the button/link            link = ${Jabbertype_Partner_Org_Name}-spend-profile.csv
    And the user should see the element            jQuery = h2:contains("The spend profile has been approved.")

Project finance user cannot access external users' spend profile page
    [Documentation]    INFUND-5911
    [Tags]
    When the user navigates to the page and gets a custom error message  ${server}/project-setup/project/${PS_SP_Project_Id}/partner-organisation/${Ooba_Lead_Org_Id}/spend-profile    ${403_error_message}

*** Keywords ***
the sum of tds equals the total
    [Arguments]    ${table}    ${row}    ${duration}    ${total}
    # This Keyword performs a for loop that iterates per column (in a specific row)
    # gets the sum of the cells and evaluates whether the sum of them equals their total
    ${sum} =    convert to number    0
    ${total} =    convert to number    ${total}
    : FOR    ${i}    IN RANGE    2    ${duration}    # due to header in the first column
    \    ${text} =    Get Text    css = ${table} tr:nth-child(${row}) td:nth-child(${i})
    \    ${formatted} =    Remove String    ${text}    ,    # Remove the comma from the number
    \    ${cell} =    convert to integer    ${formatted}
    \    ${sum} =    Evaluate    ${sum}+${cell}
    Should Be Equal As Integers    ${sum}    ${total}

the user should see all spend profiles as complete
    the user should see the element  jQuery = .task-list li:contains(${Ooba_Lead_Org_Name}) span:contains("Complete")
    the user should see the element  jQuery = .task-list li:contains(${Wordpedia_Partner_Org_Name}) span:contains("Complete")
    the user should see the element  jQuery = .task-list li:contains(${Jabbertype_Partner_Org_Name}) span:contains("Complete")

the user returns edit rights for the organisation
    [Arguments]    ${org_name}
    the user clicks the button/link             link = ${org_name}
    the user clicks the button/link             jQuery = .govuk-button:contains("Allow edits")
    the user clicks the button/link             jQuery = .govuk-button:contains("Allow partner to edit")

check if project manager, project address and finance contact fields are editable
    [Arguments]  ${project}
    the user navigates to the page  ${server}/project-setup/project/${project}/details
    check if project address can be changed
    check if project manager can be changed
    check if finance contact can be changed

check if project address can be changed
    the user clicks the button/link                    jQuery = a:contains("Correspondence address")
    the user clicks the button/link                    jQuery = button:contains("Save address")

check if project manager can be changed
    the user clicks the button/link                    jQuery = a:contains("Project Manager")
    the user selects the radio button                  projectManager  projectManager2
    the user clicks the button/link                    jQuery = button:contains("Save")
    the user clicks the button/link                    jQuery = a:contains("Project Manager")
    the user sees that the radio button is selected    projectManager  projectManager2
    the user selects the radio button                  projectManager  projectManager1
    the user clicks the button/link                    jQuery = button:contains("Save")

# Finance contact is changed below and switched back to original value so that
# the tests which follow this are not impacted by permissions error
check if finance contact can be changed
    the user clicks the button/link                    jQuery = a:contains("${Ooba_Lead_Org_Name}")
    the user selects the radio button                  financeContact  financeContact2
    the user clicks the button/link                    jQuery = button:contains("Save")
    the user clicks the button/link                    jQuery = a:contains("${Ooba_Lead_Org_Name}")
    the user sees that the radio button is selected    financeContact  financeContact2
    the user selects the radio button                  financeContact  financeContact1
    the user clicks the button/link                    jQuery = button:contains("Save")

the project finance user downloads the spend profile file and checks the content of it
    [Arguments]  ${file}
    the user downloads the file  ${internal_finance_credentials["email"]}  ${server}/project-setup-management/project/${PS_SP_Project_Id}/partner-organisation/${Ooba_Lead_Org_Id}/spend-profile-export/csv  ${DOWNLOAD_FOLDER}/${file}
    the user opens the csv file and checks the content  ${file}
    remove the file from the operating system  ${file}

the user opens the csv file and checks the content
    [Arguments]  ${file}
    ${contents} =          read csv file  ${DOWNLOAD_FOLDER}/${file}
    ${labourRow} =         get from list  ${contents}  1
    ${labour} =            get from list  ${labourRow}  0
    should be equal        ${labour}  Labour
    ${labourFirstMonth} =  get from list  ${labourRow}  1
    should be equal        ${labourFirstMonth}  14.00
    ${overheadsRow} =      get from list  ${contents}  2
    ${overheads} =         get from list  ${overheadsRow}  0
    should be equal        ${overheads}  Overheads
    ${materialsRow} =      get from list  ${contents}  3
    ${materials} =         get from list  ${materialsRow}  0
    should be equal        ${materials}  Materials
    ${capitalUsageRow} =   get from list  ${contents}  4
    ${capitalUsage} =      get from list  ${capitalUsageRow}  0
    should be equal        ${capitalUsage}  Capital usage
    ${subcontractingRow} =  get from list  ${contents}  5
    ${subcontracting} =    get from list  ${subcontractingRow}  0
    should be equal        ${subcontracting}  Subcontracting
    ${travelRow} =         get from list  ${contents}  6
    ${travel} =            get from list  ${travelRow}  0
    should be equal        ${travel}  Travel and subsistence
    ${otherCostsRow} =     get from list  ${contents}  7
    ${otherCosts} =        get from list  ${otherCostsRow}  0
    should be equal        ${otherCosts}  Other costs
    ${totalRow} =          get from list  ${contents}  8
    ${totalFirstMonth} =   get from list  ${totalRow}  1
    should be equal        ${totalFirstMonth}  4243.00

the user should see finance eligibilty approved and able to generate spend profile
    the user should see the element     jQuery = a.eligibility-0:contains("Approved")
    the user should see the element     jQuery = a.eligibility-1:contains("Approved")
    the user should see the element     jQuery = a.eligibility-2:contains("Approved")
    the user should see the element     css = .generate-spend-profile-main-button

the project finance user post a new query
    the user clicks the button/link         link = Post a new query
    the user enters text to a text field    id = queryTitle  Eligibility query's title
    the user enters text to a text field    css = .editor    Eligibility query
    the user clicks the button/link         jQuery = .govuk-button:contains("Post query")

the project finance user generate spend profile
    the user clicks the button/link     jQuery = a:contains("Generate spend profile")
    the user clicks the button/link     link = ${PS_SP_Application_Title}
    the user clicks the button/link     css = .generate-spend-profile-main-button
    the user clicks the button/link     css = #generate-spend-profile-modal-button
    the user should see the element     jQuery = .success-alert p:contains("The finance checks have been approved and profiles generated.")

the project finance user should not see query responses flagged
    the user navigates to the page         ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    the user should see the element        css = #table-project-status tr:nth-of-type(6) td:nth-of-type(4).ok
    the user navigates to the page         ${server}/project-setup-management/competition/${PS_Competition_Id}/status/queries
    the user should not see the element    link = ${Ooba_Lead_Org_Name}
    the user reads his email               ${PS_SP_Lead_PM_Email}  ${PS_Competition_Name}: Your spend profile is available for project ${PS_SP_Application_No}  The finance checks for all partners in the project have now been completed

the lead partner can view the generated spend profile
    the user clicks the button/link             link = View the status of partners
    the user should see the element             jQuery = h1:contains("Project team status")
    the user should see the element             css = #table-project-status tr:nth-of-type(1) td:nth-of-type(7).action
    the user clicks the button/link             link = Set up your project
    the user should see the element             css = li.require-action:nth-child(7)
    the user clicks the button/link             link = Spend profile
    the user should not see the element         link = Total project profile spend
    the user clicks the button/link             link = ${Ooba_Lead_Org_Name}
    the user should not see an error in the page
    the user should see the element              jQuery = p:contains("We have reviewed and confirmed your project costs.")
    the user should see the element              jQuery = h2:contains("${Ooba_Lead_Org_Name} - Spend profile")
    the user clicks the button/link              link = Spend profile overview
    the user should see the element              jQUery = p:contains("This overview shows the spend profile status of each organisation in your project.")

the lead partner can see correct project start date and duration
    the user should see the element         jQuery = dt:contains("Project start date") ~ dd:contains("1 January 2021")
    the user should see the element          jQuery = dt:contains("Duration") ~ dd:contains("${project_duration} months")

the lead partner can see calculations in the spend profile table
    the user should see the element    css = .spend-profile-table
    the user should see the element    jQuery = th:contains("Labour") ~ td.fix-right:contains("£3,081")
    the user should see the element    jQuery = th:contains("Overheads") ~ td.fix-right:contains("£0")
    the user should see the element    jQuery = th:contains("Materials") ~ td.fix-right:contains("£100,200")
    the user should see the element    jQuery = th:contains("Capital usage") ~ td.fix-right:contains("£552")
    the user should see the element    jQuery = th:contains("Subcontracting") ~ td.fix-right:contains("£90,000")
    the user should see the element    jQuery = th:contains("Travel and subsistence") ~ td.fix-right:contains("£5,970")
    the user should see the element    jQuery = th:contains("Other costs") ~ td.fix-right:contains("£1,100")
    #${duration} is No of Months + 1, due to header
    the sum of tds equals the total    .spend-profile-table  1  50  3081    # Labour
    the sum of tds equals the total    .spend-profile-table  3  50  100200  # Materials
    the sum of tds equals the total    .spend-profile-table  5  50  90000   # Subcontracting
    the sum of tds equals the total    .spend-profile-table  6  50  5970    # Travel & subsistence
    the sum of tds equals the total    .spend-profile-table  7  50  1100    # Other costs

the lead partner can edit his spend profile with invalid values and see the error messages
    the user enters text to a text field    jQuery = th:contains("Labour") + td input   520
    Set Focus To Element                    jQuery = th:contains("Overheads") + td input
    the user should see the element         jQuery = .govuk-error-summary:contains("Unable to submit spend profile.")
    the user should see the element         jQuery = .govuk-form-group--error th:contains("Labour")
    the user should see the element         jQuery = th:contains("Labour") ~ .fix-right.cell-error input[data-calculation-rawvalue = "3528"]
    # Project costs for financial year are instantly reflecting the financial values INFUND-3971, INFUND-6148
    the user should see the element         jQuery = .govuk-main-wrapper table tr:nth-child(1) td:nth-child(2):contains("£13,115")
    the user clicks the button/link         jQuery = button:contains("Save and return to spend profile overview")
    the user should see the element         jQuery = .govuk-error-summary:contains("Your total costs are higher than the eligible project costs.")
    the user clicks the button/link         jQuery = a:contains("Edit spend profile")
    the user enters text to a text field    jQuery = th:contains("Labour") + td input  10
    the user should not see the element     jQuery = .govuk-form-group--error th:contains("Labour")
    the user should not see an error in the page
    the user enters text to a text field    jQuery = th:contains("Overheads") ~ td:nth-child(4) input  0
    Set Focus To Element                    css = .spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(2) input
    the user should not see the element     css = .govuk-error-summary__list

the user enter valid values and save changes
    the user should see the element         css = table [type = "number"]    # checking here that the table is not read-only
    the user should see the element         jQuery = th:contains("Labour") + td input
    the user enters text to a text field    jQuery = th:contains("Labour") + td input  14
    Set Focus To Element                    jQuery = th:contains("Labour") ~ td:nth-child(4) input
    the user should see the element         jQuery = th:contains("Labour") ~ td.fix-right input[data-calculation-rawvalue = "3022"]
    the user enters text to a text field    jQuery = th:contains("Subcontracting") ~ td:nth-child(5) input  0
    Set Focus To Element                    jQuery = th:contains("Subcontracting") ~ td:nth-child(7) input
    the user should see the element         jQuery = th:contains("Subcontracting") ~ td.fix-right input[data-calculation-rawvalue = "90000"]
    the user should not see the element     jQuery = .govuk-error-summary:contains("Unable to save spend profile")
    the user clicks the button/link         jQuery = button:contains("Save and return to spend profile overview")
    the user should not see the element     jQuery = .govuk-error-summary:contains("Your total costs are higher than the eligible project costs.")

the user marks spend profile as compelete and check status updated
    the user clicks the button/link             css = [name = "mark-as-complete"]
    the user should not see the element         jQuery = .success-alert p:contains("Your spend profile is marked as complete. You can still edit this page.")
    the user should not see the element         css = table a[type = "number"]    # checking here that the table has become read-only
    the user clicks the button/link             link = Set up your project
    the user clicks the button/link             link = View the status of partners
    the user should see the element             jQuery = h1:contains("Project team status")
    the user should see the element             css = #table-project-status tr:nth-of-type(1) td:nth-of-type(7).action
    the user clicks the button/link             link = Set up your project
    the user should see the element             css = li.lead-action-required:nth-child(7)
    the user should not see the element         link = Grant offer letter
    project Manager doesn't have the option to send spend profiles until all partners have marked as complete

the non lead partner able to see the submitted spend profile
    the user clicks the button/link       link = View the status of partners
    the user should see the element       jQuery = h1:contains("Project team status")
    the user should see the element       css = #table-project-status tr:nth-of-type(1) td:nth-of-type(7).action
    the user clicks the button/link       link = Set up your project
    the user should see the element       css = li.require-action:nth-child(7)
    the user clicks the button/link       link = Spend profile
    the user should not see an error in the page
    the user should see the element       jQuery = p:contains("We have reviewed and confirmed your project costs.")
    the user should see the element       jQuery = h2:contains("${Wordpedia_Partner_Org_Name} - Spend profile")
    the user clicks the button/link       link = Set up your project
    the user should see the element       jQuery = .message-alert:contains("You must complete your project and bank details within 30 days of our notification to you.")

project Manager doesn't have the option to send spend profiles until all partners have marked as complete
    the user clicks the button/link        link = Spend profile
    the user should not see the element    jQuery = .govuk-button:contains("Review spend profiles")

the user should see the validation messages triggred
    the user should see a summary error         Your total costs are higher than your eligible costs.
    the user clicks the button/link             jQuery = button:contains("Save and return to spend profile overview")

academic partner enter valid values in spend profile then should'e see validation error messages
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(1) td:nth-of-type(1) input    3  # Staff
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(2) td:nth-of-type(1) input    1  # Travel
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(3) td:nth-of-type(1) input    1  # Other - Directly incurred
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(5) td:nth-of-type(1) input    2  # Estates
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(1) input    0  # Other - Directly allocated
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(9) td:nth-of-type(1) input    0  # Other - Exceptions
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(2) input   0  # Other - Directly allocated
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(6) td:nth-of-type(3) input    0  # Other - Directly allocated
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(9) td:nth-of-type(2) input    0  # Other - Exceptions
    the user enters text to a text field     css = .spend-profile-table tbody .form-group-row:nth-child(9) td:nth-of-type(3) input    0  # Other - Exceptions
    the user should not see the element      jQuery = .govuk-error-message:contains("Your total costs are higher than your eligible costs")

project finance user can view all partners spend profile
    the user should not see an error in the page
    the user clicks the button/link            link = ${Ooba_Lead_Org_Name}
    the user should see the element            jQuery = p:contains("We have reviewed and confirmed your project costs")
    the user goes back to the previous page
    the user clicks the button/link            link = ${Wordpedia_Partner_Org_Name}
    the user should see the element            jQuery = p:contains("We have reviewed and confirmed your project costs")
    the user should not see the element        jQuery = .govuk-button:contains("Edit")
    the user should not see the element        css = [name = "mark-as-complete"]
    the user goes back to the previous page
    the user clicks the button/link            link = ${Jabbertype_Partner_Org_Name}
    the user should see the element            jQuery = p:contains("We have reviewed and confirmed your project costs")
    the user should not see the element        jQuery = .govuk-button:contains("Edit")
    the user should not see the element        css = [name = "mark-as-complete"]
    the user goes back to the previous page
    the user should see all spend profiles as complete
    the user should see the element            jQuery = a:contains("Review and submit project spend profile")

partners can see the Spend Profile section completed
    Log in as a different user          &{lead_applicant_credentials_sp}
    the user clicks the button/link     link = ${PS_SP_Application_Title}
    the user should see the element     css = li.waiting:nth-of-type(7)
    Log in as a different user          &{collaborator1_credentials_sp}
    the user clicks the button/link     link = ${PS_SP_Application_Title}
    the user should see the element     css = li.complete:nth-of-type(7)
    Log in as a different user          &{collaborator2_credentials_sp}
    the user clicks the button/link     link = ${PS_SP_Application_Title}
    the user should see the element     css = li.complete:nth-of-type(7)

the project finance user should see the spend profile details
    the user navigates to the page               ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    the user clicks the button/link              css = #table-project-status > tbody > tr:nth-child(6) > td.govuk-table__cell.status.action > a  # Review Spend profile
    the user should be redirected to the correct page    ${server}/project-setup-management/project/${PS_SP_Project_Id}/spend-profile/approval
    the user should not see the element          jQuery = h2:contains("The spend profile has been approved")
    the user should not see the element          jQuery = h2:contains("The spend profile has been rejected")
    the user should see the element              jQuery = h2:contains("Innovation Lead") ~ p:contains("Peter Freeman")
    the user should see the element              jQuery = h2:contains("Project spend profile")
    the project finance user downloads the spend profile file and checks the content of it  ${Ooba_Lead_Org_Name}-spend-profile.csv
    the user should see the element              link = ${Wordpedia_Partner_Org_Name}-spend-profile.csv
    the user should see the element              link = ${Jabbertype_Partner_Org_Name}-spend-profile.csv
    the user should see the element              jQuery = .govuk-main-wrapper h2:contains("Approved by Innovate UK")
    the element should be disabled               css = #accept-profile
    the user selects the checkbox                approvedByLeadTechnologist
    the user should see the element              css = #accept-profile
    the user should see the element              jQuery = #main-content button:contains("Reject")

the comp admin should see the spend profile details
    the element should be disabled       css = #accept-profile
    the user should see the element      jQuery = #main-content button:contains("Reject")
    the user should see the element      jQuery = h2:contains("Innovation Lead") ~ p:contains("Peter Freeman")
    the user clicks the button/link      jQuery = #main-content button:contains("Reject")
    the user should see the element      jQuery = p:contains("You should contact the Project Manager to explain why the spend profile is being returned.")
    the user clicks the button/link      jQuery = .modal-reject-profile button:contains("Cancel")
    the user should not see an error in the page
    the user selects the checkbox        approvedByLeadTechnologist
    the user should see the element      css = #accept-profile
    the user clicks the button/link      jQuery = button:contains("Approved")
    the user should see the element      jQuery = .modal-accept-profile h2:contains("Approved by Innovate UK")
    the user clicks the button/link      jQuery = .modal-accept-profile button:contains("Cancel")
    the user should not see an error in the page

the comp admin can download the SP CSV files
    the user should see the element     link = ${Ooba_Lead_Org_Name}-spend-profile.csv
    the user should see the element     link = ${Wordpedia_Partner_Org_Name}-spend-profile.csv
    the user should see the element     link = ${Jabbertype_Partner_Org_Name}-spend-profile.csv
    the user clicks the button/link     link = ${Ooba_Lead_Org_Name}-spend-profile.csv
    the user should not see an error in the page
    the user clicks the button/link     link = ${Wordpedia_Partner_Org_Name}-spend-profile.csv
    the user should not see an error in the page
    the user clicks the button/link     link = ${Jabbertype_Partner_Org_Name}-spend-profile.csv
    the user should not see an error in the page

the comp admin should see the SP status uodated correctly
    the user should see the element        css = #table-project-status tr:nth-of-type(6) td:nth-of-type(1).status.ok         # Project details
    the user should see the element        css = #table-project-status > tbody > tr:nth-child(6) > td:nth-child(3)           # Documents
    the user should see the element        css = #table-project-status > tbody > tr:nth-child(6) > td:nth-child(4)           # Monitoring officer
    the user should see the element        css = #table-project-status > tbody > tr:nth-child(6) > td:nth-child(5)           # Bank details
    the user should see the element        css = #table-project-status > tbody > tr:nth-child(6) > td:nth-child(6)           # Finance checks
    the user should see the element        css = #table-project-status > tbody > tr:nth-child(6) > td:nth-child(7)           # Spend profile
    the user should see the element        css = #table-project-status > tbody > tr:nth-child(6) > td.govuk-table__cell.status.action  # GOL
    the user should not see the element    css = #table-project-status tr:nth-of-type(6) td:nth-of-type(7).status.waiting    # specifically checking regression issue INFUND-7119

the project finance reject the SP
    the user should see the element     jQuery = #main-content button:contains("Reject")
    the user clicks the button/link     jQuery = #main-content button:contains("Reject")
    the user should see the element     jQUery = p:contains("You should contact the Project Manager to explain why the spend profile is being returned.")
    the user clicks the button/link     jQuery = .modal-reject-profile button:contains("Cancel")
    the user should not see an error in the page
    the user clicks the button/link     jQuery = #main-content button:contains("Reject")
    the user clicks the button/link     jQuery = .modal-reject-profile button:contains('Reject')

the user shouldn't see rejected SP message
    the user clicks the button/link    link = ${PS_SP_Application_Title}
    the user should see the element    css = li.complete:nth-of-type(6)
    the user clicks the button/link    link = View the status of partners
    the user should see the element    css = #table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(5)

Industrial/academic partner able to edit SP after receiving rights from lead
    [Arguments]  ${user_row id}
    the user clicks the button/link    link = ${PS_SP_Application_Title}
    the user should see the element    css = li.require-action:nth-of-type(7)
    the user clicks the button/link    link = View the status of partners
    the user should see the element    css = #table-project-status tr:nth-of-type(2) td.status.action:nth-of-type(7)
    the user goes back to the previous page
    the user clicks the button/link    link = Spend profile
    the user clicks the button/link    link = Submit to lead partner
    the user clicks the button/link    jQuery = button.govuk-button:contains("Submit")
    the user should see the element    jQuery = .success-alert:contains("Your spend profile has been sent to the lead partner")
    the user goes back to the previous page
    the user clicks the button/link     link = Set up your project
    the user clicks the button/link     link = View the status of partners
    the user should see the element     css = #table-project-status tr:nth-of-type(${user_row id}) td.status.ok:nth-of-type(7)

the project finance approves to SP
    the user selects the checkbox            approvedByLeadTechnologist
    the user should see the element          jQuery = button:contains("Approved")
    the user should see the element          jQuery = h2:contains("Innovation Lead") ~ p:contains("Peter Freeman")
    the user clicks the button/link          jQuery = button:contains("Approved")
    the user should see the element          jQuery = .modal-accept-profile h2:contains("Approved by Innovate UK")
    the user clicks the button/link          jQuery = .modal-accept-profile button:contains("Cancel")
    the user should not see an error in the page
    the user clicks the button/link          jQuery = button:contains("Approved")
    the user clicks the button/link          jQuery = .modal-accept-profile button:contains("Approve")
    the user should see the element          jQuery = th div:contains("${PS_SP_Application_Title}")
    the user should not see the element      jQuery = h3:contains("The spend profile has been approved")

the user invites a new partner org
    the user logs-in in new browser             &{internal_finance_credentials}
    the user navigates to the page              ${server}/project-setup-management/competition/${PS_Competition_Id}/project/${PS_SP_Project_Id}/team/partner
    the user adds a new partner organisation    Spend Profile Organisation  FName Surname  spendprofile@test.com

the user can not generates the Spend Profile
    the user navigates to the page          ${server}/project-setup-management/project/${PS_SP_Project_Id}/finance-check
    the user clicks the button/link         css = .generate-spend-profile-main-button
    the user clicks the button/link         css = #generate-spend-profile-modal-button
    the user should see a summary error     You cannot generate the spend profile because a new partner has not accepted their invitation to join the project.
    the user navigates to the page          ${server}/project-setup-management/competition/${PS_Competition_Id}/status/all
    the user should not see the element     jQuery = #table-project-status tr:nth-of-type(6) td:nth-of-type(7).waiting

the user removes a new partner org
    the user navigates to the page                               ${server}/project-setup-management/competition/${PS_Competition_Id}/project/${PS_SP_Project_Id}/team
    the user is able to remove a pending partner organisation    Spend Profile Organisation