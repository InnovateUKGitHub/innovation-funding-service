package org.innovateuk.ifs.project.monitoringofficer.form;

import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;

public class MonitoringOfficerViewForm {

    Long userId;

    public MonitoringOfficerViewForm() {

    }

    public MonitoringOfficerViewForm(Long userId) {
        this.userId = userId;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId= userId;
    }
}
