package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

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
