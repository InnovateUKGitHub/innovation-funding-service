package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.Role;

/**
 * Represents a dashboard tile on the multiple dashboards selection page
 */
public class DashboardPanel {

    private final Role role;
    private final String url;

    public DashboardPanel(Role role, String url) {
        this.role = role;
        this.url = url;
    }

    public Role getRole() {
        return role;
    }

    public String getUrl() {
        return url;
    }
}
