package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import java.time.ZonedDateTime;
import java.util.Optional;

public class ProjectTermsViewModel {
    private final long projectId;
    private final long organisationId;
    private final String projectName;
    private final String competitionTermsTemplate;
    private final boolean termsAccepted;
    private final ZonedDateTime termsAcceptedOn;
    private final boolean subsidyBasisRequiredAndNotCompleted;
    private final Optional<Long> subsidyBasisQuestionId;

    public ProjectTermsViewModel(long projectId,
                                 String projectName,
                                 long organisationId,
                                 String competitionTermsTemplate,
                                 boolean termsAccepted,
                                 ZonedDateTime termsAcceptedOn,
                                 boolean subsidyBasisRequiredAndNotCompleted,
                                 Optional<Long> subsidyBasisQuestionId) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.termsAccepted = termsAccepted;
        this.termsAcceptedOn = termsAcceptedOn;
        this.subsidyBasisRequiredAndNotCompleted = subsidyBasisRequiredAndNotCompleted;
        this.subsidyBasisQuestionId = subsidyBasisQuestionId;
    }


    public boolean isSubsidyBasisRequiredAndNotCompleted() {
        return subsidyBasisRequiredAndNotCompleted;
    }

    public Long getSubsidyBasisQuestionId() {
        return subsidyBasisQuestionId.orElse(null);
    }

    public long getProjectId() {
        return projectId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public String getCompetitionTermsTemplate() {
        return competitionTermsTemplate;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public Optional<ZonedDateTime> getTermsAcceptedOn() {
        return Optional.ofNullable(termsAcceptedOn);
    }

    public boolean isTermsAndConditionsSectionLocked() {
        return subsidyBasisRequiredAndNotCompleted;
    }

    public String getProjectName() {
        return projectName;
    }
}
