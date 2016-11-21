package com.worth.ifs.application.finance.view;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.service.FinanceRowService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.item.*;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import com.worth.ifs.user.resource.OrganisationSize;
import com.worth.ifs.util.Either;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.util.CollectionFunctions.flattenLists;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * {@code DefaultFinanceFormHandler} retrieves the costs and handles the finance data retrieved from the request, so it can be
 * transfered to view or stored. The costs retrieved from the {@link FinanceRowService} are converted
 * to {@link FinanceRowItem}.
 */
@Component
public class DefaultFinanceFormHandler extends BaseFinanceFormHandler implements FinanceFormHandler {
    private static final Log LOG = LogFactory.getLog(DefaultFinanceFormHandler.class);

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;
    
    @Autowired
    private UnsavedFieldsManager unsavedFieldsManager;

    @Override
    public ValidationMessages update(HttpServletRequest request, Long userId, Long applicationId, Long competitionId) {

        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        if (applicationFinanceResource == null) {
            applicationFinanceResource = financeService.addApplicationFinance(userId, applicationId);
        }

        storeFinancePosition(request, applicationFinanceResource.getId());
        ValidationMessages errors = getAndStoreCostitems(request, applicationFinanceResource.getId());
        addRemoveCostRows(request, applicationId, userId);

        return errors;
    }

    private ValidationMessages getAndStoreCostitems(HttpServletRequest request, Long applicationFinanceId) {

        ValidationMessages errors = new ValidationMessages();

        List<Either<FinanceRowItem, ValidationMessages>> costItems = getFinanceRowItems(request.getParameterMap(), applicationFinanceId);
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
        Map<Long, ValidationMessages> storedItemErrors = storeFinanceRowItems(validItems);
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


    @Override
    public ValidationMessages storeCost(Long userId, Long applicationId, String fieldName, String value, Long competitionId) {

        if (fieldName == null || value == null) {
            return new ValidationMessages();
        }

        String cleanedFieldName = fieldName;
        if (fieldName.startsWith("cost-")) {
            cleanedFieldName = fieldName.replace("cost-", "");
        } else if (fieldName.startsWith("formInput[")) {
            cleanedFieldName = fieldName.replace("formInput[", "").replace("]", "");
        }

        LOG.info("store field: " + cleanedFieldName + " val: " + value);
        return storeField(cleanedFieldName, value, userId, applicationId);
    }

    @Override
    public ValidationMessages addCost(Long applicationId, Long userId, Long questionId) {
        ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(userId, applicationId);
        return financeRowService.add(applicationFinance.getId(), questionId, null);
    }
    
    @Override
    public FinanceRowItem addCostWithoutPersisting(Long applicationId, Long userId, Long questionId) {
        ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(userId, applicationId);
        return financeRowService.addWithoutPersisting(applicationFinance.getId(), questionId);
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
            financeRowService.delete(Long.valueOf(removeCostParam));
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

    private List<Either<FinanceRowItem, ValidationMessages>> getFinanceRowItems(Map<String, String[]> params, Long applicationFinanceId) {
    	List<Either<FinanceRowItem, ValidationMessages>> costItems = new ArrayList<>();
        for (FinanceRowType costType : FinanceRowType.values()) {
            List<String> costTypeKeys = params.keySet().stream().
                    filter(k -> k.startsWith(costType.getType() + "-")).collect(Collectors.toList());
            Map<Long, List<FinanceFormField>> costFieldMap = getCostDataRows(params, costTypeKeys);
            List<Either<FinanceRowItem, ValidationMessages>> costItemsForType = getFinanceRowItems(costFieldMap, costType, applicationFinanceId);
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
            	if(StringUtils.isEmpty(financeFormField.getValue())) {
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

    /**
     * Retrieve the cost items from the request based on their type
     */
    private List<Either<FinanceRowItem, ValidationMessages>> getFinanceRowItems(Map<Long, List<FinanceFormField>> costFieldMap, FinanceRowType costType, Long applicationFinanceId) {
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
            		List<List<FinanceFormField>> fieldsSeparated = unsavedFieldsManager.separateFields(fields);
            		for(List<FinanceFormField> fieldGroup: fieldsSeparated) {
            			FinanceRowItem costItem = financeRowHandler.toFinanceRowItem(null, fieldGroup);
                        if (costItem != null && fieldGroup.size() > 0) {
                    		Long questionId = Long.valueOf(fieldGroup.get(0).getQuestionId());
                    		ValidationMessages addResult = financeRowService.add(applicationFinanceId, questionId, costItem);
                    		Either<FinanceRowItem, ValidationMessages> either;
                    		if(addResult.hasErrors()) {
                    			either = Either.right(addResult);
                    		} else {
                    			FinanceRowItem added = financeRowService.findById(addResult.getObjectId());
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

	private ValidationMessages storeField(String fieldName, String value, Long userId, Long applicationId) {
        FinanceFormField financeFormField = getCostFormField(fieldName, value);
        FinanceRowType costType = FinanceRowType.fromString(financeFormField.getKeyType());
        FinanceRowHandler financeRowHandler = getFinanceRowItemHandler(costType);
        Long costFormFieldId = 0L;
        if (financeFormField.getId() != null && !"null".equals(financeFormField.getId()) && !financeFormField.getId().startsWith("unsaved")) {
            costFormFieldId = Long.parseLong(financeFormField.getId());
        }
        FinanceRowItem costItem = financeRowHandler.toFinanceRowItem(costFormFieldId, Arrays.asList(financeFormField));
        if(costItem != null) {
        	return storeFinanceRowItem(costItem, userId, applicationId, financeFormField.getQuestionId());
        } else {
        	return new ValidationMessages();
        }
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

    private FinanceRowHandler getFinanceRowItemHandler(FinanceRowType costType) {
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

    private ValidationMessages storeFinanceRowItem(FinanceRowItem costItem, Long userId, Long applicationId, String question) {

        if (costItem.getId().equals(0L)) {
            return addFinanceRowItem(costItem, userId, applicationId, question);
        } else {
            RestResult<ValidationMessages> messages = financeRowService.update(costItem);
            ValidationMessages validationMessages = messages.getSuccessObject();

            if (validationMessages == null || validationMessages.getErrors() == null || validationMessages.getErrors().isEmpty()) {
                LOG.debug("no validation errors on cost items");
                return messages.getSuccessObject();
            } else {
                messages.getSuccessObject().getErrors().stream()
                        .peek(e -> LOG.debug(String.format("Got cost item Field error: %s", e.getErrorKey())));
                return messages.getSuccessObject();
            }
        }
    }

    private ValidationMessages addFinanceRowItem(FinanceRowItem costItem, Long userId, Long applicationId, String question) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);

        if (question != null && !question.isEmpty()) {
            Long questionId = Long.parseLong(question);
            return financeRowService.add(applicationFinanceResource.getId(), questionId, costItem);
        }
        return null;
    }

    private Map<Long, ValidationMessages> storeFinanceRowItems(List<FinanceRowItem> costItems) {
        Map<Long, ValidationMessages> validationMessagesMap = new HashMap<>();
        costItems.stream().forEach(c -> {
            RestResult<ValidationMessages> messages = financeRowService.update(c);
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
