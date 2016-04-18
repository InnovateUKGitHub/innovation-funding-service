package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.security.NotSecured;

import java.util.List;

public interface CostService {

    @NotSecured("TODO")
    ServiceResult<CostField> getCostFieldById(Long id);

    @NotSecured("TODO")
    ServiceResult<List<CostFieldResource>> findAllCostFields();

    @NotSecured("TODO")
    ServiceResult<CostItem> getCostItem(Long costItemId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<CostItem> addCost(Long applicationFinanceId, Long questionId, CostItem newCostItem);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> updateCost(Long id, CostItem newCostItem);

    @NotSecured("TODO")
    ServiceResult<List<Cost>> getCosts(Long applicationFinanceId, String costTypeName, Long questionId);

    @NotSecured("TODO")
    ServiceResult<List<CostItem>> getCostItems(Long applicationFinanceId, String costTypeName, Long questionId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> deleteCost(Long costId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(Long applicationId, Long organisationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(Long applicationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Double> getResearchParticipationPercentage(Long applicationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<ApplicationFinanceResource> addCost(Long applicationId, Long organisationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(Long applicationFinanceId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<ApplicationFinanceResource> updateCost(Long applicationFinanceId, ApplicationFinanceResource applicationFinance);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<ApplicationFinanceResource> financeDetails(Long applicationId, Long organisationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<List<ApplicationFinanceResource>> financeTotals(Long applicationId);
}