*** Settings ***
Documentation     INFUND-524 As an applicant I want to see the finance summary updated and recalculated as each partner adds their finances.
Suite Setup       The guest user opens the browser
Suite Teardown    User closes the browser
Default Tags      Autosave    Calculations    Finance    Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***

The lead applicant can add collaborators and finances
    [Documentation]     INFUND-524
    [Tags]  Finance    Finance Section   Collaboration  Failing
    Given the user logs in as lead applicant
    When the applicant goes to the Finance section
    Then the applicant enters a valid set of financial data
    And the applicant sees that this financial data has been entered
    And the applicant logs out

The first collaborator can add finances
    [Documentation]     INFUND-524
    [Tags]  Finance    Finance Section   Collaboration  Failing
    Given the user logs in as first collaborator
    And the collaborator goes to the Finance section
    When the collaborator enters a valid set of financial data
    Then the collaborator sees that this financial data has been entered
    And the collaborator sees the lead applicant's financial data
    And the collaborator logs out

The second collaborator can see the finances, but leaves the fields empty
    [Documentation]     INFUND-524
    [Tags]  Finance    Finance Section   Collaboration  Failing
    Given the user logs in as second collaborator
    When The collaborator goes to the Finance section
    Then the collaborator sees the option to enter financial data
    And the collaborator logs out

The lead applicant can see all of the finances of the collaborators
    [Documentation]     INFUND-524
    [Tags]  Finance    Finance Section   Collaboration  Failing
    Given the user logs in as lead applicant
    When the applicant goes to the finance section
    Then the details are correct for the lead applicant's financial data
    And the details are correct for the first collaborator's financial data
    And the second collaborator shows up correctly even with no financial data




*** Keywords ***


The applicant goes to the Finance section
    go to   ${your_finances_url}

The applicant enters a valid set of financial data



The applicant sees that this financial data has been entered
    Page Should Contain     foo bar

The applicant logs out
    Logout as user

The user logs in as first collaborator
    Login as user   &{collaborator1_credentials}

The collaborator goes to the Finance section
    go to       ${your_finances_url}


The collaborator enters a valid set of financial data



The collaborator sees that this financial data has been entered
    Page Should Contain     foo bar

The collaborator sees the lead applicant's financial data
    Page Should Contain     foo bar

The collaborator logs out
    Logout as user

The user logs in as second collaborator
    Login as user   &{collaborator2_credentials}

The collaborator sees the option to enter financial data
    Page Should Contain     Labour
    Make sure here that the fields are ermpty

The user logs in as lead applicant
    Login as user   &{lead_applicant_credentials}

the details are correct for the lead applicant's financial data
    Element Should Contain      foo bar

the details are correct for the first collaborator's financial data
    Element Should Contain      foo bar

the second collaborator shows up correctly even with no financial data
    Element Should Contain      foo bar


