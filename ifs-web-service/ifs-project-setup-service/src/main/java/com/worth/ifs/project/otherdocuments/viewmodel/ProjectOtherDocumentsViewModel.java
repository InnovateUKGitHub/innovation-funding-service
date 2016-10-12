package com.worth.ifs.project.otherdocuments.viewmodel;

import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.project.viewmodel.BasicProjectDetailsViewModel;

import java.time.LocalDateTime;
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
    private boolean approvalDecisionMade;
    private boolean approved;
    private boolean leadPartner;
    private boolean submitAllowed;
    private LocalDateTime submitDate;

    public ProjectOtherDocumentsViewModel(Long projectId, String projectName, FileDetailsViewModel collaborationAgreementFileDetails,
                                          FileDetailsViewModel exploitationPlanFileDetails, List<String> partnerOrganisationNames, List<String> rejectionReasons, boolean leadPartner, boolean otherDocumentsSubmitted, boolean otherDocumentsApproved, boolean approvalDecisionMade, boolean submitAllowed, LocalDateTime submitDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.collaborationAgreementFileDetails = collaborationAgreementFileDetails;
        this.exploitationPlanFileDetails = exploitationPlanFileDetails;
        this.otherDocumentsSubmitted = otherDocumentsSubmitted;
        this.partnerOrganisationNames = partnerOrganisationNames;
        this.rejectionReasons = rejectionReasons;
        this.approved = otherDocumentsApproved;
        this.approvalDecisionMade = approvalDecisionMade;
        this.leadPartner = leadPartner;
        this.submitAllowed = submitAllowed;
        this.submitDate = submitDate;
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
        return leadPartner && !otherDocumentsSubmitted && !approvalDecisionMade;
    }

    public boolean isShowSubmitDocumentsButton() {
        return leadPartner && !otherDocumentsSubmitted;
    }

    public boolean isShowRejectionMessages() {
        return !rejectionReasons.isEmpty();
    }

    public boolean isShowGenericRejectionMessage() {
        return !isShowRejectionMessages() && approvalDecisionMade && !approved;
    }

    public List<String> getRejectionReasons() {
        return rejectionReasons;
    }

    public boolean isShowApprovedMessage() {
        return approvalDecisionMade && approved;
    }

    public boolean isLeadPartner(){
        return leadPartner;
    }

    public boolean isShowLeadPartnerGuidanceInformation() {
        return leadPartner && isEditable();
    }

    public boolean isShowDocumentsBeingReviewedMessage() {
        return otherDocumentsSubmitted && !approvalDecisionMade;
    }

    public boolean isSubmitAllowed() {
        return submitAllowed;
    }

    public LocalDateTime getSubmitDate() {
        return submitDate;
    }

}
