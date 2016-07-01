package com.worth.ifs.project.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRestService {
    RestResult<ProjectResource> getProjectById(Long projectId);
    RestResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId);
    RestResult<Void> updateProjectAddress(long leadOrganisationId, long projectId, OrganisationAddressType addressType, AddressResource address);
    RestResult<List<ProjectResource>> findByUserId(long userId);
    RestResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);
    RestResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId);
    RestResult<List<ProjectUserResource>> getProjectUsersForProject(Long projectId);
    RestResult<ProjectResource> getByApplicationId(Long applicationId);
    RestResult<Void> setApplicationDetailsSubmitted(Long projectId);
    RestResult<Boolean> isSubmitAllowed(Long projectId);
    RestResult<MonitoringOfficerResource> getMonitoringOfficerForProject(Long projectId);
    RestResult<Void> updateMonitoringOfficer(Long projectId, String firstName, String lastName, String emailAddress, String phoneNumber);
}
