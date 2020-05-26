package org.innovateuk.ifs.organisation.viewmodel;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

import java.util.List;

/**
 * View model for Organisation creation lead applicant - choosing organisation type
 */
public class OrganisationCreationSelectTypeViewModel {
    private final List<OrganisationTypeResource> types;
    private final boolean leadJourney;

    public OrganisationCreationSelectTypeViewModel(List<OrganisationTypeResource> types, boolean leadJourney) {
        this.types = types;
        this.leadJourney = leadJourney;
    }

    public List<OrganisationTypeResource> getTypes() {
        return types;
    }

    public boolean isLeadJourney() {
        return leadJourney;
    }
}
