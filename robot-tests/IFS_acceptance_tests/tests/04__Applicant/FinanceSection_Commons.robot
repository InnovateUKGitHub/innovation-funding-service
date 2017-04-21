*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***
${project_guidance}    https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance

*** Keywords ***
the user should see all the Your-Finances Sections
    the user should see the element  link=Your project costs
    the user should see the element  link=Your organisation
    the user should see the element  jQuery=h3:contains("Your funding")
    the user should see the element  jQuery=h2:contains("Finance summary")

the user navigates to Your-finances page
    [Arguments]  ${Application}
    the user navigates to the page  ${DASHBOARD_URL}
    the user clicks the button/link  link=${Application}
    the user clicks the button/link  link=Your finances

Applicant navigates to the finances of the Robot application
    the user navigates to Your-finances page  Robot test application

log in and create new application if there is not one already with complete application details
    log in and create new application if there is not one already
    Mark application details as complete

log in and create a new application if there is not one already with complete application details and completed org size section
    log in and create new application if there is not one already
    Mark application details as complete
    Complete the org size section

Complete the org size section
    the user navigates to the page    ${DASHBOARD_URL}
    the user clicks the button/link    link=Robot test application
    the user clicks the button/link    link=Your finances
    the user clicks the button/link    link=Your organisation
    ${orgSizeReadonly}=  Run Keyword And Return Status    Element Should Be Visible   jQuery=button:contains("Edit")
    Run Keyword If    ${orgSizeReadonly}    the user clicks the button/link    jQuery=button:contains("Edit")
    the user selects the radio button    financePosition-organisationSize  ${LARGE_ORGANISATION_SIZE}
    the user enters text to a text field    jQuery=label:contains("Turnover") + input    150
    the user enters text to a text field    jQuery=label:contains("employees") + input    0
    the user moves focus to the element    jQuery=button:contains("Mark as complete")
    run keyword and ignore error without screenshots    the user clicks the button/link    jQuery=button:contains("Mark as complete")
    run keyword and ignore error without screenshots    the user clicks the button/link    link=Your finances

mark application details incomplete the user closes the browser
    Mark application details as incomplete
    the user closes the browser

Mark application details as complete
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=Robot test application
    the applicant completes the application details     Application details

Mark application details as incomplete
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=Robot test application
    the user clicks the button/link       link=Application details
    the user clicks the button/link       jQuery=button:contains("Edit")
    the user clicks the button/link       jQuery=button:contains("Save and return to application overview")
    the user should see the element       jQuery=li:contains("Application details") > .action-required


the Application details are completed
    ${STATUS}    ${VALUE}=  Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery=img.complete[alt*="Application details"]
    Run Keyword If  '${status}' == 'FAIL'  the applicant completes the application details

the applicant completes the application details
    [Arguments]   ${Application_details}
    the user clicks the button/link       link=${Application_details}
    the user clicks the button/link       jQuery=button:contains("research category")
    the user clicks the button/link       jQuery=label[for^="researchCategoryChoice"]:contains("Experimental development")
    the user clicks the button/link       jQuery=label[for^="researchCategoryChoice"]:contains("Experimental development")
    the user clicks the button/link       jQuery=button:contains(Save)
    the user clicks the button/link       jQuery=label[for="application.resubmission-no"]
    the user clicks the button/link       jQuery=label[for="application.resubmission-no"]
    # those Radio buttons need to be clicked twice.
    The user enters text to a text field  id=application_details-startdate_day  18
    The user enters text to a text field  id=application_details-startdate_year  2018
    The user enters text to a text field  id=application_details-startdate_month  11
    The user enters text to a text field  id=application_details-duration  20
    the user clicks the button/link       jQuery=button:contains("Mark as complete")
    the user should see the element       jQuery=button:contains("Edit")
    the user should not see the element     css=input

the user marks the finances as complete
    [Arguments]  ${Application}
    the user fills in the project costs     ${Application}
    the user fills in the organisation information  ${Application}
    the user checks Your Funding section     ${Application}
    the user should see all finance subsections complete
    the user clicks the button/link  link=Application overview
    the user should see the element  jQuery=li:contains("Your finances") > .task-status-complete

the user fills in the project costs
    [Arguments]     ${Application_name}
    the user clicks the button/link  link=Your project costs
    the user fills in Labour
    the user fills in Overhead costs  ${Application_name}
    the user fills in Material
    the user fills in Capital usage
    the user fills in Subcontracting costs
    the user fills in Travel and subsistence
    the user fills in Other costs
    the user selects the checkbox    agree-state-aid-page
    the user clicks the button/link  jQuery=button:contains("Mark as complete")
    the user clicks the button/link  link=Your project costs
    the user should see the element       jQuery=button:contains("Edit")
    the user has read only view once section is marked complete

the user has read only view once section is marked complete
    the user should not see the element   css=input
    the user should see the element     jQuery=button:contains("Edit")
    the user clicks the button/link     jQuery=a:contains("Return to finances")

the user fills in Labour
    the user clicks the button/link            jQuery=#form-input-1085 button:contains("Labour")
    the user should see the element            css=.labour-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input
    the user enters text to a text field       jQuery=input[id$="labourDaysYearly"]    230
    the user should see the element            jQuery=input.form-control[name^=labour-role]:text[value=""]:first
    the user enters text to a text field       jQuery=input.form-control[name^=labour-role]:text[value=""]:first    anotherrole
    the user enters text to a text field       jQuery=input.form-control[name^=labour-gross][value=""]:first    120000
    the user enters text to a text field       jQuery=input.form-control[name^=labour-labour][value=""]:first    100
    the user clicks the button/link            jQuery=#form-input-1085 button:contains("Labour")

the user fills in Overhead costs
    [Arguments]  ${Application_name}
    ${STATUS}  ${VALUE}=  Run Keyword And Ignore Error Without Screenshots  Should Be Equal As Strings  Evolution of the global phosphorus cycle  ${Application_name}
    run keyword if  '${status}'=='PASS'  the user chooses Calculate overheads option
    run keyword if  '${status}'=='FAIL'  the user chooses 20% overheads option

the user chooses Calculate overheads option
    When the user clicks the button/link    jQuery=button:contains("Overhead costs")
    and the user clicks the button/link     jQuery=label:contains("Custom overhead costs")
    then the user should see the element     jQuery=h3:contains("Custom overhead costs")
    and the user enters text to a text field    jQuery=input[name^="overheads-customRate"]   40
    wait for autosave
    and the total overhead costs should reflect rate entered    jQuery=input[name^="overheads-totalCosts"]   £ 28,261

the total overhead costs should reflect rate entered
    [Arguments]    ${ADMIN_TOTAL}    ${ADMIN_VALUE}
    the element should be disabled      jQuery=input[name^="overheads-totalCosts"]
    Textfield Value Should Be    ${ADMIN_TOTAL}    ${ADMIN_VALUE}

the user chooses 20% overheads option
    # overheads option : 20% Labour
    the user clicks the button/link    jQuery=#form-input-1085 button:contains("Overhead costs")
    the user clicks the button/link    css=label[data-target="overhead-default-percentage"]
    the user clicks the button/link    jQuery=#form-input-1085 button:contains("Overhead costs")

the user fills in Material
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Materials")
    the user should see the element       css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field  css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field  css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    the user enters text to a text field  css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Materials")

the user fills in Capital usage
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Capital usage")
    the user enters text to a text field  jQuery=textarea.form-control[name^=capital_usage-description]  some description
    Click Element                         jQuery=label:contains("New")
    the user enters text to a text field  css=.form-finances-capital-usage-depreciation  10
    the user enters text to a text field  css=.form-finances-capital-usage-npv  5000
    the user enters text to a text field  css=.form-finances-capital-usage-residual-value  25
    the user enters text to a text field  css=.form-finances-capital-usage-utilisation   100
    focus                                 jQuery=#section-total-192[readonly]
    the user should see the element       jQuery=#section-total-192[readonly]
    textfield should contain              css=#capital_usage .form-row:nth-of-type(1) [readonly]  £ 4,975
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Capital usage")

the user fills in Subcontracting costs
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Subcontracting costs")
    the user enters text to a text field  css=.form-finances-subcontracting-company  SomeName
    the user enters text to a text field  jQuery=input.form-control[name^=subcontracting-country]  Netherlands
    the user enters text to a text field  jQuery=textarea.form-control[name^=subcontracting-role]  Quality Assurance
    the user enters text to a text field  jQuery=input.form-control[name^=subcontracting-subcontractingCost]  1000
    #focus                                 css=#section-total-193[readonly]  # values will differ with runs. INFUND-8152
    #textfield should contain              css=#section-total-193[readonly]  £ 1,000  # values will differ with runs. INFUND-8152.
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Subcontracting costs")

the user fills in Travel and subsistence
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Travel and subsistence")
    the user enters text to a text field  css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user enters text to a text field  css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field  css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    #focus                                css=#section-total-194[readonly]    # values will differ with runs. INFUND-8152
    #textfield should contain             css=#section-total-194[readonly]    £ 1,000 # values will differ with runs. INFUND-8152.
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Travel and subsistence")

the user fills in Other costs
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Other costs")
    the user removes prev costs if there are any
    the user enters text to a text field  jQuery=textarea.form-control[name^=other_costs-description]  some other costs
    the user enters text to a text field  jQuery=input.form-control[name^=other_costs-otherCost]  50
    the user clicks the button/link       jQuery=#form-input-1085 button:contains("Other costs")

the user removes prev costs if there are any
    ${STATUS}    ${VALUE}=  Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery=table[id="other-costs-table"] tr:contains("Remove")
    Run Keyword If    '${status}' == 'PASS'    the user clicks the button/link  jQuery=table[id="other-costs-table"] tr:contains("Remove")

the user fills in the organisation information
    [Arguments]  ${Application}
    the user navigates to Your-finances page  ${Application}
    the user clicks the button/link    link=Your organisation
    ${STATUS}    ${VALUE}=  Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery=button:contains("Edit")
    Run Keyword If    '${status}' == 'PASS'    the user clicks the button/link  jQuery=button:contains("Edit")
    the user selects the radio button  financePosition-organisationSize  ${SMALL_ORGANISATION_SIZE}
    the user enters text to a text field    jQuery=label:contains("Turnover") + input    150
    the user enters text to a text field    jQuery=label:contains("employees") + input    0
    the user clicks the button/link    jQuery=button:contains("Mark as complete")
    the user clicks the button/link  link=Your organisation
    the user should see the element       jQuery=button:contains("Edit")
    the user has read only view once section is marked complete

the user checks Your Funding section
    [Arguments]  ${Application}
    ${Research_category_selected}=  Run Keyword And Return Status    Element Should Be Visible   link=Your funding
    Run Keyword if   '${Research_category_selected}' == 'False'     the user selects research area       ${Application}
    Run Keyword if   '${Research_category_selected}' == 'True'      the user fills in the funding information      ${Application}

the user selects research area
    [Arguments]  ${Application}
    the applicant completes the application details     application details
    And the user fills in the funding information    ${Application}

the user fills in the funding information
    [Arguments]  ${Application}
    the user navigates to Your-finances page   ${Application}
    the user clicks the button/link       link=Your funding
    the user enters text to a text field  css=#cost-financegrantclaim  45
    click element                         jQuery=label:contains("No")
    the user selects the checkbox         agree-terms-page
    the user clicks the button/link       jQuery=button:contains("Mark as complete")
    the user clicks the button/link  link=Your funding
    the user should see the element       jQuery=button:contains("Edit")
    the user has read only view once section is marked complete

the user should see all finance subsections complete
    the user should see the element  jQuery=li:nth-of-type(1) .task-status-complete
    the user should see the element  jQuery=li:nth-of-type(2) .task-status-complete
    the user should see the element  jQuery=li:nth-of-type(3) .task-status-complete

the user should see all finance subsections incomplete
    the user should see the element  jQuery=li:nth-of-type(1) .action-required
    the user should see the element  jQuery=li:nth-of-type(2) .action-required
    the user should see the element  jQuery=h3:contains("Your funding")

Remove previous rows
    [Arguments]  ${element}
    :FOR    ${i}    IN RANGE  10
    # The sleep of 200 ms is actually for speed, originally the test used "should not see the element" however that made it wait for 10 seconds on every loop. 
    \  sleep    200ms
    \  ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    the user should see the element    ${element}
    \  Log    ${status}
    \  Exit For Loop If  '${status}'=='FAIL'
    \  run keyword if  '${status}'=='PASS'  the user clicks the button/link  ${element}
    \  ${i} =  Set Variable  ${i + 1}
