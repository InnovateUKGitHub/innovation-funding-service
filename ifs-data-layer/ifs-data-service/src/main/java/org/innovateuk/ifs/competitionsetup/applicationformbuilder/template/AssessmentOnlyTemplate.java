package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.*;

@Component
public class AssessmentOnlyTemplate implements CompetitionTemplate {

    @Override
    public CompetitionTypeEnum type() {
        return CompetitionTypeEnum.ASSESSMENT_ONLY;
    }

    @Override
    public Competition copyTemplatePropertiesToCompetition(Competition competition) {
        return competition;
    }

    @Override
    public List<SectionBuilder> sections() {
        return newArrayList(
                projectDetails()
                        .withQuestions(newArrayList(
                                applicationDetails()
                        )),
                applicationQuestions()
        );
    }
}
