package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.ProcessRole;

import java.util.List;

/**
 * Service that encompasses functions that relate to users and their roles
 */
public interface UsersRolesService {

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<ProcessRole> getProcessRoleById(final Long id);

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<List<ProcessRole>> getProcessRolesByApplicationId(final Long applicationId);

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<ProcessRole> getProcessRoleByUserIdAndApplicationId(final Long userId, final Long applicationId);

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<List<ProcessRole>> getProcessRolesByUserId(final Long userId);

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<List<ProcessRole>> getAssignableProcessRolesByApplicationId(final Long applicationId);
}
