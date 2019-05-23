package org.innovateuk.ifs.application.common.viewmodel;

import java.time.ZonedDateTime;

/**
 * Model attributes for the application terms view.
 */
public class ApplicationTermsViewModel {
    private final long applicationId;
    private final long questionId;
    private final String competitionTermsTemplate;
    private final boolean collaborativeApplication;
    private final boolean termsAccepted;
    private final String termsAcceptedByName;
    private final ZonedDateTime termsAcceptedOn;
    private final boolean termsAcceptedByAllOrganisations;

    public ApplicationTermsViewModel(long applicationId,
                                     long questionId,
                                     String competitionTermsTemplate,
                                     boolean collaborativeApplication,
                                     boolean termsAccepted,
                                     String termsAcceptedByName,
                                     ZonedDateTime termsAcceptedOn,
                                     boolean termsAcceptedByAllOrganisations) {
        this.applicationId = applicationId;
        this.questionId = questionId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.collaborativeApplication = collaborativeApplication;
        this.termsAccepted = termsAccepted;
        this.termsAcceptedByName = termsAcceptedByName;
        this.termsAcceptedOn = termsAcceptedOn;
        this.termsAcceptedByAllOrganisations = termsAcceptedByAllOrganisations;
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

    public boolean isTermsAcceptedByAllOrganisations() {
        return termsAcceptedByAllOrganisations;
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