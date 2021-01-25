package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.procurement.milestone.domain.ApplicationProcurementMilestone;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;


public class ApplicationProcurementMilestoneBuilder extends AbstractProcurementMilestoneBuilder<ApplicationProcurementMilestone, ApplicationProcurementMilestoneBuilder> {

    private ApplicationProcurementMilestoneBuilder(final List<BiConsumer<Integer, ApplicationProcurementMilestone>> newActions) {
        super(newActions);
    }

    @Override
    protected ApplicationProcurementMilestoneBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationProcurementMilestone>> actions) {
        return new ApplicationProcurementMilestoneBuilder(actions);
    }

    public static ApplicationProcurementMilestoneBuilder newApplicationProcurementMilestone() {
        return new ApplicationProcurementMilestoneBuilder(emptyList());
    }

    @Override
    protected ApplicationProcurementMilestone createInitial() {
        return new ApplicationProcurementMilestone();
    }
}


