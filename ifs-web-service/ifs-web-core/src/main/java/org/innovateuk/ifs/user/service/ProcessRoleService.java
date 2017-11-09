package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link ProcessRoleResource} related data.
 */
public interface ProcessRoleService {
    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ProcessRoleResource findProcessRole(Long userId, Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProcessRoleResource> findProcessRolesByApplicationId(Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Future<List<ProcessRoleResource>> findAssignableProcessRoles(Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Future<ProcessRoleResource> getById(Long id);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProcessRoleResource> getByApplicationId(Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProcessRoleResource> getByUserId(Long userId);
}
