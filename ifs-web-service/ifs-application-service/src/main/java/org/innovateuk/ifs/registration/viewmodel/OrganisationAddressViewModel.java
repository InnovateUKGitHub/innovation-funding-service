package org.innovateuk.ifs.registration.viewmodel;

import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

/**
 * View model for Organisation address
 */
public class OrganisationAddressViewModel {
    private OrganisationTypeResource organisationType;
    private Boolean isLead;

    public OrganisationAddressViewModel(OrganisationTypeResource organisationType, Boolean isLead) {
        this.organisationType = organisationType;
        this.isLead = isLead;
    }

    public boolean isResearch() {
        return OrganisationTypeEnum.RESEARCH.getId().equals(organisationType.getId());
    }

    public String getOrganisationTypeName() {
        return organisationType.getName().toLowerCase();
    }

    public Boolean isShowOrgType() {
        return !isLead;
    }
}
