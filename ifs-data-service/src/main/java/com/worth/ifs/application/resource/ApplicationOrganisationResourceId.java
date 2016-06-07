package com.worth.ifs.application.resource;

import com.worth.ifs.user.domain.Organisation;

/**
 * Compound key containing {@link ApplicationResource} and {@link Organisation} ids. Used for securing services.
 */
public class ApplicationOrganisationResourceId {

    private long applicationId;
    private long organisationId;

    public ApplicationOrganisationResourceId(long applicationId, long organisationId) {
        this.applicationId = applicationId;
        this.organisationId = organisationId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(long organisationId) {
        this.organisationId = organisationId;
    }
}
