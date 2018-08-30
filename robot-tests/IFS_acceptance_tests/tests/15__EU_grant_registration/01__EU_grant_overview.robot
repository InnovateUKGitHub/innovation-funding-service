*** Settings ***
Documentation    IFS-4231 EU2020 - Create webservice & Landing page
Resource         ../../resources/defaultResources.robot

*** Variables ***
${EU_grant}    ${server}/eu-grant/overview
*** Test Cases ***
User navigate to EU grant registration page
    [Documentation]  IFS-4231
    Given the guest user opens the browser
    When the user navigates to the page     ${EU_grant}
    Then the user should see the element    jQuery = h1:contains("Horizon 2020: UK government underwrite guarantee")
    And the user should see the element     link = Your organisation
    And the user should see the element     link = Contact details
    And the user should see the element     link = Funding details