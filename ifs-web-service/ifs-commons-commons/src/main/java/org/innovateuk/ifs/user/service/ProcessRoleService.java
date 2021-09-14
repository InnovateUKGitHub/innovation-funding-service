package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link ProcessRoleResource} related data.
 */
public interface ProcessRoleService {

    List<ProcessRoleResource> findAssignableProcessRoles(Long applicationId);

    Future<ProcessRoleResource> getById(Long id);
}
