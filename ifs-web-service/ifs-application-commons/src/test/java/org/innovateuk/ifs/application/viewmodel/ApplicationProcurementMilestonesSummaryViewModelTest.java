package org.innovateuk.ifs.application.viewmodel;


import org.innovateuk.ifs.application.finance.viewmodel.ApplicationProcurementMilestonesSummaryViewModel;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneResourceBuilder.newApplicationProcurementMilestoneResource;

public class ApplicationProcurementMilestonesSummaryViewModelTest {

    @Test
    public void applicationProcurementMilestoneViewModelTotalTest(){
        List<ApplicationProcurementMilestoneResource> milestones =
                Arrays.asList(
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(10)).build(),
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(20)).build(),
                        newApplicationProcurementMilestoneResource().withPayment((BigInteger) null).build(), // This should not happen - but check it still works
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(0)).build() // This should not happen - but check it still works
                        );
        List<BaseFinanceResource> financeResources = Arrays.asList(new TestTotalFundingSoughtBaseFinanceResource(new BigDecimal(60)));

        Assert.assertEquals(BigInteger.valueOf(30), new ApplicationProcurementMilestonesSummaryViewModel(milestones, financeResources).getTotal());
    }

    @Test
    public void applicationProcurementMilestoneViewModelPercentageTest(){
        List<ApplicationProcurementMilestoneResource> milestones =
                Arrays.asList(
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(30)).build()
                );
        List<BaseFinanceResource> financeResources = Arrays.asList(new TestTotalFundingSoughtBaseFinanceResource(new BigDecimal(60)));

        Assert.assertEquals(new BigDecimal("16.67"), new ApplicationProcurementMilestonesSummaryViewModel(milestones, financeResources).getPercentage(BigInteger.valueOf(10l)));
        Assert.assertEquals(new BigDecimal("33.33"), new ApplicationProcurementMilestonesSummaryViewModel(milestones, financeResources).getPercentage(BigInteger.valueOf(20l)));
    }

    @Test
    public void applicationProcurementMilestoneViewModelPercentageNoMilestonesTest(){
        List<ApplicationProcurementMilestoneResource> milestones = new ArrayList<>();
        List<BaseFinanceResource> financeResources = Arrays.asList(new TestTotalFundingSoughtBaseFinanceResource(new BigDecimal(60)));
        Assert.assertEquals(BigDecimal.ZERO, new ApplicationProcurementMilestonesSummaryViewModel(milestones, financeResources).getPercentage(BigInteger.ZERO));
    }

    @Test
    public void applicationProcurementMilestoneViewModelPercentageNullTest(){
        List<ApplicationProcurementMilestoneResource> milestones =
                Arrays.asList(
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(100)).build()
                );
        List<BaseFinanceResource> financeResources = Arrays.asList(new TestTotalFundingSoughtBaseFinanceResource(new BigDecimal(200)));
        Assert.assertEquals(BigDecimal.ZERO, new ApplicationProcurementMilestonesSummaryViewModel(milestones, financeResources ).getPercentage(null));
    }

    private static class TestTotalFundingSoughtBaseFinanceResource extends BaseFinanceResource {

        private final BigDecimal totalFundingSought;

        public TestTotalFundingSoughtBaseFinanceResource(BigDecimal totalFundingSought){
            this.totalFundingSought = totalFundingSought;
        }

        @Override
        public BigDecimal getTotalFundingSought() {
            return totalFundingSought;
        }
    }
}
