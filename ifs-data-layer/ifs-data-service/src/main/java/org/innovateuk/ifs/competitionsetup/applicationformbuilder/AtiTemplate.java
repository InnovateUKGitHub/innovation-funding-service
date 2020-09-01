package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;

@Component
public class AtiTemplate implements CompetitionTemplate {

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.AEROSPACE_TECHNOLOGY_INSTITUTE;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        //todo remove dependency on template comp.
//        competition.setGrantClaimMaximums(new ArrayList<>(template.getGrantClaimMaximums()));
//        competition.setTermsAndConditions(template.getTermsAndConditions());
//        competition.setAcademicGrantPercentage(template.getAcademicGrantPercentage());
//        competition.setMinProjectDuration(template.getMinProjectDuration());
//        competition.setMaxProjectDuration(template.getMaxProjectDuration());
//        competition.setApplicationFinanceType(template.getApplicationFinanceType());
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
