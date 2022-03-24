package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

@Component
public class HecpTemplate implements FundingTypeTemplate {

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public FundingType type() {
        return FundingType.HECP;
    }

    @Override
    public Competition setGolTemplate(Competition competition) {
        return commonBuilders.getGolTemplate(competition);
    }

    @Override
    public Competition initialiseFinanceTypes(Competition competition) {
        List<FinanceRowType> types =
                newArrayList(
                        LABOUR,
                        SUBCONTRACTING_COSTS,
                        TRAVEL,
                        MATERIALS,
                        CAPITAL_USAGE,
                        OTHER_COSTS,
                        OVERHEADS,
                        FINANCE,
                        OTHER_FUNDING,
                        YOUR_FINANCE);
        return commonBuilders.saveFinanceRows(competition, types);
    }
}