*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***

*** Keywords ***
the user should see all the Your-Finances Sections
    the user should see the element  link=Your project costs
    the user should see the element  link=Your organisation
    the user should see the element  jQuery=h3:contains("Your funding")
    the user should see the element  jQuery=h2:contains("Finance summary")

the user navigates to his finances page
    [Arguments]  ${Application}
    the user navigates to the page  ${DASHBOARD_URL}
    the user clicks the button/link  link=${Application}
    the user clicks the button/link  link=Your finances

Applicant navigates to the finances of the Robot application
    the user navigates to his finances page  Robot test application

log in and create new application if there is not one already with complete application details
    log in and create new application if there is not one already
    Mark application details as complete

mark application details incomplete the user closes the browser
    Mark application details as incomplete
    the user closes the browser

Mark application details as complete
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=Robot test application
    the applicant completes the application details

Mark application details as incomplete
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=Robot test application
    the user clicks the button/link       link=Application details
    the user clicks the button/link       jQuery=button:contains("Edit")
    the user clicks the button/link       jQuery=button:contains("Save and return to application overview")
    the user should see the element       jQuery=img.assigned[alt*="Application details"]

The applicant enters Org Size and Funding level
    [Arguments]    ${org_size}    ${funding_level}
    Applicant navigates to the finances of the Robot application
    the user clicks the button/link        link=Your organisation
    the user clicks the button/link        jQuery=.button:contains("Edit your organisation")
    the user selects the radio button      financePosition-organisationSize  financePosition-organisationSize-${org_size}
    the user clicks the button/link        jQuery=button:contains("Mark as complete")
    Applicant navigates to the finances of the Robot application
    the user clicks the button/link        link=Your funding
    the user enters text to a text field   css=#cost-financegrantclaim  ${funding_level}
    the user moves focus to the element    jQuery=label[data-target="other-funding-table"]

the Application details are completed
    ${STATUS}    ${VALUE}=  Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery=img.complete[alt*="Application details"]
    Run Keyword If  '${status}' == 'FAIL'  the applicant completes the application details

the applicant completes the application details
    the user clicks the button/link       link=Application details
    the user clicks the button/link       jQuery=label[for^="financePosition"]:contains("Experimental development")
    the user clicks the button/link       jQuery=label[for^="financePosition"]:contains("Experimental development")
    the user clicks the button/link       jQuery=label[for="resubmission-no"]
    the user clicks the button/link       jQuery=label[for="resubmission-no"]
    # those Radio buttons need to be clicked twice.
    Clear Element Text                    id=application_details-startdate_day
    The user enters text to a text field  id=application_details-startdate_day  18
    Clear Element Text                    id=application_details-startdate_year
    The user enters text to a text field  id=application_details-startdate_year  2018
    Clear Element Text                    id=application_details-startdate_month
    The user enters text to a text field  id=application_details-startdate_month  11
    The user enters text to a text field  id=application_details-duration  20
    the user clicks the button/link       jQuery=button:contains("Mark as complete")

the user marks the finances as complete
    Applicant navigates to the finances of the Robot application
    the user fills in the project costs
    the user fills in the organisation information
    the user fills in the funding information  Robot test application
    the user should see all finance subsections complete
    the user clicks the button/link  link=Application Overview
    the user should see the element  jQuery=img.complete[alt*="finances"]

the user fills in the project costs
    the user clicks the button/link  link=Your project costs
    the user fills in Labour
    the user fills in Overhead costs
    the user fills in Material
    the user fills in Capital usage
    the user fills in Subcontracting costs
    the user fills in Travel and subsistence
    the user fills in Other Costs
    the user selects the checkbox    agree-state-aid-page
    the user clicks the button/link  jQuery=button:contains("Mark as complete")

the user fills in Labour
    the user clicks the button/link            jQuery=#form-input-20 button:contains("Labour")
    the user should see the element            css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    the user clears the text from the element  css=[name^="labour-labourDaysYearly"]
    the user enters text to a text field       css=[name^="labour-labourDaysYearly"]    230
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    120000
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
    the user moves focus to the element        jQuery=button:contains('Add another role')
    the user clicks the button/link            jQuery=button:contains('Add another role')
    the user should see the element            css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(2) input    120000
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    100
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(1) input    test
    the user clicks the button/link            jQuery=#form-input-20 button:contains("Labour")

the user fills in Overhead costs
    the user clicks the button/link    jQuery=#form-input-20 button:contains("Overhead costs")
    the user clicks the button/link    css=label[data-target="overhead-default-percentage"]
    the user clicks the button/link    jQuery=#form-input-20 button:contains("Overhead costs")

the user fills in Material
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Materials")
    the user should see the element       css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field  css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field  css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    the user enters text to a text field  css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Materials")

the user fills in Capital usage
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Capital usage")
    the user enters text to a text field  jQuery=textarea.form-control[name^=capital_usage-description]  some description
    Click Element                         jQuery=label:contains("New")
    the user enters text to a text field  css=.form-finances-capital-usage-depreciation  10
    the user enters text to a text field  css=.form-finances-capital-usage-npv  5000
    the user enters text to a text field  css=.form-finances-capital-usage-residual-value  25
    the user enters text to a text field  css=.form-finances-capital-usage-utilisation   100
    focus                                 jQuery=#section-total-12[readonly]
    the user should see the element       jQuery=#section-total-12[readonly]
    textfield should contain              css=#capital_usage .form-row:nth-of-type(1) [readonly]  £ 4,975
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Capital usage")

the user fills in Subcontracting costs
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Subcontracting costs")
    the user enters text to a text field  css=.form-finances-subcontracting-company  SomeName
    the user enters text to a text field  jQuery=input.form-control[name^=subcontracting-country]  Netherlands
    the user enters text to a text field  jQuery=textarea.form-control[name^=subcontracting-role]  Quality Assurance
    the user enters text to a text field  jQuery=input.form-control[name^=subcontracting-subcontractingCost]  1000
    focus                                 css=#section-total-13[readonly]
    textfield should contain              css=#section-total-13[readonly]  £ 1,000
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Subcontracting costs")

the user fills in Travel and subsistence
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Travel and subsistence")
    the user enters text to a text field  css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user enters text to a text field  css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field  css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    focus                                 css=#section-total-14[readonly]
    textfield should contain              css=#section-total-14[readonly]  £ 1,000
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Travel and subsistence")

the user fills in Other Costs
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Other Costs")
    the user removes prev costs if there are any
    the user enters text to a text field  jQuery=textarea.form-control[name^=other_costs-description]  some other costs
    the user enters text to a text field  jQuery=input.form-control[name^=other_costs-otherCost]  50
    focus                                 css=#section-total-15
    #    textfield should contain              css=#section-total-15  £ 50  #This is commented out because the value in the field differs in full run vs run only the suite.
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Other Costs")

the user removes prev costs if there are any
    ${STATUS}    ${VALUE}=  Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery=table[id="other-costs-table"] tr:contains("Remove")
    Run Keyword If    '${status}' == 'PASS'    the user clicks the button/link  jQuery=table[id="other-costs-table"] tr:contains("Remove")

the user fills in the organisation information
    the user clicks the button/link    link=Your organisation
    ${STATUS}    ${VALUE}=  Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery=button:contains("Edit your organisation")
    Run Keyword If    '${status}' == 'PASS'    the user clicks the button/link  jQuery=button:contains("Edit your organisation")
    the user selects the radio button  financePosition-organisationSize  financePosition-organisationSize-SMALL
    the user clicks the button/link    jQuery=button:contains("Mark as complete")

the user fills in the funding information
    [Arguments]  ${Application}
    the user navigates to his finances page  ${Application}
    the user clicks the button/link       link=Your funding
    the user enters text to a text field  css=#cost-financegrantclaim  60
    click element                         jQuery=label:contains("No")
    the user selects the checkbox         agree-terms-page
    the user clicks the button/link       jQuery=button:contains("Mark as complete")

the user should see all finance subsections complete
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(1) img.section-status.complete
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(2) img.section-status.complete
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(3) img.section-status.complete

the user should see all finance subsections incomplete
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(1) img.section-status.assigned
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(2) img.section-status.assigned
    the user should see the element  jQuery=h3:contains("Your funding")