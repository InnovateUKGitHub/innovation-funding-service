package org.innovateuk.ifs.management.dashboard.viewmodel;

import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

public class DashboardTabsViewModel {
    private UserResource userResource;

    public DashboardTabsViewModel(UserResource userResource) {
        this.userResource = userResource;
    }

    public boolean live() {
        return isInternal(userResource) || userResource.hasAuthority(Authority.STAKEHOLDER);
    }

    public boolean upcoming() {
        return hasCompetitionAdministratorAuthority(userResource);
    }

    public boolean nonIFS() {
        return hasCompetitionAdministratorAuthority(userResource);
    }

    public boolean projectSetup() {
        return isInternal(userResource) || userResource.hasAuthority(Authority.STAKEHOLDER) || isExternalFinanceUser(userResource);
    }

    public boolean previous() {
        return isInternal(userResource) || userResource.hasAuthority(Authority.STAKEHOLDER);
    }

    public boolean support() {
        return isSupport(userResource);
    }

    public boolean isInternalUser() {
        return isInternal(userResource);
    }
}