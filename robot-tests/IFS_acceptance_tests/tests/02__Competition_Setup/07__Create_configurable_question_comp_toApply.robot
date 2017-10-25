*** Settings ***
Documentation     This suite creates a comp with configurable questions and enables an applicant to apply for it
...
...               As a comp exec I am able to configure Assessed questions in Competition setup
...
Suite Setup       the user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin  Applicant
Resource          ../../resources/defaultResources.robot
Resource          CompAdmin_Commons.robot
*** Variables ***
${compName_programme}  programme configurable comp
${compName_generic}    generic configurable comp

*** Test Cases ***
User creates a non generic competition
    [Documentation]    IFS-182
    [Tags]
    When the user navigates to the page                ${CA_UpcomingComp}
    And the user clicks the button/link                 jQuery=.button:contains("Create competition")
    then the user fills in all the sections except application questions   ${compName_programme}  ${compType_Programme}
    the user clicks the button/link                     link=Return to setup overview
    the user clicks the button/link                     link=Application
#    checking for exisintg questions - remove option exists
    the user should see the element                     jQuery=li:contains("Business opportunity") .task-status:contains("Remove")
    the user clicks the button/link                     jQuery=button:contains("Add question")
#   TODO-- Ensure edit view is rendered on first click ---dependant on IFS-743
    then the user should not see the element            jQuery=button:contains("Edit this question")
#    check the default values for the assessed  questions
    the user should see the text in the element        id=question.maxWords  400
#       the user should see the text in the element         input[id="appendix-no"] checked="checked"
#   TODO-- IFS-2004 this remove link needs to be added
#    and the user should see the element                 link=Remove
#   TODO -- Return to application questions link is not addded yet
#   the user should see the element                      link=Return to application questions
    the user fills in the assessed question details     First question
    the user clicks the button/link                     link=Save and close
    the user should see the element                     jQuery=li:contains("11.First question") .task-status:contains("Remove")
#    add one more question
    the user clicks the button/link                     link=Add question
#   TODO -- Adding question can be made a separate keyword and can be reused
    the user fills in the assessed question details     Second question
    the user clicks the button/link                     css=input[value="Save and close"]
     # checking the question number is correctly reset
    the user clicks the button/link                     jQuery=li:contains("11.First question") .task-status:contains("Remove")
    then the user clicks the button/link                jQuery=li:contains("11.Second question")
#    check the complete status once the question is marked Done
    then the user should not see the element            css=input   # read only view is rendered
    the user clicks the button/link                     jQuery=a:contains("Edit this question")
    when the user clicks the button/link                css=input[value="Save and close"]
    then the user should see the element                jQuery=li:contains("11.Second question")  .task complete:contains("complete")
    the user should not see the element                 jQuery=li:contains("11.Second question") .task-status:contains("Remove")
    the user clicks the button/link                     jQuery=button:contains("Done")
    the user clicks the button/link                     jQuery=a:ccontains("Return to setup overview")
    the user clicks the button/link                     jQuery=a:contains("Complete")
    Then the user clicks the button/link                jQuery=a:contains("Done")

User able to open the comp
    Given the user navigates to the page                                      ${CA_UpcomingComp}
    Then the user should see the element                                       jQuery=h2:contains("Ready to open") ~ ul a:contains("${compName_programme}}")
    Change the open date of the Competition in the database to one day before  ${compName_programme}
    lead applicant able to apply to newly created comp                         {compName_programme}
    lead applicant can fill in the application questions and mark it complete

User can create a new generic comp
    the user fills in all the sections except application questions  ${compName_generic}  ${compType_Generic}
    the user clicks the button/link                                  link=Application
#    please note the default view is read only for first question in generic .  TODO  Dependant on IFS-743
    the user should see the element                                  jQuery=li:contains("1.Edit this question")
    the user should not see the element                              jQuery=li:contains("Edit this question") .taskcompete:contains("Remove")
    the user clicks the button/link                                  Add question
    the user fills in the assessed question details                  Generic second question
    the user clicks the button/link                                  css=input[value="Save and close"]
    the user should see the element                                  jQuery=li:contains("Generic second question) .task-status:contains("Complete")
    the user should not see the element                              jQuery=li:contains("Generic second question) .task-status:contains("Remove")
    the user clicks the button/link                                  link=Done

User reset Db to open the comp
    Given  the user navigates to the page                                      ${CA_UpcomingComp}
    Then the user should see the element                                       jQuery=h2:contains("Ready to open") ~ ul a:contains("${compName_generic}}")
    Change the open date of the Competition in the database to one day before   ${compName_generic}

Applicant able to apply to generic comp
    lead applicant able to apply to newly created comp                          ${comptype_generic}
    lead applicant can fill in the application questions and mark it complete

*** Keywords ***
# TODO -- Need to move date variables into common setup actions file
System generates date variables
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    ${month} =  get tomorrow month
    set suite variable  ${month}
    ${nextMonth} =  get next month
    set suite variable  ${nextMonth}
    ${nextYear} =       get next year
    Set suite variable  ${nextyear}

the user fills in all the sections except application questions
    [Arguments]   ${competition_name}  ${compType}
    System generates date variables
    the user fills in the CS Initial details            ${competition_name}  ${month}  ${nextyear}  ${compType}
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility                ${BUSINESS_TYPE_ID}
    the user fills in the CS Milestones                 ${month}  ${nextMonth}  ${nextYear}
    the user fills in the CS Assessors                 # TODO check if we can leave this section to open a comp
    the user clicks the button/link                     link=Public content
    the user fills in the Public content and publishes  Configurable comp

the user navigates to the eligibility of the competition
    [Arguments]  ${competition_name}
    ${competitionId} =               get comp id from comp title    ${competition_name}
    the user navigates to the page   ${server}/application/create/check-eligibility/${competitionId}

lead applicant able to apply to newly created comp
    [Arguments]  ${competition_name}
    log in as a different user                                &{lead_applicant_credentials}
    the user navigates to the eligibility of the competition  ${competition_name}
    the user clicks the button/link                           jQuery=a:contains("Sign in")
    the user clicks the button/link                           jQuery=a:contains("Begin application")

lead applicant can fill in the application questions and mark it complete
    the user clicks the button/link       link=Application overview
    the user clicks the button/link       link= new generic question
    the user enters text to a text field  css=.textarea-wrapped .editor  Applicant filling newly added question

# TODO -- Move this keyword to compAdmin if this is needed for IFS-743 or any other tests
the user fills in the assessed question details
    [Arguments]  ${question_heading}
    the user enters text to a text field  id=question.shortTitle  ${question_heading}
    the user enters text to a text field  id=question.title  sample text
    the user enters text to a text field  id=question.guidanceTitle   sample text
    the user enters text to a text field  id=question.guidance  sample text
    the user enters text to a text field  id=question.scoreTotal  sample text
    the user enters text to a text field  id=question.assessmentGuidanceTitle  sample text
    the user enters text to a text field  id=guidanceRow-0-justification  sample text
    the user enters text to a text field  id=guidanceRow-1-justification  sample text
    the user enters text to a text field  id=guidanceRow-2-justification  sample text
    the user enters text to a text field  id=guidanceRow-3-justification  sample text
    the user enters text to a text field  id=guidanceRow-4-justification  sample text

