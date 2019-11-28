package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional service to support operations on ProjectFinanceRow.  This is only permitted for use by internal finance users.
 */
public interface ProjectFinanceRowService {

    @PreAuthorize("hasPermission(#rowId, 'org.innovateuk.ifs.finance.domain.ProjectFinanceRow', 'CRUD')")
    ServiceResult<FinanceRowItem> get(long rowId);

    @PreAuthorize("hasPermission(#newCostItem.targetId, 'org.innovateuk.ifs.finance.resource.ProjectFinanceResource', 'ADD_ROW')")
    ServiceResult<FinanceRowItem> create(FinanceRowItem newCostItem);

    @PreAuthorize("hasPermission(#rowId, 'org.innovateuk.ifs.finance.domain.ProjectFinanceRow', 'CRUD')")
    ServiceResult<FinanceRowItem> update(long rowId, FinanceRowItem newCostItem);

    @PreAuthorize("hasPermission(#rowId, 'org.innovateuk.ifs.finance.domain.ProjectFinanceRow', 'CRUD')")
    ServiceResult<Void> delete(long rowId);

    @PostAuthorize("hasPermission(returnObject, 'READ_PROJECT_FINANCE')")
    ServiceResult<ProjectFinanceResource> financeChecksDetails(long projectId, long organisationId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<ProjectFinanceResource>> financeChecksTotals(long projectId);

    @NotSecured(value = "This is not getting data from the database, just getting a FinanceRowHandler for project", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getCostHandler(FinanceRowItem costItemId);
}
