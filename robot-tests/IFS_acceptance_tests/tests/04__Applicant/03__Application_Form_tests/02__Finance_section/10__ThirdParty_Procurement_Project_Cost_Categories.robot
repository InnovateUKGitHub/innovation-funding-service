*** Settings ***
Documentation     IFS-10271: Ofgem critical changes - Content changes
...
...               IFS-10307: Ofgem programme: pre-live amendments
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot

*** Variables ***
${ofGemCompetitionName}         OfGem competition
${ofGemCompetitionId}           ${competition_ids["${ofGemCompetitionName}"]}

*** Test Cases ***
Ofgem cost categories guidance
    [Documentation]  IFS-10127
    Given log in as a different user                            &{lead_applicant_credentials}
    And logged in user applies to competition                   ${ofGemCompetitionName}  1
    And the user clicks the button/link                         link = Your project finances
    When the user clicks the button/link                        link = Your project costs
    And the user clicks the button/link                         jQuery = button:contains("Open all")
    Then the user should see correct cost categories guidance

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}
    the comp admin edits the funding information
    update milestone to yesterday                   ${ofGemCompetitionId}  OPEN_DATE

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user should see correct cost categories guidance
    the user clicks the button/link        jQuery = span:contains("Labour costs guidance")
    the user should not see the element    jQuery = p:contains("review the total amount of time and cost of labour before we approve your application.") span:contains("The terms and conditions of the grant include compliance with these points.")
    the user should not see the element    jQuery = li:contains("they won’t have a residual/resale value at the end of your project. If they do you can claim the costs minus this value")
    the user should not see the element    jQuery = p:contains("You can subcontract work if you don’t have the expertise in your project team.")
    the user clicks the button/link        jQuery = span:contains("Subcontracting costs guidance")
    the user should not see the element    jQuery = span:contains("These organisations are not part of your project.")
    the user should not see the element    jQuery = p:contains("Subcontracting is eligible providing it is justified") span:contains("as to why the work cannot be performed by a project partner")
    the user should not see the element    jQuery = span:contains("We will look at the size of this contribution when assessing your eligibility and level of support.")
    the user should not see the element    jQuery = p:contains("Please note that legal or project audit and accountancy fees")
    the user should not see the element    jQuery = p:contains("Where possible you should select a UK based contractor.")
    the user should see the element        jQuery = p:contains("Please provide estimates of other costs that do not fit within any other cost headings.")

the comp admin edits the funding information
    logging in and error checking               &{Comp_admin1_credentials}
    the user navigates to the page              ${server}/management/competition/setup/${ofGemCompetitionId}/section/additional
    the user clicks the button/link             jQuery = button:contains("Edit")
    the user selects option from type ahead     funders[0].funder   ofGem   Office of Gas and Electricity Markets (Ofgem)
    the user enters text to a text field        id = funders[0].funderBudget  142424242
    the user clicks the button/link             jQuery = button:contains("Done")
    the user clicks the button/link             link = Back to competition details
    the user clicks the button/link             id = compCTA
    the user clicks the button/link             jQuery = button:contains("Done")

