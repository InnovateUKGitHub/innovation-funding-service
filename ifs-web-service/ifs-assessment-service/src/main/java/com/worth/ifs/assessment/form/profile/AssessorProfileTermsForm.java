package com.worth.ifs.assessment.form.profile;

import com.worth.ifs.controller.BindingResultTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Form field model for the Assessor Profile Terms of Contract page
 */
public class AssessorProfileTermsForm implements BindingResultTarget {

    @NotNull(message = "{validation.assessorprofiletermsform.terms.required}")
    @AssertTrue(message = "{validation.assessorprofiletermsform.terms.required}")
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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileTermsForm that = (AssessorProfileTermsForm) o;

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
