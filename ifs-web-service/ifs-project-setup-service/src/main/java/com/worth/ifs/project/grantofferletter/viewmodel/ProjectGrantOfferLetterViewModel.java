package com.worth.ifs.project.grantofferletter.viewmodel;

import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.project.viewmodel.BasicProjectDetailsViewModel;

import java.time.LocalDateTime;

/**
 * A view model that backs the Project grant offer letter page
 **/
public class ProjectGrantOfferLetterViewModel implements BasicProjectDetailsViewModel {

    private final Long projectId;
    private final String projectName;
    private final boolean leadPartner;
    private FileDetailsViewModel grantOfferLetterFile;
    private FileDetailsViewModel signedGrantOfferLetterFile;
    private FileDetailsViewModel additionalContractFile;
    private LocalDateTime submitDate;
    private boolean offerRejected;
    private boolean offerAccepted;

    public ProjectGrantOfferLetterViewModel(Long projectId, String projectName, boolean leadPartner, FileDetailsViewModel grantOfferLetterFile,
                                            FileDetailsViewModel signedGrantOfferLetterFile, FileDetailsViewModel additionalContractFile,
                                            LocalDateTime submitDate, boolean offerRejected, boolean offerAccepted) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.leadPartner = leadPartner;
        this.grantOfferLetterFile = grantOfferLetterFile;
        this.signedGrantOfferLetterFile = signedGrantOfferLetterFile;
        this.additionalContractFile = additionalContractFile;
        this.submitDate = submitDate;
        this.offerRejected = offerRejected;
        this.offerAccepted = offerAccepted;
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

    public boolean isShowSubmitButton() {
        return !offerRejected;
    }

    public boolean isSubmitted() {
        return submitDate != null;
    }

    public FileDetailsViewModel getGrantOfferLetterFile() {
        return grantOfferLetterFile;
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
        return signedGrantOfferLetterFile != null;
    }

    public LocalDateTime getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(LocalDateTime submitDate) {
        this.submitDate = submitDate;
    }

    public FileDetailsViewModel getSignedGrantOfferLetterFile() {
        return signedGrantOfferLetterFile;
    }

    public void setSignedGrantOfferLetterFile(FileDetailsViewModel signedGrantOfferLetterFile) {
        this.signedGrantOfferLetterFile = signedGrantOfferLetterFile;
    }

    public boolean isOfferAccepted() {
        return offerAccepted;
    }

    public void setOfferAccepted(boolean offerAccepted) {
        this.offerAccepted = offerAccepted;
    }
}
