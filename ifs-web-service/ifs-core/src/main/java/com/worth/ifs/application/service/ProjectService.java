package com.worth.ifs.application.service;

import com.worth.ifs.project.resource.ProjectResource;

/**
 * Interface for CRUD operations on {@link ProjectResource} related data.
 */
public interface ProjectService {
    ProjectResource getById(Long projectId);
    
    void updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId);
}
