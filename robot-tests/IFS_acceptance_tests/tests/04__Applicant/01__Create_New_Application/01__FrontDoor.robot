*** Settings ***
Documentation     INFUND-6923 Create new public Competition listings page for Applicants to view open and upcoming competitions
...
...               INFUND-7946 Sign in page facelift
Suite Setup       The guest user opens the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Test Cases ***
Guest user navigates to Front Door
    [Documentation]    INFUND-6923 INFUND-7946
    [Tags]
    [Setup]    the user navigates to the front door
    When the user should see the element     jQuery=a:contains("Innovate UK")
    Then the user should see the element     jQuery=h1:contains("Innovation competitions")
    And the user should see the element     css=#keywords
    Then the user should see the element     css=#innovation-area
    When the user clicks the button/link     link=Contact us
    Then the user should see the element     jQuery=h1:contains("Contact us")
    And the user should not see an error in the page
    When the user clicks the button/link    jQuery=a:contains("feedback")
    And the user selects feedback window
    Then the user should see the element    css=.title-text
    [Teardown]    close survey window

Guest user can see Competitions and their information
    [Documentation]    INFUND-6923
    [Tags]
    [Setup]    the user navigates to the page    ${frontDoor}
    Given the user should see the element    link=Home and industrial efficiency programme
    Then the user should see the element    jQuery=h3:contains("Eligibility")
    And the user should see the element    jQuery=p:contains("UK based business of any size. Must involve at least one SME")
    Then the user should see the element    jQuery=dt:contains("Opened") + dd:contains("15 April 2016")
    And the user should see the element    jQuery=dt:contains("Closes") + dd:contains("9 September 2067")
    #Guest user can filter competitions by Keywords, this is tested in file 05__Public_content.robot

Guest user can see the opening and closing status of competitions
    [Documentation]  IFS-268
    [Tags]    MySQL
    [Setup]    Connect to Database    @{database}
    Then Change the open date of the Competition in the database to tomorrow   ${READY_TO_OPEN_COMPETITION_NAME}
    Given the user navigates to the page  ${frontDoor}
    Then the user can see the correct date status of the competition    ${READY_TO_OPEN_COMPETITION_NAME}    Opening soon    Opens
    And Change the open date of the Competition in the database to one day before   ${READY_TO_OPEN_COMPETITION_NAME}
    Given the user navigates to the page  ${frontDoor}
    Then the user can see the correct date status of the competition    ${READY_TO_OPEN_COMPETITION_NAME}    Open now    Opened
    And Change the close date of the Competition in the database to a fortnight   ${READY_TO_OPEN_COMPETITION_NAME}
    Given the user navigates to the page  ${frontDoor}
    Then the user can see the correct date status of the competition    ${READY_TO_OPEN_COMPETITION_NAME}    Open now    Opened
    And Change the close date of the Competition in the database to thirteen days   ${READY_TO_OPEN_COMPETITION_NAME}
    Given the user navigates to the page  ${frontDoor}
    Then the user can see the correct date status of the competition    ${READY_TO_OPEN_COMPETITION_NAME}    Closing soon    Opened
    And Change the close date of the Competition in the database to tomorrow   ${READY_TO_OPEN_COMPETITION_NAME}
    Given the user navigates to the page  ${frontDoor}
    Then the user can see the correct date status of the competition    ${READY_TO_OPEN_COMPETITION_NAME}    Closing soon    Opened
    And Reset the open and close date of the Competition in the database   ${READY_TO_OPEN_COMPETITION_NAME}

Guest user can filter competitions by Innovation area
    [Documentation]    INFUND-6923
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${frontDoor}
    When the user selects the option from the drop-down menu    Space technology    id=innovation-area
    And the user clicks the button/link    jQuery=button:contains("Update results")
    Then the user should see the element    jQuery=a:contains("Transforming big data")
    And the user should not see the element    jQuery=a:contains("Home and industrial efficiency programme")
    When the user selects the option from the drop-down menu    Any    id=innovation-area
    And the user clicks the button/link    jQuery=button:contains("Update results")
    Then the user should see the element    jQuery=a:contains("Home and industrial efficiency programme")

Guest user can see the public information of an unopened competition
    [Documentation]    INFUND-8714
    [Tags]
    [Setup]    the user navigates to the page    ${frontDoor}
    Given the user clicks the button/link    link=Photonics for health
    Then the user should see the element    jQuery=h1:contains("Photonics for health")
    And the user should see the element    jQuery=strong:contains("Competition opens") + span:contains("Saturday 24 February 2018")
    And the user should see the element    jQuery=li:contains("Competition closes")
    And the user should see the element    jQuery=li:contains("Friday 16 March 2018")
    And the user should see the text in the page    This competition has not yet opened.
    And the user should not see the text in the page    Or sign in to continue an existing application
    And the user should see the element    jQuery=.button:contains("Start new application")

Guest user can see the non ifs competition warnings
    [Documentation]  IFS-38
    [Tags]    MySQL
    [Setup]    Connect to Database    @{database}
    Given the user navigates to the page  ${frontDoor}
    And Change the close date of the Competition in the database to tomorrow    ${NON_IFS_COMPETITION_NAME}
    And the user clicks the button/link    link=${NON_IFS_COMPETITION_NAME}
    Then the user should see the text in the page       Registration has now closed.
    And execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='2020-06-24 11:00:00' WHERE `${database_name}`.`competition`.`name`='Transforming big data' and `${database_name}`.`milestone`.`type` = 'SUBMISSION_DATE';

Guest user can see the public information of a competition
    [Documentation]    INFUND-6923
    [Tags]
    [Setup]    the user navigates to the page    ${frontDoor}
    Given the user clicks the button/link    link=Home and industrial efficiency programme
    Then the user should see the element    jQuery=h1:contains("Home and industrial efficiency programme")
    And the user should see the element    jQuery=strong:contains("Competition opens") + span:contains("Friday 15 April 2016")
    And the user should see the element    jQuery=li:contains("Competition closes")
    And the user should see the element    jQuery=li:contains("Friday 9 September 2067")
    And the user should see the text in the page    Or sign in to continue an existing application.
    And the user should see the element    jQuery=.button:contains("Start new application")

Guest user can see the public Summary of the competition
    [Documentation]    INFUND-6923
    [Tags]
    Given the user clicks the button/link    link=Summary
    Then the user should see the element    jQuery=h3:contains("Description")
    And the user should see the text in the page    Innovate UK is investing up to £15 million in innovation projects to stimulate the new products and services of tomorrow.
    When the user should see the element    jQuery=h3:contains("Funding type")
    Then the user should see the element    jQuery=p:contains("Grant")
    When the user should see the element    jQuery=h3:contains("Project size")
    Then the user should see the element    jQuery=p:contains("£15 million")

Guest user can see the public Eligibility of the competition
    [Documentation]    INFUND-6923
    [Tags]
    Given the user clicks the button/link    link=Eligibility
    Then the user should see the element    jQuery=h3:contains("Lead applicant eligibility")
    And the user should see the text in the page    one SME involved in your proposal carry out your project work, and intend to

Guest user can see the public Scope of the competition
    [Documentation]    INFUND-6923
    [Tags]
    Given the user clicks the button/link    link=Scope
    Then the user should see the element    jQuery=h3:contains("Project scope")
    And the user should see the text in the page    Projects will: harness E&E technologies across the economy develop and scale-up research and development to bring ideas,

Guest user can see the public Dates of the competition
    [Documentation]    INFUND-6923
    [Tags]
    Given the user clicks the button/link    link=Dates
    When the user should see the element    jQuery=dt:contains("15 April 2016") + dd:contains("Competition opens")
    And the user should see the element    jQuery=dt:contains("12 May 2016") + dd:contains("Briefing event in Belfast")
    And the user should see the element    jQuery=dt:contains("9 September 2067") + dd:contains("Competition closes")
    And the user should see the element    jQuery=dt:contains("20 June 2068") + dd:contains("Applicants notified")

Guest user can see the public How to apply of the competition
    [Documentation]    INFUND-6923
    [Tags]
    Given the user clicks the button/link    link=How to apply
    When the user should see the element    jQuery=h3:contains("How to apply")
    Then the user should see the text in the page    Collaborators will be sent a link,

Guest user can see the public Supporting information of the competition
    [Documentation]    INFUND-6923
    [Tags]
    Given the user clicks the button/link    link=Supporting information
    When the user should see the element    jQuery=h3:contains("Background and further information")
    Then the user should see the text in the page    However, we sometimes struggle to fully commercialise the opportunities.

Guest user can apply to a competition
    [Documentation]    INFUND-6923
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${frontDoor}
    Given the user clicks the button/link    link=Home and industrial efficiency programme
    When the user clicks the button/link    link=Start new application
    Then the user should see the element    jQuery=.button:contains("Sign in")
    And the user should see the element    jQuery=.button:contains("Create")

*** Keywords ***
the user navigates to the front door
    the user clicks the button/link    jQuery=span:contains("Need help signing in or creating an account")
    the user clicks the button/link    jQuery=a:contains("competitions listings page")

Close survey window
    Close Window
    Select Window

the user selects feedback window
    Select Window    title=Innovation Funding Service - Feedback Survey

the user can see the correct date status of the competition
    [Arguments]    ${competition_name}    ${date_status}    ${open_text}
    the user should see the element    jQuery=h2:contains(${competition_name}) ~ h3:contains(${date_status}) ~ dl dt:contains(${open_text})
