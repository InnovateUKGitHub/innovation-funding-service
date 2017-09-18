*** Settings ***
Resource    ../../resources/variables/GLOBAL_VARIABLES.robot
Resource    ../../resources/defaultResources.robot


*** Variables ***
#Project: London underground – enhancements to existing stock and logistics
# GOL = Grant Offer Letter
${Gabtype_Id}       52
${Gabtype_Name}     Gabtype
${Kazio_Id}         53
${Kazio_Name}       Kazio
${Cogilith_Id}      54
${Cogilith_Name}    Cogilith
${PS_GOL_Competition_Id}         ${competition_ids["Rolling stock future developments"]}
${PS_GOL_APPLICATION_TITLE}      London underground - enhancements to existing stock and logistics
${PS_GOL_APPLICATION_NO}         ${application_ids["${PS_GOL_APPLICATION_TITLE}"]}
${PS_GOL_APPLICATION_NUMBER}     ${PS_GOL_APPLICATION_NO}
${PS_GOL_APPLICATION_HEADER}     ${PS_GOL_APPLICATION_TITLE}
${PS_GOL_APPLICATION_PROJECT}    ${project_ids["${PS_GOL_APPLICATION_TITLE}"]}
${PS_GOL_APPLICATION_LEAD_ORGANISATION_ID}      ${Gabtype_ID}
${PS_GOL_APPLICATION_LEAD_ORGANISATION_NAME}    ${Gabtype_NAME}
${PS_GOL_APPLICATION_LEAD_PARTNER_EMAIL}        ${test_mailbox_one}+amy@gmail.com
${PS_GOL_APPLICATION_PM_EMAIL}                  ${test_mailbox_one}+amy@gmail.com
${PS_GOL_APPLICATION_FINANCE_CONTACT_EMAIL}     ${test_mailbox_one}+marian@gmail.com
${PS_GOL_APPLICATION_PARTNER_EMAIL}             ${test_mailbox_one}+karen@gmail.com
${PS_GOL_APPLICATION_ACADEMIC_EMAIL}            ${test_mailbox_one}+juan@gmail.com

#Project: High-speed rail and its effects on air quality
# MD = Mandatory Documents
${Ooba_Id}          49
${Ooba_Name}        Ooba
${Wordpedia_Id}     50
${Wordpedia_Name}   Wordpedia
${Jabbertype_Id}    51
${Jabbertype_Name}  Jabbertype
${PS_MD_Competition_Name}       Rolling stock future developments
${PS_MD_Competition_Id}         ${competition_ids["${PS_MD_Competition_Name}"]}
${PS_MD_APPLICATION_TITLE}      High-speed rail and its effects on air quality
${PS_MD_APPLICATION_NO}         ${application_ids["${PS_MD_APPLICATION_TITLE}"]}
${PS_MD_APPLICATION_NUMBER}     ${PS_MD_APPLICATION_NO}
${PS_MD_APPLICATION_HEADER}     ${PS_MD_APPLICATION_TITLE}
${PS_MD_APPLICATION_PROJECT}    ${project_ids["${PS_MD_APPLICATION_TITLE}"]}
${PS_MD_APPLICATION_LEAD_ORGANISATION_ID}    ${Ooba_ID}
${PS_MD_APPLICATION_LEAD_ORGANISATION_NAME}  ${Ooba_Name}
${PS_MD_APPLICATION_LEAD_PARTNER_EMAIL}      ralph.young@ooba.example.com
${PS_MD_APPLICATION_PM_EMAIL}                ralph.young@ooba.example.com
${PS_MD_APPLICATION_PARTNER_EMAIL}           tina.taylor@wordpedia.example.com
${PS_MD_APPLICATION_ACADEMIC_EMAIL}          antonio.jenkins@jabbertype.example.com

#Project: Point control and automated monitoring
# SP = Spend Profile
${Katz_Id}         46
${Katz_Name}       Katz
${Meembee_Id}      47
${Meembee_Name}    Meembee
${Zooveo_Id}       48
${Zooveo_Name}     Zooveo
${PS_SP_Competition_Name}       Rolling stock future developments
${PS_SP_Competition_Id}         ${competition_ids["${PS_SP_Competition_Name}"]}
${PS_SP_APPLICATION_TITLE}      Point control and automated monitoring
${PS_SP_APPLICATION_NO}         ${application_ids["${PS_SP_APPLICATION_TITLE}"]}
${PS_SP_APPLICATION_NUMBER}     ${PS_SP_APPLICATION_NO}
${PS_SP_APPLICATION_HEADER}     ${PS_SP_APPLICATION_TITLE}
${PS_SP_APPLICATION_PROJECT}    ${project_ids["${PS_SP_APPLICATION_TITLE}"]}
${PS_SP_APPLICATION_LEAD_ORGANISATION_ID}    ${Katz_Id}
${PS_SP_APPLICATION_LEAD_ORGANISATION_NAME}  ${Katz_Name}
${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}      theo.simpson@katz.example.com
${PS_SP_APPLICATION_PM_EMAIL}                phillip.ramos@katz.example.com
${PS_SP_APPLICATION_PARTNER_EMAIL}           kimberly.fowler@meembee.example.com
${PS_SP_APPLICATION_ACADEMIC_EMAIL}          craig.ortiz@zooveo.example.com

#Project: Grade crossing manufacture and supply
# BD = Bank Details
${Vitruvius_Id}       32
${Vitruvius_Name}     Vitruvius Stonework Limited
${A_B_Cad_Services_Id}    44
${A_B_Cad_Services_Name}  A B Cad Services
${Armstrong_Butler_Id}       45
${Armstrong_Butler_Name}     Armstrong & Butler Ltd
${PS_BD_Competition_Id}         ${competition_ids["Rolling stock future developments"]}
${PS_BD_Competition_Name}       Rolling stock future developments
${PS_BD_APPLICATION_TITLE}      Grade crossing manufacture and supply
${PS_BD_APPLICATION_NUMBER}     ${application_ids["${PS_BD_APPLICATION_TITLE}"]}
${PS_BD_APPLICATION_HEADER}     ${PS_BD_APPLICATION_TITLE}
${PS_BD_APPLICATION_PROJECT}    ${project_ids["${PS_BD_APPLICATION_TITLE}"]}
${PS_BD_APPLICATION_LEAD_ORGANISATION_ID}    ${Vitruvius_Id}
${PS_BD_APPLICATION_LEAD_ORGANISATION_NAME}  ${Vitruvius_Name}
${PS_BD_APPLICATION_LEAD_PARTNER_EMAIL}      diane.scott@vitruvius.example.com
${PS_BD_APPLICATION_PM_EMAIL}                diane.scott@vitruvius.example.com
${PS_BD_APPLICATION_LEAD_FINANCE}            Diane Scott
${PS_BD_APPLICATION_LEAD_TELEPHONE}          49692921151
${PS_BD_APPLICATION_PARTNER_EMAIL}           ryan.welch@abcad.example.com
${PS_BD_APPLICATION_PARTNER_FINANCE}         Ryan Welch
${PS_BD_APPLICATION_ACADEMIC_EMAIL}          sara.armstrong@armstrong.example.com
${PS_BD_APPLICATION_ACADEMIC_FINANCE}        Sara Armstrong

#Project: New materials for lighter stock
# EF = Experian feedback
${Ntag_Id}        41
${Ntag_Name}      Ntag
${Ntag_No}        18451018
${Ntag_Street}    39357 Fisk Drive
${Jetpulse_Id}    42
${Jetpulse_Name}  Jetpulse
${Wikivu_Id}      43
${Wikivu_Name}    Wikivu
${PS_EF_Competition_Id}         ${competition_ids["Rolling stock future developments"]}
${PS_EF_Competition_Name}       Rolling stock future developments
${PS_EF_APPLICATION_TITLE}      New materials for lighter stock
${PS_EF_APPLICATION_NO}         ${application_ids["${PS_EF_APPLICATION_TITLE}"]}
${PS_EF_APPLICATION_NUMBER}     ${PS_EF_APPLICATION_NO}
${PS_EF_APPLICATION_HEADER}     ${PS_EF_APPLICATION_TITLE}
${PS_EF_APPLICATION_PROJECT}    ${project_ids["${PS_EF_APPLICATION_TITLE}"]}
${PS_EF_APPLICATION_LEAD_ORGANISATION_ID}    ${Ntag_Id}
${PS_EF_APPLICATION_LEAD_ORGANISATION_NAME}  ${Ntag_Name}
${PS_EF_APPLICATION_LEAD_PARTNER_EMAIL}      steven.hicks@ntag.example.com
${PS_EF_APPLICATION_PM_EMAIL}                steven.hicks@ntag.example.com
${PS_EF_APPLICATION_PARTNER_EMAIL}           robert.perez@jetpulse.example.com
${PS_EF_APPLICATION_ACADEMIC_EMAIL}          bruce.perez@wikivu.example.com

#Project: Magic material
${PROJECT_SETUP_COMPETITION}          ${competition_ids["New designs for a circular economy"]}
${PROJECT_SETUP_COMPETITION_NAME}     New designs for a circular economy
${PROJECT_SETUP_APPLICATION_1_TITLE}  Magic material
${PROJECT_SETUP_APPLICATION_1}        ${application_ids["${PROJECT_SETUP_APPLICATION_1_TITLE}"]}
${PROJECT_SETUP_APPLICATION_1_NUMBER}    ${PROJECT_SETUP_APPLICATION_1}
${PROJECT_SETUP_APPLICATION_1_HEADER}    ${PROJECT_SETUP_APPLICATION_1_TITLE}
${PROJECT_SETUP_APPLICATION_1_PROJECT}   ${project_ids["${PROJECT_SETUP_APPLICATION_1_TITLE}"]}
${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_ID}    ${EMPIRE_LTD_ID}
${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}    ${EMPIRE_LTD_NAME}
${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_COMPANY_NUMBER}    60674010
${PROJECT_SETUP_APPLICATION_1_LEAD_COMPANY_TURNOVER}    4560000
${PROJECT_SETUP_APPLICATION_1_LEAD_COMPANY_HEADCOUNT}    1230
${PROJECT_SETUP_APPLICATION_1_LEAD_PARTNER_EMAIL}  ${lead_applicant_credentials["email"]}
${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}    ${test_mailbox_one}+projectsetuppm@gmail.com
${PROJECT_SETUP_APPLICATION_1_PARTNER_ID}    39
${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}    Ludlow
${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_NUMBER}    53532322
${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_TURNOVER}    1230000
${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_HEADCOUNT}    4560
${PROJECT_SETUP_APPLICATION_1_PARTNER_EMAIL}            ${collaborator1_credentials["email"]}
${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_ID}      40
${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}    EGGS
${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}   ${collaborator2_credentials["email"]}
${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}    HIVE IT LIMITED
${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_EMAIL}   ewan+1@hiveit.co.uk

${project_in_setup_page}                ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}
${project_in_setup_details_page}        ${project_in_setup_page}/details
${project_in_setup_team_status_page}    ${project_in_setup_page}/team-status
${project_start_date_page}              ${project_in_setup_details_page}/start-date
${project_address_page}                 ${project_in_setup_details_page}/project-address
${internal_competition_status}          ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status
${notified_application_competition_status}   ${server}/project-setup-management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/status


#Bank details
${account_one}   51406795
${sortCode_one}  404745
${account_two}   12345677
${sortCode_two}  000004

*** Keywords ***
project finance submits monitoring officer
    [Arguments]    ${project_id}  ${fname}  ${lname}  ${email}  ${phone_number}
    log in as a different user              &{internal_finance_credentials}
    the user navigates to the page          ${server}/project-setup-management/project/${project_id}/monitoring-officer
    the user enters text to a text field    id=firstName    ${fname}
    the user enters text to a text field    id=lastName    ${lname}
    The user enters text to a text field    id=emailAddress    ${email}
    The user enters text to a text field    id=phoneNumber    ${phone_number}
    the user clicks the button/link         jQuery=.button[type="submit"]:contains("Assign Monitoring Officer")
    the user clicks the button/link         jQuery=.modal-assign-mo button:contains("Assign Monitoring Officer")

partner submits his bank details
    [Arguments]  ${email}  ${project}  ${account_number}  ${sort_code}
    partner fills in his bank details  ${email}  ${project}  ${account_number}  ${sort_code}
    wait until keyword succeeds without screenshots  30 s  500 ms  the user should not see the element  jQuery=.button:contains("Submit bank account details")   # Added this wait so to give extra execution time
    the user should see the element    jQuery=dt:contains("Account number") + dd:contains("****")
    # Have splitted this check from the rest of the keyword, which i now name into 'partner fills in his bank details'
    # Because this little check adds a bit of extra time and validation that the Bank details are submitted.
    # However, not all test cases submit the Bank details when the button is pressed, as we also check for validation messages
    # I am using in those test cases the keyword 'partner fills in his bank details' directly

partner fills in his bank details
    [Arguments]  ${email}  ${project}  ${account_number}  ${sort_code}
    log in as a different user                       ${email}    ${short_password}
    the user navigates to the page                   ${server}/project-setup/project/${project}/bank-details
    the user enters text to a text field             id=bank-acc-number  ${account_number}
    the user enters text to a text field             id=bank-sort-code  ${sort_code}
    the user clicks the button twice                 css=label[for="address-use-org"]
    the user sees that the radio button is selected  addressType  REGISTERED  # Added this check to give extra execution time
    the user should see the element                  css=#registeredAddress
    wait until keyword succeeds without screenshots  30 s  500 ms  the user clicks the button/link  jQuery=.button:contains("Submit bank account details")
    wait until keyword succeeds without screenshots  30 s  500 ms  the user clicks the button/link  jQuery=.button[name="submit-app-details"]

Moving ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup if it isn't already

finance contacts are selected and bank details are approved
    log in as a different user      &{lead_applicant_credentials}
    the user navigates to the page  ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/details
    ${finance_contact}  ${val}=  Run Keyword And Ignore Error Without Screenshots  the user should not see the element  jQuery=#project-details-finance tr:nth-of-type(1):contains("Yes")
    run keyword if  '${finance_contact}' == 'PASS'  run keywords  partners submit their finance contacts  bank details are approved for all businesses

the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup if it isn't already
    The user logs-in in new browser  &{lead_applicant_credentials}
    ${update_comp}  ${value}=  Run Keyword And Ignore Error Without Screenshots  the user should not see the element  jQuery=h2:contains("Set up your project") ~ ul a:contains("Sensing & Control network using the lighting infrastructure")
    run keyword if    '${update_comp}' == 'PASS'  the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    log in as a different user   &{lead_applicant_credentials}
    the user navigates to the page  ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}
    ${project_details}  ${completed}=  Run Keyword And Ignore Error Without Screenshots    the user should not see the element    jQuery=ul li.complete a:contains("Project details")
    run keyword if  '${project_details}' == 'PASS'  lead partner navigates to project and fills project details
    Set Suite Variable  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  ${getProjectId("${FUNDERS_PANEL_APPLICATION_1_TITLE}")}

the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    log in as a different user              &{internal_finance_credentials}
    the user navigates to the page          ${server}/management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/funding
    the user moves focus to the element     css=label[for="app-row-1"]
    the user selects the checkbox           app-row-1
    the user moves focus to the element     css=label[for="app-row-2"]
    the user selects the checkbox           app-row-2
    the user clicks the button/link         jQuery=button:contains("Successful")
    the user should see the element         jQuery=td:contains("Successful")
    the user clicks the button/link         jQuery=a:contains("Competition")
    the user clicks the button/link         jQuery=a:contains("Manage funding notifications")
    the user moves focus to the element     css=label[for="app-row-103"]
    the user selects the checkbox           app-row-103
    the user moves focus to the element     css=label[for="app-row-104"]
    the user selects the checkbox           app-row-104
    the user clicks the button/link         jQuery=.button:contains("Write and send email")
    the user enters text to a text field    css=[labelledby="message"]      testMessage
    the user clicks the button/link         jQuery=button:contains("Send email to all applicants")
    the user clicks the button/link         jQuery=.send-to-all-applicants-modal button:contains("Send email to all applicants")
    the user should see the text in the page    Manage funding applications

lead partner navigates to project and fills project details
    log in as a different user            &{lead_applicant_credentials}
    project lead submits project details  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}

project lead submits project details
    [Arguments]  ${project_id}
    the user navigates to the page     ${server}/project-setup/project/${project_id}/details/project-address
    the user selects the radio button  addressType  address-use-org
    the user clicks the button/link    jQuery=.button:contains("Save")
    the user navigates to the page     ${server}/project-setup/project/${project_id}/details/project-manager
    the user selects the radio button  projectManager  projectManager2
    the user clicks the button/link    jQuery=.button:contains("Save")
    the user navigates to the page     ${server}/project-setup/project/${project_id}/details
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=.button:contains("Submit")

partners submit their finance contacts
    the partner submits their finance contact  ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_ID}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  &{lead_applicant_credentials}
    the partner submits their finance contact  ${PROJECT_SETUP_APPLICATION_1_PARTNER_ID}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  &{collaborator1_credentials}
    the partner submits their finance contact  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_ID}    ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  &{collaborator2_credentials}

the partner submits their finance contact
    [Arguments]    ${org_id}  ${project}  &{credentials}
    log in as a different user  &{credentials}
    navigate to external finance contact page, choose finance contact and save  ${org_id}  financeContact1  ${project}

navigate to external finance contact page, choose finance contact and save
    [Arguments]  ${org_id}   ${financeContactSelector}  ${project}
    the user navigates to the page     ${server}/project-setup/project/${project}/details/finance-contact?organisation=${org_id}
    the user selects the radio button  financeContact  ${financeContactSelector}
    the user clicks the button/link    jQuery=.button:contains("Save")

bank details are approved for all businesses
    partners submit bank details
    the project finance user has approved bank details

partners submit bank details
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_1_LEAD_PARTNER_EMAIL}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  ${account_one}  ${sortCode_one}
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_1_PARTNER_EMAIL}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  ${account_one}  ${sortCode_one}
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}  ${account_one}  ${sortCode_one}

the project finance user has approved bank details
    log in as a different user                          &{internal_finance_credentials}
    the project finance user approves bank details for  ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}
    the project finance user approves bank details for  ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}
    the project finance user approves bank details for  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}  ${FUNDERS_PANEL_APPLICATION_1_PROJECT}

the project finance user approves bank details for
    [Arguments]    ${org_name}  ${org_id}
    the user navigates to the page            ${server}/project-setup-management/project/${org_id}/review-all-bank-details
    the user clicks the button/link           link=${org_name}
    the user should see the text in the page  ${org_name}
    the user clicks the button/link           jQuery=.button:contains("Approve bank account details")
    the user clicks the button/link           jQuery=.button:contains("Approve account")

project manager submits other documents
    [Arguments]  ${email}  ${password}  ${project}
    log in as a different user       ${email}  ${password}
    the user navigates to the page   ${server}/project-setup/project/${project}/partner/documents
    choose file                      name=collaborationAgreement    ${upload_folder}/testing.pdf
    choose file                      name=exploitationPlan    ${upload_folder}/testing.pdf
    the user reloads the page
    the user clicks the button/link  jQuery=.button:contains("Submit documents")
    the user clicks the button/link  jQuery=.button:contains("Submit")

project finance approves other documents
    [Arguments]  ${project}
    log in as a different user       &{internal_finance_credentials}
    the user navigates to the page   ${SERVER}/project-setup-management/project/${project}/partner/documents
    the user clicks the button/link  jQuery=.button:contains("Accept documents")
    the user clicks the button/link  jQuery=.modal-accept-docs .button:contains("Accept Documents")

project finance generates the Spend Profile
    [Arguments]  ${lead}  ${partner}  ${academic_partner}  ${project}
    log in as a different user              &{internal_finance_credentials}
    project finance approves Viability for  ${lead}  ${project}
    project finance approves Viability for  ${partner}  ${project}
    project finance approves Eligibility    ${lead}  ${partner}  ${academic_partner}  ${project}
    the user navigates to the page          ${server}/project-setup-management/project/${project}/finance-check
    the user clicks the button/link         css=.generate-spend-profile-main-button
    the user clicks the button/link         css=#generate-spend-profile-modal-button

project finance approves Viability for
    [Arguments]  ${partner}  ${project}
    the user navigates to the page       ${server}/project-setup-management/project/${project}/finance-check/organisation/${partner}/viability
    the user selects the checkbox        costs-reviewed
    the user selects the checkbox        project-viable
    the user moves focus to the element  link=Contact us
    the user selects the option from the drop-down menu  Green  id=rag-rating
    the user clicks the button/link      css=#confirm-button
    the user clicks the button/link      jQuery=.modal-confirm-viability .button:contains("Confirm viability")

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
    the user selects the option from the drop-down menu  Green  id=rag-rating
    the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    the user clicks the button/link    name=confirm-eligibility

proj finance approves the spend profiles
    [Arguments]  ${project}
    log in as a different user       &{internal_finance_credentials}
    the user navigates to the page   ${server}/project-setup-management/project/${project}/spend-profile/approval
    the user selects the checkbox    approvedByLeadTechnologist
    the user clicks the button/link  jQuery=.button:contains("Approved")
    the user clicks the button/link  jQuery=.modal-accept-profile button:contains("Approve")

all partners submit their Spend Profile
    Login and submit partners spend profile  ${PS_GOL_APPLICATION_PARTNER_EMAIL}  ${Kazio_Id}  ${PS_GOL_APPLICATION_PROJECT}
    Login and submit partners spend profile  ${PS_GOL_APPLICATION_ACADEMIC_EMAIL}  ${Cogilith_Id}  ${PS_GOL_APPLICATION_PROJECT}
    Login and submit leads spend profile     ${PS_GOL_APPLICATION_LEAD_PARTNER_EMAIL}  ${Gabtype_Id}  ${Gabtype_Name}  ${PS_GOL_APPLICATION_PROJECT}

Login and submit partners spend profile
    [Arguments]  ${email}  ${org_id}  ${project}
    log in as a different user       ${email}  ${short_password}
    the user navigates to the page   ${server}/project-setup/project/${project}/partner-organisation/${org_id}/spend-profile
    the user clicks the button/link  jQuery=a:contains("Submit to lead partner")
    the user clicks the button/link  jQuery=.button:contains("Submit")

Login and submit leads spend profile
    [Arguments]  ${email}  ${org_id}  ${org_name}  ${project}
    log in as a different user       ${email}  ${short_password}
    the user navigates to the page   ${server}/project-setup/project/${project}/partner-organisation/${org_id}/spend-profile
    the user clicks the button/link  link=${org_name}
    the user clicks the button/link  jQuery=.button:contains("Mark as complete")
    the user navigates to the page   ${server}/project-setup/project/${project}/partner-organisation/${org_id}/spend-profile
    the user clicks the button/link  jQuery=.button:contains("Review and send total project spend profile")
    the user clicks the button/link  jQuery=.button:contains("Send project spend profile")
    the user clicks the button/link  css=.modal-confirm-spend-profile-totals .button[value="Send"]

project finance approves bank details for ${PS_GOL_APPLICATION_TITLE}
    log in as a different user                          &{internal_finance_credentials}
    the project finance user approves bank details for  ${Gabtype_Name}  ${PS_GOL_APPLICATION_PROJECT}
    the project finance user approves bank details for  ${Kazio_Name}  ${PS_GOL_APPLICATION_PROJECT}
    the project finance user approves bank details for  ${Cogilith_Name}  ${PS_GOL_APPLICATION_PROJECT}