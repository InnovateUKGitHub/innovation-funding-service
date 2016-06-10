package com.worth.ifs.project.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRestService {
    RestResult<ProjectResource> getProjectById(Long projectId);
    RestResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId);
    RestResult<Void> updateProjectAddress(long leadOrganisationId, long projectId, AddressType addressType, AddressResource address);
    RestResult<List<ProjectResource>> findByUserId(long userId);
    RestResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);
    RestResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId);
    RestResult<List<ProjectUserResource>> getProjectUsersForProject(Long projectId);
}
