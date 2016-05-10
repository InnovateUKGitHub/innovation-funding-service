package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.item.*;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.exception.BigDecimalNumberFormatException;
import com.worth.ifs.exception.IntegerNumberFormatException;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.user.domain.OrganisationSize;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@code DefaultFinanceFormHandler} retrieves the costs and handles the finance data retrieved from the request, so it can be
 * transfered to view or stored. The costs retrieved from the {@link CostService} are converted
 * to {@link CostItem}.
 */
@Component
public class DefaultFinanceFormHandler implements FinanceFormHandler {
    private static final Log LOG = LogFactory.getLog(DefaultFinanceFormHandler.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CostService costService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    public Map<String, List<String>> update(HttpServletRequest request, Long userId, Long applicationId) {
        Map<String, List<String>> errors = new HashMap<>();


        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        if (applicationFinanceResource == null) {
            applicationFinanceResource = financeService.addApplicationFinance(userId, applicationId);
        }

        storeFinancePosition(request, applicationFinanceResource.getId());
        getAndStoreCostitems(request, errors);
        addRemoveCostRows(request, applicationId, userId);

        return errors;
    }

    private void getAndStoreCostitems(HttpServletRequest request, Map<String, List<String>> errors) {
        List<Either<CostItem, ValidationMessages>> costItems = getCostItems(request);
        List<ValidationMessages> invalidItems = costItems.stream().filter(e -> e.isRight()).map(e -> e.getRight()).collect(Collectors.toList());
        invalidItems.forEach(validationMessages ->
                validationMessages.getErrors().stream().forEach(e -> {
                    if(StringUtils.hasText(e.getErrorKey())){
                        errors.put("formInput[cost-" + validationMessages.getObjectId() + "-" + e.getErrorKey() + "]", Arrays.asList(e.getErrorMessage()));
                    }else{
                        errors.put("formInput[cost-" + validationMessages.getObjectId() + "]", Arrays.asList(e.getErrorMessage()));
                    }
                })
        );

        List<CostItem> validItems = costItems.stream().filter(e -> e.isLeft()).map(e -> e.getLeft()).collect(Collectors.toList());
        storeCostItems(validItems).forEach((costId, validationMessages) -> {
            validationMessages.getErrors().stream().forEach(e -> {
                if(StringUtils.hasText(e.getErrorKey())){
                    errors.put("formInput[cost-" + costId + "-" + e.getErrorKey() + "]", Arrays.asList(e.getErrorMessage()));
                }else{
                    errors.put("formInput[cost-" + costId + "]", Arrays.asList(e.getErrorMessage()));
                }
            });
        });
    }


    @Override
    public ValidationMessages storeCost(Long userId, Long applicationId, String fieldName, String value) {
        if (fieldName != null && value != null) {
            String cleanedFieldName = fieldName;
            if (fieldName.startsWith("cost-")) {
                cleanedFieldName = fieldName.replace("cost-", "");
            } else if (fieldName.startsWith("formInput[")) {
                cleanedFieldName = fieldName.replace("formInput[", "").replace("]", "");
            }
            LOG.info("store field: " + cleanedFieldName + " val: " + value);
            return storeField(cleanedFieldName, value, userId, applicationId);
        }
        return null;
    }

    @Override
    public CostItem addCost(Long applicationId, Long userId, Long questionId) {
        ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(userId, applicationId);
        return costService.add(applicationFinance.getId(), questionId, null);
    }

    private void addRemoveCostRows(HttpServletRequest request, Long applicationId, Long userId) {
        Map<String, String[]> requestParams = request.getParameterMap();
        if (requestParams.containsKey("add_cost")) {
            String addCostParam = request.getParameter("add_cost");
            ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(userId, applicationId);
            financeService.addCost(applicationFinance.getId(), Long.valueOf(addCostParam));
        }
        if (requestParams.containsKey("remove_cost")) {
            String removeCostParam = request.getParameter("remove_cost");
            costService.delete(Long.valueOf(removeCostParam));
        }
    }

    // TODO DW - INFUND-1555 - handle rest results
    private void storeFinancePosition(HttpServletRequest request, @NotNull Long applicationFinanceId) {
        List<String> financePositionKeys = request.getParameterMap().keySet().stream().filter(k -> k.contains("financePosition-")).collect(Collectors.toList());
        if (!financePositionKeys.isEmpty()) {
            ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getById(applicationFinanceId).getSuccessObjectOrThrowException();

            financePositionKeys.stream().forEach(k -> {
                String values = request.getParameterValues(k)[0];
                LOG.debug(String.format("finance position k : %s value: %s ", k, values));
                updateFinancePosition(applicationFinance, k, values);
            });
            applicationFinanceRestService.update(applicationFinance.getId(), applicationFinance);
        }
    }

    @Override
    public void updateFinancePosition(Long userId, Long applicationId, String fieldName, String value) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        updateFinancePosition(applicationFinanceResource, fieldName, value);
        applicationFinanceRestService.update(applicationFinanceResource.getId(), applicationFinanceResource);
    }


    private void updateFinancePosition(ApplicationFinanceResource applicationFinance, String fieldName, String value) {
        String fieldNameReplaced = fieldName.replace("financePosition-", "");
        switch (fieldNameReplaced) {
            case "organisationSize":
                applicationFinance.setOrganisationSize(OrganisationSize.valueOf(value));
                break;
            default:
                LOG.error(String.format("value not saved: %s / %s", fieldNameReplaced, value));
        }
    }

    private List<Either<CostItem, ValidationMessages>> getCostItems(HttpServletRequest request) {
        return mapRequestParametersToCostItems(request);
    }

    private List<Either<CostItem, ValidationMessages>> mapRequestParametersToCostItems(HttpServletRequest request) {
        List<Either<CostItem, ValidationMessages>> costItems = new ArrayList<>();
        for (CostType costType : CostType.values()) {
            List<String> costTypeKeys = request.getParameterMap().keySet().stream().
                    filter(k -> k.startsWith(costType.getType() + "-")).collect(Collectors.toList());
            Map<Long, List<FinanceFormField>> costFieldMap = getCostDataRows(request, costTypeKeys);
            List<Either<CostItem, ValidationMessages>> costItemsForType = getCostItems(costFieldMap, costType);
            costItems.addAll(costItemsForType);
        }

        return costItems;
    }

    /**
     * Retrieve the complete cost item data row, so everything is together
     */
    private Map<Long, List<FinanceFormField>> getCostDataRows(HttpServletRequest request, List<String> costTypeKeys) {
        // make sure that we have the fields together acting on one cost
        Map<Long, List<FinanceFormField>> costKeyMap = new HashMap<>();
        for (String costTypeKey : costTypeKeys) {
            String value = request.getParameter(costTypeKey);
            FinanceFormField financeFormField = getCostFormField(costTypeKey, value);
            if (financeFormField == null)
                continue;

            if (financeFormField.getId() != null && !"null".equals(financeFormField.getId())) {
                Long id = Long.valueOf(financeFormField.getId());
                if (costKeyMap.containsKey(id)) {
                    costKeyMap.get(id).add(financeFormField);
                } else {
                    List<FinanceFormField> costKeyValues = new ArrayList<>();
                    costKeyValues.add(financeFormField);
                    costKeyMap.put(id, costKeyValues);
                }
            }
        }
        return costKeyMap;
    }

    /**
     * Retrieve the cost items from the request based on their type
     */
    private List<Either<CostItem, ValidationMessages>> getCostItems(Map<Long, List<FinanceFormField>> costFieldMap, CostType costType) {
        List<Either<CostItem, ValidationMessages>> costItems = new ArrayList<>();
        CostHandler costHandler = getCostItemHandler(costType);

        // create new cost items
        for (Map.Entry<Long, List<FinanceFormField>> entry : costFieldMap.entrySet()) {
            try{
                CostItem costItem = costHandler.toCostItem(entry.getKey(), entry.getValue());
                if (costItem != null) {
                    Either<CostItem, ValidationMessages> either = Either.left(costItem);
                    costItems.add(either);
                }
            }catch(NumberFormatException e){
                ValidationMessages validationMessages = getValidationMessageFromException(entry, e);
                Either<CostItem, ValidationMessages> either = Either.right(validationMessages);
                costItems.add(either);
            }
        }
        return costItems;
    }

    private ValidationMessages getValidationMessageFromException(Map.Entry<Long, List<FinanceFormField>> entry, NumberFormatException e) {
        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.setObjectId(entry.getKey());
        validationMessages.setObjectName("cost");
        List<Error> errors = new ArrayList<>();
        ArrayList<Object> args = new ArrayList<Object>();
        args.add(e.getMessage());
        if(IntegerNumberFormatException.class.equals(e.getClass()) || BigDecimalNumberFormatException.class.equals(e.getClass())){
            errors.add(new Error("", messageSource.getMessage(e.getMessage(), args.toArray(), Locale.UK), args, HttpStatus.INTERNAL_SERVER_ERROR));
        }else{
            errors.add(new Error("", messageSource.getMessage("field.value.not.valid", args.toArray(), Locale.UK), args, HttpStatus.INTERNAL_SERVER_ERROR));
        }
        validationMessages.setErrors(errors);
        return validationMessages;
    }

    private ValidationMessages storeField(String fieldName, String value, Long userId, Long applicationId) {
        FinanceFormField financeFormField = getCostFormField(fieldName, value);
        CostType costType = CostType.fromString(financeFormField.getKeyType());
        CostHandler costHandler = getCostItemHandler(costType);
        Long costFormFieldId = 0L;
        if (financeFormField.getId() != null && !"null".equals(financeFormField.getId())) {
            costFormFieldId = Long.parseLong(financeFormField.getId());
        }
        CostItem costItem = costHandler.toCostItem(costFormFieldId, Arrays.asList(financeFormField));
        return storeCostItem(costItem, userId, applicationId, financeFormField.getQuestionId());
    }

    private FinanceFormField getCostFormField(String costTypeKey, String value) {
        String[] keyParts = costTypeKey.split("-");
        if (keyParts.length > 3) {
            return new FinanceFormField(costTypeKey, value, keyParts[3], keyParts[2], keyParts[1], keyParts[0]);
        } else if (keyParts.length == 3) {
            return new FinanceFormField(costTypeKey, value, null, keyParts[2], keyParts[1], keyParts[0]);
        }
        return null;
    }

    private CostHandler getCostItemHandler(CostType costType) {
        switch (costType) {
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
                LOG.error("getCostItem, unsupported type: " + costType);
                return null;
        }
    }

    private ValidationMessages storeCostItem(CostItem costItem, Long userId, Long applicationId, String question) {
        if (costItem.getId().equals(0L)) {
            addCostItem(costItem, userId, applicationId, question);
        } else {
            RestResult<ValidationMessages> messages = costService.update(costItem);
            ValidationMessages validationMessages = messages.getSuccessObject();

            if (validationMessages == null || validationMessages.getErrors() == null || validationMessages.getErrors().isEmpty()) {
                LOG.debug("no validation errors on cost items");
                return messages.getSuccessObject();
            } else {
                messages.getSuccessObject().getErrors().stream()
                        .peek(e -> LOG.debug(String.format("Got cost item Field error: %s  / %s", e.getErrorKey(), e.getErrorMessage())));
                return messages.getSuccessObject();
            }
        }
        return null;
    }

    private void addCostItem(CostItem costItem, Long userId, Long applicationId, String question) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);

        if (question != null && !question.isEmpty()) {
            Long questionId = Long.parseLong(question);
            costService.add(applicationFinanceResource.getId(), questionId, costItem);
        }
    }

    private Map<Long, ValidationMessages> storeCostItems(List<CostItem> costItems) {
        Map<Long, ValidationMessages> validationMessagesMap = new HashMap<>();
        costItems.stream().forEach(c -> {
            RestResult<ValidationMessages> messages = costService.update(c);
            Optional<ValidationMessages> successObject = messages.getOptionalSuccessObject();
            if (successObject.isPresent() && successObject.get() != null &&
                    messages.getSuccessObject().getErrors() != null &&
                    !messages.getSuccessObject().getErrors().isEmpty()
            ) {
                LOG.debug("got validation errors. " + c.getId());
                validationMessagesMap.put(c.getId(), messages.getSuccessObject());
            } else {
                LOG.debug("No validation errors.");
            }
        });
        return validationMessagesMap;
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long applicationFinanceId) {
        throw new NotImplementedException("Finance upload is not available for the default finances");

    }
}
