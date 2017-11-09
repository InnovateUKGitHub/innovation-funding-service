package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link ProcessRoleResource} related data.
 */
public interface ProcessRoleService {
    @NotSecured("Not currently secured")
    ProcessRoleResource findProcessRole(Long userId, Long applicationId);

    @NotSecured("Not currently secured")
    List<ProcessRoleResource> findProcessRolesByApplicationId(Long applicationId);

    @NotSecured("Not currently secured")
    Future<List<ProcessRoleResource>> findAssignableProcessRoles(Long applicationId);

    @NotSecured("Not currently secured")
    Future<ProcessRoleResource> getById(Long id);

    @NotSecured("Not currently secured")
    List<ProcessRoleResource> getByApplicationId(Long applicationId);

    @NotSecured("Not currently secured")
    List<ProcessRoleResource> getByUserId(Long userId);
}
