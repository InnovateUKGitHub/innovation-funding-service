*** Settings ***
Documentation     INFUND-6376 As a partner I want to be shown information in IFS when I have successfully completed Project Setup so I am clear on what steps to take now the project is live
Resource          PS_Common.robot
Suite Setup       the project is completed if it is not already complete
Suite Teardown    Close browser and delete emails

*** Test Cases ***

Project dashboard shows message that the project is live
    [Documentation]    INFUND-6376
    [Tags]
    Given log in as a different user    ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}
    When the user navigates to the page    ${server}/project-setup/project/${PS_GOL_APPLICATION_PROJECT}
    Then the user should see the text in the page    The project is live, you can review progress at
    When log in as a different user    ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}
    And the user navigates to the page    ${server}/project-setup/project/${PS_GOL_APPLICATION_PROJECT}
    Then the user should see the text in the page    The project is live, you can review progress at

Status indicators should not show
    [Documentation]    INFUND-6376
    [Tags]
    When the user should not see the element    link=status of my partners
    And the user should not see the element    css.complete
    And the user should not see the element    css=.action
    And the user should not see the element    css=.waiting

Project details section is read-only
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Project details
    Then the user should not see the element    link=Target start date
    And the user should not see the element    link=Project address
    And the user should not see the element    link=Project Manager
    And the user should not see the element   link=${Gabtype_NAME}
    [Teardown]    the user goes back to the previous page

Bank details section is read-only
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Bank details
    Then the user should not see the element    name=accountNumber
    And the user should not see the element    name=sortCode
    And the user should not see the element    jQuery=.button:contains("Submit bank account details")
    [Teardown]    the user goes back to the previous page

Spend profile section is read-only
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Spend profile
    And the user clicks the button/link    link=${Cogilith_Name}
    Then the user should not see the element    jQuery=.button:contains("Edit spend profile")
    And the user should not see the element    jQuery=.button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link    link=${Kazio_Name}
    Then the user should not see the element    jQuery=.button:contains("Edit spend profile")
    And the user should not see the element    jQuery=.button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link    link=${Gabtype_NAME}
    Then the user should not see the element    jQuery=.button:contains("edit spend profile")
    And the user goes back to the previous page
    [Teardown]    the user goes back to the previous page

Other documents section is read-only
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked
    And the user should not see the element    jQuery=.button:contains("Submit documents")
    [Teardown]    the user goes back to the previous page


Grant offer letter section is read-only
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Grant offer letter
    Then the user should not see the element    jQuery=.button:contains("Send signed offer letter")
    And the user should not see the text in the page    Remove
    And the user should not see the element   name=signedGrantOfferLetter

Project dashboard shows message that the project is live for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    Given log in as a different user    ${PS_GOL_APPLICATION_PARTNER_EMAIL}  ${short_password}
    When the user navigates to the page    ${server}/project-setup/project/${PS_GOL_APPLICATION_PROJECT}
    Then the user should see the text in the page    The project is live, you can review progress at
    When log in as a different user    ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}
    And the user navigates to the page    ${server}/project-setup/project/${PS_GOL_APPLICATION_PROJECT}
    Then the user should see the text in the page    The project is live, you can review progress at


Status indicators should not show for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user should not see the element    link=status of my partners
    And the user should not see the element    css.complete
    And the user should not see the element    css=.action
    And the user should not see the element    css=.waiting

Project details section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Project details
    Then the user should not see the element    link=Target start date
    And the user should not see the element    link=Project address
    And the user should not see the element    link=Project Manager
    And the user should not see the element   link=${Gabtype_NAME}
    [Teardown]    the user goes back to the previous page

Bank details section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Bank details
    Then the user should not see the element    name=accountNumber
    And the user should not see the element    name=sortCode
    And the user should not see the element    jQuery=.button:contains("Submit bank account details")
    [Teardown]    the user goes back to the previous page

Spend profile section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Spend profile
    And the user clicks the button/link    link=${Cogilith_Name}
    Then the user should not see the element    jQuery=.button:contains("Edit spend profile")
    And the user should not see the element    jQuery=.button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link    link=${Kazio_Name}
    Then the user should not see the element    jQuery=.button:contains("Edit spend profile")
    And the user should not see the element    jQuery=.button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link    link=${Gabtype_NAME}
    Then the user should not see the element    jQuery=.button:contains("edit spend profile")
    And the user goes back to the previous page
    [Teardown]    the user goes back to the previous page

Other documents section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked
    And the user should not see the element    jQuery=.button:contains("Submit documents")
    [Teardown]    the user goes back to the previous page


Grant offer letter section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Grant offer letter
    Then the user should not see the element    jQuery=.button:contains("Send signed offer letter")
    And the user should not see the text in the page    Remove
    And the user should not see the element   name=signedGrantOfferLetter


Project dashboard shows message that the project is live for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    Given log in as a different user    ${PS_GOL_APPLICATION_ACADEMIC_EMAIL}  ${short_password}
    When the user navigates to the page    ${server}/project-setup/project/${PS_GOL_APPLICATION_PROJECT}
    Then the user should see the text in the page    The project is live, you can review progress at
    When log in as a different user    ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}
    And the user navigates to the page    ${server}/project-setup/project/${PS_GOL_APPLICATION_PROJECT}
    Then the user should see the text in the page    The project is live, you can review progress at


Status indicators should not show for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user should not see the element    link=status of my partners
    And the user should not see the element    css.complete
    And the user should not see the element    css=.action
    And the user should not see the element    css=.waiting

Project details section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Project details
    Then the user should not see the element    link=Target start date
    And the user should not see the element    link=Project address
    And the user should not see the element    link=Project Manager
    And the user should not see the element   link=${Gabtype_NAME}
    [Teardown]    the user goes back to the previous page

Bank details section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Bank details
    Then the user should not see the element    name=accountNumber
    And the user should not see the element    name=sortCode
    And the user should not see the element    jQuery=.button:contains("Submit bank account details")
    [Teardown]    the user goes back to the previous page

Spend profile section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Spend profile
    And the user clicks the button/link    link=${Cogilith_Name}
    Then the user should not see the element    jQuery=.button:contains("Edit spend profile")
    And the user should not see the element    jQuery=.button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link    link=${Kazio_Name}
    Then the user should not see the element    jQuery=.button:contains("Edit spend profile")
    And the user should not see the element    jQuery=.button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link    link=${Gabtype_NAME}
    Then the user should not see the element    jQuery=.button:contains("edit spend profile")
    And the user goes back to the previous page
    [Teardown]    the user goes back to the previous page

Other documents section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Other documents
    Then the user should not see the text in the page    Remove
    And the user should not see the element    name=removeCollaborationAgreementClicked
    And the user should not see the element    name=removeExploitationPlanClicked
    And the user should not see the element    jQuery=.button:contains("Submit documents")
    [Teardown]    the user goes back to the previous page


Grant offer letter section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link    link=Grant offer letter
    Then the user should not see the element    jQuery=.button:contains("Send signed offer letter")
    And the user should not see the text in the page    Remove
    And the user should not see the element   name=signedGrantOfferLetter


*** Keywords ***

the project is completed if it is not already complete
    The user logs-in in new browser  ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}
    the user navigates to the page   ${server}/project-setup/project/${PS_GOL_APPLICATION_PROJECT}
    ${project_not_live}  ${value} =  run keyword and ignore error without screenshots  the user should not see the text in the page  The project is live
    run keyword if  '${project_not_live}' == 'PASS'  complete the project

complete the project
    project finance approves bank details for ${PS_GOL_APPLICATION_TITLE}
    project manager submits other documents      ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}  ${PS_GOL_APPLICATION_PROJECT}
    project finance approves other documents     ${PS_GOL_APPLICATION_PROJECT}
    project finance generates the Spend Profile  ${Gabtype_Id}  ${Kazio_Id}  ${Cogilith_Id}  ${PS_GOL_APPLICATION_PROJECT}
    all partners submit their Spend Profile
    proj finance approves the spend profiles     ${PS_GOL_APPLICATION_PROJECT}
    grant offer letter is sent to users
    users upload signed grant offer letter and submit
    grant offer letter is approved

grant offer letter is sent to users
    log in as a different user       &{internal_finance_credentials}
    the user navigates to the page   ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/grant-offer-letter/send
    the user clicks the button/link  id=send-gol
    the user clicks the button/link  jQuery=.modal-accept-send-gol .button:contains("Publish to project team")

users upload signed grant offer letter and submit
    log in as a different user       ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}
    the user clicks the button/link  link=${PS_GOL_APPLICATION_HEADER}
    the user clicks the button/link  link=Grant offer letter
    choose file                      signedGrantOfferLetter    ${upload_folder}/testing.pdf
    the user clicks the button/link  jQuery=a:contains("Send to Innovate UK")
    the user clicks the button/link  jQuery=button:contains("Send to Innovate UK")

grant offer letter is approved
    log in as a different user       &{internal_finance_credentials}
    the user navigates to the page   ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/grant-offer-letter/send
    the user clicks the button/link  jQuery=#content .button:contains("Accept signed grant offer letter")
    the user clicks the button/link  jQuery=.modal-accept-signed-gol .button:contains("Accept signed grant offer letter")