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

    public org.innovateuk.ifs.sil.experian.resource.ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(org.innovateuk.ifs.sil.experian.resource.ValidationResult validationResult) {
        this.validationResult = validationResult;
    }
}
