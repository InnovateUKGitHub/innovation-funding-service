package org.innovateuk.ifs.finance.sync.filter;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Filters a list of {@link FinanceCostTotalResource}s and return only the ones tagged as "Spend Profile".
 */

@Component
public class SpendProfileCostFilter {
    public List<FinanceCostTotalResource> filterBySpendProfile(List<FinanceCostTotalResource> financeCostTotalResources) {
        return simpleFilter(financeCostTotalResources, financeResource -> isSpendProfile(financeResource.getName()));
    }

    private boolean isSpendProfile(String typeName) {
        return FinanceRowType.getByTypeName(typeName)
                .map(FinanceRowType::isIncludedInSpendProfile)
                .orElse(false);
    }
}
