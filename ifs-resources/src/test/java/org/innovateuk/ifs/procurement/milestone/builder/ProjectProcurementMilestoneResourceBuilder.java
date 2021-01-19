package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.procurement.milestone.resource.PaymentMilestoneResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ProjectProcurementMilestoneResourceBuilder extends AbstractProcurementMilestoneResourceBuilder<PaymentMilestoneResource, ProjectProcurementMilestoneResourceBuilder> {

    private ProjectProcurementMilestoneResourceBuilder(final List<BiConsumer<Integer, PaymentMilestoneResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ProjectProcurementMilestoneResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PaymentMilestoneResource>> actions) {
        return new ProjectProcurementMilestoneResourceBuilder(actions);
    }

    public static ProjectProcurementMilestoneResourceBuilder newProjectProcurementMilestoneResource() {
        return new ProjectProcurementMilestoneResourceBuilder(emptyList());
    }

    @Override
    protected PaymentMilestoneResource createInitial() {
        return new PaymentMilestoneResource();
    }

    public ProjectProcurementMilestoneResourceBuilder withProjectId(long... projectIds) {
        return withArray((projectId, resource) -> resource.setProjectId(projectId), projectIds);
    }
}