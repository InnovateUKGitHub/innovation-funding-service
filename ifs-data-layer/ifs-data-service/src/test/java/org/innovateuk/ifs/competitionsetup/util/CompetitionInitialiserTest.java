package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Test;

import java.util.EnumSet;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.*;
import static org.innovateuk.ifs.competitionsetup.util.CompetitionInitialiser.initialiseFinanceTypes;
import static org.innovateuk.ifs.competitionsetup.util.CompetitionInitialiser.initialiseProjectSetupColumns;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.junit.Assert.assertTrue;

public class CompetitionInitialiserTest {

    @Test
    public void initaliseFinance_Loan() {
        Competition competition = newCompetition().withFundingType(LOAN).build();
        Competition competitionWithFinanceTypes = initialiseFinanceTypes(competition);

        assertTrue(competitionWithFinanceTypes.getFinanceRowTypes().containsAll(EnumSet.of(
                LABOUR,
                OVERHEADS,
                MATERIALS,
                CAPITAL_USAGE,
                SUBCONTRACTING_COSTS,
                TRAVEL,
                OTHER_COSTS,
                GRANT_CLAIM_AMOUNT,
                OTHER_FUNDING,
                YOUR_FINANCE
        )));
    }
    @Test
    public void initaliseProjectSetup_Loan() {
        Competition competition = newCompetition().withFundingType(LOAN).build();
        Competition competitionWithFinanceTypes = initialiseProjectSetupColumns(competition);

        assertTrue(competitionWithFinanceTypes.getProjectSetupStages().containsAll(EnumSet.of(
                PROJECT_DETAILS,
                PROJECT_TEAM,
                MONITORING_OFFICER,
                FINANCE_CHECKS,
                SPEND_PROFILE
        )));
    }

    @Test
    public void initaliseFinance_Grant() {
        Competition competition = newCompetition().withFundingType(GRANT).build();
        Competition competitionWithFinanceTypes = initialiseFinanceTypes(competition);

        assertTrue(competitionWithFinanceTypes.getFinanceRowTypes().containsAll(EnumSet.of(
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
        )));
    }
    @Test
    public void initaliseProjectSetup_Grant() {
        Competition competition = newCompetition().withFundingType(GRANT).build();
        Competition competitionWithFinanceTypes = initialiseProjectSetupColumns(competition);

        assertTrue(competitionWithFinanceTypes.getProjectSetupStages().containsAll(EnumSet.of(
                PROJECT_DETAILS,
                PROJECT_TEAM,
                DOCUMENTS,
                MONITORING_OFFICER,
                BANK_DETAILS,
                FINANCE_CHECKS,
                SPEND_PROFILE,
                GRANT_OFFER_LETTER
        )));
    }

    @Test
    public void initaliseFinance_Procurement() {
        Competition competition = newCompetition().withFundingType(PROCUREMENT).build();
        Competition competitionWithFinanceTypes = initialiseFinanceTypes(competition);

        assertTrue(competitionWithFinanceTypes.getFinanceRowTypes().containsAll(EnumSet.of(
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
        )));
    }

    @Test
    public void initaliseProjectSetup_Procurement() {
        Competition competition = newCompetition().withFundingType(PROCUREMENT).build();
        Competition competitionWithFinanceTypes = initialiseProjectSetupColumns(competition);

        assertTrue(competitionWithFinanceTypes.getProjectSetupStages().containsAll(EnumSet.of(
                PROJECT_DETAILS,
                PROJECT_TEAM,
                MONITORING_OFFICER,
                BANK_DETAILS,
                FINANCE_CHECKS,
                SPEND_PROFILE,
                GRANT_OFFER_LETTER
        )));
    }
}
