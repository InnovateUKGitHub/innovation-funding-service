package com.worth.ifs.validator;

import com.worth.ifs.commons.error.exception.SpendProfileValidationException;
import com.worth.ifs.project.resource.SpendProfileTableResource;
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

    private static final String GENERIC_VALIDATION_ERROR_MESSAGE_TEMPLATE = "Spend Profile cost is failed to validate. Reason: %s";
    private static final BigDecimal COST_UPPER_LIMIT = new BigDecimal("1000000");
    private static final int COMPARE_LESS_THAN = -1;

    @Override
    public void validate(Object target, Errors errors) {
        SpendProfileTableResource table = (SpendProfileTableResource) target;
        Map<Long, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();

        for (Map.Entry<Long, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {
            Long category = entry.getKey();
            List<BigDecimal> monthlyCosts = entry.getValue();

            for (int index = 0; index < monthlyCosts.size(); index++) {
                try {
                    isValid(monthlyCosts.get(index), category, index, errors);
                } catch (SpendProfileValidationException ex) {
                    if (LOG.isDebugEnabled()) {
                        String message = String.format(GENERIC_VALIDATION_ERROR_MESSAGE_TEMPLATE, ex.toString());
                        LOG.debug(message);
                    } // else do nothing. The error should be populated to the Errors by the exception already
                }
            }
        }
    }

    private void isValid(BigDecimal cost, Long category, int index, Errors errors) throws SpendProfileValidationException {

        int positionInError = index + 1;

        // cost should not be null
        if (null == cost) {
            throw new SpendProfileValidationException(errors, SpendProfileValidationErrorKey.COST_SHOULD_NOT_BE_NULL, category, positionInError);
        }

        // cost should not be fractional
        if (cost.scale() > 0) {
            throw new SpendProfileValidationException(errors, SpendProfileValidationErrorKey.COST_SHOULD_NOT_BE_FRACTIONAL, category, positionInError);
        }

        // cost should not be less than zero
        if (COMPARE_LESS_THAN == cost.compareTo(BigDecimal.ZERO)) {
            throw new SpendProfileValidationException(errors, SpendProfileValidationErrorKey.COST_SHOULD_NOT_BE_LESS_THAN_ZERO, category, positionInError);
        }

        // cost should be within the upper limit
        if (COMPARE_LESS_THAN != cost.compareTo(COST_UPPER_LIMIT)) {
            throw new SpendProfileValidationException(errors, SpendProfileValidationErrorKey.COST_SHOULD_BE_WITHIN_UPPER_LIMIT, category, positionInError);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SpendProfileTableResource.class.isAssignableFrom(clazz);
    }

}
