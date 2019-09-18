*** Settings ***
Documentation     IFS-338 Update 'Funding level' calculated maximum values and validation
Suite Setup       The guest user opens the browser
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot

*** Variables ***
${Application_name_business}           Maximum funding allowed Business
${Application_name_RTO}                Maximum funding allowed RTO
${lead_business_email}                 oscar@innovateuk.com
${lead_rto_email}                      oscarRTO@innovateuk.com

*** Test Cases ***
Maximum funding level available for lead business
    [Documentation]    IFS-338
    [Tags]  HappyPath
    Given we create a new user                               ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS}  Oscar  business  ${lead_business_email}  ${BUSINESS_TYPE_ID}
    When the user clicks the button/link                     link = Untitled application (start here)
    And the applicant completes the application details      Application details
    And the user selects Research category                   Experimental development
    And the user fills the organisation details with Project growth table     ${Application_name_business}  ${SMALL_ORGANISATION_SIZE}
    When the user fills in the project costs                 labour costs  n/a
    And the user clicks the button/link                      link = Your funding
    And the user selects the radio button                    requestingFunding   true
    Then the user should see the element                     jQuery = span:contains("The maximum you can enter is 45%")
    And the correct funding displayed for lead applicant     Feasibility studies  ${MEDIUM_ORGANISATION_SIZE}  60%
    And the correct funding displayed for lead applicant     Industrial research  ${LARGE_ORGANISATION_SIZE}  50%
    And the correct funding displayed for lead applicant     Experimental development  ${SMALL_ORGANISATION_SIZE}  45%
    And the user selects the radio button                    requestingFunding   true
    Then the user should see the element                     jQuery = span:contains("The maximum you can enter is 45%")
    And the user selects the radio button                    otherFunding  false
    And the user clicks the button/link                      jQuery = a:contains("Your project finances")
    [Teardown]  the user clicks the button/link              link = Back to application overview

Lead applicant invites a Charity member
    [Documentation]    IFS-338
    [Tags]
    Given Invite a non-existing collaborator                                liamCharity@innovateuk.com  ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}
    When the user clicks the button/link                                    link = ${Application_name_business}
    And the user fills the organisation details with Project growth table   ${Application_name_business}  ${SMALL_ORGANISATION_SIZE}
    And the user fills in the project costs                                 labour costs  n/a
    And the user clicks the button/link                                     link = Your funding
    And the user selects the radio button                                   requestingFunding   true
    And the user should see the element                                     jQuery = label:contains("Select a funding level")

Invite existing academic collaborator
    [Documentation]  IFS-338
    [Tags]
    [Setup]  log in as a different user                       ${lead_business_email}  ${correct_password}
    When the user clicks the button/link                      link = ${Application_name_business}
    And the user clicks the button/link                       link = Application team
    And the user clicks the button/link                       link = Add a partner organisation
    And the user adds a partner organisation                  eggs  Pete  ${collaborator2_credentials["email"]}
    And the user clicks the button/link                       jQuery = button:contains("Invite partner organisation")
    And logout as user
    And the user accepts the invite to collaborate            ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS_NAME}  ${collaborator2_credentials["email"]}  ${collaborator2_credentials["password"]}
    Then the correct funding is displayed to academic user

Maximum funding level available for RTO lead
    [Documentation]  IFS-338
    [Tags]  HappyPath
    [Setup]  logout as user
    Given we create a new user                                              ${openCompetitionRTO}  Smith  rto  ${lead_rto_email}    ${RTO_TYPE_ID}
    When the user clicks the button/link                                    link = Untitled application (start here)
    And the applicant completes the application details for RTO lead appln  Application details
    And the user selects Research category                                  Experimental development
    And the user fills in the organisation information                      ${Application_name_RTO}  ${SMALL_ORGANISATION_SIZE}
    And the user fills in the project costs                                 labour costs  n/a
    When the user clicks the button/link                                    link = Your funding
    And the user selects the radio button                                   requestingFunding   true
    And the user should see the element                                     jQuery = span:contains("The amount you apply for must reflect other")
    And the correct funding displayed for lead RTO applicant                Feasibility studies  ${MEDIUM_ORGANISATION_SIZE}
    And the correct funding displayed for lead RTO applicant                Industrial research  ${LARGE_ORGANISATION_SIZE}
    And the user marks your funding section as complete
    [Teardown]  the user clicks the button/link                             link = Back to application overview

Editing research category does not reset your funding
    [Documentation]  IFS-4127
    [Tags]
    Given the user edits the research category   Feasibility studies
    And the user edits the organisation size     ${SMALL_ORGANISATION_SIZE}
    And The user clicks the button/link          link = Your project finances
    Then the user should see the element         jQuery = li:contains("Your funding") .task-status-complete
    [Teardown]  the user clicks the button/link  link = Back to application overview

Lead RTO applicant invites a Charity member
    [Documentation]    IFS-338
    [Tags]
    Given Invite a non-existing collaborator            liamRTO@innovateuk.com  ${openCompetitionRTO_name}
    When the user clicks the button/link                link = ${Application_name_RTO}
    And the user fills in the organisation information  ${Application_name_RTO}  ${SMALL_ORGANISATION_SIZE}
    And the user fills in the project costs             labour costs  n/a
    When the user clicks the button/link                link = Your funding
    And the user marks your funding section as complete

Invite existing academic collaborator for RTO lead
    [Documentation]  IFS-1050  IFS-1013
    [Tags]
    [Setup]  log in as a different user                ${lead_rto_email}  ${correct_password}
    When the user clicks the button/link               link = ${Application_name_RTO}
    And the user clicks the button/link                link = Application team
    And the user clicks the button/link                link = Add a partner organisation
    And the user adds a partner organisation           eggs  Pete  ${collaborator2_credentials["email"]}
    And the user clicks the button/link                jQuery = button:contains("Invite partner organisation")
    And logout as user
    When the user accepts the invite to collaborate    ${openCompetitionRTO_name}  ${collaborator2_credentials["email"]}  ${collaborator2_credentials["password"]}
    And the correct funding is displayed to academic user
    And the academic user marks your project costs as complete

Invite existing business user into RTO lead application
    [Documentation]  IFS-1050  IFS-1013
    [Tags]
    [Setup]  log in as a different user                ${lead_rto_email}  ${correct_password}
    When the user clicks the button/link               link = ${Application_name_RTO}
    And the user clicks the button/link                link = Application team
    And the user clicks the button/link                link = Add a partner organisation
    And the user adds a partner organisation           innovate bus  oscar  ${lead_business_email}
    And the user clicks the button/link                jQuery = button:contains("Invite partner organisation")
    And logout as user
    Then the user accepts the invite to collaborate    ${openCompetitionRTO_name}  ${lead_business_email}  ${correct_password}

Business user fills in the project costs
    [Documentation]  IFS-1050  IFS-1013
    [Tags]
    When the business user fills in the project costs
    And the user fills in the organisation information  ${Application_name_RTO}  ${SMALL_ORGANISATION_SIZE}
    And the user clicks the button/link                 link = Your funding
    Then the user marks your funding section as complete

Research participation is correct for RTO lead application
   [Documentation]  IFS-1050  IFS-1013
#    The Open comp used has 50% as maximum research participation. So only business partner can claim rest of the amount
#    Research participants include all non-Business participants i.e. Research, RTO and Public sector or charity who claim 50% of the overall project costs
   [Tags]
   [Setup]  log in as a different user                 ${lead_rto_email}  ${correct_password}
    When the user clicks the button/link               link = ${Application_name_RTO}
    And the user clicks the button/link                link = Finances overview
    Then the user should not see the element           jQuery = .warning-alert:contains("The participation levels of this project are not within the required range.")
    And the user should not see an error in the page

*** Keywords ***
the user navigates to the competition overview
    the user navigates to the page    ${frontDoor}

the applicant completes the application details
    [Arguments]   ${Application_details}
    the user clicks the button/link              link = ${Application_details}
    the user enters text to a text field         css = [id="name"]  ${Application_name_business}
    the user clicks the button/link              jQuery = button:contains("Choose your innovation area")
    the user clicks the button twice             jQuery = label[for^="innovationAreaChoice-22"]:contains("Digital manufacturing")
    the user clicks the button/link              jQuery = button:contains(Save)
    the user fills the other application details questions

the applicant completes the application details for RTO lead appln
    [Arguments]   ${Application_details}
    the user clicks the button/link             link = ${Application_details}
    the user enters text to a text field        css = [id="name"]  ${Application_name_RTO}
    the user fills the other application details questions

the user fills the other application details questions
    the user clicks the button twice      css = label[for="resubmission-no"]
    The user enters text to a text field  id = startDate  18
    The user enters text to a text field  id = application_details-startdate_year  2018
    The user enters text to a text field  id = application_details-startdate_month  11
    The user enters text to a text field  css = [id="durationInMonths"]  20
    the user clicks the button/link       jQuery = button:contains("Mark")
    the user clicks the button/link       link = Application overview

the business user fills in the project costs
# The project costs are added such that business partner costs are less than 50% of overall project costs
    the user clicks the button/link         link = Your project finances
    the user clicks the button/link         link = Your project costs
    the user clicks the button/link         jQuery = button:contains("Materials")
    the user should see the element         css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field    css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field    css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    131265
    the user enters text to a text field    css = #material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user clicks the button/link         jQuery = button:contains("Materials")
    the user selects the checkbox           stateAidAgreed
    the user clicks the button/link         jQuery = button:contains("Mark")

the user edits the research category
    [Arguments]   ${research_category}
    the user clicks the button/link     jQuery = a:contains("Your project finances")
    the user clicks the button/link     link = Back to application overview
    the user clicks the button/link     link = Research category
    the user clicks the button/link     jQuery = button:contains("Edit")
    the user clicks the button twice    jQuery = label[for^="researchCategory"]:contains("${research_category}")
    the user clicks the button/link     id = application-question-complete
    the user clicks the button/link     link = Your project finances

the user edits the organisation size
    [Arguments]  ${org_size}
    the user clicks the button/link     link = Your organisation
    the user clicks the button/link     jQuery = button:contains("Edit")
    the user selects the radio button   organisationSize  ${org_size}
    the user selects the checkbox       stateAidAgreed
    the user clicks the button/link     jQuery = button:contains("Mark")
    the user clicks the button/link     link = Your funding

the user accepts the invite to collaborate
    [Arguments]  ${competition_name}  ${user_name}  ${password}
    the user reads his email and clicks the link     ${user_name}  Invitation to collaborate in ${competition_name}  You will be joining as part of the organisation  2
    the user clicks the button/link                  jQuery = a:contains("Continue")
    the guest user inserts user email and password   ${user_name}  ${password}
    the guest user clicks the log-in button
    the user clicks the button/link                  css = .govuk-button[type="submit"]   #Save and continue

the correct funding is displayed to academic user
    ${status}   ${value} =  Run Keyword And Ignore Error Without Screenshots  Page Should Contain    Bath Spa University
    Run Keyword If   '${status}' == 'PASS'    Run Keywords   the user clicks the button twice      jQuery = label:contains("Bath Spa")
    ...                              AND                     the user clicks the button/link       jQuery = .govuk-button:contains("Save and continue")
    the user clicks the button/link   link = Your project finances
    the user should see the element   jQuery = td:contains("0%")

the academic user marks your project costs as complete
    the user clicks the button/link        link = Your project costs
    the user enters text to a text field   css = input[name$="tsbReference"]  academic costs
    the user uploads the file              css = .upload-section input  ${5mb_pdf}
    the user selects the checkbox          agree-terms-page
    wait for autosave
    the user clicks the button/link        jQuery = button:contains("Mark")

the correct funding displayed for lead applicant
    [Arguments]   ${research_cat}  ${org_size}  ${funding_amount}
    the user edits the research category        ${research_cat}
    the user edits the organisation size        ${org_size}
    the user selects the radio button           requestingFunding   true
    the user should see the element             jQuery = span:contains("The maximum you can enter is ${funding_amount}")

the correct funding displayed for lead RTO applicant
    [Arguments]   ${research_cat}  ${org_size}
    the user edits the research category        ${research_cat}
    the user edits the organisation size        ${org_size}
    the user selects the radio button           requestingFunding   true
    the user should see the element             jQuery = span:contains("The amount you apply for must reflect other")