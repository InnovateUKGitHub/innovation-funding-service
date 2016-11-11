*** Settings ***
Documentation     -INFUND-46: As a lead applicant and I am on the application form on an open application, I can review & submit the application, so I can see an overview of the application and the status of each section.
...               -INFUND-3954 :Applicant shouldn't be able to mark as complete with empty questions
...               -INFUND-1075: As an Applicant I want to see the Application Summary page redesigned so that they meet the agreed style
Suite Setup       log in and create new application if there is not one already
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
All sections present
    [Documentation]    INFUND-193
    ...
    ...    INFUND-1075
    [Tags]    HappyPath
    Given the user navigates to the summary page of the Robot test application
    Then all the sections should be visible

All questions present
    [Documentation]    INFUND-1075
    [Tags]
    Then all the questions should be visible

Clicking the Scope button expands the section
    [Documentation]    INFUND-1075
    [Tags]
    When the user clicks the button/link    jQuery=button:contains("Scope")
    Then the Scope section should be expanded
    [Teardown]    The user clicks the button/link    jQuery=button:contains("Scope")

Edit link navigates to the application form
    [Documentation]    INFUND-193
    [Tags]    HappyPath
    Given the user clicks the button/link    jQuery=button:contains("Project summary")
    When the user clicks the button/link    jQuery=#form-input-11 button:contains("Edit")
    Then the user redirects to the page    Please provide a short summary of your project    Project summary
    And The user enters text to a text field    css=#form-input-11 .editor    Test text 123
    [Teardown]    The user clicks the button/link    jQuery=Button:contains(Save and return to application overview)

Mark as complete possible for questions with text
    [Documentation]    INFUND-3954
    [Tags]    HappyPath
    [Setup]
    Given The user navigates to the summary page of the Robot test application
    When the user clicks the button/link    jQuery=#form-input-11 button:contains("Mark as complete")
    Then the Project summary question should be marked as complete
    And The user should not see the element    jQuery=#form-input-11 button:contains("Mark as complete")
    [Teardown]    When the user clicks the button/link    jQuery=#form-input-11 button:contains("Edit")

Mark as complete not possible for empty questions
    [Documentation]    INFUND-3954
    Given The user navigates to the summary page of the Robot test application
    And the user clicks the button/link    jQuery=button:contains("Technical approach")
    When the user clicks the button/link    jQuery=#form-input-5 button:contains("Mark as complete")
    Then the user should see the element    jQuery=#form-input-5 button:contains("Mark as complete")

Application overview button
    [Documentation]    INFUND-1075
    ...
    ...    INFUND-841
    [Tags]
    When the user clicks the button/link    link=Application Overview
    Then the user redirects to the page    Please provide Innovate UK with information about your project.    Application overview

*** Keywords ***
all the sections should be visible
    the user should see the element    css=.section-overview section:nth-of-type(1)
    the user should see the element    css=.section-overview section:nth-of-type(2)
    the user should see the element    css=.section-overview section:nth-of-type(3)

all the questions should be visible
    [Documentation]    What this test is doing:
    ...
    ...    Checking if there are 3 main sections (Details, Application Questions and Finances) and then counting if the first section has 4 subsections, the second 10 and the third 1.
    the user should see the element    css=.section-overview section:nth-of-type(1) .collapsible:nth-of-type(4)
    the user should see the element    css=.section-overview section:nth-of-type(2) .collapsible:nth-of-type(10)
    the user should see the element    css=.section-overview section:nth-of-type(3) .collapsible:nth-of-type(1)

the Scope section should be expanded
    the user should see the element    css=.section-overview > section:first-child .collapsible:nth-of-type(4) > h3 button[aria-expanded="true"]
    the user should see the element    css=.section-overview > section:first-child .collapsible:nth-of-type(4) > div[aria-hidden="false"]

the Project summary question should be marked as complete
    Element Should Contain    jQuery=button:contains("Project summary")    Complete
