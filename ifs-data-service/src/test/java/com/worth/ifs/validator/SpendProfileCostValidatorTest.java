package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static com.worth.ifs.util.MapFunctions.asMap;
import static com.worth.ifs.validator.ValidatorTestUtil.getBindingResult;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class SpendProfileCostValidatorTest {
    
	private Validator validator;
	
	private FormInputResponse formInputResponse;
	private BindingResult bindingResult;
	
	@Before
	public void setUp() {
        validator = new SpendProfileCostValidator();
        
        formInputResponse = new FormInputResponse();
        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void testCostsAreFractionalLessThanZeroOrGreaterThanMillionOrNukk() {

        SpendProfileTableResource table = new SpendProfileTableResource();

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30.12"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("-30"), null),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("1000001"))));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(4, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL.getErrorKey(), 0, 1L, 1);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO.getErrorKey(), 1, 2L, 2);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL.getErrorKey(), 2, 2L, 3);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT.getErrorKey(), 3, 3L, 3);
    }

    @Test
    public void testCostsAreFractional() {

        SpendProfileTableResource table = new SpendProfileTableResource();

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30.44"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("50.10"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("10.31"))));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(3, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL.getErrorKey(), 0, 1L, 1);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL.getErrorKey(), 1, 2L, 2);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_FRACTIONAL.getErrorKey(), 2, 3L, 3);
    }

    @Test
    public void testCostsAreLessThanZero() {
        SpendProfileTableResource table = new SpendProfileTableResource();

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("0"), new BigDecimal("00"), new BigDecimal("-1")),
                2L, asList(new BigDecimal("70"), new BigDecimal("-2"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("1"), new BigDecimal("-33"))));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(3, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO.getErrorKey(), 0, 1L, 3);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO.getErrorKey(), 1, 2L, 2);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_LESS_THAN_ZERO.getErrorKey(), 2, 3L, 3);
    }

    @Test
    public void testCostsAreMoreThanMaxAllowed() {

        SpendProfileTableResource table = new SpendProfileTableResource();

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("1000000"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("999999"), new BigDecimal("1000001"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("2000000"), new BigDecimal("10"))));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(3, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT.getErrorKey(), 0, 1L, 1);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT.getErrorKey(), 1, 2L, 2);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_BE_WITHIN_UPPER_LIMIT.getErrorKey(), 2, 3L, 2);
    }

    @Test
    public void testCostsAreNull() {

        SpendProfileTableResource table = new SpendProfileTableResource();

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("1000"), null, new BigDecimal("40")),
                2L, asList(null, new BigDecimal("101"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("200"), null)));

        validator.validate(table, bindingResult);
        assertTrue(bindingResult.hasErrors());
        Assert.assertEquals(3, bindingResult.getErrorCount());

        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL.getErrorKey(), 0, 1L, 2);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL.getErrorKey(), 1, 2L, 1);
        ValidatorTestUtil.verifyError(bindingResult, SpendProfileValidationError.COST_SHOULD_NOT_BE_NULL.getErrorKey(), 2, 3L, 3);
    }
}