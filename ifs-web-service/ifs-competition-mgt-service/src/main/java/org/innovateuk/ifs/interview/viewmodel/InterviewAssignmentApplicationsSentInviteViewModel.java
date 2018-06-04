package org.innovateuk.ifs.interview.viewmodel;

import java.time.ZonedDateTime;

public class InterviewAssignmentApplicationsSentInviteViewModel {

    private final long competitionId;
    private final String competitionName;
    private final long applicationId;
    private final String title;
    private final String leadOrganisation;
    private final ZonedDateTime dateAssigned;
    private final String feedbackFilename;
    private final String subject;
    private final String content;
    private final String additionalText;
    private final String originQuery;

    public InterviewAssignmentApplicationsSentInviteViewModel(long competitionId,
                                                              String competitionName,
                                                              long applicationId,
                                                              String title,
                                                              String leadOrganisation,
                                                              ZonedDateTime dateAssigned,
                                                              String feedbackFilename,
                                                              String subject,
                                                              String content,
                                                              String additionalText,
                                                              String originQuery) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applicationId = applicationId;
        this.title = title;
        this.leadOrganisation = leadOrganisation;
        this.dateAssigned = dateAssigned;
        this.feedbackFilename = feedbackFilename;
        this.subject = subject;
        this.content = content;
        this.additionalText = additionalText;
        this.originQuery = originQuery;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getTitle() {
        return title;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public ZonedDateTime getDateAssigned() {
        return dateAssigned;
    }

    public String getFeedbackFilename() {
        return feedbackFilename;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getAdditionalText() {
        return additionalText;
    }

    public String getOriginQuery() {
        return originQuery;
    }

    /* View logic */
    public boolean hasAttachment() {
        return feedbackFilename != null;
    }
}