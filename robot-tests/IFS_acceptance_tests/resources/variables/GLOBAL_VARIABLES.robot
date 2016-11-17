*** Variables ***
${docker}    0
${smoke_test}     0
${BROWSER}        chrome
${SERVER_BASE}    ifs-local-dev
${PROTOCOL}       https://
${SERVER}         ${PROTOCOL}${SERVER_BASE}
${RUNNING_ON_DEV}    ${EMPTY}
${LOGIN_URL}      ${SERVER}/
${LOGGED_OUT_URL_FRAGMENT}    idp/profile/SAML2/Redirect/SSO

${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}    100,837
${DEFAULT_INDUSTRIAL_OTHER_FUNDING_WITH_COMMAS}    1,234
${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}    70,586
${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}    29,017
${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITHOUT_COMMAS}    29017.04
${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}    30%
${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}    495
${DEFAULT_ACADEMIC_OTHER_FUNDING_WITH_COMMAS}    0
${DEFAULT_ACADEMIC_CONTRIBUTION_TO_PROJECT}    0
${DEFAULT_ACADEMIC_GRANT_RATE_WITH_PERCENTAGE}    100%
${DEFAULT_ACADEMIC_FUNDING_SOUGHT_WITH_COMMAS}    ${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}
${DEFAULT_TOTAL_PROJECT_COST_WITHOUT_COMMAS}    100836.81
${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}   102,837
${DEFAULT_SUBCONTRACTING_COSTS_WITH_COMMAS_PLUS_2000}    47,000

${EMPIRE_LTD_ID}    22
${EMPIRE_LTD_NAME}    Empire Ltd

${OPEN_COMPETITION}    1
${OPEN_COMPETITION_APPLICATION_1}    9
${OPEN_COMPETITION_APPLICATION_1_NUMBER}    00000009
${OPEN_COMPETITION_APPLICATION_1_TITLE}    A novel solution to an old problem
${OPEN_COMPETITION_APPLICATION_1_HEADER}    ${OPEN_COMPETITION_APPLICATION_1_NUMBER}: ${OPEN_COMPETITION_APPLICATION_1_TITLE}
${OPEN_COMPETITION_APPLICATION_2}    12
${OPEN_COMPETITION_APPLICATION_3}    11
${OPEN_COMPETITION_APPLICATION_3_NUMBER}    00000011
${OPEN_COMPETITION_APPLICATION_3_TITLE}    Mobile Phone Data for Logistics Analytics
${OPEN_COMPETITION_APPLICATION_3_HEADER}    ${OPEN_COMPETITION_APPLICATION_3_NUMBER}: ${OPEN_COMPETITION_APPLICATION_3_TITLE}
${OPEN_COMPETITION_APPLICATION_4}    10
${OPEN_COMPETITION_APPLICATION_5}    8
${OPEN_COMPETITION_APPLICATION_5_NUMBER}    00000008

${READY_TO_OPEN_COMPETITION}    6

${COMP_SETUP_COMPETITION}    9

${FUNDERS_PANEL_COMPETITION}    5
${FUNDERS_PANEL_APPLICATION_1}    24
${FUNDERS_PANEL_APPLICATION_1_NUMBER}    00000024
${FUNDERS_PANEL_APPLICATION_1_TITLE}    Cheese is good
${FUNDERS_PANEL_APPLICATION_1_HEADER}    ${FUNDERS_PANEL_APPLICATION_1_NUMBER}: ${FUNDERS_PANEL_APPLICATION_1_TITLE}
${FUNDERS_PANEL_APPLICATION_2}    25
${FUNDERS_PANEL_APPLICATION_2_NUMBER}    00000025
${FUNDERS_PANEL_APPLICATION_2_TITLE}    Cheese is great
${FUNDERS_PANEL_APPLICATION_2_HEADER}    ${FUNDERS_PANEL_APPLICATION_2_NUMBER}: ${FUNDERS_PANEL_APPLICATION_2_TITLE}
${FUNDERS_PANEL_APPLICATION_1_PROJECT}    6
${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION}    ${EMPIRE_LTD_ID}
${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}    Empire Ltd

${NEW_COMP_SETUP_COMPETITION}    10

${PROJECT_SETUP_COMPETITION}    8
${PROJECT_SETUP_APPLICATION_1}    32
${PROJECT_SETUP_APPLICATION_1_NUMBER}    00000032
${PROJECT_SETUP_APPLICATION_1_TITLE}    best riffs
${PROJECT_SETUP_APPLICATION_1_HEADER}    ${PROJECT_SETUP_APPLICATION_1_NUMBER}: ${PROJECT_SETUP_APPLICATION_1_TITLE}
${PROJECT_SETUP_APPLICATION_1_PROJECT}    3
${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_ID}    ${EMPIRE_LTD_ID}
${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}    ${EMPIRE_LTD_NAME}
${PROJECT_SETUP_APPLICATION_1_LEAD_PARTNER_EMAIL}    steve.smith@empire.com
${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}    worth.email.test+changepsw@gmail.com
${PROJECT_SETUP_APPLICATION_1_PARTNER_EMAIL}    jessica.doe@ludlow.co.uk
${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}    pete.tom@egg.com

${IN_ASSESSMENT_COMPETITION}    4
${IN_ASSESSMENT_APPLICATION_1}    22
${IN_ASSESSMENT_APPLICATION_1_NUMBER}    00000022
${IN_ASSESSMENT_APPLICATION_1_TITLE}    Spherical interactions in hyperdimensional space
${IN_ASSESSMENT_APPLICATION_1_HEADER}    ${IN_ASSESSMENT_APPLICATION_1_NUMBER}: ${IN_ASSESSMENT_APPLICATION_1_TITLE}
${IN_ASSESSMENT_APPLICATION_2}    16
${IN_ASSESSMENT_APPLICATION_3}    20
${IN_ASSESSMENT_APPLICATION_3_NUMBER}    00000020

${INFORM_COMPETITION}    7
${INFORM_APPLICATION_1_PROJECT}    4

${STEVE_SMITH_ID}    55
${JESSICA_DOE_ID}    56
${PETE_TOM_ID}    57
${TEST_TWENTY_ID}    44

${DASHBOARD_URL}    ${SERVER}/applicant/dashboard
${SUMMARY_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/summary
${QUESTION11_URL}    ${SERVER}/application-form/${OPEN_COMPETITION_APPLICATION_1}/section/1/#question-11
${APPLICATION_OVERVIEW_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}
${APPLICATION_OVERVIEW_URL_APPLICATION_2}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_2}
${APPLICATION_SUBMITTED_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/submit
${APPLICATION_2_SUMMARY_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_2}/summary
${ECONOMIC_BENEFIT_URL_APPLICATION_2}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_2}/form/question/4
${APPLICATION_3_OVERVIEW_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_3}
${ECONOMIC_BENEFIT_URL_APPLICATION_3}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_3}/form/question/4
${applicant_dashboard_url}    ${SERVER}/applicant/dashboard
${assessor_dashboard_url}    ${SERVER}/assessment/assessor/dashboard
${COMPETITION_DETAILS_URL}    ${SERVER}/competition/${OPEN_COMPETITION}/details/
${LOG_OUT}        ${LOGIN_URL}/Logout
${APPLICATION_QUESTIONS_SECTION_URL}    ${SERVER}/application-form/${OPEN_COMPETITION_APPLICATION_1}/section/2/
${SEARCH_COMPANYHOUSE_URL}    ${SERVER}/organisation/create/find-business
${APPLICATION_DETAILS_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/9
${PROJECT_SUMMARY_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/11
${PROJECT_SUMMARY_EDIT_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/edit/11
${PUBLIC_DESCRIPTION_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/12
${SCOPE_URL}      ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/13
${BUSINESS_OPPORTUNITY_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/1
${POTENTIAL_MARKET_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/2
${PROJECT_EXPLOITATION_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/3
${ECONOMIC_BENEFIT_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/4
${TECHNICAL_APPROACH_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/5
${INNOVATION_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/6
${RISKS_URL}      ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/7
${PROJECT_TEAM_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/8
${FUNDING_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/15
${ADDING_VALUE_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/16
${YOUR_FINANCES_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/section/7
${YOUR_FINANCES_URL_APPLICATION_2}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_2}/form/section/7
${FINANCES_OVERVIEW_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/section/8
${FINANCES_OVERVIEW_URL_APPLICATION_2}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_2}/form/section/8
${ACCOUNT_CREATION_FORM_URL}    ${SERVER}/registration/register?organisationId=1
${ELIGIBILITY_INFO_URL}    ${SERVER}/competition/1/info/eligibility
${CHECK_ELIGIBILITY}    ${SERVER}/application/create/check-eligibility/1
${SPEED_BUMP_URL}    ${SERVER}/application/create-authenticated/1
${YOUR_DETAILS}    ${SERVER}/application/create/your-details
${POSTCODE_LOOKUP_URL}    ${SERVER}/organisation/create/selected-organisation/05063042#
${EDIT_PROFILE_URL}    ${SERVER}/profile/edit
${APPLICATION_TEAM_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/contributors
${MANAGE_CONTRIBUTORS_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/contributors/invite
${COMP_MANAGEMENT_APPLICATIONS_LIST}    ${SERVER}/management/competition/${OPEN_COMPETITION}
${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}    ${SERVER}/management/competition/${OPEN_COMPETITION}/application/${OPEN_COMPETITION_APPLICATION_1}
${COMP_MANAGEMENT_COMP_SETUP}    ${SERVER}/management/competition/setup/8
${COMP_MANAGEMENT_PROJECT_SETUP}    ${SERVER}/management/dashboard/projectSetup
${NEWLY_CREATED_APPLICATION_YOUR_FINANCES_URL}    ${SERVER}/application/${FUNDERS_PANEL_APPLICATION_2}/form/section/7
${CONFIRM_ORGANISATION_URL}    ${SERVER}/organisation/create/confirm-organisation
${SUCCESSFUL_FUNDERS_PANEL_PROJECT_PAGE}    ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}
${SUCCESSFUL_FUNDERS_PANEL_PROJECT_PAGE_DETAILS}    ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/details
${project_in_setup_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}
${project_in_setup_details_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/details
${project_start_date_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/details/start-date
${project_address_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/details/project-address
${project_manager_page}    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/details/start-date
${internal_spend_profile_approval}    ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/spend-profile/approval
${internal_project_summary}    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status
${404_error_message}    Page Not Found
${403_error_message}    You do not have the necessary permissions for your request
${wrong_filetype_validation_error}    Please upload a file in .pdf format only
${too_large_pdf_validation_error}    the size of file or request being submitted is too large
${REGISTRATION_SUCCESS}    ${SERVER}/registration/success
${verify_link_1}    ${SERVER}/registration/verify-email/4a5bc71c9f3a2bd50fada434d888579aec0bd53fe7b3ca3fc650a739d1ad5b1a110614708d1fa083
${verify_link_2}    ${SERVER}/registration/verify-email/5f415b7ec9e9cc497996e251294b1d6bccfebba8dfc708d87b52f1420c19507ab24683bd7e8f49a0
${verify_link_3}    ${SERVER}/registration/verify-email/8223991f065abb7ed909c8c7c772fbdd24c966d246abd63c2ff7eeba9add3bafe42b067b602f761b
${REGISTRATION_VERIFIED}    ${SERVER}/registration/verified
${VIRTUAL_DISPLAY}    ${EMPTY}
${POSTCODE_LOOKUP_IMPLEMENTED}    ${EMPTY}
${LOCAL_MAIL_SENDING_IMPLEMENTED}    'YES'
${COMP_ADMINISTRATOR_DASHBOARD}    ${SERVER}/management/dashboard
${COMP_ADMINISTRATOR_OPEN}    ${SERVER}/management/competition/${OPEN_COMPETITION}
${COMP_ADMINISTRATOR_IN_ASSESSMENT}    ${SERVER}/management/competition/${IN_ASSESSMENT_COMPETITION}
${OPEN_COMPETITION_LINK}    Connected digital additive manufacturing
${Providing_Sustainable_Childcare_Application_Overview}    ${server}/management/competition/${OPEN_COMPETITION}/application/${OPEN_COMPETITION_APPLICATION_2}
${unsuccessful_login_message}    Your sign in was unsuccessful because of the following issue(s)
${application_name}    Submit test application
${test_title}     test title

# File related variables
${UPLOAD_FOLDER}                  uploaded_files
${DOWNLOAD_FOLDER}                ../download_files
${empty_field_warning_message}    This field cannot be left blank
${valid_pdf}            testing.pdf
${too_large_pdf}        large.pdf
${text_file}            testing.txt
${valid_pdf excerpt}    Adobe PDF is an ideal format for electronic document distribution

# Assessor variables
${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}    25
${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_2}    30
${IN_ASSESSMENT_APPLICATION_5_ASSESSMENT_1}    26
${IN_ASSESSMENT_APPLICATION_5_ASSESSMENT_2}    29
${IN_ASSESSMENT_COMPETITION_PROJECT_DETAILS_SECTION}    225
${IN_ASSESSMENT_COMPETITION_APPLICATION_QUESTIONS_SECTION}    226
${IN_ASSESSMENT_COMPETITION_FINANCES_SECTION}    227
${IN_ASSESSMENT_COMPETITION_SCOPE_ASSESSOR_FORM_INPUT}    231
${IN_ASSESSMENT_COMPETITION_BUSINESS_OPPORTUNITY_ASSESSOR_FORM_INPUT}    233
${IN_ASSESSMENT_COMPETITION_ECONOMIC_BENEFIT_ASSESSOR_FORM_INPUT}    239

${Assessment_overview_9}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}
${Assessment_summary_complete_9}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}/summary
${Application_question_url}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}/question/375
${Application_question_168}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}/question/376
${Application_question_169}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}/question/377
${Application_question_170}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}/question/378
${Finance_summar_9_url}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}/finances
${Assessor_competition_dashboard}    ${server}/assessment/assessor/dashboard
${Assessor_application_dashboard}    ${server}/assessment/assessor/dashboard/competition/${IN_ASSESSMENT_COMPETITION}
${Assessment_overview_11}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_2}
${Assessment_summary_Pending_12}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_5_ASSESSMENT_1}/summary
${Assessment_summary_open_11}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_2}/summary
${assessment_skills}    ${server}/assessment/profile/declaration
${Assessment_overview_10}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_5_ASSESSMENT_2}

# Database variables
${database_name}    ifs
${database_user}    root
${database_password}    password
${database_host}    ifs-database
${database_port}    3306

