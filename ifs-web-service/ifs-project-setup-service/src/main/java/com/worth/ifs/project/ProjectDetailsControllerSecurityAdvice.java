package com.worth.ifs.project;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.sections.ProjectSetupSectionPartnerAccessor;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * TODO DW - document this class
 */
@Aspect
@Component
public class ProjectDetailsControllerSecurityAdvice {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    @Before("@annotation(org.springframework.web.bind.annotation.RequestMapping) && " +
            "execution(public java.lang.String com.worth.ifs.project.ProjectDetailsController.*(..)) && " +
            "args(projectId, ..)")
    public void checkAccessToProjectDetailsSection(Long projectId) throws Throwable {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new ForbiddenActionException("Unable to access Project Details section as user is not logged in");
        }

        UserResource loggedInUser = (UserResource) authentication.getDetails();

        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId);
        ProjectSetupSectionPartnerAccessor sectionAccessor = new ProjectSetupSectionPartnerAccessor(teamStatus);

        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        Optional<ProjectUserResource> loggedInPartner = simpleFindFirst(projectUsers,
                pu -> pu.getUser().equals(loggedInUser.getId()) && "partner".equals(pu.getRoleName()));

        loggedInPartner.ifPresent(partner -> {
            OrganisationResource organisation = organisationService.getOrganisationById(partner.getOrganisation());
            sectionAccessor.checkAccessToProjectDetailsSection(organisation);
        });

        loggedInPartner.orElseThrow(() -> new ForbiddenActionException("No partner ProjectUser exists for user " +
                loggedInUser.getId() + " for Project " + projectId));

    }
}
