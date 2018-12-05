package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationService;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationViewModel
 */
@Component
public class YourOrganisationViewModelPopulator {

    private YourOrganisationService yourOrganisationService;

    public YourOrganisationViewModelPopulator(YourOrganisationService yourOrganisationService) {
        this.yourOrganisationService = yourOrganisationService;
    }

    public YourOrganisationViewModel populate(long applicationId, long organisationId) {
        boolean showStateAidAgreement = yourOrganisationService.isShowStateAidAgreement(applicationId, organisationId).getSuccess();
        return new YourOrganisationViewModel(showStateAidAgreement);
    }
}
