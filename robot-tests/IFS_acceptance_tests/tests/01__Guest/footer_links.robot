*** Settings ***
Documentation     IFS-362 As an IFS user I am able to find links to general guidance links via the footer both when signed in or not signed in.
Suite Setup       The guest user opens the browser
Suite Teardown    The user closes the browser
Force Tags        Guest
Resource          ../../resources/defaultResources.robot
# Note for future maintenance: In this file there are checks to external links (out of IFS).
# The tests are fine as they are now, but in case those links break in the future, then we can remove those tests.
# We do not need to maintain checks towards external pages.
# However, internal links eg Terms & Conditions, do need to be maintained!!

*** Test Cases ***
Guest user can click on the footer links
    [Documentation]    IFS-362
    [Tags]
    [Setup]    the user navigates to the page      ${frontDoor}
    Given the user tries the footer links          ${frontDoor}
    Then the user navigates to the page            ${LOGIN_URL}
    Then the user tries the footer links           ${DASHBOARD_URL}
    And the user navigates to the page             ${LOGIN_URL}
    When Logging in and Error Checking             &{lead_applicant_credentials}
    Then the user should see the text in the page  Sign out
    And the user tries the footer links            ${LOGIN_URL}

*** Keywords ***
the user tries the footer links
    [Arguments]    ${page}
    Given the user tries the external link  ${page}    Innovate UK    Innovate UK - GOV.UK
    And the user tries the external link    ${page}    Innovation funding advice    Business innovation: what funding you can apply for - GOV.UK
    And the user tries the external link    ${page}    Events    Events - innovateuk
    And the user tries the external link    ${page}    Connect to innovation experts    Innovation: connect to experts, specialist support and facilities - GOV.UK
    And the user tries the external link    ${page}    Innovate UK blog    Innovate UK
    And the user tries the external link    ${page}    GOV.UK accessibility    Accessibility - GOV.UK
    And the user tries the link             ${page}    Terms and conditions    Terms and conditions
    And the user tries the link             ${page}    Contact us    Contact us
    And the user tries the link             ${page}    Latest funding opportunities    Innovation competitions
    And the user tries the link             ${page}    Find out more about cookies    Cookies

the user tries the link
    [Arguments]    ${page}    ${link}    ${header_one}
    Given the user navigates to the page  ${page}
    When the user clicks the button/link  link=${link}
    Then the user should see the element  jQuery=h1:contains(${header_one})

the user tries the external link
    [Arguments]    ${page}    ${link}    ${title}
    Given the user navigates to the page   ${page}
    When the user clicks the button/link   link=${link}
    Then the user should get a new window  ${title}

the user should get a new window
    [Arguments]    ${title}
    Select Window    title=${title}