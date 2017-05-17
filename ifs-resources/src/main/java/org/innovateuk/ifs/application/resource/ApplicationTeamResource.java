package org.innovateuk.ifs.application.resource;

import java.util.List;

/**
 * Application Team data transfer object
 */
public class ApplicationTeamResource {
    ApplicationTeamOrganisationResource leadOrganisation;
    List<ApplicationTeamOrganisationResource> partnerOrganisations;

    public ApplicationTeamOrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(ApplicationTeamOrganisationResource leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public List<ApplicationTeamOrganisationResource> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    public void setPartnerOrganisations(List<ApplicationTeamOrganisationResource> partnerOrganisations) {
        this.partnerOrganisations = partnerOrganisations;
    }
}
