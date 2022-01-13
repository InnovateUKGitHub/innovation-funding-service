package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AcademicAndSecretarialSupportCostRowForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AssociateSalaryCostRowForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.AcademicAndSecretarialSupport;
import org.innovateuk.ifs.finance.resource.cost.AssociateSalaryCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimAmount;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.IndirectCostsUtil.calculateIndirectCost;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver.IndirectCostsUtil.calculateIndirectCostFromForm;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.junit.Assert.assertEquals;

public class IndirectCostsUtilTest {

    @Test
    public void testCalculateFromForm() {
        YourProjectCostsForm yourProjectCostsForm = new YourProjectCostsForm();
        yourProjectCostsForm.setGrantClaimPercentage(new BigDecimal("25"));
        Map<String, AssociateSalaryCostRowForm> salaryCostRows = new HashMap<>();
        salaryCostRows.put("1", new AssociateSalaryCostRowForm(new AssociateSalaryCost(null,
                null,
                "comp_admin",
                3,
                new BigInteger("100"))));
        yourProjectCostsForm.setAssociateSalaryCostRows(salaryCostRows);
        yourProjectCostsForm.setAcademicAndSecretarialSupportForm(
                new AcademicAndSecretarialSupportCostRowForm(
                        new AcademicAndSecretarialSupport(null,
                                null,
                                new BigInteger("100"))));

        BigDecimal actual = calculateIndirectCostFromForm(yourProjectCostsForm);

        BigDecimal expected = BigDecimal.valueOf((100 * 0.25 + 100 * 0.25) * 0.46).setScale(0);
        assertEquals(expected, actual);
    }

    @Test
    public void testCalculateIndirectCost() {
        ApplicationFinanceResource applicationFinanceResource = new ApplicationFinanceResource();
        applicationFinanceResource.setId(1L);
        Map<FinanceRowType, FinanceRowCostCategory> details = new HashMap<>();

        applicationFinanceResource.getGrantClaimPercentage();

        details.put(FinanceRowType.ASSOCIATE_SALARY_COSTS,
                newDefaultCostCategory()
                        .withTotal(new BigDecimal("100"))
                        .withCosts(ImmutableList.of(
                                new AssociateSalaryCost(applicationFinanceResource.getId(),
                                        null,
                                        "comp_admin",
                                        3,
                                        new BigInteger("100"))))
                        .build());
        details.put(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT,
                newDefaultCostCategory()
                        .withTotal(new BigDecimal("100"))
                        .withCosts(
                                ImmutableList.of(new AcademicAndSecretarialSupport(applicationFinanceResource.getId(),
                                        null,
                                        new BigInteger("100"))))
                        .build());
        details.put(FinanceRowType.GRANT_CLAIM_AMOUNT,
                newDefaultCostCategory().withCosts(
                        singletonList(
                                new GrantClaimAmount(null,
                                        new BigDecimal("50"),
                                        applicationFinanceResource.getId())
                        )).build());

        applicationFinanceResource.setFinanceOrganisationDetails(details);

        BigDecimal actual = calculateIndirectCost(applicationFinanceResource);

        BigDecimal expected = BigDecimal.valueOf((100 * 0.25 + 100 * 0.25) * 0.46).setScale(0);
        assertEquals(expected, actual);
    }

}