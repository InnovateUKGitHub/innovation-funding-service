package com.worth.ifs.commons.error.exception;

import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.validator.SpendProfileValidationErrorKey;
import org.springframework.validation.Errors;

/**
 * Created by xiaonan.zhang on 23/11/2016.
 */
public class SpendProfileValidationException extends IFSRuntimeException {

    private SpendProfileValidationErrorKey errorKey;
    private Long category;
    private int position;

    public SpendProfileValidationException(Errors errors, SpendProfileValidationErrorKey errorKey, Long category, int position) {
        this.errorKey = errorKey;
        this.category = category;
        this.position = position;

        ValidationMessages.reject(errors, errorKey.getErrorKey(), category, position);
    }

    @Override
    public String toString() {
        return "SpendProfileValidationException{" +
                "errorKey=" + errorKey +
                ", category=" + category +
                ", position=" + position +
                '}';
    }
}
