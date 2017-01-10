*** Settings ***
Resource    ../../../../resources/defaultResources.robot

*** Variables ***

*** Keywords ***
the user should see all the Your-Finances Sections
    the user should see the element  link=Your project costs
    the user should see the element  link=Your organisation
    the user should see the element  link=Your funding

Applicant navigates to the finances of the Robot application
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link   link=Robot test application
    And the user clicks the button/link   link=Your finances
