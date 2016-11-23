package com.worth.ifs.validator;

import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.project.resource.SpendProfileTableResource;
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

    private static final BigDecimal COST_VALUE_MAX = new BigDecimal("1000000");
    private static final int COMPARE_NOT_EQUAL = -1;

    @Override
    public void validate(Object target, Errors errors) {
        SpendProfileTableResource table = (SpendProfileTableResource) target;
        Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();

        for (Map.Entry<Long, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {
            Long category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            int index = 0;
            for (BigDecimal cost : monthlyCosts) {
                isValid(cost, category, index, errors);
                index++;
            }
        }
    }

    private void isValid(BigDecimal cost, Long category, int index, Errors errors) {

        checkNullCost(cost, category, index, errors);

        checkFractionalCost(cost, category, index, errors);

        checkCostLessThanZero(cost, category, index, errors);

        checkCostGreaterThanOrEqualToMillion(cost, category, index, errors);
    }

    private void checkNullCost(BigDecimal cost, Long category, int index, Errors errors) {
        if (null == cost) {
            ValidationMessages.reject(errors, SpendProfileValidationErrorKey.FIELD_MUST_NOT_BE_NULL.getErrorKey(), category, index + 1);
        }
    }

    private void checkFractionalCost(BigDecimal cost, Long category, int index, Errors errors) {
        if ((null != cost) && (cost.scale() > 0)) {
            ValidationMessages.reject(errors, SpendProfileValidationErrorKey.COST_SHOULD_NOT_BE_FRACTIONAL.getErrorKey(), category, index + 1);
        }
    }

    private void checkCostLessThanZero(BigDecimal cost, Long category, int index, Errors errors) {
        if ((null != cost) && (COMPARE_NOT_EQUAL == cost.compareTo(BigDecimal.ZERO))) { // Indicates that the cost is less than zero
            ValidationMessages.reject(errors, SpendProfileValidationErrorKey.COST_SHOULD_NOT_BE_LESS_THAN_ZERO.getErrorKey(), category, index + 1);
        }
    }

    private void checkCostGreaterThanOrEqualToMillion(BigDecimal cost, Long category, int index, Errors errors) {
        if ((null != cost) && (COMPARE_NOT_EQUAL != cost.compareTo(COST_VALUE_MAX))) { // Indicates that the cost million or more
            ValidationMessages.reject(errors, SpendProfileValidationErrorKey.COST_SHOULD_NOT_BE_MORE_THAN_ALLOWED_MAX.getErrorKey(), category, index + 1);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SpendProfileTableResource.class.isAssignableFrom(clazz);
    }

}
