package com.worth.ifs.application.service;

import com.worth.ifs.user.domain.ProcessRole;

import java.util.List;

/**
 * Interface for CRUD operations on {@link ProcessRole} related data.
 */
public interface ProcessRoleService {
    ProcessRole findProcessRole(Long userId, Long applicationId);
    List<ProcessRole> findAssignableProcessRoles(Long applicationId);
    ProcessRole getById(Long id);
}
