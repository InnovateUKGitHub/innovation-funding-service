package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.project.spendprofile.transactional.ProcurementMilestonesSpendProfileFigureDistributer.OtherAndVat;
import org.junit.Test;

import java.util.List;
import static org.junit.Assert.assertEquals;

import static java.math.BigInteger.valueOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;

public class ProcurementMilestonesSpendProfileFigureDistributerTest {

    @Test
    public void testAdjustCostsRemoveTooMuchVat() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1))
        );
        try {
            // Total vat = 3 try to remove 4
            new ProcurementMilestonesSpendProfileFigureDistributer().adjustedCosts(toAdjust, valueOf(-4));
            fail("We should get an illegal state exception if we try to remove too much vat");
        } catch (IllegalStateException e){
            // Pass
        }
    }

    @Test
    public void testAdjustCostsRemoveTooMuchOtherCosts() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1))
        );
        try {
            // Total other costs = 6, try to remove 7.
            new ProcurementMilestonesSpendProfileFigureDistributer().adjustedCosts(toAdjust, valueOf(7));
            fail("We should get an illegal state exception if we try to remove too much vat");
        } catch (IllegalStateException e){
            // Pass
        }
    }

    @Test
    public void testAdjustCostsAddToVat() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0))
        );
        List<OtherAndVat> expected = asList(
                new OtherAndVat().withOtherCost(valueOf(0)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(0)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0))
        );
        // Call method under test add 2 to vat (subtract 2 from other costs)
        List<OtherAndVat> adjusted = new ProcurementMilestonesSpendProfileFigureDistributer().adjustedCosts(toAdjust, valueOf(2));
        assertEquals(expected, adjusted);
    }

    @Test
    public void testAdjustCostsSubtractFromVat() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1))
        );
        List<OtherAndVat> expected = asList(
                new OtherAndVat().withOtherCost(valueOf(3)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(3)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1))
        );
        // Call method under test add 2 to other costs (subtract 2 from vat)
        List<OtherAndVat> adjusted = new ProcurementMilestonesSpendProfileFigureDistributer().adjustedCosts(toAdjust, valueOf(-2));
        assertEquals(expected, adjusted);
    }

    @Test
    public void testAdjustCostsAddToVatMultipleIterationsRequired() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(10)).withVat(valueOf(5)),
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(5)).withVat(valueOf(1))
        );
        List<OtherAndVat> expected = asList(
                new OtherAndVat().withOtherCost(valueOf(7)).withVat(valueOf(8)),
                new OtherAndVat().withOtherCost(valueOf(0)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(3)).withVat(valueOf(3))
        );
        // Call method under test add 6 to vat (subtract 6 from other costs)
        List<OtherAndVat> adjusted = new ProcurementMilestonesSpendProfileFigureDistributer().adjustedCosts(toAdjust, valueOf(6));
        assertEquals(expected, adjusted);
    }

    @Test
    public void testAdjustCostsAddToOtherCostsMultipleIterationsRequired() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(10)).withVat(valueOf(5)),
                new OtherAndVat().withOtherCost(valueOf(3)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(20)).withVat(valueOf(10))
        );
        List<OtherAndVat> expected = asList(
                new OtherAndVat().withOtherCost(valueOf(12)).withVat(valueOf(3)),
                new OtherAndVat().withOtherCost(valueOf(4)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(21)).withVat(valueOf(9))
        );
        // Call method under test add 4 to other costs (subtract 4 from vat)
        List<OtherAndVat> adjusted = new ProcurementMilestonesSpendProfileFigureDistributer().adjustedCosts(toAdjust, valueOf(-4));
        assertEquals(expected, adjusted);
    }
}
