package org.innovateuk.ifs.project.spendprofile.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.project.model.SpendProfileSummaryModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SpendProfileTableCalculatorTest {

    private static final Long ROW_NAME_1 = 1L;
    private static final Long ROW_NAME_2 = 2L;

    private static final String ROW_NAME_3 = "test1";
    private static final String ROW_NAME_4 = "test2";


    private static final BigDecimal CELL_1_1 = new BigDecimal("10.19");
    private static final BigDecimal CELL_1_2 = new BigDecimal("15.49");
    private static final BigDecimal CELL_2_1 = new BigDecimal("40.12");
    private static final BigDecimal CELL_2_2 = new BigDecimal("21.32");

    private static final BigDecimal ROW_1_EXPECTED_TOTAL = CELL_1_1.add(CELL_1_2);
    private static final BigDecimal ROW_2_EXPECTED_TOTAL = CELL_2_1.add(CELL_2_2);

    private static final BigDecimal COLUMN_1_EXPECTED_TOTAL = CELL_1_1.add(CELL_2_1);
    private static final BigDecimal COLUMN_2_EXPECTED_TOTAL = CELL_1_2.add(CELL_2_2);

    private static final BigDecimal TOTAL_OF_ALL_TOTALS = CELL_1_1.add(CELL_1_2)
            .add(CELL_2_1)
            .add(CELL_2_2);

    private static final Map<Long, List<BigDecimal>> TABLE_DATA = ImmutableMap.<Long, List<BigDecimal>>builder()
            .put(ROW_NAME_1, Lists.newArrayList(CELL_1_1, CELL_1_2))
            .put(ROW_NAME_2, Lists.newArrayList(CELL_2_1, CELL_2_2))
            .build();

    private static final Map<String, List<BigDecimal>> TABLE_DATA2 = ImmutableMap.<String, List<BigDecimal>>builder()
            .put(ROW_NAME_3, Lists.newArrayList(CELL_1_1, CELL_1_2))
            .put(ROW_NAME_4, Lists.newArrayList(CELL_2_1, CELL_2_2))
            .build();


    //Use row totals as eligible costs for this test.
    private static final Map<Long, BigDecimal> ELIGIBLE_DATA = ImmutableMap.<Long, BigDecimal>builder()
            .put(ROW_NAME_1, ROW_1_EXPECTED_TOTAL)
            .put(ROW_NAME_2, ROW_2_EXPECTED_TOTAL)
            .build();


    @InjectMocks
    private SpendProfileTableCalculator spendProfileTableCalculator;

    @Test
    public void testCalculateRowTotal() {
        Map<Long, BigDecimal> result = spendProfileTableCalculator.calculateRowTotal(TABLE_DATA);

        assertThat(result.get(ROW_NAME_1), equalTo(ROW_1_EXPECTED_TOTAL));
        assertThat(result.get(ROW_NAME_2), equalTo(ROW_2_EXPECTED_TOTAL));
    }

    @Test
    public void testCalculateMonthlyTotals() {
        int columns = 2;

        List<BigDecimal> result = spendProfileTableCalculator.calculateMonthlyTotals(TABLE_DATA, columns);

        assertThat(result.size(), equalTo(columns));
        assertThat(result.get(0), equalTo(COLUMN_1_EXPECTED_TOTAL));
        assertThat(result.get(1), equalTo(COLUMN_2_EXPECTED_TOTAL));
    }

    @Test
    public void testCalculateTotalOfAllActualTotals() {
        BigDecimal result = spendProfileTableCalculator.calculateTotalOfAllActualTotals(TABLE_DATA);

        assertThat(result, equalTo(TOTAL_OF_ALL_TOTALS));
    }

    @Test
    public void testCalculateTotalOfAllEligibleTotals() {
        BigDecimal result = spendProfileTableCalculator.calculateTotalOfAllEligibleTotals(ELIGIBLE_DATA);

        assertThat(result, equalTo(TOTAL_OF_ALL_TOTALS));
    }

    @Test
    public void testCreateSpendProfileSummary() {
        ProjectResource project = newProjectResource()
                .withTargetStartDate(LocalDate.of(2019, 3, 1))
                .withDuration(2L)
                .build();
        //Set two months that will spread over two financial years.
        List<LocalDateResource> months = Lists.newArrayList(
                new LocalDateResource(1, 3, 2019),
                new LocalDateResource(1, 4, 2019)
        );

        SpendProfileSummaryModel result = spendProfileTableCalculator.createSpendProfileSummary(project, TABLE_DATA, months);

        assertThat(result.getYears().size(), equalTo(2));
        assertThat(result.getYears().get(0).getAmount(), equalTo(COLUMN_1_EXPECTED_TOTAL.toPlainString()));
        assertThat(result.getYears().get(1).getAmount(), equalTo(COLUMN_2_EXPECTED_TOTAL.toPlainString()));
    }

    @Test
    public void testCalculateEligibleCostPerYear() {
        ProjectResource projectResource = newProjectResource()
                .withTargetStartDate(LocalDate.of(2019, 3, 1))
                .withDuration(2L)
                .build();

        LinkedList<BigDecimal> monthlyCosts = new LinkedList<>();
        monthlyCosts.add(BigDecimal.valueOf(300));
        monthlyCosts.add(BigDecimal.valueOf(100));
        monthlyCosts.add(BigDecimal.valueOf(400));

        LocalDateResource localDateResource1 = new LocalDateResource(1, 3, 2019);
        LocalDateResource localDateResource2 = new LocalDateResource(1, 4, 2019);
        LocalDateResource localDateResource3 = new LocalDateResource(1, 5, 2019);

        List<LocalDateResource> months = new ArrayList<>();
        months.add(localDateResource1);
        months.add(localDateResource2);
        months.add(localDateResource3);

        List<BigDecimal> eligibleCostPerYear = spendProfileTableCalculator.calculateEligibleCostPerYear(projectResource, monthlyCosts, months);

        assertTrue(eligibleCostPerYear.size() == 2);
        assertEquals(eligibleCostPerYear.get(0), BigDecimal.valueOf(300));
        assertEquals(eligibleCostPerYear.get(1), BigDecimal.valueOf(500));
    }

    @Test
    public void testCalculateGrantAllocationPerYear() {
        ProjectResource projectResource = newProjectResource()
                .withTargetStartDate(LocalDate.of(2019, 3, 1))
                .withDuration(2L)
                .build();

        LinkedList<BigDecimal> monthlyCosts = new LinkedList<>();
        monthlyCosts.add(BigDecimal.valueOf(300));
        monthlyCosts.add(BigDecimal.valueOf(100));
        monthlyCosts.add(BigDecimal.valueOf(400));

        LocalDateResource localDateResource1 = new LocalDateResource(1, 3, 2019);
        LocalDateResource localDateResource2 = new LocalDateResource(1, 4, 2019);
        LocalDateResource localDateResource3 = new LocalDateResource(1, 5, 2019);

        List<LocalDateResource> months = new ArrayList<>();
        months.add(localDateResource1);
        months.add(localDateResource2);
        months.add(localDateResource3);

        List<BigDecimal> grantAllocationPerYear = spendProfileTableCalculator.calculateGrantAllocationPerYear(projectResource, monthlyCosts, months, 30);

        assertTrue(grantAllocationPerYear.size() == 2);
        assertEquals(grantAllocationPerYear.get(0), BigDecimal.valueOf(90.0));
        assertEquals(grantAllocationPerYear.get(1), BigDecimal.valueOf(150.0));
    }

    @Test
    public void testCreateYearlyEligibleCostTotal() {
        ProjectResource projectResource = newProjectResource()
                .withTargetStartDate(LocalDate.of(2019, 3, 1))
                .withDuration(2L)
                .build();

        LinkedList<BigDecimal> monthlyCosts = new LinkedList<>();
        monthlyCosts.add(BigDecimal.valueOf(300));
        monthlyCosts.add(BigDecimal.valueOf(100));
        monthlyCosts.add(BigDecimal.valueOf(400));

        LocalDateResource localDateResource1 = new LocalDateResource(1, 3, 2019);
        LocalDateResource localDateResource2 = new LocalDateResource(1, 4, 2019);
        LocalDateResource localDateResource3 = new LocalDateResource(1, 5, 2019);

        List<LocalDateResource> months = new ArrayList<>();
        months.add(localDateResource1);
        months.add(localDateResource2);
        months.add(localDateResource3);

        Map<String, BigDecimal> yearlyEligibleCostTotal = spendProfileTableCalculator.createYearlyEligibleCostTotal(projectResource, TABLE_DATA2, months);

        assertTrue(yearlyEligibleCostTotal.size() == 2);
        assertEquals(yearlyEligibleCostTotal.get("2019"), BigDecimal.valueOf(36.81));
        assertEquals(yearlyEligibleCostTotal.get("2018"), BigDecimal.valueOf(50.31));
    }

    @Test
    public void testCreateYearlyGrantAllocationTotal() {
        ProjectResource projectResource = newProjectResource()
                .withTargetStartDate(LocalDate.of(2019, 3, 1))
                .withDuration(2L)
                .build();

        LinkedList<BigDecimal> monthlyCosts = new LinkedList<>();
        monthlyCosts.add(BigDecimal.valueOf(300));
        monthlyCosts.add(BigDecimal.valueOf(100));
        monthlyCosts.add(BigDecimal.valueOf(400));

        LocalDateResource localDateResource1 = new LocalDateResource(1, 3, 2019);
        LocalDateResource localDateResource2 = new LocalDateResource(1, 4, 2019);
        LocalDateResource localDateResource3 = new LocalDateResource(1, 5, 2019);

        List<LocalDateResource> months = new ArrayList<>();
        months.add(localDateResource1);
        months.add(localDateResource2);
        months.add(localDateResource3);

        Map<String, BigDecimal> yearlyGrantAllocationTotal = spendProfileTableCalculator.createYearlyGrantAllocationTotal(projectResource, TABLE_DATA2, months, BigDecimal.valueOf(25));

        assertTrue(yearlyGrantAllocationTotal.size() == 2);
        assertEquals(yearlyGrantAllocationTotal.get("2019"), BigDecimal.valueOf(9));
        assertEquals(yearlyGrantAllocationTotal.get("2018"), BigDecimal.valueOf(12));
    }

    @Test
    public void testGenerateSpendProfileYears() {
        ProjectResource projectResource = newProjectResource()
                .withTargetStartDate(LocalDate.of(2019, 3, 1))
                .withDuration(2L)
                .build();

        List<String> profileYears = spendProfileTableCalculator.generateSpendProfileYears(projectResource);

        assertTrue(profileYears.size() == 2);
        assertEquals(profileYears.get(0), "2018");
        assertEquals(profileYears.get(1), "2019");
    }

    @Test
    public void tesGetAllocationValue() {
        BigDecimal allocationValue = spendProfileTableCalculator.getAllocationValue(BigDecimal.valueOf(212153), BigDecimal.valueOf(30));

        assertEquals(allocationValue, BigDecimal.valueOf(63645));
    }

}
