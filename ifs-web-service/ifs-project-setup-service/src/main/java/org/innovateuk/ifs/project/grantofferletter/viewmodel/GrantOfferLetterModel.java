package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;

/**
 * A view model that backs the Project grant offer letter page
 **/
public class GrantOfferLetterModel implements BasicProjectDetailsViewModel {

    private final Long projectId;
    private final String projectName;
    private final boolean leadPartner;
    private final boolean projectManager;
    private FileDetailsViewModel grantOfferLetterFile;
    private FileDetailsViewModel signedGrantOfferLetterFile;
    private FileDetailsViewModel additionalContractFile;
    private GrantOfferLetterStateResource golState;
    private boolean useDocusign;

    public GrantOfferLetterModel(Long projectId, String projectName, boolean leadPartner, FileDetailsViewModel grantOfferLetterFile,
                                 FileDetailsViewModel signedGrantOfferLetterFile, FileDetailsViewModel additionalContractFile,
                                 boolean projectManager, GrantOfferLetterStateResource golState, boolean useDocusign) {

        this.projectId = projectId;
        this.projectName = projectName;
        this.leadPartner = leadPartner;
        this.grantOfferLetterFile = grantOfferLetterFile;
        this.signedGrantOfferLetterFile = signedGrantOfferLetterFile;
        this.additionalContractFile = additionalContractFile;
        this.projectManager = projectManager;
        this.golState = golState;
        this.useDocusign = useDocusign;
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

    public GrantOfferLetterStateResource getGolState() {
        return golState;
    }

    public boolean isSubmitted() {
        return golState.isSignedGrantOfferLetterReceivedByInternalTeam();
    }

    public FileDetailsViewModel getGrantOfferLetterFile() {
        return grantOfferLetterFile;
    }

    public boolean isGrantOfferLetterApproved() {
        return golState.isSignedGrantOfferLetterApproved();
    }

    public FileDetailsViewModel getAdditionalContractFile() {
        return additionalContractFile;
    }

    public boolean isOfferSigned() {
        return signedGrantOfferLetterFile != null;
    }

    public FileDetailsViewModel getSignedGrantOfferLetterFile() {
        return signedGrantOfferLetterFile;
    }

    public boolean isUseDocusign() {
        return useDocusign;
    }

    public boolean isShowSubmitButton() {
        return projectManager && !isSubmitted() && isOfferSigned() && grantOfferLetterFile != null;
    }

    public boolean isShowDisabledSubmitButton() {

        if (!projectManager) {
            return false;
        }

        if (isSubmitted()) {
            return false;
        }

        return !isOfferSigned();
    }

    public boolean isProjectManager() {
        return projectManager;
    }

    public boolean isGrantOfferLetterSent() {
        return golState.isGeneratedGrantOfferLetterAlreadySentToProjectTeam();
    }

    public boolean isGrantOfferLetterRejected() {
        return golState.isSignedGrantOfferLetterRejected();
    }

    public boolean isShowGrantOfferLetterRejectedMessage() {

        if (!isProjectManager()) {
            return false;
        }

        return isGrantOfferLetterRejected();
    }

    public boolean isAbleToRemoveSignedGrantOffer() {

        if (getSignedGrantOfferLetterFile() == null) {
            return false;
        }

        if (isGrantOfferLetterApproved()) {
            return false;
        }

        if (isGrantOfferLetterRejected()) {
            return isProjectManager();
        }

        if (!isSubmitted()) {
            return isLeadPartner();
        }

        return false;
    }

    public boolean isShowGrantOfferLetterReceivedByInnovateMessage() {

        if (isGrantOfferLetterRejected() && !isLeadPartner()) {
            return true;
        }

        if (isGrantOfferLetterApproved()) {
            return false;
        }

        return isSubmitted();
    }

    public boolean isShowAwaitingSignatureFromLeadPartnerMessage() {

        if (isLeadPartner()) {
            return false;
        }

        if (!isGrantOfferLetterSent()) {
            return false;
        }

        return !isSubmitted();
    }

    public boolean isShowGrantOfferLetterApprovedByInnovateMessage() {
        return isGrantOfferLetterApproved();
    }

}
