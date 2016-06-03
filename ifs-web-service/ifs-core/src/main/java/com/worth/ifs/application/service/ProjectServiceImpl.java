package com.worth.ifs.application.service;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link ProjectResource} related data,
 * through the RestService {@link ProjectRestService}.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRestService projectRestRestService;

    @Override
    public ProjectResource getById(Long projectId) {
        if (projectId == null) {
            return null;
        }

        return projectRestRestService.getProjectById(projectId).getSuccessObjectOrThrowException();
    }

	@Override
	public void updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
		projectRestRestService.updateFinanceContact(projectId, organisationId, financeContactUserId).getSuccessObjectOrThrowException();
	}
}
