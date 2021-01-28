*** Settings ***
Documentation     IFS-9009  Always open competitions: invite assessors to competitions
...
...               IFS-8850  Always open competitions: applicant changes
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown

Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          ../../../resources/keywords/05__Email_Keywords.robot

*** Variables ***
# REPLACE WEB TEST DATA VARIABLES WITH ACTUAL E2E FLOW DATA AND CHANGE NAME OF VARIABLES
${webTestCompName}                 Always open competition
${applicationName}                 Always open application
${webTestAssessor}                 Paul Plum
${webTestAssessorEmailAddress}     paul.plum@gmail.com

*** Test Cases ***
Send the email invite to the assessor for the competition using new content
    [Documentation]  IFS-9009
    Given Logging in and Error Checking         &{Comp_admin1_credentials}
    When comp admin sends invite to assesor
    Then the user reads his email               ${webTestAssessorEmailAddress}  Invitation to be an assessor for competition: '${webTestCompName}'  We invite you to assess applications for the competition:

Lead user creates an always open application and checks the status
    [Documentation]  IFS-8850
    Given the user logs out if they are logged in
    And the lead user creates an always open application     Test   user1   test.user1@gmail.com   ${applicationName}
    When the lead user completes project details, application questions and finances sections
    Then the user checks the status of the application before completion

Lead user completes the t's & c's of the always open application and checks the status
    [Documentation]  IFS-8850
    Given The user clicks the button/link                                   link = ${applicationName}
    And the user clicks the button/link                                     link = Award terms and conditions
    When the user accept the competition terms and conditions               Back to application overview
    Then the user checks the status of the application after completion

Lead user submits the always open application and checks the status
    [Documentation]  IFS-8850
    Given The user clicks the button/link                                  link = ${applicationName}
    When The user clicks the button/link                                   link = Review and submit
    and the user clicks the button/link                                    jQuery = button:contains("Submit application")
    Then the user checks the status of the application after submission

*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The guest user opens the browser

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

comp admin sends invite to assesor
    the user clicks the button/link          link = ${webTestCompName}
    the user clicks the button/link          link = Invite assessors to assess the competition
    the user enters text to a text field     id = assessorNameFilter  ${webTestAssessor}
    the user clicks the button/link          jQuery = .govuk-button:contains("Filter")
    the user clicks the button/link          jQuery = tr:contains("${webTestAssessor}") label[for^="assessor-row"]
    the user clicks the button/link          jQuery = .govuk-button:contains("Add selected to invite list")
    the user clicks the button/link          link = Invite
    the user clicks the button/link          link = Review and send invites
    the user clicks the button/link          jQuery = .govuk-button:contains("Send invitation")

the lead user creates an always open application
    [Arguments]   ${firstName}   ${lastName}   ${email}   ${applicationName}
    the user select the competition and starts application          ${webTestCompName}
    the user clicks the button/link                                 link = Continue and create an account
#    user selects where is organisation based                        isNotInternational
#    the user can see multiple options when selecting org type
    the user selects the radio button                               organisationTypeId    radio-${BUSINESS_TYPE_ID}
    the user clicks the button/link                                 jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House            ASOS  ASOS PLC
    the user should be redirected to the correct page               ${SERVER}/registration/register
    the user enters the details and clicks the create account       ${firstName}  ${lastName}  ${email}  ${short_password}
    the user reads his email and clicks the link                    ${email}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page               ${REGISTRATION_VERIFIED}
    the user clicks the button/link                                 link = Sign in
    Logging in and Error Checking                                   ${email}  ${short_password}
    the user clicks the button/link                                 link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}

the lead user completes project details, application questions and finances sections
    the user completes the application details section     ${applicationName}  ${tomorrowday}  ${month}  ${nextyear}  25
    the applicant marks EDI question as complete
    the user completes the research category               Feasibility studies
    the user marks the project details as complete
    the lead applicant fills all the questions and marks as complete(programme)
    the user marks the finances as complete        ${applicationName}  labour costs  54,000  no



#the user can see multiple options when selecting org type
#    the user should see the element     css=[name^="organisationTypeId"][value="radio-${BUSINESS_TYPE_ID}"] ~ label, [id="radio-${BUSINESS_TYPE_ID}"] ~ label
#    the user should see the element     css=[name^="organisationTypeId"][value="radio-${ACADEMIC_TYPE_ID}"] ~ label, [id="radio-${ACADEMIC_TYPE_ID}"] ~ label
#    the user should see the element     css=[name^="organisationTypeId"][value="radio-${RTO_TYPE_ID}"] ~ label, [id="radio-${RTO_TYPE_ID}"] ~ label
#    the user should see the element     css=[name^="organisationTypeId"][value="radio-${PUBLIC_SECTOR_TYPE_ID}"] ~ label, [id="radio-${PUBLIC_SECTOR_TYPE_ID}"] ~ label

the user completes the application details section
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}  ${projectDuration}
    the user clicks the button/link             link = Application details
    the user should see the element             jQuery = h1:contains("Application details")
    the user enters text to a text field        id = name  ${appTitle}
    the user enters text to a text field        id = startDate  ${tomorrowday}
    the user enters text to a text field        css = #application_details-startdate_month  ${month}
    the user enters text to a text field        css = #application_details-startdate_year  ${nextyear}
    the user should see the element             jQuery = label:contains("Project duration in months")
    the user enters text to a text field        css = [id="durationInMonths"]  ${projectDuration}
    the user clicks the button twice            css = label[for="resubmission-no"]
    the user clicks the button/link             id = application-question-complete
    the user clicks the button/link             link = Back to application overview
    the user should see the element             jQuery = li:contains("Application details") > .task-status-complete

The user completes the research category
    [Arguments]  ${res_category}
    the user clicks the button/link      link=Research category
    the user selects the checkbox        researchCategory
    the user clicks the button/link      jQuery=label:contains("${res_category}")
    the user clicks the button/link      id=application-question-complete
    the user should see the element      jQuery=li:contains("Research category") > .task-status-complete

the user checks the status of the application before completion
    the user should see the element         jQuery = dt:contains("Application deadline:") ~ dd:contains("Decision pending")
    the user clicks the button/link         link = Back to applications
    the user should see the element         jQuery = li:contains("${applicationName}") .status:contains("% complete")
    the user should not see the element     jQuery = li:contains("${applicationName}") .status:contains("days left")

the user checks the status of the application after completion
    the user should see the element         jQuery = dt:contains("Application deadline:") ~ dd:contains("Decision pending")
    the user clicks the button/link         link = Back to applications
    the user should see the element         jQuery = li:contains("${applicationName}") .status:contains("Ready to review and submit")
    the user should not see the element     jQuery = li:contains("${applicationName}") .status:contains("days left")

the user checks the status of the application after submission
    the user should see the element     jQuery = h2:contains("Application submitted")
    the user should see the element     jQuery = a:contains("Reopen application")


the applicant adds a partner for each organisation type
    the user selects the radio button       organisationTypeId    radio-${BUSINESS_TYPE_ID}
    the user clicks the button/link         jQuery = .govuk-button:contains("Save and continue")
    the user should see the element         jQuery = h3:contains("Partner") span:contains("1")
    the user should see the element         jQuery = td:contains("Business") ~ td:contains("Edit") button:contains("Remove organisation")
    the user clicks the button/link         link = Add a partner organisation
    the user selects the radio button       organisationTypeId    radio-${ACADEMIC_TYPE_ID}
    the user clicks the button/link         jQuery = .govuk-button:contains("Save and continue")
    the user should see the element         jQuery = h3:contains("Partner") span:contains("2")
    the user should see the element         jQuery = td:contains("Research") ~ td:contains("Edit") button:contains("Remove organisation")
    the user clicks the button/link         link = Add a partner organisation
    the user selects the radio button       organisationTypeId    radio-${RTO_TYPE_ID}
    the user clicks the button/link         jQuery = .govuk-button:contains("Save and continue")
    the user should see the element         jQuery = h3:contains("Partner") span:contains("3")
    the user should see the element         jQuery = td:contains("Research and technology organisation (RTO)") ~ td:contains("Edit") button:contains("Remove organisation")
    the user clicks the button/link         link = Add a partner organisation
    the user selects the radio button       organisationTypeId    radio-${PUBLIC_SECTOR_TYPE_ID}
    the user clicks the button/link         jQuery = .govuk-button:contains("Save and continue")
    the user should see the element         jQuery = h3:contains("Partner") span:contains("4")
    the user should see the element         jQuery = td:contains("Public sector, charity or non Je-S registered research organisation") ~ td:contains("Edit") button:contains("Remove organisation")
    the user clicks the button/link         id = application-question-complete
    the user clicks the button/link         link = Application overview
    the user should see the element         jQuery = li:contains("Application team") > .task-status-complete