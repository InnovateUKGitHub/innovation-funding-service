package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * View model for finance/finance-summary :: payment milestones table.
 */
public class ApplicationProcurementMilestonesSummaryViewModel {

    private final List<ApplicationProcurementMilestoneResource> applicationProcurementMilestoneResources;

    public ApplicationProcurementMilestonesSummaryViewModel(List<ApplicationProcurementMilestoneResource> applicationProcurementMilestoneResources) {
        this.applicationProcurementMilestoneResources = applicationProcurementMilestoneResources;
    }

    public List<ApplicationProcurementMilestoneResource> getApplicationProcurementMilestoneResources() {
        return applicationProcurementMilestoneResources;
    }

    public BigInteger getTotal() {
        return applicationProcurementMilestoneResources.stream()
                .map(ApplicationProcurementMilestoneResource::getPayment)
                .filter(Objects::nonNull)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    public BigDecimal getPercentage(BigInteger cost) {
        BigDecimal total = new BigDecimal(getTotal());
        if (BigDecimal.ZERO.compareTo(total) == 0 || cost == null || BigInteger.ZERO.compareTo(cost) == 0){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(cost).multiply(new BigDecimal(100)).divide(total, 2, BigDecimal.ROUND_HALF_EVEN);
    }
}