*** Settings ***
Documentation     INFUND-6794: As an applicant I will be invited to add funding details within the 'Your funding' page of the application
...               INFUND-6895: As a lead applicant I will be advised that changing my 'Research category' after completing 'Funding level' will reset the 'Funding level'
Suite Setup       log in and create a new application if there is not one already with complete application details and completed org size section    # Please note that the file names will change! This is meant to be 03, I will shift the others forward but wanted to find a good time to do it so as not to give others merge conflicts
Suite Teardown    mark application details incomplete the user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../FinanceSection_Commons.robot

*** Test Cases ***
Applicant has options to enter funding level and details of any other funding
    [Documentation]    INFUND-6794
    [Tags]    HappyPath
    When the user clicks the button/link    link=Your funding
    And the user selects the radio button    other_funding-otherPublicFunding-    Yes
    Then the user should see the element    id=cost-financegrantclaim
    And the user should see the element    css=[name*=other_funding-fundingSource]
    And the user should see the element    css=[name*=other_funding-securedDate]
    And the user should see the element    css=[name*=other_funding-fundingAmount]
    And the user should see the radio button in the page    other_funding-otherPublicFunding-

Applicant can see maximum funding size available to them
    [Documentation]    INFUND-6794
    [Tags]    HappyPath    Pending
    # TODO Pending due to INFUND-8091
    When the user should see the text in the page    50% max funding level allowed for an organisation of your size    # note that this text will change

Funding level validations
    [Documentation]    INFUND-6794
    [Tags]
    When the user enters text to a text field    id=cost-financegrantclaim    78
    And the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    Then the user should see the element    jQuery=span.error-message:contains("This field should be 50% or lower.")
    When the user enters text to a text field    id=cost-financegrantclaim    46
    Then the user should not see the element    jQuery=span.error-message:contains("This field should be 70% or lower.")
    And the user should not see the element    jQuery=.error-message

Other funding validations
    [Documentation]    INFUND-6794
    [Tags]
    When the user enters text to a text field    css=[name*=other_funding-securedDate]    20
    And the user enters text to a text field    css=[name*=other_funding-fundingAmount]    txt
    And the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    Then the user should see the text in the page    Invalid secured date
    And the user should see the text in the page    This field cannot be left blank
    When the user enters text to a text field    css=[name*=other_funding-securedDate]    12-2008
    Then the user should not see the text in the page    Please enter a valid date
    When the user enters text to a text field    css=[name*=other_funding-fundingAmount]    20000
    Then the user should not see the text in the page    This field cannot be left blank
    And the user should not see an error in the page
    And the user selects the checkbox    termsAgreed
    And the user clicks the button/link    jQuery=.button:contains("Mark as complete")

If funding is complete. application details has a warning message
    [Documentation]    INFUND-6895
    ...
    ...    INFUND-6823
    [Tags]    HappyPath
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    When the user clicks the button/link    link=Application details
    And the user clicks the button/link    jQuery=button:contains(Edit)
    Then the user should see the text in the page    Research category determines funding

Changing application details sets funding level to incomplete
    [Documentation]    INFUND-6895
    [Tags]    HappyPath
    When the user selects the radio button    application.researchCategoryId    34
    And the user clicks the button/link    name=mark_as_complete
    And applicant navigates to the finances of the robot application
    Then the user should see the element    css=.list-overview .section:nth-of-type(3) .assigned

Funding level has been reset
    [Documentation]    INFUND-6895
    [Tags]    HappyPath
    When the user clicks the button/link    link=Your funding
    Then the user should see the element    jQuery=.button:contains("Mark as complete")
    And the user should not see the text in the element    css=[name*=other_funding-fundingSource]    Lottery funding
    And the user should not see the text in the element    css=[name*=other_funding-securedDate]    12-2008
    And the user should not see the text in the element    css=[name*=other_funding-fundingAmount]    20000

Funding level can be re-entered, and this saves correctly
    [Documentation]    INFUND-6895
    [Tags]    HappyPath
    When the user enters text to a text field    id=cost-financegrantclaim    43
    And the user enters text to a text field    css=[name*=other_funding-fundingSource]    Lottery funding
    And the user enters text to a text field    css=[name*=other_funding-securedDate]    12-2008
    And the user enters text to a text field    css=[name*=other_funding-fundingAmount]    20000
    And the user selects the checkbox    termsAgreed
    And the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    Then the user should not see an error in the page
    And the user should see the element    css=.list-overview .section:nth-of-type(3) .complete
