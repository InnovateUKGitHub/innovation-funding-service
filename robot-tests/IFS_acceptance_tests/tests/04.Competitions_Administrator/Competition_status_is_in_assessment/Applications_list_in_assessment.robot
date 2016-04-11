*** Settings ***
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Force Tags          FailingForDev


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
    Empty Directory    download_files

Download File
    [Arguments]    ${COOKIE_VALUE}    ${URL}    ${FILENAME}
    log    ${COOKIE_VALUE}
    Run and Return RC    curl -v --insecure --cookie "${COOKIE_VALUE}" ${URL} > download_files/${/}${FILENAME}

the admin downloads the excel
    ${ALL_COOKIES} =    Get Cookies
    Log    ${ALL_COOKIES}
    Download File    ${ALL_COOKIES}    https://ifs-local-dev/management/competition/1/download    submitted_applications.xls
    sleep    2s

User opens the excel and checks the content
    Open Excel    download_files/submitted_applications.xls
    ${APPLICATION_ID_1}=    read Cell Data by name    Submitted Applications    A2
    Should Be Equal    ${APPLICATION_ID_1}    00000005
    ${APPLICATION_TITLE_1}=    read Cell Data by name    Submitted Applications    B2
    should be equal    ${APPLICATION_TITLE_1}    A new innovative solution
    ${LEAD_ORRGANISATION_1}=    read Cell Data by name    Submitted Applications    C2
    should be equal    ${LEAD_ORRGANISATION_1}    Empire Ltd
    ${FIRST_NAME_1}=    read Cell Data by name    Submitted Applications    D2
    should be equal    ${FIRST_NAME_1}    Steve
    ${LAST_NAME_1}=    read Cell Data by name    Submitted Applications    E2
    should be equal    ${LAST_NAME_1}    Smith
    ${EMAIL_1}=    read Cell Data by name    Submitted Applications    F2
    should be equal    ${EMAIL_1}    steve.smith@empire.com
    ${DURATION_1}=    read Cell Data by name    Submitted Applications    G2
    Should Be Equal As Numbers    ${DURATION_1}    20.0
    ${NUMBER_OF_PARTNERS_1}=    read Cell Data by name    Submitted Applications    H2
    Should Be Equal As Numbers    ${NUMBER_OF_PARTNERS_1}    4.0
    ${SUMMARY_1}=    read Cell Data by name    Submitted Applications    I2
    Should contain    ${SUMMARY_1}    The Project aims to identify, isolate and correct an issue that has hindered progress in this field for a number of years.
    ${TOTAL_COST_1}=    read Cell Data by name    Submitted Applications    J2
    Should Be Equal    ${TOTAL_COST_1}    £398,324.29
    ${FUNDING_1}=    read Cell Data by name    Submitted Applications    K2
    Should Be Equal    ${FUNDING_1}    £8,000.00
