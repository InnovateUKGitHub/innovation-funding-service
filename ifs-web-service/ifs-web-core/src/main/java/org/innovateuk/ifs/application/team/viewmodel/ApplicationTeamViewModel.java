package org.innovateuk.ifs.application.team.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamViewModel {

    private long applicationId;
    private String applicationName;
    private List<ApplicationTeamOrganisationRowViewModel> organisations;
    private boolean userLeadApplicant;
    private boolean applicationCanBegin;
    private boolean closed;
    private boolean complete;
    private boolean canMarkAsComplete;
    private boolean summary;

    public ApplicationTeamViewModel(long applicationId,
                                    String applicationName,
                                    List<ApplicationTeamOrganisationRowViewModel> organisations,
                                    boolean userLeadApplicant,
                                    boolean applicationCanBegin,
                                    boolean closed,
                                    boolean complete,
                                    boolean canMarkAsComplete) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.organisations = organisations;
        this.userLeadApplicant = userLeadApplicant;
        this.applicationCanBegin = applicationCanBegin;
        this.closed = closed;
        this.complete = complete;
        this.canMarkAsComplete = canMarkAsComplete;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public List<ApplicationTeamOrganisationRowViewModel> getOrganisations() {
        return organisations;
    }

    public boolean isUserLeadApplicant() {
        return userLeadApplicant;
    }

    public boolean isApplicationCanBegin() {
        return applicationCanBegin;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(final boolean closed) {
        this.closed = closed;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(final boolean complete) {
        this.complete = complete;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(final boolean summary) {
        this.summary = summary;
    }

    public boolean isCanMarkAsComplete() {
        return canMarkAsComplete;
    }

    public void setCanMarkAsComplete(final boolean canMarkAsComplete) {
        this.canMarkAsComplete = canMarkAsComplete;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ApplicationTeamViewModel that = (ApplicationTeamViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(userLeadApplicant, that.userLeadApplicant)
                .append(applicationCanBegin, that.applicationCanBegin)
                .append(closed, that.closed)
                .append(complete, that.complete)
                .append(canMarkAsComplete, that.canMarkAsComplete)
                .append(summary, that.summary)
                .append(applicationName, that.applicationName)
                .append(organisations, that.organisations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .append(organisations)
                .append(userLeadApplicant)
                .append(applicationCanBegin)
                .append(closed)
                .append(complete)
                .append(canMarkAsComplete)
                .append(summary)
                .toHashCode();
    }
}