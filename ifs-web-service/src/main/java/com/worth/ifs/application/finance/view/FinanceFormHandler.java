package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.model.CostFormField;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.item.*;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
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
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(applicationId, userId);
        storeFinancePosition(request, applicationFinanceResource.getId());


        List<CostItem> costItems = getCostItems(request);
        boolean storingResult = storeCostItems(costItems);

        addRemoveCostRows(request, applicationId, userId, applicationFinanceResource.getId());

        return storingResult;
    }

    private void addRemoveCostRows(HttpServletRequest request, Long applicationId, Long userId, Long applicationFinanceId) {
        log.error(String.format("Got the Add / Remove Cost Param ????? " ));
        Map<String, String[]> requestParams = request.getParameterMap();
        if(requestParams.containsKey("add_cost")){
            String addCostParam = request.getParameter("add_cost");
            log.error(String.format("Got the Add Cost Param with id: %s", addCostParam ));
            ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(applicationId, userId);
            financeService.addCost(applicationFinance.getId(), Long.valueOf(addCostParam));
        }
        if(requestParams.containsKey("remove_cost")){
            String removeCostParam = request.getParameter("remove_cost");
            log.error(String.format("Got the REMOVE cost Param with id: %s", removeCostParam ));
            costService.delete(Long.valueOf(removeCostParam));
        }
    }

    private void storeFinancePosition(HttpServletRequest request, @NotNull Long applicationFinanceId) {
        List<String> financePositionKeys = request.getParameterMap().keySet().stream().filter(k -> k.contains("financePosition-")).collect(Collectors.toList());
        if(!financePositionKeys.isEmpty()){
            ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getById(applicationFinanceId);

            financePositionKeys.stream().forEach(k -> {
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

    public void ajaxUpdateFinancePosition(String fieldName, String value){
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(applicationId, userId);
        updateFinancePosition(applicationFinanceResource, fieldName, value);
        applicationFinanceRestService.update(applicationFinanceResource.getId(), applicationFinanceResource);
    }

    private List<CostItem> getCostItems(HttpServletRequest request) {
        List<CostFieldResource> costFields = costService.getCostFields();
        return mapRequestParametersToCostItems(request, costFields);
    }

    private List<CostItem> mapRequestParametersToCostItems(HttpServletRequest request, List<CostFieldResource> costFields) {
        List<CostItem> costItems = new ArrayList<>();
        for(CostType costType : CostType.values()) {
            List<String> costTypeKeys = request.getParameterMap().keySet().stream().
                    filter(k -> k.startsWith(costType.getType()+"-")).collect(Collectors.toList());
            Map<Long, List<CostFormField>> costFieldMap = getCostDataRows(request, costTypeKeys);
            List<CostItem> costItemsForType = getCostItems(costFieldMap, costType, costTypeKeys);
            costItems.addAll(costItemsForType);
        }

        return costItems;
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

            if (costFormField.getId()!=null && !costFormField.getId().equals("null")) {
                Long id = Long.valueOf(costFormField.getId());
                if (costKeyMap.containsKey(id)) {
                    costKeyMap.get(id).add(costFormField);
                } else {
                    List<CostFormField> costKeyValues = new ArrayList<>();
                    costKeyValues.add(costFormField);
                    costKeyMap.put(id, costKeyValues);
                }
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
        CostFormField costFormField = getCostFormField(fieldName, value);
        CostType costType = CostType.fromString(costFormField.getKeyType());
        CostHandler costHandler = getCostItemHandler(costType);
        Long costFormFieldId = 0L;
        if(costFormField.getId()!=null && !costFormField.getId().equals("null")) {
            costFormFieldId = Long.parseLong(costFormField.getId());
        }
        CostItem costItem = costHandler.toCostItem(costFormFieldId, Arrays.asList(costFormField));
        storeCostItem(costItem, costFormField.getQuestionId());
    }

    private CostFormField getCostFormField(String costTypeKey, String value) {
        String[] keyParts = costTypeKey.split("-");
        if(keyParts.length > 3) {
            return new CostFormField(costTypeKey, value, keyParts[3], keyParts[2], keyParts[1], keyParts[0]);
        } else if (keyParts.length == 3) {
            return new CostFormField(costTypeKey, value, null, keyParts[2], keyParts[1], keyParts[0]);
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
            case YOUR_FINANCE:
                return new YourFinanceHandler();
            default:
                log.error("getCostItem, unsupported type: " + costType);
                return null;
        }
    }

    private void storeCostItem(CostItem costItem, String question) {
        if(costItem.getId().equals(0L)) {
            addCostItem(costItem, question);
        } else {
            costService.update(costItem);
        }
    }

    private void addCostItem(CostItem costItem, String question) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(applicationId, userId);
        if(question!=null && !question.isEmpty()) {
            Long questionId = Long.parseLong(question);
            costService.add(applicationFinanceResource.getId(), questionId, costItem);
        }
    }

    private boolean storeCostItems(List<CostItem> costItems) {
        costItems.stream().forEach(c -> costService.update(c));
        return true;
    }
}
