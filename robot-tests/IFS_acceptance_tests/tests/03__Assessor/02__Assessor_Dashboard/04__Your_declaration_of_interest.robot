*** Settings ***
Documentation     INFUND-3715 - As an Assessor I need to declare any conflicts of interest so that Innovate UK does not assign me assessments that are inappropriate for me.
...
...               INFUND-5432 As an assessor I want to receive an alert to complete my profile when I log into my dashboard so that I can ensure that it is complete.
Suite Setup       guest user log-in    worth.email.test+assessor1@gmail.com    Passw0rd
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Back to the dashboard link
    [Documentation]    INFUND-3715
    ...
    ...    INFUND-5432
    Given The user should see the element    link=your declaration of interest    #his checks the alert message on the top od the page
    When the user clicks the button/link    jQuery=a:contains("Your declaration of interest")
    And the user clicks the button/link    jQuery=a:contains("Back to your assessor dashboard")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}

Server-side validations when No selected at yes/no
    [Documentation]    INFUND-3715
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains("Your declaration of interest")
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see a field and summary error    Please enter a principal employer
    And the user should see a field and summary error    Please enter the role at your principal employer
    And the user should see a field and summary error    In order to register an account you have to agree that this is an accurate account
    And the user should see a field and summary error    Please tell us if you have any appointments, directorships or consultancies
    And the user should see a field and summary error    Please tell us if you have any other financial interests
    And the user should see a field and summary error    Please tell us if any of your close family members have any appointments, directorships or consultancies
    And the user should see a field and summary error    Please tell us if any of your close family members have any other financial interests

Server-side when Yes selected at yes/no
    [Documentation]    INFUND-3715
    [Tags]
    Given the user selects the radio button    hasAppointments    yes
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see a field and summary error    Please enter an organisation
    And the user should see a field and summary error    Please enter a position
    And the user selects the radio button    hasAppointments    no
    When the user selects the radio button    hasFinancialInterests    Yes
    And the user selects the radio button    hasFamilyAffiliations    Yes
    And the user selects the radio button    hasFamilyFinancialInterests    Yes
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see a field and summary error    Please enter a relation
    And the user should see a field and summary error    Please enter an organisation
    And the user should see a field and summary error    Please enter a position
    And the user should see a field and summary error    Please enter your family financial interests
    And the user should see a field and summary error    Please enter your financial interests

Client-side validations
    [Documentation]    INFUND-3715
    [Tags]
    #TODO Pending due to INFUND-6186    # the disabled checks should be removed when this is fixed
    When the user correctly fills out the role, principle employer and accurate fields
    #Then the user should not see the validation error    Please enter a relation
    #And the user should not see the validation error    Please enter an organisation
    #And the user should not see the validation error    Please enter a position
    And the user should not see the validation error    Please enter your family financial interests
    #And The user should not see the text in the page    Please enter your financial interests

Successful save for the Declaration form
    [Documentation]    INFUND-3715
    ...
    ...    INFUND-5432
    [Tags]
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}
    And The user should not see the element    link=your declaration of interest    #his checks the alert message on the top od the page
    And the user clicks the button/link    jQuery=a:contains("Your declaration of interest")
    And the user should see the correct inputs in the declaration form

*** Keywords ***
the user correctly fills out the role, principle employer and accurate fields
    the user enters text to a text field    id=principalEmployer    University
    the user enters text to a text field    id=role    Professor
    the user enters text to a text field    id=professionalAffiliations    Role x at Company y
    the user enters text to a text field    id=financialInterests    finance int
    the user enters text to a text field    Id=familyAffiliations0.relation    Relation
    the user enters text to a text field    id=familyAffiliations0.organisation    Innovate
    the user enters text to a text field    id=familyAffiliations0.position    Director
    the user enters text to a text field    id=familyFinancialInterests    My interests
    When the user selects the checkbox    id=accurateAccount1
    focus    jQuery=button:contains("Save and continue")
    sleep    500ms
    Wait For Autosave

the user should see the correct inputs in the declaration form
    Textfield Value Should Be    id=principalEmployer    University
    Textfield Value Should Be    id=role    Professor
    Textarea Value Should Be    id=professionalAffiliations    Role x at Company y
    Textarea Value Should Be    id=financialInterests    finance int
    Textarea Value Should Be    id=familyFinancialInterests    My interests

the user should not see the validation error
    [Arguments]    ${ERROR_TEXT}
    wait until page contains element    jQuery=.error-message
    Wait Until Page Contains    ${ERROR_TEXT}
