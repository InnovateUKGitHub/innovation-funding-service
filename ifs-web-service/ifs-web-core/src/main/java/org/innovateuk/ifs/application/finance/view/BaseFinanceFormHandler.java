package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.application.finance.view.item.*;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.exception.BigDecimalNumberFormatException;
import org.innovateuk.ifs.exception.IntegerNumberFormatException;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.util.Either;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Base methods for all FinanceFormHandlers. For example methods that handle exceptions or errors that are possibly occurring in all FinanceFormHandlers.
 */
public abstract class BaseFinanceFormHandler {

    private static final String UNSPECIFIED_AMOUNT_STR = "£ 0";

    private static final Log LOG = LogFactory.getLog(BaseFinanceFormHandler.class);

    protected ValidationMessages getValidationMessageFromException(Map.Entry<Long, List<FinanceFormField>> entry, NumberFormatException e) {
        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.setObjectId(entry.getKey());
        validationMessages.setObjectName("cost");
        List<Object> args = singletonList(e.getMessage());
        if(IntegerNumberFormatException.class.equals(e.getClass()) || BigDecimalNumberFormatException.class.equals(e.getClass())){
            validationMessages.addError(globalError(e.getMessage(), args));
        } else{
            validationMessages.addError(new Error(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR));
        }
        return validationMessages;
    }

    protected ValidationMessages getAndStoreCostitems(HttpServletRequest request, Long financeId, Function<FinanceRowItem, RestResult<ValidationMessages>> updatingFunction) {

        ValidationMessages errors = new ValidationMessages();

        List<Either<FinanceRowItem, ValidationMessages>> costItems = getFinanceRowItems(request.getParameterMap(), financeId);
        List<ValidationMessages> invalidItems = costItems.stream().filter(e -> e.isRight()).map(e -> e.getRight()).collect(Collectors.toList());
        List<Error> getFinanceRowItemErrors = flattenLists(simpleMap(invalidItems, validationMessages ->
                simpleMap(validationMessages.getErrors(), e -> {
                    if(StringUtils.hasText(e.getErrorKey())){
                        return fieldError("formInput[cost-" + validationMessages.getObjectId() + "-" + e.getFieldName() + "]", e.getFieldRejectedValue(), e.getErrorKey(), e.getArguments());
                    }else{
                        return fieldError("formInput[cost-" + validationMessages.getObjectId() + "]", e.getFieldRejectedValue(), e.getErrorKey(), e.getArguments());
                    }
                })
        ));

        errors.addErrors(getFinanceRowItemErrors);

        List<FinanceRowItem> validItems = costItems.stream().filter(e -> e.isLeft()).map(e -> e.getLeft()).collect(Collectors.toList());
        Map<Long, ValidationMessages> storedItemErrors = storeFinanceRowItems(validItems, updatingFunction);
        storedItemErrors.forEach((costId, validationMessages) ->
                validationMessages.getErrors().stream().forEach(e -> {
                    if(StringUtils.hasText(e.getErrorKey())){
                        errors.addError(fieldError("formInput[cost-" + costId + "-" + e.getFieldName() + "]", e.getFieldRejectedValue(), e.getErrorKey(), e.getArguments()));
                    }else{
                        errors.addError(fieldError("formInput[cost-" + costId + "]", e.getFieldRejectedValue(), e.getErrorKey(), e.getArguments()));
                    }
                })
        );

        return errors;
    }

    private List<Either<FinanceRowItem, ValidationMessages>> getFinanceRowItems(Map<String, String[]> params, Long financeId) {
        List<Either<FinanceRowItem, ValidationMessages>> costItems = new ArrayList<>();
        for (FinanceRowType costType : FinanceRowType.values()) {
            List<String> costTypeKeys = params.keySet().stream().
                    filter(k -> k.startsWith(costType.getType() + "-")).collect(Collectors.toList());
            Map<Long, List<FinanceFormField>> costFieldMap = getCostDataRows(params, costTypeKeys);
            List<Either<FinanceRowItem, ValidationMessages>> costItemsForType = getFinanceRowItems(costFieldMap, costType, financeId);
            costItems.addAll(costItemsForType);
        }

        return costItems;
    }

    /**
     * Retrieve the complete cost item data row, so everything is together
     */
    Map<Long, List<FinanceFormField>> getCostDataRows(Map<String, String[]> params, List<String> costTypeKeys) {
        // make sure that we have the fields together acting on one cost
        Map<Long, List<FinanceFormField>> costKeyMap = new HashMap<>();
        for (String costTypeKey : costTypeKeys) {
            String[] valueArray = params.get(costTypeKey);
            String value;
            if(valueArray.length > 0) {
                value = valueArray[0];
            } else {
                continue;
            }
            FinanceFormField financeFormField = getCostFormField(costTypeKey, value);
            if (financeFormField == null) {
                continue;
            }

            Long id;
            if (financeFormField.getId() != null && !"null".equals(financeFormField.getId()) && !financeFormField.getId().startsWith("unsaved")) {
                id = Long.valueOf(financeFormField.getId());
            } else {
                if(StringUtils.isEmpty(financeFormField.getValue()) || financeFormField.getValue().equals(UNSPECIFIED_AMOUNT_STR)) {
                    continue;
                }
                id = -1L;
            }

            if (costKeyMap.containsKey(id)) {
                costKeyMap.get(id).add(financeFormField);
            } else {
                List<FinanceFormField> costKeyValues = new ArrayList<>();
                costKeyValues.add(financeFormField);
                costKeyMap.put(id, costKeyValues);
            }
        }
        return costKeyMap;
    }

    FinanceFormField getCostFormField(String costTypeKey, String value) {
        String[] keyParts = costTypeKey.split("-");
        if (keyParts.length > 3) {
            //return new FinanceFormField(costTypeKey, value, keyParts[3], keyParts[2], keyParts[1], keyParts[0]);
            return new FinanceFormField(keyParts[0] + "-" + keyParts[2] + "-" + keyParts[3], value, keyParts[3], keyParts[2], keyParts[1], keyParts[0]);
        } else if (keyParts.length == 3) {
            //return new FinanceFormField(costTypeKey, value, null, keyParts[2], keyParts[1], keyParts[0]);
            return new FinanceFormField(keyParts[0] + "-" + keyParts[2], value, null, keyParts[2], keyParts[1], keyParts[0]);
        }
        return null;
    }

    Map<Long, ValidationMessages> storeFinanceRowItems(List<FinanceRowItem> costItems, Function<FinanceRowItem, RestResult<ValidationMessages>> updatingFunction) {
        Map<Long, ValidationMessages> validationMessagesMap = new HashMap<>();
        costItems.stream().forEach(c -> {
            RestResult<ValidationMessages> messages = updatingFunction.apply(c);
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

    protected FinanceRowHandler getFinanceRowItemHandler(FinanceRowType costType) {
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
                return new OtherCostsHandler();
            case OTHER_FUNDING:
                return new OtherFundingHandler();
            case YOUR_FINANCE:
                return new YourFinanceHandler();
            default:
                LOG.error("getFinanceRowItem, unsupported type: " + costType);
                return null;
        }
    }

    protected abstract List<Either<FinanceRowItem, ValidationMessages>> getFinanceRowItems(Map<Long, List<FinanceFormField>> costFieldMap, FinanceRowType costType, Long applicationFinanceId);
}
