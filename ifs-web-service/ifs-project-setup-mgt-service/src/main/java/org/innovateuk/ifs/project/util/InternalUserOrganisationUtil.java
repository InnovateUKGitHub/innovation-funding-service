package org.innovateuk.ifs.project.util;


import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.threads.resource.PostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InternalUserOrganisationUtil {

    @Autowired
    private OrganisationService organisationService;

    //TODO - Getting the organisation name this way is just a workaround till IFS-651 is fixed.
    public String getOrganisationName(UserResource user, PostResource p) {

        String organisationName;
        if (user.hasRole(UserRoleType.IFS_ADMINISTRATOR)) {
            organisationName = "Innovate UK";
        } else {
            OrganisationResource organisation = organisationService.getOrganisationForUser(p.author.getId());
            organisationName = organisation.getName();
        }

        return organisationName;
    }
}
