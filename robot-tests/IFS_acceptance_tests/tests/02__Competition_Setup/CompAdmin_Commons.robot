*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***
#CA = Competition Administration
${CA_UpcomingComp}   ${server}/management/dashboard/upcoming
${CA_Live}           ${server}/management/dashboard/live
${compType_Programme}  Programme
${compType_Sector}     Sector
${compType_Generic}    Generic


*** Keywords ***
the user edits the assessed question information
    the user enters text to a text field    id=question.maxWords    100
    the user enters text to a text field    id=question.scoreTotal    100
    the user enters text to a text field    id=question.assessmentGuidance    Business opportunity guidance
    the user clicks the button/link    jQuery=button:contains("+Add guidance row")
    the user enters text to a text field    id=guidanceRows[5].scoreFrom    11
    the user enters text to a text field    id=guidanceRows[5].scoreTo    12
    the user enters text to a text field    id=guidanceRows[5].justification    This is a justification
    the user clicks the button/link    id=remove-guidance-row-2

the user sees the correct assessed question information
    the user should see the text in the page    Assessment of this question
    the user should see the text in the page    Business opportunity guidance
    the user should see the text in the page    11
    the user should see the text in the page    12
    the user should see the text in the page    This is a justification
    the user should see the text in the page    100
    the user should see the text in the page    Written feedback
    the user should see the text in the page    Scored
    the user should see the text in the page    Out of
    the user should not see the text in the page    The business opportunity is plausible

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
    textfield should contain              css=input[name="competitionCode"]  18
    the user clicks the button/link       jQuery=button:contains("Done")
    the user clicks the button/link       link=Competition setup
    the user should see the element       jQuery=div:contains("Funding information") ~ .task-status-complete

the user fills in the CS Eligibility
    [Arguments]  ${organisationType}
    the user clicks the button/link   link=Eligibility
    the user clicks the button twice  css=label[for="single-or-collaborative-collaborative"]
    the user clicks the button twice  css=label[for="research-categories-33"]
    the user clicks the button twice  css=label[for="lead-applicant-type-${organisationType}"]
    the user selects the option from the drop-down menu  1  researchParticipation
    the user clicks the button/link  css=label[for="comp-resubmissions-yes"]
    the user clicks the button/link  css=label[for="comp-resubmissions-yes"]
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element   jQuery=div:contains("Eligibility") ~ .task-status-complete
    #Elements in this page need double clicking

the user fills in the CS Milestones
    [Arguments]  ${month}  ${nextMonth}  ${nextyear}
    the user clicks the button/link       link=Milestones
    the user enters text to a text field  jQuery=th:contains("Briefing event") ~ td.day input  2
    the user enters text to a text field  jQuery=th:contains("Briefing event") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Briefing event") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Submission date") ~ td.day input  2
    the user enters text to a text field  jQuery=th:contains("Submission date") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Submission date") ~ td.year input  ${nextyear}
    # the below dates need to be in a future date
    the user enters text to a text field  jQuery=th:contains("Allocate assessors") ~ td.day input  3
    the user enters text to a text field  jQuery=th:contains("Allocate assessors") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Allocate assessors") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Assessor briefing") ~ td.day input  3
    the user enters text to a text field  jQuery=th:contains("Assessor briefing") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Assessor briefing") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Assessor accepts") ~ td.day input  3
    the user enters text to a text field  jQuery=th:contains("Assessor accepts") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Assessor accepts") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Assessor deadline") ~ td.day input  3
    the user enters text to a text field  jQuery=th:contains("Assessor deadline") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Assessor deadline") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Line draw") ~ td.day input  4
    the user enters text to a text field  jQuery=th:contains("Line draw") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Line draw") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Assessment panel") ~ td.day input  4
    the user enters text to a text field  jQuery=th:contains("Assessment panel") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Assessment panel") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Panel date") ~ td.day input  4
    the user enters text to a text field  jQuery=th:contains("Panel date") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Panel date") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Funders panel") ~ td.day input  4
    the user enters text to a text field  jQuery=th:contains("Funders panel") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Funders panel") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Notifications") ~ td.day input  4
    the user enters text to a text field  jQuery=th:contains("Notifications") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Notifications") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Release feedback") ~ td.day input  4
    the user enters text to a text field  jQuery=th:contains("Release feedback") ~ td.month input  ${nextMonth}
    the user enters text to a text field  jQuery=th:contains("Release feedback") ~ td.year input  ${nextyear}
    the user clicks the button/link       jQuery=button:contains("Done")
    the user clicks the button/link       link=Competition setup
    the user should see the element       jQuery=div:contains("Milestones") ~ .task-status-complete

the user marks the Application as done
    [Arguments]  ${growthTable}
    the user clicks the button/link  link=Application
    the application questions are marked complete except finances
    the user fills in the Finances questions  ${growthTable}
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element  jQuery=div:contains("Application") ~ .task-status-complete

the application questions are marked complete except finances
    [Documentation]  IFS-743
    [Tags]
    the user marks each question as complete  Application details
    the user marks each question as complete  Project summary
    the user marks each question as complete  Public description
    the user marks each question as complete  Scope
    the user marks each question as complete  Business opportunity
    the user marks each question as complete  Potential market
    the user marks each question as complete  Project exploitation
    the user marks each question as complete  Economic benefit
    the user marks each question as complete  Technical approach
    the user marks each question as complete  Innovation
    the user marks each question as complete  Risks
    the user marks each question as complete  Project team
    the user marks each question as complete  Funding
    the user marks each question as complete  Adding value

the user marks each question as complete
    [Arguments]  ${question_link}
    the user clicks the button/link  link=${question_link}
    run keyword and ignore error without screenshots   the user clicks the button/link  jQuery=.button:contains("Done")
    run keyword and ignore error without screenshots   the user clicks the button/link  css=.button[value="Done"]

the user fills in the Finances questions
    [Arguments]  ${growthTable}
    the user clicks the button/link       link=Finances
    the user clicks the button/link       jQuery=.button:contains("Done")
    the user selects the radio button     includeGrowthTable  include-growth-table-${growthTable}
    the user enters text to a text field  css=.editor  Those are the rules that apply to Finances
    the user clicks the button/link       css=button[type="submit"]

the user fills in the CS Assessors
    the user clicks the button/link   link=Assessors
    the user clicks the button twice  jQuery=label[for^="assessors"]:contains("3")
    the user should see the element   css=#assessorPay[value="100"]
    the user selects the radio button  hasAssessmentPanel  0
    the user selects the radio button  hasInterviewStage  0
    the user clicks the button/link   jQuery=button:contains("Done")
#    the user should see the element   jQuery=dt:contains("How many") + dd:contains("3")
# Plz uncomment this line TODO due to IFS-1527
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

Change the open date of the Competition in the database to one day before
    [Arguments]  ${competition}
    ${yesterday} =  get yesterday
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${yesterday}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'OPEN_DATE';

Change the close date of the Competition in the database to tomorrow
    [Arguments]  ${competition}
    ${tomorrow} =  get tomorrow
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${tomorrow}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'SUBMISSION_DATE';

Change the close date of the Competition in the database to a fortnight
    [Arguments]  ${competition}
    ${fortnight} =  get fortnight
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${fortnight}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'SUBMISSION_DATE';

Change the close date of the Competition in the database to fifteen days
    [Arguments]  ${competition}
    ${fifteen} =  get fifteen days
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${fifteen}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'SUBMISSION_DATE';


Change the close date of the Competition in the database to thirteen days
    [Arguments]  ${competition}
    ${thirteen} =  get thirteen days
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${thirteen}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'SUBMISSION_DATE';

Change the open date of the Competition in the database to tomorrow
    [Arguments]  ${competition}
    ${tomorrow} =  get tomorrow
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${tomorrow}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'OPEN_DATE';

Reset the open and close date of the Competition in the database
    [Arguments]  ${competition}
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='2018-02-24 11:00:00' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'OPEN_DATE';
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='2018-03-16 11:00:00' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'SUBMISSION_DATE';

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
