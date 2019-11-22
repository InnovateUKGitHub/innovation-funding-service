package org.innovateuk.ifs.finance.transactional;

import java.util.List;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional service to support operations on ProjectFinanceRow.  This is only permitted for use by internal finance users.
 */
public interface ProjectFinanceRowService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "READ", securedType = ProjectFinanceResource.class, description = "Project Finance users can access cost items from project finance")
    ServiceResult<FinanceRowItem> get(long costItemId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectFinanceResource.class, description = "Project Finance users can add new costs to project finance")
    ServiceResult<FinanceRowItem> create(FinanceRowItem newCostItem);

    @PreAuthorize("hasAnyAuthority('project_finance', 'applicant')") //todo permissions
    @SecuredBySpring(value = "UPDATE", securedType = FinanceRowItem.class, description = "Project Finance users can update  costs from project finance")
    ServiceResult<FinanceRowItem> update(long costId, FinanceRowItem newCostItem);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectFinanceResource.class, description = "Project Finance users can delete costs from project finance")
    ServiceResult<Void> delete(long costId);

    @PostAuthorize("hasPermission(returnObject, 'READ_PROJECT_FINANCE')")
    ServiceResult<ProjectFinanceResource> financeChecksDetails(long projectId, long organisationId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<ProjectFinanceResource>> financeChecksTotals(long projectId);

    @NotSecured(value = "This is not getting data from the database, just getting a FinanceRowHandler for project", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getCostHandler(FinanceRowItem costItemId);
}
