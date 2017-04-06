*** Variables ***
${docker}         0
${smoke_test}     0
${BROWSER}        chrome
${SERVER_BASE}    ifs-local-dev
${PROTOCOL}       https://
${SERVER}         ${PROTOCOL}${SERVER_BASE}
${RUNNING_ON_DEV}    ${EMPTY}
${SAUCELABS_RUN}    1
${LOGIN_URL}      ${SERVER}/
${frontDoor}  ${server}/competition/search
${LOGGED_OUT_URL_FRAGMENT}    idp/profile/SAML2/Redirect/SSO
${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}    100,452
${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}    70,316
${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}    28,901
${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}    30%
${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}    495
${DEFAULT_ACADEMIC_CONTRIBUTION_TO_PROJECT}    0
${DEFAULT_ACADEMIC_GRANT_RATE_WITH_PERCENTAGE}    100%
${DEFAULT_ACADEMIC_FUNDING_SOUGHT_WITH_COMMAS}    ${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}
${DEFAULT_TOTAL_PROJECT_COST_WITH_COMMAS}   100,837
${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}    102,452
${DEFAULT_SUBCONTRACTING_COSTS_WITH_COMMAS_PLUS_2000}    47,000
${EMPIRE_LTD_ID}    22
${EMPIRE_LTD_NAME}    Empire Ltd
${OPEN_COMPETITION}    15
${OPEN_COMPETITION_NAME}    Predicting market trends programme
${OPEN_COMPETITION_NAME_2}    Home and industrial efficiency programme
${OPEN_COMPETITION_APPLICATION_1}    100
${OPEN_COMPETITION_APPLICATION_1_NUMBER}    100
${OPEN_COMPETITION_APPLICATION_2}    103
${OPEN_COMPETITION_APPLICATION_3}    102
${OPEN_COMPETITION_APPLICATION_3_TITLE}    Hydrology the dynamics of Earth's surface water
${OPEN_COMPETITION_APPLICATION_4}    101
${OPEN_COMPETITION_APPLICATION_5}    99
${OPEN_COMPETITION_APPLICATION_5_NUMBER}    99
${READY_TO_OPEN_COMPETITION}    6
${READY_TO_OPEN_COMPETITION_NAME}    Photonics for health
${COMP_SETUP_COMPETITION}    9
${FUNDERS_PANEL_COMPETITION}    5
${FUNDERS_PANEL_COMPETITION_NAME}    Internet of Things
${NOT_EDITABLE_COMPETITION}     8
${FUNDERS_PANEL_APPLICATION_1}    63
${FUNDERS_PANEL_APPLICATION_1_NUMBER}    63
${NOT_EDITABLE_COMPETITION}     7
${FUNDERS_PANEL_APPLICATION_1_TITLE}    Sensing & Control network using the lighting infrastructure
${FUNDERS_PANEL_APPLICATION_1_HEADER}    ${FUNDERS_PANEL_APPLICATION_1_TITLE}
${FUNDERS_PANEL_APPLICATION_2}    64
${FUNDERS_PANEL_APPLICATION_2_NUMBER}    64
${FUNDERS_PANEL_APPLICATION_2_TITLE}    Matter - Planning for Web
${FUNDERS_PANEL_APPLICATION_2_HEADER}    ${FUNDERS_PANEL_APPLICATION_2_TITLE}
${FUNDERS_PANEL_APPLICATION_1_PROJECT}    12
${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}    Empire Ltd
${IN_ASSESSMENT_COMPETITION}    4
${IN_ASSESSMENT_COMPETITION_NAME}    Sustainable living models for the future
${IN_ASSESSMENT_APPLICATION_1}    23
${IN_ASSESSMENT_APPLICATION_1_NUMBER}    23
${IN_ASSESSMENT_APPLICATION_1_TITLE}    3D-printed buildings
${IN_ASSESSMENT_APPLICATION_1_HEADER}    ${IN_ASSESSMENT_APPLICATION_1_TITLE}
${IN_ASSESSMENT_APPLICATION_3}    21
${IN_ASSESSMENT_APPLICATION_3_NUMBER}    21
${IN_ASSESSMENT_APPLICATION_3_LEAD_PARTNER_EMAIL}    shawn.ward@example.com
${IN_ASSESSMENT_APPLICATION_3_TITLE}    Intelligent Building
${IN_ASSESSMENT_APPLICATION_4_NUMBER}    19
${IN_ASSESSMENT_APPLICATION_4_LEAD_PARTNER_EMAIL}    ernest.austin@example.com
${IN_ASSESSMENT_APPLICATION_4_TITLE}    Park living
${IN_ASSESSMENT_APPLICATION_5_NUMBER}    20
${IN_ASSESSMENT_APPLICATION_5_LEAD_PARTNER_EMAIL}    paula.fuller@example.com
${IN_ASSESSMENT_APPLICATION_5_TITLE}    Products and Services Personalised
${INFORM_COMPETITION_NAME}    Integrated delivery programme - low carbon vehicles
${INFORM_APPLICATION_1_PROJECT}    4
${NON_IFS_COMPETITION_NAME}     Transforming big data
${STEVE_SMITH_ID}    55
${DASHBOARD_URL}    ${SERVER}/applicant/dashboard
${SUMMARY_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/summary
${APPLICATION_OVERVIEW_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}
${assessor_dashboard_url}    ${SERVER}/assessment/assessor/dashboard
${COMPETITION_DETAILS_URL}    ${SERVER}/competition/${OPEN_COMPETITION}/details/
${PUBLIC_DESCRIPTION_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/430
${TECHNICAL_APPROACH_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/question/436
${YOUR_FINANCES_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/section/187
${FINANCES_OVERVIEW_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/form/section/198
${ACCOUNT_CREATION_FORM_URL}    ${SERVER}/registration/register?organisationId=1
${ELIGIBILITY_INFO_URL}    ${SERVER}/competition/${OPEN_COMPETITION}/info/eligibility
${SPEED_BUMP_URL}    ${SERVER}/application/create-authenticated/${OPEN_COMPETITION}
${EDIT_PROFILE_URL}    ${SERVER}/profile/edit
${APPLICATION_TEAM_URL}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1}/team
${COMP_MANAGEMENT_APPLICATIONS_LIST}    ${SERVER}/management/competition/${OPEN_COMPETITION}/applications
${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}    ${SERVER}/management/competition/${OPEN_COMPETITION}/application/${OPEN_COMPETITION_APPLICATION_1}
${COMP_MANAGEMENT_COMP_SETUP}    ${SERVER}/management/competition/setup/${COMP_SETUP_COMPETITION}
${COMP_MANAGEMENT_UPDATE_COMP}    ${SERVER}/management/competition/setup/${OPEN_COMPETITION}
${COMP_MANAGEMENT_NOT_EDITABLE_COMP}    ${SERVER}/management/competition/setup/${NOT_EDITABLE_COMPETITION}
${COMP_MANAGEMENT_READY_TO_OPEN}    ${SERVER}/management/competition/setup/${READY_TO_OPEN_COMPETITION}
${COMP_MANAGEMENT_PROJECT_SETUP}    ${SERVER}/management/dashboard/project-setup
${CONFIRM_ORGANISATION_URL}    ${SERVER}/organisation/create/confirm-organisation
${403_error_message}    You do not have the necessary permissions for your request
${wrong_filetype_validation_error}    Please upload a file in .pdf format only
${too_large_pdf_validation_error}    the size of file or request being submitted is too large
${REGISTRATION_SUCCESS}    ${SERVER}/registration/success
${REGISTRATION_VERIFIED}    ${SERVER}/registration/verified
${VIRTUAL_DISPLAY}    ${EMPTY}
${POSTCODE_LOOKUP_IMPLEMENTED}    ${EMPTY}
${COMP_ADMINISTRATOR_DASHBOARD}    ${SERVER}/management/dashboard
${UNTITLED_APPLICATION_DASHBOARD_LINK}    Untitled application (start here)
${UNTITLED_APPLICATION_NAME}    Untitled application
${OPEN_COMPETITION_LINK}    ${OPEN_COMPETITION_NAME}
${OPEN_COMPETITION_LINK_2}    ${OPEN_COMPETITION_NAME_2}
${unsuccessful_login_message}    Your sign in was unsuccessful because of the following issues
${application_name}    Submit test application
${Competition_E2E}    Evolution of the global phosphorus cycle
${test_title}     test title
${SMALL_ORGANISATION_SIZE}     1
${MEDIUM_ORGANISATION_SIZE}    2
${LARGE_ORGANISATION_SIZE}     3
# File related variables
${UPLOAD_FOLDER}    uploaded_files
${DOWNLOAD_FOLDER}    download_files
${empty_field_warning_message}    This field cannot be left blank.
${valid_pdf}      testing.pdf
${too_large_pdf}    large.pdf
${text_file}      testing.txt
${excel_file}     testing.xlsx
${valid_pdf excerpt}    Adobe PDF is an ideal format for electronic document distribution
# Assessor variables
${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}    129
${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_2}    131
${IN_ASSESSMENT_APPLICATION_5_ASSESSMENT_2}    117
${Assessment_overview_9}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}
${Assessor_application_dashboard}    ${server}/assessment/assessor/dashboard/competition/${IN_ASSESSMENT_COMPETITION}
${Assessment_overview_11}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_2}
${assessment_declaration_url}    ${server}/assessment/profile/declaration
${assessment_skills_url}    ${server}/assessment/profile/skills
${assessment_details_url}    ${server}/assessment/profile/details
${Application_question_url_2}    ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}/question/117

# Database variables
${database_name}    ifs
${database_user}    root
${database_password}    password
${database_host}    ifs-database
${database_port}    3306
${CLOSED_COMPETITION_NAME}    Machine learning for transport infrastructure
${UPCOMING_COMPETITION_TO_ASSESS_NAME}    Home and industrial efficiency programme
${UPCOMING_COMPETITION_TO_ASSESS_ID}    11
${CURRENTLY_WAITING_UNTIL}      false
