*** Settings ***
Documentation     INFUND-5182 As an assessor creating an account I need to supply details of my skills and expertise so that InnovateUK can assign me appropriate applications to assess.
...
...               INFUND-5432 As an assessor I want to receive an alert to complete my profile when I log into my dashboard so that I can ensure that it is complete.
...
...               INFUND-7059 As an assessor I can view my skills page so I can decide if my skills need updating
...
...               IFS-3942 Assessor profile view - Assessor
Suite Setup       The user logs-in in new browser  &{existing_assessor1_credentials}
Suite Teardown    The user closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Client-side validations
    [Documentation]  INFUND-5182  INFUND-5432
    Given The user should see the element    jQuery = .message-alert a:contains("your skills")    #this checks the alert message on the top od the page
    When the user clicks the button/link     jQuery = a:contains("your skills")
    Then the user checks for client side validations

Cancel button redirects to the read-only view without changes
    [Documentation]    INFUND-8009  IFS-3942
    [Tags]
    Given the user clicks the button/link                     jQuery = a:contains("Cancel")
    Then the user should be redirected to the correct page    ${assessment_skills_url}
    [Teardown]    the user clicks the button/link             id = editSkills

Back button from edit page redirects to read only view
    [Documentation]    INFUND-8009  IFS-3942
    [Tags]
    Given the user clicks the button/link                     link = Your skills
    Then the user should be redirected to the correct page    ${assessment_skills_url}
    [Teardown]    the user clicks the button/link             id = editSkills

Server-side validations
    [Documentation]    INFUND-5182
    [Tags]
    Given the user clicks the button/link             jQuery = label:contains("Business")
    Then the user checks for serve side validations

Save Skills should redirect to the read-only view
    [Documentation]    INFUND-5182  IFS-3942  INFUND-5432  INFUND-7059
    [Tags]
    Given the user enter and save the 'Your skills' details
    When the user should be redirected to the correct page    ${assessment_skills_url}
    Then the user should see the correct details

Your skills does not appear in dashboard alert
    [Documentation]    INFUND-5182
    [Tags]
    When the user clicks the button/link            link = ${ASSESSOR_DASHBOARD_TITLE}
    Then The user should not see the element        jQuery = .message-alert a:contains('your skills')    #this checks the alert message on the top of the page
    [Teardown]    the user clicks the button/link   link = your details

Return to assessor dashboard from skills page
    [Documentation]    INFUND-8009
    [Tags]
    When the user clicks the button/link    jQuery = a:contains("Return to assessments")
    Then the user should be redirected to the correct page     ${ASSESSOR_DASHBOARD_URL}

*** Keywords ***
The correct radio button should be selected
    radio button should be set to    assessorType    BUSINESS

the user checks for client side validations
    the user should see the element                       jQuery = h2:contains("Innovation areas")
    the user enters multiple strings into a text field    id = skillAreas    w${SPACE}    101
    the user clicks the button/link                       jQuery = button:contains("Save and return to your skills")
    the user should see a field and summary error         Please select an assessor type.
    the user should see a field and summary error         Maximum word count exceeded. Please reduce your word count to 100.

the user checks for serve side validations
    the user enters multiple strings into a text field    id = skillAreas    w${SPACE}    102
    the user clicks the button/link                       jQuery = button:contains("Save and return to your skills")
    the user should see a field and summary error         Maximum word count exceeded. Please reduce your word count to 100.
    browser validations have been disabled
    the user enters multiple strings into a text field    id = skillAreas    e    5001
    the user clicks the button/link                       jQuery = button:contains("Save and return to your skills")
    the user should see a field and summary error         This field cannot contain more than 5,000 characters.

the user enter and save the 'Your skills' details
    the user clicks the button/link         jQuery = label:contains("Business")
    the user enters text to a text field    id = skillAreas    assessor skill areas text
    the user clicks the button/link         jQuery = button:contains("Save and return to your skills")

the user should see the correct details
    the user should see the element          jQuery = h3:contains("Skill areas")~ p:contains("assessor skill areas text")
    the user should see the element          jQuery = dt:contains("Assessor type") ~ dd:contains("Business")
    the user should see the element          jQuery = td:contains("Materials, process and manufacturing design technologies")