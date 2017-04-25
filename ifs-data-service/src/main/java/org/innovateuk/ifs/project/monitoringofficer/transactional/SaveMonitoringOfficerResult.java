package org.innovateuk.ifs.project.monitoringofficer.transactional;

/**
 * Result for saving monitoring officer successfully
 */
public class SaveMonitoringOfficerResult {

    private boolean monitoringOfficerSaved;

    public SaveMonitoringOfficerResult() {
        this.monitoringOfficerSaved = true;
    }

    public boolean isMonitoringOfficerSaved() {
        return monitoringOfficerSaved;
    }

    public void setMonitoringOfficerSaved(boolean monitoringOfficerSaved) {
        this.monitoringOfficerSaved = monitoringOfficerSaved;
    }
}
