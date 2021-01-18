package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.procurement.milestone.resource.PaymentMilestoneResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ProjectProcurementMilestoneBuilder extends AbstractProcurementMilestoneResourceBuilder<PaymentMilestoneResource, ProjectProcurementMilestoneBuilder> {

    private ProjectProcurementMilestoneBuilder(final List<BiConsumer<Integer, PaymentMilestoneResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ProjectProcurementMilestoneBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PaymentMilestoneResource>> actions) {
        return new ProjectProcurementMilestoneBuilder(actions);
    }

    public static ProjectProcurementMilestoneBuilder newProjectProcurementMilestoneResource() {
        return new ProjectProcurementMilestoneBuilder(emptyList());
    }

    @Override
    protected PaymentMilestoneResource createInitial() {
        return new PaymentMilestoneResource();
    }

    public ProjectProcurementMilestoneBuilder withProjectId(long... projectIds) {
        return withArray((projectId, resource) -> resource.setProjectId(projectId), projectIds);
    }
}