package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * View model backing the internal members view of the Grant Offer Letter send page
 */

public class ProjectGrantOfferLetterSendViewModel {
    private CompetitionSummaryResource competitionSummary;
    private FileDetailsViewModel grantOfferLetterFile;
    private FileDetailsViewModel additionalContractFile;
    private Boolean sentToProjectTeam;
    private Long projectId;
    private String projectName;
    private Long applicationId;
    private Boolean grantOfferLetterFileContentAvailable;
    private Boolean additionalContractFileContentAvailable;
    private Boolean signedGrantOfferLetterApproved;
    private Boolean signedGrantOfferLetterAvailable;
    private FileDetailsViewModel signedGrantOfferLetterFile;

    public ProjectGrantOfferLetterSendViewModel(CompetitionSummaryResource competitionSummary,
                                                FileDetailsViewModel grantOfferLetterFile,
                                                FileDetailsViewModel additionalContractFile,
                                                Boolean sentToProjectTeam,
                                                Long projectId,
                                                String projectName,
                                                Long applicationId,
                                                Boolean grantOfferLetterFileContentAvailable,
                                                Boolean additionalContractFileContentAvailable,
                                                Boolean signedGrantOfferLetterApproved,
                                                Boolean signedGrantOfferLetterAvailable,
                                                FileDetailsViewModel signedGrantOfferLetterFile) {
        this.competitionSummary = competitionSummary;
        this.grantOfferLetterFile = grantOfferLetterFile;
        this.additionalContractFile = additionalContractFile;
        this.sentToProjectTeam = sentToProjectTeam;
        this.projectId = projectId;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.grantOfferLetterFileContentAvailable = grantOfferLetterFileContentAvailable;
        this.additionalContractFileContentAvailable = additionalContractFileContentAvailable;
        this.signedGrantOfferLetterApproved = signedGrantOfferLetterApproved;
        this.signedGrantOfferLetterAvailable = signedGrantOfferLetterAvailable;
        this.signedGrantOfferLetterFile = signedGrantOfferLetterFile;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }


    public Boolean isSentToProjectTeam() { return this.sentToProjectTeam; }



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

    public Boolean getSignedGrantOfferLetterApproved() { return signedGrantOfferLetterApproved; }

    public Boolean getSignedGrantOfferLetterFileAvailable() { return signedGrantOfferLetterAvailable; }

    public boolean isShowRemoveOfferLetterButton() {
        return grantOfferLetterFile != null && !sentToProjectTeam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectGrantOfferLetterSendViewModel that = (ProjectGrantOfferLetterSendViewModel) o;

        return new EqualsBuilder()
                .append(competitionSummary, that.competitionSummary)
                .append(grantOfferLetterFile, that.grantOfferLetterFile)
                .append(additionalContractFile, that.additionalContractFile)
                .append(sentToProjectTeam, that.sentToProjectTeam)
                .append(projectId, that.projectId)
                .append(projectName, that.projectName)
                .append(applicationId, that.applicationId)
                .append(grantOfferLetterFileContentAvailable, that.grantOfferLetterFileContentAvailable)
                .append(additionalContractFileContentAvailable, that.additionalContractFileContentAvailable)
                .append(signedGrantOfferLetterApproved, that.signedGrantOfferLetterApproved)
                .append(signedGrantOfferLetterFile, that.signedGrantOfferLetterFile)
                .append(signedGrantOfferLetterAvailable, that.signedGrantOfferLetterAvailable)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionSummary)
                .append(grantOfferLetterFile)
                .append(additionalContractFile)
                .append(sentToProjectTeam)
                .append(projectName)
                .append(applicationId)
                .append(grantOfferLetterFileContentAvailable)
                .append(additionalContractFileContentAvailable)
                .append(signedGrantOfferLetterApproved)
                .append(signedGrantOfferLetterFile)
                .append(signedGrantOfferLetterAvailable)
                .toHashCode();
    }
}
