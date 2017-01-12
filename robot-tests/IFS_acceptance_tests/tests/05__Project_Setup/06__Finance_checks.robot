*** Settings ***
Documentation     INFUND-5190: As a member of Project Finance I want to view an amended Finance Checks summary page so that I can see the projects and organisations requiring Finance Checks for the Private Beta competition
...
...               INFUND-5193: As a member of Project Finance I want to be able to approve the finance details that have been updated in the Finance Checks so that these details can be used to generate the default spend profile
...
...               INFUND-5220: As a member of Project Finance I want to be able to view project costs for academic organisations so that I can review funding during the Finance Checks for the Private Beta competition
...
...               INFUND-5852:As a Project Finance team member I want a link to create the export of bank details for a competition so that this can be delivered to Finance for entry into the Innovate UK Finance SUN system
...
...               INFUND-6149: mailto link is broken on the internal finance eligibility page
Suite Setup       Moving ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot
Resource          PS_Variables.robot

*** Variables ***
${la_fromage_overview}    ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}

*** Test Cases ***
Project Finance user can see the finance check summary page
    [Documentation]    INFUND-4821, INFUND-5476, INFUND-5507
    [Tags]  HappyPath
    [Setup]    Log in as a different user         lee.bowman@innovateuk.test    Passw0rd
    Given the user navigates to the page          ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    Then the user should see the element          jQuery=table.table-progress
    And the user should see the element          jQuery=h2:contains("Finance checks")
    And the user should see the text in the page  Overview
    And the table row has expected values
    [Teardown]  the user clicks the button/link  link=Competition Dashboard

Project finance approves Viability
    [Documentation]  INFUND-7076
    [Tags]  HappyPath
    When project finance approves Viability for  1
    Then project finance approves Viability for  2
    # TODO some extra validation testing INFUND-7076

Status of the Eligibility column (workaround for private beta competition)
    [Documentation]    INFUND-5190
    [Tags]
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    Then The user should see the text in the page    Viability
    And The user should not see the text in the page    Queries raised
    And The user should not see the text in the page    Notes
    When the user should see the element    link=Review
    Then the user should see that the element is disabled    jQuery=.generate-spend-profile-main-button

Finance checks client-side validations
    [Documentation]    INFUND-5193
    [Tags]    HappyPath
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    And the user clicks the button/link     css=a.eligibility-0
    When the user enters text to a text field    name=costs[0].value    ${Empty}
    Then the user should see an error    Please enter a labour cost
    When the user enters text to a text field    name=costs[1].value    ${Empty}
    Then the user should see an error    Please enter an admin support cost
    When the user enters text to a text field    name=costs[2].value    ${Empty}
    Then the user should see an error    Please enter a materials cost
    When the user enters text to a text field    name=costs[3].value    ${Empty}
    Then the user should see an error    Please enter a capital usage cost
    When the user enters text to a text field    name=costs[4].value    ${Empty}
    Then the user should see an error    Please enter subcontracting cost
    When the user enters text to a text field    name=costs[5].value    ${Empty}
    Then the user should see an error    Please enter a travel and subsistence cost
    When the user enters text to a text field    name=costs[6].value    ${Empty}
    Then the user should see an error    Please enter any other cost
    When the user enters text to a text field    name=costs[0].value    -1
    And the user moves focus to the element    id=costs-reviewed
    Then the user should see an error    This field should be 0 or higher
    And The user should not see the text in the page    Please enter a labour cost


Approve Eligibility: Lead partner organisation
    [Documentation]    INFUND-5193, INFUND-6149
    [Tags]    HappyPath
    Given the user should see the element    xpath=//a[contains(@href,'mailto:worth.email.test+fundsuccess@gmail.com')]
    When the user fills in project costs
    And the user selects the checkbox    id=costs-reviewed
    Then the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    And the user should see the text in the page    The partner finance eligibility has been approved
    And The user clicks the button/link    jQuery=.button:contains("Return to finance checks")    #Check that also the button works
    Then the user sees the text in the element    css=a.eligibility-0    Approved


Approve Eligibility: Collaborator partner organisation
    [Documentation]    INFUND-5193
    [Tags]    HappyPath
    When the user clicks the button/link    css=a.eligibility-1
    When the user fills in project costs
    And the user selects the checkbox    id=costs-reviewed
    Then the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    And the user should see the text in the page    The partner finance eligibility has been approved
    And The user clicks the button/link    link=Finance checks
    Then the user sees the text in the element    css=a.eligibility-1    Approved


Approve Eligibility: Academic partner organisation
    [Documentation]    INFUND-5193
    [Tags]    HappyPath
    When the user clicks the button/link    css=a.eligibility-2
    And the user selects the checkbox    id=costs-reviewed
    Then the user clicks the button/link    jQuery=.button:contains("Approve finances")
    And the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    Then the user should see the text in the page    The partner finance eligibility has been approved
    And The user clicks the button/link    link=Finance checks
    Then the user sees the text in the element    css=a.eligibility-2    Approved
    And The user should see the element    jQuery=.generate-spend-profile-main-button

Project Finance user can view academic Jes form
    [Documentation]     INFUND-5220
    [Tags]    HappyPath
    Given the user navigates to the page    ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check
    # note that we are viewing the file above rather than the same project as the other tests in this suite due to INFUND-6724
    When the user clicks the button/link    css=a.eligibility-2
    Then the user should see the text in the page    Download Je-S form
    When the user clicks the button/link    link=jes-form53.pdf
    Then the user should not see an error in the page
    [Teardown]    the user goes back to the previous page

Project Finance user can export bank details
    [Documentation]    INFUND-5852
    [Tags]    Download
    When the project finance user downloads the bank details
    Then the user opens the excel and checks the content
    [Teardown]    remove the file from the operating system    bank_details.csv

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Tags]      HappyPath
    [Setup]    Log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
#    And the user should not see the element    link = Bank details
#    And the user should not see the element    link = Finance checks
#    And the user should not see the element    link = Spend profile
#    And the user should not see the element    link = Grant offer letter
    # MO link is not added because suite fails when ran independently
    #TODO please update links when working on INFUND-6815

Status updates correctly for internal user's table
     [Documentation]    INFUND-4049,INFUND-5543
     [Tags]      HappyPath
     [Setup]    log in as a different user   &{Comp_admin1_credentials}
     When the user navigates to the page    ${server}/project-setup-management/competition/${FUNDERS_PANEL_COMPETITION}/status
     Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.ok      # Project details
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(2).status.action      # MO
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(3).status.waiting       # Bank details
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(4).status.action     # Finance checks are actionable from the start-workaround for Private beta assessment
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(5).status            # Spend Profile
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(6).status.waiting  # Other Docs
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(7).status          # GOL

Other internal users do not have access to Finance checks
    [Documentation]    INFUND-4821
    [Tags]    HappyPath
    [Setup]    Log in as a different user    john.doe@innovateuk.test    Passw0rd
    # This is added to HappyPath because CompAdmin should NOT have access to FC page
    Then the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check    You do not have the necessary permissions for your request

Other internal users do not have access to bank details export
    [Documentation]    INFUND-5852
    [Tags]
    When the user navigates to the page    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status
    Then the user should not see the element    link=Export all bank details
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/bank-details/export    You do not have the necessary permissions for your request


*** Keywords ***
the table row has expected values
    the user sees the text in the element    jQuery=.table-overview td:nth-child(2)    3 months
    the user sees the text in the element    jQuery=.table-overview td:nth-child(3)    £ 303,006
    the user sees the text in the element    jQuery=.table-overview td:nth-child(4)    £ 87,547
    the user sees the text in the element    jQuery=.table-overview td:nth-child(5)    £ 3,702
    the user sees the text in the element    jQuery=.table-overview td:nth-child(6)    29%

Moving ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup if it isn't already
    the users fill out project details

the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup if it isn't already
    guest user log-in    lee.bowman@innovateuk.test    Passw0rd
    the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    ${update_comp}    ${value}=    run keyword and ignore error    the user should not see the text in the page    ${FUNDERS_PANEL_COMPETITION_NAME}
    run keyword if    '${update_comp}' == 'PASS'    the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup

the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    the user navigates to the page    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/applications
    the user selects the option from the drop-down menu    Yes    id=fund24
    the user selects the option from the drop-down menu    No    id=fund25
    the user clicks the button/link    jQuery=.button:contains("Notify applicants")
    the user clicks the button/link    name=publish
    the user should see the text in the page    Assessor Feedback
    the user can see the option to upload a file on the page    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/application/${FUNDERS_PANEL_APPLICATION_1}
    the user uploads the file    ${valid_pdf}
    the user can see the option to upload a file on the page    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/application/${FUNDERS_PANEL_APPLICATION_2}
    the user uploads the file    ${valid_pdf}
    the user navigates to the page    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/applications
    the user clicks the button/link    jQuery=.button:contains("Publish assessor feedback")
    the user clicks the button/link    name=publish

the user uploads the file
    [Arguments]    ${upload_filename}
    Choose File    id=assessorFeedback    ${UPLOAD_FOLDER}/${upload_filename}
    Sleep    500ms

the users fill out project details
    When Log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Ludlow
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    When Log in as a different user    pete.tom@egg.com    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=EGGS
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    When Log in as a different user    steve.smith@empire.com    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user clicks the button/link    link=Project Manager
    And the user selects the radio button    projectManager    projectManager1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user clicks the button/link    link=Project address
    And the user selects the radio button    addressType    REGISTERED
    And the user clicks the button/link    jQuery=.button:contains("Save")
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=button:contains("Submit")


the user fills in project costs
    Input Text    name=costs[0].value    £ 8,000
    Input Text    name=costs[1].value    £ 2,000
    Input Text    name=costs[2].value    £ 10,000
    Input Text    name=costs[3].value    £ 10,000
    Input Text    name=costs[4].value    £ 10,000
    Input Text    name=costs[5].value    £ 10,000
    Input Text    name=costs[6].value    £ 10,000
    the user moves focus to the element    id=costs-reviewed
    the user sees the text in the element    css=#content tfoot td    £ 60,000
    the user should see that the element is disabled    jQuery=.button:contains("Approve eligible costs")


the project finance user downloads the bank details
    the user downloads the file    lee.bowman@innovateuk.test    Passw0rd    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/bank-details/export    ${DOWNLOAD_FOLDER}/bank_details.csv


the user opens the excel and checks the content
    ${contents}=    read csv file    ${DOWNLOAD_FOLDER}/bank_details.csv
    ${empire_details}=    get from list    ${contents}    1
    ${empire_name}=    get from list    ${empire_details}    0
    should be equal    ${empire_name}    ${empire_ltd_name}
    ${eggs_details}=    get from list    ${contents}    2
    ${eggs_name}=    get from list    ${eggs_details}    0
    should be equal    ${eggs_name}    ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}
    ${ludlow_details}=    get from list    ${contents}    3
    ${ludlow_name}=    get from list    ${ludlow_details}    0
    should be equal    ${ludlow_name}    ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}
    ${application_number}=    get from list    ${empire_details}    1
    should be equal    ${application_number}    ${PROJECT_SETUP_APPLICATION_1_NUMBER}
    ${postcode}=    get from list    ${empire_details}    8
    should be equal    ${postcode}    CH64 3RU
    ${bank_account_name}=    get from list    ${empire_details}    9
    should be equal    ${bank_account_name}    ${empire_ltd_name}
    ${bank_account_number}=    get from list    ${empire_details}    10
    should be equal    ${bank_account_number}    51406795
    ${bank_account_sort_code}=    get from list    ${empire_details}    11
    should be equal    ${bank_account_sort_code}    404745

project finance approves Viability for
    [Arguments]  ${partner}
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    And the user should see the element     jQuery=table.table-progress tr:nth-child(${partner}) td:nth-child(2) a:contains("Review")
    When the user clicks the button/link    jQuery=table.table-progress tr:nth-child(${partner}) td:nth-child(2) a:contains("Review")
    Then the user should see the element    jQuery=h2:contains("Credit report")
    And the user selects the checkbox       id=costs-reviewed
    When the user should see the element    jQuery=h2:contains("Approve viability")
    Then the user selects the checkbox      id=project-viable
    And the user moves focus to the element  link=Contact us
    When the user selects the option from the drop-down menu  Green  id=rag-rating
    And the user clicks the button/link    jQuery=.button:contains("Confirm viability")
    When the user clicks the button/link    xpath=//*[@id="content"]/form/div[4]/div[2]/button  # Couldn't catch it othewise. TODO INFUND-4820






















