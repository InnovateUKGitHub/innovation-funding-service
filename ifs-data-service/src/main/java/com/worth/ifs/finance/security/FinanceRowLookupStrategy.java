package com.worth.ifs.finance.security;

import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.repository.ApplicationFinanceRepository;
import com.worth.ifs.finance.repository.FinanceRowRepository;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;

 /**
 * Lookup strategies for {@link FinanceRow} and {@link FinanceRowItem} for permissioning
 */
@Component
@PermissionEntityLookupStrategies
public class FinanceRowLookupStrategy {
    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private FinanceRowRepository financeRowRepository;

    @PermissionEntityLookupStrategy
    public FinanceRow getFinanceRow(final Long costId) {
        return financeRowRepository.findOne(costId);
    }
}
