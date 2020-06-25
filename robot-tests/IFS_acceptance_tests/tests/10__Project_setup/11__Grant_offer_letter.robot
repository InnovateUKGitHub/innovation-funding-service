*** Settings ***
Documentation     INFUND-4851 As a project manager I want to be able to submit an uploaded Grant Offer Letter so that Innovate UK can review my signed copy
...
...               INFUND-6059 As the contracts team I want to be able to send a Grant Offer Letter to the partners so that the project can begin
...
...               INFUND-4849 As a partner I want to be able to download a Grant Offer Letter and Appendices
...
...               INFUND-6091 As a partner / lead partner / project manager I want to get access to the GOL section in Project Setup when all other sections
...
...               INFUND-6377 As a member of the Competitions team I want to be able to select when the signed Grant Offer Letter has been approved so that Innovate UK can notify the Project Manager
...
...               INFUND-5998 As the contracts team I need to view, remove and/or re-upload the Grant Offer Letter
...
...               INFUND-6829 GOL uploaded but not submitted by PM shows wrong status
...
...               INFUND-7027 Partners can access the GOL before the internal user hits Send to proj team
...
...               INFUND-7049 Validation missing for PDF file upload in GOL upload page for internal user
...
...               INFUND-6375 As a partner I want to receive a notification when Project Setup has been successfully completed so that I am clear on what steps to take now the project is live
...
...               INFUND-6741 As the service delivery manager I want the service to generate a Grant Offer Letter once both the Spend Profiles and Other documents are approved so that the competitions team can review and publish to the project team
...
...               INFUND-7361 GOL is seen by internal user soon after the external user uploads it
...
...               INFUND-6048 As the contracts team I can have access to a generated Grant Offer Letter so that I can send it to the partners
...
...               INFUND-7170 Approved signed-GOL cannot be seen/downloaded by external users
...
...               INFUND-6780 As a project manager, I have the option to remove an uploaded signed GOL before submitting it, so that an can upload a different file if required
...
...               IFS-1577 Allow change of Project address until generation of GOL
...
...               IFS-1578 Allow change of Project Manager until generation of GOL
...
...               IFS-1579 Allow change of Finance Contact until generation of GOL
...
...               IFS-1307 CSS access: Project Setup/Previous
...
...               IFS-2174 Reject Signed Grant Offer Letter
...
...               IFS-2511 Resend Signed Grant Offer Letter
...
...               IFS-2533 Reason for rejecting GOL
...
...               IFS-4959 Navigating between IFS and ACC
...
...               IFS-5865 GOL download/upload page for admins
...
...               IFS-6054 Display completed projects in the previous tab
...
...               IFS-6021 External applicant dashboard - reflect internal Previous Tab behaviour
...
...               IFS-6731 Enable new partners to enter their location when joining a project & MO has been assigned
Suite Setup       Custom suite setup
Suite Teardown    Close browser and delete emails
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot


*** Test Cases ***
Applicant able to update project location until GOL not generated
    [Documentation]  IFS-6731
    Given log in as a different user    ${Elbow_Grease_Lead_PM_Email}   ${short_password}
    Then the user updates the project location in project setup    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/details

External user cannot view the GOL section before spend profiles have been approved
    [Documentation]    INFUND-6741
    [Tags]  HappyPath
    [Setup]
    Given the user navigates to the page        ${server}/project-setup/project/${Elbow_Grease_Project_Id}
    When the user should not see the element    css = li.waiting:nth-child(8)
    And the user should not see the element     link = Grant offer letter
    When the user clicks the button/link        link = View the status of partners
    Then the user should see the element        css = #table-project-status tr:nth-of-type(2) td.status.na:nth-of-type(8)

Support user cannot access spend profile until it is approved
    [Documentation]    IFS-1307
    [Tags]  HappyPath
    [Setup]  log in as a different user                                   &{support_user_credentials}
    Given the user navigates to the page                                  ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status
    Then the user should see the element                                  jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(7).status.action  # Spend profile status
    And the user should not see the element                               jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(7).status.action a  # Spend profile link
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/review-all-bank-details  ${403_error_message}
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/finance-check-overview  ${403_error_message}
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/spend-profile/approval  ${403_error_message}

GOL not generated before spend profiles have been approved
    [Documentation]    INFUND-6741
    [Tags]  HappyPath
    [Setup]    log in as a different user                                 &{Comp_admin1_credentials}
    When the user navigates to the page                                   ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status
    Then the user should not see the element                              css = #table-project-status tr:nth-of-type(7) td:nth-of-type(8).status.action
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/grant-offer-letter/send    ${403_error_message}
    [Teardown]    proj finance approves the spend profiles                ${Elbow_Grease_Project_Id}

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049 ,INFUND-5543
    [Setup]    log in as a different user   &{Comp_admin1_credentials}
    When the user navigates to the page     ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status
    Then the user should see the element    css = #table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.ok       # Project details
    And the user should see the element     css = #table-project-status > tbody > tr:nth-child(1) > td:nth-child(4) > a     # Other documents
    And the user should see the element     css = #table-project-status > tbody > tr:nth-child(1) > td:nth-child(5) > a     # Monitoring officer
    And the user should see the element     css = #table-project-status > tbody > tr:nth-child(1) > td:nth-child(6)         # Bank details
    And the user should see the element     css = #table-project-status > tbody > tr:nth-child(1) > td:nth-child(7)         # Finance checks
    And the user should see the element     css = #table-project-status > tbody > tr:nth-child(1) > td:nth-child(8) > a     # Spend profile
    And the user should see the element     css = #table-project-status > tbody > tr:nth-child(1) > td:nth-child(5) > a     # GOL

Project Finance can download GOL
    [Documentation]  INFUND-6377
    [Tags]   HappyPath
    [Setup]  log in as a different user                        &{internal_finance_credentials}
    Given the user navigates to the page                       ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/grant-offer-letter/send
    Then the user downloads the file                           ${internal_finance_credentials["email"]}  ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/grant-offer-letter/grant-offer-letter  ${DOWNLOAD_FOLDER}/grant_offer_letter.pdf
    [Teardown]    remove the file from the operating system    grant_offer_letter.pdf

Non lead should not be able to see GOL until it is sent by IUK
    [Documentation]  INFUND-7027
    [Tags]  HappyPath
    [Setup]    log in as a different user            ${Elbow_Grease_Partner_Email}  ${short_password}
    Given the user navigates to the page             ${server}/project-setup/project/${Elbow_Grease_Project_Id}
    Then the user should not see the element         css = li.complete:nth-child(8)
    And the user should not see the element          css = li.require-action:nth-child(8)
    When the user clicks the button/link             link = View the status of partners
    Then the user should see the element             jQuery = h1:contains("Project team status")
    And the user should see the element              css = #table-project-status tr:nth-of-type(3) td.status.na:nth-of-type(8)
    When the user clicks the button/link             link = Set up your project
    Then the user should not see the element         link = Grant offer letter

Comp Admin cannot upload big or non-pdf grant offer letter
    [Documentation]  INFUND-7049
    [Setup]  log in as a different user                 &{Comp_admin1_credentials}
    Given the user navigates to the page                ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/grant-offer-letter/send
    When the user uploads a file                        grantOfferLetter  ${too_large_pdf}
    Then the user should see a field error              ${too_large_10MB_validation_error}
    And the user uploads a file                         grantOfferLetter  ${text_file}
    Then the user should see a field error              ${wrong_filetype_validation_error}

Comp Admin is able to navigate to the Grant Offer letter page
    [Documentation]  IFS-5865
    Given the user navigates to the page         ${server}/project-setup-management/project/${PS_LP_Application_Project_Id}/grant-offer-letter/send
    When the user clicks the button/link         jQuery = a:contains("View the grant offer letter page")
    Then the user is able to see the Grant Offer letter page

Validating GOL page error message
    [Documentation]  IFS-5865
    [Setup]  the user navigates to the page              ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/grant-offer-letter/send
    Given the user uploads a file                        grantOfferLetter  ${valid_pdf}
    When the user clicks the button/link                 jQuery = button:contains("Send letter to project team")
    And the user clicks the button/link                  jQuery = button:contains("Send grant offer letter")
    Then the user should see a field and summary error   You must confirm that the grant offer letter has been approved by another member of your team.

Comp Admin user uploads new grant offer letter
    [Documentation]    INFUND-6377, INFUND-5988
    [Tags]  HappyPath
    And the user should see the element         jQuery = button:contains("Remove")
    When the user uploads a file                annex  ${valid_pdf}
    And the user selects the checkbox           confirmation
    And the user clicks the button/link         id = send-gol
    And the user clicks the button/link         jQuery = .modal-accept-send-gol .govuk-button:contains("Send grant offer letter")
    Then the user should not see the element    css = [name = "removeGrantOfferLetterClicked"]
    When the user navigates to the page         ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status
    Then the user should see the element        jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(8).status.waiting   # GOL
    And the user reads his email                ${Elbow_Grease_Lead_PM_Email}  ${PROJECT_SETUP_COMPETITION_NAME}: Your grant offer letter is available for project ${Elbow_Grease_Application_No}  We are pleased to inform you that your grant offer letter is now ready for you to sign

PM can view the grant offer letter page
    [Documentation]    INFUND-4848, INFUND-6091
    [Tags]
    [Setup]    log in as a different user            ${Elbow_Grease_Lead_PM_Email}   ${short_password}
    Given the user clicks the button/link            link = ${Elbow_Grease_Title}
    Then the user should see the element             css = li.require-action:last-of-type
    When the user clicks the button/link             link = Grant offer letter
    Then the user should see the element             jQuery = p:contains("The grant offer letter has been provided by Innovate UK.")
    And the user should see the element              jQuery = label:contains(Upload)
    And the user goes back to the previous page
    When the user clicks the button/link             link = View the status of partners
    Then the user should see the element             jQuery = h1:contains("Project team status")
    And the user should see the element              css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(8)  # GOL

Partners should not be able to send the Grant Offer
    [Documentation]    INFUND-4851, INFUND-6133
    [Tags]
    [Setup]    log in as a different user       ${Elbow_Grease_Partner_Email}    ${short_password}
    Given the user clicks the button/link       link = ${Elbow_Grease_Title}
    And the user clicks the button/link         link = Grant offer letter
    Then the user should not see the element    jQuery = label:contains(Upload)
    And the user should not see the element     css = .govuk-button[data-js-modal = "modal-confirm-grant-offer-letter"]

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Tags]
    [Setup]    Log in as a different user           ${Elbow_Grease_Partner_Email}  ${short_password}
    Given the user clicks the button/link           link = ${Elbow_Grease_Title}
    Then the user should see the element            link = Monitoring Officer
    And the user should see the element             link = Bank details
    And the user should see the element             link = Finance checks
    And the user should see the element             link = Spend profile
    And the user should see the element             link = Grant offer letter

PM should not be able to upload big Grant Offer files
    [Documentation]    INFUND-4851, INFUND-4972
    [Tags]
    [Setup]    log in as a different user            ${Elbow_Grease_Lead_PM_Email}  ${short_password}
    Given the user clicks the button/link               link = ${Elbow_Grease_Title}
    And the user clicks the button/link                 link = Grant offer letter
    When the user uploads a file                        signedGrantOfferLetter    ${too_large_pdf}
    Then the user should see the element                jQuery = .file-list .govuk-error-message:contains("${too_large_10MB_validation_error}")

PM should be able upload a file and then access the Send button
    [Documentation]    INFUND-4851, INFUND-4972, INFUND-6829
    [Tags]  HappyPath
    [Setup]    log in as a different user            ${Elbow_Grease_Lead_PM_Email}  ${short_password}
    Given the user clicks the button/link            link = ${Elbow_Grease_Title}
    And the user clicks the button/link              link = Grant offer letter
    When the user uploads a file                     signedGrantOfferLetter   ${valid_pdf}
    Then the user should see the element             link = ${valid_pdf} (opens in a new window)
    When the user reloads the page
    Then the user should see the element             css = .govuk-button[data-js-modal = "modal-confirm-grant-offer-letter"]
    And the user clicks the button/link              link = Set up your project
    And the user should see the element              css = li.require-action:nth-child(8)
    When the user clicks the button/link             link = View the status of partners
    Then the user should see the element             jQuery = h1:contains("Project team status")
    And the user should see the element              css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(8)

Applicant not able to update project location after GOL generated
    [Documentation]  IFS-6731
    Given the user navigates to the page                   ${server}/project-setup/project/${Elbow_Grease_Project_Id}/details
    And the user clicks the button/link                    link = Edit
    When the user enters text to a text field              css = #postcode  AB2 1AB
    And the user clicks the button/link                    css = button[type = "submit"]
    Then the user should see a field and summary error     You cannot edit the organisation's project location because we have already generated the grant offer letter.
    And the user clicks the button/link                    link = Project details

Project finance cannot access the GOL before it is sent by PM
    [Documentation]    INFUND-7361
    [Tags]
    [Setup]  log in as a different user              &{internal_finance_credentials}
    Given the user navigates to the page             ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/grant-offer-letter/send
    Then the user should see the element             jQuery = .upload-section p:contains("Awaiting upload by the Project Manager")

PM can view the generated Grant Offer Letter
    [Documentation]    INFUND-6059, INFUND-4849
    [Tags]
    [Setup]    log in as a different user    ${Elbow_Grease_Lead_PM_Email}  ${short_password}
    Given the user navigates to the page     ${server}/project-setup/project/${Elbow_Grease_Project_Id}/
    Then the user should see the element     css = ul li.require-action:nth-child(8)
    When the user clicks the button/link     link = Grant offer letter
    Then the user should see the element     jQuery = h2:contains("Grant offer letter")

PM can download the grant offer letter
    [Documentation]    INFUND-5998
    [Tags]
    Given the user navigates to the page    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user downloads the file        ${Elbow_Grease_Lead_PM_Email}    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer/grant-offer-letter  ${DOWNLOAD_FOLDER}/grant_offer_letter.pdf
    [Teardown]    remove the file from the operating system    grant_offer_letter.pdf

Other external users can see the uploaded Grant Offer letter
    [Documentation]    INFUND-6059
    [Tags]
    Given log in as a different user      ${Elbow_Grease_Partner_Email}  ${short_password}
    And the user navigates to the page    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/
    Then the user should see the element  css = ul li.waiting:nth-child(8)
    When the user clicks the button/link  link = Grant offer letter
    Then the user should see the element  jQuery = h2:contains("Grant offer letter")

Non lead partner can download the grant offer letter
    [Documentation]    INFUND-5998
    [Tags]
    Given the user navigates to the page                       ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user downloads the file                           ${Elbow_Grease_Partner_Email}    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer/grant-offer-letter  ${DOWNLOAD_FOLDER}/grant_offer_letter.pdf
    [Teardown]    remove the file from the operating system    grant_offer_letter.pdf

Non lead partner can download the annex
    [Documentation]    INFUND-5998
    [Tags]
    Given the user navigates to the page                       ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user downloads the file                           ${Elbow_Grease_Partner_Email}    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer/additional-contract  ${DOWNLOAD_FOLDER}/annex.pdf
    [Teardown]    remove the file from the operating system    annex.pdf

Academic users can see the uploaded Grant Offer letter
    [Documentation]    INFUND-5998
    [Tags]  HappyPath
    Given log in as a different user        ${Elbow_Grease_Academic_Email}  ${short_password}
    And the user navigates to the page      ${server}/project-setup/project/${Elbow_Grease_Project_Id}/
    Then the user should see the element    css = ul li.waiting:nth-child(8)
    When the user clicks the button/link    link = Grant offer letter
    Then the user should see the element    jQuery = h2:contains("Grant offer letter")

Academic partner can download the grant offer letter
    [Documentation]    INFUND-5998
    [Tags]
    Given the user navigates to the page                       ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user downloads the file                           ${Elbow_Grease_Academic_Email}    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer/grant-offer-letter  ${DOWNLOAD_FOLDER}/grant_offer_letter.pdf
    [Teardown]    remove the file from the operating system    grant_offer_letter.pdf

Academic partner can download the annex
    [Documentation]    INFUND-5998
    [Tags]
    Given the user navigates to the page                       ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user downloads the file                           ${Elbow_Grease_Academic_Email}    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer/additional-contract  ${DOWNLOAD_FOLDER}/annex.pdf
    [Teardown]    remove the file from the operating system    annex.pdf

PM can view the uploaded Annex file
    [Documentation]    INFUND-4851, INFUND-4849
    [Tags]  HappyPath
    [Setup]    log in as a different user        ${Elbow_Grease_Lead_PM_Email}  ${short_password}
    Given the user navigates to the page         ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then open pdf link                           jQuery = a:contains("${valid_pdf} (opens in a new window)")

PM can download the annex
    [Documentation]    INFUND-5998
    [Tags]
    Given the user navigates to the page                       ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user downloads the file                           ${Elbow_Grease_Lead_PM_Email}    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer/additional-contract  ${DOWNLOAD_FOLDER}/annex.pdf
    [Teardown]    remove the file from the operating system    annex.pdf

PM can remove the signed grant offer letter
    [Documentation]    INFUND-6780
    [Tags]  HappyPath
    When the user clicks the button/link              name = removeSignedGrantOfferLetterClicked
    Then the user should not see the element          jQuery = button:contains("Remove")
    And the user should see the element               jQuery = label:contains("Upload")
    And the user should not see the element           jQuery = .upload-section a:contains("${valid_pdf}")

PM can upload new signed grant offer letter
    [Documentation]    INFUND-6780
    [Tags]  HappyPath
    When the user uploads a file               signedGrantOfferLetter    ${valid_pdf}
    And the user reloads the page
    Then the user should see the element       css = .govuk-button[data-js-modal = "modal-confirm-grant-offer-letter"]
    And the user should not see the element    jQuery = [disabled = 'disabled'].govuk-button:contains(Send signed offer letter)

PM Sends the Grant Offer letter
    [Documentation]    INFUND-4851, INFUND-6091, INFUND-5998
    [Tags]  HappyPath
    When the user clicks the button/link  css = .govuk-button[data-js-modal = "modal-confirm-grant-offer-letter"]
    Then the user clicks the button/link  id = submit-gol-for-review
    And the user should not see an error in the page
    When the user navigates to the page   ${server}/project-setup/project/${Elbow_Grease_Project_Id}
    Then the user should see the element  css = li.waiting:nth-child(8)

PM can download the signed grant offer letter
    [Documentation]    INFUND-7170
    [Tags]
    Given the user navigates to the page                       ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user should see the element                       jQuery = h2:contains("Signed grant offer letter")
    And the user downloads the file                            ${Elbow_Grease_Lead_PM_Email}    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer/signed-grant-offer-letter  ${DOWNLOAD_FOLDER}/signedGOL.pdf
    [Teardown]    remove the file from the operating system    signedGOL.pdf

PM cannot remove the signed grant offer letter after submission
    [Documentation]    INFUND-6780
    When the user should not see the element    name = removeSignedGrantOfferLetterClicked
    Then the user should not see the element    jQuery = button:contains("Remove")

PM's status should be updated
    [Documentation]    INFUND-4851, INFUND-6091, INFUND-5998
    [Tags]
    Given the user navigates to the page             ${server}/project-setup/project/${Elbow_Grease_Project_Id}
    And the user clicks the button/link              link = View the status of partners
    Then the user should see the element             jQuery = h1:contains("Project team status")
    And the user should see the element              css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(8)

Internal Dashboard should be updated
    [Documentation]    INFUND-4851, INFUND-6091, INFUND-5998
    [Tags]  HappyPath
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    When the user navigates to the page      ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status
    Then the user should see the element     jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(8).status.action

Internal user can download the signed GOL
    [Documentation]    INFUND-6377
    [Tags]
    Given the user navigates to the page                       ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/grant-offer-letter/send
    Then the user should see the element                       jQuery = h2:contains("Signed grant offer letter") + .upload-section a
    And the user downloads the file                            ${Comp_admin1_credentials["email"]}  ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/grant-offer-letter/signed-grant-offer-letter  ${DOWNLOAD_FOLDER}/testing.pdf
    [Teardown]    remove the file from the operating system    testing.pdf

Comp Admin can accept the signed grant offer letter
    [Documentation]  INFUND-6377 IFS-2174
    [Tags]  HappyPath
    Given the user navigates to the page     ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/all
    When the user clicks the button/link     jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(8).status.action a
    Then the user navigates to the page      ${server}/project-setup-management/project/${Elbow_Grease_Project_Id}/grant-offer-letter/send
    And the user should see the element      css = label[for = "acceptGOL"]
    And the user should see the element      css = #submit-button.disabled
    When the user selects the radio button   approvalType  rejectGOL
    Then the user should see the element     css = #submit-button

Comp Admin is able to Reject the Grant Offer letter
    [Documentation]  IFS-2174  IFS-2533
    [Tags]  HappyPath
    Given the user tries to reject without a reason he should get a validation message
    Then the user rejects the GOL and sees the successful status

PM can see that the GOL section requires completion
    [Documentation]  IFS-2174
    [Tags]
    [Setup]  log in as a different user     ${Elbow_Grease_Lead_PM_Email}  ${short_password}
    Given the user navigates to the page    ${server}/project-setup/project/${Elbow_Grease_Project_Id}
    Then the user should see the element    jQuery = li.require-action:contains("Grant offer letter")
    When the user navigates to the page     ${server}/project-setup/project/${Elbow_Grease_Project_Id}/team-status
    Then the user should see the element    jQuery = th:contains("${Big_Riffs_Name}") ~ td:nth-of-type(8).action
    And the user should see the element     jQuery = th:contains("${Listen_To_Name}") ~ td:nth-of-type(8).waiting

PM is uploading the GOL one more time
    [Documentation]  IFS-2174  IFS-2511
    [Tags]  HappyPath
    [Setup]  log in as a different user     ${Elbow_Grease_Lead_PM_Email}  ${short_password}
    Given the user navigates to the page    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    When the user should see the element    jQuery = .fail-alert:contains("grant offer letter has been rejected")
    Then the user removes existing and uploads new grant offer letter

Internal user accepts signed grant offer letter
    [Documentation]  INFUND-5998, INFUND-6377
    [Tags]  HappyPath
    [Setup]  log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page   ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/all
    When the user clicks the button/link   jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(8).status.action a:contains("Review")
    And the user selects the radio button  approvalType  acceptGOL
    And the user clicks the button/link    jQuery = button:contains("Submit")
    And the user clicks the button/link    jQuery = button[type = "submit"]:contains("Accept signed grant offer letter")
    Then the user should see the element   jQuery = .success-alert h2:contains("These documents have been approved.")
    When the user navigates to the page    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/all
    Then the user should see the element   jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(8).status.ok
    [Teardown]   Sleep   60s

Project manager's status should be updated
    [Documentation]   INFUND-5998, INFUND-6377  IFS-6021  IFS-7017
    [Tags]
    [Setup]    log in as a different user                                      ${Elbow_Grease_Lead_PM_Email}  ${short_password}
    Given the user should see live project on dashboard
    When the user clicks the button/link                                       link = ${Elbow_Grease_Title}
    Then the user should see project is live with review its progress link

Non lead's status should be updated
    [Documentation]   INFUND-5998, INFUND-6377
    [Tags]
    [Setup]    log in as a different user                                      ${Elbow_Grease_Partner_Email}  ${short_password}
    Given the user navigates to the page                                       ${server}/project-setup/project/${Elbow_Grease_Project_Id}
    And the user should see the element                                        css = li.complete:nth-child(7)
    #And the user should see the element      link = review its progress
    Then the user should see project is live with review its progress link

Non lead can see the GOL approved
    [Documentation]  INFUND-6377
    [Tags]
    Given the user navigates to the page  ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user should see the element  jQuery = .grant-offer-download:contains("testing.pdf")
    And the user should see the element   jQuery = .success-alert p:contains("Your signed grant offer letter has been received and accepted by Innovate UK")

Non lead can download the GOL
    [Documentation]  INFUND-6377
    [Tags]
    Given the user navigates to the page    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user downloads the file        ${Elbow_Grease_Partner_Email}  ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer/grant-offer-letter  ${DOWNLOAD_FOLDER}/testing.pdf
    [Teardown]    remove the file from the operating system    testing.pdf

Non lead cannot see the signed GOL
    [Documentation]    INFUND-7170
    [Tags]
    Given the user navigates to the page    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer
    Then the user should not see the element     jQUery = h2:contains("Signed grant offer letter")
    When the user navigates to the page and gets a custom error message    ${server}/project-setup/project/${Elbow_Grease_Project_Id}/offer/signed-grant-offer-letter    ${403_error_message}

PM receives an email when the GOL is approved
    [Documentation]    INFUND-6375
    [Tags]
    Then the user reads his email    ${Elbow_Grease_Lead_PM_Email}    Grant offer letter approved for project ${Elbow_Grease_Application_No}    We have accepted your signed grant offer letter for your project:

Industrial finance contact receives an email when the GOL is approved
    [Documentation]    INFUND-6375
    [Tags]
    Then the user reads his email    ${Elbow_Grease_Partner_Email}    Grant offer letter approved for project ${Elbow_Grease_Application_No}    We have accepted your signed grant offer letter for your project:

Academic finance contact receives an email when the GOL is approved
    [Documentation]    INFUND-6375
    [Tags]
    Then the user reads his email    ${Elbow_Grease_Academic_Email}    Grant offer letter approved for project ${Elbow_Grease_Application_No}    We have accepted your signed grant offer letter for your project:

Internal user should see completed project in previous tab
    [Documentation]  IFS-6054
    [Setup]  log in as a different user     &{internal_finance_credentials}
    Given the user navigates to the page    ${server}/management/competition/${PROJECT_SETUP_COMPETITION}/previous
    And the user expands the section        Projects
    Then the user should see the element    jQuery = th:contains("${Elbow_Grease_Title}")
    And the user should see project setup compeletion status

Verify support users permissions in project setup tab
    [Documentation]  IFS-1307
    [Tags]
    [Setup]    log in as a different user                                   &{support_user_credentials}
    When the internal user navigates to the project setup competition       ${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should see project setup compeletion status
    And the user should see the element                                     jQuery = .success-alert h2:contains("These documents have been approved.")

Support user should see completed project in previous tab
    [Documentation]  IFS-6054
    Given the user navigates to the page    ${server}/management/competition/${PROJECT_SETUP_COMPETITION}/previous
    And the user expands the section        Projects
    Then the user should see the element    jQuery = th:contains("${Elbow_Grease_Title}")

Project is automatically sent to ACC if set up for the competition
    [Documentation]  IFS-6786
    Given log in as a different user         ${Elbow_Grease_Lead_PM_Email}   ${short_password}
    Then the user should see the element     id = dashboard-link-LIVE_PROJECTS_USER

*** Keywords ***
the user uploads a file
    [Arguments]  ${name}  ${file}
    the user uploads the file    name = ${name}    ${file}
    Wait Until Page Does Not Contain Without Screenshots    Uploading

the user is able to see the Grant Offer letter page
    Select Window                          title = Print version with CSS
    the user closes the last opened tab

the user removes existing and uploads new grant offer letter
    the user clicks the button/link  css = button[name = "removeSignedGrantOfferLetterClicked"]
    the user should see the element  jQuery = label:contains("Upload")
    the user uploads a file          signedGrantOfferLetter    ${valid_pdf}
    the user clicks the button/link  css = .govuk-button[data-js-modal="modal-confirm-grant-offer-letter"]
    the user clicks the button/link  id = submit-gol-for-review
    the user should see the element  jQuery = li:contains("Grant offer letter") .status-waiting

the user tries to reject without a reason he should get a validation message
    the user selects the radio button     approvalType  rejectGOL
    the user enters text to a text field  id = gol-reject-reason  ${empty}
    Set Focus To Element                  link = Dashboard
    the user should see a field error     ${empty_field_warning_message}

the user rejects the GOL and sees the successful status
    #Insert Rejection text and submit
    The user enters text to a text field  id = gol-reject-reason  The document was not signed.
    the user clicks the button/link       css = #submit-button
    the user clicks the button/link       jQuery = button[type = "submit"]:contains("Reject signed grant offer letter")
    #Warning message on the same page
    the user should see the element       jQuery = .warning-alert:contains("documents have been reviewed and rejected.")
    the user should see the element       jQuery = .warning-alert:contains("Reason for rejection:")
    #Warning message on Competition level
    the user navigates to the page        ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/all
    the user should see the element       jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(8).rejected

the user should see project setup compeletion status
    the user should see the element      jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(1).status.ok a  # Project details
    the user should see the element      jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(2).status.ok a  # Project team
    the user should see the element      jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(3).status.ok a  # Documents
    the user should see the element      jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(4).status.ok a  # Monitoring officer
    the user should see the element      jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(5).status.ok    # Bank details
    the user should see the element      jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(6).status.ok    # Finance checks
    the user should see the element      jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(7).status.ok a  # Spend profile
    the user clicks the button/link      jQuery = tr:contains("${Elbow_Grease_Title}") td:nth-of-type(8).status.ok a  # GOL

the user should see live project on dashboard
    the user clicks the button/link        id = dashboard-link-APPLICANT
    the user should not see the element    jQuery = .projects-in-setup ul li a:contains("${Elbow_Grease_Title}")
    the user should see the element        jQuery = .previous ul li a:contains("${Elbow_Grease_Title}")
    the user should see the element        jQUery = .task:contains("${Elbow_Grease_Title}") ~ .status-and-action:contains("Live project")

Custom suite setup
    Connect to database  @{database}
    the user logs-in in new browser     ${Elbow_Grease_Lead_PM_Email}  ${short_password}
    execute sql string  INSERT INTO `ifs`.`grant_process_configuration` (`competition_id`, `send_by_default`) VALUES ('${PROJECT_SETUP_COMPETITION}', '1');
