package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;

import java.util.List;

public interface CompetitionTemplate {

    CompetitionTypeEnum type();

    List<SectionBuilder> sections();

    Competition copyTemplatePropertiesToCompetition(Competition competition);

}
