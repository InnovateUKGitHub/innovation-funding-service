package org.innovateuk.ifs.registration.viewmodel;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

import java.util.List;

/**
 * View model for Organisation creation - choosing organisation type
 */
public class ContributorOrganisationTypeViewModel {
    private final List<OrganisationTypeResource> types;

    public ContributorOrganisationTypeViewModel(List<OrganisationTypeResource> types) {
        this.types = types;
    }

    public List<OrganisationTypeResource> getTypes() {
        return types;
    }

}
