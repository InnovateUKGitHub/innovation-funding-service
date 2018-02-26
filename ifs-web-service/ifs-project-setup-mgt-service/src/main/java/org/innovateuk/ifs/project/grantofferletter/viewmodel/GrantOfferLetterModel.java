package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;

/**
 * View model backing the internal members view of the Grant Offer Letter send page
 */
public class GrantOfferLetterModel {

    private CompetitionSummaryResource competitionSummary;
    private FileDetailsViewModel grantOfferLetterFile;
    private FileDetailsViewModel additionalContractFile;
    private Long projectId;
    private String projectName;
    private Long applicationId;
    private Boolean grantOfferLetterFileContentAvailable;
    private Boolean additionalContractFileContentAvailable;
    private FileDetailsViewModel signedGrantOfferLetterFile;
    private GrantOfferLetterStateResource grantOfferState;
    private String grantOfferLetterRejectionReason;

    public GrantOfferLetterModel(CompetitionSummaryResource competitionSummary,
                                 FileDetailsViewModel grantOfferLetterFile,
                                 FileDetailsViewModel additionalContractFile,
                                 Long projectId, String projectName, Long applicationId, Boolean grantOfferLetterFileContentAvailable, Boolean additionalContractFileContentAvailable,
                                 FileDetailsViewModel signedGrantOfferLetterFile,
                                 GrantOfferLetterStateResource grantOfferState, String grantOfferLetterRejectionReason) {

        this.competitionSummary = competitionSummary;
        this.grantOfferLetterFile = grantOfferLetterFile;
        this.additionalContractFile = additionalContractFile;
        this.projectId = projectId;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.grantOfferLetterFileContentAvailable = grantOfferLetterFileContentAvailable;
        this.additionalContractFileContentAvailable = additionalContractFileContentAvailable;
        this.signedGrantOfferLetterFile = signedGrantOfferLetterFile;
        this.grantOfferState = grantOfferState;
        this.grantOfferLetterRejectionReason = grantOfferLetterRejectionReason;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }


    public boolean isSentToProjectTeam() { return grantOfferState.isGeneratedGrantOfferLetterAlreadySentToProjectTeam(); }

    public FileDetailsViewModel getGrantOfferLetterFile() { return grantOfferLetterFile; }

    public void setGrantOfferLetterFile(FileDetailsViewModel grantOfferLetterFile) { this.grantOfferLetterFile = grantOfferLetterFile; }

    public FileDetailsViewModel getAdditionalContractFile() {
        return additionalContractFile;
    }

    public void setAdditionalContractFile(FileDetailsViewModel additionalContractFile) { this.additionalContractFile = additionalContractFile; }

    public Long getProjectId() {
        return projectId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Boolean getGrantOfferLetterFileContentAvailable() { return grantOfferLetterFileContentAvailable; }

    public Boolean getAdditionalContractFileContentAvailable() { return additionalContractFileContentAvailable; }

    public FileDetailsViewModel getSignedGrantOfferLetterFile() { return signedGrantOfferLetterFile; }

    public boolean getSignedGrantOfferLetterApproved() { return grantOfferState.isSignedGrantOfferLetterApproved(); }

    public boolean getSignedGrantOfferLetterRejected() {
        return grantOfferState.isSignedGrantOfferLetterRejected();
    }

    public String getGrantOfferLetterRejectionReason() {
        return grantOfferLetterRejectionReason;
    }

    public boolean getSignedGrantOfferLetterFileAvailable() { return grantOfferState.isSignedGrantOfferLetterReceivedByInternalTeam(); }

    public boolean isShowRemoveOfferLetterButton() {
        return grantOfferLetterFile != null && !grantOfferState.isGeneratedGrantOfferLetterAlreadySentToProjectTeam();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantOfferLetterModel that = (GrantOfferLetterModel) o;

        return new EqualsBuilder()
                .append(competitionSummary, that.competitionSummary)
                .append(grantOfferLetterFile, that.grantOfferLetterFile)
                .append(additionalContractFile, that.additionalContractFile)
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(applicationId, that.applicationId)
                .append(grantOfferLetterFileContentAvailable, that.grantOfferLetterFileContentAvailable)
                .append(additionalContractFileContentAvailable, that.additionalContractFileContentAvailable)
                .append(signedGrantOfferLetterFile, that.signedGrantOfferLetterFile)
                .append(grantOfferState, that.grantOfferState)
                .append(grantOfferLetterRejectionReason, that.grantOfferLetterRejectionReason)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionSummary)
                .append(grantOfferLetterFile)
                .append(additionalContractFile)
                .append(projectId)
                .append(projectName)
                .append(applicationId)
                .append(grantOfferLetterFileContentAvailable)
                .append(additionalContractFileContentAvailable)
                .append(signedGrantOfferLetterFile)
                .append(grantOfferState)
                .append(grantOfferLetterRejectionReason)
                .toHashCode();
    }
}
