package org.innovateuk.ifs.util;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceRowBuilder.newProjectFinanceRow;
import static org.junit.Assert.*;

public class KtpFecFilterTest extends BaseUnitTestMocksTest {

    private KtpFecFilter ktpFecFilter;

    @Before
    public void setUp() {
        ktpFecFilter = new KtpFecFilter();
        ReflectionTestUtils.setField(ktpFecFilter, "fecFinanceModel", true);
    }

    @Test
    public void filterKtpFecCostCategoriesIfRequiredFecEnabled() {
        Competition competition = newCompetition()
                .withFundingType(FundingType.KTP)
                .build();

        Application application = newApplication()
                .withCompetition(competition)
                .build();

        ApplicationFinance applicationFinance = newApplicationFinance()
                .withFecEnabled(true)
                .withApplication(application)
                .build();

        List<ProjectFinanceRow> projectFinanceRows = newProjectFinanceRow().
                withType(FinanceRowType.ASSOCIATE_SUPPORT, FinanceRowType.ESTATE_COSTS, FinanceRowType.KNOWLEDGE_BASE,
                        FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, FinanceRowType.INDIRECT_COSTS).
                build(5);

        List<? extends FinanceRow> financeRows = ktpFecFilter.filterKtpFecCostCategoriesIfRequired(applicationFinance, projectFinanceRows);

        assertNotNull(financeRows);
        assertEquals(3, financeRows.size());
        assertThat(financeRows, containsInAnyOrder(asList(
                hasProperty("type", equalTo(FinanceRowType.ASSOCIATE_SUPPORT)),
                hasProperty("type", equalTo(FinanceRowType.ESTATE_COSTS)),
                hasProperty("type", equalTo(FinanceRowType.KNOWLEDGE_BASE))
        )));
    }

    @Test
    public void filterKtpFecCostCategoriesIfRequiredFecDisabled() {
        Competition competition = newCompetition()
                .withFundingType(FundingType.KTP)
                .build();

        Application application = newApplication()
                .withCompetition(competition)
                .build();

        ApplicationFinance applicationFinance = newApplicationFinance()
                .withFecEnabled(false)
                .withApplication(application)
                .build();

        List<ProjectFinanceRow> projectFinanceRows = newProjectFinanceRow().
                withType(FinanceRowType.ASSOCIATE_SUPPORT, FinanceRowType.ESTATE_COSTS, FinanceRowType.KNOWLEDGE_BASE,
                        FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, FinanceRowType.INDIRECT_COSTS).
                build(5);

        List<? extends FinanceRow> financeRows = ktpFecFilter.filterKtpFecCostCategoriesIfRequired(applicationFinance, projectFinanceRows);

        assertNotNull(financeRows);
        assertEquals(2, financeRows.size());
        assertThat(financeRows, containsInAnyOrder(asList(
                hasProperty("type", equalTo(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT)),
                hasProperty("type", equalTo(FinanceRowType.INDIRECT_COSTS))
        )));
    }

    @Test
    public void filterKtpFecCostCategoriesIfRequiredWhenFecFinanceModelDisabled() {
        ReflectionTestUtils.setField(ktpFecFilter, "fecFinanceModel", false);

        Competition competition = newCompetition()
                .withFundingType(FundingType.KTP)
                .build();

        Application application = newApplication()
                .withCompetition(competition)
                .build();

        ApplicationFinance applicationFinance = newApplicationFinance()
                .withApplication(application)
                .build();

        List<ProjectFinanceRow> projectFinanceRows = newProjectFinanceRow().
                withType(FinanceRowType.ASSOCIATE_SUPPORT, FinanceRowType.ESTATE_COSTS, FinanceRowType.KNOWLEDGE_BASE,
                        FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, FinanceRowType.INDIRECT_COSTS).
                build(5);

        List<? extends FinanceRow> financeRows = ktpFecFilter.filterKtpFecCostCategoriesIfRequired(applicationFinance, projectFinanceRows);

        assertNotNull(financeRows);
        assertEquals(5, financeRows.size());
        assertThat(financeRows, containsInAnyOrder(asList(
                hasProperty("type", equalTo(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT)),
                hasProperty("type", equalTo(FinanceRowType.ASSOCIATE_SUPPORT)),
                hasProperty("type", equalTo(FinanceRowType.ESTATE_COSTS)),
                hasProperty("type", equalTo(FinanceRowType.INDIRECT_COSTS)),
                hasProperty("type", equalTo(FinanceRowType.KNOWLEDGE_BASE))
        )));
    }
}
