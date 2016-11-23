package com.worth.ifs.project.validation;


import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.validation.exception.SpendProfileValidationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    @Override
    public void validate(Object target, Errors errors) {
        SpendProfileTableResource table = (SpendProfileTableResource) target;
        Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();

        for (Map.Entry<Long, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {
            Long category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            int numberOfMonthlyCosts = monthlyCosts.size();
            for (int index = 0; index < numberOfMonthlyCosts; index++) {
                try {
                    isValid(monthlyCosts.get(index), category, index);
                } catch (SpendProfileValidationException ex) {

                    ValidationMessages.reject(errors, ex.getSpendProfileValidationError().getErrorKey(), ex.getCategory(), ex.getPosition());

                    if (LOG.isDebugEnabled()) {
                        LOG.debug(ex.getMessage());
                    } // else do nothing
                }
            }
        }
    }

    private void isValid(BigDecimal cost, Long category, int index) throws SpendProfileValidationException {

        int positionInError = index + 1;

        // cost should not be null
        if (null == cost) {
            throw new SpendProfileValidationException(SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL, category, positionInError);
        }

        // cost should not be fractional
        if (cost.scale() > 0) {
            throw new SpendProfileValidationException(SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL, category, positionInError);
        }

        // cost should not be less than zero
        if (COMPARE_LESS_THAN == cost.compareTo(BigDecimal.ZERO)) {
            throw new SpendProfileValidationException(SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO, category, positionInError);
        }

        // cost should be within the upper limit
        if (COMPARE_LESS_THAN != cost.compareTo(COST_UPPER_LIMIT)) {
            throw new SpendProfileValidationException(SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT, category, positionInError);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SpendProfileTableResource.class.isAssignableFrom(clazz);
    }

}

