package org.innovateuk.ifs.assessment.panel.resource;

/**
 * Created by ecormack on 01/09/17.
 */
public class AssessmentPanelKeyStatisticsResource {


    private Long applicationsInPanel;
    private Long assessorsAccepted;
    private Long assessorsPending;

    public AssessmentPanelKeyStatisticsResource(Long applicationsInPanel, Long assessorsAccepted, Long assessorsPending) {
        this.applicationsInPanel = applicationsInPanel;
        this.assessorsAccepted = assessorsAccepted;
        this.assessorsPending = assessorsPending;
    }

    public AssessmentPanelKeyStatisticsResource() {
        // no-argument constructor
    }

    public Long getApplicationsInPanel() {
        return applicationsInPanel;
    }

    public void setApplicationsInPanel(Long applicationsInPanel) {
        this.applicationsInPanel = applicationsInPanel;
    }

    public Long getAssessorsAccepted() {
        return assessorsAccepted;
    }

    public void setAssessorsAccepted(Long assessorsAccepted) {
        this.assessorsAccepted = assessorsAccepted;
    }

    public Long getAssessorsPending() {
        return assessorsPending;
    }

    public void setAssessorsPending(Long assessorsPending) { this.assessorsPending = assessorsPending; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssessmentPanelKeyStatisticsResource that = (AssessmentPanelKeyStatisticsResource) o;

        if (applicationsInPanel != null ? !applicationsInPanel.equals(that.applicationsInPanel) : that.applicationsInPanel != null)
            return false;
        if (assessorsAccepted != null ? !assessorsAccepted.equals(that.assessorsAccepted) : that.assessorsAccepted != null)
            return false;
        return assessorsPending != null ? assessorsPending.equals(that.assessorsPending) : that.assessorsPending == null;
    }

    @Override
    public int hashCode() {
        int result = applicationsInPanel != null ? applicationsInPanel.hashCode() : 0;
        result = 31 * result + (assessorsAccepted != null ? assessorsAccepted.hashCode() : 0);
        result = 31 * result + (assessorsPending != null ? assessorsPending.hashCode() : 0);
        return result;
    }
}

