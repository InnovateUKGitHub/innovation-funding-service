package org.innovateuk.ifs.commons.validation;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.validation.exception.SpendProfileValidationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * This is responsible for validating the Spend Profile Monthly Costs.
 * It ensures that the cost is not fractional, is greater than or equal to zero, and is less than a million
 */
@Component
public class SpendProfileCostValidator implements Validator {

    private static final Log LOG = LogFactory.getLog(SpendProfileCostValidator.class);


    private static final BigDecimal COST_UPPER_LIMIT = new BigDecimal("1000000");
    private static final int COMPARE_LESS_THAN = -1;
    private static final String FIELD_NAME_TEMPLATE = "table.monthlyCostsPerCategoryMap[%d][%d]";

    @Override
    public void validate(Object target, Errors errors) {
        SpendProfileTableResource table = (SpendProfileTableResource) target;
        Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();

        for (Map.Entry<Long, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {
            validateMonthlyCosts(entry.getValue(), entry.getKey(), errors);
        }
    }

    private void validateMonthlyCosts(List<BigDecimal> monthlyCosts, Long category, Errors errors) {
        int numberOfMonthlyCosts = monthlyCosts.size();

        for (int index = 0; index < numberOfMonthlyCosts; index++) {
            try {
                isValid(monthlyCosts.get(index), category, index);
            } catch (SpendProfileValidationException ex) {

                String bindFieldName = String.format(FIELD_NAME_TEMPLATE, ex.getCategory(), ex.getPosition());
                ValidationMessages.rejectValue(errors, bindFieldName, ex.getSpendProfileValidationError().getErrorKey());

                if (LOG.isDebugEnabled()) {
                    LOG.debug(ex.getMessage());
                }
            }
        }
    }

    private void isValid(BigDecimal cost, Long category, int index) throws SpendProfileValidationException {

        if (null == cost) {
            throw new SpendProfileValidationException(SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL, category, index);
        }

        if (cost.scale() > 0) {
            throw new SpendProfileValidationException(SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL, category, index);
        }

        if (COMPARE_LESS_THAN == cost.compareTo(BigDecimal.ZERO)) {
            throw new SpendProfileValidationException(SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO, category, index);
        }

        if (COMPARE_LESS_THAN != cost.compareTo(COST_UPPER_LIMIT)) {
            throw new SpendProfileValidationException(SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT, category, index);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SpendProfileTableResource.class.isAssignableFrom(clazz);
    }


}
