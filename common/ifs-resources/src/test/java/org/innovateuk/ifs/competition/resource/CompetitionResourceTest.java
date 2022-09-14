package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static  org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class CompetitionResourceTest {

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

    @Test
    public void isOfGemCompetition() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.PROCUREMENT)
                .build();

        assertFalse(competition.isOfGemCompetition());

        CompetitionFunderResource competitionFunderResource = newCompetitionFunderResource()
                .withFunder(Funder.OFFICE_OF_GAS_AND_ELECTRICITY_MARKETS_OFGEM)
                .build();
        competition.setFunders(Collections.singletonList(competitionFunderResource));

        assertFalse(competition.isOfGemCompetition());

        GrantTermsAndConditionsResource grantTermsAndConditions = newGrantTermsAndConditionsResource()
                .withName("Third Party")
                .build();
        competition.setTermsAndConditions(grantTermsAndConditions);

        assertTrue(competition.isOfGemCompetition());
    }

    @Test
    public void isThirdPartyOfgem() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.THIRDPARTY)
                .build();

        assertFalse(competition.isThirdPartyOfgem());

        competition.setCompetitionTypeEnum(CompetitionTypeEnum.OFGEM);

        assertTrue(competition.isThirdPartyOfgem());
    }

    @Test
    public void isKtpOnly() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.KTP_AKT)
                .build();

        assertFalse(competition.isKtpOnly());

        competition.setFundingType(FundingType.KTP);

        assertTrue(competition.isKtpOnly());
    }
}
