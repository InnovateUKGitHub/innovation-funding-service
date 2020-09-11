package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.QuestionBuilder.aQuestion;

@Component
public class Horizon2020Template implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.HORIZON_2020;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        competition.setGrantClaimMaximums(commonBuilders.getDefaultGrantClaimMaximums());
        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Horizon 2020"));
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
                                    .withShortName("Horizon 2020 grant agreement")
                                    .withName("Horizon 2020 grant agreement")
                                    .withAssignEnabled(false)
                                    .withMultipleStatuses(false)
                                    .withMarkAsCompletedEnabled(true)
                                    .withType(QuestionType.LEAD_ONLY)
                                    .withQuestionSetupType(QuestionSetupType.GRANT_AGREEMENT),
                                publicDescription(),
                                equalityDiversityAndInclusion()
                        )),
                finances(),
                termsAndConditions()
        );
    }

}
