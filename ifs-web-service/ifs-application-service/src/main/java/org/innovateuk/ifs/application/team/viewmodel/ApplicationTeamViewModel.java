package org.innovateuk.ifs.application.team.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.viewmodel.AbstractLeadOnlyViewModel;

import java.util.List;

/**
 * Holder of model attributes for the Application Team view.
 */
public class ApplicationTeamViewModel extends AbstractLeadOnlyViewModel {

    private String applicationName;
    private List<ApplicationTeamOrganisationRowViewModel> organisations;
    private boolean userLeadApplicant;
    private boolean applicationCanBegin;
    private boolean summary;

    public ApplicationTeamViewModel(Long applicationId,
                                    Long questionId,
                                    String applicationName,
                                    List<ApplicationTeamOrganisationRowViewModel> organisations,
                                    boolean userLeadApplicant,
                                    boolean applicationCanBegin,
                                    boolean closed,
                                    boolean complete,
                                    boolean canMarkAsComplete) {
        super(questionId, applicationId, closed, complete, canMarkAsComplete);
        this.applicationName = applicationName;
        this.organisations = organisations;
        this.userLeadApplicant = userLeadApplicant;
        this.applicationCanBegin = applicationCanBegin;
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

    @Override
    public boolean isSummary() {
        return summary;
    }

    public void setSummary(final boolean summary) {
        this.summary = summary;
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
                .append(userLeadApplicant, that.userLeadApplicant)
                .append(applicationCanBegin, that.applicationCanBegin)
                .append(summary, that.summary)
                .append(applicationName, that.applicationName)
                .append(organisations, that.organisations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationName)
                .append(organisations)
                .append(userLeadApplicant)
                .append(applicationCanBegin)
                .append(summary)
                .toHashCode();
    }
}