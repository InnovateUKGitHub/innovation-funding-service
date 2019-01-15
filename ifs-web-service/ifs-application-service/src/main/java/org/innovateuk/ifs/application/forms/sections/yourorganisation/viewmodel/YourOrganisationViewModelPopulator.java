package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationRestService;
import org.springframework.stereotype.Component;

/**
 * A populator to build a YourOrganisationViewModel
 */
@Component
public class YourOrganisationViewModelPopulator {

    private YourOrganisationRestService yourOrganisationRestService;

    public YourOrganisationViewModelPopulator(YourOrganisationRestService yourOrganisationRestService) {
        this.yourOrganisationRestService = yourOrganisationRestService;
    }

    public YourOrganisationViewModel populate(long applicationId, long organisationId) {

        boolean showStateAidAgreement =
                yourOrganisationRestService.isShowStateAidAgreement(applicationId, organisationId).getSuccess();

        return new YourOrganisationViewModel(showStateAidAgreement);
    }
}
