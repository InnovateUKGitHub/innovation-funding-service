package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.ProjectResource;

import java.time.LocalDate;

/**
 * Interface for CRUD operations on {@link ApplicationResource} related data.
 */
public interface ProjectService {
    ProjectResource getById(Long projectId);
    ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);
    ServiceResult<Void> updateAddress(Long projectId, Long addressId);

}
