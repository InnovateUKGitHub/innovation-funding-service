package com.worth.ifs.user.service;

import com.worth.ifs.user.domain.ProcessRole;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link ProcessRole} related data.
 */
public interface ProcessRoleService {
    ProcessRole findProcessRole(Long userId, Long applicationId);

    List<ProcessRole> findProcessRolesByApplicationId(Long applicationId);

    Future<List<ProcessRole>> findAssignableProcessRoles(Long applicationId);

    Future<ProcessRole> getById(Long id);
}
