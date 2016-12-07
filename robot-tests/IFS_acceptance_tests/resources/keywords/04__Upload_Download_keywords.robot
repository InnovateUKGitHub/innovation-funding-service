*** Keywords ***
the user downloads the file from the link
    [Arguments]    ${filename}    ${download_link}
    ${ALL_COOKIES} =    Get Cookies
    Log    ${ALL_COOKIES}
    Download File    ${ALL_COOKIES}    ${download_link}    ${filename}
    wait until keyword succeeds    300ms    1 seconds    Download should be done

Download should be done
    [Documentation]    Verifies that the directory has only one folder
    ...    Returns path to the file
    #TODO Pednging due to INFUND-6187
    ${files}    List Files In Directory    ${DOWNLOAD_FOLDER}
    Length Should Be    ${files}    1    Should be only one file in the download folder
    ${file}    Join Path    ${DOWNLOAD_FOLDER}    ${files[0]}
    Log    File was successfully downloaded to ${file}
    [Return]    ${file}

the file should be downloaded
    [Arguments]    ${filename}
    File Should Exist    ${filename}
    File Should Not Be Empty    ${filename}

the file has been scanned for viruses
    Sleep    5s

the user cannot see the option to upload a file on the page
    [Arguments]    ${url}
    The user navigates to the page    ${url}
    the user should not see the text in the page    Upload

the user can see the option to upload a file on the page
    [Arguments]    ${url}
    The user navigates to the page    ${url}
    Page Should Contain    Upload

the user can remove the uploaded file
    [Arguments]    ${file_name}
    Reload Page
    Click Button    name=remove_uploaded_file
    Wait Until Page Does Not Contain    Remove
    Page Should Contain    Upload
    Page Should Not Contain    ${file_name}
