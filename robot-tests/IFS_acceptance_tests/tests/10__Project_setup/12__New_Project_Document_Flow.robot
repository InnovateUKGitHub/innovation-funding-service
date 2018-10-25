*** Settings ***
Documentation   IFS-4567
...
#Suite Setup     Assign a project manager
Resource        ../../resources/defaultResources.robot

*** Variables ***
#This is using the Integrated delivery programme - solar vehicles competition
#This is using the Low-friction wheel coatings project

*** Test Cases ***
The internal user is waiting for the external user to upload their documents
    [Documentation]  IFS-4567
    [Tags]
    [Setup]  the user logs-in in new browser    &{internal_finance_credentials}
    Given the user navigates to the page        ${SERVER}/project-setup-management/competition/${WITHDRAWN_PROJECT_COMPETITION}/status/all
    When the user clicks the button/link        jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("Pending")
    Then the user should see the element        jQuery = li:contains("Collaboration agreement") span:contains("No file uploaded")
    And the user should see the element         jQuery = li:contains("Exploitation plan") span:contains("No file uploaded")

The external user cannot see that project documents are in a to be completed state
    [Documentation]  IFS-4567
    [Tags]
    Given log in as a different user            &{successful_released_credentials}
#    When the user navigates to the page         ${SERVER}/project-setup/project/${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}
    When the user navigates to the page         ${SERVER}/project-setup/project/18
    Then the user should not see the element    jQuery = li:contains("Documents") span:contains("To be completed")

The external user cannot upload a file before a Project Manager has been assigned
    [Documentation]  IFS-4567
    [Tags]
    Given the user clicks the button/link    link = Documents
#    Given the user clicks the button/link    jQuery = a:contains("Documents")
    When the user clicks the button/link     jQuery = a:contains("Collaboration agreement")
    Then the user should see the element     jQuery = p:contains("Awaiting upload by the Project Manager")
#    Then the user should not see the element     css = button[=""]

The external user selects a Project Manager
    [Documentation]  IFS-4567
    [Tags]
    Given the user navigates to the page      ${SERVER}/project-setup/project/18/details/project-manager
#    Given the user navigates to the page      ${SERVER}/project-setup/project/${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}/details/project-manager
#    When the user selects the radio button    projectManager1  52
    When the user selects the radio button    52  projectManager1
    Then the user clicks the button/link      id = save

The external user checks that they have unlocked the documents section
    [Documentation]  IFS-4567
    [Tags]
    [Setup]  the user navigates to the page    ${SERVER}/project-setup/project/18
#    [Setup]  the user navigates to the page    ${SERVER}/project-setup/project/${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}
    Given the user should see the element      jQuery = li:contains("Documents") span:contains("To be completed")
    And the user clicks the button/link        link = Documents

The external user sees that both of their documents are incomplete
    [Documentation]  IFS-4567
    [Tags]
    When the user clicks the button/link      jQuery = a:contains("Collaboration agreement")
    Then the user clicks the button/link      jQuery = p:contains("No file currently uploaded.")

The external user is alerted that the upload must be a .pdf file
    [Documentation]  IFS-4567
    [Tags]
    When the user uploads the file          jQuery = label:contains("Upload")  ${text_file}
    Then the user should see the element    jQuery = span:contains("Your upload must be a PDF.")

The external user must upload a file that is under 10mb
    [Documentation]  IFS-4567
    [Tags]
    When the user uploads the file          jQuery = label:contains("Upload")  ${11mb_pdf}
    Then the user should see the element    jQuery = h1:contains("Please upload a smaller file")
    And the user goes back to the previous page

The external user uploads a valid Collaboration Agreement
    [Documentation]  IFS-4567
    [Tags]
    Given the user uploads the file    jQuery = label:contains("Upload")  ${valid_pdf}
#    Then the user should see the element

#The external user uploads an Exploitation Plan


*** Keywords ***
#the user assigns themself as the Project Manager
#    the user navigates to the page       ${SERVER}/project-setup/project/${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}/details
#    the user selects the radio button    projectManager1  52
#    the user clicks the button/link      id = save

