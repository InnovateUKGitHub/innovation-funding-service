package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;

public class OrganisationalEligibilityViewModel extends CompetitionSetupViewModel {

    private boolean internationalOrganisationsApplicable;

    public OrganisationalEligibilityViewModel(GeneralSetupViewModel generalSetupViewModel) {
        this.generalSetupViewModel = generalSetupViewModel;
//        this.internationalOrganisationsApplicable = internationalOrganisationsApplicable;
    }

    public boolean isInternationalOrganisationsApplicable() {
        return internationalOrganisationsApplicable;
    }
}
