package org.innovateuk.ifs.finance.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.validation.ApplicationValidatorService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.Error.globalError;

@Component
public class FinanceValidationUtil {
    private final static Log LOG = LogFactory.getLog(FinanceValidationUtil.class);

    @Autowired
    private ApplicationValidatorService applicationValidatorService;

    public List<ValidationMessages> validateCostItem(List<FinanceRowItem> costItems) {
        if (costItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<ValidationMessages> results = costItems.stream()
                .map(this::validateCostItem)
                .filter(this::nonEmpty)
                .collect(Collectors.toList());

        ValidationMessages emptyRowMessages = invokeEmptyRowValidator(costItems);
        if (emptyRowMessages != null) {
            results.add(emptyRowMessages);
        }

        return results;
    }

    public ValidationMessages validateCostItem(FinanceRowItem costItem) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(costItem, "costItem");
        invokeValidator(costItem, bindingResult);
        return buildValidationMessages(costItem, bindingResult);
    }

    public ValidationMessages validateProjectCostItem(FinanceRowItem costItem) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(costItem, "costItem");
        invokeProjectCostValidator(costItem, bindingResult);
        return buildValidationMessages(costItem, bindingResult);
    }


    private void invokeProjectCostValidator(FinanceRowItem costItem, BeanPropertyBindingResult bindingResult) {
        FinanceRowHandler financeRowHandler = applicationValidatorService.getProjectCostHandler(costItem);
        financeRowHandler.validate(costItem, bindingResult);
    }

    private void invokeValidator(FinanceRowItem costItem, BeanPropertyBindingResult bindingResult) {
        FinanceRowHandler financeRowHandler = applicationValidatorService.getCostHandler(costItem);
        financeRowHandler.validate(costItem, bindingResult);
    }


    private ValidationMessages buildValidationMessages(FinanceRowItem costItem, BeanPropertyBindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("validated, with messages: ");
                bindingResult.getFieldErrors().stream().forEach(e -> LOG.debug("Field Error: " + e.getRejectedValue() + e.getDefaultMessage()));
                bindingResult.getAllErrors().stream().forEach(e -> LOG.debug("Error: " + e.getObjectName() + e.getDefaultMessage()));
            }
            return new ValidationMessages(costItem.getId(), bindingResult);
        } else {
            return ValidationMessages.noErrors(costItem.getId());
        }
    }

    private boolean nonEmpty(ValidationMessages validationMessages) {
        return validationMessages != null && validationMessages.hasErrors();
    }

    private ValidationMessages invokeEmptyRowValidator(List<FinanceRowItem> costItems) {
        ValidationMessages validationMessages = new ValidationMessages();
        int rowCount = 0;
        if (costItems.size() > 1) {
            for (final FinanceRowItem row : costItems) {
                boolean exclude = row.excludeInRowCount();
                if (!exclude) {
                    rowCount++;
                }
            }
        }

        if (rowCount < costItems.get(0).getMinRows()) {
            switch (costItems.get(0).getCostType()) {
                case OTHER_FUNDING:
                    if ("Yes".equals(((OtherFunding) costItems.get(0)).getOtherPublicFunding())) {
                        if (costItems.get(0).getMinRows() == 1) {
                            validationMessages.addError(globalError("validation.finance.min.row.other.funding.single"));
                        } else {
                            validationMessages.addError(globalError("validation.finance.min.row.other.funding.multiple", singletonList(costItems.get(0).getMinRows())));
                        }
                    }
                    break;
                default:
                    validationMessages.addError(globalError("validation.finance.min.row", singletonList(costItems.get(0).getMinRows())));
                    break;
            }
        }
        return validationMessages;
    }

}
