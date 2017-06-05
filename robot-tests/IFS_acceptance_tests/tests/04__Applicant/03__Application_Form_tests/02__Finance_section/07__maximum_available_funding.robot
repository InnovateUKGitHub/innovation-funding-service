*** Settings ***
Documentation       IFS-338 Update 'Funding level' calculated maximum values and validation
Suite Setup         the guest user opens the browser
Suite Teardown      the user closes the browser
Force Tags          Applicant
Resource          ../../../../resources/defaultResources.robot
Resource            ../../FinanceSection_Commons.robot
*** Variables ***
${newUsersEmail}             liamCharity@innovateuk.com
${Application_name}           Maximum Funding Allowed for Business
*** Test Cases ***
funding level available for lead business ResearchCategory: Fundamental Research
    [Documentation]    IFS-338
    [Tags]
    Given we create a new user                           ${COMPETITION_WITH_MORE_THAN_ONE_INNOVATION_AREAS}   Oscar  business   oscar@innovateuk.com
    When the user clicks the button/link                 link=Untitled application (start here)
    and the user clicks the button/link                  link=Begin application
    and the applicant completes the application details  Application details  Experimental development
    and the user clicks the button/link                  link=Your finances
    then the user fills in the project costs
    the user clicks the button/link                          link=Your organisation
    the user enters text to a text field                    css=input[name$="month"]    12
    and the user enters text to a text field                css=input[name$="year"]    2016
    the user selects the radio button                       financePosition-organisationSize  ${SMALL_ORGANISATION_SIZE}
    the user enters text to a text field                    jQuery=td:contains("Annual turnover") + td input   5600
    the user enters text to a text field                    jQuery=td:contains("Annual profit") + td input    3000
    the user enters text to a text field                    jQuery=td:contains("Annual export") + td input    4000
    the user enters text to a text field                    jQuery=td:contains("Research and development spend") + td input    5660
    the user enters text to a text field                    jQuery=label:contains("employees") + input    0
    the user clicks the button/link                         jQuery=button:contains("Mark as complete")
    the user clicks the button/link                          link=Your funding
   the user should see the text in the page                  Enter your funding level (maximum 45%).
   the user clicks the button/link                      jQuery=a:contains("Your finances")
   the user clicks the button/link                           link=Application overview
   the user clicks the button/link                           link=Application details
   the user clicks the button/link                          jQuery=button:contains("Edit")
   the user clicks the button/link                          jQuery=button:contains("research category")
   the user clicks the button/link                          jQuery=label[for^="researchCategoryChoice"]:contains("Feasibility studies")
   the user clicks the button/link                          jQuery=label[for^="researchCategoryChoice"]:contains("Feasibility studies")
   the user clicks the button/link       jQuery=button:contains(Save)
   the user clicks the button/link                          jQuery=button:contains("Mark as complete")
   the user clicks the button/link                          link=Application overview
   and the user clicks the button/link                  link=Your finances
    the user clicks the button/link                        link=Your organisation
    the user clicks the button/link                        jQuery=button:contains("Edit")
    the user selects the radio button                       financePosition-organisationSize  ${MEDIUM_ORGANISATION_SIZE}
    the user clicks the button/link                         jQuery=button:contains("Mark as complete")
    and the user clicks the button/link                  link=Your funding
    the user should see the text in the page              Enter your funding level (maximum 60%).
    the user clicks the button/link                      jQuery=a:contains("Your finances")
    the user clicks the button/link                           link=Application overview
    the user clicks the button/link                           link=Application details
    the user clicks the button/link                          jQuery=button:contains("Edit")
    the user clicks the button/link                          jQuery=button:contains("research category")
    the user clicks the button/link                          jQuery=label[for^="researchCategoryChoice"]:contains("Industrial research")
    the user clicks the button/link                          jQuery=label[for^="researchCategoryChoice"]:contains("Industrial research")
    the user clicks the button/link       jQuery=button:contains(Save)
    the user clicks the button/link                          jQuery=button:contains("Mark as complete")
    the user clicks the button/link                          link=Application overview
    and the user clicks the button/link                  link=Your finances
    the user clicks the button/link                        link=Your organisation
    the user clicks the button/link                        jQuery=button:contains("Edit")
    the user selects the radio button                       financePosition-organisationSize  ${LARGE_ORGANISATION_SIZE}
    the user clicks the button/link                         jQuery=button:contains("Mark as complete")
     and the user clicks the button/link                  link=Your funding
    the user should see the text in the page              Enter your funding level (maximum 50%).
     the user clicks the button/link                      jQuery=a:contains("Your finances")
        the user clicks the button/link                           link=Application overview

funding level available for business user ResearchCategory: Feasibility Studies
    [Documentation]    IFS-338
    [Tags]  Pending

funding level available for business user ResearchCategory: Industrial Research
    [Documentation]    IFS-338
    [Tags]  Pending

funding level available for business user ResearchCategory: Experimental Development
    [Documentation]    IFS-338
    [Tags]  Pending

lead applicant invites a Charity member
    [Documentation]    IFS-338
    [Tags]
    #  the user clicks the button/link                        link=view team members and add collaborators
    Invite a non-existing collaborator
    the user navigates to your-finances page                  link=${Application_name}
    the user fills in the project costs
    the user clicks the button/link                            link=Your organisation
    the user enters text to a text field                    css=input[name$="month"]    12
    and the user enters text to a text field                css=input[name$="year"]    2016
    the user selects the radio button                       financePosition-organisationSize  ${SMALL_ORGANISATION_SIZE}
       the user enters text to a text field                    jQuery=td:contains("Annual turnover") + td input   5600
       the user enters text to a text field                    jQuery=td:contains("Annual profit") + td input    3000
       the user enters text to a text field                    jQuery=td:contains("Annual export") + td input    4000
       the user enters text to a text field                    jQuery=td:contains("Research and development spend") + td input    5660
       the user enters text to a text field                    jQuery=label:contains("employees") + input    0
       the user clicks the button/link                         jQuery=button:contains("Mark as complete")
    and the user clicks the button/link                       link=Your finances
    and the user clicks the button/link                       link=Your funding
    the user should see the text in the page                  Enter your funding level (maximum 100%).

Newly invited Charity member allowed funding level
    [Documentation]  IFS-338
    [Tags]     Pending
    Invite a non-existing collaborator
    the user navigates to your-finances page                  link=Untitled Application(Start here)
    the user fills in the project costs
    and the user fills in the organisation information        ${SMALL_ORGANISATION_SIZE}
    and the user clicks the button/link                       link=Your finances
    and the user clicks the button/link                       link=Your funding
    the user should see the text in the page                  Enter your funding level (maximum 100%).

funding level available for RTO lead user ResearchCategory: Fundamental Research
    [Documentation]  IFS-338
    [Tags]      Pending
    Given we create a new user                           ${OPEN_COMPETITION}  Smith  rto   oscar@innovateuk.com
    When the user clicks the button/link                 link=Untitled application (start here)
    and the user clicks the button/link                  link=Begin application
    and the user clicks the button/link                  link=Your finances
    then the user fills in the project costs             Untitled application (start here)
    and the user fills in the organisation information   ${SMALL_ORGANISATION_SIZE}
    #  fill the application details
    and the applicant completes the application details   application_details  Experimental development
    and the user clicks the button/link                  link=Your finances
    and the user clicks the button/link                  link=Your funding
    the user should see the text in the page             Enter your funding level (maximum 45%).
    the user clicks the button/link                      jQuery=a:contains("Return to finances")
    the user fills in the organisation information       ${MEDIUM_ORGANISATION_SIZE}
    and the applicant completes the application details   application_details  Feasibility studies
    and the user clicks the button/link                   link=Your finances
    and the user clicks the button/link                   link=Your funding
    the user should see the text in the page              Enter your funding level (maximum 60%).
    the user clicks the button/link                       jQuery=a:contains("Return to finances")
    the user fills in the organisation information        ${LARGE_ORGANISATION_SIZE}
    and the applicant completes the application details   application_details  Industrial research
    and the user clicks the button/link                   link=Your finances
    and the user clicks the button/link                   link=Your funding
    the user should see the text in the page              Enter your funding level (maximum 50%)
    the user clicks the button/link                       jQuery=a:contains("Return to finances")



funding level available for RTO user ResearchCategory : Feasibility Studies
    [Documentation]    IFS-338
    [Tags]

funding level available for RTO user ResearchCategory : Fundamental Research
    [Documentation]    IFS-338
    [Tags]

lead applicant invites a Public sector member
    [Documentation]    IFS-338
    [Tags]

Newly invited Public sector member allowed funding level
    [Documentation]    IFS-338
    [Tags]

*** Keywords ***
the user navigates to the competition overview
    the user navigates to the page    ${frontDoor}

the applicant completes the application details
    [Arguments]   ${Application_details}   ${Research_category}
    the user clicks the button/link       link=${Application_details}
    the user enters text to a text field   id=application_details-title  ${Application_name}
    the user clicks the button/link       jQuery=button:contains("research category")
    the user clicks the button/link       jQuery=label[for^="researchCategoryChoice"]:contains("${Research_category}")
    the user clicks the button/link       jQuery=label[for^="researchCategoryChoice"]:contains("${Research_category}")
    the user clicks the button/link       jQuery=button:contains(Save)
    the user clicks the button/link        jQuery=button:contains("Choose your innovation area")
    the user clicks the button/link         jQuery=label[for^="innovationAreaChoice-22"]:contains("Digital manufacturing")
     the user clicks the button/link         jQuery=label[for^="innovationAreaChoice-22"]:contains("Digital manufacturing")
     the user clicks the button/link       jQuery=button:contains(Save)
    the user clicks the button/link       jQuery=label[for="application.resubmission-no"]
    the user clicks the button/link       jQuery=label[for="application.resubmission-no"]
    # those Radio buttons need to be clicked twice.
    The user enters text to a text field  id=application_details-startdate_day  18
    The user enters text to a text field  id=application_details-startdate_year  2018
    The user enters text to a text field  id=application_details-startdate_month  11
    The user enters text to a text field  id=application_details-duration  20
    the user clicks the button/link       jQuery=button:contains("Mark as complete")
    the user clicks the button/link       link=Application overview

Invite a non-existing collaborator
    the user should see the element       jQuery=h1:contains("Application overview")
    the user fills in the inviting steps  ${newUsersEmail}
    newly invited collaborator can create account and sign in    ${Application_name}

the user fills in the inviting steps
    [Arguments]  ${email}
    the user clicks the button/link       link=view team members and add collaborators
    the user clicks the button/link       link=Add partner organisation
    the user enters text to a text field  css=#organisationName  New Organisation's Name
    the user enters text to a text field  css=input[id="applicants0.name"]  Partner's name
    the user enters text to a text field  css=input[id="applicants0.email"]  ${email}
    the user clicks the button/link       jQuery=button:contains("Add organisation and invite applicants")
    logout as user

Newly invited collaborator can create account and sign in
    [Arguments]  ${Application_name}
    the user reads his email and clicks the link   ${newUsersEmail}  Invitation to collaborate in ${Application_name}  You will be joining as part of the organisation  3
    the user clicks the button/link    jQuery=a:contains("Yes, accept invitation")
    the user should see the element    jquery=h1:contains("Choose your organisation type")
    the user completes the new account creation

the user completes the new account creation
    the user selects the radio button    organisationType    radio-4
    the user clicks the button/link    jQuery=button:contains("Continue")
    the user should see the element    jQuery=span:contains("Create your account")
    the user enters text to a text field    id=organisationSearchName    innovate
    the user should see the element    jQuery=a:contains("Back to choose your organisation type")
    the user clicks the button/link    jQuery=button:contains("Search")
    wait for autosave
    the user clicks the button/link    jQuery=a:contains("INNOVATE LTD")
    the user should see the element    jQuery=h3:contains("Organisation type")
    the user selects the checkbox    address-same
    wait for autosave
    the user clicks the button/link    jQuery=button:contains("Continue")
    then the user should not see an error in the page
    the user clicks the button/link    jQuery=.button:contains("Save and continue")
    the user should be redirected to the correct page    ${SERVER}/registration/register
    the user enters text to a text field    jQuery=input[id="firstName"]    liam
    the user enters text to a text field    JQuery=input[id="lastName"]    smithson
    the user enters text to a text field    jQuery=input[id="phoneNumber"]    077712567890
    the user enters text to a text field    jQuery=input[id="password"]    ${correct_password}
    the user selects the checkbox    termsAndConditions
    the user clicks the button/link    jQuery=button:contains("Create account")
    the user should see the text in the page    Please verify your email address
    the user reads his email and clicks the link  ${newUsersEmail}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    the user clicks the button/link    link=Sign in
    then the user should see the text in the page    Sign in
    the user enters text to a text field    jQuery=input[id="username"]  ${newUsersEmail}
    the user enters text to a text field    jQuery=input[id="password"]  ${correct_password}
    the user clicks the button/link    jQuery=button:contains("Sign in")

the user fills in the project costs
    the user clicks the button/link             link=Your project costs
    the user selects the checkbox               agree-state-aid-page
    the user clicks the button/link             jQuery=button:contains("Mark as complete")

the user fills in the organisation information
    [Arguments]   ${Org_size}
    the user clicks the button/link                          link=Your organisation
    ${STATUS}    ${VALUE}=  Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery=button:contains("Edit")
    Run Keyword If    '${status}' == 'PASS'    the user clicks the button/link  jQuery=button:contains("Edit")
    the user selects the radio button                       financePosition-organisationSize  ${Org_size}
    the user enters text to a text field                    jQuery=label:contains("Turnover") + input    150
    the user enters text to a text field                    jQuery=label:contains("employees") + input    0
    the user clicks the button/link                         jQuery=button:contains("Mark as complete")




