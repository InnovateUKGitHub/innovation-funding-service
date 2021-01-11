package org.innovateuk.ifs.application.viewmodel;


import org.innovateuk.ifs.application.finance.viewmodel.ApplicationProcurementMilestoneViewModel;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneBuilder.newApplicationProcurementMilestoneResource;

public class ApplicationProcurementMilestoneViewModelTest {

    @Test
    public void applicationProcurementMilestoneViewModelTotalTest(){
        List<ApplicationProcurementMilestoneResource> milestones =
                Arrays.asList(
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(10)).build(),
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(20)).build(),
                        newApplicationProcurementMilestoneResource().withPayment(null).build(), // This should not happen - but check it still works
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(0)).build() // This should not happen - but check it still works
                        );
        Assert.assertEquals(BigInteger.valueOf(30), new ApplicationProcurementMilestoneViewModel(milestones).getTotal());
    }

    @Test
    public void applicationProcurementMilestoneViewModelPercentageTest(){
        List<ApplicationProcurementMilestoneResource> milestones =
                Arrays.asList(
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(30)).build() // Use a value that will require rounding
                );
        Assert.assertEquals(new BigDecimal("33.33"), new ApplicationProcurementMilestoneViewModel(milestones).getPercentage(BigInteger.valueOf(10l)));
        Assert.assertEquals(new BigDecimal("66.67"), new ApplicationProcurementMilestoneViewModel(milestones).getPercentage(BigInteger.valueOf(20l)));
    }

    @Test
    public void applicationProcurementMilestoneViewModelPercentageNoMilestonesTest(){
        List<ApplicationProcurementMilestoneResource> milestones = new ArrayList<>();
        Assert.assertEquals(BigDecimal.ZERO, new ApplicationProcurementMilestoneViewModel(milestones).getPercentage(BigInteger.ZERO));
    }

    @Test
    public void applicationProcurementMilestoneViewModelPercentageNullTest(){
        List<ApplicationProcurementMilestoneResource> milestones =
                Arrays.asList(
                        newApplicationProcurementMilestoneResource().withPayment(BigInteger.valueOf(100)).build()
                );
        Assert.assertEquals(BigDecimal.ZERO, new ApplicationProcurementMilestoneViewModel(milestones).getPercentage(null));
    }
}
