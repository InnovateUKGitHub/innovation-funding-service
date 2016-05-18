*** Settings ***
Documentation     INFUND-832
...               INFUND-409
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot    # Note that all of these tests will require you to set an absolute path for the upload folder robot-tests/upload_files    # If you are using the run_tests_locally shellscript then this will attempt to swap in a valid path automatically    # But if you are running pybot manually you will need to add -v UPLOAD_FOLDER:/home/foo/bar/robot-tests/upload_files
Force Tags


*** Variables ***
${valid_pdf}      testing.pdf
${too_large_pdf}    large.pdf
${text_file}      testing.txt
${valid_pdf excerpt}    Adobe PDF is an ideal format for electronic document distribution
${download_link}        ${SERVER}/application/1/form/question/8/forminput/18/download
${virus_scanning_warning}   This file is awaiting virus scanning


*** Test Cases ***
Lead applicant can upload a pdf file
    [Documentation]    INFUND-832
    [Tags]    Collaboration    Upload
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    Given the user can see the option to upload a file on the page    ${technical_approach_url}
    And the user uploads the file to the 'technical approach' question    ${valid_pdf}

Lead applicant can view a file
    [Documentation]     INFUND-2720
    [Tags]  Collaboration   Upload
    Given the user should see the text in the page  ${valid_pdf}
    And the file has been scanned for viruses
    When the user clicks the button/link        link=${valid_pdf}
    Then the user should see the text in the page   ${valid_pdf_excerpt}
    [Teardown]  The user navigates to the page  ${technical_approach_url}


Lead applicant can download a pdf file
    [Documentation]     INFUND-2720
    [Tags]  Collaboration   Upload      Pending
    # Pending until download functionality has been plugged in
    Given the user should see the text in the page  ${valid_pdf}
    When the user downloads the file from the link     ${valid_pdf}     ${download_link}
    Then the file should be downloaded      ${valid_pdf}
    [Teardown]  Remove File     ${valid_pdf}


Collaborators can view a file
    [Documentation]    INFUND-2306
    [Tags]    Collaboration    Upload
    [Setup]    Guest user log-in    &{collaborator2_credentials}
    Given the user cannot see the option to upload a file on the page    ${technical_approach_url}
    And the user should see the text in the page    ${valid_pdf}
    When the user clicks the button/link     link=${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf_excerpt}
    [Teardown]  The user navigates to the page  ${technical_approach_url}


Collaborators can download a pdf file
    [Documentation]     INFUND-2720
    [Tags]  Collaboration   Upload  Pending
    # Pending until download functionality has been plugged in
    Given the user should see the text in the page  ${valid_pdf}
    When the user downloads the file from the link     ${valid_pdf}     ${download_link}
    Then the file should be downloaded      ${valid_pdf}
    [Teardown]  Remove File     ${valid_pdf}


Collaborators cannot remove a file if not assigned to them
    [Documentation]     INFUND-2720
    [Tags]  Collaboration   Upload
    When the user should see the text in the page    ${valid_pdf}
    Then the user should not see the text in the page    Remove



Questions can be assigned with appendices to the collaborator
    [Documentation]    INFUND-832
    ...    INFUND-409
    [Tags]    Collaboration    Upload
    [Setup]     Guest user log-in   &{lead_applicant_credentials}
    Given the user navigates to the page    ${technical_approach_url}
    And the user should see the text in the page    ${valid_pdf}
    When the user assigns the question to the collaborator    Jessica Doe
    Then the user should not see the text in the page    Remove


Collaborators can view a file when the question is assigned to them
    [Documentation]     INFUND_2720
    [Tags]  Collaboration   Upload
    [Setup]     Guest user log-in       &{collaborator1_credentials}
    Given the user navigates to the page    ${technical_approach_url}
    And the user reloads the page
    And the user should see the text in the page      ${valid_pdf}
    When the user clicks the button/link        link=${valid_pdf}
    Then the user should see the text in the page       ${valid_pdf_excerpt}
    [Teardown]  The user navigates to the page      ${technical_approach_url}


Collaborator can download a file when the question is assigned to them
    [Documentation]     INFUND-2720
    [Tags]      Collaboration   Upload  Pending
    # Pending until download functionality has been plugged in
    Given the user should see the text in the page      ${valid_pdf}
    When the user downloads the file from the link  ${valid_pdf}    ${download_link}
    Then the file should be downloaded      ${valid_pdf}
    [Teardown]  The user navigates to the page      ${project_team_url}

Collaborator can remove a file when the question is assigned to them
    [Documentation]     INFUND-2720
    [Tags]  Collaboration   Upload
    Given the user should see the text in the page     ${valid_pdf}
    When the user can remove the uploaded file    ${valid_pdf}
    Then the user can re-assign the question back to the lead applicant


Appendices are only available for the correct questions
    [Documentation]    INFUND-832
    [Tags]    Collaboration    Upload
    [Setup]     Guest user log-in   &{lead_applicant_credentials}
    the user cannot see the option to upload a file on the page    ${business_opportunity_url}
    the user cannot see the option to upload a file on the page    ${potential_market_url}
    the user cannot see the option to upload a file on the page    ${project_exploitation_url}
    the user cannot see the option to upload a file on the page    ${economic_benefit_url}
    the user can see the option to upload a file on the page    ${technical_approach_url}
    the user can see the option to upload a file on the page    ${innovation_url}
    the user cannot see the option to upload a file on the page    ${risks_url}
    the user cannot see the option to upload a file on the page    ${project_team_url}
    the user cannot see the option to upload a file on the page    ${funding_url}
    the user cannot see the option to upload a file on the page    ${adding_value_url}

Large pdf uploads not allowed
    [Documentation]    INFUND-832
    [Tags]    Collaboration    Upload
    Given the user can see the option to upload a file on the page    ${technical_approach_url}
    When the user uploads the file to the 'technical approach' question    ${too_large_pdf}
    Then the user should get an error page    ${too_large_pdf_validation_error}

Non pdf uploads not allowed
    [Documentation]    INFUND-832
    [Tags]    Collaboration    Upload
    Given the user can see the option to upload a file on the page    ${technical_approach_url}
    When the user uploads the file to the 'technical approach' question    ${text_file}
    Then the user should get an error page    ${wrong_filetype_validation_error}

Quarantined files are not returned to the user and the user is informed
    [Documentation]    INFUND-2683
    ...    INFUND-2684
    [Tags]        Upload
    [Setup]     Guest user log-in   &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_team_url}
    When the user should see the text in the page    test_quarantine.pdf
    And the user clicks the button/link    link=test_quarantine.pdf
    Then the user should see the text in the page   File not available for download
    And the user should see the text in the page    This file has been quarantined by the virus scanner

*** Keywords ***
the user logs out
    logout as user

the collaborator logs in
    log in as user    &{collaborator1_credentials}

the user uploads the file to the 'technical approach' question
    [Arguments]    ${file_name}
    Choose File    name=formInput[14]    ${UPLOAD_FOLDER}/${file_name}
    Sleep    500ms

the user can see the option to upload a file on the page
    [Arguments]    ${url}
    The user navigates to the page    ${url}
    Page Should Contain    Upload

the user cannot see the option to upload a file on the page
    [Arguments]    ${url}
    The user navigates to the page    ${url}
    the user should not see the text in the page        Upload

the user can re-assign the question back to the lead applicant
    the user reloads the page
    the user clicks the button/link    name=assign_question
    the user reloads the page

the user downloads the file from the link
    [Arguments]     ${filename}     ${download_link}
    ${ALL_COOKIES} =    Get Cookies
    Log    ${ALL_COOKIES}
    Download File    ${ALL_COOKIES}    ${download_link}
    sleep    2s

the file should be downloaded
    [Arguments]     ${filename}
    File Should Exist   ${filename}
    File Should Not Be Empty    ${filename}

the file has been scanned for viruses
    Sleep   5s


