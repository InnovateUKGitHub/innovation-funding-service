package com.worth.ifs.application.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.commons.service.ServiceResult;

import com.worth.ifs.project.resource.ProjectResource;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for CRUD operations on {@link ProjectResource} related data.
 */
public interface ProjectService {
    ProjectResource getById(Long projectId);
    ServiceResult<List<ProjectResource>> findByUser(Long userId);
    ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);
    ServiceResult<Void> updateAddress(Long leadOrganisationId, Long projectId, AddressType addressType, AddressResource address);
    void updateProjectManager(Long projectId, Long projectManagerUserId);
}
