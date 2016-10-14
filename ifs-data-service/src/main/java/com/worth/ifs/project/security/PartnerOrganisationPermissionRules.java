package com.worth.ifs.project.security;

import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.user.resource.UserResource;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;
import static com.worth.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

@PermissionRules
public class PartnerOrganisationPermissionRules extends BasePermissionRules {
    @PermissionRule(value = "READ", description = "A partner can see a list of all partner organisations on their project")
    public boolean partnersOnProjectCanView(PartnerOrganisationResource partnerOrganisation, UserResource user) {
        return isPartner(partnerOrganisation.getProject(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Comp admins can see partner organisations for any project")
    public boolean compAdminsCanViewProjects(PartnerOrganisationResource partnerOrganisation, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "Project finance users can see partner organisations for any project")
    public boolean projectFinanceUsersCanViewProjects(PartnerOrganisationResource partnerOrganisation, UserResource user){
        return isProjectFinanceUser(user);
    }
}
