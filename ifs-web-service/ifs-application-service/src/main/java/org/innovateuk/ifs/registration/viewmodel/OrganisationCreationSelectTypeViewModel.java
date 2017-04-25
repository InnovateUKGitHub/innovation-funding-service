package org.innovateuk.ifs.registration.viewmodel;

import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

import java.util.List;

/**
 * View model for Organisation creation lead applicant - choosing organisation type
 */
public class OrganisationCreationSelectTypeViewModel {
    private List<OrganisationTypeResource> types;

    public OrganisationCreationSelectTypeViewModel(List<OrganisationTypeResource> types) {
        this.types = types;
    }

    public List<OrganisationTypeResource> getTypes() {
        return types;
    }

    public void setTypes(List<OrganisationTypeResource> types) {
        this.types = types;
    }

}
