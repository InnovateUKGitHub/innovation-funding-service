package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link RoleProfileStatus} data.
 */
public interface RoleProfileStatusService {

    @SecuredBySpring(value = "UPDATE_USER_STATUS", description = "Only comp admin, project finance or IFS admin can update a users status")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<Void> updateUserStatus(long userId, RoleProfileStatusResource roleProfileStatusResource);

    @SecuredBySpring(value = "RETRIEVE_USER_STATUS", description = "Only comp admin, project finance or IFS admin can retrieve a users status")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support')")
    ServiceResult<List<RoleProfileStatusResource>> findByUserId(long userId);

    @SecuredBySpring(value = "RETRIEVE_USER_STATUS", description = "Only comp admin, project finance or IFS admin can retrieve a users status")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support')")
    ServiceResult<RoleProfileStatusResource> findByUserIdAndProfileRole(long userId, ProfileRole profileRole);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<UserPageResource> findByRoleProfile(RoleProfileState state, ProfileRole profileRole, String filter, Pageable pageable);
}
