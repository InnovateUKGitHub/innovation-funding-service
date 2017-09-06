package org.innovateuk.ifs.assessment.panel.resource;

/**
 * DTO for assessment panel key statistics
 */
public class AssessmentPanelKeyStatisticsResource {


    private int applicationsInPanel;
    private int assessorsAccepted;
    private int assessorsPending;

    public AssessmentPanelKeyStatisticsResource(int applicationsInPanel, int assessorsAccepted, int assessorsPending) {
        this.applicationsInPanel = applicationsInPanel;
        this.assessorsAccepted = assessorsAccepted;
        this.assessorsPending = assessorsPending;
    }

    public AssessmentPanelKeyStatisticsResource() {
        // no-argument constructor
    }

    public int getApplicationsInPanel() {
        return applicationsInPanel;
    }

    public void setApplicationsInPanel(int applicationsInPanel) {
        this.applicationsInPanel = applicationsInPanel;
    }

    public int getAssessorsAccepted() {
        return assessorsAccepted;
    }

    public void setAssessorsAccepted(int assessorsAccepted) {
        this.assessorsAccepted = assessorsAccepted;
    }

    public int getAssessorsPending() {
        return assessorsPending;
    }

    public void setAssessorsPending(int assessorsPending) { this.assessorsPending = assessorsPending; }




}

