*** Settings ***
Documentation     INFUND-3780: As an Assessor I want the system to autosave my work so that I can be sure that my assessment is always in its most current state.
...
...               INFUND-3303: As an Assessor I want the ability to reject the application after I have been given access to the full details so I can make Innovate UK aware.
...
...               INFUND-3720 As an Assessor I can see deadlines for the assessment of applications currently in assessment on my dashboard, so that I am reminded to deliver my work on time
...
...               INFUND-1188 As an assessor I want to be able to review my assessments from one place so that I can work in my favoured style when reviewing
...
...               INFUND-4203: Prevent navigation options appearing for questions that are not part of an assessment
...
...               INFUND-1483: As an Assessor I want to be asked to confirm whether the application is in the correct research category and scope so that Innovate UK know that the application aligns with the competition
...
...               INFUND-3394 Acceptance Test: Assessor should be able to view the full application and finance summaries for assessment
...
...               INFUND-3859: As an Assessor I want to see how many words I can enter as feedback so that I know how much I can write.
...
...               INFUND-6281 As an Assessor I want to see specific scoring guidance text for each application question so that I can score the question accurately
...
...               INFUND-8065 File download links are broken for assessors
...
...               IFS-2854 Allow assessors to see full application finances
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Assessment overview should show all the questions
    [Documentation]    INFUND-3400  INFUND-1188
    [Tags]
    Given The user clicks the button/link    link = ${IN_ASSESSMENT_COMPETITION_NAME}
    When the user clicks the button/link     link = ${IN_ASSESSMENT_APPLICATION_5_TITLE}
    Then the uesr should see assessment overview details

Number of days remaining until assessment submission
    [Documentation]    INFUND-3720
    [Tags]  MySQL
    Given the user should see the element  jQuery = .sub-header:contains("days left to submit")
    #Then the days remaining should be correct (Top of the page)  ${getSimpleMilestoneDate(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_DEADLINE")}
    # TODO IFS-3176

Reject application (Unable to assess this application)
    [Documentation]    INFUND-3540  INFUND-5379
    [Tags]
    Given the user clicks the button/link                      jQuery = .govuk-details__summary-text:contains("Unable to assess this application")
    When the user fills in rejection details
    And the user clicks the button/link                       jquery = button:contains("Reject")
    Then The user should be redirected to the correct page    ${Assessor_application_dashboard}
    And The user should not see the element                   link = ${IN_ASSESSMENT_APPLICATION_5_TITLE}

Assessor should not be able to access the rejected application
    [Documentation]    INFUND-5188
    [Tags]
    Given the user navigates to the page and gets a custom error message    ${SERVER}/assessment/${IN_ASSESSMENT_APPLICATION_5_ASSESSMENT_2}    ${403_error_message}

Navigation using previous button
    [Documentation]    INFUND-4264
    [Tags]
    Given the user navigates to the page               ${Assessor_application_dashboard}
    And The user clicks the button/link                link = Intelligent water system
    When the user clicks the button/link               link = 4. Economic benefit
    Then the user should see the element               jQuery = h1:contains("Economic benefit")
    And the user navigate to previous pages

Project details sections should not be scorable
    [Documentation]    INFUND-3400 INFUND-4264
    [Tags]
    Given the user clicks the button/link       link = Back to your assessment overview
    And Application detail section should not be scorable
    Then Project summary section should not be scorable
    And Public description section should not be scorable
    And Scope section should not be scorable

Application questions should be scorable
    [Documentation]    INFUND-3400 INFUND-4264
    [Tags]
    Given the user should see assessment question details
    [Teardown]  the user clicks the button/link   link = Back to your assessment overview

Appendix can be opened on the question view
    [Documentation]    INFUND-8065
    [Tags]
    Given The user opens the link in new window  intelligent-water-system-technical-approach.pdf, 8 KB
    And The user opens the link in new window    intelligent-water-system-innovation.pdf, 8 KB
    And The user opens the link in new window    intelligent-water-system-project-team.pdf, 8 KB
    When the user clicks the button/link         jQuery = a:contains("6. Innovation")
    And The user opens the link in new window    intelligent-water-system-innovation.pdf, 8 KB

Scope: Validations
    [Documentation]  IFS-508
    [Tags]
    Given the user clicks the button/link               link = Back to your assessment overview
    And the user clicks the button/link                 link = Scope
    When the user clicks the button/link                jQuery = button:contains("Save and return to assessment overview")
    Then the user should see a field and summary error  Please select a research category.
    And the user should see a field and summary error   Please select the scope.

Scope: Status in the overview is updated
    [Documentation]    INFUND-1483
    [Tags]
    When the user selects the index from the drop-down menu  1    css = .research-category
    And the user clicks the button/link                      jQuery = label:contains("Yes")
    And The user enters text to a text field                 css = .editor    Testing feedback field when "Yes" is selected.
    Then the user clicks the button/link                     jquery = button:contains("Save and return to assessment overview")
    And the user should see the element                      jQuery = li:nth-child(4) span:contains("In scope") ~ .task-status-complete

Scope: Autosave
    [Documentation]    INFUND-1483  INFUND-3780
    [Tags]
    Given the user clicks the button/link               link = Scope
    Then the user should see the element                jQuery = .govuk-select:contains("Feasibility studies")
    And the user should see the text in the element    css = .editor    Testing feedback field when "Yes" is selected.

Scope: Word count
    [Documentation]    INFUND-1483  INFUND-3400
    [Tags]
    Given the user enters multiple strings into a text field  css = .editor  a${SPACE}  100
    Then the user should see the element              jQuery = span:contains("Words remaining: 0")

Scope: Guidance
    [Documentation]    INFUND-4142  INFUND-6281
    [Tags]
    When the user clicks the button/link          css = details summary
    Then the user should see the element          css = div[id^="details-content-"]
    And The user should see the element           jQuery = td:contains("One or more of the above requirements have not been satisfied.")
    And The user should see the element           jQuery = td:contains("Does it meet the scope of the competition as defined in the competition brief?")
    When the user clicks the button/link           css = details summary
    Then The user should not see the element       css = div[id^="details-content-"]

Economic Benefit: validations
    [Documentation]  IFS-508
    [Tags]
    Given the user clicks the button/link               link = Back to your assessment overview
    And I open one of the application questions         link = 4. Economic benefit
    When the user clicks the button/link                jQuery = button:contains("Save and return to assessment overview")
    Then the user should see a field and summary error  The assessor score must be a number.

Economic Benefit: word count
    [Documentation]    INFUND-3859
    [Tags]
    [Setup]    The user clicks the button/link             link = Back to your assessment overview
    Given I open one of the application questions          link = 4. Economic benefit
    And I should see word count underneath feedback form   Words remaining: 100
    When I enter feedback of words                         102
    And the user clicks the button/link                    jQuery = button:contains("Save and return to assessment overview")
    Then the user should see a summary error               Maximum word count exceeded. Please reduce your word count to 100.
    When I enter feedback of words                         10
    Then I should see word count underneath feedback form  Words remaining: 90
    And the user should not see an error in the page

Economic Benefit: Autosave
    [Documentation]    INFUND-3780
    [Tags]
    When the user selects the option from the drop-down menu  9    css = .assessor-question-score
    And the user enters text to a text field                  css = .editor    This is to test the feedback entry.
    And the user clicks the button/link                       jQuery = a:contains("Back to your assessment overview")
    And the user clicks the button/link                       link = 4. Economic benefit
    Then the user should see the text in the element          css = .editor    This is to test the feedback entry.
    And the user should see the element                       jQuery = .govuk-select:contains("9")

Economic Benefit: Guidance
    [Documentation]    INFUND-6281
    Given The user clicks the button/link          css = .govuk-details__summary-text
    Then the user should see the guidance for assessing economic benefits
    [Teardown]  The user clicks the button/link    link = Back to your assessment overview

Finance overview
    [Documentation]    INFUND-3394  IFS-2854
    [Tags]  MySQL
    Given the user should see finance overview
    When the user sets the finance option to detailed   ${IN_ASSESSMENT_COMPETITION_NAME}
    And the user reloads the page
    Then the users should see detailed finance overview

Status of the application should be In Progress
    [Documentation]    INFUND-6358
    [Tags]
    Given The user navigates to the page           ${ASSESSOR_DASHBOARD_URL}
    When The user clicks the button/link           link = ${IN_ASSESSMENT_COMPETITION_NAME}
    Then The user should see the element           jQuery = .progress-list li:contains("Intelligent water system") strong:contains("In progress")

*** Keywords ***
I enter feedback of words
    [Arguments]    ${no_of_words}
    the user enters multiple strings into a text field  css = .editor  a${SPACE}  ${no_of_words}

I should see word count underneath feedback form
    [Arguments]    ${wordCount}
    the user should see the element    jQuery = span:contains("${wordCount}")

I open one of the application questions
    [Arguments]    ${application_question}
    the user clicks the button/link  ${application_question}

the user clicks previous and goes to the page
    [Arguments]    ${page_content}
    the user clicks the button/link           jQuery = span:contains("Previous")
    the user should see the element           jQuery = h1:contains("${page_content}")

the finance summary total should be correct
    Element Should Contain    css = .govuk-form-group.finances-summary tbody tr:nth-child(1) td:nth-child(2)    £200,903
    Element Should Contain    css = .govuk-form-group.finances-summary tbody tr:nth-child(1) td:nth-child(3)    30%
    Element Should Contain    css = .govuk-form-group.finances-summary tbody tr:nth-child(1) td:nth-child(4)    57,803
    Element Should Contain    css = .govuk-form-group.finances-summary tbody tr:nth-child(1) td:nth-child(5)    2,468
    Element Should Contain    css = .govuk-form-group.finances-summary tbody tr:nth-child(1) td:nth-child(6)    140,632
    Element Should Contain    css = .govuk-form-group.finances-summary tbody tr:nth-child(2) td:nth-child(2)    990
    Element Should Contain    css = .govuk-form-group.finances-summary tbody tr:nth-child(2) td:nth-child(4)    0
    Element Should Contain    css = .govuk-form-group.finances-summary tbody tr:nth-child(2) td:nth-child(5)    2,468
    Element Should Contain    css = .govuk-form-group.finances-summary tbody tr:nth-child(2) td:nth-child(6)    0

the project cost breakdown total should be correct
    Element Should Contain    css = .project-cost-breakdown tbody tr:nth-child(1) td:nth-child(2)    200,903
    Element Should Contain    css = .project-cost-breakdown tbody tr:nth-child(1) td:nth-child(3)    3,081
    Element Should Contain    css = .project-cost-breakdown tbody tr:nth-child(1) td:nth-child(4)    0
    Element Should Contain    css = .project-cost-breakdown tbody tr:nth-child(1) td:nth-child(5)    100,200
    Element Should Contain    css = .project-cost-breakdown tbody tr:nth-child(1) td:nth-child(6)    552
    Element Should Contain    css = .project-cost-breakdown tbody tr:nth-child(1) td:nth-child(7)    90,000
    Element Should Contain    css = .project-cost-breakdown tbody tr:nth-child(1) td:nth-child(8)    5,970
    Element Should Contain    css = .project-cost-breakdown tbody tr:nth-child(1) td:nth-child(9)    1,100

The status of the appllications should be correct
    [Arguments]    ${APPLICATION}    ${STATUS}
    element should contain    ${APPLICATION}    ${STATUS}

The user sets the finance option to detailed
    [Arguments]  ${competition}
    execute sql string   UPDATE `${database_name}`.`competition` SET `assessor_finance_view` = 'DETAILED' WHERE `name` = '${competition}';

The project costs are correct in the overview
    The user should see the element       jQuery = button:contains("Labour") span:contains("£3,081")
    The user should see the element       jQuery = button:contains("Overhead costs") span:contains("£0")
    The user should see the element       jQuery = button:contains("Materials") span:contains("£100,200")
    The user should see the element       jQuery = button:contains("Capital usage") span:contains("£552")
    The user should see the element       jQuery = button:contains("Subcontracting costs") span:contains("£90,000")
    The user should see the element       jQuery = button:contains("Travel and subsistence") span:contains("£5,970")
    The user should see the element       jQuery = button:contains("Other costs") span:contains("£1,100")

The academic finances are correct
    The user should see the element       jQuery = .table-overview td:contains("3 months")
    The user should see the element       jQuery = .table-overview td:contains("£990")
    The user should see the element       jQuery = .table-overview td:contains("100%")
    The user should see the element       jQuery = .table-overview td:contains("990")

Custom suite setup
    The user logs-in in new browser  &{assessor_credentials}
    Connect To Database   @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user fills in rejection details
    And the user should see the element                    id = rejectReason
    the user selects the option from the drop-down menu    Select a reason    id = rejectReason    # Note that using this empty option will actually select the 'Select a reason' option at the top of the dropdown menu
    the user clicks the button/link                        jquery = button:contains("Reject")
    the user should see a field error                      Please enter a reason.
    Select From List By Index                              id = rejectReason    1
    the user should not see an error in the page
    The user enters text to a text field                   id = rejectComment    Have conflicts with the area of expertise.

the uesr should see assessment overview details
    the user should see the element     jQuery = dt:contains("Application number")~ dd:contains("${IN_ASSESSMENT_APPLICATION_5_NUMBER}")
    And the user should see the element      jQuery = dt:contains("Competition") ~ dd:contains("${IN_ASSESSMENT_COMPETITION_NAME}")
    And The user should see the element      jQuery = h2:contains("Project details")
    And The user should see the element      jQuery = h2:contains("Application questions")
    And The user should see the element      jQuery = h2:contains("Finances")

the user navigate to previous pages
    the user clicks previous and goes to the page   Project exploitation
    the user clicks previous and goes to the page   Potential market
    the user clicks previous and goes to the page   Business opportunity
    the user clicks previous and goes to the page   Scope
    the user clicks previous and goes to the page   Public description
    the user clicks previous and goes to the page   Project summary
    the user clicks previous and goes to the page   Application details
    the user should not see the element             jQuery = span:contains("Previous")

Application detail section should not be scorable
    the user clicks the button/link        link = Application details
    the user should see the element        jQuery = h3:contains("Project title")
    the user should not see the element    jQuery = label:contains("Question score")
    the user clicks the button/link        jQuery = span:contains("Next")

Project summary section should not be scorable
    the user should see the element        jQuery = p:contains("This is the applicant response for project summary.")
    the user should not see the element    jQuery = label:contains("Question score")
    the user clicks the button/link        jQuery = span:contains("Next")

Public description section should not be scorable
    the user should see the element        jQuery = p:contains("This is the applicant response for public description.")
    the user should not see the element    jQuery = label:contains("Question score")
    the user clicks the button/link        jQuery = span:contains("Next")

Scope section should not be scorable
    the user should see the element        jQuery = p:contains("This is the applicant response for how does your project align with the scope of this competition?.")
    the user should not see the element   jQuery = label:contains("Question score")

the user should see question details
    [Arguments]  ${question}  ${description}
    the user clicks the button/link      jQuery = span:contains("Next")
    the user should see the element      jQuery = h2:contains("${question}")
    the user should see the element      jQuery = p:contains("${description}")
    The user should see the element      jQuery = label:contains("Question score")

the user should see assessment question details
    the user should see question details    What is the business opportunity that your project addresses?  This is the applicant response for what is the business opportunity that your project addresses?.
    the user should see question details    What is the size of the potential market for your project   This is the applicant response for what is the size of the potential market for your project?.
    the user should see question details    How will you exploit and market your project?   This is the applicant response for how will you exploit and market your project?.
    the user should see question details    What economic, social and environmental benefits do you expect your project to deliver and when   This is the applicant response for what economic, social and environmental benefits do you expect your project to deliver and when?.
    the user should see question details    What technical approach will you use and how will you manage your project?  This is the applicant response for what technical approach will you use and how will you manage your project?.
    the user should see question details    What is innovative about your project  This is the applicant response for what is innovative about your project?.
    the user should see question details    What are the risks  This is the applicant response for what are the risks (technical, commercial and environmental) to your project's success? what is your risk management strategy?.
    the user should see question details    Does your project team have the skills  This is the applicant response for does your project team have the skills, experience and facilities to deliver this project?.
    the user should see question details    What will your project cost  This is the applicant response for what will your project cost?.
    the user should see question details    How does financial support from Innovate UK  This is the applicant response for how does financial support from innovate uk and its funding partners add value?.

the user should see the guidance for assessing economic benefits
    the user should see the element     jQuery = td:contains("The project is damaging to other stakeholders with no realistic mitigation or balance described.")
    the user should see the element     jQuery = td:contains("The project has no outside benefits or is potentially damaging to other stakeholders. No mitigation or exploitation is suggested.")
    the user should see the element     jQuery = td:contains("Some positive outside benefits are described but the methods to exploit these are not obvious. Or the project is likely to have a negative impact but some mitigation or a balance against the internal benefits is proposed.")
    the user should see the element     jQuery = td:contains("Some positive outside benefits are defined and are realistic. Methods of addressing these opportunities are described.")
    the user should see the element     jQuery = td:contains("Inside and outside benefits are well defined, realistic and of significantly positive economic, environmental or social impact. Routes to exploit these benefits are also provided.")

the user should see finance overview
    the user clicks the button/link        link = Finances overview
    the user should see the element        jQuery = h2:contains("Finances summary")
    the finance summary total should be correct
    the project cost breakdown total should be correct

the users should see detailed finance overview
    The user clicks the button/link         jQuery = th:contains("Mo Juggling Mo Problems Ltd") a:contains("View finances")
    the user should see the element         jQuery = h2:contains("Detailed finances")
    the project costs are correct in the overview
    the user clicks the button/link         link = Back to funding
    the user clicks the button/link         jQuery = th:contains("University of Bath") a:contains("View finances")
    the academic finances are correct