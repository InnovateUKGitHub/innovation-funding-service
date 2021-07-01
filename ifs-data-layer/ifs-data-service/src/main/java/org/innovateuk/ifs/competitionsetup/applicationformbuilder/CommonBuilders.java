package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.*;
import org.innovateuk.ifs.competition.repository.CompetitionFinanceRowsTypesRepository;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.project.core.domain.ProjectStages;
import org.innovateuk.ifs.project.grantofferletter.template.domain.GolTemplate;
import org.innovateuk.ifs.project.grantofferletter.template.repository.GolTemplateRepository;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.GuidanceRowBuilder.aGuidanceRow;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.MultipleChoiceOptionBuilder.aMultipleChoiceOption;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSubSection;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;

@Component
public class CommonBuilders {

    public static final String EDI_QUESTION_PATTERN = "<a href=\"%s\" target=\"_blank\" rel=\"external\">Complete the survey (opens in new window).</a><p>We will not use this data when we assess your application. We collect this data anonymously and only use it to help us understand our funding recipients better.</p>";

    @Autowired
    private CompetitionFinanceRowsTypesRepository competitionFinanceRowsTypesRepository;

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private GolTemplateRepository golTemplateRepository;

    /*
    Tech debt.
    Unused columns: section.withDisplayInAssessmentApplicationSummary
                    section.withAssessorGuidanceDescription (move to template?)
                    section.questionGroup
                    form_input.includedInApplicationSummary

     */

    public static SectionBuilder projectDetails() {
        return aSection()
                .withName("Project details")
                .withType(SectionType.PROJECT_DETAILS)
                .withDescription("Please provide information about your project. This section is not scored but will provide background to the project.")
                .withAssessorGuidanceDescription("These sections give important background information on the project. They do not need scoring however you do need to mark the scope.");
    }

    public static SectionBuilder applicationQuestions() {
        return aSection()
                .withName("Application questions")
                .withType(SectionType.APPLICATION_QUESTIONS)
                .withDescription("These are the questions which will be marked by the assessors.")
                .withAssessorGuidanceDescription("Each question should be given a score out of 10. Written feedback should also be given.");
    }

    public static SectionBuilder finances() {
        return aSection()
                .withName("Finances")
                .withType(SectionType.FINANCES)
                .withAssessorGuidanceDescription("Each partner is required to submit their own project finances and funding rates. The overall project costs for all partners can be seen in the Finances overview section")
                .withChildSections(newArrayList(
                        aSubSection()
                                .withName("Your project finances")
                                .withType(SectionType.FINANCE)
                                .withChildSections(newArrayList(
                                        aSubSection()
                                                .withName("Your project costs")
                                                .withType(SectionType.PROJECT_COST_FINANCES)
                                                .withQuestions(newArrayList(
                                                        aQuestionWithMultipleStatuses()
                                                )),
                                        aSubSection()
                                                .withName("Your project location")
                                                .withType(SectionType.PROJECT_LOCATION)
                                                .withQuestions(newArrayList(
                                                        aQuestionWithMultipleStatuses()
                                                )),
                                        aSubSection()
                                                .withName("Your organisation")
                                                .withType(SectionType.ORGANISATION_FINANCES)
                                                .withQuestions(newArrayList(
                                                        aQuestionWithMultipleStatuses()
                                                )),
                                        aSubSection()
                                                .withName("Your funding")
                                                .withType(SectionType.FUNDING_FINANCES)
                                                .withQuestions(newArrayList(
                                                        aQuestionWithMultipleStatuses()
                                                ))
                                )),
                        aSubSection()
                                .withName("Finances overview")
                                .withType(SectionType.OVERVIEW_FINANCES)
                                .withQuestions(newArrayList(
                                        aQuestion()
                                ))
                ));
    }

    public static SectionBuilder termsAndConditions() {
        return aSection()
                .withName("Terms and conditions")
                .withType(SectionType.TERMS_AND_CONDITIONS)
                .withDescription("You must agree to these before you submit your application.")
                .withQuestions(newArrayList(aQuestion()
                        .withShortName("Award terms and conditions")
                        .withName("Award terms and conditions")
                        .withDescription("Award terms and conditions")
                        .withMarkAsCompletedEnabled(true)
                        .withMultipleStatuses(true)
                        .withAssignEnabled(false)
                        .withQuestionSetupType(QuestionSetupType.TERMS_AND_CONDITIONS)));
    }

    public static QuestionBuilder applicationTeam() {
        return aQuestion()
                .withShortName("Application team")
                .withName("Application team")
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.LEAD_ONLY)
                .withQuestionSetupType(QuestionSetupType.APPLICATION_TEAM);
    }

    public static QuestionBuilder applicationDetails() {
        return aQuestion()
                .withShortName("Application details")
                .withName("Application details")
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.LEAD_ONLY)
                .withQuestionSetupType(QuestionSetupType.APPLICATION_DETAILS);
    }

    public static QuestionBuilder researchCategory() {
        return aQuestion()
                .withShortName("Research category")
                .withName("Research category")
                .withAssignEnabled(false)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.LEAD_ONLY)
                .withQuestionSetupType(QuestionSetupType.RESEARCH_CATEGORY);
    }

    public static QuestionBuilder equalityDiversityAndInclusion() {
        return aQuestion()
                .withShortName("Equality, diversity and inclusion")
                .withName("Have you completed the EDI survey?")
                .withDescription(String.format(EDI_QUESTION_PATTERN, "https://www.surveymonkey.co.uk/r/ifsaccount"))
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.EQUALITY_DIVERSITY_INCLUSION)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION)
                                .withMultipleChoiceOptions(newArrayList(
                                        aMultipleChoiceOption()
                                                .withText("Yes"),
                                        aMultipleChoiceOption()
                                                .withText("No")
                                ))
                ));
    }

    public static QuestionBuilder projectSummary() {
        return aQuestion()
                .withShortName("Project summary")
                .withName("Project summary")
                .withDescription("Please provide a short summary of your project. We will not score this summary.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.PROJECT_SUMMARY)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withGuidanceTitle("What should I include in the project summary?")
                                .withGuidanceAnswer("<p>We will not score this summary, but it will give the assessors a useful introduction to your project. It should provide a clear overview of the whole project, including:</p> <ul class=\"list-bullet\">         <li>your vision for the project</li><li>key objectives</li><li>main areas of focus</li><li>details of how it is innovative</li></ul>")
                                .withWordCount(400)
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION),
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(false)
                                .withScope(FormInputScope.APPLICATION)
                ));
    }

    public static QuestionBuilder publicDescription() {
        return aQuestion()
                .withShortName("Public description")
                .withName("Public description")
                .withDescription("Please provide a brief description of your project. If your application is successful, we will publish this description. This question is mandatory but is not scored.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.PUBLIC_DESCRIPTION)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withGuidanceTitle("What should I include in the project public description?")
                                .withGuidanceAnswer("<p>Innovate UK publishes information about projects we have funded. This is in line with government practice on openness and transparency of public-funded activities.</p><p>Describe your project in a way that will be easy for a non-specialist to understand. Don't include any information that is confidential, for example, intellectual property or patent details.</p> ")
                                .withWordCount(400)
                                .withActive(true)
                                .withScope(FormInputScope.APPLICATION),
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withActive(false)
                                .withScope(FormInputScope.APPLICATION)
                ));
    }

    public static QuestionBuilder scope() {
        return aQuestion()
                .withShortName("Scope")
                .withName("How does your project align with the scope of this competition?")
                .withDescription("If your application doesn't align with the scope, we will not assess it.")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.SCOPE)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(true)
                                .withGuidanceTitle("What should I include in the project scope?")
                                .withGuidanceAnswer("<p>It is important that you read the following guidance.</p><p>To show how your project aligns with the scope of this competition, you need to:</p><ul class=\"list-bullet\">         <li>read the competition brief in full</li><li>understand the background, challenge and scope of the competition</li><li>address the research objectives in your application</li><li>match your project's objectives and activities to these</li></ul> <p>Once you have submitted your application, you should not change this section unless:</p><ul class=\"list-bullet\">         <li>we ask you to provide more information</li><li>we ask you to make it clearer</li></ul>")
                                .withWordCount(400),
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
                                .withScope(FormInputScope.APPLICATION)
                                .withActive(false),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(true)
                                .withDescription("Is the application in scope?"),
                        aFormInput()
                                .withType(FormInputType.ASSESSOR_RESEARCH_CATEGORY)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(true)
                                .withDescription("Please select the research category for this project"),
                        aFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withActive(true)
                                .withGuidanceTitle("Guidance for assessing scope")
                                .withGuidanceAnswer("You should still assess this application even if you think that it is not in scope. Your answer should be based upon the following:")
                                .withWordCount(100)
                                .withGuidanceRows(newArrayList(
                                        aGuidanceRow()
                                                .withSubject("Yes")
                                                .withJustification("The application contains the following: Is the consortia business led? Are there two or more partners to the collaboration? Does it meet the scope of the competition as defined in the competition brief?"),
                                        aGuidanceRow()
                                                .withSubject("No")
                                                .withJustification("One or more of the above requirements have not been satisfied.")
                                ))
                ));
    }

    public static QuestionBuilder genericQuestion() {
        return aDefaultAssessedQuestion()
                .withFormInputs(
                        defaultAssessedQuestionFormInputs(Function.identity(),
                                assessorInputBuilder ->
                                        assessorInputBuilder.withGuidanceRows(newArrayList(
                                                aGuidanceRow()
                                                        .withSubject("9,10"),
                                                aGuidanceRow()
                                                        .withSubject("7,8"),
                                                aGuidanceRow()
                                                        .withSubject("5,6"),
                                                aGuidanceRow()
                                                        .withSubject("3,4"),
                                                aGuidanceRow()
                                                        .withSubject("1,2")
                                        ))
                        )
                );
    }

    public static List<FormInputBuilder> defaultAssessedQuestionFormInputs(Function<FormInputBuilder, FormInputBuilder> applicationTextAreaModifier, Function<FormInputBuilder, FormInputBuilder> assessorTextAreaModifier, Function<FormInputBuilder, FormInputBuilder> appendixFormInputModifier) {
        return newArrayList(
                applicationTextAreaModifier.apply(aFormInput()
                        .withType(FormInputType.TEXTAREA)
                        .withScope(FormInputScope.APPLICATION)
                        .withActive(true)
                        .withWordCount(400)),
                aFormInput()
                        .withType(FormInputType.MULTIPLE_CHOICE)
                        .withScope(FormInputScope.APPLICATION)
                        .withActive(false),
                appendixFormInputModifier.apply(aFormInput()
                        .withType(FormInputType.FILEUPLOAD)
                        .withScope(FormInputScope.APPLICATION)
                        .withActive(false)),
                aFormInput()
                        .withType(FormInputType.TEMPLATE_DOCUMENT)
                        .withScope(FormInputScope.APPLICATION)
                        .withActive(false),
                aFormInput()
                        .withType(FormInputType.ASSESSOR_SCORE)
                        .withScope(FormInputScope.ASSESSMENT)
                        .withActive(true),
                assessorTextAreaModifier.apply(aFormInput()
                        .withType(FormInputType.TEXTAREA)
                        .withScope(FormInputScope.ASSESSMENT)
                        .withActive(true)
                        .withWordCount(100))
        );
    }

    public static List<FormInputBuilder> defaultAssessedQuestionFormInputs(Function<FormInputBuilder, FormInputBuilder> applicationTextAreaModifier, Function<FormInputBuilder, FormInputBuilder> assessorTextAreaModifier) {
        return defaultAssessedQuestionFormInputs(applicationTextAreaModifier,
                assessorTextAreaModifier,
                Function.identity());
    }

    public static Competition addDefaultProjectSetupColumns(Competition competition) {
        addProjectSetupStage(competition, PROJECT_DETAILS);
        addProjectSetupStage(competition, PROJECT_TEAM);
        addProjectSetupStage(competition, DOCUMENTS);
        addProjectSetupStage(competition, MONITORING_OFFICER);
        addProjectSetupStage(competition, BANK_DETAILS);
        addProjectSetupStage(competition, FINANCE_CHECKS);
        addProjectSetupStage(competition, SPEND_PROFILE);
        addProjectSetupStage(competition, GRANT_OFFER_LETTER);
        return competition;
    }

    public static void addProjectSetupStage(Competition competition, ProjectSetupStage projectSetupStage) {
        competition.addProjectStage(new ProjectStages(competition, projectSetupStage));
    }

    public Competition saveFinanceRows(Competition competition, List<FinanceRowType> financeRowTypes) {
        IntStream.range(0, financeRowTypes.size()).forEach(i ->
                competition.getCompetitionFinanceRowTypes().add(
                        competitionFinanceRowsTypesRepository.save(
                                new CompetitionFinanceRowTypes(competition, financeRowTypes.get(i), i)))
        );
        return competition;
    }

    public Competition overrideTermsAndConditions(Competition competition) {
        GrantTermsAndConditions termsAndConditions =
                grantTermsAndConditionsRepository.getLatestForFundingType(competition.getFundingType());

        if (FundingRules.SUBSIDY_CONTROL == competition.getFundingRules()) {
            String subsidyControlTemplateName = competition.getFundingType().getDefaultTermsName() + " - Subsidy control";
            GrantTermsAndConditions subsidyControlTermsAndConditions =
                    grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc(subsidyControlTemplateName);

            if (subsidyControlTermsAndConditions != null) {
                competition.setTermsAndConditions(subsidyControlTermsAndConditions);
            } else {
                competition.setTermsAndConditions(termsAndConditions);
            }

            competition.setOtherFundingRulesTermsAndConditions(termsAndConditions);

        } else {
            competition.setTermsAndConditions(termsAndConditions);
        }

        return competition;
    }

    public static Competition setDefaultOrganisationConfig(Competition competition) {
        if (competition.getCompetitionOrganisationConfig() == null) {
            CompetitionOrganisationConfig competitionOrganisationConfig = new CompetitionOrganisationConfig();
            competitionOrganisationConfig.setCompetition(competition);
            competition.setCompetitionOrganisationConfig(competitionOrganisationConfig);
        }

        return competition;
    }

    public static Competition setDefaultApplicationConfig(Competition competition) {
        if (competition.getCompetitionApplicationConfig() == null) {
            CompetitionApplicationConfig competitionApplicationConfig = new CompetitionApplicationConfig();
            competitionApplicationConfig.setCompetition(competition);
            competition.setCompetitionApplicationConfig(competitionApplicationConfig);
        }

        return competition;
    }

    public Competition getGolTemplate(Competition competition) {
        String templateName = competition.getFundingType().getGolType();
        GolTemplate golTemplate =
                golTemplateRepository.findFirstByNameOrderByVersionDesc(templateName);
        competition.setGolTemplate(golTemplate);
        return competition;

    }
}
