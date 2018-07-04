package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple interface for a grant offer letter finance table
 */
public class GrantOfferLetterFinanceTable {

    protected Map<String, BigDecimal> sumByFinancialType(Map<String, List<ProjectFinanceRow>> financials, String type) {
        Map<String, BigDecimal> financeMap = new HashMap<>();
        financials.forEach( (orgName, finances) -> {
            BigDecimal financeSum = finances
                    .stream()
                    .filter(pfr -> type.equals(pfr.getName()))
                    .map(ProjectFinanceRow::getCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            financeMap.put(orgName, financeSum);
        });
        return financeMap;
    }

    protected BigDecimal sumTotals(Map<String, BigDecimal> financials) {
        return financials
                .values()
                .stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
