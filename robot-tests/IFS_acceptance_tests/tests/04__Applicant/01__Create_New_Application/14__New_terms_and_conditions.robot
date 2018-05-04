*** Settings ***
Documentation
...               IFS-3093 as an applicant i must accept new ifs site terms and conditions
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot


*** Variables ***
&{michelle_bush}     email=michelle.bush@ooba.example.com    password=Passw0rd
${michelle_bush_id}  ${user_ids['${michelle_bush["email"]}']}


*** Test Cases ***
Validation on new site terms and conditions page
    [Documentation]  IFS-3093
    Given The user logs-in in new browser               &{michelle_bush}
    When The user clicks the button/link                css=button[type="submit"]
    Then the user should see a field and summary error  In order to continue you must agree to the terms and conditions.

User is able to accept new terms and conditions
    [Documentation]  IFS-3093
    Given the user selects the checkbox   agree
    And the user cannot see a validation error in the page
    When the user clicks the button/link  css=button[type="submit"]
    Then the user should see the element  jQuery=h1:contains("Dashboard")

Terms and conditions are not shown them again
    [Documentation]  IFS-3093
    Given Logout as user
    When The user logs-in in new browser  &{michelle_bush}
    Then the user should see the element  jQuery=h1:contains("Dashboard")

*** Keywords ***

Custom suite setup
    Connect to Database  @{database}
    execute sql string  DELETE FROM `${database_name}`.`user_terms_and_conditions` WHERE `user_id`='${michelle_bush_id}' and`terms_and_conditions_id`='6';
