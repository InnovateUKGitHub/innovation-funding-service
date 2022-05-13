*** Settings ***
Resource          ../defaultResources.robot
Resource          ../common/Applicant_Commons.robot

*** Keywords ***
log in and create new application if there is not one already
    [Arguments]  ${application_name}
    Given the user logs-in in new browser  &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain   ${application_name}
    Run Keyword If    '${status}' == 'FAIL'    Run keywords  Create new application with the same user  ${application_name}  ${BUSINESS_TYPE_ID}
    ...                                AND     the user selects Research category                       Industrial research

Login new application invite academic
    [Arguments]  ${recipient}  ${subject}  ${pattern}
    [Tags]  Email
    Logging in and Error Checking                         &{lead_applicant_credentials}
    the user clicks the application tile if displayed
    ${STATUS}  ${VALUE} =  Run Keyword And Ignore Error Without Screenshots  Page Should Contain  Academic robot test application
    Run Keyword If  '${status}' == 'FAIL'  Run keywords  Create new application with the same user  Academic robot test application  1
    ...                                            AND   Invite and accept the invitation  ${recipient}  ${subject}  ${pattern}

the user marks every section but one as complete - research
    [Arguments]  ${application_name}  ${rescat}  ${ediStatus}  ${userEmail}
    the user navigates to the page                                                 ${server}
    the user clicks the button/link                                                link=${application_name}
    the applicant completes Application Team                                       ${ediStatus}  ${userEmail}
    the user selects Research category                                             ${rescat}
    the lead applicant fills all the questions and marks as complete(programme)

the user selects Research category
    [Arguments]  ${res_category}
    the user clicks the button/link   link=Research category
    # checking here applicant should see only one research category(checkbox) set while creating EOI compeition(IFS-2941 and IFS-4080)
    ${status}   ${value}=  Run Keyword And Ignore Error Without Screenshots   page should contain element    jQuery=h1 span:contains("EOI Application")
    Run Keyword If  '${status}' == 'PASS'  Run keywords    the user should not see the element   css = label[for="researchCategory2"]
    ...    AND             the user should not see the element   css = label[for="researchCategory3"]
    ...    AND             the user selects the checkbox         researchCategory
    Run Keyword If    '${status}' == 'FAIL'    the user clicks the button twice      jQuery = label:contains("${res_category}")
    the user clicks the button/link            id=application-question-complete
    the user clicks the button/link            link=Back to application overview
    the user should see the element            jQuery=li:contains("Research category") > .task-status-complete

the lead applicant fills all the questions and marks as complete(programme)
    the user marks the project details as complete
    :FOR  ${ELEMENT}    IN    @{programme_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

the lead applicant fills all the questions and marks as complete(programme ATI)
    the user marks the project details as complete
    :FOR  ${ELEMENT}    IN    @{programme_questions_procurement_ati}
     \     the lead applicant marks every question as complete     ${ELEMENT}

the lead applicant fills all the questions and marks as complete(procurement)
    the user marks the project details as complete
    :FOR  ${ELEMENT}    IN    @{programme_questions_procurement_ati}
     \     the lead applicant marks every question as complete procurement    ${ELEMENT}

the lead applicant fills all the questions and marks as complete(thirdparty)
    the user marks the project details as complete
    :FOR  ${ELEMENT}    IN    @{Thirdparty_questions}
     \     the lead applicant marks every question as complete    ${ELEMENT}

the lead applicant fills all the questions and marks as complete(Hecp)
    :FOR  ${ELEMENT}    IN    @{Hecp_questions}
     \     the lead applicant marks the questions as complete (hecp)    ${ELEMENT}

the lead completes the questions with multiple answer choice and multiple appendices
    :FOR  ${ELEMENT}    IN    @{other_questions_procurement_ati}
         \     the lead applicant marks the questions as complete (multiple appendices and multiple answer choice)    ${ELEMENT}

the lead applicant fills all the questions and marks as complete(sector)
    the user marks the project details as complete
    :FOR  ${ELEMENT}    IN    @{sector_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

the user marks the project details as complete
    :FOR  ${ELEMENT}    IN    @{project_details}
     \     the lead applicant marks every question as complete     ${ELEMENT}

the lead applicant marks every question as complete
    [Arguments]  ${question_link}
    the user clicks the button/link             jQuery=h3 a:contains("${question_link}")
    the user marks the section as complete
    the user clicks the button/link             link=Back to application overview

the lead applicant marks every question as complete procurement
    [Arguments]  ${question_link}
    the user clicks the button/link                        jQuery=h3 a:contains("${question_link}")
    the user marks the section as complete procurement     ${question_link}
    the user clicks the button/link                        link=Back to application overview

the lead applicant marks the questions as complete (multiple appendices and multiple answer choice)
    [Arguments]  ${question_link}
    the user clicks the button/link     jQuery = h3 a:contains("${question_link}")
    the user can't complete the question without selecting answer (validation error messages check)
    Run Keyword If  '${question_link}' == 'Technical approach'                     the user clicks the button/link     jQuery = label:contains("option2")
    Run Keyword If  '${question_link}' in ["Technical approach", "Project team"]   the user uploads the file           css = input[name="appendix"]    ${valid_pdf}
    Run Keyword If  '${question_link}' in ["Technical approach", "Project team"]   the user uploads the file           css = input[name="appendix"]    ${ods_file}
    Run Keyword If  '${question_link}' == 'Technical approach'                     the user uploads the file           css = input[name="appendix"]    ${excel_file}
    Run Keyword If  '${question_link}' == 'Project team'                           wait until keyword succeeds without screenshots   10s    200ms     input text       id = multipleChoiceOptionId  answer7
    Run Keyword If  '${question_link}' == 'Project team'                           Execute Javascript                  document.evaluate("//li[text()='answer7']",document.body,null,9,null).singleNodeValue.click();
    the user clicks the button/link     name = complete
    the user clicks the button/link     link = Back to application overview

the lead applicant marks the questions as complete (hecp)
    [Arguments]  ${question_link}
    the user clicks the button/link     jQuery = h3 a:contains("${question_link}")
    Run Keyword If  '${question_link}' == 'Tell us where your organisation is based'                    the user clicks the button twice   jQuery = label:contains("My organisation is based in the UK or a British Overseas Territory")
    Run Keyword If  '${question_link}' == 'What EIC call have you been successfully evaluated for?'     the user clicks the button twice   jQuery = label:contains("EIC Transition")
    Run Keyword If  '${question_link}' == 'Application reference number'     the user enters text to a text field    css=.textarea-wrapped .editor    Entering text to allow valid mark as complete
    Run Keyword If  '${question_link}' == 'UK Funding contribution applied for (GBP)'     the user enters text to a text field    css=.textarea-wrapped .editor    Entering text to allow valid mark as complete
    Run Keyword If  '${question_link}' == 'If this amount has changed please tell us how?'     the user enters text to a text field    css=.textarea-wrapped .editor    Entering text to allow valid mark as complete
    Run Keyword If  '${question_link}' == 'If so, how many PhD students will be employed at your institution on this project?'     the user enters text to a text field    css=.textarea-wrapped .editor    Entering text to allow valid mark as complete
    Run Keyword If  '${question_link}' == 'How much budget is allocated for PhD students employed at your institution on this project?'     the user enters text to a text field    css=.textarea-wrapped .editor    Entering text to allow valid mark as complete
    Run Keyword If  '${question_link}' == 'Have the tasks assigned to your institution changed significantly since the original application?'      the user clicks the button twice   jQuery = label:contains("Yes")
    Run Keyword If  '${question_link}' == 'Will you, as a UK institution, be employing PhD students as part of this project?'      the user clicks the button twice   jQuery = label:contains("Yes")
    Run Keyword If  '${question_link}' == 'Participating Organisation project region'       run keywords    wait until keyword succeeds without screenshots   10s    200ms     input text       id = multipleChoiceOptionId  London
   ...                                            AND                                       Execute Javascript                  document.evaluate("//li[text()='London']",document.body,null,9,null).singleNodeValue.click();
    the user clicks the button/link     name = complete
    the user clicks the button/link     link = Back to application overview

the user can't complete the question without selecting answer (validation error messages check)
    the user clicks the button/link     id = application-question-complete
    the user should see the element     link = Please select an answer.

the user marks the section as complete
    the user enters text to a text field    css=.textarea-wrapped .editor    Entering text to allow valid mark as complete
    the user clicks the button/link         name=complete

the user marks the section as complete procurement
    [Arguments]  ${question_link}
    the user enters text to a text field     css=.textarea-wrapped .editor             Entering text to allow valid mark as complete
    the user uploads the file                css = input[name="templateDocument"]      ${valid_pdf}
    open pdf link                            link = ${valid_pdf} (opens in a new window)
    the user can remove the uploaded file    removeTemplateDocument                    ${valid_pdf}
    the user uploads the file                css = input[name="templateDocument"]      ${valid_pdf}
    Run Keyword If  '${question_link}' == 'Innovation'   the user uploads the file     css = input[name="appendix"]    ${valid_pdf}
    the user clicks the button/link          name=complete

Create new application with the same user
    [Arguments]  ${Application_title}   ${orgType}
    the user select the competition and starts application     ${openCompetitionBusinessRTO_name}
    check if there is an existing application in progress for this competition
    the user clicks the button/link                             link=Apply with a different organisation
    the user selects the radio button                           organisationTypeId  ${orgType}
    the user clicks the button/link                             jQuery = button:contains("Save and continue")
    the user search for organisation name on Companies house    ITV  ITV PLC
    the user clicks the button/link                             link=Application details
    wait until keyword succeeds without screenshots             10 s  200 ms  Input Text  css=[id="name"]  ${Application_title}
    the user clicks the button/link                             jQuery=button:contains("Save and return")

check if there is an existing application in progress for this competition
    wait until page contains element    css=body
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain    You have an application in progress
            Run Keyword If    '${status}' == 'PASS'    Run keywords    And the user selects the radio button     createNewApplication  true      #Yes, I want to create a new application.
            ...    AND    And the user clicks the button/link    jQuery=.govuk-button:contains("Continue")

Invite and accept the invitation
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    the user navigates to the page                                  ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link                                 link=Academic robot test application
    the user fills in the inviting steps no edit                    ${test_mailbox_one}+academictest@gmail.com
    logout as user
    the user reads his email and clicks the link                    ${recipient}    ${subject}    ${pattern}    2
    the user clicks the button/link                                 jQuery=.govuk-button:contains("Yes, accept invitation")
    the user selects the radio button                               organisationTypeId    2
    the user clicks the button/link                                 css = .govuk-button[type="submit"]
    the user confirms economic activity for research organiations   No
    the research user finds org in companies house                  Live  University of Liverpool
    the invited user fills the create account form                  Arsene    Wenger
    the user reads his email and clicks the link                    ${test_mailbox_one}+academictest@gmail.com    Please verify your email address    We now need you to verify your email address
    the user clicks the button/link                                 jQuery=p:contains("Your account has been successfully verified.")~ a:contains("Sign in")
    Logging in and Error Checking                                   ${test_mailbox_one}+academictest@gmail.com    ${correct_password}

the user fills in the inviting steps no edit
    [Arguments]  ${email}
    the user clicks the button/link       link=Application team
    the user clicks the button/link       link=Add a partner organisation
    the user enters text to a text field  id = organisationName  New Organisation's Name
    the user enters text to a text field  id = name  Partner's name
    the user enters text to a text field  id = email  ${email}
    the user clicks the button/link       jQuery=button:contains("Invite partner organisation")

the user fills in the inviting steps
    [Arguments]  ${email}
    the user clicks the button/link       link=Application team
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    page should contain element    jQuery=button:contains("Edit")
    Run Keyword If    '${status}' == 'PASS'    the user clicks the button/link    jQuery=button:contains("Edit")
    the user clicks the button/link       link=Add a partner organisation
    the user enters text to a text field  id = organisationName  New Organisation's Name
    the user enters text to a text field  id = name  Partner's name
    the user enters text to a text field  id = email   ${email}
    the user clicks the button/link       jQuery=button:contains("Invite partner organisation")

the user invites a person to the same organisation
    [Arguments]  ${name}  ${email}
    the user enters text to a text field   css = [name=name]   ${name}
    the user enters text to a text field   css = [name=email]  ${email}
    the user clicks the button/link        jQuery = button:contains("Invite to application")

# The search results are specific to Research Organisation type
the research user finds org in companies house
    [Arguments]  ${search}  ${link}
    the user enters text to a text field  id=organisationSearchName  ${search}
    the user clicks the button/link       jQuery=.govuk-button:contains("Search")
    the user clicks the button/link       link= ${link}
    the user clicks the button/link       jQuery=.govuk-button:contains("Save and continue")

The user navigates to the summary page of the Robot test application
    ${id} =  get application id by name  Robot test application
    the user navigates to the page       ${server}/application/${id}/summary

The user navigates to the review and submit page of the Robot test application
    ${id} =  get application id by name  Robot test application
    the user navigates to the page       ${server}/application/${id}/review-and-submit

The user navigates to the overview page of the Robot test application
    ${id} =  get application id by name  Robot test application
    the user navigates to the page       ${server}/application/${id}

The user navigates to the finance overview of the academic
    [Arguments]  ${appl}
    When the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link    link= ${appl}
    And the user clicks the button/link    link=Finances overview

invite a registered user
    [Arguments]    ${EMAIL_LEAD}    ${EMAIL_INVITED}
    the user navigates to the page                             ${frontDoor}
    the user clicks the button/link in the paginated list      link = ${openCompetitionBusinessRTO_name}
    the user follows the flow to register their organisation   ${BUSINESS_TYPE_ID}
    the user verifies email                                    Stuart   Anderson    ${EMAIL_LEAD}
    the user clicks the button/link                            link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link                            link = Application team
    the user clicks the button/link                            link=Add a partner organisation
    the user enters text to a text field                       id = organisationName  New Organisation's Name
    the user enters text to a text field                       id = name  Partner's name
    the user enters text to a text field                       id = email  ${EMAIL_INVITED}
    the user clicks the button/link                            jQuery=button:contains("Invite partner organisation")
    the user clicks the button/link                            link = Return to application overview
    the user should see the element                            jQUery = h1:contains("Application overview")
    the user closes the browser
    the guest user opens the browser

we create a new user
    [Arguments]    ${COMPETITION_NAME}  ${first_name}  ${last_name}  ${EMAIL_INVITED}  ${org_type_id}
    the user navigates to the page                            ${frontDoor}
    the user clicks the button/link in the paginated list     link = ${COMPETITION_NAME}
    the user follows the flow to register their organisation    ${org_type_id}
    the user verifies email    ${first_name}   ${last_name}    ${EMAIL_INVITED}

the user verifies email
    [Arguments]    ${first_name}  ${last_name}  ${EMAIL_INVITED}
    The user enters the details and clicks the create account  ${first_name}  ${last_name}  ${EMAIL_INVITED}  ${correct_password}
    The user should be redirected to the correct page          ${REGISTRATION_SUCCESS}
    the user reads his email and clicks the link               ${EMAIL_INVITED}  Please verify your email address  Once verified you can sign into your account
    The user should be redirected to the correct page          ${REGISTRATION_VERIFIED}
    The user clicks the button/link                            jQuery=p:contains("Your account has been successfully verified.")~ a:contains("Sign in")
    The guest user inserts user email and password             ${EMAIL_INVITED}  ${correct_password}
    The user clicks the button/link                            jQuery = button:contains("Sign in")

the user follows the flow to register their organisation
    [Arguments]   ${org_type_id}
    the user clicks the button/link         jQuery=a:contains("Start new application")
    the user clicks the button/link         link = Continue and create an account
    the user should not see the element     jQuery=h3:contains("Organisation type")
    the user selects the radio button       organisationTypeId  ${org_type_id}
    the user clicks the button/link         jQuery=.govuk-button:contains("Save and continue")
    the user enters text to a text field    id=organisationSearchName    ROYAL
    the user clicks the button/link         id=org-search
    the user clicks the button/link         link=ROYAL MAIL PLC
    the user clicks the button/link         jQuery=.govuk-button:contains("Save and continue")

the invited user fills the create account form
    [Arguments]    ${NAME}    ${LAST_NAME}
    Input Text                          id=firstName    ${NAME}
    Input Text                          id=lastName    ${LAST_NAME}
    Input Text                          id=phoneNumber    0612121212
    Input Password                      id=password    ${correct_password}
    the user selects the checkbox       termsAndConditions
    ${status} =   Run Keyword and return status    Checkbox Should Be Selected     css=[id="termsAndConditions"]
    Run Keyword If    '${status}' == 'False'   Select checkbox     css=[id="termsAndConditions"]
    the user selects the checkbox       allowMarketing
    the user clicks the button/link     css=button[type="submit"][name="create-account"]

the user enters the details and clicks the create account
    [Arguments]   ${first_name}  ${last_name}  ${email}  ${password}
    Wait Until Page Contains Element Without Screenshots    jQuery = a:contains("Terms and conditions")
    Input Text                                              name = firstName  ${first_name}
    Input Text                                              id = lastName  ${last_name}
    Input Text                                              id = phoneNumber  23232323
    Input Text                                              id = email  ${email}
    Input Password                                          id = password  ${password}
    the user selects the checkbox                           termsAndConditions
    ${status} =   Run Keyword and return status    Checkbox Should Be Selected     css=[id="termsAndConditions"]
    Run Keyword If    '${status}' == 'False'   Select checkbox     css=[id="termsAndConditions"]
    the user selects the checkbox                           allowMarketingEmails
    Submit Form

the user clicks the forgot psw link
    The user clicks the button/link  jQuery=summary:contains("Need help signing in or creating an account?")
    The user clicks the button/link  link=Forgotten your password?

Close browser and delete emails
    Close any open browsers