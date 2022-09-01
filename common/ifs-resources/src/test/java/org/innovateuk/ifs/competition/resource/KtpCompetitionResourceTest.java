package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class KtpCompetitionResourceTest {

    private final FundingType fundingType;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public KtpCompetitionResourceTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void getFinanceRowTypesByFinanceForFecCostModel() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(fundingType)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .build();

        ApplicationFinanceResource finance = newApplicationFinanceResource()
                .withFecEnabled(true)
                .build();

        List<FinanceRowType> financeRowTypes = competition.getFinanceRowTypesByFinance(Optional.of(finance));

        assertNotNull(financeRowTypes);
        assertEquals(Arrays.asList(FinanceRowType.OTHER_COSTS,
                FinanceRowType.FINANCE,
                FinanceRowType.ASSOCIATE_SALARY_COSTS,
                FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS,
                FinanceRowType.CONSUMABLES,
                FinanceRowType.ASSOCIATE_SUPPORT,
                FinanceRowType.KNOWLEDGE_BASE,
                FinanceRowType.ESTATE_COSTS,
                FinanceRowType.KTP_TRAVEL,
                FinanceRowType.ADDITIONAL_COMPANY_COSTS,
                FinanceRowType.PREVIOUS_FUNDING), financeRowTypes);
    }

    @Test
    public void getFinanceRowTypesByFinanceForNonFecCostModel() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(fundingType)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .build();

        ApplicationFinanceResource finance = newApplicationFinanceResource()
                .withFecEnabled(false)
                .build();

        List<FinanceRowType> financeRowTypes = competition.getFinanceRowTypesByFinance(Optional.of(finance));

        assertNotNull(financeRowTypes);
        assertEquals(Arrays.asList(FinanceRowType.OTHER_COSTS,
                FinanceRowType.FINANCE,
                FinanceRowType.ASSOCIATE_SALARY_COSTS,
                FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS,
                FinanceRowType.CONSUMABLES,
                FinanceRowType.KTP_TRAVEL,
                FinanceRowType.ADDITIONAL_COMPANY_COSTS,
                FinanceRowType.PREVIOUS_FUNDING,
                FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT,
                FinanceRowType.INDIRECT_COSTS), financeRowTypes);
    }

    @Test
    public void isKtp() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(fundingType)
                .build();

        assertTrue(competition.isKtp());
    }
}
