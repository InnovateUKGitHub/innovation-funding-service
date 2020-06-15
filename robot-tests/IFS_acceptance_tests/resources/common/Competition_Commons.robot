*** Settings ***
Resource    ../../resources/defaultResources.robot

*** Variables ***
#CA = Competition Administration
${CA_UpcomingComp}   ${server}/management/dashboard/upcoming
${CA_Live}           ${server}/management/dashboard/live
${Non_Ifs_Comp}      Webtest Non IFS Comp 20

*** Keywords ***
The competition admin creates competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${stateAid}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user navigates to the page                          ${CA_UpcomingComp}
    the user clicks the button/link                         jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details                ${competition}  ${month}  ${nextyear}  ${compType}  ${stateAid}  ${fundingType}
    Run Keyword If  '${fundingType}' == 'PROCUREMENT'  the user selects procurement Terms and Conditions
    ...  ELSE  the user selects the Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility            ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}  # 1 means 30%
    the user selects the organisational eligibility to no   false
    the user fills in the CS Milestones                     ${completionStage}   ${month}   ${nextyear}
    Run Keyword If  '${fundingType}' == 'PROCUREMENT'  the user marks the procurement application as done      ${projectGrowth}  ${compType}
    ...  ELSE  the user marks the application as done       ${projectGrowth}  ${compType}
    the user fills in the CS Assessors
    Run Keyword If  '${fundingType}' == 'PROCUREMENT'  the user select no documents
    ...  ELSE  the user fills in the CS Documents in other projects
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview
    the user clicks the button/link                         jQuery = a:contains("Complete")
    the user clicks the button/link                         jQuery = button:contains('Done')
    the user navigates to the page                          ${CA_UpcomingComp}
    the user should see the element                         jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user select no documents
    the user clicks the button/link          link = Documents
    the user should not see the element      jQuery = input:checked + label:contains("Collaboration agreement")
    the user should not see the element      jQuery = input:checked + label:contains("Exploitation plan")
    the user should see the element          jQuery = .govuk-warning-text:contains("You have not selected any documents. The project manager will not be required to upload documents.")
    the user clicks the button/link          jQuery = label:contains("Collaboration agreement")
    the user should not see the element      jQuery = .govuk-warning-text:contains("You have not selected any documents. The project manager will not be required to upload documents.")
    the user clicks the button/link          jQuery = label:contains("Collaboration agreement")
    the user should see the element          jQuery = .govuk-warning-text:contains("You have not selected any documents. The project manager will not be required to upload documents.")
    the user clicks the button/link          jQuery = label:contains("Exploitation plan")
    the user should not see the element      jQuery = .govuk-warning-text:contains("You have not selected any documents. The project manager will not be required to upload documents.")
    the user clicks the button/link          jQuery = label:contains("Exploitation plan")
    the user should see the element          jQuery = .govuk-warning-text:contains("You have not selected any documents. The project manager will not be required to upload documents.")
    the user clicks the button/link          id = doneButton

the user edits the assessed question information
    the user enters text to a text field    id = question.maxWords    100
    the user enters text to a text field    id = question.scoreTotal  10
    the user enters text to a text field    id = question.assessmentGuidance    Business opportunity guidance
    the user clicks the button/link         jQuery = button:contains("+Add guidance row")
    the user enters text to a text field    id = guidanceRows[5].scoreFrom    0
    the user enters text to a text field    id = guidanceRows[5].scoreTo    1
    the user enters text to a text field    id = guidanceRows[5].justification    This is a justification
    the user clicks the button/link         id = remove-guidance-row-2

the user sees the correct read only view of the question
    the user should see the element    jQuery = dt:contains("Max word count") + dd:contains("100")
    the user should see the element    jQuery = dd p:contains("Business opportunity guidance")
    the user should see the element    jQuery = dt:contains("0-1") + dd:contains("This is a justification")
    the user should see the element    jQuery = dt:contains("Max word count") + dd:contains("10")
    the user should not see the element      jQuery = dt:contains("5-6") ~ dd:contains("The business opportunity is plausible")

the user fills in the CS Initial details
    [Arguments]  ${compTitle}  ${month}  ${nextyear}  ${compType}  ${stateAid}  ${fundingType}
    the user clicks the button/link                      link = Initial details
    the user enters text to a text field                 css = #title  ${compTitle}
    the user selects the radio button                    fundingType  ${fundingType}
    the user selects the option from the drop-down menu  ${compType}  id = competitionTypeId
    the user selects the option from the drop-down menu  Emerging and enabling  id = innovationSectorCategoryId
    the user selects the option from the drop-down menu  Robotics and autonomous systems  css = select[id^=innovationAreaCategory]
    the user enters text to a text field                 css = #openingDateDay  1
    the user enters text to a text field                 css = #openingDateMonth  ${month}
    the user enters text to a text field                 css = #openingDateYear  ${nextyear}
    the user selects the option from the drop-down menu  Ian Cooper  id = innovationLeadUserId
    the user selects the option from the drop-down menu  Robert Johnson  id = executiveUserId
    the user clicks the button twice                     css = label[for="stateAid${stateAid}"]
    the user clicks the button/link                      jQuery = button:contains("Done")
    the user clicks the button/link                      link = Competition setup
    the user should see the element                      jQuery = div:contains("Initial details") ~ .task-status-complete

the user selects procurement Terms and Conditions
    the user clicks the button/link                                     link = Terms and conditions
    the user clicks the button/link                                     jQuery = label:contains("Procurement")
    the user performs procurement Terms and Conditions validations
    the user uploads the file                                           css = .inputfile  ${valid_pdf}
    the user clicks the button/link                                     jQuery = button:contains("Done")
    the user clicks the button/link                                     link = Competition setup
    the user should see the element                                     jQuery = li:contains("Terms and conditions") .task-status-complete

the user performs procurement Terms and Conditions validations
    the user clicks the button/link                 jQuery = button:contains("Done")
    the user should see a field and summary error   Upload a terms and conditions document.
    the user uploads the file                       css = .inputfile  ${ods_file}
    the user should see the element                 jQuery = :contains("${wrong_filetype_validation_error}")

the user selects the Terms and Conditions
    the user clicks the button/link      link = Terms and conditions
    the user clicks the button/link      jQuery = button:contains("Done")
    the user clicks the button/link      link = Competition setup
    the user should see the element      jQuery = li:contains("Terms and conditions") .task-status-complete

the user fills in the CS Funding Information
    the user clicks the button/link       link = Funding information
    the user clicks the button/link       jQuery = button:contains("Generate code")
    the user enters text to an autocomplete field  id = funders[0].funder    Aerospace Technology Institute (ATI)
    the user enters text to a text field  id = funders[0].funderBudget  142424242
    the user enters text to a text field  id = pafNumber  2424
    the user enters text to a text field  id = budgetCode  Ch0col@73
    the user enters text to a text field  id = activityCode  133t
    textfield should contain              css = input[name="competitionCode"]  21
    the user clicks the button/link       jQuery = button:contains("Done")
    the user clicks the button/link       link = Competition setup
    the user should see the element       jQuery = div:contains("Funding information") ~ .task-status-complete

the user fills in the CS Project eligibility
    [Arguments]  ${organisationType}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user clicks the button/link    link = Project eligibility
    the user clicks the button twice   css = label[for="single-or-collaborative-${collaborative}"]
    the user selects the radio button  researchCategoriesApplicable    ${researchCategory}
    Run Keyword If  '${researchCategory}' == 'false'  the user selects the option from the drop-down menu  10%  fundingLevelPercentage
    Run Keyword If  '${researchCategory}' == 'true'   the user clicks the button twice  css = label[for="research-categories-33"]
    the user clicks the button twice   css = label[for="lead-applicant-type-${organisationType}"]
    the user selects Research Participation if required  ${researchParticipation}
    the user selects the radio button   resubmission  yes
    Run Keyword If  '${researchCategory}' == 'true'   the user clicks the button twice  css = label[for="comp-overrideFundingRules-no"]
    the user clicks the button/link    jQuery = button:contains("Done")
    the user clicks the button/link    link = Competition setup
    the user should see the element    jQuery = div:contains("Project eligibility") ~ .task-status-complete
    #Elements in this page need double clicking

the user selects Research Participation if required
    [Arguments]  ${percentage}
    ${status}  ${value} =   Run Keyword And Ignore Error Without Screenshots  the user should see the element  id = researchParticipationAmountId
    Run Keyword If  '${status}' == 'PASS'  the user selects the index from the drop-down menu  ${percentage}  researchParticipation
    Run Keyword If  '${status}' == 'FAIL'  the user should not see the element  id = researchParticipation

the user fills in the CS Milestones
    [Arguments]  ${completionStage}  ${month}  ${nextyear}
    the user clicks the button/link    link = Milestones
    ${status}  ${value} =   Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery = a:contains("Next")
    Run Keyword If  '${status}' == 'PASS'  the user clicks the button/link  jQuery = a:contains("Next")
    Run Keyword If  '${status}' == 'FAIL'  the user selects the radio button  selectedCompletionStage  ${completionStage}
    Run Keyword If  '${status}' == 'FAIL'  the user clicks the button/link  jQuery = button:contains("Done")
    ${i} =  Set Variable   1
     :FOR   ${ELEMENT}   IN    @{milestones}
      \    the user enters text to a text field  jQuery = th:contains("${ELEMENT}") ~ td.day input  ${i}
      \    the user enters text to a text field  jQuery = th:contains("${ELEMENT}") ~ td.month input  ${month}
      \    the user enters text to a text field  jQuery = th:contains("${ELEMENT}") ~ td.year input  ${nextyear}
      \    ${i} =   Evaluate   ${i} + 1
    the user clicks the button/link              jQuery = button:contains("Done")
    the user clicks the button/link              link = Competition setup
    the user should see the element              jQuery = div:contains("Milestones") ~ .task-status-complete

the user fills in the CS Documents in other projects
    the user clicks the button/link          link = Documents
    the user clicks the button/link          link = Add document type
    the user enters text to a text field     id = title    Test document type
    the user clicks the button/link          jQuery = span:contains("PDF")
    the user clicks the button/link          jQuery = span:contains("Spreadsheet")
    the user enters text to a text field     css = .editor    Guidance test.
    the user clicks the button/link          jQuery = button:contains('Done')
    the user should see the element          jQuery = span:contains("Test document type")
    the user clicks the button/link          link = Competition setup

the user marks the procurement application as done
    [Arguments]  ${growthTable}  ${comp_type}
    the user clicks the button/link                               link = Application
    the user marks the Application details section as complete    ${comp_type}
    the assessed questions are marked as complete(procurement)    ${growthTable}

the user marks the Application as done
    [Arguments]  ${growthTable}  ${comp_type}
    the user clicks the button/link                               link = Application
    the user marks the Application details section as complete    ${comp_type}
    Run Keyword If  '${comp_type}' == 'Generic' or '${comp_type}' == '${compType_APC}'  the user fills in the CS Application section with custom questions  ${growthTable}  ${comp_type}
    ...    ELSE  the user marks the Assessed questions as complete             ${growthTable}  ${comp_type}

The user removes the Project details questions and marks the Application section as done
    [Arguments]  ${growthTable}  ${comp_type}
    the user clicks the button/link                      link = Application
    the user marks each question as complete             Application details
    the user removes some of the Project details questions
    the user marks the Assessed questions as complete    ${growthTable}  ${comp_type}

the user marks the Assessed questions as complete
    [Arguments]  ${growthTable}  ${comp_type}
    Run Keyword If  '${comp_type}' == 'Sector'   the assessed questions are marked complete except finances(sector type)
    Run Keyword If  '${comp_type}' == 'Programme'    the assessed questions are marked complete except finances(programme type)
    Run keyword If  '${comp_type}' == '${compType_EOI}'  the assessed questions are marked complete(EOI type)
    Run Keyword If  '${comp_type}' == '${compType_EOI}'  the user opts no finances for EOI comp
    ...    ELSE   the user fills in the Finances questions  ${growthTable}  false  true
    the user clicks the button/link  jQuery = button:contains("Done")
    the user clicks the button/link  link = Competition setup
    the user should see the element  jQuery = div:contains("Application") ~ .task-status-complete

the user fills in the CS Application section with custom questions
    [Arguments]  ${growthTable}  ${competitionType}
    # Removing questions from the Assessed questions
    Remove previous rows               jQuery = .govuk-heading-s:contains("Assessed questions") ~ ul li:last-of-type button[type="submit"]:contains("Remove")
    the user clicks the button/link    jQuery = li:contains("1.") a  # Click the last question left - which now will be first
    the user is able to configure the new question  How innovative is your project?
    the user clicks the button/link    css = button[name="createQuestion"]
    the user is able to configure the new question  Your approach regarding innovation.
    the user clicks the button/link    css = button[name="createQuestion"]
    the user is able to configure the new question  Your technical approach.
    the user marks the Finance section as complete if it's present    ${growthTable}
    the user should see the element    jQuery = h1:contains("Application process")  # to check i am on the right page
    the user clicks the button/link    jQuery = button:contains("Done")
    the user clicks the button/link    link = Competition setup
    the user should see the element    jQuery = div:contains("Application") ~ .task-status-complete

the user marks the Finance section as complete if it's present
    [Arguments]  ${growthTable}
    ${status}   ${value} =   Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery = .govuk-heading-s a:contains("Finances")
    Run Keyword If  '${status}' == 'PASS'  the user fills in the Finances questions  ${growthTable}  true  false

the user opts no finances for EOI comp
    the user clicks the button/link    link = Finances
    the element should be disabled     application-finance-standard
    the user selects the radio button  applicationFinanceType  NO_FINANCES
    the user clicks the button/link    jQuery = button:contains("Done")

the assessed questions are marked complete except finances(programme type)
    :FOR   ${ELEMENT}   IN    @{programme_questions}
     \    the user marks each question as complete    ${ELEMENT}
     the user should see the element           jQuery = button:contains("Add question")

the assessed questions are marked complete except finances(sector type)
    :FOR   ${ELEMENT}   IN    @{sector_questions}
     \    the user marks each question as complete    ${ELEMENT}
     the user should see the element           jQuery = button:contains("Add question")

the assessed questions are marked complete(EOI type)
    :FOR   ${ELEMENT}   IN    @{EOI_questions}
     \    the user marks each question as complete    ${ELEMENT}
    the user should see the element      jQuery = button:contains("Add question")

the user marks the Application details section as complete
    [Arguments]  ${compType}
    the user marks each question as complete  Application details
    the user marks each question as complete  Project summary
    Run Keyword If    '${compType}'!= '${compType_EOI}'    the user marks each question as complete  Public description
    the user marks each question as complete  Scope

the user marks each question as complete
    [Arguments]  ${question_link}
    the user clicks the button/link  jQuery = h4 a:contains("${question_link}")
    the user clicks the button/link  jQuery = button:contains('Done')
    the user should see the element  jQuery = li:contains("${question_link}") .task-status-complete

the assessed questions are marked as complete(procurement)
    [Arguments]   ${growthTable}
    :FOR   ${ELEMENT}   IN    @{programme_questions}
     \    the user marks each procurement question as complete    ${ELEMENT}
     the user should see the element           jQuery = button:contains("Add question")
     the user fills in the Finances questions  ${growthTable}  false  true
     the user clicks the button/link           jQuery = button:contains("Done")
     the user clicks the button/link           link = Competition setup

the user marks each procurement question as complete
    [Arguments]  ${question_link}
    the user clicks the button/link        jQuery = h4 a:contains("${question_link}")
    the user selects the radio button      question.templateDocument  1
    the user enters text to a text field   id = question.templateTitle   ${question_link}
    the user uploads the file              css = input[id="templateDocumentFile"]   ${ods_file}
    the user selects the checkbox          question.allowedTemplateResponseFileTypes1
    the user clicks the button/link        jQuery = button:contains('Done')
    the user should see the element        jQuery = li:contains("${question_link}") .task-status-complete

the user fills in the Finances questions
    [Arguments]  ${growthTable}  ${jes}  ${organisation}
    the user clicks the button/link       link = Finances
    the user clicks the button twice      css = label[for = "include-growth-table-${growthTable}"]
    the user selects the radio button     applicationFinanceType  STANDARD
    the user selects the radio button     includeYourOrganisationSection  ${organisation}
    the user selects the radio button     includeJesForm  ${jes}
    the user enters text to a text field  css = .editor  Those are the rules that apply to Finances
    the user clicks the button/link       jQuery = button:contains('Done')
    the user clicks the button/link       link = Finances
    the user clicks the button/link       link = Application
    the user should see the element       jQuery = li:contains("Finances") .task-status-complete

the user fills in the CS Assessors
    the user clicks the button/link    link = Assessors
    the user clicks the button twice   jQuery = label[for^="assessors"]:contains("3")
    the user should see the element    css = #assessorPay[value="100"]
    the user selects the radio button  hasAssessmentPanel  0
    the user selects the radio button  hasInterviewStage  0
    the user selects the radio button  averageAssessorScore  0
    the user clicks the button/link    jQuery = button:contains("Done")
    the user should see the element    jQuery = dt:contains("How many") + dd:contains("3")
    the user clicks the button/link    link = Competition setup
    the user should see the element    jQuery = div:contains("Assessors") ~ .task-status-complete

the user fills in the Public content and publishes
    [Arguments]  ${extraKeyword}
    # Fill in the Competition information and search
    the user clicks the button/link         link = Competition information and search
    the user enters text to a text field    id = shortDescription  Short public description
    the user enters text to a text field    id = projectFundingRange  Up to £1million
    the user enters text to a text field    css = [aria-labelledby = "eligibilitySummary-label"]  Summary of eligiblity
    the user selects the radio button       publishSetting  public
    the user enters text to a text field    id = keywords  Search, Testing, Robot, ${extraKeyword}
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Competition information and search") ~ .task-status-complete
    # Fill in the Summary
    the user clicks the button/link         link = Summary
    the user enters text to a text field    css = .editor  This is a Summary description
    the user enters text to a text field    id = projectSize   10 millions
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Summary") ~ .task-status-complete
    # Fill in the publick content eligibility
    the user clicks the button/link         link = Eligibility
    the user enters text to a text field    id = contentGroups[0].heading  Heading 1
    the user enters text to a text field    jQuery = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Eligibility") ~ .task-status-complete
    # Fill in the Scope
    the user clicks the button/link         link = Scope
    the user enters text to a text field    id = contentGroups[0].heading  Heading 1
    the user enters text to a text field    jQuery = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Scope") ~ .task-status-complete
    # Save the dates
    the user clicks the button/link         link = Dates
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Dates") ~ .task-status-complete
    # Fill in the How to apply
    the user clicks the button/link         link = How to apply
    the user enters text to a text field    id = contentGroups[0].heading    Heading 1
    the user enters text to a text field    css = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("How to apply") ~ .task-status-complete
    # Fill in the Supporting information
    the user clicks the button/link         link = Supporting information
    the user enters text to a text field    id = contentGroups[0].heading    Heading 1
    the user enters text to a text field    css = div.editor:first-of-type  Content 1
    the user clicks the button/link         jQuery = button:contains("Save and review")
    the user clicks the button/link         link = Return to public content
    the user should see the element         jQuery = div:contains("Supporting information") ~ .task-status-complete
    # Publish and return
    the user clicks the button/link         jQuery = button:contains("Publish content")

the internal user navigates to public content
    [Arguments]  ${comp}
    the user navigates to the page     ${CA_UpcomingComp}
    the user clicks the button/link    link = ${comp}
    the user clicks the button/link    link = Public content

The application list is sorted by
    [Arguments]    ${sorting_factor}
    Select From List By Label    name = sort    ${sorting_factor}

The applications should be sorted by column
    [Arguments]    ${column_number}
    ${row_count}=    Get Element Count    //*[td]
    @{sorted_column_contents}=    Create List
    : FOR    ${row}    IN RANGE    2    ${row_count}
    \    ${cell_contents}=    get table cell    css=table    ${row}    ${column_number}
    \    append to list    ${sorted_column_contents}    ${cell_contents}
    ${test_sorting_list}=    Copy List    ${sorted_column_contents}
    Sort List    ${test_sorting_list}
    Lists Should Be Equal    ${sorted_column_contents}    ${test_sorting_list}

the user should see all live competitions
    the user should see the element  jQuery = h2:contains("Open")
    the user should see the element  jQuery = h2:contains("Closed")
    the user should see the element  jQuery = h2:contains("In assessment")
    the user should see the element  jQuery = h2:contains("Panel")
    the user should see the element  jQuery = h2:contains("Inform")

the user is able to configure the new question
    [Arguments]  ${questionTitle}
    the user enters text to a text field  id = question.title  Tell us how your project is innovative.
    the user enters text to a text field  id = question.shortTitle  ${questionTitle}
    the user enters text to a text field  id = question.subTitle  Adding value on existing projects is important to InnovateUK.
    the user enters text to a text field  id = question.guidanceTitle  Innovation is crucial to the continuing success of any organization.
    the user enters text to a text field  css = label[for = "question.guidance"] + * .editor  Please use Microsoft Word where possible. If you complete your application using Google Docs or any other open source software, this can be incompatible with the application form.
    the user enters text to a text field  id = question.maxWords  500
    the user selects the radio button     question.appendix  1
    click element                         css = label[for="question.allowedAppendixResponseFileTypes1"]
    the user clicks the button/link       css = label[for="question.allowedAppendixResponseFileTypes2"]
    the user enters text to a text field  css = label[for="question.appendixGuidance"] + * .editor  You may include an appendix of additional information to provide details of the specific expertise and track record of each project partner and each subcontractor.
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
    the user clicks the button/link       jQuery = button:contains('Done')
    the user should see the element       jQuery = li:contains("${questionTitle}") .task-status-complete

the user should be able to see the read only view of question correctly
    [Arguments]  ${questionTitle}
    the user clicks the button/link  jQuery = a:contains("${questionTitle}")
    the user should see the element  jQuery = dt:contains("Question heading") + dd:contains("${questionTitle}")
    the user should see the element  jQuery = dt:contains("Question title") + dd:contains("Tell us how your project is innovative.")
    the user should see the element  jQuery = dt:contains("Question subtitle") + dd:contains("Adding value on existing projects is important to InnovateUK.")
    the user should see the element  jQuery = dt:contains("Guidance title") + dd:contains("Innovation is crucial to the continuing success of any organization.")
    the user should see the element  jQuery = dt:contains("Guidance") + dd:contains("Please use Microsoft Word where possible.")
    the user should see the element  jQuery = dt:contains("Max word count") + dd:contains("500")
    the user should see the element  jQuery = dt:contains("Appendix") + dd:contains("Yes")
    the user should see the element  jQuery = dt:contains("Accepted appendix file types") + dd:contains("PDF")
    the user should see the element  jQuery = dt:contains("Accepted appendix file types") + dd:contains("spreadsheet")
    the user should see the element  jQuery = dt:contains("Appendix guidance") + dd:contains("You may include an appendix of additional information to provide details of the specific expertise and track record of each project partner and each subcontractor.")
    the user should see the element  jQuery = dt:contains("Scored") + dd:contains("Yes")
    the user should see the element  jQuery = dt:contains("Out of") + dd:contains("10")
    the user should see the element  jQuery = dt:contains("Written feedback") + dd:contains("Yes")
    the user should see the element  jQuery = dt:contains("Guidance title") + dd:contains("Please bare in mind on how well the applicant is able to justify his arguments.")
    the user should see the element  jQuery = dt:contains("Guidance") + dd:contains("The better you understand the problem the simpler your explanation is.")
    the user should see the element  jQuery = dt:contains("9-10") + dd:contains("This the 9-10 Justification")
    the user should see the element  jQuery = dt:contains("7-8") + dd:contains("This the 7-8 Justification")
    the user should see the element  jQuery = dt:contains("5-6") + dd:contains("This the 5-6 Justification")
    the user should see the element  jQuery = dt:contains("3-4") + dd:contains("This the 3-4 Justification")
    the user should see the element  jQuery = dt:contains("1-2") + dd:contains("This the 1-2 Justification")
    the user should see the element  jQuery = dt:contains("Max word count") + dd:contains("120")
    the user clicks the button/link  link = Return to application questions

moving competition to Closed
    [Arguments]  ${compID}
    ${yesterday} =  get yesterday
    execute sql string   UPDATE `${database_name}`.`milestone` SET `date` = '${yesterday}' WHERE `type` = 'SUBMISSION_DATE' AND `competition_id` = '${compID}';

making the application a successful project
    [Arguments]  ${compID}  ${appTitle}
    the user navigates to the page      ${server}/management/competition/${compID}
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  page should contain element  css = button[type="submit"][formaction$="close-assessment"]
    Run Keyword If  '${status}' == 'PASS'  the user clicks the button/link  css = button[type="submit"][formaction$="close-assessment"]
    Run Keyword If  '${status}' == 'FAIL'  Run keywords    the user clicks the button/link    css = button[type="submit"][formaction$="notify-assessors"]
    ...    AND  the user clicks the button/link    css = button[type="submit"][formaction$="close-assessment"]
    run keyword and ignore error without screenshots     the user clicks the button/link    css = button[type="submit"][formaction$="close-assessment"]
    the user clicks the button/link  link = Input and review funding decision
    the user clicks the button/link  jQuery = tr:contains("${appTitle}") label
    the user clicks the button/link  css = [type="submit"][value="FUNDED"]
    the user navigates to the page   ${server}/management/competition/${compID}/manage-funding-applications
    the user clicks the button/link  jQuery = tr:contains("${appTitle}") label
    the user clicks the button/link  css = [name="write-and-send-email"]
    the internal sends the descision notification email to all applicants  Successful!

moving competition to Project Setup
    [Arguments]   ${compID}
    the user navigates to the page   ${server}/management/competition/${compID}
    the user clicks the button/link  css = button[type="submit"][formaction$="release-feedback"]

The project finance user is able to download the Overheads file
    [Arguments]   ${ProjectID}  ${organisationId}
    the user downloads the file                   ${internal_finance_credentials["email"]}  ${server}/project-setup-management/project/${ProjectID}/finance-check/organisation/${organisationId}/project-eligibility  ${DOWNLOAD_FOLDER}/${excel_file}
    remove the file from the operating system     ${excel_file}

the user set assessor score notification to yes
    the user clicks the button/link         link = View and update competition setup
    the user clicks the button/link         link = Assessors
    the user clicks the button/link         jQuery = button:contains("Edit")
    the user selects the radio button       assessorCount   5
    the user selects the radio button       hasAssessmentPanel  hasAssessmentPanel-0
    the user selects the radio button       hasInterviewStage  hasInterviewStage-0
    the user selects the radio button       averageAssessorScore  averageAssessorScore-0
    the user clicks the button/link         jQuery = button:contains("Done")

the user selects the organisational eligibility
    [Arguments]     ${organisationEligibilityOption}            ${CanInternationalOrganisationsLead}
    the user clicks the button/link         link = ${organisationalEligibilityTitle}
    the user selects the radio button       internationalOrganisationsApplicable       ${organisationEligibilityOption}
    the user clicks the button/link         jQuery = button:contains("Save and continue")
    the user selects the radio button       leadInternationalOrganisationsApplicable  ${CanInternationalOrganisationsLead}
    the user clicks the button/link         jQuery = button:contains("Save and continue")
    the user clicks the button/link         link = Competition setup
    the user should see the element         jQuery = li:contains("Organisational eligibility") .task-status-complete

the user selects the organisational eligibility to no
    [Arguments]     ${organisationEligibilityOption}
    the user clicks the button/link         link = ${organisationalEligibilityTitle}
    the user selects the radio button       internationalOrganisationsApplicable       ${organisationEligibilityOption}
    the user clicks the button/link         jQuery = button:contains("Save and continue")
    the user clicks the button/link         link = Competition setup
    the user should see the element         jQuery = li:contains("Organisational eligibility") .task-status-complete