*** Settings ***
Documentation    INFUND-6376 As a partner I want to be shown information in IFS when I have successfully completed Project Setup so I am clear on what steps to take now the project is live
Resource          ../../resources/defaultResources.robot
Resource          PS_Variables.robot
Suite Setup    the project is completed if it is not already complete

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
    [Tags]    Pending
    # TODO Pending due to INFUND-7922
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
    [Tags]    Pending
    # TODO Pending due to INFUND-7922
    When the user should not see the element    link=status of my partners
    And the user should not see the element    css.complete
    And the user should not see the element    css=.action
    And the user should not see the element    css=.waiting

Project details section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]    Pending
    # TODO Pending due to INFUND-7924
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
    log in as user    ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}
    the user navigates to the page    ${server}/project-setup/project/${PS_GOL_APPLICATION_PROJECT}
    ${project_not_live}    ${value}=    run keyword and ignore error without screenshots    the user should not see the text in the page    The project is live
    run keyword if    '${project_not_live}' == 'PASS'     complete the project


complete the project
    the project finance user has approved bank details
    other documents have been uploaded and approved
    project finance generates the Spend Profile
    all partners submit their Spend Profile
    proj finance approves the spend profiles
    grant offer letter is sent to users
    users upload signed grant offer letter and submit
    grant offer letter is approved


the project finance user has approved bank details
    Guest user log-in  &{internal_finance_credentials}
    the project finance user approves bank details for    Gabtype
    the project finance user approves bank details for    Kazio
    the project finance user approves bank details for    Cogilith

the project finance user approves bank details for
    [Arguments]    ${org_name}
    the user navigates to the page            ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/review-all-bank-details
    the user clicks the button/link           link=${org_name}
    the user should see the text in the page  ${org_name}
    the user clicks the button/link           jQuery=.button:contains("Approve bank account details")
    the user clicks the button/link           jQuery=.button:contains("Approve account")
    the user should not see the element       jQuery=.button:contains("Approve bank account details")
    the user should see the text in the page  The bank details provided have been approved.

other documents have been uploaded and approved
    log in as a different user        ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}
    the user navigates to the page    ${server}/project-setup/project/${PS_GOL_APPLICATION_PROJECT}/partner/documents
    choose file    name=collaborationAgreement    ${upload_folder}/testing.pdf
    choose file    name=exploitationPlan    ${upload_folder}/testing.pdf
    the user reloads the page
    the user clicks the button/link    jQuery=.button:contains("Submit documents")
    the user clicks the button/link    jQuery=.button:contains("Submit")
    log in as a different user         &{internal_finance_credentials}
    the user navigates to the page     ${SERVER}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/partner/documents
    the user clicks the button/link    jQuery=.button:contains("Accept documents")
    the user clicks the button/link    jQuery=.modal-accept-docs .button:contains("Accept Documents")

project finance generates the Spend Profile
    log in as a different user      &{internal_finance_credentials}
    project finance approves Viability for  ${Gabtype_Id}
    project finance approves Viability for  ${Kazio_Id}
    project finance approves Eligibility
    the user navigates to the page  ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/finance-check
    the user clicks the button/link  jQuery=.generate-spend-profile-main-button
    the user clicks the button/link  jQuery=#generate-spend-profile-modal-button

project finance approves Viability for
    [Arguments]  ${partner}
    the user navigates to the page     ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/finance-check/organisation/${partner}/viability
    the user selects the checkbox      costs-reviewed
    the user selects the checkbox      project-viable
    the user moves focus to the element  link=Contact us
    the user selects the option from the drop-down menu  Green  id=rag-rating
    the user clicks the button/link    css=#confirm-button
    the user clicks the button/link    jQuery=.modal-confirm-viability .button:contains("Confirm viability")

project finance approves Eligibility
    the user navigates to the page     ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/finance-check/organisation/${Gabtype_Id}/eligibility
    the user approves project costs
    the user navigates to the page     ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/finance-check/organisation/${Kazio_Id}/eligibility
    the user approves project costs
    the user navigates to the page     ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/finance-check/organisation/${Cogilith_Id}/eligibility
    the user approves project costs

the user approves project costs
    the user selects the checkbox    project-eligible
    the user selects the option from the drop-down menu    Green    id=rag-rating
    the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    the user clicks the button/link    name=confirm-eligibility

all partners submit their Spend Profile
    log in as a different user         ${PS_GOL_APPLICATION_PARTNER_EMAIL}    Passw0rd
    the user navigates to the page     ${server}/project-setup/project/${PS_GOL_Competition_Id}/partner-organisation/${Kazio_Id}/spend-profile
    the user clicks the button/link    jQuery=a:contains("Submit to lead partner")
    the user clicks the button/link    jQuery=.button:contains("Submit")
    log in as a different user         ${PS_GOL_APPLICATION_ACADEMIC_EMAIL}    Passw0rd
    the user navigates to the page     ${server}/project-setup/project/${PS_GOL_Competition_Id}/partner-organisation/${Cogilith_Id}/spend-profile
    the user clicks the button/link    jQuery=a:contains("Submit to lead partner")
    the user clicks the button/link    jQuery=.button:contains("Submit")
    log in as a different user         ${PS_GOL_APPLICATION_LEAD_PARTNER_EMAIL}    Passw0rd
    the user navigates to the page     ${server}/project-setup/project/${PS_GOL_Competition_Id}/partner-organisation/${Gabtype_Id}/spend-profile
    the user clicks the button/link    link=${Gabtype_Name}
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user navigates to the page     ${server}/project-setup/project/${PS_GOL_Competition_Id}/partner-organisation/${Gabtype_Id}/spend-profile
    the user clicks the button/link    jQuery=.button:contains("Review and send total project spend profile")
    the user clicks the button/link    jQuery=.button:contains("Send project spend profile")
    the user clicks the button/link    jQuery=.modal-confirm-spend-profile-totals .button[value="Send"]

proj finance approves the spend profiles
    log in as a different user         &{internal_finance_credentials}
    the user navigates to the page     ${server}/project-setup-management/project/${PS_GOL_Competition_Id}/spend-profile/approval
    the user selects the checkbox      approvedByLeadTechnologist
    the user clicks the button/link    jQuery=.button:contains("Approved")
    the user clicks the button/link    jQuery=.modal-accept-profile button:contains("Approve")

grant offer letter is sent to users
    log in as a different user    &{internal_finance_credentials}
    the user navigates to the page    ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/grant-offer-letter/send
    the user clicks the button/link    id=send-gol
    the user clicks the button/link    jQuery=.modal-accept-send-gol .button:contains("Publish to project team")


users upload signed grant offer letter and submit
    log in as a different user    ${PS_GOL_APPLICATION_PM_EMAIL}  ${short_password}
    the user clicks the button/link    link=${PS_GOL_APPLICATION_HEADER}
    the user clicks the button/link    link=Grant offer letter
    choose file    signedGrantOfferLetter    ${upload_folder}/testing.pdf
    the user clicks the button/link    jQuery=.button:contains("Send signed offer letter")
    the user clicks the button/link    jQuery=button:contains("Send to Innovate UK")


grant offer letter is approved
    log in as a different user    &{internal_finance_credentials}
    the user navigates to the page    ${server}/project-setup-management/project/${PS_GOL_APPLICATION_PROJECT}/grant-offer-letter/send
    the user clicks the button/link    jQuery=#content .button:contains("Accept signed grant offer letter")
    the user clicks the button/link     jQuery=.modal-accept-signed-gol .button:contains("Accept signed grant offer letter")
