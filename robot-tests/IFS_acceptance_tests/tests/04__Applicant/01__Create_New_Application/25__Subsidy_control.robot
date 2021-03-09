*** Settings ***
Documentation     
...               IFS-9309 Tactical - Partner NI Protocol Question (Application)
...
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown

Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          ../../../resources/keywords/05__Email_Keywords.robot

*** Variables ***
${competitionName}                 WTO comp Subsidy control tactical
${applicationName}                 WTO comp Subsidy control tactical application

#Note: This suite will grow as the NI protocol functionality is added!

*** Test Cases ***
Applicant Applies to Sub control competition
    [Documentation]  IFS-9309
    Given logged in user applies to competition        ${competitionName}   4
    When the user clicks the button/link               link = Application details
    Then the user fills in the Application details     ${applicationName}  ${tomorrowday}  ${month}  ${nextyear}

Applicant completes the Project details section
    [Documentation]  IFS-9309
    Given the applicant fills in the Subsidy Basis question
    And the applicant completes Application Team
    When the user selects research category                     Feasibility studies
    Then the applicant marks EDI question as complete

Applicant completes the questions and terms
    [Documentation]  IFS-9309
    Given the lead applicant fills all the questions and marks as complete(programme)
    When the user accept the competition terms and conditions                             Return to application overview
    Then the user navigates to Your-finances page                                         ${applicationName}

Applicant completes the finances sections
    [Documentation]  IFS-9309
    Given the user clicks the button/link                                     link = Your project costs
    And the user fills in Other costs
    And the user clicks the button/link                                       css = label[for="stateAidAgreed"]
    And the user clicks the button/link                                       jQuery = button:contains("Mark as complete")
    When the user enters the project location
    And the user fills the organisation details with Project growth table     ${applicationName}  ${SMALL_ORGANISATION_SIZE}
    And the user clicks the button/link                                       link = Your funding
    And the user selects the radio button                                     requestingFunding   false
    And the user selects the radio button                                     otherFunding  false
    Then the user clicks the button/link                                      jQuery = button:contains("Mark as complete")

Applicant submits the applicantion
    [Documentation]  IFS-9309
    Given the user clicks the button/link          link = Back to application overview
    When the applicant submits the application
    Then the user clicks the button/link           link = Back to applications

*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser     &{lead_applicant_credentials}
    Connect to database                 @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database
