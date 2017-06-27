*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Keywords ***
Invited guest user log in
    [Arguments]  ${email}  ${password}
    Logging in and Error Checking  ${email}  ${password}
