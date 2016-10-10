package com.worth.ifs.project.util;

import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryYearModel;
import com.worth.ifs.util.CollectionFunctions;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static com.worth.ifs.util.CollectionFunctions.simpleMapValue;

/**
 * Component for calculating row and column totals for spend profile tables.
 */
@Component
public class SpendProfileTableCalculator {

    public Map<String, BigDecimal> calculateRowTotal(Map<String, List<BigDecimal>> tableData) {
        return simpleMapValue(tableData, rows -> {
            return rows.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        });
    }

    public List<BigDecimal> calculateMonthlyTotals(Map<String, List<BigDecimal>> tableData, int numberOfMonths) {
        return IntStream.range(0, numberOfMonths).mapToObj(index -> {
            return tableData.values()
                    .stream()
                    .map(list -> list.get(index))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }).collect(Collectors.toList());
    }

    public BigDecimal calculateTotalOfAllActualTotals(Map<String, List<BigDecimal>> tableData) {
        return tableData.values()
                .stream()
                .map(list -> {
                    return list.stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalOfAllEligibleTotals(Map<String, BigDecimal> eligibleCostData) {
        return eligibleCostData
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public SpendProfileSummaryModel createSpendProfileSummary(ProjectResource project, Map<String, List<BigDecimal>> tableData, List<LocalDateResource> months) {
        Integer startYear = new FinancialYearDate(DateUtil.asDate(project.getTargetStartDate())).getFiscalYear();
        Integer endYear = new FinancialYearDate(DateUtil.asDate(project.getTargetStartDate().plusMonths(project.getDurationInMonths()))).getFiscalYear();
        List<SpendProfileSummaryYearModel> years = IntStream.range(startYear, endYear + 1).
                mapToObj(
                        year -> {
                            Set<String> keys = tableData.keySet();
                            BigDecimal totalForYear = BigDecimal.ZERO;

                            for (String key : keys) {
                                List<BigDecimal> values = tableData.get(key);
                                for (int i = 0; i < values.size(); i++) {
                                    LocalDateResource month = months.get(i);
                                    FinancialYearDate financialYearDate = new FinancialYearDate(DateUtil.asDate(month.getLocalDate()));
                                    if (year == financialYearDate.getFiscalYear()) {
                                        totalForYear = totalForYear.add(values.get(i));
                                    }
                                }
                            }
                            return new SpendProfileSummaryYearModel(year, totalForYear.toPlainString());
                        }

                ).collect(toList());

        return new SpendProfileSummaryModel(years);
    }

}