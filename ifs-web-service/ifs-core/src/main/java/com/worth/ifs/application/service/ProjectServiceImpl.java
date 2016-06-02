package com.worth.ifs.application.service;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link ProjectResource} related data,
 * through the RestService {@link ProjectRestService}.
 */
// TODO DW - INFUND-1555 - get service calls to return rest responses
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
	public void updateProjectManager(Long projectId, Long projectManagerUserId) {
		projectRestService.updateProjectManager(projectId, projectManagerUserId).getSuccessObjectOrThrowException();
	}
}
