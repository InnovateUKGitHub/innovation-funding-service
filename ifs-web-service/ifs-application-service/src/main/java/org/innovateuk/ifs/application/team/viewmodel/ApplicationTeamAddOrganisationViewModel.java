package org.innovateuk.ifs.application.team.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the Add Organisation view.
 */
public class ApplicationTeamAddOrganisationViewModel {

    private long applicationId;
    private long questionId;
    private String applicationName;

    public ApplicationTeamAddOrganisationViewModel(long applicationId,
                                                   long questionId,
                                                   String applicationName) {
        this.applicationId = applicationId;
        this.questionId = questionId;
        this.applicationName = applicationName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ApplicationTeamAddOrganisationViewModel that = (ApplicationTeamAddOrganisationViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(questionId, that.questionId)
                .append(applicationName, that.applicationName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(questionId)
                .append(applicationName)
                .toHashCode();
    }
}