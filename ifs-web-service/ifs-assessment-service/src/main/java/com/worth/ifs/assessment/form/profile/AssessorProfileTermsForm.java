package com.worth.ifs.assessment.form.profile;

/**
 * Form field model for the Assessor Profile Terms of Contract page
 */
public class AssessorProfileTermsForm {

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
}
