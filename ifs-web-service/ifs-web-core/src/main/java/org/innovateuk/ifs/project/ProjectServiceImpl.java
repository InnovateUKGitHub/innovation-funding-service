package org.innovateuk.ifs.project;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNOT_GET_ANY_USERS_FOR_PROJECT;
import static org.innovateuk.ifs.commons.rest.RestResult.aggregate;
import static org.innovateuk.ifs.user.resource.Role.PARTNER;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * A service for dealing with ProjectResources via the appropriate Rest services
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectService projectService;

    @Override
    public List<ProjectUserResource> getProjectUsersForProject(Long projectId) {
        return projectRestService.getProjectUsersForProject(projectId).getSuccess();
    }

    @Override
    public ProjectResource getById(Long projectId) {
        if (projectId == null) {
            return null;
        }

        return projectRestService.getProjectById(projectId).getSuccess();
    }

    @Override
    public ProjectResource getByApplicationId(Long applicationId) {
        if(applicationId == null) {
            return null;
        }
        RestResult<ProjectResource> restResult = projectRestService.getByApplicationId(applicationId);
        if(restResult.isSuccess()){
            return restResult.getSuccess();
        } else {
            return null;
        }
    }

    @Override
    public ServiceResult<List<ProjectResource>> findByUser(Long userId) {
        return projectRestService.findByUserId(userId).toServiceResult();
    }

    @Override
    public OrganisationResource getLeadOrganisation(Long projectId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        if (project.getApplication() == null){
            return null;
        } else {
            Long id = project.getApplication();
            ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(project.getApplication());
            return organisationService.getOrganisationById(leadApplicantProcessRole.getOrganisationId());
        }
    }

    @Override
    public OrganisationResource getOrganisationByProjectAndUser(Long projectId, Long userId) {
        return projectRestService.getOrganisationByProjectAndUser(projectId, userId).getSuccess();
    }

    @Override
    public List<OrganisationResource> getPartnerOrganisationsForProject(Long projectId) {

        List<ProjectUserResource> projectUsers = getProjectUsersForProject(projectId);

        List<Long> organisationIds = removeDuplicates(simpleMap(projectUsers, ProjectUserResource::getOrganisation));
        List<RestResult<OrganisationResource>> organisationResults = simpleMap(organisationIds, organisationRestService::getOrganisationById);
        RestResult<List<OrganisationResource>> organisationResultsCombined = aggregate(organisationResults);

        return organisationResultsCombined.getSuccess();
    }

    @Override
    public List<ProjectUserResource> getLeadPartners(Long projectId) {
        List<ProjectUserResource> partnerUsers = getProjectUsersWithPartnerRole(projectId);
        OrganisationResource leadOrganisation = getLeadOrganisation(projectId);
        return simpleFilter(partnerUsers, projectUser -> projectUser.getOrganisation().equals(leadOrganisation.getId()));
    }

    @Override
    public List<ProjectUserResource> getPartners(Long projectId) {
        List<ProjectUserResource> partnerUsers = getProjectUsersWithPartnerRole(projectId);
        OrganisationResource leadOrganisation = getLeadOrganisation(projectId);
        return simpleFilter(partnerUsers, projectUser -> !(projectUser.getOrganisation().equals(leadOrganisation.getId())));
    }

    @Override
    public boolean isUserLeadPartner(Long projectId, Long userId) {
        return !simpleFilter(getLeadPartners(projectId), projectUser -> projectUser.getUser().equals(userId)).isEmpty();
    }

    @Override
    public List<ProjectUserResource> getProjectUsersWithPartnerRole(Long projectId) {
        List<ProjectUserResource> projectUsers = getProjectUsersForProject(projectId);
        return simpleFilter(projectUsers, pu -> PARTNER.getId() == pu.getRole());
    }

    @Override
    public Optional<ProjectUserResource> getProjectManager(Long projectId) {
        return projectRestService.getProjectManager(projectId).toServiceResult().getOptionalSuccessObject();
    }

    @Override
    public final Boolean isProjectManager(Long userId, Long projectId) {
        return getProjectManager(projectId).map(maybePM -> maybePM.isUser(userId)).orElse(false);
    }

    @Override
    public PartnerOrganisationResource getPartnerOrganisation(Long projectId, Long organisationId) {
        return projectRestService.getPartnerOrganisation(projectId, organisationId).getSuccess();
    }

    @Override
    public ServiceResult<ProjectResource> createProjectFromApplicationId(Long applicationId) {
        return projectRestService.createProjectFromApplicationId(applicationId).toServiceResult();
    }

    @Override
    public boolean userIsPartnerInOrganisationForProject(Long projectId, Long organisationId, Long userId) {
        if(userId == null) {
            return false;
        }

        List<ProjectUserResource> thisProjectUsers = getProjectUsersForProject(projectId);
        List<ProjectUserResource> projectUsersForOrganisation = simpleFilter(thisProjectUsers, user -> user.getOrganisation().equals(organisationId));
        List<ProjectUserResource> projectUsersForUserAndOrganisation = simpleFilter(projectUsersForOrganisation, user -> user.getUser().equals(userId));

        return !projectUsersForUserAndOrganisation.isEmpty();
    }

    @Override
    public Long getOrganisationIdFromUser(Long projectId, UserResource user) throws ForbiddenActionException {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        Optional<ProjectUserResource> projectUser = simpleFindFirst(projectUsers, pu ->
                user.getId().equals(pu.getUser()) && Role.PARTNER.getId() == pu.getRole());
        return projectUser.map(ProjectUserResource::getOrganisation).orElseThrow(() -> new ForbiddenActionException(CANNOT_GET_ANY_USERS_FOR_PROJECT.getErrorKey(), singletonList(projectId)));
    }

}
