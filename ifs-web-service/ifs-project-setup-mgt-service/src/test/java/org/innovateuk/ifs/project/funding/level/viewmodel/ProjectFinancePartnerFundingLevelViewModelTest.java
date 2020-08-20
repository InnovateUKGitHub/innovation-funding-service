package org.innovateuk.ifs.project.funding.level.viewmodel;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectFinancePartnerFundingLevelViewModelTest {

    @Test
    public void isTotalGrantZeroWithZeroTotalGrant() {

        ProjectFinancePartnerFundingLevelViewModel model = new ProjectFinancePartnerFundingLevelViewModel(1L,
        null,
        false,
        0,
        null,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        new BigDecimal("0"));

        assertTrue(model.isTotalGrantZero());
    }

    @Test
    public void isTotalGrantZeroWithZeroWithScaleTotalGrant() {

        ProjectFinancePartnerFundingLevelViewModel model = new ProjectFinancePartnerFundingLevelViewModel(1L,
                null,
                false,
                0,
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("0.00"));

        assertTrue(model.isTotalGrantZero());
    }

    @Test
    public void isTotalGrantZeroWithNonZeroTotalGrant() {

        ProjectFinancePartnerFundingLevelViewModel model = new ProjectFinancePartnerFundingLevelViewModel(1L,
                null,
                false,
                0,
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("1000"));

        assertFalse(model.isTotalGrantZero());
    }

    @Test
    public void isTotalGrantZeroWithNonZeroWithScaleTotalGrant() {

        ProjectFinancePartnerFundingLevelViewModel model = new ProjectFinancePartnerFundingLevelViewModel(1L,
                null,
                false,
                0,
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("1000.00"));

        assertFalse(model.isTotalGrantZero());
    }
}
