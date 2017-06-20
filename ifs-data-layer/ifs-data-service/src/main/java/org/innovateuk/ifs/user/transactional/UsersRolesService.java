package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service that encompasses functions that relate to users and their roles
 */
public interface UsersRolesService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProcessRoleResource> getProcessRoleById(final Long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByIds(final Long[] ids);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByApplicationId(final Long applicationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProcessRoleResource> getProcessRoleByUserIdAndApplicationId(final Long userId, final Long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByUserId(final Long userId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProcessRoleResource>> getAssignableProcessRolesByApplicationId(final Long applicationId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'CHECK_USER_APPLICATION')")
    ServiceResult<Boolean> userHasApplicationForCompetition(@P("userId")Long userId, Long competitionId);
}
