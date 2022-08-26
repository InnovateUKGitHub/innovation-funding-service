package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KtpAktTemplate extends KtpTemplate {

    @Override
    public FundingType type() {
        return FundingType.KTP_AKT;
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {
        updateFinanceSection(competitionTypeSections);
        return competitionTypeSections;
    }
}
