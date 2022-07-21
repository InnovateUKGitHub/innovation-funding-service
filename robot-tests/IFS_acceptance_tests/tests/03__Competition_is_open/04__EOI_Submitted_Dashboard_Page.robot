*** Settings ***
Documentation     IFS-12177 Pre-reg/EOI next stage decision - input
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/Applicant_Commons.robot

*** Variables ***
${quarantine_warning}    This file has been found to be unsafe
${openCompetitionPreRegApplicationName}        Horizon Europe Guarantee Eoi Application3
${openCompetitionPreRegApplicationId}          ${application_ids["${openCompetitionPreRegApplicationName}"]}
${openCompetitionPreRegName}                   Horizon Europe Guarantee Pre Registration Competition with EOI Decision
${openCompetitionPreReg}                       ${competition_ids['${openCompetitionPreregName}']}
*** Test Cases ***

Admin can view Expression of Interest
    [Documentation]    IFS-12177
    Given The user logs-in in new browser           &{Comp_admin1_credentials}
    when the user navigates to the page             ${SERVER}/management/competition/${openCompetitionPreReg}
    And the user clicks the button/link             jQuery = a:contains("Applications: All, submitted, expression of interest, ineligible")
    And the user clicks the button/link             link = Expressions of interest
    And the user should see the element             jQuery = h1:contains("Expression of interest")


Filter on application number, sent and Expression of interest decision
    [Documentation]    IFS-12177
    Given the user enters text to a text field                       id = stringFilter    ${openCompetitionPreRegApplicationId}
    And the user selects the option from the drop-down menu          Yes    id= sendFilter
    And the user selects the option from the drop-down menu          Successful    id= fundingFilter
    When the user clicks the button/link                             jQuery = button:contains("Filter")
    Then the user should see the element                             jQuery = td:contains("Horizon Europe Guarantee Eoi Application3")
    And the user should see the element                              jQuery = td:contains("Successful")
    And the user should see the element                              jQuery = td:contains(${openCompetitionPreRegApplicationId})

User clears the filter
    [Documentation]    IFS-12177
    When the user clicks the button/link                              jQuery = a:contains("Clear all filters")
    Then The user should see the text in the element                  stringFilter      ${EMPTY}
    And the user should see the option in the drop-down menu          All   sendFilter
    And the user should see the option in the drop-down menu          Show all  fundingFilter
    And the user should not see the text in the element               fundingFilter  On Hold

Pagination on Expression of interest
    [Documentation]    IFS-12177
    When the user clicks the button/link                             jQuery = span:contains("Next")
    Then the user should see the element                             jQuery = td:contains("Horizon Europe Guarantee Eoi Application21")
    And the user clicks the button/link                              jQuery = span:contains("Previous")
    And the user should see the element                              jQuery = td:contains(${openCompetitionPreRegApplicationId})

Comp admin can view read only view of Expression of interest
    [Documentation]  IFS-12177
    Given the user clicks the button/link                                  link = ${openCompetitionPreRegApplicationId}
    Then the user should see the element                                   jQuery = h1:contains(${openCompetitionPreRegApplicationName})
    Then the user should see the element                                   jQuery = h1:contains("Application overview")

Admin can view Expression of Interest notifications page
    [Documentation]    IFS-12261
    Given The user logs-in in new browser                                 &{Comp_admin1_credentials}
    when the user navigates to the page                                   ${SERVER}/management/competition/${openCompetitionPreReg}/applications/eoi
    When Internal user marks the EOI as successful/unsuccessful           Horizon Europe Guarantee Eoi Application2   EOI_APPROVED
    When Internal user marks the EOI as successful/unsuccessful           Horizon Europe Guarantee Eoi Application4   EOI_REJECTED
    And Internal user marks the EOI as successful/unsuccessful            Horizon Europe Guarantee Eoi Application6   EOI_APPROVED
    And Internal user marks the EOI as successful/unsuccessful            Horizon Europe Guarantee Eoi Application8   EOI_REJECTED
    And Internal user marks the EOI as successful/unsuccessful            Horizon Europe Guarantee Eoi Application10  EOI_APPROVED
    And Internal user marks the EOI as successful/unsuccessful            Horizon Europe Guarantee Eoi Application12   EOI_REJECTED
    And Internal user marks the EOI as successful/unsuccessful            Horizon Europe Guarantee Eoi Application14   EOI_APPROVED
    And Internal user marks the EOI as successful/unsuccessful            Horizon Europe Guarantee Eoi Application16   EOI_REJECTED
    And Internal user marks the EOI as successful/unsuccessful            Horizon Europe Guarantee Eoi Application18   EOI_APPROVED
    And Internal user marks the EOI as successful/unsuccessful            Horizon Europe Guarantee Eoi Application20  EOI_REJECTED
    And the user clicks the button/link                                   link = Manage notifications
    Then the user should see the element                                  jQuery = h1:contains("Expression of interest notifications")

Filter on application number, sent and Expression of interest decision on Expression of Interest notifications page
    [Documentation]    IFS-12261
    Given the user enters text to a text field                       id = stringFilter    ${openCompetitionPreRegApplicationId}
    And the user selects the option from the drop-down menu          Yes    id= sendFilter
    And the user selects the option from the drop-down menu          Successful    id= fundingFilter
    When the user clicks the button/link                             jQuery = button:contains("Filter")
    Then the user should see the element                             jQuery = td:contains("Horizon Europe Guarantee Eoi Application3")
    And the user should see the element                              jQuery = td:contains("Successful")
    And the user should see the element                              jQuery = td:contains(${openCompetitionPreRegApplicationId})

User clears the filter on Expression of Interest notifications page
    [Documentation]    IFS-12261
    When the user clicks the button/link                              jQuery = a:contains("Clear all filters")
    Then The user should see the text in the element                  stringFilter      ${EMPTY}
    And the user should see the option in the drop-down menu          All   sendFilter
    And the user should see the option in the drop-down menu          Show all  fundingFilter
    And the user should not see the text in the element               fundingFilter  On Hold

Pagination Expression of Interest notifications page
    [Documentation]    IFS-12261
    When the user clicks the button/link                             jQuery = span:contains("Next")
    Then the user should see the element                             jQuery = td:contains("Horizon Europe Guarantee Eoi Application21")
    And the user clicks the button/link                              jQuery = span:contains("Previous")
    And the user should see the element                              jQuery = td:contains(${openCompetitionPreRegApplicationId})

*** Keywords ***
Internal user marks the EOI as successful/unsuccessful
    [Arguments]  ${applicationName}  ${decision}
    the user clicks the button/link                     jQuery = tr:contains("${applicationName}") label
    the user clicks the button/link                     css = [type="submit"][value="${decision}"]
