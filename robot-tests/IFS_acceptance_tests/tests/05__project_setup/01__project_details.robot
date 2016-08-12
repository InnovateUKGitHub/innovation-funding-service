*** Settings ***
Documentation     INFUND-2612 As a partner I want to have a overview of where I am in the process and what outstanding tasks I have to complete so that I can understand our project setup steps
...
...
...               INFUND-2613 As a lead partner I need to see an overview of project details for my project so that I can edit the project details in order for Innovate UK to be able to assign an appropriate Monitoring Officer
...
...               INFUND-2614 As a lead partner I need to provide a target start date for the project so that Innovate UK has correct details for my project setup
...
...               INFUND-2620 As a partner I want to provide my organisation's finance contact details so that the correct person is assigned to the role
...
...               INFUND-3382 As a partner I want to be able to view our project details after they have been submitted so that I can use them for reference
Suite Setup       Run Keywords    delete the emails from both test mailboxes
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Resource          ../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***
${project_details_submitted_message}    The project details have been submitted to Innovate UK

*** Test Cases ***
Non-lead partner can see the project setup page
    [Documentation]    INFUND-2612
    [Tags]    HappyPath
    [Setup]    log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    And the user should see the text in the page    The application best riffs has been successful within the Killer Riffs competition
    And the user should see the element    link=View application and feedback
    And the user should see the element    link=View terms and conditions of grant offer
    And the user should see the text in the page    Project details
    And the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Bank details

Non-lead partner can see the application overview
    [Documentation]    INFUND-2612
    [Tags]    HappyPath
    And the user should see the text in the page    Other documents
    When the user clicks the button/link    link=View application and feedback
    Then the user should see the text in the page    Congratulations, your application has been successful
    And the user should see the text in the page    Application questions
    And the user should not see an error in the page
    [Teardown]    logout as user

Lead partner can see the project setup page
    [Documentation]    INFUND-2612
    [Tags]    HappyPath
    [Setup]    log in as user    &{lead_applicant_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    And the user should see the text in the page    The application best riffs has been successful within the Killer Riffs competition
    And the user should see the element    link=View application and feedback
    And the user should see the element    link=View terms and conditions of grant offer
    And the user should see the text in the page    Project details
    And the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Bank details
    And the user should see the text in the page    Other documents

Lead partner can see the application overview
    [Documentation]    INFUND-2612
    [Tags]    HappyPath
    When the user clicks the button/link    link=View application and feedback
    Then the user should see the text in the page    Congratulations, your application has been successful
    And the user should see the text in the page    Application questions
    And the user should not see an error in the page
    [Teardown]    the user goes back to the previous page

Lead partner can see the overview of the project details
    [Documentation]    INFUND-2613
    [Tags]    HappyPath
    When the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Please supply the following details for your project and the team
    And the user should see the element    link=Start date
    And the user should see the element    link=Project address
    And the user should see the element    link=Project manager
    And the user should see the text in the page    Finance contacts

Submit button is disabled if the details are not fully filled out
    [Documentation]    INFUND-3381
    [Tags]
    When the user should see the element    xpath=//span[contains(text(), 'No')]
    Then the submit button should be disabled

Partner nominates a finance contact
    [Documentation]    INFUND-2620
    [Tags]    HappyPath
    [Setup]    Logout as user
    When Log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    Then the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Ludlow
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the matching status checkbox is updated    project-details-finance    1    yes
    Then Logout as user
    When Log in as user    pete.tom@egg.com    Passw0rd
    Then the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=EGGS
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the matching status checkbox is updated    project-details-finance    2    yes
    Then Logout as user
    When Log in as user    steve.smith@empire.com    Passw0rd
    Then the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Vitruvius Stonework Limited
    Then the user should see the text in the page    Finance contact
    And the user selects the radio button    financeContact    financeContact2
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the matching status checkbox is updated    project-details-finance    3    yes

Lead partner can change the Start Date
    [Documentation]    INFUND-2614
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Start date
    And the duration should be visible
    When the user enters text to a text field    id=projectStartDate_year    2013
    Then the user should see a validation error    Please enter a future date
    And the user shouldn't be able to edit the day field as all projects start on the first of the month
    When the user enters text to a text field    id=projectStartDate_month    1
    And the user enters text to a text field    id=projectStartDate_year    2018
    When the user clicks the button/link    jQuery=.button:contains("Save")
    Run Keyword And Ignore Error    When the user clicks the button/link    jQuery=.button:contains("Save")    # Click the button for second time because the focus is still in the date field
    The user redirects to the page    You are providing these details as the lead applicant on behalf of the overall project    Project details
    And the user should see the text in the page    1 Jan 2018
    Then the matching status checkbox is updated    project-details    1    yes
    [Teardown]    the user changes the start date back again

Lead partner can change the project manager
    [Documentation]    INFUND-2616
    ...
    ...    INFUND-2996
    [Tags]    HappyPath
    Given the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    And the user clicks the button/link    link=Project manager
    When the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see a validation error    You need to select a Project Manager before you can continue
    When the user selects the radio button    projectManager    projectManager2
    And the user should not see the text in the page    You need to select a Project Manager before you can continue
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see the text in the page    Steve Smith
    And the user clicks the button/link    link=Project manager
    And the user selects the radio button    projectManager    projectManager1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the user should see the text in the page    test twenty
    And the matching status checkbox is updated    project-details    3    yes

Lead partner can change the project address
    [Documentation]    INFUND-3157
    ...
    ...    INFUND-2165
    [Tags]    HappyPath
    Given the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    And the user clicks the button/link    link=Project address
    When the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see the text in the page    You need to select a project address before you can continue
    When the user selects the radio button    addressType    ADD_NEW
    And the user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    Then the user should see the element    css=#select-address-block
    And the user clicks the button/link    css=#select-address-block > button
    And the address fields should be filled
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user should see the address data
    When the user clicks the button/link    link=Project address
    And the user selects the radio button    addressType    REGISTERED
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see the text in the page    1, Bath, BA1 5LR

Non-lead partner cannot change start date, project manager or project address
    [Tags]
    [Setup]    Logout as user
    Given guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    Then the user should not see the element    link=Start date
    And the user should not see the element    link=Project manager
    And the user should not see the element    link=Project address
    [Teardown]    Logout as user

Project details submission flow
    [Documentation]    INFUND-3381
    [Tags]    HappyPath
    [Setup]    guest user log-in    steve.smith@empire.com    Passw0rd
    Given the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    When all the fields are completed
    And the applicant clicks the submit button and the clicks cancel in the submit modal
    And the user should not see the text in the page    The project details have been submitted to Innovate UK
    Then the applicant clicks the submit button in the modal
    And the user should see the text in the page    The project details have been submitted to Innovate UK
    Then the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(2)
    And the user should see the element    jQuery=ul li.require-action:nth-child(4)

Project details read only after submission
    [Documentation]    INFUND-3381
    [Tags]
    Given the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    Then all the fields are completed
    And The user should not see the element    link=Start date
    And The user should not see the element    link=Project address
    And The user should not see the element    link=Project manager
    And The user should not see the element    link=Ludlow
    And The user should not see the element    link=EGGS
    And The user should not see the element    link=Cheeseco

All partners can view submitted project details
    [Documentation]    INFUND-3382
    [Setup]    the user logs out if they are logged in
    When guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    And the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    Then the user should not see the element    link=Ludlow
    And all the fields are completed
    And the user should see the text in the page    ${project_details_submitted_message}
    Then the user logs out if they are logged in
    When guest user log-in    steve.smith@empire.com    Passw0rd
    And the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    Then the user should not see the element    link=Vitruvius Stonework Limited
    And all the fields are completed
    And the user should see the text in the page    ${project_details_submitted_message}

Non-lead partner cannot change any project details
    [Documentation]    INFUND-2619
    [Setup]    Run Keywords    logout as user
    ...    AND    guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Start date
    And the user should see the text in the page    1 Jan 2017
    And the user should not see the element    link=Start date
    And the user should see the text in the page    Project manager
    And the user should see the text in the page    test twenty
    And the user should not see the element    link=Project manager
    And the user should see the text in the page    Project address
    And the user should see the text in the page    1, Bath, BA1 5LR
    And the user should not see the element    link=Project address
    And the user navigates to the page    ${project_start_date_page}
    And the user should be redirected to the correct page    ${project_in_setup_page}
    And the user navigates to the page    ${project_manager_page}
    And the user should be redirected to the correct page    ${project_in_setup_page}
    And the user navigates to the page    ${project_address_page}
    And the user should be redirected to the correct page    ${project_in_setup_page}

*** Keywords ***
the user should see a validation error
    [Arguments]    ${ERROR1}
    Focus    jQuery=button:contains("Save")
    sleep    300ms
    Then the user should see an error    ${ERROR1}

the matching status checkbox is updated
    [Arguments]    ${table_id}    ${COLUMN}    ${STATUS}
    the user should see the element    ${table_id}
    the user should see the element    jQuery=#${table_id} tr:nth-of-type(${COLUMN}) .${STATUS}

the duration should be visible
    Element Should Contain    xpath=//*[@id="content"]/form/fieldset/div/p[5]/strong    3 months

the user shouldn't be able to edit the day field as all projects start on the first of the month
    the user should see the element    css=.day [readonly]

the user should see the address data
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' != 'NO'    the user should see the valid data
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' == 'NO'    the user should see the dummy data

the user should see the valid data
    the user should see the text in the page    Am Reprographics, Bristol, BS1 4NT

the user should see the dummy data
    the user should see the text in the page    Montrose House 1, Neston, CH64 3RU

the submit button should be disabled
    Element Should Be Disabled    jQuery=.button:contains("Submit project details")

the applicant clicks the submit button and the clicks cancel in the submit modal
    Wait Until Element Is Enabled    jQuery=.button:contains("Submit project details")
    the user clicks the button/link    jQuery=.button:contains("Submit project details")
    the user clicks the button/link    jquery=button:contains("Cancel")

the applicant clicks the submit button in the modal
    Wait Until Element Is Enabled    jQuery=.button:contains("Submit project details")
    the user clicks the button/link    jQuery=.button:contains("Submit project details")
    the user clicks the button/link    jQuery=button:contains("Submit")

all the fields are completed
    the matching status checkbox is updated    project-details    1    yes
    the matching status checkbox is updated    project-details    2    yes
    the matching status checkbox is updated    project-details    3    yes
    the matching status checkbox is updated    project-details-finance    1    yes
    the matching status checkbox is updated    project-details-finance    2    yes
    the matching status checkbox is updated    project-details-finance    3    yes

the user changes the start date back again
    the user clicks the button/link    link=Start date
    the user enters text to a text field    id=projectStartDate_year    2017
    the user clicks the button/link    jQuery=.button:contains("Save")
