package org.innovateuk.ifs.registration.viewmodel;

import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

/**
 * View model for Organisation address
 */
public class OrganisationAddressViewModel {
    private OrganisationTypeResource organisationType;


    public OrganisationAddressViewModel(OrganisationTypeResource organisationType) {
        this.organisationType = organisationType;
    }

    public boolean isResearch() {
        return OrganisationTypeEnum.RESEARCH.getOrganisationTypeId().equals(organisationType.getId());
    }

    public String getOrganisationTypeName() {
        return organisationType.getName().toLowerCase();
    }
}
