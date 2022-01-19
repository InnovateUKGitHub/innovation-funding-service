package org.innovateuk.ifs.interview.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class InterviewNotifyAllocationResource {

    private final long competitionId;
    private final long assessorId;
    private final String subject;
    private final String content;
    private final List<Long> applicationIds;

    public InterviewNotifyAllocationResource() {
        competitionId = 0;
        assessorId = 0;
        subject = null;
        content = null;
        applicationIds = null;
    }

    public InterviewNotifyAllocationResource(long competitionId, long assessorId, String subject, String content, List<Long> applicationIds) {
        if (applicationIds == null) {
            throw new NullPointerException("applicationIds cannot be null");
        }
        if (applicationIds.isEmpty()) {
            throw new IllegalArgumentException("applicationIds cannot be empty");
        }

        this.competitionId = competitionId;
        this.assessorId = assessorId;
        this.subject = subject;
        this.content = content;
        this.applicationIds = applicationIds;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public List<Long> getApplicationIds() {
        return applicationIds;
    }

    public long getAssessorId() {
        return assessorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewNotifyAllocationResource that = (InterviewNotifyAllocationResource) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(assessorId, that.assessorId)
                .append(subject, that.subject)
                .append(content, that.content)
                .append(applicationIds, that.applicationIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(assessorId)
                .append(subject)
                .append(content)
                .append(applicationIds)
                .toHashCode();
    }
}