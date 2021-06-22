package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

/**
 * Monitoring officer dashboard view model for Documents section
 */
public class MonitoringOfficerDashboardDocumentSectionViewModel {

    private final String documentSectionStatus;
    private final boolean hasDocumentSection;
    private final long projectId;

    public MonitoringOfficerDashboardDocumentSectionViewModel(String documentSectionStatus, boolean hasDocumentSection, long projectId) {
        this.documentSectionStatus = documentSectionStatus;
        this.hasDocumentSection = hasDocumentSection;
        this.projectId = projectId;
    }

    public String getDocumentSectionStatus() {
        return documentSectionStatus;
    }

    public boolean isHasDocumentSection() {
        return hasDocumentSection;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getDocumentLinkUrl() {
        return String.format("/project-setup/project/%s/document/all", projectId);
    }
}
