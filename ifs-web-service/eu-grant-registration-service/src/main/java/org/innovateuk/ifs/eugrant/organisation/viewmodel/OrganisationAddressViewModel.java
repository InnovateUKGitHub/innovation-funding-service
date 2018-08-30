package org.innovateuk.ifs.eugrant.organisation.viewmodel;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

/**
 * View model for Organisation address
 */
public class OrganisationAddressViewModel {
    private OrganisationTypeResource organisationType;

    public OrganisationAddressViewModel(OrganisationTypeResource organisationType) {
        this.organisationType = organisationType;
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

}