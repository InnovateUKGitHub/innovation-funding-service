*** Settings ***
Documentation     INFUND-901: As a lead applicant I want to invite application contributors to collaborate with me on the application, so that they can contribute to the application in a collaborative competition
...
...
...               INFUND-896: As a lead applicant i want to invite partner organisations to collaborate on line in my application, so that i can create the consortium needed to complete the proposed project
...
...
...               INFUND-2375: Error message needed on contributors invite if user tries to add duplicate email address
...
...               INFUND-4807 As an applicant (lead) I want to be able to remove an invited collaborator who is still pending registration so that I can manage members no longer required to be part of the consortium
Suite Setup       Login and create a new application
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${INVITE_COLLABORATORS2_PAGE}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_3}/contributors/invite?newApplication

*** Test Cases ***
lead applicant can add/remove partners
    [Documentation]    INFUND-901
    [Tags]    HappyPath
    Given The user navigates to the invitation page of the test application
    When The user clicks the button/link    jquery=li:nth-child(1) button:contains('Add another person')
    Then The user should see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)
    And The user clicks the button/link    jquery=li:nth-child(1) button:contains('Remove')
    Then The user should not see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)

lead applicant cannot remove himself
    [Documentation]    INFUND-901
    [Tags]
    Then the lead applicant cannot be removed

Validations for the Email field
    [Documentation]    INFUND-901
    [Tags]    HappyPath
    When The user clicks the button/link    jquery=li:nth-child(1) button:contains('Add another person')
    And the applicant fills the lead organisation fields    Collaborator01    @hiveit.co.uk
    Then the user should see an error    Please enter a valid email address

Validations for the name field
    [Documentation]    INFUND-901
    [Tags]
    When the applicant fills the lead organisation fields    ${EMPTY}    ewan+5@hiveit.co.uk
    Then the user should see an error    ${empty_field_warning_message}

Link to remove partner organisation
    [Documentation]    INFUND-1039
    [Tags]    HappyPath
    # on the user interface.    All we can test is that the state is saved in cookie, so not lost on page reload.
    When The user clicks the button/link    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    And the applicant inputs details    1
    Then The user should see the element    jquery=li:nth-child(2) button:contains('Remove')
    When The user clicks the button/link    jquery=li:nth-child(2) button:contains('Remove')
    Then The user should not see the text in the page    Organisation name

Autosaved works (in cookie)
    [Documentation]    INFUND-1039
    [Tags]    HappyPath
    When The user clicks the button/link    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    And the applicant can enter Organisation name, Name and E-mail
    Then the applicant's inputs should be visible

Blank Partner organisation fields are not allowed
    [Documentation]    INFUND-896
    [Tags]
    Given the user enters text to a text field    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    MR Tester
    When the applicant fills the Partner organisation fields    1    ${EMPTY}    ${EMPTY}    ${EMPTY}
    Then the user should see an error    An organisation name is required
    And the user should see an error    This field cannot be left blank
    And the user should see an error    Please enter an email address

Invalid email address is not allowed
    [Documentation]    INFUND-896
    [Tags]
    When the applicant fills the Partner organisation fields    1    Fannie May    Collaborator 10    collaborator10_invalid_email
    Then the user should see an error    Please enter a valid email address

Already invite email should is not allowed
    [Tags]
    When the applicant fills the Partner organisation fields    1    Fannie May    Collaborator 10    ewan+5@hiveit.co.uk
    Then the user should see an error    You have already added this email address

Link to add multiple partner organisation
    [Tags]    HappyPath
    When The user clicks the button/link    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    And The user should see the element    css=li:nth-child(3)
    And The user clicks the button/link    jQuery=li:nth-child(3) button:contains("Remove")
    Then The user should not see the element    jQuery=li:nth-child(3) button:contains("Remove")
    [Teardown]  The user clicks the button/link    jQuery=li:nth-child(2) button:contains("Remove")

The Lead's inputs should not be visible in other application invites
    [Documentation]    INFUND-901
    [Tags]    HappyPath
    When the user navigates to the page    ${INVITE_COLLABORATORS2_PAGE}
    Then the user should not see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input

Lead applicant can remove the Pending partners
    [Documentation]    INFUND-4807
    Given The user navigates to the invitation page of the test application
    And the applicant fills the lead organisation fields    Test user 001    test@emai.com
    When the user clicks the button/link    jQuery=li:nth-child(2) a:contains("Remove")
    And the user clicks the button/link    jQuery=button:contains("Remove")
    Then the user should not see the element    Link=Test user 001

*** Keywords ***
the user fills the name and email field and reloads the page
    [Arguments]    ${group_number}
    The user should see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)
    The user enters text to a text field    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator01
    The user enters text to a text field    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    ewan+8@hiveit.co.uk
    sleep    2s
    wait for autosave
    the user reloads the page

the user's inputs should still be visible
    [Arguments]    ${group_number}
    Textfield Value Should Be    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator01
    ${input_value} =    Get Value    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input
    Should Be Equal As Strings    ${input_value}    ewan+8@hiveit.co.uk
    Textfield Value Should Be    name=organisations[${group_number}].organisationName    Test name
    Textfield Value Should Be    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator test
    ${input_value} =    Get Value    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input
    Should Be Equal As Strings    ${input_value} =    ${input_value} =

the lead applicant cannot be removed
    Element Should Contain    css=li:nth-child(1) tr:nth-of-type(1) td:nth-of-type(3)    Lead applicant

the applicant fills the lead organisation fields
    [Arguments]    ${LEAD_NAME}    ${LEAD_EMAIL}
    The user enters text to a text field    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    ${LEAD_NAME}
    The user enters text to a text field    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    ${LEAD_EMAIL}
    # the following keyword disables the browser's validation
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Focus    jquery=button:contains("Save Changes")
    browser validations have been disabled
    The user clicks the button/link    jquery=button:contains("Save Changes")
    sleep    500ms

the applicant can enter Organisation name, Name and E-mail
    The user enters text to a text field    name=organisations[1].organisationName    Fannie May
    The user enters text to a text field    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    The user enters text to a text field    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    ewan+10@hiveit.co.uk
    Focus    jquery=li:nth-child(2) button:contains('Add another person')
    The user clicks the button/link    jquery=li:nth-child(2) button:contains('Add another person')
    The user enters text to a text field    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator 3
    The user enters text to a text field    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(2) input    ewan+11@hiveit.co.uk
    Focus    jquery=button:contains("Save Changes")
    Sleep    2s
    Capture Page Screenshot
    the user reloads the page

the applicant's inputs should be visible
    Textfield Value Should Be    name=organisations[1].organisationName    Fannie May
    ${input_value} =    Get Value    name=organisations[1].organisationName
    Should Be Equal As Strings    ${input_value}    Fannie May
    Textfield Value Should Be    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    ${input_value} =    Get Value    css=li:nth-child(2) tr:nth-of-type(1) td:nth-of-type(1) input
    Should Be Equal As Strings    ${input_value}    Collaborator 2
    Textfield Value Should Be    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input    Collaborator 3
    ${input_value} =    Get Value    css=li:nth-child(2) tr:nth-of-type(2) td:nth-of-type(1) input
    Should Be Equal As Strings    ${input_value}    Collaborator 3

the applicant inputs details
    [Arguments]    ${group_number}
    The user enters text to a text field    name=organisations[${group_number}].organisationName    Fannie May
    The user enters text to a text field    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    Collaborator 2
    The user enters text to a text field    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    ewan+10@hiveit.co.uk

the applicant fills the Partner organisation fields
    [Arguments]    ${group_number}    ${PARTNER_ORG_NAME}    ${ORG_NAME}    ${EMAIL_NAME}
    browser validations have been disabled
    The user enters text to a text field    name=organisations[${group_number}].organisationName    ${PARTNER_ORG_NAME}
    The user enters text to a text field    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(1) input    ${ORG_NAME}
    The user enters text to a text field    css=li:nth-last-child(2) tr:nth-of-type(1) td:nth-of-type(2) input    ${EMAIL_NAME}
    # the following keyword disables the browser's validation
    Focus    jquery=button:contains("Save Changes")
    The user clicks the button/link    jquery=button:contains("Save Changes")
    sleep    500ms

a validation error is shown on organisation name
    [Arguments]    ${group_number}
    The user should see the element    css=input[name='organisations[${group_number}].organisationName'].field-error

Login and create a new application
    Given Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jquery=button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Invitation page test
    And the user clicks the button/link    jQuery=button:contains("Save and return")

The user navigates to the invitation page of the test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Invitation page test
    And the user should see the text in the page    view team members and add collaborators
    And the user clicks the button/link    link=view team members and add collaborators
    And The user clicks the button/link    jQuery=.button:contains("Invite new contributors")
