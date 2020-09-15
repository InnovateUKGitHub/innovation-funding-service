package org.innovateuk.ifs.organisation.viewmodel;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

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
        return OrganisationTypeEnum.RESEARCH.getId() == organisationType.getId();
    }

    public OrganisationTypeResource getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationTypeResource organisationType) {
        this.organisationType = organisationType;
    }

    public String getOrganisationTypeName() {
        return organisationType.getName().toLowerCase();
    }

    public boolean isShowOrgType() {
        return true;
    }
}