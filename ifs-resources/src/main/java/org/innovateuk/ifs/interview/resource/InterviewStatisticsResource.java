package org.innovateuk.ifs.interview.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for interview key statistics
 */
public class InterviewStatisticsResource {
    private final int applicationsAssigned;
    private final int respondedToFeedback;
    private final int assessorsAccepted;

    public InterviewStatisticsResource() {
        applicationsAssigned = 0;
        respondedToFeedback = 0;
        assessorsAccepted = 0;
    }

    public InterviewStatisticsResource(int applicationsAssigned, int respondedToFeedback, int assessorsAccepted) {
        this.applicationsAssigned = applicationsAssigned;
        this.respondedToFeedback = respondedToFeedback;
        this.assessorsAccepted = assessorsAccepted;
    }

    public int getApplicationsAssigned() {
        return applicationsAssigned;
    }

    public int getRespondedToFeedback() {
        return respondedToFeedback;
    }

    public int getAssessorsAccepted() {
        return assessorsAccepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewStatisticsResource that = (InterviewStatisticsResource) o;

        return new EqualsBuilder()
                .append(applicationsAssigned, that.applicationsAssigned)
                .append(respondedToFeedback, that.respondedToFeedback)
                .append(assessorsAccepted, that.assessorsAccepted)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationsAssigned)
                .append(respondedToFeedback)
                .append(assessorsAccepted)
                .toHashCode();
    }
}