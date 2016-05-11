package com.worth.ifs.alert.security;

import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.alert.domain.Alert} resources.
 */
@Component
@PermissionRules
public class AlertPermissionRules {

    @PermissionRule(value = "CREATE", description = "Competitions Admin can create Alerts")
    public boolean competitionsAdminCanCreateAlerts(final AlertResource alertResource, final UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(value = "DELETE", description = "Competitions Admin can delete Alerts")
    public boolean competitionsAdminCanDeleteAlerts(final AlertResource alertResource, final UserResource user) {
        return isCompAdmin(user);
    }
}
