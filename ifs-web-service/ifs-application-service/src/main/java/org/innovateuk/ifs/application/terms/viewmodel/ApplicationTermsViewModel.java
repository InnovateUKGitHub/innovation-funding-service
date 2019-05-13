package org.innovateuk.ifs.application.terms.viewmodel;

import java.time.ZonedDateTime;

public class ApplicationTermsViewModel {
    private final long applicationId;
    private final long questionId;
    private final String competitionTermsTemplate;
    private final boolean collaborativeApplication;
    private final boolean termsAccepted;
    private final String termsAcceptedByName;
    private final ZonedDateTime termsAcceptedOn;

    public ApplicationTermsViewModel(long applicationId,
                                     long questionId,
                                     String competitionTermsTemplate,
                                     boolean collaborativeApplication,
                                     boolean termsAccepted,
                                     String termsAcceptedByName,
                                     ZonedDateTime termsAcceptedOn) {
        this.applicationId = applicationId;
        this.questionId = questionId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.collaborativeApplication = collaborativeApplication;
        this.termsAccepted = termsAccepted;
        this.termsAcceptedByName = termsAcceptedByName;
        this.termsAcceptedOn = termsAcceptedOn;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getCompetitionTermsTemplate() {
        return competitionTermsTemplate;
    }

    public boolean isCollaborativeApplication() {
        return collaborativeApplication;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public String getTermsAcceptedByName() {
        return termsAcceptedByName;
    }

    public ZonedDateTime getTermsAcceptedOn() {
        return termsAcceptedOn;
    }

    public boolean isMigratedTerms() {
        return termsAccepted && termsAcceptedOn == null;
    }
}