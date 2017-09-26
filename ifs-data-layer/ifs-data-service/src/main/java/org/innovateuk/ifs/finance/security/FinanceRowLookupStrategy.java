package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

 /**
 * Lookup strategies for {@link FinanceRow} and {@link FinanceRowItem} for permissioning
 */
@Component
@PermissionEntityLookupStrategies
public class FinanceRowLookupStrategy {
    @Autowired
    private ApplicationFinanceRowRepository applicationFinanceRowRepository;

    @Autowired
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @PermissionEntityLookupStrategy
    public FinanceRow getFinanceRow(final Long costId) {
        FinanceRow financeRow = applicationFinanceRowRepository.findById(costId);
        if(financeRow == null)
            financeRow = projectFinanceRowRepository.findOne(costId);
        return financeRow;
    }
}
