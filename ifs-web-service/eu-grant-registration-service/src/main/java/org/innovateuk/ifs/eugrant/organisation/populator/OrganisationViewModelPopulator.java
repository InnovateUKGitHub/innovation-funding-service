package org.innovateuk.ifs.eugrant.organisation.populator;

import org.innovateuk.ifs.eugrant.EuOrganisationResource;
import org.innovateuk.ifs.eugrant.organisation.viewmodel.OrganisationViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganisationViewModelPopulator {

    @Autowired
    private OrganisationSearchRestService searchRestService;

    public OrganisationViewModel populate(EuOrganisationResource organisation) {
        if (!organisation.getOrganisationType().isResearch() && organisation.getCompaniesHouseNumber() != null) {
            OrganisationSearchResult result = searchRestService.getOrganisation(organisation.getOrganisationType(), organisation.getCompaniesHouseNumber()).getSuccess();
            return new OrganisationViewModel(organisation.getOrganisationType(), organisation.getName(), organisation.getCompaniesHouseNumber(), result.getOrganisationAddress());
        } else {
            return new OrganisationViewModel(organisation.getOrganisationType(), organisation.getName());
        }
    }


}
