package com.worth.ifs.project.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.builder.ProjectResourceBuilder;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class SpendProfileTableCalculatorTest extends BaseUnitTest {

    private static final String ROW_NAME_1 = "ROW_NAME_1";
    private static final String ROW_NAME_2 = "ROW_NAME_2";

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

    private static final Map<String, List<BigDecimal>> TABLE_DATA = ImmutableMap.<String, List<BigDecimal>>builder()
            .put(ROW_NAME_1, Lists.newArrayList(CELL_1_1, CELL_1_2))
            .put(ROW_NAME_2, Lists.newArrayList(CELL_2_1, CELL_2_2))
            .build();

    //Use row totals as eligible costs for this test.
    private static final Map<String, BigDecimal> ELIGIBLE_DATA = ImmutableMap.<String, BigDecimal>builder()
            .put(ROW_NAME_1, ROW_1_EXPECTED_TOTAL)
            .put(ROW_NAME_2, ROW_2_EXPECTED_TOTAL)
            .build();


    @InjectMocks
    private SpendProfileTableCalculator spendProfileTableCalculator;

    @Test
    public void testCalculateRowTotal() {
        Map<String, BigDecimal> result = spendProfileTableCalculator.calculateRowTotal(TABLE_DATA);

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
        ProjectResource project = ProjectResourceBuilder.newProjectResource()
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

}