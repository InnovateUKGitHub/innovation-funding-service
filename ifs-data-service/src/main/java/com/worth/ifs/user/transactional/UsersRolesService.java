package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.resource.ProcessRoleResource;

import java.util.List;

/**
 * Service that encompasses functions that relate to users and their roles
 */
public interface UsersRolesService {

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<ProcessRoleResource> getProcessRoleById(final Long id);

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByApplicationId(final Long applicationId);

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<ProcessRoleResource> getProcessRoleByUserIdAndApplicationId(final Long userId, final Long applicationId);

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByUserId(final Long userId);

    @NotSecured("TODO DW - INFUND-1555 - add correct permissions")
    ServiceResult<List<ProcessRoleResource>> getAssignableProcessRolesByApplicationId(final Long applicationId);
}
