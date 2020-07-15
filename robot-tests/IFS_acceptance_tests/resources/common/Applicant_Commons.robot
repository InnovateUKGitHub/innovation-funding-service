*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***
${project_guidance}    https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance

*** Keywords ***
the user should see all the Your-Finances Sections
    the user should see the element  link = Your project costs
    the user should see the element  link = Your organisation
    the user should see the element  jQuery = h3:contains("Your funding")
    the user should see the element  jQuery = h2:contains("Finance summary")

the user navigates to Your-finances page
    [Arguments]  ${Application}
    the user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link  jQuery = h3:contains("${Application}") a
    the user clicks the button/link  link = Your project finances

Applicant navigates to the finances of the Robot application
    the user navigates to Your-finances page  Robot test application

log in and create new application if there is not one already with complete application details
    [Arguments]  ${applicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    log in and create new application if there is not one already  ${applicationTitle}
    the applicant completes the application details  ${applicationTitle}  ${tomorrowday}  ${month}  ${nextyear}

Mark application details as incomplete and the user closes the browser
    [Arguments]  ${applicationTitle}
    Mark application details as incomplete  ${applicationTitle}
    the user closes the browser

Mark application details as incomplete
    [Arguments]  ${applicationTitle}
    the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link  link = ${applicationTitle}
    the user clicks the button/link  link = Application details
    the user clicks the button/link  jQuery = button:contains("Edit")
    the user clicks the button/link  jQuery = button:contains("Save and return to application overview")
    the user should see the element  jQuery = li:contains("Application details") > .task-status-incomplete

the Application details are completed
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  page should contain element  css = li:contains("Application details") > .task-status-complete
    Run Keyword If  '${status}' == 'FAIL'  Run keywords  the user clicks the button/link  link = Application details
    ...   AND  the user moves Application details in Edit mode
    ...   AND  the user fills in the Application details  Robot test application  ${tomorrowday}  ${month}  ${nextyear}

the applicant completes the application details
    [Arguments]  ${applicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user moves Application details in Edit mode
    ${applicationId} =  get application id by name     ${applicationTitle}
    the user navigates to the page                     ${server}/application/${applicationId}
    the applicant marks EDI question as complete
    the user clicks the button/link                    link = Application details
    the user fills in the Application details          ${applicationTitle}  ${tomorrowday}  ${month}  ${nextyear}

the user moves Application details in Edit mode
     ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  page should contain element  css = button[name=edit]
     Run Keyword If  '${status}' == 'PASS'  the user clicks the button/link  css = button[name=edit]  # the Edit link

the user fills in the Application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user should see the element       jQuery = h1:contains("Application details")
    the user enters text to a text field  id = name  ${appTitle}
    the user enters text to a text field  id = startDate  ${tomorrowday}
    the user enters text to a text field  css = #application_details-startdate_month  ${month}
    the user enters text to a text field  css = #application_details-startdate_year  ${nextyear}
    the user enters text to a text field  css = [id="durationInMonths"]  24
    the user clicks the button twice      css = label[for="resubmission-no"]
    the user should not see the element   link = Choose your innovation area
    The user clicks the button/link       id = application-question-complete
    the user clicks the button/link       link = Back to application overview
    the user should see the element       jQuery = li:contains("Application details") > .task-status-complete

the user selects research category from funding
    [Arguments]  ${res_category}
    the user clicks the button/link   link = research category
    the user clicks the button twice  jQuery = label:contains("${res_category}")
    the user clicks the button/link   id = application-question-complete
    the user clicks the button/link   link = Back to application overview
    the user should see the element   jQuery = li:contains("Research category") > .task-status-complete

the user marks the finances as complete
    [Arguments]  ${Application}  ${overheadsCost}  ${totalCosts}  ${Project_growth_table}
    the user fills in the project costs  ${overheadsCost}  ${totalCosts}
    the user enters the project location
    Run Keyword if  '${Project_growth_table}' == 'no'    the user fills in the organisation information  ${Application}  ${SMALL_ORGANISATION_SIZE}
    Run Keyword if  '${Project_growth_table}' == 'yes'  the user fills the organisation details with Project growth table  ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user checks Your Funding section        ${Application}
    the user should see all finance subsections complete
    the user clicks the button/link  link = Back to application overview
    the user should see the element  jQuery = li:contains("Your project finances") > .task-status-complete

the user fills the procurement project costs
    [Arguments]  ${overheadsCost}  ${totalCosts}
    the user clicks the button/link  link = Your project costs
    the user fills in Labour
    the user fills in procurement Overhead costs
    the user fills in Material
    the user fills in Capital usage
    the user fills in Subcontracting costs
    the user fills in Travel and subsistence
    the user fills in Other costs

the user fills in the project costs
    [Arguments]  ${overheadsCost}  ${totalCosts}
    the user clicks the button/link  link = Your project costs
    the user fills in Labour
    the user fills in Overhead costs  ${overheadsCost}  ${totalCosts}
    the user fills in Material
    the user fills in Capital usage
    the user fills in Subcontracting costs
    the user fills in Travel and subsistence
    the user fills in Other costs
    the user clicks the button/link  css = label[for="stateAidAgreed"]
    the user clicks the button/link  jQuery = button:contains("Mark as complete")
    the user clicks the button/link  link = Your project costs
    the user has read only view once section is marked complete

the user fills in project costs no overheads
    the user clicks the button/link  link = Your project costs
    the user fills in Labour
    the user checks overhead is not editable
    the user fills in Material
    the user fills in Capital usage
    the user fills in Subcontracting costs
    the user fills in Travel and subsistence
    the user fills in Other costs
    the user clicks the button/link  css = label[for="stateAidAgreed"]
    the user clicks the button/link  jQuery = button:contains("Mark as complete")
    the user clicks the button/link  link = Your project costs
    the user has read only view once section is marked complete

the user checks overhead is not editable
    the user expands the section         Overhead costs
    the user should see the element      jQuery = p:contains("Overheads are calculated as 20% of labour costs")
    the user should not see the element  jQuery = input[name="overhead.rateType"]

the user has read only view once section is marked complete
    the user should not see the element   css = input
    the user should see the element       jQuery = button:contains("Edit")
    the user clicks the button/link       jQuery = a:contains("Return to finances")

the user fills in Labour
    the user expands the section               Labour
    the user should see the element            css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input
    the user enters text to a text field       id = working-days-per-year   230
    the user should see the element            jQuery = input[id$="role"]:text[value = ""]:first
    the user enters text to a text field       jQuery = input[id$="role"]:text[value = ""]:first    anotherrole
    the user enters text to a text field       jQuery = input[id$="gross"][value = ""]:first    120000
    the user enters text to a text field       jQuery = input[id$="days"][value = ""]:first    100
    the user clicks the button/link            jQuery = button:contains("Labour")

the user fills in procurement Overhead costs
    the user expands the section  Overhead costs
    Validations for procurement Overhead costs
    the user enters text to a text field    css = #accordion-finances-content-10 tbody tr:nth-of-type(1) td:nth-of-type(1) input   Cost
    the user enters text to a text field    css = #accordion-finances-content-10 tbody tr:nth-of-type(1) td:nth-of-type(2) input   5000
    the user enters text to a text field    css = #accordion-finances-content-10 tbody tr:nth-of-type(1) td:nth-of-type(3) input   10
    the user should not see an error in the page

Validations for procurement Overhead costs
    the user clicks the button/link         jQuery = button:contains("Add another overhead")
    the user enters text to a text field    css = #accordion-finances-content-10 tbody tr:nth-of-type(1) td:nth-of-type(1) input   ${EMPTY}
    the user enters text to a text field    css = #accordion-finances-content-10 tbody tr:nth-of-type(1) td:nth-of-type(2) input   ${EMPTY}
    the user enters text to a text field    css = #accordion-finances-content-10 tbody tr:nth-of-type(1) td:nth-of-type(3) input   ${EMPTY}
    the user should see the element         jQuery = #accordion-finances-content-10 td:nth-of-type(1) .govuk-error-message:contains("${empty_field_warning_message}")
    the user should see the element         jQuery = #accordion-finances-content-10 td:nth-of-type(2) .govuk-error-message:contains("${empty_field_warning_message}")
    the user should see the element         jQuery = #accordion-finances-content-10 td:nth-of-type(3) .govuk-error-message:contains("${empty_field_warning_message}")
    the user clicks the button/link         css = #accordion-finances-content-10 tbody tr:nth-of-type(2) td:nth-of-type(5) button   #Remove

the user fills in Overhead costs
    [Arguments]  ${overheadsCost}  ${totalCosts}
    run keyword if  '${overheadsCost}' == 'Calculate'  the user chooses Calculate overheads option  ${totalCosts}
    run keyword if  '${overheadsCost}' == 'labour costs'  the user chooses 20% overheads option
#    run keyword if  '${overheadsCost}' == 'No overhead'  the user chooses No overhead costs
# The above line is commented out because we do not use the 3rd option yet. Once we do we can enable it.

the user chooses Calculate overheads option
    [Arguments]  ${totalCosts}
    the user expands the section  Overhead costs
    the user clicks the button/link                         jQuery = label:contains("Calculate overheads")
    the user should see the element                         jQuery = h3:contains("Calculate overheads")
    the user enters text to a text field                    css = input[name^="overhead.total"][id^="overhead.total"]   40
    the user uploads the file                               css = .inputfile   ${excel_file}
    the total overhead costs should reflect rate entered    css = #total-cost  £${totalCosts}

the total overhead costs should reflect rate entered
    [Arguments]    ${ADMIN_TOTAL}    ${ADMIN_VALUE}
    the element should be disabled      css = #total-cost
    Textfield Value Should Be    ${ADMIN_TOTAL}    ${ADMIN_VALUE}

the user chooses 20% overheads option
    # overheads option : 20% Labour
    the user clicks the button/link    jQuery = button:contains("Overhead costs")
    the user clicks the button/link    css = [data-target="overhead-default-percentage"] label
    the user clicks the button/link    jQuery = button:contains("Overhead costs")

the user fills in Material
    the user clicks the button/link       jQuery = button:contains("Materials")
    the user should see the element       css = table[id=material-costs-table] tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field  css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field  css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    the user enters text to a text field  css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user clicks the button/link       jQuery = button:contains("Materials")

the user fills in Capital usage
    the user expands the section          Capital usage
    the user enters text to a text field  css = textarea.govuk-textarea[name^=capitalUsageRows]  some description
    Click Element                         jQuery = label:contains("New")
    the user enters text to a text field  css = .form-finances-capital-usage-depreciation  10
    the user enters text to a text field  css = .form-finances-capital-usage-npv  5000
    the user enters text to a text field  css = .form-finances-capital-usage-residual-value  25
    the user enters text to a text field  css = .form-finances-capital-usage-utilisation   100
    textfield should contain              css = #capital-usage .form-row:nth-of-type(1) [readonly="readonly"]  £4,975
    the user expands the section          Capital usage

the user fills in Subcontracting costs
    the user clicks the button/link       jQuery = button:contains("Subcontracting costs")
    the user enters text to a text field  css = .form-finances-subcontracting-company  SomeName
    the user enters text to a text field  css = input.govuk-input[name$=country]  Netherlands
    the user enters text to a text field  css = textarea.govuk-textarea[name$=role]  Quality Assurance
    the user enters text to a text field  css = input.govuk-input[name^=subcontracting][name$=cost]  1000
    the user clicks the button/link       jQuery = button:contains("Subcontracting costs")

the user fills in Travel and subsistence
    the user clicks the button/link       jQuery = button:contains("Travel and subsistence")
    the user enters text to a text field  css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user enters text to a text field  css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field  css = #travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    the user clicks the button/link       jQuery = button:contains("Travel and subsistence")

the user fills in Other costs
    the user clicks the button/link       jQuery = button:contains("Other costs")
    the user removes prev costs if there are any
    the user enters text to a text field  css = textarea.govuk-textarea[name$=description]  some other costs
    the user enters text to a text field  css = input.govuk-input[name$=estimate]  50
    the user clicks the button/link       jQuery = button:contains("Other costs")

the user removes prev costs if there are any
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  page should contain element  css = table[id = "other-costs-table"] tr:contains("Remove")
    Run Keyword If    '${status}' == 'PASS'    the user clicks the button/link  jQuery = table[id = "other-costs-table"] tr:contains("Remove")

the academic user fills in his finances
    [Arguments]  ${application}
    the user navigates to Your-finances page  ${application}
    the user clicks the button/link           link = Your project costs
    the academic fills in the project costs

the academic fills in the project costs
    The user enters text to a text field  css = [name$="incurredStaff"]  4242
    The user enters text to a text field  css = [name$="incurredTravel"]  4243
    The user enters text to a text field  css = [name$="incurredOtherCosts"]  4244
    The user enters text to a text field  css = [name$="allocatedInvestigators"]  42
    The user enters text to a text field  css = [name$="allocatedEstateCosts"]  3000
    The user enters text to a text field  css = [name$="allocatedOtherCosts"]  5
    The user enters text to a text field  css = [name$="indirectCosts"]  8909
    The user enters text to a text field  css = [name$="exceptionsStaff"]  123
    The user enters text to a text field  css = [name$="exceptionsOtherCosts"]  7890
    The user enters text to a text field  css = input[name$="tsbReference"]  L33t
    the user should see the element       jQuery = [data-mirror^="#total"]:contains("£32,698")
    the user uploads the file             css = .inputfile  ${5mb_pdf}
    the user should see the element       jQuery = a:contains(${5mb_pdf} (opens in a new window))
    the user clicks the button/link       css = #mark-all-as-complete[type="submit"]

the user enters the project location
    the user clicks the button/link         link = Your project location
    the user enters text to a text field    postcode   BS1 4NT
    the user clicks the button/link         jQuery = button:contains("Mark as complete")

the user fills the organisation details with Project growth table
    [Arguments]   ${Application}  ${org_size}
    the user navigates to Your-finances page                ${Application}
    the user clicks the button/link                         link = Your organisation
    the user enters text to a text field                    css = #financialYearEndMonthValue    12
    the user enters text to a text field                    css = #financialYearEndYearValue    2016
    the user selects the radio button                       organisationSize  ${org_size}
    the user enters text to a text field                    css = #annualTurnoverAtLastFinancialYear   5600
    the user enters text to a text field                    css = #annualProfitsAtLastFinancialYear    3000
    the user enters text to a text field                    css = #annualExportAtLastFinancialYear    4000
    the user enters text to a text field                    css = #researchAndDevelopmentSpendAtLastFinancialYear    5660
    the user enters text to a text field                    css = #headCountAtLastFinancialYear    0
    the user clicks the button/link                         jQuery = button:contains("Mark as complete")

the user fills in the organisation information
    [Arguments]  ${Application}  ${org_size}
    the user navigates to Your-finances page  ${Application}
    the user clicks the button/link         link = Your organisation
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery = button:contains("Edit")
    Run Keyword If    '${status}' == 'PASS'    the user clicks the button/link  jQuery = button:contains("Edit")
    the user selects the radio button       organisationSize  ${org_size}
    the user enters text to a text field    css = #turnover    150
    the user enters text to a text field    css = #headCount   3
    the user clicks the button/link         jQuery = button:contains("Mark as complete")
    the user clicks the button/link         link = Your organisation
    the user should see the element         jQuery = button:contains("Edit")
    the user has read only view once section is marked complete

the user checks Your Funding section
    [Arguments]  ${Application}
    the user clicks the button/link  link = Your funding
    ${Research_category_selected} =   run keyword and return status without screenshots    Element Should Not Be Visible   jQuery = a:contains("research category")
    Run Keyword if   '${Research_category_selected}' == 'False'     the user selects research area       ${Application}
    Run Keyword if   '${Research_category_selected}' == 'True'      the user fills in the funding information      ${Application}

the user checks for funding level guidance at application level
    the user clicks the button/link     link = Your funding
    the user clicks the button/link     jQuery = button:contains("Edit your funding")
    the user should see the element     jQuery = .govuk-hint:contains("The maximum you can enter is")
    the user clicks the button/link     link = competition's rules (opens in a new window)
    the user closes the last opened tab
    the user clicks the button/link     jQuery = button:contains("Mark as complete")
    the user clicks the button/link     link = Back to application overview

the user checks for funding level guidance at PS level
    the user clicks the button/link     link = Your funding
    the user selects the radio button   requestingFunding   true
    the user should see the element     jQuery = .govuk-hint:contains("The maximum you can enter is")
    the user clicks the button/link     link = competition's rules (opens in a new window)
    the user closes the last opened tab
    the user clicks the button/link     link = Back to join project

the user selects research area
    [Arguments]  ${Application}
    the user selects Research category from funding  Feasibility studies
    the user fills in the funding information        ${Application}

the user fills in the funding information
    [Arguments]  ${Application}
    the user navigates to Your-finances page   ${Application}
    the user clicks the button/link            link = Your funding
    the user selects the radio button          requestingFunding   true
    the user enters text to a text field       css = [name^="grantClaimPercentage"]  42.34
    the user selects the radio button          otherFunding   false
    the user clicks the button/link            jQuery = button:contains("Mark as complete")
    the user clicks the button/link            link = Your funding
    the user should see the element            jQuery = button:contains("Edit")
    the user has read only view once section is marked complete

the user should see all finance subsections complete
    the user should see the element  css = li:nth-of-type(1) .task-status-complete
    the user should see the element  css = li:nth-of-type(2) .task-status-complete
    the user should see the element  css = li:nth-of-type(3) .task-status-complete
    the user should see the element  css = li:nth-of-type(4) .task-status-complete

the user should see all finance subsections incomplete
    the user should see the element  css = li:nth-of-type(1) .task-status-incomplete
    the user should see the element  css = li:nth-of-type(2) .task-status-incomplete
    the user should see the element  jQuery = h3:contains("Your funding")

Invite a non-existing collaborator
    [Arguments]   ${email}  ${competition_name}
    the user should see the element        jQuery = h1:contains("Application overview")
    the user fills in the inviting steps no edit   ${email}
    logout as user
    newly invited collaborator can create account and sign in   ${email}  ${competition_name}

the user is able to confirm the invite
    [Arguments]  ${email}  ${password}
    the user clicks the button/link                 jQuery = .govuk-button:contains("Continue")
    The guest user inserts user email and password  ${email}  ${password}
    The guest user clicks the log-in button
    ${STATUS}    ${VALUE} =     Run Keyword And Ignore Error Without Screenshots    the user should see the element   jQuery = h1:contains("Confirm your organisation")
    Run Keyword If    '${status}' == 'PASS'   the user clicks the button/link   jQuery = .govuk-button:contains("Confirm and accept invitation")
    Run Keyword If    '${status}' == 'FAIL'  Run keywords    the user should see the element   jQuery = h1:contains("Your organisation")
    ...                               AND     the user clicks the button/link   jQuery = .govuk-button:contains("Save and continue")

Newly invited collaborator can create account and sign in
    [Arguments]    ${email}  ${competition_name}
    the user reads his email and clicks the link   ${email}  Invitation to collaborate in ${competition_name}  You will be joining as part of the organisation  2
    the user clicks the button/link    jQuery = a:contains("Yes, accept invitation")
    the user should see the element    jquery = h1:contains("Choose your organisation type")
    the user completes the new account creation  ${email}  ${PUBLIC_SECTOR_TYPE_ID}

the user completes the new account creation
    [Arguments]    ${email}  ${organisationType}
    the user selects the radio button           organisationTypeId    radio-${organisationType}
    the user clicks the button/link             jQuery = .govuk-button:contains("Save and continue")
    the user should see the element             jQuery = h1:contains("Enter organisation details")
    the user selects his organisation in Companies House  innovate  INNOVATE LTD
    the user should be redirected to the correct page    ${SERVER}/registration/register
    the invited user fills the create account form       liam  smithson
    the user should see the element                      jQuery = h1:contains("Please verify your email address")
    the user reads his email and clicks the link   ${email}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    the user clicks the button/link             link = Sign in
    Logging in and Error Checking               ${email}  ${correct_password}

the applicant adds some content and marks this section as complete
    Set Focus To Element      css = .textarea-wrapped .editor
    Input Text    css = .textarea-wrapped .editor    This is some random text
    the user clicks the button/link    name = complete
    the user should see the element    name = edit

the applicant edits the "economic benefit" question
    the user clicks the button/link    name = edit
    the user should see the element    name = complete

logged in user applies to competition
    [Arguments]  ${competition}  ${applicationType}
    the user select the competition and starts application    ${competition}
    the user clicks the button/link                           jQuery = button:contains("Save and continue")

the user select the competition and starts application
    [Arguments]  ${competition}
    the user navigates to the page                            ${frontDoor}
    the user clicks the button/link in the paginated list     link = ${competition}
    the user clicks the button/link                           link = Start new application

the user search for organisation name on Companies house
    [Arguments]  ${org}  ${orgName}
    the user enters text to a text field       id = organisationSearchName    ${org}
    the user clicks the button/link            id = org-search
    the user clicks the button/link            link = ${orgName}
    the user clicks the button/link            jQuery = button:contains("Save and continue")

logged in user applies to competition research
    [Arguments]  ${competition}  ${applicationType}
    the user select the competition and starts application   ${competition}
    the user clicks the button/link     link = Apply with a different organisation
    the user selects the radio button   organisationTypeId  ${applicationType}
    the user clicks the button/link     jQuery = button:contains("Save and continue")
    the user search for organisation name on Companies house    Bath  Bath Spa University

logged in user applies to competition public
    [Arguments]  ${competition}  ${applicationType}
    the user select the competition and starts application   ${competition}
    the user clicks the button/link     link = Apply with a different organisation
    the user selects the radio button   organisationTypeId  ${applicationType}
    the user clicks the button/link     jQuery = button:contains("Save and continue")
    the user search for organisation name on Companies house    Innovate  INNOVATE LTD

the applicant submits the application
    the user clicks the button/link                    link = Review and submit
    the user should not see the element                jQuery = .task-status-incomplete
    the user clicks the button/link                    jQuery = .govuk-button:contains("Submit application")
    the user clicks the button/link                    jQuery = .govuk-button:contains("Yes, I want to submit my application")
    the user should be redirected to the correct page  track

the user applies to competition and enters organisation type
    [Arguments]  ${compId}  ${organisationType}
    the user navigates to the page     ${server}/competition/${compId}/overview
    the user fills in the address info   2

the user applies to competition and enters organisation type link
    [Arguments]  ${compId}  ${organisationType}
    the user navigates to the page      ${server}/competition/${compId}/overview
    the user clicks the button/link     link = Start new application
    The user clicks the button/link     link = Continue and create an account
    the user selects the radio button   organisationTypeId  ${organisationType}
    the user clicks the button/link     jQuery = button:contains("Save and continue")
    the user clicks the Not on companies house link
    the user clicks the button/link     jQuery = button:contains("Save and continue")

the user selects his organisation in Companies House
    [Arguments]  ${search}  ${link}
    the user enters text to a text field  id = organisationSearchName  ${search}
    the user clicks the button/link       id = org-search
    the user clicks the button/link       link = ${link}
    the user clicks the button/link       jQuery = button:contains("Save and continue")

the applicant completes Application Team
    the user clicks the button/link  link = Application team
    the user clicks the button/link  id = application-question-complete
    the user clicks the button/link  link = Application overview
    the user should see the element  jQuery = li:contains("Application team") > .task-status-complete

the user clicks the Not on companies house link
    the user clicks the button/link       jQuery = span:contains("Enter details manually")
    The user enters text to a text field  name = organisationName    org2
    the user clicks the button/link       jQuery = button:contains("Continue")

the user fills in the address info
   [Arguments]  ${organisationType}
   the user clicks the button/link        jQuery = a:contains("Start new application")
   the user clicks the button/link        jQuery = a:contains("Continue and create an account")
   the user selects the radio button      organisationTypeId  ${organisationType}
   the user clicks the button/link        jQuery = button:contains("Save and continue")

the user marks your funding section as complete
    the user selects the radio button     requestingFunding   true
    the user enters text to a text field  css = [name^="grantClaimPercentage"]  32.12
    the user selects the radio button     otherFunding  false
    the user clicks the button/link       jQuery = button:contains("Mark as complete")

the user selects medium organisation size
    the user selects the radio button  organisationSize  ${MEDIUM_ORGANISATION_SIZE}
    the user selects the radio button  organisationSize  ${MEDIUM_ORGANISATION_SIZE}

the user accept the competition terms and conditions
    [Arguments]  ${retrunLink}
    the user clicks the button/link    link = Award terms and conditions
    the user selects the checkbox      agreed
    the user clicks the button/link    jQuery = button:contains("Agree and continue")
    the user should see the element    jQuery = .form-footer:contains("Terms and conditions accepted")
    the user clicks the button/link    link = ${retrunLink}

the user accept the procurement terms and conditions
    the user clicks the button/link    link = Award terms and conditions
    the user clicks the button/link    link = View full terms and conditions
    the user goes back to the previous page
    the user selects the checkbox      agreed
    the user clicks the button/link    jQuery = button:contains("Agree and continue")
    the user should see the element    jQuery = .form-footer:contains("Terms and conditions accepted")
    the user clicks the button/link    link = Return to application overview

the internal user should see read only view of terms and conditions
    [Arguments]  ${url}  ${applicationid}  ${heading}
    the user navigates to the page             ${url}
    the user clicks the button/link            link = ${applicationid}
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element   jQuery = button:contains("Award terms and conditions")[aria-expanded="false"]
    run keyword if  '${status}'=='PASS'  the user clicks the button/link     jQuery = button:contains("Award terms and conditions")[aria-expanded="false"]
    the user clicks the button/link            link = View terms and conditions
    the user should see the element            jQuery = h1:contains("${heading}")
    the user should not see the element        jQuery = button:contains("Agree and continue")

the user adds a partner organisation
    [Arguments]  ${orgName}  ${name}  ${email}
    the user enters text to a text field          id = organisationName    ${orgName}
    the user enters text to a text field          id = name   ${name}
    the user enters text to a text field          id = email  ${email}

team member accepts the invite to join organisation
    [Arguments]  ${email}  ${compName}  ${fName}  ${lName}
    the user reads his email and clicks the link       ${email}    Invitation to contribute in ${compName}    You will be joining as part of the organisation    2
    the user should see the element                    jQuery = h1:contains("Invitation to contribute")
    the user clicks the button/link                    jQuery = .govuk-button:contains("Yes, accept invitation")
    the user should see the element                    jQuery = h1:contains("Confirm your organisation")
    the user should see the element                    link = email the lead applicant
    the user clicks the button/link                    jQuery = .govuk-button:contains("Confirm and continue")
    the invited user fills the create account form     ${fName}  ${lName}
    the user reads his email and clicks the link       ${email}    Please verify your email address    Once verified you can sign into your account
    the user should be redirected to the correct page  ${REGISTRATION_VERIFIED}

partner organisation accepts the invite to collaborate
    [Arguments]  ${email}  ${compName}  ${businessTypeId}
    the user reads his email and clicks the link  ${email}  Invitation to collaborate in ${compName}  You are invited by  2
    The user clicks the button/link               jQuery = a:contains("Yes, accept invitation")
    The user should see the element               jQuery = h1:contains("Choose your organisation type")
    The user completes the new account creation   ${email}  ${businessTypeId}
    The user clicks the button/link               jQuery = .progress-list a:contains("Untitled application (start here)")
    The user should not see an error in the page

the applicant marks EDI question as complete
    the user clicks the button/link     link = Equality, diversity and inclusion
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  page should contain element  css = button[name=edit]
    Run Keyword If  '${status}' == 'PASS'  the user clicks the button/link  css = button[name=edit]  # the Edit link
    the user clicks the button/link     jQuery = label:contains("Yes")
    the user clicks the button/link     id = application-question-complete
    the user clicks the button/link     link = Back to application overview
    the user should see the element     jQuery = li:contains("Equality, diversity and inclusion") > .task-status-complete