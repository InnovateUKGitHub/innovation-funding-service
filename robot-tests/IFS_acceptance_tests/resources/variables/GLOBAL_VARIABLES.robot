*** Settings ***
Variables  ../../libs/Initialisation.py

*** Variables ***
${docker}                                                0
${BROWSER}                                               chrome
${SERVER_BASE}                                           ifs.local-dev
${PROTOCOL}                                              https://
${SERVER}                                                ${PROTOCOL}${SERVER_BASE}
${SAUCELABS_RUN}                                         1
${LOGIN_URL}                                             ${SERVER}/
${frontDoor}                                             ${server}/competition/search
${LOGGED_OUT_URL_FRAGMENT}                               idp/profile/SAML2/Redirect/SSO
${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}                  200,903
${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}            140,632
${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}         57,803
${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}         30.00%
${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}                    990
${DEFAULT_ACADEMIC_CONTRIBUTION_TO_PROJECT}              0
${DEFAULT_ACADEMIC_GRANT_RATE_WITH_PERCENTAGE}           0%
${DEFAULT_ACADEMIC_FUNDING_SOUGHT_WITH_COMMAS}           ${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}
${DEFAULT_TOTAL_PROJECT_COST_WITH_COMMAS}                200,903
${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}        202,903
${DEFAULT_SUBCONTRACTING_COSTS_WITH_COMMAS_PLUS_2000}    92,000

${manageExternalUsers}                                   ${server}/management/admin/external/users

# Competitions and Applications Variables
${openCompetitionRTO_name}                                  Horizon Europe Guarantee Pre Registration Competition with EOI Decision
${openCompetitionRTO}                                       ${competition_ids['${openCompetitionRTO_name}']}
${openCompetitionRTOCloseDate}                              ${getSimpleMilestoneDate(${openCompetitionRTO}, "SUBMISSION_DATE")}
${openCompetitionRTOApplication1Name}                       Hydrology the dynamics of Earth\'s surface water
${openCompetitionRTOApplication1Id}                         ${application_ids["${openCompetitionRTOApplication1Name}"]}
${openCompetitionManagementRTO}                             ${SERVER}/management/competition/${openCompetitionRTO}
${applicationsForRTOComp}                                   ${SERVER}/management/competition/${openCompetitionRTO}/applications
${openCompetitionBusinessRTO_name}                          Home and industrial efficiency programme
${openCompetitionBusinessRTO}                               ${competition_ids['${openCompetitionBusinessRTO_name}']}
${openCompetitionBusinessRTO_overview}                      ${SERVER}/competition/${openCompetitionBusinessRTO}/overview/
${openCompetitionBusinessRTOCloseDate}                      ${getSimpleMilestoneDate(${openCompetitionBusinessRTO}, "SUBMISSION_DATE")}
${openCompetitionBusinessRTOOpenDate}                       ${getPrettyMilestoneDate(${openCompetitionBusinessRTO}, "OPEN_DATE")}
${openCompetitionPublicSector_name}                         Photonics for Public
${openCompetitionPublicSector}                              ${competition_ids['${openCompetitionPublicSector_name}']}
${openCompetitionResearch_name}                             Photonics for Research
${openCompetitionResearch}                                  ${competition_ids['${openCompetitionResearch_name}']}
${openGenericCompetition}                                   Generic innovation
${openGenericCompetitionId}                                 ${competition_ids['${openGenericCompetition}']}
${ktpCompetitionName}                                       KTP Competition
${ukLeadInternationalCompetition}                           UK based Lead International Competition
${internationalLeadInternationalCompetition}                International Lead International Competition
${openCompetitionPerformance_name}                          Performance testing competition
${openCompetitionPerformance}                               ${competition_ids['${openCompetitionPerformance_name}']}
${projectEligibilityLink}                                   Project eligibility
${fundingEligibilityTitle}                                  Funding eligibility
${organisationalEligibilityTitle}                           Organisational eligibility
${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}     Aerospace technology investment sector
${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS}          ${competition_ids['${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}']}
${hestaCompetitionName}                                     Hesta competition
# Using double quotes below, cause there's an apostrophe in the title
${OPEN_COMPETITION_APPLICATION_NAME}                            Climate science the history of Greenland\'s ice
${OPEN_COMPETITION_APPLICATION_1_NUMBER}                        ${application_ids["${OPEN_COMPETITION_APPLICATION_NAME}"]}
${OPEN_COMPETITION_APPLICATION_2_NAME}                          Planetary science Pluto\'s telltale heart
${OPEN_COMPETITION_APPLICATION_2_NUMBER}                        ${application_ids["${OPEN_COMPETITION_APPLICATION_2_NAME}"]}
${OPEN_COMPETITION_APPLICATION_3_NAME}                          Hydrology the dynamics of Earth\'s surface water
${OPEN_COMPETITION_APPLICATION_3_NUMBER}                        ${application_ids["${OPEN_COMPETITION_APPLICATION_3_NAME}"]}
${OPEN_COMPETITION_APPLICATION_4_NAME}                          Greenland was nearly ice-free for extended periods during the Pleistocene
${OPEN_COMPETITION_APPLICATION_4_NUMBER}                        ${application_ids['${OPEN_COMPETITION_APPLICATION_4_NAME}']}
${OPEN_COMPETITION_APPLICATION_5_NAME}                          Evolution of the global phosphorus cycle
${OPEN_COMPETITION_APPLICATION_5_NUMBER}                        ${application_ids['${OPEN_COMPETITION_APPLICATION_5_NAME}']}
${OPEN_COMPETITION_APPLICATION_6_NAME}                          Safeguarding pollinators and their values to human well-being
${OPEN_COMPETITION_APPLICATION_6_NUMBER}                        ${application_ids['${OPEN_COMPETITION_APPLICATION_6_NAME}']}
#${CLOSED_COMPETITION_APPLICATION_NAME}                          A new innovative solution
#${CLOSED_COMPETITION_APPLICATION_NAME_NUMBER}                   ${application_ids['${CLOSED_COMPETITION_APPLICATION_NAME}']}
${createApplicationOpenCompetition}                             Home and industrial efficiency programme
${createApplicationOpenCompetitionId}                           ${competition_ids['${createApplicationOpenCompetition}']}
${createApplicationOpenInternationalCompetition}                International Competition
${createApplicationOpenCompetitionApplication1Name}             Networking home IOT devices
${createApplicationOpenCompetitionApplication1Number}           ${application_ids['${createApplicationOpenCompetitionApplication1Name}']}
${createApplicationOpenCompetitionOpenDate}                     ${getPrettyMilestoneDate(${createApplicationOpenCompetitionId}, "OPEN_DATE")}
${createApplicationOpenCompetitionCloseDate}                    ${getPrettyMilestoneDate(${createApplicationOpenCompetitionId}, "SUBMISSION_DATE")}
${createApplicationOpenCompetitionAssessorAcceptsDayMonth}      ${getPrettyMilestoneDayMonth(${createApplicationOpenCompetitionId}, "ASSESSOR_ACCEPTS")}
${createApplicationOpenCompetitionAssessorDeadlineDayMonth}     ${getPrettyMilestoneDayMonth(${createApplicationOpenCompetitionId}, "ASSESSOR_DEADLINE")}
${READY_TO_OPEN_COMPETITION_NAME}                               Photonics for health
${READY_TO_OPEN_COMPETITION}                                    ${competition_ids['${READY_TO_OPEN_COMPETITION_NAME}']}
${READY_TO_OPEN_COMPETITION_OPEN_DATE_DB}                       ${getMilestoneDateTimeDb(${READY_TO_OPEN_COMPETITION}, "OPEN_DATE")}
${READY_TO_OPEN_COMPETITION_CLOSE_DATE_DB}                      ${getMilestoneDateTimeDb(${READY_TO_OPEN_COMPETITION}, "SUBMISSION_DATE")}
${READY_TO_OPEN_COMPETITION_OPEN_DATE_DATE_LONG}                ${getPrettyLongMilestoneDate(${READY_TO_OPEN_COMPETITION}, "OPEN_DATE")}
${READY_TO_OPEN_COMPETITION_NAME2}                              Reusability of waste material rocketry components
${READY_TO_OPEN_COMPETITION2}                                   ${competition_ids['${READY_TO_OPEN_COMPETITION_NAME2}']}
${READY_TO_OPEN_COMPETITION_OPEN_DATE_DB2}                      ${getMilestoneDateTimeDb(${READY_TO_OPEN_COMPETITION2}, "OPEN_DATE")}
${READY_TO_OPEN_COMPETITION_CLOSE_DATE_DB2}                     ${getMilestoneDateTimeDb(${READY_TO_OPEN_COMPETITION2}, "SUBMISSION_DATE")}
${READY_TO_OPEN_COMPETITION_OPEN_DATE_DATE_LONG2}               ${getPrettyLongMilestoneDate(${READY_TO_OPEN_COMPETITION2}, "OPEN_DATE")}
${NOT_EDITABLE_COMPETITION_NAME}                                Integrated delivery programme - low carbon vehicles
${NOT_EDITABLE_COMPETITION}                                     ${competition_ids['${NOT_EDITABLE_COMPETITION_NAME}']}
${FUNDERS_PANEL_COMPETITION_NAME}                               Internet of Things
${FUNDERS_PANEL_COMPETITION_NUMBER}                             ${competition_ids['${FUNDERS_PANEL_COMPETITION_NAME}']}
${FUNDERS_PANEL_APPLICATION_1_TITLE}                            Sensing & Control network using the lighting infrastructure
${FUNDERS_PANEL_APPLICATION_1_NUMBER}                           ${application_ids['${FUNDERS_PANEL_APPLICATION_1_TITLE}']}
${FUNDERS_PANEL_APPLICATION_2_TITLE}                            Matter - Planning for Web
${FUNDERS_PANEL_APPLICATION_2_NUMBER}                           ${application_ids['${FUNDERS_PANEL_APPLICATION_2_TITLE}']}
${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}           ${EMPIRE_LTD_NAME}
${IN_ASSESSMENT_COMPETITION_NAME}                               Sustainable living models for the future
${IN_ASSESSMENT_COMPETITION}                                    ${competition_ids['${IN_ASSESSMENT_COMPETITION_NAME}']}
${IN_ASSESSMENT_COMPETITION_ASSESSOR_DEADLINE_DB}               ${getMilestoneDateTimeDb(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_DEADLINE")}
${IN_ASSESSMENT_COMPETITION_ASSESSOR_ACCEPTS_TIME_DATE_LONG}    ${getPrettyLongMilestoneTimeDate(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_ACCEPTS")}
${IN_ASSESSMENT_COMPETITION_ASSESSOR_DEADLINE_TIME_DATE_LONG}   ${getPrettyLongMilestoneTimeDate(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_DEADLINE")}
${IN_ASSESSMENT_COMPETITION_ASSESSOR_DEADLINE_DATE_LONG}        ${getPrettyLongMilestoneDate(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_DEADLINE")}
${IN_ASSESSMENT_COMPETITION_ASSESSOR_ACCEPTS_PRETTY_DATE}       ${getPrettyMilestoneDate(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_ACCEPTS")}
${IN_ASSESSMENT_COMPETITION_ASSESSOR_ACCEPTS_DAY_MONTH_DATE}    ${getPrettyMilestoneDayMth(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_ACCEPTS")}
${IN_ASSESSMENT_COMPETITION_ASSESSOR_ACCEPTS_DAY_MONTH}         ${getPrettyMilestoneDayMth(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_ACCEPTS")}
${IN_ASSESSMENT_COMPETITION_ASSESSOR_DEADLINE_PRETTY_DATE}      ${getPrettyMilestoneDayMth(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_DEADLINE")}
${IN_ASSESSMENT_COMPETITION_ASSESSOR_DEADLINE_DAY_MONTH_YEAR}   ${getPrettyMilestoneDayMthYear(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_DEADLINE")}
${ktpAssessmentCompetitionName}                                 KTP assessment
${ktpAssessmentCompetitionId}                                   ${competition_ids['${ktpAssessmentCompetitionName}']}
${ktpAssessmentCompetitionReleaseFeedbackDayMonthYear}          ${getPrettyMilestoneDate(${ktpAssessmentCompetitionId}, "RELEASE_FEEDBACK")}
${ktpDetailsFinanceCompetitionName}                             KTP assessment Detailed Finances
${ktpDetailsFinanceCompetitionId}                               ${competition_ids['${ktpDetailsFinanceCompetitionName}']}
${ktpDetailsFinanceCompetitionReleaseFeedbackDayMonthYear}      ${getPrettyMilestoneDate(${ktpDetailsFinanceCompetitionId}, "RELEASE_FEEDBACK")}
${IN_ASSESSMENT_APPLICATION_1_TITLE}                            3D-printed buildings
${IN_ASSESSMENT_APPLICATION_1_NUMBER}                           ${application_ids['${IN_ASSESSMENT_APPLICATION_1_TITLE}']}
${IN_ASSESSMENT_APPLICATION_3_TITLE}                            Intelligent Building
${IN_ASSESSMENT_APPLICATION_3_NUMBER}                           ${application_ids['${IN_ASSESSMENT_APPLICATION_3_TITLE}']}
${IN_ASSESSMENT_APPLICATION_3_LEAD_PARTNER_EMAIL}               shawn.ward@example.com
${IN_ASSESSMENT_APPLICATION_4_TITLE}                            Park living
${IN_ASSESSMENT_APPLICATION_4_NUMBER}                           ${application_ids['${IN_ASSESSMENT_APPLICATION_4_TITLE}']}
${IN_ASSESSMENT_APPLICATION_4_LEAD_PARTNER_EMAIL}               ernest.austin@example.com
${IN_ASSESSMENT_APPLICATION_5_TITLE}                            Products and Services Personalised
${IN_ASSESSMENT_APPLICATION_5_NUMBER}                           ${application_ids['${IN_ASSESSMENT_APPLICATION_5_TITLE}']}
${IN_ASSESSMENT_APPLICATION_5_LEAD_PARTNER_EMAIL}               paula.fuller@example.com
${INFORM_COMPETITION_NAME}                                      Integrated delivery programme - low carbon vehicles
${INFORM_COMPETITION_NAME_1}                                    Climate control solution
${INFORM_COMPETITION_NAME_1_NUMBER}                             ${application_ids['${INFORM_COMPETITION_NAME_1}']}
${INFORM_COMPETITION_NAME_2}                                    High Performance Gasoline Stratified
${INFORM_COMPETITION_NAME_2_NUMBER}                             ${application_ids['${INFORM_COMPETITION_NAME_2}']}
${WITHDRAWN_PROJECT_COMPETITION_NAME}                           Integrated delivery programme - solar vehicles
${WITHDRAWN_PROJECT_COMPETITION}                                ${competition_ids['${WITHDRAWN_PROJECT_COMPETITION_NAME}']}
${WITHDRAWN_PROJECT_COMPETITION_NAME_1}                         Low-friction wheel coatings
${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}                  ${application_ids['${WITHDRAWN_PROJECT_COMPETITION_NAME_1}']}
${INELIGIBLE_PROJECT_COMPETITION_NAME_2}                        SPAM: Solar power aggregation meshes
${INELIGIBLE_PROJECT_COMPETITION_NAME_2_NUMBER}                 ${application_ids['${INELIGIBLE_PROJECT_COMPETITION_NAME_2}']}
${UNSUCCESSFUL_PROJECT_COMPETITION_NAME_3}                      Electricity harvesting from rough terrain driving
${UNSUCCESSFUL_PROJECT_COMPETITION_NAME_3_NUMBER}               ${application_ids['${UNSUCCESSFUL_PROJECT_COMPETITION_NAME_3}']}
${MARKOFFLINE_COMPETITION_NAME}                                 Biosciences round three: plastic recovery in the industrial sector
${MARKOFFLINE_COMPETITION}                                      ${competition_ids['${MARKOFFLINE_COMPETITION_NAME}']}
${MARKOFFLINE_APPLICATION_1_TITLE}                              Smart skips for plastic storage and retrieval
${MARKOFFLINE_APPLICATION_1_NUMBER}                             ${application_ids['${MARKOFFLINE_APPLICATION_1_TITLE}']}
${NON_IFS_COMPETITION_NAME}                                     Transforming big data
${NON_IFS_COMPETITION}                                          ${competition_ids['${NON_IFS_COMPETITION_NAME}']}
${DASHBOARD_SELECTION_PAGE_TITLE}                               Dashboard
${APPLICANT_DASHBOARD_URL}                                      ${SERVER}/applicant/dashboard
${APPLICANT_ADDITIONAL_FUNDING_QUERIES_URL}                     ${SERVER}/covid-19/questionnaire
${APPLICANT_DASHBOARD_TITLE}                                    Applications
${SUMMARY_URL}                                                  ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1_NUMBER}/summary
${APPLICATION_OVERVIEW_URL}                                     ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1_NUMBER}
${ASSESSOR_DASHBOARD_URL}                                       ${SERVER}/assessment/assessor/dashboard
${ASSESSOR_DASHBOARD_TITLE}                                     Assessments
${ACCOUNT_CREATION_FORM_URL}                                    ${SERVER}/registration/register?organisationId=1
${EDIT_PROFILE_URL}                                             ${SERVER}/profile/edit
${APPLICATION_TEAM_URL}                                         ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_1_NUMBER}/team
${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}                       ${SERVER}/management/competition/${openCompetitionRTO}/application/${OPEN_COMPETITION_APPLICATION_1_NUMBER}
${COMP_MANAGEMENT_UPDATE_COMP}                                  ${SERVER}/management/competition/setup/${openCompetitionRTO}
${COMP_MANAGEMENT_NOT_EDITABLE_COMP}                            ${SERVER}/management/competition/setup/${NOT_EDITABLE_COMPETITION}
${COMP_MANAGEMENT_READY_TO_OPEN}                                ${SERVER}/management/competition/setup/${READY_TO_OPEN_COMPETITION}
${COMP_MANAGEMENT_PROJECT_SETUP}                                ${SERVER}/management/dashboard/project-setup
${CONFIRM_ORGANISATION_URL}                                     ${SERVER}/organisation/create/confirm-organisation
${REGISTRATION_SUCCESS}                                         ${SERVER}/registration/success
${REGISTRATION_VERIFIED}                                        ${SERVER}/registration/verified
${VIRTUAL_DISPLAY}                                              ${EMPTY}
${POSTCODE_LOOKUP_IMPLEMENTED}                                  ${EMPTY}
${COMP_ADMINISTRATOR_DASHBOARD}                                 ${SERVER}/management/dashboard
${UNTITLED_APPLICATION_DASHBOARD_LINK}                          Untitled application (start here)
${UNTITLED_APPLICATION_NAME}                                    Untitled application

${application_bus_name}         Submit business test application
${application_rto_name}         Submit rto test application
${Competition_E2E}              Evolution of the global phosphorus cycle
${test_title}                   test title
${SMALL_ORGANISATION_SIZE}      SMALL
${MEDIUM_ORGANISATION_SIZE}     MEDIUM
${LARGE_ORGANISATION_SIZE}      LARGE

# File related variables
${UPLOAD_FOLDER}              uploaded_files
${DOWNLOAD_FOLDER}            download_files
${valid_pdf}                  testing.pdf
${webtest_pdf}                test.pdf
${gol_pdf}                    GOL_template.pdf
${contract_pdf}               Contract.pdf
${valid_xls}                  testing.xls
${valid_odt}                  testing.odt
${valid_doc}                  testing.doc
${valid_docx}                 testing.docx
${valid_jpeg}                 testing.jpeg
${5mb_pdf}                    testing_5MB.pdf
${too_large_pdf}              large.pdf
${text_file}                  testing.txt
${excel_file}                 testing.xlsx
${valid_pdf excerpt}          Adobe PDF is an ideal format for electronic document distribution
${ods_file}                   file_example_ODS.ods
${assessment-as-service}      assessment-as-service.csv
${32MB_large_pdf}             32mb_file.pdf

#shell scripts folder
${shellScriptFolder}          shell_scripts
${cloudEDICurl}               cloudEDICurl.sh
${localEDICurl}               localEDICurl.sh
${cloudLoanCurl}              cloudLoanCurl.sh
${localLoanCurl}              localLoanCurl.sh

# Assessor variables
${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}     ${assessment_ids["${IN_ASSESSMENT_APPLICATION_4_TITLE}"]["${assessor_credentials["email"]}"]}
${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_2}     ${assessment_ids["${IN_ASSESSMENT_APPLICATION_4_TITLE}"]["${assessor2_credentials["email"]}"]}
${IN_ASSESSMENT_APPLICATION_5_ASSESSMENT_2}     ${assessment_ids["${IN_ASSESSMENT_APPLICATION_5_TITLE}"]["${assessor2_credentials["email"]}"]}
${WITHDRAWN_ASSESSMENT}                         ${assessment_ids["Plastic reprocessing with zero waste"]["${assessor2_credentials["email"]}"]}
${Assessment_overview_9}                        ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}
${Assessor_application_dashboard}               ${server}/assessment/assessor/dashboard/competition/${IN_ASSESSMENT_COMPETITION}
${Assessment_overview_11}                       ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_2}
${assessment_declaration_url}                   ${assessment_details_url}/declaration
${assessment_skills_url}                        ${assessment_details_url}/skills
${assessment_details_url}                       ${server}/assessment/profile/details
${Application_question_url_2}                   ${server}/assessment/${IN_ASSESSMENT_APPLICATION_4_ASSESSMENT_1}/question/117

# Admin user variables
${USER_MGMT_URL}                                ${SERVER}/management/admin/users/active

# General error messages
${403_error_message}                               You do not have the necessary permissions for your request
${404_error_message}                               Please check the web address or search term you entered for any errors
${500_error_message}                               something went wrong
${wrong_filetype_validation_error}                 Your upload must be a PDF.
${finance_query_notes_filetype_error}              Your file must be a PDF or an open source document
${applicant_query_response_filetype_error}         Your upload must be a PDF, document or a spreadsheet
${too_large_5MB_validation_error}                  Please upload a file less than 5MB in size.
${too_large_10MB_validation_error}                 Please upload a file less than 10MB in size.
${too_large_32MB_validation_error}                 Please upload a file less than 32MB in size.
${unsuccessful_login_message}                      Your sign in was unsuccessful because of the following issues
${empty_field_warning_message}                     This field cannot be left blank.
${email_already_in_use}                            This email address is already in use.
${enter_a_valid_name}                              Please enter a valid name.
${enter_a_first_name}                              Please enter a first name.
${enter_a_last_name}                               Please enter a last name.
${enter_a_valid_email}                             Please enter a valid email address.
${enter_a_valid_date}                              Please enter a valid date.
${enter_a_phone_number}                            Please enter a phone number.
${enter_a_valid_phone_number}                      Please enter a valid phone number.
${enter_a_phone_number_between_8_and_20_digits}    Please enter a valid phone number between 8 and 20 digits.
${search_a_valid_postcode}                         Search using a valid postcode or enter the address manually.
${only_accept_whole_numbers_message}               This field can only accept whole numbers.
${field_should_be_1_or_higher}                     This field should be 1 or higher.
${estate_Error_Message}                            You should enter less than GBP 10,000 for estate costs

# Database variables
${database_name}        ifs
${database_user}        root
${database_password}    password
${database_host}        ifs-database
${database_port}        3306

${CLOSED_COMPETITION_NAME}                                          Machine learning for transport infrastructure
${CLOSED_COMPETITION}                                               ${competition_ids['${CLOSED_COMPETITION_NAME}']}
${CLOSED_COMPETITION_APPLICATION_TITLE}                             Neural networks to optimise freight train routing
${CLOSED_COMPETITION_APPLICATION}                                   ${application_ids["${CLOSED_COMPETITION_APPLICATION_TITLE}"]}
${CLOSED_COMPETITION_NAME2}                                         Personalised Smart HUDs for space helmets
${UPCOMING_COMPETITION_TO_ASSESS_NAME}                              Home and industrial efficiency programme
${UPCOMING_COMPETITION_TO_ASSESS_ID}                                ${competition_ids['${UPCOMING_COMPETITION_TO_ASSESS_NAME}']}
${UPCOMING_COMPETITION_TO_ASSESS_OPEN_DB}                           ${getMilestoneDateTimeDb(${READY_TO_OPEN_COMPETITION}, "OPEN_DATE")}
${UPCOMING_COMPETITION_TO_ASSESS_CLOSE_DB}                          ${getMilestoneDateTimeDb(${READY_TO_OPEN_COMPETITION}, "SUBMISSION_DATE")}
${UPCOMING_COMPETITION_TO_ASSESS_OPEN_DATE}                         ${getPrettyLongMilestoneDate(${UPCOMING_COMPETITION_TO_ASSESS_ID}, "OPEN_DATE")}
${UPCOMING_COMPETITION_TO_ASSESS_OPEN_DATE_TIME}                    ${getPrettyMilestoneDateTime(${UPCOMING_COMPETITION_TO_ASSESS_ID}, "OPEN_DATE")}
${UPCOMING_COMPETITION_TO_ASSESS_CLOSE_DATE_TIME}                   ${getPrettyMilestoneDateTime(${UPCOMING_COMPETITION_TO_ASSESS_ID}, "SUBMISSION_DATE")}
${UPCOMING_COMPETITION_TO_ASSESS_CLOSE_DATE_TIME_LONG}              ${getPrettyLongMilestoneDateTime(${UPCOMING_COMPETITION_TO_ASSESS_ID}, "SUBMISSION_DATE")}
${UPCOMING_COMPETITION_TO_ASSESS_NOTIFICATION_DATE}                 ${getPrettyMilestoneDate(${UPCOMING_COMPETITION_TO_ASSESS_ID}, "NOTIFICATIONS")}
${UPCOMING_COMPETITION_TO_ASSESS_ASSESSOR_DEADLINE_DATE_SIMPLE}     ${getSimpleMilestoneDate(${UPCOMING_COMPETITION_TO_ASSESS_ID}, "ASSESSOR_DEADLINE")}
${CURRENTLY_WAITING_UNTIL}                                          false

# Organisation variables
${EMPIRE_LTD_NAME}                    Empire Ltd
${EMPIRE_LTD_ID}                      ${organisation_ids["${EMPIRE_LTD_NAME}"]}
${asosName}                           ASOS PLC
${asosId}                             ${organisation_ids["${asosName}"]}
${organisationLudlowName}             Ludlow
${organisationLudlowId}               ${organisation_ids["${organisationLudlowName}"]}
${organisationEggsName}               EGGS
${organisationEggsId}                 ${organisation_ids["${organisationEggsName}"]}
${organisationRedName}                Red Planet
${organisationRedId}                  ${organisation_ids["${organisationRedName}"]}
${organisationSmithName}              SmithZone
${organisationSmithId}                ${organisation_ids["${organisationSmithName}"]}
${organisationWardName}               Ward Ltd
${organisationWardId}                 ${organisation_ids["${organisationWardName}"]}
${existingAcademicPartnerOrgName}     Aviva Plc
${newPartnerOrgName}                  ROYAL MAIL PLC
${internationalOrganisationName}      International Competition Ltd.

# Organisation type ids
${BUSINESS_TYPE_ID}       1
${ACADEMIC_TYPE_ID}       2
${RTO_TYPE_ID}            3
${PUBLIC_SECTOR_TYPE_ID}  4
${KTP_TYPE_ID}            5

# Competition template type
${compType_Programme}  Programme
${compType_Sector}     Sector
${compType_Generic}    Generic
${compType_EOI}        Expression of interest
${compType_APC}        Advanced Propulsion Centre
${compType_ATI}        Aerospace Technology Institute
${compType_PT}         The Prince's Trust
${compType_H2020}      Horizon 2020
${compType_HESTA}      Horizon Europe Guarantee
${compType_DirectAward}  Direct award

# Competition and Applicant lists
# the questions are only the assessed questions for a particular compettion type
@{milestones}                             Open date  Briefing event  Submission date  Allocate assessors  Assessor briefing  Assessor accepts  Assessor deadline  Line draw  Assessment panel  Panel date  Funders panel  Notifications  Release feedback
@{programme_questions}                    Business opportunity  Potential market  Project exploitation  Economic benefit  Technical approach  Innovation  Risks  Project team  Funding  Adding value
@{programme_questions_procurement_ati}    Business opportunity  Potential market  Project exploitation  Economic benefit  Innovation  Risks  Funding  Adding value
@{other_questions_procurement_ati}        Technical approach  Project team
@{sector_questions}                       Need or challenge  Approach and innovation  Team and resources   Market awareness  Outcomes and route to market  Wider impacts  Project management  Risks  Additionality  Costs and value for money
@{EOI_questions}                          Business opportunity and potential market  Innovation  Project team  Funding and adding value
@{APC_questions}                          How innovative is your project?   Your approach regarding innovation.   Your technical approach.
@{project_details}                        Project summary  Public description  Scope
@{Hecp_questions}                         Tell us where your organisation is based   Participating Organisation project region  What EIC call have you been successfully evaluated for?  Application reference number  UK Funding contribution applied for (GBP)  Have the tasks assigned to your institution changed significantly since the original application?   If this amount has changed please tell us how?  Will you, as a UK institution, be employing PhD students as part of this project?   If so, how many PhD students will be employed at your institution on this project?   How much budget is allocated for PhD students employed at your institution on this project?
@{Thirdparty_questions}                   How innovative is your project?  Your approach regarding innovation.  Your technical approach.
@{Directaward_questions}                  Direct award placeholder question
@{prereg_questions}                       Tell us where your organisation is based  What EIC call have you been successfully evaluated for?  Application reference number  UK Funding contribution applied for (GBP)  Have the tasks assigned to your institution changed significantly since the original application?   If this amount has changed please tell us how?  Will you, as a UK institution, be employing PhD students as part of this project?   If so, how many PhD students will be employed at your institution on this project?   How much budget is allocated for PhD students employed at your institution on this project?

#SBRI Type 1 competition milestones
@{sbriType1Milestones}     Open date  Briefing event  Submission date

#answer choices in application questions while creating a competition
@{multiple_answer_choice}     answer2  answer3  answer4  answer5  answer6  answer7  answer8  answer9  answer10

#Project Setup
${PROJECT_SETUP_COMPETITION_NAME}                                   New designs for a circular economy
${PROJECT_SETUP_COMPETITION}                                        ${competition_ids["${PROJECT_SETUP_COMPETITION_NAME}"]}
${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_COMPANY_NUMBER}     60674010
${PROJECT_SETUP_APPLICATION_1_LEAD_COMPANY_TURNOVER}                100000
${PROJECT_SETUP_APPLICATION_1_LEAD_COMPANY_HEADCOUNT}               60
${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_NUMBER}               53532322
${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_TURNOVER}             100000
${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_HEADCOUNT}            60
${EXISTING_PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}     HIVE IT LIMITED
${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}              ROYAL MAIL PLC
${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_EMAIL}             ewan+1@hiveit.co.uk
${PROJECT_SETUP_APPLICATION_1_PARTNER_EMAIL}                        ${collaborator1_credentials["email"]}
${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}               ${collaborator2_credentials["email"]}

#OrganisationTypes and Info text
${businessOrganisationName}              Business
${researchOrganisationName}              Research
${rtoOrganisationName}                   Research and technology organisation (RTO)
${nonProfitOrganisationName}             Public sector, charity or non Je-S registered research organisation
${bussinessOrgInfoText}                  A person or organisation that provides goods or services in exchange for something of value, usually money.
${nonJe-s/Public/CharityOrgInfoText}     A not-for-profit organisation focusing on innovation.
${researchOrgInfoText}                   Higher education and organisations registered with Je-S.
${rtoOrgInfoText}                        Organisations which solely promote and conduct collaborative research and innovation.

#invalid organisation validation message
${invalidOrganisationValidationMessage}     You are not eligible to start an application