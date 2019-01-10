package org.innovateuk.ifs.login.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DashboardPanel that = (DashboardPanel) o;

        return new EqualsBuilder()
                .append(role, that.role)
                .append(url, that.url)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(role)
                .append(url)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("role", role)
                .append("url", url)
                .toString();
    }
}
