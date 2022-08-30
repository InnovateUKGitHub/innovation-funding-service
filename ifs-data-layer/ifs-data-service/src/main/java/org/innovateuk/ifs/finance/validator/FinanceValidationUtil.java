package org.innovateuk.ifs.finance.validator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.validation.ApplicationValidatorService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.Error.globalError;

@SuppressWarnings("unchecked")
@Component
@Slf4j
public class FinanceValidationUtil {

    @Autowired
    private ApplicationValidatorService applicationValidatorService;

    public List<ValidationMessages> validateCostItem(FinanceRowType type, FinanceRowCostCategory costCategory, boolean isCompTypeOfgemAndFundingTypeThirdParty) {
        List<FinanceRowItem> costItems = costCategory.getCosts();
        List<ValidationMessages> results = costItems.stream()
                .map(this::validateCostItem)
                .filter(this::nonEmpty)
                .collect(toList());

        ValidationMessages emptyRowMessages = invokeEmptyRowValidator(type, costCategory, isCompTypeOfgemAndFundingTypeThirdParty);
        results.add(emptyRowMessages);

        return results;
    }

    @Transactional(readOnly = true)
    public ValidationMessages validateCostItem(FinanceRowItem costItem) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(costItem, "costItem");
        invokeValidator(costItem, bindingResult);
        return buildValidationMessages(costItem, bindingResult);
    }

    @Transactional(readOnly = true)
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
            if (log.isDebugEnabled()) {
                log.debug("validated, with messages: ");
                bindingResult.getFieldErrors().stream().forEach(e -> log.debug("Field Error: " + e.getRejectedValue() + e.getDefaultMessage()));
                bindingResult.getAllErrors().stream().forEach(e -> log.debug("Error: " + e.getObjectName() + e.getDefaultMessage()));
            }
            return new ValidationMessages(costItem.getId(), bindingResult);
        } else {
            return ValidationMessages.noErrors(costItem.getId());
        }
    }

    private boolean nonEmpty(ValidationMessages validationMessages) {
        return validationMessages != null && validationMessages.hasErrors();
    }

    private ValidationMessages invokeEmptyRowValidator(FinanceRowType type, FinanceRowCostCategory costCategory, boolean isCompTypeOfgemAndFundingTypeThirdParty) {
        List<FinanceRowItem> costItems = costCategory.getCosts();
        ValidationMessages validationMessages = new ValidationMessages();
        switch (type) {
            case OTHER_FUNDING:
                OtherFundingCostCategory otherFundingCostCategory = (OtherFundingCostCategory) costCategory;
                if ("Yes".equals(otherFundingCostCategory.getOtherFunding().getOtherPublicFunding())) {
                    if (costItems.isEmpty()) {
                        if (isCompTypeOfgemAndFundingTypeThirdParty) {
                            validationMessages.addError(globalError("validation.finance.min.row.contributions.in.kind.single"));
                        } else {
                            validationMessages.addError(globalError("validation.finance.min.row.other.funding.single"));
                        }
                    }
                }
                break;
            default:
                break;
        }
        return validationMessages;
    }

}
