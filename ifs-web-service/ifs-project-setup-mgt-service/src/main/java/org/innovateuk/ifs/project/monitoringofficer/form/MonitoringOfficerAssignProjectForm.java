package org.innovateuk.ifs.project.monitoringofficer.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

public class MonitoringOfficerAssignProjectForm extends BaseBindingResultTarget {

    @NotNull
    private Long projectNumber;

    public MonitoringOfficerAssignProjectForm() {
    }

    public Long getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(long projectNumber) {
        this.projectNumber = projectNumber;
    }
}
