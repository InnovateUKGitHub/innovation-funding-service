package org.innovateuk.ifs.assessment.form.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Form field model for the Assessor Profile Agreement view.
 */
public class AssessorProfileAgreementForm implements BindingResultTarget {

    @NotNull(message = "{validation.assessorprofileagreementform.terms.required}")
    @AssertTrue(message = "{validation.assessorprofileagreementform.terms.required}")
    private Boolean agreesToTerms;

    private BindingResult bindingResult;
    private List<ObjectError> objectErrors;

    public Boolean getAgreesToTerms() {
        return agreesToTerms;
    }

    public void setAgreesToTerms(Boolean agreesToTerms) {
        this.agreesToTerms = agreesToTerms;
    }

    @Override
    public BindingResult getBindingResult() {
        return bindingResult;
    }

    @Override
    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    @Override
    public List<ObjectError> getObjectErrors() {
        return objectErrors;
    }

    @Override
    public void setObjectErrors(List<ObjectError> objectErrors) {
        this.objectErrors = objectErrors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorProfileAgreementForm that = (AssessorProfileAgreementForm) o;

        return new EqualsBuilder()
                .append(agreesToTerms, that.agreesToTerms)
                .append(bindingResult, that.bindingResult)
                .append(objectErrors, that.objectErrors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(agreesToTerms)
                .append(bindingResult)
                .append(objectErrors)
                .toHashCode();
    }
}
