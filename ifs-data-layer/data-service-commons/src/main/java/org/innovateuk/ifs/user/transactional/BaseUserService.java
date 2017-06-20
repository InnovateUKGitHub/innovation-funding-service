package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

/**
 * A Service that covers basic operations concerning Users
 */
public interface BaseUserService {

    @NotSecured(value = "This UID method is needed prior to being able to put a User on the SecurityContext, and so it cannot be secured itself", mustBeSecuredByOtherServices = false)
    ServiceResult<UserResource> getUserResourceByUid(final String uid);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<UserResource> getUserById(final Long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<UserResource>> findAll();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<UserResource>> findByProcessRole(UserRoleType roleType);
}
