*** Settings ***
Documentation     INFUND-1231: As a collaborator registering my company as Academic, I want to be able to enter full or partial details of the Academic organisation's name so I can select my Academic organisation from a list
Suite Setup       The guest user opens the browser
Suite Teardown    Close browser and delete emails
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Academic organisations search
    [Documentation]    INFUND-1231
    [Tags]  HappyPath
    Given we create a new user                            ${openCompetitionBusinessRTO}  Stuart  Downing  ${test_mailbox_one}+invitedacademics${unique_email_number}@gmail.com  ${BUSINESS_TYPE_ID}
    And logout as user
    Given invite a registered user                       ${test_mailbox_one}+academicinvite${unique_email_number}@gmail.com    ${test_mailbox_one}+inviteacademics${unique_email_number}@gmail.com
    When the user reads his email and clicks the link     ${test_mailbox_one}+inviteacademics${unique_email_number}@gmail.com    Invitation to collaborate in ${openCompetitionBusinessRTO_name}    You will be joining as part of the organisation   2
    And the user clicks the button/link                   jQuery = .govuk-button:contains("Yes, accept invitation")
    When the user selects the radio button                organisationType    2
    And the user clicks the button/link                   jQuery = .govuk-button:contains("Save and continue")
    And the user clicks the button/link                   jQuery = .govuk-button:contains("Search")
    Then the user should see a field error                Please enter an organisation name to search.
    When the user enters text to a text field             id = organisationSearchName    abcd
    And the user clicks the button/link                   jQuery = .govuk-button:contains("Search")
    Then the user should see the element                  jQuery = li p:contains("No results found.")
    When the user enters text to a text field             id = organisationSearchName    !!
    And the user clicks the button/link                   jQuery = .govuk-button:contains("Search")
    Then the user should see the element                  jQuery = li p:contains("No results found.")

Accept invitation as academic
    [Documentation]    INFUND-1166, INFUND-917, INFUND-2450, INFUND-2256
    [Tags]  HappyPath
#    The search results are specific to Research Organisation type
    Given the research user finds org in companies house    Warwick  University of Warwick
    And the invited user fills the create account form  Steven  Gerrard
    And If the user goes to the previous page he should redirect to the login page
    And the user reads his email and clicks the link    ${test_mailbox_one}+inviteacademics${unique_email_number}@gmail.com  Please verify your email address  You have recently set up an account  1
    And the user clicks the button/link                 jQuery = p:contains("Your account has been successfully verified.")~ a:contains("Sign in")
    And Logging in and Error Checking                   ${test_mailbox_one}+inviteacademics${unique_email_number}@gmail.com  ${correct_password}
    When the user clicks the button/link                link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    And the user clicks the button/link                 link = Your project finances
    Then the user should see the element                link = Your project costs
    And the user should not see the element             link = Your organisation
    When the user clicks the button/link                link = Your project costs
    Then the user should not see the element            jQuery = h3:contains("Labour")
    And the user should not see an error in the page

*** Keywords ***
If the user goes to the previous page he should redirect to the login page
    the user goes back to the previous page
    the user should see the element    jQuery = h1:contains("Sign in")
