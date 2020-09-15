*** Settings ***
Documentation    INFUND-5629 As a Competitions team member I want to be able to edit Application Questions individually in Competition Setup so that I can manage the question and associated applicant and assessor guidance in one place
...
...              INFUND-6468 Competition setup autosave should be validating types, allowing invalid data and doing a complete validate on mark as complete
...
...              IFS-7702  Configurable multiple choice questions - Comp setup
...
...              IFS-7700 EDI application question configuration
...
Suite Setup      Custom suite setup
Force Tags       CompAdmin
Resource         ../../resources/defaultResources.robot
Resource         ../../resources/common/Competition_Commons.robot

*** Variables ***
${questionSubTitleInfo}     We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.
${surveyMonkeyUrl}          https://www.surveymonkey.co.uk/r/ifsaccount
${ediQuestion}              Have you completed the EDI survey?
${ediQuestionTitle}         Equality, diversity and inclusion
*** Test Cases ***
Business opportunity Server-side validations setup questions
    [Documentation]    INFUND-5629 INFUND-5685
    [Tags]
    Given The user clicks the button/link  link = Application
    And The user clicks the button/link    jQuery = a:contains("Business opportunity")
    When the user leaves all the question field empty
    And The user clicks the button/link    jQuery = button:contains("Done")
    Then the user should see the element   jQuery = .govuk-label:contains("Question heading") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element    jQuery = .govuk-label:contains("Question title") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element    jQuery = .govuk-label:contains("Question guidance title") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element    jQuery = .govuk-label:contains("Max word count") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element    jQuery = .govuk-fieldset__legend:contains("Accepted appendix file type") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element    jQuery = .govuk-label:contains("Appendix guidance") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element    jQuery = .govuk-label:contains("Template title") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element    jQuery = .govuk-fieldset__legend:contains("Accepted upload file types") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see a summary error    ${empty_field_warning_message}

Error message validation when answer type selected as multiple choice
    [Documentation]  IFS-7702
    Given the user selects the radio button       typeOfQuestion   MULTIPLE_CHOICE
    When the user enters text to a text field     id = question.choices[0].text     ${EMPTY}
    The user should see the element               jQuery = table:contains("Answers") .govuk-error-message:contains("${empty_field_warning_message}")

Duplicate error message validation when same answers added to multiple choice answers type
    [Documentation]  IFS-7702
    Given the user enters text to a text field             id = question.choices[0].text     Duplicate
    And the user enters text to a text field               id = question.choices[1].text     Duplicate
    When The user clicks the button/link                   jQuery = button:contains("Done")
    Then the user should see a field and summary error     Enter a different answer.
    [Teardown]   the user selects the radio button         typeOfQuestion   FREE_TEXT

Business opportunity Sever-side validations assessment questions
    [Documentation]    INFUND-5685
    [Tags]
    Given the user leaves all the assessment questions empty
    When the user clicks the button/link    jQuery = button:contains("Done")
    Then the user should see the element    jQuery = .govuk-label[for="guidanceRows[0].scoreFrom"] ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element     jQuery = .govuk-label[for="guidanceRows[0].scoreTo"] ~ .govuk-error-message:contains("${empty_field_warning_message}")
    And the user should see the element     jQuery = .govuk-label[for="guidanceRows[0].justification"] ~ .govuk-error-message:contains("${empty_field_warning_message}")

Business opportunity: Client side validations
    [Documentation]    INFUND-5629 INFUND-5685
    [Tags]
    Given the user fills the empty question fields
    And the user enters text to a text field                            id = question.shortTitle    Test Heading
    And the user selects the radio button                               numberOfUploads  0
    And the user selects the radio button                               question.templateDocument  0
    And the user fills the empty assessment fields
    Then the validation error above the question should not be visible  css = label[for="question.shortTitle"]            ${empty_field_warning_message}
    And the validation error above the question should not be visible   css = label[for="question.title"]                 ${empty_field_warning_message}
    And the validation error above the question should not be visible   css = label[for="question.guidanceTitle"]         ${empty_field_warning_message}
    And the validation error above the question should not be visible   css = label[for="question.maxWords"]              ${empty_field_warning_message}
    And the validation error above the question should not be visible   id = question.allowedAppendixResponseFileTypes    ${empty_field_warning_message}
    And the validation error above the question should not be visible   css = label[for="guidanceRows[0].scoreFrom"]      ${empty_field_warning_message}
    And the validation error above the question should not be visible   css = label[for="guidanceRows[0].scoreTo"]        ${empty_field_warning_message}
    And the validation error above the question should not be visible   css = label[for="guidanceRows[0].justification"]  ${empty_field_warning_message}

Test Heading: Mark as done
    [Documentation]    INFUND-5629
    [Tags]
    When The user clicks the button/link         jQuery = button:contains("Done")
    And the user clicks the button/link          jQuery = a:contains("Test Heading")
    Then the user should see the element         jQuery = h1:contains("Test Heading")
    And the user should see the element          jQuery = dt:contains("Question title") + dd:contains("Test title")
    And the user should see the element          jQuery = dt:contains("Max word count") + dd:contains("150")
    [Teardown]  the user clicks the button/link  link = Application

Equality, diversity and inclusion should display default content
    [Documentation]    IFS-7700
    When The user clicks the button/link                      jQuery = a:contains("Equality, diversity and inclusion")
    Then the user should see EDI question default content

Equality, diversity and inclusion: validations
    [Documentation]    IFS-7700
    [Tags]
    Given the user clears predefined text in EDI question
    And The user clicks the button/link                        jQuery = button:contains("Done")
    Then the user should see the field validation messages
    And the user should see the summary validation messages

Equality, diversity and inclusion can be removed from application section
    [Documentation]    IFS-7700
    Given the user clicks the button/link        link = Application
    When the user clicks the button/link         name = deleteQuestion
    Then the user should not see the element     jQuery = a:contains("Equality, diversity and inclusion")

Scope: Sever-side validations assessment questions
    [Documentation]    INFUND-6444
    [Tags]
    Given the user clicks the button/link               link = Scope
    When the user clicks the button/link                jQuery = Button:contains("+Add guidance row")
    And the user clicks the button/link                 jQuery = button:contains("Done")
    Then the user should see a field and summary error  ${empty_field_warning_message}
    And The user clicks the button/link                 id = remove-guidance-row-2
    And the user clicks the button/link                 jQuery = button:contains("Done")
    And the user cannot see a validation error in the page

*** Keywords ***
Custom Suite setup
    The user logs-in in new browser    &{Comp_admin1_credentials}
    ${nextYear} =  get next year
    Set suite variable  ${nextYear}
    User creates a new competition for Application tests

the user leaves all the question field empty
    Clear Element Text    css = .editor
    Press Key    css = .editor    \\8
    Set Focus To Element      jQuery = button:contains("Done")
    The user enters text to a text field    id = question.shortTitle     ${EMPTY}
    The user enters text to a text field    id = question.title          ${EMPTY}
    The user enters text to a text field    id = question.guidanceTitle  ${EMPTY}
    The user enters text to a text field    id = question.maxWords       ${EMPTY}
    the user selects the radio button       numberOfUploads  1
    the user clicks the button/link         css = label[for="question.allowedAppendixResponseFileTypes1"]
    the user selects the radio button       question.templateDocument  1

The user leaves all the assessment questions empty
    The user enters text to a text field    id = guidanceRows[0].scoreFrom      ${EMPTY}
    The user enters text to a text field    id = guidanceRows[0].scoreTo        ${EMPTY}
    the user enters text to a text field    id = guidanceRows[0].justification  ${EMPTY}

the validation error above the question should be visible
    [Arguments]    ${QUESTION}    ${ERROR}
    Element Should Contain    ${QUESTION}    ${ERROR}

the validation error above the question should not be visible
    [Arguments]    ${QUESTION}    ${ERROR}
    Set Focus To Element      jQuery = button:contains("Done")
    Wait Until Element Is Not Visible Without Screenshots    css = .govuk-error-message
    Element Should not Contain    ${QUESTION}    ${ERROR}

the user should see the correct inputs in the Applications questions form
    ${input_value} =    Get Value    id = question.shortTitle
    Should Be Equal    ${input_value}    Test Heading
    ${input_value} =    Get Value    id = question.title
    Should Be Equal    ${input_value}    Test title
    ${input_value} =    Get Value    jQuery = label:contains("Question subtitle") + div .editor
    Should Be Equal    ${input_value}    Subtitle test
    ${input_value} =    Get Value    id = question.guidanceTitle
    Should Be Equal    ${input_value}    Test guidance title
    ${input_value} =    Get Value    jQuery = label:contains("Question guidance") + div .editor
    Should Be Equal    ${input_value}    Guidance text test
    ${input_value} =    Get Value    id = question.maxWords
    Should Be Equal    ${input_value}    150

The user should see the correct inputs in assessment questions
    ${input_value} =    Get Value    id = guidanceRows[0].scoreFrom
    Should Be Equal    ${input_value}    30
    ${input_value} =    Get Value    id = guidanceRows[0].scoreTo
    Should Be Equal    ${input_value}    35
    ${input_value} =    Get Value    id = guidanceRows[0].justification
    Should Be Equal    ${input_value}    This is a justification

User creates a new competition for Application tests
    Given the user navigates to the page        ${CA_UpcomingComp}
    When the user clicks the button/link        jQuery = .govuk-button:contains("Create competition")
    And the user clicks the button/link         link = Initial details
    And the user enters text to a text field    id = title    Test competition
    And the user selects the radio button       fundingType  GRANT
    And the user selects the option from the drop-down menu    Programme    id = competitionTypeId
    And the user selects the option from the drop-down menu    Health and life sciences    id = innovationSectorCategoryId
    And the user selects the option from the drop-down menu    Advanced therapies    name = innovationAreaCategoryIds[0]
    And the user enters text to a text field    id = openingDateDay    01
    And the user enters text to a text field    id = openingDateMonth    12
    And the user enters text to a text field    id = openingDateYear  ${nextYear}
    And the user selects the option from the drop-down menu    Ian Cooper    id = innovationLeadUserId
    And the user selects the option from the drop-down menu    John Doe    id = executiveUserId
    And the user clicks the button twice        css = label[for="stateAid2"]
    And the user clicks the button/link         jQuery = button:contains("Done")
    And the user clicks the button/link         link = Competition details

the user clears predefined text in EDI question
    The user enters text to a text field    id = question.shortTitle        ${EMPTY}
    The user enters text to a text field    id = question.title             ${EMPTY}
    The user enters text to a text field    id = question.guidanceTitle     ${EMPTY}
    The user enters text to a text field    id = question.choices[0].text   ${EMPTY}
    The user enters text to a text field    id = question.choices[1].text   ${EMPTY}

the user should see the field validation messages
    the user should see the element     jQuery = .govuk-label:contains("Question heading") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    the user should see the element     jQuery = .govuk-label:contains("Question title") ~ .govuk-error-message:contains("${empty_field_warning_message}")
    the user should see the element     jQuery = table:contains("Answers") .govuk-error-message:contains("${empty_field_warning_message}")

the user should see the summary validation messages
    the user should see the element     css = [href='#question.shortTitle']
    the user should see the element     css = [href='#question.title']
    the user should see the element     css = [href='#question.choices[0].text']
    the user should see the element     css = [href='#question.choices[1].text']

the user should see EDI question default content
    the user should see the element     css = [value="${ediQuestionTitle}"]
    the user should see the element     css = [value="${ediQuestion}"]
    the user should see the element     css = [href="${surveyMonkeyUrl}"]
    the user should see the element     jQuery = p:contains("${questionSubTitleInfo}")
    the user should see the element     css = [value="Yes"]
    the user should see the element     css = [value="No"]

