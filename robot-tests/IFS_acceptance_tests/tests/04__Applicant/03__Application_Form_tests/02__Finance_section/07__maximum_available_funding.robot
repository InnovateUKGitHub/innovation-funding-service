*** Settings ***
Documentation       IFS-338 Update 'Funding level' calculated maximum values and validation
Suite Setup         the guest user opens the browser
Test Setup          Delete the emails from both test mailboxes
Suite Teardown      the user closes the browser
Force Tags          Applicant
Resource          ../../../../resources/defaultResources.robot
Resource            ../../FinanceSection_Commons.robot
*** Variables ***
${Application_name_business}           Maximum funding allowed Business
${Application_name_RTO}                Maximum funding allowed RTO
#${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}       Aerospace technology investment sector
#${OPEN_COMPETITION_NAME}              Predicting market trends programme
${lead_business_email}                 oscar@innovateuk.com
${lead_rto_email}                      oscarRTO@innovateuk.com
*** Test Cases ***
funding level available for lead business ResearchCategory: Fundamental Research
    [Documentation]    IFS-338
    [Tags]
    Given we create a new user                               ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS}  Oscar  business  ${lead_business_email}
    When the user clicks the button/link                     link=Untitled application (start here)
    And the user clicks the button/link                      link=Begin application
    And the applicant completes the application details      Application details  Experimental development
    Then the user clicks the button/link                     link=Your finances
    And the user fills in the project costs
    When the user fills the organisation details with Project growth table     ${SMALL_ORGANISATION_SIZE}
    And the user clicks the button/link                      link=Your funding
    Then the user should see the text in the page             Enter your funding level (maximum 45%).
    When the user edits the research category                 Feasibility studies
    And the user edits the organisation size                 ${MEDIUM_ORGANISATION_SIZE}
    Then the user should see the text in the page             Enter your funding level (maximum 60%).
    When the user edits the research category                 Industrial research
    And the user edits the organisation size                 ${LARGE_ORGANISATION_SIZE}
    Then the user should see the text in the page             Enter your funding level (maximum 50%).
    the user clicks the button/link                          jQuery=a:contains("Your finances")
    [Teardown]  the user clicks the button/link              link=Application overview

lead applicant invites a Charity member
    [Documentation]    IFS-338
    [Tags]
    Given Invite a non-existing collaborator                           liamCharity@innovateuk.com  ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}
    When the user clicks the button/link                               link=${Application_name_business}
    And the user clicks the button/link                                link=Your finances
    And the user fills in the project costs
    And the user fills the organisation details with Project growth table     ${SMALL_ORGANISATION_SIZE}
    And the user clicks the button/link                                link=Your funding
    Then the user should see the text in the page                      Enter your funding level (maximum 100%).
    When the user clicks the button/link                               jQuery=a:contains("Your finances")
    And the user edits the organisation size                           ${LARGE_ORGANISATION_SIZE}
    Then the user should see the text in the page                      Enter your funding level (maximum 100%).

Invite existing academic collaborator
    [Documentation]  IFS-338
    [Tags]
    [Setup]  log in as a different user                           ${lead_business_email}  ${correct_password}
    When the user clicks the button/link                          link=${Application_name_business}
    And the user clicks the button/link                          link=view team members and add collaborators
    And the user clicks the button/link                          link=Add partner organisation
    Then the user enters text to a text field                     css=#organisationName  eggs
    And the user enters text to a text field                     css=input[id="applicants0.name"]  Pete
    And the user enters text to a text field                     css=input[id="applicants0.email"]  ${collaborator2_credentials["email"]}
    And the user clicks the button/link                          jQuery=button:contains("Add organisation and invite applicants")
    logout as user
    When the user reads his email and clicks the link             ${collaborator2_credentials["email"]}  Invitation to collaborate in ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}  You will be joining as part of the organisation  3
    And the user clicks the button/link                          jQuery=a:contains("Continue or sign in")
    Then the guest user inserts user email & password             ${collaborator2_credentials["email"]}   ${collaborator2_credentials["password"]}
    And The guest user clicks the log-in button
    And the user clicks the button/link                          jQuery=a:contains("Confirm and accept invitation")
    And the user clicks the button/link                          link=Your finances
    And the user clicks the button/link                          link=Your project costs
    #the user should see the element                          css=td:nth-child(2):contains("100%")
    And logout as user

funding level available for RTO lead user ResearchCategory: Fundamental Research
    [Documentation]  IFS-338
    [Tags]
    Given we create a new user                           ${OPEN_COMPETITION}  Smith  rto  ${lead_rto_email}
    When the user clicks the button/link                 link=Untitled application (start here)
    And the user clicks the button/link                  link=Begin application
    And the applicant completes the application details for RTO lead appln   Application details  Experimental development
    And the user clicks the button/link                  link=Your finances
    And the user fills in the project costs
    And the user fills the organisation details without Project growth table    ${SMALL_ORGANISATION_SIZE}
    And the user clicks the button/link                  link=Your funding
    Then the user should see the text in the page        Enter your funding level (maximum 100%).
    When the user edits the research category            Feasibility studies
    And the user edits the organisation size             ${MEDIUM_ORGANISATION_SIZE}
    Then the user should see the text in the page        Enter your funding level (maximum 100%).
    When the user edits the research category            Industrial research
    And the user edits the organisation size             ${LARGE_ORGANISATION_SIZE}
    Then the user should see the text in the page        Enter your funding level (maximum 100%).
    And the user clicks the button/link                  jQuery=a:contains("Your finances")
    [Teardown]  the user clicks the button/link          link=Application overview

lead RTO applicant invites a Charity member
    [Documentation]    IFS-338
    [Tags]
    Given Invite a non-existing collaborator                       liamRTO@innovateuk.com  ${OPEN_COMPETITION_NAME}
    When the user clicks the button/link                           link=${Application_name_RTO}
    And the user clicks the button/link                            link=Your finances
    And the user fills in the project costs
    And the user fills the organisation details without Project growth table    ${SMALL_ORGANISATION_SIZE}
    And the user clicks the button/link                             link=Your funding
    Then the user should see the text in the page                   Enter your funding level (maximum 100%).
    When the user clicks the button/link                            jQuery=a:contains("Your finances")
    And the user edits the organisation size                        ${LARGE_ORGANISATION_SIZE}
    Then the user should see the text in the page                   Enter your funding level (maximum 100%).
    [Teardown]  logout as user

*** Keywords ***
the user navigates to the competition overview
    the user navigates to the page    ${frontDoor}

the applicant completes the application details
    [Arguments]   ${Application_details}         ${Research_category}
    the user clicks the button/link              link=${Application_details}
    the user enters text to a text field         id=application_details-title  ${Application_name_business}
    the user clicks the button/link              jQuery=button:contains("Choose your innovation area")
    the user clicks the button/link              jQuery=label[for^="innovationAreaChoice-22"]:contains("Digital manufacturing")
    the user clicks the button/link              jQuery=label[for^="innovationAreaChoice-22"]:contains("Digital manufacturing")
    the user clicks the button/link              jQuery=button:contains(Save)
    the user fills the other application details questions   ${Research_category}

the applicant completes the application details for RTO lead appln
    [Arguments]   ${Application_details}   ${Research_category}
    the user clicks the button/link             link=${Application_details}
    the user enters text to a text field        id=application_details-title   ${Application_name_RTO}
    the user fills the other application details questions   ${Research_category}

the user fills the other application details questions
    [Arguments]    ${Research_category}
    the user clicks the button/link       jQuery=button:contains("research category")
    the user clicks the button/link       jQuery=label[for^="researchCategoryChoice"]:contains("${Research_category}")
    the user clicks the button/link       jQuery=label[for^="researchCategoryChoice"]:contains("${Research_category}")
    the user clicks the button/link       jQuery=button:contains(Save)
    the user clicks the button/link       jQuery=label[for="application.resubmission-no"]
    the user clicks the button/link       jQuery=label[for="application.resubmission-no"]
    The user enters text to a text field  id=application_details-startdate_day  18
    The user enters text to a text field  id=application_details-startdate_year  2018
    The user enters text to a text field  id=application_details-startdate_month  11
    The user enters text to a text field  id=application_details-duration  20
    the user clicks the button/link       jQuery=button:contains("Mark as complete")
    the user clicks the button/link       link=Application overview

the user fills in the project costs
    the user clicks the button/link             link=Your project costs
    the user selects the checkbox               agree-state-aid-page
    the user clicks the button/link             jQuery=button:contains("Mark as complete")

#the user fills the organisation details with Project growth table
#    [Arguments]   ${org_size}
#    the user clicks the button/link                          link=Your organisation
#    the user enters text to a text field                    css=input[name$="month"]    12
#    and the user enters text to a text field                css=input[name$="year"]    2016
#    the user selects the radio button                       financePosition-organisationSize  ${org_size}
#    the user enters text to a text field                    jQuery=td:contains("Annual turnover") + td input   5600
#    the user enters text to a text field                    jQuery=td:contains("Annual profit") + td input    3000
#    the user enters text to a text field                    jQuery=td:contains("Annual export") + td input    4000
#    the user enters text to a text field                    jQuery=td:contains("Research and development spend") + td input    5660
#    the user enters text to a text field                    jQuery=label:contains("employees") + input    0
#    the user clicks the button/link                         jQuery=button:contains("Mark as complete")
#
#the user fills the organisation details without Project growth table
#    [Arguments]   ${org_size}
#    the user clicks the button/link                         link=Your organisation
#    ${STATUS}    ${VALUE}=  Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery=button:contains("Edit")
#    Run Keyword If    '${status}' == 'PASS'    the user clicks the button/link  jQuery=button:contains("Edit")
#    the user selects the radio button                       financePosition-organisationSize   ${org_size}
#    the user selects the radio button                       financePosition-organisationSize   ${org_size}
#    the user enters text to a text field                    jQuery=label:contains("Turnover") + input    150
#    the user enters text to a text field                    jQuery=label:contains("employees") + input    0
#    the user clicks the button/link                         jQuery=button:contains("Mark as complete")

the user edits the research category
    [Arguments]   ${research_category}
    the user clicks the button/link                          jQuery=a:contains("Your finances")
    the user clicks the button/link                          link=Application overview
    the user clicks the button/link                          link=Application details
    the user clicks the button/link                          jQuery=button:contains("Edit")
    the user clicks the button/link                          jQuery=button:contains("research category")
    the user clicks the button/link                          jQuery=label[for^="researchCategoryChoice"]:contains("${research_category}")
    the user clicks the button/link                          jQuery=label[for^="researchCategoryChoice"]:contains("${research_category}")
    the user clicks the button/link                          jQuery=button:contains(Save)
    the user clicks the button/link                          jQuery=button:contains("Mark as complete")
    the user clicks the button/link                          link=Application overview
    the user clicks the button/link                          link=Your finances

the user edits the organisation size
    [Arguments]  ${org_size}
    the user clicks the button/link                         link=Your organisation
    the user clicks the button/link                         jQuery=button:contains("Edit")
    the user selects the radio button                       financePosition-organisationSize  ${org_size}
    the user clicks the button/link                         jQuery=button:contains("Mark as complete")
    the user clicks the button/link                         link=Your funding


