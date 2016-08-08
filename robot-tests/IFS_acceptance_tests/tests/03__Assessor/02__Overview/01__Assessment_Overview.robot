*** Settings ***
Documentation     INFUND-3303: As an Assessor I want the ability to reject the application after I have been given access to the full details so I can make Innovate UK aware.
Suite Setup
Suite Teardown    the user closes the browser
Force Tags        Pending
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Number of sections in the Assessment overview
    [Documentation]    INFUND-3400
    [Tags]
    [Setup]    guest user log-in    paul.plum@gmail.com    Passw0rd
    When the user navigates to the page     ${Assessment_overview_9}
    Then the user should see four sections
    #TODO Same number of questions and answers are present


Non-scorable question cannot be scored/edited
    [Documentation]    INFUND-3400
    [Tags]
    When the user clicks the button/link    link=Application details
    Then The user should not see the element    jquery=button:contains("Save and return to assessment overview")
    And the user clicks the button/link    link=Back to assessment overview
    Then The user should be redirected to the correct page    ${Assessment_overview_9}
    And the user clicks the button/link    link=Project summary
    Then The user should not see the element    jquery=button:contains("Save and return to assessment overview")
    And the user clicks the button/link    link=Back to assessment overview
    Then The user should be redirected to the correct page    ${Assessment_overview_9}
    Then the user clicks the button/link    link=Public description
    And The user should not see the element    jquery=button:contains("Save and return to assessment overview")
    And the user clicks the button/link    link=Back to assessment overview
    And The user should be redirected to the correct page    ${Assessment_overview_9}

Finance summary
    [Documentation]    INFUND-3394
    [Tags]
    Given the user navigates to the page     ${Assessment_overview_9}
    When the user clicks the button/link    link=Finances overview
    Then The user is on the page    ${Finance_summar_9_url}
    And The user should see the text in the page    Funding
    And the user should see the text in the page    Project cost breakdown
    And the user should not see the element    link=your finances
    Then the finance summary total should be correct
    And the project cost breakdown total should be correct
    And the user clicks the button/link    link=Back to assessment overview
    And The user should be redirected to the correct page    ${Assessment_overview_9}

Unable to assess this application
    [Documentation]    INFUND-3540
    [Tags]
    [Setup]    guest user log-in    felix.wilson@gmail.com    Passw0rd
    # Here Assessor-Felix rejects the application 9 and paul is able to assess the application.
    When the user navigates to the page     ${Assessment_overview_9}
    Then The user should see the element    css=#content .extra-margin details summary
    And the user clicks the button/link     css=#content .extra-margin details summary
    Then The user should see the element    css=#details-content-0 button
    And the user clicks the button/link     css=#details-content-0 button
    And the user fills in rejection details
    Then the user clicks the button/link    jQuery=button:contains("X")
    And the user clicks the button/link     css=#details-content-0 button
    Then the user fills in rejection details
    And the user clicks the button/link    jquery=button:contains("Reject")
   # Then The user should be redirected to the correct page    [TODO add in assessor dashboard url which is not implemented yet]


Validation check in the Reject application modal
    [Documentation]    INFUND-3540
    [Tags]    Pending
    # TODO or pending due to INFUND-3811
    Given the user navigates to the page     ${Assessment_overview_9}
    And the user clicks the button/link     css=#content .extra-margin details summary
    And the user clicks the button/link     css=#details-content-0 button
    When the user clicks the button/link    jquery=button:contains("Reject")
    Then the user should see an error    This field cannot be left blank
    And the user should see the element    id=rejectReason
    Then Select From List By value    id=rejectReason    Please select one reason
    And the user should see an error    This field cannot be left blank
    Then the user enters text to a text field    id=rejectComment    Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium libero. Cras id dui. Aenean ut
    [Teardown]

*** Keywords ***
the user should see four sections
    the user should see the element    css=#section-16 .bold-medium
    the user should see the element    css=#section-71 .heading-medium
    the user should see the element    css=#section-17 .heading-medium
  #  the user should see the element    css=#content .heading-medium

the Assessor edits the feedback
  #  Clear Element Text    css=#form-input-195 .isModified
    Press Key    css=#form-input-195 .isModified    \\8
    Wait Until Element Contains    css=#form-input-195 .textarea-footer span    100
    Focus    css=#form-input-195 .isModified
    Input Text    css=#form-input-195 .isModified    Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris test @.
    Sleep    500ms

#the word count should be correct
 #   wait until element contains    css=#form-input-195 .textarea-footer span    69

the user fills in rejection details
    the user should see the element    id=rejectReason
    Select From List By Index    id=rejectReason    1
    The user enters text to a text field    id=rejectComment    Have conflicts with the area of expertise.

the finance summary total should be correct
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(2) td:nth-child(2)    £7,680
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(1) td:nth-child(3)    60%
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(2) td:nth-child(4)    £4,608
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(2) td:nth-child(5)    £0
    Element Should Contain    css=#content div:nth-child(5) tr:nth-child(2) td:nth-child(6)    £3,072

the project cost breakdown total should be correct
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(2)    £7,680
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(3)    £6,400
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(4)    £1,280
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(5)    £0
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(6)    £0
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(7)    £0
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(8)    £0
    Element Should Contain    css=.form-group.project-cost-breakdown tr:nth-child(2) td:nth-child(9)    £0


