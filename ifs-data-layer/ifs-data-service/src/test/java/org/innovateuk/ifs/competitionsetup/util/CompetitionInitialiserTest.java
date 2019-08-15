package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Test;

import java.util.EnumSet;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.*;
import static org.innovateuk.ifs.competitionsetup.util.CompetitionInitialiser.initialiseFinanceTypes;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

public class CompetitionInitialiserTest {

    @Test
    public void initaliseFinance_Loan() {
        Competition competition = newCompetition().withFundingType(LOAN).build();
        Competition competitionWithFinanceTypes = initialiseFinanceTypes(competition);

        competitionWithFinanceTypes.getFinanceRowTypes().containsAll(EnumSet.of(
                LABOUR,
                OVERHEADS,
                MATERIALS,
                CAPITAL_USAGE,
                SUBCONTRACTING_COSTS,
                TRAVEL,
                OTHER_COSTS,
                FINANCE,
                OTHER_FUNDING
        ));
    }

    @Test
    public void initaliseFinance_Grant() {
        Competition competition = newCompetition().withFundingType(GRANT).build();
        Competition competitionWithFinanceTypes = initialiseFinanceTypes(competition);

        competitionWithFinanceTypes.getFinanceRowTypes().containsAll(EnumSet.of(
                LABOUR,
                OVERHEADS,
                MATERIALS,
                CAPITAL_USAGE,
                SUBCONTRACTING_COSTS,
                TRAVEL,
                OTHER_COSTS,
                FINANCE,
                OTHER_FUNDING,
                YOUR_FINANCE
        ));
    }

    @Test
    public void initaliseFinance_Procurement() {
        Competition competition = newCompetition().withFundingType(PROCUREMENT).build();
        Competition competitionWithFinanceTypes = initialiseFinanceTypes(competition);

        competitionWithFinanceTypes.getFinanceRowTypes().containsAll(EnumSet.of(
                LABOUR,
                PROCUREMENT_OVERHEADS,
                MATERIALS,
                CAPITAL_USAGE,
                SUBCONTRACTING_COSTS,
                TRAVEL,
                OTHER_COSTS,
                FINANCE,
                OTHER_FUNDING,
                VAT
        ));
    }
}
