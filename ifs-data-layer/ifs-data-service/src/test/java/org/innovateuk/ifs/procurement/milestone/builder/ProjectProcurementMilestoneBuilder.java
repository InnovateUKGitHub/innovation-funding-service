package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.procurement.milestone.domain.ProjectProcurementMilestone;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ProjectProcurementMilestoneBuilder extends AbstractProcurementMilestoneBuilder<ProjectProcurementMilestone, ProjectProcurementMilestoneBuilder> {

    private ProjectProcurementMilestoneBuilder(final List<BiConsumer<Integer, ProjectProcurementMilestone>> newActions) {
        super(newActions);
    }

    @Override
    protected ProjectProcurementMilestoneBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectProcurementMilestone>> actions) {
        return new ProjectProcurementMilestoneBuilder(actions);
    }

    public static ProjectProcurementMilestoneBuilder newProjectProcurementMilestone() {
        return new ProjectProcurementMilestoneBuilder(emptyList());
    }

    @Override
    protected ProjectProcurementMilestone createInitial() {
        return new ProjectProcurementMilestone();
    }

}
