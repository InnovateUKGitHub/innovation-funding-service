package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

public interface FinanceRowService {

    @PreAuthorize("hasPermission(#costFieldId, 'org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource', 'READ')")
    ServiceResult<FinanceRowMetaField> getCostFieldById(@P("costFieldId") Long costFieldId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FinanceRowMetaFieldResource>> findAllCostFields();

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<FinanceRowItem> getCostItem(Long costItemId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<? extends FinanceRow>> getCosts(Long applicationFinanceId, String costTypeName, Long questionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FinanceRowItem>> getCostItems(Long applicationFinanceId, String costTypeName, Long questionId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<FinanceRowItem>> getCostItems(Long applicationFinanceId, Long questionId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    ServiceResult<FinanceRowItem> addCost(@P("applicationFinanceId") Long applicationFinanceId, Long questionId, FinanceRowItem newCostItem);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    ServiceResult<FinanceRowItem> addCostWithoutPersisting(@P("applicationFinanceId") Long applicationFinanceId, Long questionId);

    @PreAuthorize("hasPermission(#costId, 'org.innovateuk.ifs.finance.domain.FinanceRow', 'UPDATE')")
    ServiceResult<FinanceRowItem> updateCost(@P("costId")Long costId, FinanceRowItem newCostItem);

    @PreAuthorize("hasPermission(#costId, 'org.innovateuk.ifs.finance.domain.FinanceRow', 'DELETE')")
    ServiceResult<Void> deleteCost(@P("costId") Long costId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> findApplicationFinanceByApplicationIdAndOrganisation(Long applicationId, Long organisationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ApplicationFinanceResource>> findApplicationFinanceByApplication(Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_RESEARCH_PARTICIPATION_PERCENTAGE')")
    ServiceResult<Double> getResearchParticipationPercentage(@P("applicationId") Long applicationId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<Double> getResearchParticipationPercentageFromProject(@P("projectId") Long projectId);

    @PreAuthorize("hasPermission(#applicationFinanceResourceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    ServiceResult<ApplicationFinanceResource> addCost(@P("applicationFinanceResourceId") final ApplicationFinanceResourceId applicationFinanceResourceId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> getApplicationFinanceById(Long applicationFinanceId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'UPDATE_COST')")
    ServiceResult<ApplicationFinanceResource> updateCost(@P("applicationFinanceId")Long applicationFinanceId, ApplicationFinanceResource applicationFinance);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationFinanceResource> financeDetails(Long applicationId, Long organisationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_FINANCE_DETAILS')")
    ServiceResult<List<ApplicationFinanceResource>> financeDetails(Long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_FINANCE_TOTALS')")
    ServiceResult<List<ApplicationFinanceResource>> financeTotals(@P("applicationId") Long applicationId);

    @NotSecured(value = "This is not getting date from the database, just getting a FinanceRowHandler", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getCostHandler(Long costItemId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'CREATE_FILE_ENTRY')")
    ServiceResult<FileEntryResource> createFinanceFileEntry(@P("applicationFinanceId")long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'UPDATE_FILE_ENTRY')")
    ServiceResult<FileEntryResource> updateFinanceFileEntry(@P("applicationFinanceId")long applicationFinanceId, FileEntryResource fileEntryResource, Supplier<InputStream> inputStreamSupplier);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'DELETE_FILE_ENTRY')")
    ServiceResult<Void> deleteFinanceFileEntry(@P("applicationFinanceId")long applicationFinanceId);

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'READ_FILE_ENTRY')")
    ServiceResult<FileAndContents> getFileContents(@P("applicationFinanceId")long applicationFinanceId);

    /**
     * Not included in REST API classes as only meant to be used within data layer
     */
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource','READ_ORGANISATION_FUNDING_STATUS')")
    ServiceResult<Boolean> organisationSeeksFunding(Long projectId, Long applicationId, Long organisationId);
}
