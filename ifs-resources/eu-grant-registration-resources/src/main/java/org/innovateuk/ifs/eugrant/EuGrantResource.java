package org.innovateuk.ifs.eugrant;

import java.util.UUID;

/**
 * Resource for an EU grant registration.
 */
public class EuGrantResource {

    private UUID id;

    private EuOrganisationResource organisation;

    private boolean organisationComplete;

    private boolean contactComplete;

    private boolean fundingComplete;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public EuOrganisationResource getOrganisation() {
        return organisation;
    }

    public void setOrganisation(EuOrganisationResource organisation) {
        this.organisation = organisation;
    }

    public boolean isOrganisationComplete() {
        return organisationComplete;
    }

    public void setOrganisationComplete(boolean organisationComplete) {
        this.organisationComplete = organisationComplete;
    }

    public boolean isContactComplete() {
        return contactComplete;
    }

    public void setContactComplete(boolean contactComplete) {
        this.contactComplete = contactComplete;
    }

    public boolean isFundingComplete() {
        return fundingComplete;
    }

    public void setFundingComplete(boolean fundingComplete) {
        this.fundingComplete = fundingComplete;
    }
}
