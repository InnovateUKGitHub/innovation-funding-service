*** Settings ***
Documentation   IFS-2688 As a Portfolio manager I am able to create a Prince's Trust competition
...
...             IFS-3287 As a Portfolio Manager I am able to switch off requirement for Research category
...
...             IFS-7718 EDI question - application form
...
...             IFS-8779 Subsidy Control - Create a New Competition - Initial Details
...
...             IFS-8847 Always open competitions: new comp setup configuration
...
Suite Setup     Custom suite setup
Suite Teardown  Custom suite teardown
Resource        ../../../resources/defaultResources.robot
Resource        ../../../resources/common/Applicant_Commons.robot
Resource        ../../../resources/common/Competition_Commons.robot


*** Variables ***
${comp_name}         Princes Trust Comp 1
${application_name}  Princes Trust Application

*** Test Cases ***
Applicant applies to newly created The Prince's Trust competition
    [Documentation]  IFS-2688
    [Tags]
    Given the user logs-in in new browser           &{rto_lead_applicant_credentials}
    Then logged in user applies to competition      ${comp_name}  3

Applicant submits his application
    [Documentation]  IFS-2688  IFS-3287  IFS-5920  IFS-7718
    [Tags]
    Given the user clicks the button/link                            link = Application details
    When the user fills in the Prince's Trust Application details    ${application_name}  ${tomorrowday}  ${month}  ${nextyear}
    And the user completes all other sections of an application
    And the user selects PT Research category          Feasibility studies
    Then the applicant submits the application

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

the lead applicant fills all the questions and marks as complete(Prince's Trust comp type)
    the applicant completes application team
    then the user selects research category  Feasibility studies
    :FOR  ${ELEMENT}    IN    @{EOI_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

the user marks the Application as done(Prince's Trust comp)
    the user clicks the button/link                          link=Application
    the user marks each question as complete                 Application details
    the user marks each question as complete                 Equality, diversity and inclusion
    the assessed questions are marked complete(EOI type)
    the user opts no finances for EOI comp
    the user clicks the button/link                          jQuery=button:contains("Done")
    the user clicks the button/link                          link = Back to competition details
    the user should see the element                          jQuery=div:contains("Application") ~ .task-status-complete

the lead applicant answers the four sections as complete
    the lead applicant marks every question as complete  1. Business opportunity and potential market
    the lead applicant marks every question as complete  2. Innovation
    the lead applicant marks every question as complete  3. Project team
    the lead applicant marks every question as complete  4. Funding and adding value

the user selects PT Research category
    [Arguments]  ${res_category}
    the user clicks the button/link   link=Research category
    # checking here applicant should see only one research category(checkbox) set while creating EOI compeition(IFS-2941 and IFS-4080)
    ${status}   ${value}=  Run Keyword And Ignore Error Without Screenshots   page should contain element    jQuery=h1 span:contains("Princes Trust Application")
    Run Keyword If  '${status}' == 'PASS'  Run keywords    the user should not see the element   css = label[for="researchCategory2"]
    ...    AND             the user should not see the element   css = label[for="researchCategory3"]
    ...    AND             the user selects the checkbox         researchCategory
    Run Keyword If    '${status}' == 'FAIL'    the user clicks the button twice      jQuery = label:contains("${res_category}")
    the user clicks the button/link            id=application-question-complete
    the user clicks the button/link            link=Back to application overview
    the user should see the element            jQuery=li:contains("Research category") > .task-status-complete

the user completes all other sections of an application
    the applicant completes application team
    #the applicant marks EDI question as complete
    the lead applicant answers the four sections as complete
    the user accept the competition terms and conditions         Return to application overview
    the user should not see the element                          jQuery = h2:contains("Finances")

Custom suite teardown
    Close browser and delete emails
    Disconnect from database