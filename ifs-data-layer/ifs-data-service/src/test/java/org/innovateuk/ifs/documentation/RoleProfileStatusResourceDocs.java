package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;

import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;

public class RoleProfileStatusResourceDocs {

    public static final RoleProfileStatusResourceBuilder roleProfileStatusResourceBuilder = newRoleProfileStatusResource()
            .withUserId(1L)
            .withRoleProfileState(RoleProfileState.ACTIVE)
            .withProfileRole(ProfileRole.ASSESSOR)
            .withDescription("Description");
}
