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

Suite Setup       Custom suite setup
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          CompAdmin_Commons.robot

*** Variables ***
${public_content_competition_name}    Public content competition

*** Test Cases ***
User can view the public content
    [Documentation]    INFUND-6914
    [Tags]  HappyPath
    Given the internal user navigates to public content  ${public_content_competition_name}
    Then the user should see the element     link=Competition information and search
    And the user should see the element      link=Summary
    And the user should see the element      link=Eligibility
    And the user should see the element      link=Scope
    And the user should see the element      link=Dates
    And the user should see the element      link=How to apply
    And the user should see the element      link=Supporting information
    And the user should see the element      jQuery=button:contains("Publish public content"):disabled

Project Finance can also access the Public content sections
    [Documentation]  INFUND-7602
    [Tags]
    # This checks that also other int users have access to this area
    Given log in as a different user      &{internal_finance_credentials}
    When the internal user navigates to public content  ${public_content_competition_name}
    Then the user should not see an error in the page
    When the user visits the sub sections then he should not see any errors

External users do not have access to the Public content sections
    [Documentation]  INFUND-7602
    [Tags]
    Given log in as a different user     &{collaborator1_credentials}
    When run keyword and ignore error without screenshots  the user navigates to the page  ${public_content_overview}
    Then the user should see permissions error message

Competition information and search: server side validation
    [Documentation]    INFUND-6915
    [Tags]  HappyPath
    [Setup]  log in as a different user    &{Comp_admin1_credentials}
    Given the internal user navigates to public content  ${public_content_competition_name}
    Then the user clicks the button/link   link=Competition information and search
    When the user clicks the button/link            jQuery=.button:contains("Save and return")
    Then the user should see a summary error        Please enter a short description.
    Then the user should see a summary error        Please enter a project funding range.
    Then the user should see a summary error        Please enter an eligibility summary.
    Then the user should see a summary error        Please enter a valid set of keywords.

Competition information and search: Valid values
    [Documentation]    INFUND-6915, INFUND-8363
    [Tags]  HappyPath
    When the user enters text to a text field       id=short-description        Short public description
    And the user enters text to a text field        id=funding-range            Up to £1million
    And the user enters text to a text field        css=[labelledby="eligibility-summary"]      Summary of eligiblity
    When the user enters text to a text field       id=keywords  hellohellohellohellohellohellohellohellohellohellou
    And the user clicks the button/link             jQuery=button:contains("Save and return")
    Then the user should see the element            jQuery=.error-summary-list:contains("Each keyword must be less than 50 characters long.")
    And the user enters text to a text field        id=keywords  Search, Testing, Robot
    And the user clicks the button/link             jQuery=.button:contains("Save and return")
    Then the user should see the element            jQuery=li:nth-of-type(1) img.complete

Competition information and search: ReadOnly
    [Documentation]  INFUND-6915
    [Tags]
    When the user clicks the button/link  link=Competition information and search
    Then the user should see the element  jQuery=dt:contains("Short description") + dd:contains("Short public description")
    And the user should see the element   jQuery=dt:contains("Project funding range") + dd:contains("Up to £1million")
    And the user should see the element   jQuery=dt:contains("Eligibility summary") + dd:contains("Summary of eligiblity")
    And the user should see the element   jQuery=dt:contains("Keywords") + dd:contains("Search,Testing,Robot")
    When the user clicks the button/link  link=Edit
    Then the user should see the element  css=#short-description[value="Short public description"]
    And the user clicks the button/link   jQuery=.button:contains("Save and return")

Summary: server side validation and autosave
    [Documentation]    INFUND-6916, INFUND-7486
    [Tags]
    Given the user clicks the button/link           link=Summary
    And the user should see the text in the page    Text entered into this section will appear in the summary tab
    When the user clicks the button/link            jQuery=.button:contains("Save and return")
    Then the user should see a summary error        Please enter a funding type.
    And the user should see a summary error         Please enter a project size.
    And the user should see a summary error         Please enter a competition description.
    When the user enters valid data in the summary details
    And the user should see the element             jQuery=.buttonlink:contains("+ add new section")

Summary: User enters valid values and saves
    [Documentation]    INFUND-6916, INFUND-7486
    [Tags]  HappyPath
    Given the internal user navigates to public content  ${public_content_competition_name}
    And the user clicks the button/link        link=Summary
    When the user enters valid data in the summary details
    And the user clicks the button/link        jQuery=button:contains("+ add new section")
    When the user enters text to a text field  css=#heading-0  A nice new Heading
    Then the user enters text to a text field   jQuery=.editor:eq(1)  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco ullamcoullamco ullamco ullamco
    And the user uploads the file              id=contentGroups-0.attachment  ${valid_pdf}
    When the user clicks the button/link       jQuery=button:contains("Save and return")
    Then the user should be redirected to the correct page  ${public_content_overview}
    And the user should see the element      link=Summary
    And the user should see the element      css=img[title='The "Summary" section is marked as done']

Summary: Contains the correct values when viewed
    [Documentation]    INFUND-6916, INFUND-7486
    [Tags]
    When the user clicks the button/link      link=Summary
    Then the user should see the element      jQuery=dt:contains("Funding type") + dd:contains("Grant")
    And the user should see the element       jQuery=dt:contains("Project size") + dd:contains("10 millions")
    And the user should see the element       jQuery=h2:contains("A nice new Heading")
    And the user should see the element       jQuery=a:contains("${valid_pdf}")
    And the user should see the element       jQuery=.button:contains("Return to public content")
    When the user clicks the button/link      jQuery=.button-secondary:contains("Edit")
    And the user enters text to a text field  jQuery=.editor:eq(1)  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
    When the user clicks the button/link      jQuery=button:contains("Save and return")
    Then the user should see the element      css=img[title='The "Summary" section is marked as done']

Eligibility: server side validation and autosave
    [Documentation]    INFUND-6916, INFUND-7487
    [Tags]
    When the user clicks the button/link            link=Eligibility
    And the user should see the text in the page    Text entered into this section will appear within the eligibility tab.
    And the user clicks the button/link             jQuery=button:contains("Save and return")
    Then the user should see a summary error        Please enter content.
    And the user should see a summary error         Please enter a heading.
    When the user enters valid data in the eligibility details
    And the user should see the element             jQuery=.buttonlink:contains("+ add new section")

Eligibility: User enters valid values and saves
    [Documentation]    INFUND-6916, INFUND-7487
    [Tags]  HappyPath
    Given the internal user navigates to public content     ${public_content_competition_name}
    When the user clicks the button/link                    link=Eligibility
    And the user enters valid data in the eligibility details
    Then the user enters text to a text field               jQuery=.contentGroup:first-of-type input[id^="heading"]   Nationality Eligibility Heading
    And the user enters text to a text field                jQuery=.contentGroup:first-of-type .editor   You can give your views on new or changing government policies by responding to consultations. Government departments take these responses into consideration before making decisions
    And the user uploads the file                           jQuery=.contentGroup:first-of-type input[id^="contentGroups"][id$="attachment"]     ${valid_pdf}
    Then the user clicks the button/link                    jQuery=button:contains("+ add new section")
    And The user enters text to a text field                jQuery=.contentGroup:nth-of-type(2) input[id^="heading"]   Minimum Eligibility Threshold
    And The user enters text to a text field                jQuery=.contentGroup:nth-of-type(2) .editor    One of the important new changes we are introducing through these reforms is establishing the national eligibility criteria for adult care and support
    And the user uploads the file                           jQuery=.contentGroup:nth-of-type(2) input[id^="contentGroups"][id$="attachment"]   ${valid_pdf}
    When the user clicks the button/link                    jQuery=button:contains("Save and return")
    Then the user should be redirected to the correct page  ${public_content_overview}
    And the user should see the element                     link=Eligibility
    And the user should see the element                     css=img[title='The "Eligibility" section is marked as done']

Eligibility: Contains the correct values when viewed, Edit sections
    [Documentation]    INFUND-6916, INFUND-7487
    [Tags]  HappyPath
    When the user clicks the button/link                        link=Eligibility
    Then the user should see the element                        jQuery=h2:contains("Nationality Eligibility Heading")
    And the user should see the element                         jQuery=a:contains("${valid_pdf}")
    And the user should see the element                         jQuery=.button:contains("Return to public content")
    When the user clicks the button/link                        jQuery=.button-secondary:contains("Edit")
    And the user enters text to a text field                    jQuery=.contentGroup:first-of-type .editor   You can give your views on new or changing government policies by responding to consultations. Government departments rule of 267567£$*90 take these responses into consideration before making decisions, Local authorities can decide to meet needs that do not meet the eligibility criteria, Where they decide to do this, the same steps must be taken as would be if the person did have eligible needs (for example, the preparation of a care and support plan).
    And The user enters text to a text field                    jQuery=.contentGroup:nth-of-type(2) .editor   One of the important new changes we are introducing through these reforms is establishing the national eligibility criteria for adult care and support This is to be achieved through regulations to be made under a power in clause 13 of the Care Bill. These will set a minimum threshold.
    Then the user clicks the button/link                        jQuery=button:contains("+ add new section")
    And The user enters text to a text field                    jQuery=.contentGroup:nth-of-type(3) input[id^="heading"]    Draft Care and Support - Eligibility Criteria
    And the user enters text to a text field                    jQuery=.contentGroup:nth-of-type(3) .editor   In these Regulations— Citation, commencement “basic personal care activities” means essential personal care tasks that a person carries out as part of normal daily, An adult’s needs meet the eligibility criteria if those needs are due to a physical or mental impairment or illness and the effect of such needs is that the adult.
    And the user clicks the button/link                         jQuery=.contentGroup:first-of-type button:contains("Remove")
    And the user clicks the button/link                         jQuery=.contentGroup:nth-of-type(2) button:contains("Remove")
    Then the user uploads the file                              jQuery=.contentGroup:nth-of-type(3) input[id^="contentGroups"][id$="attachment"]    ${valid_pdf}
    And the user uploads the file                               jQuery=.contentGroup:first-of-type input[id^="contentGroups"][id$="attachment"]     ${valid_pdf}
    And the user uploads the file                               jQuery=.contentGroup:nth-of-type(2) input[id^="contentGroups"][id$="attachment"]    ${valid_pdf}
    Then the user clicks the button/link                        jQuery=.contentGroup:nth-of-type(3) button:contains("Remove")
    And the user uploads the file                               jQuery=.contentGroup:nth-of-type(3) input[id^="contentGroups"][id$="attachment"]    ${valid_pdf}
    When the user clicks the button/link                        jQuery=button:contains("Save and return")
    And the user should see the element                         css=img[title='The "Eligibility" section is marked as done']

Scope: Server side validation
    [Documentation]  INFUND-7488
    [Tags]  HappyPath
    When the user clicks the button/link  link=Scope
    And the user clicks the button/link   jQuery=button:contains("Save and return")
    Then the user should see a summary error  Please enter content.
    And the user should see a summary error   Please enter a heading.

Scope: Add, remove sections and submit
    [Documentation]    INFUND-6918, INFUND-7602
    [Tags]  HappyPath
    Given the user can add and remove multiple content groups
    When the user clicks the button/link                        jQuery=button:contains("Save and return")
    And the user should see the element  css=img[title='The "Scope" section is marked as done']

Dates: Add, remove dates and submit
    [Documentation]    INFUND-6919
    [Tags]  HappyPath
    When the user clicks the button/link                         link=Dates
    Then the user should see the text in the page                1 February ${nextyear}
    And the user should see the text in the page                 Competition opens
    And the user should see the text in the page                 Submission deadline, competition closed.
    And the user should see the text in the page                 Applicants notified
    And the user can add and remove multiple event groups
    And the user should see the element  css=img[title='The "Dates" section is marked as done']

How to apply: server side validation and autosave
    [Documentation]    INFUND-7490
    [Tags]
    When the user clicks the button/link            link=How to apply
    Then the user should see the element            jQuery=h1:contains("How to apply")
    And the user should see the text in the page    Text entered into this section will appear within the how to apply tab.
    When the user clicks the button/link            jQuery=button:contains("Save and return")
    Then the user should see a summary error        Please enter content.
    And the user should see a summary error         Please enter a heading.
    When the user enters valid data in How-to-apply details
    Then the user should see the element            jQuery=.buttonlink:contains("+ add new section")

How to apply: User enters valid values and saves
    [Documentation]    INFUND-7490
    [Tags]  HappyPath
    Given the internal user navigates to public content     ${public_content_competition_name}
    When the user clicks the button/link                    link=How to apply
    And the user enters valid data in How-to-apply details
    Then the user enters text to a text field               jQuery=.contentGroup:first-of-type input[id^="heading"]   The application process
    And the user enters text to a text field                jQuery=.contentGroup:first-of-type .editor   External, independent experts assess the quality your application. We will then select the projects that we fund, to build a portfolio of projects as described in the competition guidance for applicants. Please read this carefully before you apply.
    And the user uploads the file                           jQuery=.contentGroup:first-of-type input[id^="contentGroups"][id$="attachment"]     ${valid_pdf}
    Then the user clicks the button/link                    jQuery=button:contains("+ add new section")
    And The user enters text to a text field                jQuery=.contentGroup:nth-of-type(2) input[id^="heading"]   Application questions
    And The user enters text to a text field                jQuery=.contentGroup:nth-of-type(2) .editor    Application questions are available for reference and to assist with preparation. If you need more information, contact the competition helpline on 0700 123 98765 or email us at support@innovateTest.worth.com
    And the user uploads the file                           jQuery=.contentGroup:nth-of-type(2) input[id^="contentGroups"][id$="attachment"]    ${valid_pdf}
    When the user clicks the button/link                    jQuery=button:contains("Save and return")
    Then the user should be redirected to the correct page  ${public_content_overview}
    And the user should see the element                     link=How to apply
    And the user should see the element                     css=img[title='The "How to apply" section is marked as done']

How to apply: Contains the correct values when viewed, Edit sections
    [Documentation]    INFUND-6920 INFUND-7602, INFUND-7490
    [Tags]  HappyPath
    When the user clicks the button/link            link=How to apply
    Then the user should see the element            jQuery=h2:contains("The application process")
    And the user should see the element             jQuery=a:contains("${valid_pdf}")
    And the user should see the element             jQuery=.button:contains("Return to public content")
    When the user clicks the button/link            jQuery=.button-secondary:contains("Edit")
    And the user enters text to a text field        jQuery=.contentGroup:nth-of-type(1) .editor   External independent experts assess the quality your application. We will then select the projects that we fund, to build a portfolio of projects as described in the competition guidance. Government departments & Some departments, like the Ministry of Defence, cover the whole UK. Others don’t – the Department for Work and Pensions doesn't cover Northern Ireland. This is because some aspects of government are devolved to Scotland, Wales and Northern Ireland. Other public bodiesThese have varying degrees of independence but are directly accountable to ministers. There are 4 types of non-departmental public bodies (NDPBs).Executive NDPBs do work for the government in specific areas
    And the user moves focus to the element         css=#contentGroup-row-1 >div.form-group.textarea-wrapped >div.editor
    And The user enters text to a text field        jQuery=.contentGroup:nth-of-type(2) .editor  Application questions are available for reference and to assist with preparation. If you need more information, contact the competition helpline on 0700 123 98765.
    Then the user clicks the button/link            jQuery=button:contains("+ add new section")
    And The user enters text to a text field        jQuery=.contentGroup:nth-of-type(3) input[id^="heading"]    Application Rules -- Competition Procedures
    And the user enters text to a text field        jQuery=.contentGroup:nth-of-type(3) .editor   Sets out the rules for Competition framework provision funded by the CodeTechnology: ADReedoor8793£$%^^&&*^%%!@. This document forms part of the ADReedoor8793£$%^^&&*^%%!@ - Funding Rules 2016 to 2017. This document sets out the additional funding rules for Competition frameworks. You must read it together with other relevant funding rule documents. These include: Follow the Instructions.
    Then the user clicks the button/link            jQuery=button:contains("+ add new section")
    And The user enters text to a text field        jQuery=.contentGroup:nth-of-type(4) input[id^="heading"]    Competition Officers Contact
    And the user enters text to a text field        jQuery=.contentGroup:nth-of-type(4) .editor  You can access an up-to-date list of areas where Competition is managed locally and how to contact them on GOV.UK. Follow the guidelines attached.
    And the user uploads the file                   jQuery=.contentGroup:nth-of-type(4) input[id^="contentGroups"][id$="attachment"]    ${valid_pdf}
    And the user clicks the button/link             jQuery=.contentGroup:first-of-type button:contains("Remove")
    And the user clicks the button/link             jQuery=.contentGroup:nth-of-type(2) button:contains("Remove")
    Then the user clicks the button/link            jQuery=button:contains("+ add new section")
    And The user enters text to a text field        jQuery=.contentGroup:nth-of-type(5) input[id^="heading"]    Confidentiality and Conflicts
    And the user enters text to a text field        jQuery=.contentGroup:nth-of-type(5) .editor     We are confident that awarding an increase to your funding allocation is a good use of public funds, Providers with a Financial Memorandum or Conditions of Funding (Grant) or Conditions of Funding. For more information email us back on support@innovateTest.worth.com and find the attached memorandum.
    Then the user uploads the file                  jQuery=.contentGroup:nth-of-type(3) input[id^="contentGroups"][id$="attachment"]    ${valid_pdf}
    And the user uploads the file                   jQuery=.contentGroup:first-of-type input[id^="contentGroups"][id$="attachment"]     ${valid_pdf}
    And the user uploads the file                   jQuery=.contentGroup:nth-of-type(2) input[id^="contentGroups"][id$="attachment"]    ${valid_pdf}
    Then the user clicks the button/link            jQuery=.contentGroup:nth-of-type(3) button:contains("Remove")
    And the user uploads the file                   jQuery=.contentGroup:nth-of-type(3) input[id^="contentGroups"][id$="attachment"]    ${valid_pdf}
    And the user uploads the file                   jQuery=.contentGroup:nth-of-type(5) input[id^="contentGroups"][id$="attachment"]    ${valid_pdf}
    When the user clicks the button/link            jQuery=button:contains("Save and return")
    And the user should see the element             css=img[title='The "How to apply" section is marked as done']

Supporting information: Add, remove sections and submit
    [Documentation]    INFUND-6921 INFUND-7602
    [Tags]  HappyPath
    When the user clicks the button/link                         link=Supporting information
    Then the user can add and remove multiple content groups
    When the user clicks the button/link                        jQuery=button:contains("Save and return")
    And the user should see the element  css=img[title='The "Supporting information" section is marked as done']

Publish public content: Publish once all sections are complete
    [Documentation]    INFUND-6914
    [Tags]  HappyPath
    Given the user should not see the text in the page  Last published
    When the user clicks the button/link    jQuery=button:contains("Publish public content")
    Then the user should see the element    jQuery=small:contains("Last published")
    And the user should not see the element             jQuery=button:contains("Publish public content")
    When the user clicks the button/link                link=Competition information and search
    And the user clicks the button/link                 link=Edit
    Then the user should not see the element            jQuery=button:contains("Save and return")
    And the user should see the element                 jQuery=button:contains("Publish and return")

The user is able to edit and publish again
    [Documentation]  INFUND-6914
    [Tags]
    Given the user enters text to a text field  css=[labelledby="eligibility-summary"]  Some other summary
    And the user clicks the button/link         jQuery=button:contains("Publish and return")
    When the user should see all sections completed
    Then the user should see the element        jQuery=small:contains("${today}")
    And the user should not see the element     jQuery=button:contains("Publish and return")
    When the user clicks the button/link        link=Return to setup overview
    Then the user should see the element        JQuery=p:contains("${today}")

Guest user can filter competitions by Keywords
    [Documentation]  INFUND-6923
    [Tags]  HappyPath
    [Setup]  The guest user opens the browser
    Given the user navigates to the page  ${frontDoor}
    When the user enters text to a text field  id=keywords  Robot
    And the user clicks the button/link        jQuery=button:contains("Update results")
    Then the user should see the element       jQuery=a:contains("${public_content_competition_name}")

Guest user can see the updated Summary information
    [Documentation]  INFUND-7486
    [Tags]
    Given the user clicks the button/link  link=Public content competition
    And the user clicks the button/link    link=Summary
    Then the user should see the element   jQuery=.column-third:contains("Description") ~ .column-two-thirds:contains("This is a Summary description")
    And the user should see the element    jQuery=.column-third:contains("Funding type") ~ .column-two-thirds:contains("Grant")
    And the user should see the element    jQuery=.column-third:contains("Project size") ~ .column-two-thirds:contains("10 millions")
    And the user should see the element    jQuery=.column-third:contains("A nice new Heading") ~ .column-two-thirds:contains("Ut enim ad minim veniam,")
    Then guest user downloads the file     ${server}/competition/${competitionId}/download/43  ${DOWNLOAD_FOLDER}/summary.pdf
    [Teardown]  Remove the file from the operating system  summary.pdf

Guest user can see the updated Eligibility information
    [Documentation]  INFUND-7487
    [Tags]
    Given the user clicks the button/link   link=Eligibility
    Then the user should see the element    jQuery=.column-third:contains("Nationality Eligibility Heading") ~ .column-two-thirds:contains("changing government policies")
    Then the user should see the element    jQuery=.column-third:contains("Minimum Eligibility Threshold") ~ .column-two-thirds:contains("new changes we are introducing")
    Then the user should see the element    jQuery=.column-third:contains("Draft Care and Support - Eligibility Criteria") ~ .column-two-thirds:contains("basic personal care activities")

Guest user downloads Eligibility files
    [Documentation]  INFUND-7487
    [Tags]
    When guest user downloads the file  ${server}/competition/${competitionId}/download/44  ${DOWNLOAD_FOLDER}/eli.pdf
    Then Remove the file from the operating system  eli.pdf
    When guest user downloads the file  ${server}/competition/${competitionId}/download/45  ${DOWNLOAD_FOLDER}/eligi.pdf
    Then Remove the file from the operating system  eligi.pdf
    When guest user downloads the file   ${server}/competition/${competitionId}/download/46  ${DOWNLOAD_FOLDER}/eligibility.pdf
    Then Remove the file from the operating system  eligibility.pdf

The guest user can see updated scope information
    [Documentation]    INFUND-7488
    [Tags]
    Given the user clicks the button/link    link=Scope
    Then the user should see the element      jQuery=.column-third:contains("Heading 1") ~ .column-two-thirds:contains("Content 1")
    And the user should see the element      jQuery=.column-third:contains("Heading 2") ~ .column-two-thirds:contains("Content 2")
    And guest user downloads the file   ${server}/competition/${competitionId}/download/48    ${DOWNLOAD_FOLDER}/scope.pdf
    [Teardown]  Remove the file from the operating system  scope.pdf

The guest user can see updated date information
   [Documentation]    INFUND-7489
   [Tags]
   Given the user clicks the button/link    link=Dates
   And the user should see the element    jQuery=dt:contains("1 February ${nextyear}") + dd:contains("Competition opens")
   And the user should see the element    jQuery=dt:contains("1 February ${nextyear}") + dd:contains("Competition closes")
   And the user should see the element    jQuery=dt:contains("2 February ${nextyear}") + dd:contains("Applicants notified")
   And the user should see the element    jQuery=dt:contains("12 December ${nextyear}") + dd:contains("Content 1")
   And the user should see the element    jQuery=dt:contains("20 December ${nextyear}") + dd:contains("Content 2")

Guest user can see the updated How-to-apply information
    [Documentation]  INFUND-7490
    [Tags]
    Given the user clicks the button/link       link=How to apply
    Then the user should see the element        jQuery=.column-third:contains("The application process") ~ .column-two-thirds:contains("independent experts assess the quality your application")
    And the user should see the element         jQuery=.column-third:contains("Application questions") ~ .column-two-thirds:contains("contact the competition helpline on 0700 123 98765")
    And the user should see the element         jQuery=.column-third:contains("Application Rules -- Competition Procedures") ~ .column-two-thirds:contains("additional funding rules for Competition frameworks")
    And the user should see the element         jQuery=.column-third:contains("Competition Officers Contact") ~ .column-two-thirds:contains("can access an up-to-date list of areas")
    And the user should see the element         jQuery=.column-third:contains("Confidentiality and Conflicts") ~ .column-two-thirds:contains("confident that awarding an increase to your funding")

*** Keywords ***
Custom suite setup
    Connect to Database  @{database}
    Guest user log-in    &{Comp_admin1_credentials}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    User creates a new competition   ${public_content_competition_name}
    ${competitionId}=  get comp id from comp title  ${public_content_competition_name}
    set suite variable  ${competitionId}
    ${public_content_overview}=    catenate    ${server}/management/competition/setup/public-content/${competitionId}
    Set suite variable  ${public_content_overview}
    ${today} =  get today
    set suite variable  ${today}
    ${day} =  get tomorrow day
    Set suite variable  ${day}
    ${month} =  get tomorrow month
    set suite variable  ${month}

User creates a new competition
    [Arguments]    ${competition_name}
    Given the user navigates to the page    ${CA_UpcomingComp}
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    When the user fills in the CS Initial details      ${competition_name}  01  02  ${nextyear}
    And the user fills in the CS Milestones    01  02  02  ${nextyear}

the user enters valid data in the summary details
    The user enters text to a text field    css=.editor  This is a Summary description
    the user selects the radio button       fundingType    Grant
    the user enters text to a text field    id=project-size   10 millions

the user enters valid data in the eligibility details
    The user enters text to a text field    css=#heading-0              Minimum Eligibility Threshold
    The user enters text to a text field    jQuery=.editor:eq(0)        We are establishing a system that will place a greater focus on prevention, which will mean that the care and support needs of people will be considered earlier than is currently the case. This will build on the strengths of the person and look to prevent, reduce or delay their need for care and support. The Bill will introduce a new system that will support people to live independently and put personalisation at the heart of the process

the user enters valid data in How-to-apply details
    The user enters text to a text field    css=#heading-0              Read the Guidance
    The user enters text to a text field    jQuery=.editor:eq(0)        To make an application on our online system, you must have a validated applicant profile. We take up to five working days to validate a profile, so you must take this into account when you’re thinking about when to apply. You will keep your own contact details up to date by editing your applicant profile (please see the guidance sheet on our user accounts and applicant profiles page for more information)

the user can add and remove multiple content groups
    When the user enters text to a text field   id=heading-0    Heading 1
    And the user enters text to a text field    jQuery=.editor:eq(0)     Content 1
    And the user uploads the file               id=contentGroups-0.attachment  ${valid_pdf}
    Then the user should see the element        jQuery=.uploaded-file:contains("testing.pdf")
    And the user clicks the button/link         jQuery=button:contains("Remove")
    And the user clicks the button/link         jQuery=button:contains("+ add new section")
    And the user enters text to a text field    id=heading-1    Heading 2
    And the user enters text to a text field    jQuery=.editor:eq(1)     Content 2
    And the user uploads the file               id=contentGroups-1.attachment  ${valid_pdf}
    And the user clicks the button/link         jQuery=button:contains("+ add new section")
    And the user enters text to a text field    id=heading-2    Heading 3
    And the user enters text to a text field    jQuery=.editor:eq(2)     Content 3
    When the user uploads the file              id=contentGroups-2.attachment  ${text_file}
    Then the user should see the element        jQuery=.error-summary-list:contains("Please upload a file in .pdf format only.")
    #    And the user uploads the file               id=contentGroups-2.attachment  ${too_large_pdf}
    #    Then the user should see the element        jQuery=h1:contains("Attempt to upload a large file")
    #    and the user goes back to the previous page
    #    And the user should not see an error in the page
    # I comment those lines out due to TODO INFUND-8358
    And the user clicks the button/link         jQuery=button:contains("Remove section"):eq(1)
    Then the user should not see the element    id=heading-2
    And the user should not see the element     jQuery=.editor:eq(2)

the user can add and remove multiple content groups for summary
    When the user clicks the button/link        jQuery=button:contains("+ add new section")
    And the user clicks the button/link         jQuery=button:contains("Save and return")
    Then the user should see a summary error    Please enter a heading.
    And the user should see a summary error     Please enter content.
    When the user enters text to a text field   id=heading-0    Heading 1
    And the user enters text to a text field    jQuery=.editor:eq(1)     Content 1
    And the user clicks the button/link         jQuery=button:contains("+ add new section")
    And the user enters text to a text field    id=heading-1    Heading 2
    And the user enters text to a text field    jQuery=.editor:eq(2)     Content 2
    And the user clicks the button/link         jQuery=button:contains("+ add new section")
    And the user enters text to a text field    id=heading-2    Heading 3
    And the user enters text to a text field    jQuery=.editor:eq(3)     Content 3
    And the user clicks the button/link         jQuery=button:contains("Remove section"):eq(2)
    Then the user should not see the element    id=heading-2
    And the user should not see the element     jQuery=.editor:eq(3)

the user can add and remove multiple event groups
    When the user clicks the button/link        jQuery=button:contains("+ add new event")
    And the user clicks the button/link         jQuery=button:contains("Save and return")
    Then the user should see a summary error    Please enter a valid date.
    And the user should see a summary error     Please enter valid content.
    And the user enters text to a text field    id=dates-0-day      60
    And the user enters text to a text field    id=dates-0-month    -6
    And the user clicks the button/link         jQuery=button:contains("Save and return")
    Then the user should see a summary error    must be between 1 and 31
    And the user should see a summary error     must be between 1 and 12
    When the user enters text to a text field   id=dates-0-day      12
    And the user enters text to a text field    id=dates-0-month    12
    And the user enters text to a text field    id=dates-0-year     ${nextyear}
    And the user enters text to a text field    jQuery=.editor:eq(0)     Content 1
    And the user clicks the button/link         jQuery=button:contains("+ add new event")
    And the user enters text to a text field    id=dates-1-day      20
    And the user enters text to a text field    id=dates-1-month    12
    And the user enters text to a text field    id=dates-1-year     ${nextyear}
    And the user enters text to a text field    jQuery=.editor:eq(1)     Content 2
    And the user clicks the button/link         jQuery=button:contains("+ add new event")
    And the user enters text to a text field    id=dates-2-day      30
    And the user enters text to a text field    id=dates-2-month    12
    And the user enters text to a text field    id=dates-2-year     ${nextyear}
    And the user enters text to a text field    jQuery=.editor:eq(2)     Content 3
    And the user clicks the button/link         jQuery=button:contains("Remove event"):eq(2)
    Then the user should not see the element    id=dates-2-day
    And the user should not see the element     id=dates-2-month
    And the user should not see the element     id=dates-2-year
    And the user should not see the element     jQuery=.editor:eq(2)
    And the user clicks the button/link         jQuery=button:contains("Save and return")

the user visits the sub sections then he should not see any errors
    the user visits  Competition information and search
    the user visits  Summary
    the user visits  Eligibility
    the user visits  Scope
    the user visits  Dates
    the user visits  How to apply
    the user visits  Supporting information

the user visits
    [Arguments]  ${section}
    the user clicks the button/link  link=${section}
    the user should see the element  jQuery=h1:contains("${section}")
    the user should not see an error in the page
    the user clicks the button/link  link=Public content

the user should see all sections completed
    :FOR  ${i}  IN RANGE  1  8
    \    the user should see the element  jQuery=li:nth-child(${i}) img.complete
