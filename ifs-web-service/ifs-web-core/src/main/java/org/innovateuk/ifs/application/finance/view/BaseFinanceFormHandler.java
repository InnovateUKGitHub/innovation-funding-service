package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.application.finance.view.item.*;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.exception.BigDecimalNumberFormatException;
import org.innovateuk.ifs.exception.IntegerNumberFormatException;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.util.Either;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Base methods for all FinanceFormHandlers. For example methods that handle exceptions or errors that are possibly occurring in all FinanceFormHandlers.
 */
public abstract class BaseFinanceFormHandler<FinanceRowRestServiceType extends FinanceRowRestService> {

    private final FinanceRowRestServiceType financeRowRestService;
    private final UnsavedFieldsManager unsavedFieldsManager;

    private static final String UNSPECIFIED_AMOUNT_STR = "£ 0";

    private static final Log LOG = LogFactory.getLog(BaseFinanceFormHandler.class);

    protected BaseFinanceFormHandler(final FinanceRowRestServiceType financeRowRestService,
                                     final UnsavedFieldsManager unsavedFieldsManager) {
        this.financeRowRestService = financeRowRestService;
        this.unsavedFieldsManager = unsavedFieldsManager;
    }

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

    protected ValidationMessages getAndStoreCostitems(HttpServletRequest request,
                                                      Long financeId,
                                                      Function<FinanceRowItem, RestResult<ValidationMessages>> updatingFunction) {

        ValidationMessages errors = new ValidationMessages();

        List<Either<FinanceRowItem, ValidationMessages>> costItems = getFinanceRowItems(request.getParameterMap(),
                financeId);
        List<ValidationMessages> invalidItems = costItems.stream().filter(Either::isRight).map(Either::getRight)
                .collect(toList());
        List<Error> getFinanceRowItemErrors = flattenLists(simpleMap(invalidItems, validationMessages ->
                simpleMap(validationMessages.getErrors(), e -> {
                    if (StringUtils.hasText(e.getErrorKey())) {
                        return fieldError("formInput[cost-" + validationMessages.getObjectId()
                                + "-" + e.getFieldName() + "]", e.getFieldRejectedValue(), e.getErrorKey(), e.getArguments());
                    } else {
                        return fieldError("formInput[cost-" + validationMessages.getObjectId()
                                + "]", e.getFieldRejectedValue(), e.getErrorKey(), e.getArguments());
                    }
                })
        ));

        errors.addErrors(getFinanceRowItemErrors);

        List<FinanceRowItem> validItems = costItems.stream().filter(Either::isLeft).map(Either::getLeft).collect(toList());
        Map<Long, ValidationMessages> storedItemErrors = storeFinanceRowItems(validItems, updatingFunction);
        storedItemErrors.forEach((costId, validationMessages) ->
                validationMessages.getErrors().forEach(dataServiceError -> {
                    if (StringUtils.hasText(dataServiceError.getErrorKey())) {
                        if (dataServiceError.isFieldError() && dataServiceError.getFieldName().equals("calculationFile")) {
                            errors.addError(fieldError("overheadfile", dataServiceError));
                        } else {
                            errors.addError(fieldError("formInput[cost-" + costId + "-" + dataServiceError.getFieldName() + "]",
                                    dataServiceError.getFieldRejectedValue(), dataServiceError.getErrorKey(), dataServiceError.getArguments()));
                        }
                    } else {
                        errors.addError(fieldError("formInput[cost-" + costId + "]", dataServiceError.getFieldRejectedValue(),
                                dataServiceError.getErrorKey(), dataServiceError.getArguments()));
                    }
                })
        );

        return errors;
    }

    private List<Either<FinanceRowItem, ValidationMessages>> getFinanceRowItems(Map<String, String[]> params, Long financeId) {
        List<Either<FinanceRowItem, ValidationMessages>> costItems = new ArrayList<>();
        for (FinanceRowType costType : FinanceRowType.values()) {
            List<String> costTypeKeys = params.keySet().stream().
                    filter(k -> k.startsWith(costType.getType() + "-")).collect(toList());
            Map<Long, List<FinanceFormField>> costFieldMap = getCostDataRows(params, costTypeKeys);
            List<Either<FinanceRowItem, ValidationMessages>> costItemsForType = getFinanceRowItems(costFieldMap, costType, financeId);
            costItems.addAll(costItemsForType);
        }

        return costItems;
    }

    /**
     * Retrieve the complete cost item data row, so everything is together
     */
    private Map<Long, List<FinanceFormField>> getCostDataRows(Map<String, String[]> params, List<String> costTypeKeys) {
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
            return new FinanceFormField(keyParts[0] + "-" + keyParts[2] + "-" + keyParts[3], value, keyParts[3], keyParts[2], keyParts[1], keyParts[0]);
        } else if (keyParts.length == 3) {
            return new FinanceFormField(keyParts[0] + "-" + keyParts[2], value, null, keyParts[2], keyParts[1], keyParts[0]);
        }
        return null;
    }

    Map<Long, ValidationMessages> storeFinanceRowItems(List<FinanceRowItem> costItems, Function<FinanceRowItem, RestResult<ValidationMessages>> updatingFunction) {
        Map<Long, ValidationMessages> validationMessagesMap = new HashMap<>();
        costItems.forEach(c -> {
            RestResult<ValidationMessages> messages = updatingFunction.apply(c);
            Optional<ValidationMessages> successObject = messages.getOptionalSuccessObject();
            if (successObject.isPresent() && successObject.get() != null &&
                    messages.getSuccess().getErrors() != null &&
                    !messages.getSuccess().getErrors().isEmpty()
                    ) {
                LOG.debug("got validation errors. " + c.getId());
                validationMessagesMap.put(c.getId(), messages.getSuccess());
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

    /**
     * Retrieve the cost items from the request based on their type
     */
    protected List<Either<FinanceRowItem, ValidationMessages>> getFinanceRowItems(Map<Long, List<FinanceFormField>> costFieldMap, FinanceRowType costType, Long applicationFinanceId) {
        List<Either<FinanceRowItem, ValidationMessages>> costItems = new ArrayList<>();

        if(costFieldMap.size() == 0) {
            return costItems;
        }
        FinanceRowHandler financeRowHandler = getFinanceRowItemHandler(costType);

        // create new cost items
        for (Map.Entry<Long, List<FinanceFormField>> entry : costFieldMap.entrySet()) {
            try{
                Long id = entry.getKey();
                List<FinanceFormField> fields = entry.getValue();

                if(id == -1L) {
                    Map<String,List<FinanceFormField>> grouped = unsavedFieldsManager.separateGroups(fields);
                    for(Map.Entry<String, List<FinanceFormField>> groupedEntry : grouped.entrySet()) {
                        List<FinanceFormField> fieldGroup = groupedEntry.getValue();
                        FinanceRowItem costItem = financeRowHandler.toFinanceRowItem(null, fieldGroup);
                        if (costItem != null && !fieldGroup.isEmpty()) {
                            Long questionId = Long.valueOf(fieldGroup.get(0).getQuestionId());
                            ValidationMessages addResult = financeRowRestService.add(applicationFinanceId, questionId, costItem).getSuccess();
                            Either<FinanceRowItem, ValidationMessages> either;
                            if(addResult.hasErrors()) {
                                either = Either.right(addResult);
                            } else {
                                FinanceRowItem added = financeRowRestService.findById(addResult.getObjectId()).getSuccess();
                                either = Either.left(added);
                            }

                            costItems.add(either);
                        }
                    }
                } else {
                    FinanceRowItem costItem = financeRowHandler.toFinanceRowItem(id, fields);
                    if (costItem != null) {
                        Either<FinanceRowItem, ValidationMessages> either = Either.left(costItem);
                        costItems.add(either);
                    }
                }

            }catch(NumberFormatException e){
                ValidationMessages validationMessages = getValidationMessageFromException(entry, e);
                Either<FinanceRowItem, ValidationMessages> either = Either.right(validationMessages);
                costItems.add(either);
            }
        }
        return costItems;
    }

    protected FinanceRowRestServiceType getFinanceRowRestService() {
        return financeRowRestService;
    }
}
