package org.innovateuk.ifs.competitionsetup.core.viewmodel;

import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;

import java.util.List;

public class TermsAndConditionsViewModel extends CompetitionSetupViewModel {

    private List<TermsAndConditionsResource> termsAndConditionsList;

    private TermsAndConditionsResource currentTermsAndConditions;

    public TermsAndConditionsViewModel(GeneralSetupViewModel generalSetupViewModel,
                                       List<TermsAndConditionsResource> termsAndConditionsList,
                                       TermsAndConditionsResource currentTermsAndConditions) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.termsAndConditionsList = termsAndConditionsList;
        this.currentTermsAndConditions = currentTermsAndConditions;
    }

    public List<TermsAndConditionsResource> getTermsAndConditionsList() {
        return termsAndConditionsList;
    }

    public TermsAndConditionsResource getCurrentTermsAndConditions() {
        return currentTermsAndConditions;
    }
}
