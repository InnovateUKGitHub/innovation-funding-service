package org.innovateuk.ifs.login.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DashboardSelectionViewModel that = (DashboardSelectionViewModel) o;

        return new EqualsBuilder()
                .append(availableDashboards, that.availableDashboards)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(availableDashboards)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("availableDashboards", availableDashboards)
                .toString();
    }
}
