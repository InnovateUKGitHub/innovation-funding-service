*** Settings ***
Resource    ../../resources/defaultResources.robot
Resource    Applicant_Commons.robot

*** Variables ***

${PS_Competition_Name}     Rolling stock future developments
${PS_Competition_Id}       ${competition_ids["${PS_Competition_Name}"]}
${PS_Point_Project_Name}   Point control and automated monitoring
${PS_Point_Project_Id}     ${project_ids["${PS_Point_Project_Name}"]}

#Project: London underground – enhancements to existing stock and logistics
# EF = Experian feedback
${Gabtype_Name}   Gabtype
${Gabtype_Id}     ${organisation_ids["${Gabtype_Name}"]}
${Kazio_Name}     Kazio
${Kazio_Id}       ${organisation_ids["${Kazio_Name}"]}
${Cogilith_Name}  Cogilith
${Cogilith_Id}    ${organisation_ids["${Cogilith_Name}"]}
${PS_EF_Application_Title}         London underground - enhancements to existing stock and logistics
${PS_EF_Application_No}            ${application_ids["${PS_EF_Application_Title}"]}
${PS_EF_Application_Project_No}    ${project_ids["${PS_EF_Application_Title}"]}
${PS_EF_Application_Lead_Partner_Email}        ${test_mailbox_one}+amy@gmail.com
${PS_EF_Application_PM_Email}                  ${test_mailbox_one}+amy@gmail.com
${PS_EF_Application_Partner_Email}             ${test_mailbox_one}+karen@gmail.com
${PS_EF_Application_Academic_Email}            ${test_mailbox_one}+juan@gmail.com

#Project: Elbow grease - GOL
${Big_Riffs_Name}        Big Riffs And Insane Solos Ltd
${Big_Riffs_Id}          ${organisation_ids["${Big_Riffs_Name}"]}
${Aberystwyth_Name}      Aberystwyth University
${Aberystwyth_Id}        ${organisation_ids["${Aberystwyth_Name}"]}
${Listen_To_Name}        Listen To Metal Ltd
${Listen_To_Id}          ${organisation_ids["${Listen_To_Name}"]}
${Elbow_Grease_Title}               Elbow grease
${Elbow_Grease_Application_No}      ${application_ids["${Elbow_Grease_Title}"]}
${Elbow_Grease_Project_Id}          ${project_ids["${Elbow_Grease_Title}"]}
${Elbow_Grease_Lead_PM_Email}       louis.morgan@example.com
${Elbow_Grease_Partner_Email}       howard.white@example.com
${Elbow_Grease_Academic_Email}      deborah.moore@example.com

#Project: High-speed rail and its effects on soil compaction - Queries and notes
${Dreambit_Name}    Dreambit
${Dreambit_Id}      ${organisation_ids["${Dreambit_Name}"]}
${Queries_Application_Title}   High-speed rail and its effects on soil compaction
${Queries_Application_No}      ${application_ids["${Queries_Application_Title}"]}
${Queries_Application_Project}  ${project_ids["${Queries_Application_Title}"]}

#Project: High-speed rail and its effects on air quality
# SP = Spend Profile
${Ooba_Lead_Org_Name}           Ooba
${Ooba_Lead_Org_Id}             ${organisation_ids["${Ooba_Lead_Org_Name}"]}
${Wordpedia_Partner_Org_Name}   Wordpedia
${Wordpedia_Partner_Org_Id}     ${organisation_ids["${Wordpedia_Partner_Org_Name}"]}
${Jabbertype_Partner_Org_Name}  Jabbertype
${Jabbertype_Partner_Org_Id}    ${organisation_ids["${Jabbertype_Partner_Org_Name}"]}
${PS_SP_Application_Title}         High-speed rail and its effects on air quality
${PS_SP_Application_No}            ${application_ids["${PS_SP_Application_Title}"]}
${PS_SP_Project_Id}                ${project_ids["${PS_SP_Application_Title}"]}
${PS_SP_Lead_PM_Email}             ralph.young@ooba.example.com
${PS_SP_Partner_Email}             tina.taylor@wordpedia.example.com
${PS_SP_Academic_Partner_Email}    antonio.jenkins@jabbertype.example.com

#Project: Grade crossing manufacture and supply
# BD = Bank Details
${Vitruvius_Name}         Vitruvius Stonework Limited
${Vitruvius_Id}           ${organisation_ids["${Vitruvius_Name}"]}
${A_B_Cad_Services_Name}  A B Cad Services
${A_B_Cad_Services_Id}    ${organisation_ids["${A_B_Cad_Services_Name}"]}
${Armstrong_Butler_Name}  Armstrong & Butler Ltd
${Armstrong_Butler_Id}    ${organisation_ids["${Armstrong_Butler_Name}"]}
${Grade_Crossing_Application_Title}       Grade crossing manufacture and supply
${Grade_Crossing_Applicaiton_No}          ${application_ids["${Grade_Crossing_Application_Title}"]}
${Grade_Crossing_Project_Id}              ${project_ids["${Grade_Crossing_Application_Title}"]}
${Grade_Crossing_Lead_Partner_Email}      diane.scott@vitruvius.example.com
${Grade_Crossing_Partner_Email}           phil.mcguire@abcad.example.com
${Grade_Crossing_Partner_Finance}         Phil Mcguire
${Grade_Crossing_Academic_Email}          darren.richards@armstrong.example.com
${Grade_Crossing_Academic_Finance}        Darren Richards

&{lead_applicant_credentials_bd}  email=${Grade_Crossing_Lead_Partner_Email}  password=${short_password}
&{collaborator1_credentials_bd}   email=${Grade_Crossing_Partner_Email}   password=${short_password}
&{collaborator2_credentials_bd}   email=${Grade_Crossing_Academic_Email}  password=${short_password}

#Project: New materials for lighter stock
# IU - Intenal user
${Ntag_Name}      Ntag
${Jetpulse_Name}  Jetpulse
${Wikivu_Name}    Wikivu
${PS_IU_Application_Title}      New materials for lighter stock
${PS_IU_Application_No}         ${application_ids["${PS_IU_Application_Title}"]}
${PS_IU_Application_Project}    ${project_ids["${PS_IU_Application_Title}"]}

#Project: Magic material
#PD- Project Details
${PS_PD_Application_Title}     Magic material
${PS_PD_Application_Id}        ${application_ids["${PS_PD_Application_Title}"]}
${PS_PD_Project_Id}            ${project_ids["${PS_PD_Application_Title}"]}
${PS_PD_Lead_Partner_Email}    ${lead_applicant_credentials["email"]}

${Project_In_Setup_Page}                ${server}/project-setup/project/${PS_PD_Project_Id}
${Project_In_Setup_Details_Page}        ${Project_In_Setup_Page}/details
${Project_In_Setup_Team_Status_Page}    ${Project_In_Setup_Page}/team-status
${Project_Start_Date_Page}              ${Project_In_Setup_Details_Page}/start-date
${Project_Address_Page}                 ${Project_In_Setup_Details_Page}/project-address
${Internal_Competition_Status}          ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status
${Notified_Application_Competition_Status}   ${server}/project-setup-management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/status

#Project: Project Setup Comp 10 - On Hold
${OnHoldCompName}      Project Setup Comp 10
${OnHoldCompId}        ${competition_ids["${OnHoldCompName}"]}
${OnHoldProjectName}   PSC application 10
${OnHoldProjectId}     ${project_ids["${OnHoldProjectName}"]}

#Finances Checks
${PS_FC_Application_Title}    Office Chair for Life
${PS_FC_Project_Id}           ${project_ids["${PS_FC_Application_Title}"]}

#Bank details
${Account_One}   51406795
${Sortcode_One}  404745
${Account_Two}   12345677
${Sortcode_two}  000004
${Postcode}      BS14NT

#Project: Super-EFFY - Super Efficient Forecasting of Freight Yields
#LP: Live Project
${Crystalrover_Name}   Crystalrover
${Crystalrover_Id}     ${organisation_ids["${Crystalrover_Name}"]}
${Jabbertype_Name}     Jabbertype
${Jabbertype_Id}       ${organisation_ids["${Jabbertype_Name}"]}
${Zummacity_Name}      Zummacity
${Zummacity_Id}        ${organisation_ids["${Zummacity_Name}"]}
${PS_LP_Application_Title}         Super-EFFY - Super Efficient Forecasting of Freight Yields
${PS_LP_Application_No}            ${application_ids["${PS_LP_Application_Title}"]}
${PS_LP_Application_Project_Id}    ${project_ids["${PS_LP_Application_Title}"]}
${PS_LP_Application_Lead_PM_Email}        dave.adams@gmail.com
${PS_LP_Application_Partner_Email}        edward.morris@gmail.com
${PS_LP_Application_Academic_Email}       myrtle.barton@jabbertype.example.com

#Project: Growth table comp
${GrowthTableCompName}              Growth table comp
${GrowthTableCompId}                ${competition_ids["${GrowthTableCompName}"]}
${PS_GTC_Application_Title}         Growth table app
${PS_GTC_Application_No}            ${application_ids["${PS_GTC_Application_Title}"]}
${PS_GTC_Application_Project_Id}    ${project_ids["${PS_GTC_Application_Title}"]}

#Project: No Growth table comp
${NoGrowthTableCompName}              No Growth table comp
${NoGrowthTableCompId}                ${competition_ids["${NoGrowthTableCompName}"]}
${PS_NGTC_Application_Title}          Single applicant app
${PS_NGTC_Application_No}             ${application_ids["${PS_NGTC_Application_Title}"]}
${PS_NGTC_Application_Project_Id}     ${project_ids["${PS_NGTC_Application_Title}"]}

# Competition Connected digital additive manufacturing
${ConnectedCompName}  Connected digital additive manufacturing
${ConnectedCompId}    ${competition_ids["${ConnectedCompName}"]}
${MobileProjectName}  Mobile Phone Data for Logistics Analytics
${MobileProjectId}    ${project_ids["${MobileProjectName}"]}

*** Keywords ***
Finance reviewer is added to the project
    [Arguments]  ${projectURL}
    log in as a different user                &{ifs_admin_user_credentials}
    the user navigates to the page            ${projectURL}
    the user clicks the button/link           jQuery = a:contains("Edit")
    the user selects finance reviewer         Rianne Almeida
    the user clicks the button/link           jQuery = button:contains("Update finance reviewer")
    the user should see the element           jQuery = tr:contains("Rianne Almeida")

The user selects finance reviewer
    [Arguments]   ${FlName}
    input text                          id = userId    ${FlName}
    the user clicks the button/link     jQuery = ul li:contains("${FlName}")

The user adds a new team member
  [Arguments]  ${firstName}  ${email}
  the user enters text to a text field   css = input[name=name]   ${firstName}
  the user enters text to a text field   css = input[name=email]  ${email}
  the user clicks the button/link        jQuery = button:contains("Invite to")

internal user generates the GOL
    [Arguments]  ${setDocusign}  ${projectID}
    the user navigates to the page     ${server}/project-setup-management/project/${projectID}/grant-offer-letter/send
    the user uploads the file          grantOfferLetter  ${gol_pdf}
    the user should see the element    jQuery = a:contains("GOL_template.pdf (opens in a new window)")
    the user selects the checkbox      confirmation
    the user clicks the button/link    jQuery = button:contains("Send to project team")
    the user clicks the button/link    jQuery = button:contains("Publish to project team")

Applicant uploads the GOL
    [Arguments]  ${projectID}
    the user navigates to the page        ${server}/project-setup/project/${projectID}
    the user clicks the button/link       link = Grant offer letter
    the user uploads the file             signedGrantOfferLetter    ${valid_pdf}
    the user clicks the button/link       css = .govuk-button[data-js-modal = "modal-confirm-grant-offer-letter"]
    the user clicks the button/link       id = submit-gol-for-review

Applicant uploads the GOL using Docusign
    [Arguments]  ${projectID}
    the user navigates to the page            ${server}/project-setup/project/${projectID}
    the user clicks the button/link           link = Grant offer letter
    the user clicks the button/link           link = docusign
    the user should see the element           jQuery = span:contains("Please Review & Act on These Documents")
    the user selects the checkbox             disclosureAccepted
    the user clicks the button/link           jQuery = button:contains("Continue")
    the user clicks the button/link           jQuery = span:contains("Start")
    the user clicks the button/link           css = div.initials-tab-content
    The user enters text to a docusign field  jQuery = input[id^="tab-form"]:first  09/06/2020
    The user enters text to a docusign field  jQuery = input[id^="tab-form"]:last   10/06/2020
    the user clicks the button/link           css = div.signature-tab-content
    the user clicks the button/link           css = div.documents-finish-button-decoration
    the user should see the element           jQuery = h1:contains("Grant offer letter")

the internal user approve the GOL
    [Arguments]  ${projectID}
    log in as a different user          &{internal_finance_credentials}
    the user navigates to the page      ${server}/project-setup-management/project/${projectID}/grant-offer-letter/send
    the user selects the radio button   APPROVED  acceptGOL
    the user clicks the button/link     id = submit-button
    the user clicks the button/link     id = accept-signed-gol
    the user should see the element     jQuery = .success-alert h2:contains("These documents have been approved.")

the internal user rejects the GOL
    [Arguments]  ${projectID}
    log in as a different user            &{internal_finance_credentials}
    the user navigates to the page        ${server}/project-setup-management/project/${projectID}/grant-offer-letter/send
    the user selects the radio button     REJECTED  rejectGOL
    the user enters text to a text field  id = gol-reject-reason   Rejected
    the user clicks the button/link       id = submit-button
    the user clicks the button/link       jQuery = button:contains("Reject signed grant offer letter")
    the user should see the element       jQuery = .warning-alert p:contains("These documents have been reviewed and rejected. We have returned them to the Project Manager.")

the applicant is able to see the rejected GOL
    [Arguments]  ${projectID}
    the user navigates to the page            ${server}/project-setup/project/${projectID}
    the user clicks the button/link           link = Grant offer letter
    the user should see the element           jQuery = .fail-alert h2:contains("Your grant offer letter has been rejected by Innovate UK")

the user enters bank details
    the user clicks the button/link                      link = Bank details
    the user enters text to a text field                 name = accountNumber  ${Account_Two}
    the user enters text to a text field                 name = sortCode  ${Sortcode_two}
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex
    the user clicks the button/link                      jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link                      id = submit-bank-details-model-button

The user is able to complete project details section
    the user clicks the button/link         link = Project details
    the user clicks the button/link         link = Correspondence address
    the user updates the correspondence address
    the user clicks the button/link         link = Return to set up your project

The user completes the project team section
    the user selects their finance contact   financeContact1
    the user clicks the button/link          link = Project manager
    the user should see project manager/finance contact validations    Save project manager   You need to select a Project Manager before you can continue.
    the user selects the radio button        projectManager   projectManager1
    the user clicks the button/link          jQuery = button:contains("Save project manager")
    the user clicks the button/link          link = Set up your project

The user uploads the exploitation plan
    the user clicks the button/link     link = Exploitation plan
    the user uploads the file           css = .inputfile  ${valid_pdf}
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm
    the user clicks the button/link     link = Return to documents

The user uploads the Test document type
    the user clicks the button/link     link = Test document type
    the user uploads the file           css = .inputfile  ${valid_pdf}
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm
    the user clicks the button/link     link = Return to documents

The user is able to complete the Documents section
    [Documentation]  IFS-5700
    Given the user clicks the button/link     link = Documents
    When the user uploads the exploitation plan
    And the user clicks the button/link       link = Set up your project
    Then the user should see the element      jQuery = .progress-list li:nth-child(3):contains("Awaiting review")

The user selects their finance contact
    [Arguments]  ${financeContactName}
    the user clicks the button/link     link = Your finance contact
    the user should see project manager/finance contact validations    Save finance contact   You need to select a finance contact before you can continue.
    the user selects the radio button   financeContact   ${financeContactName}
    the user clicks the button/link     jQuery = button:contains("Save finance contact")

the user should see project manager/finance contact validations
    [Arguments]   ${save_CTA}  ${errormessage}
    the user clicks the button/link                  jQuery = button:contains("${save_CTA}")
    the user should see a field and summary error    ${errormessage}

the user updates the correspondence address
    the user clicks the button/link                     jQuery = .govuk-button:contains("Save")
    the user should see a field and summary error       Search using a valid postcode or enter the address manually.
    the user enter the Correspondence address
    the user should see the address data
    the user clicks the button/link                     link = Correspondence address
    the user clicks the button/link                     jQuery = .govuk-button:contains("Save address")
    the user should see the element                     jQuery = td:contains("Correspondence address") ~ td:contains("Montrose House 1, Neston, CH64 3RU")

the user should see the address data
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' != 'NO'    the user should see the valid data
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' == 'NO'    the user should see the dummy data

the user should see the valid data
    the user should see the element           jQuery = td:contains("Correspondence address") ~ td:contains("Am Reprographics, Bristol, BS1 4NT")

the user should see the dummy data
    the user should see the element           jQuery = td:contains("Correspondence address") ~ td:contains("Montrose House 1, Neston, CH64 3RU")

project finance submits monitoring officer
    [Arguments]    ${project_id}  ${fname}  ${lname}  ${email}  ${phone_number}
    log in as a different user              &{internal_finance_credentials}
    the user navigates to the page          ${server}/project-setup-management/project/${project_id}/monitoring-officer
    the user enters text to a text field    id = firstName    ${fname}
    the user enters text to a text field    id = lastName    ${lname}
    The user enters text to a text field    id = emailAddress    ${email}
    The user enters text to a text field    id = phoneNumber    ${phone_number}
    the user clicks the button/link         jQuery = .govuk-button[type = "submit"]:contains("Assign Monitoring Officer")
    the user clicks the button/link         jQuery = .modal-assign-mo button:contains("Assign Monitoring Officer")

partner submits his bank details
    [Arguments]  ${email}  ${project}  ${account_number}  ${sort_code}
    partner fills in his bank details  ${email}  ${project}  ${account_number}  ${sort_code}
    wait until keyword succeeds without screenshots  30 s  500 ms  the user should not see the element  jQuery = .govuk-button:contains("Submit bank account details")   # Added this wait so to give extra execution time
    the user should see the element    jQuery = dt:contains("Account number") + dd:contains("****")
    # Have splitted this check from the rest of the keyword, which i now name into 'partner fills in his bank details'
    # Because this little check adds a bit of extra time and validation that the Bank details are submitted.
    # However, not all test cases submit the Bank details when the button is pressed, as we also check for validation messages
    # I am using in those test cases the keyword 'partner fills in his bank details' directly

partner fills in his bank details
    [Arguments]  ${email}  ${project}  ${account_number}  ${sort_code}
    log in as a different user                       ${email}    ${short_password}
    the user navigates to the page                   ${server}/project-setup/project/${project}/bank-details
    the user enters text to a text field             id = accountNumber  ${account_number}
    the user enters text to a text field             id = sortCode  ${sort_code}
    the user enters text to a text field             name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                  id = postcode-lookup
    the user selects the index from the drop-down menu  1  id=addressForm.selectedPostcodeIndex
    wait until keyword succeeds without screenshots  30 s  500 ms  the user clicks the button/link  jQuery = .govuk-button:contains("Submit bank account details")
    wait until keyword succeeds without screenshots  30 s  500 ms  the user clicks the button/link  id = submit-bank-details-model-button

finance contacts are selected and bank details are approved
    log in as a different user      &{lead_applicant_credentials}
    the user navigates to the page  ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/
    ${finance_contact}  ${val} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element  jQuery = .progress-list li:nth-child(2):contains("Completed")
    the user clicks the button/link   link = Project details
    run keyword if  '${finance_contact}' == 'PASS'  run keywords  partners submit their finance contacts  bank details are approved for all businesses

Moving ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup if it isn't already

the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup if it isn't already
    The user logs-in in new browser  &{lead_applicant_credentials}
    ${update_comp}  ${value} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element  jQuery = h2:contains("Set up your project") ~ ul a:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    run keyword if    '${update_comp}' == 'PASS'  the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    log in as a different user   &{lead_applicant_credentials}
    Set Suite Variable  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  ${getProjectId("${FUNDERS_PANEL_APPLICATION_1_TITLE}")}
    the user navigates to the page  ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}
    ${project_details}  ${completed} =   Run Keyword And Ignore Error Without Screenshots    the user should not see the element    jQuery = ul li.complete a:contains("Project details")
    run keyword if  '${project_details}' == 'PASS'  lead partner navigates to project and fills project details

the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    log in as a different user              &{internal_finance_credentials}
    the user navigates to the page          ${server}/management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/funding
    Set Focus To Element                    css = label[for = "app-row-1"]
    the user selects the checkbox           app-row-1
    Set Focus To Element                    css = label[for = "app-row-2"]
    the user selects the checkbox           app-row-2
    the user clicks the button/link         jQuery = button:contains("Successful")
    the user should see the element         jQuery = td:contains("Successful")
    the user clicks the button/link         jQuery = a:contains("Competition")
    the user clicks the button/link         jQuery = a:contains("Manage funding notifications")
    Set Focus To Element                    css = label[for = "app-row-103"]
    the user selects the checkbox           app-row-103
    Set Focus To Element                    css = label[for = "app-row-104"]
    the user selects the checkbox           app-row-104
    the user clicks the button/link         jQuery = .govuk-button:contains("Write and send email")
    the internal sends the descision notification email to all applicants  EmailTextBody
    the user should see the element         jQuery = h1:contains("Manage funding applications")

lead partner navigates to project and fills project details
    log in as a different user            &{lead_applicant_credentials}
    project lead submits project details and team  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}

project lead submits project address
#Used in 12__ATI_compCreationToSubmission
    [Arguments]  ${project_id}
    the user navigates to the page                ${server}/project-setup/project/${project_id}/details/project-address
    the user enters text to a text field          id = addressForm.postcodeInput  BS1 4NT
    the user clicks the button/link               id = postcode-lookup
    the user selects the index from the drop-down menu  1  id=addressForm.selectedPostcodeIndex
    the user clicks the button/link               jQuery = button:contains("Save address")

project lead submits project details and team
    [Arguments]  ${project_id}
    project lead submits project address    ${project_id}
    the user navigates to the page     ${server}/project-setup/project/${project_id}/team/project-manager
    the user selects the radio button  projectManager  projectManager2
    the user clicks the button/link    jQuery = .govuk-button:contains("Save")
    the user navigates to the page     ${server}/project-setup/project/${project_id}/team

partners submit their finance contacts
    the partner submits their finance contact  ${EMPIRE_LTD_ID}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  &{lead_applicant_credentials}
    the partner submits their finance contact  ${organisationLudlowId}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  &{collaborator1_credentials}
    the partner submits their finance contact  ${organisationEggsId}    ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  &{collaborator2_credentials}

the partner submits their finance contact
    [Arguments]    ${org_id}  ${project}  &{credentials}
    log in as a different user  &{credentials}
    navigate to external finance contact page, choose finance contact and save  ${org_id}  financeContact1  ${project}

navigate to external finance contact page, choose finance contact and save
    [Arguments]  ${org_id}   ${financeContactSelector}  ${project}
    the user navigates to the page     ${server}/project-setup/project/${project}/team/finance-contact/organisation/${org_id}
    the user selects the radio button  financeContact  ${financeContactSelector}
    the user clicks the button/link    jQuery = .govuk-button:contains("Save")
    ${project_details}  ${complete}=  Run Keyword And Ignore Error Without Screenshots    the user should see the element    link=Select project location
    run keyword if  '${project_details}' == 'PASS'  select project location  ${org_id}  ${project}

Select project location
    [Arguments]  ${org_id}  ${projectId}
    the user navigates to the page        ${server}/project-setup/project/${projectId}/organisation/${org_id}/partner-project-location
    the user enters text to a text field  css = #postcode  ${Postcode}
    the user clicks the button/link       css = button[type = "submit"]
    the user clicks the button/link       link = Set up your project

bank details are approved for all businesses
    partners submit bank details
    the project finance user has approved bank details

partners submit bank details
    partner submits his bank details  ${PS_PD_Lead_Partner_Email}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  ${Account_One}  ${Sortcode_One}
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_1_PARTNER_EMAIL}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  ${Account_One}  ${Sortcode_One}
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  ${Account_One}  ${Sortcode_One}

the project finance user has approved bank details
    log in as a different user                          &{internal_finance_credentials}
    the project finance user approves bank details for  ${EMPIRE_LTD_NAME}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}
    the project finance user approves bank details for  ${organisationLudlowName}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}
    the project finance user approves bank details for  ${organisationEggsName}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}

the project finance user approves bank details for
    [Arguments]    ${org_name}  ${org_id}
    the user navigates to the page            ${server}/project-setup-management/project/${org_id}/review-all-bank-details
    the user clicks the button/link           link = ${org_name}
    the user should see the element           jQuery = td:contains("${org_name}")
    the user clicks the button/link           jQuery = .govuk-button:contains("Approve bank account details")
    the user clicks the button/link           jQuery = .govuk-button:contains("Approve account")

project manager submits both documents
    [Arguments]  ${email}  ${password}  ${project}
    log in as a different user          ${email}  ${password}
    the user navigates to the page      ${server}/project-setup/project/${project}/document/all
    the user clicks the button/link     link = Collaboration agreement
    the user uploads the file           css = .inputfile    ${valid_pdf}
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm
    the user clicks the button/link     link = Return to documents
    the user clicks the button/link     link = Exploitation plan
    the user uploads the file           css = .inputfile    ${upload_folder}/${valid_pdf}
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm

project finance approves both documents
    [Arguments]  ${project}
    log in as a different user             &{internal_finance_credentials}
    the user navigates to the page         ${SERVER}/project-setup-management/project/${project}/document/all
    the user clicks the button/link        link = Collaboration agreement
    internal user approve uploaded documents
    the user clicks the button/link         link = Return to documents
    the user clicks the button/link         link = Exploitation plan
    internal user approve uploaded documents

project finance generates the Spend Profile
    [Arguments]  ${lead}  ${partner}  ${academic_partner}  ${project}
    log in as a different user              &{internal_finance_credentials}
    project finance approves Viability for  ${lead}  ${project}
    project finance approves Viability for  ${partner}  ${project}
    project finance approves Eligibility    ${lead}  ${partner}  ${academic_partner}  ${project}
    the user navigates to the page          ${server}/project-setup-management/project/${project}/finance-check
    the user clicks the button/link         css = .generate-spend-profile-main-button
    the user clicks the button/link         css = #generate-spend-profile-modal-button

The user submits the spend profile
    the user clicks the button/link    jQuery = button:contains("Mark as complete")
    the user clicks the button/link    link = Review and submit project spend profile
    the user clicks the button/link    link = Submit project spend profile
    the user clicks the button/link    id = submit-send-all-spend-profiles

project finance approves Viability for
    [Arguments]  ${partner}  ${project}
    the user navigates to the page       ${server}/project-setup-management/project/${project}/finance-check/organisation/${partner}/viability
    the user selects the checkbox        costs-reviewed
    the user selects the checkbox        project-viable
    Set Focus To Element                 link = Contact us
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link      css = #confirm-button
    the user clicks the button/link      jQuery = .modal-confirm-viability .govuk-button:contains("Confirm viability")

project finance approves Eligibility
    [Arguments]  ${lead}  ${partner}  ${academic_partner}  ${project}
    the user navigates to the page  ${server}/project-setup-management/project/${project}/finance-check/organisation/${lead}/eligibility
    the user approves project costs
    the user navigates to the page  ${server}/project-setup-management/project/${project}/finance-check/organisation/${partner}/eligibility
    the user approves project costs
    the user navigates to the page  ${server}/project-setup-management/project/${project}/finance-check/organisation/${academic_partner}/eligibility
    the user approves project costs

the user approves project costs
    the user selects the checkbox      project-eligible
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link    jQuery = .govuk-button:contains("Approve eligible costs")
    the user clicks the button/link    name = confirm-eligibility

proj finance approves the spend profiles
    [Arguments]  ${project}
    log in as a different user       &{internal_finance_credentials}
    the user navigates to the page   ${server}/project-setup-management/project/${project}/spend-profile/approval
    the user selects the checkbox    approvedByLeadTechnologist
    the user clicks the button/link  jQuery = .govuk-button:contains("Approved")
    the user clicks the button/link  jQuery = .modal-accept-profile button:contains("Approve")

all partners submit their Spend Profile
    Login and submit partners spend profile  ${PS_EF_Application_Partner_Email}  ${Kazio_Id}  ${PS_EF_Application_Project_No}
    Login and submit partners spend profile  ${PS_EF_Application_Academic_Email}  ${Cogilith_Id}  ${PS_EF_Application_Project_No}
    Login and submit leads spend profile     ${PS_EF_Application_Lead_Partner_Email}  ${Gabtype_Id}  ${Gabtype_Name}  ${PS_EF_Application_Project_No}

Login and submit partners spend profile
    [Arguments]  ${email}  ${org_id}  ${project}
    log in as a different user       ${email}  ${short_password}
    the user navigates to the page   ${server}/project-setup/project/${project}/partner-organisation/${org_id}/spend-profile
    the user clicks the button/link  link = Submit to lead partner
    the user clicks the button/link  jQuery = button.govuk-button:contains("Submit")

Login and submit leads spend profile
    [Arguments]  ${email}  ${org_id}  ${org_name}  ${project}
    log in as a different user       ${email}  ${short_password}
    the user navigates to the page   ${server}/project-setup/project/${project}/partner-organisation/${org_id}/spend-profile
    the user clicks the button/link  link = ${org_name}
    the user clicks the button/link  css = [name="mark-as-complete"]
    the user navigates to the page   ${server}/project-setup/project/${project}/partner-organisation/${org_id}/spend-profile
    the user clicks the button/link  jQuery = .govuk-button:contains("Review and send total project spend profile")
    the user clicks the button/link  link = Send project spend profile
    the user clicks the button/link  id = submit-send-all-spend-profiles

project finance approves bank details for ${PS_EF_Application_Title}
    log in as a different user                          &{internal_finance_credentials}
    the project finance user approves bank details for  ${Gabtype_Name}  ${PS_EF_Application_Project_No}
    the project finance user approves bank details for  ${Kazio_Name}  ${PS_EF_Application_Project_No}
    the project finance user approves bank details for  ${Cogilith_Name}  ${PS_EF_Application_Project_No}

the user changes the start date
    [Arguments]  ${year}
    the user clicks the button/link         link = Target start date
    the user enters text to a text field    id = projectStartDate-date_year  ${year}
    the user clicks the button/link         jQuery = .govuk-button:contains("Save")

The project finance user approves bank details
    [Arguments]  ${org_id}
    the user navigates to the page            ${server}/project-setup-management/project/${org_id}/review-all-bank-details
    the user clicks the button/link           jQuery = .govuk-button:contains("Approve bank account details")
    the user clicks the button/link           jQuery = .govuk-button:contains("Approve account")

internal user approve uploaded documents
    the user selects the radio button      approved   true
    the user clicks the button/link        id = submit-button
    the user clicks the button/link        id = accept-document
    the user should see the element        jQuery = p:contains("You have approved this document.")

the user enter the Correspondence address
    the user enters text to a text field                id = addressForm.postcodeInput  BS1 4NT
    the user clicks the button/link                     id = postcode-lookup
    the user selects the index from the drop-down menu  1  id=addressForm.selectedPostcodeIndex
    the user clicks the button/link                     jQuery = .govuk-button:contains("Save address")

the user uploads to the collaboration agreement/exploitation plan
    [Arguments]   ${file_name}
    the user uploads the file  css = .inputfile  ${file_name}

the user goes to documents page
    [Arguments]  ${link1}  ${link2}
    the user clicks the button/link    link = ${link1}
    the user clicks the button/link    link = ${link2}

Search for MO
    [Arguments]  ${MO_name}  ${MO_fullname}
    the element should be disabled      jQuery = button:contains("View Monitoring Officer")
    input text                          id = userId    ${MO_name}
    the user clicks the button/link     jQuery = ul li:contains("${MO_fullname}")
    the user clicks the button/link     jQuery = button:contains("View Monitoring Officer")

The internal user assign project to MO
    [Arguments]  ${search_ID}  ${project_name}
    the element should be disabled      jQuery = button:contains("Assign")
    input text                          id = projectId    ${search_ID}
    the user clicks the button/link     jQuery = ul li:contains("${search_ID} - ${project_name}")
    the user clicks the button/link     jQuery = button:contains("Assign")

the user completes the project team details
    the user clicks the button/link     link = Project team
    the user clicks the button/link     link = Your finance contact
    the user selects the radio button   financeContact   financeContact1
    the user clicks the button/link     jQuery = button:contains("Save finance contact")
    the user clicks the button/link     link = Project manager
    the user selects the radio button   projectManager   projectManager1
    the user clicks the button/link     jQuery = button:contains("Save project manager")
    the user clicks the button/link     link = Set up your project
    #the user should see the element     jQuery = .progress-list li:nth-child(2):contains("Completed")

PM uploads the project documents
    [Arguments]  ${compName}
    the user navigates to the page         ${SERVER}/project-setup/project/${compName}/document/all
    the user clicks the button/link        link = Exploitation plan
    the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    the user should see the element        jQuery = .upload-section:contains("Exploitation plan") a:contains("${valid_pdf}")
    the user goes to documents page        Back to document overview  Collaboration agreement
    the user uploads to the collaboration agreement/exploitation plan    ${valid_pdf}
    the user should see the element        jQuery = .upload-section:contains("Collaboration agreement") a:contains("${valid_pdf}")
    the user should not see an error in the page

applicant user enters bank details
    the user enters text to a text field                name = accountNumber  ${Account_Two}
    the user enters text to a text field                name = sortCode  ${Sortcode_two}
    the user clicks the button/link                     jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link                     jquery = button:contains("Cancel")
    the user should not see the element                 jQUery = p:contains("The bank account details below are being reviewed")
    the user clicks the button/link                     jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link                     id = submit-bank-details-model-button

PM submits both documents
    [Arguments]  ${compName}
    the user clicks the button/link     link = Collaboration agreement
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     jQuery = button:contains("Cancel")
    the user should see the element     name = deleteDocument
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm
    the user goes to documents page     Return to documents  Exploitation plan
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm
    the user should be redirected to the correct page    ${SERVER}/project-setup/project/${compName}

Project finance is able to approve the bank details
    [Arguments]  ${orgName}
    the user clicks the button/link    jQuery = a:contains("Review bank details")
    the user clicks the button/link    jQuery = a:contains("${orgName}")
    the user clicks the button/link    jQuery = button:contains("Approve bank account details")
    the user clicks the button/link    jQuery = button:contains("Approve account")
    the user should see the element    jQuery = h2:contains("The bank details provided have been approved.")

the user updates the project location in project setup
    [Arguments]  ${link}
    the user navigates to the page        ${link}
    the user clicks the button/link       link = Edit
    the user enters text to a text field  css = #postcode  ${Postcode}
    the user clicks the button/link       css = button[type = "submit"]
    the user clicks the button/link       link = Set up your project

the user adds a new partner organisation
    [Arguments]   ${partnerOrgName}  ${persFullName}  ${email}
    the user enters text to a text field  id = organisationName  ${partnerOrgName}
    the user enters text to a text field  id = userName  ${persFullName}
    the user enters text to a text field  id = email  ${email}
    the user clicks the button/link       jQuery = .govuk-button:contains("Invite partner organisation")
    the user should see the element       jQuery = h2:contains(${partnerOrgName})

The user accepts invitation and selects organisation type
    [Arguments]   ${orgId}  ${orgName}
    the user clicks the button/link                       jQuery = .govuk-button:contains("Yes, create an account")
    the user selects the radio button                     organisationType    1
    the user clicks the button/link                       jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House  ${orgId}  ${orgName}

The user fills in account details
    [Arguments]  ${firstName}  ${lastName}
    the user enters text to a text field   id = firstName     ${firstName}
    the user enters text to a text field   id = lastName      ${lastName}
    the user enters text to a text field   id = phoneNumber   07123456789
    the user enters text to a text field   id = password      ${short_password}
    the user selects the checkbox          termsAndConditions

The user verifies their account
    [Arguments]  ${email}
    the user should see the element                jQuery = h1:contains("Please verify your email address")
    the user reads his email and clicks the link   ${email}  Please verify your email address  You have recently set up an account with the Innovation Funding Service.  1
    the user should see the element                jQuery = h1:contains("Account verified")

A new organisation logs in and sees the project
    [Arguments]  ${email}
    the user clicks the button/link   link = Sign in
    Logging in and Error Checking     ${email}  ${short_password}

a new organisation is able to accept project invite
    [Arguments]  ${fname}  ${sname}  ${email}  ${orgId}  ${orgName}  ${applicationID}  ${appTitle}
    logout as user
    the user reads his email and clicks the link                  ${email}  Invitation to join project ${applicationID}: ${appTitle}  You have been invited to join the project ${appTitle}
    the user accepts invitation and selects organisation type     ${orgId}  ${orgName}
    the user fills in account details                             ${fname}  ${sname}
    the user clicks the button/link                               jQuery = button:contains("Create account")
    the user verifies their account                               ${email}
    a new organisation logs in and sees the project               ${email}
    the user should see the element                               jQuery = ul:contains("${appTitle}") .status:contains("Ready to join project")
    the user clicks the button/link                               link = ${appTitle}
    the user should see the element                               jQuery = h1:contains("Join project")

the user completes your organisation
    the user enters text to a text field                    css = #financialYearEndMonthValue    12
    the user enters text to a text field                    css = #financialYearEndYearValue    2016
    the user selects the radio button                       organisationSize  MEDIUM
    the user enters text to a text field                    css = #annualTurnoverAtLastFinancialYear   5600
    the user enters text to a text field                    css = #annualProfitsAtLastFinancialYear    3000
    the user enters text to a text field                    css = #annualExportAtLastFinancialYear    4000
    the user enters text to a text field                    css = #researchAndDevelopmentSpendAtLastFinancialYear    5660
    the user enters text to a text field                    css = #headCountAtLastFinancialYear    0
    the user clicks the button/link                         jQuery = button:contains("Mark as complete")

The new partner can complete Your organisation
    the user clicks the button/link    link = Your organisation
    the user completes your organisation
    the user should see the element     jQuery = li div:contains("Your organisation") ~ .task-status-complete

the user is able to remove a pending partner organisation
    [Arguments]  ${orgName}
    the user clicks the button/link             jQuery = h2:contains("${orgName}")~ button:contains("Remove organisation"):first
    the user should not see the element         jQuery = h2:contains(${orgName})