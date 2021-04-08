package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;

import java.util.List;

public interface FundingRulesTemplate {

    FundingRules type();

    default List<SectionBuilder> sections(Competition competition, List<SectionBuilder> competitionTypeSections) {
        return competitionTypeSections;
    }

}
