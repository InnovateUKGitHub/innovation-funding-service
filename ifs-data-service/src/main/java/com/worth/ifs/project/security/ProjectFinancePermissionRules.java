package com.worth.ifs.project.security;

import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@PermissionRules
@Component
public class ProjectFinancePermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Partners can view their own Spend Profile data")
    public boolean partnersCanViewTheirOwnSpendProfileData(String projectAndOrganisationId, UserResource user) {

        String[] parts = projectAndOrganisationId.split(":");
        String projectId = parts[0];
        String organisationId = parts[1];

        return partnerBelongsToOrganisation(Long.valueOf(projectId), user.getId(), Long.valueOf(organisationId));
    }
}