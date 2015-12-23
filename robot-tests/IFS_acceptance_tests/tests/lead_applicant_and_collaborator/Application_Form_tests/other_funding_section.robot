*** Settings ***
Documentation     INFUND-438: As an applicant and I am filling in the finance details I want a fully working Other funding section
Suite Setup       Log in as user    &{lead_applicant_credentials}
Suite Teardown    User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***


${OTHER_FUNDING_SOURCE}     My mate Dave
${OTHER_FUNDING_DATE}       12-2008
${OTHER_FUNDING_AMOUNT}     10000



*** Test Cases ***


Add details for another source of funding and verify that these details have bee autosaved
    [Documentation]     INFUND-438
    [Tags]  Applicant   Application     Finances    Other funding
    Given applicant goes to the 'Your finances' section
    And applicant selects 'Yes' for other funding
    And applicant chooses to add another source of funding
    When applicant can see a new row
    And applicant enters some details into this row
    Then applicant can leave the 'Your finances' page but the details are still saved
#    And applicant selects 'No' for other funding
#    And applicant can see that the 'No' radio button is selected
#    And applicant cannot see the 'other funding' details


*** Keywords ***

Applicant can see that the 'No' radio button is selected
    Radio Button Should Be Set To       other_funding-otherPublicFunding-54     No


Applicant selects 'Yes' for other funding
    Select Radio button                 other_funding-otherPublicFunding-54     Yes


Applicant chooses to add another source of funding
    Click Link      Add another source of funding



Applicant selects 'No' for other funding
    Select Radio button                 other_funding-otherPublicFunding-54     No

Applicant can see a new row
    Element Should Be Visible      id=other-funding-table


Applicant enters some details into this row
    Wait Until Element Is Visible               id=cost-other_funding-55-source
    Input Text   id=cost-other_funding-55-source            ${OTHER_FUNDING_SOURCE}
    Wait Until Element Is Visible               id=cost-other_funding-55-date
    Input Text   id=cost-other_funding-55-date              ${OTHER_FUNDING_DATE}
    Wait Until Element Is Visible               id=cost-other_funding-55-fundingAmount
    Input Text   id=cost-other_funding-55-fundingAmount     ${OTHER_FUNDING_AMOUNT}

Applicant can leave the 'Your finances' page but the details are still saved
    Reload Page
    Wait Until Element Is Visible       id=cost-other_funding-55-source
    Textfield Should Contain         cost-other_funding-55-source           ${OTHER_FUNDING_SOURCE}
    Textfield Should Contain         cost-other_funding-55-date             ${OTHER_FUNDING_DATE}

Applicant cannot see the 'other funding' details
    Page Should Not Contain     ${OTHER_FUNDING_SOURCE}
    Page Should Not Contain     ${OTHER_FUNDING_DATE}
    Page Should Not Contain     ${OTHER_FUNDING_AMOUNT}