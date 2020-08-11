*** Settings ***
Documentation     INFUND-4840 As a project finance team member I want to be able to post a query in the finance checks section so that the relevant finance contact can be given the opportunity to provide further details
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
...
...               IFS-7215 Project finance user and lead applicant can upload multiple documents of different file types as a part of finance queries and response to it.
Suite Setup       Custom Suite Setup
Suite Teardown    Close browser and delete emails
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${dreambit_finance_checks}     ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check/organisation/${Dreambit_Id}

*** Test Cases ***
Queries section is linked from eligibility and this selects eligibility on the query dropdown
    [Documentation]    INFUND-4840
    [Tags]  HappyPath
    [Setup]  Logging in and Error Checking   &{internal_finance_credentials}
    Given the user navigates to queries page
    When the user clicks the button/link     jQuery = .govuk-button:contains("Post a new query")
    Then the user should see the dropdown option selected  Eligibility   id = section

Queries section is linked from viability and this selects viability on the query dropdown
    [Documentation]    INFUND-4840
    [Tags]  HappyPath
    [Setup]  the user navigates to the page  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    Given the user clicks the button/link    jQuery = table.table-progress th:contains("${Dreambit_Name}") + td a
    When the user clicks the button/link     jQuery = a:contains("Queries")
    And the user clicks the button/link      jQuery = .govuk-button:contains("Post a new query")
    Then the user should see the dropdown option selected    Viability     id = section

Queries section is linked to from the main finance check summary page
    [Documentation]    INFUND-4840
    [Tags]  HappyPath
    [Setup]  the user navigates to the page  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    When the user clicks the button/link     css = table.table-progress tr:nth-child(1) td:nth-child(6)
    Then the user should see the element     jQuery = h2:contains("Queries")
    And queries section contains finance contact name, email and telephone

Viability and eligibility sections both available
    [Documentation]    INFUND-4840
    [Tags]  HappyPath
    Given the user clicks the button/link                       jQuery = .govuk-button:contains("Post a new query")
    Then the user should see the option in the drop-down menu   Viability      id = section
    And the user should see the option in the drop-down menu    Eligibility    id = section

Project finance user can upload a pdf file and remove it
    [Documentation]    INFUND-4840, IFS-7215
    [Tags]  HappyPath
    Given the user uploads the file           name = attachment  ${5mb_pdf}
    Then the user should see the element      jQuery = a:contains("${5mb_pdf}")+ .button-clear:contains("Remove")
    And the user can remove an attachment     ${5mb_pdf}

Project finance user can upload a spreadsheet(.ods) file and remove it
    [Documentation]    IFS-7215
    [Tags]  HappyPath
    Given the user uploads the file           name = attachment  ${ods_file}
    Then the user should see the element      jQuery = a:contains("${ods_file}")+ .button-clear:contains("Remove")
    And the user can remove an attachment     ${ods_file}

Project finance user can upload a text document(.odt) file and remove it
    [Documentation]    IFS-7215
    [Tags]  HappyPath
    Given the user uploads the file           name = attachment  ${valid_odt}
    Then the user should see the element      jQuery = a:contains("${valid_odt}")+ .button-clear:contains("Remove")
    And the user can remove an attachment     ${valid_odt}

Project finance user cannot upload a document type that is not allowed
    [Documentation]    IFS-7215
    [Tags]  HappyPath
    Given the user uploads the file            name = attachment  ${text_file}
    Then the user should see a field error     ${finance_query_notes_filetype_error}
    And the user clicks the button/link        jQuery = button:contains("Remove")

Project finance user can upload more than one file and remove them
    [Documentation]    INFUND-4840, IFS-7215
    [Tags]
    Given the user uploads multiple file types as attachment and removes them    ${valid_pdf}  ${ods_file}  ${valid_odt}
    Then the user should not see an error in the page

Post new query client and server side validations
    [Documentation]    INFUND-4840
    [Tags]
    Given the user should see post a new query client side validations
    Then the user should see post a new query sever side validations

Word count validations
    [Documentation]    INFUND-4840
    [Tags]
    When the user enters text to a text field  css = .editor  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    Then the user should see a field error     Maximum word count exceeded. Please reduce your word count to 400.
    When the user enters text to a text field  css = .editor  this is some query text
    Then the user should not see the element   jQuery = .govuk-error-message:contains("Maximum word count exceeded.")

New query can be cancelled
    [Documentation]    INFUND-4840
    [Tags]
    When the user clicks the button/link        jQuery = a:contains("Cancel")
    Then the user should not see the element    id = queryTitle
    And the user should not see the element     css = .editor

Query can be re-entered (Eligibility)
    [Documentation]    INFUND-4840, IFS-7215
    [Tags]  HappyPath
    Given the user navigates to the page         ${dreambit_finance_checks}/query
    When the user clicks the button/link         jQuery = .govuk-button:contains("Post a new query")
    Then the user enters a new query details     ${valid_pdf}  ${ods_file}  ${valid_odt}

New query can be posted
    [Documentation]    INFUND-4840 INFUND-9546
    [Tags]  HappyPath
    Given the user clicks the button/link      jQuery = .govuk-button:contains("Post query")
    And the user should not see the element    jQuery = .govuk-button:contains("Post query")
    Then the user should see submitted query details

Query Section dropdown filters the queries displayed
    [Documentation]    INFUND-4840 INFUND-4844
    [Tags]
    When the user selects the option from the drop-down menu  Viability only  id = querySection
    Then the user should not see the element  css = #post-new-response
    # that means that the eligibility queries are not visible or any other.
    # Tried to catch with .query.eligibility-section[aria = hidden = "true"], but without success

Finance contact receives an email when new query is posted and can see a pending query
    [Documentation]  INFUND-4841 IFS-2746 IFS-3559
    [Tags]  HappyPath
    [Setup]  log in as a different user     &{PublicSector_lead_applicant_credentials}
    Given the user reads his email          ${PublicSector_lead_applicant_credentials["email"]}  ${PS_Competition_Name}: Query regarding your project finances for project ${Queries_Application_No}  We have raised a query around your project finances.
    When the user navigates to the page     ${server}/project-setup/project/${Queries_Application_Project}
    Then the user should see the element    css = .status-warning  #Pending query
    And the user clicks the button/link     link = Finance checks
    Then the user should see the element    jQuery = button:contains("an eligibility query's title") .section-status

Project finance user can add another query while he is awaiting for response
    [Documentation]    INFUND-4840
    [Tags]  HappyPath
    [Setup]  log in as a different user       &{internal_finance_credentials}
    Given the user navigates to the page      ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    Then the project finance user post another new query

Queries show in reverse chronological order
    [Documentation]    INFUND-4840 INFUND-4844
    [Tags]
    Given the user selects the option from the drop-down menu  All  id = querySection
    Then the user should see list of posted queries

Applicant - Finance contact can view query and download attachments
    [Documentation]    INFUND-4843 IFS-7215
    [Tags]  HappyPath
    Given log in as a different user                 &{PublicSector_lead_applicant_credentials}
    When the user navigates to the page              ${server}/project-setup/project/${Queries_Application_Project}/finance-checks
    Then The user clicks the button/link             jQuery = h2:contains("an eligibility query's title")
    And the user should see the element              jQuery = h2:contains("a viability query's title")
    And the user should see all the attachments
    And open pdf link                                jQuery = a:contains("${valid_pdf}")
    And the user is able to download attachments     ${ods_file}  ${valid_odt}

Applicant - Response to query validations
    [Documentation]  INFUND-4843 IFS-2746
    [Tags]
    Given the user should see the element   jQuery = button:contains("an eligibility query's title"):contains("Pending query")
    And the user should see the element     jQuery = h2:contains("an eligibility") .section-incomplete
    Then the user should see the response to query client side validations
    And the user should see the response to query server side validation
    And the user should see word count validations

Applicant - Query response can be posted
    [Documentation]    INFUND-4843
    [Tags]
    When the user clicks the button/link      jQuery = .govuk-button:contains("Post response")
    Then the user should not see the element  jQuery = .govuk-button:contains("Post response")
    And the user should see the element       jQuery = h2:contains("an eligibility") .section-awaiting
    And the user should see the element       jQuery = .govuk-heading-s:contains("Becky Mason") small:contains("${today}")
    And the user should see the element       jQuery = .govuk-heading-s:contains("Becky Mason") ~ .govuk-heading-s:contains("Supporting documentation")

Applicant - Respond to older query and cannot upload any file other than allowed file types to the response
    [Documentation]    IFS-7215
    [Tags]
    Given the user clicks the button/link      jQuery = #accordion-awaiting-queries-content-1 a:contains("Respond")   #an eligibility query response
    When the user uploads the file             name = attachment    ${text_file}
    Then the user should see a field error     ${applicant_query_response_filetype_error}
    And the user clicks the button/link        jQuery = button:contains("Remove")

Applicant - Respond to older query and upload files(.xls, .pdf and .docx) to the response
    [Documentation]    INFUND-4843, IFS-7215
    [Tags]
    Given the user enters a query response details    ${valid_pdf}  ${valid_docx}  ${excel_file}
    When the user clicks the button/link              jQuery = .govuk-button:contains("Post response")
    And the user should see the element               jQuery = .panel + .panel:contains("Becky ")  #is the 2nd response

Applicant - Repond to Viability query
    [Documentation]  IFS-2746
    [Tags]
    Given the user expands the section        a viability query's title
    When the user clicks the button/link      jQuery = #accordion-pending-queries-content-1 a:contains("Respond")   #a viability query response
    And the user enters text to a text field  css = .editor  This is applicant's response to the Viability query.
    And the user clicks the button/link       jQuery = .govuk-button:contains("Post response")
    Then the user should see the element      jQuery = h2:contains("viability") .section-awaiting

IFS Admin can see queries raised column updates to 'view'
    [Documentation]    INFUND-4843, IFS-603
    [Tags]
    Given log in as a different user       &{ifs_admin_user_credentials}
    When the user navigates to the page    ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    And the user should see the element    jQuery = table.table-progress tr:nth-child(1) td:nth-child(6) a:contains("View")

IFS Admin can see applicant's response flagged in Query responses tab and mark discussion as Resolved
    [Documentation]  IFS-1882 IFS-1987
    [Tags]
    # Query responses tab
    Given the user navigates to the page  ${server}/project-setup-management/competition/${PS_Competition_Id}/status/queries
    When the user clicks the button/link  link = Queries (1)
    Then the user mark the discussion as resolved

Project finance user can view the response and uploaded files
    [Documentation]    INFUND-4843  IFS-2716 IFS-7215
    [Tags]
    [Setup]  log in as a different user                      &{internal_finance_credentials}
    Given the user navigates to the page                     ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    Then the project finance user view the query details

Project finance user can continue the conversation
    [Documentation]    INFUND-7752
    [Tags]
    When the user clicks the button/link      jQuery = #accordion-queries-content-2 a:contains("Respond")
    And the user enters text to a text field  css = .editor  This is a response to a response
    And the user clicks the button/link       jQuery = .govuk-button:contains("Post response")

Finance contact receives an email when a new response is posted
    [Documentation]    INFUND-7753 IFS-3559
    [Tags]
    Given the user reads his email    ${PublicSector_lead_applicant_credentials["email"]}  ${PS_Competition_Name}: You have a reply to your query for project ${Queries_Application_No}  We have replied to a query regarding your project finances

Finance contact can view the new response
    [Documentation]    INFUND-7752
    [Tags]
    Given log in as a different user      &{PublicSector_lead_applicant_credentials}
    When the user clicks the button/link  jQuery = .projects-in-setup a:contains("${Queries_Application_Title}")
    And the user clicks the button/link   link = Finance checks
    And the user expands the section      Open all
    Then the user should see the element  jQuery = .govuk-heading-s:contains("Finance team") + .wysiwyg-styles:contains("This is a response to a response")

Project Finance user is able to mark a query discussion as complete
    [Documentation]  IFS-1987
    [Tags]
    Given log in as a different user                &{internal_finance_credentials}
    When the user navigates to the page             ${dreambit_finance_checks}/query
    And the user expands the section                an eligibility query's title
    Then the query conversation can be resolved by  Lee Bowman  1
    And the user should not see the element         jQuery = #accordion-pending-queries-content-2 a:contains("Respond")
    [Teardown]  the user collapses the section      an eligibility query's title

Applicant can see the the queries resolved
    [Documentation]  IFS-1987 IFS-2746
    [Tags]
    Given log in as a different user      &{PublicSector_lead_applicant_credentials}
    When the user navigates to the page   ${server}/project-setup/project/${Queries_Application_Project}/finance-checks
    Then the user should see the element  jQuery = h2:contains("an eligibility query's title") .section-complete
    And the user should see the element   jQuery = h2:contains("a viability query's title") .section-complete
    And the user should not be able to respond to resolved queries

Link to notes from viability section
    [Documentation]    INFUND-4845
    [Tags]
    Given log in as a different user      &{internal_finance_credentials}
    When the user navigates to the page   ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    Then the user navigates to notes section

Link to notes from eligibility section
    [Documentation]    INFUND-4845
    [Tags]
    Given the user navigates to the page  ${dreambit_finance_checks}/eligibility
    And the user clicks the button/link   jQuery = a:contains("Notes")
    Then the user should see the element  jQuery = .govuk-button:contains("Create a new note")

Link to notes from main finance checks summary page
    [Documentation]    INFUND-4845
    [Tags]
    Given the user navigates to the page   ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check
    Then the user clicks the button/link   css = table.table-progress tr:nth-child(1) td:nth-child(7)  # View Notes of Empire Ltd

Project finance can upload a pdf file to notes
    [Documentation]    INFUND-4845
    [Tags]
    Given the user clicks the button/link  jQuery = .govuk-button:contains("Create a new note")
    When the user uploads the file         name = attachment  ${valid_pdf}
    Then the user should see the element   jQuery = h2:contains("Supporting documentation") + ul:contains("${valid_pdf}")

Project finance can remove the file from notes
    [Documentation]    INFUND-4845
    [Tags]
    Given the user clicks the button/link       name = removeAttachment
    Then the user should not see the element    jQuery = form a:contains("${valid_pdf}")
    And the user should not see an error in the page

Project finance can re-upload the file to notes
    [Documentation]    INFUND-4845
    [Tags]
    Given the user uploads the file         name = attachment    ${valid_pdf}
    Then the user should see the element    jQuery = form a:contains("${valid_pdf}")

Project finance can view the file in notes
    [Documentation]    INFUND-4845  IFS-2716
    [Tags]
    Given the user should see the element  link = ${valid_pdf} (opens in a new window)
    Then the user should see the element   jQuery = button:contains("Save note")
    And open pdf link        link = ${valid_pdf} (opens in a new window)


Project finance can upload more than one file to notes
    [Documentation]    INFUND-4845
    [Tags]
    Given the user uploads the file       name = attachment  ${valid_pdf}
    Then the user should see the element  jQuery = form li:nth-of-type(2) a:contains("${valid_pdf}")

Project finance can still view both files in notes
    [Documentation]    INFUND-4845
    [Tags]
    Given the user should see the element   jQuery = li:nth-of-type(1) a:contains("${valid_pdf}")
    And the user should see the element     jQuery = li:nth-of-type(2) a:contains("${valid_pdf}")
    Then the user clicks the button/link    css = button[name='removeAttachment']:nth-last-of-type(1)

Create new note validations
    [Documentation]    INFUND-4845
    [Tags]
    Given the user should see new note client side validations
    Then the user should see new note server side validations
    And the user should see word count validations

New note can be cancelled
    [Documentation]    INFUND-4845
    [Tags]
    Given the user clicks the button/link      jQuery = a:contains("Cancel")
    Then the user should not see the element   id = noteTitle
    And the user should not see the element    css = .editor

Note can be re-entered and posted
    [Documentation]    INFUND-4845
    [Tags]
    Given the user enters new note details
    Then the user post created note details

Note sections are no longer editable
    [Documentation]    INFUND-4845
    [Tags]
    When the user should not see the element    css = .editor
    And the user should not see the element     id = noteTitle

Project finance user can comment on the note
    [Documentation]    INFUND-7756
    [Tags]
    When the user should see the element             jQuery = h2:contains("an eligibility query's title")
    And the user should see the element              jQuery = p:contains("this is some note text")
    And the user should see the element              id = post-new-comment

Large pdf uploads not allowed for note comments
    [Documentation]    INFUND-7756
    [Tags]
    Given the user clicks the button/link            id = post-new-comment
    When the user uploads the file                   name = attachment    ${too_large_pdf}
    Then the user should see a field error           ${too_large_10MB_validation_error}

Non pdf uploads not allowed for note comments
    [Documentation]    INFUND-7756
    [Tags]
    Given the user uploads the file                  name = attachment    ${text_file}
    Then the user should see a field error           ${finance_query_notes_filetype_error}

Project finance can upload a pdf file to note comments
    [Documentation]    INFUND-7756
    [Tags]
    Given the user uploads the file                  name = attachment   ${valid_pdf}
    Then the user should see the element             link = ${valid_pdf} (opens in a new window)

Project finance can remove the file from note comments
    [Documentation]    INFUND-7756
    [Tags]
    Given the user clicks the button/link       name = removeAttachment
    Then the user should not see the element    jQuery = form a:contains("${valid_pdf}")
    And the user should not see an error in the page

Project finance can re-upload the file to note comments
    [Documentation]    INFUND-7756
    [Tags]
    Given the user uploads the file         name = attachment    ${valid_pdf}
    Then the user should see the element    jQuery = form a:contains("${valid_pdf}")

Project finance can view the file in note comments
    [Documentation]    INFUND-7756
    [Tags]
    Given the user should see the element    link = ${valid_pdf} (opens in a new window)
    When the file has been scanned for viruses
    And the user should see the element      jQuery = button:contains("Save comment")

Project finance can upload more than one file to note comments
    [Documentation]    INFUND-7756
    [Tags]
    Given the user uploads the file         name = attachment    ${valid_pdf}
    Then project finance can view both files in note comments

Note comments validations
    [Documentation]    INFUND-7756
    [Tags]
    Given the user should see the note comments client side validations
    Then the user should see the note comments server side validations
    And the user should see word count validations

Note comment can be posted
    [Documentation]    INFUND-7756
    [Tags]
    Given the user clicks the button/link       jQuery = .govuk-button:contains("Save comment")
    Then the user should not see the element    jQuery = .govuk-button:contains("Save comment")

*** Keywords ***
Custom Suite Setup
    ${today} =  get today
    set suite variable  ${today}
    The guest user opens the browser

the user should see all the attachments
    the user should see the element     jQuery = a:contains("${valid_pdf}")
    the user should see the element     jQuery = a:contains("${ods_file}")
    the user should see the element     jQuery = a:contains("${valid_odt}")

the user can remove an attachment
    [Arguments]  ${attachment_file}
    the user clicks the button/link        name = removeAttachment
    the user should not see the element    jQuery = h3:contains("Supporting documents") + ul:contains("${attachment_file}") .button-clear:contains("Remove")
    the user should not see an error in the page

the user uploads multiple file types as attachment and removes them
    [Arguments]  ${file_type1}  ${file_type2}  ${file_type3}
    the user uploads the file            name = attachment    ${file_type1}
    the user clicks the button/link      jQuery = a:contains("${file_type1}")+ .button-clear:contains("Remove")
    the user should not see the element  jQuery = a:contains("${file_type1}")+ .button-clear:contains("Remove")
    the user uploads the file            name = attachment    ${file_type2}
    the user clicks the button/link      jQuery = a:contains("${file_type2}")+ .button-clear:contains("Remove")
    the user should not see the element  jQuery = a:contains("${file_type2}")+ .button-clear:contains("Remove")
    the user uploads the file            name = attachment    ${file_type3}
    the user clicks the button/link      jQuery = a:contains("${file_type3}")+ .button-clear:contains("Remove")
    the user should not see the element  jQuery = a:contains("${file_type3}")+ .button-clear:contains("Remove")

the user is able to download attachments
    [Arguments]  ${attachment1}  ${attachment2}
    The user downloads the file                 ${PublicSector_lead_applicant_credentials["email"]}  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check  ${DOWNLOAD_FOLDER}/${attachment1}
    Download should be done
    remove the file from the operating system   ${attachment1}
    The user downloads the file                 ${PublicSector_lead_applicant_credentials["email"]}  ${server}/project-setup-management/project/${Queries_Application_Project}/finance-check  ${DOWNLOAD_FOLDER}/${attachment2}
    Download should be done
    remove the file from the operating system   ${attachment2}

The query conversation can be resolved by
    [Arguments]  ${user}  ${section}
    the user clicks the button/link  jQuery = #accordion-queries-content-${section} a:contains("Mark as resolved")    #a viability query
    the user clicks the button/link  css = button[name="markAsResolved"]  # Submit
    the user should see the element  jQuery = #accordion-queries-heading-${section} .yes  # Resolved green check
    the user should see the element  jQuery = p:contains("${user}")
    the user should see the element  jQuery = p:contains("${today}")

the user should not be able to respond to resolved queries
    the user should not see the element  jQuery = h2:contains("eligibility") + [id^="finance-checks-query"] a[id^="post-new-response"]
    the user should not see the element  jQuery = h2:contains("viability") + [id^="finance-checks-query"] a[id^="post-new-response"]

queries section contains finance contact name, email and telephone
    the user should see the element     jQuery = #main-content p:nth-of-type(1):contains("Becky Mason")
    the user should see the element     jQuery = #main-content p:nth-of-type(1):contains("3578109078")
    the user should see the element     jQuery = #main-content p:nth-of-type(1):contains(${PublicSector_lead_applicant_credentials["email"]})

the user should see post a new query client side validations
    the user enters text to a text field    id = queryTitle   ${empty}
    the user enters text to a text field    css = .editor   ${empty}
    Set Focus To Element                    link = Sign out
    the user should see the element         jQuery = label[for = "queryTitle"] + .govuk-error-message:contains(${empty_field_warning_message})
    the user should see the element         jQuery = label[for = "query"] + .govuk-error-message:contains(${empty_field_warning_message})

the user should see post a new query sever side validations
    the user clicks the button/link         jQuery = .govuk-button:contains("Post query")
    the user should see a field and summary error    ${empty_field_warning_message}
    the user should see a field and summary error    ${empty_field_warning_message}
    the user enters text to a text field    id = queryTitle    an eligibility query's title
    the user enters text to a text field    css = .editor    this is some query text
    the user should not see an error in the page

the user enters a new query details
    [Arguments]  ${file1}  ${file2}  ${file3}
    the user enters text to a text field     id = queryTitle    an eligibility query's title
    the user enters text to a text field     css = .editor    this is some query text
    the user uploads the file                name = attachment    ${file1}
    the user should see the element          jQuery = a:contains("${file1}")+ .button-clear:contains("Remove")
    the user uploads the file                name = attachment    ${file2}
    the user should see the element          jQuery = a:contains("${file2}")+ .button-clear:contains("Remove")
    the user uploads the file                name = attachment    ${file3}
    the user should see the element          jQuery = a:contains("${file3}")+ .button-clear:contains("Remove")

the user enters a query response details
    [Arguments]  ${file-1}  ${file-2}  ${file-3}
    the user enters text to a text field     css = .editor    this is some query text
    the user uploads the file                name = attachment    ${file-1}
    the user should see the element          jQuery = a:contains("${file-1}")+ .button-clear:contains("Remove")
    the user uploads the file                name = attachment    ${file-2}
    the user should see the element          jQuery = a:contains("${file-2}")+ .button-clear:contains("Remove")
    the user uploads the file                name = attachment    ${file-3}
    the user should see the element          jQuery = a:contains("${file-3}")+ .button-clear:contains("Remove")

the user should see submitted query details
    the user expands the section         an eligibility query's title
    the user should see the element      jQuery = .govuk-heading-s:contains("Lee Bowman - Innovate UK (Finance team)")
    the user should see the element      jQuery = .govuk-heading-s:contains("${today}")
    the user should see the element      css = #post-new-response  # Respond button

the project finance user post another new query
    the user clicks the button/link                        jQuery = th:contains("${Dreambit_Name}") ~ td:contains("View")
    the user clicks the button/link                        css = a[id = "post-new-query"]
    the user enters text to a text field                   id = queryTitle  a viability query's title
    the user selects the option from the drop-down menu    Viability    id = section
    the user enters text to a text field                   css = .editor    another query body
    the user clicks the button/link                        css = .govuk-grid-column-one-half button[type = "submit"]  # Post query
    the user should not see an error in the page

the user should see list of posted queries
    the user should see the element      jQuery = #accordion-queries-heading-1:contains("a viability query's title")
    the user should see the element      jQuery = #accordion-queries-heading-2:contains("an eligibility query's title")
    # Query responses tab
    the user navigates to the page       ${server}/project-setup-management/competition/${Queries_Application_Project}/status/queries
    the user should see the element      jQuery = p:contains("There are no outstanding queries.")

the user should see the response to query server side validation
    the user clicks the button/link               jQuery = .govuk-button:contains("Post response")
    the user should see a field error             ${empty_field_warning_message}
#    TODO commmented due to IFS-5804
#    And the user should see a summary error            ${empty_field_warning_message}
    the user enters text to a text field          css = .editor  this is some response text
    the user uploads the file                     name = attachment  ${valid_pdf}
    the user should see the element               jQuery = a:contains("${valid_pdf}") + button:contains("Remove")
    the user should not see an error in the page

the user should see the response to query client side validations
    the user expands the section                  Open all
    the user clicks the button/link               jQuery = #accordion-pending-queries-content-2 a:contains("Respond")
    the user enters text to a text field          css = .editor  ${empty}
    Set Focus To Element                          jQuery = .govuk-button:contains("Post response")
    the user should see a field error             ${empty_field_warning_message}

the user should see word count validations
    the user enters text to a text field   css = .editor  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin elementum condimentum ex, ut tempus nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed pretium tellus. Vestibulum sollicitudin semper scelerisque. Sed tristique, erat in gravida gravida, felis tortor fermentum ligula, vitae gravida velit ipsum vel magna. Aenean in pharetra ex. Integer porttitor suscipit lectus eget ornare. Maecenas sed metus quis sem dapibus vestibulum vel vitae purus. Etiam sodales nisl at enim tempus, sed malesuada elit accumsan. Aliquam faucibus neque vitae commodo rhoncus. Sed orci sem, varius vitae justo quis, cursus porttitor lectus. Pellentesque eu nibh nunc. Duis laoreet enim et justo sagittis, at posuere lectus laoreet. Suspendisse rutrum odio id iaculis varius. Phasellus gravida, mi vel vehicula dignissim, lectus nunc eleifend justo, elementum lacinia enim tellus a nulla. Pellentesque consectetur sollicitudin ante, ac vehicula lorem laoreet laoreet. Fusce consequat libero mi. Quisque luctus risus neque, ut gravida quam tincidunt id. Aliquam id ante arcu. Nulla ut est ipsum. Praesent accumsan efficitur malesuada. Ut tempor auctor felis eu dapibus. Sed felis quam, aliquet sit amet urna nec, consectetur feugiat nibh. Nam id libero nec augue convallis euismod quis vitae nibh. Integer lectus velit, malesuada ut neque mollis, mattis euismod diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam aliquet porta enim sit amet rhoncus. Curabitur ornare turpis eros, sodales hendrerit tellus rutrum a. Ut efficitur feugiat turpis, eu ultrices velit pharetra non. Curabitur condimentum lacus ac ligula auctor egestas. Aliquam feugiat tellus neque, a ornare tortor imperdiet at. Integer varius turpis eu mi efficitur, at imperdiet ex posuere. Suspendisse blandit, mi at mollis placerat, magna nibh malesuada nisi, ultrices semper augue enim sit amet nisi. Donec molestie tellus vitae risus interdum, nec finibus risus interdum. Integer purus justo, fermentum id urna eu, aliquam rutrum erat. Phasellus volutpat odio metus, sed interdum magna luctus ac. Nam ullamcorper maximus sapien vitae dapibus. Vivamus ullamcorper quis sapien et mattis. Aenean aliquam arcu lacus, vel mollis ligula ultrices nec. Sed cursus placerat tortor elementum tincidunt. Pellentesque at arcu ut felis euismod vestibulum pulvinar nec neque. Quisque ipsum purus, tincidunt quis iaculis eu, malesuada nec lectus. Vivamus tempor, enim quis vestibulum convallis, ex odio pharetra tellus, eget posuere justo ligula sit amet dolor. Cras scelerisque neque id porttitor semper. Sed ut ultrices lorem. Pellentesque sed libero a velit vestibulum fermentum id et velit. Vivamus turpis risus, venenatis ac quam nec, pulvinar fringilla libero. Donec eget vestibulum orci, id lacinia mi. Aenean sed lectus viverra est feugiat suscipit. Proin eget justo turpis. Nullam maximus fringilla sapien, at pharetra odio pretium ut. Cras imperdiet mauris at bibendum dapibus.
    Set Focus To Element                   link = Sign out
    the user should see a field error      Maximum word count exceeded. Please reduce your word count to 400.
    the user should see a field error      This field cannot contain more than 4,000 characters.
    the user enters text to a text field   css = .editor  This is some response text
    the user should not see an error in the page

the user mark the discussion as resolved
    the user should see the element                 jQuery = td:contains("${Queries_Application_Title}") + td:contains("${Dreambit_Name}")
    the user clicks the button/link                 link = ${Dreambit_Name}
    the user should see the element                 jQuery = h1:contains("${Dreambit_Name}")
    the user should see the element                 link = Post a new query
    the user expands the section                    a viability query's title
    the query conversation can be resolved by       Arden Pimenta  1
    [Teardown]  the user collapses the section      a viability query's title

the project finance user view the query details
    the user clicks the button/link             css = table.table-progress tr:nth-child(1) td:nth-child(6)  # View
    the user expands the section                an eligibility query's title
    the user should see the element             jQuery = .govuk-heading-s:contains("Becky") + p:contains("This is some response text")
    the user should see the element             jQuery = a:contains("${valid_docx}")
    the user should see the element             jQuery = a:contains("${excel_file}")
    the user is able to download attachments    ${valid_docx}  ${excel_file}

the user navigates to notes section
    the user clicks the button/link   css = table.table-progress tr:nth-child(1) td:nth-child(2)
    the user clicks the button/link   jQuery = a:contains("Notes")
    the user should see the element   jQuery = h2:contains("Review notes")
    the user should see the element   jQuery = .govuk-button:contains("Create a new note")

the user should see new note server side validations
    the user clicks the button/link         jQuery = .govuk-button:contains("Save note")
    the user should see the element         jQuery = label[for="noteTitle"] + .govuk-error-message:contains(${empty_field_warning_message})
    the user should see the element         jQuery = label[for="note"] + .govuk-error-message:contains(${empty_field_warning_message})
    the user enters text to a text field    id = noteTitle    an eligibility query's title
    the user enters text to a text field    css = .editor    this is some note text
    the user should not see an error in the page

the user should see new note client side validations
    the user enters text to a text field    id = noteTitle    ${empty}
    the user should see a field error       ${empty_field_warning_message}
    the user enters text to a text field    css = .editor    ${empty}
    Set Focus To Element                    link = Sign out
    the user should see a field error       ${empty_field_warning_message}

the user enters new note details
    the user clicks the button/link         jQuery = .govuk-button:contains("Create a new note")
    the user enters text to a text field    id = noteTitle    an eligibility query's title
    the user enters text to a text field    css = .editor    this is some note text
    the user uploads the file               name = attachment    ${valid_pdf}
    the user uploads the file               name = attachment    ${valid_pdf}

the user post created note details
    the user clicks the button/link             jQuery = .govuk-button:contains("Save note")
    the user should not see the element         jQuery = .govuk-button:contains("Save note")
    the user should see the element             jQuery = p:contains("Lee Bowman - Innovate UK (Finance team)")

project finance can view both files in note comments
    the user should see the element    jQuery = form li:nth-of-type(1) a:contains("${valid_pdf}")
    the user should see the element    jQuery = form li:nth-of-type(2) a:contains("${valid_pdf}")
    the user should see the element    jQuery = button:contains("Save comment")

the user should see the note comments server side validations
    the user clicks the button/link                 jQuery = .govuk-button:contains("Save comment")
    the user should see a field error               ${empty_field_warning_message}
    #TODO commmented due to IFS-5804
    #the user should see a summary error             ${empty_field_warning_message}
    the user enters text to a text field            css = .editor  this is some comment text
    Set Focus To Element                            jQuery = .govuk-button:contains("Save comment")
    the user should not see an error in the page

the user should see the note comments client side validations
    the user enters text to a text field     css = .editor  ${empty}
    Set Focus To Element                     jQuery = .govuk-button:contains("Save comment")
    the user should see a field error        ${empty_field_warning_message}

the user navigates to queries page
    the user navigates to the page       ${dreambit_finance_checks}/eligibility
    the user clicks the button/link      jQuery = a:contains("Queries")
    the user should see the element      jQuery = h2:contains("Queries")