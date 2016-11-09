*** Settings ***
Documentation     INFUND-4851 As a project manager I want to be able to submit an uploaded Grant Offer Letter so that Innovate UK can review my signed copy
Suite Teardown    the user closes the browser
Force Tags        Project Setup    Upload    Pending    # TODO Pending completion of INFUND-5828
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Partners should not be able to submit the Grant Offer
    [Documentation]    INFUND-4851
    [Tags]
    [Setup]    log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_HEADER}
    And the user clicks the button/link    link=Grant offer letter
    Then the user should not see the element    jQuery=label:contains(+ Upload)
    And the user should not see the element    jQuery=.button:contains("Submit signed offer letter")


PM should not be able to upload big Grant Offer files
    [Documentation]    INFUND-4851
    [Tags]
    [Setup]    log in as a different user    worth.email.test+projectlead@gmail.com    Passw0rd
    Given the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_HEADER}
    And the user clicks the button/link    link=Grant offer letter
    When the lead uploads a grant offer letter    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    And the user goes back to the previous page

PM should be able upload a file and then access the Submit button
    [Documentation]    INFUND-4851
    [Tags]
    [Setup]
    # TODO remove the comment from the last check when the infund-5567 is ready
    When the lead uploads a grant offer letter    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    #And the Grant offer submit button should be enabled

PM can view the upload Grant Offer file
    [Documentation]    INFUND-4851
    [Tags]
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page
    And the user goes back to the previous page

PM Submits the Grant Offer letter
    [Documentation]    INFUND-4851
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Submit signed offer letter")
    and the user clicks the button/link    jQuery=button:contains("Confirm Submission")
    Then the user should see the element    css=li.complete:nth-child(8)
    [Teardown]

PM's dashboard should be updated
    [Documentation]    INFUND-4851
    [Tags]    Pending
    #TODO Pending INFUND-5584
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(7).status.ok

Internal Dashboard should be updated
    [Documentation]    INFUND-4851
    [Tags]    Pending
    [Setup]    log in as a different user    john.doe@innovateuk.test    Passw0rd
    #TODO Pending INFUND-5584
    When the user navigates to the page    ${internal_project_summary}
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(7).status.ok

*** Keywords ***
the lead uploads a grant offer letter
    [Arguments]    ${file_name}
    choose file    name=signedGrantOfferLetter    ${upload_folder}/${file_name}

the Grant offer submit button should be enabled
    Element Should Be Enabled    jQuery=.button:contains("Submit signed offer letter")
