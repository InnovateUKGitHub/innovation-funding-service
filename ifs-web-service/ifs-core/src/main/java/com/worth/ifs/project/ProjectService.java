package com.worth.ifs.project;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;

import java.util.List;

/**
 * A service for dealing with ProjectResources via the appropriate Rest services
 */
public interface ProjectService {

    List<ProjectUserResource> getProjectUsersForProject(Long projectId);

    ProjectResource getById(Long projectId);

    void updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId);

    void updateProjectManager(Long projectId, Long projectManagerUserId);
}
