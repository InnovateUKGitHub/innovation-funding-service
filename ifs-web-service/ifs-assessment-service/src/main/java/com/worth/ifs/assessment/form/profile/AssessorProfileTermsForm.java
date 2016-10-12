package com.worth.ifs.assessment.form.profile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.AssertTrue;

/**
 * Form field model for the Assessor Profile Terms of Contract page
 */
public class AssessorProfileTermsForm {
    @AssertTrue(message = "{validation.assessorprofiletermsform.terms.required}")
    private Boolean agreesToTerms;

    public AssessorProfileTermsForm() {
    }

    public AssessorProfileTermsForm(Boolean agreesToTerms) {
        this.agreesToTerms = agreesToTerms;
    }

    public Boolean getAgreesToTerms() {
        return agreesToTerms;
    }

    public void setAgreesToTerms(Boolean agreesToTerms) {
        this.agreesToTerms = agreesToTerms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorProfileTermsForm that = (AssessorProfileTermsForm) o;

        return new EqualsBuilder()
                .append(agreesToTerms, that.agreesToTerms)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(agreesToTerms)
                .toHashCode();
    }
}
