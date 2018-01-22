package org.innovateuk.ifs.analytics;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The Google Analytics Tag Manager Data Layer. Contains our properties for Google Analytics.
 * See https://support.google.com/analytics/answer/6164391?hl=en
 */
public class GoogleAnalyticsDataLayer {

    private String competitionName;
    private String userRole;

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GoogleAnalyticsDataLayer that = (GoogleAnalyticsDataLayer) o;

        return new EqualsBuilder()
                .append(competitionName, that.competitionName)
                .append(userRole, that.userRole)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionName)
                .append(userRole)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitionName", competitionName)
                .append("userRole", userRole)
                .toString();
    }
}
