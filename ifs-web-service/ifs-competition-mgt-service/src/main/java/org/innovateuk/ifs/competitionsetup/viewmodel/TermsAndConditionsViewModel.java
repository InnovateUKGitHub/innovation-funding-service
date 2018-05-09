package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

import java.util.List;

public class TermsAndConditionsViewModel extends CompetitionSetupViewModel {

    private List<GrantTermsAndConditionsResource> termsAndConditionsList;

    private GrantTermsAndConditionsResource currentTermsAndConditions;

    public TermsAndConditionsViewModel(GeneralSetupViewModel generalSetupViewModel,
                                       List<GrantTermsAndConditionsResource> termsAndConditionsList,
                                       GrantTermsAndConditionsResource currentTermsAndConditions) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.termsAndConditionsList = termsAndConditionsList;
        this.currentTermsAndConditions = currentTermsAndConditions;
    }

    public List<GrantTermsAndConditionsResource> getTermsAndConditionsList() {
        return termsAndConditionsList;
    }

    public GrantTermsAndConditionsResource getCurrentTermsAndConditions() {
        return currentTermsAndConditions;
    }
}
