package org.innovateuk.ifs.project.monitoringofficer.populator;


import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerAssignedProjectViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerProjectsViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerUnassignedProjectViewModel;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
public class MonitoringOfficerProjectsViewModelPopulator {

    public MonitoringOfficerProjectsViewModel populate(long monitoringOfficerId) {
        return new MonitoringOfficerProjectsViewModel(
                monitoringOfficerId,
                "Tom Jones",
                1,
                singletonList(new MonitoringOfficerAssignedProjectViewModel(
                        119,
                        2,
                        5,
                        "Grade crossing manufacture and supply",
                        "Vitruvius, Stonework Limited"
                )),
                asList( new MonitoringOfficerUnassignedProjectViewModel(1, "foo"),
                        new MonitoringOfficerUnassignedProjectViewModel(2, "bar")
                )
        );
    }
}