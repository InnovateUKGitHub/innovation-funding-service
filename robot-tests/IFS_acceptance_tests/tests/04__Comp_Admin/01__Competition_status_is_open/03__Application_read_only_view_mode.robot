*** Settings ***
Documentation     INFUND-2443 Acceptance test: Check that the comp manager cannot edit an application's finances
...
...               INFUND-2304 Read only view mode of applications from the application list page
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
    When the user clicks the button/link    link=00000001
    Then the user should be redirected to the correct page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    And the user should see the element    link=Print application
    And the user should see the text in the page    A novel solution to an old problem
    And the user should see the text in the page    ${valid_pdf}
    And the user can view this file without any errors
    # And the user should see the text in the page    ${quarantine_pdf}
    # nad the user cannot see this file but gets a quarantined message

Comp admin should not able to view but not edit the finances for every partner
    [Documentation]    INFUND-2443
    ...    INFUND-2483
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
    And the user clicks the button/link    jQuery=button:contains("Finances Summary")
    Then the user should see the correct finances change

*** Keywords ***
the user uploads the file to the 'technical approach' question
    [Arguments]    ${file_name}
    Choose File    name=formInput[14]    ${UPLOAD_FOLDER}/${file_name}
    Sleep    500ms

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
    Wait Until Element Contains    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(1)    £127,059
    Wait Until Element Contains    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(1)    £9,000
    Wait Until Element Contains    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(2)    70%
    Wait Until Element Contains    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(2)    100%
    Wait Until Element Contains    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(3)    £88,941
    Wait Until Element Contains    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(3)    £9,000
    Wait Until Element Contains    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(5)    £38,118

the finance Project cost breakdown calculations should be correct
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(1)    £127,059
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(1)    £9,000

the applicant edits the Subcontracting costs section
    the user clicks the button/link    jQuery=button:contains("Subcontracting costs")
    the user clicks the button/link    jQuery=button:contains('Add another subcontractor')
    the user should see the text in the page    Subcontractor name
    The user enters text to a text field    css=#collapsible-4 .form-row:nth-child(1) input[id$=subcontractingCost]    2000
    The user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-name"]    Jackson Ltd
    The user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-country-"]    Romania
    The user enters text to a text field    css=.form-row:nth-child(1) [name^="subcontracting-role"]    Contractor
    Mouse Out    css=input
    focus    css=.app-submit-btn

the user should see the correct finances change
    Wait Until Element Contains    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(1)    £129,059
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(1)    £129,059
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(6)    £2,000
