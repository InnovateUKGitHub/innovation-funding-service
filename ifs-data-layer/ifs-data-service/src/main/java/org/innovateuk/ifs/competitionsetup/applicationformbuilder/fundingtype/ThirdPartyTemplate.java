package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.YOUR_FINANCE;

@Component
public class ThirdPartyTemplate implements FundingTypeTemplate {

    @Value("${ifs.thirdparty.ofgem.enabled}")
    private boolean thirdPartyOfgemEnabled;

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public FundingType type() {
        return FundingType.THIRDPARTY;
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
                        MATERIALS,
                        SUBCONTRACTING_COSTS,
                        TRAVEL,
                        OTHER_COSTS,
                        OTHER_FUNDING,
                        YOUR_FINANCE);
        types.add((thirdPartyOfgemEnabled && competition.getCompetitionType().getCompetitionTypeEnum() == CompetitionTypeEnum.OFGEM) ? GRANT_CLAIM_AMOUNT : FINANCE);
        return commonBuilders.saveFinanceRows(competition, types);
    }

}

