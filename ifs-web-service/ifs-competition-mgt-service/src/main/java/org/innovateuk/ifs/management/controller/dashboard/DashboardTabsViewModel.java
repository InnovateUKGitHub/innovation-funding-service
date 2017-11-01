package org.innovateuk.ifs.management.controller.dashboard;

import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdminOrSupport;

public class DashboardTabsViewModel {
    private UserResource userResource;

    public DashboardTabsViewModel(UserResource userResource) {
        this.userResource = userResource;
    }

    public boolean live() {
        return isInternal(userResource);
    }

    public boolean upcoming() {
        return isInternalAdmin(userResource);
    }

    public boolean nonIFS() {
        return isInternalAdmin(userResource);
    }

    public boolean projectSetup(){
        return isInternalAdminOrSupport(userResource);
    }

    public boolean previous(){
        return isInternalAdminOrSupport(userResource);
    }
}