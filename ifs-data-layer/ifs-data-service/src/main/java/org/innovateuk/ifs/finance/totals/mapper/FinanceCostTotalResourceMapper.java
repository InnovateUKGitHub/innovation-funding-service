package org.innovateuk.ifs.finance.totals.mapper;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.totals.FinanceType;
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
            List<ApplicationFinanceResource> applicationFinanceResources
    ) {
        return flattenLists(applicationFinanceResources, this::mapFromApplicationFinanceResourceToList);
    }

    public List<FinanceCostTotalResource> mapFromApplicationFinanceResourceToList(
            ApplicationFinanceResource applicationFinanceResource
    ) {
        return simpleMap(
                applicationFinanceResource.getFinanceOrganisationDetails().entrySet(),
                cat -> buildFinanceCostTotalResource(
                        FinanceType.APPLICATION,
                        cat.getKey(),
                        cat.getValue(),
                        applicationFinanceResource.getId()
                )
        );
    }

    private static FinanceCostTotalResource buildFinanceCostTotalResource(
            FinanceType financeType,
            FinanceRowType financeRowType,
            FinanceRowCostCategory financeRowItem,
            Long financeId
    ) {
        return new FinanceCostTotalResource(
                financeType,
                financeRowType,
                financeRowItem.getTotal(),
                financeId
        );
    }
}