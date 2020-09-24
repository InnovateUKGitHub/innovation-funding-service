package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;

import java.util.List;

public interface FundingTypeTemplate {

    FundingType type();

    List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections);

    Competition initialiseFinanceTypes(Competition competition);

    Competition initialiseProjectSetupColumns(Competition competition);
}
