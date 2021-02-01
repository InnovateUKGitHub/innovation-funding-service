package org.innovateuk.ifs.management.competition.setup.core.viewmodel;

import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;

import java.util.List;

public class TermsAndConditionsViewModel extends CompetitionSetupViewModel {

    private final List<GrantTermsAndConditionsResource> termsAndConditionsList;

    private final GrantTermsAndConditionsResource currentTermsAndConditions;

    private final boolean termsAndConditionsDocUploaded;

    public TermsAndConditionsViewModel(GeneralSetupViewModel generalSetupViewModel,
                                       List<GrantTermsAndConditionsResource> termsAndConditionsList,
                                       GrantTermsAndConditionsResource currentTermsAndConditions,
                                       boolean termsAndConditionsDocUploaded) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.termsAndConditionsList = termsAndConditionsList;
        this.currentTermsAndConditions = currentTermsAndConditions;
        this.termsAndConditionsDocUploaded = termsAndConditionsDocUploaded;
    }

    public List<GrantTermsAndConditionsResource> getTermsAndConditionsList() {
        return termsAndConditionsList;
    }

    public GrantTermsAndConditionsResource getCurrentTermsAndConditions() {
        return currentTermsAndConditions;
    }

    public boolean isTermsAndConditionsDocUploaded() {
        return termsAndConditionsDocUploaded;
    }


}