package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.item.FinanceRowHandler;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * {@code DefaultFinanceFormHandler} retrieves the costs and handles the finance data retrieved from the request, so it can be
 * transfered to view or stored. The costs retrieved from the {@link FinanceRowRestService} are converted
 * to {@link FinanceRowItem}.
 */
@Component
public class DefaultFinanceFormHandler extends BaseFinanceFormHandler<DefaultFinanceRowRestService> implements FinanceFormHandler {

    private static final Log LOG = LogFactory.getLog(DefaultFinanceFormHandler.class);

    private final FinanceService financeService;
    private final FundingLevelResetHandler fundingLevelResetHandler;
    private final ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    public DefaultFinanceFormHandler(final FinanceService financeService,
                                     final DefaultFinanceRowRestService defaultFinanceRowRestService,
                                     final UnsavedFieldsManager unsavedFieldsManager,
                                     final ApplicationFinanceRestService applicationFinanceRestService,
                                     final FundingLevelResetHandler fundingLevelResetHandler) {
        super(defaultFinanceRowRestService, unsavedFieldsManager);
        this.financeService = financeService;
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.fundingLevelResetHandler = fundingLevelResetHandler;
    }

    @Override
    public ValidationMessages update(HttpServletRequest request, Long userId, Long applicationId, Long competitionId) {

        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        if (applicationFinanceResource == null) {
            applicationFinanceResource = financeService.addApplicationFinance(userId, applicationId);
        }

        storeFinancePosition(request, applicationFinanceResource.getId(), competitionId, userId);
        ValidationMessages errors = getAndStoreCostitems(request, applicationFinanceResource.getId(), cost ->
                getFinanceRowRestService().update(cost));
        addRemoveCostRows(request, applicationId, userId);

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
        return getFinanceRowRestService().add(applicationFinance.getId(), questionId, null).getSuccess();
    }

    @Override
    public FinanceRowItem addCostWithoutPersisting(Long applicationId, Long userId, Long questionId) {
        ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(userId, applicationId);
        return getFinanceRowRestService().addWithoutPersisting(applicationFinance.getId(), questionId).getSuccess();
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
            getFinanceRowRestService().delete(Long.valueOf(removeCostParam)).getSuccess();
        }
    }

    private void storeFinancePosition(HttpServletRequest request, @NotNull Long applicationFinanceId, Long competitionId, Long userId) {
        List<String> financePositionKeys = simpleFilter(request.getParameterMap().keySet(), k -> k.contains("financePosition-"));
        if (!financePositionKeys.isEmpty()) {
            ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getById(applicationFinanceId).getSuccess();

            financePositionKeys.forEach(k -> {
                String values = request.getParameterValues(k)[0];
                LOG.debug(String.format("finance position k : %s value: %s ", k, values));
                updateFinancePosition(applicationFinance, k, values, competitionId, userId);
            });
            applicationFinanceRestService.update(applicationFinance.getId(), applicationFinance);
        }
    }

    @Override
    public void updateFinancePosition(Long userId, Long applicationId, String fieldName, String value, Long competitionId) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        updateFinancePosition(applicationFinanceResource, fieldName, value, competitionId, userId);
        applicationFinanceRestService.update(applicationFinanceResource.getId(), applicationFinanceResource);
    }


    private void updateFinancePosition(ApplicationFinanceResource applicationFinance, String fieldName, String value, Long competitionId, Long userId) {
        String fieldNameReplaced = fieldName.replace("financePosition-", "");
        switch (fieldNameReplaced) {
            case "organisationSize":
                Long newValue = Long.valueOf(value);
                Long oldValue = applicationFinance.getOrganisationSize();
                handleOrganisationSizeChange(applicationFinance, competitionId, userId, oldValue, newValue);
                applicationFinance.setOrganisationSize(newValue);
                break;
            default:
                LOG.error(String.format("value not saved: %s / %s", fieldNameReplaced, value));
        }
    }

    private void handleOrganisationSizeChange(ApplicationFinanceResource applicationFinance, Long competitionId, Long userId, Long oldValue, Long newValue) {
        if(null != oldValue && !oldValue.equals(newValue)) {
            fundingLevelResetHandler.resetFundingAndMarkAsIncomplete(applicationFinance, competitionId, userId);
        }
    }

    private ValidationMessages storeField(String fieldName, String value, Long userId, Long applicationId) {
        FinanceFormField financeFormField = getCostFormField(fieldName, value);
        FinanceRowType costType = FinanceRowType.fromType(FormInputType.findByName(financeFormField.getKeyType()));
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

    private ValidationMessages storeFinanceRowItem(FinanceRowItem costItem, Long userId, Long applicationId, String question) {

        if (costItem.getId().equals(0L)) {
            return addFinanceRowItem(costItem, userId, applicationId, question);
        } else {
            RestResult<ValidationMessages> messages = getFinanceRowRestService().update(costItem);
            ValidationMessages validationMessages = messages.getSuccess();

            if (validationMessages == null || validationMessages.getErrors() == null || validationMessages.getErrors().isEmpty()) {
                LOG.debug("no validation errors on cost items");
                return messages.getSuccess();
            } else {
                messages.getSuccess().getErrors().stream()
                        .peek(e -> LOG.debug(String.format("Got cost item Field error: %s", e.getErrorKey())));
                return messages.getSuccess();
            }
        }
    }

    private ValidationMessages addFinanceRowItem(FinanceRowItem costItem, Long userId, Long applicationId, String question) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);

        if (question != null && !question.isEmpty()) {
            Long questionId = Long.parseLong(question);
            return getFinanceRowRestService().add(applicationFinanceResource.getId(), questionId, costItem).getSuccess();
        }
        return null;
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long applicationFinanceId) {
        throw new NotImplementedException("Finance upload is not available for the default finances");
    }
}