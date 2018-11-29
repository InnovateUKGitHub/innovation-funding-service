package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.finance.resource.OrganisationSize;

/**
 * View model to support "Your organisation" page
 */
public class YourOrganisationViewModel {

    public OrganisationSize[] getOrganisationSizeOptions() {
        return OrganisationSize.values();
    }
}
