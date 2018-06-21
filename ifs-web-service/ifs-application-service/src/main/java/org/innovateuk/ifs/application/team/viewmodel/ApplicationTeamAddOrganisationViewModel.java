package org.innovateuk.ifs.application.team.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static java.lang.String.format;

/**
 * Holder of model attributes for the Add Organisation view.
 */
public class ApplicationTeamAddOrganisationViewModel {

    private long applicationId;
    private Long questionId;
    private String applicationName;

    public ApplicationTeamAddOrganisationViewModel(long applicationId,
                                                   Long questionId,
                                                   String applicationName) {
        this.applicationId = applicationId;
        this.questionId = questionId;
        this.applicationName = applicationName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getBackUrl() {
        boolean useNewApplicantMenu = questionId != null;
        if (useNewApplicantMenu) {
            return format("/application/%s/form/question/%s", applicationId, questionId);
        } else {
            return format("/application/%s/team", applicationId);
        }
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