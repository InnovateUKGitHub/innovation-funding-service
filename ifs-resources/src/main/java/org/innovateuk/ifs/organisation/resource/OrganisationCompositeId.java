package org.innovateuk.ifs.organisation.resource;

import org.innovateuk.ifs.util.CompositeId;

/**
 * Class to enable the spring security to apply type information when applying security rules to entity ids.
 * In this case determine that the id in question relates to an organisation.
 */
public final class OrganisationCompositeId extends CompositeId {

    private OrganisationCompositeId(Long id) {
        super(id);
    }

    public static OrganisationCompositeId id(Long organisationId){
        return new OrganisationCompositeId(organisationId);
    }
}
