package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;


public interface ApplicationFinanceRowService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<FinanceRowItem> get(long financeRowId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    ServiceResult<FinanceRowItem> create(long applicationFinanceId, FinanceRowItem financeRowItem);

    @PreAuthorize("hasPermission(#financeRowId, 'org.innovateuk.ifs.finance.domain.ApplicationFinanceRow', 'UPDATE')")
    ServiceResult<FinanceRowItem> update(long financeRowId, FinanceRowItem financeRowItem);

    @PreAuthorize("hasPermission(#financeRowId, 'org.innovateuk.ifs.finance.domain.ApplicationFinanceRow', 'DELETE')")
    ServiceResult<Void> delete(long financeRowId);

    //Internal
    @NotSecured(value = "This is not getting data from the database, just getting a FinanceRowHandler", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getCostHandler(long costItemId);
}
