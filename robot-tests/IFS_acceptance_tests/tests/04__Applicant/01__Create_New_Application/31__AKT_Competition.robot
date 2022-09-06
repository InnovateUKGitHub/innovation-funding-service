*** Settings ***
Documentation     IFS-12745 Adding new template to support KTP AKT
...
...               IFS-12746 KTA is optional for KTP AKT funding type
...
...               IFS-12748 KTP AKT 2022 - Enable assessments for questions for KTP AKT funding type
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${AKT2ICompName}                            Access Knowledge Transfer to Innovate Competition
${aktLeadEmail}                             akt.ktp@gmail.com
&{aktLeadCredentials}                       email=${aktLeadEmail}  password=${short_password}
${ktaEmail}                                 hermen.mermen@ktn-uk.test
${ktpApplicationTitle}                      AKT2I New Application
${costsValue}                               123
${associateSalaryTable}                     associate-salary-costs-table
${associateDevelopmentTable}                associate-development-costs-table
&{assessor3_credentials}                    email=hermen.mermen@ktn-uk.test                              password=${short_password}

*** Test Cases ***
Comp admin can select AKT2I Competition funding type
    [Documentation]  IFS-12745
    Given the user navigates to the page            ${CA_UpcomingComp}
    When the user clicks the button/link            jQuery = .govuk-button:contains("Create competition")
    And the user fills in initial details           ${AKT2ICompName}  ${month}  ${nextyear}  ${compType_Programme}  STATE_AID  KTP_AKT
    Then the user should see the element            jQuery = dt:contains("Funding type")+dd:contains("Access Knowledge Transfer to Innovate (AKT2I)")
    And the user should see the element             jQuery = button:contains("Edit")
    [Teardown]  the user clicks the button/link     link = Back to competition details

Comp admin can not view ktp related assessment sections on selecting AKT2I funding type
    [Documentation]  IFS-12745
    When the user clicks the button/link          link = Application
    Then the user should not see the element      link = Impact
    And the user should not see the element       link = Innovation
    And the user should not see the element       link = Challenge
    And the user should not see the element       link = Cohesiveness

Comp admin create an AKT2I competition and opens to external users
    [Documentation]  IFS-12745
    Given the user clicks the button/link                            link = Back to competition details
    Then the competition admin creates AKT2I competition             ${KTP_TYPE_ID}  ${AKT2ICompName}  Access Knowledge Transfer to Innovate  ${compType_Programme}  STATE_AID  KTP_AKT  PROJECT_SETUP  no  50  true  single-or-collaborative  No
    [Teardown]  Get competition id and set open date to yesterday    ${AKT2ICompName}

Lead applicant can complete application team section without KTA
    [Documentation]  IFS-12746
    [Setup]  logout as user
    Given the user select the competition and starts application    ${AKT2ICompName}
    And The user clicks the button/link                             link = Continue and create an account
    And the user apply with knowledge base organisation             The University of Liverpool   The University of Liverpool
    And the user creates an account and verifies email              KTP  AKT  ${aktLeadEmail}  ${short_password}
    When Logging in and Error Checking                              &{aktLeadCredentials}
    And the user clicks the button/link                             jQuery = a:contains("${UNTITLED_APPLICATION_DASHBOARD_LINK}")
    And applicant completes edi profile                             COMPLETE  ${aktLeadEmail}
    And the user clicks the button/link                             link = Application team
    And the user should see the element                             jQuery = h2:contains("Knowledge transfer adviser (optional)")
    And the user clicks the button/link                             id = application-question-complete
    Then the user should see the element                            jQuery = p:contains("Application team is marked as complete")

Lead applicant can not view KTA details in application summary
    [Documentation]  IFS-12476
    Given the user clicks the button/link       link = Application overview
    When the user clicks the button/link        link = Review and submit
    And the user clicks the button/link         id = accordion-questions-heading-1-1
    Then the user should not see the element    jQuery = h2:contains("Knowledge transfer adviser")

Lead applicant submits the AKT2I application
    [Documentation]  IFS-12478
    Given the user clicks the button/link                                                           link = Application overview
    And the user completes the KTP application except application team and your project finances
    And the user fills project finances section
    And the user clicks the button/link                                                             link = Review and submit
    Then the user clicks the button/link                                                            id = submit-application-button

Assessor submits assessment of the application
    [Documentation]   IFS-12748
    [Setup]  moving competition to Closed                         ${competitionId}
    Given log in as a different user                              &{ifs_admin_user_credentials}
    When Assessor is invited to assess the AKT2I competition
    And Assessor accept the inviation to assess the AKT2I competition
    And Comp Admin allocates assessor to application
    And Allocated assessor assess the application
    Then the user should see the element                           jQuery = li:contains("${ktpApplicationTitle}") strong:contains("Recommended")

*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The user logs-in in new browser   &{Comp_admin1_credentials}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the user fills in initial details
    [Arguments]  ${compTitle}  ${month}  ${nextyear}  ${compType}  ${fundingRule}  ${fundingType}
    the user navigates to the page                          ${CA_UpcomingComp}
    the user clicks the button/link                         jQuery = .govuk-button:contains("Create competition")
    the user clicks the button/link                         jQuery = a:contains("Initial details")
    the user enters text to a text field                    css = #title  ${compTitle}
    the user selects the radio button                       fundingType  ${fundingType}
    the user selects the option from the drop-down menu     ${compType}  id = competitionTypeId
    the user selects the radio button                       fundingRule  ${fundingRule}
    the user selects the option from the drop-down menu     Emerging and enabling  id = innovationSectorCategoryId
    the user selects the option from the drop-down menu     Robotics and autonomous systems  css = select[id^=innovationAreaCategory]
    the user enters text to a text field                    css = #openingDateDay  1
    the user enters text to a text field                    css = #openingDateMonth  ${month}
    the user enters text to a text field                    css = #openingDateYear  ${nextyear}
    the user selects option from type ahead                 innovationLeadUserId  Ian Cooper  Ian Cooper
    the user selects option from type ahead                 executiveUserId  Robert Johnson  Robert Johnson
    the user clicks the button/link                         jQuery = button:contains("Done")

the competition admin creates AKT2I competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingRule}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}  ${isOpenComp}
    the user selects the Terms and Conditions                   ${compType}  ${fundingRule}
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility                ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user fills in the CS funding eligibility                true   ${compType}  ${fundingRule}
    the user selects the organisational eligibility to no       false
    the user fills in the CS Milestones                         ${completionStage}   ${month}   ${nextyear}  ${isOpenComp}
    the user marks the KTP_AKT Assessed questions as complete   ${compType}
    the user fills in the CS Documents in other projects
    the user clicks the button/link                             link = Public content
    the user fills in the Public content and publishes          ${extraKeyword}
    the user clicks the button/link                             link = Return to setup overview
    the user clicks the button/link                             jQuery = a:contains("Complete")
    the user clicks the button/link                             jQuery = button:contains('Done')
    the user navigates to the page                              ${CA_UpcomingComp}
    the user should see the element                             jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user marks the KTP_AKT Assessed questions as complete
    [Arguments]  ${comp_type}
    the user clicks the button/link                                                         link = Application
    the user marks the Application details section as complete                              ${comp_type}
    the assessment questions are marked complete for other programme type competitions
    the user fills in the Finances questions without growth table                           false  true
    the user clicks the button/link                                                         jQuery = button:contains("Done")
    the user clicks the button/link                                                         link = Back to competition details
    the user should see the element                                                         jQuery = div:contains("Application") ~ .task-status-complete

KTA accepts the invitation to assess the application
    [Arguments]    ${compettitionName}                    ${ktaEmail}   ${short_password}
    log in as a different user                            ${ktaEmail}   ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = ${compettitionName}
    the user selects the radio button                     acceptInvitation   true
    the user clicks the button/link                       jQuery = button:contains("Confirm")

KTA accepts to assess the KTP application
    [Arguments]   ${compettitionName}                    ${ktaEmail}  ${short_password}
    log in as a different user                           ${ktaEmail}  ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = ${compettitionName}
    the user clicks the button/link                       link = Accept or reject
    the user selects the radio button                     assessmentAccept  true
    the user clicks the button/link                       jQuery = button:contains("Confirm")

Assessor completes the scope section of an application
    the user selects the radio button                       govuk-radios__item     in-scope-true
    The user selects the option from the drop-down menu     Industrial research    css = .research-category
    The user enters text to a text field                    css = .editor    Testing feedback text
    Wait for autosave
    mouse out  css = .editor
    the user should see the element                                    jQuery = span:contains("Saved!")
    the user clicks the button/link                                    jQuery = button:contains("Save and return to assessment overview")
    ${error} =   Run Keyword and return status without screenshots     page should contain     An unexpected error occurred.
    Run Keyword If    '${error}' == 'True'                             the user clicks the button/link   jQuery = button:contains("Save and return to assessment overview")

Invite KTA to assess the competition
    [Arguments]   ${competitionID}   ${applicationTitle}   ${competitionName}   ${email}  ${short_password}
    Log in as a different user                               &{ifs_admin_user_credentials}
    the user navigates to the page                           ${server}/management/competition/${competitionID}/assessors/find
    ${status}   ${value} =  Run Keyword And Ignore Error Without Screenshots    the user should see the element    jQuery = span:contains("Non KTP competition all finance overview")
    Run Keyword If   '${status}' == 'PASS'    the user selects the checkbox     jQuery = tr:contains("Addison Shannon") :checkbox
    ...                              ELSE     the user selects the checkbox     jQuery = tr:contains("Amy Colin") :checkbox
    the user clicks the button/link                          id = add-selected-assessors-to-invite-list-button
    the user clicks the button/link                          id = review-and-send-assessor-invites-button
    the user clicks the button/link                          jQuery = button:contains("Send invitation")
    KTA accepts the invitation to assess the application     ${competitionName}  ${email}   ${short_password}
    Log in as a different user                               &{ifs_admin_user_credentials}
    the user navigates to the page                           ${server}/management/assessment/competition/${competitionID}/applications
    the user clicks the button/link                          link = View progress
    the user selects the checkbox                            assessor-row-1
    the user clicks the button/link                          jQuery = button:contains("Add to application")
    the user navigates to the page                           ${server}/management/competition/${competitionID}
    the user clicks the button/link                          id = notify-assessors-changes-since-last-notify-button
    KTA accepts to assess the KTP application                ${competitionName}    ${email}  ${short_password}
    the user clicks the button/link                          link = ${applicationTitle}

the user completes the KTP application except application team and your project finances
    the user clicks the button/link                                                 link = Application details
    the user fills in the KTP Application details                                   ${KTPapplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                                        ${ktpApplicationTitle}
    the lead applicant marks the KTP project location as complete
    the user selects research category                                              Feasibility studies
    the user accept the competition terms and conditions                            Return to application overview

the user fills in the KTP Application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user should see the element                jQuery = h1:contains("Application details")
    the user should not see the element            id = startDate
    the user enters text to a text field           id = name  ${appTitle}
    the user enters text to a text field           id = durationInMonths  24
    the user clicks the button twice               css = label[for="resubmission-no"]
    the user can mark the question as complete
    the user should see the element                jQuery = li:contains("Application details") > .task-status-complete

the lead applicant marks the KTP project location as complete
    the user enters the project location
    the user should see the element          jQuery = li:contains("Your project location") span:contains("Complete")
    the user clicks the button/link          link = Back to application overview

the applicant goes to the project summary, and performs actions
    click link    Project summary
    time until page contains    Please provide a short summary of your project    Loading the project summary section
    Input Text    css = #form-input-1039 .editor    This is some random text
    mark section as complete    Marking summary section as complete
    the applicant saves and returns to the overview    Saving the project summary section

the applicant goes to the public description, and performs actions
    click link    Public description
    time until page contains    Please provide a brief description of your project    Loading the public description section
    Input Text    css = #form-input-1040 .editor    This is some random text
    mark section as complete    Marking public description section as complete
    the applicant saves and returns to the overview    Saving the public description section

the applicant goes to the scope section, and performs actions
    click link    Scope
    time until page contains    If your application doesn't align with the scope    Loading the scope section
    Input Text    css = #form-input-1041 .editor    This is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random text
    mark section as complete    Marking scope section as complete
    the applicant saves and returns to the overview    Saving the scope section

the user selects research category
    [Arguments]  ${res_category}
    the user clicks the button/link   link = Research category
    the user clicks the button twice  jQuery = label:contains("${res_category}")
    the user can mark the question as complete
    the user should see the element   jQuery = li:contains("Research category") > .task-status-complete

the user fills project finances section
     the user clicks the button/link                       link = Your project finances
     the user clicks the button/link                       link = Your fEC model
     the user selects the radio button                     fecModelEnabled  fecModelEnabled-yes
     The user clicks the button/link                       jQuery = button:contains("Next")
     the user uploads the file                             css = .inputfile   testing_5MB.pdf
     the user enters empty data into date fields           01  12  2500
     the user clicks the button/link                       jQuery = button:contains("Mark as complete")
     the user fills in the funding information             ${ktpApplicationTitle}   no
     the user clicks the button/link                       link = Your project costs
     the user fills in ktp project costs
     Given the user clicks the button/link                  link = Back to application overview

the user fills in ktp project costs
    the user fills in Associate employment
    the user fills in Associate development
    the user clicks the button/link             exceed-limit-yes
    And Input Text                              css = .textarea-wrapped .editor  This is some random text
    the user clicks the button/link             css = label[for="stateAidAgreed"]
    the user clicks the button/link             jQuery = button:contains("Mark as complete")

the user enters empty data into date fields
    [Arguments]  ${date}  ${month}  ${year}
    the user enters text to a text field   id = fecCertExpiryDay  ${date}
    the user enters text to a text field   id = fecCertExpiryMonth   ${month}
    the user enters text to a text field   id = fecCertExpiryYear  ${year}

the user fills in Associate employment
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element   jQuery = table[id="${associateSalaryTable}"]
    Run Keyword If  '${status}' == 'PASS'    the user clicks the button/link         jQuery = button:contains("Associate employment")
    the user enters text to a text field    jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td input[id$="duration"]  ${costsValue}
    the user enters text to a text field    jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td input[id$="cost"]  ${costsValue}

the user fills in Associate development
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element   jQuery = table[id="${associateDevelopmentTable}"]
    Run Keyword If  '${status}' == 'PASS'    the user clicks the button/link         jQuery = button:contains("Associate development")
    the user enters text to a text field    jQuery = table[id="${associateDevelopmentTable}"] td:contains("Associate 1") ~ td input[id$="cost"]  ${costsValue}

the assessor submits the assessment for the application
    the assessor adds score and feedback for every assessor question    10
    the user clicks the button/link                            link = Scope
    Assessor completes the scope section of an application
    the user clicks the button/link               link = Review and complete your assessment
    the user selects the radio button             fundingConfirmation  true
    the user enters text to a text field          id = feedback    Assessor as a service application assessed
    the user clicks the button/link               jQuery = .govuk-button:contains("Save assessment")
    the user selects the checkbox                 id = assessmentIds1
    the user clicks the button/link               jQuery = .govuk-button:contains("Submit assessments")
    the user clicks the button/link               jQuery = button:contains("Yes I want to submit the assessments")

the assessor adds score and feedback for every assessor question
    [Arguments]   ${no_of_questions}
    The user clicks the button/link                       link = 1. Business opportunity
    The user selects the index from the drop-down menu    7    jQuery = select:nth-of-type(1)
    The user enters text to a text field                  css = .editor    Feedback Text!
    Wait for autosave
    mouse out  css = .editor
    Wait Until Page Contains Without Screenshots          Saved!
    :FOR  ${INDEX}  IN RANGE  1  ${no_of_questions}
      \    the user clicks the button/link    css = .next
      \    The user selects the option from the drop-down menu    10    css = .assessor-question-score
      \    The user enters text to a text field    css = .editor    Testing feedback text
      \    Wait for autosave
      \    mouse out  css = .editor
      \    Wait Until Page Contains Without Screenshots    Saved!
    The user clicks the button with resubmission              jquery = button:contains("Save and return to assessment overview")
    ${error} =   Run Keyword and return status without screenshots     page should contain     An unexpected error occurred.
    Run Keyword If    '${error}' == 'True'                             the user clicks the button/link   jQuery = button:contains("Save and return to assessment overview")

the user accepts the application to assess
    the user clicks the button/link       jQuery = li:contains("${applicationTitle}") a:contains("Accept or reject")
    the user selects the radio button     assessmentAccept  true
    the user clicks the button/link       jQuery = .govuk-button:contains("Confirm")

Assessor is invited to assess the AKT2I competition
    the user navigates to the page                 ${server}/management/competition/${competitionId}/assessors/find
    the user clicks the button/link                 jQuery = tr:contains("Hermen Mermen") label
    the user clicks the button/link                 id = add-selected-assessors-to-invite-list-button
    the user clicks the button/link                 id = review-and-send-assessor-invites-button
    the user clicks the button/link                 jQuery = button:contains("Send invitation")

Assessor accept the inviation to assess the AKT2I competition
    KTA accepts the invitation to assess the application      ${AKT2ICompName}   ${ktaEmail}   ${short_password}
    log in as a different user                                &{ifs_admin_user_credentials}
    the user navigates to the page                            ${server}/management/competition/${competitionId}/assessors/accepted
    the user should see the element                           link = Hermen Mermen

Comp Admin allocates assessor to application
    the user navigates to the page               ${server}/management/assessment/competition/${CompetitionID}/applications
    the user clicks the button/link              jQuery = tr:contains("${ktpApplicationTitle}") a:contains("Assign")
    the user adds an assessor to application     jQuery = tr:contains("Hermen Mermen") :checkbox
    the user navigates to the page               ${server}/management/competition/${competitionId}
    the user clicks the button/link              jQuery = button:contains("Notify assessors")

Allocated assessor assess the application
    Log in as a different user                               &{assessor3_credentials}
    KTA accepts to assess the KTP application                ${AKT2ICompName}   ${ktaEmail}   ${short_password}
    the user clicks the button/link                          link = ${ktpApplicationTitle}
    the assessor submits the assessment for the application