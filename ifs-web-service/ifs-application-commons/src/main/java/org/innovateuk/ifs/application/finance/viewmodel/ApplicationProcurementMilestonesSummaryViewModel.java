package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * View model for finance/finance-summary :: payment milestones table.
 */
public class ApplicationProcurementMilestonesSummaryViewModel {

    private final List<ApplicationProcurementMilestoneSummaryViewModel> applicationProcurementMilestoneSummaryViewModels;

    public ApplicationProcurementMilestonesSummaryViewModel(List<ApplicationProcurementMilestoneResource> applicationProcurementMilestoneResources) {
        this.applicationProcurementMilestoneSummaryViewModels
                = applicationProcurementMilestoneResources.stream()
                .map(milestone -> new ApplicationProcurementMilestoneSummaryViewModel(milestone.getMonth(), milestone.getDescription(), milestone.getPayment())).collect(toList());
    }

    public List<ApplicationProcurementMilestoneSummaryViewModel> getApplicationProcurementMilestoneSummaryViewModels() {
        return applicationProcurementMilestoneSummaryViewModels;
    }

    public BigInteger getTotal() {
        return applicationProcurementMilestoneSummaryViewModels .stream()
                .map(ApplicationProcurementMilestoneSummaryViewModel::getPayment)
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