*** Settings ***
Documentation     IFS-12292 Maximum funding amount - comp setup
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
${fastTrackCompName}            Fast Track Competition for fixed amount of funding
${fastTrackApplicationName}     Fast Track Application

*** Test Cases ***
Maximun funding amount sought default selection as No
    [Documentation]  IFS-12292
    Given The user logs-in in new browser                   &{Comp_admin1_credentials}
    And the user navigates to the page                      ${CA_UpcomingComp}
    And the user clicks the button/link                     jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details            ${fastTrackCompName}  ${month}  ${nextyear}  ${compType_Programme}  STATE_AID  GRANT
    When the user clicks the button/link                    link = Funding eligibility
    And the user selects the radio button                   researchCategoriesApplicable    true
    And the user selects the checkbox                       research-categories-33  #Feasibility
    And the user clicks the button/link                     jQuery = button:contains("Done")
    And the user clicks the button/link                     jQuery = span:contains("Funding amount sought")
    Then the user should see the element                    css = [name="fundingAmountSoughtApplicable"][checked="checked"]
    And the user should see the element                     jQuery = h1:contains("Funding amount sought")
    And the user should see the element                     jQuery = h2:contains("Is a maximum funding amount sought?")

The user can choose no maximum funding amount sought required
    [Documentation]  IFS-12292
    When the user clicks the button/link        jQuery = button:contains("Done")
    Then the user should see the element        jQuery = dt:contains("Is a maximum funding amount sought?")+dd:contains("No")

Funding amount sought field validation
    [Documentation]  IFS-12292
    Given the user clicks the button/link       jQuery = button:contains("Edit")
    When the user selects the radio button      fundingAmountSoughtApplicable   true
    And the user clicks the button/link         jQuery = button:contains("Done")
    Then the user should see the element        jQuery = li:contains("Enter the maximum funding amount.")

the user completes maximum funding required with fixed amount of funding sought value
    [Documentation]  IFS-12292
    When the user enters text to a text field   name = fundingAmountSought   40000
    And the user clicks the button/link         jQuery = button:contains("Done")
    Then the user should see the element        jQuery = dt:contains("Is a maximum funding amount sought?")+dd:contains("Yes")
    And the user should see the element         jQuery = dt:contains("Funding amount sought ")+dd:contains("£40,000")
    And the user should see the element         jQuery = p:contains("Click edit to change the funding amount sought.")
    And the user clicks the button/link         link = Back to competition details
    And the user should see the element         jQuery = div:contains("Funding eligibility") ~ .task-status-complete

the competition administrator completes fast track competition and opens to external users
    [Documentation]  IFS-12292
    When the competition admin creates fast track competition   ${business_type_id}  ${fastTrackCompName}  Fast Track  ${compType_Programme}  STATE_AID  GRANT  PROJECT_SETUP  yes  50  true  collaborative  No
    Then get competition id and set open date to yesterday      ${fastTrackCompName}

Lead applicant can not mark your funding section as complete if the total amount of funding requested is exceeded the funding amount sought
    [Documentation]  IFS-12292
    Given the user logs out if they are logged in
    And the lead user creates fast track application                                    FastTrack   User   fasttrackuser@gmail.com   ${fastTrackApplicationName}
    And the lead user completes project details, application questions sections         ${fastTrackApplicationName}  COMPLETE   fasttrackuser@gmail.com
    And the user completes project costs, project location and organisation details     ${fastTrackApplicationName}  labour costs  54,000  yes
    When the user clicks the button/link                                                link = Your funding
    And the user selects the radio button                                               requestingFunding   true
    And the user enters text to a text field                                            css = [name^="grantClaimPercentage"]  70
    And the user clicks the button/link                                                 jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error                                  Your funding sought exceeds GBP 40,000. You must lower your funding level percentage or your project costs.

Lead applicant cam mark your funding section as complete if the funding amount requested is less than the funding amount sought
    [Documentation]  IFS-12292
    Given the user enters text to a text field                            css = [name^="grantClaimPercentage"]  50
    When the user selects the radio button                                otherFunding   false
    And the user clicks the button/link                                   jQuery = button:contains("Mark as complete")
    Then the user should see the element                                  jQuery = li:contains("Your funding") .task-status-complete

Partner applicant can not mark your funding section as complete if the total amount of funding requested is exceeded the funding amount sought
    [Documentation]  IFS-12292
    Given the user clicks the button/link                                                       link = Back to application overview
    And the lead invites already registered user                                                ${collaborator1_credentials["email"]}   ${fastTrackCompName}
    And partner applicant completes project costs, project location and organisation details    ${fastTrackApplicationName}  ${collaborator1_credentials["email"]}  ${short_password}
    When the user clicks the button/link                                                        link = Your funding
    And the user selects the radio button                                                       requestingFunding   true
    And the user enters text to a text field                                                    css = [name^="grantClaimPercentage"]  70
    And the user clicks the button/link                                                         jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error                                          Your funding sought exceeds GBP 40,000. You must lower your funding level percentage or your project costs.

Partner applicant cam mark your funding section as complete if the funding amount requested is less than the funding amount sought
    [Documentation]  IFS-12292
    Given the user enters text to a text field                            css = [name^="grantClaimPercentage"]  5.00
    When the user selects the radio button                                otherFunding   false
    And the user clicks the button/link                                   jQuery = button:contains("Mark as complete")
    Then the user should see the element                                  jQuery = li:contains("Your funding") .task-status-complete

Lead applicant can submit the application if the funding amount requested is less than the funding amount sought of all partners
    [Documentation]  IFS-12292
    Given the user clicks the button/link                                 link = Back to application overview
    And the user accept the competition terms and conditions              Return to application overview
    And log in as a different user                                        fasttrackuser@gmail.com     ${short_password}
    When the user clicks the button/link                                  link = ${fastTrackApplicationName}
    And the applicant completes Application Team                          COMPLETE  fasttrackuser@gmail.com
    And the user accept the competition terms and conditions              Back to application overview
    And the user clicks the button/link                                   id = application-overview-submit-cta
    And the user clicks the button/link                                   id = submit-application-button
    Then the user should see the element                                  jQuery = h2:contains("Application submitted")


*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The guest user opens the browser

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the lead user creates fast track application
    [Arguments]   ${firstName}   ${lastName}   ${email}   ${applicationName}
    the user select the competition and starts application          ${fastTrackCompName}
    the user clicks the button/link                                 link = Continue and create an account
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

the competition admin creates fast track competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingRule}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}  ${isOpenComp}
    the user selects the Terms and Conditions               ${compType}  ${fundingRule}
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility            ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user selects the organisational eligibility to no   false
    the user fills in the CS Milestones                     ${completionStage}   ${month}   ${nextyear}  ${isOpenComp}
    the user marks the application as done                  ${projectGrowth}  ${compType}  ${competition}
    the user fills in the CS Assessors                      ${fundingType}
    the user fills in the CS Documents in other projects
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview
    the user clicks the button/link                         jQuery = a:contains("Complete")
    the user clicks the button/link                         jQuery = button:contains('Done')
    the user navigates to the page                          ${CA_UpcomingComp}
    the user should see the element                         jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user completes project costs, project location and organisation details
    [Arguments]  ${Application}  ${overheadsCost}  ${totalCosts}  ${Project_growth_table}
    the user fills in the project costs  ${overheadsCost}  ${totalCosts}
    the user enters the project location
    Run Keyword if  '${Project_growth_table}' == 'no'    the user fills in the organisation information  ${Application}  ${SMALL_ORGANISATION_SIZE}
    Run Keyword if  '${Project_growth_table}' == 'yes'  the user fills the organisation details with Project growth table  ${Application}  ${SMALL_ORGANISATION_SIZE}

partner applicant completes project costs, project location and organisation details
    [Arguments]   ${application_title}  ${collaboratorEmail}  ${collaboratorPassword}
    logging in and error checking                                                       ${collaboratorEmail}  ${collaboratorPassword}
    the user clicks the button/link                                                     css = .govuk-button[type="submit"]    #Save and continue
    the user clicks the button/link                                                     link = Your project finances
    the user completes project costs, project location and organisation details         ${application_title}   Calculate  52,214  yes