*** Settings ***
Documentation     IFS-188 Stakeholder views – Support team
...
...               IFS-1986 External users: search
Suite Setup       The user logs-in in new browser  &{support_user_credentials}
Suite Teardown    the user closes the browser
Force Tags        Support  CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../02__Competition_Setup/CompAdmin_Commons.robot

*** Test Cases ***
Support dashboard
    [Documentation]    IFS-188
    Given the user navigates to the page  ${CA_Live}
    Then the user should see all live competitions

Competition links go directly to all applications page
    [Documentation]    IFS-188
    When The user clicks the button/link    link=${openCompetitionRTO_name}
    Then the user should see the element    jQuery=span:contains("${competition_ids['${openCompetitionRTO_name}']}: ${openCompetitionRTO_name}")
    And the user should see the element     jQuery=h1:contains("All applications")
    And the user should see the element     css=#application-list

Back navigation is to dashboard
    [Documentation]    IFS-188
    Given the user clicks the button/link  jQuery=.link-back:contains("Dashboard")
    Then the user should see the element   jQuery=h1:contains("All competitions")
    And the user should see the element    jQuery=a:contains("Live")
    And the user should see the element    jQuery=a:contains("Project setup")
    And the user should see the element    jQuery=a:contains("Previous")

Support user is able to search an external user
    [Documentation]  IFS-1986
    [Tags]  HappyPath
    Given the user navigates to the page  ${server}/management/admin/external/users
    When the user is searching for external users  becky  Email
    Then the user should see the element           jQuery=td:contains("Dreambit") ~ td:contains("becky.mason@gmail.com") + td:contains("Verified")
    And the user clicks the button/link            link=Clear
    When the user is searching for external users  Empire  ORGANISATION_NAME
    Then the user should see the element           jQuery=td:contains("${EMPIRE_LTD_NAME}") + td:contains("${EMPIRE_LTD_ID}") + td:contains("${lead_applicant_credentials["email"]}")
    And the user clicks the button/link            link=Clear


*** Keywords ***
the user is searching for external users
    [Arguments]  ${string}  ${category}
    the user enters text to a text field  id=searchString  ${string}
    the user selects the option from the drop-down menu  ${category}  id=searchCategory
    the user clicks the button/link  css=button.button  #Search
