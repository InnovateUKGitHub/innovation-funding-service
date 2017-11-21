package org.innovateuk.ifs.organisation.resource;

import org.innovateuk.ifs.util.CompositeId;

/**
 * TODO
 */
public final class OrganisationCompositeId extends CompositeId {

    private OrganisationCompositeId(Long id) {
        super(id);
    }

    public static OrganisationCompositeId id(Long organisationId){
        return new OrganisationCompositeId(organisationId);
    }
}
