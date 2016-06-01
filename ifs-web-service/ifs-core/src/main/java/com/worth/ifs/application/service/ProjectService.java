package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.project.resource.ProjectResource;

/**
 * Interface for CRUD operations on {@link ApplicationResource} related data.
 */
public interface ProjectService {
    ProjectResource getById(Long projectId);
}
