package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ProjectState;

import static org.innovateuk.ifs.project.resource.ProjectState.ON_HOLD;

/**
 * View model backing the internal members view of the Grant Offer Letter send page
 */
public class GrantOfferLetterModel {

    private final long competitionId;
    private final boolean h2020;
    private final FileDetailsViewModel grantOfferLetterFile;
    private final FileDetailsViewModel additionalContractFile;
    private final long projectId;
    private final String projectName;
    private final long applicationId;
    private final boolean grantOfferLetterFileContentAvailable;
    private final boolean additionalContractFileContentAvailable;
    private final FileDetailsViewModel signedGrantOfferLetterFile;
    private final GrantOfferLetterStateResource grantOfferState;
    private final String grantOfferLetterRejectionReason;
    private final ProjectState projectState;
    private final boolean useDocusignForGrantOfferLetter;

    public GrantOfferLetterModel(long competitionId,
                                 boolean h2020,
                                 FileDetailsViewModel grantOfferLetterFile,
                                 FileDetailsViewModel additionalContractFile,
                                 Long projectId,
                                 String projectName,
                                 Long applicationId,
                                 boolean grantOfferLetterFileContentAvailable,
                                 boolean additionalContractFileContentAvailable,
                                 FileDetailsViewModel signedGrantOfferLetterFile,
                                 GrantOfferLetterStateResource grantOfferState,
                                 String grantOfferLetterRejectionReason,
                                 ProjectState projectState,
                                 boolean useDocusignForGrantOfferLetter) {
        this.competitionId = competitionId;
        this.h2020 = h2020;
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
        this.projectState = projectState;
        this.useDocusignForGrantOfferLetter = useDocusignForGrantOfferLetter;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public boolean isH2020() {
        return h2020;
    }

    public GrantOfferLetterStateResource getGrantOfferState() {
        return grantOfferState;
    }

    public boolean isSentToProjectTeam() { return grantOfferState.isGeneratedGrantOfferLetterAlreadySentToProjectTeam(); }

    public FileDetailsViewModel getGrantOfferLetterFile() { return grantOfferLetterFile; }

    public FileDetailsViewModel getAdditionalContractFile() {
        return additionalContractFile;
    }

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

    public boolean isOnHold() {
        return ON_HOLD.equals(projectState);
    }

    public boolean isProjectIsActive() {
        return projectState.isActive();
    }

    public boolean isUseDocusignForGrantOfferLetter() {
        return useDocusignForGrantOfferLetter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantOfferLetterModel that = (GrantOfferLetterModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(h2020, that.h2020)
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
                .append(projectState, that.projectState)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(h2020)
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
                .append(projectState)
                .toHashCode();
    }
}
