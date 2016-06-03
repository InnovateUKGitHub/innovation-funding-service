package com.worth.ifs.project.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.ProjectResource;

import java.time.LocalDate;

public interface ProjectRestService {
    RestResult<ProjectResource> getProjectById(Long projectId);
    RestResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);
    RestResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId);
}
