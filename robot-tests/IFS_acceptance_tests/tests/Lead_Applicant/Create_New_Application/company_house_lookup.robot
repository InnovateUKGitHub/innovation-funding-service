*** Settings ***
Documentation     INFUND-887 : As an applicant I want the option to look up my business organisation's details using Companies House lookup so I don’t have to type it in manually as part of the registration process
Suite Setup        The guest user opens the browser
Suite Teardown     TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
Search using valid company name
    [Documentation]    INFUND-887
    [Tags]    Applicant    Company house
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
    Given the user is in "Create your account" page
    When the user enters the valid registration number in the Search text field
    Then the valid company names matching the search criteria should be displayed

search using invalid registration number
    [Documentation]    INFUND-887
    [Tags]    Applicant    Company house
    Given the user is in "Create your account" page
    When the user enters the invalid registration number in the Search text field
    Then the search criteria should not fetch any result

Search for invalid charachters
    [Documentation]    INFUND-887
    [Tags]    Applicant    Company house    Failing
    # This test fails due to a bug which has been raised in Jira (INFUND-1493). I have tagged this test as failing to
    # keep all our lovely green lights however it doesn't need fixing! Leave until the bug is fixed
    Given the user is in "Create your account" page
    When the applicant inserts invalid charachters
    Then the applicant should get a validation error for the company house

*** Keywords ***
the user is in "Create your account" page
    go to    ${SEARCH_COMPANYHOUSE_URL}

the user enters the valid company name in the Search text field
    Input Text    id=org-name    innovateuk
    Click Element    id=org-search

the valid company names matching the search criteria should be displayed
    Page Should Contain    05063042 - Dissolved on 16 October 2012
    Click Link    INNOVATE UK LIMITED
    Page Should Contain    Business Organisation
    Page Should Contain    Organisation name
    Element Should Contain    css=.form-block p:nth-child(2)    INNOVATE UK LIMITED
    Page Should Contain    Registration number
    Page should contain    05063042
    Page Should Contain    Address
    Page Should Contain    M45 8QP

the user enters the invalid company name in the Search text field
    Input Text    id=org-name    innoavate
    Click Element    id=org-search

the search criteria should not fetch any result
    Page Should Contain    Sorry we couldn't find any results within Companies House.

the user enters the valid registration number in the Search text field
    Input Text    id=org-name    05063042
    Click Element    id=org-search

the user enters the invalid registration number in the Search text field
    Input Text    id=org-name    87645
    Click Element    id=org-search

the applicant inserts invalid charachters
    Input Text    id=org-name    {}{}
    Click Element    id=org-search

the applicant should get a validation error for the company house
    Element Should Be Visible    css=.error-message
