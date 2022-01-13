package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ProjectProcurementMilestoneResourceBuilder extends AbstractProcurementMilestoneResourceBuilder<ProjectProcurementMilestoneResource, ProjectProcurementMilestoneResourceBuilder> {

    private ProjectProcurementMilestoneResourceBuilder(final List<BiConsumer<Integer, ProjectProcurementMilestoneResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ProjectProcurementMilestoneResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectProcurementMilestoneResource>> actions) {
        return new ProjectProcurementMilestoneResourceBuilder(actions);
    }

    public static ProjectProcurementMilestoneResourceBuilder newProjectProcurementMilestoneResource() {
        return new ProjectProcurementMilestoneResourceBuilder(emptyList());
    }

    @Override
    protected ProjectProcurementMilestoneResource createInitial() {
        return new ProjectProcurementMilestoneResource();
    }

    public ProjectProcurementMilestoneResourceBuilder withProjectId(long... projectIds) {
        return withArray((projectId, resource) -> resource.setProjectId(projectId), projectIds);
    }
}