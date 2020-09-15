package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service that encompasses functions that relate to users and their roles
 */
public interface UsersRolesService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProcessRoleResource> getProcessRoleById(long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByIds(Long[] ids);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByApplicationId(long applicationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProcessRoleResource> getProcessRoleByUserIdAndApplicationId(long userId, final long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByUserId(long userId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<ProcessRoleResource>> getAssignableProcessRolesByApplicationId(long applicationId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'CHECK_USER_APPLICATION')")
    ServiceResult<Boolean> userHasApplicationForCompetition(long userId, long competitionId);
}