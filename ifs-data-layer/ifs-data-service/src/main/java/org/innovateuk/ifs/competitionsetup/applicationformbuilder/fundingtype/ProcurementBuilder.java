package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.FundingTypeTemplate;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcurementBuilder implements FundingTypeTemplate {

    @Override
    public FundingType type() {
        return FundingType.PROCUREMENT;
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {
        return competitionTypeSections;
    }
}
