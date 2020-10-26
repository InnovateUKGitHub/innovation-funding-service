*** Settings ***
Documentation  IFS-8414 Internal user - View co funder feedback progress - list view
...
...            IFS-8407 Internal user - View co funder feedback
...
...            IFS-8404 Internal user - Assign Supporter
...
...            IFS-8405 Internal user - Remove Supporter
...
...            IFS-8409 Co funder - application response & edit
...
...            IFS-8402 Co funder dashboard - competition level
...
...            IFS-8403  Co funder dashboard - application level
...
...            IFS-8408  Co funder view of application
...

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${supporterApplicationTitle}     	      Reconfiguring an immune response
&{Supporter01_credentials}                email=mister.branches@money.com    password=${short_password}
${KTP_Application_URL}                    ${SERVER}/assessment/supporter/application/247/response
${ktpCofundingCompetitionNavigation}      Co funder dashboard - application level
${supporterUserUsername}                  Wallace.Mccormack@money.com
${cofundingCompetitionName}               KTP cofunding
${cofundingCompetitionID}                 ${competition_ids['${cofundingCompetitionName}']}
${cofundingApplicationTitle}              How cancer invasion takes shape
${cofundingApplicationID}                 ${application_ids['${cofundingApplicationTitle}']}
${supporterOrg}                           The University of Surrey
${newApplication}                         New application

*** Test Cases ***
The internal user can allocate applications
    [Documentation]   IFS-8404
    Given Logging in and Error Checking         &{ifs_admin_user_credentials}
    When the user clicks the button/link        link = ${cofundingCompetitionName}
    And the user clicks the button/link         link = Manage supporters
    Then the user can allocate applictions

The internal user can allocate supporters and search for supporter by first name and/or last name
    [Documentation]   IFS-8404
    Given the user can allocate supporters
    When the user searches for supporter by name      Douglas
    And the user searches for supporter by name       Alston
    Then the user searches for supporter by name      Douglas Alston

The internal user can invite a supporter to a KTP application
    [Documentation]   IFS-8404
    Given the user can view already assigned supporters
    Then the user can invite a supporter to a KTP application

The internal user can remove a supporter from an application
    [Documentation]   IFS-8405
    Given the user clicks the button/link        jQuery = td:contains("Douglas Alston") ~ td button:contains("Remove")
    Then the user should not see the element     jQuery = td:contains("Douglas Alston") ~ td button:contains("Remove")

The supporter should not see the removed application on their dashboard
     [Documentation]   IFS-8405
     [Setup]    get application id using application name
     Given Log in as a different user                                        email=douglas.alston@money.com    password=${short_password}
     When the user clicks the button/link                                    link = ${cofundingCompetitionName}
     Then the user navigates to the page and gets a custom error message     ${server}/application/${protonApplicationId}/summary    ${403_error_message}

The internal user can view a supporter application by searching with an application number
    [Documentation]  IFS-8414
    [Setup]  the user requesting the application id
    Given Log in as a different user                    &{ifs_admin_user_credentials}
    And the user clicks the button/link                 link = ${cofundingCompetitionName}
    And the user clicks the button/link                 link = Manage supporters
    And the user clicks the button/link                 link = View feedback
    When the user enters text to a text field           id=applicationFilter    ${supporterApplicationID}
    And the user clicks the button/link                 jQuery = button:contains("Filter")
    And the user clicks the button/link                 link = ${supporterApplicationID}
    Then the user should see the element                jQuery = h1:contains("Application overview")

The ifs admin views the feedback of the application
    [Documentation]   IFS-8407
    Given the user clicks the button/link           link = Back to supporter feedback
    When the user clicks the button/link            jQuery = td:contains("${supporterApplicationTitle}") ~ td:contains("View feedback")
    Then the user can view the supporter review

The comp admin views the feedback of the application
    [Documentation]   IFS-8407
    Given Log in as a different user                &{Comp_admin1_credentials}
    And the user clicks the button/link             link = ${cofundingCompetitionName}
    And the user clicks the button/link             link = Manage supporters
    And the user clicks the button/link             link = View feedback
    When And the user clicks the button/link        link = ${supporterApplicationID}
    And the user clicks the button/link             link = Back to supporter feedback
    And the user clicks the button/link             jQuery = td:contains("${supporterApplicationTitle}") ~ td:contains("View feedback")
    Then the user can view the supporter review

The finance manager views the feedback of the application
    [Documentation]   IFS-8407
    Given Log in as a different user                &{internal_finance_credentials}
    And the user clicks the button/link             link = ${cofundingCompetitionName}
    And the user clicks the button/link             link = Manage supporters
    And the user clicks the button/link             link = View feedback
    When And the user clicks the button/link        link = ${supporterApplicationID}
    And the user clicks the button/link             link = Back to supporter feedback
    And the user clicks the button/link             jQuery = td:contains("${supporterApplicationTitle}") ~ td:contains("View feedback")
    Then the user can view the supporter review

The supporter can see the sections in the cofunding dashboard
    [Documentation]  IFS-8402
    Given Log in as a different user            hubert.cumberdale@salad-fingers.com  Passw0rd
    When the user clicks the button/link        jQuery = h2:contains("Supporter")
    Then the user should see the element        jQuery = h1:contains("Competitions")
    And the user should not see the element     jQuery = h2:contains("Upcoming competitions to review")

The supporter should see a newly created application from the dashboard
    [Documentation]  IFS-8402
    Given the user select the competition and starts application     KTP new competition
    input text                                                       id = knowledgeBase        ${supporterOrg}
    When the user clicks the button/link                             jQuery = ul li:contains("${supporterOrg}")
    And the user clicks the button/link                              jQuery = button:contains("Confirm")
    Then the user clicks the button/link                             id = knowledge-base-confirm-organisation-cta
    And the user clicks the button/link                              link = Application details
    Then the user enters text to a text field                        css = [id = "name"]    ${newApplication}
    And the user enters text to a text field                         css = [id = "durationInMonths"]    3
    Then the user clicks the button/link                             jQuery = button:contains("Mark as complete")
    And the user clicks the button/link                              link = Dashboard
    Then the user clicks the button/link                             jQuery = h2:contains("Applications")
    And the user should see the element                              jQuery = a:contains("${newApplication}")

Supporter can see list of applications assigned to him in the dashboard
    [Documentation]  IFS-8403
    Given log in as a different user         ${supporterUserUsername}   ${short_password}
    When the user navigates to the page      ${server}/assessment/supporter/dashboard/competition/${cofundingCompetitionID}
    Then the user should see the element     jQuery = h1:contains("Review applications") span:contains("${cofundingCompetitionName}")
    And the user should see the element      link = View competition brief (opens in a new window)

Supporter checks number of applications in the page is no more than 20
    [Documentation]  IFS-8403
    When the user gets the number of applications in page
    Then should be equal as numbers          ${applicationCount_1}    20

Supporter can navgate to the next page of applications in review
    [Documentation]  IFS-8403
    Given the user navigates to the page     ${server}/assessment/supporter/dashboard/competition/${cofundingCompetitionID}
    When the user clicks the button/link     link = Next
    Then the user should see the element     link = Previous

Supporter checks the number of applications count is correct
    [Documentation]  IFS-8403
    When the user gets the actual number of applications in all pages
    And the user gets expected number of applications in the page
    Then should be equal as numbers                                       ${actualNumberOfApplications}   ${expectedNumberOfApplications}

Supporter can view read only view of an application and see the print application link
    [Documentation]  IFS-8408
    Given the user navigates to the page        ${server}/application/${cofundingApplicationID}/summary
    When the user clicks the button/link        jQuery = button:contains("Application team")
    Then the user should see the element        jQuery = h1:contains("Application overview") span:contains("${cofundingApplicationTitle}")
    And the user should not see the element     jQuery = button:contains("Edit")
    And the user should see the element         jQuery = a:contains("Print application")

The user sees the validation when responding to the Supporter/Supporter review
    [Documentation]   IFS-8409
    Given Log in as a different user                            &{Supporter01_credentials}
    When the user navigates to the page                         ${KTP_Application_URL}
    Then the user clicks the button/link                        jQuery = button:contains("Save and return to applications")
    And the user should see a field error                       You must select an option.
    And the user should see a field and summary error           Please provide some feedback.
    And the user checks the feedback validation                 decision-no
    And the user checks the feedback validation                 decision-yes
    And the user enters multiple strings into a text field      css = .editor  a${SPACE}  252
    And the user clicks the button/link                         jQuery = button:contains("Save and return to applications")
    And the user should see a field error                       Maximum word count exceeded. Please reduce your word count to 250.

The user responds to the Supporter/Supporter review No
    [Documentation]   IFS-8409
    Given the user selects the radio button           decision  decision-no
    When the user enters text to a text field         css = .editor  This is the comments from the supporter
    Then the user clicks the button/link              jQuery = button:contains("Save and return to applications")
    And the user navigates to the page                ${KTP_Application_URL}
    And the user should see the element               jQuery = p:contains("This is the comments from the supporter")

The user responds to the Supporter/Supporter review Yes
    [Documentation]   IFS-8409
    Given the user navigates to the page         ${KTP_Application_URL}
    When the user clicks the button/link         jQuery = button:contains("Edit")
    Then the user selects the radio button       decision  decision-yes
    And the user enters text to a text field     css = .editor  This is the comments from the supporter
    And the user clicks the button/link          jQuery = button:contains("Save and return to applications")

The comp admin can close the assessment and the link to allocate applications is no longer active
    [Documentation]   IFS-8404
    Given Log in as a different user                             &{ifs_admin_user_credentials}
    When the user clicks the button/link                         link = ${cofundingCompetitionName}
    And the user can close the assessment
    Then the user is no longer able to allocate applications

*** Keywords ***
Custom suite setup
    The guest user opens the browser
    Connect To Database   @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user requesting the application id
    ${supporterApplicationID} =  get application id by name    ${supporterApplicationTitle}
    Set Suite Variable  ${supporterApplicationID}

the user can view the supporter review
    the user should see the element     jQuery = h1:contains("Supporter overview")
    the user should see the element     jQuery = h2:contains("Interested")
    the user should see the element     jQuery = h2:contains("Declined")
    the user should see the element     jQuery = h2:contains("Pending review")

the user can allocate applictions
    the user should see the element     jQuery = h1:contains("Manage supporters")
    the user should see the element     jQuery = h3:contains("Actions")
    the user should see the element     link = Assign supporters to applications
    the user should see the element     link = View feedback
    the user clicks the button/link     link = Assign supporters to applications

the user can allocate supporters
    the user should see the element     jQuery = h1:contains("Assign supporters to applications")
    the user should see the element     jQuery = h2:contains("Filter applications")
    the user should see the element     jQuery = label:contains("Search by application number")
    the user should see the element     jQuery = th:contains("Application number")
    the user should see the element     jQuery = th:contains("Title")
    the user should see the element     jQuery = th:contains("Knowledge base partner")
    the user should see the element     jQuery = th:contains("Supporters")
    the user should see the element     jQuery = span:contains("Showing 1 - 20 of 36 results")
    the user clicks the button/link     link = Next
    the user clicks the button/link     jQuery = td:contains("The proton size") ~ td a:contains("Assign")

the user searches for supporter by name
    [Arguments]   ${name}
    the user should see the element         jQuery = h2:contains("Filter")
    the user should see the element         jQuery = label:contains("Search by first or last name")
    the user enters text to a text field    id = filter    ${name}
    the user clicks the button/link         jQuery = button:contains("Filter")
    the user should see the element         jQuery = .govuk-table__cell:contains("Douglas Alston")
    the user should not see the element     jQuery = .govuk-table__cell:contains("Keane Connolly")
    the user clicks the button/link         link = Clear all filters
    the user should see the element         jQuery = .govuk-table__cell:contains("Keane Connolly")

the user can view already assigned supporters
    the user should see the element         jQuery = h1:contains("Manage supporters for this application")
    the user should see the element         jQuery = h3:contains("Partners")
    the user should see the element         jQuery = h3:contains("Innovation area")
    the user should see the element         jQuery = h2:contains("Assigned supporters")
    the user should see the element         jQuery = th:contains("Name")
    the user should see the element         jQuery = th:contains("Organisation")
    the user should see the element         jQuery = th:contains("Email")

the user can invite a supporter to a KTP application
    the user should see the element         jQuery = h2:contains("Available supporters")
    the user should see the element         jQuery = th:contains("Select")
    the user should see the element         jQuery = th:contains("Name")
    the user should see the element         jQuery = span:contains("0 selected")
    the user should see the element         jQuery = button:contains("Add selected to application")
    the user should see the element         jQuery = [disabled="disabled"]
    the user selects the checkbox           select-all-check
    the user should not see the element     jQuery = [disabled="disabled"]
    the user clicks the button/link         jQuery = button:contains("Add selected to application")
    the user should see the element         jQuery = .govuk-table__cell:contains("Douglas Alston")

the user can close the assessment
    the user navigates to the page          ${server}/management/competition/${cofundingCompetitionID}
    the user clicks the button/link         jQuery = button:contains("Close assessment")

the user is no longer able to allocate applications
    the user clicks the button/link         link = Manage supporters
    the user should see the element         jQuery = [aria-disabled="true"]

the user checks the feedback validation
    [Arguments]  ${decision}
    And the user selects the radio button                       decision  ${decision}
    Then the user clicks the button/link                        jQuery = button:contains("Save and return to applications")
    And the user should see a field and summary error           Please provide some feedback.

the user gets the number of applications in page
   ${pages} =   get element count      css = [class="pagination-links govuk-body"] a
        :FOR    ${i}    IN RANGE   1   ${pages}+1
            \    the user navigates to the page   ${server}/assessment/supporter/dashboard/competition/${cofundingCompetitionID}?page=${i}
            \    ${applicationCount} =   get element count    jQuery = h2:contains("Applications for review") ~ ul li
            \    set suite variable    ${applicationCount_${i}}    ${applicationCount}

the user gets the actual number of applications in all pages
    ${actualNumberOfApplications}     evaluate     ${applicationCount_1} + ${applicationCount_2}
    set suite variable     ${actualNumberOfApplications}

the user gets expected number of applications in the page
    ${expectedNumberOfApplications} =  get text     jQuery = h2:contains("Applications for review") span
    set suite variable     ${expectedNumberOfApplications}

get application id using application name
    ${protonApplicationId} =    get application id by name      The proton size
    set suite variable     ${protonApplicationId}

the user clicks view feedback link
    And the user clicks the button/link             link = ${cofundingCompetitionName}
    And the user clicks the button/link             link = Manage co-funders
    And the user clicks the button/link             link = View feedback

the user can view the feedback
    When the user clicks the button/link            link = ${cofunderApplicationID}
    And the user clicks the button/link             link = Back to co-funders
    And the user clicks the button/link             jQuery = td:contains("${cofunderApplicationTitle}") ~ td:contains("View feedback")
