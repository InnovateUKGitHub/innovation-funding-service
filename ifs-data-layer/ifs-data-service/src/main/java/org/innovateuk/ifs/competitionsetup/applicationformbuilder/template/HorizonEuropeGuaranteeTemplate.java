package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.MultipleChoiceOptionBuilder.aMultipleChoiceOption;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;

@Component
public class HorizonEuropeGuaranteeTemplate implements CompetitionTemplate {


    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.HORIZON_EUROPE_GUARANTEE;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Horizon Europe Guarantee"));
        competition.setAcademicGrantPercentage(100);
        competition.setResubmission(false);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(84);
        competition.setAlwaysOpen(true);
        competition.setApplicationFinanceType(STANDARD);
        competition.setIncludeProjectGrowthTable(false);
        competition.setIncludeJesForm(false);
        competition.setIncludeYourOrganisationSection(false);
        return competition;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationDetails(),
                                researchCategory(),
                                applicationTeam(),
                                horizonWorkProgramme(),
                                grantAgreement(),
                                equalityDiversityAndInclusion()
                        )),
                applicationQuestions()
                        .withQuestions(horizonEuropeDefaultQuestions()),
                finances(),
                termsAndConditions()
        );
    }

    public static List<QuestionBuilder> horizonEuropeDefaultQuestions() {
        return newArrayList(
                organisation(),
                projectRegion(),
                eic(),
                applicationReferenceNumber(),
                fundingContribution(),
                taskAssignment(),
                amountChange(),
                phdStudents(),
                projectPhdStudents(),
                phdStudentsBudget()
        );
    }

    public static QuestionBuilder grantAgreement() {
        return aQuestion()
                .withShortName("Horizon Europe Guarantee grant agreement")
                .withName("Horizon Europe Guarantee grant agreement")
                .withAssignEnabled(false)
                .withMultipleStatuses(false)
                .withMarkAsCompletedEnabled(true)
                .withType(QuestionType.LEAD_ONLY)
                .withQuestionSetupType(QuestionSetupType.GRANT_AGREEMENT);
    }

    public static QuestionBuilder organisation() {
        return aQuestion()
                .withShortName("Tell us where your organisation is based")
                .withName("Tell us where your organisation is based")
                .withDescription("You must be an organisation based in the UK or a British Overseas Territory to receive funding.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withGuidanceTitle("Further information")
                                .withGuidanceAnswer("-")
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION)
                                .withMultipleChoiceOptions(newArrayList(
                                        aMultipleChoiceOption()
                                                .withText("My organisation is based in the UK or a British Overseas Territory"),
                                        aMultipleChoiceOption()
                                                .withText("My organisation is NOT based in the UK or a British Overseas Territory")
                                )),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }

    public static QuestionBuilder projectRegion() {
        return aQuestion()
                .withShortName("Participating Organisation project region")
                .withName("Participating Organisation project region")
                .withDescription("Please select from the drop down the region your project is being carried out in.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withGuidanceTitle("further information")
                                .withGuidanceAnswer("-")
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION)
                                .withMultipleChoiceOptions(newArrayList(
                                        aMultipleChoiceOption()
                                                .withText("East Midlands"),
                                        aMultipleChoiceOption()
                                                .withText("East of England"),
                                        aMultipleChoiceOption()
                                                .withText("London"),
                                        aMultipleChoiceOption()
                                                .withText("North East & Cumbria"),
                                        aMultipleChoiceOption()
                                                .withText("North West"),
                                        aMultipleChoiceOption()
                                                .withText("Northern Ireland"),
                                        aMultipleChoiceOption()
                                                .withText("Scotland"),
                                        aMultipleChoiceOption()
                                                .withText("South East"),
                                        aMultipleChoiceOption()
                                                .withText("South West"),
                                        aMultipleChoiceOption()
                                                .withText("Wales"),
                                        aMultipleChoiceOption()
                                                .withText("West Midlands"),
                                        aMultipleChoiceOption()
                                                .withText("Yorkshire & the Humber"),
                                        aMultipleChoiceOption()
                                                .withText("Overseas British Territory")
                                )),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }

    public static QuestionBuilder horizonWorkProgramme() {
        return aQuestion()
                .withShortName("Work programme")
                .withName("Work programme")
                .withDescription("Work programme question for Horizon competitions.")
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(true)
                .withType(QuestionType.LEAD_ONLY)
                .withQuestionSetupType(QuestionSetupType.HORIZON_WORK_PROGRAMME)
                .withFormInputs(newArrayList());
    }

    public static QuestionBuilder eic() {
        return aQuestion()
                .withShortName("What EIC call have you been successfully evaluated for?")
                .withName("What EIC call have you been successfully evaluated for?")
                .withDescription("Please select below the Horizon Europe competition you have been successful with.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withGuidanceTitle("Further information")
                                .withGuidanceAnswer("-")
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION)
                                .withMultipleChoiceOptions(newArrayList(
                                        aMultipleChoiceOption()
                                                .withText("EIC Transition"),
                                        aMultipleChoiceOption()
                                                .withText("EIC Pathfinder"),
                                        aMultipleChoiceOption()
                                                .withText("EIC Accelerator")
                                )),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }

    public static QuestionBuilder applicationReferenceNumber() {
        return aQuestion()
                .withShortName("Application reference number")
                .withName("Application reference number")
                .withDescription("Enter the 8 digit reference number from your Horizon Europe application.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(false)
                                .withScope(FormInputScope.APPLICATION),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withGuidanceTitle("Further information")
                                .withGuidanceAnswer("-")
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(true)
                                .withWordCount(10),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(true)
                                .withWordCount(1)
                                .withAllowedFileTypes(newHashSet(FileTypeCategory.PDF))
                                .withGuidanceAnswer("Please attach the whole final EIC grant agreement. This is a mandatory upload and failure to include this may delay your application."),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }

    public static QuestionBuilder fundingContribution() {
        return aQuestion()
                .withShortName("UK Funding contribution applied for (GBP)")
                .withName("UK Funding contribution applied for (GBP)")
                .withDescription("Enter the UK budget total from your Horizon Europe application in GBP. A maximum exchange rate of £1:€1.160354 will be accepted.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(false)
                                .withScope(FormInputScope.APPLICATION),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withGuidanceTitle("Further Information")
                                .withGuidanceAnswer("-")
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(true)
                                .withWordCount(10),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false)
                                .withWordCount(1)
                                .withAllowedFileTypes(newHashSet(FileTypeCategory.PDF))
                                .withGuidanceAnswer("<div>\n" +
                                        " Please upload Part A of your original European Commission proposal including the budget table with your institution's costs included.\n" +
                                        "</div>\n" +
                                        "<div>\n" +
                                        " This is a mandatory upload and failure to include this may delay your application.\n" +
                                        "</div>"),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }

    public static QuestionBuilder taskAssignment() {
        return aQuestion()
                .withShortName("Have the tasks assigned to your institution changed significantly since the original application?")
                .withName("Have the tasks assigned to your institution changed significantly since the original application?")
                .withDescription("If you are part of a consortium and the tasks assigned to your institution in the final grant agreement are significantly different from those indicated in the original proposal submitted to the European Commission, select yes below. If the tasks are broadly the same, select no.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withGuidanceTitle("Further information")
                                .withGuidanceAnswer("If you select yes, you will submit further information in the next question. If you select no, the budget from your original proposal will be accepted as the basis of the UKRI grant value, subject to exchange rate and assurance checks.")
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION)
                                .withMultipleChoiceOptions(newArrayList(
                                        aMultipleChoiceOption()
                                                .withText("Yes"),
                                        aMultipleChoiceOption()
                                                .withText("No")
                                )),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }

    public static QuestionBuilder amountChange() {
        return aQuestion()
                .withShortName("If this amount has changed please tell us how?")
                .withName("If this amount has changed please tell us how?")
                .withDescription("If the answer to Q6 was yes please state the new amount in GBP and attach your latest budget table to reflect the change. A maximum exchange rate of £1:€1.160354 will be accepted. If the answer to Q6 was no, please type 'No' below in the provided textbox.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(false)
                                .withScope(FormInputScope.APPLICATION),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withGuidanceTitle("Further information")
                                .withGuidanceAnswer("-")
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(true)
                                .withWordCount(400),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false)
                                .withWordCount(1)
                                .withAllowedFileTypes(newHashSet(FileTypeCategory.PDF))
                                .withGuidanceAnswer("<div>\n" +
                                        " If you selected yes in the previous question, you will need to submit supporting information on what has changed and the consequent changes in budget request to UKRI from that originally applied for. If we believe insufficient explanation of any changes and associated budget have been provided with your submission, we may ask for additional information to verify the UKRI grant amount. We will not accept any increase of budget from that originally applied for.\n" +
                                        "</div>\n" +
                                        "<div>\n" +
                                        " This is a mandatory upload and failure to include this may delay your application.\n" +
                                        "</div>"),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }

    public static QuestionBuilder phdStudents() {
        return aQuestion()
                .withShortName("Will you, as a UK institution, be employing PhD students as part of this project?")
                .withName("Will you, as a UK institution, be employing PhD students as part of this project?")
                .withDescription("Please select an answer from the multiple choice options.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withGuidanceTitle("Further information")
                                .withGuidanceAnswer("-")
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION)
                                .withMultipleChoiceOptions(newArrayList(
                                        aMultipleChoiceOption()
                                                .withText("Yes"),
                                        aMultipleChoiceOption()
                                                .withText("No")
                                )),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }

    public static QuestionBuilder projectPhdStudents() {
        return aQuestion()
                .withShortName("If so, how many PhD students will be employed at your institution on this project?")
                .withName("If so, how many PhD students will be employed at your institution on this project?")
                .withDescription("If the answer to Q8 was yes please state how many PhD students will be employed at your institution on this project. If the answer to Q8 was no, please type 'No' in the provided textbox below.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(false)
                                .withScope(FormInputScope.APPLICATION),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withGuidanceTitle("Further information")
                                .withGuidanceAnswer("-")
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(true)
                                .withWordCount(400),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }

    public static QuestionBuilder phdStudentsBudget() {
        return aQuestion()
                .withShortName("How much budget is allocated for PhD students employed at your institution on this project?")
                .withName("How much budget is allocated for PhD students employed at your institution on this project?")
                .withDescription("If the answer to Q8 was yes, please state the total budget allocated for PhD students employed at your institution on this project. If the answer to Q8 was no, please type 'No' in the provided textbox below.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(0)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(false)
                                .withScope(FormInputScope.APPLICATION),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withGuidanceTitle("Further information")
                                .withGuidanceAnswer("-")
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(true)
                                .withWordCount(400),
                        aFormInput()
                                .withType(FormInputType.TEMPLATE_DOCUMENT)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(100)
                                .withActive(false)
                ));
    }
}