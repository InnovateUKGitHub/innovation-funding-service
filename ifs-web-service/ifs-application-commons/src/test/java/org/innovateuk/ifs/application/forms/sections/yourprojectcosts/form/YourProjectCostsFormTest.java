package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form;

import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;
import org.innovateuk.ifs.finance.resource.cost.AssociateSalaryCost;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.finance.builder.AssociateSalaryCostBuilder.newAssociateSalaryCost;
import static org.innovateuk.ifs.finance.builder.AcademicAndSecretarialSupportBuilder.newAcademicAndSecretarialSupport;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class YourProjectCostsFormTest {

    @Test
    public void getTotalIndirectCosts() {
        BigInteger associateOneCost = BigInteger.valueOf(100);
        BigInteger associateTwoCost = BigInteger.valueOf(200);
        BigInteger academicAndSecretarialSupportOneCost = BigInteger.valueOf(300);
        BigInteger academicAndSecretarialSupportTwoCost = BigInteger.valueOf(400);

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

        AcademicAndSecretarialSupport academicAndSecretarialSupportOne = newAcademicAndSecretarialSupport()
                .withCost(academicAndSecretarialSupportOneCost)
                .build();
        AcademicAndSecretarialSupportCostRowForm academicAndSecretarialSupportOneForm = new AcademicAndSecretarialSupportCostRowForm(academicAndSecretarialSupportOne);
        academicAndSecretarialSupportOneForm.setTotal(BigDecimal.valueOf(academicAndSecretarialSupportOneCost.intValue()));

        AcademicAndSecretarialSupport academicAndSecretarialSupportTwo = newAcademicAndSecretarialSupport()
                .withCost(academicAndSecretarialSupportTwoCost)
                .build();
        AcademicAndSecretarialSupportCostRowForm academicAndSecretarialSupportTwoForm = new AcademicAndSecretarialSupportCostRowForm(academicAndSecretarialSupportTwo);
        academicAndSecretarialSupportTwoForm.setTotal(BigDecimal.valueOf(academicAndSecretarialSupportTwoCost.intValue()));

        Map<String, AcademicAndSecretarialSupportCostRowForm> academicAndSecretarialSupportCostRows = asMap("academic_and_secretarial_support-1", academicAndSecretarialSupportOneForm,
                "academic_and_secretarial_support-2", academicAndSecretarialSupportTwoForm);

        form.setAssociateSalaryCostRows(associateSalaryCostRows);
        form.setAcademicAndSecretarialSupportCostRows(academicAndSecretarialSupportCostRows);

        BigInteger expected = associateOneCost
                .add(associateTwoCost)
                .add(academicAndSecretarialSupportOneCost)
                .add(academicAndSecretarialSupportTwoCost)
                .multiply(BigInteger.valueOf(46))
                .divide(BigInteger.valueOf(100));

        assertNotNull(form.getTotalIndirectCosts());
        assertEquals(expected, form.getTotalIndirectCosts().toBigInteger());
    }
}
