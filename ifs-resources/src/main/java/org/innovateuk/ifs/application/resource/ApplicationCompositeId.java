package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.util.CompositeId;

/**
 * TODO
 */
public final class ApplicationCompositeId extends CompositeId {

    private ApplicationCompositeId(Long id) {
        super(id);
    }

    public static ApplicationCompositeId id(Long applicationId){
        return new ApplicationCompositeId(applicationId);
    }
}
