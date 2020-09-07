package org.innovateuk.ifs.competitionsetup.util;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionFinanceRowsTypesRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.EnumSet;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.*;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionInitialiserTest {

    @Mock
    private CompetitionFinanceRowsTypesRepository competitionFinanceRowsTypesRepository;

    @InjectMocks
    private CompetitionInitialiser initialiser;

    @Before
    public void setUp() {
        when(competitionFinanceRowsTypesRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    public void initialiseFinance_Loan() {
        Competition competition = newCompetition().withFundingType(LOAN).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseFinanceTypes(competition);

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
    public void initialiseProjectSetup_Loan() {
        Competition competition = newCompetition().withFundingType(LOAN).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseProjectSetupColumns(competition);

        assertTrue(competitionWithFinanceTypes.getProjectSetupStages().containsAll(EnumSet.of(
                PROJECT_DETAILS,
                PROJECT_TEAM,
                MONITORING_OFFICER,
                FINANCE_CHECKS,
                SPEND_PROFILE
        )));
    }

    @Test
    public void initialiseFinance_Grant() {
        Competition competition = newCompetition().withFundingType(GRANT).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseFinanceTypes(competition);

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
    public void initialiseProjectSetup_Grant() {
        Competition competition = newCompetition().withFundingType(GRANT).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseProjectSetupColumns(competition);

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
    public void initialiseFinance_InvestorPartnership() {
        Competition competition = newCompetition().withFundingType(INVESTOR_PARTNERSHIPS).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseFinanceTypes(competition);

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
    public void initialiseProjectSetup_InvestorPartnership() {
        Competition competition = newCompetition().withFundingType(INVESTOR_PARTNERSHIPS).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseProjectSetupColumns(competition);

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
    public void initialiseFinance_Procurement() {
        Competition competition = newCompetition().withFundingType(PROCUREMENT).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseFinanceTypes(competition);

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
    public void initialiseProjectSetup_Procurement() {
        Competition competition = newCompetition().withFundingType(PROCUREMENT).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseProjectSetupColumns(competition);

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

    @Test
    public void initialiseFinance_KTP() {
        Competition competition = newCompetition().withFundingType(KTP).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseFinanceTypes(competition);

        assertTrue(competitionWithFinanceTypes.getFinanceRowTypes().containsAll(EnumSet.of(
                TRAVEL,
                OTHER_COSTS,
                FINANCE,
                OTHER_FUNDING,
                YOUR_FINANCE
        )));
    }

    @Test
    public void initialiseProjectSetup_KTP() {
        Competition competition = newCompetition().withFundingType(KTP).build();
        Competition competitionWithFinanceTypes = initialiser.initialiseProjectSetupColumns(competition);

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
}
