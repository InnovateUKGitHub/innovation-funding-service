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
    private final boolean leadPartner;
    private FileDetailsViewModel grantOfferLetterFile;
    private FileDetailsViewModel additionalContractFile;
    private boolean offerSigned;
    private boolean submitted;
    private LocalDateTime submitDate;
    private boolean offerAccepted;
    private boolean offerRejected;

    public ProjectGrantOfferLetterViewModel(Long projectId, String projectName, boolean leadPartner, FileDetailsViewModel grantOfferLetterFile,
                                            FileDetailsViewModel additionalContractFile,
                                            boolean offerSigned, boolean submitted, LocalDateTime submitDate, boolean offerAccepted, boolean offerRejected) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.leadPartner = leadPartner;
        this.grantOfferLetterFile = grantOfferLetterFile;
        this.additionalContractFile = additionalContractFile;
        this.offerSigned = offerSigned;
        this.submitted = submitted;
        this.submitDate = submitDate;
        this.offerAccepted = offerAccepted;
        this.offerRejected = offerRejected;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public boolean isLeadPartner() {
        return leadPartner;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public FileDetailsViewModel getGrantOfferLetterFile() {
        return grantOfferLetterFile;
    }

    public boolean isOfferAccepted() {
        return offerAccepted;
    }

    public void setOfferAccepted(boolean offerAccepted) {
        this.offerAccepted = offerAccepted;
    }

    public boolean isOfferRejected() {
        return offerRejected;
    }

    public void setOfferRejected(boolean offerRejected) {
        this.offerRejected = offerRejected;
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
