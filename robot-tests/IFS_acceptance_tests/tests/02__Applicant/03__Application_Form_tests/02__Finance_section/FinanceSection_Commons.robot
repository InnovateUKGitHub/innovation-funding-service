*** Settings ***
Resource    ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot

*** Variables ***

*** Keywords ***
the user should see all the Your-Finances Sections
    the user should see the element  link=Your project costs
    the user should see the element  link=Your organisation
    the user should see the element  jQuery=h3:contains("Your funding")

Applicant navigates to the finances of the Robot application
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=Robot test application
    And the user clicks the button/link   link=Your finances

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
    the applicant incompletes the application details


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