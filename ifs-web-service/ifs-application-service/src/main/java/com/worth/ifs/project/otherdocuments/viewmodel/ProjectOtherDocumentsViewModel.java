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



    public ProjectOtherDocumentsViewModel(Long projectId, String projectName, FileDetailsViewModel collaborationAgreementFileDetails,
                                          FileDetailsViewModel exploitationPlanFileDetails, boolean otherDocumentsSubmitted,
                                          List<String> partnerOrganisationNames) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.collaborationAgreementFileDetails = collaborationAgreementFileDetails;
        this.exploitationPlanFileDetails = exploitationPlanFileDetails;
        this.otherDocumentsSubmitted = otherDocumentsSubmitted;
        this.partnerOrganisationNames = partnerOrganisationNames;
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
        return !otherDocumentsSubmitted; // TODO DW - permissions
    }

    public boolean isShowSubmitDocumentsButton() {
        return !otherDocumentsSubmitted;
    }
}
