package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.cost.*;
import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.item.*;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.user.domain.OrganisationSize;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@code FinanceFormHandler} retrieves the costs and handles the finance data retrieved from the request, so it can be
 * transfered to view or stored. The costs retrieved from the {@link CostService} are converted
 * to {@link CostItem}.
 */
public class FinanceFormHandler {
    private final Log log = LogFactory.getLog(getClass());
    private final Long applicationId;
    private final Long userId;
    private CostService costService;
    private FinanceService financeService;
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    public FinanceFormHandler(CostService costService, FinanceService financeService, ApplicationFinanceRestService applicationFinanceRestService, Long userId, Long applicationId) {
        this.costService = costService;
        this.financeService = financeService;
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.userId = userId;
        this.applicationId = applicationId;
    }

    public boolean handle(HttpServletRequest request) {
        ApplicationFinance applicationFinance = financeService.getApplicationFinance(applicationId, userId);
        storeFinancePosition(request, applicationFinance.getId());

        List<Cost> costs = getCosts(request);
        return storeCosts(costs);
    }

    private void storeFinancePosition(HttpServletRequest request, @NotNull Long applicationFinanceId) {
        List<String> financePositionKeys = request.getParameterMap().keySet().stream().filter(k -> k.contains("financePosition-")).collect(Collectors.toList());
        if(!financePositionKeys.isEmpty()){
            ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getById(applicationFinanceId);

            financePositionKeys.parallelStream().forEach(k -> {
                String values = request.getParameterValues(k)[0];
                log.debug(String.format("finance position k : %s value: %s ", k, values));
                updateFinancePosition(applicationFinance, k, values);
            });
            applicationFinanceRestService.update(applicationFinance.getId(), applicationFinance);
        }
    }
    private void updateFinancePosition(ApplicationFinanceResource applicationFinance, String fieldName, String value){
        fieldName = fieldName.replace("financePosition-", "");
        switch (fieldName) {
            case "organisationSize":
                applicationFinance.setOrganisationSize(OrganisationSize.valueOf(value));
                break;
            default:
                log.error(String.format("value not saved: %s / %s", fieldName, value));
        }
    }

    public void ajaxUpdateFinancePosition(HttpServletRequest request, String fieldName, String value){

        ApplicationFinance applicationFinance = financeService.getApplicationFinance(applicationId, userId);
        ApplicationFinanceResource applicationFinanceResource = applicationFinanceRestService.getById(applicationFinance.getId());
        updateFinancePosition(applicationFinanceResource, fieldName, value);
        applicationFinanceRestService.update(applicationFinanceResource.getId(), applicationFinanceResource);
    }


    private List<Cost> getCosts(HttpServletRequest request) {
        List<CostField> costFields = costService.getCostFields();
        return mapCostItems(request, costFields);
    }

    /**
     * Retrieve a list of costs where first the request parameters are mapped to
     * the cost items and then converted to general costs.
     */
    private List<Cost> mapCostItems(HttpServletRequest request, List<CostField> costFields) {
        CostItemMapper costItemMapper = new CostItemMapper(costFields);
        List<Cost> costs = new ArrayList<>();
        for(CostType costType : CostType.values()) {
            List<String> costTypeKeys = request.getParameterMap().keySet().stream().
                    filter(k -> k.startsWith(costType.getType()+"-")).collect(Collectors.toList());
            Map<Long, List<CostFormField>> costFieldMap = getCostDataRows(request, costTypeKeys);
            List<CostItem> costItems = getCostItems(costFieldMap, costType, costTypeKeys);
            List<Cost> costsForType = costItemMapper.costItemsToCost(costType, costItems);
            costs.addAll(costsForType);
        }

        return costs;
    }

    /**
     * Retrieve the complete cost item data row, so everything is together
     */
    private Map<Long, List<CostFormField>> getCostDataRows(HttpServletRequest request, List<String> costTypeKeys) {
        // make sure that we have the fields together acting on one cost
        Map<Long, List<CostFormField>> costKeyMap = new HashMap<>();
        for(String costTypeKey : costTypeKeys) {
            String value = request.getParameter(costTypeKey);
            CostFormField costFormField = getCostFormField(costTypeKey, value);
            if (costFormField == null)
                continue;

            Long id = Long.valueOf(costFormField.getId());
            if(costKeyMap.containsKey(id)) {
                costKeyMap.get(id).add(costFormField);
            } else {
                List<CostFormField> costKeyValues = new ArrayList<>();
                costKeyValues.add(costFormField);
                costKeyMap.put(id, costKeyValues);
            }
        }
        return costKeyMap;
    }

    /**
     * Retrieve the cost items from the request based on their type
     */
    private List<CostItem> getCostItems(Map<Long, List<CostFormField>> costFieldMap, CostType costType, List<String> costTypeKeys) {
        List<CostItem> costItems = new ArrayList<>();
        CostHandler costHandler = getCostItemHandler(costType);

        // create new cost items
        for(Map.Entry<Long, List<CostFormField>> entry : costFieldMap.entrySet()) {
            CostItem costItem = costHandler.toCostItem(entry.getKey(), entry.getValue());
            if (costItem != null) {
                costItems.add(costItem);
            }
        }
        return costItems;
    }


    public void storeField(String fieldName, String value) {
        List<CostField> costFields = costService.getCostFields();
        CostFormField costFormField = getCostFormField(fieldName, value);
        CostType costType = CostType.fromString(costFormField.getKeyType());
        CostHandler costHandler = getCostItemHandler(costType);
        CostItem costItem = costHandler.toCostItem( Long.valueOf(costFormField.getId()), Arrays.asList(costFormField));
        CostItemMapper costItemMapper = new CostItemMapper(costFields);
        Cost cost = costItemMapper.costItemToCost(costType, costItem);
        costService.update(cost);
    }


    private CostFormField getCostFormField(String costTypeKey, String value) {
        String[] keyParts = costTypeKey.split("-");
        if(keyParts.length > 2) {
            return new CostFormField(costTypeKey, value, keyParts[2], keyParts[1], keyParts[0]);
        } else if (keyParts.length == 2) {
            log.info("id == null");
            return new CostFormField(costTypeKey, value, null, keyParts[1], keyParts[0]);
        }
        return null;
    }

    private CostHandler getCostItemHandler(CostType costType) {
        switch(costType) {
            case LABOUR:
                return new LabourCostHandler();
            case MATERIALS:
                return new MaterialsHandler();
            case SUBCONTRACTING_COSTS:
                return new SubContractingCostHandler();
            case FINANCE:
                return new GrantClaimHandler();
            case OVERHEADS:
                return new OverheadsHandler();
            case CAPITAL_USAGE:
                return new CapitalUsageHandler();
            case TRAVEL:
                return new TravelCostHandler();
            case OTHER_COSTS:
                return new OtherCostHandler();
            case OTHER_FUNDING:
                return new OtherFundingHandler();
            default:
                log.error("getCostItem, unsupported type: " + costType);
                return null;
        }
    }

    private boolean storeCosts(List<Cost> costs) {
        costs.stream().forEach(c -> costService.update(c));
        return true;
    }
}
