*** Settings ***
Documentation     INFUND-5432 As an assessor I want to receive an alert to complete my profile when I log into my dashboard so that I can ensure that it is complete.
Suite Setup       Log in as user    email=paul.plum@gmail.com    password=Passw0rd
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
When the deadline has passed the assessment should not be visible
    [Documentation]    INFUND-1188
    [Tags]    MySQL
    [Setup]    Connect to Database    @{database}
    When The assessment deadline for the Juggling Craziness changes to the past
    And the user reloads the page
    Then The user should not see the element    link=Juggling is fun
    [Teardown]    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='2017-01-28 00:00:00' WHERE `id`='35';

Assessor can see incompleted sections in the dashboard
    [Documentation]    INFUND-5432
    [Tags]
    [Setup]    Log in as a different user    worth.email.test+assessor1@gmail.com    Passw0rd
    Then The user should see the text in the page    Complete your assessor account
    And The user should see the element    link=your skills
    And The user should see the element    link=your declaration of interest
    And The user should see the element    link=your contract
    And The user should not see the element    link=your details

Assessor should not see the section after it is completed
    [Documentation]    INFUND-5432
    [Tags]
    When The user clicks the button/link    link=your skills
    Then The user should see the text in the page    Your skills
    And the assessor fills in skills section
    Then the user should be redirected to the correct page    ${Assessor_competition_dashboard}
    And The user should not see the element    link=your skills
    When The user clicks the button/link    link=your declaration of interest
    Then The user should see the text in the page    Declaration of interest
    And the assessor fills in declaration of interest section
    Then the user should be redirected to the correct page    ${Assessor_competition_dashboard}
    And The user should not see the element    link=your declaration of interest
    When The user clicks the button/link    link=your contract
    Then The user should see the text in the page    Terms of contract
    And the assessor fills in contract section
    Then the user should be redirected to the correct page    ${Assessor_competition_dashboard}
    And The user should not see the element    link=your contract
    And The user should not see the text in the page    Complete your assessor account

*** Keywords ***
the assessor fills in skills section
    the user enters text to a text field    id=skillAreas    assessor skill areas text
    the user selects the radio button    assessorType    BUSINESS
    the user clicks the button/link    jQuery=button:contains("Continue")

the assessor fills in declaration of interest section
    the user enters text to a text field    id=principalEmployer    University
    the user enters text to a text field    id=role    Professor
    the user enters text to a text field    id=professionalAffiliations    Role x at Company y
    the user selects the radio button    hasAppointments    no
    the user selects the radio button    hasFinancialInterests    No
    the user selects the radio button    hasFamilyAffiliations    No
    the user selects the radio button    hasFamilyFinancialInterests    No
    the user selects the checkbox    id=accurateAccount1
    the user clicks the button/link    jQuery=button:contains("Save and continue")

the assessor fills in contract section
    the user selects the checkbox    id=agreesToTerms1
    the user clicks the button/link    jQuery=button:contains("Save and continue")