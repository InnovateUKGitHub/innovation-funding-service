package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.method.P;

import java.util.List;

/**
 * Transactional and secure service for Project processing work
 */
public interface ProjectService {
    @NotSecured("TODO")
    ServiceResult<ProjectResource> getProjectById(@P("projectId") final Long projectId);
    @NotSecured("TODO")
    ServiceResult<List<ProjectResource>> findAll();
    @NotSecured("TODO")
    ServiceResult<ProjectResource> createProjectFromApplicationId(final Long applicationId);
}
