package org.innovateuk.ifs.procurement.milestone.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;

import java.math.BigInteger;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractProcurementMilestoneResourceBuilder<S extends ProcurementMilestoneResource, T extends AbstractProcurementMilestoneResourceBuilder>
        extends BaseBuilder<S, T> {

    protected AbstractProcurementMilestoneResourceBuilder(List<BiConsumer<Integer, S>> multiActions) {
        super(multiActions);
    }

    public T withId(Long... ids) {
        return withArray((id, resource) -> resource.setId(id), ids);
    }

    public T withMonth(Integer... months) {
        return withArray((month, resource) -> resource.setMonth(month), months);
    }

    public T withDescription(String... descriptions) {
        return withArray((description, resource) -> resource.setDescription(description), descriptions);
    }

    public T withTaskOrActivity(String... taskOrActivitys) {
        return withArray((taskOrActivity, resource) -> resource.setTaskOrActivity(taskOrActivity), taskOrActivitys);
    }

    public T withDeliverable(String... deliverables) {
        return withArray((deliverable, resource) -> resource.setDeliverable(deliverable), deliverables);
    }

    public T withSuccessCriteria(String... successCriterias) {
        return withArray((successCriteria, resource) -> resource.setSuccessCriteria(successCriteria), successCriterias);
    }

    public T withPayment(BigInteger... payments) {
        return withArray((payment, resource) -> resource.setPayment(payment), payments);
    }

    public T withOrganisationId(long... organisationIds) {
        return withArray((organisationId, resource) -> resource.setOrganisationId(organisationId), organisationIds);
    }
}