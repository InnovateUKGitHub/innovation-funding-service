*** Settings ***
Documentation     INFUND-3715 - As an Assessor I need to declare any conflicts of interest so that Innovate UK does not assign me assessments that are inappropriate for me.
Suite Setup       guest user log-in    &{assessor2_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Back to the dashboard link
    [Documentation]    INFUND-3715
    And the user clicks the button/link    jQuery=a:contains("Your declaration of interest")
    the user clicks the button/link    jQuery=a:contains("Back to assessor dashboard")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}

Client-side empty form validations
    [Documentation]    INFUND-3715
    [Tags]
    #TODO: Will need changes once client side validations have been implemented INFUND-5867
    Given the user clicks the button/link    jQuery=a:contains("Your declaration of interest")
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see a field and summary error    Please enter a principal employer
    And the user should see a field and summary error    Please enter the role at your principal employer
    And the user should see a field and summary error    In order to register an account you have to agree that this is an accurate account
    And the user should see a field and summary error    Please tell us if you have any appointments, directorships or consultancies
    And the user should see a field and summary error    Please tell us if you have any other financial interests
    And the user should see a field and summary error    Please tell us if any of your close family members have any appointments, directorships or consultancies
    And the user should see a field and summary error    Please tell us if any of your close family members have any other financial interests

Client-side empty yes/no question validations
    [Documentation]    INFUND-3715
    [Tags]
    [Setup]    the user is on the page or will navigate there    ${assessment_skills}
    #TODO: Will need changes once client side validations have been implemented INFUND-5867
    Given the user selects yes at yes/no question radio buttons
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see a field and summary error    Please enter a principal employer
    And the user should see a field and summary error    Please enter the role at your principal employer
    And the user should see a field and summary error    In order to register an account you have to agree that this is an accurate account
    And the user should see a field and summary error    Please enter an organisation
    And the user should see a field and summary error    Please enter a position
    And the user should see a field and summary error    Please enter your financial interests
    And the user should see a field and summary error    Please enter a relation
    And the user should see a field and summary error    Please enter an organisation
    And the user should see a field and summary error    Please enter a position
    And the user should see a field and summary error    Please enter your family financial interests

Client-side empty close family member validation
    [Documentation]    INFUND-3715
    [Tags]
    [Setup]    the user is on the page or will navigate there    ${assessment_skills}
    #TODO: Will need changes once client side validations have been implemented (INFUND-5867)
    Given the user selects yes at yes/no question radio buttons
    When the user clicks the button/link    jQuery=button:contains("Add another position")
    And the user clicks the button/link    jQuery=button:contains("Add another family member")
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see a field and summary error    Please enter a relation
    Then the user should see a field and summary error    Please enter a position
    Then the user should see a field and summary error    Please enter an organisation

Successful editing with no at yes/no questions
    [Documentation]    INFUND-3715
    [Tags]    PENDING    HappyPath
    [Setup]    the user is on the page or will navigate there    ${assessment_skills}
    #TODO: Failing because of QA issue INFUND-5868. Will need change when first rows by default have been implemented (INFUND-5871)
    Given the user correctly fills out the role, principle employer and accurate fields
    And the user selects no at yes/no question radio buttons
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    And the user clicks the button/link    jQuery=a:contains("Your declaration of interest")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}
    Then the user should see correctly filled out the role, employer, affiliation and accurate fields
    And the user should see no selected at yes/no questions

Successful editing with yes at yes/no questions
    [Documentation]    INFUND-3715
    [Tags]    PENDING    HappyPath
    [Setup]    the user is on the page or will navigate there    ${assessment_skills}
    #TODO: Failing because of QA issue INFUND-5868. Will need change when first rows by default have been implemented (INFUND-5871)
    Given the user correctly fills out the role, principle employer and accurate fields
    And the user selects yes at yes/no question radio buttons
    When the user adds positions
    And the user adds an additional position
    And the user removes the first two positions
    And the user adds close family member affiliations
    And the user adds an additional member affiliation
    And the user removes the first two family member affiliations
    And the user enters text to a text field    id=financialInterests    Financial Interests
    And the user enters text to a text field    id=familyFinancialInterests    Family Financial Interests
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    And the user clicks the button/link    jQuery=a:contains("Your declaration of interest")
    Then the user should see correctly filled out the role, employer, affiliation and accurate fields
    And the user should see yes selected at yes/no questions
    And the user should see the correct close family member affiliations
    And the user should see the correct close family member financial interests
    And the user should see only the additional family member affiliations
    And the user should see only the additional position

*** Keywords ***
the user selects yes at yes/no question radio buttons
    the user selects the checkbox    id=hasAppointments1
    the user selects the checkbox    id=hasFinancialInterests1
    the user selects the checkbox    id=hasFamilyAffiliations1
    the user selects the checkbox    id=hasFamilyFinancialInterests1

the user selects no at yes/no question radio buttons
    the user selects the checkbox    id=hasAppointments2
    the user selects the checkbox    id=hasFinancialInterests2
    the user selects the checkbox    id=hasFamilyAffiliations2
    the user selects the checkbox    id=hasFamilyFinancialInterests2

the user correctly fills out the role, principle employer and accurate fields
    the user enters text to a text field    id=principalEmployer    University
    the user enters text to a text field    id=role    Professor
    the user enters text to a text field    id=professionalAffiliations    Role x at Company y
    the user selects the checkbox    id=accurateAccount1

the user should see correctly filled out the role, employer, affiliation and accurate fields
    Textfield Value Should Be    id=principalEmployer    University
    Textfield Value Should Be    id=role    Professor
    Textarea Value Should Be    id=professionalAffiliations    Role x at Company y
    Checkbox Should Not Be Selected    id=accurateAccount1

the user should see yes selected at yes/no questions
    Checkbox Should Be Selected    id=hasAppointments1
    Checkbox Should Be Selected    id=hasFinancialInterests1
    Checkbox Should Be Selected    id=hasFamilyAffiliations1
    Checkbox Should Be Selected    id=hasFamilyFinancialInterests1

the user should see no selected at yes/no questions
    Checkbox Should Be Selected    id=hasAppointments2
    Checkbox Should Be Selected    id=hasFinancialInterests2
    Checkbox Should Be Selected    id=hasFamilyAffiliations2
    Checkbox Should Be Selected    id=hasFamilyFinancialInterests2

the user adds positions
    the user clicks the button/link    jQuery=button:contains("Add another position")
    the user enters text to a text field    id=appointments0.organisation    Organisation0
    the user enters text to a text field    id=appointments0.position    Position0

the user adds an additional position
    the user clicks the button/link    jQuery=button:contains("Add another position")
    the user enters text to a text field    id=appointments2.organisation    Organisation2
    the user enters text to a text field    id=appointments2.position    Position2

the user adds close family member affiliations
    the user clicks the button/link    jQuery=button:contains("Add another family member")
    the user enters text to a text field    id=familyAffiliations0.relation    Family member relation 0
    the user enters text to a text field    id=familyAffiliations0.organisation    Familiy member organisation 0
    the user enters text to a text field    id=familyAffiliations0.position    Family member position 0
    the user clicks the button/link    jQuery=button:contains("Add another family member")
    the user enters text to a text field    id=familyAffiliations1.relation    Family member relation 1
    the user enters text to a text field    id=familyAffiliations1.organisation    Familiy member organisation 1
    the user enters text to a text field    id=familyAffiliations1.position    Family member position 1

the user adds an additional member affiliation
    the user clicks the button/link    jQuery=button:contains("Add another family member")
    the user enters text to a text field    id=familyAffiliations2.relation    Family member relation 2
    the user enters text to a text field    id=familyAffiliations2.organisation    Familiy member organisation 2
    the user enters text to a text field    id=familyAffiliations2.position    Family member position 2

the user adds close family member financial interests
    the user enters text to a text field    id=familyFinancialInterests    Family Financial Interests

the user should see the correct positions
    Textfield Value Should Be    id=appointments0.organisation    Organisation0
    Textfield Value Should Be    id=appointments0.position    Position0
    Textfield Value Should Be    id=appointments1.organisation    Organisation1
    Textfield Value Should Be    id=appointments1.position    Position1

the user should see the correct financial interests
    Textarea Value Should Be    id=financialInterests    Financial Interests

the user should see the correct close family member affiliations
    Textfield Value Should Be    id=familyAffiliations0.relation    Family member relation 0
    Textfield Value Should Be    id=familyAffiliations0.organisation    Familiy member organisation 0
    Textfield Value Should Be    id=familyAffiliations0.position    Family member position 0
    Textfield Value Should Be    id=familyAffiliations1.relation    Family member relation 1
    Textfield Value Should Be    id=familyAffiliations1.organisation    Familiy member organisation 1
    Textfield Value Should Be    id=familyAffiliations1.position    Family member position 1

the user should see the correct close family member financial interests
    Textarea Value Should Be    id=familyFinancialInterests    Family Financial Interests

the user removes the first two positions
    the user clicks the button/link    jQuery=#position-table button:contains("Remove"):first
    the user clicks the button/link    jQuery=#position-table button:contains("Remove"):first

the user removes the first two family member affiliations
    the user clicks the button/link    jQuery=#family-table button:contains("Remove"):first
    the user clicks the button/link    jQuery=#family-table button:contains("Remove"):first

the user should see only the additional position
    Textfield Value Should Be    id=appointments0.organisation    Organisation2
    Textfield Value Should Be    id=appointments0.position    Position2
    the user should not see the element    id=appointments1.position
    the user should not see the element    id=appointments2.position

the user should see only the additional family member affiliations
    Textfield Value Should Be    id=familyAffiliations0.relation    Family member relation 2
    Textfield Value Should Be    id=familyAffiliations0.organisation    Familiy member organisation 2
    Textfield Value Should Be    id=familyAffiliations0.position    Family member position 2
    the user should not see the element    id=familyAffiliations1.relation
    the user should not see the element    id=familyAffiliations2.relation
