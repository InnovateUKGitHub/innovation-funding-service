package org.innovateuk.ifs.admin.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.user.resource.UserCompositeId;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class UserLookupStrategy {

    @PermissionEntityLookupStrategy
    public UserCompositeId getApplicationCompositeId(Long userId) {
        return UserCompositeId.id(userId);
    }
}
