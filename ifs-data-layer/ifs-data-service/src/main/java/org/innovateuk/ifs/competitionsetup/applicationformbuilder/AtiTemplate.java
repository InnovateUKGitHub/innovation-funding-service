package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;

@Component
public class AtiTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.AEROSPACE_TECHNOLOGY_INSTITUTE;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        competition.setGrantClaimMaximums(commonBuilders.getDefaultGrantClaimMaximums());
        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Aerospace Technology Institute (ATI)"));
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
                                applicationTeam(),
                                applicationDetails(),
                                researchCategory(),
                                equalityDiversityAndInclusion(),
                                projectSummary(),
                                publicDescription(),
                                scope()
                        )),
                applicationQuestions()
                        .withQuestions(atiDefaultQuestions()),
                finances(),
                termsAndConditions()
        );
    }

    private static List<QuestionBuilder> atiDefaultQuestions() {
        return ApcTemplate.apcDefaultQuestions();
    }
}
