package org.innovateuk.ifs.financecheck.eligibility.viewmodel;


import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.time.LocalDate;

/**
 * View model backing the internal Finance Team members view of the Finance Check Eligibility page
 */
public class FinanceChecksEligibilityViewModel {
    private FinanceCheckEligibilityResource eligibilityOverview;
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private String projectName;
    private Long applicationId;
    private Long projectId;
    private Long organisationId;

    private boolean eligibilityApproved;
    private EligibilityRagStatus eligibilityRagStatus;
    private String approverFirstName;
    private String approverLastName;
    private LocalDate approvalDate;

    private boolean externalView;
    private boolean isUsingJesFinances;
    private FileDetailsViewModel jesFileDetails;
    private final boolean h2020;
    private final boolean projectIsActive;
    private final boolean loanCompetition;
    private final boolean collaborativeProject;

    public FinanceChecksEligibilityViewModel(ProjectResource project,
                                             CompetitionResource competition,
                                             FinanceCheckEligibilityResource eligibilityOverview,
                                             String organisationName,
                                             boolean leadPartnerOrganisation,
                                             Long organisationId,
                                             boolean eligibilityApproved,
                                             EligibilityRagStatus eligibilityRagStatus,
                                             String approverFirstName,
                                             String approverLastName,
                                             LocalDate approvalDate,
                                             boolean externalView,
                                             boolean isUsingJesFinances,
                                             FileDetailsViewModel jesFileDetailsViewModel) {
        this.projectName = project.getName();
        this.applicationId = project.getApplication();
        this.projectId = project.getId();
        this.projectIsActive = project.getProjectState().isActive();
        this.collaborativeProject = project.isCollaborativeProject();
        this.h2020 = competition.isH2020();
        this.loanCompetition = competition.isLoan();
        this.eligibilityOverview = eligibilityOverview;
        this.organisationName = organisationName;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.organisationId = organisationId;
        this.eligibilityApproved = eligibilityApproved;
        this.eligibilityRagStatus = eligibilityRagStatus;
        this.approverFirstName = approverFirstName;
        this.approverLastName = approverLastName;
        this.approvalDate = approvalDate;
        this.externalView = externalView;
        this.isUsingJesFinances = isUsingJesFinances;
        this.jesFileDetails = jesFileDetailsViewModel;
    }

    public boolean isApproved() {
        return eligibilityApproved;
    }

    public boolean isShowSaveAndContinueButton() {
        return !isApproved() && projectIsActive;
    }

    public boolean isShowBackToFinanceCheckButton() {
        return isApproved() || !projectIsActive;
    }

    public boolean isShowApprovalMessage() {
        return isApproved();
    }

    public String getApproverName()
    {
        return StringUtils.trim(getApproverFirstName() + " " + getApproverLastName());
    }

    public FinanceCheckEligibilityResource getEligibilityOverview() {
        return eligibilityOverview;
    }

    public void setEligibilityOverview(FinanceCheckEligibilityResource eligibilityResource) {
        this.eligibilityOverview = eligibilityResource;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public boolean isLeadPartnerOrganisation() {
        return leadPartnerOrganisation;
    }

    public void setLeadPartnerOrganisation(boolean leadPartnerOrganisation) {
        this.leadPartnerOrganisation = leadPartnerOrganisation;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public boolean isEligibilityApproved() {
        return eligibilityApproved;
    }

    public void setEligibilityApproved(boolean eligibilityApproved) {
        this.eligibilityApproved = eligibilityApproved;
    }

    public EligibilityRagStatus getEligibilityRagStatus() {
        return eligibilityRagStatus;
    }

    public void setEligibilityRagStatus(EligibilityRagStatus eligibilityRagStatus) {
        this.eligibilityRagStatus = eligibilityRagStatus;
    }

    public String getApproverFirstName() {
        return approverFirstName;
    }

    public void setApproverFirstName(String approverFirstName) {
        this.approverFirstName = approverFirstName;
    }

    public String getApproverLastName() {
        return approverLastName;
    }

    public void setApproverLastName(String approverLastName) {
        this.approverLastName = approverLastName;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public boolean isExternalView() {
        return externalView;
    }

    public void setExternalView(boolean externalView) {
        this.externalView = externalView;
    }

    public boolean isUsingJesFinances() {
        return isUsingJesFinances;
    }

    public void setUsingJesFinances(boolean usingJesFinances) {
        isUsingJesFinances = usingJesFinances;
    }

    public FileDetailsViewModel getJesFileDetails() {
        return jesFileDetails;
    }

    public void setJesFileDetails(FileDetailsViewModel jesFileDetails) {
        this.jesFileDetails = jesFileDetails;
    }

    public boolean isH2020() {
        return h2020;
    }

    public boolean isProjectIsActive() {
        return projectIsActive;
    }

    public boolean isLoanCompetition() {
        return loanCompetition;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isShowChangesLink() {
        return eligibilityOverview.isHasApplicationFinances();
    }
}
