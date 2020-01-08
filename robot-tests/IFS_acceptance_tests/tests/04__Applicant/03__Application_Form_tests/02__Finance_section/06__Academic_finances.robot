*** Settings ***
Documentation     INFUND-917: As an academic partner i want to input my finances according to the JES field headings, so that i enter my figures into the correct sections
...
...               INFUND-918: As an academic partner i want to be able to mark my finances as complete, so that the lead partner can have confidence in my finances
...
...               INFUND-2399: As a Academic partner I want to be able to add my finances including decimals for accurate recording of my finances
...
...               INFUND-8347: Update 'Your project costs' for academics
...
...               IFS-2879: As a Research applicant I MUST accept the grant terms and conditions
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot

*** Test Cases ***
Academic finances should be editable when lead marks them as complete
    [Documentation]    INFUND-2314
    [Tags]
    [Setup]    Lead applicant marks the finances as complete
    Given Log in as a different user          &{collaborator2_credentials}
    When the user navigates to Your-finances page  Evolution of the global phosphorus cycle
    And the user clicks the button/link       link = Your project costs
    Then the user should not see the element  css = #incurred-staff[readonly]
    [Teardown]    Lead applicant marks the finances as incomplete

Academic finance validations
    [Documentation]    INFUND-2399  IFS-2879
    [Tags]  HappyPath
    [Setup]    Log in as a different user            &{collaborator2_credentials}
    When the user navigates to Your-finances page    Evolution of the global phosphorus cycle
    And the user clicks the button/link              link = Your project costs
    And the applicant enters invalid inputs
    And Mark academic finances as complete
    And the user should see the element              css = .govuk-error-summary__list

Academic finance calculations
    [Documentation]    INFUND-917, INFUND-2399
    [Tags]  HappyPath
    Given the user can remove the uploaded file   remove_jes  jes-form150.pdf (opens in new window)
    When the academic fills in the project costs
    And the user clicks the button/link  link = Your project costs
    Then the subtotals should be correctly updated

Large pdf upload not allowed
    [Documentation]    INFUND-2720
    [Setup]  the user clicks the button/link         jQuery = .button-clear:contains("Edit your project costs")
    Given the user can remove the uploaded file      remove_jes  ${5mb_pdf}
    When the academic partner uploads a file         ${too_large_pdf}
    Then the user should see a field error           ${too_large_10MB_validation_error}

Non pdf uploads not allowed
    [Documentation]    INFUND-2720
    When the academic partner uploads a file        ${text_file}
    Then the user should see a field error          ${wrong_filetype_validation_error}

Lead applicant can't upload a JeS file
    [Documentation]    INFUND-2720
    [Tags]  HappyPath
    [Setup]    log in as a different user     &{lead_applicant_credentials}
    Given the user navigates to Your-finances page  Evolution of the global phosphorus cycle
    When the user clicks the button/link      link = Your project costs
    Then the user should not see the element  css = .upload-section label

Academics upload
    [Documentation]    INFUND-917
    [Tags]
    [Setup]    log in as a different user              &{collaborator2_credentials}
    When the user navigates to Your-finances page      Evolution of the global phosphorus cycle
    And the user clicks the button/link                link = Your project costs
    When the academic partner uploads a file           ${5mb_pdf}
    Then the user should not see the element           jQUery = p:contains("No file currently uploaded.")
    And the user should see the element                jQuery = a:contains(${5mb_pdf} (opens in a new window))
    And open pdf link                                  jQuery = a:contains(${5mb_pdf} (opens in a new window))

Academic partner can view the file on the finances overview
    [Documentation]    INFUND-917
    [Tags]
    When the user navigates to the finance overview of the academic  Evolution of the global phosphorus cycle
    Then the user should not see an error in the page
    [Teardown]    the user goes back to the previous page

Lead applicant can't view the file on the finances page
    [Documentation]    INFUND-917
    [Tags]
    [Setup]    log in as a different user              &{lead_applicant_credentials}
    When the user navigates to Your-finances page      Evolution of the global phosphorus cycle
    And the user clicks the button/link                link = Your project costs
    Then the user should not see the element           link = ${5mb_pdf}

Academic finances JeS link showing
    [Documentation]    INFUND-2402, INFUND-8347
    [Tags]
    [Setup]    log in as a different user             &{collaborator2_credentials}
    When the user navigates to Your-finances page     Evolution of the global phosphorus cycle
    And the user should see correct grant percentage
    When the user clicks the button/link              link = Your project costs
    Then the user can see JeS details

Mark all as complete
    [Documentation]    INFUND-918  IFS-2879
    [Tags]
    Given log in as a different user               &{collaborator2_credentials}
    And the user navigates to Your-finances page   Evolution of the global phosphorus cycle
    And the user clicks the button/link            link = Your project costs
    And the user should see the element            jQuery = a:contains(${5mb_pdf} (opens in a new window))
    When the user enters text to a text field      css = input[name="tsbReference"]  123123
    Then textfield value should be                 css = input[name="tsbReference"]  123123
    When the user clicks the button/link           jQuery = button:contains("Mark as complete")
    And the user enters the project location
    And the user clicks the button/link            link = Your funding
    And the user marks your funding section as complete
    Then the user should see the element           jQuery = h1:contains("Your project finances")
    And the user navigates to the finance overview of the academic  Evolution of the global phosphorus cycle
    And the user should see the element            jQuery = .finance-summary tr:contains("EGGS") img[src*="/images/ifs-images/icons/icon-tick"]

User should not be able to edit or upload the form
    [Documentation]    INFUND-2437
    [Tags]
    When the user navigates to Your-finances page     Evolution of the global phosphorus cycle
    And the user should see correct grant percentage
    And the user clicks the button/link               link = Your project costs
    Then the user should not see the element          jQuery = button:contains("Remove")
    And the user should see the element               css = [name$="incurredStaff"][readonly]

File delete should not be allowed when marked as complete
    [Documentation]    INFUND-2437
    [Tags]
    When the user navigates to Your-finances page        Evolution of the global phosphorus cycle
    Then the user should not see the element             jQuery = button:contains("Remove")

Academic finance overview
    [Documentation]  INFUND-917 INFUND-2399
    [Tags]
    Given the user navigates to the finance overview of the academic  Evolution of the global phosphorus cycle
    Then the finance table should be correct
    Then the user should not see an error in the page
    [Teardown]  The user marks the academic application finances as incomplete

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    the guest user opens the browser
    Connect to database  @{database}

the subtotals should be correctly updated
    the user should see the element  jQuery = [data-mirror="#subtotal-directly-allocated"]:contains("£3,047")
    the user should see the element  jQuery = [data-mirror="#subtotal-exceptions"]:contains("£8,013")

the academic partner uploads a file
    [Arguments]    ${file_name}
    the user uploads the file    css = .upload-section input    ${file_name}
    Wait Until Page Does Not Contain Without Screenshots    Uploading
    the user should see the element   css = .remove-file

the finance table should be correct
    Wait Until Element Contains Without Screenshots  jQuery = .project-cost-breakdown tr:contains("EGGS") td:nth-of-type(1)  £32,698
    Element Should Contain                           jQuery = .project-cost-breakdown tr:contains("EGGS") td:nth-of-type(2)  4,407
    Element Should Contain                           jQuery = .project-cost-breakdown tr:contains("EGGS") td:nth-of-type(3)  8,909
    Element Should Contain                           jQuery = .project-cost-breakdown tr:contains("EGGS") td:nth-of-type(4)  4,244
    Element Should Contain                           jQuery = .project-cost-breakdown tr:contains("EGGS") td:nth-of-type(6)  0
    Element Should Contain                           jQuery = .project-cost-breakdown tr:contains("EGGS") td:nth-of-type(7)  4,243
    Element Should Contain                           jQuery = .project-cost-breakdown tr:contains("EGGS") td:nth-of-type(8)  10,895

Lead applicant marks the finances as complete
    Logging in and Error Checking                      &{lead_applicant_credentials}
    the user clicks the button/link                    link = Evolution of the global phosphorus cycle
    then the user selects research category            Feasibility studies
    the user navigates to Your-finances page           Evolution of the global phosphorus cycle
    the user marks the finances as complete            Evolution of the global phosphorus cycle  labour costs  n/a  no

Lead applicant marks the finances as incomplete
    log in as a different user                  &{lead_applicant_credentials}
    the user navigates to Your-finances page    Evolution of the global phosphorus cycle
    the user clicks the button/link             link = Your funding
    the user clicks the button/link             jQuery = button:contains("Edit")

the user can see JeS details
    the user should see the element  link = Je-S website
    the user should see the element  css = a[href*="https://je-s.rcuk.ac.uk"]
    the user should see the element  css = a[href*="https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance/guidance-for-academics-applying-via-the-je-s-system"]

the applicant enters invalid inputs
    the user clicks the button/link       jQuery = button:contains("Open all")
    The user enters text to a text field  css = [name="incurredStaff"]  ${EMPTY}
    The user enters text to a text field  css = [name="incurredTravel"]  ${EMPTY}
    The user enters text to a text field  css = [name="incurredOtherCosts"]  ${EMPTY}
    The user enters text to a text field  css = [name="allocatedInvestigators"]  ${EMPTY}
    The user enters text to a text field  css = [name="allocatedEstateCosts"]  ${EMPTY}
    The user enters text to a text field  css = [name="allocatedOtherCosts"]  ${EMPTY}
    The user enters text to a text field  css = [name="indirectCosts"]  ${EMPTY}
    The user enters text to a text field  css = [name="exceptionsStaff"]  ${EMPTY}
    The user enters text to a text field  css = [name="exceptionsOtherCosts"]  ${EMPTY}
    The user enters text to a text field  css = [name="tsbReference"]  ${EMPTY}

Mark academic finances as complete
    the user clicks the button/link  id = mark-all-as-complete
    the user should see a field and summary error  This field cannot be left blank.

the user should see correct grant percentage
    the user should see the text in the element   css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(2)  Funding level (%)
    the user should see the text in the element   css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(2)  0

The user marks the academic application finances as incomplete
    the user navigates to Your-finances page  Evolution of the global phosphorus cycle
    the user clicks the button/link    link = Your project costs
    Set Focus To Element      jQuery = button:contains("Edit")
    the user clicks the button/link    jQuery = button:contains("Edit")
    wait for autosave

Custom suite teardown
    Close browser and delete emails
    Disconnect from database