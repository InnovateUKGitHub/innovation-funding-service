*** Settings ***
Documentation     IFS-12745 Adding new template to support KTP AKT
...
...               IFS-12746 KTA is optional for KTP AKT funding type
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${AKT2ICompName}                Access Knowledge Transfer to Innovate Competition
${aktLeadEmail}                 akt.ktp@gmail.com
&{aktLeadCredentials}           email=${aktLeadEmail}  password=${short_password}

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
