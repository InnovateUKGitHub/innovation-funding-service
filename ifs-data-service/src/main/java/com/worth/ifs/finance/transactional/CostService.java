package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.method.P;

import java.util.List;

public interface CostService {
    // TODO qqRP
//    @PreAuthorize("hasPermission(#costFieldId, 'com.worth.ifs.finance.resource.CostFieldResource', 'READ')")
    @NotSecured("TODO")
    ServiceResult<CostField> getCostFieldById(@P("costFieldId") Long costFieldId);


    //    @PostFilter("hasPermission(filterObject, 'READ')")
    @NotSecured("TODO")
    ServiceResult<List<CostFieldResource>> findAllCostFields();

    // TODO qqRP
    @NotSecured("TODO")
    ServiceResult<CostItem> getCostItem(Long costItemId);

    // TODO qqRP
    @NotSecured("TODO")
    ServiceResult<List<Cost>> getCosts(Long applicationFinanceId, String costTypeName, Long questionId);

    // TODO qqRP
    @NotSecured("TODO")
    ServiceResult<List<CostItem>> getCostItems(Long applicationFinanceId, String costTypeName, Long questionId);

    // TODO qqRP
    @NotSecured("TODO")
    ServiceResult<List<CostItem>> getCostItems(Long applicationFinanceId, Long questionId);

    // TODO qqRP
    // @PreAuthorize("hasPermission(#applicationFinanceId, 'com.worth.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    @NotSecured("TODO")
    ServiceResult<CostItem> addCost(@P("applicationFinanceId") Long applicationFinanceId, Long questionId, CostItem newCostItem);

    // TODO qqRP
    //@PreAuthorize("hasPermission(#costId, 'com.worth.ifs.finance.domain.Cost', 'UPDATE')")
    @NotSecured("TODO")
    ServiceResult<CostItem> updateCost(@P("costId") Long costId, CostItem newCostItem);

    // TODO qqRP
    //@PreAuthorize("hasPermission(#costId, 'com.worth.ifs.finance.domain.Cost', 'DELETE')")
    @NotSecured("TODO")
    ServiceResult<Void> deleteCost(@P("costId") Long costId);

    // TODO qqRP
    //@PostAuthorize("hasPermission(returnObject, 'READ')")
    @NotSecured("TODO")
    ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(Long applicationId, Long organisationId);

    // TODO qqRP
    //@PostFilter("hasPermission(filterObject, 'READ')")
    @NotSecured("TODO")
    ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(Long applicationId);

    // TODO qqRP
    //@PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ_RESEARCH_PARTICIPATION_PERCENTAGE')")
    @NotSecured("TODO")
    ServiceResult<Double> getResearchParticipationPercentage(@P("applicationId") Long applicationId);

    // TODO qqRP
    //@PreAuthorize("hasPermission(#applicationFinanceResourceId, 'com.worth.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    @NotSecured("TODO")
    ServiceResult<ApplicationFinanceResource> addCost(@P("applicationFinanceResourceId") final ApplicationFinanceResourceId applicationFinanceResourceId);

    // TODO qqRP
    //@PostAuthorize("hasPermission(returnObject, 'READ')")
    @NotSecured("TODO")
    ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(Long applicationFinanceId);

    // TODO qqRP
    //@PreAuthorize("hasPermission(#applicationFinanceId, 'com.worth.ifs.finance.resource.ApplicationFinanceResource', 'UPDATE_COST')")
    @NotSecured("TODO")
    ServiceResult<ApplicationFinanceResource> updateCost(@P("applicationFinanceId") Long applicationFinanceId, ApplicationFinanceResource applicationFinance);

    // TODO qqRP
    //@PostAuthorize("hasPermission(returnObject, 'READ')")
    @NotSecured("TODO")
    ServiceResult<ApplicationFinanceResource> financeDetails(Long applicationId, Long organisationId);

    // TODO qqRP
    //@PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ_FINANCE_TOTALS')")
    @NotSecured("TODO")
    ServiceResult<List<ApplicationFinanceResource>> financeTotals(@P("applicationId") Long applicationId);
}