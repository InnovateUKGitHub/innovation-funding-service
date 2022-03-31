*** Settings ***
Documentation   IFS-11442 OFGEM: Create a "ThirdParty" generic template
...
...             IFS-11476 OFGEM: Remove Overhead Costs section and any reference to it
...
...             IFS-11475 OFGEM: Removal of capital usage option in "Your project cost"
...
...             IFS-11481 OFGEM: Funding Cap as a monetary value rather than percentage
...
...             IFS-11483 OFGEM: Delete Reference to General Guidance
...
...             IFS-11568 Your Project costs -> Materials - Content change
...
...             IFS-11569 Your Project costs -> Subcontracting - Content change
...
...             IFS-11570 Your Project costs -> Other costs - Content change
...
...             IFS-11566 OFGEM - Confirmation of submission page amendments
...
...             IFS-11477 OFGEM: Remove Gross Employee Cost Replace with Day Rate
...
...             IFS-11595 Modify application view in application journey, assessment & project setup for T & C changes
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${thirdPartyOfgemCompetitionName}    Thirdparty Competition - Ofgem
${thirdPartyOfgemApplicationName}    Thirdparty Application - Ofgem
${ofgemPartnerEmail}                 ThirdParty@Ofgem.com

*** Test Cases ***
Comp admin can select the funding type as Thirdparty and Competition type as Ofgem
    [Documentation]  IFS-11442
    Given the user navigates to the page            ${CA_UpcomingComp}
    When the user clicks the button/link            jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details    ${thirdPartyOfgemCompetitionName}  ${month}  ${nextyear}  Ofgem  STATE_AID  THIRDPARTY
    And the user clicks the button/link             link = Initial details
    Then the user should see the element            jQuery = dt:contains("Funding type")+dd:contains("Thirdparty")
    And the user should see the element             jQuery = dt:contains("Competition type")+dd:contains("Ofgem")

Comp admin can configure third party procurement terms and conditions
    [Documentation]  IFS-11442
    Given the user clicks the button/link                                 link = Back to competition details
    And the user clicks the button/link                                   link = Terms and conditions
    And the user completes required fields in third party competition     Innovation Fund governance document  Summary of Innovation Fund governance document   https://www.google.com
    When the user clicks the button/link                                  jQuery = button:contains("Done")
    Then the user should see the element                                  link = https://www.google.com (opens in a new window)
    And the user should see the element                                   jQuery = p:contains("This is the project costs guidance link applicants will see in the project costs section.")
    And the user verifies valid terms and conditions text is displaying   Innovation Fund governance document
    And the user clicks the button/link                                   link = Back to competition details
    And the user should see the element                                   jQuery = li:contains("Terms and conditions") .task-status-complete

Comp admin selects third party funder in funding information and completes the competition
    [Documentation]   IFS-11442
    Given comp admin creates ofgem competition
    When the user fills in funding information for the third party comp
    Then the user navigates to the page                                     ${CA_UpcomingComp}
    And the user should see the element                                     jQuery = h3 a:contains("${thirdPartyOfgemCompetitionName}")

User applies to third party ofgem competition
    [Documentation]  IFS-11575  IFS-11476
    [Setup]  get competition id and set open date to yesterday                          ${thirdPartyOfgemCompetitionName}
    Given log in as a different user                                                    &{lead_applicant_credentials}
    And logged in user applies to competition                                           ${thirdPartyOfgemCompetitionName}  3
    And the user clicks the button/link                                                 link = Application details
    When the user fills in the Application details                                      ${thirdPartyOfgemApplicationName}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team                                        COMPLETE  steve.smith@empire.com
    Then the lead applicant fills all the questions and marks as complete(thirdparty)

Applicant should not view overhead and capital usage costs in project costs
    [Documentation]   IFS-11475  IFS-11476
    Given the user navigates to Your-finances page   ${thirdPartyOfgemApplicationName}
    When the user clicks the button/link             link = Your project costs
    Then the user should not see the element         jQuery = button:contains("Overhead costs")
    And the user should not see the element          jQuery = button:contains("Capital usage")

Applicant should not view any references to overhead cost in materials
    [Documentation]   IFS-11568
    When the user clicks the button/link            jQuery = button:contains("Materials")
    Then the user should not see the element        jQuery = p:contains("You can claim the costs of materials used on your project providing:")
    And the user should not see the element         jQuery = li:contains("they are not already purchased or included in the overheads")
    [Teardown]  the user clicks the button/link     jQuery = button:contains("Materials")

Applicant should view ofgem related subcontracting content changes
    [Documentation]   IFS-11569
    When the user clicks the button/link            jQuery = button:contains("Subcontracting")
    Then the user should see the element            jQuery = label:contains("Project partner name")
    And the user should see the element             jQuery = label:contains("Country where the project partner will work")
    And the user should see the element             jQuery = label:contains("Role of the project partner in the project and description of the work they’ll do")
    And the user should not see the element         jQuery = p:contains("You can subcontract work if you don’t have the expertise in your project team.")
    [Teardown]  the user clicks the button/link     jQuery = button:contains("Subcontracting")

Applicant should not view SME applicant related content in Other costs
    [Documentation]   IFS-11570
    When the user clicks the button/link            jQuery = button:contains("Other costs")
    Then the user should not see the element        jQuery = p:contains("Please note that legal or project audit and accountancy fees are not eligible")
    [Teardown]  the user clicks the button/link     jQuery = button:contains("Other costs")

Applicant can view ofgem related labour cost fields and validations
    [Documentation]   IFS-11477
    Given the user expands the section                  Labour
    When the user highlights the fields
    Then the user should see a field error              This field cannot be left blank.
    And the user should see ofgem labour cost fields

Applicant completes ofgem labour costs
    [Documentation]   IFS-11477
    When the user fills the third party project costs
    And the user clicks the button/link                 css = label[for="stateAidAgreed"]
    And the user clicks the button/link                 jQuery = button:contains("Mark as complete")
    And the user clicks the button/link                 link = Your project costs
    Then the user should see readonly labour costs      anotherrole   500   100  £50,000

Applicant can edit labour costs
    [Documentation]   IFS-11477
    Given the user clicks the button/link       jQuery = button:contains("Edit your project costs")
    When the user fills in ofgem labour costs   anotherrole2  10  17
    And the user clicks the button/link         css = label[for="stateAidAgreed"]
    And the user clicks the button/link         jQuery = button:contains("Mark as complete")
    And the user clicks the button/link         link = Your project costs
    And the user clicks the button/link         jQuery = button:contains("Labour")
    Then the user should see the element        jQuery = td:contains("anotherrole2")+td:contains("10")+td:contains("17")+td:contains("£170")

Max funding sought validation - ofgem
    [Documentation]  IFS-7866
    Given the user clicks the button/link                        link = Your project finances
    And the user enters the project location
    And the user fills in the organisation information           ${thirdPartyOfgemApplicationName}  ${SMALL_ORGANISATION_SIZE}
    When the user navigates to Your-finances page                ${thirdPartyOfgemApplicationName}
    And the user selects funding section in project finances
    And the user enters text to a text field                     id = amount   57803
    And the user clicks the button/link                          id= mark-all-as-complete
    Then the user should see a field and summary error           Funding sought cannot be higher than your project costs.

Ofgem application Your funding - empty validation
    [Documentation]  IFS-11481
    When the user enters empty funding amount
    Then the user should see a field and summary error   Enter the amount of funding sought.
    And the user should see the element                  jQuery = span:contains("The amount you apply for must reflect the funding amount available for this competition.")

the user marks the your funding section as complete
    [Documentation]  IFS-11481
    When the user enters text to a text field                   id = amount   25678
    And the user fills thirdparty other funding information
    And the user clicks the button/link                         id = mark-all-as-complete
    Then the user should see the element                        jQuery = td:contains("53,220") ~ td:contains("25,678") ~ td:contains("85.83%") ~ td:contains("20,000") ~ td:contains("7,542")

Ofgem application finance overview
    [Documentation]  IFS-11481
    Given the user clicks the button/link  link = Back to application overview
    When the user clicks the button/link   link = Finances overview
    Then the user should see the element   jQuery = td:contains("53,220") ~ td:contains("25,678") ~ td:contains("85.83%") ~ td:contains("20,000") ~ td:contains("7,542")

the user submits the third party ofgem application
    [Documentation]   IFS-11475  IFS-11476  IFS-11480
    [Setup]  Get competitions id and set it as suite variable   ${thirdPartyOfgemCompetitionName}
    Given the user clicks the button/link                       link = Application overview
    And the user accept the thirdpary terms and conditions      Back to application overview
    When the user clicks the button/link                        id = application-overview-submit-cta
    And the user clicks the button/link                         id = submit-application-button
    Then the user should see the element                        jQuery = h2:contains("Application submitted")
    And the user should see ofgem submitted application amendments
    [Teardown]  update milestone to yesterday                   ${competitionId}  SUBMISSION_DATE

the applicant should not view overhead and capital usage costs in application summary
    [Documentation]   IFS-11475  IFS-11476
    Given the user clicks the button/link      link = View application
    When the user clicks the button/link       jQuery = button:contains("Finances summary")
    Then the user should not see the element   jQuery = th:contains("Overheads (£)")
    And the user should not see the element    jQuery = th:contains("Capital usage (£)")
    And the user should see the element        jQuery = th:contains("Other funding (£)")

The lead applicant can not view general guidenece reference
    [Documentation]  IFS-11483
    [Setup]  Requesting competition and application ID of this Project
    Given log in as a different user            &{innovation_lead_one}
    When the user navigates to the page         ${server}/management/competition/${ThirdPartyCompId}/application/${ThirdPartyApplicationId}
    Then the user should not see the element    jQuery = p:contains("You must read the General Guidance (opens in a new window) before you start")

Internal user should not view overhead and capital usage costs in application summary
    [Documentation]  IFS-11475  IFS-11476
    [Setup]  Requesting competition and application ID of this Project
    Given log in as a different user            &{Comp_admin1_credentials}
    When the user navigates to the page         ${server}/management/competition/${ThirdPartyCompId}/application/${ThirdPartyApplicationId}
    Then the user should not see the element    jQuery = th:contains("Overheads (£)")
    And the user should not see the element     jQuery = th:contains("Capital usage (£)")

Internal user can edit ofgem labour costs
    [Documentation]  IFS-11477
    Given the user moves comp to project setup
    When the user edit the labour costs in project setup
    Then the user should see the element                    jQuery = th:contains("Role within project")
    And the user should see the element                     jQuery = th:contains("Rate (£/day)")
    And the user should see the element                     jQuery = th:contains("Days to be spent on the project")
    And the user should see the element                     jQuery = td:contains("anotherrole3")+td:contains("10")+td:contains("100")+td:contains("£1,000")
    And the user should see the element                     jQuery = .labour-total:contains("£51,170")
    And the user should see the element                     css = [id="total-cost"][value="£54,220"]

New applicant added via project setup should not view any references to terms and conditions
    [Documentation]  IFS-11595
    Given internal user add new partner orgnisation to ofgem project in project setup
    And log in as a different user           ${ofgemPartnerEmail}   ${short_password}
    When the user clicks the button/link    link = ${thirdPartyOfgemApplicationName}
    And the user clicks the button/link     link = Innovation Fund governance document
    And the user selects the checkbox       agreed
    And the user clicks the button/link     jQuery = button:contains("Agree and continue")
    Then the user should see the element    jQuery = .form-footer:contains("Innovation Fund governance document accepted")

New partner can join the ofgem project via project setup
    [Documentation]  IFS-11595
    Given the user clicks the button/link                        link = Back to join project
    And the user completes ofgem project organisation details
    When the user clicks the button/link                         link = Your funding
    When the user enters text to a text field                    id = amount   250
    And the user fills thirdparty other funding information
    And the user clicks the button/link                          id = mark-all-as-complete
    When the user clicks the button/link                         id = submit-join-project-button
    Then the user should see the element                         jQuery = h1:contains("Set up your project") span:contains("${thirdPartyOfgemApplicationName}")

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Set predefined date variables
    Connect to database              @{database}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

Requesting competition and application ID of this Project
    ${ThirdPartyCompId} =  get comp id from comp title    ${thirdPartyOfgemCompetitionName}
    Set suite variable   ${ThirdPartyCompId}
    ${ThirdPartyApplicationId} =  get application id by name   ${thirdPartyOfgemApplicationName}
    Set suite variable    ${ThirdPartyApplicationId}

Requesting project ID of this project
    ${thirdPartyProjId} =  get project id by name    ${thirdPartyOfgemApplicationName}
    Set suite variable   ${thirdPartyProjId}

comp admin creates ofgem competition
    the user fills in the CS Project eligibility            ${BUSINESS_TYPE_ID}    2   false   single-or-collaborative
    the user fills in the CS funding eligibility            false   Ofgem   STATE_AID
    the user selects the organisational eligibility to no   false
    the user fills in the CS Milestones                     PROJECT_SETUP   ${month}   ${nextyear}  No
    the user marks the Application as done                  no   Ofgem   ${thirdPartyOfgemCompetitionName}
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      Thirdparty Ofgem
    the user clicks the button/link                         link = Return to setup overview

the user completes required fields in third party competition
    [Arguments]  ${title}  ${summary}  ${url}
    the user enters text to a text field        id = thirdPartyTermsAndConditionsLabel   ${title}
    the user enters text to a text field        css = .editor   ${summary}
    the user should see the element             jQuery = span:contains("Insert a link including the full URL http:// or https://")
    the user enters text to a text field        id = projectCostGuidanceLink   ${url}
    the user uploads the file                   css = .inputfile  ${valid_pdf}

the user verifies valid terms and conditions text is displaying
    [Arguments]  ${title}
    the user clicks the button/link                     jQuery = a:contains("Third Party (opens in a new window)")
    select window                                       title = ${title} - Innovation Funding Service
    the user should see the element                     jQuery = h1:contains("${title}")
    the user should see the element                     jQuery = a:contains("View ${title} (opens in a new window)")
    the user should see the element                     jQuery = p:contains("Summary of ${title}")
    [Teardown]   the user closes the last opened tab

the user fills the third party project costs
    the user fills in ofgem labour costs        anotherrole  500  100
    the user fills in Material
    the user fills in Subcontracting costs
    the user fills in Travel and subsistence
    the user fills in Other costs

the user fills thirdparty other funding information
    the user selects the radio button       otherFunding  true
    the user enters text to a text field    css = [name*=source]  Lottery funding
    the user enters text to a text field    css = [name*=date]  12-${nextyear}
    the user enters text to a text field    css = [name*=fundingAmount]  20000

the user accept the thirdpary terms and conditions
    [Arguments]  ${returnLink}
    the user clicks the button/link    link = Innovation Fund governance document
    the user selects the checkbox      agreed
    the user clicks the button/link    jQuery = button:contains("Agree and continue")
    the user should see the element    jQuery = .form-footer:contains("Innovation Fund governance document accepted")
    the user clicks the button/link    link = ${returnLink}

the user enters empty funding amount
    the user enters text to a text field           id = amount  ${EMPTY}
    the user clicks the button/link                id = mark-all-as-complete
    the user should see a field and summary error  Enter the amount of funding sought.

the user should see ofgem submitted application amendments
    the user should see the element     jQuery = h3:contains("Assessment process")
    the user should see the element     jQuery = h3:contains("Decision notification")
    the user should see the element     jQuery = h3:contains("If your application is successful")
    the user should see the element     jQuery = h3:contains("If your application is unsuccessful")
    the user should see the element     jQuery = h3:contains("Application feedback")

the user fills in ofgem labour costs
    [Arguments]  ${roleName}  ${rate}  ${days}
    the user enters text to a text field    jQuery = input[id$="role"]:text[value = ""]:first    ${roleName}
    the user enters text to a text field    jQuery = input[id$="rate"][value = ""]:first    ${rate}
    the user enters text to a text field    jQuery = input[id$="days"][value = ""]:first    ${days}
    the user clicks the button/link         jQuery = button:contains("Labour")

the user should see ofgem labour cost fields
    the user should see the element    jQuery = th:contains("Role within")
    the user should see the element    jQuery = span:contains("Rate (£/day)")
    the user should see the element    jQuery = th:contains("Days to be spent on the project")
    the user should see the element    jQuery = input[id$="role"]:text[value = ""]:first

the user highlights the fields
    Set Focus To Element               jQuery = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input
    mouse out                          jQuery = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input
    Set Focus To Element               jQuery = input[id$="rate"][value = ""]:first
    mouse out                          jQuery = input[id$="rate"][value = ""]:first
    Set Focus To Element               jQuery = input[id$="days"][value = ""]:first
    mouse out                          jQuery = input[id$="days"][value = ""]:first

the user should see readonly labour costs
    [Arguments]  ${role}  ${rate}  ${days}  ${total}
    the user clicks the button/link     jQuery = button:contains("Labour")
    the user should see the element     jQuery = th:contains("Role within project")
    the user should see the element     jQuery = th:contains("Rate (£/day)")
    the user should see the element     jQuery = th:contains("Days to be spent on the project")
    the user should see the element     jQuery = th:contains("Total costs")
    the user should see the element     jQuery = td:contains("${role}")+td:contains("${rate}")+td:contains("${days}")+td:contains("${total}")

the user moves comp to project setup
    Log in as a different user                      &{internal_finance_credentials}
    moving competition to Closed                    ${ThirdPartyCompId}
    making the application a successful project     ${ThirdPartyCompId}  ${thirdPartyOfgemApplicationName}
    moving competition to Project Setup             ${ThirdPartyCompId}
    Requesting project ID of this project

the user edit the labour costs in project setup
    the user navigates to the page          ${server}/project-setup-management/project/${thirdPartyProjId}/finance-check/organisation/${EMPIRE_LTD_ID}/eligibility
    the user clicks the button/link         link = Edit project costs
    the user clicks the button/link         jQuery = button:contains("Add another role")
    the user enters text to a text field    jQuery = input[id$="role"]:text[value = ""]:first    anotherrole3
    the user enters text to a text field    jQuery = input[id$="rate"][value = ""]:first    10
    the user enters text to a text field    jQuery = input[id$="days"][value = ""]:first    100
    the user clicks the button/link         id = save-eligibility

internal user add new partner orgnisation to ofgem project in project setup
    the user navigates to the page                         ${server}/project-setup-management/competition/${ThirdPartyCompId}/project/${thirdPartyProjId}/team/partner
    the user adds a new partner organisation               Testing Admin Organisation  Name Surname  ${ofgemPartnerEmail}
    a new organisation is able to accept project invite    Name  Surname  ${ofgemPartnerEmail}  ROYAL  ROYAL MAIL PLC  ${ThirdPartyApplicationId}  ${thirdPartyOfgemApplicationName}

the user completes ofgem project organisation details
    the user clicks the button/link         link = Your organisation
    the user selects the radio button       organisationSize  MEDIUM
    the user enters text to a text field    css = #turnover   5600
    the user enters text to a text field    css = #headCount    3000
    the user clicks the button/link         jQuery = button:contains("Mark as complete")
    the user should see the element         jQuery = li div:contains("Your organisation") ~ .task-status-complete