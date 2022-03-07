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
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.GuidanceRowBuilder.aGuidanceRow;
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
        competition.setHasAssessmentStage(false);
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
                                applicationTeam(),
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
                eic()
        );
    }

    public static QuestionBuilder grantAgreement() {
        return aQuestion()
                .withShortName("Horizon 2020 grant agreement")
                .withName("Horizon 2020 grant agreement")
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
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(10)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
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
                                .withActive(false)
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
                                .withActive(false)
                                .withWordCount(100)
                                .withGuidanceRows(newArrayList(
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
                ));
    }

    public static QuestionBuilder eic() {
        return aQuestion()
                .withShortName("What EIC call have you been successfully evaluated for?")
                .withName("What EIC call have you been successfully evaluated for?")
                .withAssignEnabled(true)
                .withMarkAsCompletedEnabled(true)
                .withMultipleStatuses(false)
                .withType(QuestionType.GENERAL)
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withAssessorMaximumScore(10)
                .withFormInputs(newArrayList(
                        aFormInput()
                                .withType(FormInputType.MULTIPLE_CHOICE)
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
                                .withActive(false)
                                .withWordCount(400),
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
                                .withActive(false)
                                .withWordCount(100)
                                .withGuidanceRows(newArrayList(
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
                ));
    }
}