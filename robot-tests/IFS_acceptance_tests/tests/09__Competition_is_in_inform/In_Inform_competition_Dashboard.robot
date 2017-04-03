*** Settings ***
Documentation     INFUND-7365 Inflight competition dashboards: Inform dashboard
...
...               INFUND-7561 Inflight competition dashboards- View milestones
...
...               INFUND-8050 Release feedback and send notification email
...
...               INFUND-7861 Feedback overview dashboard of application after feedback is released
...
...               INFUND-8168 Question list and finance summary on Feedback overview dashboard
...
...               INFUND-8169 Assessors scores viewed on Feedback Overview by applicant
...
...               INFUND-8172 Assessor's Overall feedback displayed on Feedback Overview
...
...               INFUND-7950 Updates to 'Inform' - Competition dashboard
...
...               INFUND-8005 Feedback per question for applicant
...
...               INFUND-8876 No back navigation on applicant feedback view
...
...               INFUND-8066 Filter on 'Manage funding notifications' dashboard
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Competition Dashboard
    [Documentation]    INFUND-7365
    [Tags]    HappyPath
    When The user clicks the button/link    link=${INFORM_COMPETITION_NAME}
    Then The user should see the text in the page    7: Integrated delivery programme - low carbon vehicles
    And The user should see the text in the page    Inform
    And The user should see the text in the page    Programme
    And The user should see the text in the page    Materials and manufacturing
    And The user should see the text in the page    Satellite applications
    And The user should see the element    jQuery=a:contains("Invite assessors to assess the competition")
    And the user should not see the element    link=View and update competition setup

Milestones for the In inform competition
    [Documentation]    INFUND-7561 INFUND-7950
    [Tags]
    Then the user should see the element    jQuery=.button:contains("Manage funding notifications")
    And the user should see the element    jQuery=button:contains("Release feedback")
    And the user should see the element    css=li:nth-child(13).done    #Verify that 12. Notifications
    And the user should see the element    css=li:nth-child(14).not-done    #Verify that 13. Release feedback is not done

Filtering on the Manage funding applications page
    [Documentation]    INFUND-8066
    [Tags]
    Given The user clicks the button/link    jQuery=.button:contains("Manage funding notifications")
    And the user enters text to a text field    id=stringFilter    68
    And the user selects the option from the drop-down menu    Yes    id=sendFilter
    And the user selects the option from the drop-down menu    Successful    id=fundingFilter
    When the user clicks the button/link    jQuery=button:contains("Filter")
    Then the user should see the element    jQuery=td:nth-child(2):contains("68")
    And the user should not see the element    jQuery=td:nth-child(2):contains("70")
    And the user clicks the button/link    jQuery=.button:contains("Clear all filters")
    And the user should see the element    jQuery=td:nth-child(2):contains("70")
    [Teardown]    The user clicks the button/link    link=Competition

Checking release feedback button state is correct
    [Documentation]    INFUND-7950
    [Tags]
    Given the user clicks the button/link    link=Input and review funding decision
    And the user selects the checkbox    app-row-3
    And the user clicks the button/link    jQuery=button:contains("On hold")
    When the user clicks the button/link    jQuery=.link-back:contains("Competition")
    Then the user should see that the element is disabled    jQuery=button:contains("Release feedback")
    [Teardown]    User sends the notification to enable release feedback

Release feedback
    [Documentation]    INFUND-8050
    [Tags]    Email    HappyPath
    When The user clicks the button/link    jQuery=button:contains("Release feedback")
    Then The user should not see the text in the page    Inform
    When The user clicks the button/link    jQuery=a:contains(Live)
    Then The user should not see the text in the page    ${INFORM_COMPETITION_NAME}
    And the user reads his email    ${test_mailbox_two}+releasefeedback@gmail.com    Feedback for your application    The feedback provided by the independent assessors has been reviewed by Innovate UK

Unsuccessful applicant sees unsuccessful alert
    [Documentation]    INFUND-7861
    [Tags]    Email
    [Setup]    log in as a different user    &{unsuccessful_released_credentials}
    Given the user should see the element    jQuery=.status:contains("Unsuccessful")
    When the user clicks the button/link    jQuery=a:contains("Electric Drive")
    And the user should see the element    jQuery=.warning-alert:contains("Your application has not been successful in this competition")

Successful applicant see successful alert
    [Documentation]    INFUND-7861
    [Tags]    Email    HappyPath
    [Setup]    log in as a different user    &{successful_released_credentials}
    Given the user should see the element    jQuery=.status:contains("Successful")
    When the user clicks the button/link    jQuery=.previous-applications a:contains("High Performance Gasoline Stratified")
    Then the user should see the element    jQuery=.success-alert:contains("Congratulations, your application has been successful")

View feedback from each assessor
    [Documentation]    INFUND-8172
    [Tags]    Email    HappyPath
    Then the user should see the element    jQuery=h3:contains("Assessor 1") ~ p:contains("I have no problem recommending this application")
    And the user should see the element    jQuery=h3:contains("Assessor 2") ~ p:contains("Very good, but could have been better in areas")
    And the user should see the element    jQuery=h3:contains("Assessor 3") ~ p:contains("I enjoyed reading this application, well done")

Overall scores and application details are correct
    [Documentation]    INFUND-8169 INFUND-7861
    [Tags]    Email    HappyPath
    Then the overall scores are correct
    And the application question scores are correct
    And the application details are correct

User can see feedback to individual questions
    [Documentation]    INFUND-8005
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains("6. Innovation")
    Then the user should see the element    jQuery=h3:contains("Your answer") ~ p:contains("This is the applicant response for innovation.")
    And the user should see the element    jQuery=h4:contains("Assessor 1") ~ p:contains("This is the innovation feedback")
    [Teardown]    the user clicks the button/link    jQuery=.link-back:contains("Feedback overview")

The finance details are shown
    [Documentation]    INFUND-8168
    [Tags]    Email
    When the user clicks the button/link    jQuery=.collapsible button
    Then the user should see the element    jQuery=.collapsible div[aria-hidden="false"]
    And the user should not see the element    jQuery=.collapsible div[aria-hidden="true"]

Selecting the dashboard link takes user back to the dashboard
    [Documentation]    INFUND-8876
    [Tags]
    Given the user clicks the button/link    jQuery=.link-back:contains("Dashboard")
    Then the user should see the element    jQuery=h1:contains("Your dashboard")

*** Keywords ***
the overall scores are correct
    the user should see the element    jQuery=.table-overflow td:nth-child(2):contains("6")
    the user should see the element    jQuery=.table-overflow td:nth-child(3):contains("5")
    the user should see the element    jQuery=.table-overflow td:nth-child(4):contains("6")
    the user should see the element    jQuery=.table-overflow td:nth-child(5):contains("4")
    the user should see the element    jQuery=.table-overflow td:nth-child(6):contains("4")
    the user should see the element    jQuery=.table-overflow td:nth-child(7):contains("5")
    the user should see the element    jQuery=.table-overflow td:nth-child(8):contains("7")
    the user should see the element    jQuery=.table-overflow td:nth-child(9):contains("7")
    the user should see the element    jQuery=.table-overflow td:nth-child(10):contains("3")
    the user should see the element    jQuery=.table-overflow td:nth-child(11):contains("7")

the application question scores are correct
    the user should see the element    jQuery=.column-two-thirds:contains("Business opportunity") ~ div div:contains("Average score 6 / 10")
    the user should see the element    jQuery=.column-two-thirds:contains("Potential market") ~ div div:contains("Average score 5 / 10")
    the user should see the element    jQuery=.column-two-thirds:contains("Project exploitation") ~ div div:contains("Average score 6 / 10")
    the user should see the element    jQuery=.column-two-thirds:contains("Economic benefit") ~ div div:contains("Average score 4 / 10")
    the user should see the element    jQuery=.column-two-thirds:contains("Technical approach") ~ div div:contains("Average score 4 / 10")
    the user should see the element    jQuery=.column-two-thirds:contains("Innovation") ~ div div:contains("Average score 5 / 10")
    the user should see the element    jQuery=.column-two-thirds:contains("Risks") ~ div div:contains("Average score 7 / 10")
    the user should see the element    jQuery=.column-two-thirds:contains("Project team") ~ div div:contains("Average score 7 / 10")
    the user should see the element    jQuery=.column-two-thirds:contains("Funding") ~ div div:contains("Average score 3 / 10")
    the user should see the element    jQuery=.column-two-thirds:contains("Adding value") ~ div div:contains("Average score 7 / 10")
    the user should see the element    jQuery=p:contains("Average overall: 53%")

the application details are correct
    the user should see the element    jQuery=p:contains("High Performance Gasoline Stratified")
    the user should see the element    jQuery=p:contains("Electric Sounds Ltd")
    the user should see the element    jQuery=p:contains("Project start date: ")
    the user should see the element    jQuery=p:contains("Duration")
    the user should see the element    jQuery=h3:contains("Total project cost")

User sends the notification to enable release feedback
    the user clicks the button/link    link=Input and review funding decision
    the user selects the checkbox    app-row-3
    the user clicks the button/link    jQuery=button:contains("Unsuccessful")
    the user clicks the button/link    jQuery=.link-back:contains("Competition")
    the user clicks the button/link    jQuery=button:contains("Manage funding notifications")
    the user selects the checkbox     app-row-70
    the user clicks the button/link    jQuery=button:contains("Write and send email")
    the user enters text to a text field    id=subject    Subject
    the user enters text to a text field    jQuery=.editor    Text
    the user clicks the button/link    jQuery=button:contains("Send email to all applicants")
    the user clicks the button/link    jQuery=.link-back:contains("Competition")
