package com.worth.ifs.assessment.form;

public class AssessorTermsForm {
    private Boolean agreesToTerms;

    public AssessorTermsForm() {
    }

    public AssessorTermsForm(Boolean agreesToTerms) {
        this.agreesToTerms = agreesToTerms;
    }

    public Boolean getAgreesToTerms() {
        return agreesToTerms;
    }

    public void setAgreesToTerms(Boolean agreesToTerms) {
        this.agreesToTerms = agreesToTerms;
    }
}
