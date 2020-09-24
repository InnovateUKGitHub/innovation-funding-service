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
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;

@Component
public class LoanBuilder implements FundingTypeTemplate {

    @Autowired
    private CommonBuilders commonBuilders;

    @Override
    public FundingType type() {
        return FundingType.LOAN;
    }

    @Override
    public List<SectionBuilder> sections(List<SectionBuilder> competitionTypeSections) {
        return competitionTypeSections;
    }

    @Override
    public Competition initialiseFinanceTypes(Competition competition) {
        List<FinanceRowType> types = newArrayList(LABOUR, OVERHEADS, MATERIALS, CAPITAL_USAGE, SUBCONTRACTING_COSTS, TRAVEL, OTHER_COSTS, GRANT_CLAIM_AMOUNT, OTHER_FUNDING);
        return commonBuilders.saveFinanceRows(competition, types);
    }

    @Override
    public Competition initialiseProjectSetupColumns(Competition competition) {
        addLoanProjectSetupColumns(competition);
        return competition;
    }

    private void addLoanProjectSetupColumns(Competition competition) {
        commonBuilders.addProjectSetupStage(competition, PROJECT_DETAILS);
        commonBuilders.addProjectSetupStage(competition, PROJECT_TEAM);
        commonBuilders.addProjectSetupStage(competition, MONITORING_OFFICER);
        commonBuilders.addProjectSetupStage(competition, FINANCE_CHECKS);
        commonBuilders.addProjectSetupStage(competition, SPEND_PROFILE);
        commonBuilders.addProjectSetupStage(competition, PROJECT_SETUP_COMPLETE);
    }
}
