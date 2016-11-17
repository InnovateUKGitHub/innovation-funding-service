package com.worth.ifs.project.grantofferletter.send.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
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

    public ProjectGrantOfferLetterSendViewModel(CompetitionSummaryResource competitionSummary,
                                                FileDetailsViewModel grantOfferLetterFile,
                                                FileDetailsViewModel additionalContractFile,
                                                Boolean sentToProjectTeam,
                                                Long projectId,
                                                String projectName,
                                                Long applicationId) {
        this.competitionSummary = competitionSummary;
        this.grantOfferLetterFile = grantOfferLetterFile;
        this.additionalContractFile = additionalContractFile;
        this.sentToProjectTeam = sentToProjectTeam;
        this.projectId = projectId;
        this.projectName = projectName;
        this.applicationId = applicationId;
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
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionSummary)
                .append(grantOfferLetterFile)
                .append(additionalContractFile)
                .append(sentToProjectTeam)
                .toHashCode();
    }
}
