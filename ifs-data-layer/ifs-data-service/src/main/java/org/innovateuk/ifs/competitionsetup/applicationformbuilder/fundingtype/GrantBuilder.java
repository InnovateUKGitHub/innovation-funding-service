package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

@Component
public class GrantBuilder implements FundingTypeTemplate {

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public FundingType type() {
        return FundingType.GRANT;
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {
        return competitionTypeSections;
    }

    @Override
    public Competition initialiseFinanceTypes(Competition competition) {
        List<FinanceRowType> types = newArrayList(LABOUR, OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS, FINANCE, OTHER_FUNDING, YOUR_FINANCE);
        return commonBuilders.saveFinanceRows(competition, types);
    }

    @Override
    public Competition initialiseProjectSetupColumns(Competition competition) {
        return commonBuilders.addDefaultProjectSetupColumns(competition);
    }
}
