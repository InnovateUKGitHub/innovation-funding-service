*** Settings ***
Documentation     INFUND-832
...               INFUND-409
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

# Note that all of these tests will require you to set an absolute path for the upload folder robot-tests/upload_files
# If you are using the run_tests_locally shellscript then this will attempt to swap in a valid path automatically
# But if you are running pybot manually you will need to add -v UPLOAD_FOLDER:/home/foo/bar/robot-tests/upload_files

*** Variables ***
${valid_pdf}            testing.pdf
${too_large_pdf}        large.pdf
${text_file}            testing.txt

# ${JSFUNCTION}   window.document.getElementById("18").onChange();


*** Test Cases ***

Verify that the applicant can upload pdf files
    [Documentation]        INFUND-832
    [Tags]      Collaboration       Upload
    [Setup]     Guest user log-in   &{lead_applicant_credentials}
    Given the user can see the option to upload a file on the page      ${project_team_url}
    And the user uploads the file to the project team page        ${valid_pdf}

Questions can be assigned with appendices to the collaborator
    [Documentation]     INFUND-832
    ...                 INFUND-409
    [Tags]      Collaboration       Upload
    [Setup]     Guest user log-in   &{lead_applicant_credentials}
    Given the user navigates to the page     ${project_team_url}
    And the user can see the uploaded file  ${valid_pdf}
    When the user assigns the question to the collaborator     Jessica Doe
    And the user cannot remove the uploaded file     ${valid_pdf}
    And the user logs out
    And the collaborator logs in
    And the user navigates to the page      ${project_team_url}
    Then the user can see the uploaded file         ${valid_pdf}
    And the user can remove the uploaded file       ${valid_pdf}
    And the user can re-assign the question back to the lead applicant


Appendices are only available for the correct questions
    [Documentation]        INFUND-832
    [Tags]      Collaboration       Upload
    [Setup]     Guest user log-in   &{lead_applicant_credentials}
    the user cannot see the option to upload a file on the page     ${business_opportunity_url}
    the user cannot see the option to upload a file on the page     ${potential_market_url}
    the user cannot see the option to upload a file on the page     ${project_exploitation_url}
    the user cannot see the option to upload a file on the page     ${economic_benefit_url}
    the user can see the option to upload a file on the page        ${technical_approach_url}
    the user can see the option to upload a file on the page     ${innovation_url}
    the user cannot see the option to upload a file on the page        ${risks_url}
    the user can see the option to upload a file on the page        ${project team_url}
    the user cannot see the option to upload a file on the page     ${funding_url}
    the user cannot see the option to upload a file on the page     ${adding_value_url}


Large pdf uploads not allowed
    [Documentation]        INFUND-832
    [Tags]      Collaboration       Upload      Pending
    # "Too large pdf validation error is currently empty due to INFUND-2116: Can be changed to match for an error message once this is implemented
    Given the user can see the option to upload a file on the page      ${project_team_url}
    When the user uploads the file to the project team page       ${too_large_pdf}
    Then the user should get an error page      ${too_large_pdf_validation_error}


Non pdf uploads not allowed
    [Documentation]     INFUND-832
    [Tags]      Collaboration       Upload
    Given the user can see the option to upload a file on the page      ${PROJECT_TEAM_URL}
    When the user uploads the file to the project team page     ${text_file}
    Then the user should get an error page      ${wrong_filetype_validation_error}





*** Keywords ***


the user can see the uploaded file
    [Arguments]         ${file_name}
    Page Should Contain         ${file_name}


the user can remove the uploaded file
    [Arguments]     ${file_name}
    Reload Page
    Click Button        name=remove_uploaded_file
    Wait Until Page Does Not Contain        Remove
    Page Should Contain         Upload
    Page Should Not Contain     ${file_name}

the user cannot remove the uploaded file
    [Arguments]     ${file_name}
    Page Should Not Contain         Remove
    Page Should Contain         ${file_name}


the user logs out
    logout as user

the collaborator logs in
    log in as user   &{collaborator1_credentials}


the user uploads the file to the project team page
    [Arguments]     ${file_name}
    Choose File    name=formInput[18]    ${UPLOAD_FOLDER}/${file_name}
    Sleep   500ms




the user can see the option to upload a file on the page
    [Arguments]     ${url}
    The user navigates to the page      ${url}
    Wait Until Element Is Visible       name=mark_as_complete
    Page Should Contain                 Upload


the user cannot see the option to upload a file on the page
    [Arguments]     ${url}
    The user navigates to the page      ${url}
    Wait Until Element Is Visible       name=mark_as_complete
    Page Should Not Contain             Upload


the user can re-assign the question back to the lead applicant
    reload page
    click element       name=assign_question
    Reload Page