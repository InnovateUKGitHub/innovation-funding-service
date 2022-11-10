package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
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
                horizonEuropeDefaultQuestion()
        );
    }

    public static QuestionBuilder horizonEuropeDefaultQuestion() {
        return genericQuestion()
                .withShortName("Horizon Europe placeholder question")
                .withName("Horizon Europe placeholder question")
                .withDescription("Horizon Europe placeholder question description");
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
                .withDescription("Please type the region your project is being carried out in.")
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
}