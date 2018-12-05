package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.FormOption;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * View model to support "Your organisation" page
 */
public class YourOrganisationViewModel {

    private boolean stateAidEligibility;

    public YourOrganisationViewModel(boolean stateAidEligibility) {
        this.stateAidEligibility = stateAidEligibility;
    }

    public List<FormOption> getOrganisationSizeOptions() {
        return simpleMap(OrganisationSize.values(), size -> new FormOption(size.getDescription(), size.name()));
    }

    public boolean isStateAidEligibility() {
        return stateAidEligibility;
    }
}
