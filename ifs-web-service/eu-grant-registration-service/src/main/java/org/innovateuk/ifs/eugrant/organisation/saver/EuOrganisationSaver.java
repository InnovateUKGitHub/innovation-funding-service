package org.innovateuk.ifs.eugrant.organisation.saver;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuOrganisationResource;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationForm;
import org.innovateuk.ifs.eugrant.organisation.service.EuOrganisationCookieService;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuOrganisationSaver {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    @Autowired
    private EuOrganisationCookieService organisationCookieService;

    @Autowired
    private OrganisationSearchRestService organisationSearchRestService;

    public ServiceResult<Void> save(EuOrganisationForm organisationForm, EuOrganisationType type) {
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
        return euGrantCookieService.save(euGrant)
                .andOnSuccessReturnVoid(organisationCookieService::clear);
    }
}
