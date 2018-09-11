package org.innovateuk.ifs.eugrant.organisation.populator;

import org.innovateuk.ifs.eugrant.EuOrganisationResource;
import org.innovateuk.ifs.eugrant.organisation.viewmodel.EuOrganisationViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuOrganisationViewModelPopulator {

    @Autowired
    private OrganisationSearchRestService searchRestService;

    public EuOrganisationViewModel populate(EuOrganisationResource organisation) {
        if (!organisation.getOrganisationType().isResearch() && organisation.getCompaniesHouseNumber() != null) {
            OrganisationSearchResult result = searchRestService.getOrganisation(organisation.getOrganisationType(), organisation.getCompaniesHouseNumber()).getSuccess();
            return new EuOrganisationViewModel(organisation.getOrganisationType(), organisation.getName(), organisation.getCompaniesHouseNumber(), result.getOrganisationAddress());
        } else {
            return new EuOrganisationViewModel(organisation.getOrganisationType(), organisation.getName());
        }
    }


}
