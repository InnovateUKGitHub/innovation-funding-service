package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.util.CompositeId;


/**
 * Class to enable the spring security to apply type information when applying security rules to entity ids.
 * In this case determine that the id in question relates to a user.
 */
public final class UserCompositeId extends CompositeId {

    private UserCompositeId(Long id) {
        super(id);
    }

    public static UserCompositeId id(Long userId){
        return new UserCompositeId(userId);
    }
}
