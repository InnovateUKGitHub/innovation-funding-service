*** Settings ***
Documentation     INFUND-4851 As a project manager I want to be able to submit an uploaded Grant Offer Letter so that Innovate UK can review my signed copy
Suite Setup       all the other sections of the project are completed
Suite Teardown    the user closes the browser
Force Tags        Project Setup    Upload    Pending
Resource          ../../resources/defaultResources.robot

*** Test Cases ***

PM can view the grant offer letter page
    [Documentation]    INFUND-4848
    [Tags]
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}    Passw0rd
    Given the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_HEADER}
    When the user clicks the button/link    link=Grant offer letter
    Then the user should see the text in the page    The grant offer letter has been provided by Innovate UK
    And the user should see the element    jQuery=label:contains(+ Upload)
    And the user should not see the text in the page    This document is awaiting signature by the Project Manager


Partners should not be able to submit the Grant Offer
    [Documentation]    INFUND-4851, INFUND-4428
    [Tags]
    [Setup]    log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_HEADER}
    And the user clicks the button/link    link=Grant offer letter
    Then the user should not see the element    jQuery=label:contains(+ Upload)
    And the user should not see the element    jQuery=.button:contains("Submit signed offer letter")



PM should not be able to upload big Grant Offer files
    [Documentation]    INFUND-4851, INFUND-4972
    [Tags]
    [Setup]    log in as a different user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}    Passw0rd
    Given the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_HEADER}
    And the user clicks the button/link    link=Grant offer letter
    When the lead uploads a grant offer letter    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    And the user goes back to the previous page


PM should be able upload a file and then access the Submit button
    [Documentation]    INFUND-4851, INFUND-4972
    [Tags]
    [Setup]
    When the lead uploads a grant offer letter    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}
    And the Grant offer submit button should be enabled


PM can view the uploaded Grant Offer file
    [Documentation]    INFUND-4851, INFUND-4849
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
    [Tags]
    #TODO Pending INFUND-5584
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(7).status.ok

Internal Dashboard should be updated
    [Documentation]    INFUND-4851
    [Tags]
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

all the other sections of the project are completed
    bank details have been filled out for all users
    the project finance user has approved bank details

bank details have been filled out for all users
    log in as user    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}    Passw0rd
    the user clicks the button/link    link=${FUNDERS_PANEL_APPLICATION_2_HEADER}
    the user clicks the button/link    link=Bank details
    the user fills out valid bank details
    log in as a different user    ${project_setup_application_1_partner_email}    Passw0rd
    the user clicks the button/link    link=${FUNDERS_PANEL_APPLICATION_2_HEADER}
    the user clicks the button/link    link=Bank details
    the user fills out valid bank details
    log in as a different user    ${project_setup_application_1_academic_partner_email}    Passw0rd
    the user clicks the button/link    link=${FUNDERS_PANEL_APPLICATION_2_HEADER}
    the user clicks the button/link    link=Bank details
    the user fills out valid bank details

the user fills out valid bank details
     the user enters text to a text field    name=accountNumber    51406795
     the user enters text to a text field    name=sortCode    404745
     the user selects the radio button    addressType    ADD_NEW
     the user enters text to a text field    id=addressForm.postcodeInput    BS14NT
     the user clicks the button/link    id=postcode-lookup
     the user should see the element    css=#select-address-block
     the user clicks the button/link    css=#select-address-block > button
     the address fields should be filled
     the user clicks the button/link    jQuery=.button:contains("Submit bank account details")
     the user clicks the button/link    jquery=button:contains("Cancel")
     the user should not see the text in the page    The bank account details below are being reviewed
     the user clicks the button/link    jQuery=.button:contains("Submit bank account details")
     the user clicks the button/link    jquery=button:contains("Submit")
     the user should see the text in the page    The bank account details below are being reviewed


the project finance user has approved bank details
    log in as a different user    lee.bowman@innovateuk.test    Passw0rd
    the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_2_PROJECT}/finance-check
    the user clicks the button/link    jQuery=a:contains("review") nth-of-type(1)
    the user should see the text in the page  Empire Ltd
    the user
    the user clicks the button/link    jQuery=.button:contains("Approve bank account details")
    the user clicks the button/link    jQuery=.button:contains("Approve account")
    the user should not see the element    jQuery=.button:contains("Approve bank account details")
    the user should see the text in the page    The bank details provided have been approved.
    the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_2_PROJECT}/finance-check
    the user clicks the button/link    jQuery=a:contains("review") nth-of-type(2)
    the user should see the text in the page  Ludlow
    the user clicks the button/link    jQuery=.button:contains("Approve bank account details")
    the user clicks the button/link    jQuery=.button:contains("Approve account")
    the user should not see the element    jQuery=.button:contains("Approve bank account details")
    the user should see the text in the page    The bank details provided have been approved.
    the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_2_PROJECT}/finance-check
    the user clicks the button/link    jQuery=a:contains("review") nth-of-type(3)
    the user should see the text in the page  EGGS
    the user clicks the button/link    jQuery=.button:contains("Approve bank account details")
    the user clicks the button/link    jQuery=.button:contains("Approve account")
    the user should not see the element    jQuery=.button:contains("Approve bank account details")
    the user should see the text in the page    The bank details provided have been approved.
    the project finance user approves bank details for    Empire Ltd
    the project finance user approves bank details for    Ludlow
    the project finance user approves bank details for    EGGS

the project finance user approves bank details for
    [Arguments]    ${org_name}
    the user should see the text in the page    ${org_name}
    the user selects the checkbox    costs-reviewed
    the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    the user should see the text in the page