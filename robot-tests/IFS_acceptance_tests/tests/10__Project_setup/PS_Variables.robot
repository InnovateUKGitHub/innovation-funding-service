*** Settings ***
Resource    ../../resources/variables/GLOBAL_VARIABLES.robot
*** Variables ***
#Project: London underground – enhancements to existing stock and logistics
# GOL = Grant Offer Letter
${Gabtype_Id}       53
${Gabtype_Name}     Gabtype
${Kazio_Id}         54
${Kazio_Name}       Kazio
${Cogilith_Id}      55
${Cogilith_Name}    Cogilith
${PS_GOL_Competition_Id}  10
${PS_GOL_APPLICATION_NO}         80
${PS_GOL_APPLICATION_NUMBER}     80
${PS_GOL_APPLICATION_TITLE}      London underground – enhancements to existing stock and logistics
${PS_GOL_APPLICATION_HEADER}     ${PS_GOL_APPLICATION_TITLE}
${PS_GOL_APPLICATION_PROJECT}    7
${PS_GOL_APPLICATION_LEAD_ORGANISATION_ID}      ${Gabtype_ID}
${PS_GOL_APPLICATION_LEAD_ORGANISATION_NAME}    ${Gabtype_NAME}
${PS_GOL_APPLICATION_LEAD_PARTNER_EMAIL}        ${test_mailbox_one}+amy@gmail.com
${PS_GOL_APPLICATION_PM_EMAIL}                  ${test_mailbox_one}+amy@gmail.com
${PS_GOL_APPLICATION_FINANCE_CONTACT_EMAIL}     ${test_mailbox_one}+marian@gmail.com
${PS_GOL_APPLICATION_PARTNER_EMAIL}             ${test_mailbox_one}+karen@gmail.com
${PS_GOL_APPLICATION_ACADEMIC_EMAIL}            ${test_mailbox_one}+juan@gmail.com

#Project: High-speed rail and its effects on air quality
# MD = Mandatory Documents
${Ooba_Id}          50
${Ooba_Name}        Ooba
${Wordpedia_Id}     51
${Wordpedia_Name}   Wordpedia
${Jabbertype_Id}    52
${Jabbertype_Name}  Jabbertype
${PS_MD_Competition_Id}         10
${PS_MD_Competition_Name}       Rolling stock future developments
${PS_MD_APPLICATION_NO}         79
${PS_MD_APPLICATION_NUMBER}     79
${PS_MD_APPLICATION_TITLE}      High-speed rail and its effects on air quality
${PS_MD_APPLICATION_HEADER}     ${PS_MD_APPLICATION_TITLE}
${PS_MD_APPLICATION_PROJECT}    11
${PS_MD_APPLICATION_LEAD_ORGANISATION_ID}    ${Ooba_ID}
${PS_MD_APPLICATION_LEAD_ORGANISATION_NAME}  ${Ooba_Name}
${PS_MD_APPLICATION_LEAD_PARTNER_EMAIL}      ralph.young@ooba.example.com
${PS_MD_APPLICATION_PM_EMAIL}                ralph.young@ooba.example.com
${PS_MD_APPLICATION_PARTNER_EMAIL}           tina.taylor@wordpedia.example.com
${PS_MD_APPLICATION_ACADEMIC_EMAIL}          antonio.jenkins@jabbertype.example.com

#Project: Point control and automated monitoring
# SP = Spend Profile
${Katz_Id}         47
${Katz_Name}       Katz
${Meembee_Id}      48
${Meembee_Name}    Meembee
${Zooveo_Id}       49
${Zooveo_Name}     Zooveo
${PS_SP_Competition_Id}         10
${PS_SP_Competition_Name}       Rolling stock future developments
${PS_SP_APPLICATION_NO}         78
${PS_SP_APPLICATION_NUMBER}     78
${PS_SP_APPLICATION_TITLE}      Point control and automated monitoring
${PS_SP_APPLICATION_HEADER}     ${PS_SP_APPLICATION_TITLE}
${PS_SP_APPLICATION_PROJECT}    10
${PS_SP_APPLICATION_LEAD_ORGANISATION_ID}    ${Katz_Id}
${PS_SP_APPLICATION_LEAD_ORGANISATION_NAME}  ${Katz_Name}
${PS_SP_APPLICATION_LEAD_PARTNER_EMAIL}      theo.simpson@katz.example.com
${PS_SP_APPLICATION_PM_EMAIL}                phillip.ramos@katz.example.com
${PS_SP_APPLICATION_PARTNER_EMAIL}           kimberly.fowler@meembee.example.com
${PS_SP_APPLICATION_ACADEMIC_EMAIL}          craig.ortiz@zooveo.example.com

#Project: Grade crossing manufacture and supply
# BD = Bank Details
${Eadel_Id}       44
${Eadel_Name}     Eadel
${Bluezoom_Id}    45
${Bluezoom_Name}  Bluezoom
${Npath_Id}       46
${Npath_Name}     Npath
${PS_BD_Competition_Id}         10
${PS_BD_Competition_Name}       Rolling stock future developments
${PS_BD_APPLICATION_NO}         77
${PS_BD_APPLICATION_NUMBER}     77
${PS_BD_APPLICATION_TITLE}      Grade crossing manufacture and supply
${PS_BD_APPLICATION_HEADER}     ${PS_BD_APPLICATION_TITLE}
${PS_BD_APPLICATION_PROJECT}    9
${PS_BD_APPLICATION_LEAD_ORGANISATION_ID}    ${Eadel_Id}
${PS_BD_APPLICATION_LEAD_ORGANISATION_NAME}  ${Eadel_Name}
${PS_BD_APPLICATION_LEAD_PARTNER_EMAIL}      diane.scott@eadel.example.com
${PS_BD_APPLICATION_PM_EMAIL}                diane.scott@eadel.example.com
${PS_BD_APPLICATION_LEAD_FINANCE}            Diane Scott
${PS_BD_APPLICATION_LEAD_TELEPHONE}          49692921151
${PS_BD_APPLICATION_PARTNER_EMAIL}           ryan.welch@bluezoom.example.com
${PS_BD_APPLICATION_PARTNER_FINANCE}         Ryan Welch
${PS_BD_APPLICATION_ACADEMIC_EMAIL}          sara.armstrong@npath.example.com
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
${PS_EF_Competition_Id}         10
${PS_EF_Competition_Name}       Rolling stock future developments
${PS_EF_APPLICATION_NO}         76
${PS_EF_APPLICATION_NUMBER}     76
${PS_EF_APPLICATION_TITLE}      New materials for lighter stock
${PS_EF_APPLICATION_HEADER}     ${PS_EF_APPLICATION_TITLE}
${PS_EF_APPLICATION_PROJECT}    8
${PS_EF_APPLICATION_LEAD_ORGANISATION_ID}    ${Ntag_Id}
${PS_EF_APPLICATION_LEAD_ORGANISATION_NAME}  ${Ntag_Name}
${PS_EF_APPLICATION_LEAD_PARTNER_EMAIL}      steven.hicks@ntag.example.com
${PS_EF_APPLICATION_PM_EMAIL}                steven.hicks@ntag.example.com
${PS_EF_APPLICATION_PARTNER_EMAIL}           robert.perez@jetpulse.example.com
${PS_EF_APPLICATION_ACADEMIC_EMAIL}          bruce.perez@wikivu.example.com

#Old variables - to be refactored
${PROJECT_SETUP_COMPETITION}    8
${PROJECT_SETUP_COMPETITION_NAME}    New designs for a circular economy
${PROJECT_SETUP_APPLICATION_1}    72
${PROJECT_SETUP_APPLICATION_1_NUMBER}    72
${PROJECT_SETUP_APPLICATION_1_TITLE}    Magic material
${PROJECT_SETUP_APPLICATION_1_HEADER}    ${PROJECT_SETUP_APPLICATION_1_TITLE}
${PROJECT_SETUP_APPLICATION_1_PROJECT}    5
${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_ID}    ${EMPIRE_LTD_ID}
${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}    ${EMPIRE_LTD_NAME}
${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_COMPANY_NUMBER}    60674010
${PROJECT_SETUP_APPLICATION_1_LEAD_COMPANY_TURNOVER}    4560000
${PROJECT_SETUP_APPLICATION_1_LEAD_COMPANY_HEADCOUNT}    1230
${PROJECT_SETUP_APPLICATION_1_LEAD_PARTNER_EMAIL}  ${lead_applicant_credentials["email"]}
${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}    ${test_mailbox_one}+projectsetuppm@gmail.com
${PROJECT_SETUP_APPLICATION_1_PARTNER_ID}    39
${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}    Ludlow
${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_NUMBER}    5353232
${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_TURNOVER}    1230000
${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_HEADCOUNT}    4560
${PROJECT_SETUP_APPLICATION_1_PARTNER_EMAIL}            ${collaborator1_credentials["email"]}
${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}    EGGS
${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}   ${collaborator2_credentials["email"]}
${SUCCESSFUL_FUNDERS_PANEL_PROJECT_PAGE}    ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}
${SUCCESSFUL_FUNDERS_PANEL_PROJECT_PAGE_DETAILS}    ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/details
${project_in_setup_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}
${project_in_setup_details_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/details
${project_start_date_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/details/start-date
${project_address_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/details/project-address
${project_manager_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/details/start-date
${internal_spend_profile_approval}    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/spend-profile/approval
${internal_project_summary}    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status

${ELBOW_GREASE_PROJECT_ID}  4