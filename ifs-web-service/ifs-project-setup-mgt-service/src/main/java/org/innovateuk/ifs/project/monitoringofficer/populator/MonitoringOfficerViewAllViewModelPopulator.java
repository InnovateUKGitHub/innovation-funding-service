package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerViewAllViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerViewRow;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Populator for the View all monitoring officers page
 */
@Component
public class MonitoringOfficerViewAllViewModelPopulator {

    private final MonitoringOfficerRestService monitoringOfficerRestService;

    public MonitoringOfficerViewAllViewModelPopulator(MonitoringOfficerRestService monitoringOfficerRestService) {
        this.monitoringOfficerRestService = monitoringOfficerRestService;
    }

    public MonitoringOfficerViewAllViewModel populate() {
        List<MonitoringOfficerViewRow> rows = monitoringOfficerRestService.findAll()
                .getSuccess()
                .stream()
                .map(mo -> new MonitoringOfficerViewRow(mo.getFirstName(),
                        mo.getLastName(),
                        mo.getId()))
                .collect(toList());

        return new MonitoringOfficerViewAllViewModel(rows);
    }

}
