*** Settings ***
Documentation     INFUND-832 Acceptance test: Verify that only users with the right privileges can access the uploaded files
...
...               INFUND-409 As a lead applicant, I want to assign questions with appendices to a collaborator in one go
...
...               IFS-2327 Appendix file types: external facing change and validation
...
...               IFS-2564 As an Applicant I am able to see the Appendix guidance, file type and size
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
# Note that all of these tests will require you to set an absolute path for the upload folder robot-tests/upload_files
# If you are using the run_tests_locally shellscript then this will attempt to swap in a valid path automatically
# But if you are running pybot manually you will need to add -v UPLOAD_FOLDER:/home/foo/bar/robot-tests/upload_files

*** Test Cases ***
Appendices available only for the correct questions
    [Documentation]    INFUND-832  IFS-2564
    [Tags]  HappyPath
    [Setup]    Log in as a different user                            &{lead_applicant_credentials}
    ## Please leave this test case on top. It checks the appearance of the Upload button for pdfs before other tests do an actual upload
    the user cannot see the option to upload a file on the question  link = 1. Business opportunity
    the user cannot see the option to upload a file on the question  link = 2. Potential market
    the user cannot see the option to upload a file on the question  link = 3. Project exploitation
    the user cannot see the option to upload a file on the question  link = 4. Economic benefit
    the user can see the option to upload a file on the question     link = 5. Technical approach
    the user can see the option to upload a file on the question     link = 6. Innovation
    the user cannot see the option to upload a file on the question  link = 7. Risks
    the user can see the option to upload a file on the question     link = 8. Project team
    the user cannot see the option to upload a file on the question  link = 9. Funding
    the user cannot see the option to upload a file on the question  link = 10. Adding value

Large pdf uploads not allowed
    [Documentation]    INFUND-832
    [Tags]  HappyPath
    [Setup]    log in as a different user   &{lead_applicant_credentials}
    Given the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link     link = Academic robot test application
    And the user clicks the button/link     link = 5. Technical approach
    When the user uploads the file          css = .inputfile    ${too_large_pdf}
    Then the user should get an error page  ${too_large_pdf_validation_error}

Non pdf uploads not allowed
    [Documentation]    INFUND-832
    [Tags]  HappyPath
    Given the user navigates to the page                  ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link                   link = Academic robot test application
    And the user clicks the button/link                   link = 5. Technical approach
    When the user uploads the file                        css = .inputfile    ${text_file}
    Then the user should see a field and summary error    ${wrong_filetype_validation_error}

Lead applicant can upload a pdf file
    [Documentation]    INFUND-832  IFS-2327
    [Tags]  HappyPath
    [Setup]
    Given the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link     link = Academic robot test application
    And the user clicks the button/link     link = 5. Technical approach
    Then the user uploads the file          css = .inputfile    ${5mb_pdf}
    And the user should see the element     jQuery = a:contains(${5mb_pdf})

Lead applicant can view a file
    [Documentation]    INFUND-2720
    [Tags]  HappyPath
    Given the file has been scanned for viruses
    Then open pdf link   ${5mb_pdf}

Internal users can view uploaded files
    [Documentation]    IFS-1037
    [Tags]  HappyPath
    [Setup]  get application id by name and set as suite variable   Academic robot test application
    Given Log in as a different user               &{Comp_admin1_credentials}
    Then User verifies if uploaded document can be viewed
    When Log in as a different user               &{internal_finance_credentials}
    Then User verifies if uploaded document can be viewed
    When Log in as a different user               &{ifs_admin_user_credentials}
    Then User verifies if uploaded document can be viewed
    When Log in as a different user               &{support_user_credentials}
    Then User verifies if uploaded document can be viewed

Collaborators can view a file
    [Documentation]    INFUND-2306
    [Tags]  HappyPath
    [Setup]    Log in as a different user         ${test_mailbox_one}+academictest@gmail.com  ${correct_password}
    Given the user navigates to the page          ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link           link = Academic robot test application
    And the user clicks the button/link           link = 5. Technical approach
    Then open pdf link                             ${5mb_pdf}

Collaborators cannot upload a file if not assigned
    [Documentation]    INFUND-3007
    [Tags]
    When the user should see the element               jQuery = h3:contains("Appendix")
    Then the user should not see the element           jQuery = label:contains("Upload")

Collaborators cannot remove a file if not assigned
    [Documentation]    INFUND-2720
    [Tags]
    When the user should see the element               link = ${5mb_pdf}
    Then the user should not see the element           jQuery = button:contains("Remove")

Questions can be assigned with appendices
    [Documentation]    INFUND-832  INFUND-409
    [Tags]  HappyPath
    [Setup]    Log in as a different user                   &{lead_applicant_credentials}
    Given the user navigates to the page                    ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link                     link = Academic robot test application
    And the user clicks the button/link                     link = 5. Technical approach
    And the user should see the element                     link = ${5mb_pdf}
    When the user assigns the question to the collaborator  Arsene Wenger
    Then the user should not see the element                jQuery = button:contains("Remove")
    And the user clicks the button/link                     link = Back to application overview
    Then the user clicks the button/link                    link = 6. Innovation
    And the user assigns the question to the collaborator   Arsene Wenger

Collaborators can view a file when the question is assigned
    [Documentation]    INFUND_2720
    [Tags]
    [Setup]    Log in as a different user       ${test_mailbox_one}+academictest@gmail.com  ${correct_password}
    Given the user navigates to the page        ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link         link = Academic robot test application
    When the user clicks the button/link        link = 5. Technical approach
    Then open pdf link                          ${5mb_pdf}

Collaborator can remove a file when the question is assigned
    [Documentation]    INFUND-2720
    [Tags]
    Given the user navigates to the page          ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link           link = Academic robot test application
    And the user clicks the button/link           link = 5. Technical approach
    And the user should see the element           link = ${5mb_pdf}
    When the user can remove the uploaded file    removeAppendix  ${5mb_pdf}
    Then the user clicks the button/link          name = assign

Collaborators can upload a file when the question is assigned
    [Documentation]    INFUND_3007
    [Tags]
    Given the user navigates to the page           ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link            link = Academic robot test application
    And the user clicks the button/link            link = 6. Innovation
    When the user should see the element           jQuery = label:contains("+ Upload")
    Then the user uploads the file                 css = .inputfile     ${5mb_pdf}
    And the user clicks the button/link            name = assign

Quarantined files are not returned to the user and the user is informed
    [Documentation]    INFUND-2683
    ...    INFUND-2684
    [Tags]    Pending
    [Setup]    Log in as a different user          &{lead_applicant_credentials}
    #TODO INFUND-4008, review this failing test case when 4008 is completed
    Given the user navigates to the page           ${project_team_url}
    When the user should see the element           link = test_quarantine.pdf
    And the user clicks the button/link            link = test_quarantine.pdf
    Then the user should see the text in the page  File not available for download
    And the user should see the text in the page   This file has been found to be unsafe

*** Keywords ***
Custom Suite Setup
    the guest user opens the browser
    Login new application invite academic  ${test_mailbox_one}+academictest@gmail.com  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation
    Connect to database  @{database}

the user can re-assign the question back to the lead applicant
    the user reloads the page
    the user clicks the button/link  name = assign
    the user reloads the page

the user cannot see the option to upload a file on the question
    [Arguments]    ${QUESTION}
    the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link  link = Academic robot test application
    the user clicks the button/link  ${QUESTION}
    the user should not see the element     jQuery = label:contains("Upload")

the user can see the option to upload a file on the question
    [Arguments]    ${QUESTION}
    the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link  link = Academic robot test application
    the user clicks the button/link  ${QUESTION}
    the user checks the Appendix guidance
    the user should see the element  jQuery = label:contains("Upload")

the user checks the Appendix guidance
    [Documentation]  IFS-2564
    the user clicks the button/link           jQuery = span:contains("What should I include in the appendix?")
    the user should see the element           jQuery = h3:contains("Accepted appendix file types")
    the user should see the element           jQuery = li:contains("PDF")
    the user should see the element           jQuery = p:contains("It must be less than 10MB in size.")

User verifies if uploaded document can be viewed
     the user navigates to the page            ${SERVER}/management/competition/${openCompetitionBusinessRTO}/application/${academic_applicaton_id}
     the user expands the section              5. Technical approach
     open pdf link                             ${5mb_pdf}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database
