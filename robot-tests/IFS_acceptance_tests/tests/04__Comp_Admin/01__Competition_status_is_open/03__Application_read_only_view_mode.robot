*** Settings ***
Documentation     INFUND-2443 Acceptance test: Check that the comp manager cannot edit an application's finances
...
...               INFUND-2304 Read only view mode of applications from the application list page
...               INFUND-6937  As a Competitions team member I want to be able to view Application details throughout the life of the competition
...               INFUND-6938  As a Competitions team member I want to be able to view Project summary throughout the life of the competition
...               INFUND-6939  As a Competitions team member I want to be able to view Public description throughout the life of the competition
...               INFUND-6940  As a Competitions team member I want to be able to view Scope throughout the life of the competition
...               INFUND-6941  As a Competitions team member I want to be able to view Finances throughout the life of the competition
...               INFUND-6792  As a Competitions team member I want to be able to view Eligibility throughout the life of the competition
...               INFUND-7083  As a Competitions team member I want to be able to update PAF number, budget and activity codes throughout the life of the competition

Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${valid_pdf}      testing.pdf
${quarantine_warning}    This file has been found to be unsafe

*** Test Cases ***
Comp admin can open the view mode of the application
    [Documentation]    INFUND-2300
    ...
    ...    INFUND-2304
    ...
    ...    INFUND-2435
    [Tags]    HappyPath
    [Setup]    Run keywords    Guest user log-in    &{lead_applicant_credentials}
    ...    AND    the user can see the option to upload a file on the page    ${technical_approach_url}
    ...    AND    the user uploads the file to the 'technical approach' question    ${valid_pdf}
    Given log in as a different user    &{Comp_admin1_credentials}
    And the user navigates to the page    ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    Then the user should see the element    id=sort-by
    And the user selects the option from the drop-down menu    id    id=sort-by
    When the user clicks the button/link    link=${OPEN_COMPETITION_APPLICATION_1_NUMBER}
    Then the user should be redirected to the correct page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    And the user should see the element    link=Print application
    And the user should see the text in the page    A novel solution to an old problem
    And the user should see the text in the page    ${valid_pdf}
    And the user can view this file without any errors
    # And the user should see the text in the page    ${quarantine_pdf}
    # nad the user cannot see this file but gets a quarantined message

Comp admin should be able to view but not edit the finances for every partner
    [Documentation]    INFUND-2443
    ...    INFUND-2483
    [Tags]    Failing
    Given the user navigates to the page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    When the user clicks the button/link    jQuery=button:contains("Finances Summary")
    Then the user should not see the element    link=your finances
    And the user should see the text in the page    Funding breakdown
    And the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    When Log in as a different user    &{collaborator1_credentials}
    Then the user navigates to the page    ${YOUR_FINANCES_URL}
    And the applicant edits the Subcontracting costs section
    And the user reloads the page
    When Log in as a different user    &{Comp_admin1_credentials}
    And the user navigates to the page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    Then the user should see the correct finances change

Comp admin has read only view of Application details past Open date
    [Documentation]     INFUND-6937, INFUND-6938, INFUND-6939, INFUND-6940 ,INFUND-6941
    [Tags]  Failing
    #TODO when reviewing
    [Setup]     log in as a different user    &{Comp_admin1_credentials}
    Given The user navigates to the page    ${SERVER}/management/competition/setup/11/
    And The user clicks the button/link    link=Application
    Then The user should see the text in the page   Application details
    And The user clicks the button/link     link=Application details
    And the user should see the element    jquery=h1:contains("Application details")
    And The user should not see the element     css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    And The user clicks the button/link     link = Return to application questions
    And The user clicks the button/link     link=Project summary
    And the user should see the element    jquery=h1:contains("Project summary")
    And The user should not see the element     css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    And The user clicks the button/link     link = Return to application questions
    And The user clicks the button/link     link=Public description
    And the user should see the element    jquery=h1:contains("Public description")
    And The user should not see the element     css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    And The user clicks the button/link     link = Return to application questions
    And The user clicks the button/link     link=Scope
    And the user should see the element    jquery=h1:contains("Scope")
    And The user should not see the element     css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    And The user clicks the button/link     link = Return to application questions
    And The user clicks the button/link     link=Finances
    And the user should see the element    jquery=h1:contains("Application finances")
    And The user should not see the element     css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    And The user clicks the button/link     link = Return to application questions

Comp admin has read only view of Eligibility past Open date
    [Documentation]     INFUND-6792
    [Tags]
    Given The user navigates to the page    ${SERVER}/management/competition/setup/11/
    And The user clicks the button/link    link=Eligibility
    And the user should see the element    jquery=h1:contains("Eligibility")
    And The user should not see the element     css = input
    And The user clicks the button/link     link = Return to setup overview

Comp admin actions in Funding Information section past Open date
    [Documentation]     INFUND-7083
    [Tags]
    Given The user navigates to the page    ${SERVER}/management/competition/setup/11/
    And The user clicks the button/link    link=Funding information
    And the user should see the element    jquery=h1:contains("Funding information")
    ANf the user clicks the button/link     jQuery=.button:contains("Edit")
    And The user enters text to a text field    id=funders0.funder    Best Works Test
    And The user clicks the button/link      link=+Add co-funder
    And The user enters text to a text field    id=funders2.funder    InnovateUK
    And The user enters text to a text field    id=2-funderBudget     20000
    And The user enters text to a text field    id= pafNumber    34FAP
    And The user enters text to a text field    id= budgetCode   45BC
    And The user enters text to a text field    id= activityCode  56AC
    And the user should see that the element is disabled   css = input.form-control width-large
    And The user clicks the button/link     jQuery=.button:contains("Done")

comp admin actions in Funding Information section past notifications date
    [Documentation]     INFUND-7083
    [Tags]
    Given The user navigates to the page    ${SERVER}/management/competition/setup/7/
    And The user clicks the button/link    link=Funding information
    And the user should see the element    jquery=h1:contains("Funding information")
    And The user should not see the element     css = input
    And The user should not see the element    jquery=.button:contains("Edit")
    And The user should not see the element    jquery=.button:contains("Done")
    And The user clicks the button/link     link = Return to application questions


*** Keywords ***
the user uploads the file to the 'technical approach' question
    [Arguments]    ${file_name}
    Choose File    name=formInput[14]    ${UPLOAD_FOLDER}/${file_name}


the user can see the option to upload a file on the page
    [Arguments]    ${url}
    The user navigates to the page    ${url}
    the user should see the text in the page    Upload

the user can view this file without any errors
    the user clicks the button/link    link=testing.pdf(7 KB)
    the user should not see an error in the page
    the user goes back to the previous page

the user cannot see this file but gets a quarantined message
    the user clicks the button/link    link=test_quarantine.pdf(7 KB)
    the user should not see an error in the page
    the user should see the text in the page    ${quarantine_warning}

the finance summary calculations should be correct
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(1) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(2) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(4) td:nth-of-type(1)    £${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(1) td:nth-of-type(2)    ${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(2) td:nth-of-type(2)    ${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(3) td:nth-of-type(2)    ${DEFAULT_INDUSTRIAL_GRANT_RATE_WITH_PERCENTAGE}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(4) td:nth-of-type(2)    ${DEFAULT_ACADEMIC_GRANT_RATE_WITH_PERCENTAGE}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(1) td:nth-of-type(3)    £${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(2) td:nth-of-type(3)    £${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(3) td:nth-of-type(3)    £${DEFAULT_INDUSTRIAL_FUNDING_SOUGHT_WITH_COMMAS}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(4) td:nth-of-type(3)    £${DEFAULT_ACADEMIC_FUNDING_SOUGHT_WITH_COMMAS}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(1) td:nth-of-type(5)    £${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(2) td:nth-of-type(5)    £${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(3) td:nth-of-type(5)    £${DEFAULT_INDUSTRIAL_CONTRIBUTION_TO_PROJECT}
    Wait Until Element Contains    css=.finance-summary tbody tr:nth-of-type(4) td:nth-of-type(5)    £${DEFAULT_ACADEMIC_CONTRIBUTION_TO_PROJECT}

the finance Project cost breakdown calculations should be correct
    Wait Until Element Contains    css=.project-cost-breakdown tbody tr:nth-of-type(1) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains    css=.project-cost-breakdown tbody tr:nth-of-type(2) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains    css=.project-cost-breakdown tbody tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS}
    Wait Until Element Contains    css=.project-cost-breakdown tbody tr:nth-of-type(4) td:nth-of-type(1)    £${DEFAULT_ACADEMIC_COSTS_WITH_COMMAS}

the applicant edits the Subcontracting costs section
    the user clicks the button/link    jQuery=button:contains("Subcontracting costs")
    the user clicks the button/link    jQuery=button:contains('Add another subcontractor')
    the user should see the text in the page    Subcontractor name
    The user enters text to a text field    css=#collapsible-4 .form-row:nth-child(2) input[id$=subcontractingCost]    2000
    The user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-name"]    Jackson Ltd
    The user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-country-"]    Romania
    The user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-role"]    Contractor
    Mouse Out    css=input
    focus    css=.app-submit-btn

the user should see the correct finances change
    Wait Until Element Contains    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(1)    £${DEFAULT_INDUSTRIAL_COSTS_WITH_COMMAS_PLUS_2000}
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(6)    £${DEFAULT_SUBCONTRACTING_COSTS_WITH_COMMAS_PLUS_2000}
