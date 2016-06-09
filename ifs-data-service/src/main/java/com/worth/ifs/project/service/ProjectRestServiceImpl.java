package com.worth.ifs.project.service;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.project.resource.ProjectResource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;

@Service
public class ProjectRestServiceImpl extends BaseRestService implements ProjectRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<ProjectResource> getProjectById(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId, ProjectResource.class);
    }

	@Override
	public RestResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId) {
		return postWithRestResult(projectRestURL + "/" + projectId + "/project-manager/" + projectManagerUserId, Void.class);
	}
    @Override
    public RestResult<Void> updateProjectStartDate(long projectId, LocalDate projectStartDate) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/startdate?projectStartDate=" + projectStartDate, Void.class);
    }

    @Override
    public RestResult<Void> updateProjectAddress(long leadOrganisationId, long projectId, AddressType addressType, AddressResource address) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/address?addressType=" + addressType.name() + "&leadOrganisationId=" + leadOrganisationId, address, Void.class);
    }

    @Override
    public RestResult<List<ProjectResource>> findByUserId(long userId) {
        return getWithRestResult(projectRestURL + "/user/" + userId, projectResourceListType());
    }
}
