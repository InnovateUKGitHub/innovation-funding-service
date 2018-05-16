package org.innovateuk.ifs.interview.resource;

import java.util.List;

public class InterviewNotifyAllocationResource {

    private final long competitionId;
    private final long assessorId;
    private final String subject;
    private final String content;
    private final List<Long> applicationIds;

    InterviewNotifyAllocationResource() {
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
}