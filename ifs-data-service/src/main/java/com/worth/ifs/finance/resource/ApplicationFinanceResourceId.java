package com.worth.ifs.finance.resource;

import java.io.Serializable;


/**
 * Compound class for holding the application finance resource keys
 */
public class ApplicationFinanceResourceId implements Serializable {
    Long applicationId;
    Long organisationId;

    public ApplicationFinanceResourceId(Long applicationId, Long organisationId) {
        this.applicationId = applicationId;
        this.organisationId = organisationId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

}
