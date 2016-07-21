package com.worth.ifs.sil.experian.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidationResultWrapper {

    @JsonProperty("ValidationResult")
    private ValidationResult validationResult;

    public ValidationResultWrapper() {
    }

    public ValidationResultWrapper(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public com.worth.ifs.sil.experian.resource.ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(com.worth.ifs.sil.experian.resource.ValidationResult validationResult) {
        this.validationResult = validationResult;
    }
}
