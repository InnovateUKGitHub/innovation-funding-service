package org.innovateuk.ifs.project.monitoringofficer.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

public class MonitoringOfficerDashboardForm extends BaseBindingResultTarget {

    private boolean projectInSetup;
    private boolean previousProject;

    private boolean documentsComplete;
    private boolean documentsIncomplete;
    private boolean documentsAwaitingReview;

    public MonitoringOfficerDashboardForm() {
    }

    public boolean isProjectInSetup() {
        return projectInSetup;
    }

    public void setProjectInSetup(boolean projectInSetup) {
        this.projectInSetup = projectInSetup;
    }

    public boolean isPreviousProject() {
        return previousProject;
    }

    public void setPreviousProject(boolean previousProject) {
        this.previousProject = previousProject;
    }

    public boolean isDocumentsComplete() {
        return documentsComplete;
    }

    public void setDocumentsComplete(boolean documentsComplete) {
        this.documentsComplete = documentsComplete;
    }

    public boolean isDocumentsIncomplete() {
        return documentsIncomplete;
    }

    public void setDocumentsIncomplete(boolean documentsIncomplete) {
        this.documentsIncomplete = documentsIncomplete;
    }

    public boolean isDocumentsAwaitingReview() {
        return documentsAwaitingReview;
    }

    public void setDocumentsAwaitingReview(boolean documentsAwaitingReview) {
        this.documentsAwaitingReview = documentsAwaitingReview;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MonitoringOfficerDashboardForm that = (MonitoringOfficerDashboardForm) o;

        return new EqualsBuilder()
                .append(projectInSetup, that.projectInSetup)
                .append(previousProject, that.previousProject)
                .append(documentsComplete, that.documentsComplete)
                .append(documentsIncomplete, that.documentsIncomplete)
                .append(documentsAwaitingReview, that.documentsAwaitingReview)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectInSetup)
                .append(previousProject)
                .append(documentsComplete)
                .append(documentsIncomplete)
                .append(documentsAwaitingReview)
                .toHashCode();
    }
}
