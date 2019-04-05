package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import java.util.List;

/**
 * View model for the View all monitoring officers page
 */
public class MonitoringOfficerViewAllViewModel {

    private final List<MonitoringOfficerViewRow> monitoringOfficers;

    public MonitoringOfficerViewAllViewModel(List<MonitoringOfficerViewRow> monitoringOfficers) {
        this.monitoringOfficers = monitoringOfficers;
    }

    public List<MonitoringOfficerViewRow> getMonitoringOfficers() {
        return monitoringOfficers;
    }
}
