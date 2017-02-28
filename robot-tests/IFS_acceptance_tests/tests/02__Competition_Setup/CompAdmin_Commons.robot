*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***
#CA = Competition Administration
${CA_UpcomingComp}   ${server}/management/dashboard/upcoming
${CA_Live}           ${server}/management/dashboard/live

*** Keywords ***
the user edits the assessed question information
    the user enters text to a text field    id=question.maxWords    100
    the user enters text to a text field    id=question.scoreTotal    100
    the user enters text to a text field    id=question.assessmentGuidance    Business opportunity guidance
    the user clicks the button/link    jQuery=Button:contains("+Add guidance row")
    the user enters text to a text field    id=guidancerow-5-scorefrom    11
    the user enters text to a text field    id=guidancerow-5-scoreto    12
    the user enters text to a text field    id=guidancerow-5-justification    This is a justification
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
    [Arguments]  ${compTitle}  ${day}  ${month}  ${year}
    the user clicks the button/link                      link=Initial details
    the user enters text to a text field                 css=#title  ${compTitle}
    the user selects the option from the drop-down menu  Programme  id=competitionTypeId
    the user selects the option from the drop-down menu  Emerging and enabling technologies  id=innovationSectorCategoryId
    the user selects the option from the drop-down menu  Robotics and AS  css=select[id^=innovationAreaCategory]
    the user enters text to a text field                 css=#openingDateDay  ${day}
    the user enters text to a text field                 css=#openingDateMonth  ${month}
    the user enters text to a text field                 css=#openingDateYear  ${year}
    the user selects the option from the drop-down menu  Ian Cooper  id=leadTechnologistUserId
    the user selects the option from the drop-down menu  Robert Johnson  id=executiveUserId
    the user clicks the button/link                      jQuery=button:contains("Done")
    the user clicks the button/link                      link=Competition setup
    the user should see the element                      jQuery=img[title$="is done"] + h3:contains("Initial details")

the user fills in the CS Funding Information
    the user clicks the button/link       link=Funding information
    the user enters text to a text field  id=funders0.funder  FunderName FamilyName
    the user enters text to a text field  id=0-funderBudget  142424242
    the user enters text to a text field  id=pafNumber  2424
    the user enters text to a text field  id=budgetCode  Ch0col@73
    the user enters text to a text field  id=activityCode  133t
    the user clicks the button/link       jQuery=button:contains("Generate code")
    the user clicks the button/link       jQuery=button:contains("Done")
    the user clicks the button/link       link=Competition setup

the user fills in the CS Eligibility
    the user clicks the button/link  link=Eligibility
    the user clicks the button/link  jQuery=label[for="single-or-collaborative-collaborative"]
    the user clicks the button/link  jQuery=label[for="single-or-collaborative-collaborative"]
    the user clicks the button/link  jQuery=label[for="research-categories-33"]
    the user clicks the button/link  jQuery=label[for="research-categories-33"]
    the user clicks the button/link  jQuery=label[for="lead-applicant-type-research"]
    the user clicks the button/link  jQuery=label[for="lead-applicant-type-research"]
    the user selects the option from the drop-down menu  1  researchParticipation
    the user clicks the button/link  jQuery=label[for="comp-resubmissions-yes"]
    the user clicks the button/link  jQuery=label[for="comp-resubmissions-yes"]
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element   jQuery=img[title$="is done"] + h3:contains("Eligibility")
    #Elements in this page need double clicking

the user fills in the CS Milestones
    [Arguments]  ${day}  ${month}  ${nextyear}
    the user clicks the button/link       link=Milestones
    the user enters text to a text field  jQuery=th:contains("Briefing event") ~ td.day input    ${day}
    the user enters text to a text field  jQuery=th:contains("Briefing event") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Briefing event") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Submission date") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Submission date") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Submission date") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Allocate assessors") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Allocate assessors") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Allocate assessors") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Assessor briefing") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Assessor briefing") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Assessor briefing") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Assessor accepts") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Assessor accepts") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Assessor accepts") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Assessor deadline") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Assessor deadline") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Assessor deadline") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Line draw") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Line draw") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Line draw") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Assessment panel") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Assessment panel") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Assessment panel") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Panel date") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Panel date") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Panel date") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Funders panel") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Funders panel") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Funders panel") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Notifications") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Notifications") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Notifications") ~ td.year input  ${nextyear}
    the user enters text to a text field  jQuery=th:contains("Release feedback") ~ td.day input  ${day}
    the user enters text to a text field  jQuery=th:contains("Release feedback") ~ td.month input  ${month}
    the user enters text to a text field  jQuery=th:contains("Release feedback") ~ td.year input  ${nextyear}
    the user clicks the button/link       jQuery=button:contains("Done")
    the user clicks the button/link       link=Competition setup

the user marks the Application as done
    the user clicks the button/link  link=Application
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element  jQuery=img[title$="is done"] + h3:contains("Application")


the user fills in the CS Assessors
    the user clicks the button/link  link=Assessors
    the user clicks the button/link  jQuery=label[for="assessors-62"]
    the user should see the element  css=#assessorPay[value="100"]
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element  jQuery=img[title$="is done"] + h3:contains("Assessors")


Change the open date of the Competition in the database to one day before
    [Arguments]  ${competition}
    ${yesterday} =  get yesterday
    execute sql string  UPDATE `${database_name}`.`milestone` INNER JOIN `${database_name}`.`competition` ON `${database_name}`.`milestone`.`competition_id` = `${database_name}`.`competition`.`id` SET `${database_name}`.`milestone`.`DATE`='${yesterday}' WHERE `${database_name}`.`competition`.`name`='${competition}' and `${database_name}`.`milestone`.`type` = 'OPEN_DATE';

get comp id from comp title
    [Arguments]  ${title}
    ${result} =  query  SELECT `id` FROM `${database_name}`.`competition` WHERE `name`='${title}';
    Log  ${result}
    # the result of this query looks like ((13,),) so you need get the value array[0][0]
    ${result} =  get from list  ${result}  0
    ${competitionId} =  get from list  ${result}  0
    Log  ${competitionId}
    [Return]  ${competitionId}

the internal user navigates to public content
    [Arguments]  ${comp}
    the user navigates to the page      ${CA_UpcomingComp}
    the user clicks the button/link    link=${comp}
    the user clicks the button/link    link=Public content