*** Settings ***
Documentation       IFS-338 Update 'Funding level' calculated maximum values and validation
Suite Setup         the guest user opens the browser
Test Setup          the user navigates to the competition overview
Suite Teardown      the user closes the browser
Force Tags          Applicant
Resource            ../../../resources/defaultResources.robot
Resource            ../../FinanceSection_Commons.robot
*** Test Cases ***
funding level available for lead business ResearchCategory: Fundamental Research
    [Documentation]    IFS-338
    [Tags]
    Given we create a new user                           Oscar  business   oscar@innovateuk.com
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
    the user navigates to your-finances page                  link=Untitled Application(Start here)
    the user fills in the project costs
    and the user fills in the organisation information        ${SMALL_ORGANISATION_SIZE}
    and the user clicks the button/link                       link=Your finances
    and the user clicks the button/link                       link=Your funding
    the user should see the text in the page                  Enter your funding level (maximum 100%).

Newly invited Charity member allowed funding level
    [Documentation]  IFS-338
    [Tags]
    Invite a non-existing collaborator
    the user navigates to your-finances page                  link=Untitled Application(Start here)
    the user fills in the project costs
    and the user fills in the organisation information        ${SMALL_ORGANISATION_SIZE}
    and the user clicks the button/link                       link=Your finances
    and the user clicks the button/link                       link=Your funding
    the user should see the text in the page                  Enter your funding level (maximum 100%).

funding level available for RTO lead user ResearchCategory: Fundamental Research
    [Documentation]  IFS-338
    [Tags]
    Given we create a new user      radio-3    # RTO
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
    the user enters text to a text field   id=application_details-title  Funding Application Checks
    the user clicks the button/link       jQuery=button:contains("research category")
    the user clicks the button/link       jQuery=label[for^="researchCategoryChoice"]:contains("${Research_category}")
    the user clicks the button/link       jQuery=label[for^="researchCategoryChoice"]:contains("${Research_category}")
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
    newly invited collaborator can create account and sign in    ${UNTITLED_APPLICATION_NAME}

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
    the user reads his email and clicks the link  ${newUsersEmail}  Invitation to collaborate in ${Application_name}  You will be joining as part of the organisation  3
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




