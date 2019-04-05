package org.innovateuk.ifs.project.monitoring.builder;

import org.innovateuk.ifs.project.core.builder.ProjectParticipantBuilder;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link MonitoringOfficer} entities.
 */
public class MonitoringOfficerBuilder extends ProjectParticipantBuilder<MonitoringOfficer, MonitoringOfficerBuilder> {

    private MonitoringOfficerBuilder(List<BiConsumer<Integer, MonitoringOfficer>> multiActions) {
        super(multiActions, ProjectParticipantRole.PROJECT_MONITORING_OFFICER_ROLES);
    }

    public static MonitoringOfficerBuilder newProjectMonitoringOfficer() {
        return new MonitoringOfficerBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected MonitoringOfficerBuilder createNewBuilderWithActions(List<BiConsumer<Integer, MonitoringOfficer>> actions) {
        return new MonitoringOfficerBuilder(actions);
    }

    @Override
    protected MonitoringOfficer createInitial() {
        return new MonitoringOfficer();
    }

    @Override
    public void postProcess(int index, MonitoringOfficer projectMonitoringOfficer) {
        Optional.ofNullable(projectMonitoringOfficer.getProcess())
                .ifPresent(p -> p.setProjectMonitoringOfficer(projectMonitoringOfficer));
    }
}