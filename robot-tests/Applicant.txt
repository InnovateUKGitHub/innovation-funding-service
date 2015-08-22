*** Settings ***
Library           Selenium2Library
Library           string

*** Test Cases ***
Open browser(Firefox)
    The user opens the browser

Invalid Login
    Given the user is not logged-in
    When the guest user inserts correct username
    And the guest user inserts wrong password
    And the guest user clicks the log-in button
    Then the guest user should get an error message

Login as Applicant
    Given the user is not logged-in
    When the guest user inserts applicant user name
    And the user inserts password
    And the guest user clicks the log-in button
    Then the guest user should be logged-in as Applicant and redirected to the My applications page

Select one of the applications
    Given the user is logged in as applicant
    When the Applicant clicks the "Rovel Additive Manufacturing Process" application
    Then the Applicant should see the overview of the selected application

List with sections for the application overview
    Given the Applicant is in the application overview page
    Then the applicant should see six sections

Verify that when Applicant clicks the "Scope" this section is expanded
    Given the Applicant is in the application overview page
    Then the Applicant clicks the "Scope" section
    Then the First section should not be expanded
    And the first section should be hidden
    And the second button should be expanded
    And the second section should be visible
    And the "Scope" sub-section should be visible

Verify the Autosave for the "Rovel additive..." Application form
    Given the Applicant opens the "Rovel additive" application form
    When the applicant enters some text
    and the Applicant refreshes the page
    Then the text should be visible

Verify the Questions guidance for the "Rovel additive..." Application form
    Given the Applicant opens the "Rovel additive" application form
    When the applicant clicks the "What should I include in project summary?" question
    Then the guidance should be visible

Verify the navigation for the "Rovel additive..." form
    Given the Applicant opens the "Rovel additive" application form
    When the appicant clicks the sections
    #Then the Applicant will navigate to the corect section

Verify that the applicant can upload a file in the "Rovel additive..." \ form
    Given the Applicant opens the "Rovel additive" application form
    When the applicant uploads a file
    Then the file should be uploaded

Verify the character count for the "Rovel additive..." Application form
    Given the Applicant opens the "Rovel additive" application form

Verify the "review and submit" button
    Given the Applicant opens the "Rovel additive" application form
    When the Applicant clicks the "Review and" submit button
    Then the applicant will navigate to the summary page

Verify the "Review and submit" button (overview page)
    Given the applicant is in the "Overview page"
    When the applicant can see the "Review and submit button" (overview page)
    and the applicant clicks the Review and submit button (overview page)
    Then the applicant will navigate to the summary page

Log-out
    Given the Applicant goes to Dashboard page
    When The Applicant clicks the log-out button
    Then the applicant should be logged-out

Verify the "Submit button" is dissabled when the state of the application is not valid
    Given the appication is not valid
    When when the Applicant is in the summary page
    Then the submit button should be disabled

Verify that the user gets a warning message when clicks the submit button
    Given the application is valid
    When the applicant submits the application
    Then the applicant should get a warning message

Verify the submit flow
    Given the user is in the summary page
    and the "Submit" button is visible
    When the user clicks the "Submit" \ button
    and the applicant navigates to the confirm-submit page
    and the applicant clicks the "Yes, I want to submit my application"
    Then the applicant will navigate to the submit page
    and the "Return to dashboard page" button will be visible

Verify the succceslul submit page
    Given that the applicant has a valid application
    When the applicant submits the application
    Then the Applicant should navigate to the

*** Keywords ***
The user opens the browser
    Open Browser    http://ifs.test.worth.systems/login

the user is not logged-in
    Element Should Not Be Visible    link=My dashboard
    Element Should Not Be Visible    link=Logout

the guest user inserts correct username
    Input Text    id=id_email    applicant@applicant.org

the guest user inserts wrong password
    Input Password    id=id_password    testtest

the guest user should get an error message
    Element Should Be Visible    id=error-summary-heading-example-1

the guest user inserts applicant user name
    Input Text    id=id_email    applicant@innovate.org

the user inserts password
    Input Password    id=id_password    test

the guest user clicks the log-in button
    Click Button    css=input.button

the guest user should be logged-in as Applicant and redirected to the My applications page
    Element Should Be Visible    link=Logout

the user is logged in as Applicant
    Location Should Be    http://ifs.test.worth.systems/applicant/dashboard

the Applicant clicks the My application button
    Click Button    id=js-tabs_control-item--00

the Applicant clicks the "Rovel Additive Manufacturing Process" application
    Click Link    link=Rovel Additive Manufacturing Process

the Applicant should see the overview of the selected application
    Location Should Be    http://ifs.test.worth.systems/application/1/section/1

the Applicant is in the application overview page
    Location Should Be    http://ifs.test.worth.systems/application/1/section/1

the applicant should see six sections
    Page Should Contain Element    css=.section-overview > div:nth-of-type(1)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(2)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(3)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(4)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(5)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(6)

the Applicant clicks the "Scope" section
    [Documentation]    1. click second section
    Click Element    css=.section-overview > h2:nth-of-type(2) button

The First section should not be expanded
    [Documentation]    Assumption: page is loaded with first section option because of /section/1
    ...
    ...
    ...    2. check if the first button is now not expanded anymore (aria-expanded=false)
    ...
    ...    4. check if the second button is now expanded (aria-expanded=true)
    ...
    ...    5. check if the second section is now visible \ (aria-hidden=false)
    Page Should Contain Element    css=.section-overview > h2:nth-of-type(1) button[aria-expanded="false"]

the first section should be hidden
    Page Should Contain Element    css=.section-overview > div:nth-of-type(1)[aria-hidden="true"]

the second button should be expanded
    Page Should Contain Element    css=.section-overview > h2:nth-of-type(2) button[aria-expanded="true"]

the second section should be visible
    Page Should Contain Element    css=.section-overview > div:nth-of-type(2)[aria-hidden="false"]

the "Scope" sub-section should be visible
    Element Should Be Visible    link=How does your application align with the specific competition scope?

The Applicant clicks the log-out button
    Click Element    link=Logout
    Set Selenium Speed    .2

the Applicant goes to Dashboard page
    Go To    http://ifs.test.worth.systems/applicant/dashboard

the Applicant opens the "Rovel additive" application form
    Go To    http://ifs.test.worth.systems/applicant/dashboard
    Click Element    link=Rovel Additive Manufacturing Process
    Click Element    link=Project summary

the applicant enters some text
    Clear Element Text    name=question[11]
    Input Text    name=question[11]    Save test #123
    Clear Element Text    name=question[12]
    Input Text    name=question[12]    I am a robot
    Sleep    10 seconds
    Click Element    link= Scope (Gateway question)
    Clear Element Text    name=question[13]
    Input Text    name=question[13]    test text 2
    Sleep    10 seconds
    Click Element    link=Business proposition (Q1 - Q4)
    Clear Element Text    name=question[1]
    Input Text    name=question[1]    test text 3
    Sleep    10 seconds
    Click Element    link=Funding (Q9 - Q10)

the Applicant refreshes the page
    Reload Page

the text should be visible
    Click Element    link=Application details
    Textarea Should Contain    name=question[12]    I am a robot
    Click Element    link=Scope (Gateway question)
    Textarea Should Contain    name=question[13]    test text 2
    Click Element    link=Business proposition (Q1 - Q4)
    Textfield Should Contain    name=question[1]    test text 3
    Click Element    link=Funding (Q9 - Q10)
    Textarea Should Contain    name=question[15]    text test 5

the applicant clicks the "What should I include in project summary?" question
    Click Element    css=.grid-row > .column-two-thirds > form > div:nth-child(5) > div:nth-child(1)> details > summary

the guidance should be visible
    Page Should Contain Element    css=.grid-row > .column-two-thirds > form > div:nth-child(5) > div:nth-child(1)> details > summary[aria-expanded="true"]
    Page Should Contain    This is an opportunity to provide a short summary of the key objectives and focus areas of the project. It is important that this summary is presented in reference to the main outline of the project, with sufficient information to provide a clear understanding of the overall vision of the project and its innovative nature.

the applicant should be logged-out
    Location Should Be    http://ifs.test.worth.systems/login
    Page Should Not Contain Element    link=Logout

the appicant clicks the sections
    Click Element    link= Scope (Gateway question)
    Location Should Be    http://ifs.test.worth.systems/application-form/1/section/2/?question=1
    Click Element    link= Business proposition (Q1 - Q4)
    Location Should Be    http://ifs.test.worth.systems/application-form/1/section/3/?question=1
    Click Element    link= Project approach (Q5 - Q8)
    Location Should Be    http://ifs.test.worth.systems/application-form/1/section/4/?question=1
    Click Element    link=Funding (Q9 - Q10)
    Location Should Be    http://ifs.test.worth.systems/application-form/1/section/5/?question=1
    Click Element    link=Finances
    Location Should Be    http://ifs.test.worth.systems/application-form/1/section/6/?question=1

the applicant uploads a file
    Choose File    css=div.grid-row > div.column-two-thirds > form > div:nth-child(5) > div:nth-child(3) > input[type="file"]    /home/mtsiropoulos/Pictures/images (2).jpg

the file should be uploaded
    Element Should Be Visible    //*[@id="content"]/div[2]/div[2]/form/div[3]/div[3]/input

the Applicant clicks the "Review and" submit button
    Page Should Contain Link    link=Review & submit
    Click Element    link=Review & submit

the applicant will navigate to the summary page
    Location Should Be    http://ifs.test.worth.systems/application/1/summary

the applicant is in the "Overview page"
    Go To    http://ifs.test.worth.systems/applicant/dashboard
    Click Element    link=Rovel Additive Manufacturing Process

the applicant clicks the Review and submit button (overview page)
    click element    link=Review & submit

the applicant can see the "Review and submit button" (overview page)
    Page Should Contain Element    link=Review & submit

the user is in the summary page
    Go To    http://ifs.test.worth.systems/application/1/summary

the "Submit" button is visible
    Page Should Contain Element    link= Submit application

the user clicks the "Submit" \ button
    Click Element    link=Submit application

the applicant navigates to the confirm-submit page
    Location Should Be    http://ifs.test.worth.systems/application/1/confirm-submit

the applicant clicks the "Yes, I want to submit my application"
    Click Element    link=Yes, I want to submit my application

the applicant will navigate to the submit page
    Location Should Be    http://ifs.test.worth.systems/application/1/submit

the "Return to dashboard page" button will be visible
    Page Should Contain Element    link=Return to dashboard
