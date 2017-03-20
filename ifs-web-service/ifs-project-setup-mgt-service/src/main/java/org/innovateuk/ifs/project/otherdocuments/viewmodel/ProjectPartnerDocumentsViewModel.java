package org.innovateuk.ifs.project.otherdocuments.viewmodel;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;

import java.util.List;

/**
 * View model backing the Other Documents page
 */
public class ProjectPartnerDocumentsViewModel implements BasicProjectDetailsViewModel {

    private Long projectId;
    private Long applicationId;
    private String projectName;
    private Long competitionId;

    private FileDetailsViewModel collaborationAgreementFileDetails;
    private FileDetailsViewModel exploitationPlanFileDetails;
    private List<String> partnerOrganisationNames;

    private String leadPartnerOrganisationName;
    private String projectManagerName;
    private String projectManagerTelephone;
    private String projectManagerEmail;

    private ApprovalType approved;

    public ProjectPartnerDocumentsViewModel(Long projectId, long applicationId, String projectName, Long competitionId, String leadPartnerOrganisationName, String projectManagerName,
                                            String projectManagerTelephone, String projectManagerEmail, FileDetailsViewModel collaborationAgreementFileDetails,
                                            FileDetailsViewModel exploitationPlanFileDetails, List<String> partnerOrganisationNames, ApprovalType approved
                                          ) {
        this.projectId = projectId;
        this.applicationId = applicationId;
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
    public Long getApplicationId() {
        return applicationId;
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
        return ApprovalType.APPROVED.equals(approved);
    }

    public Boolean isRejected() { return ApprovalType.REJECTED.equals(approved); }

    public Boolean isShowApproveRejectButtons() { return ApprovalType.UNSET.equals(approved); }

}
