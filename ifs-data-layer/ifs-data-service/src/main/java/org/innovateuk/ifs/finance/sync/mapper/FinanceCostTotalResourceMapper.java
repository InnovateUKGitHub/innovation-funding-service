package org.innovateuk.ifs.finance.sync.mapper;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.sync.FinanceType;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Maps ApplicationFinanceResource calculated finance totals to {@link FinanceCostTotalResource} costs.
 */
@Component
public class FinanceCostTotalResourceMapper {
    public List<FinanceCostTotalResource> mapFromApplicationFinanceResourceListToList(
            List<ApplicationFinanceResource> applicationFinanceResources) {
        return flattenLists(applicationFinanceResources, this::mapFromApplicationFinanceResourceToList);
    }

    public List<FinanceCostTotalResource> mapFromApplicationFinanceResourceToList(
            ApplicationFinanceResource applicationFinanceResource) {
        return simpleMap(applicationFinanceResource.getFinanceOrganisationDetails().entrySet(), cat ->
                buildFinanceCostTotalResource(cat.getKey(),
                        cat.getValue(),
                        FinanceType.APPLICATION.getName(),
                        applicationFinanceResource.getId()));
    }

    private static FinanceCostTotalResource buildFinanceCostTotalResource(FinanceRowType financeRowType,
                                                                          FinanceRowCostCategory financeRowItem,
                                                                          String financeType,
                                                                          Long financeId) {
        return new FinanceCostTotalResource(
                financeRowType.getName(),
                financeRowItem.getTotal(),
                financeId,
                financeType);
    }
}