package com.worth.ifs.assessment.form;

public class AssessorRegistrationTermsForm {
    private Boolean agreesToTerms;

    public AssessorRegistrationTermsForm() {
    }

    public AssessorRegistrationTermsForm(Boolean agreesToTerms) {
        this.agreesToTerms = agreesToTerms;
    }

    public Boolean getAgreesToTerms() {
        return agreesToTerms;
    }

    public void setAgreesToTerms(Boolean agreesToTerms) {
        this.agreesToTerms = agreesToTerms;
    }
}
