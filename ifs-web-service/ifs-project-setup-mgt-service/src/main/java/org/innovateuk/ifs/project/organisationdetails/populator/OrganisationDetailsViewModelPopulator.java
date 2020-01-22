package org.innovateuk.ifs.project.organisationdetails.populator;

import java.util.List;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.OrganisationDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganisationDetailsViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    public OrganisationDetailsViewModel populate(long competitionId, long projectId, long organisationId) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        AddressResource address = getOrganisationAddress(organisation);

        return new OrganisationDetailsViewModel(project,
            competitionId,
            organisation,
            address,
            hasPartners(projectId)
        );
    }

    private AddressResource getOrganisationAddress(OrganisationResource organisation) {
        AddressResource emptyAddress = new AddressResource("", "", "", "", "", "");

        List<OrganisationAddressResource> organisationAddressResources = organisation.getAddresses();
        if(organisationAddressResources.isEmpty()) {
            return emptyAddress;
        }

        return organisationAddressResources.get(0).getAddress();
    }

    private boolean hasPartners(Long projectId) {
        return partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess().size() > 1;
    }
}
