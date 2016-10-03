package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.domain.CostCategoryType;
import com.worth.ifs.project.finance.resource.CostCategoryTypeResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Service dealing with Project finance operations
 */
public interface ProjectFinanceService {

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "GENERATE_SPEND_PROFILE", securedType = ProjectResource.class, description = "A member of the internal Finance Team can generate a Spend Profile for any Project" )
    ServiceResult<Void> generateSpendProfile(Long projectId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_SPEND_PROFILE')")
    ServiceResult<SpendProfileTableResource> getSpendProfileTable(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'VIEW_SPEND_PROFILE')")
    ServiceResult<SpendProfileResource> getSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'EDIT_SPEND_PROFILE')")
    ServiceResult<Void> saveSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId, SpendProfileTableResource table);

    @PreAuthorize("hasPermission(#projectOrganisationCompositeId, 'MARK_SPEND_PROFILE_COMPLETE')")
    ServiceResult<Void> markSpendProfile(ProjectOrganisationCompositeId projectOrganisationCompositeId, Boolean complete);

    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    ServiceResult<CostCategoryTypeResource> findByCostCategoryGroupId(Long costCategoryGroupId);
}
