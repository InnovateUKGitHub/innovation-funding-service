*** Settings ***
Documentation     IFS-8180 Remove Covid support screening question banner

Suite Setup     Custom suite setup
Suite Teardown  Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${covidQuestionnarieBannerLink}     You may be eligible for additional funding

*** Test Cases ***
Applicant should not see covid questionnaire banner in the dashboard
    [Documentation]  IFS-8180
    Given the user clicks the application tile if displayed
    Then The user should not see the element                    link = ${covidQuestionnarieBannerLink}
    And the user should not see an error in the page

The users should not see covid questionnaire banner in search page
    [Documentation]  IFS-8180
    Given Logout as user
    When the user clicks the button/link                 link = Innovation Funding Service
    Then The user should not see the element             link = ${covidQuestionnarieBannerLink}
    And the user should not see an error in the page


*** Keywords ***
Custom suite setup
    the user logs-in in new browser                       &{lead_applicant_credentials}

Custom suite teardown
    The user closes the browser