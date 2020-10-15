*** Settings ***
Documentation    
...
...     IFS-8409 Co funder - application response & edit 
...

Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${supporter01_email}                  mister.branches@money.com
${supporter02_email}                  horrace.horse@anarchy.com
&{Supporter01_credentials}            email=${supporter01_email}  password=${short_password}
&{Supporter02_credentials}            email=${supporter01_email}  password=${short_password}
#&{KTP_Application_Link}               
${KTP_Application_URL}                ${SERVER}/assessment/cofunder/application/247/response

*** Test Cases ***

The user sees the validation when responding to the Cofunder/Supprter review
    [Documentation]   IFS-8409
    Given the guest user inserts user email and password        ${supporter01_email}  ${short_password}
    And the guest user clicks the log-in button
    And the user navigates to the page                          ${KTP_Application_URL} 
    Then the user selects the radio button                      decision  decision-no
    And the user clicks the button/link                         jQuery = button:contains("Save review and return to applications")
    Then the user should see a field and summary error          Please provide some feedback.
    And the user selects the radio button                       decision  decision-yes
    Then the user clicks the button/link                        jQuery = button:contains("Save review and return to applications")
    And the user should see a field and summary error           Please provide some feedback.
    Then the user enters multiple strings into a text field     css = .editor  a${SPACE}  252
    And the user clicks the button/link                         jQuery = button:contains("Save review and return to applications")
    Then the user should see a field error                      Maximum word count exceeded. Please reduce your word count to 250.

The user responds to the Cofunder/Supprter review No
    [Documentation]   IFS-8409
    Given the user selects the radio button           decision  decision-no
    When the user enters text to a text field         css = .editor  This is the comments from the supporter
    Then the user clicks the button/link              jQuery = button:contains("Save review and return to applications")
    And the user navigates to the page                ${KTP_Application_URL}
    Then the user should see the element              jQuery = p:contains("This is the comments from the supporter")

The user responds to the Cofunder/Supprter review Yes
    [Documentation]   IFS-8409
    Given the user navigates to the page         ${KTP_Application_URL} 
    When the user clicks the button/link         jQuery = button:contains("Edit")
    Then the user selects the radio button       decision  decision-yes
    And the user enters text to a text field     css = .editor  This is the comments from the supporter
    Then the user clicks the button/link         jQuery = button:contains("Save review and return to applications")


*** Keywords ***
Custom suite setup
    The guest user opens the browser

Custom suite teardown
    The user closes the browser
