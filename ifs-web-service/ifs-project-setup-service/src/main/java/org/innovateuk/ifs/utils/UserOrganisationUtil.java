package org.innovateuk.ifs.utils;

import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.error.exception.GeneralUnexpectedErrorException;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNOT_GET_ANY_USERS_FOR_PROJECT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * A component for holding functions that will be commonly used for authorisation in web layer.
 */
@Component
public class UserOrganisationUtil {

    @Autowired
    private ProjectService projectService;

    public boolean userIsPartnerInOrganisationForProject(Long projectId, Long organisationId, Long userId) {
        if(userId == null) {
            return false;
        }

        List<ProjectUserResource> thisProjectUsers = projectService.getProjectUsersForProject(projectId);
        List<ProjectUserResource> projectUsersForOrganisation = simpleFilter(thisProjectUsers, user -> user.getOrganisation().equals(organisationId));
        List<ProjectUserResource> projectUsersForUserAndOrganisation = simpleFilter(projectUsersForOrganisation, user -> user.getUser().equals(userId));

        return !projectUsersForUserAndOrganisation.isEmpty();
    }

    public Long getOrganisationIdFromUser(Long projectId, UserResource user) throws ForbiddenActionException {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        Optional<ProjectUserResource> projectUser = simpleFindFirst(projectUsers, pu ->
                user.getId().equals(pu.getUser()) && UserRoleType.PARTNER.getName().equals(pu.getRoleName()));
        return projectUser.map(ProjectUserResource::getOrganisation).orElseThrow(() -> new ForbiddenActionException(CANNOT_GET_ANY_USERS_FOR_PROJECT.getErrorKey(), singletonList(projectId)));
    }
}
