*** Settings ***
Documentation   IFS-4567
...
...             #TODO The Mandatory documents suite will have to be updated, once there are no competitions using the old 'Other documents' flow.
Resource        ../../resources/defaultResources.robot

*** Variables ***
# This is using the Integrated delivery programme - solar vehicles competition and Low-friction wheel coatings project
${LOW_FRICTION_PROJECT_ID}    ${project_ids['${WITHDRAWN_PROJECT_COMPETITION_NAME_1}']}

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
    When the user navigates to the page         ${SERVER}/project-setup/project/${LOW_FRICTION_PROJECT_ID}
    Then the user should not see the element    jQuery = li:contains("Documents") span:contains("To be completed")

The external user cannot upload a file before a Project Manager has been assigned
    [Documentation]  IFS-4567
    [Tags]
    Given the user clicks the button/link       link = Documents
    When the user clicks the button/link        jQuery = a:contains("Collaboration agreement")
    Then the user should see the element        jQuery = p:contains("Awaiting upload by the Project Manager")
    Then the user should not see the element    name = document

The external user selects a Project Manager
    [Documentation]  IFS-4567
    [Tags]
    Given the user navigates to the page      ${SERVER}/project-setup/project/${LOW_FRICTION_PROJECT_ID}/details/project-manager
    When the user selects the radio button    52  projectManager1
    Then the user clicks the button/link      id = save

The external user checks that they have unlocked the documents section
    [Documentation]  IFS-4567
    [Tags]
    [Setup]  the user navigates to the page    ${SERVER}/project-setup/project/${LOW_FRICTION_PROJECT_ID}
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
    When the user uploads the file          name = document  ${text_file}
    Then the user should see the element    jQuery = span:contains("Your upload must be a PDF.")

The external user uploads a valid Collaboration Agreement
    [Documentation]  IFS-4567
    [Tags]
    Given the user uploads the file         name = document  ${valid_pdf}
    And the user should see the element     jQuery = a:contains("${valid_pdf}")
    And the user clicks the submit buttons
    Then the user should see the element    jQuery = h2:contains("This document has been submitted")
    And the user clicks the button/link     jQuery = a:contains("Return to documents")

The external user uploads an Exploitation Plan
    [Documentation]  IFS-4567
    [Tags]
    Given the user clicks the button/link    jQuery = a:contains("Exploitation plan")
    When the user uploads the file           name = document  ${valid_pdf}
    And the user should see the element      jQuery = a:contains("${valid_pdf}")
    And the user clicks the submit buttons
    Then the user should see the element    jQuery = h2:contains("This document has been submitted")
    And the user clicks the button/link     jQuery = a:contains("Return to documents")

The internal user rejects a document
    [Documentation]  IFS-4567
    [Tags]
    [Setup]  log in as a different user         &{internal_finance_credentials}
    Given the user navigates to the page        ${SERVER}/project-setup-management/project/${LOW_FRICTION_PROJECT_ID}/document/all
    And the user clicks the button/link         jQuery = a:contains("Collaboration agreement")
    When the user selects the radio button      false  radio-review-reject
    And the user enters text to a text field    id = document-reject-reason  rejecting
    And the user clicks the button/link         jQuery = button:contains("Submit")
    And the user clicks the button/link         jQuery = button:contains("Reject document")
    Then the user should see the element        jQuery = p:contains(You have rejected this document.)

The external user must upload their document again as it was rejected
    [Documentation]  IFS-4567
    [Tags]
    [Setup]  log in as a different user         &{successful_released_credentials}
    Given the user navigates to the page        ${SERVER}/project-setup/project/${LOW_FRICTION_PROJECT_ID}/document/all
    And the user clicks the button/link         jQuery = a:contains("Collaboration agreement")
    And the user should see the element         jQuery = h2:contains("We will contact you to discuss this document.")
    And the user should not see the element     css = button[data-js-modal = "modal-configured-partner-document"]
    When the user clicks the button/link        name = deleteDocument
    Then the user uploads the file              name = document  ${valid_pdf}
    And the user clicks the submit buttons

The internal user approves the Collaboration agreement
    [Documentation]  IFS-4567
    [Tags]
    [Setup]  log in as a different user     &{internal_finance_credentials}
    Given the user navigates to the page    ${SERVER}/project-setup-management/project/${LOW_FRICTION_PROJECT_ID}/document/all
    And the user clicks the button/link     jQuery = a:contains("Collaboration agreement")
    When the user approves the document
    Then the user should see the element    jQuery = p:contains("You have approved this document.")

The internal user can click on the uploaded document to view the contents
    [Documentation]  IFS-4567
    [Tags]
    [Setup]  the user clicks the button/link    link = Documents
    Given the user clicks the button/link       jQuery = a:contains("Exploitation plan")
    When the user clicks the button/link         link = ${valid_pdf}
    Then the user should not see an error in the page
    And the user closes the last opened tab

The internal user approves the Exploitation plan
    [Documentation]  IFS-4567
    [Tags]
    When the user approves the document
    Then the user should see the element    jQuery = p:contains("You have approved this document.")

The internal user can see that the Documents section is complete
    [Documentation]  IFS-4567
    [Tags]
    When the user navigates to the page     ${SERVER}/project-setup-management/competition/${WITHDRAWN_PROJECT_COMPETITION}/status/all
    Then the user should see the element    jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("Complete")

The external user can see that their Documents are complete
    [Documentation]  IFS-4567
    [Tags]
    Given log in as a different user        &{successful_released_credentials}
    When the user navigates to the page     ${SERVER}/project-setup/project/${LOW_FRICTION_PROJECT_ID}
    Then the user should see the element    jQuery = li:contains("Documents") span:contains("Completed")

*** Keywords ***
the user clicks the submit buttons
    the user clicks the button/link     css = button[data-js-modal = "modal-configured-partner-document"]
    the user clicks the button/link     css = button[class = "govuk-button"]

the user approves the document
    the user selects the radio button    true  radio-review-approve
    the user clicks the button/link      css = button[id = "submit-button"]
    the user clicks the button/link      id = accept-document
