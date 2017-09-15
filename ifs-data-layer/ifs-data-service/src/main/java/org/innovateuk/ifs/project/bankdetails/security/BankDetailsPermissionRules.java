package org.innovateuk.ifs.project.bankdetails.security;

import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isProjectFinanceUser;

@Component
@PermissionRules
public class BankDetailsPermissionRules extends BasePermissionRules {
    @PermissionRule(
            value = "SUBMIT",
            description = "Partners can submit their own organisations bank details")
    public boolean partnersCanSubmitTheirOwnOrganisationsBankDetails(BankDetailsResource bankDetailsResource, UserResource user) {
        return isPartner(bankDetailsResource.getProject(), user.getId()) && partnerBelongsToOrganisation(bankDetailsResource.getProject(), user.getId(), bankDetailsResource.getOrganisation());
    }

    @PermissionRule(
            value = "UPDATE",
            description = "Project finance users can update any organisations bank details")
    public boolean projectFinanceUsersCanUpdateAnyOrganisationsBankDetails(BankDetailsResource bankDetailsResource, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "READ",
            description = "Partners can see their own organisations bank details")
    public boolean partnersCanSeeTheirOwnOrganisationsBankDetails(BankDetailsResource bankDetailsResource, UserResource user) {
        return isPartner(bankDetailsResource.getProject(), user.getId()) && partnerBelongsToOrganisation(bankDetailsResource.getProject(), user.getId(), bankDetailsResource.getOrganisation());
    }

    @PermissionRule(
            value = "READ",
            description = "Project finance user can see all bank details on all projects")
    public boolean projectFinanceUsersCanSeeAllBankDetailsOnAllProjects(BankDetailsResource bankDetailsResource, UserResource user) {
        return isProjectFinanceUser(user);
    }
}
