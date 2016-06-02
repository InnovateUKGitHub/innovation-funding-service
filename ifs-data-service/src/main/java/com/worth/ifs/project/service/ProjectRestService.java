package com.worth.ifs.project.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.ProjectResource;

import java.time.LocalDate;

public interface ProjectRestService {
    RestResult<ProjectResource> getProjectById(Long projectId);
    RestResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId);
    RestResult<Void> updateProjectStartDate(long projectId, LocalDate projectStartDate);
}
