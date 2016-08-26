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
    private List<String> partnerOrganisationNames;

    private String leadPartnerOrganisationName;
    private String projectManagerName;
    private String projectManagerTelephone;
    private String projectManagerEmail;

    //TODO: Probably a new projectDocuments object
    private String rejectionReason;
    private boolean approved;
    private boolean rejected;

    public ProjectOtherDocumentsViewModel(Long projectId, String projectName, String leadPartnerOrganisationName, String projectManagerName,
                                          String projectManagerTelephone, String projectManagerEmail, FileDetailsViewModel collaborationAgreementFileDetails,
                                          FileDetailsViewModel exploitationPlanFileDetails, List<String> partnerOrganisationNames, boolean approved,
                                          boolean rejected, String rejectionReason
                                          ) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.leadPartnerOrganisationName = leadPartnerOrganisationName;
        this.projectManagerName = projectManagerName;
        this.projectManagerTelephone = projectManagerTelephone;
        this.projectManagerEmail = projectManagerEmail;
        this.collaborationAgreementFileDetails = collaborationAgreementFileDetails;
        this.exploitationPlanFileDetails = exploitationPlanFileDetails;
        this.partnerOrganisationNames = partnerOrganisationNames;
        this.approved = approved;
        this.rejected = rejected;
        this.rejectionReason = rejectionReason;
    }

    @Override
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

    public String getLeadPartnerOrganisationName() { return leadPartnerOrganisationName;}

    public String getProjectManagerName() { return projectManagerName;}

    public String getProjectManagerTelephone() { return projectManagerTelephone;}

    public String getProjectManagerEmail() { return projectManagerEmail;}

    public boolean isShowRejectionMessages() {
        return rejectionReason != null;
    }

    public boolean isRejected() {
        return rejected;
    }

    public String getRejectionReasons() {
        return rejectionReason;
    }

    public boolean isApproved() {
        return approved;
    }

}
