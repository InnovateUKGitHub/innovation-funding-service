package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.GrantTermsAndConditionsRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;

@Component
public class HestaTemplate implements CompetitionTemplate {

    @Autowired
    private GrantTermsAndConditionsRepository grantTermsAndConditionsRepository;

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.HESTA;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        competition.setTermsAndConditions(grantTermsAndConditionsRepository.findFirstByNameOrderByVersionDesc("Hesta"));
        competition.setAcademicGrantPercentage(100);
        competition.setMinProjectDuration(1);
        competition.setMaxProjectDuration(84);
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
                                equalityDiversityAndInclusion()
                        )),
                applicationQuestions()
                        .withQuestions(hestaDefaultQuestions()),
                termsAndConditions()
        );
    }

    private static List<QuestionBuilder> hestaDefaultQuestions() {
        return EoiTemplate.eoiDefaultQuestions();
    }
}