package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.util.CompositeId;

/**
 * Class to enable the spring security to apply type information when applying security rules to entity ids.
 * In this case determine that the id in question relates to an application.
 */
public final class ApplicationCompositeId extends CompositeId {

    private ApplicationCompositeId(Long id) {
        super(id);
    }

    public static ApplicationCompositeId id(Long applicationId){
        return new ApplicationCompositeId(applicationId);
    }
}
