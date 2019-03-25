package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerViewAllViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerViewRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Component
public class MonitoringOfficerViewAllViewModelPopulator {

    public MonitoringOfficerViewAllViewModel populate(List<ProjectMonitoringOfficerResource> monitoringOfficers) {
        List<MonitoringOfficerViewRow> rows = simpleMap(monitoringOfficers,
                                                        mo -> new MonitoringOfficerViewRow(mo.getFirstName(),
                                                                                           mo.getLastName(),
                                                                                           mo.getUserId(),
                                                                                           mo.getAssignedProjects().size())
        );

        return new MonitoringOfficerViewAllViewModel(rows);
    }

}
