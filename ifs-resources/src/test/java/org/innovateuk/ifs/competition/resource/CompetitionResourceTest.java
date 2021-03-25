package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static  org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

public class CompetitionResourceTest {
    @Test
    public void getFinanceRowTypesByFinanceForFecCostModel() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.KTP)
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
                .withFundingType(FundingType.KTP)
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
    public void getFinanceRowTypesByFinanceForEmptyFinance() {
        List<FinanceRowType> expectedFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes();

        CompetitionResource competition = newCompetitionResource()
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .build();

        List<FinanceRowType> financeRowTypes = competition.getFinanceRowTypesByFinance(Optional.empty());

        assertNotNull(financeRowTypes);
        assertThat(financeRowTypes, containsInAnyOrder(expectedFinanceRowTypes.toArray()));
    }

    @Test
    public void getFinanceRowTypesByFinanceForNonKtpCostModel() {
        List<FinanceRowType> expectedFinanceRowTypes = Arrays.stream(FinanceRowType.values()).collect(Collectors.toList());

        CompetitionResource competition = newCompetitionResource()
                .withFinanceRowTypes(Arrays.stream(FinanceRowType.values()).collect(Collectors.toList()))
                .build();

        ApplicationFinanceResource finance = newApplicationFinanceResource()
                .withFecEnabled(null)
                .build();

        List<FinanceRowType> financeRowTypes = competition.getFinanceRowTypesByFinance(Optional.of(finance));

        assertNotNull(financeRowTypes);
        assertThat(financeRowTypes, containsInAnyOrder(expectedFinanceRowTypes.toArray()));
    }
}
