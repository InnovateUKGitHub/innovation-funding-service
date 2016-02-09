*** Settings ***
Documentation     INFUND-887 : As an applicant I want the option to look up my business organisation's details using Companies House lookup so I don’t have to type it in manually as part of the registration process
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${Valid_Company_Name}    innovate
${Valid_Company_Detail}    05493105 - Incorporated on 28 June 2005
${Valid_Company_Link}    INNOVATE LTD
${Valid_Company_Registration_Number}    05493105
${Invalid_Company_Name}    innoavte
${Invalid_Company_Registration_Number}    64536

*** Test Cases ***
Search using valid company name
    [Documentation]    INFUND-887
    [Tags]    Applicant    Company house
    # seems to have just started failing as of 27/01. Perhaps some change to the company house stuff?
    Given the user is in "Create your account" page
    When the user enters the valid company name in the Search text field
    Then the valid company names matching the search criteria should be displayed

Search using invalid company name
    [Documentation]    INFUND-887
    [Tags]    Applicant    Company house
    Given the user is in "Create your account" page
    When the user enters the invalid company name in the Search text field
    Then the search criteria should not fetch any result

Search using valid registration number
    [Documentation]    INFUND-887
    [Tags]    Applicant    Company house
    # seems to have just started failing as of 27/01. Perhaps some change to the company house stuff?
    Given the user is in "Create your account" page
    When the user enters the valid registration number in the Search text field
    Then the valid company names matching the search criteria should be displayed

search using invalid registration number
    [Documentation]    INFUND-887
    [Tags]    Applicant    Company house
    Given the user is in "Create your account" page
    When the user enters the invalid registration number in the Search text field
    Then the search criteria should not fetch any result

Search for invalid characters
    [Documentation]    INFUND-887
    [Tags]    Applicant    Company house    Pending
    Given the user is in "Create your account" page
    When the applicant inserts invalid characters
    Then the applicant should get a validation error for the company house

*** Keywords ***
the user is in "Create your account" page
    go to    ${SEARCH_COMPANYHOUSE_URL}

the user enters the valid company name in the Search text field
    Input Text    id=org-name    ${Valid_Company_Name}
    Click Element    id=org-search

the valid company names matching the search criteria should be displayed
    Page Should Contain    ${Valid_Company_Detail}
    Click Link    ${Valid_Company_Link}
    Page Should Contain    Business Organisation
    Page Should Contain    Organisation name
    Element Should Contain    css=.form-block p:nth-child(2)    ${Valid_Company_Link}
    Page Should Contain    Registration number
    Page should contain    ${Valid_Company_Registration_Number}
    # Page Should Contain    Address
    # Page Should Contain    M45 8QP

the user enters the invalid company name in the Search text field
    Input Text    id=org-name    ${Invalid_Company_Name}
    Click Element    id=org-search

the search criteria should not fetch any result
    Page Should Contain    Sorry we couldn't find any results within Companies House.

the user enters the valid registration number in the Search text field
    Input Text    id=org-name    ${Valid_Company_Registration_Number}
    Click Element    id=org-search

the user enters the invalid registration number in the Search text field
    Input Text    id=org-name    ${Invalid_Company_Registration_Number}
    Click Element    id=org-search

the applicant inserts invalid characters
    Input Text    id=org-name    {}{}
    Click Element    id=org-search

the applicant should get a validation error for the company house
    Element Should Be Visible    css=.error-message
