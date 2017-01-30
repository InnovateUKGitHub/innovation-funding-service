package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ProjectFinanceRowService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "READ", securedType = ProjectFinanceResource.class, description = "Project Finance users can access costs from project finance")
    ServiceResult<List<? extends FinanceRow>> getCosts(Long projectFinanceId, String costTypeName, Long questionId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "READ", securedType = ProjectFinanceResource.class, description = "Project Finance users can access cost items from project finance")
    ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, String costTypeName, Long questionId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "READ", securedType = ProjectFinanceResource.class, description = "Project Finance users access costs to project finance")
    ServiceResult<List<FinanceRowItem>> getCostItems(Long projectFinanceId, Long questionId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectFinanceResource.class, description = "Project Finance users can add new costs to project finance")
    ServiceResult<FinanceRowItem> addCost(@P("projectFinanceId") Long projectFinanceId, Long questionId, FinanceRowItem newCostItem);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = FinanceRowItem.class, description = "Project Finance users can update  costs from project finance")
    ServiceResult<FinanceRowItem> updateCost(@P("costId")Long costId, FinanceRowItem newCostItem);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectFinanceResource.class, description = "Project Finance users can add new rows to project finance categories")
    ServiceResult<FinanceRowItem> addCostWithoutPersisting(@P("projectFinanceId") Long projectFinanceId, Long questionId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectFinanceResource.class, description = "Project Finance users can delete costs from project finance")
    ServiceResult<Void> deleteCost(@P("costId") Long costId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectFinanceResource.class, description = "Internal users can update the finance checks details")
    ServiceResult<ProjectFinanceResource> updateCost(@P("projectFinanceId") Long projectFinanceId, ProjectFinanceResource applicationFinance);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ", securedType = ProjectFinanceResource.class,
            description = "Internal users can access the finance checks details")
    ServiceResult<ProjectFinanceResource> financeChecksDetails(Long projectId, Long organisationId);

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "READ", securedType = ProjectFinanceResource.class,
            description = "Project Finance users can view financial overviews of Organisations on Projects")
    ServiceResult<List<ProjectFinanceResource>> financeChecksTotals(Long projectId);
}
