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

*** Test Cases ***
Applicant Applies to Sub control competition
    [Documentation]  IFS-9309
    [Setup]  Log in as a different user                            becky.mason@gmail.com  ${short_password}
    And the user select the competition and starts application     ${competitionName}
    And the user apply with knowledge base organisation            Reading    The University of Reading
    #When the user completes funding level in application
    #Then the user should see the element                           jQuery = dt:contains("Funding level")+dd:contains("100.00%")
    #And the user should see the element                            jQuery = p:contains("No other funding")
    When the lead user completes project details, application questions and finances sections
    Then the user checks the status of the application before completion
    When the user clicks the button/link            link = Application details
    Then the user fills in the Application details  Application Ts&Cs  ${tomorrowday}  ${month}  ${nextyear}
    When the user clicks the button/link            link = Award terms and conditions
    Then the user should see the element            jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should see the element             jQuery = .message-alert:contains("You must read these terms and conditions and accept them by ticking the box at the end")




Lead applicant completes the application and checks the dashboard content before the application is submitted
    [Documentation]  IFS-8850
    Given the user clicks the button/link                                   link = ${applicationName}
    When the user accept the competition terms and conditions               Back to application overview
    Then the user checks the status of the application after completion

Lead applicant submits the application and checks the dashboard content and the guidance after submission
    [Documentation]  IFS-8850
    Given the user clicks the button/link                                  link = ${applicationName}
    When the user clicks the button/link                                   link = Review and submit
    And the user clicks the button/link                                    jQuery = button:contains("Submit application")
    Then the user checks the status of the application after submission



*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser  &{lead_applicant_credentials}
    Connect to database  @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database
