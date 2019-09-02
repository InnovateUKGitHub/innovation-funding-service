*** Settings ***
Documentation   IFS-6237 Loans - Application submitted screen
...
...             IFS-6238 Loans - Application submitted email
...
...             IFS-6205 Loans - T&Cs select page
...
...             IFS-6207 Loans - Your Funding - How much funding is required
...
...             IFS-6208 Loans - Updates to Finance Summary Table
Suite Setup     Custom suite setup
Suite Teardown  Custom suite teardown
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot

*** Test Cases ***
Loan application shows correct T&C's
    [Documentation]    IFS-6205
    Given the user clicks the button/link   link = Award terms and conditions
    And the user should see the element     jQuery = h1:contains("Loans terms and conditions")
    When the user clicks the button/link     link = Back to application overview
    Then the user should see the element    jQuery = li:contains("Award terms and conditions") .task-status-complete

Loan application Your funding
    [Documentation]  IFS-6207
    Given the user enters empty funding amount
    When the user enters text to a text field  id = amount   57,803
    And the user clicks the button/link        id = mark-all-as-complete
    Then the user should see the element       jQuery = td:contains("200,903") ~ td:contains("57,803") ~ td:contains("30%") ~ td:contains("2,468") ~ td:contains("140,632")

Loan application finance overview
    [Documentation]  IFS-6208
    Given the user clicks the button/link  link = Back to application overview
    When the user clicks the button/link   link = Finances overview
    Then the user should see the element   jQuery = td:contains("200,903") ~ td:contains("57,803") ~ td:contains("30%") ~ td:contains("2,468") ~ td:contains("140,632")

Loan application submission
    [Documentation]  IFS-6237  IFS-6238
    Given the user submits the loan application
    And the user should see the element   jQuery = h2:contains("Part A: Innovation Funding Service application")
    #the user clicks the button/link           link = startup high growth index survey
    #the user should be on the right page.  Update once we have this link
    When the user clicks the button/link  link = View part A
    Then the user should see the element  jQuery = h1:contains("Application overview")
    And the user reads his email          ${lead_applicant_credentials["email"]}  Complete your application for Loan Competition  To finish your application, you must complete part B

*** Keywords ***
Custom suite setup
    the user logs-in in new browser       &{lead_applicant_credentials}
    the user clicks the button/link       link = Loan Application

Custom suite teardown
    The user closes the browser

the user enters empty funding amount
    the user clicks the button/link                link = Your project finances
    the user clicks the button/link                link = Your funding
    the user clicks the button/link                jQuery = button:contains("Edit your funding")
    the user enters text to a text field           id = amount  ${EMPTY}
    the user clicks the button/link                id = mark-all-as-complete
    the user should see a field and summary error  Enter the amount of funding sought.

the user submits the loan application
    the user clicks the button/link           link = Application overview
    the user clicks the button/link           link = Review and submit
    the user clicks the button/link           id = submit-application-button
    the user clicks the button/link           jQuery = button:contains("Yes, I want to submit my application")