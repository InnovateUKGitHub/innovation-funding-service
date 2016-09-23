package com.worth.ifs.alert.security;

import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.isSystemMaintenanceUser;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.alert.domain.Alert} resources.
 */
@Component
@PermissionRules
public class AlertPermissionRules {

    @PermissionRule(value = "CREATE", description = "System maintentenance users can create Alerts")
    public boolean systemMaintenanceUserCanCreateAlerts(final AlertResource alertResource, final UserResource user) {
        return isSystemMaintenanceUser(user);
    }

    @PermissionRule(value = "DELETE", description = "System maintentenance users can delete Alerts")
    public boolean systemMaintenanceUserCanDeleteAlerts(final AlertResource alertResource, final UserResource user) {
        return isSystemMaintenanceUser(user);
    }
}
