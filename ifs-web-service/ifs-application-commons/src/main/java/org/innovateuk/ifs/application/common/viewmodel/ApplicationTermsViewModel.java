package org.innovateuk.ifs.application.common.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Model attributes for the application terms view. Optionally in the context of an organisation.
 */
public class ApplicationTermsViewModel implements BaseAnalyticsViewModel {
    private final long competitionId;
    private final long questionId;
    private final String competitionName;
    private final long applicationId;
    private final String competitionTermsTemplate;
    private final boolean collaborativeApplication;
    private final Boolean termsAccepted;
    private final String termsAcceptedByName;
    private final ZonedDateTime termsAcceptedOn;
    private final boolean termsAcceptedByAllOrganisations;
    private final boolean showHeaderAndFooter;
    private final boolean additionalTerms;

    public ApplicationTermsViewModel(long applicationId,
                                     String competitionName,
                                     long competitionId,
                                     long questionId,
                                     String competitionTermsTemplate,
                                     boolean collaborativeApplication,
                                     boolean termsAccepted,
                                     String termsAcceptedByName,
                                     ZonedDateTime termsAcceptedOn,
                                     boolean termsAcceptedByAllOrganisations,
                                     boolean additionalTerms) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.competitionId = competitionId;
        this.questionId = questionId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.collaborativeApplication = collaborativeApplication;
        this.termsAccepted = termsAccepted;
        this.termsAcceptedByName = termsAcceptedByName;
        this.termsAcceptedOn = termsAcceptedOn;
        this.termsAcceptedByAllOrganisations = termsAcceptedByAllOrganisations;
        this.additionalTerms = additionalTerms;
        this.showHeaderAndFooter = true;
    }

    public ApplicationTermsViewModel(long applicationId,
                                     String competitionName,
                                     long competitionId,
                                     long questionId,
                                     String competitionTermsTemplate,
                                     boolean collaborativeApplication,
                                     boolean termsAcceptedByAllOrganisation,
                                     boolean additionalTerms) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.competitionId = competitionId;
        this.questionId = questionId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.collaborativeApplication = collaborativeApplication;
        this.additionalTerms = additionalTerms;
        this.termsAccepted = null;
        this.termsAcceptedByName = null;
        this.termsAcceptedOn = null;
        this.termsAcceptedByAllOrganisations = termsAcceptedByAllOrganisation;
        this.showHeaderAndFooter = false;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public long getCompetitionId() {
        return competitionId;
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

    public boolean isAdditionalTerms() {
        return additionalTerms;
    }
}