package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;
import org.innovateuk.ifs.finance.resource.cost.AssociateSalaryCost;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static org.innovateuk.ifs.finance.builder.AssociateSalaryCostBuilder.newAssociateSalaryCost;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class YourProjectCostsFormTest {

    @Test
    public void getTotalIndirectCosts() {
        BigInteger associateOneCost = BigInteger.valueOf(100);
        BigInteger associateTwoCost = BigInteger.valueOf(200);
        BigInteger academicAndSecretarialSupportOneCost = BigInteger.valueOf(300);

        YourProjectCostsForm form = new YourProjectCostsForm();

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

        BigInteger expected = associateOneCost
                .add(associateTwoCost)
                .add(academicAndSecretarialSupportOneCost)
                .multiply(BigInteger.valueOf(46))
                .divide(BigInteger.valueOf(100));

        assertNotNull(form.getTotalIndirectCosts());
        assertEquals(expected, form.getTotalIndirectCosts().toBigInteger());
    }

    @Test
    public void getOrganisationFinanceTotalForNonFecModel() {
        BigInteger academicAndSecretarialSupportOneCost = BigInteger.valueOf(300);

        YourProjectCostsForm form = new YourProjectCostsForm();

        AcademicAndSecretarialSupportCostRowForm academicAndSecretarialSupportForm = new AcademicAndSecretarialSupportCostRowForm(new AcademicAndSecretarialSupport());
        academicAndSecretarialSupportForm.setCost(academicAndSecretarialSupportOneCost);
        form.setAcademicAndSecretarialSupportForm(academicAndSecretarialSupportForm);

        BigDecimal expected = form.getTotalAcademicAndSecretarialSupportCosts().add(form.getTotalIndirectCosts());

        assertNotNull(form.getOrganisationFinanceTotal());
        assertEquals(expected, form.getOrganisationFinanceTotal());
    }
}
