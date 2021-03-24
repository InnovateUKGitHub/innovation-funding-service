package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;
import org.innovateuk.ifs.finance.resource.cost.AssociateSalaryCost;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Map;

import static org.innovateuk.ifs.finance.builder.AssociateSalaryCostBuilder.newAssociateSalaryCost;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;

public class YourProjectCostsFormTest {

    @Test
    public void getTotalIndirectCostsForNonFecModel() {
        BigInteger associateOneCost = BigInteger.valueOf(100);
        BigInteger associateTwoCost = BigInteger.valueOf(200);
        BigInteger academicAndSecretarialSupportOneCost = BigInteger.valueOf(300);

        YourProjectCostsForm form = new YourProjectCostsForm();
        form.setFecModelEnabled(false);

        AssociateSalaryCost associateOne = newAssociateSalaryCost()
                .withCost(associateOneCost)
                .withDuration(1)
                .build();
        AssociateSalaryCostRowForm associateOneForm = new AssociateSalaryCostRowForm(associateOne);
        associateOneForm.setTotal(BigDecimal.valueOf(associateOneCost.intValue()));

        AssociateSalaryCost associateTwo = newAssociateSalaryCost()
                .withCost(associateTwoCost)
                .withDuration(1)
                .build();
        AssociateSalaryCostRowForm associateTwoForm = new AssociateSalaryCostRowForm(associateTwo);
        associateTwoForm.setTotal(BigDecimal.valueOf(associateTwoCost.intValue()));

        Map<String, AssociateSalaryCostRowForm> associateSalaryCostRows = asMap("associate_salary_costs-1", associateOneForm,
                "associate_salary_costs-2", associateTwoForm);

        form.setAssociateSalaryCostRows(associateSalaryCostRows);
        AcademicAndSecretarialSupportCostRowForm academicAndSecretarialSupportForm = new AcademicAndSecretarialSupportCostRowForm(new AcademicAndSecretarialSupport());
        academicAndSecretarialSupportForm.setCost(academicAndSecretarialSupportOneCost);
        form.setAcademicAndSecretarialSupportForm(academicAndSecretarialSupportForm);

        BigDecimal expected = associateOneForm.getTotal()
                .add(associateTwoForm.getTotal())
                .add(new BigDecimal(academicAndSecretarialSupportForm.getCost()))
                .multiply(BigDecimal.valueOf(46))
                .divide(new BigDecimal(100))
                .setScale(0, RoundingMode.HALF_UP);

        assertFalse(form.getFecModelEnabled());
        assertNotNull(form.getTotalIndirectCosts());
        assertEquals(expected, form.getTotalIndirectCosts());
    }

    @Test
    public void getTotalIndirectCostsForFecModel() {
        YourProjectCostsForm form = new YourProjectCostsForm();
        form.setFecModelEnabled(true);

        BigDecimal expected = BigDecimal.ZERO;

        assertTrue(form.getFecModelEnabled());
        assertNotNull(form.getTotalIndirectCosts());
        assertEquals(expected, form.getTotalIndirectCosts());
    }

    @Test
    public void getOrganisationFinanceTotalForNonFecModelIncludesNewCostCategories() {
        BigInteger academicAndSecretarialSupportOneCost = BigInteger.valueOf(300);

        YourProjectCostsForm form = new YourProjectCostsForm();
        form.setFecModelEnabled(false);

        AcademicAndSecretarialSupportCostRowForm academicAndSecretarialSupportForm = new AcademicAndSecretarialSupportCostRowForm(new AcademicAndSecretarialSupport());
        academicAndSecretarialSupportForm.setCost(academicAndSecretarialSupportOneCost);
        form.setAcademicAndSecretarialSupportForm(academicAndSecretarialSupportForm);

        BigDecimal expected = form.getTotalAcademicAndSecretarialSupportCosts()
                .add(form.getTotalIndirectCosts());

        assertFalse(form.getFecModelEnabled());
        assertNotNull(form.getOrganisationFinanceTotal());
        assertEquals(expected, form.getOrganisationFinanceTotal());
    }

    @Test
    public void getOrganisationFinanceTotalForFecModelExcludesNewCostCategories() {
        YourProjectCostsForm form = new YourProjectCostsForm();
        form.setFecModelEnabled(true);

        BigDecimal expected = BigDecimal.ZERO;

        assertTrue(form.getFecModelEnabled());
        assertNotNull(form.getOrganisationFinanceTotal());
        assertEquals(expected, form.getOrganisationFinanceTotal());
    }
}
