*** Settings ***
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    User closes the browser
Force Tags        FailingForDev
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Excel export
    [Documentation]    INFUND-1987
    [Tags]    HappyPath
    # we need to adjust this test in sprint 8 when the new competition will be ready. For now we are using the download url. And add an extra check to see if we have the correct number of rows
    Given the user navigates to the page    ${COMP_ADMINISTRATOR}
    When the admin downloads the excel
    And user opens the excel and checks the content
    [Teardown]    Empty the download directory

*** Keywords ***
Empty the download directory
    Empty Directory    ${DOWNLOAD_FOLDER}

Download File
    [Arguments]    ${COOKIE_VALUE}    ${URL}    ${FILENAME}
    log    ${COOKIE_VALUE}
    Run and Return RC    curl -v --insecure --cookie "${COOKIE_VALUE}" ${URL} > ${DOWNLOAD_FOLDER}/${/}${FILENAME}

the admin downloads the excel
    ${ALL_COOKIES} =    Get Cookies
    Log    ${ALL_COOKIES}
    Download File    ${ALL_COOKIES}    https://ifs-local-dev/management/competition/1/download    submitted_applications.xlsx
    sleep    2s

User opens the excel and checks the content
    ${Excel1}    Open Excel File    ${DOWNLOAD_FOLDER}/submitted_applications.xlsx
    ${APPLICATION_ID_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    A4
    Should Be Equal    ${APPLICATION_ID_1}    00000005
    ${APPLICATION_TITLE_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    B4
    should be equal    ${APPLICATION_TITLE_1}    A new innovative solution
    ${LEAD_ORRGANISATION_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    C4
    should be equal    ${LEAD_ORRGANISATION_1}    Empire Ltd
    ${FIRST_NAME_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    D4
    should be equal    ${FIRST_NAME_1}    Steve
    ${LAST_NAME_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    E4
    should be equal    ${LAST_NAME_1}    Smith
    ${EMAIL_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    F4
    should be equal    ${EMAIL_1}    steve.smith@empire.com
    ${DURATION_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    G4
    Should Be Equal As Numbers    ${DURATION_1}    20.0
    ${NUMBER_OF_PARTNERS_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    H4
    Should Be Equal As Numbers    ${NUMBER_OF_PARTNERS_1}    4.0
    ${SUMMARY_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    I4
    Should contain    ${SUMMARY_1}    The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.
    ${TOTAL_COST_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    J4
    Should Be Equal    ${TOTAL_COST_1}    £398,324.29
    ${FUNDING_1}=    Get Cell Value By Sheet Name    ${Excel1}    Submitted Applications    K4
    Should Be Equal    ${FUNDING_1}    £8,000.00
