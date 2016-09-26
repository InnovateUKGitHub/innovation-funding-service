package com.worth.ifs.project;

import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.project.resource.ProjectPartnerStatusResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.sections.ProjectSetupSectionPartnerAccessor;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Permission checker around the access to various sections within the Project Setup process
 */
@PermissionRules
@Component
public class ProjectSetupSectionsPermissionRules {

    private static final Log LOG = LogFactory.getLog(ProjectSetupSectionsPermissionRules.class);

    @Autowired
    private ProjectService projectService;

    @PermissionRule(value = "ACCESS_PROJECT_DETAILS_SECTION", description = "A partner can access the Project Details section when their Companies House data is complete or not required")
    public boolean partnerCanAccessProjectDetailsSection(Long projectId, UserResource user) {

        ProjectTeamStatusResource teamStatus;

        try {
            teamStatus = projectService.getProjectTeamStatus(projectId, Optional.of(user.getId()));
        } catch (ForbiddenActionException e) {
            LOG.error("User " + user.getId() + " is not a Partner on an Organisation for Project " + projectId);
            return false;
        }

        ProjectPartnerStatusResource partnerStatusForUser =
                !teamStatus.getOtherPartnersStatuses().isEmpty() ?
                        teamStatus.getOtherPartnersStatuses().get(0) :
                        teamStatus.getLeadPartnerStatus();

        ProjectSetupSectionPartnerAccessor sectionAccessor = new ProjectSetupSectionPartnerAccessor(teamStatus);
        OrganisationResource organisation = new OrganisationResource();
        organisation.setId(partnerStatusForUser.getOrganisationId());
        organisation.setOrganisationType(partnerStatusForUser.getOrganisationType().getOrganisationTypeId());

        return sectionAccessor.canAccessProjectDetailsSection(organisation);
    }
}
