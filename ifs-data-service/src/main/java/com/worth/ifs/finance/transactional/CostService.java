package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CostService {

    @PreAuthorize("hasPermission(#costFieldId, 'com.worth.ifs.finance.resource.CostFieldResource', 'READ')")
    ServiceResult<CostField> getCostFieldById(@P("costFieldId")Long costFieldId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CostFieldResource>> findAllCostFields();

    @PreAuthorize("hasPermission(#costId, 'com.worth.ifs.application.resource.ApplicationResource', 'ADD_COST')")
    ServiceResult<CostItem> addCost(@P("applicationFinanceId")Long applicationFinanceId, Long questionId, CostItem newCostItem);

    @PreAuthorize("hasPermission(#costId, 'com.worth.ifs.finance.domain.Cost', 'UPDATE')")
    ServiceResult<Void> updateCost(@P("costId")Long costId, CostItem newCostItem);

    @PreAuthorize("hasPermission(#costId, 'com.worth.ifs.finance.domain.Cost', 'DELETE')")
    ServiceResult<Void> deleteCost(@P("costId")Long costId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(Long applicationId, Long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ_RESEARCH_PARTICIPATION_PERCENTAGE')")
    ServiceResult<Double> getResearchParticipationPercentage(@P("applicationId")Long applicationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<ApplicationFinanceResource> addCost(Long applicationId, Long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(Long applicationFinanceId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<ApplicationFinanceResource> updateCost(Long applicationFinanceId, ApplicationFinanceResource applicationFinance);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> financeDetails(Long applicationId, Long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationFinanceResource>> financeTotals(Long applicationId);
}