package com.worth.ifs.finance.security;

import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.repository.FinanceRowRepository;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

 /**
 * Lookup strategies for {@link FinanceRow} and {@link FinanceRowItem} for permissioning
 */
@Component
@PermissionEntityLookupStrategies
public class FinanceRowLookupStrategy {

    @Autowired
    private FinanceRowRepository financeRowRepository;

    @PermissionEntityLookupStrategy
    public FinanceRow getFinanceRow(final Long costId) {
        FinanceRow financeRow = financeRowRepository.findOne(costId);

        // TODO INFUND-5955 - having to explicitly initialise here
        financeRow.getApplicationFinance().getApplication().getId();
        financeRow.getApplicationFinance().getOrganisation().getId();
        return financeRow;
    }
}
