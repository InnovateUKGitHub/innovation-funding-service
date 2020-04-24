*** Settings ***
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
${exfinanceemail}                  exfinance@example.com

*** Test Cases ***
IFS admin is able to invite an external PF
    [Documentation]
    [Setup]  Log in as a different user  &{ifs_admin_user_credentials}
    Given the user navigates to the page        ${server}/management/competition/setup/${externalReviewerCompId}
    When the user clicks the button/link        link = External finance reviewers
    And the user clicks the button/link         jQuery = span:contains("Invite a new external finance reviewer")
    Then complete external project finance details
    [Teardown]    logout as user

New external project finance can create account
    [Documentation]
    Given the user reads his email and clicks the link   ${exfinanceemail}  Invitation to review an Innovation Funding Service competition  You have been invited  1
    When external project finance creates account
    Then the user should see the element                 link = ${externalReviewerComp}

External project finance can see Project details
    Given the user clicks the button/link    link = Dashboard
    And The user clicks the button/link      link = ${externalReviewerComp}
    When the user clicks the button/link     jQuery = td.ok:nth-of-type(1)
    Then the user should see the element     jQuery = h1:contains("Project details")
    [Teardown]  the user clicks the button/link  link = Back to project setup

External project finance can see Project team
    Given the user clicks the button/link     jQuery = td.ok:nth-of-type(2)
    Then the user should see the element      jQuery = h1:contains("Project team")
    [Teardown]  the user clicks the button/link  link = Back to project setup

External project finance can see the application finances
    Given the user clicks the button/link    link = ${externalReviewerApplicationId}
    And the user clicks the button/link      id = accordion-questions-heading-3-1
    When the user clicks the button/link     jQuery = tr:contains("Ward") a:contains("View finances")
    And the user clicks the button/link      link = Your project costs
    Then the user should see the element     jQuery = h1:contains("Your project costs")

External project finance cannot access documents or MO
    Given the user clicks the button/link     link = Dashboard
    And The user clicks the button/link       link = ${externalReviewerComp}
    Then the user should not see the element  jQuery = td.action:nth-of-type(3) a
    And the user should not see the element   jQuery = td.action:nth-of-type(4) a
    And the user should not see the element   jQuery = td.action:nth-of-type(5) a
    [Teardown]  the project finance approves all steps before finance

External project finance can raise a query
    Given log in as a different user           ${exfinanceemail}  Passw0rd
    the user clicks the button/link             link = ${externalReviewerComp}
    then the user clicks the button/link        jQuery = td.ok + td.ok + td.ok + td.ok + td.ok + td.action a
    then the user clicks the button/link  jQuery = tr:contains("Ward") td:contains(View):nth-of-type(5)
        the user clicks the button/link                        css = a[id = "post-new-query"]
        the user enters text to a text field                   id = queryTitle  a viability query's title
        the user selects the option from the drop-down menu    Viability    id = section
        the user enters text to a text field                   css = .editor    another query body
        the user clicks the button/link                        css = .govuk-grid-column-one-half button[type = "submit"]  # Post query
        then the user should see the element                   jQuery = button:contains("a viability query's title")

External project finance can raise a note
    the user clicks the button/link   link = Finance checks
    the user clicks the button/link   css = table.table-progress tr:nth-child(1) td:nth-child(2)
    the user clicks the button/link   jQuery = a:contains("Notes")
    the user should see the element   jQuery = h2:contains("Review notes")
    the user clicks the button/link   jQuery = .govuk-button:contains("Create a new note")
        the user enters text to a text field    id = noteTitle    an eligibility note's title
        the user enters text to a text field    css = .editor    this is some note text
    the user clicks the button/link             jQuery = .govuk-button:contains("Save note")
    the user should see the element             jQuery = h2:contains("an eligibility note's title")

External project finance can approve viabilty
    [Documentation]
    the user navigates to the page  ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user clicks the button/link   jQuery = td.ok + td.ok + td.ok + td.ok + td.ok + td.action a
    the user clicks the button/link   css = .viability-0
    And the user selects the checkbox                        project-viable
    And the user selects the option from the drop-down menu  Green  id = rag-rating
    And the user clicks the button/link                      id = confirm-button      #Page confirmation button
    And the user clicks the button/link                      name = confirm-viability   #Pop-up confirmation button
    and the user clicks the button/link                      link = Return to finance checks
    the user clicks the button/link                          css = .viability-2
    And the user selects the checkbox                        project-viable
    And the user selects the option from the drop-down menu  Green  id = rag-rating
    And the user clicks the button/link                      id = confirm-button      #Page confirmation button
    And the user clicks the button/link                      name = confirm-viability   #Pop-up confirmation button
    and the user clicks the button/link                      link = Return to finance checks
    When the user clicks the button/link                     css = .eligibility-0
    And the user selects the checkbox                        project-eligible
    And the user selects the option from the drop-down menu  Green  id = rag-rating
    And the user clicks the button/link                      css = #confirm-button        #Page confirmation button
    And the user clicks the button/link                      name = confirm-eligibility   #Pop-up confirmation button
    and the user clicks the button/link                      link = Return to finance checks
    When the user clicks the button/link                     css = .eligibility-1
    And the user selects the checkbox                        project-eligible
    And the user selects the option from the drop-down menu  Green  id = rag-rating
    And the user clicks the button/link                      css = #confirm-button        #Page confirmation button
    And the user clicks the button/link                      name = confirm-eligibility   #Pop-up confirmation button
    and the user clicks the button/link                      link = Return to finance checks
    When the user clicks the button/link                     css = .eligibility-2
    And the user selects the checkbox                        project-eligible
    And the user selects the option from the drop-down menu  Green  id = rag-rating
    And the user clicks the button/link                      css = #confirm-button        #Page confirmation button
    And the user clicks the button/link                      name = confirm-eligibility   #Pop-up confirmation button
    the user clicks the button/link    link = Return to finance checks
    the user clicks the button/link    css = .generate-spend-profile-main-button
    the user clicks the button/link    css = #generate-spend-profile-modal-button
    log in as a different user         belle.smith@gmail.com  Passw0rd
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Spend profile
    the user clicks the button/link    link = Submit to lead partner
    the user clicks the button/link    jQuery = button.govuk-button:contains("Submit")
    log in as a different user         nicole.brown@gmail.com  Passw0rd
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Spend profile
    the user clicks the button/link    link = Submit to lead partner
    the user clicks the button/link    jQuery = button.govuk-button:contains("Submit")
    log in as a different user         troy.ward@gmail.com  Passw0rd
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Spend profile
    the user clicks the button/link    link = Ward Ltd
    the user clicks the button/link    id = spend-profile-mark-as-complete-button
    the user clicks the button/link    jQuery = a:contains("Review and submit project spend profile")
    the user clicks the button/link    jQuery = a:contains("Submit project spend profile")
    the user clicks the button/link    id = submit-send-all-spend-profiles
    log in as a different user            ${exfinanceemail}  Passw0rd
    the user navigates to the page        ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user should see the element   jQuery = td.action:nth-of-type(7)
    the user should not see the element  jQuery = td.action:nth-of-type(7) a
    Log in as a different user  &{ifs_admin_user_credentials}
    the user navigates to the page  ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user clicks the button/link     jQuery = td.action:nth-of-type(7) a
    the user selects the checkbox            approvedByLeadTechnologist
    the user clicks the button/link          jQuery = button:contains("Approved")
    the user clicks the button/link          jQuery = .modal-accept-profile button:contains("Approve")
    the user clicks the button/link     jQuery = td.action:nth-of-type(8) a
    the user uploads the file                grantOfferLetter  ${valid_pdf}
    the user selects the checkbox           confirmation
    the user clicks the button/link         id = send-gol
    the user clicks the button/link         jQuery = .modal-accept-send-gol .govuk-button:contains("Publish to project team")
    Log in as a different user              troy.ward@gmail.com  Passw0rd
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link     link = Grant offer letter
    the user uploads the file               signedGrantOfferLetter    ${valid_pdf}
    the user clicks the button/link  css = .govuk-button[data-js-modal = "modal-confirm-grant-offer-letter"]
    the user clicks the button/link  id = submit-gol-for-review
    Log in as a different user              ${exfinanceemail}  Passw0rd
    the user clicks the button/link             link = ${externalReviewerComp}
    the user should not see the element       jQuery = td.action:nth-of-type(8)
    the user should not see the element       jQuery = td.ok:nth-of-type(7)





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
    The user logs-in in new browser    troy.ward@gmail.com  Passw0rd
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
    log in as a different user         belle.smith@gmail.com  Passw0rd
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Project team
    The user selects their finance contact  financeContact1
    the user fills in bank details
    log in as a different user         nicole.brown@gmail.com  Passw0rd
    the user clicks the button/link    link = ${externalReviewerApplication}
    the user clicks the button/link    link = Project team
    the user selects their finance contact  financeContact1
    the user fills in bank details


the user fills in bank details
    the user clicks the button/link    link = Set up your project
    the user clicks the button/link    link = Bank details
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex
    applicant user enters bank details

the project finance approves all steps before finance
    log in as a different user  &{ifs_admin_user_credentials}
    the user navigates to the page  ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user clicks the button/link   jQuery = td.action:nth-of-type(3) a
    the user clicks the button/link   link = Collaboration agreement
    internal user approve uploaded documents
    the user goes to documents page   Return to documents  Exploitation plan
    internal user approve uploaded documents
    the user navigates to the page  ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user clicks the button/link  jQuery = td.action:nth-of-type(4)
    search for MO    Orvill  Orville Gibbs
    And the internal user assign project to MO   ${externalReviewerApplicationId}  ${externalReviewerApplication}
    the user navigates to the page  ${server}/project-setup-management/competition/${externalReviewerCompId}/status/all
    the user clicks the button/link  jQuery = td.action:nth-of-type(5)
        the user clicks the button/link    jQuery = a:contains("Ward Ltd")
        the user clicks the button/link    jQuery = button:contains("Approve bank account details")
        the user clicks the button/link    jQuery = button:contains("Approve account")
        the user should see the element    jQuery = h2:contains("The bank details provided have been approved.")
    the user clicks the button/link           link = Bank details
            the user clicks the button/link    jQuery = a:contains("Red Planet")
            the user clicks the button/link    jQuery = button:contains("Approve bank account details")
            the user clicks the button/link    jQuery = button:contains("Approve account")
            the user should see the element    jQuery = h2:contains("The bank details provided have been approved.")
    the user clicks the button/link           link = Bank details
               the user clicks the button/link    jQuery = a:contains("SmithZone")
               the user clicks the button/link    jQuery = button:contains("Approve bank account details")
               the user clicks the button/link    jQuery = button:contains("Approve account")
               the user should see the element    jQuery = h2:contains("The bank details provided have been approved.")






