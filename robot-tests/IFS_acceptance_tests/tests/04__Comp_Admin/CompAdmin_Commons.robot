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
    the user clicks the button/link                      link=Initial details
    the user enters text to a text field                 css=#title  From new Competition to New Application
    the user selects the option from the drop-down menu  Programme  id=competitionTypeId
    the user selects the option from the drop-down menu  Emerging and enablingtechnologies  id=innovationSectorCategoryId
    the user selects the option from the drop-down menu  Robotics and AS  css=select[id^=innovationAreaCategory]
    ${day} =  get tomorrow day
    the user enters text to a text field                 css=#openingDateDay  ${day}
    ${month} =  get tomorrow month
    the user enters text to a text field                 css=#openingDateMonth  ${month}
    ${year} =  get tomorrow year
    the user enters text to a text field                 css=#openingDateMonth  ${year}
    the user selects the option from the drop-down menu  Ian Cooper  id=leadTechnologistUserId
    the user selects the option from the drop-down menu  Toby Reader  id=executiveUserId
    the user clicks the button/link                      jQuery=.button:contains("Done")
    the user clicks the button/link                      link=Competition setup