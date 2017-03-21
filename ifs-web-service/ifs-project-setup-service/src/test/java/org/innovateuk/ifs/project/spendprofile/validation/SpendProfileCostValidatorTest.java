package org.innovateuk.ifs.project.spendprofile.validation;

import org.innovateuk.ifs.commons.validation.SpendProfileCostValidator;
import org.innovateuk.ifs.commons.validation.ValidatorTestUtil;
import org.innovateuk.ifs.commons.validation.SpendProfileValidationError;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SpendProfileCostValidatorTest {

    private static final String FIELD_NAME_TEMPLATE = "table.monthlyCostsPerCategoryMap[%d][%d]";
    private Validator validator;
    private TestForm form;
    private SpendProfileTableResource table;

    private BindingResult bindingResult;

    class TestForm {
        private SpendProfileTableResource table;

        TestForm() {

        }

        public SpendProfileTableResource getTable() {
            return table;
        }

        public void setTable(SpendProfileTableResource table) {
            this.table = table;
        }
    }

    @Before
    public void setUp() {
        table = new SpendProfileTableResource();
        form = new TestForm();
        form.setTable(table);
        validator = new SpendProfileCostValidator();
        bindingResult = new DataBinder(form).getBindingResult();
    }

    @Test
    public void testSuccessWhenAllCostsAreCorrect() {
        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("30"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("100"))));

        validator.validate(table, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void testCostsAreFractionalLessThanZeroOrGreaterThanMillionOrNull() {
        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30.12"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("-30"), null),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("1000001"))));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(4, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL.getErrorKey(), 0, FIELD_NAME_TEMPLATE, 1L, 0);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO.getErrorKey(), 1, FIELD_NAME_TEMPLATE, 2L, 1);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL.getErrorKey(), 2, FIELD_NAME_TEMPLATE, 2L, 2);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT.getErrorKey(), 3, FIELD_NAME_TEMPLATE, 3L, 2);
    }

    @Test
    public void testCostsAreFractional() {

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30.44"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("50.10"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("10.31"))));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(3, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL.getErrorKey(), 0, FIELD_NAME_TEMPLATE, 1L, 0);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL.getErrorKey(), 1, FIELD_NAME_TEMPLATE, 2L, 1);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL.getErrorKey(), 2, FIELD_NAME_TEMPLATE, 3L, 2);
    }

    @Test
    public void testCostsAreLessThanZero() {
        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("0"), new BigDecimal("00"), new BigDecimal("-1")),
                2L, asList(new BigDecimal("70"), new BigDecimal("-2"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("1"), new BigDecimal("-33"))));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(3, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO.getErrorKey(), 0, FIELD_NAME_TEMPLATE, 1L, 2);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO.getErrorKey(), 1, FIELD_NAME_TEMPLATE, 2L, 1);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO.getErrorKey(), 2, FIELD_NAME_TEMPLATE, 3L, 2);
    }

    @Test
    public void testCostsAreMoreThanMaxAllowed() {
        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("1000000"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("999999"), new BigDecimal("1000001"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("2000000"), new BigDecimal("10"))));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(3, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT.getErrorKey(), 0, FIELD_NAME_TEMPLATE, 1L, 0);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT.getErrorKey(), 1, FIELD_NAME_TEMPLATE, 2L, 1);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT.getErrorKey(), 2, FIELD_NAME_TEMPLATE, 3L, 1);
    }

    @Test
    public void testCostsAreNull() {
        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("1000"), null, new BigDecimal("40")),
                2L, asList(null, new BigDecimal("101"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("200"), null)));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(3, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL.getErrorKey(), 0, FIELD_NAME_TEMPLATE, 1L, 1);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL.getErrorKey(), 1, FIELD_NAME_TEMPLATE, 2L, 0);
        ValidatorTestUtil.verifyFieldError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL.getErrorKey(), 2, FIELD_NAME_TEMPLATE, 3L, 2);
    }

}
