package org.innovateuk.ifs.application.common.viewmodel;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Model attributes for the application terms view. Optionally in the context of an organisation.
 */
public class ApplicationTermsViewModel {
    private final long applicationId;
    private final long questionId;
    private final String competitionTermsTemplate;
    private final boolean collaborativeApplication;
    private final Boolean termsAccepted;
    private final String termsAcceptedByName;
    private final ZonedDateTime termsAcceptedOn;
    private final boolean termsAcceptedByAllOrganisations;
    private final boolean showHeaderAndFooter;

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
        this.showHeaderAndFooter = true;
    }

    public ApplicationTermsViewModel(long applicationId,
                                     long questionId,
                                     String competitionTermsTemplate,
                                     boolean collaborativeApplication,
                                     boolean termsAcceptedByAllOrganisation) {
        this.applicationId = applicationId;
        this.questionId = questionId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.collaborativeApplication = collaborativeApplication;
        this.termsAccepted = null;
        this.termsAcceptedByName = null;
        this.termsAcceptedOn = null;
        this.termsAcceptedByAllOrganisations = termsAcceptedByAllOrganisation;
        this.showHeaderAndFooter = false;
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

    public Optional<Boolean> getTermsAccepted() {
        return Optional.ofNullable(termsAccepted);
    }

    public Boolean isTermsAcceptedByAllOrganisations() {
        return termsAcceptedByAllOrganisations;
    }

    public Optional<String> getTermsAcceptedByName() {
        return Optional.ofNullable(termsAcceptedByName);
    }

    public Optional<ZonedDateTime> getTermsAcceptedOn() {
        return Optional.ofNullable(termsAcceptedOn);
    }

    public boolean isMigratedTerms() {
        return getTermsAccepted().orElse(false) && !getTermsAcceptedOn().isPresent();
    }

    public boolean isShowHeaderAndFooter() {
        return showHeaderAndFooter;
    }
}