*** Settings ***
Documentation   IFS-5700 - Create new project team page to manage roles in project setup
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          PS_Common.robot
Resource          ../CommonResource.robot

*** Variables ***

${notifyPortalRegistrantsPage}   ${server}/project/{projectId}/team

*** Test Cases ***
The user is able to access project details page
    Given log

*** Keywords ***
Suite Setup
    The guest user opens the browser

Suite Teardown
    The user closes the browser

