package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

public class DashboardTabsViewModel {
    private UserResource userResource;

    public DashboardTabsViewModel(UserResource userResource) {
        this.userResource = userResource;
    }

    public boolean live() {
        return isInternal(userResource) || isStakeholder(userResource);
    }

    public boolean upcoming() {
        return isInternalAdmin(userResource);
    }

    public boolean nonIFS() {
        return isInternalAdmin(userResource);
    }

    public boolean projectSetup() {
        return isInternal(userResource) || isStakeholder(userResource) || isExternalFinanceUser(userResource);
    }

    public boolean previous() {
        return isInternal(userResource) || isStakeholder(userResource);
    }

    public boolean support() {
        return isSupport(userResource);
    }

    public boolean isInternalUser() {
        return isInternal(userResource);
    }
}