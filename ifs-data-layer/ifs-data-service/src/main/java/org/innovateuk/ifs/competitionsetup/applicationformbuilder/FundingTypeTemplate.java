package org.innovateuk.ifs.competitionsetup.applicationformbuilder;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import java.util.List;

public interface FundingTypeTemplate {

    FundingType type();

    List<SectionBuilder> sections();
}
