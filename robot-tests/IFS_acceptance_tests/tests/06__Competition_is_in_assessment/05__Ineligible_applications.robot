*** Settings ***
Documentation     INFUND-8942 - Filter and sorting on 'Ineligible applications' dashboard
...
...               INFUND-7374 - As a member of the competitions team I can inform an applicant that their application is ineligible so that they know their application is not being sent for assessment
...
...               INFUND-7373 - As a member of the competitions team I can view a list of ineligible applications so that I know which applications have been marked as ineligible and which applicants have been informed
...
...               INFUND-9130 - Applicant dashboard: Application moved from 'Application in progress' section of dashboard to 'Previous applications' section
...
...               INFUND-7370 - As a member of the competitions team I can mark a submitted application as ineligible so that the application is not sent to be assessed
...
...               INFUND-8941 - As a member of the competitions team I can reinstate an application that as been marked as Ineligible
Suite Setup       Guest user log-in in new browser    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    Applicant
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
A non submitted application cannot be marked as ineligible
    [Documentation]    INFUND-7370
    [Tags]    HappyPath
    Given the user navigates to the page    ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/applications/all
    When the user clicks the button/link    link = 16
    Then the user should not see the element    jQuery=h2 button:contains("Mark application as ineligible")
    [Teardown]    the user clicks the button/link    jQuery=.link-back:contains("Back")

Selecting to mark an application as ineligible opens a text box
    [Documentation]    INFUND-7370
    [Tags]    HappyPath
    Given the user clicks the button/link     link=28
    When the user clicks the button/link    jQuery=h2 button:contains("Mark application as ineligible")  #There are 2 buttons with the same name so we need to be careful
    Then the user should see the element    id=ineligibleReason

Cancel marking the application as ineligible
    [Documentation]    INFUND-7370
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Cancel")
    Then the user should not see the element    id=ineligibleReason

Client side validation - mark an application as ineligible
    [Documentation]    IFS-159
    [Tags]
    Given the user clicks the button/link                     jQuery=h2 button:contains("Mark application as ineligible")
    And the user enters multiple strings into a text field    id=ineligibleReason  a${SPACE}  402
    Then the user should see an error                         Maximum word count exceeded. Please reduce your word count to 400.
    [Teardown]    the user clicks the button/link             jQuery=h2 button:contains("Mark application as ineligible")

Mark an application as ineligible
    [Documentation]    INFUND-7370
    [Tags]    HappyPath
    Given the user clicks the button/link           jQuery=h2 button:contains("Mark application as ineligible")
    And the user enters text to a text field        id=ineligibleReason    Hello there
    When the user clicks the button/link            jQuery=.button:contains("Mark application as ineligible")
    Then the user should see the element            jQuery=td:contains("28")

Filter ineligible applications
    [Documentation]    INFUND-8942
    [Tags]
    Given the user enters text to a text field    id=filterSearch    28
    And the user selects the option from the drop-down menu    No    id=filterInform
    When the user clicks the button/link    jQuery=.button:contains("Filter")
    Then the user should see the element    jQuery=td:contains("28") ~ td .button:contains("Inform applicant")
    And the user should not see the element    jQuery=td:contains("63") ~ td span:contains("Informed")
    When the user clicks the button/link    jQuery=a:contains("Clear all filters")
    Then the user should see the element       jQuery=td:contains("63") ~ td span:contains("Informed")

Sort ineligible applications by lead
    [Documentation]    INFUND-8942
    [Tags]
    When the application list is sorted by    Lead
    Then the applications should be sorted by column    3

Inform a user their application is ineligible
   [Documentation]    INFUND-7374
   [Tags]    HappyPath
   Given the user clicks the button/link    jQuery=td:contains("28") ~ td .button:contains("Inform applicant")
   And the user enters text to a text field    id=subject    This is ineligible
   And the user enters text to a text field    id=message    Thank you for your application but this is ineligible
   And the user clicks the button/link    jQuery=button:contains("Send")
   Then the user should see the element    jQuery=td:contains("28") ~ td span:contains("Informed")
   Then the application is in the right section    Previous applications
   And the user reads his email    ${Ineligible_user["email"]}    This is ineligible    Thank you for your application but this is ineligible

Reinstate an application
   [Documentation]    INFUND-8941
   [Tags]    HappyPath
   [Setup]    Log in as a different user    &{Comp_admin1_credentials}
   Given the user navigates to ineligible applications
   And the user clicks the button/link     link=28
   And the user clicks the button/link    jQuery=a:contains("Reinstate application")
   When the user clicks the button/link    jQuery=button:contains("Reinstate application")
   Then the application is in the right section    Applications in progress

*** Keywords ***
the application is in the right section
    [Arguments]    ${section}
    Log in as a different user    &{Ineligible_user}
    the user should see the element    jQuery=h2:contains(${section}) ~ ul a:contains("Living with Virtual Reality")

the user navigates to ineligible applications
    the user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    the user clicks the button/link    link = Applications: All, submitted, ineligible
    the user clicks the button/link    link = Ineligible applications