*** Settings ***
Documentation     IFS-12952 Super administrator - 'Remove Grant Offer Letter' redesign and permission changes

Suite Setup       Custom suite setup
Suite Teardown    Close browser and delete emails
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${HighSpeedProjectName}       High-speed rail and its effects on energy
${HighSpeedProjectID}         ${project_ids["${HighSpeedProjectName}"]}

*** Test Cases ***
IFS Admin user can reset GOL in project setup
    [Documentation]  IFS-12952
    Given the user navigates to the page                 ${server}/project-setup-management/project/${HighSpeedProjectID}/grant-offer-letter/send
    When Admin resets the GOL
    Then the user should see the element                jQuery = h2:contains("Grant offer letter upload") +* p:contains("No files have been uploaded yet.")

IFS Admin uploads the GOL and sends Grant offer letter
    [Documentation]  IFS-12952
    Given Admin uploads the GOL
    When the user selects the checkbox                confirmation
    And the user clicks the button/link               id = send-gol
    And the user clicks the button/link               jQuery = .modal-accept-send-gol .govuk-button:contains("Send grant offer letter")
    And the user should not see the element           css = [name = "removeGrantOfferLetterClicked"]
    Then the user should see the element              jQuery = a:contains("Reset grant offer letter")

PM uploads the signed offer letter
    [Documentation]  IFS-12952
    Given PM uploads and sends the signed Grant offer letter
    Then the user should see the element                          jQuery = li:contains("Grant offer letter") .status-waiting

IFS Admin user can reject and reset GOL in project setup
    [Documentation]  IFS-12952
    Given Admin rejects the GOL
    When Admin resets the GOL
    Then the user should not see the element              jQuery = .warning-alert p:contains("These documents have been reviewed and rejected. We have returned them to the Project Manager.")

*** Keywords ***
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

PM uploads and sends the signed Grant offer letter
    log in as a different user                   &{Research_lead_applicant_credentials}
    the user clicks the button/link              link = ${HighSpeedProjectName}
    the user should see the element              css = li.require-action:last-of-type
    the user clicks the button/link              link = Grant offer letter
    the user should see the element              jQuery = p:contains("The grant offer letter (GOL) has been created using the information provided during project setup.")
    the user should see the element              jQuery = label:contains(Upload)
    the user uploads the file                    signedGrantOfferLetter    ${valid_pdf}
    the user clicks the button/link              css = .govuk-button[data-js-modal="modal-confirm-grant-offer-letter"]
    the user clicks the button/link              id = submit-gol-for-review

Custom suite setup
    Connect to database  @{database}
    the user logs-in in new browser              &{ifs_admin_user_credentials}
#    execute sql string  INSERT INTO `ifs`.`grant_process_configuration` (`competition_id`, `send_by_default`) VALUES ('${PROJECT_SETUP_COMPETITION}', '1');