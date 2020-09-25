package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;

import java.util.List;

import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders.addDefaultProjectSetupColumns;

public interface FundingTypeTemplate {

    FundingType type();

    default List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {
        return competitionTypeSections;
    }

    Competition initialiseFinanceTypes(Competition competition);

    Competition overrideTermsAndConditions(Competition competition);

    default Competition initialiseProjectSetupColumns(Competition competition) {
        return addDefaultProjectSetupColumns(competition);
    }
}
