package org.innovateuk.ifs.management.controller.dashboard;

import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

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
        return isInternal(userResource);
    }

    public boolean previous(){
        return isInternalAdmin(userResource) || isSupport(userResource);
    }
}