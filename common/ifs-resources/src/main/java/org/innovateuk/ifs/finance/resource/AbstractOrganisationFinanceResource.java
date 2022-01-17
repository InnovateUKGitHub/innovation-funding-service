package org.innovateuk.ifs.finance.resource;

/**
 * A class used to capture "Your organisation" information including a growth table
 */
public abstract class AbstractOrganisationFinanceResource {

    private OrganisationSize organisationSize;

    protected AbstractOrganisationFinanceResource() {}

    protected AbstractOrganisationFinanceResource(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }
}