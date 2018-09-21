package org.innovateuk.ifs.eugrant.overview.form;

import javax.validation.constraints.AssertTrue;

public class EuGrantSubmitForm {

    @AssertTrue(message = "{validation.eu-grant.agree-terms}")
    private boolean agreeTerms;

    public boolean isAgreeTerms() {
        return agreeTerms;
    }

    public void setAgreeTerms(boolean agreeTerms) {
        this.agreeTerms = agreeTerms;
    }
}
