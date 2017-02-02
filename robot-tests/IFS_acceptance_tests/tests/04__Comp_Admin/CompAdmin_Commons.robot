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

the user fills in the Initial details
    [Arguments]  ${day}  ${month}  ${year}
    the user clicks the button/link                      link=Initial details
    the user enters text to a text field                 css=#title  From new Competition to New Application
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

the user fills in the Funding Information
    the user clicks the button/link       link=Funding information
    the user enters text to a text field  id=funders0.funder  FunderName FamilyName
    the user enters text to a text field  id=0-funderBudget  142424242
    the user enters text to a text field  id=pafNumber  2424
    the user enters text to a text field  id=budgetCode  Ch0col@73
    the user enters text to a text field  id=activityCode  133t
    the user clicks the button/link       jQuery=button:contains("Generate code")
    the user clicks the button/link       jQuery=button:contains("Done")
    the user clicks the button/link       link=Competition setup

the user fills in the Eligibility
    the user clicks the button/link  link=Eligibility
    the user clicks the button/link  jQuery=label[for="single-or-collaborative-collaborative"]
    the user clicks the button/link  jQuery=label[for="research-categories-33"]
    the user clicks the button/link  jQuery=label[for="lead-applicant-type-research"]
    the user selects the option from the drop-down menu  1  researchParticipation
    the user clicks the button/link  jQuery=label[for="comp-resubmissions-yes"]
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup

the user fills in the Milestones
    [Arguments]  ${day}  ${month}  ${nextyear}
    the user clicks the button/link       link=Milestones
    the user enters text to a text field  th:contains("Briefing event") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Briefing event") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Briefing event") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Submission date") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Submission date") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Submission date") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Allocate assessors") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Allocate assessors") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Allocate assessors") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Assessor briefing") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Assessor briefing") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Assessor briefing") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Assessor accepts") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Assessor accepts") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Assessor accepts") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Assessor deadline") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Assessor deadline") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Assessor deadline") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Line draw") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Line draw") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Line draw") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Assessment panel") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Assessment panel") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Assessment panel") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Panel date") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Panel date") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Panel date") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Funders panel") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Funders panel") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Funders panel") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Notifications") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Notifications") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Notifications") ~ td.year  ${nextyear}
    the user enters text to a text field  th:contains("Release feedback") ~ td.day  ${day}
    the user enters text to a text field  th:contains("Release feedback") ~ td.month  ${month}
    the user enters text to a text field  th:contains("Release feedback") ~ td.year  ${nextyear}
    the user clicks the button/link       jQuery=button:contains("Done")
    the user clicks the button/link       link=Competition setup