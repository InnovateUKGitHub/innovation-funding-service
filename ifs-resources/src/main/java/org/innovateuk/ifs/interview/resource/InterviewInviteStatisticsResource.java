package org.innovateuk.ifs.interview.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO for interview assessors key statistics
 */
public class InterviewInviteStatisticsResource {

    private int assessorsInvited;
    private int assessorsAccepted;
    private int assessorsRejected;
    private int assessorsOnInviteList;

    public InterviewInviteStatisticsResource() {
    }

    public InterviewInviteStatisticsResource(int assessorsInvited,
                                             int assessorsAccepted,
                                             int assessorsRejected,
                                             int assessorsOnInviteList) {
        this.assessorsInvited = assessorsInvited;
        this.assessorsAccepted = assessorsAccepted;
        this.assessorsRejected = assessorsRejected;
        this.assessorsOnInviteList = assessorsOnInviteList;
    }

    public int getAssessorsInvited() {
        return assessorsInvited;
    }

    public int getAssessorsAccepted() {
        return assessorsAccepted;
    }

    public int getAssessorsRejected() {
        return assessorsRejected;
    }

    public int getAssessorsOnInviteList() {
        return assessorsOnInviteList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewInviteStatisticsResource that = (InterviewInviteStatisticsResource) o;

        return new EqualsBuilder()
                .append(assessorsInvited, that.assessorsInvited)
                .append(assessorsAccepted, that.assessorsAccepted)
                .append(assessorsRejected, that.assessorsRejected)
                .append(assessorsOnInviteList, that.assessorsOnInviteList)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessorsInvited)
                .append(assessorsAccepted)
                .append(assessorsRejected)
                .append(assessorsOnInviteList)
                .toHashCode();
    }
}