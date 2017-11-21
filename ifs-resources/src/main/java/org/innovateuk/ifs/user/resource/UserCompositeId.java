package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.util.CompositeId;


/**
 * TODO 
 */
public final class UserCompositeId extends CompositeId {

    private UserCompositeId(Long id) {
        super(id);
    }

    public static UserCompositeId id(Long userId){
        return new UserCompositeId(userId);
    }
}
