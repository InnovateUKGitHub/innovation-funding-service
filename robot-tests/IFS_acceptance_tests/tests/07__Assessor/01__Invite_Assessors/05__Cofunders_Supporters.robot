*** Settings ***
Documentation  IFS-8414 Internal user - View co funder feedback progress - list view
...
...            IFS-8407 Internal user - View co funder feedback
...
...            IFS-8404 Internal user - Assign Co-funder

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${cofunderApplicationTitle}     KTP application

*** Test Cases ***
The internal user can view a co-funder application by searching with an application number
    [Documentation]  IFS-8414
    [Setup]  the user requesting the application id
    Given Logging in and Error Checking                 &{ifs_admin_user_credentials}
    And the user navigates to the page                  ${server}/management/competition/99/cofunders/view
    When the user enters text to a text field           id=applicationFilter    ${cofunderApplicationID}
    And the user clicks the button/link                 jQuery = button:contains("Filter")
    And the user clicks the button/link                 link = ${cofunderApplicationID}
    Then the user should see the element                jQuery = h1:contains("Application overview")

The ifs admin views the feedback of the application
    [Documentation]   IFS-8407
    Given the user clicks the button/link           link = Back to co-funders
    When the user clicks the button/link            jQuery = td:contains("${cofunderApplicationTitle}") ~ td:contains("View feedback")
    Then the user can view the cofunder review

The comp admin views the feedback of the application
    [Documentation]   IFS-8407
    Given Log in as a different user                &{Comp_admin1_credentials}
    And the user navigates to the page              ${server}/management/competition/99/cofunders/view
    When And the user clicks the button/link        link = ${cofunderApplicationID}
    And the user clicks the button/link             link = Back to co-funders
    And the user clicks the button/link             jQuery = td:contains("${cofunderApplicationTitle}") ~ td:contains("View feedback")
    Then the user can view the cofunder review

The finance manager views the feedback of the application
    [Documentation]   IFS-8407
    Given Log in as a different user                &{internal_finance_credentials}
    And the user navigates to the page              ${server}/management/competition/99/cofunders/view
    When And the user clicks the button/link        link = ${cofunderApplicationID}
    And the user clicks the button/link             link = Back to co-funders
    And the user clicks the button/link             jQuery = td:contains("${cofunderApplicationTitle}") ~ td:contains("View feedback")
    Then the user can view the cofunder review

The comp admin can allocate applications
    [Documentation]   IFS-8404
    Given Log in as a different user            &{Comp_admin1_credentials}
    When the user clicks the button/link        link = KTP cofunding
    And the user clicks the button/link         link = Manage co-funders
    Then the user can allocate applictions

The comp admin can allocate co-funders and search for cofunder by first name and/or last name
    [Documentation]   IFS-8404
    Given the user can allocate cofunders
    When the user searches for cofunder by name      Douglas
    And the user searches for cofunder by name       Alston
    Then the user searches for cofunder by name      Douglas Alston

The comp admin can invite a co-funder to a KTP application
    [Documentation]   IFS-8404
    Given the user can view already assigned co-funders
    Then the user can invite a cofunder to a KTP application

The comp admin can close the assessment and the link to allocate applications is no longer active
    [Documentation]   IFS-8404
    Given the user can close the assessment
    Then the user is no longer able to allocate applications

*** Keywords ***
Custom suite setup
    The guest user opens the browser
    Connect To Database   @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user requesting the application id
    ${cofunderApplicationID} =  get application id by name    ${cofunderApplicationTitle}
    Set Suite Variable  ${cofunderApplicationID}

the user can view the cofunder review
    the user should see the element     jQuery = h1:contains("Co-funder review")
    the user should see the element     jQuery = h2:contains("Accepted")
    the user should see the element     jQuery = h2:contains("Declined")
    the user should see the element     jQuery = h2:contains("Pending review")

the user can allocate applictions
    the user should see the element     jQuery = h1:contains("Manage co-funders")
    the user should see the element     jQuery = h3:contains("Actions")
    the user should see the element     link = Allocate applications
    the user should see the element     link = View feedback
    the user clicks the button/link     link = Allocate applications

the user can allocate cofunders
    the user should see the element     jQuery = h1:contains("Allocate co-funders")
    the user should see the element     jQuery = p:contains("Assign co-funders to applications.")
    the user should see the element     jQuery = h2:contains("Filter applications")
    the user should see the element     jQuery = label:contains("Search by application number")
    the user should see the element     jQuery = th:contains("Application number")
    the user should see the element     jQuery = th:contains("Title")
    the user should see the element     jQuery = th:contains("Knowledge base partner")
    the user should see the element     jQuery = th:contains("Co-funders")
    the user should see the element     jQuery = span:contains("Showing 1 - 20 of 36 results")
    the user clicks the button/link     link = Next
    the user clicks the button/link     jQuery = td:contains("The proton size") ~ td a:contains("Assign")

the user searches for cofunder by name
    [Arguments]   ${name}
    the user should see the element         jQuery = h2:contains("Filter co-funders")
    the user should see the element         jQuery = label:contains("Search for a co-funder by first or last name")
    the user enters text to a text field    id = filter    ${name}
    the user clicks the button/link         jQuery = button:contains("Filter")
    the user should see the element         jQuery = .govuk-table__cell:contains("Douglas Alston")
    the user should not see the element     jQuery = .govuk-table__cell:contains("Keane Connolly")
    the user clicks the button/link         link = Clear all filters
    the user should see the element         jQuery = .govuk-table__cell:contains("Keane Connolly")

Given the user can view already assigned co-funders
    the user should see the element         jQuery = h1:contains(Assign to application)
    the user should see the element         jQuery = h3:contains("Partners")
    the user should see the element         jQuery = h3:contains("Innovation area")
    the user should see the element         jQuery = h3:contains("Assigned co-funders")
    the user should see the element         jQuery = th:contains("Co-funder")
    the user should see the element         jQuery = th:contains("Organisation")
    the user should see the element         jQuery = th:contains("Email")

the user can invite a cofunder to a KTP application
    the user should see the element         jQuery = h3:contains("Available co-funders")
    the user should see the element         jQuery = th:contains("Select co-funder")
    the user should see the element         jQuery = th:contains("Co-funder name")
    the user should see the element         jQuery = span:contains("0 co-funders selected")
    the user should see the element         jQuery = button:contains("Add selected to application")
    the user should see the element         jQuery = [disabled="disabled"]
    the user selects the checkbox           select-all-check
    the user should not see the element     jQuery = [disabled="disabled"]
    the user clicks the button/link         jQuery = button:contains("Add selected to application")
    the user should see the element         jQuery = .govuk-table__cell:contains("Douglas Alston")

the user can close the assessment
    the user clicks the button/link         link = Back to competition
    the user clicks the button/link         jQuery = button:contains("Close assessment")

the user is no longer able to allocate applications
    the user clicks the button/link         link = Manage co-funders
    the user should see the element         jQuery = [aria-disabled="true"]
