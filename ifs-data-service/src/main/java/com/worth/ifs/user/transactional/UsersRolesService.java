package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;

import java.util.List;

/**
 * Service that encompasses functions that relate to users and their roles
 */
public interface UsersRolesService {

    ServiceResult<ProcessRole> getProcessRoleById(final Long id);

    ServiceResult<List<ProcessRole>> getProcessRolesByApplicationId(final Long applicationId);

    ServiceResult<ProcessRole> getProcessRoleByUserIdAndApplicationId(final Long userId, final Long applicationId);

    ServiceResult<List<ProcessRole>> getProcessRolesByUserId(final Long userId);

    ServiceResult<List<ProcessRole>> getAssignableProcessRolesByApplicationId(final Long applicationId);
}
