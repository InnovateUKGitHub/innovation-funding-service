package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;

import java.util.List;

import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.setDefaultOrganisationConfig;

public interface CompetitionTemplate {

    CompetitionTypeEnum type();

    List<SectionBuilder> sections();

    Competition copyTemplatePropertiesToCompetition(Competition competition);

    default Competition initializeOrganisationConfig(Competition competition) {
        return setDefaultOrganisationConfig(competition);
    }
}
