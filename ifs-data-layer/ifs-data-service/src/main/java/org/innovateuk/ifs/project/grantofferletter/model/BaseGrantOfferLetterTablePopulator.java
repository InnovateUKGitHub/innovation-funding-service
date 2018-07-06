package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Base class for grant offer letter finance table populators
 **/
public class BaseGrantOfferLetterTablePopulator {

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


//    protected Map<String, List<ProjectFinanceRow>> mapAndFilter()
    protected Boolean isAcademic(OrganisationType type) {
        return OrganisationTypeEnum.RESEARCH.getId().equals(type.getId());
    }
}
