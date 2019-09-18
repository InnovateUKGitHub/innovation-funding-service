package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface ApplicationFinanceRowService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<FinanceRowItem> get(long financeRowId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    ServiceResult<FinanceRowItem> create(long applicationFinanceId, FinanceRowItem financeRowItem);

    @PreAuthorize("hasPermission(#financeRowId, 'org.innovateuk.ifs.finance.domain.FinanceRow', 'UPDATE')")
    ServiceResult<FinanceRowItem> update(long financeRowId, FinanceRowItem financeRowItem);

    @PreAuthorize("hasPermission(#financeRowId, 'org.innovateuk.ifs.finance.domain.FinanceRow', 'DELETE')")
    ServiceResult<Void> delete(long financeRowId);

    //Internal
    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FinanceRowItem>> getCostItems(long applicationFinanceId, FinanceRowType type);

    //Internal
    @NotSecured(value = "This is not getting data from the database, just getting a FinanceRowHandler", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getCostHandler(long costItemId);
}
