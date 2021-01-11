package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ProjectProcurementMilestoneBuilder extends AbstractProcurementMilestoneResourceBuilder<ProjectProcurementMilestoneResource, ProjectProcurementMilestoneBuilder> {

    private ProjectProcurementMilestoneBuilder(final List<BiConsumer<Integer, ProjectProcurementMilestoneResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ProjectProcurementMilestoneBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectProcurementMilestoneResource>> actions) {
        return new ProjectProcurementMilestoneBuilder(actions);
    }

    public static ProjectProcurementMilestoneBuilder newProjectProcurementMilestoneResource() {
        return new ProjectProcurementMilestoneBuilder(emptyList());
    }

    @Override
    protected ProjectProcurementMilestoneResource createInitial() {
        return new ProjectProcurementMilestoneResource();
    }

    public ProjectProcurementMilestoneBuilder withProjectId(long... projectIds) {
        return withArray((projectId, resource) -> resource.setProjectId(projectId), projectIds);
    }
}