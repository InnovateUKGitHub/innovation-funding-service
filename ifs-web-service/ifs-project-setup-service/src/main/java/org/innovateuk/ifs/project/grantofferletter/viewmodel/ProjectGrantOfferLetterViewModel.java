package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;

import java.time.ZonedDateTime;

/**
 * A view model that backs the Project grant offer letter page
 **/
public class ProjectGrantOfferLetterViewModel implements BasicProjectDetailsViewModel {

    private final Long projectId;
    private final String projectName;
    private final boolean leadPartner;
    private final boolean projectManager;
    private FileDetailsViewModel grantOfferLetterFile;
    private FileDetailsViewModel signedGrantOfferLetterFile;
    private FileDetailsViewModel additionalContractFile;
    private ZonedDateTime submitDate;
    private boolean offerApproved;
    private boolean isGrantOfferLetterSent;

    public ProjectGrantOfferLetterViewModel(Long projectId, String projectName, boolean leadPartner, FileDetailsViewModel grantOfferLetterFile,
                                            FileDetailsViewModel signedGrantOfferLetterFile, FileDetailsViewModel additionalContractFile,
                                            ZonedDateTime submitDate, boolean offerApproved, boolean projectManager, boolean isGrantOfferLetterSent) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.leadPartner = leadPartner;
        this.grantOfferLetterFile = grantOfferLetterFile;
        this.signedGrantOfferLetterFile = signedGrantOfferLetterFile;
        this.additionalContractFile = additionalContractFile;
        this.submitDate = submitDate;
        this.offerApproved = offerApproved;
        this.projectManager = projectManager;
        this.isGrantOfferLetterSent = isGrantOfferLetterSent;
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
        return submitDate != null;
    }

    public FileDetailsViewModel getGrantOfferLetterFile() {
        return grantOfferLetterFile;
    }

    public boolean isOfferApproved() {
        return offerApproved;
    }

    public void setOfferApproved(boolean offerApproved) {
        this.offerApproved = offerApproved;
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

    public ZonedDateTime getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(ZonedDateTime submitDate) {
        this.submitDate = submitDate;
    }

    public FileDetailsViewModel getSignedGrantOfferLetterFile() {
        return signedGrantOfferLetterFile;
    }

    public void setSignedGrantOfferLetterFile(FileDetailsViewModel signedGrantOfferLetterFile) {
        this.signedGrantOfferLetterFile = signedGrantOfferLetterFile;
    }

    public boolean isShowSubmitButton() { return projectManager && !isSubmitted() && isOfferSigned() && grantOfferLetterFile != null; }

    public boolean isShowDisabledSubmitButton() { return leadPartner && (!isOfferSigned() || !projectManager); }

    public boolean isProjectManager() {
        return projectManager;
    }

    public boolean isGrantOfferLetterSent() { return isGrantOfferLetterSent; }
}
