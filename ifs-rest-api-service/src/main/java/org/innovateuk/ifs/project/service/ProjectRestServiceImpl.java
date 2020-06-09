package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectUserResourceList;

@Service
public class ProjectRestServiceImpl extends BaseRestService implements ProjectRestService {

    private final static String PROJECT_REST_URL = "/project/";

    @Override
    public RestResult<ProjectResource> getProjectById(long projectId) {
        return getWithRestResult(format(PROJECT_REST_URL + "%d", projectId), ProjectResource.class);
    }

    public RestResult<List<ProjectResource>> findByUserId(long userId) {
        return getWithRestResult(format(PROJECT_REST_URL + "user/%d", userId), projectResourceListType());
    }

    @Override
    public RestResult<List<ProjectUserResource>> getProjectUsersForProject(long projectId) {
        return getWithRestResult(format(PROJECT_REST_URL + "%d/project-users", projectId), projectUserResourceList());
    }

    @Override
    public RestResult<List<ProjectUserResource>> getDisplayProjectUsersForProject(long projectId) {
        return getWithRestResult(format(PROJECT_REST_URL + "%d/display-project-users", projectId), projectUserResourceList());
    }

    @Override
    public RestResult<ProjectResource> getByApplicationId(long applicationId) {
        return getWithRestResult(format(PROJECT_REST_URL + "application/%d", applicationId), ProjectResource.class);
    }

    @Override
    public RestResult<OrganisationResource> getOrganisationByProjectAndUser(long projectId, long userId) {
        return getWithRestResult(format(PROJECT_REST_URL + "%d/get-organisation-by-user/%d", projectId, userId), OrganisationResource.class);
    }

    @Override
    public RestResult<ProjectUserResource> getProjectManager(long projectId) {
        return getWithRestResult(format(PROJECT_REST_URL + "%d/project-manager", projectId), ProjectUserResource.class);
    }

    @Override
    public RestResult<ProjectUserResource> getProjectFinanceContact(long projectId) {
        return getWithRestResult(format(PROJECT_REST_URL + "%d/project-finance-contact", projectId), ProjectUserResource.class);
    }

    @Override
    public RestResult<ProjectResource> createProjectFromApplicationId(long applicationId) {
        return postWithRestResult(format(PROJECT_REST_URL + "create-project/application/%d", applicationId), ProjectResource.class);
    }

    @Override
    public RestResult<OrganisationResource> getLeadOrganisationByProject(long projectId) {
        return getWithRestResult(format(PROJECT_REST_URL + "%d/lead-organisation", projectId), OrganisationResource.class);
    }

    @Override
    public RestResult<Boolean> existsOnApplication(long projectId, long organisationId) {
        return getWithRestResult(format(PROJECT_REST_URL + "%d/user/%d/application-exists", projectId, organisationId), Boolean.class);
    }
}