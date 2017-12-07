package org.innovateuk.ifs.project.projectdetails.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Permissions for access to Project Details section
 */
@PermissionRules
@Component
public class ProjectDetailsPermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "UPDATE_BASIC_PROJECT_SETUP_DETAILS",
            description = "The lead partners can update the basic project details, like start date, address, Project Manager")
    public boolean leadPartnersCanUpdateTheBasicProjectDetails(ProjectResource project, UserResource user) {
        return isLeadPartner(project.getId(), user.getId());
    }

    @PermissionRule(
            value = "UPDATE_FINANCE_CONTACT",
            description = "A partner can update the finance contact for their own organisation")
    public boolean partnersCanUpdateTheirOwnOrganisationsFinanceContacts(ProjectOrganisationCompositeId composite, UserResource user) {
        return isPartner(composite.getProjectId(), user.getId()) && partnerBelongsToOrganisation(composite.getProjectId(), user.getId(), composite.getOrganisationId());
    }
}

