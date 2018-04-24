package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;

import java.util.List;

public class TermsAndConditionsForm extends CompetitionSetupForm {

    private List<TermsAndConditionsResource> termsAndConditionsList;

    public List<TermsAndConditionsResource> getTermsAndConditionsList() {
        return termsAndConditionsList;
    }

    public void setTermsAndConditionsList(List<TermsAndConditionsResource> termsAndConditionsList) {
        this.termsAndConditionsList = termsAndConditionsList;
    }
}
