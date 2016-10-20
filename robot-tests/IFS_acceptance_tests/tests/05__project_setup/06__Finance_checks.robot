*** Settings ***
Documentation     INFUND-5190: As a member of Project Finance I want to view an amended Finance Checks summary page so that I can see the projects and organisations requiring Finance Checks for the Private Beta competition
...
...               INFUND-5193: As a member of Project Finance I want to be able to approve the finance details that have been updated in the Finance Checks so that these details can be used to generate the default spend profile
Suite Setup       Moving La Fromage into project setup
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot

*** Variables ***
${la_fromage_overview}    ${server}/project-setup/project/4

*** Test Cases ***
Project Finance user can see the finance check summary page
    [Documentation]    INFUND-4821
    [Tags]  HappyPath
    [Setup]    Log in as user    project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page          ${server}/project-setup-management/project/4/finance-check
    Then the user should see the element          jQuery=h2:contains("Finance Checks")
    And the user should see the text in the page  Overview
    And the table row has expected values

Status of the Eligibility column (workaround for private beta competition)
    [Documentation]    INFUND-5190
    [Tags]
    Given The user should not see the text in the page    Viability
    And The user should not see the text in the page    Queries raised
    And The user should not see the text in the page    Notes
    When the user should see the element    link=review
    Then the Generate spend profile button should be disabled

Finance details client-side validations
    [Documentation]    INFUND-5193
    [Tags]    Pending


Approve Eligibility: Collaborator partner organisation
    [Documentation]    INFUND-5193
    [Tags]
    When the user clicks the button/link    css=table:nth-child(7) tr:nth-child(1) a
    Then the user fills in project costs
    And the user selects the checkbox    id=costs-reviewed
    Then the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    And the user should see the text in the page    The partner finance eligibility has been approved
    And The user clicks the button/link    link=Finance checks
    Then the user sees the text in the element    css=table:nth-child(7) tr:nth-child(1) a    approved

Approve Eligibility: Academic Partner organisation
    [Documentation]    INFUND-5193
    [Tags]
    When the user clicks the button/link    css=table:nth-child(7) tr:nth-child(2) a
    And the user selects the checkbox    id=costs-reviewed
    Then the user clicks the button/link    jQuery=.button:contains("Approve finances")
    the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    And the user should see the text in the page    The partner finance eligibility has been approved
    And The user clicks the button/link    link=Finance checks
    Then the user sees the text in the element    css=table:nth-child(7) tr:nth-child(2) a    approved

Approve Eligibility: Lead Partner organisation
    [Documentation]    INFUND-5193
    [Tags]
    When the user clicks the button/link    css=table:nth-child(7) tr:nth-child(3) a
    Then the user fills in project costs
    And the user selects the checkbox    id=costs-reviewed
    Then the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    And the user should see the text in the page    The partner finance eligibility has been approved
    And The user clicks the button/link    link=Finance checks
    Then the user sees the text in the element    css=table:nth-child(7) tr:nth-child(3) a    approved
    And The user should see the element    jQuery=.button:contains("Generate Spend Profile")
    [Teardown]  Logout as user

Other internal users do not have access to Finance Checks
    [Documentation]    INFUND-4821
    [Tags]    HappyPath    Pending
    #TODO INFUND-5720
    [Setup]    Log in as user    john.doe@innovateuk.test    Passw0rd
    # This is added to HappyPath because CompAdmin should NOT have access to FC page
    Then the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/4/finance-check    You do not have the necessary permissions for your request
    [Teardown]  Logout as user

*** Keywords ***
the table row has expected values
    #TODO update selectors and values after INFUND-5476 & INFUND-5431
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[2]    3 months
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[3]    £ 10,800
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[4]    £ 360
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[5]    £ 0
    the user sees the text in the element    xpath=//*[@id="content"]/table[1]/tbody/tr/td[6]    3%

Moving La Fromage into project setup
    the project finance user moves La Fromage into project setup if it isn't already
    logout as user
    the users fill out project details

the project finance user moves La Fromage into project setup if it isn't already
    log in as user    project.finance1@innovateuk.test    Passw0rd
    the user navigates to the page    ${server}/management/dashboard/projectSetup
    ${update_comp}    ${value}=    run keyword and ignore error    the user should not see the text in the page    La Fromage
    run keyword if    '${update_comp}' == 'PASS'    the project finance user moves La Fromage into project setup

the project finance user moves La Fromage into project setup
    the user navigates to the page    ${server}/management/competition/3
    the user selects the option from the drop-down menu    Yes    id=fund16
    the user selects the option from the drop-down menu    No    id=fund17
    the user clicks the button/link    jQuery=.button:contains("Notify applicants")
    the user clicks the button/link    name=publish
    the user should see the text in the page    Assessor Feedback
    the user can see the option to upload a file on the page    ${server}/management/competition/3/application/16
    the user uploads the file    ${valid_pdf}
    the user can see the option to upload a file on the page    ${server}/management/competition/3/application/17
    the user uploads the file    ${valid_pdf}
    the user navigates to the page    ${server}/management/competition/3
    the user clicks the button/link    jQuery=.button:contains("Publish assessor feedback")
    the user clicks the button/link    name=publish

the user uploads the file
    [Arguments]    ${upload_filename}
    Choose File    id=assessorFeedback    ${UPLOAD_FOLDER}/${upload_filename}
    Sleep    500ms

the users fill out project details
    When Log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Ludlow
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then Logout as user
    When Log in as user    pete.tom@egg.com    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=EGGS
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    logout as user
    When Log in as user    steve.smith@empire.com    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Cheeseco
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user clicks the button/link    link=Project manager
    And the user selects the radio button    projectManager    projectManager1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user clicks the button/link    link=Project address
    And the user selects the radio button    addressType    REGISTERED
    And the user clicks the button/link    jQuery=.button:contains("Save")
    the user clicks the button/link    jQuery=.button:contains("Submit project details")
    the user clicks the button/link    jQuery=button:contains("Submit")

the Generate spend profile button should be disabled
    Element Should Be Disabled    jQuery=.button:contains("Generate Spend Profile")

the user fills in project costs
    Input Text    name=costs[0].value    £ 8,000
    Input Text    name=costs[1].value    £ 2,000
    Input Text    name=costs[2].value    £ 10,000
    Input Text    name=costs[3].value    £ 10,000
    Input Text    name=costs[4].value    £ 10,000
    Input Text    name=costs[5].value    £ 10,000
    Input Text    name=costs[6].value    £ 10,000
    Focus    id=costs-reviewed
    the user sees the text in the element    css=#content tfoot td    £ 60,000

the Generate spend profile button should be enabled
    Element Should Be Enabled    jQuery=.button:contains("Generate Spend Profile")