package com.worth.ifs.project;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.sections.ProjectSetupSectionPartnerAccessor;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Permission checker around the access to various sections within the Project Setup process
 */
@PermissionRules
@Component
public class ProjectSetupSectionsPermissionRules {

    private static final Log LOG = LogFactory.getLog(ProjectSetupSectionsPermissionRules.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    @PermissionRule(value = "ACCESS_PROJECT_DETAILS_SECTION", description = "A partner can access the Project Details section when their Companies House data is complete or not required")
    public boolean partnerCanAccessProjectDetailsSection(Long projectId, UserResource user) {

        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId);
        ProjectSetupSectionPartnerAccessor sectionAccessor = new ProjectSetupSectionPartnerAccessor(teamStatus);

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        Optional<ProjectUserResource> loggedInPartner = simpleFindFirst(projectUsers,
                pu -> pu.getUser().equals(user.getId()) && UserRoleType.PARTNER.getName().equals(pu.getRoleName()));

        return loggedInPartner.map(partner -> {

            OrganisationResource organisation = organisationService.getOrganisationById(partner.getOrganisation());
            return sectionAccessor.canAccessProjectDetailsSection(organisation);

        }).orElseGet(() -> {
            LOG.error("No partner ProjectUser exists for user " + user.getId() + " for Project " + projectId);
            return false;
        });
    }
}
