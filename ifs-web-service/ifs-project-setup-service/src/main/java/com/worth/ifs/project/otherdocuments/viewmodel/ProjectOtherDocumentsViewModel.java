package com.worth.ifs.project.otherdocuments.viewmodel;

import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.project.viewmodel.BasicProjectDetailsViewModel;

import java.util.List;

/**
 * View model backing the Other Documents page
 */
public class ProjectOtherDocumentsViewModel implements BasicProjectDetailsViewModel {

    private Long projectId;
    private String projectName;
    private FileDetailsViewModel collaborationAgreementFileDetails;
    private FileDetailsViewModel exploitationPlanFileDetails;
    private boolean otherDocumentsSubmitted;
    private List<String> partnerOrganisationNames;
    private List<String> rejectionReasons;
    private boolean approved;
    private boolean leadPartner;
    private boolean submitAllowed;

    public ProjectOtherDocumentsViewModel(Long projectId, String projectName, FileDetailsViewModel collaborationAgreementFileDetails,
                                          FileDetailsViewModel exploitationPlanFileDetails, List<String> partnerOrganisationNames, List<String> rejectionReasons, boolean leadPartner, boolean otherDocumentsSubmitted, boolean otherDocumentsApproved, boolean submitAllowed) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.collaborationAgreementFileDetails = collaborationAgreementFileDetails;
        this.exploitationPlanFileDetails = exploitationPlanFileDetails;
        this.otherDocumentsSubmitted = otherDocumentsSubmitted;
        this.partnerOrganisationNames = partnerOrganisationNames;
        this.rejectionReasons = rejectionReasons;
        this.approved = otherDocumentsApproved;
        this.leadPartner = leadPartner;
        this.submitAllowed = submitAllowed;
    }

    public Long getProjectId() {
        return projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public FileDetailsViewModel getCollaborationAgreementFileDetails() {
        return collaborationAgreementFileDetails;
    }

    public FileDetailsViewModel getExploitationPlanFileDetails() {
        return exploitationPlanFileDetails;
    }

    public List<String> getPartnerOrganisationNames() {
        return partnerOrganisationNames;
    }

    public boolean isReadOnly() {
        return !isEditable();
    }

    public boolean isEditable() {
        return leadPartner && !otherDocumentsSubmitted && !approved;
    }

    public boolean isShowSubmitDocumentsButton() {
        return leadPartner && !otherDocumentsSubmitted;
    }

    public boolean isShowRejectionMessages() {
        return !rejectionReasons.isEmpty();
    }

    public List<String> getRejectionReasons() {
        return rejectionReasons;
    }

    public boolean isShowApprovedMessage() {
        return approved;
    }

    public boolean isLeadPartner(){
        return leadPartner;
    }

    public boolean isShowLeadPartnerGuidanceInformation() {
        return leadPartner && isEditable();
    }

    public boolean isShowDocumentsBeingReviewedMessage() {
        return otherDocumentsSubmitted && !approved;
    }

    public boolean isSubmitAllowed() {
        return submitAllowed;
    }
}
