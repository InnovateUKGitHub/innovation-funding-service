package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;

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
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(36);
        return competition;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationDetails()
                                        .withQuestionSetupType(QuestionSetupType.GRANT_TRANSFER_DETAILS),
                                applicationTeam(),
                                aQuestion()
                                        .withShortName("Horizon Europe grant agreement")
                                        .withName("Horizon Europe grant agreement")
                                        .withAssignEnabled(false)
                                        .withMultipleStatuses(false)
                                        .withMarkAsCompletedEnabled(true)
                                        .withType(QuestionType.LEAD_ONLY)
                                        .withQuestionSetupType(QuestionSetupType.GRANT_AGREEMENT),
                                publicDescription(),
                                equalityDiversityAndInclusion(),
                                horizonEuropeGuaranteeDefaultQuestions()
                        )),
                finances(),
                termsAndConditions()
        );
    }

    public static QuestionBuilder horizonEuropeGuaranteeDefaultQuestions() {
        QuestionBuilder horizonEuropeGuaranteeQuestion = genericQuestion();
        horizonEuropeGuaranteeQuestion.getFormInputs().stream()
                .filter(fi -> fi.getScope().equals(ASSESSMENT))
                .forEach(fi -> fi.withActive(false));
        return horizonEuropeGuaranteeQuestion;
    }
}