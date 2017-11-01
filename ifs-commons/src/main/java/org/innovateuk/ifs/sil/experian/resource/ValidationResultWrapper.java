package org.innovateuk.ifs.sil.experian.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidationResultWrapper {

    @JsonProperty("ValidationResult")
    private ValidationResult validationResult;

    public ValidationResultWrapper() {
    }

    public ValidationResultWrapper(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }
}
