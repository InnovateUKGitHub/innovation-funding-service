package com.worth.ifs.application.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * This class contains methods to retrieve and store {@link ProjectResource} related data,
 * through the RestService {@link ProjectRestService}.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRestService projectRestService;

    @Override
    public ProjectResource getById(Long projectId) {
        if (projectId == null) {
            return null;
        }

        return projectRestService.getProjectById(projectId).getSuccessObjectOrThrowException();
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

	@Override
	public void updateProjectManager(Long projectId, Long projectManagerUserId) {
		projectRestService.updateProjectManager(projectId, projectManagerUserId).getSuccessObjectOrThrowException();
	}
}
