*** Settings ***
Documentation       IFS-338 Update 'Funding level' calculated maximum values and validation
Suite Setup         the guest user opens the browser
Suite Teardown      the user closes the browser
Force Tags          Applicant
Resource          ../../../../resources/defaultResources.robot
Resource            ../../Applicant_Commons.robot
*** Variables ***

${Application_name_business}           Maximum funding allowed Business
${Application_name_RTO}                Maximum funding allowed RTO
#${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}       Aerospace technology investment sector
#${openCompetitionRTO_name}            Predicting market trends programme
${lead_business_email}                 oscar@innovateuk.com
${lead_rto_email}                      oscarRTO@innovateuk.com

*** Test Cases ***
maximum funding level available for lead business
    [Documentation]    IFS-338
    [Tags]
    Given we create a new user                               ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS}  Oscar  business  ${lead_business_email}
    When the user clicks the button/link                     link=Untitled application (start here)
    And the user clicks the button/link                      link=Begin application
    And the applicant completes the application details      Application details  Experimental development
    And the user fills the organisation details with Project growth table   ${Application_name_business}  ${SMALL_ORGANISATION_SIZE}
    When the user fills in the project costs
    And the user clicks the button/link                      link=Your funding
    Then the user should see the text in the page             Enter your funding level (maximum 45%).
    And the correct funding displayed for lead applicant      Feasibility studies   ${MEDIUM_ORGANISATION_SIZE}  60%
    And the correct funding displayed for lead applicant      Industrial research   ${LARGE_ORGANISATION_SIZE}   50%
    And the user clicks the button/link                       jQuery=a:contains("Your finances")
    [Teardown]  the user clicks the button/link              link=Application overview

lead applicant invites a Charity member
    [Documentation]    IFS-338
    [Tags]
    Given Invite a non-existing collaborator                           liamCharity@innovateuk.com  ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}
    When the user clicks the button/link                               link=${Application_name_business}
    And the user fills the organisation details with Project growth table   ${Application_name_business}  ${SMALL_ORGANISATION_SIZE}
    Then the funding displayed is as expected

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
    And logout as user
    Then the correct funding is displayed to academic user

maximum funding level available for RTO lead
    [Documentation]  IFS-338
    [Tags]
    Given we create a new user                                                  ${openCompetitionRTO}  Smith  rto  ${lead_rto_email}
    When the user clicks the button/link                                        link=Untitled application (start here)
    And the user clicks the button/link                                         link=Begin application
    And the applicant completes the application details for RTO lead appln      Application details  Experimental development
    And the user fills in the organisation information                          ${Application_name_RTO}  ${SMALL_ORGANISATION_SIZE}
    And the user fills in the project costs
    When the user clicks the button/link                                        link=Your funding
    Then the user should see the text in the page                               Enter your funding level (maximum 100%).
    And the correct funding displayed for lead applicant                        Feasibility studies  ${MEDIUM_ORGANISATION_SIZE}  100%
    And the correct funding displayed for lead applicant                         Industrial research  ${LARGE_ORGANISATION_SIZE}  100%
    And the user clicks the button/link                                         jQuery=a:contains("Your finances")
    [Teardown]  the user clicks the button/link                                 link=Application overview

lead RTO applicant invites a Charity member
    [Documentation]    IFS-338
    [Tags]
    Given Invite a non-existing collaborator            liamRTO@innovateuk.com  ${openCompetitionRTO_name}
    When the user clicks the button/link                link=${Application_name_RTO}
    And the user fills in the organisation information  ${Application_name_RTO}  ${SMALL_ORGANISATION_SIZE}
    Then the funding displayed is as expected
    [Teardown]  logout as user

*** Keywords ***
the user navigates to the competition overview
    the user navigates to the page    ${frontDoor}

the applicant completes the application details
    [Arguments]   ${Application_details}         ${Research_category}
    the user clicks the button/link              link=${Application_details}
    the user enters text to a text field         id=application_details-title  ${Application_name_business}
    the user clicks the button/link              jQuery=button:contains("Choose your innovation area")
    the user clicks the button twice              jQuery=label[for^="innovationAreaChoice-22"]:contains("Digital manufacturing")
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
    the user clicks the button twice      jQuery=label[for^="researchCategoryChoice"]:contains("${Research_category}")
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

the user edits the research category
    [Arguments]   ${research_category}
    the user clicks the button/link                          jQuery=a:contains("Your finances")
    the user clicks the button/link                          link=Application overview
    the user clicks the button/link                          link=Application details
    the user clicks the button/link                          jQuery=button:contains("Edit")
    the user clicks the button/link                          jQuery=button:contains("research category")
    the user clicks the button twice                         jQuery=label[for^="researchCategoryChoice"]:contains("${research_category}")
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

the funding displayed is as expected
    the user fills in the project costs
    the user clicks the button/link                               link=Your funding
    the user should see the text in the page                      Enter your funding level (maximum 100%).
    the user clicks the button/link                               jQuery=a:contains("Your finances")
    the user edits the organisation size                          ${LARGE_ORGANISATION_SIZE}
    the user should see the text in the page                      Enter your funding level (maximum 100%).

the correct funding is displayed to academic user
    the user reads his email and clicks the link             ${collaborator2_credentials["email"]}  Invitation to collaborate in ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}  You will be joining as part of the organisation  3
    the user clicks the button/link                          jQuery=a:contains("Continue or sign in")
    The guest user inserts user email and password           &{collaborator2_credentials}
    the guest user clicks the log-in button
    the user clicks the button/link                          jQuery=a:contains("Confirm and accept invitation")
    the user clicks the button/link                          link=Your finances
    the user should see the element                          jQuery=td:contains("100%")
    logout as user

the correct funding displayed for lead applicant
    [Arguments]   ${research_cat}  ${org_size}  ${funding_amoount}
    the user edits the research category                                   ${research_cat}
    the user edits the organisation size                                    ${org_size}
    the user should see the text in the page                               Enter your funding level (maximum ${funding_amoount}).
