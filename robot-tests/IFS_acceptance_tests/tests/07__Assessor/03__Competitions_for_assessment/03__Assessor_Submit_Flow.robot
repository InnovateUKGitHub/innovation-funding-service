*** Settings ***
Documentation     INFUND-550 As an assessor I want the ‘Assessment summary’ page to show me complete and incomplete sections, so that I can easily judge how much of the application is left to do
...
...               INFUND-1485 As an Assessor I want to be able to provide my final feedback for the application so that I can tell Innovate UK whether or not I recommend the application for funding.
...
...               INFUND-4217 Assessor journey form validation
...
...               INFUND-3720 As an Assessor I can see deadlines for the assessment of applications currently in assessment on my dashboard, so that I am reminded to deliver my work on time
...
...               INFUND-5179 Introduce new resource DTO classes for recommending and rejecting assessments
...
...               INFUND-5765 As an assessor I need to be able to progress an assessment to the state of Assessed so that I am able to select it to submit
...
...               INFUND-5712 As an Assessor I can review the recommended for funding status of applications that I have assessed so that I can track my work
...
...               INFUND-3726 As an Assessor I can select one or more assessments to submit so that I can work in my preferred way
...
...               INFUND-3724 As an Assessor and I am looking at my competition assessment dashboard, I can review the status of applications that I am allocated so that I can track my work
...
...               INFUND-5739 As an Assessor I can submit all the applications that I have selected so that my assessment work is completed
...
...               INFUND-3743 As an Assessor I want to see all the assessments that I have already submitted in this competition so that I can see what I have done already.
...
...               INFUND-3719 As an Assessor and I have accepted applications to assess within a competition, I can see progress on my dashboard so I can keep track of my work
Suite Setup       The user logs-in in new browser  &{assessor2_credentials}
Suite Teardown    the user closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot
Resource          ../../07__Assessor/Assessor_Commons.robot

*** Test Cases ***
Summary:All the sections are present
    [Documentation]    INFUND-4648
    [Tags]  HappyPath
    Given The user clicks the button/link                    link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And the user should see that the element is disabled    id = submit-assessment-button
    When the user clicks the button/link                     link = Intelligent Building
    And the user clicks the button/link                     jQuery = .govuk-button:contains("Review and complete your assessment")
    Then the user should see the element                    jQuery = h2:contains("Review assessment")
    And the user should see the element                     jQuery = legend:contains("Do you believe that this application is suitable for funding?")
    And the user should see the element                     id = form-input-feedback
    And the user should see the element                     id = form-input-comments

Summary:Number of days remaining until assessment submission
    [Documentation]    INFUND-3720
    [Tags]
    Given The user should see the element    jQuery = .deadline:contains("days left to submit")
    # And the days remaining should be correct (Top of the page)  ${getSimpleMilestoneDate(${IN_ASSESSMENT_COMPETITION}, "ASSESSOR_DEADLINE")}
    # TODO IFS-3176

Summary shows questions as incomplete
    [Documentation]    INFUND-550
    [Tags]  HappyPath
    Given the user should see the text in the element    jQuery = button:contains("Scope")    Incomplete
    :FOR  ${ELEMENT}    IN   @{programme_questions}
     \    the user should see the text in the element   jQuery = button:contains("${ELEMENT}")    Incomplete

Summary: Questions should show without score
    [Documentation]    INFUND-550
    [Tags]  HappyPath
     :FOR  ${ELEMENT}    IN   @{programme_questions}
      \   the user should see the text in the element    jQuery = button:contains("${ELEMENT}")    N/A
    [Teardown]    The user clicks the button/link        link = Back to your assessment overview

Summary:Questions should show as complete
    [Documentation]    INFUND-550
    [Tags]  HappyPath
    [Setup]    Go to    ${SERVER}/assessment/assessor/dashboard/competition/${IN_ASSESSMENT_COMPETITION}
    Given The user clicks the button/link                           link = Intelligent Building
    And the assessor adds score and feedback for every question     11   # 11 is the number of questions to iterate through
    When the user clicks the button/link                            link = Review and complete your assessment
    Then the user should see the text in the element                jQuery = button:contains("Scope")    Complete
    :FOR  ${ELEMENT}    IN   @{programme_questions}
     \    the user should see the text in the element               jQuery = button:contains("${ELEMENT}")    Complete

Summary:Questions should show the scores
    [Documentation]    INFUND-550
    [Tags]  HappyPath
    Then The user should see the element        jQuery = p strong:contains("Total: 100/100")
    And The user should see the element         jQuery = p strong:contains("100%")
    :FOR  ${ELEMENT}    IN   @{programme_questions}
     \    the user should see the text in the element    jQuery = button:contains("${ELEMENT}")    Score 10/10

Summary:Feedback should show in each section
    [Documentation]    INFUND-550
    [Tags]  HappyPath
    When the user clicks the button/link              jQuery = button:contains("Scope")
    Then the user should see the element              jQuery = p:contains("Testing scope feedback text")
    :FOR  ${ELEMENT}    IN   @{programme_questions}
     \    the user clicks the button/link             jQuery = button:contains("${ELEMENT}")
     \    the user should see the element             jQuery = p:contains("Testing feedback text")

Summary:Assessor can return to each question
    [Documentation]    INFUND-4648
    :FOR  ${INDEX}  IN RANGE  1  11    # 11 is the number of assessed questions to iterate through
     \    the user should see the element            jQuery = #accordion-questions-content-${INDEX} a:contains("Return to this question in the application")
    When the user clicks the button/link             jQuery = #accordion-questions-content-2 a:contains("Return to this question in the application")
    Then the user should see the element             jQuery = h2:contains("What is the business opportunity that your project addresses?")
    And the user goes back to the previous page
    When the user clicks the button/link             jQuery = #accordion-questions-content-11 a:contains("Return to this question in the application")
    Then the user should see the element             jQuery = h2:contains("How does financial support from Innovate UK and its funding partners add value?")
    And the user goes back to the previous page

Summary:Assessor should be able to re-edit before submit
    [Documentation]    INFUND-3400
    [Tags]  HappyPath
    Given The user clicks the button/link                       jQuery = #accordion-questions-content-2 a:contains("Return to this question")
    And The user should see the element                         jQuery = h2:contains("What is the business opportunity that your project addresses?")
    Then the user edit the score and description for an assessed question

Summary:Funding Decision Validations
    [Documentation]    INFUND-1485  INFUND-4217  INFUND-5228
    [Tags]
    When The user clicks the button/link                  jQuery = .govuk-button:contains("Save assessment")
    And the user should see a field and summary error     Please indicate your decision.
    And The user enters text to a text field              id = feedback    ${EMPTY}
    And The user enters text to a text field              id = comment    ${EMPTY}
    Then the user selects the radio button                fundingConfirmation    false
    And The user clicks the button/link                   jQuery = .govuk-button:contains("Save assessment")
    Then the user should see a field and summary error    Please enter your feedback.

Summary:Word count check(Your feedback)
    [Documentation]    INFUND-1485  INFUND-4217  INFUND-5178  INFUND-5179
    [Tags]
    [Setup]    browser validations have been disabled
    Given the user enters multiple strings into a text field     id = feedback  t  5001
    When the user clicks the button/link                         jQuery = .govuk-button:contains("Save assessment")
    Then the user should see a field and summary error           This field cannot contain more than 5,000 characters.
    And the user enter text more than maximum word count limit   feedback
    And the user enter correct word count                        feedback

Summary:Word count check(Comments for InnovateUK)
    [Documentation]    INFUND-1485  INFUND-4217  INFUND-5178  INFUND-5179
    Given the user enter text more than maximum word count limit  comment
    And the user enter correct word count                        comment

User Saves the Assessment as Recommended
    [Documentation]    INFUND-4996  INFUND-5765  INFUND-3726  INFUND-6040  INFUND-3724
    [Tags]  HappyPath
    Given the user enters text to a text field               id = feedback  ${EMPTY}
    And the user selects the radio button                    fundingConfirmation    true
    When The user clicks the button/link                     jQuery = .govuk-button:contains("Save assessment")
    Then the user should see correct details

User Saves the Assessment as Not Recommended
    [Documentation]    INFUND-5712  INFUND-3726  INFUND-6040  INFUND-3724
    [Tags]  HappyPath
    Given The user clicks the button/link                    link = Park living
    And the assessor adds score and feedback for every question  11  # value 11: is the number of questions to loop through to submit feedback
    And the user clicks the button/link                      jQuery = .govuk-button:contains("Review and complete your assessment")
    When the user selects the radio button                   fundingConfirmation    false
    And the user enters text to a text field                 id = feedback    Negative feedback
    And The user clicks the button/link                      jQuery = .govuk-button:contains("Save assessment")
    Then the user should see assessment as not recommended details

Submit Assessments
    [Documentation]    INFUND-5739  INFUND-3743  INFUND-6358
    [Tags]  HappyPath
    Given the user should see the element          jQuery = .in-progress li:nth-child(7):contains("Intelligent Building")
    And the user should see that the element is disabled    id = submit-assessment-button
    When the user submits the assessment
    Then the user should see correct details after assessment submitted

Progress of the applications in Dashboard
    [Documentation]    INFUND-3719, INFUND-9007
    [Tags]
    ${ACCEPTED_LIST} =     Get Webelements         jQuery = .my-applications .in-progress li:not(:contains("Pending"))
    ${EXPECTED_TOTAL_ACCEPTED} =     Get Length    ${ACCEPTED_LIST}
    ${PENDING_LIST} =     Get Webelements          jQuery = .my-applications .in-progress li:contains("Pending")
    ${EXPECTED_TOTAL_PENDING} =     Get Length     ${PENDING_LIST}
    When The user navigates to the page            ${ASSESSOR_DASHBOARD_URL}
    Then the progress of the applications should be correct    ${EXPECTED_TOTAL_ACCEPTED}    ${EXPECTED_TOTAL_PENDING}
    And the user should see the element             jQuery = h3:contains("Sustainable living models for the future") ~ div:contains("${EXPECTED_TOTAL_PENDING} applications awaiting acceptance | ${EXPECTED_TOTAL_ACCEPTED} applications to assess")

*** Keywords ***
the word count should be correct
    [Arguments]    ${wordCount}
    the user should see the element     jQuery = span:contains("${wordCount}")

The user accepts the juggling is word that sound funny application
    The user clicks the button/link             link = ${IN_ASSESSMENT_COMPETITION_NAME}
    The user clicks the button/link             jQuery = a:contains("Accept or reject")
    The user should see the element             jQUery = h1:contains("Accept application")
    And the user selects the radio button       assessmentAccept  true
    And The user clicks the button/link         jQuery = button:contains("Confirm")
    The user should be redirected to the correct page    ${Assessor_application_dashboard}

the status of the status of the application should be correct
    [Arguments]    ${ELEMENT}    ${STATUS}
    Element should contain    ${ELEMENT}    ${STATUS}

the application should have the correct status
    [Arguments]    ${APPLICATION}    ${STATUS}
    element should contain    ${APPLICATION}    ${STATUS}

the progress of the applications should be correct
    [Arguments]    ${EXPECTED_TOTAL_ACCEPTED}    ${EXPECTED_TOTAL_PENDING}
    ${TOTAL_PENDING} =     Get text    css = .action-required .pending-applications    #gets the pending apps
    Should Be Equal As Integers    ${TOTAL_PENDING}    ${EXPECTED_TOTAL_PENDING}
    ${TOTAL_ACCEPTED} =     Get text    css = .action-required .accepted-applications    #gets the total number
    Should Be Equal As Integers    ${TOTAL_ACCEPTED}    ${EXPECTED_TOTAL_ACCEPTED}

the user edit the score and description for an assessed question
    the user selects the option from the drop-down menu    8    css = .assessor-question-score
    the user enters text to a text field                   css = .editor    This is a new feedback entry.
    the user clicks the button/link                        jQuery = a:contains("Back to your assessment overview")
    the user clicks the button/link                        jQuery = a:contains("Review and complete your assessment")
    the user should see the element                        jQuery = p:contains("This is a new feedback entry.")
    the user should see the element                        jQuery = h2 button:contains("1. Business opportunity") .section-score:contains("8")

the user enter correct word count
    [Arguments]  ${element}
    the user enters text to a text field            id = ${element}    Testing the feedback word count.
    Then the user should not see the element        jQuery = .govuk-error-message:contains("Maximum word count exceeded. Please reduce your word count to 100.")
    And the word count should be correct            Words remaining: 95

the user enter text more than maximum word count limit
    [Arguments]  ${element}
    the user enters multiple strings into a text field     id = ${element}  w${SPACE}  102
    the user clicks the button/link                        jQuery = .govuk-button:contains("Save assessment")
    the user should see a field and summary error          Maximum word count exceeded. Please reduce your word count to 100.
    the word count should be correct                       Words remaining: -2

the user should see correct details
    the user should not see the element                 jQuery = .govuk-error-message:contains("Please enter your feedback")
    the user should see the element                     jQuery = .status-msg:contains("Assessed")
    the user should see the element                     css = li:nth-child(7) .positive
    the user should see the element                     css = li:nth-child(7) input[type = "checkbox"] ~ label
    the application should have the correct status      css = .progress-list li:nth-child(7)    Assessed

the user should see assessment as not recommended details
    The user should see the element                      css = li:nth-child(6) .negative
    the user should see the element                      css = li:nth-child(6) input[type = "checkbox"] ~ label
    the application should have the correct status       css = .progress-list li:nth-child(6)    Assessed
    the application should have the correct status       css = .progress-list li:nth-child(7)    Assessed

the user should see correct details after assessment submitted
    the application should have the correct status    css = div.submitted    Submitted assessment
    the user should see the element                   css = li:nth-child(6) input[type = "checkbox"] ~ label    #This keyword verifies that only one applications has been submitted
    the user should see the element                   jQuery = h4:contains("Intelligent Building")
    the user should see the element                   jQuery = strong:contains("98")
    the user should not see the element               link = Intelligent Building

the user submits the assessment
    the user clicks the button/link            css = .in-progress li:nth-child(7) input[type = "checkbox"] ~ label
    the user clicks the button/link            jQuery = button:contains("Submit assessments")
    the user clicks the button/link            jQuery = button:contains("Cancel")
    the user clicks the button/link            jQuery = button:contains("Submit assessments")
    the user clicks the button/link            jQuery = button:contains("Yes I want to submit the assessments")