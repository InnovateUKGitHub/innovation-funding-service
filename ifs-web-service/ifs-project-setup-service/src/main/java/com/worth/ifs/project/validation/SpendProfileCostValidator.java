package com.worth.ifs.project.validation;

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

        String partialErrorKey = String.format("Cost for category: %s and Month#: %d", category, index + 1);

        checkFractionalCost(cost, category, index, errors, partialErrorKey);

        checkCostLessThanZero(cost, category, index, errors, partialErrorKey);

        checkCostGreaterThanOrEqualToMillion(cost, category, index, errors, partialErrorKey);
    }

    private void checkFractionalCost(BigDecimal cost, Long category, int index, Errors errors, String partialErrorKey) {

        String errorKey = "COST_FRACTIONAL: " + partialErrorKey;
        if (cost.scale() > 0) {
            String errorMessage = String.format("Cost cannot contain fractional part. Category: %s, Month#: %d", category, index + 1);
            errors.reject(errorKey, errorMessage);

        }
    }

    private void checkCostLessThanZero(BigDecimal cost, Long category, int index, Errors errors, String partialErrorKey) {

        String errorKey = "COST_LESS_THAN_ZERO: " + partialErrorKey;
        if (-1 == cost.compareTo(BigDecimal.ZERO)) { // Indicates that the cost is less than zero
            String errorMessage = String.format("Cost cannot be less than zero. Category: %s, Month#: %d", category, index + 1);
            errors.reject(errorKey, errorMessage);
        }
    }

    private void checkCostGreaterThanOrEqualToMillion(BigDecimal cost, Long category, int index, Errors errors, String partialErrorKey) {

        String errorKey = "COST_MILLION_OR_MORE: " + partialErrorKey;
        if (-1 != cost.compareTo(new BigDecimal("1000000"))) { // Indicates that the cost million or more
            String errorMessage = String.format("Cost cannot be million or more. Category: %s, Month#: %d", category, index + 1);
            errors.reject(errorKey, errorMessage);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SpendProfileTableResource.class.isAssignableFrom(clazz);
    }

}
