*** Settings ***
Documentation     INFUND-4851 As a project manager I want to be able to submit an uploaded Grant Offer Letter so that Innovate UK can review my signed copy


Suite Setup       Custom suite setup
Suite Teardown    Close browser and delete emails
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${HighSpeedProjectName}       High-speed rail and its effects on energy

*** Test Cases ***
IFS Admin user can reset GOL in project setup
    [Documentation]  IFS-9611
    [Setup]  Requesting Project ID of this Project
    Given Log in as a different user                    &{ifs_admin_user_credentials}
    When the user navigates to the page                 ${server}/project-setup-management/project/${HighSpeedProjectID}/grant-offer-letter/send
    And Admin resets the GOL
    Then the user should see the element                jQuery = h2:contains("Grant offer letter upload") +* p:contains("No files have been uploaded yet.")

IFS Expert uploads the GOL and sends Grand offer letter
    Given Admin uploads the GOL
    And the user selects the checkbox                 confirmation
    And the user clicks the button/link               id = send-gol
    And the user clicks the button/link               jQuery = .modal-accept-send-gol .govuk-button:contains("Send grant offer letter")
    And the user should not see the element           css = [name = "removeGrantOfferLetterClicked"]
    Then the user should see the element              jQuery = a:contains("Reset grant offer letter")

PM uploads the signed offer letter
    Given Log in as a different user                 &{Research_lead_applicant_credentials}
    When the user clicks the button/link             link = ${HighSpeedProjectName}
    Then the user should see the element             css = li.require-action:last-of-type
    When the user clicks the button/link             link = Grant offer letter
    Then the user should see the element             jQuery = p:contains("The grant offer letter (GOL) has been created using the information provided during project setup.")
    And the user should see the element              jQuery = label:contains(Upload)
    When the user uploads a file                      signedGrantOfferLetter    ${valid_pdf}
    And the user clicks the button/link              css = .govuk-button[data-js-modal="modal-confirm-grant-offer-letter"]
    And the user clicks the button/link              id = submit-gol-for-review
    Then the user should see the element             jQuery = li:contains("Grant offer letter") .status-waiting

IFS Expert user can reject and reset GOL in project setup
    [Documentation]  IFS-9611
    [Setup]  Requesting Project ID of this Project
    Admin rejects the GOL
    Admin resets the GOL
    Then the user should not see the element              jQuery = .warning-alert p:contains("These documents have been reviewed and rejected. We have returned them to the Project Manager.")

*** Keywords ***
the user uploads a file
    [Arguments]  ${name}  ${file}
    the user uploads the file    name = ${name}    ${file}
    Wait Until Page Does Not Contain Without Screenshots    Uploading

Requesting Project ID of this Project
    ${HighSpeedProjectID} =  get project id by name   ${HighSpeedProjectName}
    Set suite variable    ${HighSpeedProjectID}

Admin resets the GOL
    the user clicks the button/link         jQuery = a:contains("Reset grant offer letter")
    the user clicks the button/link         jQuery = button:contains("Reset grant offer letter")

Admin uploads the GOL
    the user navigates to the page     ${server}/project-setup-management/project/${HighSpeedProjectID}/grant-offer-letter/send
    the user uploads the file          grantOfferLetter  ${gol_pdf}
    the user should see the element    jQuery = a:contains("GOL_template.pdf (opens in a new window)")
    #horrible hack but we need to wait for virus scanning
    sleep  5s


Admin rejects the GOL
    log in as a different user            &{ifs_admin_user_credentials}
    the user navigates to the page        ${server}/project-setup-management/project/${HighSpeedProjectID}/grant-offer-letter/send
    the user selects the radio button     REJECTED  rejectGOL
    the user enters text to a text field  id = gol-reject-reason   Rejected
    the user clicks the button/link       id = submit-button
    the user clicks the button/link       jQuery = button:contains("Reject signed grant offer letter")
    the user should see the element       jQuery = .warning-alert p:contains("These documents have been reviewed and rejected. We have returned them to the Project Manager.")


Custom suite setup
    Connect to database  @{database}
    the user logs-in in new browser     ${Elbow_Grease_Lead_PM_Email}  ${short_password}
    execute sql string  INSERT INTO `ifs`.`grant_process_configuration` (`competition_id`, `send_by_default`) VALUES ('${PROJECT_SETUP_COMPETITION}', '1');