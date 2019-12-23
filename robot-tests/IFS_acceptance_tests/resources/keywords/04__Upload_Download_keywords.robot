*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
#Upload
the user uploads the file
    [Arguments]  ${selector}  ${file}
    Choose File  ${selector}  ${UPLOAD_FOLDER}/${file}
    Wait Until Page Does Not Contain Without Screenshots    Uploading   30s

the user can remove the uploaded file
    [Arguments]  ${name}  ${file_name}
    Reload Page
    Click Button    name=${name}
    Wait Until Page Does Not Contain Without Screenshots    Removing
    Wait Until Page Does Not Contain Without Screenshots    Remove
    Page Should Contain    Upload
    Page Should Not Contain    ${file_name}

#Download
The user downloads the file
    [Documentation]    Makes use of a download script that logs in, grabs a cookie and downloads
    ...     the file all in one package
    [Arguments]    ${user}    ${url}    ${filename}
    ${script return status}=    Run and Return RC    python3 download.py ${user} ${short_password} ${url} ${filename}
    should be equal as integers    ${script return status}    0    The download script failed, probably this means the regex for the login page will need updating
    Wait Until Keyword Succeeds Without Screenshots    30s    200ms    Download should be done

Guest user downloads the file
    [Arguments]    ${url}    ${filename}
    ${script return status}=    Run and Return RC    python3 download.py ${url} ${filename}
    should be equal as integers    ${script return status}    0    The download script failed, probably this means the regex for the login page will need updating
    Wait Until Keyword Succeeds Without Screenshots    30s    200ms    Download should be done

Download should be done
    [Documentation]    Verifies that the directory has only one file
    ...    Returns path to the file
    ${files}    List Files In Directory    ${DOWNLOAD_FOLDER}
    #Note that there is a gitignore file as well that we have to account for here
    Length Should Be    ${files}    2    Should be only one file in the download folder
    ${file}    Join Path    ${DOWNLOAD_FOLDER}    ${files[0]}
    Log    File was successfully downloaded to ${file}
    [Return]    ${file}

remove the file from the operating system
   [Arguments]    ${filename}
    remove file    ${download_folder}/${filename}

the file has been scanned for viruses
    #This keyword does not check about viruses at all ! It only adds delay.
    Sleep    5s    #this sleep statement is necessary as we wait for the antivirus scanner to work. Please do not remove during refactoring!

open pdf link
    [Arguments]  ${selector}
    the user clicks the button/link        ${selector}
    Select Window  NEW
    And the user should not see an error in the page
    Then the user closes the last opened tab
