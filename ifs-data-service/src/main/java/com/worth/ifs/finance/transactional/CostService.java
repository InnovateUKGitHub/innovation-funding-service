package com.worth.ifs.finance.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

public interface CostService {

    @PreAuthorize("hasPermission(#costFieldId, 'com.worth.ifs.finance.resource.CostFieldResource', 'READ')")
    ServiceResult<CostField> getCostFieldById(@P("costFieldId") Long costFieldId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CostFieldResource>> findAllCostFields();

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<CostItem> getCostItem(Long costItemId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<Cost>> getCosts(Long applicationFinanceId, String costTypeName, Long questionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CostItem>> getCostItems(Long applicationFinanceId, String costTypeName, Long questionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<CostItem>> getCostItems(Long applicationFinanceId, Long questionId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'com.worth.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    ServiceResult<CostItem> addCost(@P("applicationFinanceId") Long applicationFinanceId, Long questionId, CostItem newCostItem);

    @PreAuthorize("hasPermission(#costId, 'com.worth.ifs.finance.domain.Cost', 'UPDATE')")
    ServiceResult<CostItem> updateCost(@P("costId")Long costId, CostItem newCostItem);

    @PreAuthorize("hasPermission(#costId, 'com.worth.ifs.finance.domain.Cost', 'DELETE')")
    ServiceResult<Void> deleteCost(@P("costId") Long costId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(Long applicationId, Long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ_RESEARCH_PARTICIPATION_PERCENTAGE')")
    ServiceResult<Double> getResearchParticipationPercentage(@P("applicationId") Long applicationId);

    @PreAuthorize("hasPermission(#applicationFinanceResourceId, 'com.worth.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    ServiceResult<ApplicationFinanceResource> addCost(@P("applicationFinanceResourceId") final ApplicationFinanceResourceId applicationFinanceResourceId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(Long applicationFinanceId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'com.worth.ifs.finance.resource.ApplicationFinanceResource', 'UPDATE_COST')")
    ServiceResult<ApplicationFinanceResource> updateCost(@P("applicationFinanceId")Long applicationFinanceId, ApplicationFinanceResource applicationFinance);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> financeDetails(Long applicationId, Long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'com.worth.ifs.application.resource.ApplicationResource', 'READ_FINANCE_TOTALS')")
    ServiceResult<List<ApplicationFinanceResource>> financeTotals(@P("applicationId") Long applicationId);

    @NotSecured(value = "RP will secure this", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> createFinanceFileEntry(long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @NotSecured(value = "RP will secure this", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> updateFinanceFileEntry(long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @NotSecured(value = "RP will secure this", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> deleteFinanceFileEntry(long applicationFinanceId);
}