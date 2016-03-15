*** Keywords ***
the applicant adds some content and marks this section as complete
    Input Text    css=#form-input-4 .editor    This is some random text
    Click Element    name=mark_as_complete
    Wait Until Element Is Visible       name=mark_as_incomplete

the applicant edits the "economic benefit" question
    Click Element    name=mark_as_incomplete
    Wait Until Element Is Visible       name=mark_as_complete
