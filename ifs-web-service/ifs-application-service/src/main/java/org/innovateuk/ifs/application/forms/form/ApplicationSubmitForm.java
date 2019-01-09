package org.innovateuk.ifs.application.forms.form;

public class ApplicationSubmitForm {

    private boolean agreeTerms;

    public boolean isAgreeTerms() {
        return agreeTerms;
    }

    public ApplicationSubmitForm setAgreeTerms(boolean agreeTerms) {
        this.agreeTerms = agreeTerms;
        return this;
    }
}
