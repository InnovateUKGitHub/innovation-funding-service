package com.worth.ifs.project.controller.viewmodel;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;

import java.time.LocalDate;
import java.util.List;

/**
 * View model to back the Monitoring Officer page
 */
public class ProjectMonitoringOfficerViewModel {

    private String projectTitle;
    private String area;
    private AddressResource primaryAddress;
    private LocalDate targetProjectStartDate;
    private String projectManagerName;
    private List<String> partnerOrganisationNames;
    private CompetitionSummaryResource competitionSummary;
    private boolean existingMonitoringOfficer;
    private boolean editMode;

    public ProjectMonitoringOfficerViewModel(String projectTitle, String area, AddressResource primaryAddress, LocalDate targetProjectStartDate, String projectManagerName, List<String> partnerOrganisationNames, CompetitionSummaryResource competitionSummary, boolean existingMonitoringOfficer, boolean editMode) {
        this.projectTitle = projectTitle;
        this.area = area;
        this.primaryAddress = primaryAddress;
        this.targetProjectStartDate = targetProjectStartDate;
        this.projectManagerName = projectManagerName;
        this.partnerOrganisationNames = partnerOrganisationNames;
        this.competitionSummary = competitionSummary;
        this.existingMonitoringOfficer = existingMonitoringOfficer;
        this.editMode = editMode;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getArea() {
        return area;
    }

    public AddressResource getPrimaryAddress() {
        return primaryAddress;
    }

    public LocalDate getTargetProjectStartDate() {
        return targetProjectStartDate;
    }

    public String getProjectManagerName() {
        return projectManagerName;
    }

    public List<String> getPartnerOrganisationNames() {
        return partnerOrganisationNames;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }

    public boolean isReadOnly() {
        return !editMode;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public boolean isExistingMonitoringOfficer() {
        return existingMonitoringOfficer;
    }

    public boolean isDisplayMonitoringOfficerAssignedMessage() {
        return existingMonitoringOfficer && isReadOnly();
    }

    public boolean isDisplayChangeMonitoringOfficerLink() {
        return isReadOnly();
    }

    public boolean isDisplayAssignMonitoringOfficerLink() {
        return isEditMode();
    }
}
