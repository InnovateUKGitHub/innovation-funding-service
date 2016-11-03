*** Settings ***
Documentation     INFUND-1188 As an assessor I want to be able to review my assessments from one place so that I can work in my favoured style when reviewing
...
...               INFUND-3723 As an Assessor looking at my competition assessment dashboard I can see details for the competition, so that I am able to reference key information as I want.
...
...               INFUND-1180 As an Assessor I want to accept or decline an assignment of an application to assess so that the competitions team can manage the assessment process.
Suite Setup       Log in as user    email=paul.plum@gmail.com    password=Passw0rd
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Competition link should navigate to the applications
    [Documentation]    INFUND-3716
    [Tags]    HappyPath
    [Setup]
    When The user clicks the button/link    link=Juggling Craziness
    Then The user should see the text in the page    Applications for Assessment

Calculation of the applications for assessment should be correct
    Then the total calculation in dashboard should be correct    Applications for Assessment    //div/form/ul/li

Details of the competition are visible
    [Documentation]    INFUND-3723
    Then the user should see the text in the page    Competition
    And the user should see the text in the page    Accept applications deadline
    And the user should see the text in the page    Submit applications deadline
    And the user should see the text in the page    Tuesday 12 January 2016
    And the user should see the text in the page    Saturday 28 January 2017

Accept an application for assessment
    [Documentation]    INFUND-1180
    [Tags]    Pending
    #TODO INFUND-1180 is ready to test
    Given the user should see the element
    When The user clicks the button/link
    Then the user should see the text in the page    Accept application
    And The user clicks the button/link    jQuery=button:contains("Accept")
    Then The user should be redirected to the correct page
    And the status is updated

Assessor can only make selection once
    [Documentation]    INFUND-1180
    [Tags]    Pending
    #TODO INFUND-1180 is ready to test

Reject an application for assessment
    [Documentation]    INFUND-1180
    [Tags]    Pending
    #TODO INFUND-1180 is ready to test
    Given the user should see the element
    When The user clicks the button/link
    Then the user should see the text in the page    Accept application
    And The user clicks the button/link    jQuery=button:contains("Reject")
    And the user clicks the button/link    jQuery=.button:contains("Reject")
    Then the user should see an error    The reason cannot be blank
    And the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.
    And the assessor fills all fields with valid inputs
    And the user clicks the button/link    jQuery=button:contains("Reject")
    And the user should see the text in the page    Thank you for letting us know you are unable to assess applications within this competition.
    Then The user should be redirected to the correct page
    And the status is updated

*** Keywords ***

