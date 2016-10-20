*** Settings ***
Documentation     INFUND-1622: Creating a baseline test to benchmark performance
Test Teardown     The user closes the browser
Resource          ../../../../resources/defaultResources.robot

*** Test Cases ***
Go through the applicant process
    [Tags]    Pending
    # TODO This test is for benchmarking only - please do not remove Pending tag or delete!
    Given benchmarking is set up
    And the guest logs in as lead applicant
    And the applicant views the overview page for the first application
    And the applicant visits all of the question pages, edits their content and marks them as complete before saving

Go through the assessor journey
    [Tags]    Pending
    # TODO This test is for benchmarking only - please do not remove Pending tag or delete!
    Given the guest logs in as an assessor
    And the assessor can visit the competition dashboard
    And the assessor can visit the application details
    And the assessor can visit the application questions
    And the assessor can visit the application summary

*** Keywords ***
the guest logs in as an assessor
    the guest user opens the browser
    time until page contains    New to this service?    Loading the login page
    Log in as user    &{assessor_credentials}
    time until element is visible    link=My dashboard    Logging in as assessor

the assessor can visit the competition dashboard
    click link    ${OPEN_COMPETITION_LINK}

the assessor can visit the application details

the assessor can visit the application questions

the assessor can visit the application summary

the applicant visits all of the question pages, edits their content and marks them as complete before saving
    the applicant goes to the application details page and performs actions
    the applicant goes to the project summary, and performs actions
    the applicant goes to the public description, and performs actions
    the applicant goes to the scope section, and performs actions
    the applicant goes to the business opportunity section, and performs actions
    the applicant goes to the potential market section, and performs actions
    the applicant goes to the project exploitation section, and performs actions
    the applicant goes to the economic benefit section, and performs actions
    the applicant goes to the technical approach section, and performs actions
    the applicant goes to the innovation section, and performs actions
    the applicant goes to the risks section, and performs actions
    the applicant goes to the project team section, and performs actions
    the applicant goes to the funding section, and performs actions
    the applicant goes to the adding value section, and performs actions
    the applicant goes to the your finances section, and performs actions
    the applicant goes to the summary page

the applicant goes to the application details page and performs actions
    click link    Application details
    time until page contains    full title of your project    Loading the application details page
    mark section as complete    Marking application details as complete
    the applicant saves and returns to the overview    Saving the application details page

the applicant goes to the project summary, and performs actions
    click link    Project summary
    time until page contains    Please provide a short summary of your project    Loading the project summary section
    Input Text    css=#form-input-11 .editor    This is some random text
    mark section as complete    Marking summary section as complete
    the applicant saves and returns to the overview    Saving the project summary section

the applicant goes to the public description, and performs actions
    click link    Public description
    time until page contains    Please provide a brief description of your project    Loading the public description section
    Input Text    css=#form-input-12 .editor    This is some random text
    mark section as complete    Marking public description section as complete
    the applicant saves and returns to the overview    Saving the public description section

the applicant goes to the scope section, and performs actions
    click link    Scope
    time until page contains    If your application doesn't align with the scope    Loading the scope section
    Input Text    css=#form-input-13 .editor    This is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random textThis is some random text
    mark section as complete    Marking scope section as complete
    the applicant saves and returns to the overview    Saving the scope section

the applicant goes to the business opportunity section, and performs actions
    click link    1. Business opportunity
    time until page contains    What is the business opportunity    Loading the business opportunity section
    Input Text    css=#form-input-1 .editor    This is some random text
    mark section as complete    Marking business opportunity section as complete
    the applicant saves and returns to the overview    Saving the business opportunity section

the applicant goes to the potential market section, and performs actions
    click link    2. Potential market
    time until page contains    size of the potential market    Loading the potential market section
    Input Text    css=#form-input-2 .editor    This is some random text
    mark section as complete    Marking potential market section as complete
    the applicant saves and returns to the overview    Saving the potential market section

the applicant goes to the project exploitation section, and performs actions
    click link    3. Project exploitation
    time until page contains    exploit and market your project    Loading the project exploitation section
    Input Text    css=#form-input-3 .editor    This is some random text
    mark section as complete    Marking project exploitation section as complete
    the applicant saves and returns to the overview    Saving the project exploitation section

the applicant goes to the economic benefit section, and performs actions
    click link    4. Economic benefit
    time until page contains    economic, social and environmental benefits    Loading the economic benefit section
    Input Text    css=#form-input-4 .editor    This is some random text
    mark section as complete    Marking economic benefit section as complete
    the applicant saves and returns to the overview    Saving the economic benefit section

the applicant goes to the technical approach section, and performs actions
    click link    5. Technical approach
    time until page contains    What technical approach will you use    Loading the technical approach section
    Input Text    css=#form-input-5 .editor    This is some random text
    mark section as complete    Marking technical approach section as complete
    the applicant saves and returns to the overview    Saving the technical approach section

the applicant goes to the innovation section, and performs actions
    click link    6. Innovation
    time until page contains    Explain how your project is innovative    Loading the innovation section
    Input Text    css=#form-input-6 .editor    This is some random text
    mark section as complete    Marking innovation section as complete
    the applicant saves and returns to the overview    Saving the innovation section

the applicant goes to the risks section, and performs actions
    click link    7. Risks
    time until page contains    What are the risks    Loading the risks section
    Input Text    css=#form-input-7 .editor    This is some random text
    mark section as complete    Marking risks section as complete
    the applicant saves and returns to the overview    Saving the risks section

the applicant goes to the project team section, and performs actions
    click link    8. Project team
    time until page contains    Does your project team have the skills    Loading the project team section
    Input Text    css=#form-input-8 .editor    This is some random text
    mark section as complete    Marking project team section as complete
    the applicant saves and returns to the overview    Saving the project team section

the applicant goes to the funding section, and performs actions
    click link    9. Funding
    time until page contains    how much funding you need    Loading the funding section
    Input Text    css=#form-input-15 .editor    This is some random text
    mark section as complete    Marking funding section as complete
    the applicant saves and returns to the overview    Saving the funding section

the applicant goes to the adding value section, and performs actions
    click link    10. Adding value
    time until page contains    funding partners add value    Loading the adding value section
    Input Text    css=#form-input-16 .editor    This is some random text
    Assign the adding value section to Jessica Doe
    the applicant saves and returns to the overview    Saving the adding value section

the applicant goes to the your finances section, and performs actions
    click link    Your finances
    time until page contains    acesss and edit your finances    Loading the your finances section
    the applicant edits the finance sections
    the applicant saves and returns to the overview    Saving the your finances section

the applicant edits the finance sections
    the applicant adds in some labour costs
    the applicant edits the overhead details
    the applicant adds in some materials costs
    the applicant adds in some subcontracting costs
    the applicant adds in some travel and subsistence costs

Assign the adding value section to Jessica Doe
    focus    css=#form-input-16 .editor
    Input Text    css=#form-input-16 .editor    lead Applicant's text 123...
    Click Element    css=#form-input-16 .assign-button button
    Click Element    xpath=//div[@id="form-input-16"]//button[contains(text(),"Jessica Doe")]
    Time Until Page Contains    Question assigned successfully    Assigning a question

the applicant goes to the summary page
    click link    Review & submit
    time until page contains    Please review your application before final submission    Loading the summary page

the applicant adds in some labour costs
    Click Element    css=[aria-controls="collapsible-1"]
    Click Element    link=Add another role
    Time Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    Adding a row to the Labour table
    Clear Element Text    css=#cost-labour-1-workingDays
    Input Text    css=#cost-labour-1-workingDays    230
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
    Input Text    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    test
    Click Element    link=Add another role
    Time Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    Adding another row to the Labour table
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    100
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(1) input    test
    Click Element    Link=Remove
    time until element is not visible    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    Removing a row from the labour table
    reload page

the applicant edits the overhead details
    Click Element    css=[aria-controls="collapsible-2"]
    Select Radio Button    overheads-rateType-29-51    CUSTOM_RATE
    focus    css=.app-submit-btn
    Time Until Element Is Visible    id=cost-overheads-51-customRate    Changing the options in the overheads subsection
    reload page

the applicant adds in some materials costs
    Click Element    xpath=//*[@aria-controls="collapsible-3"]
    Click link    Add another materials cost
    Time Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    Adding a row to the Materials table
    Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    Input Text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    input text    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    focus    css=.app-submit-btn
    Click link    link=Add another materials cost
    Time Until Page Contains Element    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    Adding another row to the Materials table
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    Input Text    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    test
    click element    link=Remove
    time until element is not visible    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    Removing a row from the Materials table
    reload page

the applicant adds in some subcontracting costs
    Click Element    css=[aria-controls="collapsible-5"]
    Click Link    link=Add another subcontractor
    Time Until Page Contains Element    css=#collapsible-5 .form-row:nth-child(1)    Adding a row to the subcontracting costs table
    Input Text    css=#collapsible-5 .form-row:nth-child(1) input[id$=subcontractingCost]    100
    input text    css=#collapsible-5 .form-row:nth-child(1) input[id$=companyName]    test
    focus    css=.app-submit-btn
    Click link    Link=Add another subcontractor
    Time Until Page Contains Element    css=#collapsible-5 .form-row:nth-child(2)    Adding another row to the subcontracting costs table
    Input Text    css=#collapsible-5 .form-row:nth-child(2) input[id$=subcontractingCost]    100
    input text    css=#collapsible-5 .form-row:nth-child(2) input[id$=companyName]    test
    focus    css=.app-submit-btn
    click element    link=Remove
    time until element is not visible    css=#collapsible-5 .form-row:nth-child(2) input[id$=subcontractingCost]    Removing a row from the subcontracting costs table
    reload page

the applicant adds in some travel and subsistence costs
    Click Element    css=[aria-controls="collapsible-6"]
    Click link    Add another travel cost
    Time Until Page Contains Element    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    Adding a row to the travel table
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    focus    css=.app-submit-btn
    Click link    Add another travel cost
    Time Until Page Contains Element    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    Adding another row to the travel table
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    100
    Input Text    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    test
    focus    css=.app-submit-btn
    click element    link=Remove
    time until element is not visible    css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    Removing a row from the travel table
    reload page

the guest logs in as lead applicant
    the guest user opens the browser
    time until page contains    New to this service?    Loading the login page
    login as user    &{lead_applicant_credentials}
    time until element is visible    link=My dashboard    Logging in

the applicant views the overview page for the first application
    click link    A novel solution to an old problem
    time until page contains    Application overview    Loading the overview page

the applicant saves and returns to the overview
    [Arguments]    ${step_name}
    go to    ${application_overview_url}
    time until page contains    Application overview    ${step_name}
    reload page

mark section as complete
    [Arguments]    ${step_name}
    click element    name=mark_as_complete
    time until element is visible    name=mark_as_incomplete    ${step_name}
