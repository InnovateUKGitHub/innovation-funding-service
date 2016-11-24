package com.worth.ifs.project.bankdetails.security;

import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

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
