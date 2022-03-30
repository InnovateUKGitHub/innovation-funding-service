package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;

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
    private final boolean isThirdPartyProcurementCompetition;
    private CompetitionThirdPartyConfigResource thirdPartyConfig;
    private FileEntryResource competitionTerms;
    private final long competitionId;

    public ProjectTermsViewModel(long projectId,
                                 String projectName,
                                 long organisationId,
                                 String competitionTermsTemplate,
                                 boolean termsAccepted,
                                 ZonedDateTime termsAcceptedOn,
                                 boolean subsidyBasisRequiredAndNotCompleted,
                                 Optional<Long> subsidyBasisQuestionId,
                                 boolean isThirdPartyProcurementCompetition,
                                 CompetitionThirdPartyConfigResource thirdPartyConfig,
                                 FileEntryResource competitionTerms,
                                 long competitionId) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
        this.competitionTermsTemplate = competitionTermsTemplate;
        this.termsAccepted = termsAccepted;
        this.termsAcceptedOn = termsAcceptedOn;
        this.subsidyBasisRequiredAndNotCompleted = subsidyBasisRequiredAndNotCompleted;
        this.subsidyBasisQuestionId = subsidyBasisQuestionId;
        this.isThirdPartyProcurementCompetition = isThirdPartyProcurementCompetition;
        this.thirdPartyConfig = thirdPartyConfig;
        this.competitionTerms = competitionTerms;
        this.competitionId = competitionId;
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

    public boolean isThirdPartyProcurementCompetition() {
        return isThirdPartyProcurementCompetition;
    }

    public CompetitionThirdPartyConfigResource getThirdPartyConfig() {
        return thirdPartyConfig;
    }

    public FileEntryResource getCompetitionTerms() {
        return competitionTerms;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public boolean isTermsAndConditionsUploaded() {
        return competitionTerms != null;
    }
}
