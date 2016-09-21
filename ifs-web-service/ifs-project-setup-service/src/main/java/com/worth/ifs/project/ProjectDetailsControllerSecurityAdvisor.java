package com.worth.ifs.project;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.sections.ProjectSetupSectionPartnerAccessor;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This Advice targets any public @RequestMapping methods in ProjectDetailsController that can supply a projectId as
 * its first argument.  Based on the projectId this class looks up the current ProjectTeamStatus and decides whether or
 * not the current Partner is able to access the Project Details section
 */
@Component
public class ProjectDetailsControllerSecurityAdvisor {

    private static final Log LOG = LogFactory.getLog(ProjectDetailsControllerSecurityAdvisor.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    public boolean canAccessProjectDetailsSection(Long projectId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            LOG.error("Unable to access Project Details section as user is not logged in");
            return false;
        }

        UserResource loggedInUser = (UserResource) authentication.getDetails();

        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId);
        ProjectSetupSectionPartnerAccessor sectionAccessor = new ProjectSetupSectionPartnerAccessor(teamStatus);

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        Optional<ProjectUserResource> loggedInPartner = simpleFindFirst(projectUsers,
                pu -> pu.getUser().equals(loggedInUser.getId()) && "partner".equals(pu.getRoleName()));

        return loggedInPartner.map(partner -> {

            OrganisationResource organisation = organisationService.getOrganisationById(partner.getOrganisation());
            return sectionAccessor.canAccessProjectDetailsSection(organisation);

        }).orElseGet(() -> {
            LOG.error("No partner ProjectUser exists for user " + loggedInUser.getId() + " for Project " + projectId);
            return false;
        });
    }
}
