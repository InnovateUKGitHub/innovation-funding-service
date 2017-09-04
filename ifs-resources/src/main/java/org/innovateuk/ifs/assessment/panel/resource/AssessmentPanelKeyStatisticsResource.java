package org.innovateuk.ifs.assessment.panel.resource;

/**
 * Created by ecormack on 01/09/17.
 */
public class AssessmentPanelKeyStatisticsResource {


    private Long applicationsInPanel;
    private Long assessorsAccepted;
    private Long assessorsInvited;

    public AssessmentPanelKeyStatisticsResource(Long applicationsInPanel, Long assessorsAccepted, Long assessorsInvited) {
        this.applicationsInPanel = applicationsInPanel;
        this.assessorsAccepted = assessorsAccepted;
        this.assessorsInvited = assessorsInvited;
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

    public Long getAssessorsInvited() {
        return assessorsInvited;
    }

    public void setAssessorsInvited(Long assessorsInvited) {
        this.assessorsInvited = assessorsInvited;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssessmentPanelKeyStatisticsResource that = (AssessmentPanelKeyStatisticsResource) o;

        if (applicationsInPanel != null ? !applicationsInPanel.equals(that.applicationsInPanel) : that.applicationsInPanel != null)
            return false;
        if (assessorsAccepted != null ? !assessorsAccepted.equals(that.assessorsAccepted) : that.assessorsAccepted != null)
            return false;
        return assessorsInvited != null ? assessorsInvited.equals(that.assessorsInvited) : that.assessorsInvited == null;
    }

    @Override
    public int hashCode() {
        int result = applicationsInPanel != null ? applicationsInPanel.hashCode() : 0;
        result = 31 * result + (assessorsAccepted != null ? assessorsAccepted.hashCode() : 0);
        result = 31 * result + (assessorsInvited != null ? assessorsInvited.hashCode() : 0);
        return result;
    }
}
