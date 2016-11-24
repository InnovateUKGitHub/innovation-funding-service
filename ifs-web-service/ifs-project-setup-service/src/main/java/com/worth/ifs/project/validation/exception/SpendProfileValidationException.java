package com.worth.ifs.project.validation.exception;

import com.worth.ifs.commons.error.exception.IFSRuntimeException;
import com.worth.ifs.project.validation.SpendProfileValidationError;

import java.util.Collections;

/**
 * generic exception for validating Spend Profile
 */
public class SpendProfileValidationException extends IFSRuntimeException {

    private static final String MESSAGE = "Spend Profile cost is failed to validate. ErrorKey: %s, Category: %d, Position: %d";

    private SpendProfileValidationError spendProfileValidationError;
    private Long category;
    private int position;

    public SpendProfileValidationException(SpendProfileValidationError spendProfileValidationError, Long category, int position) {
        super(String.format(MESSAGE, spendProfileValidationError.getErrorKey(), category, position), Collections.emptyList());

        this.spendProfileValidationError = spendProfileValidationError;
        this.category = category;
        this.position = position;
    }

    public SpendProfileValidationError getSpendProfileValidationError() {
        return spendProfileValidationError;
    }

    public Long getCategory() {
        return category;
    }

    public int getPosition() {
        return position;
    }
}