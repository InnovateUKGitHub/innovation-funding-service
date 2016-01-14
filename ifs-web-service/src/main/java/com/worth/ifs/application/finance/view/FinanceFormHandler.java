package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.CostItemMapper;
import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.application.finance.cost.*;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
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
import java.math.BigDecimal;
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

        // create new cost items
        for(Map.Entry<Long, List<CostFormField>> entry : costFieldMap.entrySet()) {
            CostItem costItem = getCostItem(costType, entry.getKey(), entry.getValue());
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
        CostItem costItem = getCostItem(costType, Long.valueOf(costFormField.getId()), Arrays.asList(costFormField));
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

    private CostItem getCostItem(CostType costType, Long id, List<CostFormField> costFields) {
        CostItem costItem = null;
        switch(costType) {
            case LABOUR:
                costItem = getLabourCost(id, costFields);
                break;
            case MATERIALS:
                costItem = getMaterials(id, costFields);
                break;
            case SUBCONTRACTING_COSTS:
                costItem = getSubContractingCosts(id, costFields);
                break;
            case FINANCE:
                costItem = getClaimGrantPercentage(id, costFields);
                break;
            case OVERHEADS:
                costItem = getOverheadsCosts(id, costFields);
                break;
            case CAPITAL_USAGE:
                costItem = getCapitalUsage(id, costFields);
                break;
            case TRAVEL:
                costItem = getTravelCost(id, costFields);
                break;
            case OTHER_COSTS:
                costItem = getOtherCost(id, costFields);
                break;
            case OTHER_FUNDING:
                costItem = getOtherFunding(id, costFields);
                break;
            default:
                log.error("getCostItem, unsupported type: " + costType);
                break;
        }
        return costItem;
    }

    private CostItem getCapitalUsage(Long id, List<CostFormField> costFields) {
        costFields.stream().forEach(c -> log.debug("CostField: " + c.getCostName()));
        Integer deprecation = null;
        String description = null;
        String existing = null;
        BigDecimal npv = null;
        BigDecimal residualValue = null;
        Integer utilisation = null;

        for(CostFormField costFormField : costFields) {
            final String costFormValue = costFormField.getValue();
            switch (costFormField.getCostName()) {
                case "item":
                    description = costFormValue;
                    break;
                case "existing":
                    existing = costFormValue;
                    break;
                case "deprecation_period":
                    deprecation = Integer.valueOf(costFormValue);
                    break;
                case "npv":
                    npv = getBigDecimalValue(costFormValue, 0d);
                    break;
                case "residual_value":
                    residualValue = getBigDecimalValue(costFormValue, 0d);
                    break;
                case "utilisation":
                    utilisation = Integer.valueOf(costFormValue);
                    break;
                default:
                    log.info("Unused costField: " + costFormField.getCostName());
                    break;
            }
        }

        return new CapitalUsage(id, deprecation, description, existing,
                npv, residualValue, utilisation);
    }

    private CostItem getOverheadsCosts(Long id, List<CostFormField> costFields) {
        costFields.stream().forEach(c -> log.debug("CostField: " + c.getCostName()));
        Integer customRate = null;
        String acceptRate = null;

        for(CostFormField costFormField : costFields) {
            switch (costFormField.getCostName()) {
                case "acceptRate":
                    acceptRate = costFormField.getValue();
                    break;
                case "customRate":
                    customRate = Integer.valueOf(costFormField.getValue());
                    break;
                default:
                    log.info("Unused costField: " + costFormField.getCostName());
                    break;
            }
        }
        return new Overhead(id, acceptRate, customRate);
    }

    private CostItem getLabourCost(Long id, List<CostFormField> costFormFields) {
        BigDecimal grossAnnualSalary = null;
        String role = null;
        Integer labourDays = null;
        String description = null;

        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if(fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "grossAnnualSalary":
                        grossAnnualSalary = getBigDecimalValue(fieldValue, 0D);
                        break;
                    case "role":
                        role = fieldValue;
                        break;
                    case "labourDays":
                    case "workingDays":
                        labourDays = getIntegerValue(fieldValue, 0);
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }
        return new LabourCost(id, role, grossAnnualSalary, labourDays, description);
    }

    private CostItem getMaterials(Long id, List<CostFormField> costFormFields) {
        String item = null;
        BigDecimal cost = null;
        Integer quantity = null;
        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if(fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "item":
                        item = fieldValue;
                        break;
                    case "cost":
                        cost = getBigDecimalValue(fieldValue, 0D);
                        break;
                    case "quantity":
                        quantity = getIntegerValue(fieldValue, 0);
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }
        return new Materials(id, item, cost, quantity);
    }

    private CostItem getSubContractingCosts(Long id, List<CostFormField> costFormFields) {
        BigDecimal cost = null;
        String country = null;
        String name = null;
        String role = null;

        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if (fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "country":
                        country = fieldValue;
                        break;
                    case "subcontractingCost":
                        cost = getBigDecimalValue(fieldValue, 0D);
                        break;
                    case "name":
                        name = fieldValue;
                        break;
                    case "role":
                        role = fieldValue;
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }

        return new SubContractingCost(id, cost, country, name, role);
    }

    private CostItem getTravelCost(Long id, List<CostFormField> costFormFields) {
        BigDecimal costPerItem = null;
        String item = null;
        Integer quantity = null;

        for(CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if(fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "travelPurpose":
                        item = fieldValue;
                        break;
                    case "travelNumTimes":
                        quantity = getIntegerValue(fieldValue, 0);
                        break;
                    case "travelCostEach":
                        costPerItem = getBigDecimalValue(fieldValue, 0d);
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }
        return new TravelCost(id, costPerItem, item, quantity);
    }

    private CostItem getOtherCost(Long id, List<CostFormField> costFormFields) {
        String description = null;
        BigDecimal cost = null;

        for (CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if (fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "description":
                        description = fieldValue;
                        break;
                    case "otherCost":
                        cost = getBigDecimalValue(fieldValue, 0d);
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }
        return new OtherCost(id, cost, description);
    }

    private CostItem getOtherFunding(Long id, List<CostFormField> costFormFields) {
        String otherPublicFunding = null;
        String fundingSource = null;
        String dateSecured = null;
        BigDecimal fundingAmount = null;

        for (CostFormField costFormField : costFormFields) {
            String fieldValue = costFormField.getValue();
            if (fieldValue != null) {
                switch (costFormField.getCostName()) {
                    case "otherPublicFunding":
                        otherPublicFunding = fieldValue;
                        break;
                    case "fundingAmount":
                        fundingAmount = getBigDecimalValue(fieldValue, 0d);
                        break;
                    case "fundingSource":
                        fundingSource = fieldValue;
                        break;
                    case "dateSecured":
                        dateSecured = fieldValue;
                        break;
                    default:
                        log.info("Unused costField: " + costFormField.getCostName());
                        break;
                }
            }
        }

        return new OtherFunding(id, otherPublicFunding, fundingSource, dateSecured, fundingAmount);
    }

    private CostItem getClaimGrantPercentage(Long id, List<CostFormField> costFormFields) {
        Optional<CostFormField> grantClaimPercentageField = costFormFields.stream().findFirst();
        Integer grantClaimPercentage = 0;
        if (grantClaimPercentageField.isPresent()) {
            grantClaimPercentage = getIntegerValue(grantClaimPercentageField.get().getValue(), 0);
        }
        return new GrantClaim(id, grantClaimPercentage);
    }

    private BigDecimal getBigDecimalValue(String value, Double defaultValue) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException nfe) {
            return new BigDecimal(defaultValue);
        }
    }

    private Integer getIntegerValue(String value, Integer defaultValue) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    private boolean storeCosts(List<Cost> costs) {
        costs.stream().forEach(c -> costService.update(c));
        return true;
    }

    private Cost updateCost(Cost cost) {
        Cost originalCost = costService.getById(cost.getId());
        if (cost.getCost() != null) {
            originalCost.setCost(cost.getCost());
        }
        if (cost.getDescription() != null) {
            originalCost.setDescription(cost.getDescription());
        }
        if (cost.getItem() != null) {
            originalCost.setItem(cost.getItem());
        }
        if (cost.getQuantity() != null) {
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
