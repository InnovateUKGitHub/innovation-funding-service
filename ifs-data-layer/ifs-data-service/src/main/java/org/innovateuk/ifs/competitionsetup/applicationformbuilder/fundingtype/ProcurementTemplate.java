package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

@Component
public class ProcurementTemplate implements FundingTypeTemplate {

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public FundingType type() {
        return FundingType.PROCUREMENT;
    }

    @Override
    public Competition initialiseFinanceTypes(Competition competition) {
        List<FinanceRowType> types = newArrayList(LABOUR, PROCUREMENT_OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS, FINANCE, OTHER_FUNDING, VAT);
        return commonBuilders.saveFinanceRows(competition, types);
    }

    @Override
    public Competition setGolTemplate(Competition competition) {
        return commonBuilders.getGolTemplate(competition);
    }

    @Override
    public Competition overrideTermsAndConditions(Competition competition) {
        return commonBuilders.overrideTermsAndConditions(competition);
    }
}
