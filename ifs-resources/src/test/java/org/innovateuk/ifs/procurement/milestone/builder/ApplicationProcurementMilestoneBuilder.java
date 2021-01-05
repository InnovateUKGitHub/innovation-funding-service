package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationProcurementMilestoneBuilder extends AbstractProcurementMilestoneResourceBuilder<ApplicationProcurementMilestoneResource, ApplicationProcurementMilestoneBuilder> {

    private ApplicationProcurementMilestoneBuilder(final List<BiConsumer<Integer, ApplicationProcurementMilestoneResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ApplicationProcurementMilestoneBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationProcurementMilestoneResource>> actions) {
        return new ApplicationProcurementMilestoneBuilder(actions);
    }

    public static ApplicationProcurementMilestoneBuilder newApplicationProcurementMilestoneResource() {
        return new ApplicationProcurementMilestoneBuilder(emptyList());
    }

    @Override
    protected ApplicationProcurementMilestoneResource createInitial() {
        return new ApplicationProcurementMilestoneResource();
    }

    public ApplicationProcurementMilestoneBuilder withApplicationId(long... applicationIds) {
        return withArray((applicationId, resource) -> resource.setApplicationId(applicationId), applicationIds);
    }
}