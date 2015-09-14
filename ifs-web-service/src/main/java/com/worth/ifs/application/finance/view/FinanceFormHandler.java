package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.CostItemMapper;
import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.application.finance.cost.CostItem;
import com.worth.ifs.application.finance.cost.LabourCost;
import com.worth.ifs.application.finance.cost.Materials;
import com.worth.ifs.application.finance.cost.SubContractingCost;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public class FinanceFormHandler {
    private final Log log = LogFactory.getLog(getClass());

    private Object costItemsForType;
    private CostService costService;

    public FinanceFormHandler(CostService costService) {
        this.costService = costService;
    }

    public void handle(HttpServletRequest request) {
        List<Cost> costs = getCostsForType(request);
        storeCosts(costs);
    }

    public void handle(String fieldName, String value) {
        List<CostField> costFields = costService.getCostFields();
        CostFormField costFormField = getCostFormField(fieldName, value);
        CostType costType = CostType.fromString(costFormField.getKeyType());
        CostItem costItem = getCostItem(costType, Long.valueOf(costFormField.getId()), Arrays.asList(costFormField));
        CostItemMapper costItemMapper = new CostItemMapper(costFields);
        Cost cost = costItemMapper.costItemToCost(costType, costItem);
        costService.update(cost);
    }

    private List<Cost> getCostsForType(HttpServletRequest request) {
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
        for (CostType costType : CostType.values()) {
            List<String> costTypeKeys = request.getParameterMap().keySet().stream().
                    filter(k -> k.startsWith(costType.getType())).collect(Collectors.toList());
            Map<Long, List<CostFormField>> costFieldMap = getCostDataRows(request, costTypeKeys);
            List<CostItem> costItems = getCostItems(costFieldMap, costType, costTypeKeys);
            List<Cost> costsForType = costItemMapper.costItemsToCost(costType,costItems);
            costs.addAll(costsForType);
        }

        return costs;
    }

    /**
     * Retrieve the cost items from the request based on their type
     */
    private List<CostItem> getCostItems(Map<Long, List<CostFormField>> costFieldMap, CostType costType, List<String> costTypeKeys) {
        List<CostItem> costItems = new ArrayList<>();

        // create new cost items
        for(Map.Entry<Long, List<CostFormField>> entry : costFieldMap.entrySet()) {
            CostItem costItem = getCostItem(costType, entry.getKey(), entry.getValue());
            if (costItem != null) {
                costItems.add(costItem);
            }
        }
        return costItems;
    }

    /**
     * Retrieve the complete cost item data row, so everything is together
     */
    private Map<Long, List<CostFormField>> getCostDataRows(HttpServletRequest request, List<String> costTypeKeys) {
        // make sure that we have the fields together acting on one cost
        Map<Long, List<CostFormField>> costKeyMap = new HashMap<>();
        for (String costTypeKey : costTypeKeys) {
            String value = request.getParameter(costTypeKey);
            CostFormField costFormField = getCostFormField(costTypeKey, value);
            if(costFormField==null)
                continue;

            Long id = Long.valueOf(costFormField.getId());
            if (costKeyMap.containsKey(id)) {
                costKeyMap.get(id).add(costFormField);
            } else {
                List<CostFormField> costKeyValues = new ArrayList<>();
                costKeyValues.add(costFormField);
                costKeyMap.put(id, costKeyValues);
            }
        }
        return costKeyMap;
    }

    private CostFormField getCostFormField(String costTypeKey, String value) {
        String[] keyParts = costTypeKey.split("-");

        if (keyParts.length > 2) {
            Long id = Long.valueOf(keyParts[2]);
            return new CostFormField(costTypeKey, value, keyParts[2], keyParts[1], keyParts[0]);
        }
        return null;
    }

    private CostItem getCostItem(CostType costType, Long id, List<CostFormField> costFields) {
        CostItem costItem = null;
        switch (costType) {
            case LABOUR:
                costItem = getLabourCost(id, costFields);
                break;
            case MATERIALS:
                costItem = getMaterials(id, costFields);
                break;
            case SUBCONTRACTING_COSTS:
                costItem = getSubContractingCosts(id, costFields);
        }
        return costItem;
    }

    private CostItem getLabourCost(Long id, List<CostFormField> costFormFields) {
        Double grossAnnualSalary = null;
        String role = null;
        Integer labourDays = null;

        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if (fieldValue != null) {
                if (costFormField.getCostName().equals("grossAnnualSalary")) {
                    grossAnnualSalary = Double.valueOf(fieldValue);
                } else if (costFormField.getCostName().equals("role")) {
                    role = fieldValue;
                } else if (costFormField.getCostName().equals("labourDays")) {
                    labourDays = Integer.valueOf(fieldValue);
                }
            }
        }
        return new LabourCost(id, role, grossAnnualSalary, labourDays, "");
    }

    private CostItem getMaterials(Long id, List<CostFormField> costFormFields) {
        String item = null;
        Double cost = null;
        Integer quantity = null;
        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if (fieldValue != null) {
                if (costFormField.getCostName().equals("item")) {
                    item = fieldValue;
                } else if (costFormField.getCostName().equals("cost")) {
                    cost = Double.valueOf(fieldValue);
                } else if (costFormField.getCostName().equals("quantity")) {
                    quantity = Integer.valueOf(fieldValue);
                }
            }
        }
        return new Materials(id, item, cost, quantity);
    }

    private CostItem getSubContractingCosts(Long id, List<CostFormField> costFormFields) {
        Double cost = null;
        String country = null;
        String name = null;
        String role = null;

        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if(fieldValue!=null) {
                if (costFormField.getCostName().equals("country")) {
                    country = fieldValue;
                } else if (costFormField.getCostName().equals("cost")) {
                    cost = Double.valueOf(fieldValue);
                } else if (costFormField.getCostName().equals("name")) {
                    name = fieldValue;
                } else if (costFormField.getCostName().equals("role")) {
                    role = fieldValue;
                }
            }
        }

        return new SubContractingCost(id, cost, country, name, role);
    }

    private void storeCosts(List<Cost> costs) {
        costs.stream().forEach(c -> costService.update(c));
    }

    private Cost updateCost(Cost cost) {
        Cost originalCost = costService.getById(cost.getId());
        if(cost.getCost()!=null) {
            originalCost.setCost(cost.getCost());
        }
        if(cost.getDescription()!=null) {
            originalCost.setDescription(cost.getDescription());
        }
        if(cost.getItem()!=null) {
            originalCost.setItem(cost.getItem());
        }
        if(cost.getQuantity()!=null) {
            originalCost.setQuantity(cost.getQuantity());
        }

        return originalCost;
    }

    class CostFormField {
        String fieldName;
        String costName;
        String keyType;
        String value;
        String id;

        public CostFormField(String fieldName, String value, String id, String costName, String keyType) {
            this.fieldName = fieldName;
            this.value = value;
            this.id = id;
            this.keyType = keyType;
            this.costName = costName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getKeyType() {
            return keyType;
        }

        public String getCostName() {
            return costName;
        }

        public String getValue() {
            return value;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return "CostFormField : " + this.fieldName + " " + this.costName +
                    " " + this.value + " " + this.id + " " + this.keyType;
        }
    }

}
