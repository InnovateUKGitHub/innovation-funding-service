package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Defines rules for the FinanceTotalsSender service.
 */
@Component
@PermissionRules
public class FinanceTotalsRules extends BasePermissionRules {
    @PermissionRule(value = "SEND_APPLICATION_TOTALS_ON_SUBMIT", description = "Internal users and lead applicants are allowed to send the application totals.")
    public boolean leadApplicantAndInternalUsersCanUpdateTotalsForAnApplication(final Application application, final UserResource user) {
        return isLeadApplicant(application.getId(), user);
    }
}