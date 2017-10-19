*** Settings ***
Documentation     This suite creates a comp with configurable questions and enables an applicant to apply for it
...
...               As a comp exec I am able to configure Assessed questions in Competition setup
...
Suite Setup       the user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin  Applicant  Pending
Resource          ../../resources/defaultResources.robot
Resource          CompAdmin_Commons.robot
*** Test Cases ***
User creates a non generic competition
    [Documentation]    IFS-182
    [Tags]
    When  the user navigates to the page                ${CA_UpcomingComp}
    And the user clicks the button/link                 jQuery=.button:contains("Create competition")
    then the user fills in all the sections except application questions  programme
    the user clicks the button/link                     link=Appplication
#    then the user should see the element                jQuery=.buttonlink:contains("Add question")
    the user clicks the button/link                     link=Add question
    then the user should see the element                jQuery=li:contains("Edit this question") .task-status:contains("Remove")
    When the user clicks the button/link                jQuery=li:contains("Edit this question")
#   this is based on assumption we give read ony view on first opening the section but i think it might be implemented wrong way
    then the user should not see the element            css= input  # read only view check
#    check the default values for the assessed  questions
     the user should see the text in the element         Max word count  400
     the user should see the text in the element         Apeendix defaulted to  NO
    and the user should see the element                 link=Remove
    the user clicks the button/link                     link=Edit this question
    the user enters text to a text field                titleid   new question
    the user clicks the button/link                     link=Save and close
#    add one more question
    the user clicks the button/link                     link=Add question
    the user clicks the button/link                     jQuery=li:contains(" Question number.Edit this question") .task-status:contains("Remove")
    the user clicks the button/link                     jQuery=li:contains("new question") .task-status:contains("Remove")
    then the user should see the element                 11.edit this question   # checking the number is correctly reset
#    Application questions are  marked done
    when the user clicks the button/link                link = Done
    then the user should see the element                .task complete : contains("complete")
    and the user should not see the element             link=Remove

User able to open the comp
    given the comp appears in ready to open section
    Set in Db : Update the milestones to have the comp open for today

Applicant able to apply to newly created comp
    log in steve smith
    Apply for the comp       non generic one
    begin application
    Applicant overview check the application questions are as set in comp setup
    Applicant can fill in the application questions and mark it complete

User can create a new generic comp
    the user fills in all the sections except application questions   generic comp
    the user clicks the button/link         link=Application
    the user should see the element         li : contains("1.Edit this question")
    the user should not see the element     li: contains("Edit this question") .taskcompete:contains("Remove")
    the user clicks the button/link         Add question
    the user should see the element         jQuery=li:contains("Edit this question") .task-status:contains("Remove")
    the user clicks the button/link         link=1.Edit this question
     the user clicks the button/link        link=Edit this question
    the user enters text to a text field    titleid   new generic question
    the user clicks the button/link          link =Save and close
    the user clicks the button/link          link=Add question
    the user navigates to comp set up overivew and
    the user clicks the button/link         button:contains("Complete")

user reset Db to open the comp
    connect to Db
    reset the open and close date of the competition in the database

Applicant able to apply to generic comp
    Applicant able to apply to newly created comp   ${comptype_generic}
    begin application
    application_overview
    check the  application questions as created in comp setup

*** Keywords ***
the user fills in all the sections except application questions
    the user fills in the CS Initial details            Sector comp  # TODO check programme comp manually
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility
    the user fills in the CS Milestones
#    the user fills in the CS Assessors  # TODO check if we can leave this section to open a comp
    the user fills in the Public content and publishes
