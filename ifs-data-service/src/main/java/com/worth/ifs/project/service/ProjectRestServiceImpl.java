package com.worth.ifs.project.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.project.resource.ProjectResource;
import org.springframework.stereotype.Service;

@Service
public class ProjectRestServiceImpl extends BaseRestService implements ProjectRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<ProjectResource> getProjectById(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId, ProjectResource.class);
    }
}
