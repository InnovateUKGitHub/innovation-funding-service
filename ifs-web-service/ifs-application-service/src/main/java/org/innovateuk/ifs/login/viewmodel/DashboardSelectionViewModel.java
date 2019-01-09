package org.innovateuk.ifs.login.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the selection of a dashboard by a multi-role user
 */
public final class DashboardSelectionViewModel {

    private final List<DashboardPanel> availableDashboards;

    public DashboardSelectionViewModel(List<DashboardPanel> availableDashboards) {
        this.availableDashboards = availableDashboards;
    }

    public List<DashboardPanel> getAvailableDashboards() {
        return availableDashboards;
    }
}
