package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * View model for finance/finance-summary :: payment milestones table.
 */
public class ApplicationProcurementMilestoneViewModel {

    private final List<ApplicationProcurementMilestoneResource> applicationProcurementMilestoneResources;

    public ApplicationProcurementMilestoneViewModel(List<ApplicationProcurementMilestoneResource> applicationProcurementMilestoneResources) {
        this.applicationProcurementMilestoneResources = applicationProcurementMilestoneResources;
    }

    public List<ApplicationProcurementMilestoneResource> getApplicationProcurementMilestoneResources() {
        return applicationProcurementMilestoneResources;
    }

    public BigInteger getTotal() {
        BigInteger total = BigInteger.ZERO;

        for (ApplicationProcurementMilestoneResource resource : applicationProcurementMilestoneResources) {
            total = total.add(resource.getPayment());
        }

        return total;
    }

    public BigDecimal getPercentage(BigInteger cost) {
        BigDecimal total = new BigDecimal(getTotal());
        BigDecimal costAsDecimal = new BigDecimal(cost);

        return costAsDecimal.multiply(new BigDecimal(100)).divide(total, 2, BigDecimal.ROUND_HALF_EVEN);
    }
}