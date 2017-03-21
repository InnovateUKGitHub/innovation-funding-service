package org.innovateuk.ifs.project.otherdocuments.viewmodel;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View model backing the Other Documents page
 */
public class ProjectOtherDocumentsViewModel implements BasicProjectDetailsViewModel {

    private Long projectId;
    private Long applicationId;
    private String projectName;
    private FileDetailsViewModel collaborationAgreementFileDetails;
    private FileDetailsViewModel exploitationPlanFileDetails;
    private boolean otherDocumentsSubmitted;
    private List<String> partnerOrganisationNames;
    private List<String> rejectionReasons;
    private boolean approvalDecisionMade;
    private ApprovalType approved;
    private boolean projectManager;
    private boolean submitAllowed;
    private LocalDateTime submitDate;

    public ProjectOtherDocumentsViewModel(Long projectId,
                                          Long applicationId, String projectName,
                                          FileDetailsViewModel collaborationAgreementFileDetails,
                                          FileDetailsViewModel exploitationPlanFileDetails,
                                          List<String> partnerOrganisationNames,
                                          List<String> rejectionReasons,
                                          boolean projectManager,
                                          boolean otherDocumentsSubmitted,
                                          ApprovalType otherDocumentsApproved,
                                          boolean submitAllowed,
                                          LocalDateTime submitDate) {
        this.projectId = projectId;
        this.applicationId = applicationId;
        this.projectName = projectName;
        this.collaborationAgreementFileDetails = collaborationAgreementFileDetails;
        this.exploitationPlanFileDetails = exploitationPlanFileDetails;
        this.otherDocumentsSubmitted = otherDocumentsSubmitted;
        this.partnerOrganisationNames = partnerOrganisationNames;
        this.rejectionReasons = rejectionReasons;
        this.approved = otherDocumentsApproved;
        this.approvalDecisionMade = !ApprovalType.UNSET.equals(otherDocumentsApproved);
        this.projectManager = projectManager;
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
        return projectManager && !otherDocumentsSubmitted && !ApprovalType.APPROVED.equals(approved);
    }

    public boolean isRejected() { return ApprovalType.REJECTED.equals(approved); }

    public boolean isShowSubmitDocumentsButton() {
        return projectManager && !otherDocumentsSubmitted && submitAllowed && !isRejected();
    }

    public boolean isShowDisabledSubmitDocumentsButton() { return (projectManager && !otherDocumentsSubmitted && !submitAllowed)  || isRejected(); }

    public boolean isShowRejectionMessages() {
        return !rejectionReasons.isEmpty();
    }

    public boolean isShowGenericRejectionMessage() {
        return !isShowRejectionMessages() && approvalDecisionMade && ApprovalType.REJECTED.equals(approved);
    }

    public List<String> getRejectionReasons() {
        return rejectionReasons;
    }

    public boolean isShowApprovedMessage() {
        return approvalDecisionMade && ApprovalType.APPROVED.equals(approved);
    }

    public boolean isShowGuidanceInformation() {
        return !otherDocumentsSubmitted && !ApprovalType.APPROVED.equals(approved);
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

    public boolean isProjectManager() {
        return projectManager;
    }

    public Long getApplicationId() {
        return applicationId;
    }
}
