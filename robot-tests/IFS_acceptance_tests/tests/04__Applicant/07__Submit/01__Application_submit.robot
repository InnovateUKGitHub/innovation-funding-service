*** Settings ***
Documentation     INFUND-172: As a lead applicant and I am on the application summary, I can submit the application, so I can verify it that it is ready for submission
...
...               INFUND-185: As an applicant, on the application summary and pressing the submit application button, it should give me a message that I can no longer alter the application.
...
...               INFUND-927 As a lead partner i want the system to show me when all questions and sections (partner finances) are complete on the finance summary, so that i know i can submit the application
...
...               INFUND-1137 As an applicant I want to be shown confirmation information when I submit my application submission so I can confirm this has been sent and I can be given guidance for the next stages
...
...               INFUND-1887 As the Service Delivery Manager, I want to send an email notification to an applicant once they have successfully submitted a completed application so they have confidence their application has been received by Innovate UK
...
...               INFUND-3107 When clicking the "X", the form submits and doesn't cancel the action when using a modal popup
...
...               INFUND-1786 As a lead applicant I would like view the submitting an application terms and conditions page so that I know what I am agreeing to
...
...               INFUND-9058 Update 'Application submitted' and 'Application status' pages to the same view
...
...               IFS-942 Information message when application has reached 100% complete
Suite Setup       new account complete all but one
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot
Resource          ../../10__Project_setup/PS_Common.robot

# In this suite we are using the competition: Predicting market trends programme
# That forces the lead applicant to be RTO!! So we need to be careful with the Research participation in the project.

*** Test Cases ***
Submit button disabled when application is incomplete
    [Documentation]    INFUND-927, IFS-942
    [Tags]    Email    HappyPath
    Given the user navigates to the page    ${DASHBOARD_URL}
    When the user clicks the button/link    link=${application_name}
    And the user should not see the text in the page    Now your application is complete, you need to review and then submit.
    And the user clicks the button/link    link=Your finances
    And the user clicks the button/link    link= Application overview
    And the user clicks the button/link    jQuery=.button:contains("Review and submit")
    Then the submit button should be disabled

Applicant has read only view after submission
    [Documentation]    INFUND-7405, INFUND-8599
    [Tags]    HappyPath
    When the user navigates to the page    ${DASHBOARD_URL}
    and the user clicks the button/link    link=${application_name}
    then the applicant completes the application details    Application details
    and the user clicks the button/link    link=Return to application overview
    and the user clicks the button/link    link=Your finances
    When Run Keyword And Ignore Error Without Screenshots  the user clicks the button/link  jQuery=.extra-margin-bottom [aria-expanded="false"]
    Then the user clicks the button/link   jQuery=button:contains("Not requesting funding")
    And the user puts zero project costs
    When the user clicks the button/link     link=Return to application overview
    And the user clicks the button/link      link=Review and submit
#    and the user marks the finances as complete    ${application_name}
#    then the user should not see the element    css=input

Your Project costs section is read-only once application is submitted
    [Documentation]    INFUND-6788, INFUND-7405
    [Tags]
    When the user navigates to Your-finances page    ${application_name}
    then the user clicks the button/link    link=Your project costs
    and the user should not see the element    css=input
    When the user clicks the button/link    jQuery=button:contains("Overhead costs")
    then the user should not see the element    css=input

Submit flow (complete application)
    [Documentation]    INFUND-205, INFUND-9058, INFUND-1887, INFUND-3107, INFUND-4010, IFS-942
    [Tags]    HappyPath    Email    SmokeTest
    Given log in as a different user                        ${submit_test_email}    ${correct_password}
    And the user navigates to the page                      ${dashboard_url}
    And the user clicks the button/link                     link=${application_name}
    And the user should see the text in the element         css=.message-alert  Now your application is complete, you need to review and then submit.
    When the user clicks the button/link                    link=Review and submit
    Then the user should be redirected to the correct page  summary
    And the applicant clicks the submit button and the clicks cancel in the submit modal
    And the applicant clicks the submit and then clicks the "close button" in the modal
    And the applicant clicks Yes in the submit modal
    Then the user should be redirected to the correct page  submit
    And the user should see the text in the page            Application submitted
    And The user should see the element                     link=Finished
    # TODO add check here once IFS-270 done

The applicant should get a confirmation email
    [Documentation]    INFUND-1887
    [Tags]    Email    HappyPath    SmokeTest
    Then the user reads his email    ${test_mailbox_one}+submittest@gmail.com    Successful submission of application    You have successfully submitted an application

Submitted application is read only
    [Documentation]    INFUND-1938, INFUND-9058
    [Tags]    Email    SmokeTest
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=${application_name}
    and the user clicks the button/link     link=Return to dashboard
    and the user clicks the button/link     link=${application_name}
    When the user clicks the button/link    link=View application
    And The user should be redirected to the correct page    summary
    Then the user can check that the sections are read only

Status of the submitted application
    [Documentation]    INFUND-1137
    [Tags]    Email
    When the user navigates to the page    ${DASHBOARD_URL}
    Then the user should see the text in the page    Application submitted
    And the user clicks the button/link    Link=${application_name}
    And the user should see the element    Link=View application
    And the user should see the element    Link=Print application
    When the user clicks the button/link    Link=Print application
    Then the user should be redirected to the correct page without the usual headers    print

*** Keywords ***
the applicant clicks Yes in the submit modal
    the user clicks the button/link    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jQuery=.button:contains("Yes, I want to submit my application")

the applicant clicks the submit button and the clicks cancel in the submit modal
    Wait Until Element Is Enabled Without Screenshots    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jquery=button:contains("Cancel")

The user can check that the sections are read only
    the user navigates to the page    ${dashboard_url}
    the user clicks the button/link    link=${application_name}
    the user clicks the button/link    link=View application
    the user clicks the button/link    css=.section-overview section:nth-of-type(1) .collapsible:nth-of-type(4)
    the user should not see the element    jQuery=button:contains("Edit")
    the user clicks the button/link    css=.section-overview section:nth-of-type(2) .collapsible:nth-of-type(10)
    the user should not see the element    jQuery=.button:contains("Edit")
    the user clicks the button/link    css=.section-overview section:nth-of-type(3) .collapsible:nth-of-type(1)
    the user should not see the element    jQuery=.button:contains("Edit")

the submit button should be disabled
    Element Should Be Disabled    jQuery=button:contains("Submit application")

the applicant accepts the terms and conditions
    the user selects the checkbox    agree-terms-page
    the user selects the checkbox    agree-state-aid-page

the applicant marks the first section as complete
    Given the user navigates to the page    ${DASHBOARD_URL}
    the user clicks the button/link    link=${application_name}
    the applicant completes the application details

the applicant clicks the submit and then clicks the "close button" in the modal
    Wait Until Element Is Enabled Without Screenshots    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jQuery=.button:contains("Submit application")
    the user clicks the button/link    jQuery=button:contains("Close")

the user puts zero project costs
    [Documentation]  To be refactored with existing keyword
    the user clicks the button/link  link=Your project costs
    the user clicks the button/link  css=label[for="agree-state-aid-page"]
    the user clicks the button/link  jQuery=button:contains("Mark as complete")
    the user clicks the button/link  link=Your project costs
    the user has read only view once section is marked complete