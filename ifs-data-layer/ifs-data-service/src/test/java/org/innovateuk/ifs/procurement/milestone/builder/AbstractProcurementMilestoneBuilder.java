package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.procurement.milestone.domain.ProcurementMilestone;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractProcurementMilestoneBuilder<S extends ProcurementMilestone, T extends AbstractProcurementMilestoneBuilder>
        extends BaseBuilder<S, T> {

    protected AbstractProcurementMilestoneBuilder(List<BiConsumer<Integer, S>> multiActions) {
        super(multiActions);
    }

    public T withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    public T withMonth(Integer... months) {
        return withArraySetFieldByReflection("month", months);
    }

    public T withDescription(String... descriptions) {
        return withArraySetFieldByReflection("description", descriptions);
    }

    public T withTaskOrActivity(String... taskOrActivitys) {
        return withArraySetFieldByReflection("taskOrActivity", taskOrActivitys);
    }

    public T withDeliverable(String... deliverables) {
        return withArraySetFieldByReflection("deliverable", deliverables);
    }

    public T withSuccessCriteria(String... successCriterias) {
        return withArraySetFieldByReflection("successCriteria", successCriterias);
    }

    public T withPayment(BigInteger... payments) {
        return withArraySetFieldByReflection("payment", payments);
    }
}


