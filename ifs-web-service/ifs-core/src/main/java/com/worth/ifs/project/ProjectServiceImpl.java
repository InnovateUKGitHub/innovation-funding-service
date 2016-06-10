package com.worth.ifs.project;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * A service for dealing with ProjectResources via the appropriate Rest services
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRestService projectRestService;

    @Override
    public List<ProjectUserResource> getProjectUsersForProject(Long projectId) {
        return projectRestService.getProjectUsersForProject(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectResource getById(Long projectId) {
        if (projectId == null) {
            return null;
        }

        return projectRestService.getProjectById(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectResource getByApplicationId(Long applicationId) {
        if(applicationId == null) {
            return null;
        }
        RestResult<ProjectResource> restResult = projectRestService.getByApplicationId(applicationId);
        if(restResult.isSuccess()){
            return restResult.getSuccessObject();
        } else {
            return null;
        }
    }

    @Override
    public void updateProjectManager(Long projectId, Long projectManagerUserId) {
        projectRestService.updateProjectManager(projectId, projectManagerUserId).getSuccessObjectOrThrowException();
    }

    @Override
    public void updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
        projectRestService.updateFinanceContact(projectId, organisationId, financeContactUserId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<List<ProjectResource>> findByUser(Long userId) {
        return projectRestService.findByUserId(userId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return projectRestService.updateProjectStartDate(projectId, projectStartDate).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateAddress(Long leadOrganisationId, Long projectId, AddressType addressType, AddressResource address) {
        return projectRestService.updateProjectAddress(leadOrganisationId, projectId, addressType, address).toServiceResult();
    }
}
