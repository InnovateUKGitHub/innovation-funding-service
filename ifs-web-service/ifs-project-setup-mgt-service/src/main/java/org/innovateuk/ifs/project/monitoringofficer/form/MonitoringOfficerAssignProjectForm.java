package org.innovateuk.ifs.project.monitoringofficer.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

public class MonitoringOfficerAssignProjectForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.monitoring-officer.assign.required}")
    private Long projectId;

    public MonitoringOfficerAssignProjectForm() {
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
}