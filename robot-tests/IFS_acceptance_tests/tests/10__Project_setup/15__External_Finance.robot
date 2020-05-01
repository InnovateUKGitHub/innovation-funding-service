*** Settings ***
Documentation     IFS-7357   Allowing external users to complete viability & eligibility checks
...
...               IFS-7365  DocuSign Integration
Suite Setup       Custom suite setup
Suite Teardown    The user closes the browser
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Applicant_Commons.robot

*** Variables ***
${externalReviewerComp}            Project Setup Comp 18
${externalReviewerCompId}          ${competition_ids['${externalReviewerComp}']}
${externalReviewerApplication}     PSC application 18
${externalReviewerApplicationId}   ${application_ids['${externalReviewerApplication}']}
${externalReviewerProject}         PSC application 18
${externalReviewerProjectId}       ${project_ids['${externalReviewerApplication}']}
${exfinanceemail}                  exfinance@example.com
&{leadApplicantLogin}              email=troy.ward@gmail.com    password=Passw0rd

*** Test Cases ***
IFS admin is able to invite an external PF
    [Documentation]  IFS-7357
    [Setup]  Log in as a different user         &{ifs_admin_user_credentials}
    Given the user navigates to the page        ${server}/management/competition/setup/${externalReviewerCompId}
    When the user clicks the button/link        link = External finance reviewers
    And the user clicks the button/link         jQuery = span:contains("Invite a new external finance reviewer")
    Then complete external project finance details
    [Teardown]    logout as user

New external project finance can create account
    [Documentation]  IFS-7357
    Given the user reads his email and clicks the link   ${exfinanceemail}  Invitation to review an Innovation Funding Service competition  You have been invited  1
    When external project finance creates account
    Then the user should see the element                 link = ${externalReviewerComp}

External project finance can see Project details
    [Documentation]  IFS-7357
    Given the user clicks the button/link    link = Dashboard
    And The user clicks the button/link      link = ${externalReviewerComp}
    When the user clicks the button/link     jQuery = td.ok:nth-of-type(1)
    Then the user should see the element     jQuery = h1:contains("Project details")
    [Teardown]  the user clicks the button/link  link = Back to project setup

External project finance can see Project team
    [Documentation]  IFS-7357
    Given the user clicks the button/link     jQuery = td.ok:nth-of-type(2)
    Then the user should see the element      jQuery = h1:contains("Project team")
    [Teardown]  the user clicks the button/link  link = Back to project setup

External project finance can see the application finances
    [Documentation]  IFS-7357
    Given the user clicks the button/link    link = ${externalReviewerApplicationId}
    And the user clicks the button/link      id = accordion-questions-heading-3-1
    When the user clicks the button/link     jQuery = tr:contains("Ward") a:contains("View finances")
    And the user clicks the button/link      link = Your project costs
    Then the user should see the element     jQuery = h1:contains("Your project costs")

External project finance cannot access documents or MO
    [Documentation]  IFS-7357
    Given the user clicks the button/link     link = Dashboard
    When the user clicks the button/link      link = ${externalReviewerComp}
    Then the user should not have access to documents MO or bank details
    [Teardown]  the project finance approves all steps before finance

External project finance can raise a query
    [Documentation]  IFS-7357
    [Setup]    log in as a different user         ${exfinanceemail}  ${short_password}
    Given the user clicks the button/link         link = ${externalReviewerComp}
    And the user clicks the button/link           jQuery = td.ok + td.ok + td.ok + td.ok + td.ok + td.action a
    When the user raises a query
    Then the user should see the element          jQuery = button:contains("a viability query's title")
    [Teardown]  the user clicks the button/link   link = Finance checks

External project finance can raise a note
    [Documentation]  IFS-7357
    Given the user clicks the button/link   css = .eligibility-0
    When the user raises a note
    Then the user should see the element    jQuery = h2:contains("an eligibility note's title")

External project finance can approve viabilty
    [Documentation]  IFS-7357
    Given the user navigates to the page    ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    When the user clicks the button/link    jQuery = td.ok + td.ok + td.ok + td.ok + td.ok + td.action a
    Then the user can confirm all viability and eligibility

External project finance can generate spend profile
    [Documentation]  IFS-7357
    Given the user clicks the button/link    css = .generate-spend-profile-main-button
    And the user clicks the button/link      css = #generate-spend-profile-modal-button
    When all external users send spend profile
    Then the external finance cannot access spend profile

Internal user is able to approve Spend profile and generates the GOL
    [Documentation]  IFS-7365
    Given proj finance approves the spend profiles  ${externalReviewerProjectId}
    Then the user should see the element            css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(7)
    And internal user generates the GOL             YES  ${externalReviewerProjectId}

Applicant is able to upload the GOL
    [Documentation]  IFS-7365
    Given log in as a different user               &{leadApplicantLogin}
    When applicant uploads the GOL using Docusign  ${externalReviewerProjectId}

Internal user is able to reject the GOL and applicant can re-upload
    [Documentation]  IFS-7365
    Given the internal user rejects the GOL             ${externalReviewerProjectId}
    When log in as a different user                     &{leadApplicantLogin}
    Then the applicant is able to see the rejected GOL  ${externalReviewerProjectId}
    And applicant uploads the GOL using Docusign        ${externalReviewerProjectId}

Internal user is able to approve the GOL and the project is now Live
      [Documentation]  IFS-7365
      Given the internal user approve the GOL  ${externalReviewerProjectId}
      When log in as a different user          &{leadApplicantLogin}
      And the user navigates to the page       ${server}/project-setup/project/${externalReviewerProjectId}
      Then the user should see the element     jQuery = p:contains("The project is live")

External project finance cannot access GOL
    [Documentation]  IFS-7357
    Given Log in as a different user  &{ifs_admin_user_credentials}
    Then the external finance cannot access GOL section

*** Keywords ***
Complete external project finance details
    the user enters text to a text field    id = firstName     External
    the user enters text to a text field    id = lastName      Finance
    the user enters text to a text field    id = emailAddress  ${exfinanceemail}
    the user clicks the button/link         css = button[name = "inviteFinanceUser"]

External project finance creates account
    the user clicks the button/link          jQuery = .govuk-button:contains("Create account")
    the user enters text to a text field     id = firstName  External
    the user enters text to a text field     id = lastName  Finance
    the user enters text to a text field     id = password  ${short_password}
    the user clicks the button/link          jQuery = .govuk-button:contains("Create account")
    the user clicks the button/link          link = Sign into your account
    Logging in and Error Checking            ${exfinanceemail}   ${short_password}

Custom suite setup
    The user logs-in in new browser    troy.ward@gmail.com  ${short_password}
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Project details
    the user clicks the button/link    link = Correspondence address
    the user enter the Correspondence address
    the user clicks the button/link    id = return-to-set-up-your-project-button
    the user clicks the button/link    link = Project team
    the user clicks the button/link    link = Project manager
    the user selects the radio button  projectManager    projectManager1
    the user clicks the button/link    id = save-project-manager-button
    The user selects their finance contact  financeContact1
    the user clicks the button/link    link = Set up your project
    PM uploads the project documents   39
    the user clicks the button/link    link = Back to document overview
    PM submits both documents          39
    the user clicks the button/link    link = Back to document overview
    the user fills in bank details
    log in as a different user         belle.smith@gmail.com  ${short_password}
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Project team
    The user selects their finance contact  financeContact1
    the user fills in bank details
    log in as a different user         nicole.brown@gmail.com  ${short_password}
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Project team
    the user selects their finance contact  financeContact1
    the user fills in bank details

the user fills in bank details
    the user clicks the button/link                      link = Set up your project
    the user clicks the button/link                      link = Bank details
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex
    applicant user enters bank details

the project finance approves all steps before finance
    log in as a different user                   &{ifs_admin_user_credentials}
    the user navigates to the page               ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user clicks the button/link              jQuery = td.action:nth-of-type(3) a
    the user clicks the button/link              link = Collaboration agreement
    internal user approve uploaded documents
    the user goes to documents page              Return to documents  Exploitation plan
    internal user approve uploaded documents
    the user navigates to the page               ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user clicks the button/link              jQuery = td.action:nth-of-type(4)
    search for MO                                Orvill  Orville Gibbs
    And the internal user assign project to MO   ${externalReviewerApplicationId}  ${externalReviewerApplication}
    the user navigates to the page               ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user clicks the button/link              jQuery = td.action:nth-of-type(5)
    approve bank account details                 Ward Ltd
    approve bank account details                 Red Planet
    approve bank account details                 SmithZone

the user should not have access to documents MO or bank details
    the user should not see the element  jQuery = td.action:nth-of-type(3) a
    the user should not see the element   jQuery = td.action:nth-of-type(4) a
    the user should not see the element   jQuery = td.action:nth-of-type(5) a

the user raises a query
    the user clicks the button/link                        jQuery = tr:contains("Ward") td:contains("View"):nth-of-type(5)
    the user clicks the button/link                        css = a[id = "post-new-query"]
    the user enters text to a text field                   id = queryTitle  a viability query's title
    the user selects the option from the drop-down menu    Viability    id = section
    the user enters text to a text field                   css = .editor    another query body
    the user clicks the button/link                        css = .govuk-grid-column-one-half button[type = "submit"]  # Post query

the user raises a note
    the user clicks the button/link         jQuery = a:contains("Notes")
    the user should see the element         jQuery = h2:contains("Review notes")
    the user clicks the button/link         jQuery = .govuk-button:contains("Create a new note")
    the user enters text to a text field    id = noteTitle    an eligibility note's title
    the user enters text to a text field    css = .editor    this is some note text
    the user clicks the button/link         jQuery = .govuk-button:contains("Save note")

the user can confirm all viability and eligibility
    confirm viability    0
    confirm viability    2
    confirm eligibility  0
    confirm eligibility  1
    confirm eligibility  2

confirm viability
    [Arguments]  ${viability}
    the user clicks the button/link   css = .viability-${viability}
    the user selects the checkbox                        project-viable
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link                      id = confirm-button      #Page confirmation button
    the user clicks the button/link                      name = confirm-viability   #Pop-up confirmation button
    the user clicks the button/link                      link = Return to finance checks

confirm eligibility
    [Arguments]  ${eligibility}
    the user clicks the button/link                     css = .eligibility-${eligibility}
    the user selects the checkbox                        project-eligible
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link                      css = #confirm-button        #Page confirmation button
    the user clicks the button/link                      name = confirm-eligibility   #Pop-up confirmation button
    the user clicks the button/link                      link = Return to finance checks

all external users send spend profile
    user sends SP to lead              belle.smith@gmail.com
    user sends SP to lead              nicole.brown@gmail.com
    log in as a different user         troy.ward@gmail.com  ${short_password}
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Spend profile
    the user clicks the button/link    link = Ward Ltd
    the user clicks the button/link    id = spend-profile-mark-as-complete-button
    the user clicks the button/link    jQuery = a:contains("Review and submit project spend profile")
    the user clicks the button/link    jQuery = a:contains("Submit project spend profile")
    the user clicks the button/link    id = submit-send-all-spend-profiles

user sends SP to lead
    [Arguments]  ${user}
    log in as a different user         ${user}  ${short_password}
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Spend profile
    the user clicks the button/link    link = Submit to lead partner
    the user clicks the button/link    jQuery = button.govuk-button:contains("Submit")

the external finance cannot access spend profile
    log in as a different user            ${exfinanceemail}  ${short_password}
    the user navigates to the page        ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user should see the element       jQuery = td.action:nth-of-type(7)
    the user should not see the element   jQuery = td.action:nth-of-type(7) a

the external finance cannot access GOL section
    Log in as a different user              ${exfinanceemail}  ${short_password}
    the user clicks the button/link         link = ${externalReviewerComp}
    the user should not see the element     jQuery = td.action:nth-of-type(8) a
    the user should not see the element     jQuery = td.ok:nth-of-type(7) a

approve bank account details
    [Arguments]  ${org}
    the user clicks the button/link    link = ${org}
    the user clicks the button/link    jQuery = button:contains("Approve bank account details")
    the user clicks the button/link    jQuery = button:contains("Approve account")
    the user should see the element    jQuery = h2:contains("The bank details provided have been approved.")
    the user clicks the button/link    link = Bank details