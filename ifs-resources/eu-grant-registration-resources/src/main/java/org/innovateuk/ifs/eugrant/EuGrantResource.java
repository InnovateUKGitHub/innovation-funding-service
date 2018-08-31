package org.innovateuk.ifs.eugrant;

import java.util.UUID;

/**
 * Resource for an EU grant registration.
 */
public class EuGrantResource {

    private UUID id;

    private EuOrganisationResource organisation;

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
}
