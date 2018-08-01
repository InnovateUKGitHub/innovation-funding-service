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
...
...               IFS-1882 Project Setup internal project dashboard: Query responses
...
...               IFS-1987 Queries: close a conversation. See also IFS-2638, IFS-2639
...
...               IFS-2746 External queries redesign: query statuses and banner messages
...
...               IFS-3559 Email subject for new finance queries to include competition name and application ID
Suite Setup       Custom Suite Setup
Suite Teardown    Close browser and delete emails
Force Tags        Project Setup
Resource          PS_Common.robot

# This suite is using Competition: Internet of Things
# and Application: Sensing & Control network using the lighting infrastructure

*** Variables ***
${opens_in_new_window}    (opens in a new window)

# TODO actually check the downloading of the pdf files. In this suite is only checked that the link to the file is visible to the user.
# But no actual download is happening. This suite used to click all the links and in that way increasing the amount of browser tabs open. This is now removed.
# TODO IFS-2716

*** Test Cases ***
Queries section is linked from eligibility and this selects eligibility on the query dropdown
    [Documentation]    INFUND-4840
    [Tags]  HappyPath
    Given Logging in and Error Checking   &{internal_finance_credentials}
    When the user navigates to the page   ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check/organisation/${Dreambit_Id}/eligibility
    And the user clicks the button/link   jQuery=.govuk-button:contains("Queries")
    Then the user should see the element  jQuery=h2:contains("Queries")
    When the user clicks the button/link  jQuery=.govuk-button:contains("Post a new query")
    Then the user should see the dropdown option selected  Eligibility  section

Queries section is linked from viability and this selects viability on the query dropdown
    [Documentation]    INFUND-4840
    [Tags]
    [Setup]  the user navigates to the page  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    Given the user clicks the button/link    jQuery=table.table-progress th:contains("Lead") + td a
    When the user clicks the button/link     jQuery=.govuk-button:contains("Queries")
    And the user clicks the button/link      jQuery=.govuk-button:contains("Post a new query")
    Then the user should see the dropdown option selected    Viability    section

Queries section is linked to from the main finance check summary page
    [Documentation]    INFUND-4840
    [Tags]
    [Setup]  the user navigates to the page  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    When the user clicks the button/link     css=table.table-progress tr:nth-child(1) td:nth-child(6)
    Then the user should see the element     jQuery=h2:contains("Queries")

Queries section contains finance contact name, email and telephone
    [Documentation]    INFUND-4840
    [Tags]
    When the user should see the element    jQuery=#main-content p:nth-of-type(1):contains("Becky Mason")
    And the user should see the element     jQuery=#main-content p:nth-of-type(1):contains("3578109078")
    And the user should see the element     jQuery=#main-content p:nth-of-type(1):contains(${PublicSector_lead_applicant_credentials["email"]})

Viability and eligibility sections both available
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link    jQuery=.govuk-button:contains("Post a new query")
    Then the user should see the option in the drop-down menu    Viability    section
    And the user should see the option in the drop-down menu    Eligibility    section

Project finance user can upload a pdf file
    [Documentation]    INFUND-4840
    [Tags]
    When the user uploads the file        name=attachment  ${valid_pdf}
    Then the user should see the element  jQuery=h3:contains("Supporting documentation") + ul:contains("testing.pdf") .button-clear:contains("Remove")

Project finance can remove the file
    [Documentation]    INFUND-4840
    [Tags]
    Given the user navigates to the page  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check/organisation/${Dreambit_Id}/query/new-query
    When the user clicks the button/link  name=removeAttachment
    Then the user should not see the text in the page    ${valid_pdf}
    And the user should not see an error in the page

Project finance user can upload more than one file and remove it
    [Documentation]    INFUND-4840
    [Tags]
    When the user uploads the file      name=attachment    ${valid_pdf}
    Then the user clicks the button/link  jQuery=h3:contains("Supporting documentation") ~ ul:contains("testing.pdf") .button-clear:contains("Remove")

Post new query server side validations
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link     jQuery=.govuk-button:contains("Post query")
    Then the user should see the element     jQuery=label[for="queryTitle"] .govuk-error-message:contains(This field cannot be left blank.)
    And the user should see the element      jQuery=label[for="query"] .govuk-error-message:contains(This field cannot be left blank.)
    And the user should see a summary error  This field cannot be left blank.

Post new query client side validations
    [Documentation]    INFUND-4840
    [Tags]
    When the user moves focus to the element    link=Sign out
    And the user enters text to a text field    id=queryTitle    an eligibility query's title
    Then the user should not see the element    jQuery=label[for="queryTitle"] .govuk-error-message:contains(This field cannot be left blank.)
    When the user enters text to a text field    css=.editor    this is some query text
    Then the user should not see the element    jQuery=label[for="query] .govuk-error-message:contains(This field cannot be left blank.)

Word count validations
    [Documentation]    INFUND-4840
    [Tags]
    When the user enters text to a text field  css=.editor  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    Then the user should see a field error     Maximum word count exceeded. Please reduce your word count to 400.
    When the user enters text to a text field  css=.editor  this is some query text
    Then the user should not see the element   jQuery=.govuk-error-message:contains("Maximum word count exceeded.")

New query can be cancelled
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link    jQuery=a:contains("Cancel")
    Then the user should not see the element    id=queryTitle
    And the user should not see the element    css=.editor

Query can be re-entered (Eligibility)
    [Documentation]    INFUND-4840
    [Tags]  HappyPath
    When the user navigates to the page  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check/organisation/${Dreambit_Id}/query
    And the user clicks the button/link    jQuery=.govuk-button:contains("Post a new query")
    And the user enters text to a text field    id=queryTitle    an eligibility query's title
    And the user enters text to a text field    css=.editor    this is some query text
    And the user uploads the file    name=attachment    ${valid_pdf}
    And the user uploads the file    name=attachment    ${valid_pdf}

New query can be posted
    [Documentation]    INFUND-4840 INFUND-9546
    [Tags]  HappyPath
    When the user clicks the button/link      jQuery=.govuk-button:contains("Post query")
    Then the user should not see the element  jQuery=.govuk-button:contains("Post query")
    When the user expands the section         an eligibility query's title
    Then the user should see the element      jQuery=.govuk-heading-s:contains("Lee Bowman - Innovate UK (Finance team)")
    When the user should see the element      jQuery=.govuk-heading-s:contains("${today}")
    Then the user should see the element      css=#post-new-response  # Respond button

Query Section dropdown filters the queries displayed
    [Documentation]    INFUND-4840 INFUND-4844
    [Tags]
    When the user selects the option from the drop-down menu  viability  querySection
    Then the user should not see the element  css=#post-new-response
    # that means that the eligibility queries are not visible or any other.
    # Tried to catch with .query.eligibility-section[aria=hidden="true"], but without success

Finance contact receives an email when new query is posted and can see a pending query
    [Documentation]  INFUND-4841 IFS-2746 IFS-3559
    [Tags]  Email
    [Setup]  log in as a different user     &{PublicSector_lead_applicant_credentials}
    Given the user reads his email          ${PublicSector_lead_applicant_credentials["email"]}  ${PS_EF_Competition_Name}: Query regarding your finances for project ${Queries_Application_No}  We have raised a query around your project finances.
    When the user navigates to the page     ${server}/project-setup/project/${Queries_Application_Project}
    Then the user should see the element    css=.status-warning  #Pending query
    And the user clicks the button/link     link=Finance checks
    Then the user should see the element    jQuery=#title-query-1:contains("Pending query")

Project finance user can add another query while he is awaiting for response
    [Documentation]    INFUND-4840
    [Tags]
    [Setup]  log in as a different user       &{internal_finance_credentials}
    Given the user navigates to the page      ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    Then the user clicks the button/link      jQuery=th:contains("${Dreambit_Name}") ~ td:contains("View")
    When the user clicks the button/link      css=a[id="post-new-query"]
    And the user enters text to a text field  id=queryTitle  a viability query's title
    And the user selects the option from the drop-down menu  VIABILITY    id=section
    And the user enters text to a text field  css=.editor    another query body
    And the user clicks the button/link       css=.govuk-grid-column-one-half button[type="submit"]  # Post query
    Then the user should not see an error in the page

Queries show in reverse chronological order
    [Documentation]    INFUND-4840 INFUND-4844
    [Tags]
    Given the user selects the option from the drop-down menu  all  querySection
    When the user should see the element  jQuery=h2:nth-of-type(1):contains("a viability query's title")
    Then the user should see the element  jQuery=h2:nth-of-type(2):contains("an eligibility query's title")
    # Query responses tab
    When the user navigates to the page    ${server}/project-setup-management/competition/${Queries_Application_Project}/status/queries
    Then the user should see the element   jQuery=p:contains("There are no outstanding queries.")

Applicant - Finance contact can view query
    [Documentation]    INFUND-4843
    [Tags]
    Given log in as a different user      &{PublicSector_lead_applicant_credentials}
    When the user navigates to the page   ${server}/project-setup/project/${Queries_Application_Project}/finance-checks
    Then the user should see the element  jQuery=h2:contains("an eligibility query's title")
    And the user should see the element   jQuery=h2:contains("a viability query's title")

Applicant - Finance contact can view the project finance user's uploads
    [Documentation]    INFUND-4843
    [Tags]
    When the user downloads the file  ${PublicSector_lead_applicant_credentials["email"]}  ${server}/project-setup/project/${Queries_Application_Project}/finance-checks/attachment/4  ${DOWNLOAD_FOLDER}/${valid_pdf}
    Then remove the file from the operating system  testing.pdf

Applicant - Response to query server side validations
    [Documentation]  INFUND-4843 IFS-2746
    [Tags]
    Given the user should see the element   jQuery=#title-query-2:contains("Pending query")
    And the user should see the element     jQuery=h2:contains("an eligibility") .section-incomplete
    Then the user expands the section       an eligibility query's title
    When the user clicks the button/link    jQuery=h2:contains("eligibility") + [id^="finance-checks-query"] a[id^="post-new-response"]
    And the user clicks the button/link     jQuery=.govuk-button:contains("Post response")
    Then the user should see a field error  This field cannot be left blank.
#    TODO commmented due to IFS-2622
#    And the user should see a summary error            This field cannot be left blank.

Applicant - Response to query client side validations
    [Documentation]    INFUND-4843
    [Tags]
    When the user enters text to a text field          css=.editor  this is some response text
    And the user moves focus to the element            jQuery=.govuk-button:contains("Post response")
    Then the user should not see the text in the page  This field cannot be left blank.
    When the user uploads the file                     name=attachment  ${valid_pdf}
    Then the user should see the element               jQuery=a:contains("testing.pdf") + button:contains("Remove")

Applicant - Word count validations for response
    [Documentation]    INFUND-4843
    When the user enters text to a text field  css=.editor  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    And the user moves focus to the element    jQuery=.govuk-button:contains("Post response")
    Then the user should see a field error     Maximum word count exceeded. Please reduce your word count to 400.
    And the user should see a field error      This field cannot contain more than 4,000 characters.
    When the user enters text to a text field  css=.editor  This is some response text
    Then the user should not see an error in the page

Applicant - Query response can be posted
    [Documentation]    INFUND-4843
    [Tags]
    When the user clicks the button/link      jQuery=.govuk-button:contains("Post response")
    Then the user should not see the element  jQuery=.govuk-button:contains("Post response")
    And the user should see the element       jQuery=h2:contains("an eligibility") .section-awaiting
    And the user should see the element       jQuery=.govuk-heading-s:contains("Becky Mason") small:contains("${today}")
    And the user should see the element       jQuery=.govuk-heading-s:contains("Becky Mason") ~ .govuk-heading-s:contains("Supporting documentation")

Applicant - Respond to older query
    [Documentation]    INFUND-4843
    [Tags]
    Given the user clicks the button/link      jQuery=h2:contains("eligibility") + [id^="finance-checks-query"] a[id^="post-new-response"]
    When the user enters text to a text field  css=.editor    one more response to the eligibility query
    Then the user clicks the button/link       jQuery=.govuk-button:contains("Post response")
    And the user should see the element        jQuery=.panel + .panel:contains("Becky ")  #is the 2nd response

Applicant - Repond to Viability query
    [Documentation]  IFS-2746
    [Tags]
    Given the user expands the section        a viability query's title
    When the user clicks the button/link      jQuery=h2:contains("viability") + [id^="finance-checks-query"] a:contains("Respond")
    And the user enters text to a text field  css=.editor  This is applicant's response to the Viability query.
    And the user clicks the button/link       jQuery=.govuk-button:contains("Post response")
    Then the user should see the element      jQuery=h2:contains("viability") .section-awaiting

IFS Admin can see queries raised column updates to 'view'
    [Documentation]    INFUND-4843, IFS-603
    [Tags]  #Administrator
    Given log in as a different user       &{ifs_admin_user_credentials}
    When the user navigates to the page    ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    And the user should see the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(6) a:contains("View")

IFS Admin can see applicant's response flagged in Query responses tab and mark discussion as Resolved
    [Documentation]  IFS-1882 IFS-1987
    [Tags]  #Administrator
    # Query responses tab
    Given the user navigates to the page  ${server}/project-setup-management/competition/${Queries_Competition_Id}/status/queries
    When the user clicks the button/link  link=Query responses (1)
    Then the user should see the element  jQuery=td:contains("${Queries_Application_Title}") + td:contains("${Dreambit_Name}")
    When the user clicks the button/link  link=${Dreambit_Name}
    Then the user should see the element  jQuery=h1:contains("${Dreambit_Name}")
    And the user should see the element   link=Post a new query
    When the user expands the section     a viability query's title
    Then the query conversation can be resolved by  Arden Pimenta  viability
    [Teardown]  the user collapses the section      a viability query's title

Project finance user can view the response and uploaded files
    [Documentation]    INFUND-4843
    [Tags]
    [Setup]  log in as a different user   &{internal_finance_credentials}
    Given the user navigates to the page  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    When the user clicks the button/link  css=table.table-progress tr:nth-child(1) td:nth-child(6)  # View
    And the user expands the section      an eligibility query's title
    Then the user should see the element  jQuery=.govuk-heading-s:contains("Becky") + p:contains("This is some response text")
    And the user should see the element   jQuery=.panel li:nth-of-type(1) a:contains("${valid_pdf}")

Project finance user can continue the conversation
    [Documentation]    INFUND-7752
    [Tags]
    When the user clicks the button/link      jQuery=h2:contains("an eligibility query's title") + [id^="finance-checks-internal-query"] a:contains("Respond")
    And the user enters text to a text field  css=.editor  This is a response to a response
    And the user clicks the button/link       jQuery=.govuk-button:contains("Post response")

Finance contact receives an email when a new response is posted
    [Documentation]    INFUND-7753 IFS-3559
    [Tags]    Email
    Given the user reads his email    ${PublicSector_lead_applicant_credentials["email"]}  ${Queries_Competition_Name}: You have a reply to your query for project ${Queries_Application_No}  We have replied to a query regarding your finances

Finance contact can view the new response
    [Documentation]    INFUND-7752
    [Tags]
    Given log in as a different user      &{PublicSector_lead_applicant_credentials}
    When the user clicks the button/link  jQuery=.projects-in-setup a:contains("${Queries_Application_Title}")
    And the user clicks the button/link   link=Finance checks
    Then the user should see the element  jQuery=.govuk-heading-s:contains("Finance team") + .wysiwyg-styles:contains("This is a response to a response")

Project Finance user is able to mark a query discussion as complete
    [Documentation]  IFS-1987
    [Tags]  HappyPath
    Given log in as a different user     &{internal_finance_credentials}
    When the user navigates to the page  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check/organisation/${Dreambit_Id}/query
    And the user expands the section     an eligibility query's title
    Then the query conversation can be resolved by  Lee Bowman  eligibility
    And the user should not see the element         jQuery=h2:contains("an eligibility query's title") + [id^="finance-checks-internal-query"] a:contains("Respond")
    [Teardown]  the user collapses the section      an eligibility query's title

Applicant can see the the queries resolved
    [Documentation]  IFS-1987 IFS-2746
    [Tags]
    Given log in as a different user      &{PublicSector_lead_applicant_credentials}
    When the user navigates to the page   ${server}/project-setup/project/${Queries_Application_Project}/finance-checks
    Then the user should see the element  jQuery=h2:contains("an eligibility query's title") .section-complete
    And the user should see the element   jQuery=h2:contains("a viability query's title") .section-complete
    And the user should not be able to respond to resolved queries

Link to notes from viability section
    [Documentation]    INFUND-4845
    [Tags]
    Given log in as a different user      &{internal_finance_credentials}
    When the user navigates to the page   ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    And the user clicks the button/link   css=table.table-progress tr:nth-child(1) td:nth-child(2)
    And the user clicks the button/link   jQuery=.govuk-button:contains("Notes")
    Then the user should see the element  jQuery=h2:contains("Review notes")
    And the user should see the element   jQuery=.govuk-button:contains("Create a new note")

Link to notes from eligibility section
    [Documentation]    INFUND-4845
    [Tags]
    Given the user navigates to the page  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check/organisation/${Dreambit_Id}/eligibility
    And the user clicks the button/link   jQuery=.govuk-button:contains("Notes")
    Then the user should see the element  jQuery=.govuk-button:contains("Create a new note")

Link to notes from main finance checks summary page
    [Documentation]    INFUND-4845
    [Tags]
    When the user navigates to the page   ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    And the user clicks the button/link   css=table.table-progress tr:nth-child(1) td:nth-child(7)  # View Notes of Empire Ltd

Project finance can upload a pdf file to notes
    [Documentation]    INFUND-4845
    [Tags]
    Given the user clicks the button/link  jQuery=.govuk-button:contains("Create a new note")
    When the user uploads the file         name=attachment  ${valid_pdf}
    Then the user should see the element   jQuery=h2:contains("Supporting documentation") + ul:contains("${valid_pdf} ${opens_in_new_window}")

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
    Given the user should see the element  link=${valid_pdf} ${opens_in_new_window}
    Then the user should see the element   jQuery=button:contains("Save note")

Project finance can upload more than one file to notes
    [Documentation]    INFUND-4845
    [Tags]
    When the user uploads the file        name=attachment  ${valid_pdf}
    Then the user should see the element  jQuery=form li:nth-of-type(2) > a:contains("${valid_pdf}")

Project finance can still view both files in notes
    [Documentation]    INFUND-4845
    [Tags]
    When the user should see the element  jQuery=li:nth-of-type(1) > a:contains("${valid_pdf}")
    Then the user should see the element  jQuery=li:nth-of-type(2) > a:contains("${valid_pdf}")
    And the user clicks the button/link   css=button[name='removeAttachment']:nth-last-of-type(1)

Create new note server side validations
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link    jQuery=.govuk-button:contains("Save note")
    Then the user should see the element   jQuery=label[for="noteTitle"] .govuk-error-message:contains(This field cannot be left blank.)
    And the user should see the element    jQuery=label[for="note"] .govuk-error-message:contains(This field cannot be left blank.)

Create new note client side validations
    [Documentation]    INFUND-4845
    [Tags]
    When the user moves focus to the element    link=Sign out
    And the user enters text to a text field    id=noteTitle    an eligibility query's title
    Then the user should not see the element    jQuery=label[for="noteTitle"] .govuk-error-message:contains(This field cannot be left blank.)
    When the user enters text to a text field   css=.editor    this is some note text
    Then the user should not see the element    jQuery=label[for="note"] .govuk-error-message:contains(This field cannot be left blank.)

Word count validations for notes
    [Documentation]    INFUND-4845
    [Tags]
    When the user enters text to a text field  css=.editor    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    Then the user should see a field error     Maximum word count exceeded. Please reduce your word count to 400.
    When the user enters text to a text field  css=.editor    this is some note text
    Then the user should not see the element   jQuery=.govuk-error-message:contains("Maximum word count exceeded.")

New note can be cancelled
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link      jQuery=a:contains("Cancel")
    Then the user should not see the element  id=noteTitle
    And the user should not see the element   css=.editor

Note can be re-entered
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link    jQuery=.govuk-button:contains("Create a new note")
    And the user enters text to a text field    id=noteTitle    an eligibility query's title
    And the user enters text to a text field    css=.editor    this is some note text
    And the user uploads the file    name=attachment    ${valid_pdf}
    And the user uploads the file    name=attachment    ${valid_pdf}

New note can be posted
    [Documentation]    INFUND-4845
    [Tags]
    When the user clicks the button/link    jQuery=.govuk-button:contains("Save note")
    Then the user should not see the element  jQuery=.govuk-button:contains("Save note")
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
    When the file has been scanned for viruses
    And the user should see the element    jQuery=button:contains("Save comment")

Project finance can upload more than one file to note comments
    [Documentation]    INFUND-7756
    [Tags]
    Then the user uploads the file      name=attachment    ${valid_pdf}
    And the user should see the element    jQuery=form li:nth-of-type(2) > a:contains("${valid_pdf}")

Project finance can still view both files in note comments
    [Documentation]    INFUND-7756
    [Tags]
    When the user should see the element  jQuery=form li:nth-of-type(1) > a:contains("${valid_pdf}")
    Then the user should see the element  jQuery=form li:nth-of-type(2) > a:contains("${valid_pdf}")
    And the user should see the element   jQuery=button:contains("Save comment")

Note comments server side validations
    [Documentation]    INFUND-7756
    [Tags]
    When the user clicks the button/link    jQuery=.govuk-button:contains("Save comment")
    Then the user should see the element    jQuery=label[for="comment"] .govuk-error-message:contains("This field cannot be left blank.")

Note comments client side validations
    [Documentation]    INFUND-7756
    [Tags]
    When the user enters text to a text field    css=.editor  this is some comment text
    And the user moves focus to the element    jQuery=.govuk-button:contains("Save comment")
    Then the user should not see the element    jQuery=label[for="comment"] .govuk-error-message:contains("This field cannot be left blank.")

Word count validations for note comments
    [Documentation]    INFUND-7756
    When the user enters text to a text field  css=.editor  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    And the user moves focus to the element    jQuery=.govuk-button:contains("Save comment")
    Then the user should see a field error     Maximum word count exceeded. Please reduce your word count to 400.
    And the user should see a field error      This field cannot contain more than 4,000 characters.
    When the user enters text to a text field  css=.editor  this is some comment text
    Then the user should not see the element   jQuery=.govuk-error-message:contains("4,000")

Note comment can be posted
    [Documentation]    INFUND-7756
    [Tags]
    When the user clicks the button/link    jQuery=.govuk-button:contains("Save comment")
    Then the user should not see the element   jQuery=.govuk-button:contains("Save comment")


*** Keywords ***
Custom Suite Setup
    ${today} =  get today
    set suite variable  ${today}
    The guest user opens the browser

The query conversation can be resolved by
    [Arguments]  ${user}  ${section}
    the user clicks the button/link  jQuery=h2:contains("${section}") + [id^="finance-checks-internal-query"] a:contains("Mark as resolved")
    the user clicks the button/link  css=button[name="markAsResolved"]  # Submit
    the user should see the element  jQuery=h2:contains("${section}") .yes  # Resolved green check
    the user should see the element  jQuery=.message-alert:contains("${user} on")
    the user should see the element  jQuery=.message-alert:contains("${today}")

the user should not be able to respond to resolved queries
    the user should not see the element  jQuery=h2:contains("eligibility") + [id^="finance-checks-query"] a[id^="post-new-response"]
    the user should not see the element  jQuery=h2:contains("viability") + [id^="finance-checks-query"] a[id^="post-new-response"]