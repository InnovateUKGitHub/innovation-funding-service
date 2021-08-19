package org.innovateuk.ifs.project.monitoringofficer.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class MonitoringOfficerDashboardForm extends BaseBindingResultTarget {

    @Min(value = 3, message = "{validation.modashboard.filterprojects.keywords.min.length}")
    @Max(value = 50, message = "{validation.modashboard.filterprojects.keywords.max.length}")
    private String keywords;
    private boolean projectInSetup;
    private boolean previousProject;

    private boolean documentsComplete;
    private boolean documentsIncomplete;
    private boolean documentsAwaitingReview;

    private boolean spendProfileComplete;
    private boolean spendProfileIncomplete;
    private boolean spendProfileAwaitingReview;

    public MonitoringOfficerDashboardForm() {
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
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

    public boolean isSpendProfileComplete() {
        return spendProfileComplete;
    }

    public void setSpendProfileComplete(boolean spendProfileComplete) {
        this.spendProfileComplete = spendProfileComplete;
    }

    public boolean isSpendProfileIncomplete() {
        return spendProfileIncomplete;
    }

    public void setSpendProfileIncomplete(boolean spendProfileIncomplete) {
        this.spendProfileIncomplete = spendProfileIncomplete;
    }

    public boolean isSpendProfileAwaitingReview() {
        return spendProfileAwaitingReview;
    }

    public void setSpendProfileAwaitingReview(boolean spendProfileAwaitingReview) {
        this.spendProfileAwaitingReview = spendProfileAwaitingReview;
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
                .append(spendProfileComplete, that.spendProfileComplete)
                .append(spendProfileIncomplete, that.spendProfileIncomplete)
                .append(spendProfileAwaitingReview, that.spendProfileAwaitingReview)
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
                .append(spendProfileComplete)
                .append(spendProfileIncomplete)
                .append(spendProfileAwaitingReview)
                .toHashCode();
    }
}
