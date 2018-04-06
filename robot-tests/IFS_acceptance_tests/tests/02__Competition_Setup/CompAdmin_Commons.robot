*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***
#CA = Competition Administration
${CA_UpcomingComp}   ${server}/management/dashboard/upcoming
${CA_Live}           ${server}/management/dashboard/live

*** Keywords ***
the user edits the assessed question information
    the user enters text to a text field    id=question.maxWords    100
    the user enters text to a text field    id=question.scoreTotal  10
    the user enters text to a text field    id=question.assessmentGuidance    Business opportunity guidance
    the user clicks the button/link    jQuery=button:contains("+Add guidance row")
    the user enters text to a text field    id=guidanceRows[5].scoreFrom    0
    the user enters text to a text field    id=guidanceRows[5].scoreTo    1
    the user enters text to a text field    id=guidanceRows[5].justification    This is a justification
    the user clicks the button/link    id=remove-guidance-row-2

the user sees the correct read only view of the question
    the user should see the element  jQuery=dt:contains("Max word count") + dd:contains("100")
    the user should see the element  jQuery=dd p:contains("Business opportunity guidance")
    the user should see the element  jQuery=dt:contains("0-1") + dd:contains("This is a justification")
    the user should see the element  jQuery=dt:contains("Max word count") + dd:contains("10")
    the user should not see the text in the page  The business opportunity is plausible

the user fills in the CS Initial details
    [Arguments]  ${compTitle}  ${month}  ${nextyear}  ${compType}
    the user clicks the button/link                      link=Initial details
    the user enters text to a text field                 css=#title  ${compTitle}
    the user selects the option from the drop-down menu  ${compType}  id=competitionTypeId
    the user selects the option from the drop-down menu  Emerging and enabling  id=innovationSectorCategoryId
    the user selects the option from the drop-down menu  Robotics and autonomous systems  css=select[id^=innovationAreaCategory]
    the user enters text to a text field                 css=#openingDateDay  1
    the user enters text to a text field                 css=#openingDateMonth  ${month}
    the user enters text to a text field                 css=#openingDateYear  ${nextyear}
    the user selects the option from the drop-down menu  Ian Cooper  id=innovationLeadUserId
    the user selects the option from the drop-down menu  Robert Johnson  id=executiveUserId
    the user clicks the button/link                      jQuery=button:contains("Done")
    the user clicks the button/link                      link=Competition setup
    the user should see the element                      jQuery=div:contains("Initial details") ~ .task-status-complete

the user fills in the CS Funding Information
    the user clicks the button/link       link=Funding information
    the user enters text to a text field  id=funders[0].funder  FunderName FamilyName
    the user enters text to a text field  id=funders[0].funderBudget  142424242
    the user enters text to a text field  id=pafNumber  2424
    the user enters text to a text field  id=budgetCode  Ch0col@73
    the user enters text to a text field  id=activityCode  133t
    the user clicks the button/link       jQuery=button:contains("Generate code")
    sleep  2s  #This sleeps is intended as the competition Code needs some time
    textfield should contain              css=input[name="competitionCode"]  19
    the user clicks the button/link       jQuery=button:contains("Done")
    the user clicks the button/link       link=Competition setup
    the user should see the element       jQuery=div:contains("Funding information") ~ .task-status-complete

the user fills in the CS Eligibility
    [Arguments]  ${organisationType}  ${researchParticipation}
    the user clicks the button/link   link=Eligibility
    the user clicks the button twice  css=label[for="single-or-collaborative-collaborative"]
    the user clicks the button twice  css=label[for="research-categories-33"]
    the user clicks the button twice  css=label[for="lead-applicant-type-${organisationType}"]
    the user selects Research Participation if required  ${researchParticipation}
    the user clicks the button/link  css=label[for="comp-resubmissions-yes"]
    the user clicks the button/link  css=label[for="comp-resubmissions-yes"]
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element   jQuery=div:contains("Eligibility") ~ .task-status-complete
    #Elements in this page need double clicking

the user selects Research Participation if required
    [Arguments]  ${percentage}
    ${status}  ${value}=  Run Keyword And Ignore Error Without Screenshots  the user should see the element  id=researchParticipationAmountId
    Run Keyword If  '${status}' == 'PASS'  the user selects the option from the drop-down menu  ${percentage}  researchParticipation
    Run Keyword If  '${status}' == 'FAIL'  the user should not see the element  id=researchParticipation

the user fills in the CS Milestones
    [Arguments]  ${month}  ${nextyear}
    the user clicks the button/link       link=Milestones
    ${i} =  Set Variable   1
     :FOR   ${ELEMENT}   IN    @{milestones}
      \    the user enters text to a text field  jQuery=th:contains("${ELEMENT}") ~ td.day input  ${i}
      \    the user enters text to a text field  jQuery=th:contains("${ELEMENT}") ~ td.month input  ${month}
      \    the user enters text to a text field  jQuery=th:contains("${ELEMENT}") ~ td.year input  ${nextyear}
      \    ${i} =   Evaluate   ${i} + 1
    the user clicks the button/link       jQuery=button:contains("Done")
    the user clicks the button/link       link=Competition setup
    the user should see the element       jQuery=div:contains("Milestones") ~ .task-status-complete

the user marks the Application as done
    [Arguments]  ${growthTable}  ${comp_type}
    the user clicks the button/link  link=Application
    the user marks the Application details section as complete    ${comp_type}
    the user marks the Assessed questions as complete  ${growthTable}  ${comp_type}

the user marks the Assessed questions as complete
    [Arguments]  ${growthTable}  ${comp_type}
    Run Keyword If  '${comp_type}' == 'Sector'   the assessed questions are marked complete except finances(sector type)
    Run Keyword If  '${comp_type}' == 'Programme'    the assessed questions are marked complete except finances(programme type)
    Run keyword If  '${comp_type}' == '${compType_EOI}'  the assessed questions are marked complete(EOI type)
    Run Keyword If  '${comp_type}' == '${compType_EOI}'  the user opts no finances for EOI comp
    #No need to mark generic competition assessed question as complete as they already are.
    Run keyword If  '${comp_type}'!='${compType_EOI}'   the user fills in the Finances questions  ${growthTable}
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element  jQuery=div:contains("Application") ~ .task-status-complete

the user fills in the CS Application section with custom questions
    [Arguments]  ${growthTable}  ${competitionType}
    the user clicks the button/link   link=Application
    # Removing questions from the Assessed questions
    Remove previous rows              jQuery=.no-margin-bottom li:last-of-type button[type="submit"]:contains("Remove")
    the user clicks the button/link   jQuery=li:contains("1.") a  # Click the last question left - which now will be first
    the user is able to configure the new question  How innovative is your project?
    the user clicks the button/link   css=button[name="createQuestion"]
    the user is able to configure the new question  Your approach regarding innovation.
    the user clicks the button/link   css=button[name="createQuestion"]
    the user is able to configure the new question  Your technical approach.
    the user marks the Finance section as complete if it's present  ${growthTable}
    the user marks the Application details section as complete  ${competitionType}
    the user should see the element  jQuery=h1:contains("Application process")  # to check i am on the right page
    the user clicks the button/link   jQuery=button:contains("Done")
    the user clicks the button/link   link=Competition setup
    the user should see the element   jQuery=div:contains("Application") ~ .task-status-complete

the user marks the Finance section as complete if it's present
    [Arguments]  ${growthTable}
    ${status}   ${value}=  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery=.heading-small a:contains("Finances")
    Run Keyword If  '${status}' == 'PASS'  the user fills in the Finances questions  ${growthTable}

the user opts no finances for EOI comp
    the user clicks the button/link    link=Finances
    the user selects the radio button  applicationFinanceType  NONE
    the user clicks the button/link    jQuery=.button:contains("Done")

the assessed questions are marked complete except finances(programme type)
    :FOR   ${ELEMENT}   IN    @{programme_questions}
     \    the user marks each question as complete    ${ELEMENT}
     the user should see the element           jQuery=button:contains("Add question")

the assessed questions are marked complete except finances(sector type)
    :FOR   ${ELEMENT}   IN    @{sector_questions}
     \    the user marks each question as complete    ${ELEMENT}
     the user should see the element           jQuery=button:contains("Add question")

the assessed questions are marked complete(EOI type)
    :FOR   ${ELEMENT}   IN    @{EOI_questions}
     \    the user marks each question as complete    ${ELEMENT}
    the user should see the element      jQuery=button:contains("Add question")

the user marks the Application details section as complete
    [Arguments]  ${compType}
    the user marks each question as complete  Application details
    the user marks each question as complete  Project summary
    Run Keyword If    '${compType}'!='${compType_EOI}'    the user marks each question as complete  Public description
    the user marks each question as complete  Scope

the user marks each question as complete
    [Arguments]  ${question_link}
    the user clicks the button/link  jQuery=h4 a:contains("${question_link}")
    the user clicks the button/link  css=button[type="submit"]
    the user should see the element  jQuery=li:contains("${question_link}") .task-status-complete

the user fills in the Finances questions
    [Arguments]  ${growthTable}
    the user clicks the button/link       link=Finances
    the user selects the radio button     includeGrowthTable  include-growth-table-${growthTable}
    the user enters text to a text field  css=.editor  Those are the rules that apply to Finances
    the user clicks the button/link       css=button[type="submit"]
    the user should see the element       jQuery=li:contains("Finances") .task-status-complete

the user fills in the CS Assessors
    the user clicks the button/link   link=Assessors
    the user clicks the button twice  jQuery=label[for^="assessors"]:contains("3")
    the user should see the element   css=#assessorPay[value="100"]
    the user selects the radio button  hasAssessmentPanel  0
    the user selects the radio button  hasInterviewStage  0
    the user clicks the button/link   jQuery=button:contains("Done")
    the user should see the element   jQuery=dt:contains("How many") + dd:contains("3")
    the user clicks the button/link   link=Competition setup
    the user should see the element   jQuery=div:contains("Assessors") ~ .task-status-complete

the user fills in the Public content and publishes
    [Arguments]  ${extraKeyword}
    # Fill in the Competition information and search
    the user clicks the button/link         link=Competition information and search
    the user enters text to a text field    id=shortDescription  Short public description
    the user enters text to a text field    id=projectFundingRange  Up to £1million
    the user enters text to a text field    css=[aria-labelledby="eligibilitySummary"]  Summary of eligiblity
    the user selects the radio button       publishSetting  public
    the user enters text to a text field    id=keywords  Search, Testing, Robot, ${extraKeyword}
    the user clicks the button/link         jQuery=button:contains("Save and review")
    the user clicks the button/link         jQuery=.button:contains("Return to public content")
    the user should see the element         jQuery=div:contains("Competition information and search") ~ .task-status-complete
    # Fill in the Summary
    the user clicks the button/link         link=Summary
    the user enters text to a text field    css=.editor  This is a Summary description
    the user selects the radio button       fundingType  Grant
    the user enters text to a text field    id=projectSize   10 millions
    the user clicks the button/link         jQuery=button:contains("Save and review")
    the user clicks the button/link         jQuery=.button:contains("Return to public content")
    the user should see the element         jQuery=div:contains("Summary") ~ .task-status-complete
    # Fill in the Eligibility
    the user clicks the button/link         link=Eligibility
    the user enters text to a text field    id=contentGroups[0].heading  Heading 1
    the user enters text to a text field    jQuery=div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery=button:contains("Save and review")
    the user clicks the button/link         jQuery=.button:contains("Return to public content")
    the user should see the element         jQuery=div:contains("Eligibility") ~ .task-status-complete
    # Fill in the Scope
    the user clicks the button/link         link=Scope
    the user enters text to a text field    id=contentGroups[0].heading  Heading 1
    the user enters text to a text field    jQuery=div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery=button:contains("Save and review")
    the user clicks the button/link         jQuery=.button:contains("Return to public content")
    the user should see the element         jQuery=div:contains("Scope") ~ .task-status-complete
    # Save the dates
    the user clicks the button/link  link=Dates
    the user clicks the button/link  jQuery=button:contains("Save and review")
    the user clicks the button/link  jQuery=.button:contains("Return to public content")
    the user should see the element  jQuery=div:contains("Dates") ~ .task-status-complete
    # Fill in the How to apply
    the user clicks the button/link         link=How to apply
    the user enters text to a text field    id=contentGroups[0].heading    Heading 1
    the user enters text to a text field    css=div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery=button:contains("Save and review")
    the user clicks the button/link         jQuery=.button:contains("Return to public content")
    the user should see the element         jQuery=div:contains("How to apply") ~ .task-status-complete
    # Fill in the Supporting information
    the user clicks the button/link         link=Supporting information
    the user enters text to a text field    id=contentGroups[0].heading    Heading 1
    the user enters text to a text field    css=div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery=button:contains("Save and review")
    the user clicks the button/link         jQuery=.button:contains("Return to public content")
    the user should see the element         jQuery=div:contains("Supporting information") ~ .task-status-complete
    # Publish and return
    the user clicks the button/link         jQuery=button:contains("Publish content")

the internal user navigates to public content
    [Arguments]  ${comp}
    the user navigates to the page     ${CA_UpcomingComp}
    the user clicks the button/link    link=${comp}
    the user clicks the button/link    link=Public content

The application list is sorted by
    [Arguments]    ${sorting_factor}
    Select From List    name=sort    ${sorting_factor}

The applications should be sorted by column
    [Arguments]    ${column_number}
    ${row_count}=    get matching xpath count    //*[td]
    @{sorted_column_contents}=    Create List
    : FOR    ${row}    IN RANGE    2    ${row_count}
    \    ${cell_contents}=    get table cell    css=table    ${row}    ${column_number}
    \    append to list    ${sorted_column_contents}    ${cell_contents}
    ${test_sorting_list}=    Copy List    ${sorted_column_contents}
    Sort List    ${test_sorting_list}
    Lists Should Be Equal    ${sorted_column_contents}    ${test_sorting_list}

the user should see all live competitions
    the user should see the element  jQuery=h2:contains("Open")
    the user should see the element  jQuery=h2:contains("Closed")
    the user should see the element  jQuery=h2:contains("In assessment")
    the user should see the element  jQuery=h2:contains("Panel")
    the user should see the element  jQuery=h2:contains("Inform")

the user is able to configure the new question
    [Arguments]  ${questionTitle}
    the user enters text to a text field  id=question.title  Tell us how your project is innovative.
    the user enters text to a text field  id=question.shortTitle  ${questionTitle}
    the user enters text to a text field  id=question.subTitle  Adding value on existing projects is important to InnovateUK.
    the user enters text to a text field  id=question.guidanceTitle  Innovation is crucial to the continuing success of any organization.
    the user enters text to a text field  css=label[for="question.guidance"] + * .editor  Please use Microsoft Word where possible. If you complete your application using Google Docs or any other open source software, this can be incompatible with the application form.
    the user enters text to a text field  id=question.maxWords  500
    the user selects the radio button     question.appendix  1
    click element                         css=label[for="allowed-file-types-PDF"]
    the user clicks the button/link       css=label[for="allowed-file-types-Spreadsheet"]
    the user enters text to a text field  css=label[for="question.appendixGuidance"] + * .editor  You may include an appendix of additional information to provide details of the specific expertise and track record of each project partner and each subcontractor.

    the user selects the radio button     question.scored  1
    the user enters text to a text field  question.scoreTotal  10
    the user selects the radio button     question.writtenFeedback  1
    the user enters text to a text field  question.assessmentGuidanceTitle  Please bare in mind on how well the applicant is able to justify his arguments.
    the user enters text to a text field  question.assessmentGuidance   The better you understand the problem the simpler your explanation is.
    the user enters text to a text field  guidanceRows[0].justification  This the 9-10 Justification
    the user enters text to a text field  guidanceRows[1].justification  This the 7-8 Justification
    the user enters text to a text field  guidanceRows[2].justification  This the 5-6 Justification
    the user enters text to a text field  guidanceRows[3].justification  This the 3-4 Justification
    the user enters text to a text field  guidanceRows[4].justification  This the 1-2 Justification
    the user enters text to a text field  question.assessmentMaxWords  120
    the user clicks the button/link       css=button[type="submit"]
    the user should see the element       jQuery=li:contains("${questionTitle}") .task-status-complete

the user should be able to see the read only view of question correctly
    [Arguments]  ${questionTitle}
    the user clicks the button/link  jQuery=a:contains("${questionTitle}")
    the user should see the element  jQuery=dt:contains("Question heading") + dd:contains("${questionTitle}")
    the user should see the element  jQuery=dt:contains("Question title") + dd:contains("Tell us how your project is innovative.")
    the user should see the element  jQuery=dt:contains("Question subtitle") + dd:contains("Adding value on existing projects is important to InnovateUK.")
    the user should see the element  jQuery=dt:contains("Guidance title") + dd:contains("Innovation is crucial to the continuing success of any organization.")
    the user should see the element  jQuery=dt:contains("Guidance") + dd:contains("Please use Microsoft Word where possible.")
    the user should see the element  jQuery=dt:contains("Max word count") + dd:contains("500")
    the user should see the element  jQuery=dt:contains("Appendix") + dd:contains("Yes")
    the user should see the element  jQuery=dt:contains("Accepted appendix file types") + dd:contains("PDF")
    the user should see the element  jQuery=dt:contains("Accepted appendix file types") + dd:contains("Spreadsheet")
    the user should see the element  jQuery=dt:contains("Appendix guidance") + dd:contains("You may include an appendix of additional information to provide details of the specific expertise and track record of each project partner and each subcontractor.")
    the user should see the element  jQuery=dt:contains("Scored") + dd:contains("Yes")
    the user should see the element  jQuery=dt:contains("Out of") + dd:contains("10")
    the user should see the element  jQuery=dt:contains("Written feedback") + dd:contains("Yes")
    the user should see the element  jQuery=dt:contains("Guidance title") + dd:contains("Please bare in mind on how well the applicant is able to justify his arguments.")
    the user should see the element  jQuery=dt:contains("Guidance") + dd:contains("The better you understand the problem the simpler your explanation is.")
    the user should see the element  jQuery=dt:contains("9-10") + dd:contains("This the 9-10 Justification")
    the user should see the element  jQuery=dt:contains("7-8") + dd:contains("This the 7-8 Justification")
    the user should see the element  jQuery=dt:contains("5-6") + dd:contains("This the 5-6 Justification")
    the user should see the element  jQuery=dt:contains("3-4") + dd:contains("This the 3-4 Justification")
    the user should see the element  jQuery=dt:contains("1-2") + dd:contains("This the 1-2 Justification")
    the user should see the element  jQuery=dt:contains("Max word count") + dd:contains("120")
    the user clicks the button/link  link=Return to application questions

the competition moves to Open state
    [Arguments]  ${competitionId}
    ${yesterday} =  get yesterday
    Connect to Database  @{database}
    execute sql string  UPDATE `${database_name}`.`milestone` SET `date`='${yesterday}' WHERE `competition_id`='${competitionId}' AND `type`='OPEN_DATE';

# Note here we are passing the comp title not the comp ID to update the correct comp milestone
the competition is open
    [Arguments]  ${compTitle}
    Connect to Database  @{database}
    change the open date of the competition in the database to one day before  ${compTitle}

moving competition to Closed
    [Arguments]  ${compID}
    ${yesterday} =  get yesterday
    Connect to Database  @{database}
    execute sql string   UPDATE `${database_name}`.`milestone` SET `date`='${yesterday}' WHERE `type`='SUBMISSION_DATE' AND `competition_id`='${compID}';

making the application a successful project
    [Arguments]  ${compID}  ${appTitle}
    the user navigates to the page      ${server}/management/competition/${compID}
    the user clicks the button/link  css=button[type="submit"][formaction$="notify-assessors"]
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  page should contain element  css=button[type="submit"][formaction$="close-assessment"]
    Run Keyword If  '${status}' == 'PASS'  the user clicks the button/link  css=button[type="submit"][formaction$="close-assessment"]
    Run Keyword If  '${status}' == 'FAIL'  Run keywords    the user clicks the button/link    css=button[type="submit"][formaction$="notify-assessors"]
    ...    AND  the user clicks the button/link    css=button[type="submit"][formaction$="close-assessment"]
    run keyword and ignore error without screenshots     the user clicks the button/link    css=button[type="submit"][formaction$="close-assessment"]
    the user clicks the button/link  link=Input and review funding decision
    the user clicks the button/link  jQuery=tr:contains("${appTitle}") label
    the user clicks the button/link  css=[type="submit"][value="FUNDED"]
    the user navigates to the page   ${server}/management/competition/${compID}/manage-funding-applications
    the user clicks the button/link  jQuery=tr:contains("${appTitle}") label
    the user clicks the button/link  css=[name="write-and-send-email"]
    the internal sends the descision notification email to all applicants  Successful!

moving competition to Project Setup
    [Arguments]   ${compID}
    the user navigates to the page   ${server}/management/competition/${compID}
    the user clicks the button/link  css=button[type="submit"][formaction$="release-feedback"]

The project finance user is able to download the Overheads file
    [Arguments]   ${ProjectID}  ${organisationId}
    the user downloads the file                   ${internal_finance_credentials["email"]}  ${server}/project-setup-management/project/${ProjectID}/finance-check/organisation/${organisationId}/eligibility  ${DOWNLOAD_FOLDER}/${excel_file}
    remove the file from the operating system     ${excel_file}