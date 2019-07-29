*** Settings ***
Documentation     As an applicant I want to see a readonly view of my application once its no longer editable.
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
All sections present
    [Documentation]    INFUND-193
    ...
    ...    INFUND-1075
    [Tags]
    Given the user navigates to the summary page of the Robot test application
    Then all the sections should be visible

Clicking the Scope button expands the section
    [Documentation]    INFUND-1075
    [Tags]
    When the user clicks the button/link             jQuery = button:contains("Scope")
    Then the Scope section should be expanded

*** Keywords ***
all the sections should be visible
    the user should see the element    css = section:nth-of-type(1)
    the user should see the element    css = section:nth-of-type(2)
    the user should see the element    css = section:nth-of-type(3)

the Scope section should be expanded
    the user should see the element    jQuery = h2:contains("Scope") button[aria-expanded="true"]
    The user clicks the button/link    jQuery = button:contains("Scope")
    the user should see the element    jQuery = h2:contains("Scope") button[aria-expanded="false"]

Custom suite setup
    log in and create new application if there is not one already  Robot test application
    Connect to database  @{database}

Custom suite teardown
    Disconnect from database
    The user closes the browser