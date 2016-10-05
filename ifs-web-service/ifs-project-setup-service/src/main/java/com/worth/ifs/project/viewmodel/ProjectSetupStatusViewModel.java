package com.worth.ifs.project.viewmodel;

import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;

import java.util.Optional;

/**
 * A view model that backs the Project Status page
 */
public class ProjectSetupStatusViewModel implements BasicProjectDetailsViewModel {

    private Long projectId;
    private String projectName;
    private Long applicationId;
    private String competitionName;
    private boolean projectDetailsSubmitted;
    private boolean partnerDocumentsSubmitted;
    private boolean monitoringOfficerAssigned;
    private boolean grantOfferLetterSubmitted;
    private String monitoringOfficerName;
    private BankDetailsResource bankDetails;
    private boolean isFunded;
    private Long organisationId;

    public ProjectSetupStatusViewModel(ProjectResource project, CompetitionResource competition, Optional<MonitoringOfficerResource> monitoringOfficerResource, Optional<BankDetailsResource> bankDetails, boolean isFunded, Long organisationId, boolean projectDetailsSubmitted, boolean grantOfferLetterSubmitted) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.applicationId = project.getApplication();
        this.competitionName = competition.getName();
        this.projectDetailsSubmitted = projectDetailsSubmitted;
        this.partnerDocumentsSubmitted = project.isPartnerDocumentsSubmitted();
        this.monitoringOfficerAssigned = monitoringOfficerResource.isPresent();
        this.monitoringOfficerName = monitoringOfficerResource.map(mo -> mo.getFullName()).orElse("");
        this.bankDetails = bankDetails.orElse(null);
        this.isFunded = isFunded;
        this.organisationId = organisationId;
        this.grantOfferLetterSubmitted = grantOfferLetterSubmitted;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isProjectDetailsSubmitted() {
        return projectDetailsSubmitted;
    }

    public boolean isPartnerDocumentsSubmitted() {
        return partnerDocumentsSubmitted;
    }

    public boolean isMonitoringOfficerAssigned() {
        return monitoringOfficerAssigned;
    }

    public String getMonitoringOfficerName() {
        return monitoringOfficerName;
    }

    public BankDetailsResource getBankDetails() {
        return bankDetails;
    }

    public boolean isFunded() {
        return isFunded;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public boolean isGrantOfferLetterSubmitted() {
        return grantOfferLetterSubmitted;
    }
}
