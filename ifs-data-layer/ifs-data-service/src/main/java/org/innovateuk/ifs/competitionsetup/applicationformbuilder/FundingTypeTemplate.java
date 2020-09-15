package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import java.util.List;

public interface FundingTypeTemplate {

    FundingType type();

    List<SectionBuilder> sections();

//    is this needed for funding types? probs not
    Competition copyTemplatePropertiesToCompetition(Competition competition);
}
