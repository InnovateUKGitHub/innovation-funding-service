*** Keywords ***
the applicant adds some content and marks this section as complete
    sleep    300ms
    Focus    css=#form-input-4 .editor
    Input Text    css=#form-input-4 .editor    This is some random text
    the user clicks the button/link    name=mark_as_complete
    the user should see the element    name=mark_as_incomplete

the applicant edits the "economic benefit" question
    the user clicks the button/link    name=mark_as_incomplete
    the user should see the element    name=mark_as_complete
