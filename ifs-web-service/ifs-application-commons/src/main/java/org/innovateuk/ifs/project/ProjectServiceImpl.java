package org.innovateuk.ifs.project;

import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNOT_GET_ANY_USERS_FOR_PROJECT;
import static org.innovateuk.ifs.commons.rest.RestResult.aggregate;
import static org.innovateuk.ifs.user.resource.Role.FINANCE_CONTACT;
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
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectService projectService;

    @Override
    public List<ProjectUserResource> getProjectUsersForProject(long projectId) {
        return projectRestService.getProjectUsersForProject(projectId).getSuccess();
    }

    @Override
    public List<ProjectUserResource> getDisplayProjectUsersForProject(long projectId) {
        return projectRestService.getDisplayProjectUsersForProject(projectId).getSuccess();
    }

    @Override
    public ProjectResource getById(long projectId) {
        return projectRestService.getProjectById(projectId).getSuccess();
    }

    @Override
    public ProjectResource getByApplicationId(long applicationId) {
        RestResult<ProjectResource> restResult = projectRestService.getByApplicationId(applicationId);
        if(restResult.isSuccess()){
            return restResult.getSuccess();
        } else {
            return null;
        }
    }

    @Override
    public OrganisationResource getLeadOrganisation(long projectId) {
        return projectRestService.getLeadOrganisationByProject(projectId).getSuccess();
    }

    @Override
    public List<OrganisationResource> getPartnerOrganisationsForProject(long projectId) {

        List<PartnerOrganisationResource> partnerOrganisationResources = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();

        List<Long> organisationIds = removeDuplicates(simpleMap(partnerOrganisationResources, PartnerOrganisationResource::getOrganisation));
        List<RestResult<OrganisationResource>> organisationResults = simpleMap(organisationIds, organisationId -> organisationRestService.getOrganisationById(organisationId));
        RestResult<List<OrganisationResource>> organisationResultsCombined = aggregate(organisationResults);

        return organisationResultsCombined.getSuccess();
    }

    @Override
    public List<ProjectUserResource> getLeadPartners(long projectId) {
        List<ProjectUserResource> partnerUsers = getProjectUsersWithPartnerRole(projectId);
        OrganisationResource leadOrganisation = getLeadOrganisation(projectId);
        return simpleFilter(partnerUsers, projectUser -> projectUser.getOrganisation().equals(leadOrganisation.getId()));
    }

    @Override
    public List<ProjectUserResource> getPartners(long projectId) {
        List<ProjectUserResource> partnerUsers = getProjectUsersWithPartnerRole(projectId);
        OrganisationResource leadOrganisation = getLeadOrganisation(projectId);
        return simpleFilter(partnerUsers, projectUser -> !(projectUser.getOrganisation().equals(leadOrganisation.getId())));
    }

    @Override
    public boolean isUserLeadPartner(long projectId, long userId) {
        return simpleAnyMatch(getLeadPartners(projectId),
                              projectUser -> projectUser.getUser().equals(userId));
    }

    @Override
    public List<ProjectUserResource> getProjectUsersWithPartnerRole(long projectId) {
        List<ProjectUserResource> projectUsers = getProjectUsersForProject(projectId);
        return simpleFilter(projectUsers, pu -> PARTNER.getId() == pu.getRole());
    }

    @Override
    public Optional<ProjectUserResource> getProjectManager(long projectId) {
        return projectRestService.getProjectManager(projectId).toServiceResult().getOptionalSuccessObject();
    }

    @Override
    public final Boolean isProjectManager(long userId, long projectId) {
        return getProjectManager(projectId).map(maybePM -> maybePM.isUser(userId)).orElse(false);
    }

    @Override
    public Boolean isProjectFinanceContact(long userId, long projectId) {
        return projectRestService.getProjectFinanceContact(projectId).getOptionalSuccessObject()
                .map(pu -> pu.isUser(userId)).orElse(false);
    }

    @Override
    public boolean userIsPartnerInOrganisationForProject(long projectId, long organisationId, long userId) {
        List<ProjectUserResource> thisProjectUsers = getProjectUsersForProject(projectId);
        List<ProjectUserResource> projectUsersForOrganisation = simpleFilter(thisProjectUsers, user -> user.getOrganisation().equals(organisationId));
        List<ProjectUserResource> projectUsersForUserAndOrganisation = simpleFilter(projectUsersForOrganisation, user -> user.getUser().equals(userId));

        return !projectUsersForUserAndOrganisation.isEmpty();

    }

    @Override
    public Long getOrganisationIdFromUser(long projectId, UserResource user) throws ForbiddenActionException {

        RestResult<OrganisationResource> organisationResource = organisationRestService.getByUserAndProjectId(user.getId(), projectId);

        if (organisationResource.toServiceResult().isFailure()) {
            throw new ForbiddenActionException(CANNOT_GET_ANY_USERS_FOR_PROJECT.getErrorKey(), Collections.singletonList(projectId));
        }

        return organisationResource.getSuccess().getId();
    }

}