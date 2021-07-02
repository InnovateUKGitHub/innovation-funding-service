package org.innovateuk.ifs.project.monitoringofficer.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

public class MonitoringOfficerDashboardForm extends BaseBindingResultTarget {

    private boolean projectInSetup;
    private boolean previousProject;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MonitoringOfficerDashboardForm that = (MonitoringOfficerDashboardForm) o;

        return new EqualsBuilder()
                .append(projectInSetup, that.projectInSetup)
                .append(previousProject, that.previousProject)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectInSetup)
                .append(previousProject)
                .toHashCode();
    }
}
