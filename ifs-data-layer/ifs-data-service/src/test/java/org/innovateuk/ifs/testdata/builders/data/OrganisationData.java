package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;

/**
 * Running data context for generating Organisations
 */
public class OrganisationData {

    private OrganisationResource organisation;

    public OrganisationResource getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationResource organisation) {
        this.organisation = organisation;
    }
}
