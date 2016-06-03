package com.worth.ifs.project.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProjectRestServiceImpl extends BaseRestService implements ProjectRestService {

    @Value("${ifs.data.service.rest.project}")
    String projectRestURL;

    @Override
    public RestResult<ProjectResource> getProjectById(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId, ProjectResource.class);
    }

    @Override
    public RestResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/startdate?projectStartDate=" + projectStartDate, Void.class);
    }

	@Override
	public RestResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
		return postWithRestResult(projectRestURL + "/" + projectId + "/finance-contact/" + organisationId + "?financeContact=" + financeContactUserId, Void.class);
	}
}
