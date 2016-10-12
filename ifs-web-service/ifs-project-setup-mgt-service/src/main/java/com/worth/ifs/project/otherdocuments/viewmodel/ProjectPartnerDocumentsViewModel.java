package com.worth.ifs.project.otherdocuments.viewmodel;

import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.project.viewmodel.BasicProjectDetailsViewModel;

import java.util.List;

/**
 * View model backing the Other Documents page
 */
public class ProjectPartnerDocumentsViewModel implements BasicProjectDetailsViewModel {

    private Long projectId;
    private String projectName;
    private Long competitionId;

    private FileDetailsViewModel collaborationAgreementFileDetails;
    private FileDetailsViewModel exploitationPlanFileDetails;
    private List<String> partnerOrganisationNames;

    private String leadPartnerOrganisationName;
    private String projectManagerName;
    private String projectManagerTelephone;
    private String projectManagerEmail;

    private Boolean approved;

    public ProjectPartnerDocumentsViewModel(Long projectId, String projectName, Long competitionId, String leadPartnerOrganisationName, String projectManagerName,
                                            String projectManagerTelephone, String projectManagerEmail, FileDetailsViewModel collaborationAgreementFileDetails,
                                            FileDetailsViewModel exploitationPlanFileDetails, List<String> partnerOrganisationNames, Boolean approved
                                          ) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.competitionId = competitionId;
        this.leadPartnerOrganisationName = leadPartnerOrganisationName;
        this.projectManagerName = projectManagerName;
        this.projectManagerTelephone = projectManagerTelephone;
        this.projectManagerEmail = projectManagerEmail;
        this.collaborationAgreementFileDetails = collaborationAgreementFileDetails;
        this.exploitationPlanFileDetails = exploitationPlanFileDetails;
        this.partnerOrganisationNames = partnerOrganisationNames;
        this.approved = approved;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public Long getCompetitionId() {
        return competitionId;
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

    public Boolean isApproved() {
        return approved;
    }

    public boolean isShowApproveRejectButtons() { return approved != null && approved; }

}
