package org.innovateuk.ifs.project.monitor.builder;

import org.innovateuk.ifs.project.core.builder.ProjectParticipantBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.monitor.domain.ProjectMonitoringOfficer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link ProjectMonitoringOfficer} entities.
 */
public class ProjectMonitoringOfficerBuilder extends ProjectParticipantBuilder<ProjectMonitoringOfficer, ProjectMonitoringOfficerBuilder> {

    private ProjectMonitoringOfficerBuilder(List<BiConsumer<Integer, ProjectMonitoringOfficer>> multiActions) {
        super(multiActions, ProjectParticipantRole.PROJECT_MONITORING_OFFICER_ROLES);
    }

    public static ProjectMonitoringOfficerBuilder newProjectMonitoringOfficer() {
        return new ProjectMonitoringOfficerBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectMonitoringOfficerBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectMonitoringOfficer>> actions) {
        return new ProjectMonitoringOfficerBuilder(actions);
    }

    @Override
    protected ProjectMonitoringOfficer createInitial() {
        return new ProjectMonitoringOfficer();
    }

    @Override
    public void postProcess(int index, ProjectMonitoringOfficer projectMonitoringOfficer) {
        Project project = projectMonitoringOfficer.getProcess();

        if (project != null) {

            if (project.getProjectMonitoringOfficers() == null) {
                project.setProjectMonitoringOfficers(new ArrayList<>());
            }
            if (!project.getProjectMonitoringOfficers().contains(projectMonitoringOfficer)) {
                project.addProjectMonitoringOfficer(projectMonitoringOfficer);
            }
        }
    }
}