package com.worth.ifs.user.service;

import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link ProcessRole} related data.
 */
public interface ProcessRoleService {
    ProcessRoleResource findProcessRole(Long userId, Long applicationId);

    List<ProcessRoleResource> findProcessRolesByApplicationId(Long applicationId);

    Future<List<ProcessRoleResource>> findAssignableProcessRoles(Long applicationId);

    Future<ProcessRoleResource> getById(Long id);
}
