package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.user.resource.ProcessRoleResource;

import java.util.List;

/**
 * Service that encompasses functions that relate to users and their roles
 */
public interface UsersRolesService {

    @NotSecured(value = "TODO DW - INFUND-1555 - add correct permissions", mustBeSecuredByOtherServices = false)
    ServiceResult<ProcessRoleResource> getProcessRoleById(final Long id);

    @NotSecured(value = "TODO DW - INFUND-1555 - add correct permissions", mustBeSecuredByOtherServices = false)
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByIds(final Long[] ids);

    @NotSecured(value = "TODO DW - INFUND-1555 - add correct permissions", mustBeSecuredByOtherServices = false)
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByApplicationId(final Long applicationId);

    @NotSecured(value = "TODO DW - INFUND-1555 - add correct permissions", mustBeSecuredByOtherServices = false)
    ServiceResult<ProcessRoleResource> getProcessRoleByUserIdAndApplicationId(final Long userId, final Long applicationId);

    @NotSecured(value = "TODO DW - INFUND-1555 - add correct permissions", mustBeSecuredByOtherServices = false)
    ServiceResult<List<ProcessRoleResource>> getProcessRolesByUserId(final Long userId);

    @NotSecured(value = "TODO DW - INFUND-1555 - add correct permissions", mustBeSecuredByOtherServices = false)
    ServiceResult<List<ProcessRoleResource>> getAssignableProcessRolesByApplicationId(final Long applicationId);
}
