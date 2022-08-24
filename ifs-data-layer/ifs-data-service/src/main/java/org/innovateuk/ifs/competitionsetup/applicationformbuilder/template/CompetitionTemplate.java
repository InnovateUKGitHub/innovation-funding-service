package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;

import java.util.List;

import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.setDefaultApplicationConfig;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.setDefaultOrganisationConfig;

public interface CompetitionTemplate {

    CompetitionTypeEnum type();

    List<SectionBuilder> sections();

    Competition copyTemplatePropertiesToCompetition(Competition competition);

    default Competition initialiseOrganisationConfig(Competition competition) {
        return setDefaultOrganisationConfig(competition);
    }

    default Competition initialiseApplicationConfig(Competition competition) {
        return setDefaultApplicationConfig(competition);
    }
}
