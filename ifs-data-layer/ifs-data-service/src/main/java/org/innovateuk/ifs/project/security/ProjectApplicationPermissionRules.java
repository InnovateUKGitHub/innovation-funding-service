package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Project permission rules for viewing an application.
 */
@PermissionRules
@Component
public class ProjectApplicationPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ", description = "Project Partners can see applications that are linked to their Projects")
    public boolean projectPartnerCanViewApplicationsLinkedToTheirProjects(final ApplicationResource application, final UserResource user) {
        Project linkedProject = projectRepository.findOneByApplicationId(application.getId());
        if (linkedProject == null) {
            return false;
        }
        return isPartner(linkedProject.getId(), user.getId());
    }
}
