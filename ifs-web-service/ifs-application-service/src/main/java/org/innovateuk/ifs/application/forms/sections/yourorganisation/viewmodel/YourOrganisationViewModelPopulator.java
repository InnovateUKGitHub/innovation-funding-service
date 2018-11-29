package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationViewModel
 */
@Component
public class YourOrganisationViewModelPopulator {

    public YourOrganisationViewModel populate() {
        return new YourOrganisationViewModel();
    }
}
