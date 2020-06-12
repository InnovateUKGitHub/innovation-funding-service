package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * View model to back the Monitoring Officer page
 */
public class LegacyMonitoringOfficerViewModel {

    private Long projectId;
    private String projectTitle;
    private Long applicationId;
    private String area;
    private LocalDate targetProjectStartDate;
    private String projectManagerName;
    private List<String> partnerOrganisationNames;
    private String leadOrganisationName;
    private CompetitionSummaryResource competitionSummary;
    private boolean existingMonitoringOfficer;
    private boolean editMode;
    private boolean editable;
    private List<String> primaryAddressLines;
    private boolean collaborativeProject;

    public LegacyMonitoringOfficerViewModel(ProjectResource project, String area, String projectManagerName,
                                            List<String> partnerOrganisationNames, String leadOrganisationName,
                                            CompetitionSummaryResource competitionSummary, boolean editable) {
        this.projectId = project.getId();
        this.projectTitle = project.getName();
        this.applicationId = project.getApplication();
        this.area = area;
        AddressResource primaryAddress = project.getAddress();
        this.primaryAddressLines = primaryAddress != null ? primaryAddress.getNonEmptyLinesInternational() : emptyList();
        this.targetProjectStartDate = project.getTargetStartDate();
        this.projectManagerName = projectManagerName;
        this.partnerOrganisationNames = partnerOrganisationNames;
        this.leadOrganisationName = leadOrganisationName;
        this.competitionSummary = competitionSummary;
        this.existingMonitoringOfficer = true;
        this.editMode = false;
        this.editable = editable;
        this.collaborativeProject = project.isCollaborativeProject();
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getArea() {
        return area;
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

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    public void setLeadOrganisationName(String leadOrganisationName) {
        this.leadOrganisationName = leadOrganisationName;
    }

    public List<String> getPrimaryAddressLines() {
        return primaryAddressLines;
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
        return isReadOnly() && isEditable();
    }

    public boolean isDisplayAssignMonitoringOfficerButton() {
        return isEditMode() && isEditable();
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }
}
