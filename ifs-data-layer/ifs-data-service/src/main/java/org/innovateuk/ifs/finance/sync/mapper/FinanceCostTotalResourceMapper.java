package org.innovateuk.ifs.finance.sync.mapper;

import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.sync.FinanceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps ApplicationFinanceResource calculated finance totals to FinanceCostTotalResource costs.
 */
@Component
public class FinanceCostTotalResourceMapper {
    public List<FinanceCostTotalResource> mapFromApplicationFinanceResourceListToList(List<ApplicationFinanceResource> applicationFinanceResources) {

        List<FinanceCostTotalResource> financeCostTotalResourceList = new ArrayList<>();
        applicationFinanceResources.stream().map(
                financeResource -> mapFromApplicationFinanceResourceToList(financeResource)
        ).collect(Collectors.toList()).forEach(financeCostTotalResourceList::addAll);

        return financeCostTotalResourceList;
    }

    public List<FinanceCostTotalResource> mapFromApplicationFinanceResourceToList(ApplicationFinanceResource applicationFinanceResource) {
        return applicationFinanceResource.getFinanceOrganisationDetails().entrySet().stream().map(
                cat -> buildFinanceCostTotalResource(cat.getKey(), cat.getValue(), FinanceType.APPLICATION.getName(), applicationFinanceResource.getId())
        ).collect(Collectors.toList());
    }

    private static FinanceCostTotalResource buildFinanceCostTotalResource(FinanceRowType financeRowType, FinanceRowCostCategory financeRowItem, String financeType, Long setFinanceId) {
        FinanceCostTotalResource financeCostTotalResource = new FinanceCostTotalResource();
        financeCostTotalResource.setName(financeRowType.getName());
        financeCostTotalResource.setTotal(financeRowItem.getTotal());
        financeCostTotalResource.setFinanceType(financeType);
        financeCostTotalResource.setFinanceId(setFinanceId);

        return financeCostTotalResource;
    }
}