*** Settings ***
Documentation     INFUND-6914 Create 'Public content' menu page for "Front Door" setup pages
...
...               INFUND-6916 As a Competitions team member I want to create a Public content summary page
...
...               INFUND-7602 Add / Remove sections for Competition setup > Public content
...
...               INFUND-7486 Create Competition > Summary tab for external "Front Door" view of competition summary
...
...               INFUND-7489 Create 'Competition' > 'Dates' tab for external "Front Door" view of competition dates
...
...               INFUND-7487 Create Competition > Eligibility tab for external "Front Door" view of competition eligibility
...
...               INFUND-7488 Create 'Competition' > 'Scope' tab for external "Front Door" view of competition scope
...
...               INFUND-7490 Create Competition > How to apply tab for external "Front Door" view of competition eligibility
...
...               IFS-1969 As a comp exec I am able to set a Funding type of Loan in Public content > Summary
...
...               IFS-4982 Move Funding type selection from front door to Initial details
...
...               IFS-5370 Public content review button is always redirecting to Dates page
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Variables ***
${public_content_competition_name}      Public content competition

*** Test Cases ***
User can view the public content
    [Documentation]    INFUND-6914
    [Tags]
    Given the internal user navigates to public content  ${public_content_competition_name}
    Then the user should not see the element            jQuery = .message-alert:contains("This information will be publicly viewable by prospective applicants.")
    And the user should not see the element             jQuery = .message-alert:contains("Competition URL")
    And the user should see the element                 link = Competition information and search
    And the user should see the element                 link = Summary
    And the user should see the element                 link = Eligibility
    And the user should see the element                 link = Scope
    And the user should see the element                 link = Dates
    And the user should see the element                 link = How to apply
    And the user should see the element                 link = Supporting information
    And the user should see the element                 jQuery = button:contains("Publish content"):disabled

Project Finance can also access the Public content sections
    [Documentation]  INFUND-7602
    [Tags]
    # This checks that also other int users have access to this area
    Given log in as a different user                    &{internal_finance_credentials}
    When the internal user navigates to public content  ${public_content_competition_name}
    Then the user should not see an error in the page
    When the user visits the sub sections then he should not see any errors

External users do not have access to the Public content sections
    [Documentation]  INFUND-7602
    [Tags]
    Given log in as a different user                                     &{collaborator1_credentials}
    Then The user navigates to the page and gets a custom error message  ${public_content_overview}  ${403_error_message}

Competition information and search: server side validation
    [Documentation]    INFUND-6915  IFS-179
    [Tags]
    [Setup]  log in as a different user                  &{Comp_admin1_credentials}
    Given the internal user navigates to public content  ${public_content_competition_name}
    Then the user clicks the button/link                 link = Competition information and search
    When the user clicks the button/link                 jQuery = .govuk-button:contains("Save and review")
    Then the user should see a summary error             Please enter a short description.
    Then the user should see a summary error             Please enter a project funding range.
    Then the user should see a summary error             Please enter an eligibility summary.
    Then the user should see a summary error             Please enter a valid set of keywords.
    Then the user should see a summary error             Please select a publish setting.

Competition information and search: Valid values
    [Documentation]    INFUND-6915  INFUND-8363  IFS-179
    [Tags]
    When the user enters text to a text field       id = shortDescription  Short public description
    And the user enters text to a text field        id = projectFundingRange  Up to £1million
    And the user selects the radio button           publishSetting  invite
    And the user enters text to a text field        css = [aria-labelledby="eligibilitySummary-label"]  Summary of eligiblity
    When the user enters text to a text field       id = keywords  hellohellohellohellohellohellohellohellohellohellou
    And the user clicks the button/link             jQuery = button:contains("Save and review")
    Then the user should see the element            jQuery = .govuk-error-summary__list:contains("Each keyword must be less than 50 characters long.")
    And the user enters text to a text field        id = keywords  Search, Testing, Robot
    Then the user clicks the button/link            jQuery = .govuk-button:contains("Save and review")
    And the user clicks the button/link             link = Return to public content
    Then the user should see the element            css = li:nth-of-type(1) .task-status-complete

Competition information and search: ReadOnly
    [Documentation]  INFUND-6915  IFS-179
    [Tags]
    When the user clicks the button/link    link = Competition information and search
    Then the user should see the element    jQuery = dt:contains("Short description") + dd:contains("Short public description")
    And the user should see the element     jQuery = dt:contains("Project funding range") + dd:contains("Up to £1million")
    And the user should see the element     jQuery = dt:contains("Eligibility summary") + dd:contains("Summary of eligiblity")
    And the user should see the element     jQuery = dt:contains("Keywords") + dd:contains("Search,Testing,Robot")
    And the user should see the element     jQuery = dt:contains("Publish setting") + dd:contains("Invite only")
    When the user clicks the button/link    link = Edit
    Then the user should see the element    css = #shortDescription[value="Short public description"]
    And the user clicks the button/link     jQuery = .govuk-button:contains("Save and review")
    And the user clicks the button/link     link = Return to public content

Summary: server side validation and autosave
    [Documentation]    INFUND-6916  INFUND-7486  IFS-4982
    [Tags]
    # Using Loan as a funding Type in order to check the ticket IFS-1969
    Given the user clicks the button/link         link = Summary
    And the user should see the element           jQuery = p:contains("Text entered into this section will appear in the summary tab")
    When the user clicks the button/link          jQuery = .govuk-button:contains("Save and review")
    Then the user should see a summary error      Please enter a project size.
    And the user should see a summary error       Please enter a competition description.
    When the user enters valid data in the summary details
    And the user should see the element           jQuery = .button-clear:contains("+ add new section")
    When the user enters text to a text field     id = projectSize    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius.Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus.
    And the user clicks the button/link           jQuery = button:contains("Save and review")
    Then the user should see a field error        Project size has a maximum length of 255 characters.

Summary: User enters valid values and saves
    [Documentation]    INFUND-6916  INFUND-7486
    [Tags]
    # Using Loan as a funding Type in order to check the ticket IFS-1969
    Given the internal user navigates to public content  ${public_content_competition_name}
    And the user clicks the button/link          link = Summary
    When the user enters valid data in the summary details
    And the user clicks the button/link          jQuery = button:contains("+ add new section")
    When the user enters text to a text field    id = contentGroups[0].heading  A nice new Heading
    Then the user enters text to a text field    jQuery = .editor:eq(1)  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco ullamcoullamco ullamco ullamco
    And the user uploads the file                name = contentGroups[0].attachment  ${valid_pdf}
    When the user clicks the button/link         jQuery = button:contains("Save and review")
    Then the user clicks the button/link         link = Return to public content
    And the user should be redirected to the correct page  ${public_content_overview}
    Then the user should see the element         link = Summary
    And the user should see the element          css = li:nth-child(2) .task-status-complete

Summary: Contains the correct values when viewed
    [Documentation]    INFUND-6916, INFUND-7486, IFS-1969
    [Tags]
    When the user clicks the button/link      link = Summary
    Then the user should see the element      jQuery = h2:contains("Project size")
    And the user should see the element       jQuery = div:contains("10 millions")
    And the user should see the element       jQuery = h2:contains("A nice new Heading")
    And the user should see the element       jQuery = a:contains("${valid_pdf}")
    And the user should see the element       link = Return to public content
    When the user clicks the button/link      jQuery = .govuk-button:contains("Edit")
    And the user enters text to a text field  jQuery = .editor:eq(1)  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
    When the user clicks the button/link      jQuery = button:contains("Save and review")
    Then the user clicks the button/link      link = Return to public content
    And the user should see the element       jQuery = li:nth-child(2) .task-status-complete

Eligibility: server side validation and autosave
    [Documentation]    INFUND-6916, INFUND-7487
    [Tags]
    When the user clicks the button/link            link = Eligibility
    And the user should see the element             jQuery = p:contains("Text entered into this section will appear within the eligibility tab.")
    And the user clicks the button/link             jQuery = button:contains("Save and review")
    Then the user should see a summary error        Please enter content.
    And the user should see a summary error         Please enter a heading.
    When the user enters valid data in the eligibility details
    And the user should see the element             jQuery = .button-clear:contains("+ add new section")

Eligibility: User enters valid values and saves
    [Documentation]    INFUND-6916, INFUND-7487
    [Tags]
    Given the internal user navigates to public content     ${public_content_competition_name}
    When the user clicks the button/link                    link = Eligibility
    And the user enters valid data in the eligibility details
    Then the user enters text to a text field               css = .contentGroup:first-of-type input[id^="contentGroups"][id$="heading"]   Nationality Eligibility Heading
    And the user enters text to a text field                css = .contentGroup:first-of-type .editor   You can give your views on new or changing government policies by responding to consultations. Government departments take these responses into consideration before making decisions
    And the user uploads the file                           css = .contentGroup:first-of-type input[name^="contentGroups"][name$="attachment"]     ${valid_pdf}
    Then the user clicks the button/link                    jQuery = button:contains("+ add new section")
    And The user enters text to a text field                css = .contentGroup:nth-of-type(2) input[id^="contentGroups"][id$="heading"]   Minimum Eligibility Threshold
    And The user enters text to a text field                css = .contentGroup:nth-of-type(2) .editor    One of the important new changes we are introducing through these reforms is establishing the national eligibility criteria for adult care and support
    And the user uploads the file                           css = .contentGroup:nth-of-type(2) input[name^="contentGroups"][name$="attachment"]   ${valid_pdf}
    When the user clicks the button/link                    jQuery = button:contains("Save and review")
    Then the user clicks the button/link                    link = Return to public content
    And the user should be redirected to the correct page   ${public_content_overview}
    Then the user should see the element                    link = Eligibility
    And the user should see the element                     css = li:nth-child(3) .task-status-complete

Eligibility: Contains the correct values when viewed, Edit sections
    [Documentation]    INFUND-6916, INFUND-7487
    [Tags]
    When the user clicks the button/link      link = Eligibility
    Then the user should see the element      jQuery = h2:contains("Nationality Eligibility Heading")
    And the user should see the element       jQuery = a:contains("${valid_pdf}")
    And the user should see the element       link = Return to public content
    When the user clicks the button/link      jQuery = .govuk-button:contains("Edit")
    And the user enters text to a text field  css = .contentGroup:first-of-type .editor   You can give your views on new or changing government policies by responding to consultations. Government departments rule of 267567£$*90 take these responses into consideration before making decisions, Local authorities can decide to meet needs that do not meet the eligibility criteria, Where they decide to do this, the same steps must be taken as would be if the person did have eligible needs (for example, the preparation of a care and support plan).
    And The user enters text to a text field  css = .contentGroup:nth-of-type(2) .editor   One of the important new changes we are introducing through these reforms is establishing the national eligibility criteria for adult care and support This is to be achieved through regulations to be made under a power in clause 13 of the Care Bill. These will set a minimum threshold.
    Then the user clicks the button/link      jQuery = button:contains("+ add new section")
    And The user enters text to a text field  css = .contentGroup:nth-of-type(3) input[id^="contentGroups"][id$="heading"]    Draft Care and Support - Eligibility Criteria
    And the user enters text to a text field  css = .contentGroup:nth-of-type(3) .editor   In these Regulations— Citation, commencement “basic personal care activities” means essential personal care tasks that a person carries out as part of normal daily, An adult’s needs meet the eligibility criteria if those needs are due to a physical or mental impairment or illness and the effect of such needs is that the adult.
    And the user clicks the button/link       jQuery = .contentGroup:first-of-type button.remove-file
    Wait Until Page Does Not Contain Without Screenshots    Removing
    And the user clicks the button/link       jQuery = .contentGroup:nth-of-type(2) button.remove-file
    Wait Until Page Does Not Contain Without Screenshots    Removing
    Then the user uploads the file            css = .contentGroup:nth-of-type(3) input[name^="contentGroups"][name$="attachment"]    ${valid_pdf}
    And the user uploads the file             css = .contentGroup:first-of-type input[name^="contentGroups"][name$="attachment"]     ${valid_pdf}
    And the user uploads the file             css = .contentGroup:nth-of-type(2) input[name^="contentGroups"][name$="attachment"]    ${valid_pdf}
    Then the user clicks the button/link      jQuery = .contentGroup:nth-of-type(3) button.remove-file
    Wait Until Page Does Not Contain Without Screenshots    Removing
    And the user should see the element       jQuery = .contentGroup:nth-of-type(3) label:contains("Upload")
    And the user uploads the file             css = .contentGroup:nth-of-type(3) input[name^="contentGroups"][name$="attachment"]    ${valid_pdf}
    When the user clicks the button/link      jQuery = button:contains("Save and review")
    And the user clicks the button/link       link = Return to public content
    Then the user should see the element      css = li:nth-child(3) .task-status-complete

Scope: Server side validation
    [Documentation]  INFUND-7488
    [Tags]
    When the user clicks the button/link      link = Scope
    And the user clicks the button/link       jQuery = button:contains("Save and review")
    Then the user should see a summary error  Please enter content.
    And the user should see a summary error   Please enter a heading.

Scope: Add, remove sections and submit
    [Documentation]    INFUND-6918, INFUND-7602
    [Tags]
    Given the user can add and remove multiple content groups
    When the user clicks the button/link  jQuery = button:contains("Save and review")
    And the user clicks the button/link   link = Return to public content
    Then the user should see the element  css = li:nth-child(4) .task-status-complete

Dates: Add, remove dates and submit
    [Documentation]    INFUND-6919
    [Tags]
    When the user clicks the button/link           link = Dates
    Then the user should see the element           jQuery = h2:contains("${tomorrowMonthWord} ${nextyear}")
    And the user should see the element            jQuery = .govuk-body:contains("Competition opens")
    And the user should see the element            jQuery = .govuk-body:contains("Submission deadline, competition closed.")
    And the user should see the element            jQuery = .govuk-body:contains("Applicants notified")
    And the user can add and remove multiple event groups
    And the user should see the element            css = li:nth-child(5) .task-status-complete

How to apply: server side validation and autosave
    [Documentation]    INFUND-7490
    [Tags]
    When the user clicks the button/link          link = How to apply
    Then the user should see the element          jQuery = h1:contains("Public content how to apply")
    And the user should see the element           jQuery = p:contains("Text entered into this section will appear within the how to apply tab.")
    When the user clicks the button/link          jQuery = button:contains("Save and review")
    Then the user should see a summary error      Please enter content.
    And the user should see a summary error       Please enter a heading.
    When the user enters valid data in How-to-apply details
    Then the user should see the element          jQuery = .button-clear:contains("+ add new section")

How to apply: User enters valid values and saves
    [Documentation]    INFUND-7490
    [Tags]
    Given the internal user navigates to public content     ${public_content_competition_name}
    When the user clicks the button/link                    link = How to apply
    And the user enters valid data in How-to-apply details
    Then the user enters text to a text field               css = .contentGroup:first-of-type input[id^="contentGroups"][id$="heading"]   The application process
    And the user enters text to a text field                css = .contentGroup:first-of-type .editor   External, independent experts assess the quality your application. We will then select the projects that we fund, to build a portfolio of projects as described in the competition guidance for applicants. Please read this carefully before you apply.
    And the user uploads the file                           css = .contentGroup:first-of-type input[name^="contentGroups"][name$="attachment"]     ${valid_pdf}
    Then the user clicks the button/link                    jQuery = button:contains("+ add new section")
    And The user enters text to a text field                css = .contentGroup:nth-of-type(2) input[id^="contentGroups"][id$="heading"]   Application questions
    And The user enters text to a text field                css = .contentGroup:nth-of-type(2) .editor    Application questions are available for reference and to assist with preparation. If you need more information, contact the competition helpline on 0700 123 98765 or email us at support@innovateTest.worth.com
    And the user uploads the file                           css = .contentGroup:nth-of-type(2) input[name^="contentGroups"][name$="attachment"]    ${valid_pdf}
    When the user clicks the button/link                    jQuery = button:contains("Save and review")
    Then the user clicks the button/link                    link = Return to public content
    And the user should be redirected to the correct page   ${public_content_overview}
    Then the user should see the element                    link = How to apply
    And the user should see the element                     css = li:nth-child(6) .task-status-complete

How to apply: Contains the correct values when viewed, Edit sections
    [Documentation]    INFUND-6920  INFUND-7602  INFUND-7490
    [Tags]
    When the user clicks the button/link      link = How to apply
    Then the user should see the element      jQuery = h2:contains("The application process")
    And the user should see the element       jQuery = a:contains("${valid_pdf}")
    And the user should see the element       link = Return to public content
    When the user clicks the button/link      jQuery = .govuk-button:contains("Edit")
    And the user enters text to a text field  css = .contentGroup:nth-of-type(1) .editor   External independent experts assess the quality your application. We will then select the projects that we fund, to build a portfolio of projects as described in the competition guidance. Government departments & Some departments, like the Ministry of Defence, cover the whole UK. Others don’t – the Department for Work and Pensions doesn't cover Northern Ireland. This is because some aspects of government are devolved to Scotland, Wales and Northern Ireland. Other public bodiesThese have varying degrees of independence but are directly accountable to ministers. There are 4 types of non-departmental public bodies (NDPBs).Executive NDPBs do work for the government in specific areas
    And Set Focus To Element                  css = #contentGroup-row-1 >div.govuk-form-group.textarea-wrapped >div.editor
    And The user enters text to a text field  css = .contentGroup:nth-of-type(2) .editor  Application questions are available for reference and to assist with preparation. If you need more information, contact the competition helpline on 0700 123 98765.
    Then the user clicks the button/link      jQuery = button:contains("+ add new section")
    And The user enters text to a text field  css = .contentGroup:nth-of-type(3) input[id^="contentGroups"][id$="heading"]    Application Rules -- Competition Procedures
    And the user enters text to a text field  css = .contentGroup:nth-of-type(3) .editor   Sets out the rules for Competition framework provision funded by the CodeTechnology: ADReedoor8793£$%^^&&*^%%!@. This document forms part of the ADReedoor8793£$%^^&&*^%%!@ - Funding Rules 2016 to 2017. This document sets out the additional funding rules for Competition frameworks. You must read it together with other relevant funding rule documents. These include: Follow the Instructions.
    Then the user clicks the button/link      jQuery = button:contains("+ add new section")
    And The user enters text to a text field  css = .contentGroup:nth-of-type(4) input[id^="contentGroups"][id$="heading"]    Competition Officers Contact
    And the user enters text to a text field  css = .contentGroup:nth-of-type(4) .editor  You can access an up-to-date list of areas where Competition is managed locally and how to contact them on GOV.UK. Follow the guidelines attached.
    And the user uploads the file             css = .contentGroup:nth-of-type(4) input[name^="contentGroups"][name$="attachment"]    ${valid_pdf}
    And the user clicks the button/link       jQuery = .contentGroup:first-of-type button.remove-file
    Wait Until Page Does Not Contain Without Screenshots    Removing
    And the user clicks the button/link       jQuery = .contentGroup:nth-of-type(2) button.remove-file
    Wait Until Page Does Not Contain Without Screenshots    Removing
    Then the user clicks the button/link      jQuery = button:contains("+ add new section")
    And The user enters text to a text field  css = .contentGroup:nth-of-type(5) input[id^="contentGroups"][id$="heading"]    Confidentiality and Conflicts
    And the user enters text to a text field  css = .contentGroup:nth-of-type(5) .editor     We are confident that awarding an increase to your funding allocation is a good use of public funds, Providers with a Financial Memorandum or Conditions of Funding (Grant) or Conditions of Funding. For more information email us back on support@innovateTest.worth.com and find the attached memorandum.
    Then the user uploads the file            css = .contentGroup:nth-of-type(3) input[name^="contentGroups"][name$="attachment"]    ${valid_pdf}
    And the user uploads the file             css = .contentGroup:first-of-type input[name^="contentGroups"][name$="attachment"]     ${valid_pdf}
    And the user uploads the file             css = .contentGroup:nth-of-type(2) input[name^="contentGroups"][name$="attachment"]    ${valid_pdf}
    Then the user clicks the button/link      jQuery = .contentGroup:nth-of-type(3) button.remove-file
    Wait Until Page Does Not Contain Without Screenshots    Removing
    And the user should see the element       jQuery = .contentGroup:nth-of-type(3) label:contains("Upload")
    And the user uploads the file             css = .contentGroup:nth-of-type(3) input[name^="contentGroups"][name$="attachment"]    ${valid_pdf}
    And the user uploads the file             css = .contentGroup:nth-of-type(5) input[name^="contentGroups"][name$="attachment"]    ${valid_pdf}
    When the user clicks the button/link      jQuery = button:contains("Save and review")
    And the user clicks the button/link       link = Return to public content
    Then the user should see the element      css = li:nth-child(6) .task-status-complete

Supporting information: Add, remove sections and submit
    [Documentation]    INFUND-6921 INFUND-7602
    [Tags]
    When the user clicks the button/link  link = Supporting information
    Then the user can add and remove multiple content groups
    When the user clicks the button/link  jQuery = button:contains("Save and review")
    And the user clicks the button/link   link = Return to public content
    Then the user should see the element  css = li:nth-child(7) .task-status-complete

Publish public content: Publish once all sections are complete
    [Documentation]    INFUND-6914
    [Tags]
    Given the user should not see the element           jQuery = small:contains("Last published")
    When the user clicks the button/link                jQuery = button:contains("Publish content")
    Then the user should see the element                jQuery = small:contains("Last published")
    And the user should not see the element             jQuery = button:contains("Publish content")
    When the user clicks the button/link                link = Competition information and search
    And the user clicks the button/link                 link = Edit
    Then the user should not see the element            jQuery = button:contains("Save and review")
    And the user should see the element                 jQuery = button:contains("Publish and review")
    Then the user clicks the button/link                jQuery = a:contains("Public content")

User can view the competition url for invite only competitions
    [Documentation]    IFS-262  IFS-5370
    [Tags]
    Given the user should not see the element           jQuery = .message-alert:contains("This information will be publicly viewable by prospective applicants.")
    When the user clicks the button/link                jQuery = a:contains("${server}/competition/${competitionId}/overview")
    Then the user should see the element                jQuery = h1:contains("Public content competition")
    And the user should see the element                 jQUery = .govuk-body:contains("This is a Summary description")
    Then the internal user navigates to public content  ${public_content_competition_name}
    When the user clicks the button/link                link = Competition information and search
    And the user clicks the button/link                 link = Edit
    Then the user selects the radio button              publishSetting  public
    When the user clicks the button/link                jQuery = button:contains("Publish and review")
    Then the user should see the element                jQuery = h1:contains("Competition information and search")
    And the user clicks the button/link                 link = Return to public content
    Then the user should see the element                jQuery = .message-alert:contains("This information will be publicly viewable by prospective applicants.")
    And the user should not see the element             jQuery = p:contains("Competition URL:")
    Then the user clicks the button/link                link = Competition information and search
    And the user clicks the button/link                 link = Edit
    Then the user selects the radio button              publishSetting  invite

The user is able to edit and publish again
    [Documentation]  INFUND-6914
    [Tags]
    Given the user enters text to a text field  css = [aria-labelledby="eligibilitySummary-label"]  Some other summary
    And the user clicks the button/link         jQuery = button:contains("Publish and review")
    Then the user clicks the button/link        link = Return to public content
    When the user should see all sections completed
    Then the user should see the element        jQuery = small:contains("${today}")
    And the user should not see the element     jQuery = button:contains("Publish and review")
    When the user clicks the button/link        link = Return to setup overview
    Then the user should see the element        JQuery = .notification:contains("${today}")

Guest user not find the invite only competition by Keywords
    [Documentation]  IFS-261
    [Tags]
    [Setup]  the user logs out if they are logged in
    Given the user navigates to the page       ${frontDoor}
    When the user enters text to a text field  id = keywords  Robot
    And the user clicks the button/link        jQuery = button:contains("Update results")
    Then the user should not see the element   jQuery = a:contains("${public_content_competition_name}")

The user is able to make the competition public
    [Documentation]  IFS-261, IFS-179  IFS-5370
    [Tags]
    [Setup]  The user logs-in in new browser             &{Comp_admin1_credentials}
    Given the internal user navigates to public content  ${public_content_competition_name}
    Then the user should see the element                 link = Competition information and search
    When the user clicks the button/link                 link = Competition information and search
    And the user clicks the button/link                  link = Edit
    Then the user selects the radio button               publishSetting  public
    And the user clicks the button/link                  jQuery = .govuk-button:contains("Publish and review")
    When the user clicks the button/link                 link = Return to public content
    And the user should redirect to the correct page after publish and review
    Then the user should see the element                 jQuery = a:contains("Return to setup overview")
    [Teardown]  the user logs out if they are logged in

Guest user can filter competitions by Keywords
    [Documentation]  INFUND-6923
    [Tags]
    Given the user navigates to the page       ${frontDoor}
    When the user enters text to a text field  id = keywords  Robot
    And the user clicks the button/link        jQuery = button:contains("Update results")
    Then the user should see the element       link = ${public_content_competition_name}

Guest user can see the updated Summary information
    [Documentation]  INFUND-7486 IFS-1969
    [Tags]
    Given the user clicks the button/link                  link = Public content competition
    And the user clicks the button/link                    link = Summary
    Then the user should see the element                   jQuery = .govuk-grid-column-one-third:contains("Description") ~ .govuk-grid-column-two-thirds:contains("This is a Summary description")
    And the user should see the element                    jQuery = .govuk-grid-column-one-third:contains("Project size") ~ .govuk-grid-column-two-thirds:contains("10 millions")
    And the user should see the element                    jQuery = .govuk-grid-column-one-third:contains("A nice new Heading") ~ .govuk-grid-column-two-thirds:contains("Ut enim ad minim veniam,")
    Then guest user downloads the file                     ${server}/competition/${competitionId}/download/43  ${DOWNLOAD_FOLDER}/summary.pdf
    [Teardown]  Remove the file from the operating system  summary.pdf

Guest user can see the updated Eligibility information
    [Documentation]  INFUND-7487
    [Tags]
    Given the user clicks the button/link  link = Eligibility
    Then the user should see the element   jQuery = .govuk-grid-column-one-third:contains("Nationality Eligibility Heading") ~ .govuk-grid-column-two-thirds:contains("changing government policies")
    Then the user should see the element   jQuery = .govuk-grid-column-one-third:contains("Minimum Eligibility Threshold") ~ .govuk-grid-column-two-thirds:contains("new changes we are introducing")
    Then the user should see the element   jQuery = .govuk-grid-column-one-third:contains("Draft Care and Support - Eligibility Criteria") ~ .govuk-grid-column-two-thirds:contains("basic personal care activities")

Guest user downloads Eligibility files
    [Documentation]  INFUND-7487
    [Tags]
    When guest user downloads the file              ${server}/competition/${competitionId}/download/44  ${DOWNLOAD_FOLDER}/eli.pdf
    Then Remove the file from the operating system  eli.pdf
    When guest user downloads the file              ${server}/competition/${competitionId}/download/45  ${DOWNLOAD_FOLDER}/eligi.pdf
    Then Remove the file from the operating system  eligi.pdf
    When guest user downloads the file              ${server}/competition/${competitionId}/download/46  ${DOWNLOAD_FOLDER}/eligibility.pdf
    Then Remove the file from the operating system  eligibility.pdf

The guest user can see updated scope information
    [Documentation]    INFUND-7488
    [Tags]
    Given the user clicks the button/link                  link = Scope
    Then the user should see the element                   jQuery = .govuk-grid-column-one-third:contains("Heading 1") ~ .govuk-grid-column-two-thirds:contains("Content 1")
    And the user should see the element                    jQuery = .govuk-grid-column-one-third:contains("Heading 2") ~ .govuk-grid-column-two-thirds:contains("Content 2")
    And guest user downloads the file                      ${server}/competition/${competitionId}/download/48    ${DOWNLOAD_FOLDER}/scope.pdf
    [Teardown]  Remove the file from the operating system  scope.pdf

The guest user can see updated date information
   [Documentation]    INFUND-7489
   [Tags]
   Given the user clicks the button/link  link = Dates
   And the user should see the element    jQuery = dt:contains("${nextyear}") + dd:contains("Competition opens")
   And the user should see the element    jQuery = dt:contains("${nextyear}") + dd:contains("Competition closes")
   And the user should see the element    jQuery = dt:contains("${nextyear}") + dd:contains("Applicants notified")
   And the user should see the element    jQuery = dt:contains("${nextyear}") + dd:contains("Content 1")

Guest user can see the updated How-to-apply information
    [Documentation]  INFUND-7490
    [Tags]
    Given the user clicks the button/link  link = How to apply
    Then the user should see the element   jQuery = .govuk-grid-column-one-third:contains("The application process") ~ .govuk-grid-column-two-thirds:contains("independent experts assess the quality your application")
    And the user should see the element    jQuery = .govuk-grid-column-one-third:contains("Application questions") ~ .govuk-grid-column-two-thirds:contains("contact the competition helpline on 0700 123 98765")
    And the user should see the element    jQuery = .govuk-grid-column-one-third:contains("Application Rules -- Competition Procedures") ~ .govuk-grid-column-two-thirds:contains("additional funding rules for Competition frameworks")
    And the user should see the element    jQuery = .govuk-grid-column-one-third:contains("Competition Officers Contact") ~ .govuk-grid-column-two-thirds:contains("can access an up-to-date list of areas")
    And the user should see the element    jQuery = .govuk-grid-column-one-third:contains("Confidentiality and Conflicts") ~ .govuk-grid-column-two-thirds:contains("confident that awarding an increase to your funding")

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Connect to database  @{database}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    ${today} =  get today
    set suite variable  ${today}
    ${day} =  get tomorrow day
    Set suite variable  ${day}
    ${month} =  get tomorrow month
    set suite variable  ${month}
    ${tomorrowMonthWord} =  get tomorrow month as word
    set suite variable  ${tomorrowMonthWord}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    User creates a new competition   ${public_content_competition_name}
    ${competitionId} =   get comp id from comp title  ${public_content_competition_name}
    set suite variable  ${competitionId}
    ${public_content_overview} =     catenate    ${server}/management/competition/setup/public-content/${competitionId}
    Set suite variable  ${public_content_overview}

User creates a new competition
    [Arguments]    ${competition_name}
    Given the user navigates to the page    ${CA_UpcomingComp}
    When the user clicks the button/link    jQuery = .govuk-button:contains("Create competition")
    When the user fills in the CS Initial details  ${competition_name}  ${month}  ${nextyear}  ${compType_Programme}  2  GRANT
    And the user selects the Terms and Conditions
    And the user fills in the CS Milestones     PROJECT_SETUP   ${month}   ${nextyear}

the user enters valid data in the summary details
    The user enters text to a text field    css = .editor  This is a Summary description
    the user enters text to a text field    id = projectSize   10 millions

the user enters valid data in the eligibility details
    The user enters text to a text field    id = contentGroups[0].heading              Minimum Eligibility Threshold
    The user enters text to a text field    jQuery = .editor:eq(0)        We are establishing a system that will place a greater focus on prevention, which will mean that the care and support needs of people will be considered earlier than is currently the case. This will build on the strengths of the person and look to prevent, reduce or delay their need for care and support. The Bill will introduce a new system that will support people to live independently and put personalisation at the heart of the process

the user enters valid data in How-to-apply details
    The user enters text to a text field    id = contentGroups[0].heading              Read the Guidance
    The user enters text to a text field    jQuery = .editor:eq(0)        To make an application on our online system, you must have a validated applicant profile. We take up to five working days to validate a profile, so you must take this into account when you’re thinking about when to apply. You will keep your own contact details up to date by editing your applicant profile (please see the guidance sheet on our user accounts and applicant profiles page for more information)

the user can add and remove multiple content groups
    When the user enters text to a text field  id = contentGroups[0].heading    Heading 1
    And the user enters text to a text field   jQuery = .editor:eq(0)     Content 1
    And the user uploads the file              name = contentGroups[0].attachment  ${5mb_pdf}
    Then the user should see the element       jQuery = a:contains("testing_5MB.pdf")
    And the user clicks the button/link        jQuery = button.remove-file
    Wait Until Page Does Not Contain Without Screenshots    Removing
    And the user clicks the button/link        jQuery = button:contains("+ add new section")
    And the user enters text to a text field   id = contentGroups[1].heading    Heading 2
    And the user enters text to a text field   jQuery = .editor:eq(1)     Content 2
    And the user uploads the file              name = contentGroups[1].attachment  ${valid_pdf}
    And the user clicks the button/link        jQuery = button:contains("+ add new section")
    And the user enters text to a text field   id = contentGroups[2].heading    Heading 3
    And the user enters text to a text field   jQuery = .editor:eq(2)     Content 3
    When the user uploads the file             name = contentGroups[2].attachment  ${text_file}
    Then the user should see the element       jQuery = :contains("${wrong_filetype_validation_error}")
    And the user uploads the file              name = contentGroups[2].attachment  ${too_large_pdf}
    Then the user should see the element       jQuery = :contains("${too_large_5MB_validation_error}")
    And the user clicks the button/link        jQuery = button:contains("Remove section"):eq(1)
    Then the user should not see the element   id = contentGroups[2].heading
    And the user should not see the element    jQuery = .editor:eq(2)

the user can add and remove multiple content groups for summary
    When the user clicks the button/link       jQuery = button:contains("+ add new section")
    And the user clicks the button/link        jQuery = button:contains("Save and review")
    Then the user should see a summary error   Please enter a heading.
    And the user should see a summary error    Please enter content.
    When the user enters text to a text field  id = contentGroups[0].heading    Heading 1
    And the user enters text to a text field   jQuery = .editor:eq(1)     Content 1
    And the user clicks the button/link        jQuery = button:contains("+ add new section")
    And the user enters text to a text field   id = contentGroups[1].heading    Heading 2
    And the user enters text to a text field   jQuery = .editor:eq(2)     Content 2
    And the user clicks the button/link        jQuery = button:contains("+ add new section")
    And the user enters text to a text field   id = contentGroups[2].heading    Heading 3
    And the user enters text to a text field   jQuery = .editor:eq(3)     Content 3
    And the user clicks the button/link        jQuery = button:contains("Remove section"):eq(2)
    Then the user should not see the element   id = contentGroups[2].heading
    And the user should not see the element    jQuery = .editor:eq(3)

the user can add and remove multiple event groups
    When the user clicks the button/link       jQuery = button:contains("+ add new event")
    And the user clicks the button/link        jQuery = button:contains("Save and review")
    Then The user should see a summary error   ${enter_a_valid_date}
    #TODO add keywork to check field error check when IFS-3126 done
    And The user should see a field and summary error    Please enter valid content.
    And the user enters text to a text field   id = dates[0].day      60
    And the user enters text to a text field   id = dates[0].month    -6
    And the user enters text to a text field   id = dates[0].year     22
    And the user clicks the button/link        jQuery = button:contains("Save and review")
    Then The user should see a field and summary error   Please enter a valid day.
    And The user should see a field and summary error    Please enter a valid month.
    And The user should see a field and summary error    Please enter a valid year.
    When the user enters text to a text field  id = dates[0].day      12
    And the user enters text to a text field   id = dates[0].month    12
    And the user enters text to a text field   id = dates[0].year     ${nextyear}
    And the user enters text to a text field   jQuery = .editor:eq(0)     Content 1
    And the user clicks the button/link        jQuery = button:contains("+ add new event")
    And the user enters text to a text field   id = dates[1].day      20
    And the user enters text to a text field   id = dates[1].month    12
    And the user enters text to a text field   id = dates[1].year     ${nextyear}
    And the user enters text to a text field   jQuery = .editor:eq(1)     Content 2
    And the user clicks the button/link        jQuery = button:contains("Remove event"):eq(1)
    Then the user should not see the element   id = dates[1].day
    And the user should not see the element    id = dates[1].month
    And the user should not see the element    id = dates[1].year
    And the user should not see the element    jQuery = .editor:eq(1)
    And the user clicks the button/link        jQuery = button:contains("Save and review")
    And the user clicks the button/link        link = Return to public content

the user visits the sub sections then he should not see any errors
    the user visits  Competition information and search   Competition information and search
    the user visits  Summary   Public content summary
    the user visits  Eligibility   Public content eligibility
    the user visits  Scope   Public content scope
    the user visits  Dates   Public content dates
    the user visits  How to apply   Public content how to apply
    the user visits  Supporting information   Public content supporting information

the user visits
    [Arguments]  ${section}  ${title}
    the user clicks the button/link  link = ${section}
    the user should see the element  jQuery = h1:contains("${title}")
    the user should not see an error in the page
    the user clicks the button/link  link = Public content

the user should see all sections completed
    :FOR  ${i}  IN RANGE  1  8
    \    the user should see the element  css = li:nth-child(${i}) .task-status-complete

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user should redirect to the correct page after publish and review
    the user able to see edit view for  Summary   Public content summary
    the user able to see edit view for  Eligibility   Public content eligibility
    the user able to see edit view for  Scope   Public content scope
    the user able to see edit view for  Dates   Public content dates
    the user able to see edit view for  How to apply   Public content how to apply
    the user able to see edit view for  Supporting information   Public content supporting information

the user able to see edit view for
    [Arguments]  ${section_name}  ${sectionTitle}
    the user clicks the button/link    link = ${section_name}
    the user clicks the button/link    link = Edit
    the user clicks the button/link    jQuery = button:contains("Publish and review")
    the user should see the element    jQuery = h1:contains("${sectionTitle}")
    the user clicks the button/link    link = Return to public content