*** Settings ***
Documentation
...               INFUND-4840 As a project finance team member I want to be able to post a query in the finance checks section so that the relevant finance contact can be given the opportunity to provide further details
...
...               INFUND-4843 As a partner I want to be able to respond to a query posted by project finance so that they can review the additional information requested
...
...               INFUND-4845 As a project finance team member I want to be able to post a note in the finance checks section so that colleagues reviewing the partner's progress can be kept informed of any further information needed to support the finance checks section
...
...               INFUND-4841 As a project finance team member I want to send an email to the relevant finance contact so that they can be notified when a query has been posted in finance checks
...
...               INFUND-7752 Internal user can further respond to an external parter's response to a query
...
...               INFUND-7753 Partner receives an email alerting them to a further response to an earlier query
...
...               INFUND-7756 Project finance can post an update to an existing note

Suite Setup       Moving ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
Suite Teardown    Close browser and delete emails
Force Tags        Project Setup
Resource          PS_Common.robot

*** Variables ***
${opens_in_new_window}    (opens in a new window)

*** Test Cases ***
Queries section is linked from eligibility and this selects eligibility on the query dropdown
    [Documentation]    INFUND-4840
    [Tags]  HappyPath
    [Setup]  finance contacts are selected and bank details are approved
    Given log in as a different user      &{internal_finance_credentials}
    When the user navigates to the page          ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/${EMPIRE_LTD_ID}/eligibility
    And the user clicks the button/link    jQuery=.button:contains("Queries")
    Then the user should see the text in the page    Raise finance queries to the organisation in this section
    When the user clicks the button/link    jQuery=.button:contains("Post a new query")
    Then the user should see the dropdown option selected    Eligibility    section

Queries section is linked from viability and this selects viability on the query dropdown
    [Documentation]    INFUND-4840
    [Tags]
    [Setup]  the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    Given the user clicks the button/link    jQuery=table.table-progress th:contains("Lead") + td a
    When the user clicks the button/link    jQuery=.button:contains("Queries")
    Then the user should see the text in the page    Raise finance queries to the organisation in this section
    When the user clicks the button/link    jQuery=.button:contains("Post a new query")
    Then the user should see the dropdown option selected    Viability    section

Queries section is linked to from the main finance check summary page
    [Documentation]    INFUND-4840
    [Tags]
    [Setup]  the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link    css=table.table-progress tr:nth-child(1) td:nth-child(6)
    Then the user should see the text in the page    Raise finance queries to the organisation in this section

Queries section contains finance contact name, email and telephone
    [Documentation]    INFUND-4840
    [Tags]
    When the user should see the element    jQuery=#content p:nth-of-type(1):contains("Sarah Peacock")
    And the user should see the element     jQuery=#content p:nth-of-type(1):contains("74373688727")
    And the user should see the element     jQuery=#content p:nth-of-type(1):contains(${successful_applicant_credentials["email"]})

Viability and eligibility sections both available
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Post a new query")
    Then the user should see the option in the drop-down menu    Viability    section
    And the user should see the option in the drop-down menu    Eligibility    section

Large pdf uploads not allowed
    [Documentation]    INFUND-4840
    [Tags]
    When the user uploads the file     name=attachment    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    [Teardown]    the user goes back to the previous page

Non pdf uploads not allowed
    [Documentation]    INFUND-4840
    [Tags]
    When the user uploads the file      name=attachment    ${text_file}
    Then the user should see the text in the page    ${wrong_filetype_validation_error}

Project finance user can upload a pdf file
    [Documentation]    INFUND-4840
    [Tags]
    Then the user uploads the file      name=attachment    ${valid_pdf}
    And the user should see the text in the page    ${valid_pdf}

Project finance user cannot add query for an organisation not part of the project
    [Documentation]  IFS-281, IFS-379
    [Tags]
    When the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/23/query/new-query    ${403_error_message}
    [Teardown]    the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/22/query/new-query

Project finance can remove the file
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link    name=removeAttachment
    Then the user should not see the text in the page    ${valid_pdf}
    And the user should not see an error in the page


Project finance can re-upload the file
    [Documentation]    INFUND-4840
    [Tags]
    When the user uploads the file    name=attachment    ${valid_pdf}
    Then the user should see the text in the page    ${valid_pdf}


Project finance user can view the file
    [Documentation]    INFUND-4840
    [Tags]
    Given the user should see the element    link=${valid_pdf} ${opens_in_new_window}
    And the file has been scanned for viruses
    When the user opens the link in new window   ${valid_pdf}
    Then the user goes back to the previous tab


Project finance user can upload more than one file
    [Documentation]    INFUND-4840
    [Tags]
    When the user uploads the file      name=attachment    ${valid_pdf}
    Then the user should see the element    jQuery=li:nth-of-type(2) a:contains("${valid_pdf}")

Project finance user can still view and delete both files
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link    jQuery=li:nth-of-type(1) a:contains("${valid_pdf} ${opens_in_new_window}")
    Then the user goes back to the previous tab
    And the user clicks the button/link   css=button[name='removeAttachment']:nth-last-of-type(1)
    When the user clicks the button/link    jQuery=li:nth-of-type(1) a:contains("${valid_pdf} ${opens_in_new_window}")
    Then the user goes back to the previous tab
    And the user clicks the button/link   css=button[name='removeAttachment']:nth-last-of-type(1)

Post new query server side validations
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Post Query")
    Then the user should see the element   jQuery=label[for="queryTitle"] .error-message:contains(This field cannot be left blank.)
    And the user should see the element    jQuery=label[for="query"] .error-message:contains(This field cannot be left blank.)

Post new query client side validations
    [Documentation]    INFUND-4840
    [Tags]
    When the user moves focus to the element    link=Sign out
    And the user enters text to a text field    id=queryTitle    an eligibility query's title
    Then the user should not see the element    jQuery=label[for="queryTitle"] .error-message:contains(This field cannot be left blank.)
    When the user enters text to a text field    css=.editor    this is some query text
    Then the user should not see the element    jQuery=label[for="query] .error-message:contains(This field cannot be left blank.)

Word count validations
    [Documentation]    INFUND-4840
    [Tags]
    When the user enters text to a text field    css=.editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    Then the user should see the text in the page    Maximum word count exceeded. Please reduce your word count to 400.
    When the user enters text to a text field    css=.editor    this is some query text
    Then the user should not see the text in the page    Maximum word count exceeded. Please reduce your word count to 400.

New query can be cancelled
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link    jQuery=a:contains("Cancel")
    Then the user should not see the text in the page    ${valid_pdf}
    And the user should not see the element    id=queryTitle
    And the user should not see the element    css=.editor

Query can be re-entered
    [Documentation]    INFUND-4840
    [Tags]  HappyPath
    When the user navigates to the page  ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/${EMPIRE_LTD_ID}/query?query_section=ELIGIBILITY
    And the user clicks the button/link    jQuery=.button:contains("Post a new query")
    And the user enters text to a text field    id=queryTitle    an eligibility query's title
    And the user enters text to a text field    css=.editor    this is some query text
    And the user uploads the file    name=attachment    ${valid_pdf}
    And the user uploads the file    name=attachment    ${valid_pdf}

New query can be posted
    [Documentation]    INFUND-4840 INFUND-9546
    [Tags]  HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Post Query")
    Then the user should not see the element  jQuery=.button:contains("Post Query")
    And the user should see the text in the page    Lee Bowman - Innovate UK (Finance team)
    And the user should see the element  css=#post-new-response

Query sections are no longer editable
    [Documentation]    INFUND-4840
    [Tags]
    When the user should not see the element    css=.editor

Queries raised column updates to 'awaiting response'
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link    link=Finance checks
    Then the user should see the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(6) a:contains("Awaiting response")

Finance contact receives an email when new query is posted
    [Documentation]    INFUND-4841
    [Tags]    Email
    Then the user reads his email    ${successful_applicant_credentials["email"]}    Query regarding your finances    We have raised a query around your project finances.

Project finance user can add another query
    [Documentation]    INFUND-4840
    [Tags]
    Given the user clicks the button/link    css=table.table-progress tr:nth-child(1) td:nth-child(6)
    When the user clicks the button/link    jQuery=.button:contains("Post a new query")
    And the user enters text to a text field    id=queryTitle    a viability query's title
    And the user selects the option from the drop-down menu    VIABILITY    id=section
    And the user enters text to a text field    css=.editor    another query body
    And the user clicks the button/link    jQuery=.button:contains("Post Query")
    Then the user should not see an error in the page

Queries show in reverse chronological order
    [Documentation]    INFUND-4840
    [Tags]
    Given the user should see the element   jQuery=#querySection
    And the user should see the element     jQuery=.queries-list .query:nth-of-type(1) h2:contains("a viability query's title")
    And the user should see the element     jQuery=.queries-list .query:nth-of-type(2) h2:contains("an eligibility query's title")

Project finance user can filter queries by Eligibility section
    [Documentation]  INFUND-4844
    [Tags]
    Given the user selects the option from the drop-down menu    Eligibility only    id=querySection
    Then the user should see the element       jQuery=.queries-list .query:nth-of-type(2) h2:contains("an eligibility query's title")
    And the user should see the element       jQuery=.queries-list .query:nth-of-type(2) h3:contains("Eligibility")
    And the user should not see the element    jQuery=.queries-list .query:nth-of-type(1) h2:contains("a viability query's title")
    And the user should not see the element    jQuery=.queries-list .query:nth-of-type(1) h3:contains("Viability")

Project finance user can filter queries by Viability section
    [Documentation]  INFUND-4844
    [Tags]
    Given the user selects the option from the drop-down menu    Viability only    id=querySection
    Then the user should see the element   jQuery=.queries-list .query:nth-of-type(1) h2:contains("a viability query's title")
    And the user should see the element    jQuery=.queries-list .query:nth-of-type(1) h3:contains("Viability")
    And the user should not see the element      jQuery=.queries-list .query:nth-of-type(2) h2:contains("an eligibility query's title")
    And the user should not see the element      jQuery=.queries-list .query:nth-of-type(2) h3:contains("Eligibility")

Project finance user can view all queries back
    [Documentation]  INFUND-4844
    [Tags]
    Given the user selects the option from the drop-down menu    All    id=querySection
    Then the user should see the element     jQuery=.queries-list .query:nth-of-type(1) h2:contains("a viability query's title")
    And the user should see the element    jQuery=.queries-list .query:nth-of-type(1) h3:contains("Viability")
    And the user should see the element      jQuery=.queries-list .query:nth-of-type(2) h2:contains("an eligibility query's title")
    And the user should see the element    jQuery=.queries-list .query:nth-of-type(2) h3:contains("Eligibility")

Finance contact can view query
    [Documentation]    INFUND-4843
    [Tags]
    Given log in as a different user        &{successful_applicant_credentials}
    When the user clicks the button/link    link=${FUNDERS_PANEL_APPLICATION_1_TITLE}
    And the user clicks the button/link    link=Finance checks
    Then the user should see the text in the page    an eligibility query's title
    And the user should see the text in the page    this is some query text

Finance contact can view the project finance user's uploads
    [Documentation]    INFUND-4843
    [Tags]
    When the user clicks the button/link    jQuery=li:nth-of-type(1) > a:contains("${valid_pdf} ${opens_in_new_window}")
    Then the user goes back to the previous tab
    When the user clicks the button/link    jQuery=li:nth-of-type(2) > a:contains("${valid_pdf} ${opens_in_new_window}")
    Then the user goes back to the previous tab

Queries show in reverse chronological order for finance contact
    [Documentation]    INFUND-4843
    [Tags]
    When the user should see the element    jQuery=h2:contains("an eligibility query's title")
    And the user should see the element    jQuery=h2:contains("a viability query's title")

Large pdf uploads not allowed for query response
    [Documentation]    INFUND-4843
    [Tags]
    Given the user clicks the button/link    jQuery=.button.button-secondary:eq(0)
    When the user uploads the file     name=attachment    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    [Teardown]    the user goes back to the previous page

Non pdf uploads not allowed for query response
    [Documentation]    INFUND-4843
    [Tags]
    When the user uploads the file      name=attachment    ${text_file}
    Then the user should see the text in the page    ${wrong_filetype_validation_error}

Finance contact can upload a pdf file
    [Documentation]    INFUND-4843
    [Tags]
    Then the user uploads the file      name=attachment   ${valid_pdf}
    And the user should see the text in the page    ${valid_pdf}

Finance contact can remove the file
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link    name=removeAttachment
    Then the user should not see the element    jQuery=form a:contains("${valid_pdf} ${opens_in_new_window}")
    And the user should not see an error in the page

Finance contact can re-upload the file
    [Documentation]    INFUND-4840
    [Tags]
    When the user uploads the file    name=attachment    ${valid_pdf}
    Then the user should see the element    jQuery=form a:contains("${valid_pdf} ${opens_in_new_window}")

Finance contact can view the file
    [Documentation]    INFUND-4843
    [Tags]
    Given the user should see the element    link=${valid_pdf} ${opens_in_new_window}
    And the file has been scanned for viruses
    When the user opens the link in new window   ${valid_pdf}
    Then the user goes back to the previous tab

Finance contact can upload more than one file
    [Documentation]    INFUND-4843
    [Tags]
    Then the user uploads the file      name=attachment    ${valid_pdf}
    And the user should see the element    jQuery=li:nth-of-type(2) > a:contains("${valid_pdf} ${opens_in_new_window}")

Finance contact can still view both files
    [Documentation]    INFUND-4843
    [Tags]
    When the user clicks the button/link    jQuery=li:nth-of-type(1) > a:contains("${valid_pdf}")
    Then the user should not see an error in the page
    And the user goes back to the previous tab
    When the user clicks the button/link    jQuery=li:nth-of-type(2) > a:contains("${valid_pdf}")
    Then the user should not see an error in the page
    And the user goes back to the previous tab

Response to query server side validations
    [Documentation]    INFUND-4843
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Post response")
    Then The user should see a field error  This field cannot be left blank.

Response to query client side validations
    [Documentation]    INFUND-4843
    [Tags]
    When the user enters text to a text field    css=.editor    this is some response text
    And the user moves focus to the element    jQuery=.button:contains("Post response")
    Then the user should not see the text in the page    This field cannot be left blank.

Word count validations for response
    [Documentation]    INFUND-4843
    When the user enters text to a text field    css=.editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    And the user moves focus to the element    jQuery=.button:contains("Post response")
    Then the user should see the text in the page    Maximum word count exceeded. Please reduce your word count to 400.
    And the user should see the text in the page    This field cannot contain more than 4,000 characters.
    When the user enters text to a text field    css=.editor    this is some response text
    Then the user should not see the text in the page    Maximum word count exceeded. Please reduce your word count to 400.
    And the user should not see the text in the page    This field cannot contain more than 4,000 characters.

Query response can be posted
    [Documentation]    INFUND-4843
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Post response")
    Then the user should not see the element   jQuery=.button:contains("Post response")

Query section now becomes read-only
    [Documentation]    INFUND-4843
    [Tags]
    When the user should not see the element    css=.editor

Respond to older query
    [Documentation]    INFUND-4843
    [Tags]
    Given the user clicks the button/link    jQuery=.button.button-secondary:eq(0)
    When the user enters text to a text field    css=.editor    this is some response text for other query
    When the user clicks the button/link    jQuery=.button:contains("Post response")
    When the user should not see the element    css=.editor

IFS Admin can see queries raised column updates to 'view'
    [Documentation]    INFUND-4843, IFS-603
    [Tags]
    Given log in as a different user    &{ifs_admin_user_credentials}
    When the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    And the user should see the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(6) a:contains("Awaiting response")

Project finance user can view the response
    [Documentation]    INFUND-4843
    [Tags]
    [Setup]    log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link    css=table.table-progress tr:nth-child(1) td:nth-child(6)
    Then the user should see the text in the page    this is some response text

Project finance user can view the finance contact's uploaded files
    [Documentation]    INFUND-4843
    [Tags]
    When the user clicks the button/link    jQuery=.panel li:nth-of-type(1) > a:contains("${valid_pdf} ${opens_in_new_window}")
    Then the user goes back to the previous tab
    When the user clicks the button/link    jQuery=.panel li:nth-of-type(2) > a:contains("${valid_pdf} ${opens_in_new_window}")
    Then the user goes back to the previous tab

Project finance user can continue the conversation
    [Documentation]    INFUND-7752
    [Tags]
    When the user clicks the button/link    jQuery=.button.button-secondary:eq(0)
    And the user enters text to a text field    css=.editor    this is a response to a response
    And the user clicks the button/link    jQuery=.button:contains("Post response")
    Then the user should not see an error in the page
    And the user should not see the element    css=.editor

Finance contact receives an email when a new response is posted
    [Documentation]    INFUND-7753
    [Tags]    Email
    Then the user reads his email    ${successful_applicant_credentials["email"]}    You have a reply to your query    We have replied to a query regarding your finances

Finance contact can view the new response
    [Documentation]    INFUND-7752
    [Tags]
    Given log in as a different user    &{successful_applicant_credentials}
    When the user clicks the button/link   jQuery=.projects-in-setup a:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    And the user clicks the button/link    link=Finance checks
    Then the user should see the text in the page    this is a response to a response

Link to notes from viability section
    [Documentation]    INFUND-4845
    [Tags]
    Given log in as a different user    &{internal_finance_credentials}
    When the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    And the user clicks the button/link    css=table.table-progress tr:nth-child(1) td:nth-child(2)
    And the user clicks the button/link    jQuery=.button:contains("Notes")
    Then the user should see the text in the page    Use this section to make notes related to the finance checks
    And the user should see the element    jQuery=.button:contains("Create a new note")

Link to notes from eligibility section
    [Documentation]    INFUND-4845
    [Tags]
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/${EMPIRE_LTD_ID}/eligibility
    And the user clicks the button/link    jQuery=.button:contains("Notes")
    Then the user should see the text in the page    Use this section to make notes related to the finance checks
    And the user should see the element    jQuery=.button:contains("Create a new note")

Link to notes from main finance checks summary page
    [Documentation]    INFUND-4845
    [Tags]
    When the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    And the user clicks the button/link    css=table.table-progress tr:nth-child(1) td:nth-child(7)
    Then the user should see the text in the page    Use this section to make notes related to the finance checks
    And the user should see the element    jQuery=.button:contains("Create a new note")

Large pdf uploads not allowed for notes
    [Documentation]    INFUND-4845
    [Tags]
    Given the user clicks the button/link    jQuery=.button:contains("Create a new note")
    When the user uploads the file     name=attachment    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    [Teardown]    the user goes back to the previous page

Non pdf uploads not allowed for notes
    [Documentation]    INFUND-4845
    [Tags]
    When the user uploads the file      name=attachment    ${text_file}
    Then the user should see the text in the page    ${wrong_filetype_validation_error}

Project finance can upload a pdf file to notes
    [Documentation]    INFUND-4845
    [Tags]
    Then the user uploads the file      name=attachment   ${valid_pdf}
    And the user should see the text in the page    ${valid_pdf}

Project finance can remove the file from notes
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link    name=removeAttachment
    Then the user should not see the element    jQuery=form a:contains("${valid_pdf} ${opens_in_new_window}")
    And the user should not see an error in the page

Project finance can re-upload the file to notes
    [Documentation]    INFUND-4845
    [Tags]
    When the user uploads the file    name=attachment    ${valid_pdf}
    Then the user should see the element    jQuery=form a:contains("${valid_pdf} ${opens_in_new_window}")

Project finance can view the file in notes
    [Documentation]    INFUND-4845
    [Tags]
    Given the user should see the element    link=${valid_pdf} ${opens_in_new_window}
    And the file has been scanned for viruses
    When The user opens the link in new window   ${valid_pdf}
    Then the user goes back to the previous tab
    And the user should see the element    jQuery=button:contains("Save note")

Project finance can upload more than one file to notes
    [Documentation]    INFUND-4845
    [Tags]
    Then the user uploads the file      name=attachment    ${valid_pdf}
    And the user should see the element    jQuery=form li:nth-of-type(2) > a:contains("${valid_pdf}")

Project finance can still view both files in notes
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link    jQuery=li:nth-of-type(1) > a:contains("${valid_pdf}")
    Then the user should not see an error in the page
    And the user goes back to the previous tab
    When the user clicks the button/link    jQuery=li:nth-of-type(2) > a:contains("${valid_pdf}")
    Then the user should not see an error in the page
    And the user goes back to the previous tab
    And the user clicks the button/link   css=button[name='removeAttachment']:nth-last-of-type(1)

Create new note server side validations
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Save note")
    Then the user should see the element   jQuery=label[for="noteTitle"] .error-message:contains(This field cannot be left blank.)
    And the user should see the element    jQuery=label[for="note"] .error-message:contains(This field cannot be left blank.)

Create new note client side validations
    [Documentation]    INFUND-4845
    [Tags]
    When the user moves focus to the element    link=Sign out
    And the user enters text to a text field    id=noteTitle    an eligibility query's title
    Then the user should not see the element    jQuery=label[for="noteTitle"] .error-message:contains(This field cannot be left blank.)
    When the user enters text to a text field    css=.editor    this is some note text
    Then the user should not see the element    jQuery=label[for="note"] .error-message:contains(This field cannot be left blank.)

Word count validations for notes
    [Documentation]    INFUND-4845
    [Tags]
    When the user enters text to a text field    css=.editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    Then the user should see the text in the page    Maximum word count exceeded. Please reduce your word count to 400.
    When the user enters text to a text field    css=.editor    this is some note text
    Then the user should not see the text in the page    Maximum word count exceeded. Please reduce your word count to 400.

New note can be cancelled
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link    jQuery=a:contains("Cancel")
    Then the user should not see the text in the page    ${valid_pdf}
    And the user should not see the element    id=noteTitle
    And the user should not see the element    css=.editor

Note can be re-entered
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Create a new note")
    And the user enters text to a text field    id=noteTitle    an eligibility query's title
    And the user enters text to a text field    css=.editor    this is some note text
    And the user uploads the file    name=attachment    ${valid_pdf}
    And the user uploads the file    name=attachment    ${valid_pdf}

New note can be posted
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Save note")
    Then the user should not see the element  jQuery=.button:contains("Save note")
    Then the user should see the text in the page    Lee Bowman - Innovate UK (Finance team)

Note sections are no longer editable
    [Documentation]    INFUND-4845
    [Tags]
    When the user should not see the element    css=.editor
    And the user should not see the element    id=noteTitle

Project finance user can comment on the note
    [Documentation]    INFUND-7756
    [Tags]
    When the user should see the text in the page    an eligibility query's title
    And the user should see the text in the page    this is some note text
    And the user should see the element    id=post-new-comment

Large pdf uploads not allowed for note comments
    [Documentation]    INFUND-7756
    [Tags]
    Given the user clicks the button/link    id=post-new-comment
    When the user uploads the file     name=attachment    ${too_large_pdf}
    Then the user should see the text in the page    ${too_large_pdf_validation_error}
    [Teardown]    the user goes back to the previous page

Non pdf uploads not allowed for note comments
    [Documentation]    INFUND-7756
    [Tags]
    When the user uploads the file      name=attachment    ${text_file}
    Then the user should see the text in the page    ${wrong_filetype_validation_error}

Project finance can upload a pdf file to note comments
    [Documentation]    INFUND-7756
    [Tags]
    Then the user uploads the file      name=attachment   ${valid_pdf}
    And the user should see the text in the page    ${valid_pdf}

Project finance can remove the file from note comments
    [Documentation]    INFUND-7756
    [Tags]
    When the user clicks the button/link    name=removeAttachment
    Then the user should not see the element    jQuery=form a:contains("${valid_pdf} ${opens_in_new_window}")
    And the user should not see an error in the page

Project finance can re-upload the file to note comments
    [Documentation]    INFUND-7756
    [Tags]
    When the user uploads the file    name=attachment    ${valid_pdf}
    Then the user should see the element    jQuery=form a:contains("${valid_pdf} ${opens_in_new_window}")

Project finance can view the file in note comments
    [Documentation]    INFUND-7756
    [Tags]
    Given the user should see the element    link=${valid_pdf} ${opens_in_new_window}
    And the file has been scanned for viruses
    When the user opens the link in new window   ${valid_pdf}
    And the user goes back to the previous tab
    And the user should see the element    jQuery=button:contains("Save comment")

Project finance can upload more than one file to note comments
    [Documentation]    INFUND-7756
    [Tags]
    Then the user uploads the file      name=attachment    ${valid_pdf}
    And the user should see the element    jQuery=form li:nth-of-type(2) > a:contains("${valid_pdf}")

Project finance can still view both files in note comments
    [Documentation]    INFUND-7756
    [Tags]
    When the user clicks the button/link    jQuery=form li:nth-of-type(1) > a:contains("${valid_pdf}")
    Then the user should not see an error in the page
    And the user goes back to the previous tab
    When the user clicks the button/link    jQuery=form li:nth-of-type(2) > a:contains("${valid_pdf}")
    Then the user should not see an error in the page
    And the user goes back to the previous tab
    And the user should see the element    jQuery=button:contains("Save comment")

Note comments server side validations
    [Documentation]    INFUND-7756
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Save comment")
    Then the user should see the element    jQuery=label[for="comment"] .error-message:contains("This field cannot be left blank.")

Note comments client side validations
    [Documentation]    INFUND-7756
    [Tags]
    When the user enters text to a text field    css=.editor    this is some comment text
    And the user moves focus to the element    jQuery=.button:contains("Save comment")
    Then the user should not see the element    jQuery=label[for="comment"] .error-message:contains("This field cannot be left blank.")

Word count validations for note comments
    [Documentation]    INFUND-7756
    When the user enters text to a text field    css=.editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    And the user moves focus to the element    jQuery=.button:contains("Save comment")
    Then the user should see the text in the page    Maximum word count exceeded. Please reduce your word count to 400.    # subject to change of course
    And the user should see the text in the page    This field cannot contain more than 4,000 characters.
    When the user enters text to a text field    css=.editor    this is some comment text
    Then the user should not see the text in the page    Maximum word count exceeded. Please reduce your word count to 400.    # subject to change of course
    And the user should not see the text in the page    This field cannot contain more than 4,000 characters.

Note comment can be posted
    [Documentation]    INFUND-7756
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Save comment")
    Then the user should not see the element   jQuery=.button:contains("Save comment")

Note comment section now becomes read-only
    [Documentation]    INFUND-7756
    [Tags]
    When the user should not see the element    css=.editor
