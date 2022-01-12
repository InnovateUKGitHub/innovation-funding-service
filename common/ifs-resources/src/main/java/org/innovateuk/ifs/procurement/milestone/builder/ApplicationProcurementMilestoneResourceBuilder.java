package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationProcurementMilestoneResourceBuilder extends AbstractProcurementMilestoneResourceBuilder<ApplicationProcurementMilestoneResource, ApplicationProcurementMilestoneResourceBuilder> {

    private ApplicationProcurementMilestoneResourceBuilder(final List<BiConsumer<Integer, ApplicationProcurementMilestoneResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ApplicationProcurementMilestoneResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationProcurementMilestoneResource>> actions) {
        return new ApplicationProcurementMilestoneResourceBuilder(actions);
    }

    public static ApplicationProcurementMilestoneResourceBuilder newApplicationProcurementMilestoneResource() {
        return new ApplicationProcurementMilestoneResourceBuilder(emptyList());
    }

    @Override
    protected ApplicationProcurementMilestoneResource createInitial() {
        return new ApplicationProcurementMilestoneResource();
    }

    public ApplicationProcurementMilestoneResourceBuilder withApplicationId(long... applicationIds) {
        return withArray((applicationId, resource) -> resource.setApplicationId(applicationId), applicationIds);
    }
}