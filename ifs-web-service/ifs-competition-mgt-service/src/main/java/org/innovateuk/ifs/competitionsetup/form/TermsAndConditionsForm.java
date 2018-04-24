package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;

public class TermsAndConditionsForm extends CompetitionSetupForm {

    private TermsAndConditionsResource termsAndConditions;

    public TermsAndConditionsResource getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(TermsAndConditionsResource termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
}
