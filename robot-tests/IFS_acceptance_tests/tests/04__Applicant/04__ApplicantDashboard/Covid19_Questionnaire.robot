*** Settings ***
Documentation     IFS-7435 As an applicant I should be able to see the COVID-19 additional funding questionnaire
...
...               IFS-7499 Content changes to screening questions
Suite Setup     Custom suite setup
Suite Teardown  Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${project_change_request_message}     Contact your Innovate UK monitoring officer to discuss a project change request.
${no_support_message}                 We will unfortunately not be able to offer you that form of support
${continuity_grant_message}           We may be able to offer you a continuity grant of up to £250,000 to support your project and reasonable business costs not covered by your grant. We will need you to provide confirmation of your eligibility.
${continuity_loan_message}            We may be able to offer you an innovation continuity loan of between £250,000 and £1,600,000 for a period of up to 7 years, to support project costs not covered by your grant. You will have to repay the loan with interest.

*** Test Cases ***
Applicant goes through the queries to apply for additional funding
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given The user clicks the button/link     link = You may be eligible for additional funding
    When the user clicks the button/link      link = Start now
    Then the user should not see an error in the page

Applicant applying for additional funding is a business or third sector
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user goes to the next query     yes
    Then the user should see the element      link = Change answer
    And the user should not see an error in the page

Applicant applying for additional funding is a business but not an Innovate UK award recipient
    [Documentation]  IFS-7435  IFS-7499
    [Tags]  HappyPath
    Given the user goes to the next query     no
    When the user is able to see other relevant links for award recipients
    Then the user should see the element      link = Start again

Applicant applying for additional funding is a business and an Innovate UK award recipient
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user navigates to the page     ${APPLICANT_ADDITIONAL_FUNDING_QUERIES_URL}/award-recipient
    When the user goes to the next query     yes
    Then the user should see the element     link = Start again

Applicant applying for additional funding is an Innovate UK award recipient and needs extension in project period
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user goes to the next query     yes
    Then the user should see the element      jQuery = p:contains("${project_change_request_message}")

Applicant cannot apply for additional funding if he is an Innovate UK award recipient but does not have any challenge
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user clicks the button/link     link = Back
    When the user does not have any challenge mentioned in the questionnaire
    Then the user is able to see other relevant links for award recipients

Applicant applying for additional funding changes his answers
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user clicks the button/link     jQuery = td:contains("Is your challenge in managing your cashflow") ~ td a
    When the user goes to the next query      yes
    Then the user should see the element      link = Start again
    Then the user should see the element      jQuery = h3:contains("Cashflow")

Applicant applying for additional funding has challenge in meeting a larger funding gap
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user clicks the button/link      jQuery = td:contains("Is your challenge in managing your cashflow") ~ td a
    When the user goes to the next query       no
    And the user goes to the next query        yes
    Then the user should see the element       jQuery = p:contains("${continuity_grant_message}")

Applicant applying for additional funding has challenge in meeting a significant funding gap
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user clicks the button/link      jQuery = td:contains("Is your challenge in meeting a larger funding gap") ~ td a
    When the user goes to the next query       no
    And the user goes to the next query        yes
    Then the user should see the element       jQuery = p:contains("${continuity_loan_message}")

Applicant can start the questionnaire all over again
    [Documentation]  IFS-7435
    [Tags]  HappyPath
    Given the user clicks the button/link      link = Start again
    Then the user should see the element       link = Start now

*** Keywords ***
the user is able to see other relevant links for award recipients
    the user should see the element     link = Visit the Innovate UK EEN (opens in a new window)
    the user should see the element     link = Visit GOV.UK (opens in a new window)
    the user should see the element     link = Visit the British Business Bank (opens in a new window)
    the user should see the element     link = Apply for funding with Innovate UK (opens in a new window)

the user does not have any challenge mentioned in the questionnaire
    the user goes to the next query     no
    the user goes to the next query     no
    the user goes to the next query     no
    the user goes to the next query     no

the user goes to the next query
    [Arguments]  ${radio_button_choice}
    the user selects the radio button   govuk-radios__item  ${radio_button_choice}
    the user clicks the button/link     jQuery = button:contains("Next")

Custom suite setup
    the user logs-in in new browser    &{lead_applicant_credentials}

Custom suite teardown
    The user closes the browser