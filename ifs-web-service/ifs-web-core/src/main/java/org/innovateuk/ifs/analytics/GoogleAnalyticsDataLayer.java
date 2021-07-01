package org.innovateuk.ifs.analytics;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;

/**
 * The Google Analytics Tag Manager Data Layer. Contains our properties for Google Analytics.
 * See https://support.google.com/analytics/answer/6164391?hl=en
 */
public class GoogleAnalyticsDataLayer {

    private String competitionName;
    private List<String> userRoles = new ArrayList<>();
    private Long applicationId;

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getUserRoles() {

        if(userRoles == null || userRoles.isEmpty()) {
            return "anonymous";
        }
        return simpleJoiner(userRoles,
                String::toLowerCase,
                            ",");

    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }

    public void addUserRoles(List<String> newRoles) {
        userRoles.addAll(newRoles);
    }

    public void addRole(String role) {
        userRoles.add(role);
    }
    public Long getApplicationId() { return applicationId; }

    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GoogleAnalyticsDataLayer that = (GoogleAnalyticsDataLayer) o;

        return new EqualsBuilder()
                .append(competitionName, that.competitionName)
                .append(userRoles, that.userRoles)
                .append(applicationId, that.applicationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionName)
                .append(userRoles)
                .append(applicationId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitionName", competitionName)
                .append("userRoles", userRoles)
                .append("applicationId", applicationId)
                .toString();
    }
}
