package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
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

import java.util.List;


public interface FinanceRowCostsService {

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

    @PreAuthorize("hasPermission(#applicationFinanceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'UPDATE_COST')")
    ServiceResult<ApplicationFinanceResource> updateApplicationFinance(@P("applicationFinanceId")Long applicationFinanceId, ApplicationFinanceResource applicationFinance);

    @PreAuthorize("hasPermission(#applicationFinanceResourceId, 'org.innovateuk.ifs.finance.resource.ApplicationFinanceResource', 'ADD_COST')")
    ServiceResult<ApplicationFinanceResource> addCost(@P("applicationFinanceResourceId") final ApplicationFinanceResourceId applicationFinanceResourceId);

    @NotSecured(value = "This is not getting data from the database, just getting a FinanceRowHandler", mustBeSecuredByOtherServices = false)
    FinanceRowHandler getCostHandler(Long costItemId);

}
