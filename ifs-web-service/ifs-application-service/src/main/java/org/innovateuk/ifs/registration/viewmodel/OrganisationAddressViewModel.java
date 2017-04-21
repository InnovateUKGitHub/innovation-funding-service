package org.innovateuk.ifs.registration.viewmodel;

import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

/**
 * View model for Organisation address
 */
public class OrganisationAddressViewModel {
    private OrganisationTypeResource organisationType;
    private boolean isLeadApplicant;

    public OrganisationAddressViewModel(OrganisationTypeResource organisationType, boolean isLeadApplicant) {
        this.organisationType = organisationType;
        this.isLeadApplicant = isLeadApplicant;
    }

    public boolean isResearch() {
        return OrganisationTypeEnum.RESEARCH.getOrganisationTypeId().equals(organisationType.getId());
    }

    public String getOrganisationTypeName() {
        return organisationType.getName().toLowerCase();
    }

    public boolean isLeadApplicant() {
        return isLeadApplicant;
    }
}
