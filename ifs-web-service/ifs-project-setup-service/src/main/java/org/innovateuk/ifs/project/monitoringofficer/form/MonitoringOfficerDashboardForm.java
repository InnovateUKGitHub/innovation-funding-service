package org.innovateuk.ifs.project.monitoringofficer.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.Size;

public class MonitoringOfficerDashboardForm extends BaseBindingResultTarget {

    @Size(min = 3, message = "{validation.modashboard.filterprojects.keywordsearch.min.length}")
    @Size(max = 100, message = "{validation.modashboard.filterprojects.keywordsearch.max.length}")
    private String keywordSearch;
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

    public String getKeywordSearch() {
        return keywordSearch;
    }

    public void setKeywordSearch(String keywordSearch) {
        this.keywordSearch = keywordSearch;
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
                .append(keywordSearch, that.keywordSearch)
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
                .append(keywordSearch)
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
