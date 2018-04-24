package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

import java.util.List;

public class TermsAndConditionsViewModel extends CompetitionSetupViewModel {

    List<TermsAndConditionsResource> termsAndConditionsList;

    public TermsAndConditionsViewModel(GeneralSetupViewModel generalSetupViewModel,
                                       List<TermsAndConditionsResource> termsAndConditionsList) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.termsAndConditionsList = termsAndConditionsList;
    }

    public List<TermsAndConditionsResource> getTermsAndConditionsList() {
        return termsAndConditionsList;
    }
}
