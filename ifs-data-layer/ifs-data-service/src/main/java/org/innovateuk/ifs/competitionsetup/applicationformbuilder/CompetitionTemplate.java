package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;

import java.util.List;

public interface CompetitionTemplate {

    CompetitionTypeEnum type();

    List<SectionBuilder> sections();

    Competition copyTemplatePropertiesToCompetition(Competition competition);

}
