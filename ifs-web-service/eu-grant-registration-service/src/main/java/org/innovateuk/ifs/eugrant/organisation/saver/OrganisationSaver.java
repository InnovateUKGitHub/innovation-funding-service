package org.innovateuk.ifs.eugrant.organisation.saver;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuOrganisationResource;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.form.OrganisationForm;
import org.innovateuk.ifs.eugrant.service.EuGrantCookieService;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Component
public class OrganisationSaver {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    @Autowired
    private OrganisationSearchRestService organisationSearchRestService;

    public ServiceResult<Void> save(OrganisationForm organisationForm, EuOrganisationType type) {
        EuGrantResource euGrant = euGrantCookieService.get();
        EuOrganisationResource euOrganisation = new EuOrganisationResource();
        euOrganisation.setOrganisationType(type);

        if (organisationForm.isManualEntry()) {
            euOrganisation.setName(organisationForm.getOrganisationName());
        } else {
            OrganisationSearchResult searchResult = organisationSearchRestService.getOrganisation(type, organisationForm.getSelectedOrganisationId()).getSuccess();
            euOrganisation.setName(searchResult.getName());
            if (!type.isResearch()) {
                euOrganisation.setCompaniesHouseNumber(organisationForm.getSelectedOrganisationId());
            }
        }

        euGrant.setOrganisation(euOrganisation);
        euGrantCookieService.save(euGrant);
        return serviceSuccess();
    }
}
