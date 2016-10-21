*** Settings ***
Documentation     INFUND-1859: As an applicant I want the Eligibility (process information) separate from applying, so that i don't have to apply in order to find out general information
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${ELIGIBILITY}    ${SERVER}/competition/1/info/eligibility
${Before you apply}    ${SERVER}/competition/1/info/before-you-apply
${What we ask you}    ${SERVER}/competition/1/info/what-we-ask-you

*** Test Cases ***
User goes to the Eligibility page
    [Documentation]    INFUND-1859
    [Tags]    HappyPath
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    Then the user should be redirected to the correct page    ${ELIGIBILITY}

User can navigate to the Before you apply pages
    [Documentation]    INFUND-1859
    [Tags]    HappyPath
    When the user clicks the button/link    Link=Using the new online Innovation Funding Service
    Then the user should be redirected to the correct page    ${Before you apply}
    And the user should see the text in the page    This innovation funding service is an online service run by Innovate UK.
    And the user goes back to the previous page
    When the user clicks the button/link    link=What we ask you
    Then the user should be redirected to the correct page    ${What we ask you}
    And the user should see the text in the page    The application has the following sections
