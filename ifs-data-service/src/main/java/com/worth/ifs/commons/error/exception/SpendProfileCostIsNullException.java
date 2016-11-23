package com.worth.ifs.commons.error.exception;

import com.worth.ifs.validator.SpendProfileValidationErrorKey;
import org.springframework.validation.Errors;

/**
 * Created by xiaonan.zhang on 23/11/2016.
 */
public class SpendProfileCostIsNullException extends SpendProfileValidationException {

    public SpendProfileCostIsNullException(Errors errors, SpendProfileValidationErrorKey errorKey, Long category, int position) {
        super(errors, errorKey, category, position);
    }

}
