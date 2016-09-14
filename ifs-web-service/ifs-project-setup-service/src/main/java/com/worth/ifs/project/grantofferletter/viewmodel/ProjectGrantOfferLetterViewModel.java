package com.worth.ifs.project.grantofferletter.viewmodel;

import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.project.viewmodel.BasicProjectDetailsViewModel;

import java.time.LocalDateTime;

/**
 * Module: innovation-funding-service-dev
 **/
public class ProjectGrantOfferLetterViewModel implements BasicProjectDetailsViewModel {

    private final Long projectId;
    private final String projectName;
    private FileDetailsViewModel grantOfferLetterFile;
    private FileDetailsViewModel additionalContractFile;
    private boolean offerSigned;
    private LocalDateTime submitDate;

    public ProjectGrantOfferLetterViewModel(Long projectId, String projectName, FileDetailsViewModel grantOfferLetterFile,
                                            FileDetailsViewModel additionalContractFile,
                                            boolean offerSigned, LocalDateTime submitDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.grantOfferLetterFile = grantOfferLetterFile;
        this.additionalContractFile = additionalContractFile;
        this.offerSigned = offerSigned;
        this.submitDate = submitDate;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public FileDetailsViewModel getGrantOfferLetterFile() {
        return grantOfferLetterFile;
    }

    public void setGrantOfferLetterFile(FileDetailsViewModel grantOfferLetterFile) {
        this.grantOfferLetterFile = grantOfferLetterFile;
    }

    public FileDetailsViewModel getAdditionalContractFile() {
        return additionalContractFile;
    }

    public void setAdditionalContractFile(FileDetailsViewModel additionalContractFile) {
        this.additionalContractFile = additionalContractFile;
    }

    public boolean isOfferSigned() {
        return offerSigned;
    }

    public void setOfferSigned(boolean offerSigned) {
        this.offerSigned = offerSigned;
    }

    public LocalDateTime getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDateTime submitDate) {
        this.submitDate = submitDate;
    }
}
